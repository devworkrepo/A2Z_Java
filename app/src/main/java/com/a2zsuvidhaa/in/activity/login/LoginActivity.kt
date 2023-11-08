package com.a2zsuvidhaa.`in`.activity.login


import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.a2zsuvidhaa.`in`.AppPreference
import com.a2zsuvidhaa.`in`.BuildConfig
import com.a2zsuvidhaa.`in`.R
import com.a2zsuvidhaa.`in`.activity.ForgetPasswordActivity
import com.a2zsuvidhaa.`in`.activity.MainActivity
import com.a2zsuvidhaa.`in`.activity.login_id.forgot.ForgotLoginIdActivity
import com.a2zsuvidhaa.`in`.activity.register.RegistrationActivity
import com.a2zsuvidhaa.`in`.databinding.ActivityLogin2Binding
import com.a2zsuvidhaa.`in`.firebase.FirebaseService
import com.a2zsuvidhaa.`in`.util.*
import com.a2zsuvidhaa.`in`.util.apis.Resource
import com.a2zsuvidhaa.`in`.util.dialogs.StatusDialog
import com.a2zsuvidhaa.`in`.util.ents.handleNetworkFailure
import com.a2zsuvidhaa.`in`.util.ents.launchIntent
import com.github.dhaval2404.imagepicker.ImagePickerActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val REQUEST_CODE_APP_UPDATE = 102

@AndroidEntryPoint
class LoginActivity : AppCompatActivity(), InstallStateUpdatedListener {

    @Inject
    lateinit var firebaseService: FirebaseService

    private val mLocationManager: LocationManager by lazy {
        getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }


    //APP UPDATE VARIABLES
    private lateinit var appUpdateManager: AppUpdateManager

    private var forceUpdate: Boolean = true
    private var shouldUpdate: Boolean = true

    private lateinit var binding: ActivityLogin2Binding
    val viewModel: LoginViewModel by viewModels()


    @Inject
    lateinit var appPreference: AppPreference

    @Inject
    lateinit var security: Security

    private var progressDialog: Dialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogin2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        checkForInAppUpdate()

        binding.btnLogin.setOnClickListener {
            if (!validateInputs()) return@setOnClickListener
            if (viewModel.location == null) {
                fetchLocation(onStart = {
                    progressDialog = StatusDialog.progress(this, "Validating Location")
                }) {
                    progressDialog?.dismiss()
                    viewModel.location = it
                    onLoginButtonClick()
                }
            } else onLoginButtonClick()
        }

        binding.btnSignup.setOnClickListener {
            launchIntent(
                RegistrationActivity::class.java, bundleOf(
                    AppConstants.IS_SELF_REGISTRATION to true
                )
            )
        }

        binding.btnForgotPassword.setOnClickListener {
            forgotBottomSheetDialog()
        }

        subscribeObserver()

        setupLoginChecked()

        fetchLocation()

        ImagePickerActivity

