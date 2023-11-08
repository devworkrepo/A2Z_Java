package com.a2zsuvidhaa.`in`.data.response.matm

import android.os.Parcelable
import androidx.annotation.Keep
import com.a2zsuvidhaa.`in`.data.model.matm.MatmInitiate
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class MatmInitiateResponse(
    val status : Int,
    val message : String,
    val data : MatmInitiate?
) : Parcelable