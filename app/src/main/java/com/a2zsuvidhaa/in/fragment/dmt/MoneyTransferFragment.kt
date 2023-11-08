package com.a2zsuvidhaa.`in`.fragment.dmt

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.annotation.ColorInt
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.a2zsuvidhaa.`in`.R
import com.a2zsuvidhaa.`in`.activity.DmtTransactionResponseActivity
import com.a2zsuvidhaa.`in`.activity.UpiTransactionResponseActivity
import com.a2zsuvidhaa.`in`.adapter.dmt.DmtCommissionAdapter
import com.a2zsuvidhaa.`in`.data.model.dmt.BankDownCheck
import com.a2zsuvidhaa.`in`.data.model.dmt.DmtCommission
import com.a2zsuvidhaa.`in`.data.preference.AppPreference
import com.a2zsuvidhaa.`in`.databinding.FragmentMoneyTransferBinding
import com.a2zsuvidhaa.`in`.fragment.BaseFragment
import com.a2zsuvidhaa.`in`.util.*
import com.a2zsuvidhaa.`in`.util.dialogs.ConfirmDialog
import com.a2zsuvidhaa.`in`.util.dialogs.StatusDialog
import com.a2zsuvidhaa.`in`.util.ents.*
import com.a2zsuvidhaa.`in`.util.enums.DmtType
import com.a2zsuvidhaa.`in`.util.ui.Resource
import com.a2zsuvidhaa.`in`.viewmodel.AppViewModel
import com.a2zsuvidhaa.`in`.viewmodel.dmt.MoneyTransferViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MoneyTransferFragment :
    BaseFragment<FragmentMoneyTransferBinding>(R.layout.fragment_money_transfer) {

    private val args: MoneyTransferFragmentArgs by navArgs()
    private val viewModel: MoneyTransferViewModel by viewModels()
    private val appViewModel: AppViewModel by viewModels()

    @Inject
    lateinit var appPreference: AppPreference


    @Inject
    lateinit var appPreferenceOld: com.a2zsuvidhaa.`in`.AppPreference

    private val mLocationManager: LocationManager by lazy {
        activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        viewModel.moneySender = args.sender
        viewModel.beneficiary = args.beneficiary
        viewModel.dmtType = args.dmtType

        val title = if (args.dmtType == DmtType.UPI) "UPI Transfer" else "Money Transfer"
        setupToolbar(
            binding.toolbar.toolbar,
            title,
            subtitle = DmtUtil.getDmtServiceName(viewModel.dmtType)
        )


        if (viewModel.dmtType == DmtType.UPI) {
            viewModel.upiBankDownCheck()
        } else {
            viewModel.bankDownCheck()
        }

        setupAmount()

        binding.edAmount.afterTextChange {
            viewModel.isTransactionListVisible = false
            viewModel.isChargeFetched = false
            setupTransactionListVisibility()
        }
        binding.edPin.afterTextChange {
            viewModel.isTransactionListVisible = false
            viewModel.isChargeFetched = false
            setupTransactionListVisibility()
        }


        binding.btnTransfer.setOnClickListener {

            if (!validateInput()) return@setOnClickListener

            fetchLocation(onStart = {
                progressDialog = StatusDialog.progress(requireContext(), "Validating Location")
            }) {
                progressDialog?.dismiss()

                if (AppUtil.canMakeTransaction(
                        context = requireActivity(),
                        doubleTxn = appPreference.doubleTxn,
                        number = viewModel.beneficiary.accountNumber.orEmpty(),
                        amount = viewModel.amount,
                        type = "money"
                    )
                ) confirmTransaction()
            }


        }
        setupViews()
        subscribeObservers()

        setupTransactionListVisibility()

        binding.llShowHideTransactionList.setOnClickListener {

            if (viewModel.isTransactionListVisible) {
                viewModel.isTransactionListVisible = !viewModel.isTransactionListVisible
                setupTransactionListVisibility()
            } else {
                if (!validateInput()) return@setOnClickListener

                if (!viewModel.isChargeFetched)
                    viewModel.fetchTransactionCharge()
                else {
                    viewModel.isTransactionListVisible = !viewModel.isTransactionListVisible
                    setupTransactionListVisibility()
                }
            }
        }

        binding.tvAmountText.text = "Wallet available balance: ₹" + appPreferenceOld.userBalance

        onTextChangeListeners()

        if (viewModel.beneficiary.bankVerified == 1 || viewModel.beneficiary.upiBankVerified == 1) {
            binding.tvBeneficiaryName.setupTextColor(R.color.green)
            binding.ivVerifiedCheck.show()
        } else {
            binding.tvBeneficiaryName.setupTextColor(R.color.yellow_dark)
            binding.ivVerifiedCheck.hide()
        }

        binding.rbImps.isChecked = true

        binding.rbImps.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) viewModel.channel = "2"
        }
        binding.rbNfts.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) viewModel.channel = "1"
        }

        setAmountHintText("Enter amount", color = R.color.grey)

        fetchLocation()

        ViewUtil.resetErrorOnTextInputLayout(binding.tilAmount, binding.tilPin)
        makeBlink()

        binding.llRefresh.setOnClickListener {
            appViewModel.fetchBalanceInfo()
        }


    }


    private fun onTextChangeListeners() {
        binding.edAmount.afterTextChange {
            onAmountChange()
        }
    }

    private fun confirmTransaction() {
        ConfirmDialog.dmtTransactionConfirmation(
            context = requireActivity(),
            dmtType = viewModel.dmtType,
            beneficiaryName = viewModel.beneficiary.name.orEmpty(),
            accountNumber = viewModel.beneficiary.accountNumber.orEmpty(),
            bankName = viewModel.beneficiary.bankName.orEmpty(),
            amount = viewModel.amount,
            ifsc = viewModel.beneficiary.ifsc.orEmpty(),
            onConfirm = {
                if (viewModel.dmtType == DmtType.UPI) {
                    viewModel.upiTransfer()
                } else {
                    viewModel.dmtTransfer()
                }
            })
    }


    private fun setupTransactionListVisibility() {

        if (viewModel.isTransactionListVisible) {
            binding.apply {
                ivShowHide.setImageResource(R.drawable.v_arrow_up)
                llCharge.show()
                tvShowHide.text = "Hide"
            }
        } else {
            binding.apply {
                ivShowHide.setImageResource(R.drawable.v_arrow_down)
                llCharge.hide()
                tvShowHide.text = "Show"
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupViews() {

        binding.tvBankNotVerified.let {
            if (viewModel.beneficiary.bankVerified == 1 || viewModel.beneficiary.upiBankVerified == 1) {
                it.hide()
            } else it.show()
        }

        binding.apply {
            tvBeneficiaryName.text = viewModel.beneficiary.name
            tvAccountNumber.text = viewModel.beneficiary.accountNumber
            tvBankName.text = viewModel.beneficiary.bankName

            tvIfscCode.text = viewModel.beneficiary.ifsc

            if (viewModel.dmtType == DmtType.UPI) {
                llIfscCode.hide()
                cvTransactionType.hide()
                tvBankNameTitle.text = "Provider"
                tvAccountNumberTitle.text = "Upi ID"
            }


        }
    }

    private fun setupAmount() {
        binding.edAmount.setText(viewModel.amount)
        binding.edAmount.afterTextChange {

            CashUtil.amountInWordSetupTextView(
                strAmount = it.toString(),
                tvAmountInWord = binding.tvAmountInWord,
                walletBalance = appPreferenceOld.userBalance
            )
        }
    }

    private fun subscribeObservers() {
        viewModel.transferObs.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    progressDialog = StatusDialog.fullScreenProgress(requireActivity())
                }
                is Resource.Success -> {
                    progressDialog?.dismiss()

                    if (it.data.status == 1) {
                        if (it.data.data?.status == 1 ||
                            it.data.data?.status == 2 ||
                            it.data.data?.status == 3 ||
                            it.data.data?.status == 34 ||
                            it.data.data?.status == 37
                        ) {

                            when (viewModel.dmtType) {

                                DmtType.UPI -> {
                                    activity?.launchIntent(
                                        UpiTransactionResponseActivity::class.java, bundleOf(
                                            AppConstants.DATA_OBJECT to it.data.data,
                                            AppConstants.ORIGIN to AppConstants.TRANSACTION_ORIGIN
                                        )
                                    )
                                }
                                else -> {
                                    activity?.launchIntent(
                                        DmtTransactionResponseActivity::class.java, bundleOf(
                                            AppConstants.DATA_OBJECT to it.data.data,
                                            AppConstants.ORIGIN to AppConstants.TRANSACTION_ORIGIN,

                                            )
                                    )
                                }


                            }

                        } else {
                            StatusDialog.failure(requireActivity(), it.data.message.orEmpty())
                        }

                    } else StatusDialog.failure(requireActivity(), it.data.message.orEmpty())
                }
                is Resource.Failure -> {
                    progressDialog?.dismiss()
                    activity?.handleNetworkFailure(it.exception)
                }
            }
        }

        viewModel.dmtCommissionResponseObs.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Loading -> {
                    progressDialog =
                        StatusDialog.progress(requireContext(), title = "Fetching Charge...")
                }
                is Resource.Success -> {
                    progressDialog?.dismiss()
                    if (it.data.status == 1) {
                        viewModel.isChargeFetched = true
                        viewModel.isTransactionListVisible = true
                        setupTransactionListVisibility()

                        setupChargeList(it.data.commissions!!)
                        binding.cvTransactionCharges.show()
                        binding.root.showSnackbar("Charge fetched successfully")
                    } else StatusDialog.failure(requireActivity(), it.data.message)
                }
                is Resource.Failure -> {
                    progressDialog?.dismiss()
                    activity?.handleNetworkFailure(it.exception)
                }
            }
        })

        viewModel.bankDownCheckCheckObs.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    binding.progress.show()
                    binding.svContent.hide()
                }
                is Resource.Success -> {
                    binding.progress.hide()
                    if (it.data.status == 1) {
                        binding.svContent.show()
                        if (viewModel.dmtType == DmtType.UPI) {
                            it.data.bankDownCheck?.let { it1 -> setupUpiBankDown(it1) }
                        } else {
                            it.data.bankDownCheck?.let { it1 -> setupDmtBankDown(it1) }
                        }
                    } else if (it.data.status == 404 && viewModel.beneficiary.bankName == "BANK UPI") {
                        binding.svContent.show()
                    } else {
                        StatusDialog.failure(requireActivity(), it.data.message) {
                            activity?.onBackPressed()
                        }
                    }
                }
                is Resource.Failure -> {
                    binding.progress.hide()
                    binding.btnTransfer.hide()
                    activity?.handleNetworkFailure(it.exception)
                }
            }
        }

        appViewModel.balanceObs.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Loading -> {
                    progressDialog = StatusDialog.progress(requireContext())
                }
                is Resource.Success -> {

                    progressDialog?.dismiss()
                    appPreferenceOld.userBalance = it.data.balance.userBalance
                    binding.tvAmountText.text =
                        "Wallet available balance: ₹" + appPreferenceOld.userBalance

                }
                is Resource.Failure -> {
                    progressDialog?.dismiss()
                    activity?.handleNetworkFailure(it.exception)
                }
            }
        })

    }


    private fun setupDmtBankDown(it: BankDownCheck) {
        if (it.isTxnAllowed == true) {

            if (it.isImpsAllowed == true) {
                binding.rbImps.isChecked = true
                viewModel.channel = "2"
            } else {
                binding.rbNfts.isChecked = true
                binding.rbImps.isEnabled = false
                viewModel.channel = "1"
                binding.tvMessage.show()
                binding.tvMessage.text = it.message
            }
        } else {
            StatusDialog.failure(requireActivity(), it.message.toString()) {
                binding.rbImps.isEnabled = false
                binding.rbNfts.isEnabled = false
                binding.tvMessage.show()
                binding.tvMessage.text = it.message
                binding.btnTransfer.hide()
            }
        }
    }


    private fun setupUpiBankDown(it: BankDownCheck) {
        if (it.isTxnAllowed == false) {
            StatusDialog.failure(requireActivity(), it.message.toString()) {
                binding.tvMessage.show()
                binding.tvMessage.text = it.message
                binding.btnTransfer.hide()
            }
        }
    }

    private fun setupChargeList(listCharge: List<DmtCommission>) {

        if (viewModel.dmtType == DmtType.WALLET_ONE) {
            binding.tvSerialNumber.hide()
        }

        val adapter = DmtCommissionAdapter(viewModel.dmtType).apply {
            addItems(listCharge)
            context = requireActivity()
        }
        binding.recyclerView.setup().adapter = adapter
    }


    private fun onAmountChange() {
        viewModel.amount = binding.edAmount.text.toString()
        if (viewModel.amount.isEmpty()) viewModel.amount = "0"
        val doubleAmount = viewModel.amount.toInt()

        val minAmount = 1

        when (doubleAmount) {
            0 -> {
                setAmountHintText("Enter Amount", R.color.grey)
            }
            !in minAmount..senderMonthLimit() -> {
                setAmountHintText("Remitter limit crossed : available limit : Rs. ${senderMonthLimit()}")
                viewModel.isAmountValidate = false
            }
            else -> {
                when {
                    appPreferenceOld.userBalance.toDouble() < doubleAmount -> {
                        setAmountHintText("Insufficient wallet balance : Rs. ${appPreferenceOld.userBalance}")
                        viewModel.isAmountValidate = false
                    }
                    else -> {
                        binding.tvAmountInWord.apply {
                            text = CashUtil.doubleConvert(doubleAmount.toDouble())
                            setupTextColor(R.color.green)
                        }
                        viewModel.isAmountValidate = true
                    }
                }
            }
        }
    }

    private fun validateInput(): Boolean {

        var isValid = true
        viewModel.amount = binding.edAmount.text.toString()
        viewModel.txnPin = binding.edPin.text.toString()


        onAmountChange()

        if (viewModel.isAmountValidate.not()) {
            isValid = false
            activity?.hideKeyboard()

        }


        if (viewModel.txnPin.length !in 4..6) {
            binding.tilPin.error = "Enter 4 to 6 digit M-PIN"
            binding.tilPin.isErrorEnabled = true
            isValid = false

            if (viewModel.isAmountValidate) {
                binding.edPin.makeFocus()
            }

        } else {
            binding.tilPin.isErrorEnabled = false
        }
        return isValid
    }

    private fun senderMonthLimit() = (viewModel.moneySender?.remBal?.makeInt() ?: 0)


    private fun setAmountHintText(message: String, @ColorInt color: Int = R.color.red) {
        binding.tvAmountInWord.apply {
            setupTextColor(color)
            text = message
        }
    }

    private fun fetchLocation(onStart: () -> Unit = {}, onFetch: (Location?) -> Unit = {}) {
        if (viewModel.location != null) {
            onFetch(viewModel.location)
        } else {
            activity?.requestForLocationPhoneReadState {
                val isLocationServiceEnable = LocationService.isLocationEnabled(requireContext())
                if (isLocationServiceEnable) {
                    onStart()
                    LocationService.getCurrentLocation(mLocationManager)
                    LocationService.setupListener(object : LocationService.MLocationListener {
                        override fun onLocationChange(location: Location) {
                            if (viewModel.location == null) {
                                viewModel.location = location
                                onFetch(location)
                            }

                        }
                    })
                }
            }
        }
    }

    private fun makeBlink() {

        val text =
            if (viewModel.dmtType == DmtType.UPI) "Selected UPI ID is not verified!" else "Selected account is not verified!"
        binding.tvBankNotVerified.text = text
        binding.tvBankNotVerified.blink()
    }

}