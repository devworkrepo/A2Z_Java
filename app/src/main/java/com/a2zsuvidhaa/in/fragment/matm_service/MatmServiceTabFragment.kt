package com.a2zsuvidhaa.`in`.fragment.matm_service

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.a2zsuvidhaa.`in`.R
import com.a2zsuvidhaa.`in`.adapter.MatmServiceTabAdapter
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MatmServiceTabFragment : Fragment() {

    private lateinit var viewpager: ViewPager
    private lateinit var tabs: TabLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_tab_container, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewpager = view.findViewById(R.id.viewpager)
        tabs = view.findViewById(R.id.tabs)
        renderViewPager()
    }

    companion object {
        fun newInstance() = MatmServiceTabFragment()
    }


    private fun renderViewPager() {

        val viewPagerAdapter = MatmServiceTabAdapter(childFragmentManager)
        viewpager.adapter = viewPagerAdapter
        tabs.setupWithViewPager(viewpager)
    }

}
