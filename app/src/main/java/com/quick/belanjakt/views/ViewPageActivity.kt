package com.quick.belanjakt.views

import android.R.attr.spacing
import android.content.ContentValues
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.quick.belanjakt.R
import com.quick.belanjakt.databinding.ActivityViewPageBinding
import com.quick.belanjakt.models.ContentOfflineDatabase
import com.quick.belanjakt.viewmodels.KontenAdapter
import kotlinx.android.synthetic.main.activity_view_page.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class ViewPageActivity : AppCompatActivity() {
    lateinit var binding: ActivityViewPageBinding
    private val db = FirebaseFirestore.getInstance()
    lateinit var mAdapter: KontenAdapter
    lateinit var helperKonten: ContentOfflineDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewPageBinding.inflate(layoutInflater)
        val v: View = binding.root
        setContentView(v)
        supportActionBar?.hide()
        helperKonten = ContentOfflineDatabase(this)
        helperKonten.delete()
//        helperKonten.createTable()
        getData()
        binding.rgLayout.setOnCheckedChangeListener { group, checkedId ->
            initView()
        }
        binding.btnSearch.setOnClickListener {
            initView()
        }

    }

    private fun getData() = CoroutineScope(Dispatchers.IO).launch {
        db.collection("konten").get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        val value = ContentValues()
                        value.put("documentID", document.id)
                        value.put("judul", document.data["judul"].toString())
                        value.put("harga", document.data["harga"].toString().toInt())
                        value.put("harga_awal", document.data["hargaAwal"].toString().toInt())
                        value.put("free_shipping", document.data["freeOngkir"].toString().toInt())
                        value.put("rating", document.data["rating"].toString().toInt())
                        value.put("image_url", document.data["imageUrl"].toString())
                        value.put("deskripsi", document.data["deskripsi"].toString())
                        value.put("real_date", document.data["realdate"].toString())
                        helperKonten.insertData(value)
                    }
                }
            }.await()
        withContext(Dispatchers.Main) {
            initView()
        }

    }

    private fun initView() {
//        if (binding.rbListView.isChecked) {
//            mAdapter = KontenAdapter(helperKonten.select(), this, R.layout.layout_grid)
//            binding.rvList.apply {
//                adapter = mAdapter
//                layoutManager = GridLayoutManager(this@ViewPageActivity, 2)
//                addItemDecoration(GridItemDecoration(10,2 ))
//            }
//        } else {
//            mAdapter = KontenAdapter(helperKonten.select(), this, R.layout.layout_grid)
//            binding.rvList.apply {
//                adapter = mAdapter
//                layoutManager = GridLayoutManager(this@ViewPageActivity, 2)
//                addItemDecoration(GridItemDecoration(10,2))
//            }

        if (binding.etSeach.text.toString().equals("") || binding.etSeach.text.toString().equals(" ")) {
            mAdapter = KontenAdapter(helperKonten.select(), this, R.layout.layout_grid, )
            binding.rvList.apply {
                adapter = mAdapter
                layoutManager = GridLayoutManager(this@ViewPageActivity, 2)
            }
            binding.lloverlay.visibility = View.GONE
        }else{
            mAdapter = KontenAdapter(helperKonten.selectBy(binding.etSeach.text.toString()), this, R.layout.layout_grid)
            binding.rvList.apply {
                adapter = mAdapter
                layoutManager = GridLayoutManager(this@ViewPageActivity, 2)
            }
            binding.lloverlay.visibility = View.GONE

        }

    }


    class GridItemDecoration(gridSpacingPx: Int, gridSize: Int) :
        RecyclerView.ItemDecoration() {
        private var mSizeGridSpacingPx: Int = gridSpacingPx
        private var mGridSize: Int = gridSize

        private var mNeedLeftSpacing = false

        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val frameWidth =
                ((parent.width - mSizeGridSpacingPx.toFloat() * (mGridSize - 1)) / mGridSize).toInt()
            val padding = parent.width / mGridSize - frameWidth
            val itemPosition =
                (view.getLayoutParams() as RecyclerView.LayoutParams).viewAdapterPosition
            if (itemPosition < mGridSize) {
                outRect.top = 0
            } else {
                outRect.top = mSizeGridSpacingPx
            }
            if (itemPosition % mGridSize == 0) {
                outRect.left = 0
                outRect.right = padding
                mNeedLeftSpacing = true
            } else if ((itemPosition + 1) % mGridSize == 0) {
                mNeedLeftSpacing = false
                outRect.right = 0
                outRect.left = padding
            } else if (mNeedLeftSpacing) {
                mNeedLeftSpacing = false
                outRect.left = mSizeGridSpacingPx - padding
                if ((itemPosition + 2) % mGridSize == 0) {
                    outRect.right = mSizeGridSpacingPx - padding
                } else {
                    outRect.right = mSizeGridSpacingPx / 2
                }
            } else if ((itemPosition + 2) % mGridSize == 0) {
                mNeedLeftSpacing = false
                outRect.left = mSizeGridSpacingPx / 2
                outRect.right = mSizeGridSpacingPx - padding
            } else {
                mNeedLeftSpacing = false
                outRect.left = mSizeGridSpacingPx / 2
                outRect.right = mSizeGridSpacingPx / 2
            }
            outRect.bottom = 0
        }
    }
}