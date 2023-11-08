package com.a2zsuvidhaa.`in`.fragment.agreement

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.a2zsuvidhaa.`in`.AppPreference
import com.a2zsuvidhaa.`in`.PermissionHandler
import com.a2zsuvidhaa.`in`.R
import com.a2zsuvidhaa.`in`.databinding.FragmentAgreementBinding
import com.a2zsuvidhaa.`in`.fragment.BaseFragment
import com.a2zsuvidhaa.`in`.model.AgreementInitialInfo
import com.a2zsuvidhaa.`in`.util.PermissionHandler2
import com.a2zsuvidhaa.`in`.util.dialogs.StatusDialog
import com.a2zsuvidhaa.`in`.util.ents.handleNetworkFailure
import com.a2zsuvidhaa.`in`.util.ents.hide
import com.a2zsuvidhaa.`in`.util.ents.show
import com.a2zsuvidhaa.`in`.util.ents.showToast
import com.a2zsuvidhaa.`in`.util.file.AppFileUtil
import com.a2zsuvidhaa.`in`.util.ui.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class AgreementFragment : BaseFragment<FragmentAgreementBinding>(R.layout.fragment_agreement) {

    @Inject
    lateinit var appPreference: AppPreference
    private val viewModel: AgreementViewModel by viewModels()

    var type: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        type = arguments?.getSerializable("type") as Int
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.agreementType = if (type == 0) AgreementType.USER else AgreementType.IRCTC
        viewModel.fetchInitialAgreement()
        subscribers()
        clickEvent()
    }

    private fun clickEvent() {
        binding.btnProceed.setOnClickListener { setupWebView() }
        binding.btnAgree.setOnClickListener { viewModel.generateAgreementPdf() }

    }

    private fun setupWebView() {
        progressDialog = StatusDialog.progress(requireContext())


        binding.webView.settings.javaScriptEnabled = true
        binding.webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {

                binding.cvForm.hide()
                binding.rlWebView.show()
                progressDialog?.dismiss()

                val script2 = """
                            
                         document.querySelector("input[name=checkbox]").addEventListener('change', function() {
                      if (this.checked) {
                        AgreementCallback.onAgree("true");
                      } else {
                        AgreementCallback.onAgree("false");
                      }
                    });
                       """.trimIndent()

                binding.webView.evaluateJavascript(script2, null)


            }
        }
        binding.webView.addJavascriptInterface(
            AgreementCallback {
                lifecycleScope.launch {
                    withContext(Dispatchers.Main) {
                        if (it) binding.btnAgree.show()
                        else binding.btnAgree.hide()
                    }
                }
            }, "AgreementCallback"
        )

        val url = when (viewModel.agreementType) {
            AgreementType.USER -> "agreement/content"
            AgreementType.IRCTC -> "agreement/irctc/content"
        }
        binding.webView.loadUrl(
            "https://partners.a2zsuvidhaa.com/mobileapp/api/$url", mapOf(
                "user-id" to appPreference.id.toString(),
                "token" to appPreference.token
            )
        )
    }

    private fun subscribers() {
        initialAgreementInfoResponse()
        generateAgreementPdfResponse()
        startAgreementResponse()
        checkStatusResponse()
        downloadAgreementResponse()
    }

    private fun downloadAgreementResponse() =
        viewModel.downloadAgreementObs.observe(viewLifecycleOwner) {
            when (it) {

                is Resource.Loading -> {
                    progressDialog = StatusDialog.progress(requireContext())
                }
                is Resource.Success -> {
                    progressDialog?.dismiss()
                    if (it.data.status == 1) {
                        binding.cvInfo.hide()
                        binding.rlWebView.hide()
                        binding.cvForm.hide()
                        viewModel.fetchInitialAgreement()
                    } else StatusDialog.alert(requireActivity(), it.data.message) {
                        activity?.onBackPressed()
                    }
                }
                is Resource.Failure -> {
                    progressDialog?.dismiss()
                    activity?.handleNetworkFailure(it.exception)
                }
            }
        }

    private fun checkStatusResponse() = viewModel.checkStatusObs.observe(viewLifecycleOwner) {
        when (it) {

            is Resource.Loading -> {
                if (progressDialog != null)
                    if (!progressDialog!!.isShowing)
                        progressDialog =
                            StatusDialog.progress(requireContext(), "Checking E-Sing...")


            }
            is Resource.Success -> {
                when (it.data.status) {
                    1 -> {
                        progressDialog?.dismiss()
                        viewModel.shouldCheckStatus = false
                        viewModel.downloadAgreementReport()
                    }
                    2 -> {
                        viewModel.checkStatus()
                    }
                    else -> {
                        progressDialog?.dismiss()
                        StatusDialog.alert(requireActivity(), it.data.message) {
                            activity?.onBackPressed()
                        }
                    }
                }
            }
            is Resource.Failure -> {
                progressDialog?.dismiss()
                activity?.handleNetworkFailure(it.exception)
            }
        }
    }

    private fun startAgreementResponse() = viewModel.agreementStartObs.observe(viewLifecycleOwner) {
        when (it) {
            is Resource.Loading -> {
                progressDialog = StatusDialog.progress(requireContext())
            }
            is Resource.Success -> {
                progressDialog?.dismiss()
                if (it.data.status == 1) {
                    viewModel.orderId = it.data.orderId
                    viewModel.shouldCheckStatus = true
                    val browserIntent =
                        Intent(Intent.ACTION_VIEW, Uri.parse(it.data.url))
                    startActivity(browserIntent)

                    viewModel.checkStatus(delayInSecond = 10)

                } else StatusDialog.failure(requireActivity(), it.data.message)
            }
            is Resource.Failure -> {
                progressDialog?.dismiss()
                activity?.handleNetworkFailure(it.exception)
            }
        }
    }

    private fun generateAgreementPdfResponse() {
        viewModel.generatePdfObs.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    progressDialog = StatusDialog.progress(requireContext())
                }
                is Resource.Failure -> {
                    progressDialog?.dismiss()
                    activity?.handleNetworkFailure(it.exception)
                }
                is Resource.Success -> {
                    progressDialog?.dismiss()
                    if (it.data.status == 1) {
                        viewModel.startAgreement()
                    } else StatusDialog.failure(requireActivity(), it.data.message)


                }
            }
        }
    }


    private fun initialAgreementInfoResponse() {

        fun setupUIData(data: AgreementInitialInfo?) {
            binding.edName.setText(data?.name)
            binding.edEmail.setText(data?.email)
            binding.edMobile.setText(data?.mobile)
        }

        viewModel.agreementInitialInfoObs.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    binding.progress.show()
                    binding.cvForm.hide()
                    binding.cvInfo.hide()
                    binding.rlWebView.hide()
                }
                is Resource.Failure -> {
                    binding.progress.hide()
                    activity?.handleNetworkFailure(it.exception)
                }
                is Resource.Success -> {
                    binding.progress.hide()
                    when (it.data.status) {
                        1 -> {
                            binding.cvInfo.show()
                            binding.tvStatusMessage.text = it.data.message
                            binding.btnDownload.setOnClickListener { _ ->
                                downloadFile(it.data.data!!.agreementUrl!!)
                            }
                        }
                        2 -> {
                            StatusDialog.failure(
                                requireActivity(),
                                it.data.message
                            ) { activity?.onBackPressed() }
                        }
                        3 -> {
                            setupUIData(it.data.data)
                            binding.cvForm.show()
                        }
                        else -> {
                            StatusDialog.alert(
                                requireActivity(),
                                it.data.message
                            ) { activity?.onBackPressed() }
                        }
                    }
                }
            }
        }
    }

    private fun downloadFile(url: String) {

        requireContext().showToast("Downloading...")

        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val fileName = "A2Z_Agreement_${timeStamp}.pdf"
        val desc = "A2Z Suvidhaa Agreement File"
        
        PermissionHandler2.checkStoragePermission(requireActivity()){
            if(!it) return@checkStoragePermission
            val request = DownloadManager.Request(Uri.parse(url))
                .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                .setTitle(fileName)
                .setDescription(desc)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(false)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
            val downloadManager =
                requireContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            downloadManager.enqueue(request)
            requireContext().showToast("Download completed")

        }



    }



    companion object {
        enum class AgreementType {
            IRCTC, USER
        }

        fun newInstance(type: Int): AgreementFragment {

            return AgreementFragment().apply {
                arguments = bundleOf(
                    "type" to type
                )
            }
        }

    }

}