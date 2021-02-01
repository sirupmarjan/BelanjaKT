package com.quick.belanjakt.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.quick.belanjakt.R
import com.quick.belanjakt.databinding.ActivitySimpelViewBinding
import com.quick.belanjakt.viewmodels.SimpelAdapter

class SimpelViewActivity : AppCompatActivity() {
    lateinit var binding : ActivitySimpelViewBinding
    var mAdapter : SimpelAdapter = SimpelAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySimpelViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.rvList.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(this@SimpelViewActivity)
        }

    }
}