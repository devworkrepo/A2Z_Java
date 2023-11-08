package com.a2zsuvidhaa.in.model;

public class PGModel {

    String charge_type ;
    String mdd;
    String payment_mode;
    String txn_charge;
    String gst_rate ;
    String id;
    String status;

    public String getMdd() {
        return mdd;
    }

    public void setMdd(String mdd) {
        this.mdd = mdd;
    }


    public String getPayment_mode() {
        return payment_mode;
    }

    public void setPayment_mode(String payment_mode) {
        this.payment_mode = payment_mode;
    }

    public String getTxn_charge() {
        return txn_charge;
    }

    public void setTxn_charge(String txn_charge) {
        this.txn_charge = txn_charge;
    }

    public String getCharge_type() {
        return charge_type;
    }

    public void setCharge_type(String charge_type) {
        this.charge_type = charge_type;
    }

    public String getGst_rate() {
        return gst_rate;
    }

    public void setGst_rate(String gst_rate) {
        this.gst_rate = gst_rate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}
