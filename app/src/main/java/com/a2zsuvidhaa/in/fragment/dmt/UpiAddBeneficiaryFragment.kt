package com.a2zsuvidhaa.`in`.fragment.dmt

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.a2zsuvidhaa.`in`.R
import com.a2zsuvidhaa.`in`.activity.QRScannerActivity
import com.a2zsuvidhaa.`in`.adapter.VpaBankExtensionListAdapter
import com.a2zsuvidhaa.`in`.adapter.VpaBankListAdapter
import com.a2zsuvidhaa.`in`.data.model.dmt.AccountVerify
import com.a2zsuvidhaa.`in`.data.model.dmt.UpiBank
import com.a2zsuvidhaa.`in`.data.model.dmt.VpaBankExtensionResponse
import com.a2zsuvidhaa.`in`.databinding.FragmentUpiAddBeneficiaryBinding
import com.a2zsuvidhaa.`in`.fragment.BaseFragment
import com.a2zsuvidhaa.`in`.util.AppConstants
import com.a2zsuvidhaa.`in`.util.AppUtil
import com.a2zsuvidhaa.`in`.util.PermissionHandler2
import com.a2zsuvidhaa.`in`.util.ViewUtil
import com.a2zsuvidhaa.`in`.util.dialogs.Dialogs
import com.a2zsuvidhaa.`in`.util.dialogs.StatusDialog
import com.a2zsuvidhaa.`in`.util.ents.*
import com.a2zsuvidhaa.`in`.util.ui.Resource
import com.a2zsuvidhaa.`in`.viewmodel.dmt.UpiAddBeneficiaryViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


