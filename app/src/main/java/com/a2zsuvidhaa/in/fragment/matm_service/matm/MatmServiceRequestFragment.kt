package com.a2zsuvidhaa.`in`.fragment.matm_service.matm

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.a2zsuvidhaa.`in`.AppPreference
import com.a2zsuvidhaa.`in`.R
import com.a2zsuvidhaa.`in`.databinding.FragmentMatmServiceRequestBinding
import com.a2zsuvidhaa.`in`.fragment.BaseFragment
import com.a2zsuvidhaa.`in`.util.*
import com.a2zsuvidhaa.`in`.util.BitmapUtil.addWatermark
import com.a2zsuvidhaa.`in`.util.BitmapUtil.reduceSize
import com.a2zsuvidhaa.`in`.util.BitmapUtil.toFile
import com.a2zsuvidhaa.`in`.util.apis.Resource
import com.a2zsuvidhaa.`in`.util.dialogs.ConfirmDialog
import com.a2zsuvidhaa.`in`.util.dialogs.Dialogs
import com.a2zsuvidhaa.`in`.util.dialogs.StatusDialog
import com.a2zsuvidhaa.`in`.util.ents.*
import com.a2zsuvidhaa.`in`.util.file.AppFileUtil.processForRightAngleImage
import com.a2zsuvidhaa.`in`.util.file.AppFileUtil.toBitmap
import com.a2zsuvidhaa.`in`.util.file.FragmentImagePicker
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject


