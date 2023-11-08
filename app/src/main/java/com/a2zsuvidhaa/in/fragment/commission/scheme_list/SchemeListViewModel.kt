package com.a2zsuvidhaa.`in`.fragment.commission.scheme_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.a2zsuvidhaa.`in`.BaseViewModel
import com.a2zsuvidhaa.`in`.data.model.report.CommissionSchemeListResponse
import com.a2zsuvidhaa.`in`.data.repository.ReportRepository
import com.a2zsuvidhaa.`in`.data.response.DocumentKycResponse
import com.a2zsuvidhaa.`in`.util.apis.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class SchemeListViewModel  @Inject constructor(
        private val reportRepository: ReportRepository
): BaseViewModel(){

    var isFetched : Boolean = false


    private val _schemeListObs = MutableLiveData<Resource<CommissionSchemeListResponse>>()
    val schemeListObs: LiveData<Resource<CommissionSchemeListResponse>> = _schemeListObs


    fun fetchSchemeList() {
        _schemeListObs.value = Resource.Loading()
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    reportRepository.schemeList()
                }
                _schemeListObs.value = Resource.Success(response)
            } catch (e: Exception) {
                _schemeListObs.value = Resource.Failure(e)
            }
        }
    }


}