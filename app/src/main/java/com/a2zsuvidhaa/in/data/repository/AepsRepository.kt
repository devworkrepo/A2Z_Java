package com.a2zsuvidhaa.`in`.data.repository

import com.a2zsuvidhaa.`in`.data.service.AepsService
import javax.inject.Inject


class AepsRepository @Inject constructor(private val aepsService : AepsService){


    suspend fun fetchAepsCharge(
            transactionType : String,
            amount : String
    ) = aepsService.fetchAepsTransactionCharage(transactionType,amount)
}