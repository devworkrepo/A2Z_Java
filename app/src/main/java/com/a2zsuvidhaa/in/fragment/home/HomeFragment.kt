package com.a2zsuvidhaa.`in`.fragment.home

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.a2zsuvidhaa.`in`.AppPreference
import com.a2zsuvidhaa.`in`.BuildConfig
import com.a2zsuvidhaa.`in`.R
import com.a2zsuvidhaa.`in`.activity.DmtActivity
import com.a2zsuvidhaa.`in`.activity.AepsKycActivity
import com.a2zsuvidhaa.`in`.activity.MainActivity
import com.a2zsuvidhaa.`in`.activity.ShowQRImageActivity
import com.a2zsuvidhaa.`in`.activity.aeps.AepsSettlementActivityKT
import com.a2zsuvidhaa.`in`.activity.aeps2.Aeps2Activity
import com.a2zsuvidhaa.`in`.activity.fund_request.FundRequestActivity
import com.a2zsuvidhaa.`in`.activity.matm.MatmActivity
import com.a2zsuvidhaa.`in`.activity.recharge.ProviderActivity
import com.a2zsuvidhaa.`in`.adapter.SliderAdapter
import com.a2zsuvidhaa.`in`.databinding.FragmentHomeBinding
import com.a2zsuvidhaa.`in`.fragment.BaseFragment
import com.a2zsuvidhaa.`in`.listener.KycRequiredListener
import com.a2zsuvidhaa.`in`.model.Banner
import com.a2zsuvidhaa.`in`.util.AppConstants
import com.a2zsuvidhaa.`in`.util.Security
import com.a2zsuvidhaa.`in`.util.SessionManager
import com.a2zsuvidhaa.`in`.util.apis.Resource
import com.a2zsuvidhaa.`in`.util.dialogs.AepsDialogs
import com.a2zsuvidhaa.`in`.util.dialogs.Dialogs
import com.a2zsuvidhaa.`in`.util.dialogs.StatusDialog
import com.a2zsuvidhaa.`in`.util.ents.*
import com.a2zsuvidhaa.`in`.util.enums.DmtType
import com.a2zsuvidhaa.`in`.util.enums.ProviderType
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment() : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {

    private var runnableForUpdateUi: Runnable? = null
    private var timer: Timer? = null
    private var handler: Handler? = null
    private var mediator: TabLayoutMediator? = null

    private var currentPage = 0
    private lateinit var sessionManager: SessionManager

    private val viewModel: HomeViewModel by viewModels()

    @Inject
    lateinit var security: Security

    @Inject
    lateinit var appPreference: AppPreference


    private var kycRequiredListener: KycRequiredListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        kycRequiredListener = activity as MainActivity

    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)




        if (BuildConfig.DEBUG) {
            binding.tvOne.text = "Dmt One"
            binding.tvTwo.text = "Dmt Two"
            binding.tvThree.text = "Dmt Three"
        }

        sessionManager = SessionManager(requireContext())
        setupClickListener()

        viewModel.fetchBanners()

        viewModel.bannerObs.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    if (it.data.status == 1) {
                        binding.llPager.show()
                        it.data.banners?.let { it1 -> setupViewPager(it1) }
                        setAutoSliderToViewPager()
                        runnableForUpdateUi = Runnable {
                            if (currentPage == it.data.banners?.size) currentPage = 0
                            binding.viewPager.setCurrentItem(currentPage++, true)
                        }
                    } else {

                        binding.llPager.hide()
                    }

                }
                else -> {
                    binding.llPager.hide()
                }
            }
        }





        showKycWarning()

        binding.tvNumber.text = security.decrypt(appPreference.mobile)
        binding.tvRole.text = appPreference.roleTitle

        viewModel.fetchBalanceInfo()
        viewModel.fetchNews()
        viewModel.fetchBankDown()
        subscribeObserver()
        binding.cvNews.setOnClickListener {
            Dialogs.news(requireActivity(), viewModel.news)
        }

        binding.cvBankDown.setOnClickListener {
            viewModel.bankDownResponse?.bankList?.let {
                Dialogs.bankDown(requireContext(), it)
            }
        }
        binding.tvBlink1.blink()
        binding.tvBlink2.blink()
        binding.tvBlink3.blink()
    }

    private fun showKycWarning() {
        binding.tvKycInfo.text = kycWarningMessage()
        if (checkAnyKycPendingForDmtAndAeps()) {
            binding.tvKycInfo.show()
        } else binding.tvKycInfo.hide()
    }

    private fun checkAnyKycPendingForDmt(): Boolean {
        return appPreference.isAadhaarKyc == 0
                || appPreference.isVideoKyc == 0
    }

    private fun checkAnyKycPendingForDmtAndAeps(): Boolean {
        return appPreference.isAadhaarKyc == 0
                || appPreference.kyc == 0
                || appPreference.settlementBankInfo == 0
                || appPreference.isVideoKyc == 0
    }


    private fun kycWarningMessage() = when {
        appPreference.isAadhaarKyc == 0 -> {
            "Aadhaar Kyc is required, to enable DMT and AEPS Services"
        }
        appPreference.isVideoKyc == 0 -> {
            "Please upload all required documents, to enable DMT and AEPS Services"
        }
        appPreference.settlementBankInfo == 0 -> {
            "Please add aeps settlement bank first, to enable AEPS"
        }
        appPreference.kyc == 0 -> {
            "Aadhaar e-kyc is required, to enable AEPS Services"
        }
        else -> ""
    }

    private fun subscribeObserver() {
        viewModel.balanceObs.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Loading -> {
                    binding.run {

                        llBalance.invisible()
                        tvViewBalance.hide()
                        tvAmountProgressBar.show()
                    }
                }
                is Resource.Success -> {
                    binding.run {
                        appPreference.userBalance = it.data.balance.userBalance
                        tvAmount.text = it.data.balance.userBalance
                        llBalance.show()
                        tvAmountProgressBar.hide()
                    }
                }
                is Resource.Failure -> {
                    binding.run {
                        llBalance.hide()
                        tvViewBalance.show()
                        tvAmountProgressBar.hide()
                    }
                }
            }
        })

        viewModel.checkKycObs.observe(viewLifecycleOwner, Observer {
            when (it) {

                is Resource.Loading -> {
                    progressDialog = StatusDialog.progress(requireActivity())
                }
                is Resource.Success -> {
                    progressDialog?.dismiss()
                    if (it.data.status == 1) {

                        appPreference.isAadhaarKyc = it.data.data.is_aadhaar_kyc
                        appPreference.isVideoKyc = it.data.data.is_video_kyc
                        appPreference.settlementBankInfo =
                            it.data.data.is_user_has_active_settlemnet_account
                        appPreference.kyc = it.data.data.aeps_kyc

                        showKycWarning()

                        if (checkAnyKycPendingForDmtAndAeps()) {
                            showDialogForDmtAndAepsKyc()
                        } else StatusDialog.success(
                            requireActivity(),
                            "You kyc process has done, Now you can access all Aeps and Dmt services. Thankyou"
                        )

                    }
                }
                is Resource.Failure -> {
                    progressDialog?.dismiss()
                    StatusDialog.failure(requireActivity(), it.exception.toString())

                }
            }
        })


        viewModel.newsObs.observe(viewLifecycleOwner) {
            when (it) {

                is Resource.Loading -> {
                    binding.cvNews.hide()
                }
                is Resource.Success -> {

                    if (it.data.status == 1) {

                        if (it.data.retailerNews.isNotEmpty()) {

                            binding.cvNews.show()
                            binding.tvNews.text = it.data.retailerNews
                        }

                        if(appPreference.rollId == 4 ||  appPreference.rollId == 3){
                            binding.tvNews.text = it.data.distributorNews
                        }

                    }
                }
                is Resource.Failure -> {
                    binding.cvNews.hide()
                }
            }
        }

        viewModel.bankDownObs.observe(viewLifecycleOwner, Observer {
            when (it) {

                is Resource.Loading -> {
                    binding.cvBankDown.hide()
                }
                is Resource.Success -> {

                    if (it.data.status == 1) {
                        binding.cvBankDown.show()
                        binding.tvBankDown.text = it.data.bankString
                        binding.tvBankDown.isSelected = true
                    }
                }
                is Resource.Failure -> {
                    binding.cvBankDown.hide()
                }
            }
        })
    }

    private fun showDialogForDmtAndAepsKyc(): Boolean {


        when {
            appPreference.isAadhaarKyc == 0 -> {
                AepsDialogs.kycRequired(
                    context = requireActivity(),
                    title = "Aadhaar Kyc Required!",
                    description = "Aadhaar kyc is required, to enable DMT and AEPS Services"
                ) {
                    kycRequiredListener?.onAadhaarKycRequired()
                }
                return false
            }
            appPreference.isVideoKyc == 0 -> {
                AepsDialogs.kycRequired(
                    context = requireActivity(),
                    title = "Upload documents First",
                    description = "Please upload all required documents, to enable DMT and AEPS Services"
                ) {
                    kycRequiredListener?.onDocumentKycRequired()
                }



                return false
            }
            appPreference.settlementBankInfo == 0 -> {
                AepsDialogs.kycRequired(
                    context = requireActivity(),
                    title = "Add Bank First!",
                    description = "Please add aeps settlement bank first, to enable AEPS"
                ) {
                    activity?.launchIntent(AepsSettlementActivityKT::class.java)
                }

                return false
            }
            appPreference.kyc == 0 -> {
                AepsDialogs.kycRequired(
                    context = requireActivity(),
                    title = "Aeps Kyc Required!",
                    description = "Aadhaar e-kyc is required, to enable AEPS Services"
                ) {
                    activity?.launchIntent(AepsKycActivity::class.java)
                }

                return false
            }
            else -> return true
        }

    }


    private fun setupClickListener() {
        binding.let {

            //payment services
            it.llAepsOne.setOnClickListener {

                if (checkAnyKycPendingForDmtAndAeps()) viewModel.checkKyc()
                else activity?.launchIntent(
                    Aeps2Activity::class.java,
                    bundleOf(AppConstants.SERVICE_TYPE to AppConstants.AEPS_ONE)
                )
            }

            it.llAepsThree.setOnClickListener {
                if (checkAnyKycPendingForDmtAndAeps()) viewModel.checkKyc()
                else activity?.launchIntent(
                    Aeps2Activity::class.java,
                    bundleOf(AppConstants.SERVICE_TYPE to AppConstants.AEPS_THREE)
                )
            }

            it.llWalletOne.setOnClickListener {
                if (checkAnyKycPendingForDmt()) viewModel.checkKyc()
                //   else context?.launchIntent(A2ZWalletActivity::class.java)

                else context?.launchIntent(
                    DmtActivity::class.java, bundleOf(
                        AppConstants.DATA to DmtType.WALLET_ONE,
                    )
                )
            }
            it.llWalletTwo.setOnClickListener {
                if (checkAnyKycPendingForDmt()) viewModel.checkKyc()
                //   else context?.launchIntent(A2ZWalletActivity::class.java)

                else context?.launchIntent(
                    DmtActivity::class.java, bundleOf(
                        AppConstants.DATA to DmtType.WALLET_TWO
                    )
                )
            }
            it.llWalletThree.setOnClickListener {
                if (checkAnyKycPendingForDmt()) viewModel.checkKyc()
                //   else context?.launchIntent(A2ZWalletActivity::class.java)

                else context?.launchIntent(
                    DmtActivity::class.java, bundleOf(
                        AppConstants.DATA to DmtType.WALLET_THREE
                    )
                )
            }

            it.llDmtThree.setOnClickListener {
                if (checkAnyKycPendingForDmt()) viewModel.checkKyc()
                //   else context?.launchIntent(A2ZWalletActivity::class.java)

                else context?.launchIntent(
                    DmtActivity::class.java, bundleOf(
                        AppConstants.DATA to DmtType.DMT_THREE
                    )
                )
            }

            it.llUpi.setOnClickListener {
                if (checkAnyKycPendingForDmt()) viewModel.checkKyc()
                //   else context?.launchIntent(A2ZWalletActivity::class.java)

                else context?.launchIntent(
                    DmtActivity::class.java, bundleOf(
                        AppConstants.DATA to DmtType.UPI
                    )
                )
            }


            //recharge
            it.llPrepaid.setOnClickListener {
                sessionManager.addString("bbps", "1")
                val bundleData = bundleOf(PROVIDER_TYPE to ProviderType.MOBILE_PREPAID)
                context?.launchIntent(ProviderActivity::class.java, data = bundleData)
            }
            it.llDth.setOnClickListener {
                sessionManager.addString("bbps", "1")
                val bundleData = bundleOf(PROVIDER_TYPE to ProviderType.DTH)
                context?.launchIntent(ProviderActivity::class.java, data = bundleData)
            }

            it.llFasttag.setOnClickListener {
                sessionManager.addString("bbps", "2")
                val bundleData = bundleOf(PROVIDER_TYPE to ProviderType.FASTTAG)
                context?.launchIntent(ProviderActivity::class.java, data = bundleData)
            }

            //bill payment
            it.llElectricity.setOnClickListener {
                sessionManager.addString("bbps", "2")
                val bundleData = bundleOf(PROVIDER_TYPE to ProviderType.ELECTRICITY)
                context?.launchIntent(ProviderActivity::class.java, data = bundleData)
            }

            it.llWater.setOnClickListener {
                sessionManager.addString("bbps", "2")
                val bundleData = bundleOf(PROVIDER_TYPE to ProviderType.WATER)
                context?.launchIntent(ProviderActivity::class.java, data = bundleData)
            }

            it.llGas.setOnClickListener {
                sessionManager.addString("bbps", "2")
                val bundleData = bundleOf(PROVIDER_TYPE to ProviderType.GAS)
                context?.launchIntent(ProviderActivity::class.java, data = bundleData)
            }

            it.llInsurance.setOnClickListener {
                sessionManager.addString("bbps", "2")
                val bundleData = bundleOf(PROVIDER_TYPE to ProviderType.INSURANCE)
                context?.launchIntent(ProviderActivity::class.java, data = bundleData)
            }

            it.llLoanReplayment.setOnClickListener {
                sessionManager.addString("bbps", "2")
                val bundleData = bundleOf(PROVIDER_TYPE to ProviderType.LOAN_REPAYMENT)
                context?.launchIntent(ProviderActivity::class.java, data = bundleData)
            }

            it.llPostpaid.setOnClickListener {
                sessionManager.addString("bbps", "2")
                val bundleData = bundleOf(PROVIDER_TYPE to ProviderType.POSTPAID)
                context?.launchIntent(ProviderActivity::class.java, data = bundleData)
            }

            //top up
            it.llTopUpWallet.setOnClickListener {

                activity?.launchIntent(
                    FundRequestActivity::class.java,
                    bundleOf(AppConstants.ORIGIN to "topup")
                )

                /* MainActivity.spos = 0
                 (activity as MainActivity?)?.replaceFragment(BankDetailFragment.newInstance())*/
                //context?.launchIntent(AddMoneyActivity::class.java)
            }


            it.tvViewBalance.setOnClickListener {
                viewModel.fetchBalanceInfo()
            }

            it.llBalance.setOnClickListener {
                viewModel.fetchBalanceInfo()
            }
            it.tvKycInfo.setOnClickListener {
                viewModel.checkKyc()
            }

            it.llQrCode.setOnClickListener {
                context?.launchIntent(ShowQRImageActivity::class.java)
            }

            it.llMatm.setOnClickListener {
                if (appPreference.matmStatus == "1") {
                    context?.launchIntent(MatmActivity::class.java, bundleOf("is_mpos" to false))
                } else {
                    StatusDialog.alert(requireActivity(), "Service is not activate") {
                        kycRequiredListener?.onMATMService()
                    }
                }
            }
            it.llMpos.setOnClickListener {
                if (appPreference.mposStatus == "1") {
                    context?.launchIntent(MatmActivity::class.java, bundleOf("is_mpos" to true))
                } else {
                    StatusDialog.alert(requireActivity(), "Service is not activate") {
                        kycRequiredListener?.onMPOSService()
                    }
                }
            }

        }
    }

    private fun setupViewPager(list: List<Banner>) {
        val pagerAdapter = SliderAdapter()
        pagerAdapter.context = requireContext()
        pagerAdapter.addItems(list)
        binding.viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.viewPager.adapter = pagerAdapter
        binding.viewPager.offscreenPageLimit = 2

        val pageMargin = resources.getDimensionPixelOffset(R.dimen.pageMargin).toFloat()
        val pageOffset = resources.getDimensionPixelOffset(R.dimen.offset).toFloat()

        binding.viewPager.setPageTransformer { page, position ->
            val myOffset = position * -(2 * pageOffset + pageMargin)
            if (binding.viewPager.orientation == ViewPager2.ORIENTATION_HORIZONTAL) {
                if (ViewCompat.getLayoutDirection(binding.viewPager) == ViewCompat.LAYOUT_DIRECTION_RTL) {
                    page.translationX = -myOffset
                } else {
                    page.translationX = myOffset
                }
            } else {
                page.translationY = myOffset
            }
        }
        mediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { _, _ -> }
        mediator?.attach()

    }

    private fun setAutoSliderToViewPager() {

        handler = Handler(Looper.getMainLooper())
        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                runnableForUpdateUi?.let { handler?.post(it) }
            }
        }, DELAY_MS, PERIOD_MS)



        binding.tabLayout.onTabSelectedListener {
            if (currentPage != it.position)
                currentPage = it.position
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.let { timer = null }
        runnableForUpdateUi?.let {
            handler?.removeCallbacks(it)
        }

        //detaching mediator and setting as null
        //so memory leak will not happen
        mediator?.detach()
        mediator = null
        binding.viewPager.adapter = null

    }

    companion object {
        const val DELAY_MS: Long = 10
        const val PERIOD_MS: Long = 2500
        const val PROVIDER_TYPE: String = "provider_type"
        fun newInstance() = HomeFragment()
    }


    override fun onStop() {
        super.onStop()
        binding.let {
            it.tvViewBalance.show()
            it.llBalance.hide()
        }
    }
}































































