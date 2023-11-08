package com.a2zsuvidhaa.`in`.activity.aeps

import android.app.Dialog
import android.os.Bundle
import android.text.Html
import android.widget.Button
import com.a2zsuvidhaa.`in`.R
import com.a2zsuvidhaa.`in`.AppPreference
import com.a2zsuvidhaa.`in`.databinding.FragmentAepsSettlmentBinding
import com.a2zsuvidhaa.`in`.fragment.BaseFragment
import com.a2zsuvidhaa.`in`.listener.WebApiCallListener
import com.a2zsuvidhaa.`in`.model.BankDetail
import com.a2zsuvidhaa.`in`.util.APIs
import com.a2zsuvidhaa.`in`.util.WebApiCall
import com.a2zsuvidhaa.`in`.util.dialogs.Dialogs
import com.a2zsuvidhaa.`in`.util.dialogs.StatusDialog
import com.a2zsuvidhaa.`in`.util.ents.goToMainActivity
import com.a2zsuvidhaa.`in`.util.ents.setupToolbar
import com.a2zsuvidhaa.`in`.util.ents.showToast
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import java.util.*

@AndroidEntryPoint
class SettlementFragment() : BaseFragment<FragmentAepsSettlmentBinding>(R.layout.fragment_aeps_settlment) {

    private lateinit var bankDetail: BankDetail
    lateinit var appPreference: AppPreference
    private lateinit var amount: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bankDetail = requireArguments().getParcelable(BANK_DETAIL)!!

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupToolbar(binding.toolbar, "Aeps Settlement")
        setupUIData()
        binding.btnTransfer.setOnClickListener {

            Dialogs.commonConfirmDialog(requireContext(),"").apply {
                findViewById<Button>(R.id.btn_confirm).setOnClickListener {
                    dismiss()
                    onSettlement()
                }
            }
        }
    }

    private fun onSettlement() {

        if (!validateInput()) return@onSettlement

        val params = HashMap<String, String>()
        params["id"] = bankDetail.id
        params["beneName"] = bankDetail.beneName
        params["ifsc"] = bankDetail.ifsc
        params["bank_account"] = bankDetail.account_number
        params["amount"] = amount
        params["channel"] = "2"

        progressDialog = StatusDialog.fullScreenProgress(requireActivity())

        WebApiCall.postRequest(requireActivity(), APIs.AEPS_SETTLEMENT, params)
       WebApiCall.webApiCallback(object : WebApiCallListener {
            override fun onFailure(message: String) {
                progressDialog?.dismiss()
                StatusDialog.failure(requireActivity(), message)
            }

            override fun onSuccessResponse(jsonObject: JSONObject) {
                progressDialog?.dismiss()
                val status = jsonObject.getInt("status")
                val message = jsonObject.optString("message")

                if (status == 1 || status == 34) StatusDialog.success(requireActivity(), message).apply {
                    setOnDismissListener { activity?.goToMainActivity() }
                }
                else if(status == 3) StatusDialog.pending(requireActivity(),message).apply {
                    setOnDismissListener { activity?.goToMainActivity() }
                }
                else StatusDialog.failure(requireActivity(), message)
            }

        })


    }

    private fun validateInput(): Boolean {
        amount = binding.edAmount.text.toString()

        if (amount.isEmpty()) amount = "0"
        if (amount.toDouble() < 10) {
            activity?.showToast("Minimum amount 10 rupees")
            return false
        }

        var availableBalance = bankDetail.balance
        if (availableBalance.contains(",")) availableBalance = availableBalance.replace(",", "")

        if (amount.toDouble() > availableBalance.toDouble()) {
            activity?.showToast("Entered amount is greater than your wallet available balance")
            return false
        }


        return true
    }

    private fun setupUIData() {
        binding.let {
            it.tvBeneName.text = bankDetail.beneName
            it.tvAccountNo.text = bankDetail.account_number
            it.tvBankName.text = bankDetail.bankName
            it.tvIfsc.text = bankDetail.ifsc
            it.tvAvailBalance.text = bankDetail.balance


            val termAndCondition = """
               1. Please check account number before settlement for imps option. Any transaction to wrong account using imps will not be returned back.
            """.trimIndent()
            it.tvTermAndCondition.text = Html.fromHtml(termAndCondition)
        }
    }

    companion object {


        const val BANK_DETAIL = "bank_detail"
        fun newInstance(bankDetail: BankDetail): SettlementFragment {
            val bundle = Bundle().apply {
                putParcelable(BANK_DETAIL, bankDetail)
            }
            return SettlementFragment().apply {
                arguments = bundle
            }
        }
    }

}