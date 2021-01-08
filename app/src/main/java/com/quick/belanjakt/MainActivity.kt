package com.quick.belanjakt

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.quick.belanjakt.views.DashBoardActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File


private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    lateinit var mStorageReference: StorageReference
    lateinit var selectedImage : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mStorageReference = FirebaseStorage.getInstance().getReference()

        var imageOption = arrayOf("sun eater", "sirupmarjan")
        val mAdapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, imageOption)
        mAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        sp_image.apply {
            adapter = mAdapter
            onItemSelectedListener = this@MainActivity
        }


        btn_select.setOnClickListener {
            val intent = Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT)

            startActivityForResult(Intent.createChooser(intent, "Select a file"), 111)
        }

        btn_download.setOnClickListener {
            downloadProcedure()
        }

        btn_switch.setOnClickListener {
            startActivity(Intent(this@MainActivity, DashBoardActivity::class.java))
        }
    }

    private fun downloadProcedure() {
        val localFile = File.createTempFile("images", ".jpg")
        val riversRef: StorageReference = mStorageReference.child("data/$selectedImage.jpg")
        val imgView: ImageView = findViewById(R.id.iv_sirup)

        riversRef.downloadUrl.addOnSuccessListener { Uri ->
            var strImage : String = Uri.toString()
            Glide.with(this)
                .load(strImage)
                .into(imgView)
        }
    }

    private fun uploadProcedure(dataSelected: Uri) {

//        val file: Uri = Uri.fromFile(File("path/to/images/rivers.jpg"))
        val file: Uri = dataSelected
        val riversRef: StorageReference = mStorageReference.child("data/example.jpg")

        riversRef.putFile(dataSelected)
            .addOnSuccessListener { taskSnapshot -> // Get a URL to the uploaded content
                val downloadUrl: String = taskSnapshot.metadata?.reference?.downloadUrl.toString()
            }
            .addOnFailureListener {
                // Handle unsuccessful uploads
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 111 && resultCode == RESULT_OK) {
            val selectedFile : String = data?.data.toString() //The uri with the location of the file
            tv_contentAddress.text = selectedFile+" and type : " + selectedFile.substringAfterLast(".","")

            if (data != null) {
                uploadProcedure(data.data!!)
            }

        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
       Toast.makeText(this, parent?.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show()
        selectedImage = parent?.getItemAtPosition(position).toString()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}