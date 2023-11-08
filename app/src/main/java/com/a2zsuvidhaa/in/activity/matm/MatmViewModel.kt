package com.a2zsuvidhaa.`in`.activity.matm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.a2zsuvidhaa.`in`.BaseViewModel
import com.a2zsuvidhaa.`in`.data.model.matm.MatmTransactionResponse
import com.a2zsuvidhaa.`in`.data.model.matm.MatmInitiate
import com.a2zsuvidhaa.`in`.data.model.matm.MatmPostResponse
import com.a2zsuvidhaa.`in`.data.model.matm.MatmSaleAmountLimitResponse
import com.a2zsuvidhaa.`in`.data.repository.MatmRepository
import com.a2zsuvidhaa.`in`.data.response.matm.MatmInitiateResponse
import com.a2zsuvidhaa.`in`.util.AppUtil
import com.a2zsuvidhaa.`in`.util.apis.SingleMutableLiveData
import com.a2zsuvidhaa.`in`.util.ui.ContextInjectUtil
import com.a2zsuvidhaa.`in`.util.ui.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MatmViewModel @Inject constructor(
    private val matmRepository: MatmRepository,
    private val contextInjectUtil: ContextInjectUtil
) : BaseViewModel() {

    var mobileNumber: String = ""
    var amount: String = ""
    var transactionTypeObs = MutableLiveData(MatmTransactionType.CASH_WITHDRAWAL)
    val initiateTransactionObs = SingleMutableLiveData<Resource<MatmInitiateResponse>>()

    val postDataObs = SingleMutableLiveData<Resource<MatmPostResponse>>()

    var matmInitiate: MatmInitiate? = null
    var saleAmountLimit: MatmSaleAmountLimitResponse? = null

    lateinit var matmTransactionResponse: MatmTransactionResponse


    fun setTransactionType(type: MatmTransactionType) {
        transactionTypeObs.value = type
    }

    fun getTransactionType(): String {
        return when (transactionTypeObs.value!!) {
            MatmTransactionType.CASH_WITHDRAWAL -> "CASH WITHDRAWAL"
            MatmTransactionType.BALANCE_ENQUIRY -> "BALANCE ENQUIRY"
            MatmTransactionType.SALE -> "SALE"
        }
    }

    private fun getTransactionTypeParam(): String {
        return when (transactionTypeObs.value!!) {
            MatmTransactionType.CASH_WITHDRAWAL -> "CASH_WITHDRAWAL"
            MatmTransactionType.BALANCE_ENQUIRY -> "BALANCE_ENQUIRY"
            MatmTransactionType.SALE -> "SALE"
        }
    }

    fun initiateMatm() {

        if (matmInitiate != null) return

        initiateTransactionObs.value = Resource.Loading()
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    apiRequest {
                        if(transactionTypeObs.value == MatmTransactionType.SALE){
                            matmRepository.initiateMPosTransaction(
                                mobileNumber,
                                getTransactionTypeParam(),
                                amount
                            )
                        }
                        else{
                            matmRepository.initiateMatmTransaction(
                                mobileNumber,
                                getTransactionTypeParam(),
                                amount
                            )
                        }
                    }
                }
                initiateTransactionObs.value = Resource.Success(response)
            } catch (e: Exception) {
                initiateTransactionObs.value = Resource.Failure(e)
            }
        }
    }

    fun postResultData(data: String) {
        AppUtil.logger("matm check status test : posting...")
        postDataObs.value = Resource.Loading()
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    matmRepository.postResultData(data).body()!!
                    //  contextInjectUtil.loadJSONFromAsset<MatmTransactionResponse>("matm_transaction_response")
                }
                postDataObs.value = Resource.Success(response)
            } catch (e: Exception) {
                postDataObs.value = Resource.Failure(e)
            }
        }
    }


    var checkStatusCount: Int = 0
    val checkStatusObs = SingleMutableLiveData<Resource<MatmTransactionResponse>>()
    fun checkStatus() {
        checkStatusCount++
        AppUtil.logger("matm check status test : $checkStatusCount")
        try {
            checkStatusObs.value = Resource.Loading()
            viewModelScope.launch(Dispatchers.Main) {

                val response = withContext(Dispatchers.IO) {
                    delay(5000)
                    matmRepository.checkStatus(matmTransactionResponse.recordId!!).body()!!
                }
                checkStatusObs.value = Resource.Success(response)
            }
        } catch (e: Exception) {
            checkStatusObs.value = Resource.Failure(e)
        }
    }

    fun shouldBack(): Boolean {
        return transactionTypeObs.value == MatmTransactionType.BALANCE_ENQUIRY
    }


    val saleAmountLimitObs = SingleMutableLiveData<Resource<MatmSaleAmountLimitResponse>>()
    fun fetchSaleAmountLimit() {

        if(saleAmountLimit !=null) return

        try {
            saleAmountLimitObs.value = Resource.Loading()
            viewModelScope.launch(Dispatchers.Main) {

                val response = withContext(Dispatchers.IO) {
                    matmRepository.salemAmountLimit().body()!!
                }
                saleAmountLimitObs.value = Resource.Success(response)
            }
        } catch (e: Exception) {
            saleAmountLimitObs.value = Resource.Failure(e)
        }

    }


}

enum class MatmTransactionType {
    CASH_WITHDRAWAL, BALANCE_ENQUIRY, SALE
}