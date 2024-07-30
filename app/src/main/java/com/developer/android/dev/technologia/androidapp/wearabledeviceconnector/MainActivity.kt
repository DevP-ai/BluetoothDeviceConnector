package com.developer.android.dev.technologia.androidapp.wearabledeviceconnector

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.developer.android.dev.technologia.androidapp.wearabledeviceconnector.ui.theme.WearableDeviceConnectorTheme

class MainActivity : ComponentActivity() {
    private val bluetoothViewModel: BluetoothViewModel by viewModels()

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>



    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bluetoothViewModel.initialize(this)

        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val allPermissionGranted = permissions.all { it.value }
            bluetoothViewModel.updatePermissionsGranted(allPermissionGranted)
        }

        if(!bluetoothViewModel.permissionsGranted.value){
            requestPermissionLauncher.launch(
                arrayOf(
                    android.Manifest.permission.BLUETOOTH,
                    android.Manifest.permission.BLUETOOTH_ADMIN,
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.BLUETOOTH_CONNECT,
                    android.Manifest.permission.BLUETOOTH_SCAN
                )
            )
        }

        setContent {
            BluetoothScreen(bluetoothViewModel,requestPermissionLauncher = requestPermissionLauncher)
        }
    }
}
