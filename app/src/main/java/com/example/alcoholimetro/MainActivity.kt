package com.example.alcoholimetro

import android.Manifest
import android.bluetooth.*
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity(), View.OnClickListener {

    var actionBar: ActionBar? = null
    private lateinit var mProgressBar: ProgressBar
    private lateinit var name: TextView
    private lateinit var mac: TextView
    var colorDrawable: ColorDrawable? = null
    private lateinit var scanButton: Button
    private lateinit var connectButton: Button
    val MY_PERMISSIONS_REQUEST_LOCATION = 99
    private val REQUEST_ENABLE_BT = 1
    private lateinit var bluetoothLeScanner: BluetoothLeScanner
    private var scanning = false
    private lateinit var mLeScanCallback : ScanCallback
    private lateinit var bluetoothManager : BluetoothManager
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var scanResult : ScanResult

    companion object {
        lateinit var bluetoothDevice: BluetoothDevice
    }



    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        scanButton = findViewById(R.id.scanButton)
        connectButton = findViewById(R.id.connectButton)
        mProgressBar = findViewById(R.id.progressbar)
        name = findViewById(R.id.name)
        mac = findViewById(R.id.mac)

        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            MY_PERMISSIONS_REQUEST_LOCATION
        )

        actionBar = supportActionBar
        colorDrawable = ColorDrawable(Color.parseColor("#cfff95"))
        actionBar!!.setBackgroundDrawable(colorDrawable)
        mProgressBar.visibility = View.GONE

        bluetoothManager = getSystemService(BluetoothManager::class.java)
        bluetoothAdapter = bluetoothManager.adapter


        scanButton.setOnClickListener(this)
        connectButton.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.scanButton ->{
                if (checkBluetoothConnectivity()) {
                    bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
                    progressBarAction()
                    searchDevice()
                    connectButton.visibility = View.VISIBLE
                }
            }
            R.id.connectButton -> {
                getServices()
            }
        }
    }

    private fun checkBluetoothConnectivity() : Boolean {
        if(!bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            return false
        }
        return true
    }

    private fun progressBarAction(){
        mProgressBar.visibility = View.VISIBLE
        Handler().postDelayed({
            mProgressBar.visibility = View.GONE
        }, 2000)
    }

    private fun searchDevice() {
        mLeScanCallback = object : ScanCallback(){
            override fun onScanFailed(errorCode: Int) {
                super.onScanFailed(errorCode)
                Log.e(":::", "ERROR: $errorCode")
            }

            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                super.onScanResult(callbackType, result)
                if (result?.device?.name == "Eustaquio_H"){
                    bluetoothDevice = bluetoothAdapter.getRemoteDevice(result?.device?.address)
//                    for (i in result.scanRecord?.bytes?.indices!!)
//                        Log.d(":::", String.format("Byte %d: %02X", i, result.scanRecord?.bytes!![i]))
                    name.text = bluetoothDevice.name
                    mac.text = bluetoothDevice.address

                    scanResult = result
                }
            }
        }
        scanLeDevice()
    }

    private fun scanLeDevice() {
        val SCAN_PERIOD = 2000L
        bluetoothLeScanner?.let { scanner ->
            if (!scanning) {
                Handler().postDelayed({
                    scanning = false
                    scanner.stopScan(mLeScanCallback)
                }, SCAN_PERIOD)
                scanning = true
                scanner.startScan(mLeScanCallback)
            } else {
                scanning = false
                scanner.stopScan(mLeScanCallback)
            }
        }
    }

    private fun getServices() {
        val intent = Intent(this, Services::class.java)
        startActivity(intent)
    }

}
