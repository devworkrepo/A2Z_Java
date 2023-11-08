package com.a2zsuvidhaa.`in`.viewmodel.dmt

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.a2zsuvidhaa.`in`.BaseViewModel
import com.a2zsuvidhaa.`in`.data.model.dmt.MoneySender
import com.a2zsuvidhaa.`in`.data.model.dmt.SenderRegistrationResponse
import com.a2zsuvidhaa.`in`.data.repository.DmtThreeRepository
import com.a2zsuvidhaa.`in`.data.repository.DmtWalletRepository
import com.a2zsuvidhaa.`in`.data.response.CommonResponse
import com.a2zsuvidhaa.`in`.util.enums.DmtSenderRegisterType
import com.a2zsuvidhaa.`in`.util.enums.DmtType
import com.a2zsuvidhaa.`in`.util.ui.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AddSenderViewModel @Inject constructor(
    private val dmtWalletRepository: DmtWalletRepository,
    private val dmtThreeRepository: DmtThreeRepository
) : BaseViewModel() {


    lateinit var dmtType: DmtType
    lateinit var senderRegisterType: DmtSenderRegisterType

    var sender: MoneySender? = null
    var senderFirstName = ""
    var senderLastName = ""
    var senderMobile = ""
    var pinCode = ""
    var address = ""
    var otp = ""
    var state = ""
    var isRegistered = false


    fun proceed() {
        if (senderRegisterType == DmtSenderRegisterType.REGISTER) {
            if (isRegistered) {
                verifySender()
            } else {
                registerSender()
            }
        } else {
            verifyAndRegisterSender()
        }

    }


    private val _registerSenderObs = MutableLiveData<Resource<SenderRegistrationResponse>>()
    val registerSenderObs: LiveData<Resource<SenderRegistrationResponse>> = _registerSenderObs
    private fun registerSender() {
        _registerSenderObs.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.Main) {
            try {
                val response = withContext(Dispatchers.IO) {

                    when (dmtType) {
                        DmtType.DMT_THREE -> dmtThreeRepository.registerSender(
                            hashMapOf(
                                "mobile" to senderMobile,
                                "otpType" to "registrationOtp",
                            )
                        ).body()!!
                        else -> dmtWalletRepository.registerSender(
                            hashMapOf(
                                "mobile" to senderMobile,
                                "firstName" to senderFirstName,
                                "lastName" to senderLastName,
                                "address" to address,
                                "pincode" to pinCode,
                            )
                        ).body()!!
                    }

                }
                _registerSenderObs.value = Resource.Success(response)
            } catch (e: Exception) {
                _registerSenderObs.value = Resource.Failure(e)
            }

        }
    }


    private val _verifySenderObs = MutableLiveData<Resource<CommonResponse>>()
    val verifySenderObs: LiveData<Resource<CommonResponse>> = _verifySenderObs
    private fun verifySender() {
        _verifySenderObs.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.Main) {
            try {
                val response = withContext(Dispatchers.IO) {

                    when (dmtType) {
                        DmtType.DMT_THREE -> dmtThreeRepository.verifySender(
                            hashMapOf(
                                "mobile" to senderMobile,
                                "registrationOtp" to otp,
                                "stateId" to state,
                                "firstName" to senderFirstName,
                                "lastName" to senderLastName,
                                "address" to address,
                                "pincode" to pinCode,
                            )
                        ).body()!!

                        else -> dmtWalletRepository.verifySender(
                            hashMapOf(
                                "mobile" to senderMobile,
                                "otp" to otp,
                                "state" to state,
                            )
                        ).body()!!
                    }


                }
                _verifySenderObs.value = Resource.Success(response)
            } catch (e: Exception) {
                _verifySenderObs.value = Resource.Failure(e)
            }

        }
    }


    private fun verifyAndRegisterSender() {
        _verifySenderObs.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.Main) {
            try {
                val response = withContext(Dispatchers.IO) {
                    dmtWalletRepository.verifyAndUpdateSender(
                        hashMapOf(
                            "mobile" to senderMobile,
                            "firstName" to senderFirstName,
                            "lastName" to senderLastName,
                            "pincode" to pinCode,
                            "address" to address,
                            "otp" to otp,
                            "state" to state,
                        )
                    ).body()!!
                }
                _verifySenderObs.value = Resource.Success(response)
            } catch (e: Exception) {
                _verifySenderObs.value = Resource.Failure(e)
            }

        }
    }


    private val _resendOtpObs = MutableLiveData<Resource<SenderRegistrationResponse>>()
    val resendOtpObs: LiveData<Resource<SenderRegistrationResponse>> = _resendOtpObs

    fun resendOtp() {
        _resendOtpObs.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.Main) {

            try {
                val response = withContext(Dispatchers.IO) {
                    when(dmtType){
                        DmtType.DMT_THREE ->dmtThreeRepository.resendSenderRegistrationOtp(
                            hashMapOf(
                                "mobile" to senderMobile,
                                "otpType" to "registrationOtp",
                            )
                        )
                        else ->dmtWalletRepository.resendSenderRegistrationOtp(
                            hashMapOf(
                                "mobile" to senderMobile
                            )
                        )
                    }
                }.body()!!
                _resendOtpObs.value = Resource.Success(response)
            } catch (e: Exception) {
                _resendOtpObs.value = Resource.Failure(e)
            }

        }
    }


}