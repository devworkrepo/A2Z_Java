package com.a2zsuvidhaa.in.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Report implements Parcelable{
  private String id;
  private String date;
  private String mobileNumber;
  private String rRNo;
  private String number;
  private String apiId;
  private String description;
  private String beneName;
  private String beneAccount;
  private String ifscCode;
  private String bankName;
  private String remark;
  private String userName;
  private String product;
  private String txnId;
  private String providerName;
  private String amount;
  private String billerName;
  private String type;
  private String openingBalance;
  private String credtAmount;
  private String debitAmount;
  private String tds;
  private String gst;
  private String serviceTax;
  private String balance;
  private String txnType;
  private String r2rTransfer;
  private String refId;
  private String opId;
  private String operator;
  private String operatorId;
  private String status;
  private String statusId;

  private String rmnNumber;
  private String mode;
  private String name;



  private boolean isHide = true;
  private boolean isStatus = true;

  public Report(){}


  public Report(String id, String date, String number,String mobileNumber, String userName, String txnId,String refId,
                String providerName, String amount,String gst, String credtAmount, String debitAmount,
                String tds, String txnType, String status, String statusId, String mode,String opId) {
    this.id = id;
    this.date = date;
    this.number = number;
    this.mobileNumber = mobileNumber;
    this.userName = userName;
    this.txnId = txnId;
    this.providerName = providerName;
    this.amount = amount;
    this.credtAmount = credtAmount;
    this.debitAmount = debitAmount;
    this.tds = tds;
    this.gst = gst;
    this.refId = refId;
    this.opId = opId;
    this.txnType = txnType;
    this.status = status;
    this.statusId = statusId;
    this.mode = mode;
  }

  public Report(String id, String date, String mobileNumber, String number, String description,
                String bankName, String remark, String userName, String name, String product, String txnId,
                String amount, String openingBalance, String credtAmount,
                String debitAmount, String balance, String status,
                String statusId, String mode) {
    this.id = id;
    this.date = date;
    this.mobileNumber = mobileNumber;
    this.number = number;
    this.description = description;
    this.bankName = bankName;
    this.remark = remark;
    this.userName = userName;
    this.product = product;
    this.txnId = txnId;
    this.amount = amount;
    this.openingBalance = openingBalance;
    this.credtAmount = credtAmount;
    this.debitAmount = debitAmount;
    this.balance = balance;
    this.name = name;
    this.status = status;
    this.statusId = statusId;
    this.mode = mode;
  }

  public Report(String id, String date, String mobileNumber, String rRNo, String number,
                String apiId, String description, String beneName, String beneAccount, String ifscCode, String bankName,
                String remark, String userName, String product, String txnId, String providerName,
                String amount, String billerName, String type, String openingBalance,
                String credtAmount, String debitAmount, String tds, String serviceTax,
                String balance, String txnType, String r2rTransfer, String operator,
                String operatorId, String status, String statusId) {
    this.id = id;
    this.date = date;
    this.mobileNumber = mobileNumber;
    this.rRNo = rRNo;
    this.number = number;
    this.apiId = apiId;
    this.description = description;
    this.beneName = beneName;
    this.ifscCode = ifscCode;
    this.bankName = bankName;
    this.beneAccount = beneAccount;
    this.remark = remark;
    this.userName = userName;
    this.product = product;
    this.txnId = txnId;
    this.providerName = providerName;
    this.amount = amount;
    this.billerName = billerName;
    this.type = type;
    this.openingBalance = openingBalance;
    this.credtAmount = credtAmount;
    this.debitAmount = debitAmount;
    this.tds = tds;
    this.serviceTax = serviceTax;
    this.balance = balance;
    this.txnType = txnType;
    this.r2rTransfer = r2rTransfer;
    this.operator = operator;
    this.operatorId = operatorId;
    this.status = status;
    this.statusId = statusId;

  }

  public Report(String id, String date, String mobileNumber, String beneName, String beneAccount, String ifscCode,
                String bankName, String amount, String number, String type, String txnType,
                String operator, String operatorId, String status, String statusId,
                String rmnNumber, String mode) {
    this.id = id;
    this.date = date;
    this.mobileNumber = mobileNumber;
    this.beneName = beneName;
    this.ifscCode = ifscCode;
    this.bankName = bankName;
    this.beneAccount = beneAccount;
    this.amount = amount;
    this.number = number;
    this.type = type;
    this.txnType = txnType;
    this.operator = operator;
    this.operatorId = operatorId;
    this.status = status;
    this.statusId = statusId;
    this.rmnNumber = rmnNumber;
    this.mode = mode;
  }

  protected Report(Parcel in) {
    id = in.readString();
    date = in.readString();
    mobileNumber = in.readString();
    rRNo = in.readString();
    number = in.readString();
    apiId = in.readString();
    description = in.readString();
    beneName = in.readString();
    beneAccount = in.readString();
    ifscCode = in.readString();
    bankName = in.readString();
    remark = in.readString();
    userName = in.readString();
    product = in.readString();
    txnId = in.readString();
    providerName = in.readString();
    amount = in.readString();
    billerName = in.readString();
    type = in.readString();
    openingBalance = in.readString();
    credtAmount = in.readString();
    debitAmount = in.readString();
    tds = in.readString();
    gst = in.readString();
    serviceTax = in.readString();
    balance = in.readString();
    txnType = in.readString();
    r2rTransfer = in.readString();
    refId = in.readString();
    opId = in.readString();
    operator = in.readString();
    operatorId = in.readString();
    status = in.readString();
    statusId = in.readString();
    rmnNumber = in.readString();
    mode = in.readString();
    name = in.readString();
    isHide = in.readByte() != 0;
    isStatus = in.readByte() != 0;
  }


  public static final Creator<Report> CREATOR = new Creator<Report>() {
    @Override
    public Report createFromParcel(Parcel in) {
      return new Report(in);
    }

    @Override
    public Report[] newArray(int size) {
      return new Report[size];
    }
  };

  public String getType() {
    return type;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public String getMobileNumber() {
    return mobileNumber;
  }

  public void setMobileNumber(String mobileNumber) {
    this.mobileNumber = mobileNumber;
  }

  public String getrRNo() {
    return rRNo;
  }

  public void setrRNo(String rRNo) {
    this.rRNo = rRNo;
  }

  public String getNumber() {
    return number;
  }

  public void setNumber(String number) {
    this.number = number;
  }

  public String getApiId() {
    return apiId;
  }

  public void setApiId(String apiId) {
    this.apiId = apiId;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getBeneName() {
    return beneName;
  }

  public void setBeneName(String beneName) {
    this.beneName = beneName;
  }

  public String getBeneAccount() {
    return beneAccount;
  }

  public void setBeneAccount(String beneAccount) {
    this.beneAccount = beneAccount;
  }

  public String getIfscCode() {
    return ifscCode;
  }

  public void setIfscCode(String ifscCode) {
    this.ifscCode = ifscCode;
  }

  public String getBankName() {
    return bankName;
  }

  public void setBankName(String bankName) {
    this.bankName = bankName;
  }

  public String getRemark() {
    return remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getProduct() {
    return product;
  }

  public void setProduct(String product) {
    this.product = product;
  }

  public String getTxnId() {
    return txnId;
  }

  public void setTxnId(String txnId) {
    this.txnId = txnId;
  }

  public String getProviderName() {
    return providerName;
  }

  public void setProviderName(String providerName) {
    this.providerName = providerName;
  }

  public String getAmount() {
    return amount;
  }

  public void setAmount(String amount) {
    this.amount = amount;
  }

  public String getBillerName() {
    return billerName;
  }

  public void setBillerName(String billerName) {
    this.billerName = billerName;
  }

  public String getCreditDebit() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getOpeningBalance() {
    return openingBalance;
  }

  public void setOpeningBalance(String openingBalance) {
    this.openingBalance = openingBalance;
  }

  public String getCredtAmount() {
    return credtAmount;
  }

  public void setCredtAmount(String credtAmount) {
    this.credtAmount = credtAmount;
  }

  public String getDebitAmount() {
    return debitAmount;
  }

  public void setDebitAmount(String debitAmount) {
    this.debitAmount = debitAmount;
  }

  public String getTds() {
    return tds;
  }

  public void setTds(String tds) {
    this.tds = tds;
  }

  public String getServiceTax() {
    return serviceTax;
  }

  public void setServiceTax(String serviceTax) {
    this.serviceTax = serviceTax;
  }

  public String getBalance() {
    return balance;
  }

  public void setBalance(String balance) {
    this.balance = balance;
  }

  public String getTxnType() {
    return txnType;
  }

  public void setTxnType(String txnType) {
    this.txnType = txnType;
  }

  public String getR2rTransfer() {
    return r2rTransfer;
  }

  public void setR2rTransfer(String r2rTransfer) {
    this.r2rTransfer = r2rTransfer;
  }

  public String getOperator() {
    return operator;
  }

  public void setOperator(String operator) {
    this.operator = operator;
  }

  public String getOperatorId() {
    return operatorId;
  }

  public void setOperatorId(String operatorId) {
    this.operatorId = operatorId;
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


  public String getRmnNumber() {
    return rmnNumber;
  }

  public void setRmnNumber(String rmnNumber) {
    this.rmnNumber = rmnNumber;
  }

  public String getMode() {
    return mode;
  }

  public void setMode(String mode) {
    this.mode = mode;
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

  public String getGst() {
    return gst;
  }

  public void setGst(String gst) {
    this.gst = gst;
  }

  public String getRefId() {
    return refId;
  }

  public void setRefId(String refId) {
    this.refId = refId;
  }

  public String getOpId() {
    return opId;
  }

  public void setOpId(String opId) {
    this.opId = opId;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel parcel, int i) {
    parcel.writeString(id);
    parcel.writeString(date);
    parcel.writeString(mobileNumber);
    parcel.writeString(rRNo);
    parcel.writeString(number);
    parcel.writeString(apiId);
    parcel.writeString(description);
    parcel.writeString(beneName);
    parcel.writeString(beneAccount);
    parcel.writeString(ifscCode);
    parcel.writeString(bankName);
    parcel.writeString(remark);
    parcel.writeString(userName);
    parcel.writeString(product);
    parcel.writeString(txnId);
    parcel.writeString(providerName);
    parcel.writeString(amount);
    parcel.writeString(billerName);
    parcel.writeString(type);
    parcel.writeString(openingBalance);
    parcel.writeString(credtAmount);
    parcel.writeString(debitAmount);
    parcel.writeString(tds);
    parcel.writeString(gst);
    parcel.writeString(serviceTax);
    parcel.writeString(balance);
    parcel.writeString(txnType);
    parcel.writeString(r2rTransfer);
    parcel.writeString(refId);
    parcel.writeString(opId);
    parcel.writeString(operator);
    parcel.writeString(operatorId);
    parcel.writeString(status);
    parcel.writeString(statusId);
    parcel.writeString(rmnNumber);
    parcel.writeString(mode);
    parcel.writeString(name);
    parcel.writeByte((byte) (isHide ? 1 : 0));
    parcel.writeByte((byte) (isStatus ? 1 : 0));
  }
}
