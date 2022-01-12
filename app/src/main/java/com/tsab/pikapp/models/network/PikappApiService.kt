package com.tsab.pikapp.models.network

import android.util.Log
import com.tsab.pikapp.BuildConfig
import com.tsab.pikapp.models.model.*
import com.tsab.pikapp.util.*
import io.reactivex.Single
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class PikappApiService {
    val api = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create(PikappApi::class.java)

    private fun checkBuildConfig(): String {
        return if (BuildConfig.BASE_URL == "https://dev-api.pikapp.id/") {
            "https://dev-report-api.pikapp.id/"
        } else {
            "https://report-api.pikapp.id/"
        }
    }

    fun webReport(): String {
        return if (BuildConfig.BASE_URL == "https://dev-api.pikapp.id/") {
            "https://dev-report.pikapp.id/"
        } else {
            "https://report.pikapp.id/"
        }
    }

    fun menuWeb(): String {
        return if (BuildConfig.BASE_URL == "https://dev-api.pikapp.id/") {
            "https://web-dev.pikapp.id/"
        } else {
            "https://order.pikapp.id/"
        }
    }

    val reportApi = Retrofit.Builder()
        .baseUrl(checkBuildConfig())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(PikappApi::class.java)

    val shipmentApi = Retrofit.Builder()
        .baseUrl(checkBuildConfigShipment())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(PikappApi::class.java)

    private fun checkBuildConfigShipment(): String {
        return if (BuildConfig.BASE_URL == "https://dev-api.pikapp.id/") {
            "http://dev-api.pikapp.id:9005/"
        } else {
            "http://dev-api.pikapp.id:9005/"
        }
    }

    // Merchant LOGIN, LOGOUT, REGISTER
    // AUTH
    fun loginUser(email: String, password: String, fcmToken: String): Single<LoginResponse> {
        val uuid = getUUID()
        val loginData = LoginRequest(email, password, fcmToken)
        return api.loginUser(uuid, getTimestamp(), getClientID(), loginData)
    }

    fun loginMerchant(username: String, pin: String, token: String): Single<LoginResponseV2> {
        val uuid = getUUID()
        val loginData = LoginRequestV2(username, pin, token)
        return api.loginMerchant(uuid, getTimestamp(), getClientID(), loginData)
    }

    fun logoutUser(sessionID: String): Single<LogoutResponse> {
        return api.logoutUser(getUUID(), getTimestamp(), getClientID(), sessionID)
    }

    fun logoutMerchant(sessionID: String): Single<LogoutResponseV2> {
        return api.logoutMerchant(getUUID(), getClientID(), getTimestamp(), sessionID)
    }

    fun registerUser(
        email: String,
        password: String,
        fullName: String,
        phoneNumber: String,
        birthday: String,
        gender: String
    ): Single<RegisterResponse> {
        val registerData = RegisterRequest(
            email = email,
            password = password,
            fullName = fullName,
            phoneNumber = phoneNumber,
            birthday = birthday,
            gender = gender
        )
        return api.registerUser(getUUID(), getTimestamp(), getClientID(), registerData)
    }

    // HOME
    fun getHomeBannerSlider(): Single<ItemHomeBannerSliderResponse> {
        return api.getHomeBannerSlider(getUUID(), getTimestamp(), getClientID(), setTokenPublic())
    }

    fun getHomeCategory(): Single<ItemHomeCategoryResponse> {
        return api.getHomeCategory(getUUID(), getTimestamp(), getClientID(), setTokenPublic())
    }

    fun sendRecommendation(
        email: String,
        token: String,
        userExclusiveRequest: UserExclusiveRecommendationRequest
    ): Single<UserExclusiveRecommendationResponse> {
        val timestamp = getTimestamp()
        val signature = getSignature(email, timestamp)
        return api.postUserExclusive(
            getUUID(),
            timestamp,
            getClientID(),
            signature,
            token,
            userExclusiveRequest
        )
    }

    fun getMerchant(
        category: String,
        latitude: String,
        longitude: String
    ): Single<MerchantListResponse> {
        return api.getMerchant(
            getUUID(),
            getTimestamp(),
            getClientID(),
            setTokenPublic(),
            category,
            longitude,
            latitude,
            "ALL"
        )
    }

    fun getMerchantDetail(
        mid: String,
        latitude: String,
        longitude: String
    ): Single<MerchantDetailResponse> {
        return api.getMerchantDetail(
            getUUID(),
            getTimestamp(),
            getClientID(),
            setTokenPublic(),
            mid,
            longitude,
            latitude
        )
    }

    fun getProductList(mid: String): Single<ProductListResponse> {
        return api.getProductList(getUUID(), getTimestamp(), getClientID(), setTokenPublic(), mid)
    }

    fun getProductDetail(
        pid: String,
        latitude: String,
        longitude: String
    ): Single<ProductDetailResponse> {
        return api.getProductDetail(
            getUUID(),
            getTimestamp(),
            getClientID(),
            setTokenPublic(),
            pid,
            longitude,
            latitude
        )
    }

    // MENU CATEGORY
    fun listMenuCategory(
        email: String,
        token: String,
        merchantId: String
    ): Single<ListMenuCategoryResponse> {
        val timestamp = getTimestamp()

        return api.listMenuCategory(
            requestId = getUUID(),
            requestTimestamp = timestamp,
            clientId = getClientID(),
            signature = getSignature(email, timestamp),
            token = token,
            merchantId = merchantId
        )
    }

    // MERCHANT
    fun getStoreProductList(
        email: String,
        token: String,
        mid: String,
        available: Boolean
    ): Single<StoreProductListResponse> {
        val timestamp = getTimestamp()
        val signature = getSignature(email, timestamp)
        val status = if (available) "ON" else "OFF"
        Log.d("Debug", "timestamp getStoreProductList : $timestamp")
        Log.d("Debug", "signature getStoreProductList : $signature")
        return api.getStoreProductList(
            getUUID(),
            timestamp,
            getClientID(),
            signature,
            token,
            mid,
            status
        )
    }

    fun getStoreProductDetail(
        email: String,
        token: String,
        pid: String
    ): Single<StoreProductDetailResponse> {
        val timestamp = getTimestamp()
        val signature = getSignature(email, timestamp)
        Log.d("Debug", "timestamp getStoreProductDetail : $timestamp")
        Log.d("Debug", "signature getStoreProductDetail : $signature")
        return api.getStoreProductDetail(getUUID(), timestamp, getClientID(), signature, token, pid)
    }

    fun postStoreAddProduct(
        email: String,
        token: String,
        mid: String,
        file01: MultipartBody.Part,
        file02: MultipartBody.Part,
        file03: MultipartBody.Part,
        productName: RequestBody,
        price: RequestBody,
        condition: RequestBody,
        status: RequestBody,
        productQty: RequestBody,
        productDesc: RequestBody
    ): Single<StoreProductActionResponse> {
        val timestamp = getTimestamp()
        val signature = getSignature(email, timestamp)

        val action: RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), "ADD")
        return api.postStoreProduct(
            getUUID(), timestamp, getClientID(), signature, token, mid, file01, file02, file03,
            productName, price, condition, status, productQty, productDesc, action
        )
    }

    fun postStoreEditProductWithImage(
        email: String,
        token: String,
        mid: String,
        pid: String,
        file01: MultipartBody.Part?,
        file02: MultipartBody.Part?,
        file03: MultipartBody.Part?,
        productName: RequestBody,
        price: RequestBody,
        condition: RequestBody,
        productQty: RequestBody,
        productDesc: RequestBody
    ): Single<StoreProductActionResponse> {
        val timestamp = getTimestamp()
        val signature = getSignature(email, timestamp)

        val action: RequestBody =
            RequestBody.create(MediaType.parse("multipart/form-data"), "MODIFY")
        return api.postStoreEditProduct(
            getUUID(), timestamp, getClientID(), signature, token, mid, pid, file01, file02, file03,
            productName, price, condition, productQty, productDesc, action
        )
    }

    fun postStoreEditProductWithoutImage(
        email: String,
        token: String,
        mid: String,
        pid: String,
        productName: RequestBody,
        price: RequestBody,
        condition: RequestBody,
        productQty: RequestBody,
        productDesc: RequestBody
    ): Single<StoreProductActionResponse> {
        val timestamp = getTimestamp()
        val signature = getSignature(email, timestamp)

        val action: RequestBody =
            RequestBody.create(MediaType.parse("multipart/form-data"), "MODIFY")
        return api.postStoreProductWithoutImage(
            getUUID(), timestamp, getClientID(), signature, token, mid, pid,
            productName, price, condition, productQty, productDesc, action
        )
    }

    fun postStoreOnOffProduct(
        email: String,
        token: String,
        mid: String,
        pid: String,
        stts: Boolean
    ): Single<StoreProductActionResponse> {
        val timestamp = getTimestamp()
        val signature = getSignature(email, timestamp)
        val status = if (stts) "ON" else "OFF"
        val action: RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), status)
        return api.postStoreProductAction(
            getUUID(),
            timestamp,
            getClientID(),
            signature,
            token,
            mid,
            pid,
            action
        )
    }

    fun postStoreDeleteProduct(
        email: String,
        token: String,
        mid: String,
        pid: String
    ): Single<StoreProductActionResponse> {
        val timestamp = getTimestamp()
        val signature = getSignature(email, timestamp)
        val action: RequestBody =
            RequestBody.create(MediaType.parse("multipart/form-data"), "DELETE")
        return api.postStoreProductAction(
            getUUID(),
            timestamp,
            getClientID(),
            signature,
            token,
            mid,
            pid,
            action
        )
    }

    // TRANSACTIONS
    fun postAddToCart(
        email: String,
        token: String,
        addToCartModel: AddToCartModel
    ): Single<AddToCartResponse> {
        val timestamp = getTimestamp()
        val signature = getSignature(email, timestamp)
        return api.addToCart(getUUID(), timestamp, getClientID(), signature, token, addToCartModel)
    }

    fun getCartList(email: String, token: String): Single<CartListResponse> {
        val timestamp = getTimestamp()
        val signature = getSignature(email, timestamp)
        return api.getCartList(getUUID(), timestamp, getClientID(), signature, token)
    }

    fun postTransaction(
        email: String,
        token: String,
        transactionModel: TransactionModel
    ): Single<TransactionResponse> {
        val timestamp = getTimestamp()
        val signature = getSignature(email, timestamp)
        return api.postTransaction(
            getUUID(),
            timestamp,
            getClientID(),
            signature,
            token,
            transactionModel
        )
    }

    fun getTransactionList(email: String, token: String): Single<GetOrderListResponse> {
        val timestamp = getTimestamp()
        val signature = getSignature(email, timestamp)
        Log.d("Debug", "timestamp txnList : $timestamp")
        Log.d("Debug", "signature txnList : $signature")
        return api.getTransactionList(getUUID(), timestamp, getClientID(), signature, token)
    }

    fun getTransactionDetail(
        email: String,
        token: String,
        transactionID: String
    ): Single<GetOrderDetailResponse> {
        val timestamp = getTimestamp()
        val signature = getSignature(email, timestamp)
        Log.d("Debug", "timestamp txnDetail : $timestamp")
        Log.d("Debug", "signature txnDetail : $signature")
        return api.getTransactionDetail(
            getUUID(),
            timestamp,
            getClientID(),
            signature,
            token,
            transactionID
        )
    }

    fun getTransactionListMerchant(
        email: String,
        token: String,
        mid: String
    ): Single<GetStoreOrderListResponse> {
        val timestamp = getTimestamp()
        val signature = getSignature(email, timestamp)
        return api.getTransactionListMerchant(
            getUUID(),
            timestamp,
            getClientID(),
            signature,
            token,
            mid
        )
    }

    /*fun getTransactionListV2Merchant(
        email: String,
        token: String,
        mid: String,
        transactionReq: TransactionListRequest
    ): Single<GetStoreOrderListV2Response> {
        val timestamp = getTimestamp()
        val signature = getSignature(email, timestamp)
        return api.getTransactionListV2Merchant(
            getUUID(),
            timestamp,
            getClientID(),
            signature,
            token,
            mid,
            transactionReq
        )
    }*/

    fun getTransactionDetailMerchant(
        email: String,
        token: String,
        transactionID: String,
        tableNo: String
    ): Single<GetStoreOrderDetailResponse> {
        val timestamp = getTimestamp()
        val signature = getSignature(email, timestamp)
        Log.d("Debug", "timestamp txnDetail merchant : $timestamp")
        Log.d("Debug", "signature txnDetail merchant : $signature")
        return api.getTransactionDetailMerchant(
            getUUID(),
            timestamp,
            getClientID(),
            signature,
            token,
            transactionID,
            tableNo
        )
    }

    fun postUpdateOrderStatus(
        email: String,
        token: String,
        transactionID: RequestBody,
        status: RequestBody
    ): Single<UpdateStatusResponse> {
        val timestamp = getTimestamp()
        val signature = getSignature(email, timestamp)
        return api.postUpdateTransactionStatus(
            getUUID(),
            timestamp,
            getClientID(),
            signature,
            token,
            transactionID,
            status
        )
    }

    // Advanced menu
    fun addAdvanceMenu(
        email: String,
        token: String,
        merchantId: String,
        advanceMenuList: List<AdvanceMenu>
    ): Single<AddAdvanceMenuResponse> {
        val timestamp = getTimestamp()
        val signature = getSignature(email, timestamp)

        return api.addAdvanceMenu(
            requestId = getUUID(),
            requestTimestamp = timestamp,
            clientId = getClientID(),
            signature = signature,
            token = token,
            merchantId = merchantId,
            addAdvancedMenuRequest = AddAdvanceMenuRequest(advanceMenuList)
        )
    }

    fun listAdvanceMenu(
        email: String,
        token: String,
        pid: String,
        timeStamp: String
    ): Single<ListAdvanceMenuResponse> = api.listAdvanceMenu(
        requestId = getUUID(),
        requestTimestamp = timeStamp,
        clientId = getClientID(),
        signature = getSignature(email, timeStamp),
        token = token,
        productId = pid
    )

    fun listAdvanceMenuEdit(
            email: String,
            token: String,
            pid: String,
            timeStamp: String
    ): Single<ListAdvanceMenuEditResponse> = api.listAdvanceMenuEdit(
            requestId = getUUID(),
            requestTimestamp = timeStamp,
            clientId = getClientID(),
            signature = getSignature(email, timeStamp),
            token = token,
            productId = pid
    )

    fun changePinOfMerchant(
        email: String,
        token: String,
        oldPin: String?,
        mid: String?,
        newPin: String?
    ): Single<OtherBaseResponse> {
        val timeStamp = getTimestamp()
        val signature = getSignature(email, timeStamp)
        val pinModel = pinMerchant(oldPin, mid, newPin)

        return api.changePinMerchantDisposable(
            uuid = getUUID(),
            time = timeStamp,
            clientID = getClientID(),
            signature = signature,
            token = token,
            pinModel = pinModel
        )
    }

    // Omnichannel Integration
    fun listIntegration(
        merchantId: String
    ): Single<IntegrationArrayResponse> = api.listIntegration(
        requestId = getUUID(),
        requestTimestamp = getTimestamp(),
        clientId = getClientID(),
        merchantId = merchantId
    )

    fun connectIntegration(
        merchantId: String,
        email: String,
        phoneNumber: String,
        shopName: String,
        shopDomain: String,
        channelType: OmnichannelType,
        shopCategory: ShopCategory
    ): Single<IntegrationObjectResponse> = api.connectIntegration(
        requestId = getUUID(),
        requestTimestamp = getTimestamp(),
        clientId = getClientID(),
        connectIntegrationRequest = ConnectIntegrationRequest(
            merchantId = merchantId,
            email = email,
            phoneNumber = phoneNumber,
            shopName = shopName,
            shopDomain = shopDomain,
            channelType = channelType,
            shopCategory = shopCategory
        )
    )

    fun disconnectIntegration(
        channelId: String
    ): Single<IntegrationObjectResponse> = api.disconnectIntegration(
        requestId = getUUID(),
        requestTimestamp = getTimestamp(),
        clientId = getClientID(),
        channelId = channelId
    )
}