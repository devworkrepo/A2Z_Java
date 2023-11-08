package com.a2zsuvidhaa.`in`.viewmodel.dmt

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.a2zsuvidhaa.`in`.BaseViewModel
import com.a2zsuvidhaa.`in`.data.model.dmt.AccountVerify
import com.a2zsuvidhaa.`in`.data.model.dmt.BankListResponse
import com.a2zsuvidhaa.`in`.data.model.dmt.MoneySender
import com.a2zsuvidhaa.`in`.data.repository.DmtWalletRepository
import com.a2zsuvidhaa.`in`.data.response.CommonResponse
import com.a2zsuvidhaa.`in`.util.AppConstants
import com.a2zsuvidhaa.`in`.util.enums.DmtType
import com.a2zsuvidhaa.`in`.util.ui.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AddBeneficiaryViewModel @Inject constructor(
    private val dmtWalletRepository: DmtWalletRepository,
) : BaseViewModel() {


    var moneySender: MoneySender? = null
    lateinit var dmtType: DmtType
    var beneficiaryName = AppConstants.EMPTY
    var bankName = AppConstants.EMPTY
    var bankId = AppConstants.EMPTY
    var accountDigit = 0
    var ifscCode = AppConstants.EMPTY
    var accountNumber = AppConstants.EMPTY


    private val _bankListObs = MutableLiveData<Resource<BankListResponse>>()
    val bankListObs: LiveData<Resource<BankListResponse>> = _bankListObs

    fun fetchBankList() {
        _bankListObs.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.Main) {

            try {
                val response = withContext(Dispatchers.IO) {
                    dmtWalletRepository.bankList(hashMapOf())
                }.body()!!
                _bankListObs.value = Resource.Success(response)
            } catch (e: Exception) {
                _bankListObs.value = Resource.Failure(e)
            }
        }
    }


    private val _verifyAccountObs = MutableLiveData<Resource<AccountVerify>>()
    val verifyAccountObs: LiveData<Resource<AccountVerify>> = _verifyAccountObs

    fun verifyAccount() {
        _verifyAccountObs.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.Main) {

            try {
                val response = withContext(Dispatchers.IO) {
                    dmtWalletRepository.accountValidation(hashMapOf(
                        "mobile_number" to moneySender?.mobileNumber.orEmpty(),
                        "bank_account" to accountNumber,
                        "ifsc" to ifscCode,
                        "bank_name" to bankName
                    ))

                }
                _verifyAccountObs.value = Resource.Success(response)
            } catch (e: Exception) {
                _verifyAccountObs.value = Resource.Failure(e)
            }

        }
    }

    private val _addBeneficiaryObs = MutableLiveData<Resource<CommonResponse>>()
    val addBeneficiaryObs : LiveData<Resource<CommonResponse>> = _addBeneficiaryObs

    fun addBeneficiary(){
        _addBeneficiaryObs.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.Main) {
            try {
                val response = withContext(Dispatchers.IO) {
                    dmtWalletRepository.addBeneficiary(
                        hashMapOf(
                            "sender_number" to moneySender?.mobileNumber.orEmpty(),
                            "bene_name" to beneficiaryName,
                            "account_number" to accountNumber,
                            "bank_id" to bankId,
                            "ifsc" to ifscCode,
                            "bank_name" to bankName
                        )
                    )
                }.body()!!
                _addBeneficiaryObs.value = Resource.Success(response)
            } catch (e: Exception) {
                _addBeneficiaryObs.value = Resource.Failure(e)
            }
        }
    }


}