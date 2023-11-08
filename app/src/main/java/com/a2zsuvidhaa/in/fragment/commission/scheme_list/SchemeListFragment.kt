package com.a2zsuvidhaa.`in`.fragment.commission.scheme_list

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.navigation.NavArgs
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.a2zsuvidhaa.`in`.R
import com.a2zsuvidhaa.`in`.adapter.report.SchemeListAdapter
import com.a2zsuvidhaa.`in`.databinding.FragmentCommonReportBinding
import com.a2zsuvidhaa.`in`.fragment.BaseFragment
import com.a2zsuvidhaa.`in`.fragment.commission.scheme_detail.SchemeDetailFragmentArgs
import com.a2zsuvidhaa.`in`.util.apis.Resource
import com.a2zsuvidhaa.`in`.util.dialogs.StatusDialog
import com.a2zsuvidhaa.`in`.util.ents.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SchemeListFragment : BaseFragment<FragmentCommonReportBinding>(R.layout.fragment_common_report) {


    private val viewModel: SchemeListViewModel by viewModels()


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupToolbar(binding.layoutToolbar.toolbar,"Commission Schemes")


        if(viewModel.isFetched.not())viewModel.fetchSchemeList()


        subscribeObserver()
    }

    private fun subscribeObserver() {
        viewModel.schemeListObs.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    binding.apply {
                        this.layoutProgress.show()
                        this.recyclerView.hide()
                    }
                }
                is Resource.Success -> {
                    binding.layoutProgress.hide()

                    if(it.data.status == 1){
                        binding.recyclerView.show()
                        viewModel.isFetched = true

                       val adapter =  SchemeListAdapter().apply {
                            this.addItems(it.data.result!!)
                        }

                        binding.recyclerView.setup().adapter = adapter
                        adapter.onItemClick = {_,item,_->
                          findNavController().navigate(  SchemeListFragmentDirections.actionCommissionSchemeListFragmentToCommissionSchemeFragment(
                                  type = item.type,
                                  title = item.title
                          ))
                        }
                    }
                    else StatusDialog.failure(requireActivity(),it.data.message){
                        activity?.onBackPressed()
                    }
                }
                is Resource.Failure -> {
                    binding.apply {
                        this.layoutProgress.hide()
                        activity?.handleNetworkFailure(it.exception)

                    }
                }
            }
        }
    }
}