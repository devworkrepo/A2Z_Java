package com.a2zsuvidhaa.`in`.activity.matm

import android.app.Activity
import android.app.Dialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import com.a2zsuvidhaa.`in`.R
import com.a2zsuvidhaa.`in`.databinding.ActivityMatmBinding
import com.a2zsuvidhaa.`in`.model.DoubleTxn
import com.a2zsuvidhaa.`in`.util.*
import com.a2zsuvidhaa.`in`.util.dialogs.Dialogs
import com.a2zsuvidhaa.`in`.util.dialogs.StatusDialog
import com.a2zsuvidhaa.`in`.util.ents.*
import com.a2zsuvidhaa.`in`.util.ui.Resource
import com.mosambee.lib.Currency
import com.mosambee.lib.MosCallback
import com.mosambee.lib.ResultData
import com.mosambee.lib.TransactionResult
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import javax.inject.Inject


@AndroidEntryPoint
class MatmActivity : AppCompatActivity(), TransactionResult {

    private lateinit var binding: ActivityMatmBinding
    private var isMpos: Boolean = false
    private val viewModel: MatmViewModel by viewModels()

    private lateinit var mosCallback: MosCallback
    private var progressDialog: Dialog? = null

    private val bluetoothAdapter by lazy {
        (getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
    }

    @Inject
    lateinit var appPreference: com.a2zsuvidhaa.`in`.data.preference.AppPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMatmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = findViewById(R.id.toolbar)

        isMpos = intent.getBooleanExtra("is_mpos", false)

        setupToolbar(toolbar, if (isMpos) "M-POS" else "M-ATM")


        initView()

        initMatm()

        clickHandlers()

        observers()

        if (isMpos) {
            binding.rbBalanceEnquiry.hide()
            binding.rbCashWithdrawal.hide()
            binding.rbMpos.isChecked = true
            viewModel.transactionTypeObs.value = MatmTransactionType.SALE
            binding.btnProceed.text = "Proceed M-POS Transaction"
            binding.tvAmountHint2.hide()
        } else {
            binding.rbMpos.hide()
            binding.btnProceed.text = "Proceed M-ATM Transaction"
            binding.tvAmountHint.text = "Enter amount 100 to 10000"
        }

        ViewUtil.resetErrorOnTextInputLayout(binding.tilAmount, binding.tilCustomerMobile)


    }

    private fun validateAmountDivisibleByTen(value: String): Boolean {

        if (viewModel.transactionTypeObs.value == MatmTransactionType.SALE) {
            return true
        }

        val amount = value.ifEmpty { "0" }.toDouble()
        return amount % 10.0 == 0.0


    }

    private fun initMatm() {

        mosCallback = MosCallback(this)
        mosCallback.setInternalUi(this, false)
        mosCallback.initializeSignatureView(binding.fmLayout, "#1A5C91", "#D81B60")

    }

    private fun initView() {
        ViewUtil.resetErrorOnTextInputLayout(binding.tilAmount, binding.tilCustomerMobile)
        binding.rbCashWithdrawal.isChecked = true
    }

