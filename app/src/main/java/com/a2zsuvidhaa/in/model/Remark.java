package com.a2zsuvidhaa.in.model;

public class Remark {

    private String key;
    private String remark;

    public Remark(String key, String remark) {
        this.key = key;
        this.remark = remark;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
