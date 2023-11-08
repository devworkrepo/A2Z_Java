package com.a2zsuvidhaa.`in`.adapter.user

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.a2zsuvidhaa.`in`.activity.register.user.RegisterUserListingFragment
import com.a2zsuvidhaa.`in`.databinding.FragmentRegisterUserListingBinding
import com.a2zsuvidhaa.`in`.fragment.matm_service.matm.MatmServiceRequestFragment
import com.a2zsuvidhaa.`in`.fragment.matm_service.mpos.MposServiceRequestFragment

class RegistrationUserTabAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {

    override fun getItem(position: Int): Fragment {
        var fragment: Fragment? = null
        when (position) {
            0 -> fragment = RegisterUserListingFragment.newInstance(0)
            1 -> fragment = RegisterUserListingFragment.newInstance(1)
        }
        return fragment!!
    }

    override fun getCount()= 2


    override fun getPageTitle(position: Int): CharSequence? {
        var title: String? = null
        when (position) {
            0 -> title = "Completed"
            1 -> title = "In-Completed"
        }
        return title
    }
}