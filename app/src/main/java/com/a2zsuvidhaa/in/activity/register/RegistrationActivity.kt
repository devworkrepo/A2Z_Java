package com.a2zsuvidhaa.`in`.activity.register

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.InputFilter
import android.text.InputFilter.AllCaps
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.a2zsuvidhaa.`in`.AppPreference
import com.a2zsuvidhaa.`in`.R
import com.a2zsuvidhaa.`in`.adapter.RoleListAdapter
import com.a2zsuvidhaa.`in`.data.response.RegistrationCommonResponse
import com.a2zsuvidhaa.`in`.data.response.RegistrationRole
import com.a2zsuvidhaa.`in`.data.response.RegistrationRoleUser
import com.a2zsuvidhaa.`in`.databinding.ActivityRegistrationBinding
import com.a2zsuvidhaa.`in`.util.AppConstants
import com.a2zsuvidhaa.`in`.util.AppUitls
import com.a2zsuvidhaa.`in`.util.Utils
import com.a2zsuvidhaa.`in`.util.apis.Resource
import com.a2zsuvidhaa.`in`.util.dialogs.Dialogs
import com.a2zsuvidhaa.`in`.util.dialogs.StatusDialog
import com.a2zsuvidhaa.`in`.util.ents.*
import com.shuhart.stepview.StepView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class RegistrationActivity : AppCompatActivity() {

    @Inject
    lateinit var appPreference: AppPreference

    private val viewModel: RegisterViewModel by viewModels()
    private lateinit var binding: ActivityRegistrationBinding

    private var timer: CountDownTimer? = null
    private var verifyOtpDialog: Dialog? = null
    private var progressDialog: Dialog? = null


    private var dProgress: ProgressBar? = null
    private var dBtnResend: Button? = null
    private var dBtnVerify: Button? = null
    private var dBtnCancel: ImageButton? = null
    private var dTvTimer: TextView? = null
    private var dTvMessage: TextView? = null
    private var dTvWaitingHint: TextView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.isSelfRegistration =
            intent.getBooleanExtra(AppConstants.IS_SELF_REGISTRATION, false)
        viewModel.shouldMapRole = intent.getBooleanExtra(AppConstants.SHOULD_MAP_ROLE, false)
        viewModel.incompleteUserMobile = intent.getStringExtra(AppConstants.DATA)

        val title = if (!viewModel.isSelfRegistration) "Create User" else "Retailer Registration"
        setupToolbar(binding.toolbar, title)


        onEditTextChangeListener()
        showMobileNumberLayout()

        binding.edPanCard.filters = arrayOf(AllCaps(), InputFilter.LengthFilter(10))
        binding.edShopAddress.filters = arrayOf(AllCaps())
        binding.edShopName.filters = arrayOf(AllCaps())

        subscribeObservers()



        binding.stepView.state

            .animationType(StepView.ANIMATION_CIRCLE)
            .selectedCircleRadius(resources.getDimensionPixelSize(R.dimen.dim_20))
            .selectedStepNumberColor(ContextCompat.getColor(this, R.color.white))
            .doneStepMarkColor(ContextCompat.getColor(this, R.color.white))
            .stepsNumber(4)
            .animationDuration(resources.getInteger(android.R.integer.config_shortAnimTime))
            .stepLineWidth(resources.getDimensionPixelSize(R.dimen.dim_1))
            .textSize(resources.getDimensionPixelSize(R.dimen.sp_14))
            .stepNumberTextSize(resources.getDimensionPixelSize(R.dimen.sp_16))
            .commit()


        binding.btnResetPanVerification.setOnClickListener {
            showPanLayout()
        }



        if (!viewModel.isSelfRegistration) {
            binding.svMainContent.hide()
            binding.rlRoleContent.hide()
            viewModel.fetchCreateRoles()

            binding.btnSelectRole.setOnClickListener {
                if (viewModel.shouldMapRole) {
                    if (viewModel.registrationRole.roleId == 7
                        || (appPreference.rollId == 24 && viewModel.registrationRole.roleId == 3)
                    ) {
                        viewModel.mappedUnder = RegistrationRoleUser(
                            id = 1,
                            userDetails = "Excel One Stop Solution Pvt. Ltd.(A 1)",
                            mobile = "+91-925113333",
                            relationId = appPreference.id
                        )
                        binding.svMainContent.show()
                        binding.rlRoleContent.hide()
                        binding.toolbar.subtitle = viewModel.registrationRole.title

                        if (viewModel.incompleteUserMobile != null) {
                            binding.edMobile.setText(viewModel.incompleteUserMobile)
                            viewModel.onProceedButtonClick(null)
                        }
                    } else {
                        val mIntent = Intent(this, RegistrationRoleMapActivity::class.java)
                        mIntent.putExtra("create_role", viewModel.registrationRole.roleId)
                        startActivityForResult(mIntent, 101)
                    }
                } else {
                    binding.svMainContent.show()
                    binding.rlRoleContent.hide()
                    binding.toolbar.subtitle = viewModel.registrationRole.title
                }


            }

        } else {

            binding.svMainContent.show()
            binding.rlRoleContent.hide()
        }


    }

    private fun setupRoleList(roles: List<RegistrationRole>) {


        binding.rlRoleContent.show()
        binding.recyclerView.setup()
            .adapter = RoleListAdapter()
            .apply {
                addItems(roles)
                onSelected = { role: RegistrationRole, selected: Boolean ->
                    viewModel.registrationRole = role
                    if (selected) binding.btnSelectRole.show()
                    else binding.btnSelectRole.hide()
                }
            }
    }

    private fun subscribeObservers() {

        viewModel.fetchCreateRoles.observe(this) {
            when (it) {
                is Resource.Loading -> {
                    progressDialog = StatusDialog.progress(this)
                }
                is Resource.Success -> {
                    progressDialog?.dismiss()
                    if (it.data.status == 1) {

                        if (it.data.roles != null && it.data.roles.isNotEmpty()) {
                            setupRoleList(it.data.roles)
                        } else StatusDialog.alert(this, "Data not found!") {
                            onBackPressed()
                        }

                    } else StatusDialog.alert(this, it.data.message) {
                        onBackPressed()
                    }
                }
                is Resource.Failure -> {
                    progressDialog?.dismiss()
                    handleNetworkFailure(it.exception)
                }
            }
        }


        viewModel.onProceedButtonClickObs.observe(this) {
            onProceedButtonClick(it)
        }

        //mobile number process
        viewModel.onPostMobileNumberObs.observe(this) {
            viewModel.isFinalProcess = false
            onPostMobileResponse(it)
        }
        viewModel.onMobileNumberVerifyObs.observe(this) {
            viewModel.isFinalProcess = false
            onMobileVerifyResponse(it)
        }

        //email process
        viewModel.onPostEmailIdObs.observe(this) {
            viewModel.isFinalProcess = false
            onPostEmailId(it)
        }
        viewModel.onVerifyEmailIdObs.observe(this) {
            viewModel.isFinalProcess = false
            onVerifyEmailId(it)
        }


        //pan process
        viewModel.onPanNumberPostObs.observe(this) {
            viewModel.isFinalProcess = false
            onPostPanNumber(it)
        }

        viewModel.onRegisterObs.observe(this, { onRegisterResponse(it) })
        viewModel.onResendOtp.observe(this, { onResendOtpResponse(it) })
    }

    private fun onRegisterResponse(it: Resource<RegistrationCommonResponse>?) {
        when (it) {
            is Resource.Loading -> {
                progressDialog = StatusDialog.progress(this, "Registering...")
            }

            is Resource.Success -> {
                progressDialog?.dismiss()
                if (it.data.status == 1) {
                    binding.stepView.done(true)
                    StatusDialog.success(this, it.data.message) {
                        gotoLoginActivity()
                    }
                } else StatusDialog.failure(this, it.data.message)
            }

            is Resource.Failure -> {
                progressDialog?.dismiss()
            }
        }
    }

    private fun onPostPanNumber(it: Resource<RegistrationCommonResponse>?) {
        when (it) {
            is Resource.Loading -> {
                progressDialog = StatusDialog.progress(this)
            }

            is Resource.Success -> {
                progressDialog?.dismiss()
                if (it.data.status == 17) {

                    viewModel.isFinalProcess = true
                    viewModel.requestId = it.data.requestId
                    viewModel.mobileNumber = it.data.details.mobile
                    viewModel.emailId = it.data.details.email
                    viewModel.panNumber = it.data.details.pan_card
                    viewModel.name = it.data.details.name

                    binding.run {
                        tvMobile.text = it.data.details.mobile
                        tvEmail.text = it.data.details.email
                        tvPanNumber.text = it.data.details.pan_card
                        tvName.text = it.data.details.name
                    }

                    StatusDialog.success(this, it.data.message) {
                        showFinalRegistrationLayout()
                    }
                } else StatusDialog.failure(this, it.data.message)
            }

            is Resource.Failure -> {
                progressDialog?.dismiss()
                StatusDialog.failure(this, it.exception.message.toString())
            }
        }
    }

    private fun onVerifyEmailId(it: Resource<RegistrationCommonResponse>?) {
        when (it) {
            is Resource.Loading -> {
                verifyOtpDialog?.hide()
                progressDialog = StatusDialog.progress(this, "Verifying...")
            }
            is Resource.Success -> {
                progressDialog?.dismiss()

                when (it.data.status) {
                    3 -> {
                        verifyOtpDialog?.dismiss()
                        viewModel.requestId = it.data.requestId
                        StatusDialog.success(this, it.data.message) { showPanLayout() }
                    }
                    else -> StatusDialog.failure(this, it.data.message) {
                        verifyOtpDialog?.show()
                    }
                }
            }

            is Resource.Failure -> {
                progressDialog?.dismiss()
                verifyOtpDialog?.dismiss()
                StatusDialog.failure(this, it.exception.message.toString())
            }
        }
    }

    private fun onPostEmailId(it: Resource<RegistrationCommonResponse>?) {
        when (it) {
            is Resource.Loading -> {
                progressDialog = StatusDialog.progress(this)
            }
            is Resource.Success -> {
                progressDialog?.dismiss()

                //3. email already verified
                //4. mobile not verified
                //20. otp has sent to your email id

                when (it.data.status) {
                    3 -> {
                        viewModel.requestId = it.data.requestId
                        StatusDialog.success(this, it.data.message) { showPanLayout() }
                    }
                    20 -> handleOtpVerifyDialog(
                        "E",
                        "Otp has sent to you email Id, please check Inbox and Spam mail too. Please enter below"
                    ) {
                        viewModel.requestId = it.data.requestId
                        viewModel.verifyEmailId()

                    }
                    else -> StatusDialog.failure(this, it.data.message)
                }

            }
            is Resource.Failure -> {
                progressDialog?.dismiss()
                StatusDialog.failure(this, it.exception.message.toString())
            }
        }
    }

    private fun onMobileVerifyResponse(it: Resource<RegistrationCommonResponse>?) {
        when (it) {
            is Resource.Loading -> {
                verifyOtpDialog?.hide()
                progressDialog = StatusDialog.progress(this, "Verifying...")
            }
            is Resource.Success -> {
                progressDialog?.dismiss()
                //5 and 9. mobile registration complete, now can proceed for email process
                //6. invalid otp

                when (it.data.status) {
                    5, 9 -> {
                        verifyOtpDialog?.dismiss()
                        viewModel.layoutType = LayoutType.EMAIL
                        viewModel.requestId = it.data.requestId
                        StatusDialog.success(this, it.data.message) { showEmailLayout() }
                    }
                    else -> {
                        StatusDialog.failure(this, it.data.message) {
                            verifyOtpDialog?.show()
                        }
                    }
                }
            }
            is Resource.Failure -> {
                progressDialog?.dismiss()
                verifyOtpDialog?.dismiss()
                StatusDialog.failure(this, it.exception.message.toString())
            }
        }
    }

    private fun onPostMobileResponse(it: Resource<RegistrationCommonResponse>?) {
        when (it) {
            is Resource.Loading -> {
                progressDialog = StatusDialog.progress(this)
            }
            is Resource.Success -> {
                progressDialog?.dismiss()
                //1 show otp dialog for mobile number verification
                //8. show otp dialog for mobile number verification(number already saved not verified)
                //9. show email submit screen
                //10. show otp dialog for email verification
                //11. show pan card submit screen
                //17. show password create screen
                //13. mobile number already registered, try with different one
                when (it.data.status) {

                    1, 8 -> {
                        viewModel.requestId = it.data.requestId
                        handleOtpVerifyDialog(
                            "M",
                            "Otp has sent to your mobile number, please enter below"
                        ) { viewModel.verifyMobileNumber() }
                    }
                    9 -> {
                        viewModel.requestId = it.data.requestId
                        viewModel.mobileNumber = it.data.mobile
                        StatusDialog.success(this, it.data.message) {
                            showEmailLayout()
                        }
                    }
                    10 -> {
                        viewModel.requestId = it.data.requestId
                        viewModel.mobileNumber = it.data.mobile
                        viewModel.emailId = it.data.email_id

                        StatusDialog.success(this, it.data.message) {
                            binding.edEmail.setText(it.data.email_id)
                            showEmailLayout()
                            handleOtpVerifyDialog(
                                "E",
                                "Otp has sent to you email Id, please check Inbox and Spam mail too. Please enter below"
                            ) {
                                viewModel.verifyEmailId()
                            }
                        }

                    }
                    11 -> {
                        viewModel.requestId = it.data.requestId
                        viewModel.mobileNumber = it.data.mobile
                        StatusDialog.success(this, it.data.message) {
                            showPanLayout()

                        }
                    }

                    17 -> {

                        viewModel.isFinalProcess = true
                        viewModel.requestId = it.data.requestId
                        viewModel.mobileNumber = it.data.details.mobile
                        viewModel.emailId = it.data.details.email
                        viewModel.panNumber = it.data.details.pan_card
                        viewModel.name = it.data.details.name

                        binding.run {
                            tvMobile.text = it.data.details.mobile
                            tvEmail.text = it.data.details.email
                            tvPanNumber.text = it.data.details.pan_card
                            tvName.text = it.data.details.name
                        }
                        showFinalRegistrationLayout()
                    }

                    else -> StatusDialog.failure(this, it.data.message)

                }

            }
            is Resource.Failure -> {
                progressDialog?.dismiss()
                StatusDialog.failure(this, it.exception.message.toString())
            }
        }
    }

    private fun onResendOtpResponse(it: Resource<RegistrationCommonResponse>?) {
        when (it) {
            is Resource.Loading -> {
                dProgress?.show()
                dBtnResend?.hide()
                dTvWaitingHint?.hide()
                dTvTimer?.hide()
            }
            is Resource.Success -> {
                dProgress?.hide()
                dBtnResend?.hide()
                dTvWaitingHint?.show()
                dTvTimer?.show()

                when (it.data.status) {
                    1 -> {
                        showToast(it.data.message)
                        countDownTime()
                    }
                    else -> StatusDialog.failure(this, it.data.message)
                }
            }

            is Resource.Failure -> {
                dProgress?.hide()
                dBtnResend?.show()
                dTvWaitingHint?.hide()
                dTvTimer?.hide()
                StatusDialog.failure(this, it.exception.message.toString())
            }
        }
    }

    private inline fun handleOtpVerifyDialog(
        type: String,
        message: String,
        crossinline onSuccess: () -> Unit
    ) {
        verifyOtpDialog = Dialogs.otpVerify(this).apply {

            dProgress = findViewById(R.id.progress)

            dBtnResend = findViewById(R.id.btn_resend)
            dBtnVerify = findViewById(R.id.btn_verify)
            dBtnCancel = findViewById(R.id.btn_cancel)

            dTvTimer = findViewById(R.id.tv_count_down)
            dTvMessage = findViewById(R.id.tv_message)
            dTvWaitingHint = findViewById(R.id.tv_waiting_hint)

            dTvMessage?.text = message

            dBtnVerify?.setOnClickListener {
                viewModel.otp = findViewById<EditText>(R.id.ed_otp).text.toString()
                if (viewModel.otp.length == 6) {
                    onSuccess()
                } else showToast("Enter 6 digit otp")
            }

            dBtnCancel?.setOnClickListener { dismiss() }
            dBtnResend?.setOnClickListener { viewModel.resendOtp(type) }

            countDownTime()

            setOnDismissListener { timer?.cancel() }

        }
    }


    private fun onProceedButtonClick(it: Boolean) {
        if (it) {
            val input: String
            val inputFor: String
            when (viewModel.layoutType) {
                LayoutType.MOBILE_NUMBER -> {
                    input = viewModel.mobileNumber
                    inputFor = "Mobile Number"
                }
                LayoutType.EMAIL -> {
                    input = viewModel.emailId
                    inputFor = "Email ID"
                }
                LayoutType.PAN_CARD -> {
                    input = viewModel.panNumber
                    inputFor = "Pan Card"
                }
                LayoutType.FINAL_REGISTRATION -> {
                    input = ""
                    inputFor = ""
                }
            }

            if (viewModel.isFinalProcess) {
                if (finalRegistrationValidation()) {
                    Dialogs.commonConfirmDialog(
                        this,
                        "You are sure to register with A2Z Suvidhaa services"
                    ).apply {
                        findViewById<Button>(R.id.btn_confirm).setOnClickListener {
                            dismiss()
                            if (!viewModel.isSelfRegistration) {
                                viewModel.registerFromDistributor()
                            } else {
                                viewModel.register()
                            }
                        }
                    }
                }

            } else Dialogs.confirmField(this, input, forInput = inputFor).apply {
                findViewById<Button>(R.id.btn_confirm).setOnClickListener {
                    dismiss()

                    when (viewModel.layoutType) {
                        LayoutType.MOBILE_NUMBER -> viewModel.postMobileNumber()
                        LayoutType.EMAIL -> viewModel.postEmailId()
                        LayoutType.PAN_CARD -> viewModel.postPanNumber()
                        LayoutType.FINAL_REGISTRATION -> {}
                    }
                }
            }

        }
    }

    private fun onEditTextChangeListener() {
        binding.edMobile.afterTextChange {
            if (viewModel.layoutType == LayoutType.MOBILE_NUMBER) {
                viewModel.mobileNumber = it
                confirmButtonVisibility(it.length == 10)
            }
        }
        binding.edEmail.afterTextChange {
            if (viewModel.layoutType == LayoutType.EMAIL) {
                viewModel.emailId = it
                confirmButtonVisibility(AppUitls.isValidEmail(it))
            }
        }

        binding.edPanCard.afterTextChange {
            if (viewModel.layoutType == LayoutType.PAN_CARD) {
                viewModel.panNumber = it
                confirmButtonVisibility(it.length == 10)
            }
        }
    }

    private fun confirmButtonVisibility(visible: Boolean = true) {
        binding.llConfirmButton.apply {
            if (visible) {
                isEnabled = true
                alpha = 1f
            } else {
                isEnabled = false
                alpha = 0.5f
            }
        }
    }

    private fun showMobileNumberLayout() {
        viewModel.layoutType = LayoutType.MOBILE_NUMBER
        binding.apply {
            cvPhoneNumber.show()
            cvEmail.hide()
            cvPanNumber.hide()
            confirmButtonVisibility(edMobile.text.toString().length == 10)
            edMobile.makeFocus()


        }
    }

    private fun showEmailLayout() {
        viewModel.layoutType = LayoutType.EMAIL
        binding.stepView.go(1, true)
        binding.apply {
            cvPhoneNumber.hide()
            cvEmail.show()
            cvPanNumber.hide()

            confirmButtonVisibility(AppUitls.isValidEmail(edEmail.text.toString()))
            edEmail.makeFocus()
        }
    }

    private fun showPanLayout() {
        binding.stepView.go(2, true)
        viewModel.layoutType = LayoutType.PAN_CARD
        binding.apply {
            cvPhoneNumber.hide()
            cvEmail.hide()
            llFinalDetail.hide()
            this@RegistrationActivity.viewModel.isFinalProcess = false
            cvPanNumber.show()
            confirmButtonVisibility(edPanCard.text.toString().length == 10)
            edPanCard.makeFocus()
        }
    }

    private fun showFinalRegistrationLayout() {
        binding.stepView.go(3, true)
        viewModel.layoutType = LayoutType.FINAL_REGISTRATION
        binding.apply {
            cvPhoneNumber.hide()
            cvEmail.hide()
            cvPanNumber.hide()
            llFinalDetail.show()
            confirmButtonVisibility(true)
            edShopName.makeFocus()

        }
    }


    private fun countDownTime() {

        dTvTimer?.show()
        dTvWaitingHint?.show()
        dBtnVerify?.isEnabled = true
        dProgress?.hide()
        dBtnResend?.hide()

        timer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val timerInSecond = (millisUntilFinished / 1000).toInt()
                dTvTimer?.text = timerInSecond.toString()
            }

            override fun onFinish() {
                dTvTimer?.hide()
                dTvWaitingHint?.hide()
                dBtnVerify?.isEnabled = true
                dProgress?.hide()
                dBtnResend?.show()
            }
        }
        timer?.start()
    }

    private fun finalRegistrationValidation(): Boolean {


        if (!Utils.isAlphabeticInput(viewModel.shopName)) {
            binding.tilShopName.apply {
                error = "Shop name should not contain special character"
                isErrorEnabled = true
            }
            return false
        } else binding.tilShopName.isErrorEnabled = false

        if (viewModel.shopName.isEmpty()) {
            binding.tilShopName.apply {
                error = "Shop name can't be empty!"
                isErrorEnabled = true
            }
            return false
        } else binding.tilShopName.isErrorEnabled = false





        if (!Utils.isAlphabeticInput(viewModel.shopAddress)) {
            binding.tilShopAddress.apply {
                error = "Shop address should not contain special character"
                isErrorEnabled = true
            }
            return false
        } else binding.tilShopAddress.isErrorEnabled = false

        if (viewModel.shopAddress.isEmpty()) {
            binding.tilShopAddress.apply {
                error = "Shop address can't be empty!"
                isErrorEnabled = true
            }
            return false
        } else binding.tilShopAddress.isErrorEnabled = false


        if (!Utils.isValidPasswordFormat(viewModel.password)) {
            binding.tilPassword.apply {
                error = "Enter valid password with rules"
                isErrorEnabled = true
                return false
            }
        } else binding.tilPassword.isErrorEnabled = false

        if (viewModel.password != viewModel.confirmPassword || viewModel.confirmPassword.isEmpty()) {
            binding.tilConfirmPassword.apply {
                error = "Confirm password doesn't match with new password"
                isErrorEnabled = true
                return false
            }
        } else binding.tilConfirmPassword.isErrorEnabled = false



        return true


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
            val mData = data?.getParcelableExtra<RegistrationRoleUser>(AppConstants.DATA)
            if (mData != null) {
                viewModel.mappedUnder = mData
                binding.svMainContent.show()
                binding.rlRoleContent.hide()
                binding.toolbar.subtitle = viewModel.registrationRole.title

                if (viewModel.incompleteUserMobile != null) {
                    binding.edMobile.setText(viewModel.incompleteUserMobile)
                    viewModel.onProceedButtonClick(null)
                }
            }

        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}