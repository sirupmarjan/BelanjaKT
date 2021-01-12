package com.quick.belanjakt.viewmodels

import android.content.Context
import android.database.Cursor
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.quick.belanjakt.R
import java.text.NumberFormat
import java.util.*

class KontenAdapter(c: Cursor?, cxt: Context?, lyt : Int?) : RecyclerView.Adapter<KontenAdapter.Holder>() {
    private val cursor: Cursor = c!!
    private val context : Context = cxt!!
    private val layout : Int = lyt!!
    lateinit var mStorageReference: StorageReference
    private lateinit var formatRupiah: NumberFormat

    class Holder(v: View):RecyclerView.ViewHolder(v) {
        val mTumbnail : ImageView = v.findViewById(R.id.iv_konten)
        val mNamaKonten : TextView = v.findViewById(R.id.tv_namaKonten)
        val mHargaKonten : TextView = v.findViewById(R.id.tv_hargaKonten)
        val mDiskonKonten : TextView = v.findViewById(R.id.tv_diskonKonten)
        val mRating : RatingBar = v.findViewById(R.id.ratingBar)
        val mRatingScore : TextView = v.findViewById(R.id.tv_ratingScore)
        val mFreeShiping : ImageView = v.findViewById(R.id.iv_freeOngkir)
        val mDiscPercent : TextView = v.findViewById(R.id.tv_persenDiskon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
       return Holder(LayoutInflater.from(parent.context).inflate(R.layout.layout_grid, parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        mStorageReference = FirebaseStorage.getInstance().getReference()
        val localeID = Locale("in", "ID")
        formatRupiah = NumberFormat.getCurrencyInstance(localeID)

        cursor.moveToPosition(position)
        val pathString : String = cursor.getString(cursor.getColumnIndex("image_url"))
        val judul : String = cursor.getString(cursor.getColumnIndex("judul"))
        val diskon : Int = cursor.getInt(cursor.getColumnIndex("harga_awal"))
        val harga : Int = cursor.getInt(cursor.getColumnIndex("harga"))
        val rating : Int = cursor.getInt(cursor.getColumnIndex("rating"))
        val freeOngkir : Int = cursor.getInt(cursor.getColumnIndex("free_shipping"))
        val riversRef: StorageReference = mStorageReference.child(pathString)

        holder.mTumbnail.clipToOutline = true
        riversRef.downloadUrl.addOnSuccessListener { Uri ->
            Glide.with(context)
                .load(Uri.toString())
                .centerCrop()
                .into(holder.mTumbnail)
        }
        holder.mNamaKonten.text = judul

        if (diskon == 0){
            holder.mDiskonKonten.visibility = View.GONE
            holder.mDiscPercent.visibility = View.GONE
        }else{
            var disc : Long = diskon.toLong()-harga.toLong()
            var percentDiscount :Long = disc * 100 / diskon.toLong()
            holder.mDiscPercent.text = "-$percentDiscount%"
        }

        holder.mDiskonKonten.apply {
            paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            text = formatRupiah.format(diskon)
        }

        holder.mRating.rating = rating.toFloat()
        holder.mRatingScore.text = "($rating)"
        holder.mHargaKonten.text = formatRupiah.format(harga)
        if (freeOngkir != 1){
            holder.mFreeShiping.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return cursor.count
    }
}