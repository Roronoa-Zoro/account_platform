package com.lp.account.server.rest;

import com.lp.transaction.client.enums.CallbackState;

public interface AccountCallback {

	CallbackState transactionCommit(Long participantTrxId);
	
	CallbackState transactionRollback(Long participantTrxId);
}
