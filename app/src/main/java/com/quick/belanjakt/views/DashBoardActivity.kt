package com.quick.belanjakt.views

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.RatingBar
import android.widget.Toast
import androidx.core.net.toFile
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.quick.belanjakt.R
import com.quick.belanjakt.models.ContentModel
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.default
import id.zelory.compressor.constraint.destination
import kotlinx.android.synthetic.main.activity_dash_board.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "DashBoardActivity"

class DashBoardActivity : AppCompatActivity(), RatingBar.OnRatingBarChangeListener {
    var db = FirebaseFirestore.getInstance()
    lateinit var selectedFile: String
    lateinit var mStorageReference: StorageReference
    lateinit var selectedFileUri: Uri
    var ratingKonten : Int = 0
    lateinit var selectedFileExtension: String
    lateinit var selectedFileUploadReference :String
    lateinit var selectedFileUploadname :String
    lateinit var compressedImageFile : File
    private val contentCollectionRef = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dash_board)
        mStorageReference = FirebaseStorage.getInstance().getReference()
        btn_uploadTumbnail.setOnClickListener {
            val intent = Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT)

            startActivityForResult(Intent.createChooser(intent, "Select a file"), 111)
        }
        ratingBarInput.onRatingBarChangeListener = this
        cb_diskon.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                txt_hargaAwal.visibility = View.VISIBLE
                et_hargaAwal.visibility = View.VISIBLE
            }else{
                txt_hargaAwal.visibility = View.GONE
                et_hargaAwal.visibility = View.GONE
            }
        }

        btn_process.setOnClickListener {
            getContent()
        }
        btnView.setOnClickListener { startActivity(Intent(this@DashBoardActivity, ViewPageActivity::class.java)) }

    }

    private fun getContent() {
        val realTime = SimpleDateFormat("yyyyMMddhhmmss")
        var mFreeShipping : Int = 0
        if (cb_freeOngkir.isChecked){
            mFreeShipping = 1
        }
        var mJudul: String = et_judul.text.toString()
        var mDeskripsi: String = et_deskripsi.text.toString()
        var mHarga: Int = et_harga.text.toString().toInt()
        var mHargaAwal : Int = 0
        if (!et_hargaAwal.text.toString().equals("")){
            mHargaAwal = et_hargaAwal.text.toString().toInt()
        }
        val realDate = realTime.format(Date())

        if (!mJudul.equals("") || !mDeskripsi.equals("") ||  mHarga != null || !selectedFile.equals("")){
            val mContent = ContentModel(
                mJudul,
                mDeskripsi,
                mHarga,
                mHargaAwal,
                mFreeShipping,
                ratingKonten,
                selectedFileUploadReference,
                realDate
            )
            sendContent(mContent)
        }else{
            Toast.makeText(this, "Ups, anda belum melengkapi seluruh form", Toast.LENGTH_SHORT).show()
        }

    }

    private fun sendContent(mContent: ContentModel) = CoroutineScope(Dispatchers.IO).launch {
        try {
            contentCollectionRef.collection("konten")
                .add(mContent)
            withContext(Dispatchers.IO){
//                uploadTumbnail(selectedFileUri)
                uploadTumbnail(selectedFileUri)

            }

            withContext(Dispatchers.Main){
                Toast.makeText(this@DashBoardActivity, "Success add content", Toast.LENGTH_SHORT).show()
            }

        }catch (e : Exception){
            Log.d(TAG, "sendContent: "+e.localizedMessage)
            withContext(Dispatchers.Main){
                Toast.makeText(this@DashBoardActivity, "Something Wrong", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadTumbnail(dataSelected: Uri) = CoroutineScope(Dispatchers.IO).launch {
        val riversRef: StorageReference = mStorageReference.child(selectedFileUploadReference)
        riversRef.putFile(dataSelected)
            .addOnSuccessListener { taskSnapshot -> // Get a URL to the uploaded content
                val downloadUrl: String = taskSnapshot.metadata?.reference?.downloadUrl.toString()
                Log.d(TAG, "uploadTumbnail: success")
            }
            .addOnFailureListener {
            }
    }

    private fun getRandomString(length: Int) : String {
        val charset = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return (1..length)
            .map { charset.random() }
            .joinToString("")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 111 && resultCode == RESULT_OK) {
            selectedFileUri = data?.data!!
            selectedFile = getRealPathFromURI(this, selectedFileUri) //The uri with the location of the file
//            var fileImage = File(selectedFile)

            CoroutineScope(Dispatchers.IO).launch {
                val folder = filesDir
                val f = File(folder, "tempFile")
                f.mkdir()

                compressedImageFile = Compressor.compress(this@DashBoardActivity, File(selectedFile)){
                    default()
                    destination(f)
                }
            }

            Glide.with(this)
                .load(selectedFileUri)
                .into(iv_preview)

            selectedFileExtension = selectedFile.substringAfterLast(".", "")
//            tv_contentAddress.text = selectedFile+" and type : " + selectedFile.substringAfterLast(".","")
           val refDataUpload : String = "data/${getRandomString(16)}.$selectedFileExtension"
            selectedFileUploadReference = refDataUpload

        }
    }

    override fun onRatingChanged(ratingBar: RatingBar?, rating: Float, fromUser: Boolean) {
        ratingKonten = rating.toInt()
    }

    private fun getRealPathFromURI(context: Context, contentUri: Uri): String {
        var cursor: Cursor? = null
        return try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)

            cursor = context.contentResolver.query(contentUri, proj, null, null, null)

            val column_index: Int = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)

            cursor!!.moveToFirst()

            cursor.getString(column_index)

        } catch (e: Exception) {


            ""
        } finally {
            if (cursor != null) {
                cursor.close()
            }
        }
    }
}