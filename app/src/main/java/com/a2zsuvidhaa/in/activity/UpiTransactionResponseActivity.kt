package com.a2zsuvidhaa.`in`.activity

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.a2zsuvidhaa.`in`.BuildConfig
import com.a2zsuvidhaa.`in`.R
import com.a2zsuvidhaa.`in`.adapter.DmtTransactionAdapter
import com.a2zsuvidhaa.`in`.data.model.DmtTransactionDetail
import com.a2zsuvidhaa.`in`.data.model.TransactionDetail
import com.a2zsuvidhaa.`in`.databinding.ActivityDmtResponseBinding
import com.a2zsuvidhaa.`in`.databinding.ActivityUpiResponseBinding
import com.a2zsuvidhaa.`in`.util.AppConstants
import com.a2zsuvidhaa.`in`.util.PermissionHandler2
import com.a2zsuvidhaa.`in`.util.dialogs.Dialogs
import com.a2zsuvidhaa.`in`.util.ents.*
import com.a2zsuvidhaa.`in`.util.file.StorageHelper
import com.a2zsuvidhaa.`in`.util.pdf.PdfHelper

class UpiTransactionResponseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpiResponseBinding
    private lateinit var origin: String
    private var status: Int? = null
    private var transactionId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpiResponseBinding.inflate(layoutInflater)

        setContentView(binding.root)
        origin = intent.getStringExtra(AppConstants.ORIGIN)!!


        setupDataFromTransaction()

        binding.let {
            it.btnClose.setOnClickListener { onBackPressed() }
            it.btnShareWhatsapp.setOnClickListener { onShareButtonClick(true) }
            it.btnShare.setOnClickListener { onShareButtonClick(false) }
            it.llDownloadReceipt.setOnClickListener { onDownloadReceipt() }
        }
    }

    override fun onBackPressed() {
        if (origin == AppConstants.REPORT_ORIGIN) super.onBackPressed()
        else goToMainActivity()
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
                    if (BuildConfig.DEBUG)
                        it.llDownloadReceipt.show()
                    else it.llDownloadReceipt.hide()
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


    private fun setupDataFromTransaction() {
        val printResponse: TransactionDetail? = intent.getParcelableExtra(AppConstants.DATA_OBJECT)
        status = printResponse?.status
        transactionId = printResponse?.reportId
        binding.let {
            it.tvStatus.text = printResponse?.statusDesc.orEmpty()
            it.tvMessage.text = printResponse?.message.orEmpty()
            it.tvTxnTime.text = printResponse?.txnTime.orEmpty()
            it.tvServiceName.text = printResponse?.serviceName.orEmpty()
            it.tvAmount.text = printResponse?.amount.orEmpty()
            it.tvSenderName.text = printResponse?.senderName.orEmpty()
            it.tvAccountNumber.text = printResponse?.number.orEmpty()
            it.tvBeneName.text = printResponse?.name.orEmpty()
            it.tvSenderNumber.text = printResponse?.senderNumber.orEmpty()
            it.tvPaymentAmount.text = printResponse?.amount.orEmpty()
            it.tvWalletAmount.text = printResponse?.amount.orEmpty()
            it.tvShopName.text = printResponse?.outletName.orEmpty()
            it.tvContactNumber.text = printResponse?.outletNumber.orEmpty()
            it.tvTxnId.text = printResponse?.reportId.orEmpty()
            it.tvBankRef.text = printResponse?.bankRef.orEmpty()

            if(printResponse?.reportId.orEmpty().isEmpty()) binding.llTxnId.hide()
            if(printResponse?.bankRef.orEmpty().isEmpty()) binding.llBankRef.hide()
        }
        setupStatus()

    }


    private fun onShareButtonClick(isWhatsAppOnly: Boolean = false) {
        PermissionHandler2.checkStoragePermission(this) { isGranted ->
            if (!isGranted) return@checkStoragePermission

            binding.viewSpace.hide()
            binding.llDownloadReceipt.hide()
            binding.scrollView.setBackgroundColor(ContextCompat.getColor(this, R.color.black))

            val bitmap: Bitmap? = StorageHelper.getBitmapFromView(scrollView = binding.scrollView,
                    onAfterConvert = {
                        binding.scrollView.setBackgroundColor(ContextCompat.getColor(this, R.color.white2))
                        binding.viewSpace.show()
                        if (status != 2) binding.llDownloadReceipt.show()
                    })

            val imageFileUri: Uri = StorageHelper.saveImageToCacheDirectory(
                    context = this,
                    bitmap = bitmap,
                    fileName = "transaction_receipt.jpg")
            StorageHelper.shareImage(imageFileUri, this, isWhatsAppOnly)

        }
    }

    private fun onDownloadReceipt() {
        PermissionHandler2.checkStoragePermission(this, onPermissionGranted = {
            if (it) {

                Dialogs.dmtCommissionAmount(this).apply {
                    val edCommission = findViewById<EditText>(R.id.ed_commission)
                    val btnSkip = findViewById<Button>(R.id.btn_skip)
                    val btnSubmit = findViewById<Button>(R.id.btn_submit)

                    btnSkip.setOnClickListener {
                        dismiss()
                        PdfHelper.downloadPdfData(
                                this@UpiTransactionResponseActivity,
                                transactionId.orEmpty(),
                                "0"
                        )
                    }
                    btnSubmit.setOnClickListener {
                        dismiss()
                        val commission = if (edCommission.text.toString() == "") "0" else edCommission.text.toString()
                        PdfHelper.downloadPdfData(
                                this@UpiTransactionResponseActivity,
                                transactionId.orEmpty(),
                                commission
                        )
                    }
                }

            }
        })
    }
}