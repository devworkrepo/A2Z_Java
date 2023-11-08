package com.a2zsuvidhaa.`in`.data.service

import com.a2zsuvidhaa.`in`.data.model.BankDownResponse
import com.a2zsuvidhaa.`in`.data.model.dmt.*
import com.a2zsuvidhaa.`in`.data.response.CommonResponse
import com.a2zsuvidhaa.`in`.util.ents.FieldMapData
import retrofit2.Response
import retrofit2.http.*

interface UpiService {

    @GET("vpa/mobile/remitter")
    suspend fun searchSender(@QueryMap data: FieldMapData): Response<MoneySenderResponse>

    @GET("vpa/beneficiary")
    suspend fun beneficiaryList(@QueryMap data: FieldMapData): BeneficiaryListResponse

    @FormUrlEncoded
    @POST("vpa/validate")
    suspend fun accountValidation(@FieldMap data: FieldMapData): AccountVerify


    @FormUrlEncoded
    @POST("vpa/beneficiary/add")
    suspend fun addBeneficiary(@FieldMap data: FieldMapData): CommonResponse


    @GET("vpa/bank")
    suspend fun vpaList(): VpaBankExtensionResponse

    @GET("vpa/validation/charge")
    suspend fun verificationCharge(@QueryMap data : FieldMapData): VpaVerificationChargeResponse


   @GET("vpa/checkdown")
    suspend fun bankDownCheck(@QueryMap data : FieldMapData): BankDownCheckResponse


}