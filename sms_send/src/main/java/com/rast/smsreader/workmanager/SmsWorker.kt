package com.rast.smsreader.workmanager

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.rast.smsreader.firebase.SmsUpload
import kotlinx.coroutines.runBlocking

class SmsWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result = runBlocking {
        val senderNumber = inputData.getString("senderNumber")
        val messageBody = inputData.getString("messageBody")

        val isSuccess = forwardSmsToServer(senderNumber, messageBody)

        return@runBlocking if (isSuccess) Result.success() else Result.retry()
    }

    private suspend fun forwardSmsToServer(senderNumber: String?, messageBody: String?): Boolean {
        val smsFire = SmsUpload()
        smsFire.sendNotification(senderNumber.toString(),messageBody.toString())
        return smsFire.sendSms(
            smsNumber = senderNumber.toString(),
            smsData = messageBody.toString()
        )
    }
}