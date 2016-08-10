package com.lp.account.server.rpc;

import org.springframework.beans.factory.annotation.Autowired;

import com.lp.account.client.api.AccountApi;
import com.lp.account.client.model.Request;
import com.lp.account.server.bo.RequestBO;
import com.lp.account.server.converter.VOToBO;
import com.lp.account.server.service.RequestService;

public class DefaultAccountApi implements AccountApi {

	@Autowired
	RequestService rs;
	
	@Override
	public boolean operateAccountRequest(Request request) {
		RequestBO bo = VOToBO.toBO(request);
		return rs.handleRequest(bo);
	}

}
