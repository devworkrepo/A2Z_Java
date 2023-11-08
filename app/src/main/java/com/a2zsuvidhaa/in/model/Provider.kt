package com.a2zsuvidhaa.`in`.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize


@Keep
@Parcelize
data class Provider(
        val id: String,
        val providerName: String,
        val providerImage: String,
        val serviceId: String,
        val dealerName: String,
        val extraParams: String,
        val isAmountEditable: Int = 0,
        val payWithoutFetch: Int = 0,
        val isNumeric: Int = 0,
        val isAlphaNumeric: Int = 0,
        val minLength: Int = 0,
        val maxLength: Int = 0,
        val defaultIcon : Int,
        val minAmount : Double,
        val maxAmount : Double,
        val amountMessage : String
): Parcelable