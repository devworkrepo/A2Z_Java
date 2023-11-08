package com.a2zsuvidhaa.`in`.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.a2zsuvidhaa.`in`.databinding.ActivityQrScannerBinding
import com.a2zsuvidhaa.`in`.util.AppConstants
import com.google.zxing.Result
import me.dm7.barcodescanner.zxing.ZXingScannerView


class QRScannerActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {

    private lateinit var binding: ActivityQrScannerBinding
    private var isFlashOne = false
    public override fun onCreate(state: Bundle?) {
        super.onCreate(state)

        binding = ActivityQrScannerBinding.inflate(layoutInflater)

        setContentView(binding.root)

        setupFlashStatus()

        binding.tvFlashStatus.setOnClickListener {
            isFlashOne = !isFlashOne
            setupFlashStatus()
        }

    }

    private fun setupFlashStatus() {
        when (isFlashOne) {
            true -> {
                binding.tvFlashStatus.text = "Flash Off"
                binding.zxScan.flash = true
            }
            false -> {
                binding.tvFlashStatus.text = "Flash On"
                binding.zxScan.flash = false
            }
        }
    }


    public override fun onResume() {
        super.onResume()
        binding.zxScan.setResultHandler(this)
        binding.zxScan.startCamera()
    }

    public override fun onPause() {
        super.onPause()
        binding.zxScan.stopCamera()
    }

    override fun handleResult(rawResult: Result) {

        val returnIntent = Intent()
        returnIntent.putExtra(AppConstants.DATA, rawResult.text.toString())
        setResult(101, returnIntent)
        finish()

       // binding.zxScan.resumeCameraPreview(this)

    }
}