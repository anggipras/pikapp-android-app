package com.tsab.pikapp.viewmodel.menu.advance

import android.app.Application
import android.util.Log
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tsab.pikapp.models.model.*
import com.tsab.pikapp.models.network.PikappApiService
import com.tsab.pikapp.util.*
import com.tsab.pikapp.viewmodel.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdvanceMenuViewModel(application: Application) : BaseViewModel(application) {
    private val TAG = javaClass.simpleName

    private var sessionManager = SessionManager(getApplication())

    private val apiService = PikappApiService()
    private val disposable = CompositeDisposable()

    /**
     * Set the add or edit navigation
     */
    private val mutableAddOrEdit = MutableLiveData<Boolean>()
    val addOrEdit: LiveData<Boolean> = mutableAddOrEdit
    fun setAddOrEdit(boolean: Boolean) {
        mutableAddOrEdit.value = boolean
    }

    /**
     * Set the parent productId. Must be called whenever the activity is first created.
     */
    private val mutableProductId = MutableLiveData<String?>(null)
    val productId: LiveData<String?> = mutableProductId
    fun setProductId(productId: String?) {
        mutableProductId.value = productId
    }

    /**
     * General screen flow.
     */
    private val mutableIsLoading = MutableLiveData<Boolean>(true)
    val isLoading: LiveData<Boolean> = mutableIsLoading
    fun setLoading(isLoading: Boolean) {
        mutableIsLoading.value = isLoading
    }

    private val mutableIsLocalLoading = MutableLiveData<Boolean>()
    val isLocalLoading: LiveData<Boolean> = mutableIsLocalLoading
    fun setLocalLoading(isLoading: Boolean) {
        mutableIsLocalLoading.value = isLoading
    }

    private val mutableIsAdvanceMenuActive = MutableLiveData<Boolean>(false)
    val isAdvanceMenuActive: LiveData<Boolean> = mutableIsAdvanceMenuActive
    fun setAdvanceMenuActive(isActive: Boolean) {
        mutableIsAdvanceMenuActive.value = isActive
    }

    private val mutableAdvanceId = MutableLiveData<Long>()
    val advanceId: LiveData<Long> = mutableAdvanceId
    fun setAdvanceId(id: Long) {
        mutableAdvanceId.value = id
    }

    private val mutableMenuExtId = MutableLiveData<Long>()
    val menuExtId: LiveData<Long> = mutableMenuExtId
    fun setMenuExtId(id: Long) {
        mutableMenuExtId.value = id
    }

    /*ADD ADVANCE MENU START*/
    private val mutableAdvanceMenuList = MutableLiveData<List<AdvanceMenu>>(listOf())
    val advanceMenuList: LiveData<List<AdvanceMenu>> = mutableAdvanceMenuList
    fun setAdvanceMenuList(advanceMenuList: List<AdvanceMenu>) {
        mutableAdvanceMenuList.value = advanceMenuList
    }

    private fun addAdvanceMenu(advanceMenu: AdvanceMenu) {
        if (!mutableAdvanceMenuList.value!!.none { it.template_name == advanceMenu.template_name }) {
            mutableAdvanceMenuList.value =
                advanceMenuList.value?.filter { it.template_name != advanceMenu.template_name }
        }

        mutableAdvanceMenuList.value = advanceMenuList.value?.toMutableList()?.apply {
            add(advanceMenu)
        }
    }

    fun fetchAdvanceMenuData() {
        if (productId.value == null) {
            setLoading(false)
            return
        }
        val timeStamp = getTimestamp()

        setLoading(true)
        disposable.add(
            apiService.listAdvanceMenu(
                email = sessionManager.getUserData()?.email ?: "",
                token = sessionManager.getUserToken() ?: "",
                pid = mutableProductId.value ?: "",
                    timeStamp = timeStamp
            ).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<ListAdvanceMenuResponse>() {
                    override fun onSuccess(response: ListAdvanceMenuResponse) {
                        // TODO: Add is advance menu active.
                        if (response.results.isNotEmpty()) {
                            setAdvanceMenuActive(true)
                            setAdvanceMenuList(response.results)
                        }
                        setLoading(false)
                    }

                    override fun onError(e: Throwable) {
                        Log.d(TAG, e.message.toString())
                        setLoading(false)
                    }
                })
        )
    }
    /*ADD ADVANCE MENU END*/

    /*EDIT ADVANCE MENU START*/
    private val mutableAdvanceMenuEditList = MutableLiveData<List<AdvanceMenuEdit>>(listOf())
    val advanceMenuEditList: LiveData<List<AdvanceMenuEdit>> = mutableAdvanceMenuEditList
    fun setAdvanceMenuEditList(advanceMenuList: List<AdvanceMenuEdit>) {
        mutableAdvanceMenuEditList.value = advanceMenuList
    }

    private fun editAdvanceMenu(advanceMenu: AdvanceMenuEdit) {
        if (!mutableAdvanceMenuEditList.value!!.none { it.template_name == advanceMenu.template_name }) {
            mutableAdvanceMenuEditList.value =
                    advanceMenuEditList.value?.filter { it.template_name != advanceMenu.template_name }
        }

        mutableAdvanceMenuEditList.value = advanceMenuEditList.value?.toMutableList()?.apply {
            add(advanceMenu)
        }
    }

    fun fetchAdvanceMenuEditData() {
        if (productId.value == null) {
            setLoading(false)
            return
        }
        val timeStamp = getTimestamp()

        setLoading(true)
        disposable.add(
                apiService.listAdvanceMenuEdit(
                        email = sessionManager.getUserData()?.email ?: "",
                        token = sessionManager.getUserToken() ?: "",
                        pid = mutableProductId.value ?: "",
                        timeStamp = timeStamp
                ).subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableSingleObserver<ListAdvanceMenuEditResponse>() {
                            override fun onSuccess(response: ListAdvanceMenuEditResponse) {
                                // TODO: Add is advance menu active.
                                if (response.results.isNotEmpty()) {
                                    setAdvanceMenuActive(true)
                                    setAdvanceMenuEditList(response.results)
                                }
                                setLoading(false)
                            }

                            override fun onError(e: Throwable) {
                                Log.d(TAG, e.message.toString())
                                setLoading(false)
                            }
                        })
        )
    }
    /*EDIT ADVANCE MENU END*/

    /**
     * Details screen specific flow
     */
    private val mutableDetailsNamaPilihan = MutableLiveData<String>("")
    private val mutableDetailsNamaPilihanError = MutableLiveData<String>("")
    val detailsNamaPilihan: LiveData<String> = mutableDetailsNamaPilihan
    val detailsNamaPilihanError: LiveData<String> = mutableDetailsNamaPilihanError
    private val isDetailsNamaPilihanValid = MutableLiveData<Boolean>(false)
    fun setDetailsNamaPilihan(namaPilihan: String) {
        mutableDetailsNamaPilihan.value = namaPilihan
    }

    private val mutableIsDetailsAktif = MutableLiveData<Boolean>(true)
    val isDetailsAktif: LiveData<Boolean> = mutableIsDetailsAktif
    fun setDetailsAktif(isAktif: Boolean) {
        mutableIsDetailsAktif.value = isAktif
        if (!isAktif) mutableIsDetailsWajib.value = false
    }

    private val mutableIsDetailsWajib = MutableLiveData<Boolean>(true)
    val isDetailsWajib: LiveData<Boolean> = mutableIsDetailsWajib
    fun setDetailsWajib(isWajib: Boolean) {
        mutableIsDetailsWajib.value = isWajib
        if (isWajib) mutableIsDetailsAktif.value = true
    }

    private val mutableDetailsPilihanMaksimal = MutableLiveData(1)
    private val mutableDetailsPilihanMaksimalError = MutableLiveData<String>("")
    val detailsPilihanMaksimal: LiveData<Int> = mutableDetailsPilihanMaksimal
    val detailsPilihanMaksimalError: LiveData<String> = mutableDetailsPilihanMaksimalError
    private val isDetailsPilihanMaksimalValid = MutableLiveData<Boolean>(false)
    fun setDetailsPilihanMaksimal(pilihanMaksimal: Int) {
        mutableDetailsPilihanMaksimal.value = pilihanMaksimal
    }

    fun validateDetailsNamaPilihan(namaPilihan: String): Boolean {
        if (namaPilihan.isEmpty() || namaPilihan.isBlank()) {
            mutableDetailsNamaPilihanError.value = "Nama pilihan tidak boleh kosong"
        } else {
            mutableDetailsNamaPilihanError.value = ""
        }

        mutableDetailsNamaPilihan.value = namaPilihan
        isDetailsNamaPilihanValid.value =
                mutableDetailsNamaPilihanError.value!!.isEmpty()
        return isDetailsNamaPilihanValid.value!!
    }

    fun validateDetailsPilihanMaksimal(pilihanMaksimal: String): Boolean {
        if (pilihanMaksimal.isEmpty() || pilihanMaksimal.isBlank()) {
            mutableDetailsPilihanMaksimalError.value = "Jumlah pilihan maksimal tidak boleh kosong"
        } else if (!pilihanMaksimal.isDigitsOnly()) {
            mutableDetailsPilihanMaksimalError.value =
                    "Jumlah pilihan maksimal hanya boleh mengandung angka"
        } else {
            mutableDetailsPilihanMaksimalError.value = ""
        }

        mutableDetailsPilihanMaksimal.value =
                if (pilihanMaksimal.isNotEmpty() && pilihanMaksimal.isDigitsOnly()) pilihanMaksimal.toInt() else 1
        isDetailsPilihanMaksimalValid.value = mutableDetailsPilihanMaksimalError.value!!.isEmpty()
        return isDetailsPilihanMaksimalValid.value!!
    }

    /*ADD EXTRA MENUS START*/
    private val mutableDetailsAdditionalMenuList =
        MutableLiveData<List<AdvanceAdditionalMenu>>(listOf())
    val detailsAdditionalMenuList: LiveData<List<AdvanceAdditionalMenu>> =
        mutableDetailsAdditionalMenuList
    fun setDetailsAdditionalMenuList(additionalMenuList: List<AdvanceAdditionalMenu>) {
        mutableDetailsAdditionalMenuList.value = additionalMenuList
    }

    private fun addAdditionalMenu(additionalMenu: AdvanceAdditionalMenu) {
        if (!mutableDetailsAdditionalMenuList.value!!.none { it.ext_menu_name == additionalMenu.ext_menu_name }) {
            mutableDetailsAdditionalMenuList.value =
                    detailsAdditionalMenuList.value?.filter { it.ext_menu_name != additionalMenu.ext_menu_name }
        }

        mutableDetailsAdditionalMenuList.value =
                detailsAdditionalMenuList.value?.toMutableList()?.apply { add(additionalMenu) }
        if (additionalNamaDaftarPilihan.value != mutableAdditionalPreviousName.value) {
            mutableDetailsAdditionalMenuList.value = detailsAdditionalMenuList.value?.filter { it.ext_menu_name != mutableAdditionalPreviousName.value }
        }
    }

    fun validateDetailsScreen(): Boolean {
        if (!isDetailsNamaPilihanValid.value!! || !isDetailsPilihanMaksimalValid.value!!) return false

        addAdvanceMenu(
                AdvanceMenu(
                        template_name = detailsNamaPilihan.value!!,
                        template_type = if (detailsPilihanMaksimal.value!! == 1) AdvanceMenuTemplateType.RADIO.toString() else AdvanceMenuTemplateType.CHECKBOX.toString(),
                        active = isDetailsAktif.value!!,
                        mandatory = isDetailsWajib.value!!,
                        max_choose = detailsPilihanMaksimal.value!!,
                        ext_menus = detailsAdditionalMenuList.value ?: listOf()
                )
        )

        return true
    }
    /*ADD EXTRA MENUS END*/

    /*EDIT EXTRA MENUS START*/
    private val mutableDetailsAdditionalMenuEdit =
            MutableLiveData<List<AdvanceAdditionalMenuEdit>>(listOf())
    val detailsAdditionalMenuListEdit: LiveData<List<AdvanceAdditionalMenuEdit>> =
            mutableDetailsAdditionalMenuEdit
    fun setDetailsAdditionalMenuEditList(additionalMenuList: List<AdvanceAdditionalMenuEdit>) {
        mutableDetailsAdditionalMenuEdit.value = additionalMenuList
    }

    private fun editAdditionalMenu(additionalMenu: AdvanceAdditionalMenuEdit) {
        if (!mutableDetailsAdditionalMenuEdit.value!!.none { it.ext_menu_name == additionalMenu.ext_menu_name }) {
            mutableDetailsAdditionalMenuEdit.value =
                    detailsAdditionalMenuListEdit.value?.filter { it.ext_menu_name != additionalMenu.ext_menu_name }
        }

        mutableDetailsAdditionalMenuEdit.value =
                detailsAdditionalMenuListEdit.value?.toMutableList()?.apply { add(additionalMenu) }
        if (additionalNamaDaftarPilihan.value != mutableAdditionalPreviousName.value) {
            mutableDetailsAdditionalMenuEdit.value = detailsAdditionalMenuListEdit.value?.filter { it.ext_menu_name != mutableAdditionalPreviousName.value }
        }
    }

    fun validateDetailsScreenEdit(): Boolean {
        if (!isDetailsNamaPilihanValid.value!! || !isDetailsPilihanMaksimalValid.value!!) return false

        editAdvanceMenu(
                AdvanceMenuEdit(
                        product_id = productId.value,
                        template_name = detailsNamaPilihan.value!!,
                        template_type = if (detailsPilihanMaksimal.value!! == 1) AdvanceMenuTemplateType.RADIO.toString() else AdvanceMenuTemplateType.CHECKBOX.toString(),
                        active = isDetailsAktif.value!!,
                        mandatory = isDetailsWajib.value!!,
                        max_choose = detailsPilihanMaksimal.value!!,
                        id = advanceId.value!!,
                        ext_menus = detailsAdditionalMenuListEdit.value ?: listOf()
                )
        )

        return true
    }

    fun validateDetailsScreenForUpdate(): Boolean {
        if (!isDetailsNamaPilihanValid.value!! || !isDetailsPilihanMaksimalValid.value!!) return false

        val updateMenuChoice = AdvanceMenuEdit(
                product_id = productId.value,
                template_name = detailsNamaPilihan.value!!,
                template_type = if (detailsPilihanMaksimal.value!! == 1) AdvanceMenuTemplateType.RADIO.toString() else AdvanceMenuTemplateType.CHECKBOX.toString(),
                active = isDetailsAktif.value!!,
                mandatory = isDetailsWajib.value!!,
                max_choose = detailsPilihanMaksimal.value!!,
                id = advanceId.value!!,
                ext_menus = detailsAdditionalMenuListEdit.value ?: listOf()
        )

        var sessionManager = SessionManager(getApplication())
        val email = sessionManager.getUserData()!!.email!!
        val token = sessionManager.getUserToken()!!
        val timestamp = getTimestamp()
        val signature = getSignature(email, timestamp)

        // TODO: Update API call.
        PikappApiService().api.uptadeAdvanceMenu(
                getUUID(), timestamp, getClientID(), signature, token, updateMenuChoice
        ).enqueue(object : Callback<ListAdvanceMenuEditResp> {
            override fun onResponse(call: Call<ListAdvanceMenuEditResp>, response: Response<ListAdvanceMenuEditResp>) {
                setLoading(false)
                setLocalLoading(false)
            }

            override fun onFailure(call: Call<ListAdvanceMenuEditResp>, t: Throwable) {
                setLoading(false)
                Log.e("failed", t.message.toString())
            }

        })

        return true
    }

    fun updateAdvanceMenu() {
        val updateMenuChoice = AdvanceMenuEdit(
                product_id = productId.value,
                template_name = detailsNamaPilihan.value!!,
                template_type = if (detailsPilihanMaksimal.value!! == 1) AdvanceMenuTemplateType.RADIO.toString() else AdvanceMenuTemplateType.CHECKBOX.toString(),
                active = isDetailsAktif.value!!,
                mandatory = isDetailsWajib.value!!,
                max_choose = detailsPilihanMaksimal.value!!,
                id = advanceId.value!!,
                ext_menus = detailsAdditionalMenuListEdit.value ?: listOf()
        )

        var sessionManager = SessionManager(getApplication())
        val email = sessionManager.getUserData()!!.email!!
        val token = sessionManager.getUserToken()!!
        val timestamp = getTimestamp()
        val signature = getSignature(email, timestamp)

        // TODO: Update API call.
        PikappApiService().api.uptadeAdvanceMenu(
                getUUID(), timestamp, getClientID(), signature, token, updateMenuChoice
        ).enqueue(object : Callback<ListAdvanceMenuEditResp> {
            override fun onResponse(call: Call<ListAdvanceMenuEditResp>, response: Response<ListAdvanceMenuEditResp>) {
                setLoading(false)
                setLocalLoading(false)
            }

            override fun onFailure(call: Call<ListAdvanceMenuEditResp>, t: Throwable) {
                setLoading(false)
                Log.e("failed", t.message.toString())
            }

        })
    }
    /*EDIT EXTRA MENUS END*/

    fun addNewAdvanceMenus(): Boolean {
        if (!isDetailsNamaPilihanValid.value!! || !isDetailsPilihanMaksimalValid.value!!) return false

        var sessionManager = SessionManager(getApplication())
        val email = sessionManager.getUserData()!!.email!!
        val token = sessionManager.getUserToken()!!
        val timestamp = getTimestamp()
        val signature = getSignature(email, timestamp)
        val mid = sessionManager.getUserData()!!.mid!!

        val newAdvanceMenus = AddNewAdvanceMenu(
                product_id = productId.value!!,
                advance_menu = AdvanceMenu(
                        template_name = detailsNamaPilihan.value!!,
                        template_type = if (detailsPilihanMaksimal.value!! == 1) AdvanceMenuTemplateType.RADIO.toString() else AdvanceMenuTemplateType.CHECKBOX.toString(),
                        active = isDetailsAktif.value!!,
                        mandatory = isDetailsWajib.value!!,
                        max_choose = detailsPilihanMaksimal.value!!,
                        ext_menus = detailsAdditionalMenuList.value ?: listOf()
                )
        )

        apiService.api.addNewAdvanceMenu(
                getUUID(), timestamp, getClientID(), signature, token, mid, newAdvanceMenus
        ).enqueue(object : Callback<ListNewAdvanceMenuResponse> {
            override fun onResponse(call: Call<ListNewAdvanceMenuResponse>, response: Response<ListNewAdvanceMenuResponse>) {
                setLoading(false)
                setLocalLoading(false)
            }

            override fun onFailure(call: Call<ListNewAdvanceMenuResponse>, t: Throwable) {
                setLoading(false)
                Log.e(TAG, t.message.toString())
            }

        })

        return true
    }

    fun resetDetailsScreen() {
        mutableDetailsNamaPilihan.value = ""
        mutableDetailsNamaPilihanError.value = ""
        isDetailsNamaPilihanValid.value = false

        mutableIsDetailsAktif.value = true
        mutableIsDetailsWajib.value = true

        mutableDetailsPilihanMaksimal.value = 1
        mutableDetailsPilihanMaksimalError.value = ""
        isDetailsPilihanMaksimalValid.value = false

        mutableDetailsAdditionalMenuList.value = listOf()
        mutableDetailsAdditionalMenuEdit.value = listOf()
    }

    /**
     * Additional screen specific flow
     */
    private val mutableAdditionalNamaDaftarPilihan = MutableLiveData<String>("")
    private val mutableAdditionalNamaDaftarPilihanError = MutableLiveData<String>("")
    private val isAdditionalNamaDaftarPilihanValid = MutableLiveData<Boolean>(false)
    val additionalNamaDaftarPilihan: LiveData<String> = mutableAdditionalNamaDaftarPilihan
    val additionalNamaDaftarPilihanError: LiveData<String> = mutableAdditionalNamaDaftarPilihanError
    fun setAdditionalNamaDaftarPilihan(namaDaftarPilihan: String) {
        mutableAdditionalNamaDaftarPilihan.value = namaDaftarPilihan
    }

    private val mutableAdditionalHarga = MutableLiveData<String>("")
    private val mutableAdditionalHargaError = MutableLiveData<String>("")
    private val isAdditionalHargaValid = MutableLiveData<Boolean>(false)
    val additionalHarga: LiveData<String> = mutableAdditionalHarga
    val additionalHargaError: LiveData<String> = mutableAdditionalHargaError
    fun setAdditionalHarga(harga: String) {
        mutableAdditionalHarga.value = harga
    }

    private val mutableAdditionalPreviousName = MutableLiveData<String>("")
    fun setAdditionalPreviousName(namaDaftarPilihan: String) {
        mutableAdditionalPreviousName.value = namaDaftarPilihan
    }

    private val mutableIsNewMenuChoice = MutableLiveData(0)
    val isNewMenuChoice: LiveData<Int> = mutableIsNewMenuChoice
    fun setNewMenuChoice(num: Int) {
        mutableIsNewMenuChoice.value = num
    }

    fun validateAdditionalNamaDaftarPilihan(namaDaftarPilihan: String): Boolean {
        if (namaDaftarPilihan.isEmpty() || namaDaftarPilihan.isBlank()) {
            mutableAdditionalNamaDaftarPilihanError.value = "Nama daftar pilihan tidak boleh kosong"
        } else {
            mutableAdditionalNamaDaftarPilihanError.value = ""
        }

        mutableAdditionalNamaDaftarPilihan.value = namaDaftarPilihan
        isAdditionalNamaDaftarPilihanValid.value =
            mutableAdditionalNamaDaftarPilihanError.value!!.isEmpty()
        return isAdditionalNamaDaftarPilihanValid.value!!
    }

    fun validateAdditionalHarga(harga: String): Boolean {
        if (harga.isEmpty() || harga.isBlank()) {
            mutableAdditionalHargaError.value = "Harga tidak boleh kosong"
        } else if (!harga.isDigitsOnly()) {
            mutableAdditionalHargaError.value = "Harga hanya boleh mengandung angka"
        } else {
            mutableAdditionalHargaError.value = ""
        }

        mutableAdditionalHarga.value = harga
        isAdditionalHargaValid.value = mutableAdditionalHargaError.value!!.isEmpty()
        return isAdditionalHargaValid.value!!
    }

    /*ADD EACH EXTRA MENU START*/
    fun validateAdditionalScreen(): Boolean {
        if (!isAdditionalNamaDaftarPilihanValid.value!! || !isAdditionalHargaValid.value!!) return false

        addAdditionalMenu(
                AdvanceAdditionalMenu(
                        ext_menu_name = additionalNamaDaftarPilihan.value!!,
                        ext_menu_price = additionalHarga.value!!,
                        active = true
                )
        )
        return true
    }

    fun sortAdditionalMenu(additionalMenu: List<AdvanceAdditionalMenu>) {
        mutableDetailsAdditionalMenuList.value = additionalMenu
    }

    fun deleteAdditionalMenu(choiceName: String?) {
        mutableDetailsAdditionalMenuList.value = detailsAdditionalMenuList.value?.filter { it.ext_menu_name != choiceName }
        setLocalLoading(false)
    }
    /*ADD EACH EXTRA MENU END*/

    /*EDIT EACH EXTRA MENU START*/
    fun validateAdditionalScreenEdit(): Boolean {
        if (!isAdditionalNamaDaftarPilihanValid.value!! || !isAdditionalHargaValid.value!!) return false

        editAdditionalMenu(
                AdvanceAdditionalMenuEdit(
                        ext_menu_name = additionalNamaDaftarPilihan.value!!,
                        ext_menu_price = additionalHarga.value!!,
                        active = true,
                        ext_id = menuExtId.value!!
                )
        )
        return true
    }

    fun sortAdditionalMenuEdit(additionalMenu: List<AdvanceAdditionalMenuEdit>) {
        mutableDetailsAdditionalMenuEdit.value = additionalMenu
    }

    fun deleteAdditionalMenuEdit() {
        var sessionManager = SessionManager(getApplication())
        val email = sessionManager.getUserData()!!.email!!
        val token = sessionManager.getUserToken()!!
        val timestamp = getTimestamp()
        val signature = getSignature(email, timestamp)
        val mid = sessionManager.getUserData()!!.mid!!
        val pid = productId.value.toString()

        setNewMenuChoice(2)
        val extId = menuExtId.value.toString()

        apiService.api.deleteExtraMenu(
                getUUID(), timestamp, getClientID(), signature, token, mid, pid, extId
        ).enqueue(object : Callback<BaseResponse> {
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                fetchEditDataAfterUpdate()
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                setLoading(false)
                Log.e(TAG, t.message.toString())
            }

        })
    }

    fun fetchEditDataAfterUpdate() {
        val timeStamp = getTimestamp()

        setLoading(true)
        disposable.add(
                apiService.listAdvanceMenuEdit(
                        email = sessionManager.getUserData()?.email ?: "",
                        token = sessionManager.getUserToken() ?: "",
                        pid = mutableProductId.value ?: "",
                        timeStamp = timeStamp
                ).subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableSingleObserver<ListAdvanceMenuEditResponse>() {
                            override fun onSuccess(response: ListAdvanceMenuEditResponse) {
                                val selectedAdvanceMenu = response.results.filter { it.template_name == detailsNamaPilihan.value }
                                val objectAdvanceMenuSelection = selectedAdvanceMenu[0]
                                setDetailsAdditionalMenuEditList(objectAdvanceMenuSelection.ext_menus)
                                if (detailsPilihanMaksimal.value!! > objectAdvanceMenuSelection.ext_menus.size) {
                                    setDetailsPilihanMaksimal(detailsPilihanMaksimal.value!! - 1)
                                    updateAdvanceMenu()
                                } else {
                                    setLoading(false)
                                    setLocalLoading(false)
                                }
                            }

                            override fun onError(e: Throwable) {
                                Log.d(TAG, e.message.toString())
                                setLoading(false)
                            }
                        })
        )
    }
    /*EDIT EACH EXTRA MENU END*/

    fun sendNewExtraMenu() : Boolean {
        if (!isAdditionalNamaDaftarPilihanValid.value!! || !isAdditionalHargaValid.value!!) return false

        var sessionManager = SessionManager(getApplication())
        val email = sessionManager.getUserData()!!.email!!
        val token = sessionManager.getUserToken()!!
        val timestamp = getTimestamp()
        val signature = getSignature(email, timestamp)
        val mid = sessionManager.getUserData()!!.mid!!

        val newExtMenuChoice = AddNewExtraMenu(
                advance_menu_id = advanceId.value!!,
                ext_menu_name = additionalNamaDaftarPilihan.value!!,
                ext_menu_price = additionalHarga.value!!,
                active = true,
                product_id = productId.value!!
        )

        apiService.api.addNewExtraMenu(
                getUUID(), timestamp, getClientID(), signature, token, mid, newExtMenuChoice
        ).enqueue(object : Callback<NewExtraMenuResponse> {
            override fun onResponse(call: Call<NewExtraMenuResponse>, response: Response<NewExtraMenuResponse>) {
                fetchEditDataAfterUpdate()
            }

            override fun onFailure(call: Call<NewExtraMenuResponse>, t: Throwable) {
                setLoading(false)
                Log.e(TAG, t.message.toString())
            }

        })

        return true
    }

    fun resetAdditionalScreen() {
        mutableAdditionalNamaDaftarPilihan.value = ""
        mutableAdditionalNamaDaftarPilihanError.value = ""
        isAdditionalNamaDaftarPilihanValid.value = false

        mutableAdditionalHarga.value = ""
        mutableAdditionalHargaError.value = ""
        isAdditionalHargaValid.value = false
    }
}