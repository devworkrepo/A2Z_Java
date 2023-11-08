package com.a2zsuvidhaa.`in`.data.model.dmt

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
@Keep
data class MoneySenderResponse(

    @SerializedName("status") var status: Int,
    @SerializedName("message") var message: String,
    @SerializedName("state") var state: String?=null,
    @SerializedName("data", alternate = ["remitter_details"])
    var moneySender: MoneySender? = MoneySender()

) : Parcelable

@Parcelize
@Keep
data class MoneySender(

    @SerializedName("first_name") var firstName: String? = null,
    @SerializedName("last_name") var lastName: String? = null,
    @SerializedName("monthly_limit") var monthlyLimit: String? = null,
    @SerializedName("used_limit") var usedLimit: String? = null,
    @SerializedName("rem_bal", alternate = ["available_limit"]) var remBal: String? = null,
    @SerializedName("verify") var verify: String? = null,
    @SerializedName("mobile") var mobileNumber: String? = null

) : Parcelable