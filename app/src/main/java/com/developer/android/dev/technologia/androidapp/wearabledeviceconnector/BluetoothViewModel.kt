package com.developer.android.dev.technologia.androidapp.wearabledeviceconnector

import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BluetoothViewModel:ViewModel() {
    private val _devices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val devices:StateFlow<List<BluetoothDevice>> = _devices

    private val _connectedDevice = MutableStateFlow<BluetoothDevice?>(null)
    val connectedDevice:StateFlow<BluetoothDevice?> = _connectedDevice

    private val _connectionStatus = MutableStateFlow("Disconnected")
    val connectionStatus:StateFlow<String> = _connectionStatus

    private val _permissionsGranted = MutableStateFlow(false)
    val permissionsGranted:StateFlow<Boolean> = _permissionsGranted

    private lateinit var bluetoothManager: BluetoothManager

    @RequiresApi(Build.VERSION_CODES.S)
    fun initialize(context: Context){
        bluetoothManager = BluetoothManager(context)

        if(bluetoothManager.isBluetoothSupported()){
            _permissionsGranted.value = bluetoothManager.hasPermission()
        }else{
            Toast.makeText(context,"Bluetooth not supported",Toast.LENGTH_SHORT).show()
        }
    }

    fun updatePermissionsGranted(isGranted: Boolean) {
        _permissionsGranted.value = isGranted
    }


    @RequiresApi(Build.VERSION_CODES.S)
    fun requestPermission(activity: Activity, requestCode:Int){
        bluetoothManager.requestPermission(activity,requestCode)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun scanDevice(activity: Activity){
        bluetoothManager.scanDevices({device->
            viewModelScope.launch {
                _devices.value += device
            }
        },activity)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun connectToDevice(device: BluetoothDevice){
        bluetoothManager.connectToDevice(device){gatt->
            viewModelScope.launch {
                _connectedDevice.value = device
                _connectionStatus.value = "Connected"
            }
        }
    }

    fun disconnect(){
        bluetoothManager.disconnect()
        _connectedDevice.value = null
        _connectionStatus.value = "Disconnected"
    }


    @RequiresApi(Build.VERSION_CODES.S)
    fun ensureBluetoothPermissions(activity: Activity, onPermissionsResult: (Boolean) -> Unit) {
        if (hasBluetoothPermissions(activity)) {
            onPermissionsResult(true)
        } else {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(
                    android.Manifest.permission.BLUETOOTH,
                    android.Manifest.permission.BLUETOOTH_ADMIN,
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.BLUETOOTH_CONNECT,
                    android.Manifest.permission.BLUETOOTH_SCAN
                ),
                BluetoothViewModel.REQUEST_BLUETOOTH_SCAN_PERMISSION
            )
            onPermissionsResult(false)
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun hasBluetoothPermissions(activity: Activity): Boolean {
        return ContextCompat.checkSelfPermission(activity, android.Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(activity, android.Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(activity, android.Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(activity, android.Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        const val REQUEST_BLUETOOTH_SCAN_PERMISSION = 1
    }


}