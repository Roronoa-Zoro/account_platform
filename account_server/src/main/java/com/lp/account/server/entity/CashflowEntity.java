package com.lp.account.server.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotations.IdType;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;

import lombok.Data;

@Data
@TableName("cashflow")
public class CashflowEntity {

	@TableId(value="flow_id", type=IdType.AUTO)
	private Long flowId;
	@TableField("acc_id")
	private Long accId;
	@TableField("flow_amount")
	private BigDecimal flowAmount;
	@TableField("flow_desc")
	private String flowDesc;
	@TableField("create_time")
	private LocalDateTime createTime;
	@TableField("update_time")
	private LocalDateTime updateTime;
}
