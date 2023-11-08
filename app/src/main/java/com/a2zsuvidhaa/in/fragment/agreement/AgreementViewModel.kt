package com.a2zsuvidhaa.`in`.fragment.agreement

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.a2zsuvidhaa.`in`.BaseViewModel
import com.a2zsuvidhaa.`in`.data.repository.AgreementRepository
import com.a2zsuvidhaa.`in`.data.response.CommonResponse
import com.a2zsuvidhaa.`in`.model.AgreementInitialInfoResponse
import com.a2zsuvidhaa.`in`.model.AgreementStartResponse
import com.a2zsuvidhaa.`in`.util.ui.Resource
import com.squareup.okhttp.ResponseBody
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class AgreementViewModel @Inject constructor(
    private val repository: AgreementRepository
) : BaseViewModel() {

    lateinit var agreementType: AgreementFragment.Companion.AgreementType
    var orderId: String? = null
    var shouldCheckStatus: Boolean = false
    private val _agreementInitialInfoObs = MutableLiveData<Resource<AgreementInitialInfoResponse>>()
    val agreementInitialInfoObs: LiveData<Resource<AgreementInitialInfoResponse>> =
        _agreementInitialInfoObs


    fun fetchInitialAgreement() {
        _agreementInitialInfoObs.value = Resource.Loading()
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    when (agreementType) {
                        AgreementFragment.Companion.AgreementType.IRCTC -> repository.fetchInitialIrctcAgreement()
                        AgreementFragment.Companion.AgreementType.USER -> repository.fetchInitialAgreement()
                    }
                }
                _agreementInitialInfoObs.value = Resource.Success(response)
            } catch (e: Exception) {
                _agreementInitialInfoObs.value = Resource.Failure(e)
            }
        }
    }

    private val _generatePdfObs = MutableLiveData<Resource<CommonResponse>>()
    val generatePdfObs: LiveData<Resource<CommonResponse>> =
        _generatePdfObs

    fun generateAgreementPdf() {
        _generatePdfObs.value = Resource.Loading()
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {

                    when (agreementType) {
                        AgreementFragment.Companion.AgreementType.IRCTC -> repository.generateIrctcAgreementPdf()
                        AgreementFragment.Companion.AgreementType.USER -> repository.generateAgreementPdf()
                    }


                }
                _generatePdfObs.value = Resource.Success(response)
            } catch (e: Exception) {
                _generatePdfObs.value = Resource.Failure(e)
            }
        }
    }


    private val _agreementStartObs = MutableLiveData<Resource<AgreementStartResponse>>()
    val agreementStartObs: LiveData<Resource<AgreementStartResponse>> =
        _agreementStartObs

    fun startAgreement() {
        _agreementStartObs.value = Resource.Loading()
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {

                    when (agreementType) {
                        AgreementFragment.Companion.AgreementType.IRCTC -> repository.startIrctcAgreement()
                        AgreementFragment.Companion.AgreementType.USER -> repository.startAgreement()
                    }
                }
                _agreementStartObs.value = Resource.Success(response)
            } catch (e: Exception) {
                _agreementStartObs.value = Resource.Failure(e)
            }
        }
    }

    private val _checkStatusObs = MutableLiveData<Resource<CommonResponse>>()
    val checkStatusObs: LiveData<Resource<CommonResponse>> =
        _checkStatusObs

    fun checkStatus(delayInSecond: Int = 10) {
        if (!shouldCheckStatus) return
        _checkStatusObs.value = Resource.Loading()
        viewModelScope.launch {

            try {
                val response = withContext(Dispatchers.IO) {
                    delay(timeMillis = (1000 * delayInSecond).toLong())


                    when (agreementType) {
                        AgreementFragment.Companion.AgreementType.IRCTC ->
                            repository.checkStatusIrctc(
                                hashMapOf(
                                    "order_id" to orderId.toString()
                                )
                            )
                        AgreementFragment.Companion.AgreementType.USER ->
                            repository.checkStatus(
                                hashMapOf(
                                    "order_id" to orderId.toString()
                                )
                            )
                    }


                }
                _checkStatusObs.value = Resource.Success(response)
            } catch (e: Exception) {
                _checkStatusObs.value = Resource.Failure(e)
            }
        }
    }

    private val _downloadAgreementObs = MutableLiveData<Resource<CommonResponse>>()
    val downloadAgreementObs: LiveData<Resource<CommonResponse>> =
        _downloadAgreementObs

    fun downloadAgreementReport() {
        _downloadAgreementObs.value = Resource.Loading()
        viewModelScope.launch {

            try {
                val response = withContext(Dispatchers.IO) {
                    val result =
                        withContext(Dispatchers.Default) {

                            when (agreementType) {
                                AgreementFragment.Companion.AgreementType.IRCTC ->
                                    repository.agreementIrctcDownload(
                                        hashMapOf("order_id" to orderId.toString())
                                    )
                                AgreementFragment.Companion.AgreementType.USER ->
                                    repository.agreementDownload(
                                        hashMapOf("order_id" to orderId.toString())
                                    )
                            }


                        }

                    if (result.status == 1)
                        when (agreementType) {
                            AgreementFragment.Companion.AgreementType.IRCTC ->
                                repository.agreementDownloadReportIrctc(hashMapOf("order_id" to orderId.toString()))
                            AgreementFragment.Companion.AgreementType.USER ->
                                repository.agreementDownloadReport(hashMapOf("order_id" to orderId.toString()))
                        }
                    else throw java.lang.Exception(result.message)
                }

                _downloadAgreementObs.value = Resource.Success(response)
            } catch (e: Exception) {
                _downloadAgreementObs.value = Resource.Failure(e)
            }
        }
    }

}