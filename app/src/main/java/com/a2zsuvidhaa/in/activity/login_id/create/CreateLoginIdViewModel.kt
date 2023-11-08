package com.a2zsuvidhaa.`in`.activity.login_id.create

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.a2zsuvidhaa.`in`.AppPreference
import com.a2zsuvidhaa.`in`.BaseViewModel
import com.a2zsuvidhaa.`in`.data.repository.AuthRepository
import com.a2zsuvidhaa.`in`.data.response.AadhaarKycResponse
import com.a2zsuvidhaa.`in`.data.response.CommonResponse
import com.a2zsuvidhaa.`in`.util.AppLog
import com.a2zsuvidhaa.`in`.util.apis.Resource
import com.a2zsuvidhaa.`in`.util.apis.SingleMutableLiveData
import com.google.android.gms.common.internal.service.Common
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class CreateLoginIdViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    val appPreference: AppPreference
) : BaseViewModel() {

    var loginId = ""
    var confirmLoginId = ""
    var otp = ""

    private val _createLoginIdObs = SingleMutableLiveData<Resource<CommonResponse>>()
    val createLoginIdObs: LiveData<Resource<CommonResponse>> = _createLoginIdObs

    fun createLoginId() {
        _createLoginIdObs.value = Resource.Loading()

        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    apiRequest {
                        authRepository.createLoginId(
                            hashMapOf(
                                "login_id" to loginId,
                                "confirm_login_id" to confirmLoginId,
                            )
                        )
                    }
                }
                _createLoginIdObs.value = Resource.Success(response)
            } catch (e: Exception) {
                AppLog.d("exception_test : ${e.printStackTrace()}")
                _createLoginIdObs.value = Resource.Failure(e)
            }
        }
    }








    private val _onVerifyOtp = MutableLiveData<Resource<CommonResponse>>()
    val onVerifyOtp: LiveData<Resource<CommonResponse>> = _onVerifyOtp


    fun verifyLoginId() {
        _onVerifyOtp.value = Resource.Loading()
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    apiRequest {
                         authRepository.verifyLoginId(
                             hashMapOf(
                                 "login_id" to loginId,
                                 "loginIdOtp" to otp
                             )
                         )

                    }
                }
                _onVerifyOtp.value = Resource.Success(response)
            } catch (e: Exception) {
                _onVerifyOtp.value = Resource.Failure(e)
            }
        }
    }

}