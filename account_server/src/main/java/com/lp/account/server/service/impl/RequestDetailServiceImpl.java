package com.lp.account.server.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.framework.service.impl.SuperServiceImpl;
import com.lp.account.server.dao.RequestDetailMapper;
import com.lp.account.server.entity.RequestDetailEntity;
import com.lp.account.server.service.RequestDetailService;

@Service
@Transactional
public class RequestDetailServiceImpl extends SuperServiceImpl<RequestDetailMapper, RequestDetailEntity> 
				implements RequestDetailService {


}
