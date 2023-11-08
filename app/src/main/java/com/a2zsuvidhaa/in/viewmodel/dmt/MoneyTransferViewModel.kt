package com.a2zsuvidhaa.`in`.viewmodel.dmt

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.a2zsuvidhaa.`in`.BaseViewModel
import com.a2zsuvidhaa.`in`.activity.login.globalLocation
import com.a2zsuvidhaa.`in`.data.model.TransactionDetail
import com.a2zsuvidhaa.`in`.data.model.TransactionDetailResponse
import com.a2zsuvidhaa.`in`.data.model.dmt.BankDownCheckResponse
import com.a2zsuvidhaa.`in`.data.model.dmt.Beneficiary
import com.a2zsuvidhaa.`in`.data.model.dmt.DmtCommissionResponse
import com.a2zsuvidhaa.`in`.data.model.dmt.MoneySender
import com.a2zsuvidhaa.`in`.data.preference.AppPreference
import com.a2zsuvidhaa.`in`.data.repository.DmtWalletRepository
import com.a2zsuvidhaa.`in`.data.repository.UpiRepository
import com.a2zsuvidhaa.`in`.model.DoubleTxn
import com.a2zsuvidhaa.`in`.util.AppConstants
import com.a2zsuvidhaa.`in`.util.VolleyClient
import com.a2zsuvidhaa.`in`.util.enums.DmtType
import com.a2zsuvidhaa.`in`.util.ui.ContextInjectUtil
import com.a2zsuvidhaa.`in`.util.ui.Resource
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MoneyTransferViewModel @Inject constructor(
    private val appPreference: AppPreference,
    private val dmtWalletRepository: DmtWalletRepository,
    private val upiRepository: UpiRepository,
    private val volleyClient: VolleyClient,
    private val contextInjectUtil: ContextInjectUtil
) : BaseViewModel() {


    lateinit var txnPin: String
    var isTransactionListVisible = false

    lateinit var beneficiary: Beneficiary
    lateinit var dmtType: DmtType
    var moneySender: MoneySender? = null
    var amount: String = ""
    var isAmountValidate = false
    val remark = " Transaction"
    var channel = "2"
    var location: Location? = globalLocation
    var isChargeFetched = false


    private val _dmtCommissionResponseObs = MutableLiveData<Resource<DmtCommissionResponse>>()
    val dmtCommissionResponseObs: LiveData<Resource<DmtCommissionResponse>> =
        _dmtCommissionResponseObs

    fun fetchTransactionCharge() {
        _dmtCommissionResponseObs.value = Resource.Loading()
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    dmtWalletRepository.commissionCharge(
                        hashMapOf(
                            "amount" to amount,
                            "txnChargeApiName" to "PAYTM",
                            "txn_pin" to txnPin,
                        )
                    )
                }.body()!!
                _dmtCommissionResponseObs.value = Resource.Success(response)
            } catch (e: Exception) {
                _dmtCommissionResponseObs.value = Resource.Failure(e)
            }
        }
    }


    private val _bankDownCheckObs = MutableLiveData<Resource<BankDownCheckResponse>>()
    val bankDownCheckCheckObs: LiveData<Resource<BankDownCheckResponse>> =
        _bankDownCheckObs

    fun bankDownCheck() {
        _bankDownCheckObs.value = Resource.Loading()
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    dmtWalletRepository.bankDownCheck(
                        hashMapOf(
                            "bank_name" to beneficiary.bankName.orEmpty(),
                        )
                    )
                }
                _bankDownCheckObs.value = Resource.Success(response)
            } catch (e: Exception) {
                _bankDownCheckObs.value = Resource.Failure(e)
            }
        }
    }


    fun upiBankDownCheck() {
        _bankDownCheckObs.value = Resource.Loading()
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    upiRepository.bankDownCheck(
                        hashMapOf(
                            "upiId" to beneficiary.accountNumber.orEmpty(),
                        )
                    )
                }
                _bankDownCheckObs.value = Resource.Success(response)
            } catch (e: Exception) {
                _bankDownCheckObs.value = Resource.Failure(e)
            }
        }
    }


    private val _transferObs = MutableLiveData<Resource<TransactionDetailResponse>>()
    val transferObs: LiveData<Resource<TransactionDetailResponse>> = _transferObs

    fun dmtTransfer() {
        _transferObs.value = Resource.Loading()
        viewModelScope.launch {
            try {
                val param = hashMapOf(
                    "beneName" to beneficiary.name.orEmpty(),
                    "bank_account" to beneficiary.accountNumber.orEmpty(),
                    "account_number" to beneficiary.accountNumber.orEmpty(),//dmt3
                    "mobile_number" to moneySender?.mobileNumber.orEmpty(),
                    "mobile" to moneySender?.mobileNumber.orEmpty(),//dmt3
                    "sender_name" to moneySender?.firstName.orEmpty() + " " + moneySender?.lastName.toString(),//dmt3
                    "amount" to amount,
                    "channel" to channel,
                    "txn_pin" to txnPin,
                    "a2z_bene_id" to beneficiary.a2zBeneId.orEmpty(),
                    "beneficiary_id" to beneficiary.a2zBeneId.orEmpty(),//dmt3
                    "latitude" to location?.latitude.toString(),
                    "longitude" to location?.longitude.toString(),
                )

                val url = when (dmtType) {
                    DmtType.WALLET_ONE -> "a2z/plus/wallet/transaction"
                    DmtType.WALLET_TWO -> "a2z/plus/wallet-two/transaction"
                    DmtType.WALLET_THREE -> "a2z/plus/wallet-three/transaction"
                    DmtType.DMT_THREE -> "dmt-three/transaction"
                    else -> throw Exception("Unknown Dmt service, please contact with admin")
                }


                volleyClient.postRequest(
                    url = AppConstants.BASE_URL + url,
                    params = param,
                    onSuccess = {
                        val response =
                            Gson().fromJson(it.toString(), TransactionDetailResponse::class.java)
                        _transferObs.value = Resource.Success(response)
                    },
                    onFailure = {
                        appPreference.doubleTxn = DoubleTxn(
                            isException = true,
                            number = beneficiary.accountNumber.orEmpty(),
                            amount = amount,
                            type = "money",
                            time = System.currentTimeMillis() + AppConstants.TRANSACTION_WAITING_TIME
                        )
                        _transferObs.value = Resource.Failure(it)
                    },
                    timeOutInSecond = 60
                )
            } catch (e: Exception) {
                appPreference.doubleTxn = DoubleTxn(
                    isException = true,
                    number = beneficiary.accountNumber.orEmpty(),
                    amount = amount,
                    type = "money",
                    time = System.currentTimeMillis() + AppConstants.TRANSACTION_WAITING_TIME
                )
                _transferObs.value = Resource.Failure(e)
            }
        }
    }


    fun upiTransfer() {
        _transferObs.value = Resource.Loading()
        viewModelScope.launch {
            try {
                val param = hashMapOf(
                    "amount" to amount,
                    "bene_id" to beneficiary.id.orEmpty(),
                    "sender_number" to moneySender?.mobileNumber.orEmpty(),
                    "latitude" to location?.latitude.toString(),
                    "longitude" to location?.longitude.toString(),
                    "txn_pin" to txnPin,
                )


                volleyClient.postRequest(
                    url = AppConstants.BASE_URL + "vpa/payment",
                    params = param,
                    onSuccess = {

                        val status = when (it.optInt("status")) {
                            1, 2, 3, 34, 37, 24 -> 1
                            else -> 2
                        }

                        val response = TransactionDetailResponse(
                            status = status,
                            message = it.optString("message"),
                            data = Gson().fromJson(it.toString(), TransactionDetail::class.java)
                        )
                        _transferObs.value = Resource.Success(response)
                    },
                    onFailure = {
                        appPreference.doubleTxn = DoubleTxn(
                            isException = true,
                            number = beneficiary.accountNumber.orEmpty(),
                            amount = amount,
                            type = "money",
                            time = System.currentTimeMillis() + AppConstants.TRANSACTION_WAITING_TIME
                        )
                        _transferObs.value = Resource.Failure(it)
                    },
                    timeOutInSecond = 60
                )
            } catch (e: Exception) {
                appPreference.doubleTxn = DoubleTxn(
                    isException = true,
                    number = beneficiary.accountNumber.orEmpty(),
                    amount = amount,
                    type = "money",
                    time = System.currentTimeMillis() + AppConstants.TRANSACTION_WAITING_TIME
                )
                _transferObs.value = Resource.Failure(e)
            }
        }
    }


}