        ViewUtil.resetErrorOnTextInputLayout(binding.tilMobileNumber, binding.tilPassword)
    }

    private fun forgotBottomSheetDialog() {

        val bottomSheetDialog = BottomSheetDialog(this, R.style.BottomSheetDialogStyle)
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_forgot)

        bottomSheetDialog.findViewById<TextView>(R.id.tv_forgot_password)?.setOnClickListener {
            bottomSheetDialog.dismiss()
            launchIntent(ForgetPasswordActivity::class.java)
        }

        bottomSheetDialog.findViewById<TextView>(R.id.tv_forgot_username)?.setOnClickListener {
            bottomSheetDialog.dismiss()
            launchIntent(ForgotLoginIdActivity::class.java)
        }

        bottomSheetDialog.show()


    }


    private fun subscribeObserver() {

        viewModel.loginObs.observe(this) {
            when (it) {
                is Resource.Loading -> {
                    progressDialog = StatusDialog.progress(this, "Login")
                }
                is Resource.Success -> {
                    progressDialog?.dismiss()
                    if (it.data.status == 1) {

                        if (it.data.role_id == 5
                            || it.data.role_id == 4
                            || it.data.role_id == 3
                            || it.data.role_id == 22
                            || it.data.role_id == 23
                            || it.data.role_id == 24
                        ) {
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else StatusDialog.failure(
                            this,
                            "Sorry App is available only for your role id"
                        )

                    } else if (it.data.status == 108) {
                        val dialog1: Dialog = AppDialogs.transactionStatus(this, it.data.message, 1)
                        val bntOk = dialog1.findViewById<Button>(R.id.btn_ok)
                        bntOk.setOnClickListener { _ ->
                            dialog1.dismiss()
                            val intent = Intent(this, ForgetPasswordActivity::class.java)
                            intent.putExtra("mobile", viewModel.loginId)
                            startActivity(intent)
                        }
                        dialog1.show()
                    } else if (it.data.status == 700) {
                        val intent = Intent(this, DeviceVerificationActivity::class.java)
                        intent.putExtra("mobile_number", viewModel.loginId)
                        startActivity(intent)
                    } else StatusDialog.failure(this, it.data.message)
                }
                is Resource.Failure -> {
                    progressDialog?.dismiss()
                    handleNetworkFailure(it.exception)
                }
            }
        }

    }

    private fun setupLoginChecked() {


        if (appPreference.autoLogin) {

            try {
                viewModel.loginId = security.decrypt(appPreference.loginID)
                viewModel.password = security.decrypt(appPreference.loginPassword)
            } catch (e: Exception) {
                viewModel.loginId = ""
                viewModel.password = ""
            }


        } else {
            appPreference.deleteMobile()
            appPreference.deleteLoginPassword()

            binding.run {
                edMobileNumber.setText("")
                edPassword.setText("")
            }
        }
    }

    private fun onLoginButtonClick() {

        viewModel.imei = AppUitls.id(this)
        viewModel.login()
    }

    private fun validateInputs(): Boolean {
        var isValidate: Boolean = true

        if (viewModel.loginId.length !in 6..10) {
            binding.tilMobileNumber.apply {
                error = "Enter valid user id"
                isErrorEnabled = true
            }
            isValidate = false
        } else binding.tilMobileNumber.isErrorEnabled = false

        if (viewModel.password.length < 6) {
            binding.tilPassword.apply {
                error = "Enter minimum 6 characters password"
                isErrorEnabled = true
            }
            isValidate = false
        } else binding.tilPassword.isErrorEnabled = false


        return isValidate
    }

    ///////////////////////////////////////===APP UPDATE FUNCATIONALITY===///////////////////////


    override fun onResume() {
        super.onResume()
        if (shouldUpdate)
            appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                    showSnackbar(
                        getString(R.string.app_update_msg),
                        getString(R.string.restart),
                        onClickListener
                    )
                } else if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                    requestUpdate(appUpdateInfo, AppUpdateType.IMMEDIATE)
                }
            }
    }

    private fun checkForInAppUpdate() {

        appUpdateManager = AppUpdateManagerFactory.create(this)

        firebaseService.getInAppUpdateData { forceUpdate, shouldUpdate ->
            this.forceUpdate = forceUpdate
            this.shouldUpdate = shouldUpdate

            AppLog.d("AppUpdate : force update $forceUpdate")
            AppLog.d("AppUpdate : should update $shouldUpdate")

            appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->

                AppLog.d("AppUpdate :  package :${appUpdateInfo.packageName()}")
                AppLog.d("AppUpdate :  versionCode :${appUpdateInfo.availableVersionCode()}")

                if (shouldUpdate && appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                    if (shouldUpdate && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                        requestUpdate(appUpdateInfo)
                        appUpdateManager.registerListener(this@LoginActivity)
                    } else if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                        requestUpdate(appUpdateInfo, AppUpdateType.IMMEDIATE)
                    }
                }
            }
        }


    }

    private fun requestUpdate(
        appUpdateInfo: AppUpdateInfo?,
        updateType: Int = AppUpdateType.IMMEDIATE
    ) {
        appUpdateManager.startUpdateFlowForResult(
            appUpdateInfo!!,
            updateType,
            this@LoginActivity,
            REQUEST_CODE_APP_UPDATE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_APP_UPDATE && resultCode != RESULT_OK) {

            if (forceUpdate) {
                checkForInAppUpdate()
                Toast.makeText(
                    this,
                    "There was some major changes on this app, need update first to to run",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onStateUpdate(state: InstallState) {
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            showSnackbar(
                getString(R.string.app_update_msg),
                getString(R.string.restart),
                onClickListener
            )
            appUpdateManager.unregisterListener(this)
        }
    }

    private fun showSnackbar(
        message: String,
        actionText: String,
        clickListener: View.OnClickListener
    ) {

        Snackbar.make(binding.root, message, Snackbar.LENGTH_INDEFINITE)
            .setAction(actionText, clickListener).show()

    }

    private val onClickListener = View.OnClickListener { view ->
        appUpdateManager.completeUpdate()
    }

    private fun fetchLocation(onStart: () -> Unit = {}, onFetch: (Location?) -> Unit = {}) {
        if (viewModel.location != null) {
            onFetch(viewModel.location)
        } else {
            val isLocationServiceEnable = LocationService.isLocationEnabled(this)
            if (isLocationServiceEnable) {
                requestForLocationPhoneReadState {
                    onStart()
                    LocationService.getCurrentLocation(mLocationManager)
                    LocationService.setupListener(object : LocationService.MLocationListener {
                        override fun onLocationChange(location: Location) {
                            if (viewModel.location == null) {
                                globalLocation = location
                                viewModel.location = location
                                onFetch(location)
                            }

                        }
                    })
                }
            }

        }
    }

}
