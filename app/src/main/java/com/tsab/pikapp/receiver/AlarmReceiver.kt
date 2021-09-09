package com.tsab.pikapp.receiver

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.tsab.pikapp.R

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val contentIntent = Intent(Intent.ACTION_MAIN)
        contentIntent.addCategory(Intent.CATEGORY_APP_EMAIL)
        intent!!.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val contentPendingIntent = PendingIntent.getActivity(context, 0, contentIntent, 0)

        val builder = NotificationCompat.Builder(context!!, context.getString(R.string.general_notif_id))
            .setSmallIcon(R.drawable.logo_with_name)
            .setContentTitle(context.getString(R.string.general_notif_title))
            .setContentText(context.getString(R.string.general_notif_message))
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(contentPendingIntent)

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(200, builder.build())
    }

}