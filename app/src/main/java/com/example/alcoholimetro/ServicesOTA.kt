package com.example.alcoholimetro

import android.bluetooth.*
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.alcoholimetro.adapt.ServiceAdapter

class ServicesOTA : AppCompatActivity(), ServiceAdapter.OnItemClickListener{
    private var actionBar : ActionBar? = null
    lateinit var recyclerView : RecyclerView
    private var gattServiceList = arrayListOf<BluetoothGattService>()
    private lateinit var bluetoothDevice : BluetoothDevice
    private lateinit var context: Context
    private lateinit var listener: ServiceAdapter.OnItemClickListener
    private lateinit var mProgressBar: ProgressBar

    companion object {
        lateinit var bluetoothGatt: BluetoothGatt
        lateinit var bluetoothGattCallback: BluetoothGattCallback
        lateinit var serviceAdapter: ServiceAdapter
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.services_ota)

        context = applicationContext
        listener = this
        bluetoothDevice = MainActivityOTA.bluetoothDevice

        actionBar = supportActionBar
        MainActivity.Companion.ActionBarStyle.changeActionBarColor(actionBar!!)

        mProgressBar = findViewById(R.id.progressbar2)
        recyclerView = findViewById(R.id.recycler2)
        recyclerView.layoutManager = LinearLayoutManager(context)


        mProgressBar.visibility = View.GONE
        progressBarAction()

        connectDevice()

    }
    override fun onItemClick(position: Int) {
        val intent = Intent(this, ChatacteristicsOTA::class.java)
        intent.putExtra("position", position)
        startActivity(intent)
    }

    private fun connectDevice(){
        bluetoothGattCallback = object : BluetoothGattCallback(){
            override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
                super.onConnectionStateChange(gatt, status, newState)
                when {
                    status == BluetoothGatt.GATT_SUCCESS -> {
                        Log.d(":::", "Conectado a ${gatt?.device?.address}")
                        gatt?.discoverServices()
                    }
                    newState == BluetoothProfile.STATE_DISCONNECTED -> {
                        Log.d(":::", "Desconectado de ${gatt?.device?.address}")
                        gatt?.close()
                    }
                    else -> {
                        Log.d(":::", "Error $status encontrado con ${gatt?.device?.address}. Desconectando...")
                        gatt?.close()
                    }
                }

            }
            override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
                super.onServicesDiscovered(gatt, status)
                gattServiceList.addAll(bluetoothGatt.services)
                if (gattServiceList.isEmpty()){
                    Log.d(":::", "No se han detectado servicios")
                } else {
                    Log.d(":::", "Se han detectado servicios")
                    serviceAdapter = ServiceAdapter(listener, gattServiceList)
                    serviceAdapter.setData(gattServiceList)
                    runOnUiThread {
                        recyclerView.adapter = serviceAdapter
                        recyclerView.setHasFixedSize(true)
                        recyclerView.layoutManager = LinearLayoutManager(context)
                        serviceAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
        runOnUiThread {
            bluetoothGatt = bluetoothDevice.connectGatt(applicationContext, false, bluetoothGattCallback)
        }
        Toast.makeText(applicationContext, "CONECTADO", Toast.LENGTH_SHORT).show()
    }

    private fun progressBarAction(){
        mProgressBar.visibility = View.VISIBLE
        Handler().postDelayed({
            mProgressBar.visibility = View.GONE
        }, 1500)
    }
}