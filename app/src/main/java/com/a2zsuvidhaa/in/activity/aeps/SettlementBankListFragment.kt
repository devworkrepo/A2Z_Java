package com.a2zsuvidhaa.`in`.activity.aeps

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.lifecycle.lifecycleScope
import com.a2zsuvidhaa.`in`.R
import com.a2zsuvidhaa.`in`.adapter.SettlementBankListAdapter
import com.a2zsuvidhaa.`in`.databinding.FragmentSettlementBankListBinding
import com.a2zsuvidhaa.`in`.fragment.BaseFragment
import com.a2zsuvidhaa.`in`.listener.WebApiCallListener
import com.a2zsuvidhaa.`in`.model.BankDetail
import com.a2zsuvidhaa.`in`.util.*
import com.a2zsuvidhaa.`in`.util.BitmapUtil.addWatermark
import com.a2zsuvidhaa.`in`.util.BitmapUtil.reduceSize
import com.a2zsuvidhaa.`in`.util.BitmapUtil.toFile
import com.a2zsuvidhaa.`in`.util.dialogs.Dialogs
import com.a2zsuvidhaa.`in`.util.dialogs.StatusDialog
import com.a2zsuvidhaa.`in`.util.ents.*
import com.a2zsuvidhaa.`in`.util.file.AppFileUtil
import com.a2zsuvidhaa.`in`.util.file.AppFileUtil.processForRightAngleImage
import com.a2zsuvidhaa.`in`.util.file.AppFileUtil.toBitmap
import com.a2zsuvidhaa.`in`.util.file.FragmentImagePicker
import com.a2zsuvidhaa.`in`.util.file.StorageHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File


