package com.a2zsuvidhaa.`in`.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.a2zsuvidhaa.`in`.util.AppConstants

import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


data class DmtTransaction(
    val txnId: String = AppConstants.EMPTY,
    val bankRef: String = AppConstants.EMPTY,
    val statusDesc: String = AppConstants.EMPTY,
    val amount: String = AppConstants.EMPTY,
    val txnTime: String = AppConstants.EMPTY,
    val message: String = AppConstants.EMPTY,
    val status: Int = 0,
    val chargeAmount: String = AppConstants.EMPTY,
    val totalTxnDebitAmount: String = AppConstants.EMPTY
)

@Keep
@Parcelize
data class DmtTransactionResponse(

    @SerializedName("txnId") var txnId: Int? = null,
    @SerializedName("report_id") var reportId: Int? = null,
    @SerializedName("pay_type") var payType: Int? = null,
    @SerializedName("refNo") var refNo: Int? = null,
    @SerializedName("status") var status: Int,
    @SerializedName("message") var message: String,
    @SerializedName("statusDesc") var statusDesc: String? = null,
    @SerializedName("walletName") var walletName: String? = null,
    @SerializedName("txnTime") var txnTime: String? = null,
    @SerializedName("amount") var amount: String? = null,
    @SerializedName("accountNumber") var accountNumber: String? = null,
    @SerializedName("bankName") var bankName: String? = null,
    @SerializedName("transactionType") var transactionType: String? = null,
    @SerializedName("beneName") var beneName: String? = null,
    @SerializedName("ifsc") var ifsc: String? = null,
    @SerializedName("senderData") var senderData: SenderData? = null,
    @SerializedName("bankRef") var bankRef: String? = null,
    @SerializedName("api_id") var apiId: Int? = null

) : Parcelable

@Parcelize
@Keep
data class SenderData(

    @SerializedName("name") var name: String? = null,
    @SerializedName("mobile") var mobile: String? = null,
    @SerializedName("outlet_name") var outletName: String? = null

) : Parcelable