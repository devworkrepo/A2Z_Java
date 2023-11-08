package com.a2zsuvidhaa.`in`.data.service

import com.a2zsuvidhaa.`in`.data.response.AadhaarKycResponse
import com.a2zsuvidhaa.`in`.data.response.CommonResponse
import com.a2zsuvidhaa.`in`.data.response.DocumentKycResponse
import com.a2zsuvidhaa.`in`.model.BalanceResponse
import com.a2zsuvidhaa.`in`.model.QRCodeResponse
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface KycService {

    @FormUrlEncoded
    @POST("aadhaar/kyc/send-otp")
    suspend fun postAadharKyc(
        @Field("aadhaar_number") aadhaarNumber: String,
        @Field("mobile") mobileNumber: String,
        @Field("latitude") latitude: String,
        @Field("longitude") longitude: String,
    ): Response<AadhaarKycResponse>


    @FormUrlEncoded
    @POST("sales/created/member/aadhaar/verify")
    suspend fun registerUserPostAadharKyc(
        @Field("aadhaar_number") aadhaarNumber: String,
        @Field("mobile") mobileNumber: String,
        @Field("userId") userId: String,
        @Field("latitude") latitude: String,
        @Field("longitude") longitude: String,
    ): Response<AadhaarKycResponse>


    @FormUrlEncoded
    @POST("aadhaar/kyc/verify-otp")
    suspend fun verifyAadharKyc(
        @Field("request_id") requestId: String,
        @Field("otp") otp: String,
        @Field("latitude") latitude: String,
        @Field("longitude") longitude: String,
    ): Response<AadhaarKycResponse>

    @FormUrlEncoded
    @POST("sales/created/member/aadhaar/verify-otp")
    suspend fun registerUserVerifyAadharKyc(
        @Field("request_id") requestId: String,
        @Field("otp") otp: String,
        @Field("userId") userId: String,
        @Field("latitude") latitude: String,
        @Field("longitude") longitude: String,
    ): Response<AadhaarKycResponse>


    @Multipart
    @POST("document/upload")
    suspend fun uploadDocs(
        @Part panFilePart: MultipartBody.Part?,
        @Part profileFilePart: MultipartBody.Part?,
        @Part aadhaarFrontPart: MultipartBody.Part?,
        @Part aadhaarBackPart: MultipartBody.Part?,
        @Part shopPart: MultipartBody.Part?,
        @Part cancelChequePart: MultipartBody.Part?,
        @Part sealChequePart: MultipartBody.Part?,
        @Part gstPart: MultipartBody.Part?,
    ): Response<CommonResponse>

    @Multipart
    @POST("sales/created/member/document/update")
    suspend fun registerUserUploadDocs(
        @Part panFilePart: MultipartBody.Part?,
        @Part profileFilePart: MultipartBody.Part?,
        @Part aadhaarFrontPart: MultipartBody.Part?,
        @Part aadhaarBackPart: MultipartBody.Part?,
        @Part shopPart: MultipartBody.Part?,
        @Part cancelChequePart: MultipartBody.Part?,
        @Part sealChequePart: MultipartBody.Part?,
        @Part gstPart: MultipartBody.Part?,
        @Part("userId") userId: RequestBody
    ): Response<CommonResponse>


    @GET("document/list")
    suspend fun uploadedFilesDetails(): Response<DocumentKycResponse>

    @GET("sales/created/member/document")
    suspend fun registerUserUploadedFilesDetails(
        @Query("userId") userId: String
    ): Response<DocumentKycResponse>
}