package com.a2zsuvidhaa.`in`.fragment.addhar_kyc

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.a2zsuvidhaa.`in`.BaseViewModel
import com.a2zsuvidhaa.`in`.data.repository.KycRepository
import com.a2zsuvidhaa.`in`.data.response.AadhaarKycResponse
import com.a2zsuvidhaa.`in`.model.QRCodeResponse
import com.a2zsuvidhaa.`in`.util.AppConstants
import com.a2zsuvidhaa.`in`.util.apis.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AadhaarKycViewModel @Inject constructor(
    private val kycRepository: KycRepository
) : BaseViewModel() {

    var aadhhaarNumber: String = AppConstants.EMPTY
    var mobileNumber: String = AppConstants.EMPTY
    var requestId: String = AppConstants.EMPTY
    var otp: String = AppConstants.EMPTY
    var registerUserId: String? = null
    var latitude: String = ""
    var longitude: String = ""


    private val _onPostKycObs = MutableLiveData<Resource<AadhaarKycResponse>>()
    val onPostKycObs: LiveData<Resource<AadhaarKycResponse>> = _onPostKycObs


    fun postAadhaarKyc() {
        _onPostKycObs.value = Resource.Loading()
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    apiRequest {
                        if (registerUserId == null) kycRepository.postAadharKyc(
                            aadhhaarNumber.replace(
                                "-",
                                ""
                            ), mobileNumber,
                            latitude,
                            longitude
                        )
                        else kycRepository.registerUserPostAadharKyc(
                            aadhhaarNumber.replace(
                                "-",
                                ""
                            ), mobileNumber,
                            registerUserId!!,
                            latitude,
                            longitude
                        )
                    }
                }
                _onPostKycObs.value = Resource.Success(response)
            } catch (e: Exception) {
                _onPostKycObs.value = Resource.Failure(e)
            }
        }
    }


    private val _onVerifyKycObs = MutableLiveData<Resource<AadhaarKycResponse>>()
    val onVerifyKycObs: LiveData<Resource<AadhaarKycResponse>> = _onVerifyKycObs


    fun verifyAadhaarKyc() {
        _onVerifyKycObs.value = Resource.Loading()
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    apiRequest {
                        if (registerUserId == null)
                            kycRepository.verifyAadharKyc(
                                requestId, otp, latitude,
                                longitude
                            )
                        else kycRepository.registerUserVerifyAadharKyc(
                            requestId,
                            otp,
                            registerUserId!!,
                            latitude,
                            longitude
                        )
                    }
                }
                _onVerifyKycObs.value = Resource.Success(response)
            } catch (e: Exception) {
                _onVerifyKycObs.value = Resource.Failure(e)
            }
        }
    }
}


