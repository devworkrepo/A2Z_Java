package com.a2zsuvidhaa.in.model;

public class FundReport {

    private String id;
    private String created_at;
    private String username;
    private String deposit_date;
    private String bank_name;
    private String account_number;
    private String wallet_amount;
    private String request_for;
    private String bank_ref;
    private String payment_mode;
    private String branch_name;
    private String request_remark;
    private String approval_remark;
    private String update_remark;
    private String status;
    private String status_id;

    private String wallet;
    private String number;
    private String userId;
    private String transferFromTo;
    private String shopName;
    private String description;
    private String opening_bal;
    private String credit_amount;
    private String closing_bal;
    private String bank_charge;
    private String remark;


    private boolean isHide = true;

    public FundReport(){}

    public FundReport(String id, String created_at, String username, String status, String wallet,
                      String userId, String transferFromTo, String shopName, String refId,
                      String description, String opening_bal, String credit_amount, String closing_bal,
                      String bank_charge, String remark,String number,String ignore) {
        this.id = id;
        this.created_at = created_at;
        this.username = username;
        this.status = status;
        this.wallet = wallet;
        this.userId = userId;
        this.transferFromTo = transferFromTo;
        this.shopName = shopName;
        this.bank_ref = refId;
        this.description = description;
        this.opening_bal = opening_bal;
        this.credit_amount = credit_amount;
        this.closing_bal = closing_bal;
        this.bank_charge = bank_charge;
        this.remark = remark;
        this.number = number;
    }

    public FundReport(String id, String created_at, String username, String deposit_date,
                      String bank_name, String account_number, String wallet_amount,
                      String request_for, String bank_ref, String payment_mode,
                      String branch_name, String request_remark, String approval_remark,
                      String update_remark, String status, String status_id) {
        this.id = id;
        this.created_at = created_at;
        this.username = username;
        this.deposit_date = deposit_date;
        this.bank_name = bank_name;
        this.account_number = account_number;
        this.wallet_amount = wallet_amount;
        this.request_for = request_for;
        this.bank_ref = bank_ref;
        this.payment_mode = payment_mode;
        this.branch_name = branch_name;
        this.request_remark = request_remark;
        this.approval_remark = approval_remark;
        this.update_remark = update_remark;
        this.status = status;
        this.status_id = status_id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getWallet() {
        return wallet;
    }

    public void setWallet(String wallet) {
        this.wallet = wallet;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTransferFromTo() {
        return transferFromTo;
    }

    public void setTransferFromTo(String transferFromTo) {
        this.transferFromTo = transferFromTo;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOpening_bal() {
        return opening_bal;
    }

    public void setOpening_bal(String opening_bal) {
        this.opening_bal = opening_bal;
    }

    public String getCredit_amount() {
        return credit_amount;
    }

    public void setCredit_amount(String credit_amount) {
        this.credit_amount = credit_amount;
    }

    public String getClosing_bal() {
        return closing_bal;
    }

    public void setClosing_bal(String closing_bal) {
        this.closing_bal = closing_bal;
    }

    public String getBank_charge() {
        return bank_charge;
    }

    public void setBank_charge(String bank_charge) {
        this.bank_charge = bank_charge;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDeposit_date() {
        return deposit_date;
    }

    public void setDeposit_date(String deposit_date) {
        this.deposit_date = deposit_date;
    }

    public String getBank_name() {
        return bank_name;
    }

    public void setBank_name(String bank_name) {
        this.bank_name = bank_name;
    }

    public String getAccount_number() {
        return account_number;
    }

    public void setAccount_number(String account_number) {
        this.account_number = account_number;
    }

    public String getWallet_amount() {
        return wallet_amount;
    }

    public void setWallet_amount(String wallet_amount) {
        this.wallet_amount = wallet_amount;
    }

    public String getRequest_for() {
        return request_for;
    }

    public void setRequest_for(String request_for) {
        this.request_for = request_for;
    }

    public String getBank_ref() {
        return bank_ref;
    }

    public void setBank_ref(String bank_ref) {
        this.bank_ref = bank_ref;
    }

    public String getPayment_mode() {
        return payment_mode;
    }

    public void setPayment_mode(String payment_mode) {
        this.payment_mode = payment_mode;
    }

    public String getBranch_name() {
        return branch_name;
    }

    public void setBranch_name(String branch_name) {
        this.branch_name = branch_name;
    }

    public String getRequest_remark() {
        return request_remark;
    }

    public void setRequest_remark(String request_remark) {
        this.request_remark = request_remark;
    }

    public String getApproval_remark() {
        return approval_remark;
    }

    public void setApproval_remark(String approval_remark) {
        this.approval_remark = approval_remark;
    }

    public String getUpdate_remark() {
        return update_remark;
    }

    public void setUpdate_remark(String update_remark) {
        this.update_remark = update_remark;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus_id() {
        return status_id;
    }

    public void setStatus_id(String status_id) {
        this.status_id = status_id;
    }

    public boolean isHide() {
        return isHide;
    }

    public void setHide(boolean hide) {
        isHide = hide;
    }
}
