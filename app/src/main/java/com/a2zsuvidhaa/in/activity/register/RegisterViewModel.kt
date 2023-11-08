package com.a2zsuvidhaa.`in`.activity.register

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.a2zsuvidhaa.`in`.BaseViewModel
import com.a2zsuvidhaa.`in`.data.repository.RegistrationRepository
import com.a2zsuvidhaa.`in`.data.response.RegistrationCommonResponse
import com.a2zsuvidhaa.`in`.data.response.RegistrationRole
import com.a2zsuvidhaa.`in`.data.response.RegistrationRoleResponse
import com.a2zsuvidhaa.`in`.data.response.RegistrationRoleUser
import com.a2zsuvidhaa.`in`.util.AppConstants
import com.a2zsuvidhaa.`in`.util.apis.Resource
import com.a2zsuvidhaa.`in`.util.apis.SingleMutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.http.Field
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registrationRepository: RegistrationRepository,

    ) : BaseViewModel() {

    var isFinalProcess = false

    var isSelfRegistration = false
    var layoutType = LayoutType.MOBILE_NUMBER
    var mobileNumber = AppConstants.EMPTY
    var emailId = AppConstants.EMPTY
    var panNumber = AppConstants.EMPTY
    var name = AppConstants.EMPTY
    var otp = AppConstants.EMPTY
    var requestId = AppConstants.EMPTY
    var password = AppConstants.EMPTY
    var confirmPassword = AppConstants.EMPTY
    var shopName = AppConstants.EMPTY
    var shopAddress = AppConstants.EMPTY
    lateinit var registrationRole: RegistrationRole
    var mappedUnder: RegistrationRoleUser? = null
    var shouldMapRole: Boolean = false
    var incompleteUserMobile: String? = null


    // on proceed button click
    private val _onProceedButtonClickObs = SingleMutableLiveData(false)
    val onProceedButtonClickObs: LiveData<Boolean> = _onProceedButtonClickObs
    fun onProceedButtonClick(view: View?) {
        _onProceedButtonClickObs.value = true
    }

    private val _onPostMobileNumberObs = MutableLiveData<Resource<RegistrationCommonResponse>>()
    val onPostMobileNumberObs: LiveData<Resource<RegistrationCommonResponse>> =
        _onPostMobileNumberObs


    fun postMobileNumber() {
        _onPostMobileNumberObs.value = Resource.Loading()
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    apiRequest {
                        registrationRepository.postMobileNumber(
                            mobileNumber,
                            isSelfRegistration.toString()
                        )
                    }
                }
                _onPostMobileNumberObs.value = Resource.Success(result)
            } catch (e: Exception) {
                _onPostMobileNumberObs.value = Resource.Failure(e)
            }
        }
    }

    private val _onMobileNumberVerifyObs = MutableLiveData<Resource<RegistrationCommonResponse>>()
    val onMobileNumberVerifyObs: LiveData<Resource<RegistrationCommonResponse>> =
        _onMobileNumberVerifyObs

    fun verifyMobileNumber() {
        _onMobileNumberVerifyObs.value = Resource.Loading()
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    apiRequest {
                        delay(2000)
                        registrationRepository.mobileNumberVerify(
                            requestId,
                            otp,
                            isSelfRegistration.toString()
                        )
                    }
                }
                _onMobileNumberVerifyObs.value = Resource.Success(result)
            } catch (e: Exception) {
                _onMobileNumberVerifyObs.value = Resource.Failure(e)
            }
        }
    }

    private val _onPostEmailIdObs = MutableLiveData<Resource<RegistrationCommonResponse>>()
    val onPostEmailIdObs: LiveData<Resource<RegistrationCommonResponse>> = _onPostEmailIdObs


    fun postEmailId() {
        _onPostEmailIdObs.value = Resource.Loading()
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    apiRequest {
                        registrationRepository.postEmailId(
                            emailId,
                            requestId,
                            isSelfRegistration.toString()
                        )
                    }
                }
                _onPostEmailIdObs.value = Resource.Success(result)
            } catch (e: Exception) {
                _onPostEmailIdObs.value = Resource.Failure(e)
            }
        }
    }


    private val _onVerifyEmailIdObs = MutableLiveData<Resource<RegistrationCommonResponse>>()
    val onVerifyEmailIdObs: LiveData<Resource<RegistrationCommonResponse>> = _onVerifyEmailIdObs


    fun verifyEmailId() {
        _onVerifyEmailIdObs.value = Resource.Loading()
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    apiRequest {
                        registrationRepository.verifyEmailId(
                            otp,
                            requestId,
                            isSelfRegistration.toString()
                        )
                    }
                }
                _onVerifyEmailIdObs.value = Resource.Success(result)
            } catch (e: Exception) {
                _onVerifyEmailIdObs.value = Resource.Failure(e)
            }
        }
    }


    private val _onPanNumberPostObs = MutableLiveData<Resource<RegistrationCommonResponse>>()
    val onPanNumberPostObs: LiveData<Resource<RegistrationCommonResponse>> = _onPanNumberPostObs


    fun postPanNumber() {
        _onPanNumberPostObs.value = Resource.Loading()
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    apiRequest {
                        registrationRepository.postPanNumber(
                            requestId,
                            panNumber,
                            isSelfRegistration.toString()
                        )
                    }
                }
                _onPanNumberPostObs.value = Resource.Success(result)
            } catch (e: Exception) {
                _onPanNumberPostObs.value = Resource.Failure(e)
            }
        }
    }

    private val _onRegisterObs = MutableLiveData<Resource<RegistrationCommonResponse>>()
    val onRegisterObs: LiveData<Resource<RegistrationCommonResponse>> = _onRegisterObs


    fun register() {
        _onRegisterObs.value = Resource.Loading()
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    apiRequest {

                        registrationRepository.selfRegister(
                            requestId = requestId,
                            password = password,
                            confirmPassword = confirmPassword,
                            outletName = shopName,
                            outletAddress = shopAddress,
                        )

                    }
                }
                _onRegisterObs.value = Resource.Success(result)
            } catch (e: Exception) {
                _onRegisterObs.value = Resource.Failure(e)
            }
        }
    }


    fun registerFromDistributor() {
        _onRegisterObs.value = Resource.Loading()
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    apiRequest {

                        val param = if (shouldMapRole)

                            hashMapOf(
                                "role_id" to registrationRole.roleId.toString(),
                                "requestId" to requestId,
                                "password" to password,
                                "confirm_password" to confirmPassword,
                                "outlet_name" to shopName,
                                "outlet_address" to shopAddress,
                                "relationId" to mappedUnder!!.relationId.toString(),
                                "parentId" to mappedUnder!!.id.toString(),
                            )
                        else hashMapOf(
                            "role_id" to registrationRole.roleId.toString(),
                            "requestId" to requestId,
                            "password" to password,
                            "confirm_password" to confirmPassword,
                            "outlet_name" to shopName,
                            "outlet_address" to shopAddress,

                            )
                        registrationRepository.registerFromDistributor(param)
                    }
                }
                _onRegisterObs.value = Resource.Success(result)
            } catch (e: Exception) {
                _onRegisterObs.value = Resource.Failure(e)
            }
        }
    }


    private val _onResendOtp = SingleMutableLiveData<Resource<RegistrationCommonResponse>>()
    val onResendOtp: LiveData<Resource<RegistrationCommonResponse>> = _onResendOtp


    fun resendOtp(type: String) {
        _onResendOtp.value = Resource.Loading()
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    apiRequest {
                        registrationRepository.resendOtp(
                            requestId = requestId,
                            type = type,
                            isSelfRegistration.toString()
                        )
                    }
                }
                _onResendOtp.value = Resource.Success(result)
            } catch (e: Exception) {
                _onResendOtp.value = Resource.Failure(e)
            }
        }
    }


    private val _fetchCreateRoles = SingleMutableLiveData<Resource<RegistrationRoleResponse>>()
    val fetchCreateRoles: LiveData<Resource<RegistrationRoleResponse>> = _fetchCreateRoles


    fun fetchCreateRoles() {
        _fetchCreateRoles.value = Resource.Loading()
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    apiRequest {
                        registrationRepository.fetchCreateRole()
                    }
                }
                _fetchCreateRoles.value = Resource.Success(result)
            } catch (e: Exception) {
                _fetchCreateRoles.value = Resource.Failure(e)
            }
        }
    }

}

enum class LayoutType {
    MOBILE_NUMBER, EMAIL, PAN_CARD, FINAL_REGISTRATION
}
