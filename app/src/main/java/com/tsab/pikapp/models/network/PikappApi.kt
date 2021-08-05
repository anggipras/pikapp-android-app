package com.tsab.pikapp.models.network

import com.tsab.pikapp.models.model.*
import com.tsab.pikapp.models.model.MerchantListResponse
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface PikappApi {
    // AUTH
    @POST("merchant/v1/merchant-login/")
    fun loginMerchant(
        @Header("x-request-id") uuid: String,
        @Header("x-request-timestamp") time: String,
        @Header("x-client-id") clientID: String,
        @Body loginRequest: LoginRequestV2
    ): Single<LoginResponseV2>

    @Multipart
    @POST("merchant/v1/merchant-registration/")
    fun uploadRegister(
            @Header("x-request-id") uuid: String,
            @Header("x-client-id") clientID: String,
            @Header("x-request-timestamp") time: String,
            @Part file_01: MultipartBody.Part,
            @Part file_02: MultipartBody.Part,
            @Part file_03: MultipartBody.Part,
            @Part ("address") address: RequestBody,
            @Part ("category") category: RequestBody,
            @Part ("bank_name") bank_name: RequestBody,
            @Part ("merchant_name") merchant_name: RequestBody,
            @Part ("bank_account_no") bank_account_no: RequestBody,
            @Part ("bank_account_name") bank_account_name: RequestBody,
            @Part ("email") email: RequestBody,
            @Part ("phone_number") phone_number: RequestBody,
            @Part ("restaurant_name") restaurant_name: RequestBody,
            @Part ("fcm_token") fcm_token: RequestBody,
            @Part ("pin") pin: RequestBody,
            @Part ("bank_branch") bank_branch: RequestBody,
            @Part ("food_court_name") food_court_name: RequestBody
    ): Call<BaseResponse>

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

    @GET("merchant/exit/")
    fun logoutMerchant(
            @Header("x-request-id") uuid: String,
            @Header("x-client-id") clientID: String,
            @Header("x-request-timestamp") time: String,
            @Header("x-session-id") sessionID: String
    ): Single<LogoutResponseV2>

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

    @POST("merchant/v1/menu/category/")
    fun menuCategory(
        @Header("x-request-id") uuid: String,
        @Header("x-request-timestamp") time: String,
        @Header("x-client-id") clientID: String,
        @Header("x-signature") signature: String,
        @Header("token") token: String,
        @Header("mid") mid: String,
        @Body MenuCategoryRequest: MenuCategoryRequest
    ): Call<BaseResponse>

    @GET("merchant/v1/menu/{mid}/category/list/")
    fun getMenuCategoryList(
        @Header("x-request-id") uuid: String,
        @Header("x-request-timestamp") time: String,
        @Header("x-client-id") clientID: String,
        @Header("x-signature") signature: String,
        @Header("token") token: String,
        @Path("mid") mid: String
    ): Call<MerchantListCategoryResponse>

    @POST("merchant/v1/menu/category/update/")
    fun updateMenuCategory(
        @Header("x-request-id") uuid: String,
        @Header("x-request-timestamp") time: String,
        @Header("x-client-id") clientID: String,
        @Header("x-signature") signature: String,
        @Header("token") token: String,
        @Header("mid") mid: String,
        @Body UpdateMenuCategoryRequest: UpdateMenuCategoryRequest
    ): Call<BaseResponse>

    @GET("merchant/v1/menu/{id}/category/delete/")
    fun deleteMenuCategory(
        @Header("x-request-id") uuid: String,
        @Header("x-request-timestamp") time: String,
        @Header("x-client-id") clientID: String,
        @Header("x-signature") signature: String,
        @Header("token") token: String,
        @Header("mid") mid: String,
        @Path("id") id: String
    ): Call<BaseResponse>

    @POST("merchant/v1/menu/category/bulk/update/")
    fun sortMenuCategory(
            @Header("x-request-id") uuid: String,
            @Header("x-request-timestamp") time: String,
            @Header("x-client-id") clientID: String,
            @Header("x-signature") signature: String,
            @Header("token") token: String,
            @Header("mid") mid: String,
            @Body SortCategoryRequest: SortCategoryRequest
    ): Call<BaseResponse>



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

    @GET("/home/v2/detail/merchant/{longitude}/{latitude}/")
    fun getMerchantProfile(
            @Header("x-request-id") uuid: String,
            @Header("x-request-timestamp") time: String,
            @Header("x-client-id") clientID: String,
            @Header("token") token: String,
            @Header("mid") mid: String,
            @Path("longitude") longitude: String,
            @Path("latitude") latitude: String
    ): Single<MerchantProfileResponse>
}