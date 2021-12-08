package com.tsab.pikapp.viewmodel.homev2

import android.app.Activity
import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tsab.pikapp.R
import com.tsab.pikapp.models.model.*
import com.tsab.pikapp.models.network.PikappApiService
import com.tsab.pikapp.util.*
import com.tsab.pikapp.view.homev2.transaction.OmniTransactionListAdapter
import com.tsab.pikapp.view.homev2.transaction.manualTxn.ManualAddAdvMenuFragment
import com.tsab.pikapp.view.homev2.transaction.manualTxn.ManualTxnCustomerPage
import com.tsab.pikapp.view.homev2.transaction.manualTxn.ManualTxnListAdapter
import com.tsab.pikapp.view.onboarding.login.navController
import com.tsab.pikapp.viewmodel.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

class ManualTxnViewModel(application: Application) : BaseViewModel(application) {
    private val tag = javaClass.simpleName
    private val sessionManager = SessionManager(getApplication())

    private val apiService = PikappApiService()
    private val disposable = CompositeDisposable()

    lateinit var manualTxnAdapter: ManualTxnListAdapter
    lateinit var linearLayoutManager: LinearLayoutManager

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

    val mutablePayStat = MutableLiveData(false)
    val payStatus: LiveData<Boolean> get() = mutablePayStat

    val mutableSearchMenu = MutableLiveData("")
    val MenuSubmit: LiveData<String> get() = mutableSearchMenu

    val mutableNamaEkspedisi = MutableLiveData("")
    val NamaEkspedisi: LiveData<String> get() = mutableNamaEkspedisi

    val mutableHargaEkspedisi = MutableLiveData("")
    val HargaEkspedisi: LiveData<String> get() = mutableHargaEkspedisi

    val mutableAsal = MutableLiveData("")
    val AsalPesanan: LiveData<String> get() = mutableAsal

    val mutableBayar = MutableLiveData("")
    val BayarPesanan: LiveData<String> get() = mutableBayar

    val mutablePostWaktu = MutableLiveData("")
    val postWaktu: LiveData<String> get() = mutablePostWaktu

    val mutableDate = MutableLiveData("")
    val DatePesanan: LiveData<String> get() = mutableDate

    val mutableHour = MutableLiveData("")
    val JamPesanan: LiveData<String> get() = mutableHour

    val mutableWaktu = MutableLiveData("")
    val WaktuPesan: LiveData<String> get() = mutableWaktu

    val mutableCustomWaktu = MutableLiveData("")
    val WaktuPesanCustom: LiveData<String> get() = mutableCustomWaktu

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

    fun countTotalPrice(){
        //count only all extra price with amount if available
        val extraAmount = quantity.value?.times(extraPrice.value!!)

        //count only menu with amount
        val menuAmount: Long? = quantity.value?.times(menuPrice.value?.toLong()!!)

        mutableTotalPrice.value = menuAmount!! + extraAmount!!
    }

    val mutableCartPrice = MutableLiveData(0)
    val totalCart: LiveData<Int> get() = mutableCartPrice
    fun cartTotalPrice(totPrice: String, price: String){
        if (totPrice != "null"){
            mutableCartPrice.value = mutableCartPrice.value?.plus(totPrice.toInt())
        } else {
            mutableCartPrice.value = mutableCartPrice.value?.plus(price.toInt())
        }
    }

    fun setTotalPrice() {
        var totalPrice = 0
        selectedMenuTemp.value?.forEach { menu ->
            totalPrice += menu.foodTotalPrice.toInt()
        }
        mutableCartPrice.value = totalPrice
    }

    fun setEkspedisi(nama: String, harga: String){
        mutableNamaEkspedisi.value = nama
        mutableHargaEkspedisi.value = harga
    }

    fun setAsal(nama: String){
        mutableAsal.value = nama
    }

    fun setBayar(nama: String){
        mutableBayar.value = nama
    }

    fun setWaktu(nama: String, custom:String){
        mutableWaktu.value = nama
        mutableCustomWaktu.value = custom
    }

    fun setDate(nama: String){
        mutableDate.value = nama
    }

    fun setTime(nama: String){
        mutableHour.value = nama
    }


