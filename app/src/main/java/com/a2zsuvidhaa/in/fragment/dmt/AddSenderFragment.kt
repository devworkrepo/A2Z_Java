package com.a2zsuvidhaa.`in`.fragment.dmt

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.a2zsuvidhaa.`in`.R
import com.a2zsuvidhaa.`in`.databinding.FragmentAddSenderBinding
import com.a2zsuvidhaa.`in`.fragment.BaseOtpResendFragment
import com.a2zsuvidhaa.`in`.util.ViewUtil
import com.a2zsuvidhaa.`in`.util.dialogs.StatusDialog
import com.a2zsuvidhaa.`in`.util.ents.*
import com.a2zsuvidhaa.`in`.util.enums.DmtSenderRegisterType
import com.a2zsuvidhaa.`in`.util.enums.DmtType
import com.a2zsuvidhaa.`in`.util.ui.Resource
import com.a2zsuvidhaa.`in`.viewmodel.dmt.AddSenderViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddSenderFragment :
    BaseOtpResendFragment<FragmentAddSenderBinding>(R.layout.fragment_add_sender) {


    private val viewModel by viewModels<AddSenderViewModel>()
    private val args by navArgs<AddSenderFragmentArgs>()


    //dialog fields


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.senderMobile = args.mobileNumber
        viewModel.state = args.state
        viewModel.dmtType = args.dmtType
        viewModel.senderRegisterType = args.registerType
        viewModel.sender = args.sender

        setupToolbar(binding.toolbar.toolbar, "Add Sender")

        setupInitialData()

        binding.btnAddSender.setOnClickListener {
            if (!validateInputs()) return@setOnClickListener
            viewModel.proceed()
        }

        ViewUtil.resetErrorOnTextInputLayout(
            binding.tilFistName,
            binding.tilLastName,
            binding.tilMobileNumber,
            binding.tilPinCode,
            binding.tilAddress,
            binding.layoutOtp.tilOtp,
        )


        startTimer()
        binding.layoutOtp.btnResend.setOnClickListener {
            viewModel.resendOtp();
        }

        subscribeObservers()
    }

    private fun setupInitialData() {
        binding.edFirstName.setText(viewModel.sender?.firstName.orEmpty())
        binding.edLastName.setText(viewModel.sender?.lastName.orEmpty())
        binding.edMobileNumber.setText(viewModel.senderMobile)

        if (viewModel.senderRegisterType == DmtSenderRegisterType.VERIFY_AND_UPDATE) {
            binding.edMobileNumber.isEnabled = false
            binding.layoutOtp.root.show()
            binding.btnAddSender.text = "Verify And Update"
        } else {
            binding.edMobileNumber.isEnabled = true
            binding.layoutOtp.root.hide()
            binding.btnAddSender.text = "Register"
        }
    }

    private fun startTimer() {
        countDownTime(
            binding.layoutOtp.btnResend,
            binding.layoutOtp.tvTicker,
            binding.layoutOtp.tvWaitingHint
        )
    }


    private fun subscribeObservers() {
        viewModel.registerSenderObs.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    progressDialog = StatusDialog.progress(requireActivity(), "Requesting...")
                }
                is Resource.Success -> {
                    progressDialog?.dismiss()
                    if (it.data.status == 12) {
                        StatusDialog.success(requireActivity(), it.data.message) {
                            viewModel.state = it.data.state.orEmpty()
                            viewModel.isRegistered = true
                            binding.btnAddSender.text = "Verify Sender"
                            binding.edMobileNumber.isEnabled = false
                            binding.edFirstName.isEnabled = false
                            binding.edLastName.isEnabled = false
                            binding.edPinCode.isEnabled = false
                            binding.edAddress.isEnabled = false
                            binding.layoutOtp.root.show()
                            startTimer()
                        }
                    } else {
                        StatusDialog.failure(requireActivity(), it.data.message)
                    }
                }
                is Resource.Failure -> {
                    progressDialog?.dismiss()
                    activity?.handleNetworkFailure(it.exception)
                }
            }
        }

        viewModel.verifySenderObs.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    progressDialog = StatusDialog.progress(requireContext(), title = "Verifying")
                }

                is Resource.Success -> {
                    progressDialog?.dismiss()
                    if (it.data.status == 17 || it.data.status == 15) {
                        StatusDialog.success(requireActivity(), it.data.message) {
                            setNavigationResult(viewModel.senderMobile)
                            activity?.onBackPressed()
                        }
                    } else StatusDialog.failure(requireActivity(), it.data.message)
                }
                is Resource.Failure -> {
                    progressDialog?.dismiss()
                    activity?.handleNetworkFailure(it.exception)
                }
            }
        }

        viewModel.resendOtpObs.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    progressDialog = StatusDialog.progress(requireContext())
                }
                is Resource.Success -> {
                    progressDialog?.dismiss()
                    activity?.showToast(it.data.message)
                    if (it.data.status == 12) {
                        if (viewModel.dmtType == DmtType.DMT_THREE)
                            viewModel.state = it.data.state ?: viewModel.state
                        startTimer()
                    }
                }
                is Resource.Failure -> {
                    progressDialog?.dismiss()
                    activity?.handleNetworkFailure(it.exception)
                }
            }
        }
    }


    private fun validateInputs(): Boolean {

        viewModel.senderFirstName = binding.edFirstName.text.toString()
        viewModel.senderLastName = binding.edLastName.text.toString()
        viewModel.pinCode = binding.edPinCode.text.toString()
        viewModel.address = binding.edAddress.text.toString()
        viewModel.otp = binding.layoutOtp.edOtp.text.toString()
        var isValid = true

        if (viewModel.senderMobile.length != 10) {
            binding.tilMobileNumber.apply {
                error = "Enter 10 digits mobile number "
                isErrorEnabled = true
            }
            isValid = false
        } else binding.tilMobileNumber.isErrorEnabled = false

        if (viewModel.senderFirstName.length < 3) {
            binding.tilFistName.apply {
                error = "Enter minimum 3 character"
                isErrorEnabled = true
            }
            isValid = false
        } else binding.tilFistName.isErrorEnabled = false

        if (viewModel.senderLastName.length < 3) {
            binding.tilLastName.apply {
                error = "Enter minimum 3 character"
                isErrorEnabled = true
            }
            isValid = false
        } else binding.tilLastName.isErrorEnabled = false



        if (viewModel.pinCode.length < 6) {
            binding.tilPinCode.apply {
                error = "Enter 6 digits area pin code"
                isErrorEnabled = true
            }
            isValid = false
        } else binding.tilPinCode.isErrorEnabled = false

        if (viewModel.address.length < 3) {
            binding.tilAddress.apply {
                error = "Enter minimum 3 characters"
                isErrorEnabled = true
            }
            isValid = false
        } else binding.tilAddress.isErrorEnabled = false

        if (viewModel.senderRegisterType == DmtSenderRegisterType.VERIFY_AND_UPDATE ||
            viewModel.isRegistered
        ) {
            if (viewModel.otp.length < 6) {
                binding.layoutOtp.tilOtp.apply {
                    error = "Enter 6 digits Otp"
                    isErrorEnabled = true
                }
                isValid = false
            } else binding.layoutOtp.tilOtp.isErrorEnabled = false
        }

        return isValid
    }

}