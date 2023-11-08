package com.a2zsuvidhaa.`in`.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class QRCodeResponse(
        val status : Int,
        val message : String,
        @SerializedName("str_url")
        val strUrl: String,
        @SerializedName("retailer_qr_upi_data")
        val retailerQRUpiData : String,
        @SerializedName("dynamic_vpn_info")
        val dynamicVpnInfo: String
)