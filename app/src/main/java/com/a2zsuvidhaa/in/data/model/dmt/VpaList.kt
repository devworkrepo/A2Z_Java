package com.a2zsuvidhaa.`in`.data.model.dmt

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Keep
@Parcelize
data class VpaBankExtensionResponse(

        @SerializedName("status") var status: Int,
        @SerializedName("message") var message: String,
        @SerializedName("upiBank") var upiBank: ArrayList<UpiBank>? = null

) : Parcelable


@Keep
@Parcelize
data class UpiExtension(

        @SerializedName("id") var id: String? = null,
        @SerializedName("name") var name: String? = null,
        @SerializedName("upi_bank_id") var upiBankId: Int? = null

) : Parcelable



@Keep
@Parcelize
data class UpiBank(

        @SerializedName("id") var id: String? = null,
        @SerializedName("name") var name: String? = null,
        @SerializedName("upiextensions") var upiextensions: ArrayList<UpiExtension>? = null

) : Parcelable