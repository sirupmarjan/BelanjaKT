package com.quick.belanjakt.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.FirebaseFirestore
import com.quick.belanjakt.R
import com.quick.belanjakt.databinding.ActivityCheckBinding

class CheckActivity : AppCompatActivity() {
    lateinit var binding : ActivityCheckBinding
    val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}