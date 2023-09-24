package com.rast.smsreader

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.core.app.ActivityCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.rast.smsreader.foreground.SmsForegroundService
import com.rast.smsreader.permissions.PermissionHandler
import com.rast.smsreader.ui.theme.SmsReaderTheme
import com.rast.smsreader.utils.PreferencesManager

class MainActivity : ComponentActivity() {


    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        unSubscribeToTopic()

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
                        Column(
                            modifier = Modifier,
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "Sim kartani tanla")
                            val selectedSimCard = remember {
                                mutableStateOf(
                                    PreferencesManager(this@MainActivity).getSimSlotData(0)
                                )
                            }
                            val simCards = listOf("Birinchi Sim karta", "Ikkinchi ikkinchi")
                            var mExpanded by remember { mutableStateOf(false) }
                            var mTextFieldSize by remember { mutableStateOf(androidx.compose.ui.geometry.Size.Zero)}

                            // Up Icon when expanded and down icon when collapsed
                            val icon = if (mExpanded)
                                Icons.Filled.KeyboardArrowUp
                            else
                                Icons.Filled.KeyboardArrowDown

                            Column(Modifier.padding(20.dp)) {

                                // Create an Outlined Text Field
                                // with icon and not expanded
                                OutlinedTextField(
                                    value = simCards[selectedSimCard.value],
                                    onValueChange = { selectedSimCard.value = it.toInt() },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .onGloballyPositioned { coordinates ->
                                            // This value is used to assign to
                                            // the DropDown the same width
                                            mTextFieldSize = coordinates.size.toSize()
                                        },
                                    label = {Text("Sms uchun simkartani tanlash")},
                                    trailingIcon = {
                                        Icon(icon,"contentDescription",
                                            Modifier.clickable { mExpanded = !mExpanded })
                                    },
                                    readOnly = true
                                )

                                // Create a drop-down menu with list of cities,
                                // when clicked, set the Text Field text as the city selected
                                DropdownMenu(
                                    expanded = mExpanded,
                                    onDismissRequest = { mExpanded = false },
                                    modifier = Modifier
                                        .width(with(LocalDensity.current){mTextFieldSize.width.toDp()})
                                ) {
                                    simCards.forEachIndexed { index, label ->
                                        DropdownMenuItem(onClick = {
                                            PreferencesManager(this@MainActivity).saveSimSlotData(index)
                                            selectedSimCard.value = index
                                            mExpanded = false
                                        }, text = {
                                            Text(text = label)
                                        } )
                                    }
                                }
                            }
                        }
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
        android.Manifest.permission.RECEIVE_BOOT_COMPLETED,
        android.Manifest.permission.READ_PHONE_STATE
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
    private fun unSubscribeToTopic() {
        FirebaseMessaging.getInstance().unsubscribeFromTopic("allDevices")
            .addOnCompleteListener { task ->
                var msg = "UnSubscribed"
                if (!task.isSuccessful) {
                    msg = "UnSubscribe failed"
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