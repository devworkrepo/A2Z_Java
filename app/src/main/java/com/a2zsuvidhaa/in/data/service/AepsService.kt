package com.a2zsuvidhaa.`in`.data.service

import com.a2zsuvidhaa.`in`.data.response.CommonResponse
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Query

interface AepsService {


   @POST("aeps/transaction/charge-commission")
   suspend fun fetchAepsTransactionCharage(
           @Query("transaction_type") transactionType : String,
           @Query("amount") amount : String
   )


}