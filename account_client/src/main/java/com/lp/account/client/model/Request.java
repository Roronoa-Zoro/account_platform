package com.lp.account.client.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

/**
 * Created by Administrator on 2016/8/3.
 */
@Data
public class Request implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Long requestId;
    //业务请求id
    private Long busReqId;
    //参与者事务id
    private long participantId;
    //主事务id
    private long trxId;
    private String businessType;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<RequestDetail> details;
}
