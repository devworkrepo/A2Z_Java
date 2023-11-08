package com.a2zsuvidhaa.`in`.viewmodel.dmt

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.a2zsuvidhaa.`in`.BaseViewModel
import com.a2zsuvidhaa.`in`.data.model.dmt.AccountVerify
import com.a2zsuvidhaa.`in`.data.model.dmt.Beneficiary
import com.a2zsuvidhaa.`in`.data.model.dmt.BeneficiaryListResponse
import com.a2zsuvidhaa.`in`.data.model.dmt.MoneySender
import com.a2zsuvidhaa.`in`.data.repository.DmtWalletRepository
import com.a2zsuvidhaa.`in`.data.repository.UpiRepository
import com.a2zsuvidhaa.`in`.data.response.CommonResponse
import com.a2zsuvidhaa.`in`.util.apis.SingleMutableLiveData
import com.a2zsuvidhaa.`in`.util.enums.DmtType
import com.a2zsuvidhaa.`in`.util.ui.ContextInjectUtil
import com.a2zsuvidhaa.`in`.util.ui.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class BeneficiaryListingViewModel @Inject constructor(
    private val dmtWalletRepository: DmtWalletRepository,
    private val upiReportRepository: UpiRepository,
    private val contextInjectUtil: ContextInjectUtil
) : BaseViewModel() {

    var isNavigate = false

    lateinit var beneciary: Beneficiary
    var moneySender: MoneySender? = null
    lateinit var dmtType: DmtType

    private val _beneficiaryListingObs = MutableLiveData<Resource<BeneficiaryListResponse>>()
    val beneficiaryListingObs: LiveData<Resource<BeneficiaryListResponse>> = _beneficiaryListingObs

    fun fetchBeneficiary() {
        _beneficiaryListingObs.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.Main) {

            try {
                val response = withContext(Dispatchers.IO) {
                    val param = hashMapOf(
                        "mobile_number" to moneySender?.mobileNumber.orEmpty(),
                        "mobile" to moneySender?.mobileNumber.orEmpty()
                    )
                    when (dmtType) {
                        DmtType.WALLET_ONE,
                        DmtType.WALLET_TWO,
                        DmtType.WALLET_THREE,
                        DmtType.DMT_THREE -> dmtWalletRepository.beneficiaryList(param)
                        DmtType.UPI -> upiReportRepository.beneficiaryList(param)
                    }
                }
                _beneficiaryListingObs.value = Resource.Success(response)
            } catch (e: java.lang.Exception) {
                _beneficiaryListingObs.value = Resource.Failure(e)
            }
        }
    }


    private val _onVerifyAccountObs = SingleMutableLiveData<Resource<AccountVerify>>()
    val onVerifyAccountObs: LiveData<Resource<AccountVerify>> = _onVerifyAccountObs


    fun verifyAccount() {
        _onVerifyAccountObs.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.Main) {

            try {

                val response = withContext(Dispatchers.IO) {

                    when (dmtType) {
                        DmtType.WALLET_ONE,
                        DmtType.WALLET_TWO,
                        DmtType.WALLET_THREE,
                        DmtType.DMT_THREE -> {
                            dmtWalletRepository.accountReValidation(
                                hashMapOf(
                                    "bene_id" to beneciary.a2zBeneId.orEmpty(),
                                )
                            )
                        }
                        DmtType.UPI -> {
                            upiReportRepository.accountValidation(
                                hashMapOf(
                                    "number" to beneciary.accountNumber.orEmpty(),
                                    "type" to "VPA"
                                )
                            )
                        }
                        else -> throw Exception("Unknown Dmt service, please contact with admin")
                    }


                }
                _onVerifyAccountObs.value = Resource.Success(response)

            } catch (e: Exception) {
                _onVerifyAccountObs.value = Resource.Failure(e)
            }

        }
    }


    private val _onDeleteBeneficiaryObs = SingleMutableLiveData<Resource<CommonResponse>>()
    val onDeleteBeneficiaryObs: LiveData<Resource<CommonResponse>> = _onDeleteBeneficiaryObs


    private fun getBeneId() = when (dmtType) {
        DmtType.UPI -> beneciary.id.orEmpty()
        DmtType.WALLET_ONE,
        DmtType.WALLET_TWO,
        DmtType.DMT_THREE,
        DmtType.WALLET_THREE -> beneciary.a2zBeneId.orEmpty()
        else -> ""

    }

    fun deleteBeneficiary() {
        _onDeleteBeneficiaryObs.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.Main) {
            try {
                val response = withContext(Dispatchers.IO) {
                    dmtWalletRepository.deleteBeneficiary(
                        hashMapOf(
                            "bene_id" to getBeneId()
                        )
                    )
                }.body()!!
                _onDeleteBeneficiaryObs.value = Resource.Success(response)
            } catch (e: Exception) {
                _onDeleteBeneficiaryObs.value = Resource.Failure(e)
            }
        }
    }

    private val _onDeleteBeneficiaryVerifyObs = SingleMutableLiveData<Resource<CommonResponse>>()
    val onDeleteBeneficiaryVerifyObs: LiveData<Resource<CommonResponse>> =
        _onDeleteBeneficiaryVerifyObs


    fun deleteBeneficiaryVerify(otp: String) {
        _onDeleteBeneficiaryVerifyObs.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.Main) {

            try {
                val response = withContext(Dispatchers.IO) {
                    dmtWalletRepository.deleteBeneficiaryConfirm(
                        hashMapOf(
                            "bene_id" to getBeneId(),
                            "otp" to otp
                        )
                    )
                }.body()!!
                _onDeleteBeneficiaryVerifyObs.value = Resource.Success(response)


            } catch (e: Exception) {
                _onDeleteBeneficiaryVerifyObs.value = Resource.Failure(e)
            }
        }
    }


}