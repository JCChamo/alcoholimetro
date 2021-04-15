package com.example.alcoholimetro

import android.bluetooth.*
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Services : AppCompatActivity(), ServiceAdapter.OnItemClickListener{
    lateinit var recyclerView : RecyclerView
    lateinit var serviceAdapter: ServiceAdapter
    private var gattServiceList = arrayListOf<BluetoothGattService>()
    private lateinit var bluetoothDevice : BluetoothDevice
    private lateinit var bluetoothGatt: BluetoothGatt
    private lateinit var bluetoothGattCallback: BluetoothGattCallback
    private lateinit var context: Context
    private lateinit var listener: ServiceAdapter.OnItemClickListener
    private lateinit var mProgressBar: ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.services)

        context = applicationContext
        listener = this
        bluetoothDevice = MainActivity.bluetoothDevice

        mProgressBar = findViewById(R.id.progressbar2)
        recyclerView = findViewById(R.id.recycler)
        recyclerView.layoutManager = LinearLayoutManager(context)

        mProgressBar.visibility = View.GONE
        progressBarAction()


        connectDevice()

        var adapter = ArrayAdapter<BluetoothGattCharacteristic>(applicationContext, R.layout.spinner_list, )

    }
    override fun onItemClick(position: Int) {

    }

    private fun connectDevice(){
        bluetoothGattCallback = object : BluetoothGattCallback(){
            override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
                super.onConnectionStateChange(gatt, status, newState)
                when {
                    status == BluetoothGatt.GATT_SUCCESS -> {
                        Log.d(":::", "Conectado a ${gatt?.device?.name}")
                        gatt?.discoverServices()

                    }
                    newState == BluetoothProfile.STATE_DISCONNECTED -> {
                        Log.d(":::", "Desconectado de ${gatt?.device?.name}")
                        gatt?.close()
                    }
                    else -> {
                        Log.d(":::", "Error $status encontrado con ${gatt?.device?.name}. Desconectando...")
                        gatt?.close()
                    }
                }

            }
            override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
                super.onServicesDiscovered(gatt, status)
                gattServiceList.addAll(bluetoothGatt.services)
                if (gattServiceList.isEmpty()){
                    Log.d(":::", "NO SE HAN DETECTADO SERVICIOS")
                } else {
                    serviceAdapter = ServiceAdapter(listener)
                    serviceAdapter.setData(gattServiceList)

                    for (i in 0 until gattServiceList.size)
                        Log.d(":::", gattServiceList[i].uuid.toString())
                    runOnUiThread {
                        recyclerView.adapter = serviceAdapter
                        recyclerView.setHasFixedSize(true)
                        recyclerView.layoutManager = LinearLayoutManager(context)
                        serviceAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
        bluetoothGatt = bluetoothDevice.connectGatt(applicationContext, false, bluetoothGattCallback)

        Toast.makeText(applicationContext, "CONECTADO", Toast.LENGTH_SHORT).show()
    }

    private fun progressBarAction(){
        mProgressBar.visibility = View.VISIBLE
        Handler().postDelayed({
            mProgressBar.visibility = View.GONE
        }, 1500)
    }
}