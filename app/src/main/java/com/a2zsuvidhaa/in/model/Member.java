package com.a2zsuvidhaa.in.model;

public class Member {

    private String id;
    private String name;
    private String balance;
    private String status;
    private String shopName;
    private String email;
    private String mobile;
    private String status_id;
    private String parentDetails;
    private String prefix;

    private boolean isHide = true;


    public Member() { }

    public Member(String id, String name, String balance,
                  String status, String shopName, String email,String mobile,
                  String status_id,String parentDetails,String prefix) {
        this.id = id;
        this.name = name;
        this.balance = balance;
        this.status = status;
        this.shopName = shopName;
        this.email = email;
        this.mobile = mobile;
        this.status_id = status_id;
        this.parentDetails = parentDetails;
        this.prefix = prefix;
    }

    public boolean isHide() {
        return isHide;
    }

    public void setHide(boolean hide) {
        isHide = hide;
    }

    public String getStatus_id() {
        return status_id;
    }

    public void setStatus_id(String status_id) {
        this.status_id = status_id;
    }

    public String getParentDetails() {
        return parentDetails;
    }

    public void setParentDetails(String parentDetails) {
        this.parentDetails = parentDetails;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