    private val mutableTotalQuantity = MutableLiveData(0)
    val totalQuantity: LiveData<Int> get() = mutableTotalQuantity
    fun addTotalQty(){
        var totalAmount = 0
        selectedMenuTemp.value?.forEach { amount ->
            totalAmount += amount.foodAmount
        }
        mutableTotalQuantity.value = totalAmount
    }

    private val mutableTotalItems = MutableLiveData(0)
    val totalItems: LiveData<Int> get() = mutableTotalItems
    private var x : String = ""
    fun addTotalItems(quantity: Int){
        mutableTotalItems.value = mutableTotalItems.value?.plus(quantity)
    }
    fun setCartItems(size: Int) {
        mutableTotalItems.value = size
    }

    private val mutableQuantity = MutableLiveData(1)
    val quantity: LiveData<Int> get() = mutableQuantity
    fun setQty(qty: Int) {
        mutableQuantity.value = qty
    }
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

    //customer detail
    val mutableCustName = MutableLiveData("")
    val custName: LiveData<String> get() = mutableCustName
    fun setCustName(custName: String) {
        mutableCustName.value = custName
    }

    val mutableCustPhone = MutableLiveData("")
    val custPhone: LiveData<String> get() = mutableCustPhone
    fun setCustPhone(custPhone: String) {
        mutableCustPhone.value = custPhone
    }

    val mutableCustAddress = MutableLiveData("")
    val custAddress: LiveData<String> get() = mutableCustAddress
    fun setCustAddress(custAddress: String) {
        mutableCustAddress.value = custAddress
    }

    val mutableCustAddressDetail = MutableLiveData("")
    val custAddressDetail: LiveData<String> get() = mutableCustAddressDetail
    fun setCustAddressDetail(addressDetail: String) {
        mutableCustAddressDetail.value = addressDetail
    }

    private val mutableCustId = MutableLiveData(0L)
    val custId: LiveData<Long> get() = mutableCustId
    fun setCustId(id: Long) {
        mutableCustId.value = id
    }

    val mutableAddCustName = MutableLiveData("")
    val addCustName: LiveData<String> get() = mutableAddCustName
    fun addCustName(custName: String) {
        mutableAddCustName.value = custName
    }

    val mutableAddCustPhone = MutableLiveData("")
    val addCustPhone: LiveData<String> get() = mutableAddCustPhone
    fun addCustPhone(custPhone: String) {
        mutableAddCustPhone.value = custPhone
    }

    val mutableAddCustAddress = MutableLiveData("")
    val addCustAddress: LiveData<String> get() = mutableAddCustAddress
    fun addCustAddress(custAddress: String) {
        mutableAddCustAddress.value = custAddress
    }

    val mutableAddCustAddressDetail = MutableLiveData("")
    val addCustAddressDetail: LiveData<String> get() = mutableAddCustAddressDetail
    fun addCustAddressDetail(addressDetail: String) {
        mutableAddCustAddressDetail.value = addressDetail
    }

    private val mutableAddCustId = MutableLiveData(0L)
    val addCustId: LiveData<Long> get() = mutableAddCustId
    fun addCustId(id: Long) {
        mutableAddCustId.value = id
    }


    val mutableCustNameTemp = MutableLiveData("")
    val custNameTemp: LiveData<String> get() = mutableCustNameTemp
    fun setCustNameTemp(custName: String) {
        mutableCustNameTemp.value = custName
    }

    val mutableCustPhoneTemp = MutableLiveData("")
    val custPhoneTemp: LiveData<String> get() = mutableCustPhoneTemp
    fun setCustPhoneTemp(custPhone: String) {
        mutableCustPhoneTemp.value = custPhone
    }

    val mutableCustAddressTemp = MutableLiveData("")
    val custAddressTemp: LiveData<String> get() = mutableCustAddressTemp
    fun setCustAddressTemp(custAddress: String) {
        mutableCustAddressTemp.value = custAddress
    }

    val mutableCustAddressDetailTemp = MutableLiveData("")
    val custAddressDetailTemp: LiveData<String> get() = mutableCustAddressDetailTemp
    fun setCustAddressDetailTemp(addressDetail: String) {
        mutableCustAddressDetailTemp.value = addressDetail
    }

