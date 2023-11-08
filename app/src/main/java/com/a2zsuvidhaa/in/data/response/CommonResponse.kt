package com.a2zsuvidhaa.`in`.data.response

import androidx.annotation.Keep
import com.a2zsuvidhaa.`in`.util.AppConstants
import com.google.gson.annotations.SerializedName

@Keep
data class CommonResponse(
        val status : Int=0,
        val code : Int=0,
        val message : String="",
        val verifyId : String = "",
        val state : String = ""
)



@Keep
data class AadhaarKycResponse(
        val status : Int,
        val message : String,
        @SerializedName("request_id")
        val requestId : String = AppConstants.EMPTY
)