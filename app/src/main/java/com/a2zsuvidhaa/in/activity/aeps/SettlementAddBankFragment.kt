package com.a2zsuvidhaa.`in`.activity.aeps

import `in`.galaxyofandroid.spinerdialog.SpinnerDialog
import android.os.Bundle
import android.text.Html
import com.a2zsuvidhaa.`in`.R
import com.a2zsuvidhaa.`in`.databinding.FragmentAddBankBinding
import com.a2zsuvidhaa.`in`.fragment.BaseFragment
import com.a2zsuvidhaa.`in`.listener.WebApiCallListener
import com.a2zsuvidhaa.`in`.model.BankDetail
import com.a2zsuvidhaa.`in`.util.APIs
import com.a2zsuvidhaa.`in`.util.AppConstants
import com.a2zsuvidhaa.`in`.util.WebApiCall
import com.a2zsuvidhaa.`in`.util.dialogs.Dialogs
import com.a2zsuvidhaa.`in`.util.dialogs.StatusDialog
import com.a2zsuvidhaa.`in`.util.ents.hide
import com.a2zsuvidhaa.`in`.util.ents.setupToolbar
import com.a2zsuvidhaa.`in`.util.ents.show
import com.a2zsuvidhaa.`in`.util.ents.showToast
import org.json.JSONArray
import org.json.JSONObject
import java.lang.StringBuilder
import java.util.ArrayList

class SettlementAddBankFragment : BaseFragment<FragmentAddBankBinding>(R.layout.fragment_add_bank) {

    private var mListener: OnFragmentInteractionListener? = null
    private var spinnerDialog: SpinnerDialog? = null
    private var bankName: String = AppConstants.EMPTY
    private var ifscCode: String = AppConstants.EMPTY
    private var bankId: String = AppConstants.EMPTY
    private var accountNumber: String = AppConstants.EMPTY
    private var confirmAccountNumber: String = AppConstants.EMPTY


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupToolbar(binding.toolbar,"Add Settlement Bank")

        fetchAddBankInfo()
        binding.let {
            it.llBankName.setOnClickListener {
                spinnerDialog?.showSpinerDialog()
            }
            it.btnAddBank.setOnClickListener {
                if (validateInput()) addBank()
            }
        }

    }

    private fun addBank() {

        val progressDialog = StatusDialog.progress(requireActivity(), "Processing...")

        val param = hashMapOf(
                "account_number" to accountNumber,
                "confirm_account_number" to confirmAccountNumber,
                "ifsc" to ifscCode,
                "bank_id" to bankId
        )

        WebApiCall.postRequest(requireActivity(), APIs.SETTLEMENT_ADD_BANK, param)
        WebApiCall.webApiCallback(object : WebApiCallListener {
            override fun onFailure(message: String) {
                progressDialog.dismiss()
                StatusDialog.failure(requireActivity(), message)
            }

            override fun onSuccessResponse(jsonObject: JSONObject) {
                progressDialog.dismiss()

                val status = jsonObject.getInt("status")
                val message = jsonObject.optString("message")


                if (status == 1) StatusDialog.success(requireActivity(), message).apply {
                    setOnDismissListener {
                        mListener?.onFragmentInteraction()
                        activity?.onBackPressed()
                    }
                }
                else StatusDialog.failure(requireActivity(), message)
            }

        })
    }

    private fun validateInput(): Boolean {

        accountNumber = binding.edAccountNumber.text.toString()
        confirmAccountNumber = binding.edConfirmAccountNumber.text.toString()

        if (bankName.isNotEmpty()) {
            if (ifscCode.isNotEmpty()) {
                if (accountNumber.length in 8..20) {
                    if (accountNumber == confirmAccountNumber) {
                        return true
                    } else activity?.showToast("Account number didn't matched!")
                } else activity?.showToast("Enter 8 to 12 account number")
            } else activity?.showToast("Enter Ifsc code")
        } else activity?.showToast("Select Bank Name")
        return false

    }


    private fun fetchAddBankInfo() {

        binding.progress.show()

        WebApiCall.getRequestWithHeader(requireContext(), APIs.SETTLEMENT_ADD_BANK_INFO)
        WebApiCall.webApiCallback(object : WebApiCallListener {
            override fun onFailure(message: String) {
                binding.progress.hide()
                failedDialog(message)
            }

            override fun onSuccessResponse(jsonObject: JSONObject) {
                binding.progress.hide()
                binding.scrollView.show()

                val status = jsonObject.getInt("status")
                val message = jsonObject.optString("message")

                if (status == 1) {
                    val data = jsonObject.getJSONObject("data")
                    val bankListJsonArray = data.getJSONArray("bank_list")
                    val information = data.getJSONObject("information")

                    setupTextInfo(information)
                    setupSpinnerDialogBankList(bankListJsonArray)
                } else failedDialog(message)

            }
        })

    }

    private fun setupTextInfo(information: JSONObject) {


        val lineOneObject = information.getJSONObject("line_one")
        val stringFirst = lineOneObject.getString("string_first")
        val userName = lineOneObject.getString("user_name")
        val jointer = lineOneObject.getString("jointer")
        val shopName = lineOneObject.getString("shop_name")
        val lineTwoString = information.getString("line_two")

        val stringBuilder = StringBuilder()
        stringBuilder.append(" * ")
        stringBuilder.append(stringFirst)
        stringBuilder.append(" $userName")
        stringBuilder.append(" $jointer ")
        stringBuilder.append(shopName)
        stringBuilder.append("\n * $lineTwoString")


        val mText = " * $stringFirst <b>$userName</b> $jointer <b>$shopName</b> <br>* $lineTwoString"

        binding.tvInfo.text = Html.fromHtml(mText)


    }

    private fun setupSpinnerDialogBankList(bankListJsonArray: JSONArray) {

        val bankList = arrayListOf<BankDetail>()
        for (i in 0 until bankListJsonArray.length()) {

            val bankObject: JSONObject = bankListJsonArray.getJSONObject(i)

            bankList.add(BankDetail(
                    id = bankObject.optString("id"),
                    bankName = bankObject.optString("bank_name"),
                    ifsc = bankObject.optString("ifsc")
            ))
        }


        val bankNameList: List<String> = bankList.map {
            it.bankName
        }

        spinnerDialog = SpinnerDialog(requireActivity(), bankNameList as ArrayList<String>, "Search Bank Name", "Close")
        spinnerDialog?.let { spinner ->
            spinner.setCancellable(true); // for cancellable
            spinner.setShowKeyboard(false);// for open keyboard by default
            spinner.bindOnSpinerListener { item, position ->
                val bank = bankList.find {
                    it.bankName == item
                }
                bank?.let {
                    bankName = it.bankName
                    bankId = it.id
                    ifscCode = it.ifsc

                    binding.tvBankName.text = bankName
                    binding.edIfseCode.setText(ifscCode)

                }
            }
        }

    }

    private fun failedDialog(message: String) {
        StatusDialog.failure(requireActivity(), message).apply {
            setOnDismissListener { activity?.onBackPressed() }
        }
    }

    companion object {
        fun newInstance() = SettlementAddBankFragment()
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction()
    }
}