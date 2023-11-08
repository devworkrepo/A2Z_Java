package com.a2zsuvidhaa.`in`.activity


import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.text.InputFilter.LengthFilter
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.a2zsuvidhaa.`in`.PidParser2
import com.a2zsuvidhaa.`in`.R
import com.a2zsuvidhaa.`in`.AppPreference
import com.a2zsuvidhaa.`in`.databinding.ActivityKYCApprovalBinding
import com.a2zsuvidhaa.`in`.listener.WebApiCallListener
import com.a2zsuvidhaa.`in`.util.*
import com.a2zsuvidhaa.`in`.util.dialogs.AepsDialogs
import com.a2zsuvidhaa.`in`.util.dialogs.Dialogs
import com.a2zsuvidhaa.`in`.util.dialogs.StatusDialog
import com.a2zsuvidhaa.`in`.util.ents.*
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class AepsKycActivity : AppCompatActivity() {


    @Inject
    lateinit var appPreference: AppPreference


    private lateinit var binding: ActivityKYCApprovalBinding

    private var selectRDDeviceDialog: Dialog? = null
    private var verifyOtpDialog: Dialog? = null

    private var pidData: String = ""

    private var progressDialog: Dialog? = null


    private var timer: CountDownTimer? = null
    private var dProgress: ProgressBar? = null
    private var dBtnResend: Button? = null
    private var dBtnVerify: Button? = null
    private var dBtnCancel: ImageButton? = null
    private var dTvTimer: TextView? = null
    private var dTvMessage: TextView? = null
    private var dTvWaitingHint: TextView? = null
    private var latitude = ""
    private var longitude = ""

    private val mLocationManager: LocationManager by lazy {
        getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }


    @SuppressLint("ResourceType")
    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityKYCApprovalBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)

        setupToolbar(toolbar, "Aeps Kyc")

        binding.btnNext.setOnClickListener { v: View? -> selectRDDevice() }
        kycDetails()

        fetchLocation()
    }

    private fun kycDetails() {
        val progressDialog: Dialog = StatusDialog.progress(this, "Loading")
        WebApiCall.getRequestWithHeader(this, APIs.USER_KYC_DETAILS)
        WebApiCall.webApiCallback(object : WebApiCallListener {
            override fun onSuccessResponse(jsonObject: JSONObject) {
                progressDialog.dismiss()
                try {
                    val status = jsonObject.getInt("status")
                    val message = jsonObject.optString("message")
                    if (status == 1) {
                        val detailObject = jsonObject.getJSONObject("details")
                        binding.edName.setText(detailObject.getString("agent_name"))
                        binding.edPanCard.setText(detailObject.getString("pan_number"))
                        binding.edAadharNumber.setText(detailObject.getString("aadhaar_number"))
                        binding.edMerchantId.setText(detailObject.getString("merchant_login_id"))
                    } else {
                        StatusDialog.failure(this@AepsKycActivity, message)
                    }
                } catch (e: Exception) {
                    StatusDialog.failure(this@AepsKycActivity, e.message.toString())
                }
            }

            override fun onFailure(message: String) {
                progressDialog.dismiss()
                StatusDialog.failure(this@AepsKycActivity, message)
            }
        })
    }

    private fun selectRDDevice() {

        selectRDDeviceDialog = AepsDialogs.selectRDDevice(
            context = this,
            type = "KYC",
            selectedDevice = appPreference.selectedRdServiceDevice,
            onApprove = { device, packageUrl ->

                appPreference.setSelectRdServiceDevice(device)
                selectRDDeviceDialog?.hide()
                captureData(packageUrl)
            })
    }


    private fun captureData(packageUrl: String) {
        try {
            val pidOption =
                "<PidOptions ver=\"1.0\"><Opts env=\"P\" fCount=\"1\" fType=\"2\" iCount=\"0\" format=\"0\" pidVer=\"2.0\" timeout=\"15000\" wadh=\"E0jzJ/P8UopUHAieZn8CKqS4WPMi5ZSYXgfnlfkWjrc=\" posh=\"UNKNOWN\" /></PidOptions>"

            val intent = Intent()
            intent.setPackage(packageUrl)
            intent.action = "in.gov.uidai.rdservice.fp.CAPTURE"
            intent.putExtra("PID_OPTIONS", pidOption)
            startActivityForResult(intent, RD_SERVICE_RESPONSE_DODE)
        } catch (e: Exception) {
            StatusDialog.failure(this, "Please connect biometric device") {
                selectRDDeviceDialog?.show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RD_SERVICE_RESPONSE_DODE) {
            if (resultCode == RESULT_OK) {
                try {
                    if (data != null) {
                        val result = data.getStringExtra("PID_DATA")
                        if (result != null) {
                            try {
                                val respString: Array<String> = PidParser2.parse(result)
                                if (respString[0].equals("0", ignoreCase = true)) {
                                    selectRDDeviceDialog?.dismiss()
                                    pidData = result

                                    if (latitude.isEmpty() && longitude.isEmpty()) {
                                        fetchLocation(onStart = {
                                            progressDialog = StatusDialog.progress(this)
                                        }, onFetch = {
                                            progressDialog?.dismiss()
                                            requestOtp()
                                        })
                                    } else requestOtp()

                                } else {
                                    StatusDialog.failure(
                                        this,
                                        "Please check you have connect correct biometric device"
                                    ) {
                                        selectRDDeviceDialog?.show()
                                    }
                                }
                            } catch (e: Exception) {
                                StatusDialog.failure(
                                    this,
                                    "Please check you have connect correct biometric device"
                                ) {
                                    selectRDDeviceDialog?.show()
                                }
                            }
                        } else {
                            StatusDialog.failure(
                                this,
                                "Please check you have connect correct biometric device"
                            ) {
                                selectRDDeviceDialog?.show()
                            }
                        }
                    } else {
                        StatusDialog.failure(
                            this,
                            "Please check you have connect correct biometric device"
                        ) {
                            selectRDDeviceDialog?.show()
                        }
                    }
                } catch (e: Exception) {
                    StatusDialog.failure(
                        this,
                        "Please check you have connect correct biometric device"
                    ) {
                        selectRDDeviceDialog?.show()
                    }
                }
            } else {
                StatusDialog.failure(
                    this,
                    "Please check you have connect correct biometric device"
                ) {
                    selectRDDeviceDialog?.show()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun requestOtp() {
        val progressDialog: Dialog = StatusDialog.progress(this, "Requesting for OTP")
        WebApiCall.getRequestWithHeader(this, APIs.SEND_OTP+"?latitude="+latitude+"&longitude="+longitude)
        WebApiCall.webApiCallback(object : WebApiCallListener {
            override fun onSuccessResponse(jsonObject: JSONObject) {
                progressDialog.dismiss()
                try {
                    val message = jsonObject.optString("message")
                    val status = jsonObject.getInt("status")
                    if (status == 1) {
                        showVerifyOtpDialog()
                    } else StatusDialog.failure(this@AepsKycActivity, message)
                    showToast(message)
                } catch (e: Exception) {
                    StatusDialog.failure(this@AepsKycActivity, e.message.toString())
                }
            }

            override fun onFailure(message: String) {
                progressDialog.dismiss()
                StatusDialog.failure(this@AepsKycActivity, message)
            }
        })
    }

    private fun showVerifyOtpDialog() {

        verifyOtpDialog = Dialogs.otpVerify(this).apply {

            dProgress = findViewById(R.id.progress)

            dBtnResend = findViewById(R.id.btn_resend)
            dBtnVerify = findViewById(R.id.btn_verify)
            dBtnCancel = findViewById(R.id.btn_cancel)

            dTvTimer = findViewById(R.id.tv_count_down)
            dTvMessage = findViewById(R.id.tv_message)
            dTvWaitingHint = findViewById(R.id.tv_waiting_hint)

            val edOtp = findViewById<EditText>(R.id.ed_otp)
            edOtp.filters = arrayOf(LengthFilter(7))


            dBtnVerify?.setOnClickListener {
                val otp = edOtp.text.toString()
                if (otp.length == 7) {
                    verifyOtpDialog?.hide()
                    verifyOtp(otp)
                } else showToast("Enter 7 digit otp")
            }

            dBtnCancel?.setOnClickListener { dismiss() }
            dBtnResend?.setOnClickListener { resendOtp() }

            countDownTime()

            setOnDismissListener { timer?.cancel() }

        }
    }

    private fun verifyOtp(otp: String) {

        progressDialog = StatusDialog.progress(this, "Verifying OTP")

        val params = HashMap<String, String>()
        params["otp"] = otp
        params["latitude"] = latitude
        params["longitude"] = longitude
        WebApiCall.postRequest(this, APIs.VERIFY_OTP, params)
        WebApiCall.webApiCallback(object : WebApiCallListener {
            override fun onSuccessResponse(jsonObject: JSONObject) {
                progressDialog?.dismiss()
                try {
                    val status = jsonObject.getInt("status")
                    val message = jsonObject.optString("message")
                    if (status == 1) {
                        verifyOtpDialog?.dismiss()
                        val dialog: Dialog = StatusDialog.success(this@AepsKycActivity, message)
                        dialog.setOnDismissListener { dialog1: DialogInterface? -> proceedForKyc() }
                    } else {
                        StatusDialog.failure(this@AepsKycActivity, message) {
                            verifyOtpDialog?.show()
                        }
                    }
                } catch (e: Exception) {
                    StatusDialog.failure(this@AepsKycActivity, e.message.toString()) {
                        verifyOtpDialog?.show()
                    }
                }
            }

            override fun onFailure(message: String) {
                progressDialog?.dismiss()
                StatusDialog.failure(this@AepsKycActivity, message) {
                    verifyOtpDialog?.show()
                }
            }
        })
    }

    private fun resendOtp() {

        dProgress?.show()
        dBtnResend?.hide()
        dTvTimer?.hide()
        dTvWaitingHint?.hide()

        WebApiCall.getRequestWithHeader(this, APIs.RESEND_OTP_AES)
        WebApiCall.webApiCallback(object : WebApiCallListener {
            override fun onSuccessResponse(jsonObject: JSONObject) {

                val message = jsonObject.optString("message")
                val status = jsonObject.getInt("status")

                if (status == 1) {
                    countDownTime()
                } else {
                    dProgress?.hide()
                    dBtnResend?.hide()
                    dTvTimer?.text = message
                    dTvWaitingHint?.hide()
                }

                MakeToast.show(this@AepsKycActivity, message)
            }

            override fun onFailure(message: String) {
                dProgress?.hide()
                dBtnResend?.hide()
                dTvTimer?.text = "Error Occurred!"
                dTvWaitingHint?.hide()
            }
        })
    }


    private fun proceedForKyc() {
        selectRDDeviceDialog?.dismiss()
        progressDialog = StatusDialog.progress(this)
        val params = HashMap<String, String>()
        params["biometricData"] = pidData
        params["latitude"] = latitude
        params["longitude"] = longitude
        WebApiCall.postRequest(this, APIs.VERIFY_KYC, params)
        WebApiCall.webApiCallback(object : WebApiCallListener {
            override fun onSuccessResponse(jsonObject: JSONObject) {
                progressDialog?.dismiss()
                try {
                    val status = jsonObject.getInt("status")
                    val message = jsonObject.optString("message")
                    if (status == 1) {
                        appPreference.kyc = 1
                        StatusDialog.success(
                            this@AepsKycActivity,
                            "KYC successfully completed."
                        ) {
                            goToMainActivity()
                        }
                    } else StatusDialog.failure(this@AepsKycActivity, message)
                } catch (e: Exception) {
                    StatusDialog.failure(this@AepsKycActivity, e.message.toString())
                }
            }

            override fun onFailure(message: String) {
                progressDialog?.dismiss()
                StatusDialog.failure(this@AepsKycActivity, message)
            }
        })
    }

    companion object {
        private const val RD_SERVICE_RESPONSE_DODE = 122
    }

    private fun countDownTime() {

        dTvTimer?.show()
        dTvWaitingHint?.show()
        dBtnVerify?.isEnabled = true
        dProgress?.hide()
        dBtnResend?.hide()

        timer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val timerInSecond = (millisUntilFinished / 1000).toInt()
                dTvTimer?.text = timerInSecond.toString()
            }

            override fun onFinish() {
                dTvTimer?.hide()
                dTvWaitingHint?.hide()
                dBtnVerify?.isEnabled = true
                dProgress?.hide()
                dBtnResend?.show()
            }
        }
        timer?.start()
    }

    private fun fetchLocation(onStart: () -> Unit = {}, onFetch: (Location?) -> Unit = {}) {
        val isLocationServiceEnable = LocationService.isLocationEnabled(this)
        if (isLocationServiceEnable) {
            onStart()
            requestForLocationPhoneReadState {
                LocationService.getCurrentLocation(mLocationManager)
                LocationService.setupListener(object : LocationService.MLocationListener {
                    override fun onLocationChange(location: Location) {
                        latitude = location.latitude.toString()
                        longitude = location.longitude.toString()
                        AppLog.d("Location fetched : $location")
                        onFetch(location)
                    }
                })
            }
        }
    }

}
