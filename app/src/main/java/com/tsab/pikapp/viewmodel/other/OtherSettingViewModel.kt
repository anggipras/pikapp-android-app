package com.tsab.pikapp.viewmodel.other

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.LayoutLoadingOverlayBinding
import com.tsab.pikapp.models.model.*
import com.tsab.pikapp.models.network.PikappApiService
import com.tsab.pikapp.util.*
import com.tsab.pikapp.view.other.otherSettings.shopMgmtSetting.ShopManagementAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.second_alert_dialog.view.*
import retrofit2.*
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class OtherSettingViewModel : ViewModel() {
    private var sessionManager = SessionManager()
    val gson = Gson()
    val type = object : TypeToken<BaseResponse>() {}.type
    private val disposable = CompositeDisposable()

    // Profile Setting Variable
    val profileFullName = MutableLiveData<String>()
    val profileDOB = MutableLiveData<String>()
    val profileGender = MutableLiveData<String>()
    val profileEmail = MutableLiveData<String>()
    val profileFullNameOwner = MutableLiveData<String>()
    val profileAccNo = MutableLiveData<String>()
    val profileBankName = MutableLiveData<String>()
    val profilePhone = MutableLiveData<String>()
    val _genderConfirmation = MutableLiveData<Boolean>()
    val _genderSelection = MutableLiveData<String?>()
    val _genderDialogAlert = MutableLiveData<Boolean>()
    val _birthdaySelection = MutableLiveData<String>()

    // Information Setting Variable
    val _restaurantBanner = MutableLiveData(Uri.EMPTY)
    val _restaurantLogo = MutableLiveData(Uri.EMPTY)

    // Pin Setting Variable
    val currentPin = MutableLiveData<String>()
    val _newPin = MutableLiveData<String>()
    val loadPin = MutableLiveData<Boolean>()
    val pinAlert = MutableLiveData<String>()

    // Shop Management Setting Variable
    lateinit var shopManagementAdapter: ShopManagementAdapter
    private var _shopStatus = MutableLiveData<String>()

    private val mutableDays = MutableLiveData("")
    val days: LiveData<String> get() = mutableDays

    private val mutableOpenTime = MutableLiveData("")
    private val mutableOpenTimeError = MutableLiveData("")
    val isOpenTimeValid = MutableLiveData(false)
    val openTime: LiveData<String> get() = mutableOpenTime
    val openTimeError: LiveData<String> get() = mutableOpenTimeError

    private val mutableCloseTime = MutableLiveData("")
    private val mutableCloseTimeError = MutableLiveData("")
    val isCloseTimeValid = MutableLiveData(false)
    val closeTime: LiveData<String> get() = mutableCloseTime
    val closeTimeError: LiveData<String> get() = mutableOpenTimeError

    private val mutableIsForceClose = MutableLiveData(true)
    val isForceClose: LiveData<Boolean> get() = mutableIsForceClose

    private val mutableAutoOnOff = MutableLiveData(true)
    val autoOnOff: LiveData<Boolean> get() = mutableAutoOnOff

    private val mutableScheduleList = MutableLiveData<List<ShopSchedule>>(listOf())
    val shopScheduleResult: LiveData<List<ShopSchedule>> = mutableScheduleList
    fun setShopSchedule(shopScheduleResult: List<ShopSchedule>) {
        mutableScheduleList.value = shopScheduleResult
    }

    private val mutableLoading = MutableLiveData(true)
    val Loading: LiveData<Boolean> get() = mutableLoading

    var isLoading = MutableLiveData<Boolean>()
    var isLoadingBackButton = MutableLiveData(false)

    // Profile Setting Method
    fun getProfileDetail() {
        val fullName = sessionManager.getMerchantProfile()?.fullName!!
        val dateOfBirth = sessionManager.getDOBProfile()!!
        val gender = sessionManager.getGenderProfile()!!
        val email = sessionManager.getMerchantProfile()!!.email!!
        val phone = sessionManager.getMerchantProfile()!!.phoneNumber!!
        val ownerName = sessionManager.getMerchantProfile()!!.bankAccountName!!
        val bankNo = sessionManager.getMerchantProfile()!!.bankAccountNo!!
        val bankName = sessionManager.getMerchantProfile()!!.bankName!!
        profileFullName.value = fullName
        profileDOB.value = dateOfBirth
        profileGender.value = gender
        profileEmail.value = email
        profileFullNameOwner.value = ownerName
        profileBankName.value = bankName
        profileAccNo.value = bankNo
        profilePhone.value = phone
    }

    fun setGenderAndDOB() {
        val gender = when (_genderSelection.value) {
            "Perempuan" -> "FEMALE"
            else -> "MALE"
        }

        sessionManager.setDOBProfile(_birthdaySelection.value)
        sessionManager.setGenderProfile(gender)
    }

    fun setDefaultGender() {
        _genderSelection.value = null
    }

    // Pin Setting Method
    fun setCurrentPin(pin: String?) {
        currentPin.value = pin!!
    }

    fun setNewPin(pin: String?) {
        _newPin.value = pin!!
    }

    fun onLoadChangePin(bool: Boolean) {
        loadPin.value = bool
    }

    fun changePinAlert(alert: String) {
        pinAlert.value = alert
    }

    fun changePin() {
        onLoadChangePin(true)
        val timeStamp = getTimestamp()
        val email = sessionManager.getUserData()!!.email!!
        val mid = sessionManager.getUserData()!!.mid!!
        val signature = getSignature(email, timeStamp)
        val token = sessionManager.getUserToken()!!
        val uuid = getUUID()
        val clientId = getClientID()

        val pinModel = createPinModel(mid)

        PikappApiService().api.changePinMerchant("application/json", uuid, timeStamp, clientId, signature, token, pinModel)
            .enqueue(object : Callback<OtherBaseResponse> {
                override fun onResponse(call: Call<OtherBaseResponse>, response: Response<OtherBaseResponse>) {
                    if (response.code() == 200) {
                        changePinAlert("APPROVED/OK")
                    } else {
                        val gson = Gson()
                        val type = object : TypeToken<OtherBaseResponse>() {}.type


                        var errorResponse: OtherBaseResponse? = gson.fromJson(response.errorBody()!!.charStream(), type)

                        if (errorResponse != null) {
                            changePinAlert(errorResponse.errMessage!!)
                        }
                    }
                }

                override fun onFailure(call: Call<OtherBaseResponse>, t: Throwable) {
                    Log.d("ONFAILURE", t.toString())
                }

            })
    }

    private fun createPinModel(mid: String) : pinMerchant {
        return pinMerchant(
            oldPin = currentPin.value,
            mid = mid,
            pin = _newPin.value
        )
    }

    // Information Setting Method
    fun setBannerImg(banner: Uri?) {
        _restaurantBanner.value = banner
    }

    fun setLogoImg(logo: Uri?) {
        _restaurantLogo.value = logo
    }

    // Shop Management Setting Method
    fun setGender(bool: Boolean, gender: String) {
        _genderConfirmation.value = bool
        _genderSelection.value = gender
    }

    fun setBirthday(date: String?) {
        _birthdaySelection.value = date!!
    }

    fun confirmGender(bool: Boolean) {
        _genderDialogAlert.value = bool
    }

    fun getMerchantSchedule(
        baseContext: Context,
        shopSchedule_recyclerView: RecyclerView,
        listener: ShopManagementAdapter.OnItemClickListener
    ) {
        val uuid = getUUID()
        val timestamp = getTimestamp()
        val clientId = getClientID()
        val email = sessionManager.getUserData()!!.email!!
        val signature = getSignature(email, timestamp)
        val token = sessionManager.getUserToken()!!
        val mid = sessionManager.getUserData()!!.mid!!

        PikappApiService().api.getMerchantShopManagement(
            uuid, timestamp, clientId, signature, token, mid
        ).enqueue(object : Callback<MerchantTimeManagement> {
            override fun onFailure(call: Call<MerchantTimeManagement>, t: Throwable) {
                Log.e("getTimeManagementFailed", t.message.toString())
            }

            override fun onResponse(
                call: Call<MerchantTimeManagement>,
                response: Response<MerchantTimeManagement>
            ) {
                val timeManagementResult = response.body()?.results?.timeManagement
                shopManagementAdapter = ShopManagementAdapter(
                    baseContext,
                    timeManagementResult as MutableList<ShopSchedule>,
                    listener
                )

                shopManagementAdapter.notifyDataSetChanged()
                shopSchedule_recyclerView.adapter = shopManagementAdapter
                setShopSchedule(timeManagementResult)
                mutableLoading.value = false
            }

        })
    }

    fun updateShopStatus(baseContext: Context){
        //var sessionManager = SessionManager(getApplication())
        val email = sessionManager.getUserData()!!.email!!
        val token = sessionManager.getUserToken()!!
        val timestamp = getTimestamp()
        val signature = getSignature(email, timestamp)
        val mid = sessionManager.getUserData()!!.mid!!
        var shopStatusReq = ShopManagementUpdateRequest()
        shopStatusReq.days = days.value
        shopStatusReq.closeTime = closeTime.value
        shopStatusReq.openTime = openTime.value
        shopStatusReq.isForceClose = isForceClose.value
        shopStatusReq.autoOnOff = autoOnOff.value

        PikappApiService().api.updateShopManagement(
                getUUID(), timestamp, getClientID(), signature, token, mid, shopStatusReq
        ).enqueue(object : retrofit2.Callback<BaseResponse> {
            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                Toast.makeText(baseContext, "failed", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (response.code() == 200 && response.body()!!.errCode.toString() == "EC0000") {
                    Toast.makeText(baseContext, "Berhasil disimpan", Toast.LENGTH_SHORT).show()
                } else {
                    var errorResponse: BaseResponse? =
                            gson.fromJson(response.errorBody()!!.charStream(), type)
                    Toast.makeText(
                            baseContext,
                            generateResponseMessage(
                                    errorResponse?.errCode,
                                    errorResponse?.errMessage
                            ).toString(),
                            Toast.LENGTH_LONG
                    ).show()
                }
            }
        })
    }

    fun validateOpenTime(openTIme: String): Boolean {
        if (openTIme.isEmpty() || openTIme.isBlank()) {
            mutableOpenTimeError.value = "Jam Buka tidak boleh kosong"
        } else {
            mutableOpenTimeError.value = ""
        }
        mutableOpenTime.value = openTIme
        isOpenTimeValid.value = mutableOpenTimeError.value!!.isEmpty()
        return isOpenTimeValid.value!!
    }

    fun validateCloseTime(closeTime: String): Boolean {
        if (closeTime.isEmpty() || closeTime.isBlank()) {
            mutableCloseTimeError.value = "Jam Tutup tidak boleh kosong"
        } else {
            mutableCloseTimeError.value = ""
        }
        mutableCloseTime.value = closeTime
        isCloseTimeValid.value = mutableCloseTime.value!!.isEmpty()
        return isCloseTimeValid.value!!
    }

    fun setShopStatus(status: String) {
        _shopStatus.value = status
    }

    // Loading
    fun loadProcess(bool: Boolean) {
        isLoading.value = bool
    }

    // Load merchant profile
    fun getMerchantProfile() {
        val timeStamp = getTimestamp()
        val email = sessionManager.getUserData()!!.email!!
        val mid = sessionManager.getUserData()!!.mid!!
        val signature = getSignature(email, timeStamp)
        val token = sessionManager.getUserToken()!!
        val uuid = getUUID()
        val clientId = getClientID()
        val username = sessionManager.getUserName()

        disposable.add(
            PikappApiService().api.getMerchantProfile(
                uuid, timeStamp, clientId, signature, token, username
            )
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<MerchantProfileResponse>() {
                    override fun onSuccess(t: MerchantProfileResponse) {
                        t.results?.let { res ->
                            merchantProfileRetrieved(res)
                        }
                    }

                    override fun onError(e: Throwable) {}
                })
        )
    }

    fun merchantProfileRetrieved(response: MerchantProfileData) {
        sessionManager.setMerchantProfile(response)
        sessionManager.setProfileNum(2)

        loadProcess(false)
        isLoadingBackButton.value = true
    }

    // Restart gender dialog
    fun restartFragment() {
        _genderConfirmation.value = false
        _genderDialogAlert.value = false
    }

    fun getDays(days: String) {
        mutableDays.value = days
    }

    fun getOpenTime(openTime: String) {
        mutableOpenTime.value = openTime
    }

    fun getCLoseTime(closeTime: String) {
        mutableCloseTime.value = closeTime
    }

    fun getForceClose(isForceClose: Boolean) {
        mutableIsForceClose.value = isForceClose
    }

    fun setAutoOnOffTrue(autoOnOff: Boolean){
        mutableAutoOnOff.value = true
    }

    fun setAutoOnOffFalse(autoOnOff: Boolean){
        mutableAutoOnOff.value = false
    }

    /* SHIPPING SETTINGS ------- */
    private val mutableEditOrAddShipment = MutableLiveData<Boolean>()
    val editOrAddShipment: LiveData<Boolean> = mutableEditOrAddShipment
    fun setEditOrAddShipment(bool: Boolean) {
        mutableEditOrAddShipment.value = bool
    }

    private val mutableFirstEnterEdit = MutableLiveData<Boolean>()
    val firstEnterEdit: LiveData<Boolean> = mutableFirstEnterEdit
    fun setFirstEnterEdit(bool: Boolean) {
        mutableFirstEnterEdit.value = bool
    }

    private val mutableShippingMode = MutableLiveData<Boolean>()
    val shippingMode: LiveData<Boolean> = mutableShippingMode
    fun setShippingMode(act: Boolean) {
        mutableShippingMode.value = act
    }

    private val mutableCurrentLatLng = MutableLiveData<CurrentLatLng>()
    val currentLatLng: LiveData<CurrentLatLng> = mutableCurrentLatLng
    fun setCurrentLocation(latLng: CurrentLatLng) {
        mutableCurrentLatLng.value = latLng
    }

    private val mutableAddressLocation = MutableLiveData<List<Address>>()
    val addressLocation: LiveData<List<Address>> = mutableAddressLocation
    fun setAddressLocation(context: Context, latLng: CurrentLatLng) {
        val gcd = Geocoder(context, Locale.getDefault())
        val addresses: List<Address> = gcd.getFromLocation(latLng.latitude, latLng.longitude, 1)
        mutableAddressLocation.value = addresses
        if (!addresses[0].postalCode.isNullOrEmpty()) {
            setPostalCode(addresses[0].postalCode)
        } else {
            setPostalCode("")
        }
    }

    private val mutablePostalCode = MutableLiveData<String?>()
    val postalCode: LiveData<String?> = mutablePostalCode
    fun setPostalCode(postalCd: String?) {
        mutablePostalCode.value = postalCd
    }

    private val mutableAddPostalCode = MutableLiveData<String>()
    val addPostalCode: LiveData<String> = mutableAddPostalCode
    fun setAddPostalCode(postalCd: String) {
        mutableAddPostalCode.value = postalCd
    }
    private val mutableAddressShippingDetail = MutableLiveData<String>()
    val addressShippingDetail: LiveData<String> = mutableAddressShippingDetail
    fun setAddressShippingDetail(shippingdet: String) {
        mutableAddressShippingDetail.value = shippingdet
    }
    private val mutableMerchantAddress = MutableLiveData<String>()
    val merchantAddress: LiveData<String> = mutableMerchantAddress
    fun setMerchantAddress(address: String) {
        mutableMerchantAddress.value = address
    }

    fun validateAddress(context: Context, postalCode: String, merchantAddress: String): Boolean {
        if (postalCode.isBlank() || merchantAddress.isBlank()) {
            Toast.makeText(context, "Mohon isi field yang kosong terlebih dahulu", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    fun checkMerchantShipmentCondition(
        context: Context,
        view: View,
        nestedShipmentLayout: NestedScrollView,
        shipmentButtonSection: ConstraintLayout,
        loadingOverlay: LayoutLoadingOverlayBinding,
    ) {
        nestedShipmentLayout.isVisible = false
        shipmentButtonSection.isVisible = false
        loadingOverlay.loadingView.isVisible = true
        val token = sessionManager.getUserToken()!!
        val timestamp = getTimestamp()
        val mid = sessionManager.getUserData()!!.mid!!
        disposable.add(
            PikappApiService().shipmentApi.checkShipmentCondition(getUUID(), timestamp, getClientID(), token, mid)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<ShipmentConditionResponse>() {
                    override fun onSuccess(t: ShipmentConditionResponse) {
                        t.result.let {
                            val checkResult = t.result[0]
                            if (checkResult.postal_code) {
                                setEditOrAddShipment(true)
                                if (firstEnterEdit.value == true) {
                                    nestedShipmentLayout.isVisible = true
                                    shipmentButtonSection.isVisible = true
                                    loadingOverlay.loadingView.isVisible = false
                                } else {
                                    getMerchantShipment(context, nestedShipmentLayout, shipmentButtonSection, loadingOverlay)
                                    setFirstEnterEdit(true)
                                }
                            } else {
                                setEditOrAddShipment(false)
                                loadingOverlay.loadingView.isVisible = false
                                Navigation.findNavController(view).navigate(R.id.navigateTo_merchantAddShipmentFragment)
                            }
                        }
                    }

                    override fun onError(e: Throwable) {
                        Log.e("ERROR", e.message.toString())
                        loadingOverlay.loadingView.isVisible = false
                    }

                })
        )
    }

    fun getMerchantShipment(
        context: Context,
        nestedShipmentLayout: NestedScrollView,
        shipmentButtonSection: ConstraintLayout,
        loadingOverlay: LayoutLoadingOverlayBinding
    ) {
        val token = sessionManager.getUserToken()!!
        val timestamp = getTimestamp()
        val mid = sessionManager.getUserData()!!.mid!!
        disposable.add(
            PikappApiService().shipmentApi.getMerchantShipment(getUUID(), timestamp, getClientID(), token, mid)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<MerchantShipmentDataResponse>() {
                    override fun onSuccess(t: MerchantShipmentDataResponse) {
                        val theResult = t.result
                        val currentLatLng = CurrentLatLng(latitude = theResult.latitude.toDouble(), longitude = theResult.longitude.toDouble())

                        /* set longlat from database */
                        setCurrentLocation(currentLatLng)

                        /* set longlat to be extracted */
                        setAddressLocation(context, currentLatLng)

                        /* set shipping mode */
                        setShippingMode(theResult.shipping_available)

                        /* set merchant address */
                        setMerchantAddress(theResult.address)

                        /* set postal code */
                        setPostalCode(theResult.postal_code)

                        /* set courier list to be shown */
                        setCourierList(theResult.courier)

                        /* final change the view to show */
                        nestedShipmentLayout.isVisible = true
                        shipmentButtonSection.isVisible = true
                        loadingOverlay.loadingView.isVisible = false
                    }

                    override fun onError(e: Throwable) {
                        Log.e("ERROR", e.message.toString())
                        loadingOverlay.loadingView.isVisible = false
                    }

                })
        )
    }

    //GET LIST GOOGLE PLACE
    private var liveDataGooglePlacesList: MutableLiveData<List<ListGooglePlaces>> = MutableLiveData()
    fun getLiveDataGooglePlacesListObserver(): MutableLiveData<List<ListGooglePlaces>> {
        return liveDataGooglePlacesList
    }
    fun setLiveDataPlaces(placeList: List<ListGooglePlaces>) {
        liveDataGooglePlacesList.postValue(placeList)
    }

    fun getListGooglePlaces(text: String, context: Context) {
        disposable.add(
            PikappApiService().googleApi.getListOfPlaces(text, context.getString(R.string.google_api_key))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<GooglePlacesResponse>() {
                    override fun onSuccess(t: GooglePlacesResponse) {
                        t.results.let { res ->
                            liveDataGooglePlacesList.postValue(res)
                        }
                    }

                    override fun onError(e: Throwable) {
                        Log.e("ERROR_GET_PLACES", e.message.toString())
                    }
                })
        )
    }

    //COURIER SETTINGS DATA
    private var liveDataCourierList: MutableLiveData<MutableList<CourierList>> = MutableLiveData()
    fun getLiveDataCourierListObserver(): MutableLiveData<MutableList<CourierList>> {
        return liveDataCourierList
    }

    private fun setCourierList(courierList: MutableList<CourierList>) {
        liveDataCourierList.value = courierList
    }

    fun getCourierList(loadingOverlay: LayoutLoadingOverlayBinding) {
        loadingOverlay.loadingView.isVisible = true
        val token = sessionManager.getUserToken()!!
        val timestamp = getTimestamp()
        disposable.add(
            PikappApiService().shipmentApi.getCourierList(getUUID(), timestamp, getClientID(), token)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<CourierListResponse>() {
                    override fun onSuccess(t: CourierListResponse) {
                        val listOfCourier: MutableList<CourierList> = ArrayList()
                        listOfCourier.addAll(t.result)
                        listOfCourier.forEach { courierName ->
                            courierName.services_list.forEach { courierService ->
                                courierService.courier_service_type = true
                            }
                        }
                        setCourierList(listOfCourier)
                        loadingOverlay.loadingView.isVisible = false
                    }

                    override fun onError(e: Throwable) {
                        Log.e("ERROR", e.message.toString())
                        loadingOverlay.loadingView.isVisible = false
                    }

                })
        )
    }

    fun changeCourierService(courierNameIndex: Int, courierServiceIndex: Int, isChecked: Boolean) {
        liveDataCourierList.value!![courierNameIndex].services_list[courierServiceIndex].courier_service_type = isChecked
    }

    private fun submitShipmentData(
        view: View,
        loadingOverlay: LayoutLoadingOverlayBinding,
        postalCode: String,
        merchantAddress: String
    ) {
        val mappedCourierData: MutableList<Courier> = ArrayList()
        val gojekTemplate = Gojek(gojek_main = true, instant_services = true, same_day_services = true)
        val grabTemplate = Grab(grab_main = true, instant_services = true, same_day_services = true, instant_car_services = true)
        val paxelTemplate = Paxel(paxel_main = true, big_services = true, large_services = true, medium_services = true, small_services = true)
        val lalamoveTemplate = Lalamove(lalamove_main = true, motor_services = true, mpv_services = true, truck_services = true, van_services = true)
        val raraTemplate = Rara(rara_main = true, instant_services = true)
        val mrspeedyTemplate = MrSpeedy(mr_speedy_main = true, car_services = true, bike_services = true)
        liveDataCourierList.value?.forEach { courierName ->
            when(courierName.courier_name) {
                "Gojek" -> {
                    var countService = 0
                    courierName.services_list.forEach { courierService ->
                        if (courierService.courier_service_type) {
                            countService++
                        }

                        when(courierService.courier_services_code) {
                            "instant" ->gojekTemplate.instant_services = courierService.courier_service_type
                            else -> gojekTemplate.same_day_services = courierService.courier_service_type
                        }
                    }
                    gojekTemplate.gojek_main = countService > 0
                }
                "Grab" -> {
                    var countService = 0
                    courierName.services_list.forEach { courierService ->
                        if (courierService.courier_service_type) {
                            countService++
                        }

                        when(courierService.courier_services_code) {
                            "instant" -> grabTemplate.instant_services = courierService.courier_service_type
                            "same_day" -> grabTemplate.same_day_services = courierService.courier_service_type
                            else -> grabTemplate.instant_car_services = courierService.courier_service_type
                        }
                    }
                    grabTemplate.grab_main = countService > 0
                }
                "Paxel" -> {
                    var countService = 0
                    courierName.services_list.forEach { courierService ->
                        if (courierService.courier_service_type) {
                            countService++
                        }

                        when(courierService.courier_services_code) {
                            "small" -> paxelTemplate.small_services = courierService.courier_service_type
                            "medium" -> paxelTemplate.medium_services = courierService.courier_service_type
                            "large" -> paxelTemplate.large_services = courierService.courier_service_type
                            else -> paxelTemplate.big_services = courierService.courier_service_type
                        }
                    }
                    paxelTemplate.paxel_main = countService > 0
                }
                "Lalamove" -> {
                    var countService = 0
                    courierName.services_list.forEach { courierService ->
                        if (courierService.courier_service_type) {
                            countService++
                        }

                        when(courierService.courier_services_code) {
                            "motorcycle" -> lalamoveTemplate.motor_services = courierService.courier_service_type
                            "mpv" -> lalamoveTemplate.mpv_services = courierService.courier_service_type
                            "van" -> lalamoveTemplate.van_services = courierService.courier_service_type
                            else -> lalamoveTemplate.truck_services = courierService.courier_service_type
                        }
                    }
                    lalamoveTemplate.lalamove_main = countService > 0
                }
                "Rara Delivery" -> {
                    var countService = 0
                    courierName.services_list.forEach { courierService ->
                        if (courierService.courier_service_type) {
                            countService++
                        }

                        raraTemplate.instant_services = courierService.courier_service_type
                    }
                    raraTemplate.rara_main = countService > 0
                }
                "Mr Speedy" -> {
                    var countService = 0
                    courierName.services_list.forEach { courierService ->
                        if (courierService.courier_service_type) {
                            countService++
                        }

                        when(courierService.courier_services_code) {
                            "instant_bike" -> mrspeedyTemplate.bike_services = courierService.courier_service_type
                            else -> mrspeedyTemplate.car_services = courierService.courier_service_type
                        }
                    }
                    mrspeedyTemplate.mr_speedy_main = countService > 0
                }
            }
        }
        mappedCourierData.add(Courier(gojek = gojekTemplate, grab = grabTemplate, paxel = paxelTemplate, lalamove = lalamoveTemplate, rara = raraTemplate, mr_speedy = mrspeedyTemplate))
        val reqMerchShipment = RequestMerchantShipment(
            merchant_address = merchantAddress,
            latitude = currentLatLng.value?.latitude.toString(),
            longitude = currentLatLng.value?.longitude.toString(),
            postal_code = postalCode,
            subdistrict_name = addressLocation.value?.get(0)?.locality!!,
            province = addressLocation.value?.get(0)?.adminArea!!,
            shipping_available = shippingMode.value ?: false,
            courier = mappedCourierData
        )

        loadingOverlay.loadingView.isVisible = true
        val token = sessionManager.getUserToken()!!
        val timestamp = getTimestamp()
        val mid = sessionManager.getUserData()!!.mid!!

        if (editOrAddShipment.value == true) { // EDIT TO UPDATE MERCHANT LOCATION AND DELIVERY OPTIONS
            disposable.add(
                PikappApiService().shipmentApi.updateMerchantShipment(getUUID(), timestamp, getClientID(), token, mid, reqMerchShipment)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableSingleObserver<SubmitDataShipmentResponse>() {
                        override fun onSuccess(t: SubmitDataShipmentResponse) {
                            Log.e("UPDATE_SUCCEED", "succeed")
                            loadingOverlay.loadingView.isVisible = false
                            setFirstEnterEdit(false)
                            Navigation.findNavController(view).navigateUp()
                        }

                        override fun onError(e: Throwable) {
                            Log.e("UPDATE_ERROR", e.message.toString())
                            loadingOverlay.loadingView.isVisible = false
                        }
                    })
            )
        } else { // ADD MERCHANT LOCATION AND DELIVERY OPTIONS
            disposable.add(
                PikappApiService().shipmentApi.submitMerchantShipment(getUUID(), timestamp, getClientID(), token, mid, reqMerchShipment)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableSingleObserver<SubmitDataShipmentResponse>() {
                        override fun onSuccess(t: SubmitDataShipmentResponse) {
                            Log.e("SUBMIT_SUCCEED", "succeed")
                            loadingOverlay.loadingView.isVisible = false
                            setFirstEnterEdit(false)
                            Navigation.findNavController(view).navigate(R.id.fromShipmentAddCourier_navigateTo_settingFragment)
                        }

                        override fun onError(e: Throwable) {
                            Log.e("SUBMIT_ERROR", e.message.toString())
                            loadingOverlay.loadingView.isVisible = false
                        }
                    })
            )
        }
    }

    fun openSubmitDialog(
        activity: Activity,
        view: View,
        loadingOverlay: LayoutLoadingOverlayBinding,
        postalCode: String,
        merchantAddress: String
    ) {
        val mDialogView = LayoutInflater.from(activity).inflate(R.layout.second_alert_dialog, null)
        val mBuilder = AlertDialog.Builder(activity)
            .setView(mDialogView)
        val mAlertDialog = mBuilder.show()
        mAlertDialog.getWindow()?.setBackgroundDrawable(
            AppCompatResources.getDrawable(
                activity,
                R.drawable.dialog_background
            )
        )
        mDialogView.second_dialog_text.text = "Anda yakin ingin menyimpan informasi terkait pengiriman?"

        mDialogView.second_dialog_back.setOnClickListener {
            mAlertDialog.dismiss()
        }
        mDialogView.second_dialog_close.setOnClickListener {
            mAlertDialog.dismiss()
        }
        mDialogView.second_dialog_ok.setOnClickListener {
            mAlertDialog.dismiss()
            submitShipmentData(view, loadingOverlay, postalCode, merchantAddress)
        }
    }

    // DUMMY DATA
    fun setDummyCourierList() {
        val listOfCourier: MutableList<CourierList> = ArrayList()
        listOfCourier.add(CourierList(courier_image = null, courier_main = true, courier_name = "Gojek", services_list = mutableListOf(
            CourierServiceList(courier_services_code = "instant", courier_services_name = "Instant", description = "On Demand Instant (bike) (1-3 hours)", courier_service_type = true),
            CourierServiceList(courier_services_code = "same_day", courier_services_name = "Same Day", description = "On Demand Instant (bike) (1-3 hours)", courier_service_type = true),
        )))
        listOfCourier.add(CourierList(courier_image = null, courier_main = true, courier_name = "Grab", services_list = mutableListOf(
            CourierServiceList(courier_services_code = "instant", courier_services_name = "Instant Bike", description = "On Demand Instant (bike) (1-3 hours)", courier_service_type = true),
            CourierServiceList(courier_services_code = "same_day", courier_services_name = "Same Day", description = "On Demand Instant (bike) (1-3 hours)", courier_service_type = true),
            CourierServiceList(courier_services_code = "instant_car", courier_services_name = "Instant Car", description = "On Demand Instant (car) (1-3 hours)", courier_service_type = true)
        )))
        listOfCourier.add(CourierList(courier_image = null, courier_main = true, courier_name = "Paxel", services_list = mutableListOf(
            CourierServiceList(courier_services_code = "small", courier_services_name = "Small Package", description = "On Demand Instant (bike) (1-3 hours)", courier_service_type = true),
            CourierServiceList(courier_services_code = "medium", courier_services_name = "Medium Package", description = "On Demand Instant (car) (1-3 hours)", courier_service_type = true),
            CourierServiceList(courier_services_code = "large", courier_services_name = "Large Package", description = "On Demand Instant (car) (1-3 hours)", courier_service_type = true),
            CourierServiceList(courier_services_code = "paxel_big", courier_services_name = "Paxel Big", description = "On Demand Instant (car) (1-3 hours)", courier_service_type = true)
        )))
        listOfCourier.add(CourierList(courier_image = null, courier_main = true, courier_name = "Lalamove", services_list = mutableListOf(
            CourierServiceList(courier_services_code = "motorcycle", courier_services_name = "Motorcycle", description = "On Demand Instant (bike) (1-3 hours)", courier_service_type = true),
            CourierServiceList(courier_services_code = "mpv", courier_services_name = "MPV", description = "On Demand Instant (car) (1-3 hours)", courier_service_type = true),
            CourierServiceList(courier_services_code = "van", courier_services_name = "Van", description = "On Demand Instant (car) (1-3 hours)", courier_service_type = true),
            CourierServiceList(courier_services_code = "truck", courier_services_name = "Truck", description = "On Demand Instant (car) (1-3 hours)", courier_service_type = true)
        )))
        listOfCourier.add(CourierList(courier_image = null, courier_main = true, courier_name = "Rara Delivery", services_list = mutableListOf(
            CourierServiceList(courier_services_code = "instant", courier_services_name = "Instant", description = "On Demand Instant (bike) (1-3 hours)", courier_service_type = true)
        )))
        listOfCourier.add(CourierList(courier_image = null, courier_main = true, courier_name = "Mr Speedy", services_list = mutableListOf(
            CourierServiceList(courier_services_code = "instant_bike", courier_services_name = "Instant Bike", description = "On Demand Instant (bike) (1-3 hours)", courier_service_type = true),
            CourierServiceList(courier_services_code = "instant_car", courier_services_name = "Instant Car", description = "On Demand Instant (car) (1-3 hours)", courier_service_type = true)
        )))

        setCourierList(listOfCourier)
    }
}
