package com.a2zsuvidhaa.`in`.fragment.addhar_kyc

import android.app.Dialog
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import com.a2zsuvidhaa.`in`.R
import com.a2zsuvidhaa.`in`.AppPreference
import com.a2zsuvidhaa.`in`.data.response.AadhaarKycResponse
import com.a2zsuvidhaa.`in`.databinding.FragmentAadhaarKycBinding
import com.a2zsuvidhaa.`in`.fragment.BaseFragment
import com.a2zsuvidhaa.`in`.util.AppLog
import com.a2zsuvidhaa.`in`.util.LocationService
import com.a2zsuvidhaa.`in`.util.apis.Resource
import com.a2zsuvidhaa.`in`.util.dialogs.AepsDialogs
import com.a2zsuvidhaa.`in`.util.dialogs.Dialogs
import com.a2zsuvidhaa.`in`.util.dialogs.StatusDialog
import com.a2zsuvidhaa.`in`.util.ents.*
import com.a2zsuvidhaa.`in`.util.requestForLocationPhoneReadState
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AadhaarKycFragment() :
    BaseFragment<FragmentAadhaarKycBinding>(R.layout.fragment_aadhaar_kyc) {


    private var verifyOtpDialog: Dialog? = null

    private val viewModel: AadhaarKycViewModel by viewModels()


    @Inject
    lateinit var appPreference: AppPreference

    private val mLocationManager: LocationManager by lazy {
        activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }


    private var dProgress: ProgressBar? = null
    private var dBtnVerify: Button? = null
    private var dBtnCancel: ImageButton? = null
    private var dTvTimer: TextView? = null
    private var dTvMessage: TextView? = null
    private var dTvWaitingHint: TextView? = null
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userId = arguments?.getString("user_id")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.registerUserId = userId

        if (appPreference.isAadhaarKyc == 1) {
            binding.run {
                layoutContent.hide()
                layoutDone.show()
            }
        }

        if (viewModel.registerUserId != null) {
            binding.layoutContent.show()
            binding.layoutDone.hide()
        }

        binding.btnSubmit.setOnClickListener {
            confirmInputs {
                if (viewModel.latitude.isEmpty() && viewModel.longitude.isEmpty()) {
                    fetchLocation(onStart = {
                        progressDialog = StatusDialog.progress(requireContext())
                    }, onFetch = {
                        progressDialog?.dismiss()
                        viewModel.postAadhaarKyc()
                    })
                } else viewModel.postAadhaarKyc()
            }
        }

        onTextChangeListener()

        subscribeObserver()
        fetchLocation()

    }


    private fun confirmInputs(onConfirm: () -> Unit) {

        AepsDialogs.aadhaarKycConfirm(
            requireActivity(),
            viewModel.aadhhaarNumber, viewModel.mobileNumber
        ).apply {
            findViewById<Button>(R.id.btn_confirm).setOnClickListener {
                dismiss()
                onConfirm()

            }
        }


    }

    private fun subscribeObserver() {
        viewModel.onPostKycObs.observe(viewLifecycleOwner, { onPostKycResponse(it) })
        viewModel.onVerifyKycObs.observe(viewLifecycleOwner, { onVerifyKycResponse(it) })
    }

    private fun onVerifyKycResponse(resource: Resource<AadhaarKycResponse>) {
        when (resource) {
            is Resource.Loading -> progressDialog = StatusDialog.progress(requireActivity())
            is Resource.Success -> {
                progressDialog?.dismiss()
                if (resource.data.status == 1) {
                    if (viewModel.registerUserId != null)
                        appPreference.isAadhaarKyc = 1
                    verifyOtpDialog?.dismiss()
                    StatusDialog.success(requireActivity(), resource.data.message) {
                        activity?.onBackPressed()
                    }
                } else StatusDialog.failure(requireActivity(), resource.data.message)
            }

            is Resource.Failure -> {
                progressDialog?.dismiss()
                StatusDialog.failure(requireActivity(), resource.exception.message.toString())
            }
        }
    }

    private fun onPostKycResponse(resource: Resource<AadhaarKycResponse>) {
        when (resource) {
            is Resource.Loading -> progressDialog = StatusDialog.progress(requireActivity())
            is Resource.Success -> {
                progressDialog?.dismiss()
                if (resource.data.status == 3) {
                    viewModel.requestId = resource.data.requestId
                    handleOtpVerifyDialog("Otp has sent to your mobile number, please enter below") {
                        viewModel.verifyAadhaarKyc()
                    }

                } else StatusDialog.failure(requireActivity(), resource.data.message)
            }

            is Resource.Failure -> {
                progressDialog?.dismiss()
                StatusDialog.failure(requireActivity(), resource.exception.message.toString())
            }
        }
    }


    private inline fun handleOtpVerifyDialog(message: String, crossinline onSuccess: () -> Unit) {
        verifyOtpDialog = Dialogs.otpVerify(requireActivity()).apply {

            dProgress = findViewById(R.id.progress)

            findViewById<RelativeLayout>(R.id.rv_count).hide()

            findViewById<Button>(R.id.btn_resend).hide()
            dBtnVerify = findViewById(R.id.btn_verify)
            dBtnCancel = findViewById(R.id.btn_cancel)

            dTvTimer = findViewById(R.id.tv_count_down)
            dTvMessage = findViewById(R.id.tv_message)
            dTvWaitingHint = findViewById(R.id.tv_waiting_hint)

            dTvWaitingHint?.hide()

            dTvMessage?.text = message

            dBtnVerify?.setOnClickListener {
                viewModel.otp = findViewById<EditText>(R.id.ed_otp).text.toString()
                if (viewModel.otp.length == 6) {
                    onSuccess()
                } else activity?.showToast("Enter 6 digit otp")
            }

            dBtnCancel?.setOnClickListener { dismiss() }

        }
    }


    var strAadhaarNumber = ""
    var strOldlen = 0

    private fun onTextChangeListener() {
        binding.edAadharNumber.afterTextChange {

            strAadhaarNumber = it
            val strLen = strAadhaarNumber.length
            if (strOldlen < strLen) {
                if (strLen > 0) {
                    if (strLen == 4 || strLen == 9) {
                        strAadhaarNumber = "$strAadhaarNumber-"
                        binding.edAadharNumber.setText(strAadhaarNumber)
                        binding.edAadharNumber.setSelection(binding.edAadharNumber.text!!.length)
                    } else {
                        if (strLen == 5) {
                            if (!strAadhaarNumber.contains("-")) {
                                var tempStr = strAadhaarNumber.substring(0, strLen - 1)
                                tempStr += "-" + strAadhaarNumber.substring(strLen - 1, strLen)
                                binding.edAadharNumber.setText(tempStr)
                                binding.edAadharNumber.setSelection(binding.edAadharNumber.text!!.length)
                            }
                        }
                        if (strLen == 10) {
                            if (strAadhaarNumber.lastIndexOf("-") != 9) {
                                var tempStr = strAadhaarNumber.substring(0, strLen - 1)
                                tempStr += "-" + strAadhaarNumber.substring(strLen - 1, strLen)
                                binding.edAadharNumber.setText(tempStr)
                                binding.edAadharNumber.setSelection(binding.edAadharNumber.text!!.length)
                            }
                        }
                        strOldlen = strLen
                    }
                }
            } else {
                strOldlen = strLen
                Log.i(
                    "MainActivity ",
                    "keyDel is Pressed ::: strLen : $strLen\n old Str Len : $strOldlen"
                )
            }
            viewModel.aadhhaarNumber = strAadhaarNumber
            if (viewModel.aadhhaarNumber.length == 14 && viewModel.mobileNumber.length == 10) {
                binding.btnSubmit.setupVisibility(true)
            } else binding.btnSubmit.setupVisibility(false)

        }

        binding.edMobileNumber.afterTextChange {
            viewModel.mobileNumber = it
            if (viewModel.aadhhaarNumber.length == 14 && viewModel.mobileNumber.length == 10) {
                binding.btnSubmit.setupVisibility(true)
            } else binding.btnSubmit.setupVisibility(false)
        }
    }

    companion object {
        fun newInstance(userId: String? = null) = AadhaarKycFragment().apply {
            arguments = bundleOf(
                "user_id" to userId
            )
        }
    }


    private fun fetchLocation(onStart: () -> Unit = {}, onFetch: (Location?) -> Unit = {}) {
        val isLocationServiceEnable = LocationService.isLocationEnabled(requireContext())
        if (isLocationServiceEnable) {
            onStart()
            activity?.requestForLocationPhoneReadState {
                LocationService.getCurrentLocation(mLocationManager)
                LocationService.setupListener(object : LocationService.MLocationListener {
                    override fun onLocationChange(location: Location) {
                        viewModel.latitude = location.latitude.toString()
                        viewModel.longitude = location.longitude.toString()
                        AppLog.d("Location fetched : $location")
                        onFetch(location)
                    }
                })
            }
        }
    }

}