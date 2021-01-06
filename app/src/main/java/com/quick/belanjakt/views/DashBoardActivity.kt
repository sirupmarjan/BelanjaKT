package com.quick.belanjakt.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.quick.belanjakt.R
import com.quick.belanjakt.models.ContentModel
import kotlinx.android.synthetic.main.activity_dash_board.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "DashBoardActivity"

class DashBoardActivity : AppCompatActivity() {
    var db = FirebaseFirestore.getInstance()
    lateinit var selectedFile: String
    lateinit var selectedFileExtension: String
    private val asthCollectionRef = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dash_board)

        btn_uploadTumbnail.setOnClickListener {
            val intent = Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT)

            startActivityForResult(Intent.createChooser(intent, "Select a file"), 111)
        }

        btn_process.setOnClickListener {
            getContent()
        }

    }

    private fun getContent() {
        val realTime = SimpleDateFormat("yyyyMMddhhmmss")

        var mJudul: String = et_judul.text.toString()
        var mDeskripsi: String = et_deskripsi.text.toString()
        var mHarga: Int = et_harga.text.toString().toInt()
        val realDate = realTime.format(Date()).toInt()

        if (mJudul.equals("") || mDeskripsi.equals("") || mHarga == null || selectedFile.equals("")){
            val mContent = ContentModel(
                mJudul,
                mDeskripsi,
                mHarga,
                selectedFile,
                realDate
            )
            sendContent(mContent)
        }else{
            Toast.makeText(this, "Ups, anda belum melengkapi seluruh form", Toast.LENGTH_SHORT).show()
        }

    }

    private fun sendContent(mContent: ContentModel) = CoroutineScope(Dispatchers.IO).launch {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 111 && resultCode == RESULT_OK) {
            selectedFile = data?.data.toString() //The uri with the location of the file
            selectedFileExtension = selectedFile.substringAfterLast(".", "")
//            tv_contentAddress.text = selectedFile+" and type : " + selectedFile.substringAfterLast(".","")
        }
    }
}