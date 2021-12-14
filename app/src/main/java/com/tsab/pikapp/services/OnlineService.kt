package com.tsab.pikapp.services

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.widget.Toast

class OnlineService {
    fun isOnline(mContext: Context?): Boolean {
        val connMgr = mContext?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connMgr.activeNetworkInfo
        return networkInfo?.isConnected == true
    }

    fun showToast(mContext: Context) {
        Toast.makeText(mContext, "No internet connection", Toast.LENGTH_SHORT).show()
    }
}