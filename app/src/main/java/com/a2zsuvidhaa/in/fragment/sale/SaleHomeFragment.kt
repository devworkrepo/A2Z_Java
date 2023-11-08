package com.a2zsuvidhaa.`in`.fragment.sale

import android.content.Context
import android.os.Bundle
import androidx.core.os.bundleOf
import com.a2zsuvidhaa.`in`.AppPreference
import com.a2zsuvidhaa.`in`.R
import com.a2zsuvidhaa.`in`.activity.MainActivity
import com.a2zsuvidhaa.`in`.activity.register.RegistrationActivity
import com.a2zsuvidhaa.`in`.activity.register.user.RegisterUserListingActivity
import com.a2zsuvidhaa.`in`.adapter.GridViewUserAdapter
import com.a2zsuvidhaa.`in`.databinding.FragmentSaleHomeBinding
import com.a2zsuvidhaa.`in`.fragment.BaseFragment
import com.a2zsuvidhaa.`in`.listener.OnSaleItemClickListener
import com.a2zsuvidhaa.`in`.util.AppConstants
import com.a2zsuvidhaa.`in`.util.ents.launchIntent
import com.a2zsuvidhaa.`in`.view.GridDividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SaleHomeFragment() : BaseFragment<FragmentSaleHomeBinding>(R.layout.fragment_sale_home) {

    @Inject
    lateinit var appPreference: AppPreference

    companion object {
        fun newInstance() = SaleHomeFragment()

    }
    private var listner: OnSaleItemClickListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listner = activity as MainActivity

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        var mList = arrayListOf<String>()
        if(appPreference.rollId == 22) //RM
            mList = arrayListOf("ASM","API","FOS","MD","Distributor","Retailer")
        else if(appPreference.rollId == 23)//ASM
            mList = arrayListOf("FOS","MD","Distributor","Retailer")
        else if(appPreference.rollId == 24)//FOS
            mList = arrayListOf("MD","Distributor","Retailer")


        binding.recyclerView.addItemDecoration(
            GridDividerItemDecoration(
                spacing = 3
            )
        )
        binding.recyclerView.adapter = GridViewUserAdapter().apply {
            addItems(mList)
            onItemClick = {_,item,_->
                listner?.onSaleItemClick(item)
            }
        }

        binding.llCreateUser.setOnClickListener{
            activity?.launchIntent(RegistrationActivity::class.java, data = bundleOf(
                AppConstants.IS_SELF_REGISTRATION to false,
                AppConstants.SHOULD_MAP_ROLE to true,

            ))
        }

        binding.tvUsers.setOnClickListener{
            activity?.launchIntent(RegisterUserListingActivity::class.java)
        }
    }

}