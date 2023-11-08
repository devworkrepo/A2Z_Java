package com.a2zsuvidhaa.`in`.data.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Keep
@Parcelize
data class TransactionDetailResponse(

    @SerializedName("status") var status: Int? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("data") var data: TransactionDetail? = null

) : Parcelable


@Keep
@Parcelize
data class TransactionDetail(

    @SerializedName("report_id") var reportId: String? = null,
    @SerializedName("status") var status: Int? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("status_desc") var statusDesc: String? = null,
    @SerializedName("txn_time") var txnTime: String? = null,
    @SerializedName("txn_type") var txnType: String? = null,
    @SerializedName("slip_type") var slipType: String? = null,
    @SerializedName("amount") var amount: String? = null,
    @SerializedName("payment_channel") var paymentChannel: String? = null,
    @SerializedName("agent_channel") var agentChannel: String? = null,
    @SerializedName("outlet_name") var outletName: String? = null,
    @SerializedName("outlet_address") var outletAddress: String? = null,
    @SerializedName("outlet_number") var outletNumber: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("ifsc") var ifsc: String? = null,
    @SerializedName("bank_name") var bankName: String? = null,
    @SerializedName("card_type") var cardType: String? = null,
    @SerializedName("bank_ref") var bankRef: String? = null,
    @SerializedName("number") var number: String? = null,
    @SerializedName("service_name") var serviceName: String? = null,
    @SerializedName("provider") var provider: String? = null,
    @SerializedName("sender_name", alternate = ["customer_number"]) var senderName: String? = null,
    @SerializedName("api_id") var apiId: String? = null,
    @SerializedName("sender_number") var senderNumber: String? = null,
    @SerializedName(
        "dmt_details",
        alternate = ["transaction_details"]
    ) var dmtDetails: ArrayList<DmtTransactionDetail>? = null,
    @SerializedName("statement") var miniStatement: ArrayList<MiniStatement>? = null,
    @SerializedName("total_amount") var totalAmount: String? = null,
    @SerializedName("available_balance") var availableBalance: String? = null,
    @SerializedName("disclaimer") var declaimers: List<String>? = null,

    ) : Parcelable

@Keep
@Parcelize
data class DmtTransactionDetail(

    @SerializedName("txn_time", alternate = ["txnTime"]) var txnTime: String? = null,
    @SerializedName("txn_id", alternate = ["txnId"]) var txnId: String? = null,
    @SerializedName("amount") var amount: String? = null,
    @SerializedName("status") var status: Int? = null,
    @SerializedName("status_desc", alternate = ["statusDesc"]) var statusDesc: String? = null,
    @SerializedName("bank_ref", alternate = ["bankRef"]) var bankRef: String? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("chargeAmount") var chargeAmount: String? = null,
    @SerializedName("totalTxnDebitAmount") var totalTxnDebitAmount: String? = null,
    @SerializedName("walletName") var walletName: String? = null,

    ) : Parcelable


@Keep
@Parcelize
data class MiniStatement(

    @SerializedName("date") var txnTime: String,
    @SerializedName("narration") var narration: String,
    @SerializedName("txnType") var txnType: String,
    @SerializedName("amount") var amount: String,

    ) : Parcelable