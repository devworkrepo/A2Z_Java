package com.a2zsuvidhaa.`in`.fragment.document_kyc

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.a2zsuvidhaa.`in`.R
import com.a2zsuvidhaa.`in`.activity.MainActivity
import com.a2zsuvidhaa.`in`.activity.eye_blink.EyeBlinkCameraActivity
import com.a2zsuvidhaa.`in`.data.response.CommonResponse
import com.a2zsuvidhaa.`in`.data.response.DocumentKycDetail
import com.a2zsuvidhaa.`in`.data.response.DocumentKycResponse
import com.a2zsuvidhaa.`in`.databinding.FragmentDocumentKycBinding
import com.a2zsuvidhaa.`in`.fragment.BaseFragment
import com.a2zsuvidhaa.`in`.fragment.document_kyc.DocumentKycViewModel.DocType
import com.a2zsuvidhaa.`in`.listener.KycRequiredListener
import com.a2zsuvidhaa.`in`.util.*
import com.a2zsuvidhaa.`in`.util.BitmapUtil.addWatermark
import com.a2zsuvidhaa.`in`.util.BitmapUtil.reduceSize
import com.a2zsuvidhaa.`in`.util.BitmapUtil.rotatePortrait
import com.a2zsuvidhaa.`in`.util.apis.Resource
import com.a2zsuvidhaa.`in`.util.dialogs.AepsDialogs
import com.a2zsuvidhaa.`in`.util.dialogs.Dialogs
import com.a2zsuvidhaa.`in`.util.dialogs.StatusDialog
import com.a2zsuvidhaa.`in`.util.ents.*
import com.a2zsuvidhaa.`in`.util.BitmapUtil.toFile
import com.a2zsuvidhaa.`in`.util.file.AppFileUtil.processForRightAngleImage
import com.a2zsuvidhaa.`in`.util.file.AppFileUtil.toBitmap
import com.a2zsuvidhaa.`in`.util.file.FragmentImagePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


