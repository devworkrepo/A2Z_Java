package com.a2zsuvidhaa.`in`.activity.register.kyc

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.a2zsuvidhaa.`in`.R
import com.a2zsuvidhaa.`in`.activity.register.user.RegisterUserKycType
import com.a2zsuvidhaa.`in`.data.response.RegisterCompleteUser
import com.a2zsuvidhaa.`in`.databinding.ActivityFragCommonContainerBinding
import com.a2zsuvidhaa.`in`.fragment.addhar_kyc.AadhaarKycFragment
import com.a2zsuvidhaa.`in`.fragment.document_kyc.FragmentDocumentKyc
import com.a2zsuvidhaa.`in`.util.ents.setupToolbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterUserKycActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFragCommonContainerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFragCommonContainerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val kycType: RegisterUserKycType =
            intent.getSerializableExtra("kyc_type") as RegisterUserKycType
        val item = intent.getParcelableExtra<RegisterCompleteUser>("user")

        val (fragment, title) = when (kycType) {
            RegisterUserKycType.AADHAAR ->
                Pair(AadhaarKycFragment.newInstance(item?.id.toString()), "Aadhaar Kyc")
            RegisterUserKycType.DOCUMENT ->
                Pair(
                    FragmentDocumentKyc.newInstance("register", item?.id.toString()),
                    "Document Kyc"
                )
            else -> Pair(null, null)
        }

        setupToolbar(binding.toolbar, title.toString(), item?.userDetails)
        setFragment(fragment!!)
    }

    private fun setFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.fl_container, fragment).commit()
    }
}