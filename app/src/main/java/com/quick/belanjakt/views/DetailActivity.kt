package com.quick.belanjakt.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.quick.belanjakt.R
import com.quick.belanjakt.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    lateinit var binding : ActivityDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        val v : View = binding.root
        setContentView(v)
        val ss:String = intent.getStringExtra("doc").toString()
        binding.tvDoc.text = ss


    }
}