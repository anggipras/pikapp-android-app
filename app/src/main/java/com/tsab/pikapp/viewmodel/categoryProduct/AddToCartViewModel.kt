package com.tsab.pikapp.viewmodel.categoryProduct

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.tsab.pikapp.models.model.AddToCartResponse
import com.tsab.pikapp.models.model.CartModel
import com.tsab.pikapp.models.network.PikappApiService
import com.tsab.pikapp.util.CartUtil
import com.tsab.pikapp.util.SessionManager
import com.tsab.pikapp.viewmodel.BaseViewModel
import io.reactivex.disposables.CompositeDisposable

class AddToCartViewModel(application: Application) : BaseViewModel(application) {

    private var sessionManager = SessionManager(getApplication())
    private var cartUtil = CartUtil(getApplication())
    private val pikappService = PikappApiService()
    private val disposable = CompositeDisposable()

    val quantity = MutableLiveData<Int>()
    val note = MutableLiveData<String>()
    val errorResponse = MutableLiveData<AddToCartResponse>()
    val addedToCart = MutableLiveData<Boolean>()
    val differentCart = MutableLiveData<List<CartModel>>()
    val loading = MutableLiveData<Boolean>()
    var productID = ""

    private val carts = arrayListOf<CartModel>()
    fun decreaseQty(qty: Int) {
        var q = qty - 1
        if (qty <= 1) {
            q = 1
        }
        quantity.value = q
    }

    fun increaseQty(qty: Int) {
        val q = qty + 1
        quantity.value = q
    }

    fun getCart(pid: String) {
        productID = pid
        val tCart = cartUtil.getCart()
        tCart?.let {
            carts.addAll(tCart)
        }

        for (cart in carts) {
            if (productID == cart.productID) {
                quantity.value = cart.qty
                note.value = cart.notes
                break
            }
        }
    }

    fun validateCart(
        mid: String,
        pid: String,
        pName: String,
        pImage: String,
        qty: Int,
        pPrice: String,
        notes: String
    ) {
        loading.value = true
        val price = pPrice.toInt()
        val newCart = CartModel(mid, pid, pName, pImage, qty, notes, price)
        val size = carts.size - 1
        if (carts.isEmpty()) {
            addToCart(newCart)
        } else if (carts[0].merchantID == newCart.merchantID) addToCart(newCart) else {
            Log.d("Debug", "kesini anjing")
            differentCart.value = arrayListOf(newCart)
        }
    }

    fun addToCart(newCart: CartModel) {
        val size = carts.size - 1
        var isAdded = false
        for (i in 0..size) {
            if (carts[i].productID == newCart.productID) {
                carts.set(i, newCart)
                isAdded = true
                break
            }
        }
        if (!isAdded) {
            carts.add(newCart)
            isAdded = true
        }
        if (isAdded) cartSuccess() else addToCartFail(
            AddToCartResponse(
                "EC0001",
                "Gagal masuk ke keranjang"
            )
        )
    }

    private fun addToCartFail(err: AddToCartResponse) {
        loading.value = false
        errorResponse.value = err
    }

    private fun cartSuccess() {
        cartUtil.setCartStatus(true)
        cartUtil.setCart(carts)

        loading.value = false
        addedToCart.value = true
        errorResponse.value = AddToCartResponse("", "")
    }

    fun resetCart(newCart: CartModel) {
        carts.clear()
        carts.add(newCart)
        cartUtil.setCart(carts)
        cartUtil.setCartIsNew(true)
        cartUtil.setCartType("")
        addedToCart.value = true
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}