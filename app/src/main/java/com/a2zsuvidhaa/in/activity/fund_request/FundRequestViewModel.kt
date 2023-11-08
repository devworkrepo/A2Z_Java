package com.a2zsuvidhaa.`in`.activity.fund_request

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.a2zsuvidhaa.`in`.BaseViewModel
import com.a2zsuvidhaa.`in`.AppPreference
import com.a2zsuvidhaa.`in`.data.repository.FundRequestRepository
import com.a2zsuvidhaa.`in`.data.response.CommonResponse
import com.a2zsuvidhaa.`in`.model.BankDetail
import com.a2zsuvidhaa.`in`.model.BankDetailResponse
import com.a2zsuvidhaa.`in`.util.apis.Resource
import com.a2zsuvidhaa.`in`.util.enums.FundRequestType

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class FundRequestViewModel @Inject constructor(
        private val fundRequestRepository: FundRequestRepository,
        private val appPreference: AppPreference
) : BaseViewModel() {

    var requestType: FundRequestType? = null


     var bank : BankDetail? = null

    var paymentMode : String = ""
    var paymentDate : String = ""
    var amount : String = ""
    var refNumber : String = ""
    var onlineMode : String = ""
    var remark : String = ""
    var requestTo :String = "1"
    var fileSlip : File ? = null

    private val _bankListObs = MutableLiveData<Resource<BankDetailResponse>>()
    val bankListObs: LiveData<Resource<BankDetailResponse>> = _bankListObs

    fun fetchBankList() {
        _bankListObs.value = Resource.Loading()

        viewModelScope.launch {

            try {
                val response = withContext(Dispatchers.IO) {
                    apiRequest { fundRequestRepository.fetchBalanceInfo(
                            appPreference.id.toString(),
                            appPreference.token,

                            getRequestType()
                    ) }
                }
                _bankListObs.value = Resource.Success(response)
            }catch (e : Exception){
                _bankListObs.value = Resource.Failure(e)
            }
        }
    }

    private fun getRequestType(): String = when (requestType) {
        FundRequestType.BANK_TRANSFER -> "BT"
        FundRequestType.CASH_COLLECT -> "CASH_COLLECT"
        FundRequestType.CASH_DEPOSIT_COUNTER -> "CASH_DEPOSIT"
        FundRequestType.CASH_DEPOSIT_MACHINE -> "CASH_CDM"
        else -> ""
    }


    private val _requestResultObs = MutableLiveData<Resource<CommonResponse>>()
    val requestResultObs : LiveData<Resource<CommonResponse>> = _requestResultObs


    fun submitRequest(){

        val fileSlipPart = getMultipartFormData(fileSlip, "d_picture")
        val amountBody = amount.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val requestToBody = requestTo.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val paymentDateBody = paymentDate.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val paymentModeBody = paymentMode.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val refNumberBody = refNumber.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val onlineModeBody = onlineMode.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val bankIdBody = bank?.id?.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val bankNameBody = bank?.bankName?.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val remarkBody = remark.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        _requestResultObs.value = Resource.Loading()

        viewModelScope.launch {

            try{
                val response = withContext(Dispatchers.IO){
                   apiRequest {
                       fundRequestRepository.submitRequest(
                               slipFile = fileSlipPart,
                               amount = amountBody,
                               requestTo = requestToBody,
                               paymentDate= paymentDateBody,
                               paymentMode = paymentModeBody,
                               refNumber = refNumberBody,
                               onlineMode = onlineModeBody,
                               bankId =  bankIdBody,
                               bankName = bankNameBody,
                               remark = remarkBody,
                       )
                   }
                }

                _requestResultObs.value= Resource.Success(response)


            }catch (e : Exception){
                _requestResultObs.value = Resource.Failure(e)
            }
        }

    }

    private fun getMultipartFormData(file: File?, fileField: String): MultipartBody.Part? {
        val requestBody = getRequestBody(file) ?: return null
        return MultipartBody.Part.createFormData(fileField, file?.name, requestBody)
    }


    private fun getRequestBody(file: File?) =
            file?.asRequestBody("multipart/form-data".toMediaTypeOrNull())
}