package com.a2zsuvidhaa.`in`.data.response

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize


@Keep
@Parcelize
data class DocumentKycDetail(
        val shop_image_status: String,
        val pan_card_image_status: String,
        val seal_image_status: String,
        val aadhaar_front_image_status: String,
        val aadhaar_back_image_status: String,
        val cheque_image_status: String,
        val profile_image_status: String,
        val gst_image_status: String,
        val user_id: String,

        val shop_image: String,
        val pan_card_image: String,
        val seal_image: String,
        val aadhaar_card_image: String,
        val aadhaar_img_back: String,
        val cheque_image: String,
        val profile_picture: String,
        val gst_image: String,
        val is_pan_verified: Int?,
        val is_aadhaar_kyc: Int?,
        val aeps_kyc: Int?,
        val is_video_kyc: Int?

):Parcelable

@Keep
@Parcelize
data class DocumentKycResponse(
        val status : Int,
        val message : String,
        val data : DocumentKycDetail
):Parcelable