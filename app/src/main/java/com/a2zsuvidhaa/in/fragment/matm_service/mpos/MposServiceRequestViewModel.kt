package com.a2zsuvidhaa.`in`.fragment.matm_service.mpos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.a2zsuvidhaa.`in`.BaseViewModel
import com.a2zsuvidhaa.`in`.data.model.matm.MatmServiceInformation
import com.a2zsuvidhaa.`in`.data.model.matm.MatmServiceInformationfoResponse
import com.a2zsuvidhaa.`in`.data.model.matm.MposDocTypeResponse
import com.a2zsuvidhaa.`in`.data.repository.MatmRepository
import com.a2zsuvidhaa.`in`.data.response.CommonResponse
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
import javax.inject.Inject

@HiltViewModel
class MposServiceRequestViewModel @Inject constructor(
    private val contextInjectUtil: ContextInjectUtil,
    private val matmRepository: MatmRepository
) : BaseViewModel() {

    enum class DocType {
        SHOP_INSIDE, SHOP_OUTSIDE, BUSINESS_LEGALITY, BUSINESS_ADDRESS
    }

    var matmInfo: MatmServiceInformation? = null
    var docTypeListResponse: MposDocTypeResponse? = null

    lateinit var imagePickerDocType: DocType
    var shopInsideFile: File? = null
    var shopOutsideFile: File? = null
    var businessLegalityFile: File? = null
    var businessAddressFile: File? = null

    var businessAddressType : String = ""
    var businessLegalityType : String = ""


    var businessAddressImageStatus = 0
    var businessLegalityImageStatus = 0
    var shopInsideImageStatus = 0
    var shopOutsideImageStatus = 0

    var businessLegalityTypeStatus = 0
    var businessAddressTypeStatus = 0


    private val _fetchInfoObs = MutableLiveData<Resource<MatmServiceInformationfoResponse>>()
    val fetchInfoObs: LiveData<Resource<MatmServiceInformationfoResponse>> = _fetchInfoObs


    fun fetchInfoAndTypeList() {
        _fetchInfoObs.value = Resource.Loading()
        try {
            viewModelScope.launch(Dispatchers.Main) {
                val infoResponse = matmRepository.fetchOrderInfo().body()!!
                matmInfo = infoResponse.data

                if (infoResponse.status == 2 || infoResponse.data?.service_status == "4") {
                    _fetchInfoObs.value = Resource.Success(infoResponse)
                } else {
                    docTypeListResponse = matmRepository.getDocTypeList().body()!!
                    _fetchInfoObs.value = Resource.Success(infoResponse)
                }
            }
        }catch (e: Exception){
            _fetchInfoObs.value = Resource.Failure(e)
        }
    }


    private val _uploadDetailsObs = MutableLiveData<Resource<CommonResponse>>()
    val uploadDetailsObs: LiveData<Resource<CommonResponse>> = _uploadDetailsObs

    fun uploadDetails() {
        val shopInsideFilePart = getMultipartFormData(shopInsideFile, "shop_inside_image")
        val shopOutsideFilePart = getMultipartFormData(shopInsideFile, "shop_outside_image")
        val businessLegalityFilePart = getMultipartFormData(shopInsideFile, "business_proof_image")
        val businessAddressFilePart = getMultipartFormData(shopInsideFile, "business_address_proof_image")
        val businessLegalityTypeBodyPart = businessLegalityType.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val businessAddressTypeBodyPart = businessAddressType.toRequestBody("multipart/form-data".toMediaTypeOrNull())


        _uploadDetailsObs.value = Resource.Loading()

        viewModelScope.launch {

            try {
                val response = withContext(Dispatchers.IO) {
                    apiRequest {
                        matmRepository.uploadDetail(
                            shopInsideFilePart = shopInsideFilePart,
                            shopOutsideFilePart = shopOutsideFilePart,
                            businessLegalityFilePart = businessLegalityFilePart,
                            businessAddressFilePart = businessAddressFilePart,
                            businessAddressTypeBodyPart = businessAddressTypeBodyPart,
                            businessLegalityTypeBodyPart = businessLegalityTypeBodyPart,

                        )
                    }
                }

                _uploadDetailsObs.value = Resource.Success(response)


            } catch (e: Exception) {
                _uploadDetailsObs.value = Resource.Failure(e)
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


