package com.a2zsuvidhaa.`in`.fragment.dmt

import `in`.galaxyofandroid.spinerdialog.SpinnerDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.a2zsuvidhaa.`in`.R
import com.a2zsuvidhaa.`in`.data.model.dmt.AccountVerify
import com.a2zsuvidhaa.`in`.data.model.dmt.Bank
import com.a2zsuvidhaa.`in`.data.model.dmt.BankListResponse
import com.a2zsuvidhaa.`in`.data.response.CommonResponse
import com.a2zsuvidhaa.`in`.databinding.FragmentAddBeneficiaryBinding
import com.a2zsuvidhaa.`in`.fragment.BaseFragment
import com.a2zsuvidhaa.`in`.util.ViewUtil
import com.a2zsuvidhaa.`in`.util.dialogs.Dialogs
import com.a2zsuvidhaa.`in`.util.dialogs.StatusDialog
import com.a2zsuvidhaa.`in`.util.ents.*
import com.a2zsuvidhaa.`in`.util.ui.Resource
import com.a2zsuvidhaa.`in`.viewmodel.dmt.AddBeneficiaryViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class AddBeneficiaryFragment :
    BaseFragment<FragmentAddBeneficiaryBinding>(R.layout.fragment_add_beneficiary) {


    private val viewModel: AddBeneficiaryViewModel by viewModels()
    private val args by navArgs<AddBeneficiaryFragmentArgs>()

    private var bankSpinnerDialog: SpinnerDialog? = null




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.moneySender = args.sender
        viewModel.dmtType = args.dmtType
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        setupToolbar(binding.toolbar.toolbar, "Add Beneficiary", subtitle = DmtUtil.getDmtServiceName(viewModel.dmtType))

        binding.edBankName.setOnClickListener {
            bankSpinnerDialog?.showSpinerDialog()
        }

        binding.btnAddBeneficiary.setOnClickListener {
            if (!validateInputs()) return@setOnClickListener
            viewModel.addBeneficiary()
        }

        viewModel.fetchBankList()

        binding.btnVerify.setOnClickListener {
            if(!validateVerifyAccount()) return@setOnClickListener

            Dialogs.commonConfirmDialog(
                    requireContext(),
                    "Confirm account verification").apply {

                findViewById<Button>(R.id.btn_confirm).setOnClickListener {
                    dismiss()
                    viewModel.verifyAccount()
                }
            }


        }


        ViewUtil.resetErrorOnTextInputLayout(
            binding.tilBeneficiaryName,
            binding.tilBankName,
            binding.tilAccountNumber,
            binding.tilIfscCode
        )

        binding.tvName.text = viewModel.moneySender?.firstName + " "+viewModel.moneySender?.lastName
        binding.tvMobileNumber.text = viewModel.moneySender?.mobileNumber

        subscribeObservers()

        binding.edAccountNumber.afterTextChange {
            val number = binding.edAccountNumber.text.toString()
            binding.tvEnteredAccount.show()
            binding.tvEnteredAccount.text = "Length : ${number.length}"
            if(viewModel.accountDigit != 0){
                binding.tvSuggestedAccount.show()
                binding.tvSuggestedAccount.text = "A/C Suggested Length : ${viewModel.accountDigit}"
            }
            if(number.length >=8){
                binding.btnVerify.show()
            }
            else binding.btnVerify.hide()
        }
    }



    private fun subscribeObservers() {
        viewModel.bankListObs.observe(viewLifecycleOwner, Observer {
            onBankListResponse(it)
        })

        viewModel.addBeneficiaryObs.observe(viewLifecycleOwner) { onAddBeneficiaryResponse(it) }

        viewModel.verifyAccountObs.observe(viewLifecycleOwner) {
            onVerifyAccountResponse(it)
        }
    }

    private fun onVerifyAccountResponse(it: Resource<AccountVerify>) {
        when(it){
            is Resource.Loading ->{
                progressDialog = StatusDialog.progress(requireActivity(),"Verifying...")
            }
            is Resource.Success ->{
                progressDialog?.dismiss()

                if(it.data.status == 1){
                    StatusDialog.success(requireActivity(),it.data.beneName.orEmpty(),title = "Beneficiary Name"){
                        binding.edBeneficiaryName.setText(it.data.beneName)
                    }
                }else StatusDialog.failure(requireActivity(),it.data.message)
            }
            is Resource.Failure->{
                progressDialog?.dismiss()
                activity?.handleNetworkFailure(it.exception)
            }
        }
    }

    private fun onAddBeneficiaryResponse(it: Resource<CommonResponse>) {
        when(it){
            is Resource.Loading ->{progressDialog = StatusDialog.progress(requireActivity(),"Adding")}
            is Resource.Success ->{
                progressDialog?.dismiss()
                if(it.data.status == 500 || it.data.status == 5514 ) {
                    StatusDialog.success(requireActivity(),it.data.message){
                        setNavigationResult(true)
                        activity?.onBackPressed()
                    }
                } else StatusDialog.failure(requireActivity(),it.data.message)
            }
            is Resource.Failure ->{
                progressDialog?.dismiss()
                activity?.handleNetworkFailure(e = it.exception)
            }
        }
    }

    private fun onBankListResponse(it: Resource<BankListResponse>) {
        when (it) {
            is Resource.Loading -> {
                binding.run {
                    scrollView.hide()
                    progress.show()
                }
            }
            is Resource.Success -> {
                binding.run {
                    scrollView.show()
                    progress.hide()
                }
                if(it.data.status == 1){
                    setupBankSpinnerDialog(it.data.data!!)
                }
                else StatusDialog.failure(requireActivity(),it.data.message){
                    activity?.onBackPressed()
                }
            }
            is Resource.Failure -> {
                binding.run {
                    scrollView.show()
                    progress.hide()
                }
                activity?.handleNetworkFailure(it.exception, shouldBack = true)
            }
        }
    }

    private fun setupBankSpinnerDialog(data: List<Bank>) {

        val bankNameList = data.map { it.bankName }

        bankSpinnerDialog =
            SpinnerDialog(requireActivity(), bankNameList as ArrayList<String>, "Select Bank")
        bankSpinnerDialog?.bindOnSpinerListener { item, _ ->

            data.find { it.bankName == item }?.let {
                viewModel.bankId = it.id.toString()
                binding.edBankName.setText(it.bankName)
                binding.edIfscCode.setText(it.ifsc)
                viewModel.accountDigit = it.accountDigit ?: 0

            }
        }


    }

    private fun validateVerifyAccount(): Boolean {
        var isValid = true

        if (viewModel.bankName.isEmpty()) {
            binding.tilBankName.apply {
                error = "Select bank name "
                isErrorEnabled = true
            }
            isValid = false
        } else binding.tilBankName.isErrorEnabled = false

        if (viewModel.ifscCode.length < 11) {
            binding.tilIfscCode.apply {
                error = "Enter valid 11 digits ifsc Code"
                isErrorEnabled = true
            }
            isValid = false
        } else binding.tilIfscCode.isErrorEnabled = false


        if (viewModel.accountNumber.length < 8) {
            binding.tilAccountNumber.apply {
                error = "Enter minimum 8 digits account number"
                isErrorEnabled = true
            }
            isValid = false
        } else binding.tilAccountNumber.isErrorEnabled = false


        return isValid
    }

    private fun validateInputs(): Boolean {
        var isValid = true




        if (viewModel.bankName.isEmpty()) {
            binding.tilBankName.apply {
                error = "Select bank name "
                isErrorEnabled = true
            }
            isValid = false
        } else binding.tilBankName.isErrorEnabled = false

        if (viewModel.ifscCode.length < 11) {
            binding.tilIfscCode.apply {
                error = "Enter valid 11 digits ifsc Code"
                isErrorEnabled = true
            }
            isValid = false
        } else binding.tilIfscCode.isErrorEnabled = false


        if (viewModel.accountNumber.length < 8) {
            binding.tilAccountNumber.apply {
                error = "Enter minimum 8 digits acccount number"
                isErrorEnabled = true
            }
            isValid = false
        } else binding.tilAccountNumber.isErrorEnabled = false

        if (viewModel.beneficiaryName.length < 3) {
            binding.tilBeneficiaryName.apply {
                error = "Enter minimum 3 character name"
                isErrorEnabled = true
            }
            isValid = false
        } else binding.tilBeneficiaryName.isErrorEnabled = false

        return isValid
    }

}