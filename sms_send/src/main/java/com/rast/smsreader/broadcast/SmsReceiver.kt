package com.rast.smsreader.broadcast

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Telephony
import android.telephony.SubscriptionManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.rast.smsreader.foreground.SmsForegroundService
import com.rast.smsreader.utils.PreferencesManager
import com.rast.smsreader.workmanager.SmsWorker


class SmsReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        when(intent?.action){
            Telephony.Sms.Intents.SMS_RECEIVED_ACTION -> {
                Log.d("TTT", "onReceive: SMS_RECEIVED_ACTION")



                context?.let {
                    val bundle = intent.extras
                    val requiredSlot = PreferencesManager(context).getSimSlotData(0)

                    val slot = bundle?.getInt("slot", -2)
                    val sub = bundle?.getInt("subscription", -2)
                    val manager = SubscriptionManager.from(context)
                    if (ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.READ_PHONE_STATE
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        val subscriptionInfo = sub?.let { it1 ->
                            manager.getActiveSubscriptionInfo(
                                it1
                            )


                        }
                        Log.d("TTT", "slot $slot subscription $sub")
                        Log.d("TTT", "subscriptionInfo= ${subscriptionInfo?.subscriptionId}")
                        Log.d("TTT", "subscriptionInfo?.simSlotIndex ${subscriptionInfo?.simSlotIndex}")
                        if (requiredSlot == subscriptionInfo?.simSlotIndex){
                            for (smsMessage in Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                                // Retrieves a map of extended data from the intent.
                                val messageBody = smsMessage.messageBody
                                val senderNumber = smsMessage.originatingAddress

                                val inputData = workDataOf(
                                    "senderNumber" to senderNumber,
                                    "messageBody" to messageBody
                                )
                                val workRequest = OneTimeWorkRequestBuilder<SmsWorker>()
                                    .setInputData(inputData)
                                    .build()

                                context.let { WorkManager.getInstance(it).enqueue(workRequest) }
                            }
                        }
                    }

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