package com.a2zsuvidhaa.`in`.viewmodel.dmt

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.a2zsuvidhaa.`in`.BaseViewModel
import com.a2zsuvidhaa.`in`.data.model.dmt.AccountVerify
import com.a2zsuvidhaa.`in`.data.model.dmt.MoneySender
import com.a2zsuvidhaa.`in`.data.model.dmt.VpaBankExtensionResponse
import com.a2zsuvidhaa.`in`.data.model.dmt.VpaVerificationChargeResponse
import com.a2zsuvidhaa.`in`.data.repository.UpiRepository
import com.a2zsuvidhaa.`in`.data.response.CommonResponse
import com.a2zsuvidhaa.`in`.util.ui.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class UpiAddBeneficiaryViewModel @Inject constructor(
        private val upiRepository: UpiRepository,
) : BaseViewModel() {


    var moneySender: MoneySender? = null
    var upiNumber: String = ""
    var beneficiaryName: String = ""
    var actionType = ActionType.VERIFICATION
    var bankType = "Bank Upi"



    private val _verifyAccountObs = MutableLiveData<Resource<AccountVerify>>()
    val verifyAccountObs: LiveData<Resource<AccountVerify>> = _verifyAccountObs


    fun verifyUpiId() {
        _verifyAccountObs.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.Main) {

            try {
                val response = withContext(Dispatchers.IO) {
                    upiRepository.accountValidation(hashMapOf(
                            "number" to upiNumber,
                            "type" to "VPA"
                    ))

                }
                _verifyAccountObs.value = Resource.Success(response)
            } catch (e: Exception) {
                _verifyAccountObs.value = Resource.Failure(e)
            }

        }
    }


    private val _registerObs = MutableLiveData<Resource<CommonResponse>>()
    val registerObs: LiveData<Resource<CommonResponse>> = _registerObs
    fun registerUpiID() {


        _registerObs.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.Main) {

            try {
                val response = withContext(Dispatchers.IO) {
                    upiRepository.addBeneficiary(hashMapOf(
                            "sender_number" to moneySender?.mobileNumber.orEmpty(),
                            "beneficiary_vpa" to upiNumber,
                            "wallet_type" to bankType,
                            "beneficiary_name" to beneficiaryName,
                            "is_bank_verified" to "1",
                    ))

                }
                _registerObs.value = Resource.Success(response)
            } catch (e: Exception) {
                _registerObs.value = Resource.Failure(e)
            }

        }
    }



    private val _verificationChargeObs = MutableLiveData<Resource<VpaVerificationChargeResponse>>()
    val verificationChargeObs: LiveData<Resource<VpaVerificationChargeResponse>> = _verificationChargeObs
    fun fetchVerificationCharge() {


        _verificationChargeObs.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.Main) {

            try {
                val response = withContext(Dispatchers.IO) {
                    upiRepository.verificationCharge(hashMapOf(
                            "type" to "VPA",
                    ))

                }
                _verificationChargeObs.value = Resource.Success(response)
            } catch (e: Exception) {
                _verificationChargeObs.value = Resource.Failure(e)
            }

        }
    }



    private val _vpaListResponse = MutableLiveData<Resource<VpaBankExtensionResponse>>()
    val vpaListResponse: LiveData<Resource<VpaBankExtensionResponse>> = _vpaListResponse


    fun fetchVpaBank() {
        _vpaListResponse.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.Main) {

            try {
                val response = withContext(Dispatchers.IO) {
                    upiRepository.vpaList()
                }
                _vpaListResponse.value = Resource.Success(response)
            } catch (e: Exception) {
                _vpaListResponse.value = Resource.Failure(e)
            }

        }
    }

    fun getButtonText() = when (actionType) {
        ActionType.VERIFICATION -> "Verify UPI ID"
        ActionType.REGISTRATION -> "Register UPI ID"

    }


    enum class ActionType {
        VERIFICATION, REGISTRATION
    }
}