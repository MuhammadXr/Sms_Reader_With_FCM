package com.rast.sms_reader

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.rast.sms_reader.ui.theme.SmsReaderTheme
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        subscribeToTopic()
        setContent {
            SmsReaderTheme {
                val requestPermissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission(),
                    onResult = {

                    }
                )

                fun askNotificationPermission() {
                    // This is only necessary for API level >= 33 (TIRAMISU)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        if (ContextCompat.checkSelfPermission(
                                this,
                                android.Manifest.permission.POST_NOTIFICATIONS
                            ) ==
                            PackageManager.PERMISSION_GRANTED
                        ) {
                            // FCM SDK (and your app) can post notifications.
                        } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
                            // TODO: display an educational UI explaining to the user the features that will be enabled
                            //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                            //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                            //       If the user selects "No thanks," allow the user to continue without notifications.
                        } else {
                            // Directly ask for the permission
                            requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }
                }

                val coroutineScope = rememberCoroutineScope()
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LaunchedEffect(key1 = true, block = {
                        coroutineScope.launch {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                askNotificationPermission()
                            }
                        }
                    })
                    Greeting("Android")
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
    }

    private fun subscribeToTopic(){
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