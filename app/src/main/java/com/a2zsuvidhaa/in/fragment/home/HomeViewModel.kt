package com.a2zsuvidhaa.`in`.fragment.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.a2zsuvidhaa.`in`.AppPreference
import com.a2zsuvidhaa.`in`.BaseViewModel
import com.a2zsuvidhaa.`in`.data.model.BankDownBank
import com.a2zsuvidhaa.`in`.data.model.BankDownResponse
import com.a2zsuvidhaa.`in`.data.model.dmt.BankDownCheckResponse
import com.a2zsuvidhaa.`in`.data.repository.HomeRepository
import com.a2zsuvidhaa.`in`.data.response.CommonResponse
import com.a2zsuvidhaa.`in`.model.*
import com.a2zsuvidhaa.`in`.util.apis.Resource
import com.a2zsuvidhaa.`in`.util.apis.SingleMutableLiveData
import com.a2zsuvidhaa.`in`.util.ui.ContextInjectUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
        private val homeRepository: HomeRepository,
        private val appPreference: AppPreference,
        private val contextInjectUtil: ContextInjectUtil
) : BaseViewModel() {

    var news : String = ""
    var bankDownResponse : BankDownResponse ? = null

    //live data observers
    private val _balanceObs = MutableLiveData<Resource<BalanceResponse>>()
    val balanceObs: LiveData<Resource<BalanceResponse>> = _balanceObs

    private val _qrCodeObs = MutableLiveData<Resource<QRCodeResponse>>()
    val qrCodeObs : LiveData<Resource<QRCodeResponse>> = _qrCodeObs


    fun fetchBalanceInfo() {
        _balanceObs.value = Resource.Loading()
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    apiRequest {
                        homeRepository.fetchBalanceInfo()
                    }
                }
                _balanceObs.value = Resource.Success(response)
            } catch (e: Exception) {
                _balanceObs.value = Resource.Failure(e)
            }
        }
    }


    fun fetchQRCodeData() {
        _qrCodeObs.value = Resource.Loading()
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    apiRequest {
                        homeRepository.fetchQurCodeData()
                    }
                }
                _qrCodeObs.value = Resource.Success(response)
            } catch (e: Exception) {
                _qrCodeObs.value = Resource.Failure(e)
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
                    homeRepository.bankDown()
                    //contextInjectUtil.loadJSONFromAsset<BankDownResponse>("bank_down_test")
                }
                bankDownResponse = response
                _bankDownObs.value = Resource.Success(response)
            } catch (e: Exception) {
                _bankDownObs.value = Resource.Failure(e)
            }
        }
    }


    private val _checkKycObs = SingleMutableLiveData<Resource<KycInfoResponse>>()
    val checkKycObs: LiveData<Resource<KycInfoResponse>> = _checkKycObs

    fun checkKyc() {
        _checkKycObs.value = Resource.Loading()
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    apiRequest {
                        homeRepository.kycCheck()
                    }
                }
                _checkKycObs.value = Resource.Success(response)
            } catch (e: Exception) {
                _checkKycObs.value = Resource.Failure(e)
            }
        }
    }



    private val _newsObs = SingleMutableLiveData<Resource<NewsInfo>>()
    val newsObs :LiveData<Resource<NewsInfo>> = _newsObs

    fun fetchNews() {
        _newsObs.value = Resource.Loading()
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    apiRequest {
                        homeRepository.fetchNews(
                                appPreference.id.toString(),
                                appPreference.token
                        )
                    }
                }

                news = response.retailerNews
                _newsObs.value = Resource.Success(response)
            } catch (e: Exception) {
                _newsObs.value = Resource.Failure(e)
            }
        }
    }


    private val _bannerObs = SingleMutableLiveData<Resource<BannerResponse>>()
    val bannerObs :LiveData<Resource<BannerResponse>> = _bannerObs

    fun fetchBanners() {
        _bannerObs.value = Resource.Loading()
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    apiRequest {
                        homeRepository.fetchBanner()
                    }
                }
                _bannerObs.value = Resource.Success(response)
            } catch (e: Exception) {
                _bannerObs.value = Resource.Failure(e)
            }
        }
    }
}


