package com.a2zsuvidhaa.`in`.activity.fund_request

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import com.a2zsuvidhaa.`in`.R
import com.a2zsuvidhaa.`in`.AppPreference
import com.a2zsuvidhaa.`in`.databinding.FragmentUpiPaymentBinding
import com.a2zsuvidhaa.`in`.fragment.BaseFragment
import com.a2zsuvidhaa.`in`.listener.WebApiCallListener
import com.a2zsuvidhaa.`in`.util.APIs
import com.a2zsuvidhaa.`in`.util.Security
import com.a2zsuvidhaa.`in`.util.WebApiCall
import com.a2zsuvidhaa.`in`.util.dialogs.StatusDialog
import com.a2zsuvidhaa.`in`.util.ents.goToMainActivity
import com.a2zsuvidhaa.`in`.util.ents.setupToolbar
import com.a2zsuvidhaa.`in`.util.ents.showToast
import com.android.volley.*
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

@AndroidEntryPoint
class UpiPaymentFragment() : BaseFragment<FragmentUpiPaymentBinding>(R.layout.fragment_upi_payment) {

    @Inject
    lateinit var appPreference: AppPreference

    @Inject
    lateinit var security: Security

    private lateinit var toolbar: Toolbar
    private lateinit var amount: String


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar = view.findViewById(R.id.toolbar)
        setupToolbar(toolbar, "UPI Payment")

        binding.btnProceed.setOnClickListener {
            if (!validateInput()) return@setOnClickListener
            checkSetupService()
        }

        binding.edAmount.requestFocus()

    }

    private fun validateInput(): Boolean {

        var isValid = true

        amount = binding.edAmount.text.toString()
        if (amount.isEmpty()) amount = "0"

        if (amount.toDouble() == 0.0) {

            isValid = false
            binding.tilAmount.error = "Amount can't be zero!"
            binding.tilAmount.isErrorEnabled = true


        } else {
            binding.tilAmount.isErrorEnabled = false
        }
        return isValid
    }

    private fun checkSetupService() {

        progressDialog = StatusDialog.progress(requireActivity())

        val param = HashMap<String, String>()
        param["amount"] = amount

        WebApiCall.postRequest(requireActivity(), APIs.GENERATE_QRCODE_URL, param)
        WebApiCall.webApiCallback(object : WebApiCallListener {
            override fun onSuccessResponse(jsonObject: JSONObject) {

                progressDialog?.dismiss()

                val status = jsonObject.getInt("status")
                val message = jsonObject.optString("message")

                activity?.showToast(message)
                if (status == 1) {
                    val refNumber = jsonObject.getString("refId")
                    startPayment(refNumber)
                } else StatusDialog.failure(requireActivity(), message)

            }

            override fun onFailure(message: String) {
                progressDialog?.dismiss()
                StatusDialog.failure(requireActivity(), message)
            }

        })
    }

    private fun startPayment(refId: String) {
        try{
            val upiStr = "upi://pay?pa=excelone@icici&pn=Excel Stop&tr=$refId&am=$amount" +
                    "&cu=INR&mc=5411&tn=${security.decrypt(appPreference.mobile)}${appPreference.shopName}"
            val intent = Intent()
            //  intent.addCategory(Intent.CATEGORY_HOME);
            //intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.data = Uri.parse(upiStr)
            val chooser = Intent.createChooser(intent, "Pay with...")
            startActivityForResult(chooser, 1, null)
        }catch (e : Exception){
            activity?.showToast("Failed to make payment! please try again")
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 1) activity?.goToMainActivity()

    }

    companion object {
        fun newInstance() = UpiPaymentFragment()
    }



}