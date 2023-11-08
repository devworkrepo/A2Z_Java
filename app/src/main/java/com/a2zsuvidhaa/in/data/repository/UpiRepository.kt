package com.a2zsuvidhaa.`in`.data.repository

import com.a2zsuvidhaa.`in`.data.service.UpiService
import com.a2zsuvidhaa.`in`.util.ents.FieldMapData
import javax.inject.Inject

class UpiRepository @Inject constructor(private val upiService: UpiService) {


    suspend fun searchSender(data: FieldMapData) = upiService.searchSender(data)
    suspend fun beneficiaryList(data: FieldMapData) = upiService.beneficiaryList(data)
    suspend fun accountValidation(data: FieldMapData) = upiService.accountValidation(data)
    suspend fun addBeneficiary(data: FieldMapData) = upiService.addBeneficiary(data)
    suspend fun vpaList() = upiService.vpaList()
    suspend fun verificationCharge(data: FieldMapData) = upiService.verificationCharge(data)
    suspend fun bankDownCheck(data: FieldMapData) = upiService.bankDownCheck(data)


}