package com.tsab.pikapp.viewmodel.store

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.tsab.pikapp.models.model.StoreImage
import com.tsab.pikapp.models.model.StoreProductActionResponse
import com.tsab.pikapp.models.model.StoreProductDetailResponse
import com.tsab.pikapp.models.model.StoreProductList
import com.tsab.pikapp.models.network.PikappApiService
import com.tsab.pikapp.util.SessionManager
import com.tsab.pikapp.viewmodel.BaseViewModel
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import java.io.File


class StoreProductFormViewModel(application: Application) : BaseViewModel(application) {

    private var sessionManager = SessionManager(getApplication())

    private val pikappService = PikappApiService()
    private val disposable = CompositeDisposable()

    private var imageArray: ArrayList<StoreImage> = arrayListOf()
    private var imageArrayFromDB: ArrayList<StoreImage> = arrayListOf()

    val loading = MutableLiveData<Boolean>()

    val imageAdd = MutableLiveData<StoreImage>()
    val imageRemove = MutableLiveData<Int>()

    val productDetailResponse = MutableLiveData<StoreProductList>()

    val addSuccess = MutableLiveData<Boolean>()

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
                .subscribeWith(object : DisposableSingleObserver<StoreProductDetailResponse>(){
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
        val pict02 = Uri.parse(storeProductList.productPicture2)
        val pict03 = Uri.parse(storeProductList.productPicture3)
        imageArrayFromDB.add(StoreImage(pict01))
        imageArrayFromDB.add(StoreImage(pict02))
        imageArrayFromDB.add(StoreImage(pict03))

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
        loading.value = true
        val email = sessionManager.getUserData()!!.email!!
        val token = sessionManager.getUserToken()!!
        val mid = sessionManager.getUserData()!!.mid!!

        val img1 = imageArray[0].imageUri?.let { getPath("file_01", context, it, "${productName}1") }!!
        val img2 = imageArray[1].imageUri?.let { getPath("file_02", context, it, "${productName}2") }!!
        val img3 = imageArray[2].imageUri?.let { getPath("file_03", context, it, "${productName}3") }!!

        val name: RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), productName)
        val prc: RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), price)
        val condc: RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), "new")
        val status: RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), "true")
        val qty: RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), "1")
        val desc: RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), description)

        disposable.add(
            pikappService.postStoreAddProduct(email, token, mid, img1, img2, img3, name, prc, condc, status, qty, desc)
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

    fun editProduct() {

    }

    private fun onAddSuccess() {
        loading.value = false
        addSuccess.value = true
    }

    private fun getPath(key: String, context: Context, uri: Uri, name: String): MultipartBody.Part {
        val file= File(uri.path)
        val absolutePath = file.absolutePath
        val filePart = RequestBody.create(MediaType.parse("multipart/form-file"), absolutePath)
        val body = MultipartBody.Part.createFormData(key, name, filePart)
        return body
    }

    @SuppressLint("Recycle")
    private fun getRealPathFromURI(context: Context, contentURI: Uri): String? {
        val result: String?
        val cursor: Cursor = context.getContentResolver().query(contentURI, null, null, null, null)!!
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
}