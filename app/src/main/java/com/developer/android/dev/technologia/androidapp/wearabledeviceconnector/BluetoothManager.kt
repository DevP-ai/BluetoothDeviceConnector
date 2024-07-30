package com.developer.android.dev.technologia.androidapp.wearabledeviceconnector

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

@SuppressLint("ServiceCast")
class BluetoothManager(private val context: Context){
    private val bluetoothAdapter: BluetoothAdapter by lazy {
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as android.bluetooth.BluetoothManager
        bluetoothManager.adapter
    }

    private var bluetoothGatt : BluetoothGatt?=null

    fun isBluetoothSupported():Boolean{
        return bluetoothAdapter != null
    }

    fun isBluetoothEnabled():Boolean{
        return bluetoothAdapter.isEnabled
    }


    @RequiresApi(Build.VERSION_CODES.S)
    fun hasPermission(): Boolean {
        return ContextCompat.checkSelfPermission(context,android.Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context,android.Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context,android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context,android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context,android.Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context,android.Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun requestPermission(activity: Activity, requestCode:Int){
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
            requestCode
        )
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun scanDevices(callback: (BluetoothDevice) -> Unit, activity: Activity) {
        if(ActivityCompat.checkSelfPermission(activity,android.Manifest.permission.BLUETOOTH)==PackageManager.PERMISSION_GRANTED){
            val leScanCallback = BluetoothAdapter.LeScanCallback { device, _, _ ->
                device?.let { callback(it) }
            }

            val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            bluetoothAdapter.startLeScan(leScanCallback)
            Handler(Looper.getMainLooper()).postDelayed({
                bluetoothAdapter.stopLeScan(leScanCallback)
            },1000)
        }else{
            ActivityCompat.requestPermissions(activity, arrayOf(android.Manifest.permission.BLUETOOTH_SCAN), REQUEST_BLUETOOTH_SCAN_PERMISSION)
        }

    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun connectToDevice(device: BluetoothDevice,callback: (BluetoothGatt) -> Unit){
        if(ActivityCompat.checkSelfPermission(context,android.Manifest.permission.BLUETOOTH)==PackageManager.PERMISSION_GRANTED){
            bluetoothGatt = device.connectGatt(context,false,object :BluetoothGattCallback(){

                override fun onConnectionStateChange(
                    gatt: BluetoothGatt?,
                    status: Int,
                    newState: Int
                ) {
                    super.onConnectionStateChange(gatt, status, newState)
                }

                override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
                    super.onServicesDiscovered(gatt, status)
                }

                override fun onCharacteristicRead(
                    gatt: BluetoothGatt,
                    characteristic: BluetoothGattCharacteristic,
                    value: ByteArray,
                    status: Int
                ) {
                    super.onCharacteristicRead(gatt, characteristic, value, status)
                }



            })
        }else{
            ActivityCompat.requestPermissions(context as Activity, arrayOf(android.Manifest.permission.BLUETOOTH_SCAN), REQUEST_BLUETOOTH_SCAN_PERMISSION)
        }

    }

    fun disconnect(){
        if(ActivityCompat.checkSelfPermission(context as Activity,android.Manifest.permission.BLUETOOTH)==PackageManager.PERMISSION_GRANTED){
            bluetoothGatt?.close()
            bluetoothGatt = null
        }
    }


    companion object {
        const val REQUEST_BLUETOOTH_SCAN_PERMISSION = 1
    }
}

