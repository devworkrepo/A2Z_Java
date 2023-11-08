package com.a2zsuvidhaa.`in`.data.service

import com.a2zsuvidhaa.`in`.data.response.CommonResponse
import com.a2zsuvidhaa.`in`.data.response.RegistrationCommonResponse
import com.a2zsuvidhaa.`in`.model.Login
import com.a2zsuvidhaa.`in`.model.LoginData
import com.a2zsuvidhaa.`in`.util.ents.FieldMapData
import com.google.android.gms.common.internal.service.Common
import retrofit2.Response
import retrofit2.http.*

interface AuthService {


    @FormUrlEncoded
    @POST("agentLogin")
    suspend fun login(
        @Field("case") case: String,
        @Field("password") password: String,
        @Field("mobileNumber") mobileNumber: String,
        @Field("device_token") deviceToken: String,
        @Field("hardwareSerialNumber") hardwareSerialNumber: String,
        @Field("deviceName") deviceName: String,
        @Field("imei") imei: String,
        @Field("latitude") latitude: String,
        @Field("longitude") longitude: String,
    ): Response<Login>

    @FormUrlEncoded
    @POST("login-id/creation/send-otp")
    suspend fun createLoginId(@FieldMap data: FieldMapData): Response<CommonResponse>

  @FormUrlEncoded
    @POST("login-id/creation/verify-otp")
    suspend fun verifyLoginId(@FieldMap data: FieldMapData): Response<CommonResponse>


    @FormUrlEncoded
    @POST("login-id/forget/send-otp")
    suspend fun forgotLoginId(@FieldMap data: FieldMapData): Response<CommonResponse>


    @FormUrlEncoded
    @POST("login-id/forget/verify-otp")
    suspend fun forgotLoginIdVerify(@FieldMap data: FieldMapData): Response<CommonResponse>

}