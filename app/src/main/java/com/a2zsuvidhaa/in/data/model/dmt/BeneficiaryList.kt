package com.a2zsuvidhaa.`in`.data.model.dmt

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class BeneficiaryListResponse(

    @SerializedName("status") var status: Int,
    @SerializedName("message") var message: String,
    @SerializedName("data") var data: ArrayList<Beneficiary>? = null

) : Parcelable

@Keep
@Parcelize
data class Beneficiary(

    @SerializedName("id") var id: String? = null,
    @SerializedName("account_number") var accountNumber: String? = null,
    @SerializedName("a2z_bene_id") var a2zBeneId: String? = null,
    @SerializedName("benficiary_id") var beneficiaryId: String? = null,
    @SerializedName("bank_name") var bankName: String? = null,
    @SerializedName("customer_number") var customerNumber: String? = null,
    @SerializedName("mobile_number") var mobileNumber: String? = null,
    @SerializedName("ifsc") var ifsc: String? = null,
    @SerializedName("beneId") var beneId: String? = null,
    @SerializedName("bank_verified") var bankVerified: Int? = null,
    @SerializedName("is_bank_verified") var upiBankVerified: Int? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("status_id") var statusId: Int? = null,
    @SerializedName("last_success_time") var lastSuccessTime: String? = null

) : Parcelable