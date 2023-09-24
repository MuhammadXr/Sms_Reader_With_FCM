package com.rast.smsreader.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.rast.smsreader.foreground.SmsForegroundService
import com.rast.smsreader.workmanager.SmsWorker

class SmsReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        when(intent?.action){
            Telephony.Sms.Intents.SMS_RECEIVED_ACTION -> {
                Log.d("TTT", "onReceive: SMS_RECEIVED_ACTION")
                for (smsMessage in Telephony.Sms.Intents.getMessagesFromIntent(intent)) {


                    val messageBody = smsMessage.messageBody
                    val senderNumber = smsMessage.originatingAddress

                    val inputData = workDataOf(
                        "senderNumber" to senderNumber,
                        "messageBody" to messageBody
                    )
                    val workRequest = OneTimeWorkRequestBuilder<SmsWorker>()
                        .setInputData(inputData)
                        .build()

                    context?.let { WorkManager.getInstance(it).enqueue(workRequest) }
                }
            }

            Intent.ACTION_BOOT_COMPLETED -> {
                Log.d("TTT", "onReceive: ACTION_BOOT_COMPLETED")
                val serviceIntent = Intent(context, SmsForegroundService::class.java)
                context?.startForegroundService(serviceIntent)
            }
            Intent.ACTION_LOCKED_BOOT_COMPLETED -> {
                Log.d("TTT", "onReceive: ACTION_LOCKED_BOOT_COMPLETED")
                try {
                    val serviceIntent = Intent(context, SmsForegroundService::class.java)
                    context?.startForegroundService(serviceIntent)
                }catch (_: Exception){
                    Log.d("TTT", "onReceive: ACTION_LOCKED_BOOT_COMPLETED ERROR")
                }
            }
        }
    }
}