    private fun observers() {


        viewModel.transactionTypeObs.observe(this) {
            if (it == MatmTransactionType.BALANCE_ENQUIRY)
                binding.tilAmount.hide()
            else binding.tilAmount.show()
        }

        viewModel.initiateTransactionObs.observe(this) {
            when (it) {
                is Resource.Loading -> {
                    progressDialog = StatusDialog.progress(this, title = "Initiating...")
                }
                is Resource.Success -> {
                    progressDialog?.dismiss()
                    if (it.data.status == 1) {
                        viewModel.matmInitiate = it.data.data
                        startTransaction()
                    } else StatusDialog.failure(this, it.data.message)
                }
                is Resource.Failure -> {
                    progressDialog?.dismiss()
                    handleNetworkFailure(it.exception)
                }
            }
        }

        viewModel.postDataObs.observe(this) {
            when (it) {
                is Resource.Loading -> {
                    progressDialog = StatusDialog.fullScreenProgress(this)
                }
                is Resource.Success -> {

                    if (it.data.status == 1) {
                        when (it.data.data!!.status) {
                            1, 2 -> {
                                progressDialog?.dismiss()
                                launchIntent(
                                    MatmResponseActivity::class.java, bundleOf(
                                        AppConstants.DATA to it.data.data,
                                        AppConstants.ORIGIN to AppConstants.TRANSACTION_ORIGIN,
                                        AppConstants.ABLE_TO_BACK to (it.data.data.status == 2 || viewModel.shouldBack())
                                    )
                                )
                            }
                            3, 34 -> {
                                viewModel.matmTransactionResponse = it.data.data
                                checkStatus()
                            }
                            else -> {
                                progressDialog?.dismiss()
                                StatusDialog.pending(
                                    this,
                                    "Transaction in pending, please check report",
                                    onComplete = {
                                        goToMainActivity()
                                    })
                            }
                        }
                    } else {
                        progressDialog?.dismiss()
                        StatusDialog.pending(
                            this,
                            "Transaction in pending, please check report",
                            onComplete = {
                                goToMainActivity()
                            })
                    }
                }
                is Resource.Failure -> {
                    progressDialog?.dismiss()
                    appPreference.doubleTxn = DoubleTxn(
                        isException = true,
                        number = viewModel.matmInitiate?.transactionId.toString(),
                        amount = viewModel.amount,
                        type = "matm",
                        time = System.currentTimeMillis() + AppConstants.TRANSACTION_WAITING_TIME
                    )
                    handleNetworkFailure(it.exception)
                }
            }
        }

        viewModel.checkStatusObs.observe(this) {
            when (it) {
                is Resource.Loading -> {}
                is Resource.Success -> {
                    when (it.data.status) {
                        1, 2 -> {
                            progressDialog?.dismiss()
                            launchIntent(
                                MatmResponseActivity::class.java, bundleOf(
                                    AppConstants.DATA to it.data,
                                    AppConstants.ORIGIN to AppConstants.TRANSACTION_ORIGIN,
                                    AppConstants.ABLE_TO_BACK to (it.data.status == 2 || viewModel.shouldBack())
                                )
                            )
                        }
                        11, 3, 34 -> {
                            checkStatus()
                        }
                        else -> {
                            progressDialog?.dismiss()
                            goToMainActivity()
                        }
                    }

                }
                is Resource.Failure -> {
                    progressDialog?.dismiss()
                    appPreference.doubleTxn = DoubleTxn(
                        isException = true,
                        number = viewModel.matmInitiate?.transactionId.toString(),
                        amount = viewModel.amount,
                        type = "matm",
                        time = System.currentTimeMillis() + AppConstants.TRANSACTION_WAITING_TIME
                    )
                    handleNetworkFailure(it.exception)
                }
            }
        }

        viewModel.saleAmountLimitObs.observe(this) {
            when (it) {
                is Resource.Loading -> {
                    progressDialog = StatusDialog.progress(this, "Fetching Amount Limit")
                }
                is Resource.Success -> {
                    progressDialog?.dismiss()
                    viewModel.saleAmountLimit = it.data
                    binding.tvAmountHint.text =
                        "Enter amount ${it.data.minAmount} to ${it.data.maxAmount}"
                    if (it.data.status != 1) {
                        StatusDialog.failure(this, it.data.message) {
                            onBackPressed()
                        }
                    }
                }
                is Resource.Failure -> {
                    progressDialog?.dismiss()
                    handleNetworkFailure(it.exception)
                }
            }
        }
    }

