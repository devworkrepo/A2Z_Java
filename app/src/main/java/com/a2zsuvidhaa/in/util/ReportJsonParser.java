package com.a2zsuvidhaa.in.util;

import com.a2zsuvidhaa.in.model.PaymentReport;
import com.a2zsuvidhaa.in.util.enums.PaymentReportType;
import com.a2zsuvidhaa.in.util.enums.ReportTypes;
import com.a2zsuvidhaa.in.model.Report;
import com.a2zsuvidhaa.in.model.FundReport;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ReportJsonParser {
    private JSONArray jsonArray;

    public ReportJsonParser(JSONArray jsonArray) {
        this.jsonArray = jsonArray;
    }

    public ArrayList<Report> parseReportData(ReportTypes REPORT_TYPE) throws JSONException {
        ArrayList<Report> reportList = new ArrayList<>();

        if(REPORT_TYPE == ReportTypes.LEDGER_REPORT){
            for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    String id = jsonObject.getString("id");
                    String created_at = jsonObject.getString("created_at");
                    String rRNo = jsonObject.getString("rRNo");
                    String remitter_number = jsonObject.getString("remitter_number");
                    String number = jsonObject.getString("number");
                    String api_id = jsonObject.getString("api_id");
                    String description = jsonObject.getString("description");
                    String bene_name = jsonObject.getString("bene_name");
                    String bene_accont = jsonObject.getString("bene_accont");
                    String ifsc_code = jsonObject.getString("ifsc_code");
                    String bank_name = jsonObject.getString("bank_name");
                    String remark = jsonObject.getString("remark");
                    String userName = jsonObject.getString("userName");
                    String product = jsonObject.getString("product");
                    String txnId = jsonObject.getString("txnId");
                    String providerName = jsonObject.getString("providerName");
                    String amount = jsonObject.getString("amount");
                    String billerName = jsonObject.getString("billerName");
                    String type1 = jsonObject.getString("credit_debit");
                    String opening_balance = jsonObject.getString("opening_balance");
                    String credit_amount = jsonObject.getString("credit_amount");
                    String debit_amount = jsonObject.getString("debit_amount");
                    String tds = jsonObject.getString("tds");
                    String service_tax = jsonObject.getString("service_tax");
                    String balance = jsonObject.getString("balance");
                    String txn_type = jsonObject.getString("txn_type");
                    String r2r_transfer = jsonObject.getString("r2r_transfer");
                    String operator = jsonObject.getString("operator");
                    String op_id = jsonObject.getString("op_id");
                    String status = jsonObject.getString("status");
                    String statusId = jsonObject.getString("statusId");

                    Report report = new Report(id,created_at,remitter_number,rRNo,number,api_id,description,
                            bene_name,bene_accont,ifsc_code,bank_name,remark,userName,product,txnId,providerName,
                            amount,billerName,type1,opening_balance,credit_amount,debit_amount,tds,
                            service_tax,balance,txn_type,r2r_transfer,operator,op_id,status,statusId);

                    reportList.add(report);

            }
        }
        else if(REPORT_TYPE == ReportTypes.USAGE_REPORT){
            for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    String id = jsonObject.getString("id");
                    String created_at = jsonObject.getString("created_at");
                    String rmn_no = jsonObject.getString("rmn_no");
                    String bene_name = jsonObject.getString("bene_name");
                    String bene_accont = jsonObject.getString("bene_accont");
                    String ifsc_code = jsonObject.getString("ifsc_code");
                    String bank_name = jsonObject.getString("bank_name");
                    String remiter_number = jsonObject.getString("remiter_number");
                    String amount = jsonObject.getString("amount");
                    String number = jsonObject.getString("number");
                    String type1 = jsonObject.getString("type");
                    String per_name = jsonObject.getString("per_name");
                    String txn_type = jsonObject.getString("txn_type");
                    String operator = jsonObject.getString("operator");
                    String op_id = jsonObject.getString("op_id");
                    String status = jsonObject.getString("status");
                    String statusId = jsonObject.getString("statusId");

                    Report report = new Report(id,created_at,remiter_number,bene_name,bene_accont
                            ,ifsc_code,bank_name,amount,number,type1,txn_type,operator,op_id,status,statusId,rmn_no,per_name);

                    reportList.add(report);
            }
        }
        else if(REPORT_TYPE == ReportTypes.ACCOUNT_STATEMENT){
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String id = jsonObject.getString("id");
                String created_at = jsonObject.getString("created_at");
                String user_name = jsonObject.getString("user_name");
                String mobile_number = jsonObject.getString("mobile_number");
                String product = jsonObject.getString("product");
                String bank_name = jsonObject.getString("bank_name");
                String name = jsonObject.getString("name");
                String number = jsonObject.getString("number");
                String txn_id = jsonObject.getString("txn_id");
                String description = jsonObject.getString("description");
                String opening_bal = jsonObject.getString("opening_bal");
                String amount = jsonObject.getString("amount");
                String credit_charge = jsonObject.getString("credit_charge");
                String debit_charge = jsonObject.getString("debit_charge");
                String total_bal = jsonObject.getString("total_bal");
                String remark = jsonObject.getString("remark");
                String statusId = jsonObject.getString("statusId");
                String status = jsonObject.getString("status");
                String mode = jsonObject.getString("mode");

                Report report = new Report(id,created_at,mobile_number,number,description,bank_name,
                        remark,user_name,name,product,txn_id,amount,opening_bal,
                        credit_charge,debit_charge,total_bal,status,statusId,mode);

                reportList.add(report);
            }
        }
        else if(REPORT_TYPE == ReportTypes.NETWORK_RECHARGE){
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String id = jsonObject.getString("id");
                String created_at = jsonObject.getString("created_at");
                String user_name = jsonObject.getString("user_name");
                String number = jsonObject.getString("number");
                String mobile_number = jsonObject.getString("mobile_number");
                String provider = jsonObject.getString("provider");
                String gst = jsonObject.getString("gst");
                String tds = jsonObject.getString("tds");
                String txn_type = jsonObject.getString("txn_type");
                String txn_id = jsonObject.getString("txn_id");
                String op_id = jsonObject.getString("op_id");
                String amount = jsonObject.getString("amount");
                String credit_amount = jsonObject.getString("credit_amount");
                String debit_amount = jsonObject.getString("debit_amount");
                String statusId = jsonObject.getString("statusId");
                String status = jsonObject.getString("status");
                String mode = jsonObject.getString("mode");

                Report report = new Report(id,created_at,number,mobile_number,user_name,txn_id,"",provider,amount,gst,
                        credit_amount,debit_amount,tds,txn_type,status,statusId,mode,op_id);

                reportList.add(report);
            }
        }
        return reportList;
    }

    public ArrayList<FundReport> parseFundRequest(ReportTypes reportTypes) throws JSONException {
        ArrayList<FundReport> reportList = new ArrayList<>();

        if(reportTypes == ReportTypes.FUND_REPORT){
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String id = jsonObject.getString("id");
                String created_at = jsonObject.getString("created_at");
                String username = jsonObject.getString("username");
                String deposit_date = jsonObject.getString("deposit_date");
                String bank_name = jsonObject.getString("bank_name");
                String account_number = jsonObject.getString("account_number");
                String wallet_amount = jsonObject.getString("wallet_amount");
                String request_for = jsonObject.getString("request_for");
                String bank_ref = jsonObject.getString("bank_ref");
                String payment_mode = jsonObject.getString("payment_mode");
                String branch_name = jsonObject.getString("branch_name");
                String request_remark = jsonObject.getString("request_remark");
                String approval_remark = jsonObject.getString("approval_remark");
                String update_remark = jsonObject.getString("update_remark");
                String status = jsonObject.getString("status");
                String status_id = jsonObject.getString("status_id");


                FundReport report = new FundReport(
                        id,created_at,username,deposit_date,bank_name,account_number,wallet_amount,
                        request_for,bank_ref,payment_mode,branch_name,request_remark,approval_remark,
                        update_remark,status,status_id
                );
                reportList.add(report);
            }
        }
        else if(reportTypes == ReportTypes.DT_FUND_REPORT){
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);


                String created_at = jsonObject.getString("created_at");
                String order_id = jsonObject.getString("order_id");
                String wallet = jsonObject.getString("wallet");
                String number = jsonObject.getString("number");
                String username = jsonObject.getString("username");
                String user_id = jsonObject.getString("user_id");
                String transfer_to_from = jsonObject.getString("transfer_to_from");
                String firm_name = jsonObject.getString("firm_name");
                String ref_id = jsonObject.getString("ref_id");
                String description = jsonObject.getString("description");
                String opening_bal = jsonObject.getString("opening_bal");
                String credit_amount = jsonObject.getString("credit_amount");
                String closing_bal = jsonObject.getString("closing_bal");
                String bank_charge = jsonObject.getString("bank_charge");
                String remark = jsonObject.getString("remark");
                String status = jsonObject.getString("status");


                FundReport report = new FundReport(
                        order_id,created_at,username,status,wallet,user_id,transfer_to_from,
                        firm_name,ref_id,description,opening_bal,credit_amount,closing_bal,bank_charge,remark,number,
                        "ignore"
                );
                reportList.add(report);
            }
        }

        return reportList;
    }

    public ArrayList<PaymentReport> parsePaymentReport(PaymentReportType paymentReportType) throws JSONException {
        ArrayList<PaymentReport> paymentList = new ArrayList<>();

        if(paymentReportType == PaymentReportType.FUND_TRANSFER_REPORT){
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String date = jsonObject.optString("date");
                String request_date = jsonObject.optString("request_date");
                String update_date = jsonObject.optString("update_date");
                String order_id = jsonObject.optString("order_id");
                String wallet = jsonObject.optString("wallet");
                String user = jsonObject.optString("user");
                String transfer_to_from = jsonObject.optString("transfer_to_from");
                String firm_name = jsonObject.optString("firm_name");
                String ref_id = jsonObject.optString("ref_id");
                String description = jsonObject.optString("description");
                String bank_ref = jsonObject.optString("bank_ref");
                String agent_remark = jsonObject.optString("agent_remark");
                String opening_bal = jsonObject.optString("opening_bal");
                String credit_amount = jsonObject.optString("credit_amount");
                String closing_bal = jsonObject.optString("closing_bal");
                String bank_charge = jsonObject.optString("bank_charge");
                String remark = jsonObject.optString("remark");
                String status = jsonObject.optString("status");
                String status_id = jsonObject.optString("status_id");


                PaymentReport report = new PaymentReport(date,order_id,ref_id,remark,status,status_id,
                        request_date,update_date,wallet,user,transfer_to_from,firm_name,description,
                        bank_ref,agent_remark,opening_bal,credit_amount,closing_bal,bank_charge);
                paymentList.add(report);
            }
        }

        else if(paymentReportType == PaymentReportType.PAYMENT_REPORT){
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);


                String date = jsonObject.getString("date");
                String id = jsonObject.getString("id");
                String request_to = jsonObject.getString("request_to");
                String bank_name = jsonObject.getString("bank_name");
                String mode = jsonObject.getString("mode");
                String branch_code = jsonObject.getString("branch_code");
                String deposit_date = jsonObject.getString("deposit_date");
                String amount = jsonObject.getString("amount");
                String deposit_slip = jsonObject.getString("deposit_slip");
                String customer_remark = jsonObject.getString("customer_remark");
                String ref_id = jsonObject.getString("ref_id");
                String updated_remark = jsonObject.getString("updated_remark");
                String remark = jsonObject.getString("remark");
                String status = jsonObject.getString("status");
                String status_id = jsonObject.getString("status_id");

                PaymentReport fundTransfer=new PaymentReport(
                        date,id,request_to,bank_name,mode,branch_code,deposit_date,amount,deposit_slip,
                        customer_remark,ref_id,updated_remark,remark,status,status_id
                );
                paymentList.add(fundTransfer);
            }
        }

        return paymentList;
    }

}
