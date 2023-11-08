package com.a2zsuvidhaa.in.model;

public class PaymentReport {

    private String date;
    private String id;
    private String request_to;
    private String bank_name;
    private String mode;
    private String branch_code;
    private String deposit_date;
    private String amount;
    private String deposit_slip;
    private String customer_remark;
    private String ref_id;
    private String updated_remark;
    private String remark;
    private String status;
    private String status_id;

    private String request_date;
    private String update_date;
    private String wallet;
    private String user;
    private String transfer_to_from;
    private String firm_name;
    private String description;
    private String bank_ref;
    private String agent_remark;
    private String opening_bal;
    private String credit_amount;
    private String closing_bal;
    private String bank_charge;

    private boolean isHide = true;

    public PaymentReport() {
    }

    public PaymentReport(String date, String id, String ref_id, String remark, String status,
                         String status_id, String request_date, String update_date, String wallet,
                         String user, String transfer_to_from, String firm_name, String description,
                         String bank_ref, String agent_remark, String opening_bal, String credit_amount,
                         String closing_bal, String bank_charge) {
        this.date = date;
        this.id = id;
        this.ref_id = ref_id;
        this.remark = remark;
        this.status = status;
        this.status_id = status_id;
        this.request_date = request_date;
        this.update_date = update_date;
        this.wallet = wallet;
        this.user = user;
        this.transfer_to_from = transfer_to_from;
        this.firm_name = firm_name;
        this.description = description;
        this.bank_ref = bank_ref;
        this.agent_remark = agent_remark;
        this.opening_bal = opening_bal;
        this.credit_amount = credit_amount;
        this.closing_bal = closing_bal;
        this.bank_charge = bank_charge;
    }

    public PaymentReport(String date, String id, String request_to, String bank_name, String mode,
                         String branch_code, String deposit_date, String amount, String deposit_slip,
                         String customer_remark, String ref_id, String updated_remark, String remark,
                         String status, String status_id) {
        this.date = date;
        this.id = id;
        this.request_to = request_to;
        this.bank_name = bank_name;
        this.mode = mode;
        this.branch_code = branch_code;
        this.deposit_date = deposit_date;
        this.amount = amount;
        this.deposit_slip = deposit_slip;
        this.customer_remark = customer_remark;
        this.ref_id = ref_id;
        this.updated_remark = updated_remark;
        this.remark = remark;
        this.status = status;
        this.status_id = status_id;
    }


    public String getRequest_date() {
        return request_date;
    }

    public void setRequest_date(String request_date) {
        this.request_date = request_date;
    }

    public String getUpdate_date() {
        return update_date;
    }

    public void setUpdate_date(String update_date) {
        this.update_date = update_date;
    }

    public String getWallet() {
        return wallet;
    }

    public void setWallet(String wallet) {
        this.wallet = wallet;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getTransfer_to_from() {
        return transfer_to_from;
    }

    public void setTransfer_to_from(String transfer_to_from) {
        this.transfer_to_from = transfer_to_from;
    }

    public String getFirm_name() {
        return firm_name;
    }

    public void setFirm_name(String firm_name) {
        this.firm_name = firm_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBank_ref() {
        return bank_ref;
    }

    public void setBank_ref(String bank_ref) {
        this.bank_ref = bank_ref;
    }

    public String getAgent_remark() {
        return agent_remark;
    }

    public void setAgent_remark(String agent_remark) {
        this.agent_remark = agent_remark;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRequest_to() {
        return request_to;
    }

    public void setRequest_to(String request_to) {
        this.request_to = request_to;
    }

    public String getBank_name() {
        return bank_name;
    }

    public void setBank_name(String bank_name) {
        this.bank_name = bank_name;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getBranch_code() {
        return branch_code;
    }

    public void setBranch_code(String branch_code) {
        this.branch_code = branch_code;
    }

    public String getDeposit_date() {
        return deposit_date;
    }

    public void setDeposit_date(String deposit_date) {
        this.deposit_date = deposit_date;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDeposit_slip() {
        return deposit_slip;
    }

    public void setDeposit_slip(String deposit_slip) {
        this.deposit_slip = deposit_slip;
    }

    public String getCustomer_remark() {
        return customer_remark;
    }

    public void setCustomer_remark(String customer_remark) {
        this.customer_remark = customer_remark;
    }

    public String getRef_id() {
        return ref_id;
    }

    public void setRef_id(String ref_id) {
        this.ref_id = ref_id;
    }

    public String getUpdated_remark() {
        return updated_remark;
    }

    public void setUpdated_remark(String updated_remark) {
        this.updated_remark = updated_remark;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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
