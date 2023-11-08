package com.a2zsuvidhaa.`in`.activity.register

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.a2zsuvidhaa.`in`.AppPreference
import com.a2zsuvidhaa.`in`.R
import com.a2zsuvidhaa.`in`.adapter.user.RegistrationUserPagingAdapter
import com.a2zsuvidhaa.`in`.data.response.RegistrationRoleUser
import com.a2zsuvidhaa.`in`.databinding.ActivityRegistrationRoleMapBinding
import com.a2zsuvidhaa.`in`.util.AppConstants
import com.a2zsuvidhaa.`in`.util.AppUtil
import com.a2zsuvidhaa.`in`.util.dialogs.Dialogs
import com.a2zsuvidhaa.`in`.util.dialogs.UserFilterDialog
import com.a2zsuvidhaa.`in`.util.ents.hide
import com.a2zsuvidhaa.`in`.util.ents.setup
import com.a2zsuvidhaa.`in`.util.ents.setupToolbar
import com.a2zsuvidhaa.`in`.util.ents.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class RegistrationRoleMapActivity : AppCompatActivity() {


    lateinit var binding: ActivityRegistrationRoleMapBinding
    private val viewModel: RegistrationRoleMapViewModel by viewModels()

    @Inject
    lateinit var appPreference: AppPreference

    private var createRoleId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationRoleMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createRoleId = intent.getIntExtra("create_role", 0)
        viewModel.setupCreateRole(createRoleId)

        viewModel.titleObs.observe(this) {
            binding.tvTitle.text = it
        }

        setupToolbar(binding.toolbar, "Map User")

        fetchUsers()
        binding.btnFetchDistributor.setOnClickListener {
            viewModel.isFetchDistributor = true
            viewModel.setUserType()
            viewModel.inputMode = ""
            viewModel.inputText = ""
            fetchUsers()
        }

    }

    private fun fetchUsers() {

        binding.cvFetchDistributor.hide()
        val adapter =
            RegistrationUserPagingAdapter().apply { context = this@RegistrationRoleMapActivity }
        binding.recyclerView.setup().adapter = adapter
        lifecycleScope.launchWhenStarted {
            viewModel.fetchUsers().collectLatest {
                adapter.submitData(it)
            }
        }


        adapter.onItemClick = { _, item, _ ->


            if (viewModel.titleObs.value == "FOS List" && item.id != 1) {
                viewModel.fosId = item.id
            }
            if (viewModel.userMapperType == RegistrationRoleMapViewModel.UserType.FOS) {
                viewModel.fosId = appPreference.id
            }

            if (item.id == 1) {
                showConfirmDialog("Are you sure to mapped under direct to company", item)
            } else if (viewModel.createUserType == RegistrationRoleMapViewModel.UserType.MD
                && viewModel.userType == RegistrationRoleMapViewModel.UserType.ASM
            ) {
                showConfirmDialog("Are you sure to mapped under (${item.userDetails})", item)
            } else if (viewModel.createUserType == RegistrationRoleMapViewModel.UserType.Distributor
                && viewModel.userType == RegistrationRoleMapViewModel.UserType.FOS
            ) {
                showConfirmDialog("Are you sure to mapped under (${item.userDetails})", item)
            } else if (viewModel.createUserType == RegistrationRoleMapViewModel.UserType.Retailer
                && viewModel.userType == RegistrationRoleMapViewModel.UserType.MD
            ) {
                showConfirmDialog("Are you sure to mapped under (${item.userDetails})", item)
            } else {
                viewModel.setUserType()
                viewModel.userIds.add(item.id.toString())
                viewModel.inputMode = ""
                viewModel.inputText = ""
                fetchUsers()
            }

        }



        adapter.addLoadStateListener { loadStates ->
            when (loadStates.source.refresh) {
                is LoadState.NotLoading -> {
                    binding.progress.hide()
                    binding.cvContent.show()

                    if (adapter.itemCount > 0) {
                        binding.layoutNoItemFound.hide()
                    } else binding.layoutNoItemFound.show()
                }
                is LoadState.Loading -> {
                    binding.layoutNoItemFound.hide()
                    binding.progress.show()
                    binding.cvContent.hide()
                }
                is LoadState.Error -> {
                }

            }
        }

        adapter.addLoadStateListener {

            if (viewModel.createUserType == RegistrationRoleMapViewModel.UserType.Retailer) {
                if (viewModel.titleObs.value == "MD List") {
                    if (adapter.itemCount ==1) {
                        binding.cvFetchDistributor.show()
                    }
                }
            }
            else if(viewModel.userType == RegistrationRoleMapViewModel.UserType.FOS
                && viewModel.titleObs.value == "MD List"
                && adapter.itemCount ==1){
                binding.cvFetchDistributor.show()
            }
        }

    }

    private fun showConfirmDialog(message: String, item: RegistrationRoleUser) {

        item.relationId = viewModel.fosId

        if (createRoleId == 3) {//md
            item.id = 1

        }

        Dialogs.commonConfirmDialog(this, message).apply {
            findViewById<Button>(R.id.btn_confirm).setOnClickListener {
                setResult(RESULT_OK, intent.putExtra(AppConstants.DATA, item))
                finish()
            }
        }
    }

    override fun onBackPressed() {
        if (viewModel.userMapperType == viewModel.userType) {
            super.onBackPressed()
        } else {
            viewModel.inputMode = ""
            viewModel.inputText = ""
            viewModel.setUserType(true)
            if (viewModel.isFetchDistributor) {
                viewModel.isFetchDistributor = false
            } else viewModel.userIds.removeLast()
            fetchUsers()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_filter, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.menu_filter) {
            UserFilterDialog(
                viewModel.inputMode,
                viewModel.inputText
            ).showDialog(this) { inputMode, inputText ->
                viewModel.inputMode = inputMode
                viewModel.inputText = inputText
                fetchUsers()
            }
        }
        return super.onOptionsItemSelected(item)
    }


}