package com.lp.account.server.rest.impl;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;

import com.lp.account.server.bo.CallbackBO;
import com.lp.account.server.rest.AccountCallback;
import com.lp.account.server.service.RequestService;
import com.lp.transaction.client.enums.CallbackState;

@Path("account")
public class DefaultAccountCallback implements AccountCallback {

	@Autowired
	RequestService rs;
	
	@Override
	@POST
    @Path("callback/commit")
    @Consumes({MediaType.APPLICATION_JSON})
	public CallbackState transactionCommit(Long participantTrxId) {
		return rs.commitRequest(participantTrxId);
	}

	@Override
	@POST
    @Path("callback/rollback")
    @Consumes({MediaType.APPLICATION_JSON})
	public CallbackState transactionRollback(Long participantTrxId) {
		return rs.rollbackRequest(participantTrxId);
	}

}
