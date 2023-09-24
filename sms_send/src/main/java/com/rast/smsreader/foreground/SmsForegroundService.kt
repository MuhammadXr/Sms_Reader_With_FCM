package com.rast.smsreader.foreground

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.os.Build
import android.provider.Telephony
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import com.rast.smsreader.MainActivity
import com.rast.smsreader.R
import com.rast.smsreader.broadcast.SmsReceiver

class SmsForegroundService: LifecycleService() {

    private lateinit var foregroundNotification: Notification
    val broadcastReceiver = SmsReceiver()

    override fun onCreate() {
        super.onCreate()
        createNotifyForeground(context = this)

        val intentFilter = IntentFilter().apply {
            addAction(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)
            addAction(Intent.ACTION_BOOT_COMPLETED)
            addAction(Intent.ACTION_LOCKED_BOOT_COMPLETED)
        }
        //registerReceiver(broadcastReceiver,intentFilter)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)


        return START_STICKY
    }

    var iconNotification: Bitmap? = null
    val notificationList: MutableList<Notification> = emptyList<Notification>().toMutableList()
    var notificationManager: NotificationManager? = null
    var notificationChannelId = "sms_listen"
    val notificationChannel = NotificationChannel(
        notificationChannelId, "Sms listen",
        NotificationManager.IMPORTANCE_MIN
    )
    private fun createNotifyForeground(context: Context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {


            if (notificationManager == null) {
                notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                assert(notificationManager != null)
                notificationManager?.createNotificationChannelGroup(
                    NotificationChannelGroup("sms_listen", "Smslar")
                )
                notificationChannel.enableLights(false)
                notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                notificationManager?.createNotificationChannel(notificationChannel)
            }
            foregroundNotification = initNotification(context)
            startForeground(1, foregroundNotification)
        }
    }

    private fun initNotification(
        context: Context,
        pageTitle: String = "Sms jo'natuvchi ishlayapti",
    ): Notification {
        context.apply {
            val intent = Intent(this, MainActivity::class.java)
            //intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            val pendIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)


            val builder = NotificationCompat.Builder(this, notificationChannelId)

            builder.setContentTitle(pageTitle)
                //.setTicker(StringBuilder(resources.getString(R.string.app_name)).toString())
                .setContentText("") //                    , swipe down for more options.
                .setSmallIcon(R.drawable.baseline_message_24)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(false)
                .setContentIntent(pendIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

            builder.color = Color(0xFF4CAF50).toArgb()
            return builder.build()

        }
    }

    override fun onDestroy() {
        //unregisterReceiver(broadcastReceiver)
        super.onDestroy()

    }
}