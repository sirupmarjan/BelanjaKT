package com.quick.belanjakt.views

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Adapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.quick.belanjakt.R
import com.quick.belanjakt.databinding.ActivityViewPageBinding
import com.quick.belanjakt.models.ContentOfflineDatabase
import com.quick.belanjakt.viewmodels.KontenAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ViewPageActivity : AppCompatActivity() {
    lateinit var binding : ActivityViewPageBinding
    private val db = FirebaseFirestore.getInstance()
    lateinit var mAdapter: KontenAdapter
    lateinit var helperKonten : ContentOfflineDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewPageBinding.inflate(layoutInflater)
        val v : View = binding.root
        setContentView(v)
        helperKonten = ContentOfflineDatabase(this)
        helperKonten.delete()
        getData()
    }

    private fun getData() = CoroutineScope(Dispatchers.IO).launch {
        db.collection("konten").get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    for (document in task.result!!){
                        val value = ContentValues()
                        value.put("documentID", document.id)
                        value.put("judul", document.data["judul"].toString())
                        value.put("harga", document.data["harga"].toString())
                        value.put("image_url", document.data["imageUrl"].toString())
                        value.put("deskripsi", document.data["deskripsi"].toString())
                        value.put("real_date", document.data["realdate"].toString())
                        helperKonten.insertData(value)
                    }
                }
            }.await()
        withContext(Dispatchers.Main){
            initView()
        }

    }

    private fun initView() {
        mAdapter = KontenAdapter(helperKonten.select(),this)
        binding.rvList.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(this@ViewPageActivity)
        }
    }
}