package com.a2zsuvidhaa.`in`.data.model.dmt


import com.google.gson.annotations.SerializedName


data class BankListResponse(

    @SerializedName("status") var status: Int,
    @SerializedName("message") var message: String,
    @SerializedName("data") var data: ArrayList<Bank>? = null

)


data class Bank(

    @SerializedName("bank_name") var bankName: String? = null,
    @SerializedName("id") var id: Int? = null,
    @SerializedName("ifsc") var ifsc: String? = null,
    @SerializedName("account_digit") var accountDigit: Int? = null,

)