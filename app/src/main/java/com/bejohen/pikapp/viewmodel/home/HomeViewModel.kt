package com.bejohen.pikapp.viewmodel.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import com.bejohen.pikapp.models.model.UserAccess
import com.bejohen.pikapp.util.SessionManager
import com.bejohen.pikapp.util.SharedPreferencesUtil
import com.bejohen.pikapp.view.HomeActivity
import com.bejohen.pikapp.view.LoginActivity
import com.bejohen.pikapp.view.StoreActivity
import com.bejohen.pikapp.view.categoryProduct.CategoryFragmentDirections
import com.bejohen.pikapp.view.home.HomeFragmentDirections
import com.bejohen.pikapp.view.home.ProfileFragment
import com.bejohen.pikapp.viewmodel.BaseViewModel
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.fragment_home_home.*
import java.util.*

class HomeViewModel(application: Application) : BaseViewModel(application) {

    private var prefHelper = SharedPreferencesUtil(getApplication())
    private var sessionManager = SessionManager(getApplication())

    val isLocationEnabled = MutableLiveData<Boolean>()
    val isLocationRetrieved = MutableLiveData<Boolean>()
    val isDeeplinkEnabled = MutableLiveData<Boolean>()

    val isUserMerchant = MutableLiveData<Boolean>()

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

    fun getUserLocation(activity: Activity) {
        var locationRequest = LocationRequest()
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        if (ActivityCompat.checkSelfPermission(
                (activity as HomeActivity),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                (activity as HomeActivity),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        LocationServices.getFusedLocationProviderClient(activity as HomeActivity).requestLocationUpdates(locationRequest, object : LocationCallback() {
                @SuppressLint("SetTextI18n")
                override fun onLocationResult(locationResult: LocationResult?) {
                    super.onLocationResult(locationResult)
                    LocationServices.getFusedLocationProviderClient(activity as HomeActivity)
                        .removeLocationUpdates(this)
                    if (locationResult != null && locationResult.locations.size > 0) {
                        val locIndex = locationResult.locations.size - 1

                        val latitude = locationResult.locations.get(locIndex).latitude
                        val longitude = locationResult.locations.get(locIndex).longitude

                        saveUserLocation(
                            longitude = longitude.toString(),
                            latitude = latitude.toString()
                        )
                    }
                }
            }, Looper.getMainLooper())
    }

    private fun saveUserLocation(longitude: String, latitude: String) {
        prefHelper.saveLatestLocation(longitude, latitude)
        isLocationRetrieved.value = true
    }

    fun checkDeeplink() {
        val deeplink = prefHelper.getDeeplink()
        isDeeplinkEnabled.value = !deeplink.mid.isNullOrEmpty()
    }

    fun checkUserMerchantStatus() {
        val userAccess: UserAccess? = sessionManager.getUserData()
        Log.d("Debug", "user access : $userAccess")
        isUserMerchant.value = userAccess!!.isMerchant!!
    }

    fun goToStoreHome(context: Context) {
        val storeActivity = Intent(context, StoreActivity::class.java)
        (context as HomeActivity).startActivity(storeActivity)
    }

    fun goToMerchant(view: View) {
        val deeplink = prefHelper.getDeeplink()
        val action =
            HomeFragmentDirections.actionFromHomeFragmentToMerchantDetailFragment(deeplink.mid!!)
        Navigation.findNavController(view).navigate(action)
    }
}