    private fun checkStatus() {

        if (viewModel.checkStatusCount < 3) {
            viewModel.checkStatus()
        } else {
            launchIntent(
                MatmResponseActivity::class.java, bundleOf(
                    AppConstants.DATA to viewModel.matmTransactionResponse,
                    AppConstants.ORIGIN to AppConstants.TRANSACTION_ORIGIN,
                    AppConstants.ABLE_TO_BACK to viewModel.shouldBack()
                )
            )
        }
    }


    private fun clickHandlers() {
        binding.btnProceed.setOnClickListener {
            proceedTransaction()
        }

        binding.rbBalanceEnquiry.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.llAmountHints.hide()
                viewModel.setTransactionType(MatmTransactionType.BALANCE_ENQUIRY)
            }
        }
        binding.rbCashWithdrawal.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.llAmountHints.show()
                viewModel.setTransactionType(MatmTransactionType.CASH_WITHDRAWAL)
            }
        }
        binding.rbMpos.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.setTransactionType(MatmTransactionType.SALE)
                viewModel.fetchSaleAmountLimit()
            }
        }
    }

    private fun proceedTransaction() {
        requestPhoneLocationBluetoothPermissions {
            if (validateFields()) {
                val (isEnableBT, isPairedWithDevice) = isDevicePairedWithMachine()
                if (isEnableBT && isPairedWithDevice) {
                    viewModel.matmInitiate = null
                    viewModel.initiateMatm()
                } else if (!isPairedWithDevice && isEnableBT) {
                    Dialogs.bluetoothNotPaired(this) {
                        startActivity(Intent().setAction(Settings.ACTION_BLUETOOTH_SETTINGS))
                    }
                }
            }
        }
    }


    private fun startTransaction() {

        if (viewModel.matmInitiate == null) {
            StatusDialog.failure(this, "Something went wrong! please contact admin")
            return
        }

        progressDialog = StatusDialog.progress(this, title = "Initiating...")
        mosCallback.initialise(
            viewModel.matmInitiate!!.username,
            viewModel.matmInitiate!!.password,
            this
        )
        mosCallback.initialiseFields(
            viewModel.getTransactionType(),
            //viewModel.mobileNumber,
            "na",
            viewModel.matmInitiate!!.appKey,
            false,
            "",
            "",
            "bt",
            "",
            "0"
        )
        mosCallback.processTransaction(
            viewModel.matmInitiate!!.transactionId,
            viewModel.matmInitiate!!.transactionId,
            viewModel.amount.toDouble(),
            0.0,
            "",
            Currency.INR
        )
    }

    private fun validateFields(): Boolean {

        viewModel.mobileNumber = binding.edCustomerMobile.text.toString()
        viewModel.amount = binding.edAmount.text.toString().ifEmpty { "0" }

        if (viewModel.transactionTypeObs.value == MatmTransactionType.BALANCE_ENQUIRY) {
            viewModel.amount = "0"
        }

        var isValid = true

        if (viewModel.mobileNumber.length != 10) {
            binding.tilCustomerMobile.error = "Enter Customer Mobile Number"
            binding.tilCustomerMobile.isErrorEnabled = true
            isValid = false
        } else {
            binding.tilCustomerMobile.isErrorEnabled = false
        }


        if (viewModel.mobileNumber.length != 10 || !(
                    viewModel.mobileNumber.startsWith("6") ||
                            viewModel.mobileNumber.startsWith("7") ||
                            viewModel.mobileNumber.startsWith("8") ||
                            viewModel.mobileNumber.startsWith("9")
                    )
        ) {
            binding.tilCustomerMobile.error = "Enter valid Customer Mobile"
            binding.tilCustomerMobile.isErrorEnabled = true
            isValid = false
        } else {
            binding.tilCustomerMobile.isErrorEnabled = false
        }




        if (viewModel.transactionTypeObs.value == MatmTransactionType.SALE) {
            try {
                if ((viewModel.amount.toDouble() !in viewModel.saleAmountLimit!!.minAmount.toString()
                        .toDouble()..viewModel.saleAmountLimit!!.maxAmount.toString()
                        .toDouble()) || !validateAmountDivisibleByTen(viewModel.amount)
                ) {

                    binding.tilAmount.error =
                        "Enter Amount ${viewModel.saleAmountLimit!!.minAmount} to ${viewModel.saleAmountLimit!!.maxAmount} Rs"


                    if (!validateAmountDivisibleByTen(viewModel.amount)) {

                        binding.tilAmount.error = "Enter amount multiple of 10"
                    }
                    binding.tilAmount.isErrorEnabled = true

                    isValid = false
                } else {

                    binding.tilAmount.isErrorEnabled = false
                }
            } catch (e: Exception) {
                StatusDialog.failure(this, e.message.toString()) {
                    onBackPressed()
                }
            }

        } else {
            if ((viewModel.amount.toDouble() !in 100.0..10000.0 && viewModel.transactionTypeObs.value == MatmTransactionType.CASH_WITHDRAWAL)
                || !validateAmountDivisibleByTen(viewModel.amount)
            ) {

                binding.tilAmount.error = "Enter Amount 100 to 10000 Rs"


                if (!validateAmountDivisibleByTen(viewModel.amount)) {
                    binding.tilAmount.error = "Enter amount multiple of 10"

                }
                binding.tilAmount.isErrorEnabled = true
                isValid = false
            } else {

                binding.tilAmount.isErrorEnabled = false
            }
        }



        return isValid


    }

    override fun onResult(data: ResultData?) {
        progressDialog?.dismiss()
        if (data != null) {
            if (data.result) viewModel.postResultData(data.transactionData)
            else {
                val reasonMessage = data.reason.orEmpty()
                val mData = """ {
                         "orderId" : "${viewModel.matmInitiate!!.transactionId}",
                         "transactionStatus" : "$reasonMessage",
                         "statusCode" : "5500",
                         "errorCode" : "${data.reasonCode}",
                         "result": "failure"} """.trimIndent()
                viewModel.postResultData(JSONObject(mData).toString())
            }

            /*   val mData = """ {
                           "result" : "${data.result.toString()}",
                           "reason" : "${data.reason.toString()}",
                           "resultCode" : "${data.reasonCode.toString()}",
                           "amount" : "${data.amount.toString()}",
                           "transactionId" : "${data.transactionId.toString()}",
                           "transactionData" : "${data.transactionData.toString()}",

                           } """
                   .trimIndent()
               viewModel.postResultData(mData)*/

        } else {
            val mData = """ {
                        "orderId" : "${viewModel.matmInitiate!!.transactionId}",
                        "statusCode" : "5501"} """.trimIndent()
            viewModel.postResultData(mData)
        }

        AppLog.d("Result data :result - ${data?.result}")
        AppLog.d("Result data :amount - ${data?.amount}")
        AppLog.d("Result data :transaction Id - ${data?.transactionId}")
        AppLog.d("Result data :transaction data - ${data?.transactionData}")
        AppLog.d("Result data :reason - ${data?.reason}")
        AppLog.d("Result data :reason code - ${data?.reasonCode}")

    }

    override fun onCommand(progressData: String?) {
        progressDialog?.findViewById<TextView>(R.id.tv_title)?.text = progressData
        AppLog.d("ProgressData : $progressData")
    }


    private fun isDevicePairedWithMachine(): Pair<Boolean, Boolean> {

        val boundedDevices = bluetoothAdapter.bondedDevices

        return if (!bluetoothAdapter.isEnabled) {
            val intentBtEnabled = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(intentBtEnabled, 101)
            Pair(false, false)
        } else {
            boundedDevices.forEach {
                AppUtil.logger("bluetoothDevice : ${it.name}")
                if (it.name.startsWith("QPOS")) {
                    return Pair(true, true)
                }
            }
            Pair(true, false)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 101) {
                proceedTransaction()
            }
        }
    }
}