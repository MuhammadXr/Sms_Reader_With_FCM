package com.rast.smsreader.permissions

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AppOpsManager
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat


object PermissionHandler {

    fun checkBatterOptimizationIgnoringPermission(context: Context): Boolean {

        val packageName = context.packageName
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        return pm.isIgnoringBatteryOptimizations(packageName)
        return true
    }

    @SuppressLint("BatteryLife")
    fun openBatteryOptimizationIgnoringSettings(
        context: Context,
        launcher: ManagedActivityResultLauncher<Intent, ActivityResult>? = null
    ) {
        if (checkBatterOptimizationIgnoringPermission(context)) {
            return
        }

        val intent = Intent()
        val packageName = context.packageName
        intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
        intent.data = Uri.parse("package:$packageName")
        if (launcher != null) {
            launcher.launch(intent)
        } else {
            context.startActivity(intent)
        }
    }


    fun openSettingsApplication(context: Context) {
        context.apply {

            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.parse("package:" + context.packageName)
            startActivity(intent)
        }
    }

    fun openAppNotificationSettings(
        context: Context,
        launcher: ManagedActivityResultLauncher<Intent, ActivityResult>? = null
    ) {
        context.apply {
            val intent = Intent()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            // for Android 5-7

            // for Android 5-7
            intent.putExtra("app_package", packageName)
            intent.putExtra("app_uid", applicationInfo.uid)

            // for Android 8 and above

            // for Android 8 and above
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
            }

            if (launcher != null) {
                launcher.launch(intent)
            } else {
                startActivity(
                    intent
                )
            }
        }
    }

    fun checkNotifications(context: Context): Boolean {
        return NotificationManagerCompat.from(context).areNotificationsEnabled()
    }
}