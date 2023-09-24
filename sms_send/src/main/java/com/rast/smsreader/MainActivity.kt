package com.rast.smsreader

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.rast.smsreader.foreground.SmsForegroundService
import com.rast.smsreader.permissions.PermissionHandler
import com.rast.smsreader.ui.theme.SmsReaderTheme

class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        subscribeToTopic()

        setContent {
            SmsReaderTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                    Column(
                        modifier = Modifier,
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Batereya Settings, 1-sini tanla")
                            IconButton(onClick = {
                                PermissionHandler.openBatteryOptimizationIgnoringSettings(
                                    this@MainActivity
                                )
                            }) {
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = null
                                )
                            }
                        }
                        Box(modifier = Modifier.size(24.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Auto Startni yoqib qo'y")
                            IconButton(onClick = {
                                PermissionHandler.openSettingsApplication(
                                    this@MainActivity
                                )
                            }) {
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        checkAndRequestPermissions()
        super.onResume()
    }

    private val PERMISSIONS_REQUEST_CODE = 0

    private val permissions = arrayOf(
        android.Manifest.permission.RECEIVE_SMS,
        android.Manifest.permission.READ_SMS,
        android.Manifest.permission.RECEIVE_BOOT_COMPLETED
    )

    fun checkAndRequestPermissions() {
        val notGrantedPermissions = permissions.filter {
            ActivityCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (notGrantedPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                notGrantedPermissions.toTypedArray(),
                PERMISSIONS_REQUEST_CODE
            )
        } else {
            val serviceIntent = Intent(this, SmsForegroundService::class.java)
            startForegroundService(serviceIntent)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE -> {
                val isAllPermissionsGranted =
                    grantResults.all { it == PackageManager.PERMISSION_GRANTED }
                if (isAllPermissionsGranted) {
                    val serviceIntent = Intent(this, SmsForegroundService::class.java)
                    startForegroundService(serviceIntent)
                } else {
                    checkAndRequestPermissions()
                }
            }

            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun subscribeToTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic("allDevices")
            .addOnCompleteListener { task ->
                var msg = "Subscribed"
                if (!task.isSuccessful) {
                    msg = "Subscribe failed"
                }
                Log.d("TTT", msg)
                Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
            }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SmsReaderTheme {
        Greeting("Android")
    }
}