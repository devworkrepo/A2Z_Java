package com.a2zsuvidhaa.`in`.data.repository

import com.a2zsuvidhaa.`in`.data.service.MatmService
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject


class MatmRepository @Inject constructor(private val matmService: MatmService) {


    suspend fun initiateMatmTransaction(
        customerMobile: String,
        transactionType: String,
        amount: String,
    ) = matmService.initiateMatmTransaction(customerMobile, transactionType, amount)

    suspend fun initiateMPosTransaction(
        customerMobile: String,
        transactionType: String,
        amount: String,
    ) = matmService.initiateMPosTransaction(customerMobile, transactionType, amount)

    suspend fun postResultData(data: String) = matmService.postResultData(data)
    suspend fun checkStatus(recordId: String) = matmService.checkStatus(recordId)
    suspend fun salemAmountLimit() = matmService.salemAmountLimit()


    suspend fun fetchOrderInfo() = matmService.fetchOrderInfo()
    suspend fun requestOTp() = matmService.requestOtp()
    suspend fun verifyOtp(otp : String) = matmService.verifyOtp(otp)
    suspend fun getDocTypeList() = matmService.getDocTypeList()
    suspend fun orderNow() = matmService.orderNow()

    suspend fun uploadDetail(
        panCardFilePart: MultipartBody.Part? = null,
        addressProofFilePart:MultipartBody.Part?= null,
        shopInsideFilePart:MultipartBody.Part?= null,
        shopOutsideFilePart:MultipartBody.Part?= null,
        businessLegalityFilePart:MultipartBody.Part?= null,
        businessAddressFilePart:MultipartBody.Part?= null,
        gstNumberBodyPart:RequestBody?= null,
        shopAddressBodyPart:RequestBody?= null,
        mobileBodyPart:RequestBody?= null,
        emailBodyPart:RequestBody?= null,
        shopNameBodyPart:RequestBody?= null,
        courierAddressBodyPart:RequestBody?= null,
        landmarkBodyPart:RequestBody?= null,
        pinCodeBodyPart:RequestBody?= null,
       nameBodyPart:RequestBody?= null,
       cityBodyPart:RequestBody?= null,
       aadhaarBodyPart:RequestBody?= null,
       panBodyPart:RequestBody?= null,
        matmReceivedBodyPart: RequestBody?= null,
        latitudeBodyPart: RequestBody?= null,
        longitudeBodyPart: RequestBody?= null,
        businessLegalityTypeBodyPart: RequestBody?= null,
        businessAddressTypeBodyPart: RequestBody?= null,
    ) = matmService.uploadDetail(
        panCardFilePart = panCardFilePart,
        addressProofFilePart = addressProofFilePart,
        businessLegalityFilePart = businessLegalityFilePart,
        businessAddressFilePart = businessAddressFilePart,
        shopInsideFilePart = shopInsideFilePart,
        shopOutsideFilePart = shopOutsideFilePart,
        gstNumberBodyPart = gstNumberBodyPart,
        shopAddressBodyPart = shopAddressBodyPart,
        mobileBodyPart = mobileBodyPart,
        emailBodyPart = emailBodyPart,
        shopNameBodyPart = shopNameBodyPart,
        courierAddressBodyPart = courierAddressBodyPart,
        landmarkBodyPart = landmarkBodyPart,
        pinCodeBodyPart = pinCodeBodyPart,
        nameBodyPart = nameBodyPart,
        cityBodyPart = cityBodyPart,
        aadhaarBodyPart = aadhaarBodyPart,
        panBodyPart = panBodyPart,
        matmReceivedBodyPart = matmReceivedBodyPart,
        latitudeBodyPart = latitudeBodyPart,
        longitudeBodyPart = longitudeBodyPart,
        businessLegalityTypeBodyPart = businessLegalityTypeBodyPart,
        businessAddressTypeBodyPart = businessAddressTypeBodyPart,
    )
}