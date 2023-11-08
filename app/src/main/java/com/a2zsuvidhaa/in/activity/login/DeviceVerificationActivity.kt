package com.a2zsuvidhaa.`in`.activity.login

import android.app.Dialog
import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import com.a2zsuvidhaa.`in`.databinding.ActivityDeviceVerificationBinding
import com.a2zsuvidhaa.`in`.listener.WebApiCallListener
import com.a2zsuvidhaa.`in`.util.APIs
import com.a2zsuvidhaa.`in`.util.AppUitls
import com.a2zsuvidhaa.`in`.util.Security
import com.a2zsuvidhaa.`in`.util.WebApiCall
import com.a2zsuvidhaa.`in`.util.dialogs.StatusDialog
import com.a2zsuvidhaa.`in`.util.ents.hide
import com.a2zsuvidhaa.`in`.util.ents.show
import com.a2zsuvidhaa.`in`.util.ents.showToast
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import javax.inject.Inject

@AndroidEntryPoint
class DeviceVerificationActivity : AppCompatActivity() {

    @Inject
    lateinit var security: Security

    private lateinit var binding: ActivityDeviceVerificationBinding
    private lateinit var otp: String
    private var timer: CountDownTimer? = null

    private lateinit var mobileNumber: String

    private var progress : Dialog? = null

    private var resendCount : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeviceVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mobileNumber = intent.getStringExtra("mobile_number")!!

        binding.btnVerify.setOnClickListener { verifyOtp() }

        binding.btnResend.setOnClickListener { resendOtp() }

        countDownTime()
    }


    private fun countDownTime() {

        binding.btnResend.hide()
        binding.tvCountDown.show()
        binding.tvWaitingHint.show()

        timer?.cancel()

        timer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val timerInSecond = (millisUntilFinished / 1000).toInt()
                binding.tvCountDown.text = timerInSecond.toString()
            }

            override fun onFinish() {
                binding.btnResend.show()
                binding.tvCountDown.hide()
                binding.tvWaitingHint.hide()
            }
        }
        timer?.start()
    }


    private fun resendOtp() {

        resendCount += 1

        binding.run {
            tvWaitingHint.hide()
            btnResend.hide()
            progress.show()
        }

        val params = hashMapOf(
                "count" to resendCount.toString(),
                "mobileNumber" to security.encrypt(mobileNumber),
                "type" to security.encrypt("NEW_DEVICE_OTP"),
        )

        WebApiCall.postRequest(this, APIs.WALLET_PLUS_REMITTER_REGISTRATIOIN_RESEND_OTP, params)
        WebApiCall.webApiCallback(object : WebApiCallListener {
            override fun onSuccessResponse(jsonObject: JSONObject) {

                binding.run {
                    btnResend.show()
                    progress.hide()
                }


                val status = jsonObject.getInt("status")
                val message = jsonObject.optString("message")

                this@DeviceVerificationActivity.showToast(message)
                if (status == 700) {
                    countDownTime()
                }
                 else  StatusDialog.failure(this@DeviceVerificationActivity,message)
            }

            override fun onFailure(message: String) {
                StatusDialog.failure(this@DeviceVerificationActivity,message)
            }
        })
    }

    private fun verifyOtp() {


        if (!validateInput()) return@verifyOtp


        progress = StatusDialog.progress(this,"Verifying")

        val params = hashMapOf(
                "mobileNumber" to security.encrypt(mobileNumber),
                "otp" to security.encrypt(otp),
                "imei" to AppUitls.id(this)
        )

        WebApiCall.postRequest(this,APIs.VERIFY_DEVICE,params)
        WebApiCall.webApiCallback(object : WebApiCallListener{
            override fun onSuccessResponse(jsonObject: JSONObject) {
                progress?.dismiss()
                val status = jsonObject.getInt("status")
                val message = jsonObject.optString("message")

                if(status == 701) {
                    StatusDialog.success(this@DeviceVerificationActivity,message){
                        onBackPressed()
                    }
                } else StatusDialog.failure(this@DeviceVerificationActivity,message)
            }

            override fun onFailure(message: String) {
                progress?.dismiss()
                StatusDialog.failure(this@DeviceVerificationActivity,message)
            }
        })
    }


    private fun validateInput(): Boolean {
        var isValidate = true
        otp = binding.edOtp.text.toString()
        if (otp.length != 6) {
            binding.tilOtp.apply {
                error = "Enter valid 6 digits otp"
                isErrorEnabled = true
            }
            isValidate = false
        } else binding.tilOtp.isErrorEnabled = false
        return isValidate
    }
}