package com.a2zsuvidhaa.in.model;

public class Complain {

    private String id;
    private String userId;
    private String createdAt;
    private String issueType;
    private String txnId;
    private String lookingBy;
    private String approveBy;
    private String status;
    private String statusId;
    private String approveDate;
    private String remark;
    private String currentStatusRemark;

    private boolean isHide = true;
    private boolean isStatus = true;

    public Complain(){}
    public Complain(String id, String userId, String createdAt, String issueType,
                    String txnId, String lookingBy, String approveBy, String status, String statusId,
                    String approveDate,String remark, String currentStatusRemark) {
        this.id = id;
        this.userId = userId;
        this.createdAt = createdAt;
        this.issueType = issueType;
        this.txnId = txnId;
        this.lookingBy = lookingBy;
        this.approveBy = approveBy;
        this.status = status;
        this.statusId = statusId;
        this.approveDate = approveDate;
        this.remark = remark;
        this.currentStatusRemark = currentStatusRemark;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getIssueType() {
        return issueType;
    }

    public void setIssueType(String issueType) {
        this.issueType = issueType;
    }

    public String getTxnId() {
        return txnId;
    }

    public void setTxnId(String txnId) {
        this.txnId = txnId;
    }

    public String getLookingBy() {
        return lookingBy;
    }

    public void setLookingBy(String lookingBy) {
        this.lookingBy = lookingBy;
    }

    public String getApproveBy() {
        return approveBy;
    }

    public void setApproveBy(String approveBy) {
        this.approveBy = approveBy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusId() {
        return statusId;
    }

    public void setStatusId(String statusId) {
        this.statusId = statusId;
    }

    public String getApproveDate() {
        return approveDate;
    }

    public void setApproveDate(String approveDate) {
        this.approveDate = approveDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCurrentStatusRemark() {
        return currentStatusRemark;
    }

    public void setCurrentStatusRemark(String currentStatusRemark) {
        this.currentStatusRemark = currentStatusRemark;
    }

    public boolean isHide() {
        return isHide;
    }

    public void setHide(boolean hide) {
        isHide = hide;
    }

    public boolean isStatus() {
        return isStatus;
    }

    public void setStatus(boolean status) {
        isStatus = status;
    }
}
