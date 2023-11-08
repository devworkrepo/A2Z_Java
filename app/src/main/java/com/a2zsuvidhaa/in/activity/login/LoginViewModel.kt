package com.a2zsuvidhaa.`in`.activity.login

import android.location.Location
import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.a2zsuvidhaa.`in`.BaseViewModel
import com.a2zsuvidhaa.`in`.AppPreference
import com.a2zsuvidhaa.`in`.data.repository.AuthRepository
import com.a2zsuvidhaa.`in`.model.Login
import com.a2zsuvidhaa.`in`.util.AppLog
import com.a2zsuvidhaa.`in`.util.Security
import com.a2zsuvidhaa.`in`.util.apis.Resource
import com.a2zsuvidhaa.`in`.util.apis.SingleMutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import javax.inject.Inject


var  globalLocation : Location? = null


@HiltViewModel
class LoginViewModel @Inject constructor(
        private val appPreference: AppPreference,
        private val security: Security,
        private val loginRepository: AuthRepository
) : BaseViewModel() {

    var loginId: String = ""
    var password: String = ""
    var case = "FIRST"
    var hardwareSerialNumber: String = if (Build.SERIAL != null) Build.SERIAL else "not available"
    var deviceName: String = Build.BRAND
    var deviceToken: String =""
    var imei: String = ""

    var location : Location? = null


    var isCheckObs = MutableLiveData(false)

    init {

        val isLoginRemember = appPreference.autoLogin
        isCheckObs.value = isLoginRemember

    }


    private val _loginObs = SingleMutableLiveData<Resource<Login>>()
    val loginObs: LiveData<Resource<Login>> = _loginObs

    fun login() {
        _loginObs.value = Resource.Loading()

        val enPassword = security.encrypt(password)
        val enMobileNumber = security.encrypt(loginId)
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    apiRequest {
                        loginRepository.login(
                                case = case,
                                password = enPassword,
                                mobileNumber = enMobileNumber,
                                deviceToken = deviceToken,
                                hardwareSerialNumber = hardwareSerialNumber,
                                deviceName = deviceName,
                                imei = imei,
                                latitude = location?.latitude.toString(),
                                longitude = location?.longitude.toString(),
                        )
                    }
                }
                if (response.status == 1) saveLoginData(response)
                _loginObs.value = Resource.Success(response)
            } catch (e: Exception) {
                AppLog.d("exception_test : ${e.printStackTrace()}")
                _loginObs.value = Resource.Failure(e)
            }
        }
    }

    private fun saveLoginData(login: Login) {

        if (isCheckObs.value!!) {
            appPreference.loginID = security.encrypt(loginId)
            appPreference.loginPassword = security.encrypt(password)
            appPreference.autoLogin = true
        } else {
            appPreference.deleteLoginPassword()
            appPreference.deleteLoginID()
            appPreference.deleteAutoLogin()
            appPreference.autoLogin = false
        }


        if (login.is_offline_kyc == 1) appPreference.userKYC = 1
        if ( login.is_pan_verified  == 1)  appPreference.userKYC = 2

        appPreference.userKYC =login.user_kyc
        appPreference.emailVerified =login.is_email_verified
        appPreference.mobileVerified =login.is_mobile_verified
        appPreference.id =login.id
        appPreference.setChangePin(login.changePin)
        appPreference.token =login.token
        appPreference.name =login.name
        appPreference.mobile = security.encrypt(login.mobile)
        appPreference.userBalance =login.user_balance
        appPreference.roleTitle =login.role_title
        appPreference.otp =login.otp_number
        appPreference.rollId =login.role_id
        appPreference.email = security.encrypt(login.email)
        appPreference.profilePic =login.profile_picture
        appPreference.pancarD_PIC =login.pancard_img
        appPreference.adhaarPic = security.encrypt(login.adhaar_img)
        appPreference.adhaaR_BACK_PIC =login.adhaar_back_img
        appPreference.shopName =login.shop_name
        appPreference.shopAdress =login.shop_address
        appPreference.address =login.address
        appPreference.joiningDate =login.joining_date
        appPreference.lastUpdate =login.last_update
        appPreference.stateid = "" +login.state_id
        appPreference.pincode =login.pincode
        appPreference.adhaar =login.adhaar
        appPreference.kyC_STATUS = 1
        appPreference.popup = login.popup
        appPreference.popupSee = false

        val isLoginIdCreated = login.is_login_id_created ?: "0"
        appPreference.isLoginIdCreated = isLoginIdCreated.toInt()


        appPreference.isAadhaarKyc = login.is_aadhaar_kyc
        appPreference.isVideoKyc = login.is_video_kyc
        appPreference.settlementBankInfo = login.is_user_has_active_settlemnet_account
        appPreference.kyc = login.aeps_kyc

        appPreference.matmStatus = login.matm
        appPreference.mposStatus = login.mpos

        appPreference.aepsDriviers = login.aepsDrivier



    }


}