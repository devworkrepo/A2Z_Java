package com.a2zsuvidhaa.`in`.data.model.dmt

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Keep
@Parcelize
data class AccountVerify(

    @SerializedName("status") var status: Int,
    @SerializedName("message") var message: String,
    @SerializedName("txnId") var txnId: String? = null,
    @SerializedName("type") var type: Int? = null,
    @SerializedName("beneName") var beneName: String? = null,
    @SerializedName("bene_name") var upiBeneName: String? = null,
    @SerializedName("isVerified") var isVerified: Int? = null,
    @SerializedName("reportId") var reportId: String? = null

) : Parcelable



@Keep
@Parcelize
data class VpaVerificationChargeResponse(
        val status : Int,
        val message : String,
        val data : VpaVerificationCharge? = null
) : Parcelable



@Keep
@Parcelize
data class VpaVerificationCharge(
        @SerializedName("charge_amount") val chargeAmount : String
) : Parcelable

