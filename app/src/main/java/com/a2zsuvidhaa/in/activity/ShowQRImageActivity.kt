package com.a2zsuvidhaa.`in`.activity

import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.a2zsuvidhaa.`in`.AppPreference
import com.a2zsuvidhaa.`in`.databinding.ActivityQrScannerBinding
import com.a2zsuvidhaa.`in`.databinding.ActivityShowQrImageBinding
import com.a2zsuvidhaa.`in`.fragment.home.HomeViewModel
import com.a2zsuvidhaa.`in`.util.file.StorageHelper
import com.a2zsuvidhaa.`in`.util.PermissionHandler2
import com.a2zsuvidhaa.`in`.util.apis.Resource
import com.a2zsuvidhaa.`in`.util.ents.hide
import com.a2zsuvidhaa.`in`.util.ents.setupToolbar
import com.a2zsuvidhaa.`in`.util.ents.show
import com.a2zsuvidhaa.`in`.util.ents.showToast
import com.a2zsuvidhaa.`in`.util.pdf.PdfHelper
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ShowQRImageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShowQrImageBinding
    private val viewModel: HomeViewModel by viewModels()
    private val QRcodeWidth = 500

    @Inject
    lateinit var appPreference: AppPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityShowQrImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar(binding.toolbar, "A2Z Accepted Payment")

        viewModel.fetchQRCodeData()

        subscribeObservers()

        setupOnClickListener()

    }

    private fun setupOnClickListener() {
        binding.let {

            it.btnDownload.setOnClickListener { _ ->

                PermissionHandler2.checkStoragePermission(this) { isGranted ->
                    if (!isGranted) return@checkStoragePermission
                    it.llHideableOnScreenshot.hide()

                    val bitmap: Bitmap? = StorageHelper.getBitmapFromView(
                            scrollView = it.scrollView,
                            onAfterConvert = {
                                it.llHideableOnScreenshot.show()
                            }
                    )

                    val pdfFileUri = StorageHelper.savePdfToCacheDirectory(this, bitmap!!, "A2Z Payment QRCode.pdf")
                    PdfHelper.viewPdf(pdfFileUri, this)

                }
            }

            it.btnShareWhatsapp.setOnClickListener { _ ->

                PermissionHandler2.checkStoragePermission(this) { isGranted ->
                    if (!isGranted) return@checkStoragePermission

                    it.llHideableOnScreenshot.hide()

                    val bitmap: Bitmap? = StorageHelper.getBitmapFromView(
                            scrollView = it.scrollView,
                            onAfterConvert = {
                                it.llHideableOnScreenshot.show()
                            }
                    )

                    val imageFileUri: Uri =
                            StorageHelper.saveImageToCacheDirectory(
                                    context = this,
                                    bitmap = bitmap,
                                    fileName = "qr_code.jpg"
                            )

                    StorageHelper.shareImage(imageFileUri, this, false)

                }
            }
        }
    }

    private fun subscribeObservers() {
        viewModel.qrCodeObs.observe(this, Observer {
            when (it) {
                is Resource.Loading -> {
                    binding.run {
                        llQrCode.hide()
                        progress.show()
                    }
                }

                is Resource.Success -> {

                    binding.run {

                        llQrCode.show()
                        progress.hide()

                        if (it.data.status == 1) {
                            tvTitle1.text = it.data.dynamicVpnInfo
                            tvTitle2.text = it.data.retailerQRUpiData
                            showQRCode(it.data.strUrl)
                        } else {
                            scrollView.hide()
                            tvServiceDown.show()
                            tvServiceDown.text = it.data.message
                        }
                    }
                }

                is Resource.Failure -> {
                    binding.run {
                        progress.hide()
                        llQrCode.show()
                        showToast("Something went wrong")
                    }
                }
            }
        })
    }

    private fun showQRCode(strUrl: String) {

        try {
            val bitmap = textToImageEncode(strUrl)
            binding.ivQrCode.setImageBitmap(bitmap)
            binding.ivQrCode.invalidate()
        } catch (e: WriterException) {
            e.printStackTrace()
        }

    }


    private fun textToImageEncode(Value: String): Bitmap? {
        val bitMatrix: BitMatrix
        try {
            bitMatrix = MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.QR_CODE,
                    QRcodeWidth, QRcodeWidth, null
            )
        } catch (exception: IllegalArgumentException) {
            return null
        }
        val bitMatrixWidth = bitMatrix.getWidth()
        val bitMatrixHeight = bitMatrix.getHeight()
        val pixels = IntArray(bitMatrixWidth * bitMatrixHeight)
        for (y in 0 until bitMatrixHeight) {
            val offset = y * bitMatrixWidth
            for (x in 0 until bitMatrixWidth) {
                pixels[offset + x] = if (bitMatrix.get(x, y))
                    Color.parseColor("#000000")
                else Color.parseColor("#ffffff")
            }
        }
        val bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444)
        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight)
        return bitmap
    }

}

