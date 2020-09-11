package com.bejohen.pikapp.models.network

import com.bejohen.pikapp.models.model.*
import com.bejohen.pikapp.models.model.MerchantListResponse
import io.reactivex.Single
import retrofit2.http.*

interface PikappApi {
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

    @GET("auth/exit")
    fun logoutUser(
        @Header("x-request-id") uuid: String,
        @Header("x-request-timestamp") time: String,
        @Header("x-client-id") clientID: String,
        @Header("x-session-id") sessionID: String
    ): Single<LogoutResponse>

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
}