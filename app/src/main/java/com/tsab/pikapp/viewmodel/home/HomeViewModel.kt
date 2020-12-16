package com.tsab.pikapp.viewmodel.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import com.tsab.pikapp.models.model.UserAccess
import com.tsab.pikapp.util.SessionManager
import com.tsab.pikapp.util.SharedPreferencesUtil
import com.tsab.pikapp.view.HomeActivity
import com.tsab.pikapp.view.StoreActivity
import com.tsab.pikapp.view.home.HomeFragmentDirections
import com.tsab.pikapp.view.home.ProfileFragment
import com.tsab.pikapp.viewmodel.BaseViewModel
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnCompleteListener
import com.tsab.pikapp.R
import com.tsab.pikapp.models.model.LocationModel
import com.tsab.pikapp.util.LocationLiveData
import com.tsab.pikapp.view.OrderListActivity

class HomeViewModel(application: Application) : BaseViewModel(application) {

    private var prefHelper = SharedPreferencesUtil(getApplication())
    private var sessionManager = SessionManager(getApplication())
    private val locationData = LocationLiveData(application)

    val isLocationEnabled = MutableLiveData<Boolean>()
    val isLocationRetrieved = MutableLiveData<Boolean>()
    val isDeeplinkEnabled = MutableLiveData<Boolean>()

    val isUserMerchant = MutableLiveData<Boolean>()
    val notificationActive = MutableLiveData<Boolean>()
    var deeplinkTarget = ""

    fun goToProfile(context: Context) {
        val profileFragment = ProfileFragment()
        profileFragment.show(
            (context as HomeActivity).supportFragmentManager,
            profileFragment.getTag()
        )
    }

    fun setStatusLocation(status: Boolean) {
        Log.d("Debug", "token : ${sessionManager.getUserToken()}")
        isLocationEnabled.value = status
    }

    fun getLocationData() = locationData

    fun saveUserLocation(longitude: String, latitude: String) {
        prefHelper.saveLatestLocation(longitude, latitude)
        isLocationRetrieved.value = true
    }

    fun checkDeeplink() {
        val deeplink = prefHelper.getDeeplink()
        if(deeplink.mid!! != "") {
            deeplinkTarget = "merchant"
            isDeeplinkEnabled.value = true
        } else if (deeplink.address!! != "") {
            deeplinkTarget = "list"
            isDeeplinkEnabled.value = true
        }
    }

    fun checkUserMerchantStatus() {
        val userAccess: UserAccess? = sessionManager.getUserData()
        Log.d("Debug", "user access : $userAccess")
        userAccess?.let {
            isUserMerchant.value = it.isMerchant!!
        }
    }

    fun goToStoreHome(context: Context) {
        val storeActivity = Intent(context, StoreActivity::class.java)
        (context as HomeActivity).startActivity(storeActivity)
    }

    fun goToOrderList(context: Context) {
        val orderListActivity = Intent(context, OrderListActivity::class.java)
        (context as HomeActivity).startActivity(orderListActivity)
    }

    fun goToMerchant(view: View) {
        val deeplink = prefHelper.getDeeplink()
//        if (Navigation.findNavController(view).currentDestination?.id == R.id.homeFragment) {
            if(deeplinkTarget == "merchant"){
                val action = HomeFragmentDirections.actionFromHomeFragmentToMerchantDetailFragment(deeplink.mid!!)
                Navigation.findNavController(view).navigate(action)
            } else if(deeplinkTarget == "list"){
                val action = HomeFragmentDirections.actionToCategoryFragment(1L)
                Navigation.findNavController(view).navigate(action)
            }
//        }
    }

    fun checkNotification() {
        val notif = prefHelper.getNotificationDetail()
        notif?.let {
            it.isMerchant?.let {isMerchant ->
                if (!isMerchant) notificationActive.value = true
            }
        }
    }
}