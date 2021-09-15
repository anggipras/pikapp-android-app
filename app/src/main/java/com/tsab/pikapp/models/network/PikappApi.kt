package com.tsab.pikapp.models.network

import com.tsab.pikapp.models.model.*
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
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
        @Part("address") address: RequestBody,
        @Part("category") category: RequestBody,
        @Part("bank_name") bank_name: RequestBody,
        @Part("merchant_name") merchant_name: RequestBody,
        @Part("bank_account_no") bank_account_no: RequestBody,
        @Part("bank_account_name") bank_account_name: RequestBody,
        @Part("email") email: RequestBody,
        @Part("phone_number") phone_number: RequestBody,
        @Part("restaurant_name") restaurant_name: RequestBody,
        @Part("fcm_token") fcm_token: RequestBody,
        @Part("pin") pin: RequestBody,
        @Part("bank_branch") bank_branch: RequestBody,
        @Part("food_court_name") food_court_name: RequestBody
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
    fun listMenuCategory(
        @Header("x-request-id") requestId: String,
        @Header("x-request-timestamp") requestTimestamp: String,
        @Header("x-client-id") clientId: String,
        @Header("x-signature") signature: String,
        @Header("token") token: String,
        @Path("mid") merchantId: String
    ): Single<ListMenuCategoryResponse>

    // TODO: Delete old API call
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

    // Update status
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

    @Multipart
    @POST("merchant/v2/product-action/")
    fun uploadMenu(
        @Header("x-request-id") uuid: String,
        @Header("x-request-timestamp") time: String,
        @Header("x-client-id") clientID: String,
        @Header("x-signature") signature: String,
        @Header("token") token: String,
        @Header("mid") mid: String,
        @Part file_01: MultipartBody.Part,
        @Part file_02: MultipartBody.Part,
        @Part file_03: MultipartBody.Part,
        @Part("product_name") product_name: RequestBody,
        @Part("product_desc") product_desc: RequestBody,
        @Part("menu_category_id") id: RequestBody,
        @Part("price") price: RequestBody,
        @Part("condition") condition: RequestBody,
        @Part("action") action: RequestBody,
        @Part("status") status: RequestBody,
        @Part("product_qty") qty: RequestBody,
        @Part("extra_menu") extra: RequestBody
    ): Call<BaseResponse>

    @GET("/merchant/v1/merchant/{mid}/profile/")
    fun getMerchantProfile(
            @Header("x-request-id") uuid: String,
            @Header("x-request-timestamp") time: String,
            @Header("x-client-id") clientID: String,
            @Header("x-signature") signature: String,
            @Header("token") token: String,
            @Path("mid") mid: String
    ): Single<MerchantProfileResponse>

    // Advanced Menu
    @POST("merchant/v1/menu/advance/add/")
    fun addAdvanceMenu(
        @Header("Content-Type") contentType: String? = "application/json",
        @Header("x-request-id") requestId: String,
        @Header("x-request-timestamp") requestTimestamp: String,
        @Header("x-client-id") clientId: String,
        @Header("x-signature") signature: String,
        @Header("token") token: String,
        @Header("mid") merchantId: String,
        @Body addAdvancedMenuRequest: AddAdvanceMenuRequest
    ): Single<AddAdvanceMenuResponse>

    @GET("merchant/v1/menu/advance/{pid}/list/")
    fun listAdvanceMenu(
        @Header("x-request-id") requestId: String,
        @Header("x-request-timestamp") requestTimestamp: String,
        @Header("x-client-id") clientId: String,
        @Header("x-signature") signature: String,
        @Header("token") token: String,
        @Path("pid") productId: String
    ): Single<ListAdvanceMenuResponse>

    @GET("/merchant/v1/shop/management/list/")
    fun getMerchantShopManagement(
            @Header("x-request-id") uuid: String,
            @Header("x-request-timestamp") time: String,
            @Header("x-client-id") clientID: String,
            @Header("x-signature") signature: String,
            @Header("token") token: String,
            @Header("mid") mid: String
    ): Call<MerchantTimeManagement>

    @Multipart
    @POST("merchant/v1/merchant/update/profile/")
    fun uploadMerchantProfile(
            @Header("x-request-id") uuid: String,
            @Header("x-request-timestamp") time: String,
            @Header("x-client-id") clientID: String,
            @Header("x-signature") signature: String,
            @Header("token") token: String,
            @Part file_01: MultipartBody.Part,
            @Part file_02: MultipartBody.Part,
            @Part ("address") address: RequestBody,
            @Part ("merchant_name") merchant_name: RequestBody,
            @Part ("gender") gender: RequestBody,
            @Part ("dob") dob: RequestBody,
            @Part ("bank_account_no") bank_account_no: RequestBody,
            @Part ("bank_account_name") bank_account_name: RequestBody,
            @Part ("bank_name") bank_name: RequestBody,
            @Part ("mid") mid: RequestBody
    ): Call<BaseResponse>

    @POST("merchant/v1/shop/management/update/")
    fun updateShopManagement(
            @Header("x-request-id") uuid: String,
            @Header("x-request-timestamp") time: String,
            @Header("x-client-id") clientID: String,
            @Header("x-signature") signature: String,
            @Header("token") token: String,
            @Header("mid") mid: String,
            @Body shopManagementUpdateRequest: ShopManagementUpdateRequest
    ): Call<BaseResponse>

    @POST("merchant/v1/merchant/change/pin/")
    fun changePinMerchantDisposable(
        @Header("Content-Type") contentType: String? = "application/json",
        @Header("x-request-id") uuid: String,
        @Header("x-request-timestamp") time: String,
        @Header("x-client-id") clientID: String,
        @Header("x-signature") signature: String,
        @Header("token") token: String,
        @Body pinModel: pinMerchant
    ): Single<OtherBaseResponse>

    @POST("merchant/v1/merchant/change/pin/")
    fun changePinMerchant(
        @Header("Content-Type") contentType: String? = "application/json",
        @Header("x-request-id") uuid: String,
        @Header("x-request-timestamp") time: String,
        @Header("x-client-id") clientID: String,
        @Header("x-signature") signature: String,
        @Header("token") token: String,
        @Body pinModel: pinMerchant
    ): Call<OtherBaseResponse>

    @POST("merchant/v2/product-list/")
    fun searchMenu(
        @Header("x-request-id") uuid: String,
        @Header("x-request-timestamp") time: String,
        @Header("x-client-id") clientID: String,
        @Header("x-signature") signature: String,
        @Header("token") token: String,
        @Header("mid") mid: String,
        @Body search: SearchRequest
    ): Call<SearchResponse>

    // Omnichannel integration
    @GET("channel/v1/channel-integration/list/")
    fun listIntegration(
        @Header("x-request-id") requestId: String,
        @Header("x-request-timestamp") requestTimestamp: String,
        @Header("x-client-id") clientId: String,
        @Header("mid") merchantId: String
    ): Single<IntegrationArrayResponse>

    @POST("channel/v1/channel-integration/add/")
    fun connectIntegration(
        @Header("x-request-id") requestId: String,
        @Header("x-request-timestamp") requestTimestamp: String,
        @Header("x-client-id") clientId: String,
        @Body connectIntegrationRequest: ConnectIntegrationRequest
    ): Single<IntegrationObjectResponse>

    @DELETE("channel/v1/channel-integration/{id}/disconnect/")
    fun disconnectIntegration(
        @Header("x-request-id") requestId: String,
        @Header("x-request-timestamp") requestTimestamp: String,
        @Header("x-client-id") clientId: String,
        @Path("id") channelId: String
    ): Single<IntegrationObjectResponse>
}
