package com.lp.account.server.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.framework.service.impl.SuperServiceImpl;
import com.lp.account.client.enums.OperationType;
import com.lp.account.server.bo.RequestBO;
import com.lp.account.server.dao.CashflowMapper;
import com.lp.account.server.dao.RequestMapper;
import com.lp.account.server.entity.AccountEntity;
import com.lp.account.server.entity.CashflowEntity;
import com.lp.account.server.entity.RequestDetailEntity;
import com.lp.account.server.entity.RequestEntity;
import com.lp.account.server.service.AccountService;
import com.lp.account.server.service.RequestDetailService;
import com.lp.account.server.service.RequestService;
import com.lp.transaction.client.api.TransactionClient;
import com.lp.transaction.client.enums.CallbackState;
import com.lp.transaction.client.enums.TransactionState;
import com.lp.transaction.client.model.TransactionParticipantsVO;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class RequestServiceImpl extends SuperServiceImpl<RequestMapper, RequestEntity> implements RequestService {

	@Autowired
	RequestDetailService rds;
	@Autowired
	AccountService as;
	@Autowired
	CashflowMapper cashflow;
	@Autowired
	TransactionClient trxClient;
	@Value("${account.callback.commit}")
	String commitCallback;
	@Value("${account.callback.rollback}")
	String rollbackCallback;
	
	@Override
	public boolean handleRequest(RequestBO req) {
		TransactionParticipantsVO participant = new TransactionParticipantsVO();
		participant.setTrxId(req.getReq().getTrxId());
		participant.setParticipantsCallback("");
		participant.setParticipantsRollbackCallback(rollbackCallback);
		participant.setParticipantsSubmitCallback(commitCallback);
		participant.setParticipantsVersion(0);
		participant.setParticipantsInvokeState(1);
		TransactionParticipantsVO resVO = trxClient.addParticipantsTrx(participant);
		if (resVO.getParticipantsId() == null) {
			log.warn("insert coordinator trx:{} fails.", req.getReq().getTrxId());
			return false;
		}
		log.info("add a participant:{} trx:{} record.", resVO.getParticipantsId(), resVO.getTrxId());
		
		req.getReq().setTrxState(TransactionState.UNKNOWN.getState());
		req.getReq().setParticipantId(resVO.getParticipantsId());
		req.getReq().setVersion(0);
		super.insert(req.getReq());
		req.getDetail().forEach(de -> {
			de.setRequestId(req.getReq().getRequestId());
		});
		rds.insertBatch(req.getDetail());
		List<Long> accIdList = new ArrayList<>();
		Map<Long, RequestDetailEntity> map = new HashMap<>();
		
		req.getDetail().forEach(detail -> {
			accIdList.add(detail.getAccId());
			map.put(detail.getAccId(), detail);
		});
		List<AccountEntity> accounts = as.selectBatchIds(accIdList);
		List<CashflowEntity> flows = new ArrayList<>(accounts.size());
		LocalDateTime current = LocalDateTime.now();
		accounts.forEach(acc -> {
			CashflowEntity flow = new CashflowEntity();
			RequestDetailEntity de = map.get(acc.getAccId());
			if (OperationType.Plus.getType() == de.getOperateType()) {
				acc.setTransitAmount(acc.getTransitAmount().add(de.getAmount()));
				acc.setUpdateTime(current);
				flow.setAccId(acc.getAccId());
				flow.setFlowAmount(de.getAmount());
				flow.setFlowDesc("充值操作,资金先进入transitAmount");
				flow.setCreateTime(current);
				flow.setUpdateTime(current);
				flows.add(flow);
				return;
			}
			if (OperationType.Minus.getType() == de.getOperateType()) {
				acc.setTransitAmount(acc.getTransitAmount().add(de.getAmount()));
				acc.setAvailAmount(acc.getAvailAmount().subtract(de.getAmount()));
				acc.setUpdateTime(current);
				flow.setAccId(acc.getAccId());
				flow.setFlowAmount(de.getAmount());
				flow.setFlowDesc("支付操作,资金从availAmount先进入到transitAmount");
				flow.setCreateTime(current);
				flow.setUpdateTime(current);
				flows.add(flow);
			}
			
		});
		as.updateBatchById(accounts);
		cashflow.insertBatch(flows);
		log.info("account req:{} is saved", req.getReq().getRequestId());
		return true;
	}

	@Override
	public CallbackState commitRequest(long participantId) {
		RequestEntity where = new RequestEntity();
		where.setParticipantId(participantId);
		RequestEntity re = super.selectOne(where);
		if (re == null) {
			log.error("no such coordinator trx:{}", participantId);
			return CallbackState.CallbackIllegal;
		}
		if (TransactionState.COMMIT.getState() == re.getTrxState()) {
			log.error("participantId:{} already commited", participantId);
			return CallbackState.CallbackCommitSuccess;
		}
		
		RequestEntity update = new RequestEntity();
		update.setTrxState(TransactionState.COMMIT.getState());
		update.setUpdateTime(LocalDateTime.now());
		where.setTrxState(TransactionState.UNKNOWN.getState());
		boolean updated = super.updateSelective(update, where);
		if (!updated) {
			log.info("other thread get chance to do commit logic");
			return CallbackState.CallbackCommitSuccess;
		}
		
		RequestDetailEntity detail = new RequestDetailEntity();
		detail.setRequestId(re.getRequestId());
		List<RequestDetailEntity> details = rds.selectList(detail);
		List<Long> accIdList = new ArrayList<>();
		Map<Long, RequestDetailEntity> map = new HashMap<>();
		
		details.forEach(de -> {
			accIdList.add(de.getAccId());
			map.put(de.getAccId(), de);
		});
		LocalDateTime current = LocalDateTime.now();
		List<AccountEntity> accounts = as.selectBatchIds(accIdList);
		List<CashflowEntity> flows = new ArrayList<>(accounts.size());
		accounts.forEach(acc -> {
			CashflowEntity flow = new CashflowEntity();
			RequestDetailEntity de = map.get(acc.getAccId());
			if (OperationType.Plus.getType() == de.getOperateType()) {
				acc.setAvailAmount(acc.getAvailAmount().add(de.getAmount()));
				acc.setTransitAmount(acc.getTransitAmount().subtract(de.getAmount()));
				acc.setUpdateTime(current);
				flow.setAccId(acc.getAccId());
				flow.setFlowAmount(de.getAmount());
				flow.setFlowDesc("确认充值操作,资金从transitAmount进入到availAmount");
				flow.setCreateTime(current);
				flow.setUpdateTime(current);
				flows.add(flow);
				return;
			}
			if (OperationType.Minus.getType() == de.getOperateType()) {
				acc.setTransitAmount(acc.getTransitAmount().subtract(de.getAmount()));
				acc.setUpdateTime(current);
				flow.setAccId(acc.getAccId());
				flow.setFlowAmount(de.getAmount());
				flow.setFlowDesc("确认支付操作,资金从transitAmount扣除");
				flow.setCreateTime(current);
				flow.setUpdateTime(current);
				flows.add(flow);
			}
		});
		as.updateBatchById(accounts);
		cashflow.insertBatch(flows);
		return CallbackState.CallbackCommitSuccess;
	}

	@Override
	public CallbackState rollbackRequest(long participantId) {
		RequestEntity where = new RequestEntity();
		where.setParticipantId(participantId);
		RequestEntity re = super.selectOne(where);
		if (re == null) {
			log.error("no such coordinator trx:{}", participantId);
			return CallbackState.CallbackIllegal;
		}
		
		if (TransactionState.ROLLBACK.getState() == re.getTrxState()) {
			log.error("participantId:{} already rollback", participantId);
			return CallbackState.CallbackRollbackSuccess;
		}
		
		RequestEntity update = new RequestEntity();
		update.setTrxState(TransactionState.ROLLBACK.getState());
		update.setUpdateTime(LocalDateTime.now());
		where.setTrxState(re.getTrxState());
		boolean updated = super.updateSelective(update, where);
		if (!updated) {
			log.info("other thread get chance to do rollback logic");
			return CallbackState.CallbackCommitSuccess;
		}
		
		RequestDetailEntity detail = new RequestDetailEntity();
		detail.setRequestId(re.getRequestId());
		List<RequestDetailEntity> details = rds.selectList(detail);
		List<Long> accIdList = new ArrayList<>();
		Map<Long, RequestDetailEntity> map = new HashMap<>();
		
		details.forEach(de -> {
			accIdList.add(de.getAccId());
			map.put(de.getAccId(), de);
		});
		LocalDateTime current = LocalDateTime.now();
		List<AccountEntity> accounts = as.selectBatchIds(accIdList);
		List<CashflowEntity> flows = new ArrayList<>(accounts.size());
		accounts.forEach(acc -> {
			CashflowEntity flow = new CashflowEntity();
			RequestDetailEntity de = map.get(acc.getAccId());
			if (OperationType.Plus.getType() == de.getOperateType()) {
				acc.setTransitAmount(acc.getTransitAmount().subtract(de.getAmount()));
				acc.setUpdateTime(current);
				flow.setAccId(acc.getAccId());
				flow.setFlowAmount(de.getAmount());
				flow.setFlowDesc("取消充值操作,资金从transitAmount扣除");
				flow.setCreateTime(current);
				flow.setUpdateTime(current);
				flows.add(flow);
				return;
			}
			if (OperationType.Minus.getType() == de.getOperateType()) {
				acc.setAvailAmount(acc.getAvailAmount().add(de.getAmount()));
				acc.setTransitAmount(acc.getTransitAmount().subtract(de.getAmount()));
				acc.setUpdateTime(current);
				flow.setAccId(acc.getAccId());
				flow.setFlowAmount(de.getAmount());
				flow.setFlowDesc("取消支付操作,资金从transitAmount回退到availAmount");
				flow.setCreateTime(current);
				flow.setUpdateTime(current);
				flows.add(flow);
			}
		});
		as.updateBatchById(accounts);
		cashflow.insertBatch(flows);
		return CallbackState.CallbackRollbackSuccess;
	}

}
