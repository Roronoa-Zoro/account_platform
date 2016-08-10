package com.lp.account.server.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.framework.service.impl.SuperServiceImpl;
import com.lp.account.server.dao.AccountMapper;
import com.lp.account.server.entity.AccountEntity;
import com.lp.account.server.service.AccountService;

@Service
@Transactional
public class AccountServiceImpl extends SuperServiceImpl<AccountMapper, AccountEntity> implements AccountService {

}
