package com.a2zsuvidhaa.`in`.data.repository

import com.a2zsuvidhaa.`in`.data.service.RegistrationService
import com.a2zsuvidhaa.`in`.util.ents.FieldMapData
import javax.inject.Inject

class RegistrationRepository @Inject constructor(private val registrationRepository: RegistrationService) {

    suspend fun postMobileNumber(mobileNumber: String, isSelf: String) =
        registrationRepository.postMobileNumber(mobileNumber, isSelf)

    suspend fun mobileNumberVerify(requestId: String, otp: String, isSelf: String) =
        registrationRepository.mobileNumberVerify(requestId, otp, isSelf)

    suspend fun postEmailId(email: String, requestId: String, isSelf: String) =
        registrationRepository.postEmailId(email, requestId, isSelf)

    suspend fun fetchMappingUserList(url: String, data: FieldMapData) =
        registrationRepository.fetchMappingUserList(url, data)

    suspend fun fetchCompletedUserList(data: FieldMapData) =
        registrationRepository.fetchCompletedUserList(data)

    suspend fun fetchInCompletedUserList(data: FieldMapData) =
        registrationRepository.fetchInCompletedUserList(data)


    suspend fun verifyEmailId(otp: String, requestId: String, isSelf: String) =
        registrationRepository.verifyEmailId(otp, requestId, isSelf)

    suspend fun postPanNumber(requestId: String, panNumber: String, isSelf: String) =
        registrationRepository.postPanNumber(requestId, panNumber, isSelf)

    suspend fun selfRegister(
        requestId: String,
        password: String,
        confirmPassword: String,
        outletName: String,
        outletAddress: String,


        ) = registrationRepository.selfRegister(
        requestId,
        password,
        confirmPassword,
        outletName,
        outletAddress,

        )

    suspend fun registerFromDistributor(data: FieldMapData) =
        registrationRepository.registerFromDistributor(data)

    suspend fun resendOtp(
        requestId: String,
        type: String,
        isSelf: String
    ) = registrationRepository.resendOtp(requestId, type, isSelf)

    suspend fun fetchCreateRole(
    ) = registrationRepository.fetchCreateRole()

}