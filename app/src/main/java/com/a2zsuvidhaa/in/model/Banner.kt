package com.a2zsuvidhaa.`in`.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class Banner(
    val image: String
) : Parcelable

@Keep
@Parcelize
data class BannerResponse(
    val status: Int,
    val message: String,
    @SerializedName("data") val banners: List<Banner>?
) : Parcelable