package com.a2zsuvidhaa.`in`.activity

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.a2zsuvidhaa.`in`.R
import com.a2zsuvidhaa.`in`.activity.aeps2.Aeps2ViewModel
import com.a2zsuvidhaa.`in`.adapter.MiniStatementAdapter
import com.a2zsuvidhaa.`in`.data.model.MiniStatement
import com.a2zsuvidhaa.`in`.data.model.TransactionDetail
import com.a2zsuvidhaa.`in`.databinding.ActivityAadharTransactionResponseBinding
import com.a2zsuvidhaa.`in`.util.AppConstants
import com.a2zsuvidhaa.`in`.util.AppLog
import com.a2zsuvidhaa.`in`.util.PermissionHandler2
import com.a2zsuvidhaa.`in`.util.ents.*
import com.a2zsuvidhaa.`in`.util.file.StorageHelper
import com.a2zsuvidhaa.`in`.util.pdf.PdfHelper
import org.json.JSONArray
import org.json.JSONObject

class AadharTransactionResponseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAadharTransactionResponseBinding

    private lateinit var origin: String
    private lateinit var recordId: String
    private var status: Int = 0
    private var isAeps = false

    private var goBack = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAadharTransactionResponseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        origin = intent.getStringExtra(AppConstants.ORIGIN)!!
        if (origin == AppConstants.TRANSACTION_ORIGIN)
            setupDataFromTransaction()
        else if (origin == AppConstants.REPORT_ORIGIN) {
            setupDataFromReport()
        }

    }

    private fun setupDataFromTransaction() = binding.apply {


        isAeps = true

        val jsonStringData = intent.getStringExtra(AppConstants.DATA)!!
        val jsonObject = JSONObject(jsonStringData)

        val appTransactionType = intent.getSerializableExtra(AppConstants.TRANSACTION_TYPE) as Aeps2ViewModel.TransactionType

        val serviceName = jsonObject.optString("service_name")
        status = jsonObject.getInt("status")
        recordId = jsonObject.optString("record_id")

        val statusDescription = jsonObject.optString("status_desc")
        val message = jsonObject.optString("message")
        val transactionId = jsonObject.optString("txn_id")

        val orderId = jsonObject.optString("order_id")
        val transactionType = jsonObject.optString("transaction_type")
        val aadhaarNumber = jsonObject.optString("aadhaar_number")
        val availableBalance = jsonObject.optString("available_amount")
        val transactionAmount = jsonObject.optString("transaction_amount")
        val bankRnn = jsonObject.optString("bank_ref")
        val transactionTime = jsonObject.optString("txn_time")
        val bankName = jsonObject.optString("bank_name")
        val mobileNumber = jsonObject.optString("customer_number")
        val shopName = jsonObject.optString("shop_name")
        val contactNumber = jsonObject.optString("retailer_number")

        tvStatus.text = statusDescription
        tvTxnTime.text = transactionTime
        tvTransactionType.text = transactionType
        tvAmount.text = transactionAmount
        tvServiceName.text = serviceName
        tvAvailBalance.text = availableBalance
        tvPayId.text = orderId
        if (orderId == "null" || orderId.isEmpty())
            tvPayId.text = transactionId
        tvOperatorRef.text = bankRnn
        tvAadharNumber.text = aadhaarNumber
        tvBankName.text = bankName
        tvMobileNumber.text = mobileNumber
        tvContactNumber.text = contactNumber
        tvShopName.text = shopName

        if(bankRnn.isEmpty()) llBankRef.hide()

        tvMessage.apply {
            text = message
            if (message.isEmpty() || message == "null") hide()
        }

        setupStatus()
        setupBasic(transactionType)
        if (appTransactionType == Aeps2ViewModel.TransactionType.MINI_STATEMENT) {
            val dataObject = JSONObject(jsonStringData)
            val statements: JSONArray = dataObject.getJSONArray("statement")
            if (statements.length() > 0) {
                llMiniStatement.show()
                val miniStatementList = arrayListOf<MiniStatement>()
                for (i in 0 until statements.length()) {
                    val statement: JSONObject = statements.getJSONObject(i)
                    val model = MiniStatement(
                            txnTime = statement.optString("date"),
                            narration = statement.optString("narration"),
                            txnType = statement.optString("txnType"),
                            amount = statement.optString("amount"),
                    )
                    miniStatementList.add(model)
                }

                recyclerView.setup(false).adapter = MiniStatementAdapter().apply {
                    addItems(miniStatementList)
                    context = this@AadharTransactionResponseActivity
                }
            }
        }

        if (appTransactionType == Aeps2ViewModel.TransactionType.AADHAAR_PAY
                || appTransactionType == Aeps2ViewModel.TransactionType.CASH_WITHDRAWAL) {
            goBack = status == 2

        }
    }

    override fun onBackPressed() {
        if(goBack){
            super.onBackPressed()
        }
        else goToMainActivity()
    }

    private fun setupDataFromReport() = binding.apply {

        val transactionDetail: TransactionDetail? = intent.getParcelableExtra(AppConstants.DATA_OBJECT)
        val from = intent.getStringExtra(AppConstants.FROM_REPORT)

        isAeps = from == AppConstants.AEPS_REPORT

        transactionDetail?.let {
            status = it.status!!
            recordId = it.reportId.orEmpty()


            tvStatus.text = it.statusDesc
            tvTxnTime.text = it.txnTime
            tvTransactionType.text = it.txnType
            tvAmount.text = it.amount
            tvServiceName.text = it.serviceName
            tvAvailBalance.text = it.availableBalance
            tvPayId.text = it.reportId
            tvOperatorRef.text = it.bankRef
            tvAadharNumber.text = it.number
            tvBankName.text = it.bankName
            tvMobileNumber.text = it.senderNumber
            tvContactNumber.text = it.outletNumber
            tvShopName.text = it.outletName
            tvMessage.text = it.message

            if(it.bankRef.orEmpty().isEmpty()) llBankRef.hide()

            setupStatus()
            setupBasic(it.txnType.orEmpty())
            if (it.txnType == "AEPS(MINI_STATEMENT)") {


                val adapter = MiniStatementAdapter().apply {

                    it.miniStatement?.let {it1->
                        addItems(it1)
                    } ?: AppLog.d("aadhaartesting : data not avaialbe")
                    context = this@AadharTransactionResponseActivity
                }
                recyclerView.setup(false).adapter = adapter

                if(adapter.itemCount > 0) llMiniStatement.show() else llMiniStatement.hide()
            }
        }
    }

    private fun setupStatus() {
        binding.let {
            when (status) {
                1, 24 -> {
                    it.ivImage.setImageResource(R.drawable.icon_sucess2)
                    it.ivImage.setColorFilter(ContextCompat.getColor(this, R.color.success))
                    it.tvStatus.setupTextColor(R.color.success)
                }
                2 -> {
                    it.ivImage.setImageResource(R.drawable.icon_failed)
                    it.ivImage.setColorFilter(ContextCompat.getColor(this, R.color.red))
                    it.tvStatus.setupTextColor(R.color.red)
                }
                3 -> {
                    it.ivImage.setImageResource(R.drawable.pending)
                    it.ivImage.setColorFilter(ContextCompat.getColor(this, R.color.yellow_dark))
                    it.tvStatus.setupTextColor(R.color.yellow_dark)
                }
                else -> {
                    it.ivImage.setImageResource(R.drawable.icon_sucess2)
                    it.ivImage.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary))
                    it.tvStatus.setupTextColor(R.color.colorPrimary)
                }
            }
        }

    }

    private fun setupBasic(transactionType: String) {
        binding.let {

            if (transactionType == "BALANCE_ENQUIRY"
                    || transactionType == "MINI_STATEMENT"
                    || transactionType == "BALANCE_INQUIRY"
                    || transactionType == "AEPS(MINI_STATEMENT)"
            ) it.tvAmount.hide()
            it.btnShare.setOnClickListener { onShareButtonClick(false) }
            it.btnShareWhatsapp.setOnClickListener { onShareButtonClick(true) }
            it.llDownloadReceipt.setOnClickListener { onDownloadReceiptButtonClick() }

            it.btnClose.setOnClickListener {
                onBackPressed()
            }
        }
    }


    private fun onShareButtonClick(isWhatsAppOnly: Boolean = false) {
        PermissionHandler2.checkStoragePermission(this) { isGranted ->
            if (!isGranted) return@checkStoragePermission

            binding.viewSpace.hide()
            binding.llDownloadReceipt.hide()
            binding.scrollView.setBackgroundColor(ContextCompat.getColor(this, R.color.black))

            val bitmap: Bitmap? = StorageHelper.getBitmapFromView(scrollView = binding.scrollView,
                    onAfterConvert = {
                        binding.scrollView.setBackgroundColor(
                                ContextCompat.getColor(
                                        this,
                                        R.color.white2
                                )
                        )
                        binding.viewSpace.show()
                        if (status != 2) binding.llDownloadReceipt.show()
                    })

            val imageFileUri: Uri = StorageHelper.saveImageToCacheDirectory(
                    context = this,
                    bitmap = bitmap,
                    fileName = "transaction_receipt.jpg"
            )
            StorageHelper.shareImage(imageFileUri, this, isWhatsAppOnly)

        }
    }


    private fun onDownloadReceiptButtonClick() {
        PermissionHandler2.checkStoragePermission(this, onPermissionGranted = {
            if (it) {
                PdfHelper.downloadPdfData(this, recordId, isAeps = isAeps)
            }
        })
    }
}