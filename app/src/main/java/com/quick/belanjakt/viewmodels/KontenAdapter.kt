package com.quick.belanjakt.viewmodels

import android.app.Activity
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.quick.belanjakt.R

class KontenAdapter(c: Cursor?, cxt: Context?) : RecyclerView.Adapter<KontenAdapter.Holder>() {
    private val cursor: Cursor = c!!
    private val context : Context = cxt!!
    lateinit var mStorageReference: StorageReference
    class Holder(v: View):RecyclerView.ViewHolder(v) {
        val mTumbnail : ImageView = v.findViewById(R.id.iv_konten)
        val mNamaKonten : TextView = v.findViewById(R.id.tv_namaKonten)
        val mHargaKonten : TextView = v.findViewById(R.id.tv_hargaKonten)
        val mDeskripsiKonten : TextView = v.findViewById(R.id.tv_deskripsiKonten)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
       return Holder(LayoutInflater.from(parent.context).inflate(R.layout.layout_list, parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        mStorageReference = FirebaseStorage.getInstance().getReference()
        cursor.moveToPosition(position)
        val pathString : String = cursor.getString(cursor.getColumnIndex("image_url"))
        val judul : String = cursor.getString(cursor.getColumnIndex("judul"))
        val harga : String = cursor.getString(cursor.getColumnIndex("harga"))
        val deskripsi : String = cursor.getString(cursor.getColumnIndex("deskripsi"))
        val riversRef: StorageReference = mStorageReference.child(pathString)

        riversRef.downloadUrl.addOnSuccessListener { Uri ->
            Glide.with(context)
                .load(Uri.toString())
                .centerCrop()
                .into(holder.mTumbnail)
        }
        holder.mNamaKonten.text = judul
        holder.mHargaKonten.text = harga
        holder.mDeskripsiKonten.text = deskripsi
    }

    override fun getItemCount(): Int {
        return cursor.count
    }
}