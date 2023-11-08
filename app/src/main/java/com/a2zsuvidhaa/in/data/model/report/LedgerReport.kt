package com.a2zsuvidhaa.`in`.data.model.report

import com.google.gson.annotations.SerializedName

data class LedgerReportResponse(

    @SerializedName("status") var status: Int? = null,
    @SerializedName("reports") var reports: ArrayList<LedgerReport>? = null,
    @SerializedName("count") var count: Int? = null,
    @SerializedName("next_page") var nextPage: Int? = null,
    @SerializedName("prev_page") var prevPage: Int? = null,

    )


data class LedgerReport(

    @SerializedName("id") var id: Int? = null,
    @SerializedName("transaction_type_id") var transactionTypeId: String? = null,
    @SerializedName("txn_time") var txnTime: String? = null,
    @SerializedName("remark") var remark: String? = null,
    @SerializedName("mode") var mode: String? = null,
    @SerializedName("operator_id") var operatorId: String? = null,
    @SerializedName("sender_number") var senderNumber: String? = null,
    @SerializedName("number") var number: String? = null,
    @SerializedName("service_name") var serviceName: String? = null,
    @SerializedName("biller_name") var billerName: String? = null,
    @SerializedName("bene_name") var beneName: String? = null,
    @SerializedName("bank_name") var bankName: String? = null,
    @SerializedName("ifsc") var ifsc: String? = null,
    @SerializedName("provider") var providerName: String? = null,
    @SerializedName("user_details") var userDetails: String? = null,
    @SerializedName("amount") var amount: String? = null,
    @SerializedName("credit") var credit: String? = null,
    @SerializedName("debit") var debit: String? = null,
    @SerializedName("tds") var tds: String? = null,
    @SerializedName("gst") var gst: String? = null,
    @SerializedName("op_balance") var opBalance: String? = null,
    @SerializedName("cl_balance") var clBalance: String? = null,
    @SerializedName("txn_type") var txnType: String? = null,
    @SerializedName("status_id") var statusId: Int? = null,
    @SerializedName("status_desc") var statusDesc: String? = null,
    @SerializedName("is_check_status") var isCheckStatus: Boolean,
    @SerializedName("is_print") var isPrint: Boolean,
    @SerializedName("is_complain") var isComplain: Boolean,
    var isExpanded: Boolean = false

)