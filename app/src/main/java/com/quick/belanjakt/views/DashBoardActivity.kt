package com.quick.belanjakt.views

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.quick.belanjakt.R
import com.quick.belanjakt.models.ContentModel
import kotlinx.android.synthetic.main.activity_dash_board.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "DashBoardActivity"

class DashBoardActivity : AppCompatActivity() {
    var db = FirebaseFirestore.getInstance()
    lateinit var selectedFile: String
    lateinit var mStorageReference: StorageReference
    lateinit var selectedFileUri: Uri
    lateinit var selectedFileExtension: String
    lateinit var selectedFileUploadReference :String
    lateinit var selectedFileUploadname :String
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

        btn_process.setOnClickListener {
            getContent()
        }
        btnView.setOnClickListener { startActivity(Intent(this@DashBoardActivity, ViewPageActivity::class.java)) }

    }

    private fun getContent() {
        val realTime = SimpleDateFormat("yyyyMMddhhmmss")

        var mJudul: String = et_judul.text.toString()
        var mDeskripsi: String = et_deskripsi.text.toString()
        var mHarga: Int = et_harga.text.toString().toInt()
        val realDate = realTime.format(Date())

        if (!mJudul.equals("") || !mDeskripsi.equals("") ||  mHarga != null || !selectedFile.equals("")){
            val mContent = ContentModel(
                mJudul,
                mDeskripsi,
                mHarga,
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
            selectedFile = data?.data.toString() //The uri with the location of the file
            selectedFileExtension = selectedFile.substringAfterLast(".", "")
//            tv_contentAddress.text = selectedFile+" and type : " + selectedFile.substringAfterLast(".","")
           val refDataUpload : String = "data/${getRandomString(16)}.$selectedFileExtension"
            selectedFileUploadReference = refDataUpload

        }
    }
}