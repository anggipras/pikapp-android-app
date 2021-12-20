package com.tsab.pikapp.services

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import com.tsab.pikapp.R
import kotlinx.android.synthetic.main.nonetwork_dialog.view.*
import kotlinx.android.synthetic.main.noservice_dialog.view.*
import kotlinx.android.synthetic.main.profile_birthday_dialog.view.*

class OnlineService {
    fun isOnline(mContext: Context?): Boolean {
        val connMgr = mContext?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connMgr.activeNetworkInfo
        return networkInfo?.isConnected == true
    }

    fun showToast(mContext: Context) {
        Toast.makeText(mContext, "No internet connection", Toast.LENGTH_SHORT).show()
    }

    fun serviceDialog(activity: Activity) {
        val mDialogView = LayoutInflater.from(activity).inflate(R.layout.noservice_dialog, null)
        val mBuilder = AlertDialog.Builder(activity)
            .setView(mDialogView)
        val mAlertDialog = mBuilder.show()
        mAlertDialog.getWindow()?.setBackgroundDrawable(
            AppCompatResources.getDrawable(
                activity,
                R.drawable.dialog_background
            )
        )
        mDialogView.service_close.setOnClickListener {
            mAlertDialog.dismiss()
        }
    }

    fun networkDialog(activity: Activity) {
        val mDialogView = LayoutInflater.from(activity).inflate(R.layout.nonetwork_dialog, null)
        val mBuilder = AlertDialog.Builder(activity)
            .setView(mDialogView)
        val mAlertDialog = mBuilder.show()
        mAlertDialog.getWindow()?.setBackgroundDrawable(
            AppCompatResources.getDrawable(
                activity,
                R.drawable.dialog_background
            )
        )
        mDialogView.network_close.setOnClickListener {
            mAlertDialog.dismiss()
        }
    }
}