package com.a2zsuvidhaa.`in`.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class AgreementInitialInfoResponse(
    val status: Int,
    val message: String,
    val data: AgreementInitialInfo?
) : Parcelable

@Parcelize
data class AgreementInitialInfo(
    @SerializedName("agreement_url")
    val agreementUrl: String?,
    val name: String?,
    val email: String?,
    val mobile: String?
) : Parcelable


@Parcelize
data class AgreementStartResponse(
    val status: Int,
    val message: String,
    val url: String,
    @SerializedName("order_id")
    val orderId: String

) : Parcelable