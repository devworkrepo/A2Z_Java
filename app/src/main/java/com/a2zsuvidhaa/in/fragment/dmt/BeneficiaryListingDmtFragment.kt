package com.a2zsuvidhaa.`in`.fragment.dmt

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.a2zsuvidhaa.`in`.AppPreference
import com.a2zsuvidhaa.`in`.R
import com.a2zsuvidhaa.`in`.adapter.dmt.BeneficiaryAdapter
import com.a2zsuvidhaa.`in`.data.model.dmt.Beneficiary
import com.a2zsuvidhaa.`in`.databinding.FragmentBeneficiaryListingDmtBinding
import com.a2zsuvidhaa.`in`.fragment.BaseFragment
import com.a2zsuvidhaa.`in`.util.AppUtil
import com.a2zsuvidhaa.`in`.util.dialogs.Dialogs
import com.a2zsuvidhaa.`in`.util.dialogs.StatusDialog
import com.a2zsuvidhaa.`in`.util.ents.*
import com.a2zsuvidhaa.`in`.util.enums.DmtType
import com.a2zsuvidhaa.`in`.util.ui.Resource
import com.a2zsuvidhaa.`in`.viewmodel.dmt.BeneficiaryListingViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class BeneficiaryListingDmtFragment :
    BaseFragment<FragmentBeneficiaryListingDmtBinding>(R.layout.fragment_beneficiary_listing_dmt) {


    @Inject
    lateinit var appPreference: AppPreference

    private val args by navArgs<BeneficiaryListingDmtFragmentArgs>()

    private val viewModel: BeneficiaryListingViewModel by viewModels()

    private var adapter: BeneficiaryAdapter? = null
    private var beneficiaryList = arrayListOf<Beneficiary>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.moneySender = args.sender
        viewModel.dmtType = args.dmtType

        setupToolbar(binding.toolbar.toolbar, "Beneficiary List", subtitle = DmtUtil.getDmtServiceName(viewModel.dmtType))


        binding.fabAddBeneficiary.setOnClickListener {

            if (viewModel.dmtType == DmtType.UPI) {
                findNavController().navigate(
                        BeneficiaryListingDmtFragmentDirections
                                .actionBeneficiaryListingFragmentToAddUpiBeneficiaryFragment(
                                        sender = viewModel.moneySender,
                                )
                )
            }else{
                findNavController().navigate(
                        BeneficiaryListingDmtFragmentDirections
                                .actionBeneficiaryListingFragmentToAddBeneficiaryFragment(
                                        sender = viewModel.moneySender,
                                        dmtType = viewModel.dmtType
                                )
                )
            }

            viewModel.isNavigate = true
        }

        if (!viewModel.isNavigate)
            viewModel.fetchBeneficiary()

        subscribeObservers()

        setupTextView()

        binding.edSearch.afterTextChange {
            filter(it)
        }
        viewModel.isNavigate = false

    }

    @SuppressLint("SetTextI18n")
    private fun setupTextView() {
        binding.apply {
            tvName.text = viewModel.moneySender?.firstName + " " + viewModel.moneySender?.lastName
            tvMobileNumber.text = "Mobile Number : ${viewModel.moneySender?.mobileNumber}"
            tvLimit.text = "Available Limit : " + viewModel.moneySender?.remBal.orEmpty()

            tvTitleAdd.text = if (DmtType.UPI == viewModel.dmtType) "Add New\nUPI ID" else "Add New\nBeneficiary"
        }
    }


    private fun filter(text: String) {
        adapter?.let {
            val filteredList: ArrayList<Beneficiary> = ArrayList()
            for (item in beneficiaryList) {
                if (item.name.orEmpty().toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(item)
                } else if (item.accountNumber.orEmpty().toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(item)
                }
            }
            it.addItemsWithClear(filteredList)
        }
    }

    private fun subscribeObservers() {

        getNavigationResultAsLiveData<Boolean>().observe(viewLifecycleOwner) {
            if (it) viewModel.fetchBeneficiary()
        }

        viewModel.beneficiaryListingObs.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    binding.run {
                        coordinateLayout.hide()
                        progress.show()
                    }
                }
                is Resource.Success -> {

                    binding.run {
                        coordinateLayout.show()
                        progress.hide()
                        cvNotItemFound.hide()
                        recyclerView.show()
                    }

                    when (it.data.status) {
                        22 -> {
                            setupRecyclerView(it.data.data!!)
                            AppUtil.logger("Test Beneficiary List : ${it.data.data}")
                        }
                        23 -> {
                            binding.cvNotItemFound.show()
                            binding.recyclerView.hide()
                        }
                        else -> StatusDialog.failure(requireActivity(), it.data.message)
                    }
                }
                is Resource.Failure -> {

                    activity?.handleNetworkFailure(it.exception, shouldBack = true)
                }
            }
        }


        viewModel.onVerifyAccountObs.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    progressDialog = StatusDialog.progress(requireActivity(), "Verifying...")
                }
                is Resource.Success -> {
                    progressDialog?.dismiss()
                    if (it.data.status == 1) {
                        val beneName = if(viewModel.dmtType == DmtType.UPI) it.data.upiBeneName else it.data.beneName
                        StatusDialog.success(requireActivity(), beneName.orEmpty(),title = "Beneficiary Name") {
                            viewModel.fetchBeneficiary()
                        }
                    } else StatusDialog.failure(requireActivity(), it.data.message)

                }
                is Resource.Failure -> {
                    progressDialog?.dismiss()
                    activity?.handleNetworkFailure(it.exception, shouldBack = true)
                }
            }
        }


        viewModel.onDeleteBeneficiaryObs.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    progressDialog = StatusDialog.progress(requireActivity(), "Deleting...")
                }
                is Resource.Success -> {
                    progressDialog?.dismiss()

                    if (it.data.status == 37) {

                        val otpLength = 4

                        Dialogs.otpVerify(requireActivity(),showTimer = false,otpLength = otpLength,onSubmit = {otp->
                            viewModel.deleteBeneficiaryVerify(otp)
                        })

                    }
                    else StatusDialog.failure(requireActivity(), it.data.message)

                }
                is Resource.Failure -> {
                    progressDialog?.dismiss()
                    activity?.handleNetworkFailure(it.exception, shouldBack = true)
                }
            }
        }


        viewModel.onDeleteBeneficiaryVerifyObs.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    progressDialog = StatusDialog.progress(requireActivity(), "Deleting...")
                }
                is Resource.Success -> {
                    progressDialog?.dismiss()

                    if (it.data.status == 38) {
                        StatusDialog.success(requireActivity(),it.data.message){
                            viewModel.fetchBeneficiary()
                        }
                    }
                    else StatusDialog.failure(requireActivity(), it.data.message)
                }
                is Resource.Failure -> {
                    progressDialog?.dismiss()
                    activity?.handleNetworkFailure(it.exception, shouldBack = true)
                }
            }
        }
    }


    private fun setupRecyclerView(mList: ArrayList<Beneficiary>) {

        val item = mList.find { it.accountNumber?.trim() == args.searchAccount?.trim() }
        beneficiaryList = mList

        if (mList.isEmpty()) binding.run {
            cvNotItemFound.show()
            coordinateLayout.hide()
        }
        else binding.run {
            cvNotItemFound.hide()
            coordinateLayout.show()
        }

        adapter = BeneficiaryAdapter(viewModel.dmtType,highLightFirst = item != null).apply {
            context = requireActivity()
            addItems(mList)
        }



        item?.let {
            val indexA = mList.indexOf(it)
            val indexB = 0
            adapter?.swapItems(indexA, indexB)
        }



        adapter?.onTransactionButtonClick = { beneficiary ->
            findNavController()
                    .navigate(
                            BeneficiaryListingDmtFragmentDirections
                                    .actionBeneficiaryListingFragmentToMoneyTransfer(
                                            beneficiary = beneficiary,
                                            sender = viewModel.moneySender,
                                            dmtType = viewModel.dmtType
                                    )
                    )
            viewModel.isNavigate = true
        }

        adapter?.onVerifyBeneficiary = { bene ->

            val text = if (DmtType.UPI == viewModel.dmtType)
                "Confirm verification for upi id : ${bene.accountNumber.orEmpty()}"
            else "Confirm verification for account number : ${bene.accountNumber.orEmpty()}"

            Dialogs.commonConfirmDialog(requireContext(), text).apply {

                findViewById<Button>(R.id.btn_confirm).setOnClickListener {
                    viewModel.beneciary = bene
                    dismiss()
                    viewModel.verifyAccount()
                }
            }


        }

        adapter?.onDeleteBeneficiary = {
            viewModel.beneciary = it

            val text = if (viewModel.dmtType == DmtType.UPI) "upi id" else "account number"

            Dialogs.commonConfirmDialog(
                    requireContext(),
                    "You are sure! to delete ${viewModel.beneciary.name} with $text ${viewModel.beneciary.accountNumber}"
            ).apply {
                findViewById<Button>(R.id.btn_confirm).setOnClickListener {
                    dismiss()
                    viewModel.deleteBeneficiary()
                }
            }
        }

        binding.recyclerView.setup().adapter = adapter
    }
}