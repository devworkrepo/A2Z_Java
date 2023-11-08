package com.a2zsuvidhaa.`in`.fragment

import android.os.Bundle
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.EditText
import androidx.core.os.bundleOf
import com.a2zsuvidhaa.`in`.R
import com.a2zsuvidhaa.`in`.activity.matm.MatmResponseActivity
import com.a2zsuvidhaa.`in`.adapter.MatmReportAdapter
import com.a2zsuvidhaa.`in`.data.model.TransactionDetail
import com.a2zsuvidhaa.`in`.databinding.FragmentReportAepsBinding
import com.a2zsuvidhaa.`in`.listener.WebApiCallListener
import com.a2zsuvidhaa.`in`.model.MatmReportData
import com.a2zsuvidhaa.`in`.util.*
import com.a2zsuvidhaa.`in`.util.dialogs.AepsFilterDialog
import com.a2zsuvidhaa.`in`.util.dialogs.MatmFilterDialog
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
class MatmReportFragment : BaseFragment<FragmentReportAepsBinding>(R.layout.fragment_report_aeps) {

    @Inject
    lateinit var volleyClient: VolleyClient

    private lateinit var strFromDate: String
    private lateinit var strToDate: String
    private var strSearchInput: String = ""
    private var strSearchType: String = ""
    private var strStatusId: String = ""
    private var strTxnType: String = ""


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupInitialDate()

        fetchReport()

        binding.btnSearch.setOnClickListener {
            MatmFilterDialog(
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
            "${APIs.MATM_REPORT}?fromdate=$strFromDate&todate=$strToDate&txn_type=$strTxnType&status_id=$strStatusId&searchType=$strSearchType&number=$strSearchInput"
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

    @Throws
    private fun parseListData(data: JSONArray) {

        val mList = ArrayList<MatmReportData>()

        for (i in 0 until data.length()) {
            val jsonObject = data.getJSONObject(i)

            val aepsData = MatmReportData(
                status = jsonObject.optInt("status"),
                status_desc = jsonObject.optString("status_desc"),
                message = jsonObject.optString("message"),
                record_id = jsonObject.optString("record_id"),
                service_name = jsonObject.optString("service_name"),
                customer_number = jsonObject.optString("customer_number"),
                txn_id = jsonObject.optString("txn_id"),
                order_id = jsonObject.optString("order_id"),
                transaction_type = jsonObject.optString("transaction_type"),
                card_type = jsonObject.optString("card_type"),
                credit_debit_card_type = jsonObject.optString("credit_debit_card_type"),
                available_amount = jsonObject.optString("available_amount"),
                transaction_amount = jsonObject.optString("transaction_amount"),
                is_pin_verified = jsonObject.optString("is_pin_verified"),
                transaction_mode = jsonObject.optString("transaction_mode"),
                bank_ref = jsonObject.optString("bank_ref"),
                txn_time = jsonObject.optString("txn_time"),
                is_print = jsonObject.optBoolean("is_print"),
                is_check_status = jsonObject.optBoolean("is_check_status"),
                is_complain = jsonObject.optBoolean("is_complain"),
                tds = jsonObject.optString("tds"),
                credit_charge = jsonObject.optString("credit_charge"),
                debit_charge = jsonObject.optString("debit_charge"),
                gst = jsonObject.optString("gst"),



                )
            mList.add(aepsData)
        }

        if (mList.isNotEmpty()) {

            val adapter = MatmReportAdapter().apply {
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
        } else binding.tvNoResult.show()


    }

    private fun onPrintButtonClick(it: MatmReportData) {

        val dialog = StatusDialog.progress(requireActivity())
        val url = AppConstants.BASE_URL + "matm/report-slip" + "/${it.record_id}"
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
                            MatmResponseActivity::class.java, bundleOf(
                                AppConstants.DATA_OBJECT to transactionDetail,
                                AppConstants.REPORT_ORIGIN to AppConstants.MATM_REPORT
                            )
                        )
                    } else activity?.showToast(message)

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })

    }

    private fun onCheckStatusButtonClick(it: MatmReportData) {

        val dialog = StatusDialog.progress(requireActivity())


        volleyClient.getRequest(APIs.MATM_DIRECT_CHECK_STATUS,
            hashMapOf("recordId" to it.record_id.toString()),
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
        fun newInstance() = MatmReportFragment()
    }
}