package com.a2zsuvidhaa.`in`.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.a2zsuvidhaa.`in`.util.AppConstants
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class BankDetail(
        var id: String = AppConstants.EMPTY,
        var mobile: String = AppConstants.EMPTY,
        var bank_transfer_place_holder: String = AppConstants.EMPTY,
        var cash_deposit_place_holder: String = AppConstants.EMPTY,
        var cash_cdm_place_holder: String = AppConstants.EMPTY,
        var account_number: String = AppConstants.EMPTY,
        var ifsc: String = AppConstants.EMPTY,
        @SerializedName("branch_name")
        var branchName: String = AppConstants.EMPTY,
        @SerializedName("bank_name")
        var bankName: String = AppConstants.EMPTY,
        @SerializedName("message_one")
        var messageOne: String = AppConstants.EMPTY,
        @SerializedName("message_two")
        var messageTwo: String = AppConstants.EMPTY,
        var status: String = AppConstants.EMPTY,
        @SerializedName("charge")
        var charges: String = AppConstants.EMPTY,
        var balance: String = AppConstants.EMPTY,
        var aeps_bloack_amount: String = AppConstants.EMPTY,
        var aeps_charge: String = AppConstants.EMPTY,
        var beneName: String = AppConstants.EMPTY,
        var document_status: Int = 0,
        var isHide: Boolean = false,
):Parcelable


@Parcelize
@Keep
data class BankDetailResponse(
        val status : Int,
        val message : String,
        val banks : List<BankDetail>
):Parcelable

