package com.a2zsuvidhaa.`in`.activity.login_id.forgot

import android.app.Dialog
import android.os.Bundle
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.a2zsuvidhaa.`in`.R
import com.a2zsuvidhaa.`in`.databinding.ActivityForgotLoginIdBinding
import com.a2zsuvidhaa.`in`.util.apis.Resource
import com.a2zsuvidhaa.`in`.util.dialogs.Dialogs
import com.a2zsuvidhaa.`in`.util.dialogs.StatusDialog
import com.a2zsuvidhaa.`in`.util.ents.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotLoginIdActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotLoginIdBinding
    private val viewModel: ForgotLoginIdViewModel by viewModels()
    private var progressDialog: Dialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotLoginIdBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar(binding.toolbar, "Forgot Login ID")


        onTextChangeListener()

        subscribers()

        binding.btnSubmit.setOnClickListener {
            viewModel.mobileNumber = binding.edMobileNumber.text.toString()
            viewModel.aadhaarNumber = binding.edAadharNumber.text.toString()
            viewModel.forgotLoginId()
        }

    }

    private fun subscribers() {


        viewModel.onForgotLoginId.observe(this) {
            when (it) {
                is Resource.Loading -> {
                    progressDialog = StatusDialog.progress(this)
                }
                is Resource.Success -> {
                    progressDialog?.dismiss()
                    if (it.data.status == 1) {
                        handleOtpVerifyDialog(it.data.message) {
                            viewModel.forgotLoginIdVerify()
                        }
                    } else StatusDialog.failure(this, it.data.message)
                }
                is Resource.Failure -> {
                    progressDialog?.dismiss()
                    handleNetworkFailure(it.exception)
                }
            }
        }

        viewModel.onVerifyOtp.observe(this) {
            when (it) {
                is Resource.Loading -> {
                    progressDialog = StatusDialog.progress(this)
                }
                is Resource.Success -> {
                    progressDialog?.dismiss()
                    if (it.data.status == 1) {
                        viewModel.appPreference.isLoginIdCreated = 1
                        StatusDialog.success(this, it.data.message) {
                            onBackPressed()
                        }
                    } else StatusDialog.failure(this, it.data.message)
                }
                is Resource.Failure -> {
                    progressDialog?.dismiss()
                    handleNetworkFailure(it.exception)
                }
            }
        }
    }


    private inline fun handleOtpVerifyDialog(message: String, crossinline onSuccess: () -> Unit) {
        Dialogs.otpVerify(this).apply {


            findViewById<RelativeLayout>(R.id.rv_count).hide()

            findViewById<Button>(R.id.btn_resend).hide()
            val dBtnVerify: Button = findViewById(R.id.btn_verify)
            val dBtnCancel: ImageButton = findViewById(R.id.btn_cancel)

            val dTvMessage: TextView = findViewById(R.id.tv_message)
            val dTvWaitingHint: TextView = findViewById(R.id.tv_waiting_hint)

            dTvWaitingHint.hide()

            dTvMessage.text = message

            dBtnVerify.setOnClickListener {
                viewModel.otp = findViewById<EditText>(R.id.ed_otp).text.toString()
                if (viewModel.otp.length == 6) {
                    onSuccess()
                } else showToast("Enter 6 digit otp")
            }

            dBtnCancel.setOnClickListener { dismiss() }

        }
    }


    private fun onTextChangeListener() {

        binding.edMobileNumber.afterTextChange {
            validateInput()
        }
        binding.edAadharNumber.afterTextChange {
            validateInput()
        }
    }

    private fun validateInput(){

        val validateMobile = validateMobileInput()
        val validateAadhaar = validateAadhaarInput()

        if(validateMobile && validateAadhaar){
            binding.btnSubmit.apply {
                alpha = 1.0f
                isEnabled = true
            }
        }
        else{
            binding.btnSubmit.apply {
                alpha = 0.5f
                isEnabled = false
            }
        }
    }

    private fun validateMobileInput(): Boolean {
        val mobileNumber = binding.edMobileNumber.text.toString()
        return if (mobileNumber.isEmpty()) {
            binding.tilMobileNumber.isErrorEnabled = false
            false
        }
        else if (mobileNumber.length == 10) {
            binding.tilMobileNumber.isErrorEnabled = false
            true
        }else {
            binding.tilMobileNumber.error = "Enter 10 digit mobile number"
            binding.tilMobileNumber.isErrorEnabled = true
            false
        }
    }

    private fun validateAadhaarInput(): Boolean {
        val aadhaarNumber = binding.edAadharNumber.text.toString()

        return if (aadhaarNumber.isEmpty()) {
            binding.tilAadhaarNumber.isErrorEnabled = false
            false
        }
        else if (aadhaarNumber.length == 12) {
            binding.tilAadhaarNumber.isErrorEnabled = false
            true
        } else {
            binding.tilAadhaarNumber.error = "Enter 12 digit aadhaar number"
            binding.tilAadhaarNumber.isErrorEnabled = true
            false
        }
    }

}