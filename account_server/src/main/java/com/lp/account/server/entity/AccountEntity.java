package com.lp.account.server.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotations.IdType;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;

import lombok.Data;

@Data
@TableName("account")
public class AccountEntity {

	@TableId(value="acc_id", type=IdType.AUTO)
	private Long accId;
	@TableField("user_id")
	private String userId;
	@TableField("avail_amount")
	private BigDecimal availAmount;
	@TableField("lock_amount")
	private BigDecimal lockAmount;
	@TableField("transit_amount")
	private BigDecimal transitAmount;
	@TableField("create_time")
	private LocalDateTime createTime;
	@TableField("update_time")
    private LocalDateTime updateTime;
}
