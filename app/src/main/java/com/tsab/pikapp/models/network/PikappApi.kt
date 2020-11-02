package com.tsab.pikapp.models.network

import com.tsab.pikapp.models.model.*
import com.tsab.pikapp.models.model.MerchantListResponse
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface PikappApi {

    // AUTH

    @POST("auth/login")
    fun loginUser(
        @Header("x-request-id") uuid: String,
        @Header("x-request-timestamp") time: String,
        @Header("x-client-id") clientID: String,
        @Body loginRequest: LoginRequest
    ): Single<LoginResponse>

    @POST("auth/register")
    fun registerUser(
        @Header("x-request-id") uuid: String,
        @Header("x-request-timestamp") time: String,
        @Header("x-client-id") clientID: String,
        @Body registerRequest: RegisterRequest
    ): Single<RegisterResponse>

    @GET("auth/exit")
    fun logoutUser(
        @Header("x-request-id") uuid: String,
        @Header("x-request-timestamp") time: String,
        @Header("x-client-id") clientID: String,
        @Header("x-session-id") sessionID: String
    ): Single<LogoutResponse>

    // HOME
    @POST("home/v1/exclusive-member")
    fun postUserExclusive(
        @Header("x-request-id") uuid: String,
        @Header("x-request-timestamp") time: String,
        @Header("x-client-id") clientID: String,
        @Header("x-signature") signature: String,
        @Header("token") token: String,
        @Body userExclusiveRequest: UserExclusiveRecommendationRequest
    ): Single<UserExclusiveRecommendationResponse>

    @GET("home/v1/slider")
    fun getHomeBannerSlider(
        @Header("x-request-id") uuid: String,
        @Header("x-request-timestamp") time: String,
        @Header("x-client-id") clientID: String,
        @Header("token") token: String
    ): Single<ItemHomeBannerSliderResponse>

    @GET("home/v1/category")
    fun getHomeCategory(
        @Header("x-request-id") uuid: String,
        @Header("x-request-timestamp") time: String,
        @Header("x-client-id") clientID: String,
        @Header("token") token: String
    ): Single<ItemHomeCategoryResponse>

    @GET("home/v1/merchant/{longitude}/{latitude}/{merchant_name}/")
    fun getMerchant(
        @Header("x-request-id") uuid: String,
        @Header("x-request-timestamp") time: String,
        @Header("x-client-id") clientID: String,
        @Header("token") token: String,
        @Header("category") category: String,
        @Path("longitude") longitude: String,
        @Path("latitude") latitude: String,
        @Path("merchant_name") merchantName: String
    ): Single<MerchantListResponse>

    @GET("home/v1/detail/merchant/{longitude}/{latitude}/")
    fun getMerchantDetail(
        @Header("x-request-id") uuid: String,
        @Header("x-request-timestamp") time: String,
        @Header("x-client-id") clientID: String,
        @Header("token") token: String,
        @Header("mid") mid: String,
        @Path("longitude") longitude: String,
        @Path("latitude") latitude: String
    ): Single<MerchantDetailResponse>

    @GET("home/v1/list/product/")
    fun getProductList(
        @Header("x-request-id") uuid: String,
        @Header("x-request-timestamp") time: String,
        @Header("x-client-id") clientID: String,
        @Header("token") token: String,
        @Header("mid") mid: String
    ): Single<ProductListResponse>

    @GET("home/v1/detail/product/{longitude}/{latitude}/")
    fun getProductDetail(
        @Header("x-request-id") uuid: String,
        @Header("x-request-timestamp") time: String,
        @Header("x-client-id") clientID: String,
        @Header("token") token: String,
        @Header("pid") pid: String,
        @Path("longitude") longitude: String,
        @Path("latitude") latitude: String
    ): Single<ProductDetailResponse>


    // STORE

    @GET("merchant/v1/product-list/")
    fun getStoreProductList(
        @Header("x-request-id") uuid: String,
        @Header("x-request-timestamp") time: String,
        @Header("x-client-id") clientID: String,
        @Header("x-signature") signature: String,
        @Header("token") token: String,
        @Header("mid") mid: String,
        @Header("status") status: String
    ): Single<StoreProductListResponse>

    @GET("merchant/v1/product-detail/")
    fun getStoreProductDetail(
        @Header("x-request-id") uuid: String,
        @Header("x-request-timestamp") time: String,
        @Header("x-client-id") clientID: String,
        @Header("x-signature") signature: String,
        @Header("token") token: String,
        @Header("pid") mid: String
    ): Single<StoreProductDetailResponse>

    @Multipart
    @POST("merchant/v1/product-action/")
    fun postStoreProduct(
        @Header("x-request-id") uuid: String,
        @Header("x-request-timestamp") time: String,
        @Header("x-client-id") clientID: String,
        @Header("x-signature") signature: String,
        @Header("token") token: String,
        @Header("mid") mid: String,
        @Part file01: MultipartBody.Part?,
        @Part file02: MultipartBody.Part?,
        @Part file03: MultipartBody.Part?,
        @Part("product_name") productName: RequestBody,
        @Part("price") price: RequestBody,
        @Part("condition") condition: RequestBody,
        @Part("status") status: RequestBody,
        @Part("product_qty") productQty: RequestBody,
        @Part("product_desc") productDesc: RequestBody,
        @Part("action") action: RequestBody
    ): Single<StoreProductActionResponse>

    @Multipart
    @POST("merchant/v1/product-action/")
    fun postStoreEditProduct(
        @Header("x-request-id") uuid: String,
        @Header("x-request-timestamp") time: String,
        @Header("x-client-id") clientID: String,
        @Header("x-signature") signature: String,
        @Header("token") token: String,
        @Header("mid") mid: String,
        @Header("pid") pid: String,
        @Part file01: MultipartBody.Part?,
        @Part file02: MultipartBody.Part?,
        @Part file03: MultipartBody.Part?,
        @Part("product_name") productName: RequestBody,
        @Part("price") price: RequestBody,
        @Part("condition") condition: RequestBody,
        @Part("status") status: RequestBody,
        @Part("product_qty") productQty: RequestBody,
        @Part("product_desc") productDesc: RequestBody,
        @Part("action") action: RequestBody
    ): Single<StoreProductActionResponse>

    @Multipart
    @POST("merchant/v1/product-action/")
    fun postStoreProductWithoutImage(
        @Header("x-request-id") uuid: String,
        @Header("x-request-timestamp") time: String,
        @Header("x-client-id") clientID: String,
        @Header("x-signature") signature: String,
        @Header("token") token: String,
        @Header("mid") mid: String,
        @Header("pid") pid: String,
        @Part("product_name") productName: RequestBody,
        @Part("price") price: RequestBody,
        @Part("condition") condition: RequestBody,
        @Part("status") status: RequestBody,
        @Part("product_qty") productQty: RequestBody,
        @Part("product_desc") productDesc: RequestBody,
        @Part("action") action: RequestBody
    ): Single<StoreProductActionResponse>

    @Multipart
    @POST("merchant/v1/product-action/")
    fun postStoreProductAction(
        @Header("x-request-id") uuid: String,
        @Header("x-request-timestamp") time: String,
        @Header("x-client-id") clientID: String,
        @Header("x-signature") signature: String,
        @Header("token") token: String,
        @Header("mid") mid: String,
        @Header("pid") pid: String,
        @Part("action") action: RequestBody
    ): Single<StoreProductActionResponse>


    // TRANSACTIONS

    @POST("txn/v1/cart-post/")
    fun addToCart(
        @Header("x-request-id") uuid: String,
        @Header("x-request-timestamp") time: String,
        @Header("x-client-id") clientID: String,
        @Header("x-signature") signature: String,
        @Header("token") token: String,
        @Body addToCartModel: AddToCartModel
    ): Single<AddToCartResponse>

    @GET("txn/v1/cart-list/")
    fun getCartList(
        @Header("x-request-id") uuid: String,
        @Header("x-request-timestamp") time: String,
        @Header("x-client-id") clientID: String,
        @Header("x-signature") signature: String,
        @Header("token") token: String
    ): Single<CartListResponse>

    @POST("txn/v1/txn-post/")
    fun postTransaction(
        @Header("x-request-id") uuid: String,
        @Header("x-request-timestamp") time: String,
        @Header("x-client-id") clientID: String,
        @Header("x-signature") signature: String,
        @Header("token") token: String,
        @Body transactionModel: TransactionModel
    ): Single<TransactionResponse>

    @GET("txn/v1/txn-history/")
    fun getTransactionList(
        @Header("x-request-id") uuid: String,
        @Header("x-request-timestamp") time: String,
        @Header("x-client-id") clientID: String,
        @Header("x-signature") signature: String,
        @Header("token") token: String
    ): Single<GetOrderListResponse>

    @GET("txn/v1/{txnNo}/txn-detail/")
    fun getTransactionDetail(
        @Header("x-request-id") uuid: String,
        @Header("x-request-timestamp") time: String,
        @Header("x-client-id") clientID: String,
        @Header("x-signature") signature: String,
        @Header("token") token: String,
        @Path("txnNo") txnNo: String
    ): Single<GetOrderDetailResponse>

    // TRANSACTIONS - Merchant

    @GET("merchant/v1/mch-order-history/")
    fun getTransactionListMerchant(
        @Header("x-request-id") uuid: String,
        @Header("x-request-timestamp") time: String,
        @Header("x-client-id") clientID: String,
        @Header("x-signature") signature: String,
        @Header("token") token: String,
        @Header("mid") merchantID: String
    ): Single<GetStoreOrderListResponse>

    @GET("merchant/v1/order/{txnId}/{tableNo}")
    fun getTransactionDetailMerchant(
        @Header("x-request-id") uuid: String,
        @Header("x-request-timestamp") time: String,
        @Header("x-client-id") clientID: String,
        @Header("x-signature") signature: String,
        @Header("token") token: String,
        @Path("txnId") txnNo: String,
        @Path("tableNo") tableNo: String
    ): Single<GetStoreOrderDetailResponse>

    //update status

    @Multipart
    @POST("txn/v1/txn-update/")
    fun postUpdateTransactionStatus(
        @Header("x-request-id") uuid: String,
        @Header("x-request-timestamp") time: String,
        @Header("x-client-id") clientID: String,
        @Header("x-signature") signature: String,
        @Header("token") token: String,
        @Part("transaction_id") transactionID: RequestBody,
        @Part("status") status: RequestBody
    ): Single<UpdateStatusResponse>
}