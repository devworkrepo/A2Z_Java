package com.a2zsuvidhaa.`in`.data.repository

import com.a2zsuvidhaa.`in`.data.service.DmtWalletService
import com.a2zsuvidhaa.`in`.util.ents.FieldMapData
import javax.inject.Inject

class DmtWalletRepository @Inject constructor(private val dmtWalletService: DmtWalletService) {


    suspend fun searchMobileNumberWalletOne(data: FieldMapData) =
        dmtWalletService.searchMobileNumberWalletOne(data)
    suspend fun searchMobileNumberWalletTwo(data: FieldMapData) =
        dmtWalletService.searchMobileNumberWalletTwo(data)
    suspend fun searchMobileNumberWalletThree(data: FieldMapData) =
        dmtWalletService.searchMobileNumberWalletThree(data)

    suspend fun searchAccountNumber(data: FieldMapData) = dmtWalletService.searchAccountNumber(data)
    suspend fun registerSender(data: FieldMapData) = dmtWalletService.registerSender(data)
    suspend fun resendSenderRegistrationOtp(data: FieldMapData) =
        dmtWalletService.resendSenderRegistrationOtp(data)

    suspend fun verifySender(data: FieldMapData) = dmtWalletService.verifySender(data)
    suspend fun verifyAndUpdateSender(data: FieldMapData) = dmtWalletService.verifyAndUpdateSender(data)
    suspend fun beneficiaryList(data: FieldMapData) = dmtWalletService.beneficiaryList(data)
    suspend fun bankList(data: FieldMapData) = dmtWalletService.bankList(data)
    suspend fun addBeneficiary(data: FieldMapData) = dmtWalletService.addBeneficiary(data)
    suspend fun accountValidation(data: FieldMapData) = dmtWalletService.accountValidation(data)
    suspend fun accountReValidation(data: FieldMapData) = dmtWalletService.accountReValidation(data)
    suspend fun deleteBeneficiary(data: FieldMapData) = dmtWalletService.deleteBeneficiary(data)
    suspend fun deleteBeneficiaryConfirm(data: FieldMapData) = dmtWalletService.deleteBeneficiaryConfirm(data)
    suspend fun commissionCharge(data: FieldMapData) = dmtWalletService.commissionCharge(data)
    suspend fun bankDownCheck(data: FieldMapData) = dmtWalletService.bankDownCheck(data)
    suspend fun transfer(data: FieldMapData) = dmtWalletService.transfer(data)
    suspend fun bankDown()  = dmtWalletService.bankDown()




}