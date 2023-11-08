package com.a2zsuvidhaa.`in`.data.service

import com.a2zsuvidhaa.`in`.data.model.BankDownResponse
import com.a2zsuvidhaa.`in`.data.response.CommonResponse
import com.a2zsuvidhaa.`in`.model.*
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface HomeService {

    @GET("user/balance")
    suspend fun fetchBalanceInfo(): Response<BalanceResponse>

    @GET("user/static/upi")
    suspend fun fetchQurCodeData(): Response<QRCodeResponse>

    @GET("user/kyc/check")
    suspend fun kycCheck(): Response<KycInfoResponse>

    @GET("user/slide")
    suspend fun fetchBanner(): Response<BannerResponse>


    @GET("get-news")
    suspend fun fetchNews(
            @Query("userId") userId: String,
            @Query("token") token: String
    ): Response<NewsInfo>

    @GET("a2z/plus/wallet/bank-down")
    suspend fun bankDown(): BankDownResponse



}