@AndroidEntryPoint
class FragmentDocumentKyc() :
    BaseFragment<FragmentDocumentKycBinding>(R.layout.fragment_document_kyc) {


    private val viewModel: DocumentKycViewModel by viewModels()


    private lateinit var uploadedFileDetails: DocumentKycDetail


    private var kycRequiredListener: KycRequiredListener? = null
    private var userId: String? = null


    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (origin == "main_activity")
            kycRequiredListener = activity as MainActivity

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userId = arguments?.getString("user_id")
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.registerUserId = userId
        subscribeObserver()

        viewModel.getUploadedFilesDetails()

        onPickImageClickListener()

        uploadButtonClick()

    }

    private fun uploadButtonClick() {
        binding.run {
            btnUploadPan.setOnClickListener {
                viewModel.fileUploadDocType = DocType.PAN
                viewModel.uploadFiles()
            }

            btnUploadProfile.setOnClickListener {
                viewModel.fileUploadDocType = DocType.PROFILE_IMAGE
                viewModel.uploadFiles()
            }

            btnUploadAadhaarFront.setOnClickListener {
                viewModel.fileUploadDocType = DocType.AADHHAAR_FRONT
                viewModel.uploadFiles()
            }

            btnUploadAadhaarBack.setOnClickListener {
                viewModel.fileUploadDocType = DocType.AADHAAR_BACK
                viewModel.uploadFiles()
            }

            btnUploadShopImage.setOnClickListener {
                viewModel.fileUploadDocType = DocType.SHOP_IMAGE
                viewModel.uploadFiles()
            }

            btnUploadCancelCheque.setOnClickListener {
                viewModel.fileUploadDocType = DocType.CANCEL_CHEQUE
                viewModel.uploadFiles()
            }

            btnUploadSealCheque.setOnClickListener {
                viewModel.fileUploadDocType = DocType.SEAL_CHEQUE
                viewModel.uploadFiles()
            }

            btnUploadGst.setOnClickListener {
                viewModel.fileUploadDocType = DocType.GST_IMAGE
                viewModel.uploadFiles()
            }
        }
    }

    private fun onPickImageClickListener() {
        binding.run {
            rlPan.setOnClickListener {

                if (uploadedFileDetails.pan_card_image_status == "0") {
                    viewModel.imagePickerDocType = DocType.PAN
                    //showImageFileChooserDialog()
                    FragmentImagePicker.pickMultipleWithLocation(this@FragmentDocumentKyc)
                } else {
                    showImageDialog(uploadedFileDetails.pan_card_image)
                }


            }
            rlProfilePhoto.setOnClickListener {

                if (uploadedFileDetails.profile_image_status == "0") {
                    viewModel.imagePickerDocType = DocType.PROFILE_IMAGE
                    PermissionHandler2.checkCameraStorageAndLocationPermission(this@FragmentDocumentKyc) {

                        if (FragmentImagePicker.mLocation != null) {
                            startActivityForResult(
                                Intent(requireContext(), EyeBlinkCameraActivity::class.java),
                                EYE_BLINK_CAMERA_INTENT_CODE
                            )
                        } else {
                            LocationService.fetchLocation(requireContext()) {
                                FragmentImagePicker.mLocation = it
                                startActivityForResult(
                                    Intent(requireContext(), EyeBlinkCameraActivity::class.java),
                                    EYE_BLINK_CAMERA_INTENT_CODE
                                )
                            }
                        }


                    }
                } else {
                    showImageDialog(uploadedFileDetails.profile_picture)
                }
            }
            rlAadhhaarFront.setOnClickListener {

                if (uploadedFileDetails.aadhaar_front_image_status == "0") {

                    viewModel.imagePickerDocType = DocType.AADHHAAR_FRONT
                    //showImageFileChooserDialog()
                    FragmentImagePicker.pickMultipleWithLocation(this@FragmentDocumentKyc)
                } else {
                    showImageDialog(uploadedFileDetails.aadhaar_card_image)
                }


            }
            rlAadhhaarBack.setOnClickListener {

                if (uploadedFileDetails.aadhaar_back_image_status == "0") {

                    viewModel.imagePickerDocType = DocType.AADHAAR_BACK
                    //showImageFileChooserDialog()
                    FragmentImagePicker.pickMultipleWithLocation(this@FragmentDocumentKyc)
                } else {
                    showImageDialog(uploadedFileDetails.aadhaar_img_back)
                }


            }
            rlShopImage.setOnClickListener {

                if (uploadedFileDetails.shop_image_status == "0") {

                    viewModel.imagePickerDocType = DocType.SHOP_IMAGE
                    // showImageFileChooserDialog()
                    FragmentImagePicker.pickMultipleWithLocation(this@FragmentDocumentKyc)
                } else {
                    showImageDialog(uploadedFileDetails.shop_image)
                }


            }
            rlCancelCheque.setOnClickListener {

                if (uploadedFileDetails.cheque_image_status == "0") {

                    viewModel.imagePickerDocType = DocType.CANCEL_CHEQUE
                    //showImageFileChooserDialog()
                    FragmentImagePicker.pickMultipleWithLocation(this@FragmentDocumentKyc)
                } else {
                    showImageDialog(uploadedFileDetails.cheque_image)
                }


            }
            rlSealCheque.setOnClickListener {

                if (uploadedFileDetails.seal_image_status == "0") {


                    viewModel.imagePickerDocType = DocType.SEAL_CHEQUE
                    //showImageFileChooserDialog()
                    FragmentImagePicker.pickMultipleWithLocation(this@FragmentDocumentKyc)
                } else {
                    showImageDialog(uploadedFileDetails.seal_image)
                }


            }
            rlGstImage.setOnClickListener {

                if (uploadedFileDetails.gst_image_status == "0") {
                    viewModel.imagePickerDocType = DocType.GST_IMAGE
                    //showImageFileChooserDialog()
                    FragmentImagePicker.pickMultipleWithLocation(this@FragmentDocumentKyc)
                } else {
                    showImageDialog(uploadedFileDetails.gst_image)
                }


            }
        }
    }

    private fun showImageDialog(strImage: String) {
        Dialogs.docImageView(requireActivity(), strImage)
    }


    private fun subscribeObserver() {
        viewModel.onUploadedFilesDetailsObs.observe(
            viewLifecycleOwner,
            { onUploadedFilesDetailsResponse(it) })
        viewModel.onFileUploadedObs.observe(viewLifecycleOwner, { onFileUploadResponse(it) })

    }

    private fun onFileUploadResponse(it: Resource<CommonResponse>?) {
        when (it) {
            is Resource.Loading -> {
                progressDialog = StatusDialog.progress(requireActivity(), "Uploading...")
            }
            is Resource.Success -> {
                progressDialog?.dismiss()

                if (it.data.status == 1)
                    StatusDialog.success(
                        requireActivity(),
                        it.data.message
                    ) { viewModel.getUploadedFilesDetails() }
                else StatusDialog.failure(requireActivity(), it.data.message)
            }

            is Resource.Failure -> {
                progressDialog?.dismiss()
                StatusDialog.failure(requireActivity(), it.exception.message.toString())
            }
        }
    }

    private fun onUploadedFilesDetailsResponse(it: Resource<DocumentKycResponse>?) {
        when (it) {
            is Resource.Loading -> {
                binding.also {
                    it.cardView.hide()
                    it.progress.show()
                }
            }
            is Resource.Success -> {

                binding.run {
                    progress.hide()
                    cardView.show()
                    if (it.data.status == 1) {

                        val panKyc = it.data.data.is_pan_verified
                        val aadhaarKyc = it.data.data.is_aadhaar_kyc
                        val aepsKyc = it.data.data.aeps_kyc

                        AppLog.d(aepsKyc.toString())

                        fun getDescription(value: String) =
                            "$value kyc is required first for document or video kyc"

                        when {
                            panKyc == 0 || panKyc == 4 -> {
                                AepsDialogs.kycRequired(
                                    context = requireActivity(),
                                    title = "Pan Kyc Required!",
                                    description = getDescription("Pan")
                                ).setOnDismissListener {
                                    activity?.onBackPressed()
                                }
                            }
                            aadhaarKyc == 0 || aadhaarKyc == 4 -> {
                                AepsDialogs.kycRequired(
                                    context = requireActivity(),
                                    title = "Aadhaar Kyc Required!",
                                    description = getDescription("Aadhaar")
                                ).setOnDismissListener {
                                    kycRequiredListener?.onAadhaarKycRequired()
                                }
                            }
                            aepsKyc == 0 || aepsKyc == 4 -> {
                                AepsDialogs.kycRequired(
                                    context = requireActivity(),
                                    title = "Aeps Kyc Required!",
                                    description = getDescription("Aeps")
                                ).setOnDismissListener {
                                    activity?.onBackPressed()
                                }
                            }
                        }

                        setupDocumentView(it.data.data)
                    } else StatusDialog.failure(requireActivity(), it.data.message)
                        .setOnDismissListener {
                            activity?.onBackPressed()
                        }
                }
            }
            is Resource.Failure -> {
                binding.run {
                    progress.hide()
                }
                StatusDialog.failure(requireActivity(), it.exception.message.toString())
                    .setOnDismissListener {
                        activity?.onBackPressed()
                    }
            }
        }
    }

    private fun setupDocumentView(data: DocumentKycDetail) {
        uploadedFileDetails = data
        binding.run {

            //pan image
            when (data.pan_card_image_status) {
                "0" -> {
                    llPanCard.hide()
                    ivPanCard.show()

                }
                "1" -> {
                    llPanCard.show()
                    ivPanCard.hide()
                    tvPanApproved.text = "Approved"
                    tvPanApproved.setupTextColor(R.color.green)
                    btnUploadPan.setupVisibility(false)
                }
                "3" -> {
                    llPanCard.show()
                    ivPanCard.hide()
                    tvPanApproved.text = "Scanning..."
                    tvPanApproved.setupTextColor(R.color.yellow_dark)
                    btnUploadPan.setupVisibility(false)
                }
            }

            //shop image
            when (data.profile_image_status) {
                "0" -> {
                    llProfilePhoto.hide()
                    ivProfilePhoto.show()

                }
                "1" -> {
                    llProfilePhoto.show()
                    ivProfilePhoto.hide()
                    tvProfileApproved.text = "Approved"
                    tvProfileApproved.setupTextColor(R.color.green)
                    btnUploadProfile.setupVisibility(false)
                }
                "3" -> {
                    llProfilePhoto.show()
                    ivProfilePhoto.hide()
                    tvProfileApproved.text = "Scanning..."
                    tvProfileApproved.setupTextColor(R.color.yellow_dark)
                    btnUploadProfile.setupVisibility(false)
                }
            }

            // aadhaar front image
            when (data.aadhaar_front_image_status) {
                "0" -> {
                    llAadhaarFront.hide()
                    ivAadhaarFront.show()

                }
                "1" -> {
                    llAadhaarFront.show()
                    ivAadhaarFront.hide()
                    tvAadhaarFrontApproved.text = "Approved"
                    tvAadhaarFrontApproved.setupTextColor(R.color.green)
                    btnUploadAadhaarFront.setupVisibility(false)
                }
                "3" -> {
                    llAadhaarFront.show()
                    ivAadhaarFront.hide()
                    tvAadhaarFrontApproved.text = "Scanning..."
                    tvAadhaarFrontApproved.setupTextColor(R.color.yellow_dark)
                    btnUploadAadhaarFront.setupVisibility(false)
                }
            }

            // aadhaar back image
            when (data.aadhaar_back_image_status) {
                "0" -> {
                    llAadhaarBack.hide()
                    ivAadhaarBack.show()

                }
                "1" -> {
                    llAadhaarBack.show()
                    ivAadhaarBack.hide()
                    tvAadhaarBackApproved.text = "Approved"
                    tvAadhaarBackApproved.setupTextColor(R.color.green)
                    btnUploadAadhaarBack.setupVisibility(false)
                }
                "3" -> {
                    llAadhaarBack.show()
                    ivAadhaarBack.hide()
                    tvAadhaarBackApproved.text = "Scanning..."
                    tvAadhaarBackApproved.setupTextColor(R.color.yellow_dark)
                    btnUploadAadhaarBack.setupVisibility(false)
                }
            }

            // cancel cheque
            when (data.cheque_image_status) {
                "0" -> {
                    llCancelCheque.hide()
                    ivCancelCheque.show()

                }
                "1" -> {
                    llCancelCheque.show()
                    ivCancelCheque.hide()
                    tvCancelChequeApproved.text = "Approved"
                    tvCancelChequeApproved.setupTextColor(R.color.green)
                    btnUploadCancelCheque.setupVisibility(false)
                }
                "3" -> {
                    llCancelCheque.show()
                    ivCancelCheque.hide()
                    tvCancelChequeApproved.text = "Scanning..."
                    tvCancelChequeApproved.setupTextColor(R.color.yellow_dark)
                    btnUploadCancelCheque.setupVisibility(false)
                }
            }

            // seal cheque
            when (data.seal_image_status) {
                "0" -> {
                    llSealCheque.hide()
                    ivSealCheque.show()
                }
                "1" -> {
                    llSealCheque.show()
                    ivSealCheque.hide()
                    tvSealChequeApproved.text = "Approved"
                    tvSealChequeApproved.setupTextColor(R.color.green)
                    btnUploadSealCheque.setupVisibility(false)
                }
                "3" -> {
                    llSealCheque.show()
                    ivSealCheque.hide()
                    tvSealChequeApproved.text = "Scanning..."
                    tvSealChequeApproved.setupTextColor(R.color.yellow_dark)
                    btnUploadSealCheque.setupVisibility(false)
                }
            }

            //shop image
            when (data.shop_image_status) {
                "0" -> {
                    llShopImage.hide()
                    ivShopImage.show()
                }
                "1" -> {
                    llShopImage.show()
                    ivShopImage.hide()
                    tvShopApproved.text = "Approved"
                    tvShopApproved.setupTextColor(R.color.green)
                    btnUploadShopImage.setupVisibility(false)
                }
                "3" -> {
                    llShopImage.show()
                    ivShopImage.hide()
                    tvShopApproved.text = "Scanning..."
                    tvShopApproved.setupTextColor(R.color.yellow_dark)
                    btnUploadShopImage.setupVisibility(false)
                }
            }

            //gst image
            when (data.gst_image_status) {
                "0" -> {
                    llGstImage.hide()
                    ivGstImage.show()
                }
                "1" -> {
                    llGstImage.show()
                    ivGstImage.hide()
                    tvGstApproved.text = "Approved"
                    tvGstApproved.setupTextColor(R.color.green)
                    btnUploadGst.setupVisibility(false)
                }
                "3" -> {
                    llGstImage.show()
                    ivGstImage.hide()
                    tvGstApproved.text = "Scanning..."
                    tvGstApproved.setupTextColor(R.color.yellow_dark)
                    btnUploadGst.setupVisibility(false)
                }
            }
        }
    }

    companion object {

        lateinit var origin: String

        fun newInstance(origin: String, userId: String?=null): FragmentDocumentKyc {
            this.origin = origin
            return FragmentDocumentKyc().apply {
                arguments = bundleOf(
                    "user_id" to userId
                )
            }
        }

        const val EYE_BLINK_CAMERA_INTENT_CODE = 10001
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (FragmentImagePicker.mLocation == null) {
            StatusDialog.failure(requireActivity(), "Location didn't fetch, please try again")
            return
        }


        val (address1, address2, address3) =
            LocationService.getAddress(requireContext(), FragmentImagePicker.mLocation!!)


        if (requestCode == EYE_BLINK_CAMERA_INTENT_CODE) {
            val filePath = data?.getStringExtra("file_path") ?: return
            val file = File(filePath)
            lifecycleScope.launch {
                val dialog = StatusDialog.progress(requireContext(), "Optimizing Image...")
                val mFile = withContext(Dispatchers.IO) {
                    file.processForRightAngleImage()
                        .toBitmap()
                        .rotatePortrait()
                        .reduceSize(15)
                        .addWatermark(
                            DateUtil.currentDateInMinuteHourSecond(),
                            address1,
                            address2,
                            address3
                        ).toFile(requireContext())
                }
                dialog.dismiss()

                if (mFile == null) {
                    StatusDialog.failure(requireActivity(), "File not found!")
                    return@launch
                }

                Dialogs.docImageView(requireActivity(), mFile.toBitmap())
                setupSelectedImages(mFile)
            }

        } else {
            val file: File =
                FragmentImagePicker.onActivityResult(this, requestCode, resultCode, data) ?: return

            lifecycleScope.launch {
                val dialog = StatusDialog.progress(requireContext(), "Optimizing Image...")

                val optimizeFile: File? = withContext(Dispatchers.IO) {
                    file.processForRightAngleImage()
                        .toBitmap()
                        .rotatePortrait()
                        .reduceSize(10)
                        .addWatermark(
                            DateUtil.currentDateInMinuteHourSecond(),
                            address1,
                            address2,
                            address3
                        ).toFile(context)
                }
                dialog.dismiss()

                optimizeFile?.let {
                    AppUtil.logger("File Testing : ${it.sizeInKb}")
                    Dialogs.docImageView(requireActivity(), it.toBitmap())
                    setupSelectedImages(it)
                } ?: kotlin.run { StatusDialog.failure(requireActivity(), "File not found!") }
            }
        }
    }


    private fun setupSelectedImages(file: File) {

        val uri = FileUtils.getUri(file)


        when (viewModel.imagePickerDocType) {
            DocType.PAN -> {
                binding.ivPanCard.setImageURI(uri)
                binding.btnUploadPan.setupVisibility()
                viewModel.panFile = file
            }
            DocType.PROFILE_IMAGE -> {
                binding.ivProfilePhoto.setImageURI(uri)
                binding.btnUploadProfile.setupVisibility()
                viewModel.profileFile = file
            }
            DocType.AADHHAAR_FRONT -> {
                binding.ivAadhaarFront.setImageURI(uri)
                binding.btnUploadAadhaarFront.setupVisibility()
                viewModel.aadhaarFrontFile = file
            }
            DocType.AADHAAR_BACK -> {
                binding.ivAadhaarBack.setImageURI(uri)
                binding.btnUploadAadhaarBack.setupVisibility()
                viewModel.aadhaarBackFile = file
            }
            DocType.SHOP_IMAGE -> {
                binding.ivShopImage.setImageURI(uri)
                binding.btnUploadShopImage.setupVisibility()
                viewModel.shopFile = file
            }
            DocType.CANCEL_CHEQUE -> {
                binding.ivCancelCheque.setImageURI(uri)
                binding.btnUploadCancelCheque.setupVisibility()
                viewModel.cancelChequeFile = file
            }
            DocType.SEAL_CHEQUE -> {
                binding.ivSealCheque.setImageURI(uri)
                binding.btnUploadSealCheque.setupVisibility()
                viewModel.sealChequeFile = file
            }
            DocType.GST_IMAGE -> {
                binding.ivGstImage.setImageURI(uri)
                binding.btnUploadGst.setupVisibility()
                viewModel.gstFile = file
            }
        }
    }


}

