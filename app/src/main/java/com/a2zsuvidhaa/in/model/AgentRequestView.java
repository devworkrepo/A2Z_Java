package com.a2zsuvidhaa.in.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class AgentRequestView implements Parcelable{

    private String created_at;
    private String id;
    private String user_id;
    private String user_name;
    private String firm_name;
    private String role;
    private String mobile;
    private String mode;
    private String branch_code;
    private String online_payment_mode;
    private String deposit_date;
    private String bank_name;
    private String remark;
    private String slip;
    private String ref_id;
    private String amount;
    private String status;
    private String status_id;
    private ArrayList<Remark> remarks;
    private boolean isHide = true;

    public AgentRequestView() {
    }

    public AgentRequestView(String created_at, String id, String user_id, String user_name,
                            String firm_name, String role, String mobile, String mode,
                            String branch_code, String online_payment_mode, String deposit_date,
                            String bank_name, String remark, String slip, String ref_id, String amount,
                            String status, String status_id,ArrayList<Remark> remarks) {
        this.created_at = created_at;
        this.id = id;
        this.user_id = user_id;
        this.user_name = user_name;
        this.firm_name = firm_name;
        this.role = role;
        this.mobile = mobile;
        this.mode = mode;
        this.branch_code = branch_code;
        this.online_payment_mode = online_payment_mode;
        this.deposit_date = deposit_date;
        this.bank_name = bank_name;
        this.remark = remark;
        this.slip = slip;
        this.ref_id = ref_id;
        this.amount = amount;
        this.status = status;
        this.status_id = status_id;
        this.remarks = remarks;
    }


    protected AgentRequestView(Parcel in) {
        created_at = in.readString();
        id = in.readString();
        user_id = in.readString();
        user_name = in.readString();
        firm_name = in.readString();
        role = in.readString();
        mobile = in.readString();
        mode = in.readString();
        branch_code = in.readString();
        online_payment_mode = in.readString();
        deposit_date = in.readString();
        bank_name = in.readString();
        remark = in.readString();
        slip = in.readString();
        ref_id = in.readString();
        amount = in.readString();
        status = in.readString();
        status_id = in.readString();
        isHide = in.readByte() != 0;
    }

    public static final Creator<AgentRequestView> CREATOR = new Creator<AgentRequestView>() {
        @Override
        public AgentRequestView createFromParcel(Parcel in) {
            return new AgentRequestView(in);
        }

        @Override
        public AgentRequestView[] newArray(int size) {
            return new AgentRequestView[size];
        }
    };

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getFirm_name() {
        return firm_name;
    }

    public void setFirm_name(String firm_name) {
        this.firm_name = firm_name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
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

    public String getOnline_payment_mode() {
        return online_payment_mode;
    }

    public void setOnline_payment_mode(String online_payment_mode) {
        this.online_payment_mode = online_payment_mode;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getSlip() {
        return slip;
    }

    public void setSlip(String slip) {
        this.slip = slip;
    }

    public String getRef_id() {
        return ref_id;
    }

    public void setRef_id(String ref_id) {
        this.ref_id = ref_id;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
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

    public ArrayList<Remark> getRemarks() {
        return remarks;
    }

    public void setRemarks(ArrayList<Remark> remarks) {
        this.remarks = remarks;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(created_at);
        parcel.writeString(id);
        parcel.writeString(user_id);
        parcel.writeString(user_name);
        parcel.writeString(firm_name);
        parcel.writeString(role);
        parcel.writeString(mobile);
        parcel.writeString(mode);
        parcel.writeString(branch_code);
        parcel.writeString(online_payment_mode);
        parcel.writeString(deposit_date);
        parcel.writeString(bank_name);
        parcel.writeString(remark);
        parcel.writeString(slip);
        parcel.writeString(ref_id);
        parcel.writeString(amount);
        parcel.writeString(status);
        parcel.writeString(status_id);
        parcel.writeByte((byte) (isHide ? 1 : 0));
    }
}
