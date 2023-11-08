package com.a2zsuvidhaa.`in`.data.service

import com.a2zsuvidhaa.`in`.data.response.CommonResponse
import com.a2zsuvidhaa.`in`.model.AgreementInitialInfoResponse
import com.a2zsuvidhaa.`in`.model.AgreementStartResponse
import com.a2zsuvidhaa.`in`.util.ents.FieldMapData
import retrofit2.http.*
import com.squareup.okhttp.ResponseBody
import retrofit2.Call


interface AgreementService {






    @GET("agreement")
    suspend fun fetchInitialAgreement(): AgreementInitialInfoResponse
    @GET("agreement/irctc")
    suspend fun fetchInitialIrctcAgreement() : AgreementInitialInfoResponse




    @GET("agreement/e-sign")
    suspend fun generateAgreementPdf(): CommonResponse
    @GET("agreement/irctc/generate-pdf")
    suspend fun generateIrctcPdf() : CommonResponse



    @POST("agreement/start")
    @FormUrlEncoded
    suspend fun startAgreement(
        @FieldMap fieldMap: FieldMapData
    ): AgreementStartResponse


    @POST("agreement/irctc/start")
    @FormUrlEncoded
    suspend fun startIrctcAgreement(
        @FieldMap fieldMap: FieldMapData
    ): AgreementStartResponse




    @POST("agreement/e-sign/checkstatus")
    @FormUrlEncoded
    suspend fun checkStatus(
        @FieldMap fieldMap: FieldMapData
    ): CommonResponse
    @POST("agreement/irctc/checkstatus")
    @FormUrlEncoded
    suspend fun checkStatusIrctc(
        @FieldMap fieldMap: FieldMapData
    ): CommonResponse


    @POST("agreement/e-sign/download")
    @FormUrlEncoded
    suspend fun agreementDownload(
        @FieldMap fieldMap: FieldMapData
    ): CommonResponse
    @POST("agreement/irctc/getsigneddocument")
    @FormUrlEncoded
    suspend fun agreementIrctcDownload(
        @FieldMap fieldMap: FieldMapData
    ): CommonResponse


    @POST("agreement/get/report")
    @FormUrlEncoded
    suspend fun agreementDownloadReport(
        @FieldMap fieldMap: FieldMapData
    ): CommonResponse
    @POST("agreement/irctc/get/report")
    @FormUrlEncoded
    suspend fun agreementDownloadReportIrctc(
        @FieldMap fieldMap: FieldMapData
    ): CommonResponse


}