package com.a2zsuvidhaa.in.model;

import android.os.Parcel;
import android.os.Parcelable;

public class FundTransfer implements Parcelable {

    private String id;
    private String role;
    private String name;
    private String shopName;
    private String mobile;
    private String moneyBalance;


    public FundTransfer(){}

    public FundTransfer(String id, String role, String name, String shopName, String mobile, String moneyBalance) {
        this.id = id;
        this.role = role;
        this.name = name;
        this.shopName = shopName;
        this.mobile = mobile;
        this.moneyBalance = moneyBalance;
    }

    protected FundTransfer(Parcel in) {
        id = in.readString();
        role = in.readString();
        name = in.readString();
        shopName = in.readString();
        mobile = in.readString();
        moneyBalance = in.readString();
    }

    public static final Creator<FundTransfer> CREATOR = new Creator<FundTransfer>() {
        @Override
        public FundTransfer createFromParcel(Parcel in) {
            return new FundTransfer(in);
        }

        @Override
        public FundTransfer[] newArray(int size) {
            return new FundTransfer[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getMoneyBalance() {
        return moneyBalance;
    }

    public void setMoneyBalance(String moneyBalance) {
        this.moneyBalance = moneyBalance;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(role);
        dest.writeString(name);
        dest.writeString(shopName);
        dest.writeString(mobile);
        dest.writeString(moneyBalance);
    }
}
