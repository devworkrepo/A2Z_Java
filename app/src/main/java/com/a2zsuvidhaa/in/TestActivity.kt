package com.a2zsuvidhaa.`in`

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.a2zsuvidhaa.`in`.adapter.TestAdapter
import com.a2zsuvidhaa.`in`.databinding.ActivityTestBinding
import com.a2zsuvidhaa.`in`.util.ents.setup

class TestActivity : AppCompatActivity() {

    private lateinit var binding : ActivityTestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.recyclerView.setup().adapter = TestAdapter().apply {
            addItems(arrayListOf(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30))
        }

    }


}