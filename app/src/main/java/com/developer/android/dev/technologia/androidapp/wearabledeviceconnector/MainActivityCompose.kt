package com.developer.android.dev.technologia.androidapp.wearabledeviceconnector

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi

class MainActivityCompose : ComponentActivity() {
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
