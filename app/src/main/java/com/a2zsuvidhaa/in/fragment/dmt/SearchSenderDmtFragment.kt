package com.a2zsuvidhaa.`in`.fragment.dmt

import android.os.Bundle
import android.text.InputFilter
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.a2zsuvidhaa.`in`.AppPreference
import com.a2zsuvidhaa.`in`.R
import com.a2zsuvidhaa.`in`.adapter.dmt.AccountListAdapter
import com.a2zsuvidhaa.`in`.data.model.dmt.MoneySenderResponse
import com.a2zsuvidhaa.`in`.data.model.dmt.SenderAccountDetailResponse
import com.a2zsuvidhaa.`in`.databinding.FragmentSearchSenderDmtBinding
import com.a2zsuvidhaa.`in`.fragment.BaseFragment
import com.a2zsuvidhaa.`in`.util.AppUtil
import com.a2zsuvidhaa.`in`.util.ViewUtil
import com.a2zsuvidhaa.`in`.util.dialogs.Dialogs
import com.a2zsuvidhaa.`in`.util.dialogs.StatusDialog
import com.a2zsuvidhaa.`in`.util.ents.*
import com.a2zsuvidhaa.`in`.util.enums.DmtSenderRegisterType
import com.a2zsuvidhaa.`in`.util.enums.DmtType
import com.a2zsuvidhaa.`in`.util.ui.Resource
import com.a2zsuvidhaa.`in`.viewmodel.dmt.SearchSenderDmtViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SearchSenderDmtFragment :
    BaseFragment<FragmentSearchSenderDmtBinding>(R.layout.fragment_search_sender_dmt) {

    @Inject
    lateinit var appPreference: AppPreference
    private val viewModel: SearchSenderDmtViewModel by viewModels()
    private val args by navArgs<SearchSenderDmtFragmentArgs>()


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.dmtType = args.dmtType

        setupToolbar(binding.toolbar.toolbar, DmtUtil.getDmtServiceName(viewModel.dmtType))




        onClickHandler()

        setupSearchMode()

        subscribeObserver()

        getNavigationResult<String>() {
            it?.let {
                if (it.length == 10) {
                    viewModel.number = it
                    binding.edNumber.setText(viewModel.number)
                }
            }
        }

        ViewUtil.resetErrorOnTextInputLayout(binding.tilNumber)

        ViewUtil.showKeyboard(binding.edNumber)

        if (viewModel.dmtType == DmtType.UPI) {
            binding.cvBankDown.hide()
        } else {
            viewModel.fetchBankDown()
        }

        binding.cvBankDown.setOnClickListener {
            viewModel.bankDownResponse?.bankList?.let {
                Dialogs.bankDown(requireContext(), it)
            }
        }
    }


    private fun subscribeObserver() {
        viewModel.moneySenderSearchResponseObs.observe(
            viewLifecycleOwner
        ) { onSearchRemitterMobileResponse(it) }

        viewModel.searchRemitterAccountObs.observe(
            viewLifecycleOwner
        ) { onSearchRemitterAccountResponse(it) }

        viewModel.bankDownObs.observe(viewLifecycleOwner) {
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
        }

    }

    private fun onSearchRemitterAccountResponse(it: Resource<SenderAccountDetailResponse>) {
        when (it) {
            is Resource.Loading -> {
                progressDialog = StatusDialog.progress(requireActivity(), "Searching")
            }
            is Resource.Success -> {
                progressDialog?.dismiss()

                if (it.data.status == 1) {
                    val adapter = AccountListAdapter(viewModel.dmtType).apply {

                        addItems(it.data.senderAccountDetail!!)
                        context = requireActivity()
                    }

                    adapter.onItemClick = { _, item, _ ->
                       if(viewModel.dmtType == DmtType.DMT_THREE){
                           viewModel.searchedAccount = viewModel.number
                           viewModel.number = item.mobileNumber
                           viewModel.searchedIfscCode = item.ifscCode
                           viewModel.searchRemitterMobile()
                       }
                        else{
                           Dialogs.selectWallet(
                               requireActivity(),
                               item.remainingLimit,
                               item.remainingLimit2,
                               item.remainingLimit3,
                           ) {
                               viewModel.dmtType = it
                               viewModel.searchedAccount = viewModel.number
                               viewModel.number = item.mobileNumber
                               viewModel.searchedIfscCode = item.ifscCode
                               viewModel.searchRemitterMobile()
                           }
                       }
                    }

                    binding.recyclerView.setup().adapter = adapter

                    binding.tvBeneficiaryAccount.text = "Beneficiary Account : ${viewModel.number}"




                    if (viewModel.searchMode == SearchSenderDmtViewModel.SearchMode.ACCOUNT)
                        binding.cardViewRecyclerView.show()
                } else StatusDialog.failure(requireActivity(), it.data.message)
            }
            is Resource.Failure -> {
                progressDialog?.dismiss()
                activity?.handleNetworkFailure(it.exception)
            }
        }
    }

    private fun onSearchRemitterMobileResponse(it: Resource<MoneySenderResponse>?) {
        when (it) {
            is Resource.Loading -> {
                progressDialog = StatusDialog.progress(requireActivity(), "Searching...")
            }
            is Resource.Success -> {
                progressDialog?.dismiss()

                when (it.data.status) {
                    13 -> {
                        findNavController()
                            .navigate(
                                SearchSenderDmtFragmentDirections
                                    .actionSearchSenderFragmentToBeneficiaryListingFragment(
                                        dmtType = viewModel.dmtType,
                                        sender = it.data.moneySender?.apply {
                                            mobileNumber = mobileNumber
                                                ?: viewModel.number
                                        },
                                        searchAccount = viewModel.searchedAccount,
                                        searchIfsc = viewModel.searchedIfscCode

                                    )
                            )
                        viewModel.searchedAccount = ""
                        viewModel.searchedIfscCode = ""
                    }
                    11, 111 -> {
                        findNavController()
                            .navigate(
                                SearchSenderDmtFragmentDirections
                                    .actionSearchSenderFragmentToAddSenderFragment(
                                        dmtType = viewModel.dmtType,
                                        mobileNumber = viewModel.number,
                                        state = it.data.state.orEmpty(),
                                        registerType = DmtSenderRegisterType.REGISTER,
                                        sender = it.data.moneySender,
                                    )
                            )
                    }
                    12 -> {

                        findNavController()
                            .navigate(
                                SearchSenderDmtFragmentDirections
                                    .actionSearchSenderFragmentToAddSenderFragment(
                                        dmtType = viewModel.dmtType,
                                        mobileNumber = viewModel.number,
                                        sender = it.data.moneySender,
                                        state = it.data.state.orEmpty(),
                                        registerType = DmtSenderRegisterType.VERIFY_AND_UPDATE
                                    )
                            )
                    }
                    else -> StatusDialog.failure(requireActivity(), it.data.message)
                }
            }
            is Resource.Failure -> {
                progressDialog?.dismiss()
                activity?.handleNetworkFailure(it.exception)
            }
        }
    }

    private fun onClickHandler() = binding.also {
        it.btnSearch.setOnClickListener {
            if (!validateInputNumber()) return@setOnClickListener

            if (viewModel.searchMode == SearchSenderDmtViewModel.SearchMode.MOBILE)
                this.viewModel.searchRemitterMobile()
            else this.viewModel.searchRemitterAccount()
        }


        it.tvMobileSearch.setOnClickListener {
            viewModel.searchMode = SearchSenderDmtViewModel.SearchMode.MOBILE
            setupSearchMode()
        }
        it.tvAccountSearch.setOnClickListener {

            when (viewModel.dmtType) {
                DmtType.UPI -> {}
                else -> {
                    viewModel.searchMode = SearchSenderDmtViewModel.SearchMode.ACCOUNT
                    setupSearchMode()
                }

            }
        }

    }


    private fun validateInputNumber(): Boolean {
        viewModel.number = binding.edNumber.text.toString()
        var isValid = true
        if (viewModel.number.length < 10 && viewModel.searchMode == SearchSenderDmtViewModel.SearchMode.MOBILE) {
            binding.tilNumber.apply {
                error = "Enter 10 digits remitter mobile number!"
                isErrorEnabled = true
            }
            isValid = false
        } else if (viewModel.number.length < 8 && viewModel.searchMode == SearchSenderDmtViewModel.SearchMode.ACCOUNT) {
            binding.tilNumber.apply {
                error = "Enter minimum 8 digits account number!"
                isErrorEnabled = true
            }
            isValid = false
        } else binding.tilNumber.isErrorEnabled = false

        return isValid

    }

    private fun setupSearchMode() {
        binding.also {
            when (viewModel.searchMode) {
                SearchSenderDmtViewModel.SearchMode.MOBILE -> {
                    binding.btnSearch.setupBGColor(R.color.colorPrimaryDark)
                    binding.cardViewRecyclerView.hide()
                    it.tvMobileSearch.apply {
                        setupTextColor(R.color.white)
                        background =
                            ContextCompat.getDrawable(requireActivity(), R.drawable.bg_blue_30)
                        setPadding(0, 16, 0, 16)
                    }

                    it.tvAccountSearch.apply {
                        setupTextColor(R.color.black2)
                        background =
                            ContextCompat.getDrawable(requireActivity(), R.drawable.bg_white_black)
                        setPadding(0, 16, 0, 16)
                    }

                    if (viewModel.dmtType == DmtType.UPI) {
                        it.tvAccountSearch.visibility = View.INVISIBLE
                    }

                }
                SearchSenderDmtViewModel.SearchMode.ACCOUNT -> {
                    binding.btnSearch.setupBGColor(R.color.green)
                    it.tvMobileSearch.apply {
                        setupTextColor(R.color.black2)
                        background =
                            ContextCompat.getDrawable(requireActivity(), R.drawable.bg_white_black)
                        setPadding(0, 16, 0, 16)
                    }

                    it.tvAccountSearch.apply {
                        setupTextColor(R.color.white)
                        background =
                            ContextCompat.getDrawable(requireActivity(), R.drawable.bg_green_30)
                        setPadding(0, 16, 0, 16)
                    }
                }
            }


            ///////////////


            viewModel.number = ""
            it.edNumber.setText("")
            it.edNumber.isFocusable = true

            when (viewModel.searchMode) {
                SearchSenderDmtViewModel.SearchMode.MOBILE -> {
                    val mobileFilters = arrayOf<InputFilter>(InputFilter.LengthFilter(10))
                    it.tilNumber.hint = "Search Remitter Mobile"
                    it.edNumber.filters = mobileFilters

                }
                SearchSenderDmtViewModel.SearchMode.ACCOUNT -> {
                    val filters = arrayOf<InputFilter>(InputFilter.LengthFilter(20))
                    it.edNumber.filters = filters
                    it.tilNumber.hint = "Search Beneficiary Account"
                }
            }

        }
    }

}