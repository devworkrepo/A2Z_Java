package com.a2zsuvidhaa.`in`.data.model.matm

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Keep
@Parcelize
data class MatmTransactionResponse(
    @SerializedName("status") val status: Int? = null,
    @SerializedName("statusDescription") val statusDesc: String? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("service_name") val serviceName: String? = null,
    @SerializedName("customer_number") val customerNumber: String? = null,
    @SerializedName("txn_id") val txnId: String? = null,
    @SerializedName("order_id") val orderId: String? = null,
    @SerializedName("record_id") val recordId: String? = null,
    @SerializedName("transaction_type") val transactionType: String? = null,
    @SerializedName("card_number") val cardNumber: String? = null,
    @SerializedName("card_type") val cardType: String? = null,
    @SerializedName("credit_debit_card_type") val creditDebitCardType: String? = null,
    @SerializedName("available_amount") val availableAmount: String? = null,
    @SerializedName("transaction_amount") val transactionAmount: String? = null,
    @SerializedName("transaction_mode") val transactionMode: String? = null,
    @SerializedName("bank_ref") val bankRef: String? = null,
    @SerializedName("txn_time") val txnTime: String? = null,
    @SerializedName("shop_name") val shopName: String? = null,
    @SerializedName("retailer_number") val retailerNumber: String? = null,
    @SerializedName("retailer_name") val retailerName: String? = null,
    @SerializedName("outlet_address") val outletAddress: String? = null,
) : Parcelable

@Keep
@Parcelize
data class MatmPostResponse(
    val status : Int,
    val message : String,
    val data : MatmTransactionResponse? = null
) : Parcelable