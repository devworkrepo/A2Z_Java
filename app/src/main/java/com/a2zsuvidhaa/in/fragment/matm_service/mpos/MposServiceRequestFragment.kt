package com.a2zsuvidhaa.`in`.fragment.matm_service.mpos

import `in`.galaxyofandroid.spinerdialog.SpinnerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.a2zsuvidhaa.`in`.R
import com.a2zsuvidhaa.`in`.activity.MainActivity
import com.a2zsuvidhaa.`in`.databinding.FragmentMposServiceRequestBinding
import com.a2zsuvidhaa.`in`.fragment.BaseFragment
import com.a2zsuvidhaa.`in`.listener.KycRequiredListener
import com.a2zsuvidhaa.`in`.util.*
import com.a2zsuvidhaa.`in`.util.BitmapUtil.addWatermark
import com.a2zsuvidhaa.`in`.util.BitmapUtil.reduceSize
import com.a2zsuvidhaa.`in`.util.BitmapUtil.toFile
import com.a2zsuvidhaa.`in`.util.apis.Resource
import com.a2zsuvidhaa.`in`.util.dialogs.Dialogs
import com.a2zsuvidhaa.`in`.util.dialogs.StatusDialog
import com.a2zsuvidhaa.`in`.util.ents.*
import com.a2zsuvidhaa.`in`.util.file.AppFileUtil.processForRightAngleImage
import com.a2zsuvidhaa.`in`.util.file.AppFileUtil.toBitmap
import com.a2zsuvidhaa.`in`.util.file.FragmentImagePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.ArrayList