    private val mutableCustIdTemp = MutableLiveData(0L)
    val custIdTemp: LiveData<Long> get() = mutableCustIdTemp
    fun setCustIdTemp(id: Long) {
        mutableCustIdTemp.value = id
    }

    private val mutableEditCustName = MutableLiveData("")
    val editCustName: LiveData<String> get() = mutableEditCustName
    fun editCustName(custName: String) {
        mutableEditCustName.value = custName
    }

    private val mutableEditCustPhone = MutableLiveData("")
    val editCustPhone: LiveData<String> get() = mutableEditCustPhone
    fun editCustPhone(custPhone: String) {
        mutableEditCustPhone.value = custPhone
    }

    private val mutableEditCustAddress = MutableLiveData("")
    val editCustAddress: LiveData<String> get() = mutableEditCustAddress
    fun editCustAddress(custAddress: String) {
        mutableEditCustAddress.value = custAddress
    }

    private val mutableEditCustAddressDetail = MutableLiveData("")
    val editCustAddressDetail: LiveData<String> get() = mutableEditCustAddressDetail
    fun editCustAddressDetail(addressDetail: String) {
        mutableEditCustAddressDetail.value = addressDetail
    }

    private val mutableCustomerList = MutableLiveData<List<CustomerResponseDetail>>(listOf())
    val customerList: LiveData<List<CustomerResponseDetail>> = mutableCustomerList

    private val mutableSizeCustomer = MutableLiveData(0)
    val customerSize: LiveData<Int> get() = mutableSizeCustomer

    fun addToCart(foodNote: String, foodExtraList: ArrayList<ManualAddAdvMenuFragment.AddAdvMenuTemp>, view: View) {
        //mapping radio and or checkbox menu choice
        var foodExtraRadio: MutableList<FoodListParentRadio> = ArrayList()
        var foodExtraCheck: MutableList<FoodListParentCheck> = ArrayList()
        foodExtraList.forEach {
            if (it.template_type == "RADIO") {
                it.ext_menus.forEach { extMenuRad ->
                    foodExtraRadio.add(FoodListParentRadio(menuChoiceName = it?.template_name!!, foodListChildRadio = FoodListRadio(name = extMenuRad?.ext_menu_name!!, price = extMenuRad.ext_menu_price!!)))
                }
            } else {
                val foodListCheck: MutableList<FoodListCheck> = ArrayList()
                it.ext_menus.forEach { extMenuCheck ->
                    foodListCheck.add(FoodListCheck(name = extMenuCheck?.ext_menu_name!!, price = extMenuCheck.ext_menu_price!!))
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
                foodExtra = "",
                foodNote = foodNote,
                foodTotalPrice = totalPrice.value.toString()
        )) }
//        addTotalQty(quantity.value!!)
        addTotalItems(quantity.value!!)
        cartTotalPrice(totalPrice.value.toString(), menuPrice.value.toString())
        Navigation.findNavController(view).popBackStack()
    }

    fun addCustomer() {
        val mid = sessionManager.getUserData()!!.mid!!
        val addReq = addCustomerRequest(
            name = addCustName.value,
            mid = mid,
            address = addCustAddress.value,
            addressDetail = addCustAddressDetail.value,
            phoneNumber = addCustPhone.value
        )

        PikappApiService().api.addCustomer(addReq).enqueue(object : Callback<CustomerResponse>{
            override fun onResponse(
                call: Call<CustomerResponse>,
                response: Response<CustomerResponse>
            ) {
                Log.e("response body", "succeed")
            }

            override fun onFailure(call: Call<CustomerResponse>, t: Throwable) {
                Timber.tag(tag).d("Failed to add customer list: ${t.message.toString()}")
            }

        })
    }

    fun editCustomer(){
        val mid = sessionManager.getUserData()!!.mid!!
        val editReq = EditCustomerRequest(
            customerId = custIdTemp.value,
            mid = mid,
            name = editCustName.value,
            address = editCustAddress.value,
            addressDetail = editCustAddressDetail.value,
            phoneNumber = editCustPhone.value
        )

        PikappApiService().api.editCustomer(editReq).enqueue(object : Callback<CustomerResponse>{
            override fun onResponse(
                call: Call<CustomerResponse>,
                response: Response<CustomerResponse>
            ) {
                Log.e("edit customer: ", "succeed")
            }

            override fun onFailure(call: Call<CustomerResponse>, t: Throwable) {
                Timber.tag(tag).d("Failed to edit customer : ${t.message.toString()}")
            }

        })
    }

