package com.quick.belanjakt.views

import android.app.Activity
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.quick.belanjakt.databinding.ActivityDetailBinding
import com.quick.belanjakt.models.ContentOfflineDatabase
import kotlinx.coroutines.*
import java.text.NumberFormat
import java.util.*


class DetailActivity : AppCompatActivity() {
    lateinit var binding: ActivityDetailBinding
    lateinit var kontenHelper: ContentOfflineDatabase
    private lateinit var formatRupiah: NumberFormat
    lateinit var storageReference: StorageReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        val v: View = binding.root
        setContentView(v)
        supportActionBar?.hide()
        val ss: String = intent.getStringExtra("doc").toString()
        val localeID = Locale("in", "ID")
        formatRupiah = NumberFormat.getCurrencyInstance(localeID)
        kontenHelper = ContentOfflineDatabase(this)
        getItemRun(ss)


    }

    private fun getItemRun(
        documentID: String,
    ) = CoroutineScope(Dispatchers.IO).launch {
        lateinit var nNamaKonten: String
        lateinit var nDeskripsi: String
        lateinit var nImageUrl: String
        var nHarga: Int = 0
        var nHargaAwal: Int = 0
        var nRating: Int = 0
        var nFreeOngkir: Int = 0
        val db: SQLiteDatabase = kontenHelper.getWritableDatabase()
        val mQuery2 = "SELECT * FROM tb_konten where documentID = '$documentID'"
        val c = db.rawQuery(mQuery2, null)
        while (c.moveToNext()) {
            nNamaKonten = c.getString(c.getColumnIndex("judul"))
            nHarga = c.getInt(c.getColumnIndex("harga"))
            nHargaAwal = c.getInt(c.getColumnIndex("harga_awal"))
            nDeskripsi = c.getString(c.getColumnIndex("deskripsi"))
            nFreeOngkir = c.getInt(c.getColumnIndex("free_shipping"))
            nRating = c.getInt(c.getColumnIndex("rating"))
            nImageUrl = c.getString(c.getColumnIndex("image_url"))
            withContext(Dispatchers.Main) {
                setView(
                    nNamaKonten,
                    nHarga,
                    nHargaAwal,
                    nDeskripsi,
                    nFreeOngkir,
                    nRating,
                    nImageUrl
                )
            }
        }
    }

    private fun setView(
        nNamaKonten: String?,
        nHarga: Int,
        nHargaAwal: Int,
        nDeskripsi: String?,
        nFreeOngkir: Int,
        nRating: Int,
        nImageUrl: String?
    ) = CoroutineScope(Dispatchers.Main).launch {
        storageReference = FirebaseStorage.getInstance().getReference()
        val riversRef: StorageReference = storageReference.child(nImageUrl!!)
        riversRef.downloadUrl.addOnSuccessListener { Uri ->
//            if (isValidContextForGlide(this)){
//                Glide.with(this)
//                    .load(Uri.toString())
//                    .centerCrop()
//                    .into(binding.ivProduk)
//            }
            Glide.with(this@DetailActivity)
                .load(Uri.toString())
                .centerCrop()
                .into(binding.ivProduk)
//            Picasso.get()
//                .load(Uri.toString())
//                .resize(50, 50)
//                .centerCrop()
//                .into(binding.ivProduk)
        }


        binding.tvNamaKonten.text = nNamaKonten
        binding.tvDescriptionKonten.text = nDeskripsi
        binding.tvHargaKonten.text = formatRupiah.format(nHarga)
        if (nHargaAwal != 0) {
            var disc: Long = nHargaAwal.toLong() - nHarga.toLong()
            var percenDiscount: Long = disc * 100 / nHargaAwal.toLong()
            binding.tvDiskonKonten.apply {
                paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                text = formatRupiah.format(nHargaAwal)
            }
            binding.tvPersenDiskon.text = "-$percenDiscount%"
        } else {
            binding.tvDiskonKonten.visibility = View.GONE
            binding.tvPersenDiskon.visibility = View.GONE
        }
        if (nFreeOngkir == 0) {
            binding.ivFreeOngkir.visibility = View.GONE
        }
        binding.ratingBarKonten.rating = nRating.toFloat()

    }

    fun isValidContextForGlide(context: Context?): Boolean {
        if (context == null) {
            return false
        }
        if (context is Activity) {
            val activity = context as Activity
            if (activity.isDestroyed || activity.isFinishing) {
                return false
            }
        }
        return true
    }
//    override fun onBackPressed() {
//        Toast.makeText(this, " back pressed", Toast.LENGTH_SHORT).show()
//    }
}
