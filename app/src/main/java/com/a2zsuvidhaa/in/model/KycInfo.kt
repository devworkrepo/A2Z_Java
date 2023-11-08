package com.a2zsuvidhaa.`in`.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Parcelize
@Keep
data class KycInfo(

        val is_user_has_active_settlemnet_account : Int,
        val is_aadhaar_kyc : Int,
        val is_video_kyc : Int,
        val aeps_kyc : Int,

):Parcelable

@Parcelize
@Keep
data class  KycInfoResponse (
        val status : Int,
        val message : String,
        val data : KycInfo
        ) : Parcelable