    fun deleteCustomer(){
        custIdTemp.value?.let { PikappApiService().api.deleteCustomer(it).enqueue(object : Callback<DeleteCustomerResponse>{
            override fun onResponse(
                call: Call<DeleteCustomerResponse>,
                response: Response<DeleteCustomerResponse>
            ) {
                Log.e("delete customer: ", "succeed")
            }

            override fun onFailure(call: Call<DeleteCustomerResponse>, t: Throwable) {
                Timber.tag(tag).d("Failed to delete customer : ${t.message.toString()}")
            }

        }) }
    }

    fun getCustomer(){
        val mid = sessionManager.getUserData()!!.mid!!
        val page = "0"
        val size = "50"
        setLoading(true)

        PikappApiService().api.getListCustomer(page, size, mid).enqueue(object : Callback<CustomerResponse>{
            override fun onResponse(
                call: Call<CustomerResponse>,
                response: Response<CustomerResponse>
            ) {
                val orderResponse = response.body()
                val resultList = orderResponse?.results
                if (resultList != null){
                    mutableCustomerList.value = resultList!!
                    mutableSizeCustomer.value = mutableCustomerList.value?.size
                    setLoading(false)
                }
            }

            override fun onFailure(call: Call<CustomerResponse>, t: Throwable) {
                Timber.tag(tag).d("Failed to get customer list: ${t.message.toString()}")
            }

        })
    }

    fun editToCart(note: String, indexOfCart: Int, foodExtraList: ArrayList<ManualAddAdvMenuFragment.AddAdvMenuTemp>) {
        var foodExtraRadio: MutableList<FoodListParentRadio> = ArrayList()
        var foodExtraCheck: MutableList<FoodListParentCheck> = ArrayList()
        foodExtraList.forEach {
            if (it.template_type == "RADIO") {
                it.ext_menus.forEach { extMenuRad ->
                    foodExtraRadio.add(FoodListParentRadio(menuChoiceName = it?.template_name!!, foodListChildRadio = FoodListRadio(name = extMenuRad?.ext_menu_name!!, price = extMenuRad.ext_menu_price!!)))
                }
            } else {
                val foodListCheck: MutableList<FoodListCheck> = ArrayList()
                it.ext_menus.forEach { extMenuCheck ->
                    foodListCheck.add(FoodListCheck(name = extMenuCheck?.ext_menu_name!!, price = extMenuCheck.ext_menu_price!!))
                }
                foodExtraCheck.add(FoodListParentCheck(menuChoiceName = it.template_name!!, foodListChildCheck = foodListCheck))
            }
        }

        mutableSelectedMenuTemp.value?.get(indexOfCart)?.foodNote = note
        mutableSelectedMenuTemp.value?.get(indexOfCart)?.foodAmount = quantity.value!!
        mutableSelectedMenuTemp.value?.get(indexOfCart)?.foodTotalPrice = totalPrice.value.toString()
        mutableSelectedMenuTemp.value?.get(indexOfCart)?.foodListCheckbox = foodExtraCheck
        mutableSelectedMenuTemp.value?.get(indexOfCart)?.foodListRadio = foodExtraRadio
        setTotalPrice()
        addTotalQty()
    }

