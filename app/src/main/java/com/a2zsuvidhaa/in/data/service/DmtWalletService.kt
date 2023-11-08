package com.a2zsuvidhaa.`in`.data.service

import com.a2zsuvidhaa.`in`.data.model.BankDownResponse
import com.a2zsuvidhaa.`in`.data.model.dmt.*
import com.a2zsuvidhaa.`in`.data.response.CommonResponse
import com.a2zsuvidhaa.`in`.util.ents.FieldMapData
import retrofit2.Response
import retrofit2.http.*

interface DmtWalletService {

    @GET("a2z/plus/wallet/sender/search")
    suspend fun searchMobileNumberWalletOne(@QueryMap data: FieldMapData): Response<MoneySenderResponse>

    @GET("a2z/plus/wallet-two/sender/search")
    suspend fun searchMobileNumberWalletTwo(@QueryMap data: FieldMapData): Response<MoneySenderResponse>

    @GET("a2z/plus/wallet-three/sender/search")
    suspend fun searchMobileNumberWalletThree(@QueryMap data: FieldMapData): Response<MoneySenderResponse>

    @GET("a2z/plus/wallet/account-search")
    suspend fun searchAccountNumber(@QueryMap data: FieldMapData): Response<SenderAccountDetailResponse>

    @POST("a2z/plus/wallet/sender/registration")
    @FormUrlEncoded
    suspend fun registerSender(@FieldMap data: FieldMapData): Response<SenderRegistrationResponse>

    @GET("a2z/plus/wallet/sender/verification/resendotp")
    suspend fun resendSenderRegistrationOtp(@QueryMap data: FieldMapData): Response<SenderRegistrationResponse>

    @POST("a2z/plus/wallet/sender/verification")
    @FormUrlEncoded
    suspend fun verifySender(@FieldMap data: FieldMapData): Response<CommonResponse>

    @POST("a2z/plus/wallet/sender/updateAndVerification")
    @FormUrlEncoded
    suspend fun verifyAndUpdateSender(@FieldMap data: FieldMapData): Response<CommonResponse>


    @GET("a2z/plus/wallet/bene-list")
    suspend fun beneficiaryList(@QueryMap data: FieldMapData): BeneficiaryListResponse

    @GET("a2z/plus/wallet/bank-list")
    suspend fun bankList(@QueryMap data: FieldMapData): Response<BankListResponse>

    @POST("a2z/plus/wallet/add-beneficiary")
    @FormUrlEncoded
    suspend fun addBeneficiary(@FieldMap data: FieldMapData): Response<CommonResponse>

    @POST("a2z/plus/wallet/account/validation")
    @FormUrlEncoded
    suspend fun accountValidation(@FieldMap data: FieldMapData): AccountVerify

    @POST("a2z/plus/wallet/account/re-validation")
    @FormUrlEncoded
    suspend fun accountReValidation(@FieldMap data: FieldMapData): AccountVerify


    @POST("a2z/plus/wallet/bene-delete")
    @FormUrlEncoded
    suspend fun deleteBeneficiary(@FieldMap data: FieldMapData): Response<CommonResponse>

    @POST("a2z/plus/wallet/bene-delete/confirm")
    @FormUrlEncoded
    suspend fun deleteBeneficiaryConfirm(@FieldMap data: FieldMapData): Response<CommonResponse>

    @GET("a2z/plus/wallet/charge-commission")
    suspend fun commissionCharge(@QueryMap data: FieldMapData): Response<DmtCommissionResponse>

    @GET("a2z/plus/wallet/check-bank-down")
    suspend fun bankDownCheck(@QueryMap data: FieldMapData): BankDownCheckResponse

    @GET("a2z/plus/wallet/bank-down")
    suspend fun bankDown(): BankDownResponse

    @POST("a2z/plus/wallet/transaction")
    @FormUrlEncoded
    suspend fun transfer(@FieldMap data: FieldMapData): Response<CommonResponse>


}