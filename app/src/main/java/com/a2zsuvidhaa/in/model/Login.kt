package com.a2zsuvidhaa.`in`.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.common.collect.Lists
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Keep
@Parcelize
data class Login(
    val status: Int,
    val changePin: Int,
    val id: Int,
    val token: String,
    val name: String,
    val mobile: String,
    val email: String,
    val aeps_kyc: Int,
    val user_kyc: Int,
    val is_offline_kyc: Int,
    val is_pan_verified: Int,
    val is_mobile_verified: Int,
    val is_email_verified: Int,
    val user_balance: String,
    val otp_number: Int,
    val profile_picture: String,
    val role_id: Int,
    val role_title: String,
    val message: String,
    val shop_name: String,
    val address: String,
    val is_user_has_active_settlemnet_account: Int,
    val shop_address: String,
    val joining_date: String,
    val last_update: String,
    val state_id: Int,
    val pincode: String,
    val adhaar: String,
    val pancard_img: String,
    val adhaar_img: String,
    val adhaar_back_img: String,
    val kyc_status: String,
    val popup: String,
    val is_aadhaar_kyc: Int,
    val is_video_kyc: Int,
    val matm: String,
    val mpos: String,
    val is_login_id_created: String?,
    @SerializedName("aeps_drivers") val aepsDrivier: List<AepsDriver>

) : Parcelable