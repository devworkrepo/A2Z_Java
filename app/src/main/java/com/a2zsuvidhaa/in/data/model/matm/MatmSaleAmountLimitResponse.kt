package com.a2zsuvidhaa.`in`.data.model.matm

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class MatmSaleAmountLimitResponse(
    val status: Int,
    val message: String,
    val minAmount: String? = null,
    val maxAmount: String? = null

) : Parcelable