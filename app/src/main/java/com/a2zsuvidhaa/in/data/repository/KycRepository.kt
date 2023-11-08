package com.a2zsuvidhaa.`in`.data.repository

import com.a2zsuvidhaa.`in`.data.service.HomeService
import com.a2zsuvidhaa.`in`.data.service.KycService
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class KycRepository @Inject constructor(private val kycService: KycService) {

    suspend fun postAadharKyc(
        aadhharNumber: String,
        mobileNumber: String,
        latitude : String,
        longitude : String,
    ) = kycService.postAadharKyc(aadhharNumber, mobileNumber,latitude,longitude)

    suspend fun registerUserPostAadharKyc(
        aadhharNumber: String,
        mobileNumber: String,
        userId: String,
        latitude : String,
        longitude : String,
    ) = kycService.registerUserPostAadharKyc(aadhharNumber, mobileNumber, userId,latitude,longitude)


    suspend fun verifyAadharKyc(
        requestId: String,
        otp: String,
        latitude : String,
        longitude : String,
    ) = kycService.verifyAadharKyc(requestId, otp,latitude,longitude)


    suspend fun registerUserVerifyAadharKyc(
        requestId: String,
        otp: String,
        userId: String,
        latitude : String,
        longitude : String,
    ) = kycService.registerUserVerifyAadharKyc(requestId, otp, userId,latitude,longitude)


    //upload docs

    suspend fun uploadedFilesDetails() = kycService.uploadedFilesDetails()
    suspend fun registerUserUploadedFilesDetails(userId: String) =
        kycService.registerUserUploadedFilesDetails(userId)

    suspend fun uploadDocs(
        panFilePart: MultipartBody.Part?,
        profileFilePart: MultipartBody.Part?,
        aadhaarFrontPart: MultipartBody.Part?,
        aadhaarBackPart: MultipartBody.Part?,
        shopPart: MultipartBody.Part?,
        cancelChequePart: MultipartBody.Part?,
        sealChequePart: MultipartBody.Part?,
        gstPart: MultipartBody.Part?,
    ) = kycService.uploadDocs(
        panFilePart = panFilePart,
        profileFilePart = profileFilePart,
        aadhaarFrontPart = aadhaarFrontPart,
        aadhaarBackPart = aadhaarBackPart,
        shopPart = shopPart,
        cancelChequePart = cancelChequePart,
        sealChequePart = sealChequePart,
        gstPart = gstPart,
    )

    suspend fun registerUserUploadDocs(
        panFilePart: MultipartBody.Part?,
        profileFilePart: MultipartBody.Part?,
        aadhaarFrontPart: MultipartBody.Part?,
        aadhaarBackPart: MultipartBody.Part?,
        shopPart: MultipartBody.Part?,
        cancelChequePart: MultipartBody.Part?,
        sealChequePart: MultipartBody.Part?,
        gstPart: MultipartBody.Part?,
        userId: RequestBody,
    ) = kycService.registerUserUploadDocs(
        panFilePart = panFilePart,
        profileFilePart = profileFilePart,
        aadhaarFrontPart = aadhaarFrontPart,
        aadhaarBackPart = aadhaarBackPart,
        shopPart = shopPart,
        cancelChequePart = cancelChequePart,
        sealChequePart = sealChequePart,
        gstPart = gstPart,
        userId = userId,
    )
}