package com.a2zsuvidhaa.`in`.data.service

import com.a2zsuvidhaa.`in`.data.model.dmt.MoneySenderResponse
import com.a2zsuvidhaa.`in`.data.model.dmt.SenderRegistrationResponse
import com.a2zsuvidhaa.`in`.data.response.CommonResponse
import com.a2zsuvidhaa.`in`.util.ents.FieldMapData
import retrofit2.Response
import retrofit2.http.*

interface DmtThreeService {

    @GET("dmt-three/mobile-verification")
    suspend fun searchMobileNumberDmtThree(@QueryMap data: FieldMapData): Response<MoneySenderResponse>


    @FormUrlEncoded
    @POST("dmt-three/remitter/send/otp")
    suspend fun registerSender(@FieldMap data: FieldMapData): Response<SenderRegistrationResponse>

    @FormUrlEncoded
    @POST("dmt-three/remitter/registration")
    suspend fun verifySender(@FieldMap data: FieldMapData): Response<CommonResponse>


    @FormUrlEncoded
    @POST("dmt-three/remitter/send/otp")
    suspend fun resendSenderRegistrationOtp(@FieldMap data: FieldMapData): Response<SenderRegistrationResponse>

}