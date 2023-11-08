package com.a2zsuvidhaa.`in`.data.model.dmt

import com.google.gson.annotations.SerializedName


data class BankDownCheckResponse(
         val status: Int,
         val message: String,
        @SerializedName("data")  val bankDownCheck: BankDownCheck? = null
)

data class BankDownCheck(
        @SerializedName("is_txn_allowed") val isTxnAllowed: Boolean? = null,
        @SerializedName("is_imps_allowed") val isImpsAllowed: Boolean? = null,
        @SerializedName("message") val message: String? = null,
        @SerializedName("ifsc") val ifsc: String? = null,
        @SerializedName("account_digit") val accountDigit: Int? = null
)