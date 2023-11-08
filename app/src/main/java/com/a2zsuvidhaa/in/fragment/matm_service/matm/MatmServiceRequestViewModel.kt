package com.a2zsuvidhaa.`in`.fragment.matm_service.matm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.a2zsuvidhaa.`in`.BaseViewModel
import com.a2zsuvidhaa.`in`.data.model.matm.MatmServiceInformation
import com.a2zsuvidhaa.`in`.data.model.matm.MatmServiceInformationfoResponse
import com.a2zsuvidhaa.`in`.data.repository.MatmRepository
import com.a2zsuvidhaa.`in`.data.response.CommonResponse
import com.a2zsuvidhaa.`in`.util.AppUtil
import com.a2zsuvidhaa.`in`.util.apis.Resource
import com.a2zsuvidhaa.`in`.util.file.FragmentImagePicker
import com.a2zsuvidhaa.`in`.util.ui.ContextInjectUtil
import com.google.android.gms.common.internal.service.Common
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class MatmServiceRequestViewModel @Inject constructor(
    private val contextInjectUtil: ContextInjectUtil,
    private val matmRepository: MatmRepository
) : BaseViewModel() {

    enum class DocType {
        PAN_CARD, ADDRESS_PROOF_IMAGE,
    }

    var matmInfo: MatmServiceInformation? = null
    lateinit var imagePickerDocType: DocType

    var gstNumber: String = ""
    var shopAddress: String = ""
    var shopName: String = ""
    var mobileNumber: String = ""
    var emailId: String = ""
    var name: String = ""
    var city: String = ""
    var aadhaar: String = ""
    var pan: String = ""
    var courierAddress: String = ""
    var landmark: String = ""
    var pinCode: String = ""
    var panCardFile: File? = null
    var addressProofFile: File? = null
    var isAddressCheck: Boolean = false


    //
    var panImageStatus = 0
    var addressProofImageStatus = 0
    var shopAddressStatus = 0
    var shopNameStatus = 0
    var mobileStatus = 0
    var emailStatus = 0
    var cityStatus = 0
    var nameStatus = 0
    var aadhaarStatus = 0
    var panStatus = 0
    var courierAddressStatus = 0
    var landmarkStatus = 0
    var pinCodeStatus = 0
    var matmReceivedStatus = 0

    var otp = ""


    private val _fetchInfoObs = MutableLiveData<Resource<MatmServiceInformationfoResponse>>()
    val fetchInfoObs: LiveData<Resource<MatmServiceInformationfoResponse>> = _fetchInfoObs

    fun fetchInfo() {

        gstNumber = ""
        shopAddress = ""
        courierAddress = ""
        landmark = ""
        pinCode = ""
        panCardFile = null
        addressProofFile = null
        isAddressCheck = false

        panImageStatus = 0
        addressProofImageStatus = 0
        shopAddressStatus = 0
        shopNameStatus = 0
        mobileStatus = 0
        emailStatus = 0
        courierAddressStatus = 0
        landmarkStatus = 0
        pinCodeStatus = 0
        matmReceivedStatus = 0
        cityStatus = 0
        nameStatus = 0
        aadhaarStatus = 0
        panStatus = 0


        matmInfo = null

        _fetchInfoObs.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.Main) {
            try {
                val response = withContext(Dispatchers.IO) {
                    //contextInjectUtil.loadJSONFromAsset<MatmOrderInfoResponse>("matm_form_submit")
                    matmRepository.fetchOrderInfo().body()!!
                }
                _fetchInfoObs.value = Resource.Success(response)
            } catch (e: Exception) {
                _fetchInfoObs.value = Resource.Failure(e)
            }
        }
    }


    private val _requestOtpObs = MutableLiveData<Resource<CommonResponse>>()
    val requestOtpObs: LiveData<Resource<CommonResponse>> = _requestOtpObs

    fun requestOtp() {
        _requestOtpObs.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.Main) {
            try {
                val response = withContext(Dispatchers.IO) {
                    matmRepository.requestOTp().body()!!
                }
                _requestOtpObs.value = Resource.Success(response)
            } catch (e: Exception) {
                _requestOtpObs.value = Resource.Failure(e)
            }
        }
    }


    private val _verifyOtpObs = MutableLiveData<Resource<CommonResponse>>()
    val verifyOtpObs: LiveData<Resource<CommonResponse>> = _verifyOtpObs

    fun verifyOtp() {
        _verifyOtpObs.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.Main) {
            try {
                val response = withContext(Dispatchers.IO) {
                    matmRepository.verifyOtp(otp).body()!!
                }
                _verifyOtpObs.value = Resource.Success(response)
            } catch (e: Exception) {
                _verifyOtpObs.value = Resource.Failure(e)
            }
        }
    }


    private val _uploadDetailsObs = MutableLiveData<Resource<CommonResponse>>()
    val uploadDetailsObs: LiveData<Resource<CommonResponse>> = _uploadDetailsObs

    fun uploadDetails() {


        AppUtil.logger("HelloDev: ${FragmentImagePicker.mLocation?.latitude.toString()}")

        val panCardFilePart = getMultipartFormData(panCardFile, "pan_image")
        val addressProofFilePart = getMultipartFormData(addressProofFile, "address_proof_image")
        val gstNumberBodyPart = gstNumber.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val shopAddressBodyPart =
            shopAddress.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val shopNameBodyPart =
            shopName.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val mobileBodyPart =
            mobileNumber.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val emailBodyPart =
            emailId.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        val nameBodyPart =
            name.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val cityBodyPart =
            city.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val aadhaarBodyPart =
            aadhaar.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val panBodyPart =
            pan.toRequestBody("multipart/form-data".toMediaTypeOrNull())


        val landmarkBodyPart = landmark.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val pinCodeBodyPart = pinCode.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val courierBodyPart =
            courierAddress.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        val matmReceivedBodyPart =
            matmReceivedStatus.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val latitudeBodyPart =
            FragmentImagePicker.mLocation?.latitude.toString()
                .toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val longitudeBodyPart =
            FragmentImagePicker.mLocation?.longitude.toString()
                .toRequestBody("multipart/form-data".toMediaTypeOrNull())


        _uploadDetailsObs.value = Resource.Loading()

        viewModelScope.launch {

            try {
                val response = withContext(Dispatchers.IO) {
                    apiRequest {
                        matmRepository.uploadDetail(
                            panCardFilePart = panCardFilePart,
                            addressProofFilePart = addressProofFilePart,
                            gstNumberBodyPart = gstNumberBodyPart,
                            shopAddressBodyPart = shopAddressBodyPart,
                            mobileBodyPart = mobileBodyPart,
                            emailBodyPart = emailBodyPart,
                            shopNameBodyPart = shopNameBodyPart,
                            courierAddressBodyPart = courierBodyPart,
                            landmarkBodyPart = landmarkBodyPart,
                            pinCodeBodyPart = pinCodeBodyPart,
                            nameBodyPart = nameBodyPart,
                            cityBodyPart = cityBodyPart,
                            aadhaarBodyPart = aadhaarBodyPart,
                            panBodyPart = panBodyPart,
                            matmReceivedBodyPart = matmReceivedBodyPart,
                            latitudeBodyPart = latitudeBodyPart,
                            longitudeBodyPart = longitudeBodyPart
                        )
                    }
                }

                _uploadDetailsObs.value = Resource.Success(response)


            } catch (e: Exception) {
                _uploadDetailsObs.value = Resource.Failure(e)
            }
        }
    }


    private val _orderNowObs = MutableLiveData<Resource<CommonResponse>>()
    val orderNowObs: LiveData<Resource<CommonResponse>> = _orderNowObs

    fun orderNow() {
        _orderNowObs.value = Resource.Loading()
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    matmRepository.orderNow().body()!!
                    //contextInjectUtil.loadJSONFromAsset<CommonResponse>("matm_order_request")

                }
                _orderNowObs.value = Resource.Success(response)
            } catch (e: Exception) {
                _orderNowObs.value = Resource.Failure(e)
            }
        }
    }


    private fun getMultipartFormData(file: File?, fileField: String): MultipartBody.Part? {
        val requestBody = getRequestBody(file) ?: return null
        return MultipartBody.Part.createFormData(fileField, file?.name, requestBody)
    }

    private fun getRequestBody(file: File?) =
        file?.asRequestBody("multipart/form-data".toMediaTypeOrNull())


}


