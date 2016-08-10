package com.lp.account.server.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotations.IdType;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;

import lombok.Data;

@Data
@TableName("request_detail")
public class RequestDetailEntity {

	@TableId(value="detail_id", type=IdType.AUTO)
	private Long detailId;
	@TableField(value="request_id")
	private Long requestId;
	@TableField("acc_id")
    private Long accId;
	@TableField("operate_type")
    private Integer operateType;
	@TableField("amount")
    private BigDecimal amount;
	@TableField("create_time")
    private LocalDateTime createTime;
	@TableField("update_time")
    private LocalDateTime updateTime;
}
