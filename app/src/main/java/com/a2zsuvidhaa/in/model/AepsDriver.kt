package com.a2zsuvidhaa.`in`.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
@Keep
data class AepsDriver(
    @SerializedName("driver_name") val driverName : String,
    @SerializedName("package_name")val packageName : String
) : Parcelable