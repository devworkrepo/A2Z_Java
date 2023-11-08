package com.a2zsuvidhaa.`in`.activity.aeps

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.a2zsuvidhaa.`in`.R
import com.a2zsuvidhaa.`in`.databinding.ActivityAepsSettlementBinding
import com.a2zsuvidhaa.`in`.model.BankDetail
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AepsSettlementActivityKT : AppCompatActivity(),
        SettlementBankListFragment.SettlementBankListFragmentListener {

    private lateinit var binding: ActivityAepsSettlementBinding

    private var listener : OnFileImagePickerResult ? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAepsSettlementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setFragment(SettlementBankListFragment.newInstance(),false)


    }

    private fun setFragment(fragment: Fragment, backStack: Boolean = true) {



        val fragmentTransition = supportFragmentManager.beginTransaction().add(R.id.fragment_container, fragment)
        if (backStack) fragmentTransition.addToBackStack(null)
        fragmentTransition.commit()
    }

    override fun onAddBankFragment() {
        setFragment(SettlementAddBankFragment.newInstance(),)
    }

    override fun onSettlementFragment(bankDetail: BankDetail) {
        setFragment(SettlementFragment.newInstance(bankDetail),)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        listener?.fileImagePickerResult(requestCode,resultCode,data)

    }

    fun  setOnDataListener(listener : OnFileImagePickerResult){
        this.listener=listener
    }



    interface OnFileImagePickerResult {
        fun fileImagePickerResult(requestCode: Int, resultCode: Int, data: Intent?)
    }


}