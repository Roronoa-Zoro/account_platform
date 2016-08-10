package com.lp.account.server.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotations.IdType;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;

import lombok.Data;

@Data
@TableName("request")
public class RequestEntity {

	@TableId(value="request_id", type=IdType.AUTO)
	private Long requestId;
    //业务请求id
	@TableField("bus_req_id")
    private Long busReqId;
    //参与者事务id
	@TableField("participant_id")
    private Long participantId;
    //
	@TableField(exist=false)
    private long trxId;
	@TableField("business_type")
    private String businessType;
	@TableField("create_time")
    private LocalDateTime createTime;
	@TableField("update_time")
    private LocalDateTime updateTime;
	@TableField("trx_state")
	private Integer trxState;
	@TableField("version")
	private Integer version;
}