    fun getManualTxnList(status: String, baseContext: Context, recyclerview_transaction: RecyclerView, activity: Activity){
        val mid = sessionManager.getUserData()!!.mid!!
        val status = status
        Log.e("mid", mid)

        PikappApiService().api.getManualTransactionList(
            size = 1000,
            page = 0,
            mid,
            status
        ).enqueue(object : Callback<GetManualTransactionResp>{
            override fun onResponse(
                call: Call<GetManualTransactionResp>,
                response: Response<GetManualTransactionResp>
            ) {
                val response1 = response.body()
                val result = response1?.results
                Log.e("response", response.code().toString())
                Log.e("result", result.toString())
                val productList = ArrayList<ArrayList<ManualProductListResponse>>()

                if(result != null){
                    for (r in result){
                        r.productList.let { productList.add(it as ArrayList<ManualProductListResponse>) }
                        manualTxnAdapter = ManualTxnListAdapter(
                            baseContext,
                            result as MutableList<ManualTransactionResult>,
                            productList as MutableList<List<ManualProductListResponse>>,
                            mid,
                            recyclerview_transaction,activity
                        )
                        manualTxnAdapter.notifyDataSetChanged()
                        recyclerview_transaction.adapter = manualTxnAdapter
                    }
                } else {
                    Timber.tag(tag).d("Result is null")
                }
            }

            override fun onFailure(call: Call<GetManualTransactionResp>, t: Throwable) {
                Timber.tag(tag).d("Failed to show transaction : ${t.message.toString()}")
            }

        })
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

    fun postOrder(paymentStatus: Boolean, nav: NavController, activity: Activity): Int{
        mutablePayStat.value = paymentStatus
        var hargaEkspedisi: String = ""
        var orderType: String = ""
        var payStatus: String = ""
        var status = 0
        var ekspedisi = ""
        var menuList: ArrayList<MenuList> = ArrayList()
        var mid: String? = sessionManager.getUserData()?.mid

        if(paymentStatus){
            payStatus = "PAID"
        }else{
            payStatus = "UNPAID"
        }

        if(mutableHargaEkspedisi.value == " "){
            hargaEkspedisi = "0"
        }else{
            hargaEkspedisi = mutableHargaEkspedisi.value.toString()
        }

        for(q in mutableSelectedMenuTemp.value!!){
            var extraList: ArrayList<ExtraList> = ArrayList()
            for (q in q.foodListRadio){
                if (q != null) {
                    var price: Int = q.foodListChildRadio!!.price.substringBefore(".").toInt()
                    extraList.add(ExtraList(q.foodListChildRadio!!.name, price))
                }
            }
            for (q in q.foodListCheckbox){
                if (q != null) {
                    for(m in q.foodListChildCheck){
                        if (m != null) {
                            var price: Int = m.price.substringBefore(".").toInt()
                            extraList.add(ExtraList(m.name, price))
                        }
                    }
                }
            }
            menuList.add(MenuList(q.product_id.toString(), q.foodName, "", q.foodPrice.toInt(), q.foodNote.toString(), q.foodAmount, 0, "0", extraList))
        }

        if(mutableNamaEkspedisi.value == "Pickup Sendiri"){
            orderType = "PICKUP"
        }else{
            orderType = "DELIVERY"
        }


        if(mutableAsal.value == "Telepon"){
            ekspedisi ="PHONE_CALL"
        }else{
            ekspedisi = mutableAsal.value.toString().uppercase(Locale.getDefault())
        }

        var tanggalKirim: String = mutableDate.value.toString() + " " + mutableHour.value.toString()
        var shippingData: ShippingData = ShippingData(mutableNamaEkspedisi.value.toString() ,hargaEkspedisi.toInt(), mutablePostWaktu.value.toString())
        PikappApiService().api.uploadManualTxn(ManualTxnRequest(menuList, shippingData, mutableCustId.value.toString(), mid.toString(), orderType,
            ekspedisi, mutableCartPrice.value!!.toInt(), payStatus,
            mutableBayar.value!!.toString().uppercase(Locale.getDefault()), "OPEN", 0, mutableCartPrice.value!!.toInt() + hargaEkspedisi.toInt())).
        enqueue(object : Callback<ManualTxnResponse>{
            override fun onResponse(
                call: Call<ManualTxnResponse>,
                response: Response<ManualTxnResponse>
            ) {
                status = response.code()
                if(response.code() == 200){
                   nav.navigate(R.id.action_checkoutFragment_to_invoiceFragment)
                }else{
                    Toast.makeText(activity,"Transaksi Gagal Dilakukan", Toast.LENGTH_SHORT).show()
                }
                Log.e("WAKTU", mutablePostWaktu.value.toString())
                Log.e("WAKTU", mutableCustId.value.toString())
                Log.e("Fail", response.code().toString())
                Log.e("success", response.body().toString())
            }

            override fun onFailure(call: Call<ManualTxnResponse>, t: Throwable) {
               Log.e("Fail", t.toString())
            }

        })
        return status
    }
}