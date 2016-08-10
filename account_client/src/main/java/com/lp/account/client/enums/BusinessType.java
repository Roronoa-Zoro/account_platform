package com.lp.account.client.enums;

/**
 * Created by Administrator on 2016/8/3.
 */
public enum BusinessType {
    Finance("1001", "购买理财");

    private String type;
    private String desc;

    BusinessType(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public String getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}
