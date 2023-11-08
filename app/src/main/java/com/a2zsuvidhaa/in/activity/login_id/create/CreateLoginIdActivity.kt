package com.a2zsuvidhaa.`in`.activity.login_id.create

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.a2zsuvidhaa.`in`.R
import com.a2zsuvidhaa.`in`.data.response.CommonResponse
import com.a2zsuvidhaa.`in`.databinding.ActivityCreateLoginIdBinding
import com.a2zsuvidhaa.`in`.util.AppConstants
import com.a2zsuvidhaa.`in`.util.apis.Resource
import com.a2zsuvidhaa.`in`.util.dialogs.Dialogs
import com.a2zsuvidhaa.`in`.util.dialogs.StatusDialog
import com.a2zsuvidhaa.`in`.util.ents.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateLoginIdActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateLoginIdBinding
    private val viewModel: CreateLoginIdViewModel by viewModels()
    private var progressDialog: Dialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateLoginIdBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar(binding.toolbar, "Create Login ID")

        if (viewModel.appPreference.isLoginIdCreated == 1) {
            StatusDialog.alert(
                this,
                "You have already created login id! please login with login id"
            ) {
                onBackPressed()
            }
        }

        onTextChangeListener()

        subscribers()

        binding.btnSubmit.setOnClickListener {
            viewModel.loginId = binding.edLoginId.text.toString()
            viewModel.confirmLoginId = binding.edConfirmLoginId.text.toString()
            viewModel.createLoginId()
        }

    }

    private fun subscribers() {


        viewModel.createLoginIdObs.observe(this) {
            when (it) {
                is Resource.Loading -> {
                    progressDialog = StatusDialog.progress(this)
                }
                is Resource.Success -> {
                    progressDialog?.dismiss()
                    if (it.data.status == 1) {
                        handleOtpVerifyDialog(it.data.message) {
                            viewModel.verifyLoginId()
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
        binding.edLoginId.afterTextChange {
            if (validateLoginId()) {
                if (validateConfirmLoginId()) {
                    binding.btnSubmit.isEnabled = true
                    binding.btnSubmit.alpha = 1.0f
                } else {
                    binding.btnSubmit.isEnabled = false
                    binding.btnSubmit.alpha = 0.5f
                }
            }
        }

        binding.edConfirmLoginId.afterTextChange {
            if (validateConfirmLoginId()) {
                binding.btnSubmit.isEnabled = true
                binding.btnSubmit.alpha = 1.0f
            } else {
                binding.btnSubmit.isEnabled = false
                binding.btnSubmit.alpha = 0.5f
            }
        }
    }

    private fun validateConfirmLoginId(): Boolean {
        val confirmLoginId = binding.edConfirmLoginId.text.toString()
        val loginId = binding.edLoginId.text.toString()

        return if (loginId.isEmpty() || confirmLoginId.isEmpty()) {
            binding.tilConfirmLoginId.error = ""
            binding.tilConfirmLoginId.isErrorEnabled = false
            false
        } else if (confirmLoginId == loginId) {
            binding.tilConfirmLoginId.error = ""
            binding.tilConfirmLoginId.isErrorEnabled = false
            true
        } else {
            binding.tilConfirmLoginId.error = "Confirm login id didn't match"
            binding.tilConfirmLoginId.isErrorEnabled = true
            false
        }
    }

    private fun validateLoginId(): Boolean {
        val loginId = binding.edLoginId.text.toString()
        return if (loginId.length in 6..10) {
            binding.tilLoginId.isErrorEnabled = false
            true
        } else {
            binding.tilLoginId.error = "Enter 6 to 10 characters login id"
            binding.tilLoginId.isErrorEnabled = true
            false
        }
    }

}