@AndroidEntryPoint
class UpiAddBeneficiaryFragment :
        BaseFragment<FragmentUpiAddBeneficiaryBinding>(R.layout.fragment_upi_add_beneficiary) {


    private val viewModel: UpiAddBeneficiaryViewModel by viewModels()
    private val args by navArgs<UpiAddBeneficiaryFragmentArgs>()
    private var adapter: VpaBankListAdapter? = null
    private var upiBankList: List<UpiBank>? = arrayListOf()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.moneySender = args.sender

        setupToolbar(binding.toolbar.toolbar, "Add New UPI")

        binding.btnProceed.text = viewModel.getButtonText()

        binding.btnProceed.setOnClickListener {

            viewModel.upiNumber = binding.edUpiId.text.toString()
            when (viewModel.actionType) {
                UpiAddBeneficiaryViewModel.ActionType.VERIFICATION -> {
                    if (viewModel.upiNumber.contains("@")) {
                        viewModel.fetchVerificationCharge()
                    } else {
                        binding.tilUpiId.apply {
                            error = "Enter valid upi id"
                            isErrorEnabled = true
                        }
                    }
                }
                UpiAddBeneficiaryViewModel.ActionType.REGISTRATION -> {
                    viewModel.upiNumber = binding.edUpiId.text.toString()
                    viewModel.beneficiaryName = binding.edBeneficiaryName.text.toString()

                    if (viewModel.upiNumber.isEmpty() || viewModel.beneficiaryName.isEmpty()) {
                        StatusDialog.failure(requireActivity(), "Some fields are empty! please go back and register again")
                    } else {
                        viewModel.registerUpiID()
                    }
                }
            }
        }

        binding.cvScan.setOnClickListener {

            if (UpiAddBeneficiaryViewModel.ActionType.REGISTRATION == viewModel.actionType) {
                requireActivity().showToast("Please reset verification detail to scan upi code")
            } else {
                PermissionHandler2.checkCameraPermission(activity, onPermissionGranted = {
                    val mIntent = Intent(requireActivity(), QRScannerActivity::class.java)
                    startActivityForResult(mIntent, 101)
                })
            }

        }


        setupTextView()

        ViewUtil.resetErrorOnTextInputLayout(
                binding.tilUpiId,
                binding.tilBeneficiaryName
        )
        subscribeObservers()

        binding.btnReset.setOnClickListener {
            Dialogs.commonConfirmDialog(requireContext(), "You are sure! to reset current successful verification detail ?").apply {
                findViewById<Button>(R.id.btn_confirm).setOnClickListener {
                    dismiss()
                    resetVerification()
                }
            }
        }


        viewModel.fetchVpaBank()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 101 && data != null) {

            val upiJson = AppUtil.urlToJson(data.getStringExtra(AppConstants.DATA)) ?: return

            val upiId = upiJson.optString("pa")
            val upiName = upiJson.optString("pn").replace("%20"," ")

            if (upiId.isNotEmpty() && upiId.contains("@")) {

                setupUpiBank()
                binding.recyclerViewUpiExtension.hide()
                viewModel.bankType = "Bank Upi"
                binding.edUpiId.setText(upiId)
                if (upiName.isNotEmpty()
                        && ! upiName.toLowerCase(Locale.ROOT).contains("paytm merchant")
                        && ! upiName.toLowerCase(Locale.ROOT).contains("phonepe merchant")
                        && ! upiName.toLowerCase(Locale.ROOT).contains("phonepemerchant")
                        && ! upiName.toLowerCase(Locale.ROOT).contains("googlepaymerchant")
                        && ! upiName.toLowerCase(Locale.ROOT).contains("google pay merchant")
                        && ! upiName.toLowerCase(Locale.ROOT).contains("googlepay merchant")
                        && ! upiName.toLowerCase(Locale.ROOT).contains("bharatpemerchant")
                        && ! upiName.toLowerCase(Locale.ROOT).contains("bharatpaymerchant")
                        && ! upiName.toLowerCase(Locale.ROOT).contains("bharat pay merchant")
                        && ! upiName.toLowerCase(Locale.ROOT).contains("bharatpay merchant")
                        && ! upiName.toLowerCase(Locale.ROOT).contains("whatsapp merchant")
                        && ! upiName.toLowerCase(Locale.ROOT).contains("whatsappmerchant")
                        && ! upiName.toLowerCase(Locale.ROOT).contains("amazonpaymerchant")
                        && ! upiName.toLowerCase(Locale.ROOT).contains("amazonpay merchant")
                        && ! upiName.toLowerCase(Locale.ROOT).contains("amazon pay merchant")
                        && !upiName.contains("*")
                ) {
                    viewModel.actionType = UpiAddBeneficiaryViewModel.ActionType.REGISTRATION
                    binding.btnProceed.text = viewModel.getButtonText()
                    adapter?.isClickable = false
                    binding.edBeneficiaryName.setText(upiName)
                    binding.edUpiId.setText(upiId)
                    binding.tilBeneficiaryName.show()
                    binding.edUpiId.isEnabled = false
                    binding.btnReset.show()
                }

            } else {
                activity?.showToast("Unable to fetch information please fill data manually!")
            }
        }
    }

    private fun resetVerification() {
        adapter?.isClickable = true
        viewModel.actionType = UpiAddBeneficiaryViewModel.ActionType.VERIFICATION
        binding.btnProceed.text = viewModel.getButtonText()
        binding.edUpiId.setText("")
        binding.edUpiId.isEnabled = true
        binding.edBeneficiaryName.setText("")
        binding.tilBeneficiaryName.hide()
        binding.btnReset.hide()
        viewModel.bankType = "Bank Upi"
    }

    @SuppressLint("SetTextI18n")
    private fun setupTextView() {
        binding.apply {
            tvName.text = viewModel.moneySender?.firstName + " " + viewModel.moneySender?.lastName
            tvMobileNumber.text = "Mobile Number : ${viewModel.moneySender?.mobileNumber}"

            if (viewModel.actionType == UpiAddBeneficiaryViewModel.ActionType.VERIFICATION) "Verify Upi ID" else "Register"
        }
    }


    private fun subscribeObservers() {
        viewModel.verifyAccountObs.observe(viewLifecycleOwner) {
            onVerifyAccountResponse(it)
        }

        viewModel.vpaListResponse.observe(viewLifecycleOwner) {
            onVpaListResponse(it)

        }
        viewModel.verificationChargeObs.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    progressDialog = StatusDialog.progress(requireContext())
                }
                is Resource.Success -> {
                    progressDialog?.dismiss()
                    if (it.data.status == 1) {
                        Dialogs.commonConfirmDialog(requireContext(), "Verification for upi id ${viewModel.upiNumber} ?" + "\n\nIt will charge for rs ${it.data.data?.chargeAmount.toString()}").apply {
                            findViewById<Button>(R.id.btn_confirm).setOnClickListener {
                                dismiss()
                                viewModel.verifyUpiId()
                            }
                        }
                    } else StatusDialog.failure(requireActivity(), it.data.message)
                }
                is Resource.Failure -> {
                    progressDialog?.dismiss()
                    activity?.handleNetworkFailure(it.exception)
                }
            }
        }

        viewModel.registerObs.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Loading -> progressDialog = StatusDialog.progress(requireContext())
                is Resource.Success -> {
                    progressDialog?.dismiss()
                    if (it.data.status == 1) {
                        StatusDialog.success(requireActivity(), it.data.message) {
                            setNavigationResult(true)
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
    }

    private fun onVpaListResponse(it: Resource<VpaBankExtensionResponse>?) {

        when (it) {
            is Resource.Loading -> {
                binding.svContent.hide()
                binding.layoutProgress.show()
            }
            is Resource.Success -> {
                binding.layoutProgress.hide()
                if (it.data.status == 1) {
                    binding.svContent.show()
                    upiBankList = it.data.upiBank
                    setupUpiBank()
                } else {
                    StatusDialog.failure(requireActivity(), it.data.message)
                }
            }
            is Resource.Failure -> {
                binding.layoutProgress.hide()
                activity?.handleNetworkFailure(it.exception)
            }
        }

    }

    private fun setupUpiBank() {
        if (upiBankList == null) return

        binding.recyclerViewUpiBank.layoutManager = GridLayoutManager(requireContext(), 3)
        adapter = VpaBankListAdapter().apply {
            context = requireContext()
            addItems(upiBankList!!)
            binding.recyclerViewUpiBank.adapter = this
        }
        adapter?.onItemClick = { _, item, _ ->

            viewModel.bankType = item.name.orEmpty()
            binding.recyclerViewUpiExtension.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            VpaBankExtensionListAdapter(item.name).apply {
                item.upiextensions?.let {
                    if (it[0].name.orEmpty().isEmpty()) {
                        binding.recyclerViewUpiExtension.hide()
                        binding.tvExtensionHint.show()
                    } else {
                        binding.tvExtensionHint.hide()
                        binding.recyclerViewUpiExtension.show()
                        addItems(it)
                    }
                }
                binding.recyclerViewUpiExtension.adapter = this
            }.onItemClick = { _, item2, _ ->
                onExtensionItemClick(item2.name.orEmpty())

            }
        }


    }


    private fun onExtensionItemClick(it: String) {

        if (viewModel.actionType == UpiAddBeneficiaryViewModel.ActionType.REGISTRATION) {
            return
        }

        val edUpiId = binding.edUpiId
        val tilUpiId = binding.tilUpiId

        val upiNumber = edUpiId.text.toString()

        if (upiNumber.isEmpty()) {
            tilUpiId.error = "Field can't be black"
            tilUpiId.isErrorEnabled = true
            return
        }
        if (upiNumber.contains("@")) {
            val indexOf = upiNumber.indexOf("@")

            val newInput = upiNumber.substring(0, indexOf)
            edUpiId.setText("$newInput@$it")
        } else {
            edUpiId.setText("$upiNumber@$it")
        }

    }

    private fun onVerifyAccountResponse(it: Resource<AccountVerify>) {
        when (it) {
            is Resource.Loading -> {
                progressDialog = StatusDialog.progress(requireActivity(), "Verifying...")
            }
            is Resource.Success -> {
                progressDialog?.dismiss()

                if (it.data.status == 1 || it.data.status == 7001) {

                    val title = if (it.data.status == 1) "Beneficiary Name" else it.data.message

                    StatusDialog.success(requireActivity(), it.data.upiBeneName.orEmpty(), title = title) {
                        viewModel.actionType = UpiAddBeneficiaryViewModel.ActionType.REGISTRATION
                        binding.btnProceed.text = viewModel.getButtonText()
                        binding.tilBeneficiaryName.show()
                        binding.edUpiId.isEnabled = false
                        binding.edBeneficiaryName.setText(it.data.upiBeneName)
                        viewModel.beneficiaryName = it.data.upiBeneName.orEmpty()
                        binding.btnReset.show()
                        adapter?.isClickable = false
                    }
                } else StatusDialog.failure(requireActivity(), it.data.message)
            }
            is Resource.Failure -> {
                progressDialog?.dismiss()
                activity?.handleNetworkFailure(it.exception)
            }
        }
    }

}