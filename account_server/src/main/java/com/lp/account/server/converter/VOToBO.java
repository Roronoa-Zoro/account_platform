package com.lp.account.server.converter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.lp.account.client.model.Request;
import com.lp.account.server.bo.RequestBO;
import com.lp.account.server.entity.RequestDetailEntity;
import com.lp.account.server.entity.RequestEntity;

public class VOToBO {

	public static RequestBO toBO(Request req) {
		RequestBO bo = new RequestBO();
		bo.setReq(toReqEntity(req));
		bo.setDetail(toReqDetailEntity(req));
		return bo;
	}
	
	public static RequestEntity toReqEntity(Request req) {
		RequestEntity entity = new RequestEntity();
		entity.setBusinessType(req.getBusinessType());
		entity.setBusReqId(req.getBusReqId());
		entity.setRequestId(req.getRequestId());
		entity.setTrxId(req.getTrxId());
		entity.setCreateTime(LocalDateTime.now());
		entity.setUpdateTime(LocalDateTime.now());
		
		return entity;
	}
	
	public static List<RequestDetailEntity> toReqDetailEntity(Request req) {
		List<RequestDetailEntity> details = new ArrayList<RequestDetailEntity>();
		req.getDetails().forEach(detail -> {
			RequestDetailEntity de = new RequestDetailEntity();
			de.setAccId(detail.getAccId());
			de.setAmount(detail.getAmount());
			de.setOperateType(detail.getOperateType());
			de.setCreateTime(LocalDateTime.now());
			de.setUpdateTime(LocalDateTime.now());
			details.add(de);
		});
		return details;
	}
}
