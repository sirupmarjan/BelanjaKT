package com.quick.belanjakt.viewmodels

import android.content.ContentValues
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.quick.belanjakt.R
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import kotlin.properties.Delegates

class SimpelAdapter() : RecyclerView.Adapter<SimpelAdapter.Holder>() {
    private val db = FirebaseFirestore.getInstance()
    var ini: Int = 0
    class Holder(v: View) : RecyclerView.ViewHolder(v) {
        val mNama: TextView = v.findViewById(R.id.tvNama)
        val nHarga: TextView = v.findViewById(R.id.tvHarga)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            LayoutInflater.from(parent.context).inflate(R.layout.layout_simple_list, parent, false)
        )
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        db.collection("konten").get().addOnSuccessListener { task ->
            var a = ""
            for (document in task) {
                    a =  document.data["judul"].toString()
                   val b =  document.data["judul"].toString()
//
            }
            holder.mNama.text = a
        }
    }

    override fun getItemCount(): Int {
        return 15
    }
}






