package com.a2zsuvidhaa.`in`.data.service

import com.a2zsuvidhaa.`in`.data.response.CommonResponse
import com.a2zsuvidhaa.`in`.model.*
import com.google.android.gms.common.internal.service.Common
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface FundRequestService {

    @GET("get-bank-details")
    suspend fun fetchBankList(
            @Query("userId") userId: String,
            @Query("token") token: String,
            @Query("type") type: String
    ): Response<BankDetailResponse>

/*
    @FormUrlEncoded
    @POST("fund-request-save")
    suspend fun submitRequest(
            @Field("userId") userId: String,
            @Field("token") token: String,
            @Field("amount") amount: String,
            @Field("requestTo") requestTo: String,
            @Field("paymentDate") paymentDate: String,
            @Field("paymentMode") paymentMode: String,
            @Field("refNumber") refNumber: String,
            @Field("onlineMode") onlineMode: String,
            @Field("bankId") bankId: String,
            @Field("bankName") bankName: String,
            @Field("d_picture") fileSlip: String,
            @Field("remark") remark: String,
            @Field("mime_type") mimeType: String,
    ): Response<CommonResponse>*/


    @Multipart
    @POST("fund-request-save-new")
    suspend fun submitRequest(
            @Part slipFile: MultipartBody.Part?,
            @Part("amount") amount: RequestBody,
            @Part("requestTo") requestTo: RequestBody,
            @Part("paymentDate") paymentDate: RequestBody,
            @Part("paymentMode") paymentMode: RequestBody,
            @Part("refNumber") refNumber: RequestBody,
            @Part("onlineMode") onlineMode: RequestBody,
            @Part("bankId") bankId: RequestBody?,
            @Part("bankName") bankName: RequestBody?,
            @Part("remark") remark: RequestBody
            ): Response<CommonResponse>


}