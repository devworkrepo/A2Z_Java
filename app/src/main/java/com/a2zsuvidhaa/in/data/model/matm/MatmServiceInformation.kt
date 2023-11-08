package com.a2zsuvidhaa.`in`.data.model.matm

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class MatmServiceInformation(
    @SerializedName("user_id") val userId: Int? = null,
    @SerializedName("panNumber") val panNumber: String? = null,
    @SerializedName("aadhaarNumber") val aadhaarNumber: String? = null,
    @SerializedName("city") val city: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("mobile") val mobile: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("gst_number") val gstNumber: String? = null,
    @SerializedName("shop_address") val shopAddress: String? = null,
    @SerializedName("shopName") val shopName: String? = null,
    @SerializedName("landmark") val landmark: String? = null,
    @SerializedName("pan_image_status") val pangImageStatus: String? = null,
    @SerializedName("pan_image") val panImage: String? = null,
    @SerializedName("address_proof_image_status") val addressProofImageStatus: String? = null,
    @SerializedName("address_proof_image") val addressProofImage: String? = null,
    @SerializedName("is_mAtm_received") val isMatmReceived: String? = null,
    @SerializedName("matm_service_status") val matmServiceStatus: String? = null,
    @SerializedName("service_status") val service_status: String? = null,
    @SerializedName("remark") val remark: String? = null,
    @SerializedName("pin_code") val pinCode: String? = null,
    @SerializedName("courier_address") val courierAddress: String? = null,
    //mpos
    @SerializedName("business_address_proof_type") val businessAddressType: String? = null,
    @SerializedName("business_address_proof_image_status") val business_address_proof_image_status: String? = null,
    @SerializedName("business_address_proof_image") val business_address_proof_image: String? = null,
    @SerializedName("mpos_service_status") val mpos_service_status: String? = null,
    @SerializedName("business_proof_type") val businessLegalityType: String? = null,
    @SerializedName("business_proof_image_status") val businessLegalityImageStatus: String? = null,
    @SerializedName("business_proof_image") val business_proof_image: String? = null,
    @SerializedName("shop_outside_image_status") val shop_outside_image_status: String? = null,
    @SerializedName("shop_outside_image") val shop_outside_image: String? = null,
    @SerializedName("shop_inside_image_status") val shop_inside_image_status: String? = null,
    @SerializedName("shop_inside_image") val shop_inside_image: String? = null,
    @SerializedName("otpVerify") val otpVerify: String? = null,

    ) : Parcelable

@Keep
@Parcelize
data class MatmServiceInformationfoResponse(
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: MatmServiceInformation? = null,
) : Parcelable