class SettlementBankListFragment() :
    BaseFragment<FragmentSettlementBankListBinding>(R.layout.fragment_settlement_bank_list) {

    var listener: SettlementBankListFragmentListener? = null
    private var selectedFilePath: String = AppConstants.EMPTY
    private var selectedFileMimeType: String = AppConstants.EMPTY
    private var selectedFileName: String = AppConstants.EMPTY
    private lateinit var settlementId: String


    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = activity as SettlementBankListFragmentListener
        } catch (e: ClassCastException) {
            throw ClassCastException(activity.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    /* override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)

         (activity as AepsSettlementActivityKT?)?.setOnDataListener(object : AepsSettlementActivityKT.OnFileImagePickerResult {
             override fun fileImagePickerResult(requestCode: Int, resultCode: Int, data: Intent?) {
                 if (resultCode == AppCompatActivity.RESULT_OK) {
                     val uri: Uri? = if (PickerHelperFragment.CAMERA_PICKER_CODE == requestCode) {
                         val bitmap = data?.extras?.get("data") as Bitmap
                         getImageUriForCameraCapture(bitmap)
                     } else data?.data
                     selectedFilePath = FileUtils.getPath(requireContext(), uri)
                     selectedFileMimeType = FileUtils.getMimeType(requireContext(), uri)
                     selectedFileName = FileUtils.getFileName(requireContext(), uri)
                     showUploadConfirmationDialog()

                 }
             }

         })
     }
 */
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupToolbar(binding.toolbar, "Settlement Banks")
        binding.llAddBank.setOnClickListener {
            listener?.onAddBankFragment()
        }

        fetchAvailableBankList()

    }

    private fun fetchAvailableBankList() {


        binding.let {
            it.progress.show()
            it.llContent.hide()
        }

        WebApiCall.getRequest(requireActivity(), APIs.GET_MY_BANK_DETAILS + "?")
        WebApiCall.webApiCallback(object : WebApiCallListener {
            override fun onFailure(message: String) {

                binding.let {
                    it.progress.hide()
                    it.llContent.show()
                }
            }

            override fun onSuccessResponse(jsonObject: JSONObject) {

                binding.let {
                    it.progress.hide()
                    it.llContent.show()
                }

                val status = jsonObject.getInt("status")
                val message = jsonObject.optString("message")

                if (status == 1) {

                    val bankListJsonArray = jsonObject.getJSONArray("data")
                    parseBankList(bankListJsonArray)
                } else StatusDialog.failure(requireActivity(), message)


            }

        })

    }

    private fun parseBankList(bankListJsonArray: JSONArray) {

        val bankList = arrayListOf<BankDetail>()

        for (i in 0 until bankListJsonArray.length()) {
            val bankJsonObject = bankListJsonArray.getJSONObject(i)
            val bankDetail = BankDetail()
            bankDetail.id = bankJsonObject.getString("id")
            bankDetail.beneName = bankJsonObject.getString("name")
            bankDetail.status = bankJsonObject.getString("status_id")
            bankDetail.account_number = bankJsonObject.getString("account_number")
            bankDetail.branchName = bankJsonObject.getString("branch_name")
            bankDetail.bankName = bankJsonObject.getString("bank_name")
            bankDetail.ifsc = bankJsonObject.getString("ifsc")
            bankDetail.balance = bankJsonObject.getString("balance")
            bankDetail.aeps_bloack_amount = bankJsonObject.getString("aeps_bloacked_amount")
            bankDetail.messageOne = bankJsonObject.getString("remark")
            bankDetail.document_status = bankJsonObject.getInt("document_status")
            bankList.add(bankDetail)
        }
        setupRecyclerView(bankList)
    }

    private fun setupRecyclerView(bankList: ArrayList<BankDetail>) {

        bankList.sortWith(compareBy { it.status.toInt() })

        val adapter = SettlementBankListAdapter().apply {
            context = requireContext()
            addItems(bankList)
        }
        binding.recyclerView.setup().adapter = adapter
        adapter.onItemClick = { _, bankDetail, _ ->
            listener?.onSettlementFragment(bankDetail)
        }
        adapter.onUploadDocs = {
            settlementId = it
            FragmentImagePicker.pickMultiple(this)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val file: File = FragmentImagePicker.onActivityResult(this, requestCode,resultCode, data) ?: return
        lifecycleScope.launch(Dispatchers.Main) {
            val dialog = StatusDialog.progress(requireContext(),"Optimizing Image...")

            val optimizeFile: File? = withContext(Dispatchers.IO) {
                file.processForRightAngleImage()
                    .toBitmap()
                    .reduceSize(10)
                    .toFile(context)
            }
            dialog.dismiss()
            optimizeFile?.let {
                selectedFilePath = it.path.toString()
                selectedFileMimeType = FileUtils.getMimeType(it)
                selectedFileName = it.name.toString()
                showUploadConfirmationDialog()
            } ?: kotlin.run { StatusDialog.failure(requireActivity(), "File not found!") }
        }
    }


    private fun showUploadConfirmationDialog() {
        Dialogs.commonConfirmDialog(requireActivity(), selectedFileName).apply {
            findViewById<Button>(R.id.btn_confirm).apply {
                text = "Confirm & Upload"
                setOnClickListener {
                    dismiss()
                    if (selectedFileName.isNotEmpty() || selectedFilePath.isNotEmpty()) {
                        uploadDocument()
                    } else activity?.showToast("Invalid file selection!")
                }
            }
        }

    }

    private fun uploadDocument() {

        progressDialog = StatusDialog.progress(requireActivity(), "Uploading...")

        val param = hashMapOf(
            "bank_document" to StorageHelper.fileToString(selectedFilePath)!!,
            "settlement_id" to settlementId,
            "mime_type" to selectedFileMimeType
        )

        WebApiCall.postRequest(requireActivity(), APIs.AEPS_SETTLEMENT_UPLOAD_DOCS, param)
        WebApiCall.webApiCallback(object : WebApiCallListener {
            override fun onSuccessResponse(jsonObject: JSONObject) {
                progressDialog?.dismiss()
                val status = jsonObject.getInt("status")
                val message = jsonObject.getString("message")

                if (status == 1) {
                    StatusDialog.success(requireActivity(), message) {
                        fetchAvailableBankList()
                    }
                } else StatusDialog.failure(requireActivity(), message)
            }

            override fun onFailure(message: String) {
                progressDialog?.dismiss()
                StatusDialog.failure(requireActivity(), message)
            }

        })

    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    companion object {
        fun newInstance() = SettlementBankListFragment()
    }

    interface SettlementBankListFragmentListener {
        fun onAddBankFragment()
        fun onSettlementFragment(bankDetail: BankDetail)
    }

}