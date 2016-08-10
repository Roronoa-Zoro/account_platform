package com.lp.account.server.service;

import com.baomidou.framework.service.ISuperService;
import com.lp.account.server.bo.RequestBO;
import com.lp.account.server.entity.RequestEntity;
import com.lp.transaction.client.enums.CallbackState;

public interface RequestService extends ISuperService<RequestEntity> {

	boolean handleRequest(RequestBO req);
	
	CallbackState commitRequest(long participantId);
	
	CallbackState rollbackRequest(long participantId);
	
}
