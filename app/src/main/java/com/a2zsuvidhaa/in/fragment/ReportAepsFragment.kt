package com.a2zsuvidhaa.`in`.fragment

import android.os.Bundle
import android.text.InputFilter
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.LinearLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import com.a2zsuvidhaa.`in`.R
import com.a2zsuvidhaa.`in`.activity.AadharTransactionResponseActivity
import com.a2zsuvidhaa.`in`.adapter.AepsReportListAdapter
import com.a2zsuvidhaa.`in`.data.model.TransactionDetail
import com.a2zsuvidhaa.`in`.databinding.FragmentReportAepsBinding
import com.a2zsuvidhaa.`in`.fragment.report.LedgerReportComponent
import com.a2zsuvidhaa.`in`.fragment.report.ReportViewModel
import com.a2zsuvidhaa.`in`.listener.WebApiCallListener
import com.a2zsuvidhaa.`in`.model.AepsData
import com.a2zsuvidhaa.`in`.util.*
import com.a2zsuvidhaa.`in`.util.apis.Resource
import com.a2zsuvidhaa.`in`.util.dialogs.AepsFilterDialog
import com.a2zsuvidhaa.`in`.util.dialogs.StatusDialog
import com.a2zsuvidhaa.`in`.util.ents.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject

@AndroidEntryPoint
class ReportAepsFragment : BaseFragment<FragmentReportAepsBinding>(R.layout.fragment_report_aeps) {

    @Inject
    lateinit var volleyClient: VolleyClient

    private lateinit var strFromDate: String
    private lateinit var strToDate: String
    private var strSearchType: String = ""
    private var strSearchInput: String = ""
    private var strStatusId: String = ""
    private var strTxnType: String = ""

    private val viewModel: ReportViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupInitialDate()

        fetchReport()

