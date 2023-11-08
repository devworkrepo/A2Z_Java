package com.a2zsuvidhaa.`in`.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.a2zsuvidhaa.`in`.fragment.matm_service.matm.MatmServiceRequestFragment
import com.a2zsuvidhaa.`in`.fragment.matm_service.mpos.MposServiceRequestFragment

class MatmServiceTabAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {

    override fun getItem(position: Int): Fragment {
        var fragment: Fragment? = null
        when (position) {
            0 -> fragment = MatmServiceRequestFragment.newInstance()
            1 -> fragment = MposServiceRequestFragment.newInstance()
        }
        return fragment!!
    }

    override fun getCount()= 2


    override fun getPageTitle(position: Int): CharSequence? {
        var title: String? = null
        when (position) {
            0 -> title = "M-ATM Service"
            1 -> title = "M-POS Service"
        }
        return title
    }
}