package com.a2zsuvidhaa.`in`.data.model.dmt

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class DmtCommission(
    @SerializedName("txnAmount") val txnAmount: String? = null,
    @SerializedName("total") val total: String? = null,
    @SerializedName("charge") val charge: String? = null,
    val serialNumber: Int
)


@Keep
data class DmtCommissionResponse(

    @SerializedName("status") var status: Int,
    @SerializedName("message") var message: String,
    @SerializedName("totalAmount") var totalAmount: String? = null,
    @SerializedName("result") var commissions: ArrayList<DmtCommission>? = null

)