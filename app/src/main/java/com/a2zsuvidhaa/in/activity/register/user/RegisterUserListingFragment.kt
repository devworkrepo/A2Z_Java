package com.a2zsuvidhaa.`in`.activity.register.user

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.a2zsuvidhaa.`in`.R
import com.a2zsuvidhaa.`in`.activity.register.RegistrationActivity
import com.a2zsuvidhaa.`in`.activity.register.kyc.RegisterUserKycActivity
import com.a2zsuvidhaa.`in`.adapter.user.CompleteUserPagingAdapter
import com.a2zsuvidhaa.`in`.adapter.user.InCompleteUserPagingAdapter
import com.a2zsuvidhaa.`in`.databinding.FragmentRegisterUserListingBinding
import com.a2zsuvidhaa.`in`.fragment.BaseFragment
import com.a2zsuvidhaa.`in`.util.AppConstants
import com.a2zsuvidhaa.`in`.util.dialogs.StatusDialog
import com.a2zsuvidhaa.`in`.util.dialogs.UserFilterDialog
import com.a2zsuvidhaa.`in`.util.ents.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class RegisterUserListingFragment :
    BaseFragment<FragmentRegisterUserListingBinding>(R.layout.fragment_register_user_listing) {

    private var key: Int = 0
    lateinit var viewModel: RegisterUserListingViewModel

    companion object {
        fun newInstance(key: Int): RegisterUserListingFragment {
            return RegisterUserListingFragment().apply {
                this.arguments = bundleOf("key" to key)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        key = arguments?.getInt("key") ?: 0
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel =
            ViewModelProvider(this).get(key.toString(), RegisterUserListingViewModel::class.java)
        fetchUsers()

        binding.fabFilter.setOnClickListener {
            UserFilterDialog(
                viewModel.inputMode,
                viewModel.inputText
            ).showDialog(requireActivity()) { inputMode, inputText ->
                viewModel.inputMode = inputMode
                viewModel.inputText = inputText
                fetchUsers()
            }
        }

    }

    private fun fetchUsers() {
        if (key == 0) {
            fetchCompletedUsers()
        }
        else{
            fetchInCompletedUsers()
        }
    }

    private fun fetchCompletedUsers() {
        val adapter =
            CompleteUserPagingAdapter().apply { context = requireContext() }
        binding.recyclerView.setup().adapter = adapter
        lifecycleScope.launchWhenStarted {
            viewModel.fetchCompleteUsers().collectLatest {
                adapter.submitData(it)
            }
        }

        adapter.addLoadStateListener { loadStates ->
            when (loadStates.source.refresh) {
                is LoadState.NotLoading -> {
                    binding.progress.hide()


                    if (adapter.itemCount > 0) {
                        binding.cvContent.show()
                        binding.layoutNoItemFound.hide()
                    } else {
                        binding.layoutNoItemFound.show()
                        binding.cvContent.hide()
                    }
                }
                is LoadState.Loading -> {
                    binding.layoutNoItemFound.hide()
                    binding.progress.show()
                    binding.cvContent.hide()
                }
                is LoadState.Error -> {
                    activity?.showToast("Hello error")
                }

            }
        }

        adapter.onItemClick = { _, item, _ ->
            RegisterUserInfoDialog(requireContext(), item) {

                when (it) {
                    RegisterUserKycType.PAN -> {
                        StatusDialog.alert(
                            requireActivity(),
                            "Please contact with admin for pan number verification"
                        )
                    }
                    RegisterUserKycType.AEPS -> {
                        StatusDialog.alert(
                            requireActivity(),
                            "Please contact with admin for AEPS kyc"
                        )
                    }
                    else -> activity?.launchIntent(
                        RegisterUserKycActivity::class.java, bundleOf(
                            "kyc_type" to it,
                            "user" to item
                        )
                    )
                }
            }
        }
    }

    private fun fetchInCompletedUsers() {
        val adapter =
            InCompleteUserPagingAdapter().apply { context = requireContext() }
        binding.recyclerView.setup().adapter = adapter
        lifecycleScope.launchWhenStarted {
            viewModel.fetchInCompleteUsers().collectLatest {
                adapter.submitData(it)
            }
        }

        adapter.addLoadStateListener { loadStates ->
            when (loadStates.source.refresh) {
                is LoadState.NotLoading -> {
                    binding.progress.hide()
                    if (adapter.itemCount > 0) {
                        binding.cvContent.show()
                        binding.layoutNoItemFound.hide()
                    } else {
                        binding.layoutNoItemFound.show()
                        binding.cvContent.hide()
                    }
                }
                is LoadState.Loading -> {
                    binding.layoutNoItemFound.hide()
                    binding.progress.show()
                    binding.cvContent.hide()
                }
                is LoadState.Error -> {
                    activity?.showToast("Hello error")
                }

            }
        }

        adapter.onItemClick = { _, item, _ ->
            activity?.launchIntent(RegistrationActivity::class.java, bundleOf(
                AppConstants.IS_SELF_REGISTRATION to false,
                AppConstants.SHOULD_MAP_ROLE to true,
                AppConstants.DATA to item.mobile
            ))
        }
    }

}