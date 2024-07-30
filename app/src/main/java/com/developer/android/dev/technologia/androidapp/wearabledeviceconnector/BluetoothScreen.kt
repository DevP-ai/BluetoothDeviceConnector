package com.developer.android.dev.technologia.androidapp.wearabledeviceconnector
import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Build.VERSION
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlin.math.acos

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun BluetoothScreen(
    bluetoothViewModel: BluetoothViewModel = viewModel(),
    requestPermissionLauncher: ActivityResultLauncher<Array<String>>
) {
    val devices by bluetoothViewModel.devices.collectAsState()
    val connectedDevice by bluetoothViewModel.connectedDevice.collectAsState()
    val connectionStatus by bluetoothViewModel.connectionStatus.collectAsState()
    val permissionsGranted by bluetoothViewModel.permissionsGranted.collectAsState()

    val activity = LocalContext.current as ComponentActivity

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            bluetoothViewModel.initialize(activity)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bluetooth Devices") }
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(text = "Connection Status: $connectionStatus")

                Spacer(modifier = Modifier.height(16.dp))

                if (permissionsGranted) {
                    Button(
                        onClick = {
                            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.S){
                                bluetoothViewModel.scanDevice(activity)
                            }
                        }
                    ) {
                        Text(text = "Scan Device")
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    Log.d("BluetoothScreen", "Devices: ${devices.size}")

                    devices.forEach { device ->
                        DeviceItem(device = device, onClick = {
                            if(ActivityCompat.checkSelfPermission(
                                activity,
                                android.Manifest.permission.BLUETOOTH_CONNECT
                            )==PackageManager.PERMISSION_GRANTED){
                                bluetoothViewModel.connectToDevice(device)
                            }else{
                                requestPermissionLauncher.launch(
                                    arrayOf(android.Manifest.permission.BLUETOOTH_CONNECT)
                                )
                            }
                        })
                    }

                    connectedDevice?.let {
                        Text(text = "Connected to: ${it.name}")
                    }
                } else {
                    Text(text = "Permissions not granted.")
                }
            }
        }
    )
}

@Composable
fun DeviceItem(device: BluetoothDevice, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (ActivityCompat.checkSelfPermission(
                LocalContext.current as Context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        Text(text = device.name ?: "Unknown Device")
        Text(text = device.address)
    }
}