@AndroidEntryPoint
class MposServiceRequestFragment() :
    BaseFragment<FragmentMposServiceRequestBinding>(R.layout.fragment_mpos_service_request) {


    private val viewModel: MposServiceRequestViewModel by viewModels()

    private var kycRequiredListener: KycRequiredListener? = null

    private var spinnerDialogAddressType: SpinnerDialog? = null
    private var spinnerDialogProofType: SpinnerDialog? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        kycRequiredListener = activity as MainActivity

    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        subscribeObserver()

        viewModel.fetchInfoAndTypeList()

        clickHandlers()


    }


    private fun setupSelectedImages(file: File) {
        val uri = FileUtils.getUri(file)
        when (viewModel.imagePickerDocType) {
            MposServiceRequestViewModel.DocType.SHOP_INSIDE -> {
                binding.ivShopInside.setImageURI(uri)
                viewModel.shopInsideFile = file
            }
            MposServiceRequestViewModel.DocType.SHOP_OUTSIDE -> {
                binding.ivShopOutside.setImageURI(uri)
                viewModel.shopOutsideFile = file
            }
            MposServiceRequestViewModel.DocType.BUSINESS_LEGALITY -> {
                binding.ivBusinessLegality.setImageURI(uri)
                viewModel.businessLegalityFile = file
            }
            MposServiceRequestViewModel.DocType.BUSINESS_ADDRESS -> {
                binding.ivBusinessAddress.setImageURI(uri)
                viewModel.businessAddressFile = file
            }
        }
    }


    private fun clickHandlers() {
        binding.btnProceed.setOnClickListener {
            if (!validateInputs()) return@setOnClickListener
            viewModel.uploadDetails()
        }

        binding.ivShopInside.setOnClickListener {
            viewModel.imagePickerDocType = MposServiceRequestViewModel.DocType.SHOP_INSIDE
            FragmentImagePicker.pickMultipleWithLocation(this@MposServiceRequestFragment)
        }

        binding.ivShopOutside.setOnClickListener {
            viewModel.imagePickerDocType = MposServiceRequestViewModel.DocType.SHOP_OUTSIDE
            FragmentImagePicker.pickMultipleWithLocation(this@MposServiceRequestFragment)
        }
        binding.ivBusinessLegality.setOnClickListener {
            viewModel.imagePickerDocType = MposServiceRequestViewModel.DocType.BUSINESS_LEGALITY
            FragmentImagePicker.pickMultipleWithLocation(this@MposServiceRequestFragment)
        }
        binding.ivBusinessAddress.setOnClickListener {
            viewModel.imagePickerDocType = MposServiceRequestViewModel.DocType.BUSINESS_ADDRESS
            FragmentImagePicker.pickMultipleWithLocation(this@MposServiceRequestFragment)
        }

        binding.edBusinessAddressType.setOnClickListener {
            spinnerDialogAddressType?.showSpinerDialog()
        }

        binding.edBusinessLegalityType.setOnClickListener {
            spinnerDialogProofType?.showSpinerDialog()
        }

        binding.btnRefresh.setOnClickListener {
            viewModel.fetchInfoAndTypeList()
        }


    }

    private fun validateInputs(): Boolean {
        var isValid = true



        if (viewModel.businessLegalityType.length < 3 && viewModel.businessLegalityTypeStatus == 0) {
            isValid = false
            binding.tilBusinessLegalityType.error = "enter min 3 characters"
            binding.tilBusinessLegalityType.isErrorEnabled = true

        } else binding.tilBusinessLegalityType.isErrorEnabled = false


        if (viewModel.businessAddressType.length < 3 && viewModel.businessAddressTypeStatus == 0) {
            isValid = false
            binding.tilBusinessAddressType.error = "enter min 3 characters"
            binding.tilBusinessAddressType.isErrorEnabled = true

        } else binding.tilBusinessAddressType.isErrorEnabled = false



        if (viewModel.shopInsideFile == null && (viewModel.shopInsideImageStatus == 0 || viewModel.shopInsideImageStatus == 4)) {
            isValid = false
            activity?.showToast("Upload shop inside image")
            return false
        }

        if (viewModel.shopOutsideFile == null && (viewModel.shopOutsideImageStatus == 0 || viewModel.shopOutsideImageStatus == 4)) {
            isValid = false
            activity?.showToast("Upload shop outside image")
            return false
        }
        if (viewModel.businessLegalityFile == null && (viewModel.businessLegalityImageStatus == 0 || viewModel.businessLegalityImageStatus == 4)) {
            isValid = false
            activity?.showToast("Upload business proof image")
            return false
        }
        if (viewModel.businessAddressFile == null && (viewModel.businessAddressImageStatus == 0 || viewModel.businessAddressImageStatus == 4)) {
            isValid = false
            activity?.showToast("Upload business address proof image")
            return false
        }


        return isValid

    }


    private fun subscribeObserver() {

        viewModel.fetchInfoObs.observe(viewLifecycleOwner) {

            fun getFileStatus(value: String?): Int {
                if (value == null) return 0
                if (value.isEmpty()) return 0
                return value.toInt()
            }

            fun getNonFileStatus(value: String?): Int {
                if (value == null) return 0
                if (value.isEmpty()) return 0
                return 1
            }

            when (it) {
                is Resource.Loading -> {
                    binding.progress.show()
                    binding.cvOrderForm.hide()
                    binding.cvMatmOrder.hide()
                }
                is Resource.Success -> {
                    binding.progress.hide()

                    when (it.data.status) {
                        1 -> {
                            if (it.data.data?.service_status == "4") {
                                binding.cvMatmOrder.show()
                                binding.cvOrderForm.hide()
                            } else if (
                                (it.data.data?.pangImageStatus == "1" || it.data.data?.pangImageStatus == "3") &&
                                (it.data.data.addressProofImageStatus == "1" || it.data.data.addressProofImageStatus == "3")
                            ) {
                                binding.cvOrderForm.show()
                                binding.cvMatmOrder.hide()


                                setupSpinnerDialogs()




                                viewModel.businessLegalityImageStatus =
                                    getFileStatus(it.data.data.businessLegalityImageStatus)
                                viewModel.businessAddressImageStatus =
                                    getFileStatus(it.data.data.business_address_proof_image_status)
                                viewModel.shopInsideImageStatus =
                                    getFileStatus(it.data.data.shop_inside_image_status)
                                viewModel.shopOutsideImageStatus =
                                    getFileStatus(it.data.data.shop_outside_image_status)

                                viewModel.businessLegalityType = it.data.data.businessLegalityType ?: ""
                                viewModel.businessAddressType = it.data.data.businessAddressType ?: ""

                                if(viewModel.businessLegalityImageStatus == 4){
                                    viewModel.businessLegalityType = ""
                                }
                                if(viewModel.businessAddressImageStatus == 4){
                                    viewModel.businessAddressType = ""
                                }

                                viewModel.businessAddressTypeStatus = getNonFileStatus(viewModel.businessAddressType)
                                viewModel.businessLegalityTypeStatus = getNonFileStatus(viewModel.businessLegalityType)


                                if (viewModel.businessLegalityImageStatus == 3
                                    || viewModel.businessLegalityImageStatus == 1
                                ) {
                                    binding.ivBusinessLegality.hide()
                                    binding.llBusinessLegality.show()

                                    if (viewModel.businessLegalityImageStatus == 1) {
                                        binding.tvBusinessLegalityApproved.text = "Approved"
                                        binding.tvBusinessLegalityApproved.setupTextColor(R.color.green)
                                    }
                                    binding.llBusinessLegality.setOnClickListener { _ ->
                                        Dialogs.docImageView(
                                            requireActivity(),
                                            it.data.data.business_proof_image!!
                                        )
                                    }
                                }




                                if (viewModel.businessAddressImageStatus == 3
                                    || viewModel.businessAddressImageStatus == 1
                                ) {
                                    binding.ivBusinessAddress.hide()
                                    binding.llBusinessAddress.show()

                                    if (viewModel.businessAddressImageStatus == 1) {
                                        binding.tvBusinessAddress.text = "Approved"
                                        binding.tvBusinessAddress.setupTextColor(R.color.green)
                                    }
                                    binding.llBusinessAddress.setOnClickListener { _ ->
                                        Dialogs.docImageView(
                                            requireActivity(),
                                            it.data.data.business_address_proof_image!!
                                        )
                                    }
                                }

                                if (viewModel.shopInsideImageStatus == 3
                                    || viewModel.shopInsideImageStatus == 1
                                ) {
                                    binding.ivShopInside.hide()
                                    binding.llShopInsdie.show()

                                    if (viewModel.shopInsideImageStatus == 1) {
                                        binding.tvShopInside.text = "Approved"
                                        binding.tvShopInside.setupTextColor(R.color.green)
                                    }
                                    binding.llShopInsdie.setOnClickListener { _ ->
                                        Dialogs.docImageView(
                                            requireActivity(),
                                            it.data.data.shop_inside_image!!
                                        )
                                    }
                                }

                                if (viewModel.shopOutsideImageStatus == 3
                                    || viewModel.shopOutsideImageStatus == 1
                                ) {
                                    binding.ivShopOutside.hide()
                                    binding.llShopOutside.show()

                                    if (viewModel.shopOutsideImageStatus == 1) {
                                        binding.tvShopOutside.text = "Approved"
                                        binding.tvShopOutside.setupTextColor(R.color.green)
                                    }
                                    binding.llShopOutside.setOnClickListener { _ ->
                                        Dialogs.docImageView(
                                            requireActivity(),
                                            it.data.data.shop_outside_image!!
                                        )
                                    }
                                }


                                if (viewModel.businessLegalityType == "") {
                                    binding.tilBusinessLegalityType.show()
                                    viewModel.businessLegalityTypeStatus = 0
                                } else {
                                    binding.tilBusinessLegalityType.hide()
                                    viewModel.businessLegalityTypeStatus = 1
                                }

                                if (viewModel.businessAddressType== "") {
                                    binding.tilBusinessAddressType.show()
                                    viewModel.businessAddressTypeStatus = 0
                                } else {
                                    binding.tilBusinessAddressType.hide()
                                    viewModel.businessAddressTypeStatus = 1
                                }


                                if (
                                    it.data.data.businessLegalityType.toString().isNotEmpty() &&
                                    it.data.data.businessAddressType.toString()
                                        .isNotEmpty() &&
                                    it.data.data.business_proof_image.toString().isNotEmpty() &&
                                    it.data.data.business_address_proof_image.toString()
                                        .isNotEmpty() &&
                                    it.data.data.shop_inside_image.toString().isNotEmpty() &&
                                    it.data.data.shop_outside_image.toString().isNotEmpty() &&
                                    it.data.data.shop_inside_image_status.toString() != "4" &&
                                    it.data.data.shop_outside_image_status.toString() != "4" &&
                                    it.data.data.business_address_proof_image_status.toString() != "4" &&
                                    it.data.data.businessLegalityImageStatus.toString() != "4"
                                ) {
                                    binding.btnProceed.hide()
                                }


                            } else {
                                binding.cvMatmOrder.show()
                                binding.cvOrderForm.hide()
                            }
                        }
                        2 -> {
                            binding.cvMatmOrder.show()
                            binding.cvOrderForm.hide()
                        }


                    }


                }
                is Resource.Failure -> {
                    binding.progress.hide()
                    activity?.handleNetworkFailure(it.exception)
                }
            }
        }
        viewModel.uploadDetailsObs.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    progressDialog = StatusDialog.progress(requireContext())
                }
                is Resource.Success -> {
                    progressDialog?.dismiss()
                    if (it.data.status == 1)
                        StatusDialog.success(requireActivity(), it.data.message) {
                            viewModel.fetchInfoAndTypeList()
                        }
                    else StatusDialog.failure(requireActivity(), it.data.message)
                }
                is Resource.Failure -> {
                    progressDialog?.dismiss()
                    activity?.handleNetworkFailure(it.exception)
                }
            }
        }

    }

    private fun setupSpinnerDialogs() {
        spinnerDialogAddressType =
            SpinnerDialog(
                requireActivity(),
                viewModel.docTypeListResponse!!.businessAddressProofType as ArrayList<String>,
                "Search Address Type"
            )
        spinnerDialogAddressType?.let { spinner ->
            spinner.setCancellable(true)
            spinner.setShowKeyboard(false)
            spinner.bindOnSpinerListener { item, position ->

                viewModel.businessAddressType =
                    viewModel.docTypeListResponse!!.businessAddressProofType!!.find { mValue ->
                        mValue == item
                    } ?: ""
                binding.edBusinessAddressType.setText(viewModel.businessAddressType)
            }
        }

        spinnerDialogProofType =
            SpinnerDialog(
                requireActivity(),
                viewModel.docTypeListResponse!!.businessLegalityProofType as ArrayList<String>,
                "Search Proof Type"
            )
        spinnerDialogProofType?.let { spinner ->
            spinner.setCancellable(true)
            spinner.setShowKeyboard(false)
            spinner.bindOnSpinerListener { item, position ->

                viewModel.businessLegalityType =
                    viewModel.docTypeListResponse!!.businessLegalityProofType!!.find { mValue ->
                        mValue == item
                    } ?: ""

                binding.edBusinessLegalityType.setText(viewModel.businessLegalityType)
            }
        }
    }


    companion object {
        fun newInstance() = MposServiceRequestFragment()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (FragmentImagePicker.mLocation == null) {
            StatusDialog.failure(requireActivity(), "Location didn't fetch, please try again")
            return
        }


        val (address1, address2, address3) =
            LocationService.getAddress(requireContext(), FragmentImagePicker.mLocation!!)


        val file: File =
            FragmentImagePicker.onActivityResult(this, requestCode, resultCode, data) ?: return

        lifecycleScope.launch {
            val dialog = StatusDialog.progress(requireContext(), "Optimizing Image...")

            val optimizeFile: File? = withContext(Dispatchers.IO) {
                file.processForRightAngleImage()
                    .toBitmap()
                    .reduceSize(10)
                    .addWatermark(
                        DateUtil.currentDateInMinuteHourSecond(),
                        /*address1,
                        address2,
                        address3*/
                    ).toFile(context)
            }
            dialog.dismiss()

            optimizeFile?.let {

                AppUtil.logger("file testing : file size :  ${it.sizeInKb}")
                AppUtil.logger("file testing : file name : ${it.name}")
                AppUtil.logger("file testing : file mime type : ${FileUtils.getMimeType(it)}")

                setupSelectedImages(it)
                Dialogs.docImageView(requireActivity(), it.toBitmap())
            } ?: kotlin.run { StatusDialog.failure(requireActivity(), "File not found!") }
        }

    }


}

