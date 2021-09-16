package com.tsab.pikapp.viewmodel.menu

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonSizeSpec
import com.skydoves.balloon.createBalloon
import com.tsab.pikapp.R
import com.tsab.pikapp.models.model.*
import com.tsab.pikapp.models.network.PikappApiService
import com.tsab.pikapp.util.*
import com.tsab.pikapp.view.CategoryAdapter
import com.tsab.pikapp.viewmodel.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class MenuViewModel(application: Application) : BaseViewModel(application) {

    private var sessionManager = SessionManager(getApplication())
    private var prefHelper = SharedPreferencesUtil(getApplication())

    private val apiService = PikappApiService()
    private val disposable = CompositeDisposable()

    var size: String = "0"

    var name1: String = "Test"

    private val mutableMenuList = MutableLiveData<SearchList>()
    val menuList : LiveData<SearchList> get() = mutableMenuList
    fun setMenu(menu: SearchList) {
        mutableMenuList.value = menu
        menu.product_id?.let { fetchAdvanceMenuData(it) }
    }

    private val mutableAddOrEdit = MutableLiveData<Boolean>()
    val addOrEdit : LiveData<Boolean> get() = mutableAddOrEdit
    fun setAddOrEdit(bool: Boolean) {
        mutableAddOrEdit.value = bool
    }

    private val mutableMenu = MutableLiveData(Uri.EMPTY)
    private val mutableMenuError = MutableLiveData("")
    private val isMenuValid = MutableLiveData(false)
    val menu: LiveData<Uri> get() = mutableMenu
    val menuError: LiveData<String> get() = mutableMenuError

    private val mutableCategory = MutableLiveData("")
    private val mutableCategoryError = MutableLiveData("")
    private val isCategoryValid = MutableLiveData(false)
    val category: LiveData<String> get() = mutableCategory
    val categoryError: LiveData<String> get() = mutableCategoryError

    private val mutableCategoryId = MutableLiveData("")
    private val mutableCategoryIdError = MutableLiveData("")
    private val isCategoryIdValid = MutableLiveData(false)
    val categoryId: LiveData<String> get() = mutableCategoryId
    val categoryIdError: LiveData<String> get() = mutableCategoryIdError

    lateinit var categoryAdapter: CategoryAdapter

    private val mutableImg = MutableLiveData(Uri.EMPTY)
    val img: LiveData<Uri> get() = mutableImg

    private val mutableNama = MutableLiveData("")
    private val mutableNamaError = MutableLiveData("")
    private val isNamaValid = MutableLiveData(false)
    val nama: LiveData<String> get() = mutableNama
    val namaError: LiveData<String> get() = mutableNamaError

    private val mutableAdvanceMenuList = MutableLiveData<AddAdvanceMenuRequest>()
    val advanceMenuList: LiveData<AddAdvanceMenuRequest> = mutableAdvanceMenuList
    fun setAdvanceMenuList(advanceMenuList: List<AdvanceMenu>) {
        mutableAdvanceMenuList.value = AddAdvanceMenuRequest(advanceMenuList)
    }

    private val mutableDesc = MutableLiveData("")
    private val mutableDescError = MutableLiveData("")
    private val isDescValid = MutableLiveData(false)
    val desc: LiveData<String> get() = mutableDesc
    val descError: LiveData<String> get() = mutableDescError

    private val mutableHarga = MutableLiveData("")
    private val mutableHargaError = MutableLiveData("")
    private val isHargaValid = MutableLiveData(false)
    val harga: LiveData<String> get() = mutableHarga
    val hargaError: LiveData<String> get() = mutableHargaError

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading
    fun setLoading(bool: Boolean) {
        _isLoading.value = bool
    }

    private val _isLoadingFinish = MutableLiveData<Boolean>(true)
    val isLoadingFinish: LiveData<Boolean> get() = _isLoadingFinish
    fun setLoadingFinish(bool: Boolean) {
        _isLoadingFinish.value = bool
    }

    fun showTooltip(context: Context): Balloon {
        var balloon = createBalloon(context) {
            setArrowSize(10)
            setWidth(BalloonSizeSpec.WRAP)
            setHeight(65)
            setMarginRight(14)
            setArrowPosition(0.9f)
            setCornerRadius(4f)
            setAlpha(0.8f)
            setPaddingRight(20)
            setPaddingLeft(20)
            setAutoDismissDuration(5000L)
            setText("Harga belum termasuk\n" + "pajak dan biaya layanan")
            setTextColorResource(R.color.tooltipText)
            setBackgroundColorResource(R.color.tooltipBackground)
            onBalloonClickListener?.let { setOnBalloonClickListener(it) }
            setBalloonAnimation(BalloonAnimation.FADE)
            setLifecycleOwner(lifecycleOwner)
        }
        return balloon
    }

    fun getCategoryName(): String? {
        return mutableCategory.value
    }

    fun getCategoryName1(): String? {
        Log.e("nama", name1)
        return name1
    }

    fun backBtn() {
        mutableImg.value = null
    }

    fun firstOpen() {
        mutableMenuError.value = ""
        mutableHargaError.value = ""
        mutableNamaError.value = ""
        mutableDescError.value = ""
    }

    fun validateMenu(menu: Uri?): Boolean {
        if (menu == null || menu == Uri.EMPTY) {
            mutableMenuError.value = "Gambar menu tidak boleh kosong"
            isMenuValid.value = false
        } else {
            mutableMenuError.value = ""
            mutableMenu.value = menu
            isMenuValid.value = true
        }
        return isMenuValid.value!!
    }

    fun validateImg(img: Uri?): Uri? {
        if (menu == null || menu == Uri.EMPTY) {
            Log.e("Kosong", "Kosongg")
        } else {
            mutableImg.value = img
        }
        return img
    }

    fun validateNama(nama: String): Boolean {
        if (nama.isEmpty() || nama.isBlank()) {
            mutableNamaError.value = "Nama menu tidak boleh kosong"
        } else {
            mutableNamaError.value = ""
        }

        mutableNama.value = nama
        isNamaValid.value = mutableNamaError.value!!.isEmpty()
        return isNamaValid.value!!
    }

    fun validateCategory(Category: String): Boolean {
        if (Category.isEmpty() || Category.isBlank()) {
            mutableCategoryError.value = "Nama kategori tidak boleh kosong"
        } else {
            mutableCategoryError.value = ""
        }
        mutableCategory.value = Category
        isCategoryValid.value = mutableCategoryError.value!!.isEmpty()
        return isCategoryValid.value!!
    }

    fun validateCategoryId(CategoryId: String): Boolean {
        if (CategoryId.isEmpty() || CategoryId.isBlank()) {
            mutableCategoryIdError.value = "Nama kategori tidak boleh kosong"
        } else {
            mutableCategoryIdError.value = ""
        }
        mutableCategoryId.value = CategoryId
        isCategoryIdValid.value = mutableCategoryIdError.value!!.isEmpty()
        return isCategoryIdValid.value!!
    }

    fun validateHarga(harga: String): Boolean {
        if (harga.isEmpty() || harga.isBlank()) {
            mutableHargaError.value = "Harga tidak boleh kosong"
        } else {
            mutableHargaError.value = ""
        }

        mutableHarga.value = harga
        isHargaValid.value = mutableHargaError.value!!.isEmpty()
        return isHargaValid.value!!
    }

    fun validateDesc(desc: String): Boolean {
        if (desc.isEmpty() || desc.isBlank()) {
            mutableDescError.value = "Deskripsi tidak boleh kosong"
        } else {
            mutableDescError.value = ""
        }

        mutableDesc.value = desc
        isDescValid.value = mutableDescError.value!!.isEmpty()
        return isDescValid.value!!
    }

    fun getCategory(
        baseContext: Context,
        recyclerview_category: RecyclerView,
        listener1: CategoryAdapter.OnItemClickListener
    ) {
        var sessionManager = SessionManager(getApplication())
        val email = sessionManager.getUserData()!!.email!!
        val token = sessionManager.getUserToken()!!
        val timestamp = getTimestamp()
        val signature = getSignature(email, timestamp)
        val mid = sessionManager.getUserData()!!.mid!!

        // TODO: Update API call.
        PikappApiService().api.getMenuCategoryList(
            getUUID(), timestamp, getClientID(), signature, token, mid
        ).enqueue(object : Callback<MerchantListCategoryResponse> {
            override fun onFailure(call: Call<MerchantListCategoryResponse>, t: Throwable) {
                Log.e("failed", t.message.toString())
            }

            override fun onResponse(
                call: Call<MerchantListCategoryResponse>,
                response: Response<MerchantListCategoryResponse>
            ) {

                val categoryResponse = response.body()
                var categoryResult = response.body()?.results
                Log.e("result", categoryResponse?.results.toString())
                Log.e("Response raw", response.raw().toString())
                Log.e("response body", response.body().toString())
                Log.d("SUCCEED", "succeed")
                Log.e("uuid", getUUID())
                Log.e("timestamp", timestamp)
                Log.e("client id", getClientID())
                Log.e("signature", signature)
                Log.e("token", token)
                Log.e("mid", mid)

                Log.i("MyTag", "onCreate")
                Log.e("size", categoryResponse?.results?.size.toString())
                size = categoryResponse?.results?.size.toString()
                Log.e("size on response", size)

                categoryAdapter = CategoryAdapter(
                    baseContext,
                    categoryResult as MutableList<CategoryListResult>,
                    listener1
                )
                name1 = categoryAdapter.name
                categoryAdapter.notifyDataSetChanged()
                recyclerview_category.adapter = categoryAdapter
            }
        })
    }

    fun validatePage(): Boolean = isMenuValid.value!! && isNamaValid.value!! && isHargaValid.value!! && isDescValid.value!!

    fun postMenu() {
        setLoading(true)
        val email = sessionManager.getUserData()!!.email!!
        val token = sessionManager.getUserToken()!!
        val timestamp = getTimestamp()
        val signature = getSignature(email, timestamp)
        val mid = sessionManager.getUserData()!!.mid!!
        val application = getApplication<Application>()

        Log.e("UUID", getUUID())
        Log.e("TIMESTAMP", timestamp)
        Log.e("CLIENTID", getClientID())
        Log.e("SIGNATURE", signature)
        Log.e("TOKEN", token)
        Log.e("MID", mid)

        val gson = Gson()
        val type = object : TypeToken<BaseResponse>() {}.type

        val menuParcelFileDescriptor = application.contentResolver.openFileDescriptor(
            menu.value!!,
            "r", null
        ) ?: return
        val menuInputStream = FileInputStream(menuParcelFileDescriptor.fileDescriptor)
        val menuFile = File(
            application.cacheDir, application.contentResolver.getFileName(
                menu.value!!
            )
        )
        val menuOutputStream = FileOutputStream(menuFile)
        menuInputStream.copyTo(menuOutputStream)

        val actionMenu = if (addOrEdit.value == true) "MODIFY" else "ADD"

        val jsonString = GsonBuilder().create().toJson(advanceMenuList.value)

        apiService.api.uploadMenu(
            getUUID(), timestamp, getClientID(), signature, token, mid,
            MultipartBody.Part.createFormData(
                "file_01", menuFile.name,
                RequestBody.create(MediaType.parse("multipart/form-data"), menuFile)
            ),
            MultipartBody.Part.createFormData(
                "file_02", menuFile.name,
                RequestBody.create(MediaType.parse("multipart/form-data"), menuFile)
            ),
            MultipartBody.Part.createFormData(
                "file_03", menuFile.name,
                RequestBody.create(MediaType.parse("multipart/form-data"), menuFile)
            ),
            RequestBody.create(MediaType.parse("multipart/form-data"), nama.value),
            RequestBody.create(MediaType.parse("multipart/form-data"), desc.value),
            RequestBody.create(MediaType.parse("multipart/form-data"), categoryId.value),
            RequestBody.create(MediaType.parse("multipart/form-data"), harga.value),
            RequestBody.create(MediaType.parse("multipart/form-data"), "new"),
            RequestBody.create(MediaType.parse("multipart/form-data"), actionMenu),
            RequestBody.create(MediaType.parse("multipart/form-data"), "True"),
            RequestBody.create(MediaType.parse("multipart/form-data"), "1"),
            RequestBody.create(MediaType.parse("multipart/form-data"), jsonString)
        ).enqueue(object : Callback<BaseResponse> {
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (response.code() == 200 && response.body()!!.errCode.toString() == "EC0000") {
                    Log.e("RESPONSEEE", response.toString())
                    setLoading(false)
                    setLoadingFinish(false)
                    val toastAddEdit = if (actionMenu == "ADD") "Ditambahkan" else "Diubah"
                    Toast.makeText(getApplication(), "Menu Berhasil ${toastAddEdit}", Toast.LENGTH_LONG).show()
                } else {
                    setLoading(false)
                    var errorResponse: BaseResponse? =
                        gson.fromJson(response.errorBody()!!.charStream(), type)
                    Log.e(
                        "Result",
                        generateResponseMessage(errorResponse?.errCode, errorResponse?.errMessage)
                    )
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                Log.e("UploadRegisterError", t.message.toString())
            }
        })
    }

    fun deleteMenu() {
        setLoading(true)
        val email = sessionManager.getUserData()!!.email!!
        val token = sessionManager.getUserToken()!!
        val timestamp = getTimestamp()
        val signature = getSignature(email, timestamp)
        val mid = sessionManager.getUserData()!!.mid!!

        apiService.api.deleteMenu(
                getUUID(), timestamp, getClientID(), signature, token, mid, menuList.value?.product_id.toString(),
                RequestBody.create(MediaType.parse("multipart/form-data"), "new"),
                RequestBody.create(MediaType.parse("multipart/form-data"), "DELETE"),
                RequestBody.create(MediaType.parse("multipart/form-data"), "1")
        ).enqueue(object : Callback<BaseResponse> {
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (response.code() == 200 && response.body()!!.errCode.toString() == "EC0000") {
                    Log.e("RESPONSEEE", response.toString())
                    setLoading(false)
                    setLoadingFinish(false)
                    Toast.makeText(getApplication(), "Menu Berhasil Dihapus", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                setLoading(false)
                Log.e("MenuDeleteFailed", t.message.toString())
            }
        })
    }

    fun fetchAdvanceMenuData(productId: String) {
        setLoading(true)
        val timeStamp = getTimestamp()
        disposable.add(
                apiService.listAdvanceMenu(
                        email = sessionManager.getUserData()?.email ?: "",
                        token = sessionManager.getUserToken() ?: "",
                        pid = productId,
                        timeStamp = timeStamp
                ).subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableSingleObserver<ListAdvanceMenuResponse>() {
                            override fun onSuccess(response: ListAdvanceMenuResponse) {
                                // TODO: Add is advance menu active.
                                if (response.results.isNotEmpty()) {
                                    setAdvanceMenuList(response.results)
                                }
                                setLoading(false)
                            }

                            override fun onError(e: Throwable) {
                                Log.d("ERRORFETCH", e.message.toString())
                                setLoading(false)
                            }
                        })
        )
    }
}