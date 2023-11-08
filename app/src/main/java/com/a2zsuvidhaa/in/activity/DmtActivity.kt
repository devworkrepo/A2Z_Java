package com.a2zsuvidhaa.`in`.activity

import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import com.a2zsuvidhaa.`in`.R
import com.a2zsuvidhaa.`in`.data.model.BankDownResponse
import com.a2zsuvidhaa.`in`.fragment.dmt.SearchSenderDmtFragmentArgs
import com.a2zsuvidhaa.`in`.util.AppConstants
import com.a2zsuvidhaa.`in`.util.enums.DmtType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DmtActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dmt)


        val dmtType = intent.getSerializableExtra(AppConstants.DATA) as DmtType

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        navController.setGraph(R.navigation.dmt_nav_graph)
        navController.setGraph(
            navController.graph,
            SearchSenderDmtFragmentArgs(dmtType).toBundle()
        )
    }
}