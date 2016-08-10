package com.lp.account.server.bo;

import java.util.List;

import com.lp.account.server.entity.RequestDetailEntity;
import com.lp.account.server.entity.RequestEntity;

import lombok.Data;

@Data
public class RequestBO {

	private RequestEntity req;
	private List<RequestDetailEntity> detail;
}
