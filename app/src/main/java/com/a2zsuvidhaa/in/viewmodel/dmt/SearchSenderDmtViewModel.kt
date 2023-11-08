package com.a2zsuvidhaa.`in`.viewmodel.dmt

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.a2zsuvidhaa.`in`.BaseViewModel
import com.a2zsuvidhaa.`in`.data.model.BankDownResponse
import com.a2zsuvidhaa.`in`.data.model.dmt.MoneySenderResponse
import com.a2zsuvidhaa.`in`.data.model.dmt.SenderAccountDetailResponse
import com.a2zsuvidhaa.`in`.data.repository.DmtThreeRepository
import com.a2zsuvidhaa.`in`.data.repository.DmtWalletRepository
import com.a2zsuvidhaa.`in`.data.repository.UpiRepository
import com.a2zsuvidhaa.`in`.util.apis.SingleMutableLiveData
import com.a2zsuvidhaa.`in`.util.enums.DmtType
import com.a2zsuvidhaa.`in`.util.ui.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class SearchSenderDmtViewModel @Inject constructor(
    private val dmtWalletRepository: DmtWalletRepository,
    private val dmtThreeRepository: DmtThreeRepository,
    private val upiRepository: UpiRepository
) : BaseViewModel() {

    lateinit var dmtType: DmtType
    var number: String = ""
    var searchMode = SearchMode.MOBILE
    var bankDownResponse: BankDownResponse? = null

    var searchedAccount = ""
    var searchedIfscCode = ""


    private val _moneySenderSearchResponseObs =
        SingleMutableLiveData<Resource<MoneySenderResponse>>()
    val moneySenderSearchResponseObs: LiveData<Resource<MoneySenderResponse>> =
        _moneySenderSearchResponseObs

    fun searchRemitterMobile() {

        _moneySenderSearchResponseObs.value = Resource.Loading()

        viewModelScope.launch(Dispatchers.Main) {
            try {
                val response = withContext(Dispatchers.IO) {

                    val param = hashMapOf("mobile_number" to number,"mobile" to number)

                    when (dmtType) {
                        DmtType.WALLET_ONE -> dmtWalletRepository.searchMobileNumberWalletOne(param)
                            .body()!!
                        DmtType.WALLET_TWO -> dmtWalletRepository.searchMobileNumberWalletTwo(param)
                            .body()!!
                        DmtType.WALLET_THREE -> dmtWalletRepository.searchMobileNumberWalletThree(
                            param
                        ).body()!!
                        DmtType.DMT_THREE -> dmtThreeRepository.searchMobileNumberDmtThree(param)
                            .body()!!
                        DmtType.UPI -> upiRepository.searchSender(param).body()!!
                        else -> throw Exception("Unknown Dmt service, please contact with admin")
                    }

                }
                _moneySenderSearchResponseObs.value = Resource.Success(response)

            } catch (e: Exception) {
                _moneySenderSearchResponseObs.value = Resource.Failure(e)
            }
        }

    }


    private val _searchRemitterAccountObs =
        SingleMutableLiveData<Resource<SenderAccountDetailResponse>>()
    val searchRemitterAccountObs: LiveData<Resource<SenderAccountDetailResponse>> =
        _searchRemitterAccountObs

    fun searchRemitterAccount() {
        _searchRemitterAccountObs.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.Main) {
            try {
                val response = withContext(Dispatchers.IO) {
                    dmtWalletRepository.searchAccountNumber(
                        hashMapOf(
                            "account_number" to number
                        )
                    ).body()!!
                }
                _searchRemitterAccountObs.value = Resource.Success(response)
            } catch (e: Exception) {
                _searchRemitterAccountObs.value = Resource.Failure(e)
            }
        }
    }


    private val _bankDownObs = MutableLiveData<Resource<BankDownResponse>>()
    val bankDownObs: LiveData<Resource<BankDownResponse>> = _bankDownObs


    fun fetchBankDown() {
        _bankDownObs.value = Resource.Loading()
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    dmtWalletRepository.bankDown()
                    //contextInjectUtil.loadJSONFromAsset<BankDownResponse>("bank_down_test")
                }
                bankDownResponse = response
                _bankDownObs.value = Resource.Success(response)
            } catch (e: Exception) {
                _bankDownObs.value = Resource.Failure(e)
            }
        }
    }


    enum class SearchMode { MOBILE, ACCOUNT }

}