@AndroidEntryPoint
class MatmServiceRequestFragment() :
    BaseFragment<FragmentMatmServiceRequestBinding>(R.layout.fragment_matm_service_request) {

    @Inject
    lateinit var appPreference: AppPreference

    @Inject
    lateinit var security: Security

    private val viewModel: MatmServiceRequestViewModel by viewModels()


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        subscribeObserver()

        viewModel.fetchInfo()

        clickHandlers()

        ViewUtil.resetErrorOnTextInputLayout(
            binding.tilMobile,
            binding.tilEmail,
            binding.tilShopName,
            binding.tilShopAddress,
            binding.tilCourierAddress,
            binding.tilPinCode,
            binding.tilGstNumber,
            binding.tilOtp,
            binding.tilLandmark,
            binding.tilCity,
            binding.tilPanNumber,
            binding.tilAadhaarNumber,
            binding.tilName,
        )



        binding.edName.setText(appPreference.name)
        binding.edCity.setText(appPreference.city)
        binding.edAadhaarNumber.setText(appPreference.adhaar)
        binding.edMobile.setText(security.decrypt(appPreference.mobile))
        binding.edEmail.setText(security.decrypt(appPreference.email))
        binding.edShopName.setText(appPreference.shopName)
        binding.edPinCode.setText(appPreference.pincode)
        binding.edShopAddress.setText(appPreference.shopAdress)


        setFieldVisibility(appPreference.name, binding.edName)
        setFieldVisibility(appPreference.mobile, binding.edMobile)
        setFieldVisibility(appPreference.email, binding.edEmail)
        setFieldVisibility(appPreference.shopName, binding.edShopName)
        setFieldVisibility(appPreference.shopAdress, binding.edShopAddress)
        setFieldVisibility(appPreference.city, binding.edCity)
        setFieldVisibility(appPreference.pincode, binding.edPinCode)
        setFieldVisibility(appPreference.pan, binding.edPanNumber)
        setFieldVisibility(appPreference.adhaar, binding.edAadhaarNumber)

        binding.btnEditDetail.setOnClickListener {
            setFieldVisibility(appPreference.name, binding.edName, true)
            setFieldVisibility(appPreference.mobile, binding.edMobile, true)
            setFieldVisibility(appPreference.email, binding.edEmail, true)
            setFieldVisibility(appPreference.shopName, binding.edShopName, true)
            setFieldVisibility(appPreference.shopAdress, binding.edShopAddress, true)
            setFieldVisibility(appPreference.city, binding.edCity, true)
            setFieldVisibility(appPreference.pincode, binding.edPinCode, true)
            setFieldVisibility(appPreference.pan, binding.edPanNumber, true)
            setFieldVisibility(appPreference.adhaar, binding.edAadhaarNumber, true)
        }


    }

    private fun setFieldVisibility(
        name: String,
        edName: TextInputEditText,
        enable: Boolean? = null
    ) {
        if (enable != null) {
            edName.isEnabled = enable
        } else edName.isEnabled = name.isEmpty()
    }


    private fun setupSelectedImages(file: File) {
        val uri = FileUtils.getUri(file)
        when (viewModel.imagePickerDocType) {
            MatmServiceRequestViewModel.DocType.PAN_CARD -> {
                binding.ivPanCard.setImageURI(uri)
                viewModel.panCardFile = file
            }
            MatmServiceRequestViewModel.DocType.ADDRESS_PROOF_IMAGE -> {
                binding.ivAddressProof.setImageURI(uri)
                viewModel.addressProofFile = file
            }
        }
    }

    private fun validateOtpInput(): Boolean {
        viewModel.otp = binding.edOtp.text.toString()
        var validate = false
        if (viewModel.otp.length != 6) {
            binding.tilOtp.apply {
                error = "Enter 6 digits otp"
                isEnabled = true
            }
            validate = false
        } else {
            binding.tilOtp.apply {
                error = ""
                isEnabled = false
            }
            validate = true
        }
        return validate
    }


    private fun clickHandlers() {

        binding.btnRequestOtp.setOnClickListener {
            viewModel.requestOtp()
        }

        binding.btnVerifyOtp.setOnClickListener {
            if (validateOtpInput())
                viewModel.verifyOtp()
        }
        binding.btnProceed.setOnClickListener {
            if (!validateInputs()) return@setOnClickListener
            viewModel.uploadDetails()
        }

        binding.cbIsMatmReceived.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.matmReceivedStatus = 3
                binding.btnProceed.show()
            } else {
                viewModel.matmReceivedStatus = 0
                binding.btnProceed.hide()
            }
        }

        binding.cbAddress.setOnCheckedChangeListener { _, isChecked ->
            viewModel.isAddressCheck = isChecked
            binding.edCourierAddress.isEnabled = !isChecked
        }


        binding.ivPanCard.setOnClickListener {
            viewModel.imagePickerDocType = MatmServiceRequestViewModel.DocType.PAN_CARD
            FragmentImagePicker.pickMultipleWithLocation(this@MatmServiceRequestFragment)
        }

        binding.ivAddressProof.setOnClickListener {
            viewModel.imagePickerDocType = MatmServiceRequestViewModel.DocType.ADDRESS_PROOF_IMAGE
            FragmentImagePicker.pickMultipleWithLocation(this@MatmServiceRequestFragment)
        }

        binding.btnOrderNow.setOnClickListener {

            Dialogs.commonConfirmDialog(
                requireContext(),
                message = "M-ATM purchase confirmation. Once purchased than can not be reverse it."
            ).apply {
                this.findViewById<Button>(R.id.btn_confirm).setOnClickListener {
                    dismiss()
                    viewModel.orderNow()
                }
            }
        }

    }


    private fun subscribeObserver() {

        viewModel.fetchInfoObs.observe(viewLifecycleOwner) {

            fun getStatus(value: String?): Int {
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
                            } else {
                                viewModel.matmInfo = it.data.data

                                binding.cvOrderForm.show()
                                viewModel.panImageStatus =
                                    (it.data.data?.pangImageStatus ?: "0").toInt()
                                viewModel.addressProofImageStatus =
                                    (it.data.data?.addressProofImageStatus ?: "0").toInt()
                                viewModel.shopAddressStatus = getStatus(it.data.data?.shopAddress)
                                viewModel.shopNameStatus = getStatus(it.data.data?.shopName)
                                viewModel.mobileStatus = getStatus(it.data.data?.mobile)
                                viewModel.emailStatus = getStatus(it.data.data?.email)

                                viewModel.nameStatus = getStatus(it.data.data?.name)
                                viewModel.cityStatus = getStatus(it.data.data?.city)
                                viewModel.aadhaarStatus = getStatus(it.data.data?.aadhaarNumber)
                                viewModel.panStatus = getStatus(it.data.data?.panNumber)

                                viewModel.courierAddressStatus =
                                    getStatus(it.data.data?.courierAddress)
                                viewModel.landmarkStatus = getStatus(it.data.data?.landmark)
                                viewModel.pinCodeStatus = getStatus(it.data.data?.pinCode)

                                if (viewModel.courierAddressStatus == 0)
                                    viewModel.shopAddressStatus = 0

                                if (viewModel.landmarkStatus == 1)
                                    binding.tilLandmark.hide()
                                if (viewModel.shopAddressStatus == 1)
                                    binding.tilShopAddress.hide()
                                if (viewModel.shopNameStatus == 1)
                                    binding.tilShopName.hide()
                                if (viewModel.mobileStatus == 1)
                                    binding.tilMobile.hide()
                                if (viewModel.emailStatus == 1)
                                    binding.tilEmail.hide()


                                if (viewModel.nameStatus == 1)
                                    binding.tilName.hide()
                                if (viewModel.cityStatus == 1)
                                    binding.tilCity.hide()
                                if (viewModel.panStatus == 1)
                                    binding.tilPanNumber.hide()
                                if (viewModel.aadhaarStatus == 1)
                                    binding.tilAadhaarNumber.hide()


                                if (viewModel.courierAddressStatus == 1)
                                    binding.tilCourierAddress.hide()
                                if (viewModel.pinCodeStatus == 1) {
                                    binding.tilPinCode.hide()
                                    binding.cbAddress.hide()
                                }

                                if (it.data.data?.remark != null) {
                                    if (it.data.data.remark.isEmpty() || it.data.data.remark != "null") {
                                        binding.tvRemark.text = it.data.data.remark
                                    }
                                }

                                if (viewModel.landmarkStatus == 1
                                    && viewModel.shopAddressStatus == 1
                                    && viewModel.courierAddressStatus == 1
                                    && viewModel.pinCodeStatus == 1
                                    && viewModel.shopNameStatus == 1
                                    && viewModel.mobileStatus == 1
                                    && viewModel.emailStatus == 1
                                    && viewModel.nameStatus == 1
                                    && viewModel.aadhaarStatus == 1
                                    && viewModel.panStatus == 1
                                    && viewModel.cityStatus == 1
                                ) {
                                    binding.btnEditDetail.hide()
                                    binding.tilGstNumber.hide()

                                    if (
                                        (viewModel.panImageStatus == 3 || viewModel.panImageStatus == 1)
                                        && (viewModel.addressProofImageStatus == 3 || viewModel.addressProofImageStatus == 1)
                                    ) {
                                        binding.btnProceed.hide()
                                    }
                                }



                                if (viewModel.panImageStatus == 3
                                    || viewModel.panImageStatus == 1
                                ) {
                                    binding.ivPanCard.hide()
                                    binding.llPanCard.show()

                                    if (viewModel.panImageStatus == 1) {
                                        binding.tvPanApproved.text = "Approved"
                                        binding.tvPanApproved.setupTextColor(R.color.green)
                                    }
                                    binding.llPanCard.setOnClickListener { _ ->
                                        Dialogs.docImageView(
                                            requireActivity(),
                                            it.data.data?.panImage!!
                                        )
                                    }
                                }


                                if (viewModel.addressProofImageStatus == 3
                                    || viewModel.addressProofImageStatus == 1
                                ) {
                                    binding.ivAddressProof.hide()
                                    binding.llAddressProofPhoto.show()

                                    if (viewModel.addressProofImageStatus == 1) {
                                        binding.tvAddressProofApproved.text = "Approved"
                                        binding.tvAddressProofApproved.setupTextColor(R.color.green)
                                    }
                                    binding.llAddressProofPhoto.setOnClickListener { _ ->
                                        Dialogs.docImageView(
                                            requireActivity(),
                                            it.data.data?.addressProofImage!!
                                        )
                                    }
                                }


                                if (
                                    viewModel.panImageStatus == 4
                                    || viewModel.panImageStatus == 1
                                    || viewModel.addressProofImageStatus == 4
                                    || viewModel.addressProofImageStatus == 1
                                ) {
                                    binding.tvRemark.show()
                                }

                                if (
                                    viewModel.panImageStatus == 1 &&
                                    viewModel.addressProofImageStatus == 1 &&
                                    it.data.data?.service_status == "1"
                                ) {

                                    if (it.data.data.matmServiceStatus == "0") {

                                        if (it.data.data.isMatmReceived == "0") {
                                            binding.llIsMatmReceived.show()
                                        } else {
                                            binding.llIsMatmReceived.hide()

                                            binding.tvAppliedMessage.apply {
                                                text =
                                                    "Applied successfully. Your request in pending, please wait for approval. "
                                                setupTextColor(R.color.yellow_dark)
                                                show()
                                            }
                                        }

                                    } else if (it.data.data.matmServiceStatus == "1") {
                                        binding.tvAppliedMessage.apply {
                                            text =
                                                "Application for purchase matm approved. Please do transactions. Thank you"
                                            setupTextColor(R.color.green)
                                            show()
                                        }
                                    }
                                }

                                if (viewModel.panImageStatus == 3
                                    && viewModel.addressProofImageStatus == 3
                                    && it.data.data?.otpVerify == "0"
                                ) {
                                    binding.llVerify.show()
                                } else binding.llVerify.hide()
                            }

                        }
                        2 -> {
                            binding.cvMatmOrder.show()
                        }
                        else -> StatusDialog.failure(requireActivity(), it.data.message) {
                            activity?.onBackPressed()
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
                            viewModel.fetchInfo()
                        }
                    else StatusDialog.failure(requireActivity(), it.data.message)
                }
                is Resource.Failure -> {
                    progressDialog?.dismiss()
                    activity?.handleNetworkFailure(it.exception)
                }
            }
        }

        viewModel.orderNowObs.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    progressDialog = StatusDialog.progress(requireContext())
                }
                is Resource.Success -> {
                    progressDialog?.dismiss()
                    if (it.data.status == 1)
                        StatusDialog.success(requireActivity(), it.data.message) {
                            viewModel.fetchInfo()
                        }
                    else StatusDialog.failure(requireActivity(), it.data.message)
                }
                is Resource.Failure -> {
                    progressDialog?.dismiss()
                    activity?.handleNetworkFailure(it.exception)
                }
            }
        }

        viewModel.requestOtpObs.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    progressDialog = StatusDialog.progress(requireContext())
                }
                is Resource.Success -> {
                    progressDialog?.dismiss()
                    if (it.data.status == 1) {
                        activity?.showToast(it.data.message)
                        binding.btnRequestOtp.hide()
                        binding.llVerifyOtp.show()
                    } else StatusDialog.failure(requireActivity(), it.data.message)
                }
                is Resource.Failure -> {
                    progressDialog?.dismiss()
                    activity?.handleNetworkFailure(it.exception)
                }
            }
        }

        viewModel.verifyOtpObs.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    progressDialog = StatusDialog.progress(requireContext())
                }
                is Resource.Success -> {
                    progressDialog?.dismiss()
                    if (it.data.status == 1)
                        StatusDialog.success(requireActivity(), it.data.message) {
                            viewModel.fetchInfo()
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


    private fun validateInputs(): Boolean {
        var isValid = true

        viewModel.name = viewModel.matmInfo?.name ?: binding.edName.text.toString()
        viewModel.pan = viewModel.matmInfo?.panNumber ?: binding.edPanNumber.text.toString()
        viewModel.city = viewModel.matmInfo?.city ?: binding.edCity.text.toString()
        viewModel.aadhaar =
            viewModel.matmInfo?.aadhaarNumber ?: binding.edAadhaarNumber.text.toString()
        viewModel.mobileNumber = viewModel.matmInfo?.mobile ?: binding.edMobile.text.toString()
        viewModel.emailId = viewModel.matmInfo?.email ?: binding.edEmail.text.toString()
        viewModel.gstNumber = viewModel.matmInfo?.gstNumber ?: binding.edGstNumber.text.toString()
        viewModel.shopAddress =
            viewModel.matmInfo?.shopAddress ?: binding.edShopAddress.text.toString()
        viewModel.shopName =
            viewModel.matmInfo?.shopName ?: binding.edShopName.text.toString()
        viewModel.landmark = viewModel.matmInfo?.landmark ?: binding.edLandmark.text.toString()
        viewModel.pinCode = viewModel.matmInfo?.pinCode ?: binding.edPinCode.text.toString()
        viewModel.courierAddress =
            viewModel.matmInfo?.courierAddress ?: binding.edCourierAddress.text.toString()

        if (viewModel.isAddressCheck) {
            viewModel.courierAddress =
                viewModel.shopAddress + ", landmark: " + viewModel.landmark + ", pin code: " + viewModel.pinCode
        }

        if (viewModel.shopAddress.length < 3 && viewModel.shopAddressStatus == 0) {
            isValid = false
            binding.tilShopAddress.error = "enter min 3 characters"
            binding.tilShopAddress.isErrorEnabled = true

        } else binding.tilShopAddress.isErrorEnabled = false


        if (viewModel.name.length < 3 && viewModel.nameStatus == 0) {
            isValid = false
            binding.tilName.error = "Enter valid name"
            binding.tilName.isErrorEnabled = true

        } else {
            binding.tilName.isErrorEnabled = false
        }


        if (viewModel.city.length < 3 && viewModel.cityStatus == 0) {
            isValid = false
            binding.tilCity.error = "Enter valid city name"
            binding.tilCity.isErrorEnabled = true

        } else {
            binding.tilCity.isErrorEnabled = false
        }

        if (viewModel.pan.length != 10 && viewModel.panStatus == 0) {
            isValid = false
            binding.tilPanNumber.error = "Enter valid pan number"
            binding.tilPanNumber.isErrorEnabled = true

        } else {
            binding.tilPanNumber.isErrorEnabled = false
        }

        if (viewModel.aadhaar.length != 12 && viewModel.aadhaarStatus == 0) {
            isValid = false
            binding.tilAadhaarNumber.error = "Enter valid aadhaar number"
            binding.tilAadhaarNumber.isErrorEnabled = true

        } else {
            binding.tilAadhaarNumber.isErrorEnabled = false
        }




        if (viewModel.mobileNumber.length != 10 && viewModel.mobileStatus == 0) {
            isValid = false
            binding.tilMobile.error = "enter 10 digits mobile number"
            binding.tilMobile.isErrorEnabled = true

        } else {
            binding.tilMobile.isErrorEnabled = false
        }

        if (viewModel.emailId.length < 3 && viewModel.emailStatus == 0) {
            isValid = false
            binding.tilEmail.error = "enter valid email"
            binding.tilEmail.isErrorEnabled = true

        } else binding.tilEmail.isErrorEnabled = false

        if (viewModel.shopName.length < 3 && viewModel.shopNameStatus == 0) {
            isValid = false
            binding.tilShopName.error = "enter min 3 characters"
            binding.tilShopName.isErrorEnabled = true

        } else binding.tilShopName.isErrorEnabled = false


        if (viewModel.landmark.length < 3 && viewModel.landmarkStatus == 0) {
            isValid = false
            binding.tilLandmark.error = "enter min 3 characters"
            binding.tilLandmark.isErrorEnabled = true

        } else binding.tilLandmark.isErrorEnabled = false


        if (viewModel.pinCode.length != 6 && viewModel.pinCodeStatus == 0) {
            isValid = false
            binding.tilPinCode.error = "enter 6 characters"
            binding.tilPinCode.isErrorEnabled = true

        } else binding.tilPinCode.isErrorEnabled = false



        if (viewModel.courierAddress.length < 3 && !viewModel.isAddressCheck && viewModel.courierAddressStatus == 0) {
            isValid = false
            binding.tilCourierAddress.error = "enter min 3 characters"
            binding.tilCourierAddress.isErrorEnabled = true

        } else binding.tilCourierAddress.isErrorEnabled = false


        if (viewModel.panCardFile == null && (viewModel.panImageStatus == 0 || viewModel.panImageStatus == 4)) {
            isValid = false
            activity?.showToast("Upload Pan Card")
        }

        if (viewModel.addressProofFile == null && (viewModel.addressProofImageStatus == 0 || viewModel.addressProofImageStatus == 4)) {
            isValid = false
            activity?.showToast("Upload Address Proof")
        }


        return isValid


    }


    companion object {
        fun newInstance() = MatmServiceRequestFragment()
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

