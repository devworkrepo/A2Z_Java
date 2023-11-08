package com.a2zsuvidhaa.`in`.data.model.dmt

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class SenderAccountDetailResponse(
    val status: Int,
    val message: String,
    @SerializedName("data") val senderAccountDetail: List<SenderAccountDetail>?
) : Parcelable

@Keep
@Parcelize
data class SenderAccountDetail(

    @SerializedName("id") val id: String,
    @SerializedName("ifsc") val ifscCode: String,
    @SerializedName("customer_number") val mobileNumber: String,
    @SerializedName("bank_name") val bankName: String,
    @SerializedName("rem_bal") val remainingLimit: String,
    @SerializedName("my_wallet_two") val remainingLimit2: String,
    @SerializedName("my_wallet_three") val remainingLimit3: String,
    @SerializedName("name") val name: String,
    @SerializedName("last_success_time") val lastSuccessTime: String?,
) : Parcelable