        binding.btnSearch.setOnClickListener {
            AepsFilterDialog(
                strFromDate, strToDate, strStatusId, strSearchType, strTxnType, strSearchInput
            ).showDialog(requireActivity(),
                onSearchClick = { fromDate, toDate, status, inputMode, transactionType, inputText ->
                    strFromDate = fromDate
                    strToDate = toDate
                    strStatusId = status
                    strSearchType = inputMode
                    strTxnType = transactionType
                    strSearchInput = inputText

                    fetchReport()
                })
        }

    }

    private fun setupInitialDate() {

        strFromDate = AppUitls.currentDate()
        strToDate = AppUitls.currentDate()

    }

    private fun fetchReport() {

        binding.progressBar.show()
        binding.recyclerView.hide()
        binding.tvNoResult.hide()

        val url =
            "${APIs.AEPS_REPORT}?fromdate=$strFromDate&todate=$strToDate&txn_type=$strTxnType&status_id=$strStatusId&search_type=$strSearchType&search=$strSearchInput"

        WebApiCall.getRequestWithHeader(requireActivity(), url)
        WebApiCall.webApiCallback(object : WebApiCallListener {
            override fun onSuccessResponse(jsonObject: JSONObject) {

                binding.progressBar.hide()
                binding.recyclerView.show()
                binding.tvNoResult.hide()

                val status = jsonObject.getInt("status")
                val message = jsonObject.optString("message")

                if (status == 1) {
                    val data = jsonObject.getJSONArray("data")
                    parseListData(data)
                } else {
                    binding.progressBar.hide()
                    binding.recyclerView.hide()
                    binding.tvNoResult.text = message
                    binding.tvNoResult.show()
                }


            }

            override fun onFailure(message: String) {
                binding.progressBar.hide()
                binding.recyclerView.hide()
                binding.tvNoResult.text = message
                binding.tvNoResult.show()
            }

        })

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.complainTypes.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    progressDialog = StatusDialog.progress(requireContext())
                }
                is Resource.Success -> {
                    progressDialog?.dismiss()
                    if (it.data.status == 1) {
                        LedgerReportComponent(
                            requireContext(),
                            it.data.data
                        ) { complainType, remark ->
                            viewModel.makeComplain(complainType, remark)
                        }
                    } else StatusDialog.failure(requireActivity(), it.data.message)
                }
                is Resource.Failure -> {
                    progressDialog?.dismiss()
                    activity?.handleNetworkFailure(it.exception)
                }
            }
        }

        viewModel.complainObs.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    progressDialog = StatusDialog.progress(requireContext())
                }
                is Resource.Success -> {
                    progressDialog?.dismiss()
                    if (it.data.status == 1)
                        StatusDialog.success(requireActivity(), it.data.message)
                    else StatusDialog.failure(requireActivity(), it.data.message)
                }
                is Resource.Failure -> {
                    progressDialog?.dismiss()
                    activity?.handleNetworkFailure(it.exception)
                }
            }
        }

    }

    @Throws
    private fun parseListData(data: JSONArray) {

        val mList = ArrayList<AepsData>()

        for (i in 0 until data.length()) {
            val jsonObject = data.getJSONObject(i)

            val aepsData = AepsData(
                id = jsonObject.optString("id"),
                txnid = jsonObject.optString("txnid"),
                user_id = jsonObject.optString("user_id"),
                number = jsonObject.optString("number"),
                status_id = jsonObject.optString("status_id"),
                status = jsonObject.optString("status"),
                fail_msg = jsonObject.optString("fail_msg"),
                opening_balance = jsonObject.optString("opening_balance"),
                amount = jsonObject.optString("amount"),
                tds = jsonObject.optString("tds"),
                credit_charge = jsonObject.optString("credit_charge"),
                debit_charge = jsonObject.optString("debit_charge"),
                total_balance = jsonObject.optString("total_balance"),
                type = jsonObject.optString("type"),
                txn_type = jsonObject.optString("txn_type"),
                bankName = jsonObject.optString("bank_name"),
                customer_number = jsonObject.optString("customer_number"),
                ackno = jsonObject.optString("ackno"),
                bank_ref = jsonObject.optString("bank_ref"),
                apiName = jsonObject.optString("api_name"),
                mode = jsonObject.optString("mode"),
                orderId = jsonObject.optString("order_id"),
                report_id = jsonObject.optString("report_id"),
                ip_address = jsonObject.optString("ip_address"),
                created_at = jsonObject.optString("created_at"),
                transactionTypeId = jsonObject.optString("transaction_type_id"),
                is_check_status = jsonObject.optBoolean("is_check_status"),
                is_print = jsonObject.optBoolean("is_print"),
                is_complain = jsonObject.optBoolean("is_complain")
            )
            mList.add(aepsData)
        }

        if (mList.isNotEmpty()) {

            val adapter = AepsReportListAdapter().apply {
                addItems(mList)
                context = activity
            }
            binding.recyclerView.setup().adapter = adapter
            adapter.onPrint = { it ->
                onPrintButtonClick(it)
            }
            adapter.onCheckStatus = { it ->
                onCheckStatusButtonClick(it)
            }

            adapter.onComplain = { it ->
                viewModel.transactionId = it.orderId
                viewModel.fetchComplainTypes(it.transactionTypeId)
            }

        } else binding.tvNoResult.show()


    }

    private fun onPrintButtonClick(it: AepsData) {

        val dialog = StatusDialog.progress(requireActivity())
        val url = AppConstants.BASE_URL + "aeps/report/slip/new" + "/${it.id}"
        WebApiCall.getRequestWithHeader(context, url)
        WebApiCall.webApiCallback(object : WebApiCallListener {
            override fun onFailure(message: String?) {
                dialog.dismiss()
            }

            override fun onSuccessResponse(jsonObject: JSONObject) {
                dialog.dismiss()
                try {

                    val status = jsonObject.getInt("status")
                    val message = jsonObject.optString("message")
                    if (status == 1) {

                        val dataObject = jsonObject.getJSONObject("data")
                        val transactionDetail =
                            Gson().fromJson(dataObject.toString(), TransactionDetail::class.java)
                        activity?.launchIntent(
                            AadharTransactionResponseActivity::class.java, bundleOf(
                                AppConstants.DATA_OBJECT to transactionDetail,
                                AppConstants.ORIGIN to AppConstants.REPORT_ORIGIN,
                                AppConstants.FROM_REPORT to AppConstants.AEPS_REPORT,
                            )
                        )
                    } else activity?.showToast(message)

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })

    }

    private fun onCheckStatusButtonClick(it: AepsData) {

        val dialog = StatusDialog.progress(requireActivity())


        volleyClient.postRequest(APIs.AEPS_THREE_CHECK_STATUS_FROM_BANK,
            hashMapOf("record_id" to it.id),
            onSuccess = {
                dialog.dismiss()
                val status = it.getInt("status")
                val message = it.optString("message")
                when (status) {
                    1 -> StatusDialog.success(requireActivity(), message)
                    2 -> StatusDialog.failure(requireActivity(), message)
                    3 -> StatusDialog.pending(requireActivity(), message)
                    else -> StatusDialog.alert(requireActivity(), message)
                }
            },
            onFailure = {
                dialog.dismiss()
                activity?.showToast(it.message.toString())
            })

    }


    companion object {
        fun newInstance() = ReportAepsFragment()
    }


    private fun setupDate(view: BottomSheetDialog) {

        val edFromDate = view.findViewById<EditText>(R.id.ed_from_date)
        val edToDate = view.findViewById<EditText>(R.id.ed_to_date)


        edFromDate?.setText(strFromDate)
        edToDate?.setText(strToDate)


        edFromDate?.setOnClickListener {
            DatePicker.datePicker(requireActivity())
            DatePicker.setupOnDatePicker {
                edFromDate.setText(it)
                strFromDate = it
            }
        }

        edToDate?.setOnClickListener {
            DatePicker.datePicker(requireActivity())
            DatePicker.setupOnDatePicker {
                edToDate.setText(it)
                strToDate = it
            }
        }
    }


    private fun setupAutoCompleteView(view: BottomSheetDialog) {


        view.findViewById<AutoCompleteTextView>(R.id.actv_transaction_status)
            ?.setup(transactionStatusList()) {
                strStatusId = when (it) {
                    "Success" -> "1"
                    "Pending" -> "3"
                    "Failure" -> "2"
                    else -> ""
                }
            }
        view.findViewById<AutoCompleteTextView>(R.id.actv_input_mode)?.setup(inputModeList()) {
            val tilInput = view.findViewById<TextInputLayout>(R.id.til_input)
            val edInput = view.findViewById<EditText>(R.id.ed_input)
            when (it) {
                "Mobile Number" -> {

                    strSearchType = "MOB"
                    tilInput?.show()
                    tilInput?.hint = "Enter Mobile Number"
                    edInput?.filters = arrayOf(InputFilter.LengthFilter(10))

                }
                "Aadhaar Number" -> {
                    strSearchType = "AADHAAR"
                    tilInput?.hint = "Enter Aadhaar Number"
                    tilInput?.show()
                    edInput?.filters = arrayOf(InputFilter.LengthFilter(12))
                }
                else -> {
                    strSearchType = ""
                    strSearchInput = ""
                    tilInput?.hide()
                }
            }
        }

        view.findViewById<AutoCompleteTextView>(R.id.actv_transaction_type)
            ?.setup(transactionTypeList()) {

                strTxnType = when (it) {
                    "Balance Enquiry" -> "BALANCE_INQUIRY"
                    "Mini Statement" -> "AEPS(MINI_STATEMENT)"
                    "Cash Withdrawal" -> "AEPS(CASH_WITHDRAWAL)"
                    "Aadhaar Pay" -> "Wallet Update(Adhaar Pay)"
                    else -> ""
                }
            }
    }

    private fun transactionTypeList() = ArrayList<String>().apply {
        add("All")
        add("Balance Enquiry")
        add("Mini Statement")
        add("Cash Withdrawal")
        add("Aadhaar Pay")

    }

    private fun inputModeList() = ArrayList<String>().apply {
        add("None")
        add("Mobile Number")
        add("Aadhaar Number")

    }

    private fun transactionStatusList() = ArrayList<String>().apply {
        add("All")
        add("Success")
        add("Failure")
        add("Pending")

    }
}