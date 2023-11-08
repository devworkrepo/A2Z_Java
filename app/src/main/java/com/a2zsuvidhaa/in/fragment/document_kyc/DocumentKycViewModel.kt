package com.a2zsuvidhaa.`in`.fragment.document_kyc

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.a2zsuvidhaa.`in`.BaseViewModel
import com.a2zsuvidhaa.`in`.data.repository.KycRepository
import com.a2zsuvidhaa.`in`.data.response.CommonResponse
import com.a2zsuvidhaa.`in`.data.response.DocumentKycDetail
import com.a2zsuvidhaa.`in`.data.response.DocumentKycResponse
import com.a2zsuvidhaa.`in`.util.apis.Resource
import com.a2zsuvidhaa.`in`.util.ui.ContextInjectUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class DocumentKycViewModel @Inject constructor(
    private val kycRepository: KycRepository,
    private val contextInjectUtil: ContextInjectUtil
) : BaseViewModel() {
    enum class DocType {
        PAN, AADHHAAR_FRONT, AADHAAR_BACK, PROFILE_IMAGE, SHOP_IMAGE, CANCEL_CHEQUE, SEAL_CHEQUE, GST_IMAGE
    }

    lateinit var imagePickerDocType: DocType
    lateinit var fileUploadDocType: DocType
    var panFile: File? = null
    var profileFile: File? = null
    var aadhaarFrontFile: File? = null
    var aadhaarBackFile: File? = null
    var shopFile: File? = null
    var cancelChequeFile: File? = null
    var sealChequeFile: File? = null
    var gstFile: File? = null
    var registerUserId: String? = null


    private val _onUploadedFilesDetailsObs = MutableLiveData<Resource<DocumentKycResponse>>()
    val onUploadedFilesDetailsObs: LiveData<Resource<DocumentKycResponse>> =
        _onUploadedFilesDetailsObs


    fun getUploadedFilesDetails() {
        _onUploadedFilesDetailsObs.value = Resource.Loading()
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    // contextInjectUtil.loadJSONFromAsset<DocumentKycResponse>("document_list_detail")

                    apiRequest {
                        if (registerUserId == null)
                            kycRepository.uploadedFilesDetails()
                        else kycRepository.registerUserUploadedFilesDetails(registerUserId!!)
                    }
                }
                _onUploadedFilesDetailsObs.value = Resource.Success(response)
            } catch (e: Exception) {
                _onUploadedFilesDetailsObs.value = Resource.Failure(e)
            }
        }
    }


    private val _onFileUploadedObs = MutableLiveData<Resource<CommonResponse>>()
    val onFileUploadedObs: LiveData<Resource<CommonResponse>> = _onFileUploadedObs


    fun uploadFiles() {

        var uploadPanPart: MultipartBody.Part? = null
        var uploadProfilePart: MultipartBody.Part? = null
        var uploadAadhaarFrontPart: MultipartBody.Part? = null
        var uploadAadhaarBackPart: MultipartBody.Part? = null
        var uploadShopPart: MultipartBody.Part? = null
        var uploadCancelChequePart: MultipartBody.Part? = null
        var uploadSealChequePart: MultipartBody.Part? = null
        var uploadGstPart: MultipartBody.Part? = null


        when (fileUploadDocType) {
            DocType.PAN -> uploadPanPart = getMultipartFormData(panFile, "pan_card_image")
            DocType.PROFILE_IMAGE -> uploadProfilePart =
                getMultipartFormData(profileFile, "profile_picture")
            DocType.AADHHAAR_FRONT -> uploadAadhaarFrontPart =
                getMultipartFormData(aadhaarFrontFile, "aadhaar_card_image")
            DocType.AADHAAR_BACK -> uploadAadhaarBackPart =
                getMultipartFormData(aadhaarBackFile, "aadhaar_img_back")
            DocType.SHOP_IMAGE -> uploadShopPart = getMultipartFormData(shopFile, "shop_image")
            DocType.CANCEL_CHEQUE -> uploadCancelChequePart =
                getMultipartFormData(cancelChequeFile, "cheque_image")
            DocType.SEAL_CHEQUE -> uploadSealChequePart =
                getMultipartFormData(sealChequeFile, "seal_image")
            DocType.GST_IMAGE -> uploadGstPart = getMultipartFormData(gstFile, "gst_image")
        }

        _onFileUploadedObs.value = Resource.Loading()
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    apiRequest {
                        if (registerUserId == null)
                            kycRepository.uploadDocs(
                                panFilePart = uploadPanPart,
                                profileFilePart = uploadProfilePart,
                                aadhaarFrontPart = uploadAadhaarFrontPart,
                                aadhaarBackPart = uploadAadhaarBackPart,
                                shopPart = uploadShopPart,
                                cancelChequePart = uploadCancelChequePart,
                                sealChequePart = uploadSealChequePart,
                                gstPart = uploadGstPart,
                            )
                        else {
                            val userIdBodyPart =
                                registerUserId!!.toRequestBody("multipart/form-data".toMediaTypeOrNull())

                            kycRepository.registerUserUploadDocs(
                                panFilePart = uploadPanPart,
                                profileFilePart = uploadProfilePart,
                                aadhaarFrontPart = uploadAadhaarFrontPart,
                                aadhaarBackPart = uploadAadhaarBackPart,
                                shopPart = uploadShopPart,
                                cancelChequePart = uploadCancelChequePart,
                                sealChequePart = uploadSealChequePart,
                                gstPart = uploadGstPart,
                                userId = userIdBodyPart
                            )
                        }
                    }
                }
                _onFileUploadedObs.value = Resource.Success(response)
            } catch (e: Exception) {
                _onFileUploadedObs.value = Resource.Failure(e)
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


