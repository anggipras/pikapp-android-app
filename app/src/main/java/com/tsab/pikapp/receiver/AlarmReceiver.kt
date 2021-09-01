package com.tsab.pikapp.receiver

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.tsab.pikapp.R
import com.tsab.pikapp.util.NOTIFICATION_CHANNEL_ID
import com.tsab.pikapp.util.SessionManager
import com.tsab.pikapp.view.homev2.HomeNavigation

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val sessionManager = SessionManager()
        val contentIntent = Intent(context, HomeNavigation::class.java)
        intent!!.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val contentPendingIntent = PendingIntent.getActivity(context, 0, contentIntent, 0)
        val contentTitle = "Hello, ${sessionManager.getUserData()?.customerName}"

        val builder = NotificationCompat.Builder(context!!, context.getString(R.string.general_notif_id))
            .setSmallIcon(R.drawable.ic_integration)
            .setContentTitle(contentTitle)
            .setContentText(context.getString(R.string.general_notif_message))
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(contentPendingIntent)

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(200, builder.build())
    }

}