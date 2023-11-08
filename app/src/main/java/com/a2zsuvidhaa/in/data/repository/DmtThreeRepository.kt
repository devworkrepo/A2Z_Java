package com.a2zsuvidhaa.`in`.data.repository

import com.a2zsuvidhaa.`in`.data.service.DmtThreeService
import com.a2zsuvidhaa.`in`.util.ents.FieldMapData
import javax.inject.Inject

class DmtThreeRepository @Inject constructor(private val dmtWalletService: DmtThreeService) {

    suspend fun searchMobileNumberDmtThree(data: FieldMapData) =
        dmtWalletService.searchMobileNumberDmtThree(data)

    suspend fun registerSender(data: FieldMapData) =
        dmtWalletService.registerSender(data)


  suspend fun verifySender(data: FieldMapData) =
        dmtWalletService.verifySender(data)

    suspend fun resendSenderRegistrationOtp(data: FieldMapData) =
        dmtWalletService.resendSenderRegistrationOtp(data)


}