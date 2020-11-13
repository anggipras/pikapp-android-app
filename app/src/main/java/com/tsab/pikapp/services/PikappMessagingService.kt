package com.tsab.pikapp.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.tsab.pikapp.R
import com.tsab.pikapp.models.model.NotificationModel
import com.tsab.pikapp.util.NOTIFICATION_CHANNEL_ID
import com.tsab.pikapp.util.SharedPreferencesUtil
import com.tsab.pikapp.view.StoreOrderListActivity
import java.util.*


class PikappMessagingService : FirebaseMessagingService() {

    private var prefHelper = SharedPreferencesUtil()

    val sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
    val att = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
        .build()

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d("Debug", "fcm notification : ${remoteMessage.data}")
        showNotification(remoteMessage.data)
//        if (remoteMessage.data.isEmpty())
//            showNotification(remoteMessage.notification!!.title!!, remoteMessage.notification!!.body!!)
//        else showNotification(remoteMessage.data, remoteMessage.notification!!.title!!, remoteMessage.notification!!.body!!)
    }

    private fun showNotification(data: Map<String, String>) {
        val title = data["title"].toString()
        val body = data["body"].toString()
        val isMerchant = data["is_merchant"].toString()
        val transactionId = data["transaction_id"].toString()
        val tableNo = data["table_no"].toString()
        var clickAction = data["click_action"].toString()
        var isMerchantSaved = false
        if(isMerchant == "true") {
            isMerchantSaved = true
            clickAction = "StoreOrderListActivity"
        } else {
            clickAction = "OrderListActivity"
        }
        prefHelper.setNotificationDetail(NotificationModel(isMerchantSaved, transactionId, tableNo))


        val intent = Intent(clickAction)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val customSound = Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.pikapp_sound)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID, "Notification", NotificationManager.IMPORTANCE_DEFAULT)
            notificationChannel.description = ""
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.BLUE
            notificationChannel.enableLights(true)
            notificationChannel.setSound(customSound, att)
            notificationChannel.vibrationPattern = longArrayOf(0, 1000, 500, 1000)
            notificationChannel.enableVibration(true)
            notificationManager.createNotificationChannel(notificationChannel)
        }

//        setNotificationModel(isMerchant, transactionId)

        val notificationBuilder: NotificationCompat.Builder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
        notificationBuilder.setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_ALL)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.drawable.ic_pikafood)
            .setContentTitle(title)
            .setContentText(body)
            .setContentIntent(pendingIntent)
            .setContentInfo("Info")

        notificationManager.notify(Random().nextInt(), notificationBuilder.build())
    }

    private fun showNotification(title: String, body: String) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID, "Notification",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val customSound = Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.pikapp_sound)

            notificationChannel.description = ""
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.BLUE
            notificationChannel.enableLights(true)
            notificationChannel.setSound(customSound, att)
            notificationChannel.vibrationPattern = longArrayOf(0, 1000, 500, 1000)
            notificationChannel.enableVibration(true)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
        notificationBuilder.setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_ALL)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.drawable.ic_pikafood)
            .setContentTitle(title)
            .setContentText(body)
            .setContentInfo("Info")
        notificationManager.notify(Random().nextInt(), notificationBuilder.build())
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        prefHelper.setFcmToken(p0)
        Log.d("Debug", "fcm token : $p0")
    }
}