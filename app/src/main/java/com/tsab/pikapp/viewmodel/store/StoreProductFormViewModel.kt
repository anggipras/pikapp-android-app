package com.tsab.pikapp.viewmodel.store

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.tsab.pikapp.models.model.*
import com.tsab.pikapp.models.network.PikappApiService
import com.tsab.pikapp.util.SessionManager
import com.tsab.pikapp.util.UploadRequestBody
import com.tsab.pikapp.util.detectSpecialCharacter
import com.tsab.pikapp.util.getFileName
import com.tsab.pikapp.viewmodel.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


class StoreProductFormViewModel(application: Application) : BaseViewModel(application),
    UploadRequestBody.UploadCallback {

    private var sessionManager = SessionManager(getApplication())

    private val pikappService = PikappApiService()
    private val disposable = CompositeDisposable()

    private var imageArray: ArrayList<StoreImage> = arrayListOf()
    private var imageArrayBitmap: ArrayList<StoreImageBitmap> = arrayListOf()
    private var imageArrayFromDB: ArrayList<StoreImage> = arrayListOf()

    val loading = MutableLiveData<Boolean>()

    val imageAdd = MutableLiveData<StoreImage>()
    val imageRemove = MutableLiveData<Int>()

    val productDetailResponse = MutableLiveData<StoreProductList>()

    val addSuccess = MutableLiveData<Boolean>()

    val imageError = MutableLiveData<String>()
    val productNameError = MutableLiveData<String>()
    val descriptionError = MutableLiveData<String>()
    val priceError = MutableLiveData<String>()

    var isImageValid = false
    var isProductNameValid = false
    var isDescriptionValid = false
    var isPriceValid = false

    fun addImage(uri: Uri) {
        val img = StoreImage(uri)
        imageArray.add(img)
        imageAdd.value = img
    }

    fun deleteImage(p: Int) {
        Log.d("Debug", "position : $p")
        imageArray.removeAt(p)
        imageRemove.value = p
    }

    fun getProduct(pid: String) {
        loading.value = true
        val email = sessionManager.getUserData()!!.email!!
        val token = sessionManager.getUserToken()!!

        disposable.add(
            pikappService.getStoreProductDetail(email, token, pid)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<StoreProductDetailResponse>() {
                    override fun onSuccess(t: StoreProductDetailResponse) {
                        t.results?.let { it1 -> productDetailRetrieved(it1) }
                    }

                    override fun onError(e: Throwable) {

                    }
                })
        )
    }

    private fun productDetailRetrieved(storeProductList: StoreProductList) {
        val pict01 = Uri.parse(storeProductList.productPicture1)
//        val pict02 = Uri.parse(storeProductList.productPicture2)
//        val pict03 = Uri.parse(storeProductList.productPicture3)
        imageArrayFromDB.add(StoreImage(pict01))
//        imageArrayFromDB.add(StoreImage(pict02))
//        imageArrayFromDB.add(StoreImage(pict03))

        imageArray.addAll(imageArrayFromDB)
        productDetailResponse.value = storeProductList
        loading.value = false
    }

    fun submitProduct(
        context: Context,
        productName: String,
        description: String,
        price: String,
        condition: String
    ) {
        checkUserInput(productName, description, price)
        if (isImageValid && isProductNameValid && isDescriptionValid && isPriceValid) {
            proceedSubmitProduct(context, productName, description, price)
        }
    }

    private fun checkUserInput(productName: String, description: String, price: String) {
        if (imageArray.isEmpty()) {
            imageError.value = "Silakan masukkan gambar produk anda"
            isImageValid = false
        } else {
            imageError.value = ""
            isImageValid = true
        }

        when {
            productName.isEmpty() -> {
                productNameError.value = "Silakan masukkan nama produk anda"
                isProductNameValid = false
            }
            detectSpecialCharacter(productName) -> {
                productNameError.value = "Karakter spesial tidak diperbolehkan"
                isProductNameValid = false
            }
            else -> {
                productNameError.value = ""
                isProductNameValid = true
            }
        }

        when {
            description.isEmpty() -> {
                descriptionError.value = "Silakan masukkan deskripsi produk anda"
                isDescriptionValid = false
            }
            else -> {
                descriptionError.value = ""
                isDescriptionValid = true
            }
        }

        when {
            price.isEmpty() -> {
                priceError.value = "Silakan masukkan harga produk anda"
                isPriceValid = false
            }
            else -> {
                priceError.value = ""
                isPriceValid = true
            }
        }

    }

    private fun proceedSubmitProduct(
        context: Context,
        productName: String,
        description: String,
        price: String
    ) {
        loading.value = true
        val email = sessionManager.getUserData()!!.email!!
        val token = sessionManager.getUserToken()!!
        val mid = sessionManager.getUserData()!!.mid!!

        val image = imageArray[0].imageUri
        Log.d("Debug", "image isinya : $image")

        val cR = context.contentResolver
        val mime = MimeTypeMap.getSingleton()
        val type = mime.getExtensionFromMimeType(cR.getType(image!!))

        val img1 = imageArray[0].imageUri?.let {
            getPath(
                "file_01",
                context,
                it,
                "${productName}1.${type}"
            )
        }!!
        // change this array upload 3 different images
        val img2 = imageArray[0].imageUri?.let {
            getPath(
                "file_02",
                context,
                it,
                "${productName}2.${type}"
            )
        }!!
        val img3 = imageArray[0].imageUri?.let {
            getPath(
                "file_03",
                context,
                it,
                "${productName}3.${type}"
            )
        }!!

        val name: RequestBody =
            RequestBody.create(MediaType.parse("multipart/form-data"), productName)
        val prc: RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), price)
        val condc: RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), "new")
        val status: RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), "true")
        val qty: RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), "1")
        val desc: RequestBody =
            RequestBody.create(MediaType.parse("multipart/form-data"), description)

        disposable.add(
            pikappService.postStoreAddProduct(
                email,
                token,
                mid,
                img1,
                img2,
                img3,
                name,
                prc,
                condc,
                status,
                qty,
                desc
            )
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<StoreProductActionResponse>() {
                    override fun onSuccess(t: StoreProductActionResponse) {
                        onAddSuccess()
                        Toast.makeText(
                            getApplication(),
                            "Berhasil menambahkan produk",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onError(e: Throwable) {
                        var errorResponse: StoreProductActionResponse
                        try {
                            Log.d("Debug", "error merchant detail : " + e)
                            val responseBody = (e as HttpException)
                            val body = responseBody.response()?.errorBody()?.string()
                            errorResponse =
                                Gson().fromJson<StoreProductActionResponse>(
                                    body,
                                    StoreProductActionResponse::class.java
                                )
                        } catch (err: Throwable) {
                            errorResponse = StoreProductActionResponse(
                                "now", "503"
                            )
                        }
                        Toast.makeText(
                            getApplication(),
                            "${errorResponse.errCode} ${errorResponse.errMessage}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        )
    }

    fun editProduct(
        context: Context,
        pid: String,
        productName: String,
        description: String,
        price: String
    ) {
        loading.value = true
        checkUserInput(productName, description, price)
        if (isImageValid && isProductNameValid && isDescriptionValid && isPriceValid) {
            proceedEditProduct(context, pid, productName, description, price)
        }

    }

    private fun proceedEditProduct(
        context: Context,
        pid: String,
        productName: String,
        description: String,
        price: String
    ) {
        var isWithImage = false
        val email = sessionManager.getUserData()!!.email!!
        val token = sessionManager.getUserToken()!!
        val mid = sessionManager.getUserData()!!.mid!!
        val image = imageArray[0].imageUri.toString()
        if (image.contains("content://")) {
            isWithImage = true
        }

        val name: RequestBody =
            RequestBody.create(MediaType.parse("multipart/form-data"), productName)
        val prc: RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), price)
        val condc: RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), "new")
        val status: RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), "true")
        val qty: RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), "1")
        val desc: RequestBody =
            RequestBody.create(MediaType.parse("multipart/form-data"), description)

        val imageBuffer = imageArray[0].imageUri
        Log.d("Debug", "image isinya : $image")

        val cR = context.contentResolver
        val mime = MimeTypeMap.getSingleton()
        val type = mime.getExtensionFromMimeType(cR.getType(imageBuffer!!))

        if (isWithImage) {
            val img1 = imageArray[0].imageUri?.let {
                getPath(
                    "file_01",
                    context,
                    it,
                    "${productName}1.${type}"
                )
            }!!
            val img2 = imageArray[0].imageUri?.let {
                getPath(
                    "file_02",
                    context,
                    it,
                    "${productName}2.${type}"
                )
            }!!
            val img3 = imageArray[0].imageUri?.let {
                getPath(
                    "file_03",
                    context,
                    it,
                    "${productName}3.${type}"
                )
            }!!
            disposable.add(
                pikappService.postStoreEditProductWithImage(
                    email,
                    token,
                    mid,
                    pid,
                    img1,
                    img2,
                    img3,
                    name,
                    prc,
                    condc,
                    qty,
                    desc
                )
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableSingleObserver<StoreProductActionResponse>() {
                        override fun onSuccess(t: StoreProductActionResponse) {
                            onAddSuccess()
                            Toast.makeText(
                                getApplication(),
                                "Berhasil Mengubah produk",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        override fun onError(e: Throwable) {
                            var errorResponse: StoreProductActionResponse
                            try {
                                Log.d("Debug", "error merchant detail : " + e)
                                val responseBody = (e as HttpException)
                                val body = responseBody.response()?.errorBody()?.string()
                                errorResponse =
                                    Gson().fromJson<StoreProductActionResponse>(
                                        body,
                                        StoreProductActionResponse::class.java
                                    )
                            } catch (err: Throwable) {
                                errorResponse = StoreProductActionResponse(
                                    "now", "503"
                                )
                            }
                            Toast.makeText(
                                getApplication(),
                                "${errorResponse.errCode} ${errorResponse.errMessage}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
            )
        } else {
            disposable.add(
                pikappService.postStoreEditProductWithoutImage(
                    email,
                    token,
                    mid,
                    pid,
                    name,
                    prc,
                    condc,
                    qty,
                    desc
                )
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableSingleObserver<StoreProductActionResponse>() {
                        override fun onSuccess(t: StoreProductActionResponse) {
                            onAddSuccess()
                            Toast.makeText(
                                getApplication(),
                                "Berhasil Mengubah produk",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        override fun onError(e: Throwable) {
                            var errorResponse: StoreProductActionResponse
                            try {
                                Log.d("Debug", "error merchant detail : " + e)
                                val responseBody = (e as HttpException)
                                val body = responseBody.response()?.errorBody()?.string()
                                errorResponse =
                                    Gson().fromJson<StoreProductActionResponse>(
                                        body,
                                        StoreProductActionResponse::class.java
                                    )
                            } catch (err: Throwable) {
                                errorResponse = StoreProductActionResponse(
                                    "now", "503"
                                )
                            }
                            Toast.makeText(
                                getApplication(),
                                "${errorResponse.errCode} ${errorResponse.errMessage}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
            )
        }
    }

    private fun onAddSuccess() {
        loading.value = false
        addSuccess.value = true
    }

    private fun getPath(
        key: String,
        context: Context,
        uri: Uri,
        name: String
    ): MultipartBody.Part? {
//        val file= File(uri.path!!)
//        val absolutePath = file.absolutePath

        val parcelFileDescriptor =
            context.contentResolver.openFileDescriptor(uri, "r", null) ?: return null

        val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
        val file = File(context.cacheDir, context.contentResolver.getFileName(uri))
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)

//        val filePart = RequestBody.create(MediaType.parse("multipart/form-file"), absolutePath)
        val filePart = UploadRequestBody(file, "multipart/form-file", this)
        val body = MultipartBody.Part.createFormData(key, name, filePart)
        return body
    }

    @SuppressLint("Recycle")
    private fun getRealPathFromURI(context: Context, contentURI: Uri): String? {
        val result: String?
        val cursor: Cursor =
            context.contentResolver.query(contentURI, null, null, null, null)!!
        cursor.moveToFirst()
        val idx: Int = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
        result = cursor.getString(idx)
        cursor.close()
        return result
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

    override fun onProgressUpdate(percentage: Int) {

    }
}