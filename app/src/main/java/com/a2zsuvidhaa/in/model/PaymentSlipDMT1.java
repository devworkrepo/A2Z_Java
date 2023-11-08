package com.a2zsuvidhaa.in.model;

public class PaymentSlipDMT1 {

    private String txnId;
    private String refNo;
    private String amount;
    private String txnTIme;
    private String status;


    public PaymentSlipDMT1() {
    }

    public PaymentSlipDMT1(String txnId, String refNo, String amount, String txnTIme, String status)
    {
        this.txnId = txnId;
        this.refNo = refNo;
        this.amount = amount;
        this.txnTIme = txnTIme;
        this.status = status;
    }

    public String getTxnId() {
        return txnId;
    }

    public void setTxnId(String txnId) {
        this.txnId = txnId;
    }

    public String getRefNo() {
        return refNo;
    }

    public void setRefNo(String refNo) {
        this.refNo = refNo;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTxnTIme() {
        return txnTIme;
    }

    public void setTxnTIme(String txnTIme) {
        this.txnTIme = txnTIme;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
