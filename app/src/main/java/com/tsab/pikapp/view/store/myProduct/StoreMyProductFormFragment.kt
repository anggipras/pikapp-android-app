package com.tsab.pikapp.view.store.myProduct

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentStoreMyProductFormBinding
import com.tsab.pikapp.models.model.StoreImage
import com.tsab.pikapp.models.model.StoreProductList
import com.tsab.pikapp.view.StoreActivity
import com.tsab.pikapp.viewmodel.store.StoreProductFormViewModel
import kotlinx.android.synthetic.main.fragment_store_my_product_form.*


class StoreMyProductFormFragment : Fragment(), StoreImageUploadAdapter.DeleteImageInterface {

    private val storeImageUploadAdapter = StoreImageUploadAdapter(arrayListOf(), this)

    // request code to pick images
    private val PICK_IMAGE_CODE = 9544

    var isAdd = false
    var pid = ""

    private var imageCount = 0

    private lateinit var dataBinding: FragmentStoreMyProductFormBinding
    private lateinit var viewModel: StoreProductFormViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        dataBinding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_store_my_product_form,
                container,
                false
            )
        viewModel = ViewModelProviders.of(this).get(StoreProductFormViewModel::class.java)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataBinding.imageContainer.apply {
            val gridLayoutManager =
                GridLayoutManager(context, 3, LinearLayoutManager.VERTICAL, false)
            layoutManager = gridLayoutManager
            adapter = storeImageUploadAdapter
        }


        arguments?.let {
            isAdd = StoreMyProductFormFragmentArgs.fromBundle(it).isAdd
            pid = StoreMyProductFormFragmentArgs.fromBundle(it).pid
        }

        if (isAdd) {
            dataBinding.textTitle.text = "Tambah Produk"
            dataBinding.buttonActionProduct.text = "Tambah Produk"

        } else {
            dataBinding.textTitle.text = "Ubah Produk"
            dataBinding.buttonActionProduct.text = "Ubah Produk"
            viewModel.getProduct(pid)
        }

        dataBinding.buttonToGallery.setOnClickListener {
            checkStoragePermission()
        }

        dataBinding.buttonActionProduct.setOnClickListener {
            val productName = dataBinding.textProductName.text.trim().toString()
            val textDescription = dataBinding.textDescription.text.trim().toString()
            val price = dataBinding.textPrice.text.trim().toString()
            val condition = dataBinding.textCondition.text.trim().toString()

            if (isAdd) {
                viewModel.submitProduct((activity as StoreActivity), productName, textDescription, price, condition)
            } else {
                val pid = dataBinding.textProductID.text.toString()
                viewModel.editProduct((activity as StoreActivity), pid, productName, textDescription, price)
            }
        }

        observeViewModel()
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun observeViewModel() {

        viewModel.loading.observe(this, Observer {
            if(it) {
                dataBinding.loadingView.visibility = View.VISIBLE
                dataBinding.buttonToGallery.isEnabled = false
                dataBinding.buttonActionProduct.isEnabled = false
            } else {
                dataBinding.loadingView.visibility = View.GONE
                dataBinding.buttonToGallery.isEnabled = true
                dataBinding.buttonActionProduct.isEnabled = true
            }
        })

        viewModel.imageError.observe(this, Observer { t ->
            t?.let {
                dataBinding.textErrorImage.apply {
                    visibility = if (t.isNotEmpty()) View.VISIBLE else View.INVISIBLE
                    text = t
                }
            }
        })

        viewModel.productNameError.observe(this, Observer { t ->
            t?.let {
                dataBinding.textErrorProductName.apply {
                    visibility = if (t.isNotEmpty()) View.VISIBLE else View.INVISIBLE
                    text = t
                }
            }
        })

        viewModel.descriptionError.observe(this, Observer { t ->
            t?.let {
                dataBinding.textErrorDescription.apply {
                    visibility = if (t.isNotEmpty()) View.VISIBLE else View.INVISIBLE
                    text = t
                }
            }
        })

        viewModel.priceError.observe(this, Observer { t ->
            t?.let {
                dataBinding.textErrorPrice.apply {
                    visibility = if (t.isNotEmpty()) View.VISIBLE else View.INVISIBLE
                    text = t
                }
            }
        })

        viewModel.imageAdd.observe(this, Observer {
            imageCount += 1
            storeImageUploadAdapter.addProductList(it)
        })

        viewModel.imageRemove.observe(this, Observer {
            imageContainer.removeViewAt(it)
            storeImageUploadAdapter.deleteProductList(it)
            imageCount -= 1
        })

        viewModel.addSuccess.observe(this, Observer {
            if (it) activity?.onBackPressed()
        })

        viewModel.productDetailResponse.observe(this, Observer {
            bindToScreen(it)
            imageCount = 1
        })


    }

    private fun bindToScreen(storeProductList: StoreProductList) {
        //image
        val imageArrayFromDB: ArrayList<StoreImage> = arrayListOf()
        val pict01 = Uri.parse(storeProductList.productPicture1)
//        val pict02 = Uri.parse(storeProductList.productPicture2)
//        val pict03 = Uri.parse(storeProductList.productPicture3)
        imageArrayFromDB.add(StoreImage(pict01))
//        imageArrayFromDB.add(StoreImage(pict02))
//        imageArrayFromDB.add(StoreImage(pict03))
        storeImageUploadAdapter.addAllProductList(imageArrayFromDB)

        //product name
        dataBinding.textProductID.setText(storeProductList.productId)
        dataBinding.textProductName.setText(storeProductList.productName)
        dataBinding.textDescription.setText(storeProductList.productDesc)
        dataBinding.textPrice.setText(storeProductList.productPrice.toString())
    }

    private fun startImageGallery() {
        if (imageCount < 1) {
            pickImageIntent()
        } else {
            Toast.makeText(context, "Maksimum gambar yang diperbolehkan hanya 1", Toast.LENGTH_SHORT).show()
        }
    }

    private fun pickImageIntent() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.apply {
            type = "image/*"
//            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            action = Intent.ACTION_GET_CONTENT
        }
        startActivityForResult(
            Intent.createChooser(intent, "Select images"), PICK_IMAGE_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_CODE && resultCode == Activity.RESULT_OK) {
            val imageUri = data!!.data
//            val mphoto: Bitmap? = data!!.data as Bitmap?

            try {
                viewModel.addImage(imageUri!!)
//                viewModel.addBitmap(mphoto!!)
            } catch (e: Error) {
                Toast.makeText(context, "$e", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDelete(p: Int) {
        viewModel.deleteImage(p)
    }

    fun checkStoragePermission() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermission()
        } else {
            startImageGallery()
        }
    }

    private fun requestPermission() {
        requestPermissions(
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            PICK_IMAGE_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PICK_IMAGE_CODE) {
            if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
//                viewModel.setStatusLocation(false)
            } else {
                startImageGallery()
            }
        }
    }
}