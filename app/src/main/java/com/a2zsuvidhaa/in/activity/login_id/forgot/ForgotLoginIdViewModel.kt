package com.a2zsuvidhaa.`in`.activity.login_id.forgot

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.a2zsuvidhaa.`in`.AppPreference
import com.a2zsuvidhaa.`in`.BaseViewModel
import com.a2zsuvidhaa.`in`.data.repository.AuthRepository
import com.a2zsuvidhaa.`in`.data.response.CommonResponse
import com.a2zsuvidhaa.`in`.util.AppLog
import com.a2zsuvidhaa.`in`.util.apis.Resource
import com.a2zsuvidhaa.`in`.util.apis.SingleMutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class ForgotLoginIdViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    val appPreference: AppPreference
) : BaseViewModel() {

    var mobileNumber = ""
    var aadhaarNumber = ""
    var otp = ""

    private val _onForgotLoginId = SingleMutableLiveData<Resource<CommonResponse>>()
    val onForgotLoginId: LiveData<Resource<CommonResponse>> = _onForgotLoginId

    fun forgotLoginId() {
        _onForgotLoginId.value = Resource.Loading()

        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    apiRequest {
                        authRepository.forgotLoginId(
                            hashMapOf(
                                "mobile" to mobileNumber,
                                "aadhaar_number" to aadhaarNumber,
                            )
                        )
                    }
                }
                _onForgotLoginId.value = Resource.Success(response)
            } catch (e: Exception) {
                AppLog.d("exception_test : ${e.printStackTrace()}")
                _onForgotLoginId.value = Resource.Failure(e)
            }
        }
    }








    private val _onVerifyOtp = MutableLiveData<Resource<CommonResponse>>()
    val onVerifyOtp: LiveData<Resource<CommonResponse>> = _onVerifyOtp


    fun forgotLoginIdVerify() {
        _onVerifyOtp.value = Resource.Loading()
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    apiRequest {
                         authRepository.forgotLoginIdVerify(
                             hashMapOf(
                                 "mobile" to mobileNumber,
                                 "aadhaar_number" to aadhaarNumber,
                                 "otp" to otp
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