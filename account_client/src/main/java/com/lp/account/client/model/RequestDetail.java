package com.lp.account.client.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

/**
 * Created by Administrator on 2016/8/3.
 */
@Data
public class RequestDetail implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Long detailId;
    private long accId;
    private int operateType;
    private BigDecimal amount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
