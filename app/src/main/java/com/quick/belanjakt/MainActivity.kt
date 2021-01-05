package com.quick.belanjakt

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File


private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    lateinit var mStorageReference : StorageReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mStorageReference = FirebaseStorage.getInstance().getReference()

        btn_select.setOnClickListener {
            val intent = Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT)

            startActivityForResult(Intent.createChooser(intent, "Select a file"), 111)
        }
    }

    private fun uploadProcedure(dataSelected : Uri) {

//        val file: Uri = Uri.fromFile(File("path/to/images/rivers.jpg"))
        val file: Uri = dataSelected
        val riversRef: StorageReference = mStorageReference.child("data/newFile.txt")

        riversRef.putFile(dataSelected)
            .addOnSuccessListener { taskSnapshot -> // Get a URL to the uploaded content
                val downloadUrl: String = taskSnapshot.metadata?.reference?.downloadUrl.toString()
            }
            .addOnFailureListener {
                // Handle unsuccessful uploads
                // ...
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 111 && resultCode == RESULT_OK) {
            val selectedFile = data?.data //The uri with the location of the file
            tv_contentAddress.text = selectedFile.toString()
            if (selectedFile != null) {
                uploadProcedure(selectedFile)
            }

        }
    }
}