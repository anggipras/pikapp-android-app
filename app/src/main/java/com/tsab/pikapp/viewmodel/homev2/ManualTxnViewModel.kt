package com.tsab.pikapp.viewmodel.homev2

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.tsab.pikapp.models.model.*
import com.tsab.pikapp.models.network.PikappApiService
import com.tsab.pikapp.util.*
import com.tsab.pikapp.view.homev2.transaction.manualTxn.ManualAddAdvMenuFragment
import com.tsab.pikapp.viewmodel.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManualTxnViewModel(application: Application) : BaseViewModel(application) {
    private val tag = javaClass.simpleName
    private val sessionManager = SessionManager(getApplication())

    private val apiService = PikappApiService()
    private val disposable = CompositeDisposable()

    private val mutableSelectedMenuTemp = MutableLiveData<List<AddManualAdvMenu>>(listOf())
    val selectedMenuTemp: LiveData<List<AddManualAdvMenu>> = mutableSelectedMenuTemp

    val mutableMenuList = MutableLiveData<List<SearchItem>>(listOf())
    val menuList: LiveData<List<SearchItem>> = mutableMenuList
    fun setMenuList(menuList: List<SearchItem>) {
        mutableMenuList.value = menuList
    }

    val mutableMenuListEmpty = MutableLiveData<List<SearchItem>>(listOf())
    val menuListEmpty: LiveData<List<SearchItem>> = mutableMenuListEmpty

    val mutableSearchEnter = MutableLiveData(false)
    val isSearch: LiveData<Boolean> get() = mutableSearchEnter

    val mutableSearchMenu = MutableLiveData("")
    val MenuSubmit: LiveData<String> get() = mutableSearchMenu

    val mutableAdvanceData = MutableLiveData<ArrayList<AdvanceMenu>>()
    val AdvanceData: LiveData<ArrayList<AdvanceMenu>> get() =mutableAdvanceData

    /*SET MENU DETAIL START*/
    val mutablePID = MutableLiveData("")
    val PID: LiveData<String> get() = mutablePID
    fun setPID(pid: String){
        mutablePID.value = pid
    }

    private val mutableMenuImg = MutableLiveData(Uri.EMPTY)
    val menuImg: LiveData<Uri> get() = mutableMenuImg
    fun setMenuImg(img: String){
        mutableMenuImg.value = img.toUri()
    }

    private val mutableMenuName = MutableLiveData("")
    val menuName: LiveData<String> get() = mutableMenuName
    fun setMenuName(name: String){
        mutableMenuName.value = name
    }

    private val mutableMenuPrice = MutableLiveData("")
    val menuPrice: LiveData<String> get() = mutableMenuPrice
    fun setMenuPrice(price: String){
        mutableMenuPrice.value = price
    }
    /*SET MENU DETAIL END*/

    private var mutableLoading = MutableLiveData<Boolean>(true)
    val isLoading: LiveData<Boolean> get() = mutableLoading
    fun setLoading(boolean: Boolean) {
        mutableLoading.value = boolean
    }

    private var receivedAdv = MutableLiveData<Boolean>(false)
    val isAdvReceived: LiveData<Boolean> get() = receivedAdv
    fun setTrigger(boolean: Boolean) {
        receivedAdv.value = boolean
    }

    private val mutableNote = MutableLiveData("")
    val note: LiveData<String> get() = mutableNote
    fun setManualNote(note: String) {
        mutableNote.value = note
    }

    private val mutableTotalPrice = MutableLiveData<Long>()
    val totalPrice: LiveData<Long> get() = mutableTotalPrice

    private val mutableExtraPrice = MutableLiveData(0)
    val extraPrice: LiveData<Int> get() = mutableExtraPrice
    fun setExtraPrice(extPrice: Int) {
        mutableExtraPrice.value = extPrice
        countTotalPrice()
    }

    private fun countTotalPrice(){
        //count only all extra price with amount if available
        val extraAmount = quantity.value?.times(extraPrice.value!!)

        //count only menu with amount
        val menuAmount: Long? = quantity.value?.times(menuPrice.value?.toLong()!!)

        mutableTotalPrice.value = menuAmount!! + extraAmount!!
    }

    private val mutableCartPrice = MutableLiveData(0)
    val totalCart: LiveData<Int> get() = mutableCartPrice
    fun cartTotalPrice(totPrice: String, price: String){
        if (totPrice != "null"){
            mutableCartPrice.value = mutableCartPrice.value?.plus(totPrice.toInt())
        } else {
            mutableCartPrice.value = mutableCartPrice.value?.plus(price.toInt())
        }
    }

    private val mutableTotalQuantity = MutableLiveData(0)
    val totalQuantity: LiveData<Int> get() = mutableTotalQuantity
    fun addTotalQty(qty : Int){
        mutableTotalQuantity.value = mutableTotalQuantity.value?.plus(qty)
    }

    private val mutableTotalItems = MutableLiveData(0)
    val totalItems: LiveData<Int> get() = mutableTotalItems
    private var x : String = ""
    fun addTotalItems(name: String){
        if (name != x) {
            mutableTotalItems.value = mutableTotalItems.value?.plus(1)
            x = name
        }
    }

    private val mutableQuantity = MutableLiveData(1)
    val quantity: LiveData<Int> get() = mutableQuantity
    fun addQty() {
        mutableQuantity.value = mutableQuantity.value?.plus(1)
        countTotalPrice()
    }
    fun minusQty() {
        if (mutableQuantity.value!! > 1) {
            mutableQuantity.value = mutableQuantity.value?.minus(1)
            countTotalPrice()
        }
    }

    fun addToCart(foodNote: String, foodExtraList: ArrayList<ManualAddAdvMenuFragment.AddAdvMenuTemp>) {
        //mapping radio and or checkbox menu choice
        var foodExtraRadio: MutableList<FoodListParentRadio> = ArrayList()
        var foodExtraCheck: MutableList<FoodListParentCheck> = ArrayList()
        var foodExtraNotes: String = ""
        foodExtraList.forEach {
            if (it.template_type == "RADIO") {
                it.ext_menus.forEach { extMenuRad ->
                    foodExtraRadio.add(FoodListParentRadio(menuChoiceName = it?.template_name!!, foodListChildRadio = FoodListRadio(name = extMenuRad?.ext_menu_name!!, price = extMenuRad.ext_menu_price!!)))
                    foodExtraNotes.plus("${extMenuRad.ext_menu_name} ")
                }
            } else {
                val foodListCheck: MutableList<FoodListCheck> = ArrayList()
                it.ext_menus.forEach { extMenuCheck ->
                    foodListCheck.add(FoodListCheck(name = extMenuCheck?.ext_menu_name!!, price = extMenuCheck.ext_menu_price!!))
                    if (extMenuCheck.ext_menu_name.isNotEmpty()) {
                        foodExtraNotes.plus("${extMenuCheck.ext_menu_name} ")
                    }
                }
                foodExtraCheck.add(FoodListParentCheck(menuChoiceName = it.template_name!!, foodListChildCheck = foodListCheck))
            }
        }

        mutableSelectedMenuTemp.value = selectedMenuTemp.value?.toMutableList()?.apply {
            add(AddManualAdvMenu(
                product_id = PID.value,
                foodName = menuName.value!!,
                foodImg = menuImg.value.toString(),
                foodAmount = quantity.value!!,
                foodPrice = menuPrice.value!!,
                foodListCheckbox = foodExtraCheck,
                foodListRadio = foodExtraRadio,
                foodExtra = foodExtraNotes,
                foodNote = foodNote,
                foodTotalPrice = totalPrice.value.toString()
        )) }
        addTotalQty(quantity.value!!)
        addTotalItems(menuName.value.toString())
        cartTotalPrice(totalPrice.value.toString(), menuPrice.value.toString())
        Log.e("cart list", mutableSelectedMenuTemp.value.toString())
    }

    fun getMenuList() {
        val email = sessionManager.getUserData()!!.email!!
        val token = sessionManager.getUserToken()!!
        val timestamp = getTimestamp()
        val signature = getSignature(email, timestamp)
        val mid = sessionManager.getUserData()!!.mid!!

        PikappApiService().api.searchMenu(
                getUUID(), timestamp, getClientID(), signature, token, mid, SearchRequest(mutableSearchMenu.value!!, 0, 7)
        ).enqueue(object : Callback<SearchResponse> {
            override fun onResponse(
                    call: Call<SearchResponse>,
                    response: Response<SearchResponse>
            ) {
                if (response.code() == 200 && response.body()!!.errCode.toString() == "EC0000") {
                    val amountOfMenus = response.body()!!.total_items
                    if (amountOfMenus != 0) {
                        getSearchList(amountOfMenus)
                    } else {
                        mutableMenuList.value = mutableMenuListEmpty.value
                        Log.e("Zero_Product", "There is no product available")
                    }
                    setLoading(false)
                } else {
                    Log.e("FAIL", "Failed get amount of menus")
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                Log.e("FAILED", t.message.toString())
            }
        })
    }

    fun getSearchList(amountOfMenus: Int) {
        /*if (menuList.value!!.isNotEmpty()) return*/

        val email = sessionManager.getUserData()!!.email!!
        val token = sessionManager.getUserToken()!!
        val timestamp = getTimestamp()
        val signature = getSignature(email, timestamp)
        val mid = sessionManager.getUserData()!!.mid!!

        PikappApiService().api.searchMenu(
                getUUID(), timestamp, getClientID(), signature, token,
                mid, SearchRequest(mutableSearchMenu.value!!, 0, amountOfMenus)
        ).enqueue(object : Callback<SearchResponse> {
            override fun onResponse(
                    call: Call<SearchResponse>,
                    response: Response<SearchResponse>
            ) {
                val searchResult = response.body()?.results
                setMenuList(searchResult ?: listOf())
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                Log.e(tag, "Error: " + t.message.toString())
            }
        })
    }

    fun searchMenu(name: String, status: Boolean){
        mutableSearchMenu.value = name
        mutableSearchEnter.value = status
    }

    fun fetchAdvanceMenuData(menu: ArrayList<AdvanceMenu>, list: RecyclerView) {
        if (mutablePID.value == null) {
            return
        }
        val timeStamp = getTimestamp()

        setLoading(true)
        disposable.add(
            apiService.listAdvanceMenu(
                email = sessionManager.getUserData()?.email ?: "",
                token = sessionManager.getUserToken() ?: "",
                pid = mutablePID.value ?: "",
                timeStamp = timeStamp
            ).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<ListAdvanceMenuResponse>() {
                    override fun onSuccess(response: ListAdvanceMenuResponse) {
                        // TODO: Add is advance menu active.
                        if (response.results.isNotEmpty()) {
                            for (i in response.results){
                                menu.add(i)
                            }
                            list.adapter?.notifyDataSetChanged()
                            setTrigger(true)
                        }
                        setLoading(false)
                    }

                    override fun onError(e: Throwable) {
                        Log.d("Fail", e.message.toString())
                        setLoading(false)
                    }
                })
        )
    }
}