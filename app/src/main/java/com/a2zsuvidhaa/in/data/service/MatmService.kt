package com.a2zsuvidhaa.`in`.data.service

import com.a2zsuvidhaa.`in`.data.model.matm.*
import com.a2zsuvidhaa.`in`.data.response.CommonResponse
import com.a2zsuvidhaa.`in`.data.response.matm.MatmInitiateResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface MatmService {


    @FormUrlEncoded
    @POST("matm/get-txn-data")
    suspend fun initiateMatmTransaction(
        @Field("customerMobile") customerMobile: String,
        @Field("transactionType") transactionType: String,
        @Field("amount") amount: String
    ): Response<MatmInitiateResponse>


    @FormUrlEncoded
    @POST("matm/mpos/transaction")
    suspend fun initiateMPosTransaction(
        @Field("customerMobile") customerMobile: String,
        @Field("transactionType") transactionType: String,
        @Field("amount") amount: String
    ): Response<MatmInitiateResponse>


    @FormUrlEncoded
    @POST("matm/update/txn-data")
    suspend fun postResultData(
        @Field("app_response") data: String,
    ): Response<MatmPostResponse>

    @GET("matm/userdata")
    suspend fun fetchOrderInfo(): Response<MatmServiceInformationfoResponse>


 @GET("matm/send/otp")
    suspend fun requestOtp(): Response<CommonResponse>

    @FormUrlEncoded
    @POST("matm/verify/otp")
    suspend fun verifyOtp(
        @Field("otp") otp: String,
    ): Response<CommonResponse>


    @GET("matm/checkstatus")
    suspend fun checkStatus(@Query("recordId") orderId: String): Response<MatmTransactionResponse>


    @GET("matm/mpos/amount-range")
    suspend fun salemAmountLimit(): Response<MatmSaleAmountLimitResponse>

    @GET("matm/getdocument-type")
    suspend fun getDocTypeList(): Response<MposDocTypeResponse>


    @GET("matm/serviceactivation")
    suspend fun orderNow(): Response<CommonResponse>


    @Multipart
    @POST("matm/uploadDocument")
    suspend fun uploadDetail(
        @Part panCardFilePart: MultipartBody.Part? = null,
        @Part addressProofFilePart: MultipartBody.Part? = null,
        @Part shopInsideFilePart: MultipartBody.Part? = null,
        @Part shopOutsideFilePart: MultipartBody.Part? = null,
        @Part businessLegalityFilePart: MultipartBody.Part? = null,
        @Part businessAddressFilePart: MultipartBody.Part? = null,
        @Part("gstNumber") gstNumberBodyPart: RequestBody? = null,
        @Part("shopAddress") shopAddressBodyPart: RequestBody? = null,
        @Part("mobile") mobileBodyPart: RequestBody? = null,
        @Part("email") emailBodyPart: RequestBody? = null,
        @Part("shopName") shopNameBodyPart: RequestBody? = null,
        @Part("courierAddress") courierAddressBodyPart: RequestBody? = null,
        @Part("landMark") landmarkBodyPart: RequestBody? = null,
        @Part("pinCode") pinCodeBodyPart: RequestBody? = null,
        @Part("name") nameBodyPart: RequestBody? = null,
        @Part("city") cityBodyPart: RequestBody? = null,
        @Part("aadhaarNumber") aadhaarBodyPart: RequestBody? = null,
        @Part("panNumber") panBodyPart: RequestBody? = null,
        @Part("is_mAtm_received") matmReceivedBodyPart: RequestBody? = null,
        @Part("lat") latitudeBodyPart: RequestBody? = null,
        @Part("long") longitudeBodyPart: RequestBody? = null,
        @Part("business_proof_type") businessLegalityTypeBodyPart: RequestBody? = null,
        @Part("business_address_proof_type") businessAddressTypeBodyPart: RequestBody? = null,
    ): Response<CommonResponse>


}