package com.example.alcoholimetro

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattService
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Services : AppCompatActivity(), ServiceAdapter.OnItemClickListener{
    lateinit var recyclerView : RecyclerView
    lateinit var serviceAdapter: ServiceAdapter
    private var gattServiceList = arrayListOf<BluetoothGattService>()
    private lateinit var bluetoothGatt: BluetoothGatt


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.services)

        recyclerView = findViewById(R.id.recycler)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)

        serviceAdapter = ServiceAdapter(this)

        Handler().post {
            bluetoothGatt = MainActivity.bluetoothGatt
            gattServiceList = MainActivity.gattServiceList
            Log.d(":::SERVICES", MainActivity.bluetoothGatt.toString())
            Log.d(":::SERVICES", MainActivity.gattServiceList.toString())
        }


//        Log.d(":::", (bluetoothGatt == null).toString())


        if (gattServiceList.isEmpty()){
            Log.d(":::", "NO SE HAN DETECTADO SERVICIOS")
        } else {
            serviceAdapter.setData(gattServiceList)
            recyclerView.adapter = serviceAdapter
            serviceAdapter.notifyDataSetChanged()
        }

    }
    override fun onItemClick(position: Int) {

    }
}