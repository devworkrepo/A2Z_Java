package com.a2zsuvidhaa.`in`.data.service

import com.a2zsuvidhaa.`in`.data.model.TransactionDetailResponse
import com.a2zsuvidhaa.`in`.data.model.report.*
import com.a2zsuvidhaa.`in`.data.response.CommonResponse
import com.a2zsuvidhaa.`in`.util.ents.FieldMapData
import retrofit2.Response
import retrofit2.http.*

interface ReportService {

    @GET("report/ledger")
    suspend fun ledgerReport(@QueryMap data : FieldMapData) : LedgerReportResponse

    @POST("check-status")
    @FormUrlEncoded
    suspend fun checkStatus(@FieldMap data : FieldMapData) : CommonResponse

    @POST("complain/store")
    @FormUrlEncoded
    suspend fun makeComplain(@FieldMap data : FieldMapData) : CommonResponse

    @GET
    suspend fun downloadLedgerReceiptData(@Url url : String) : Response<TransactionDetailResponse>

    @GET("user/scheme/list")
    suspend fun schemeList() : CommissionSchemeListResponse


    @GET("user/scheme")
    suspend fun schemeDetail(@QueryMap data : FieldMapData) : CommissionSchemeDetailResponse

    @GET("complain/remark/list")
    suspend fun fetchComplainTypes(@QueryMap data : FieldMapData) : ComplainTypeListResponse



}