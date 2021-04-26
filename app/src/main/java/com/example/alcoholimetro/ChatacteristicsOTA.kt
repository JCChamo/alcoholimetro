package com.example.alcoholimetro


import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.alcoholimetro.ServicesOTA.Companion.serviceAdapter
import com.example.alcoholimetro.adapt.CharacteristicAdapter
import com.example.alcoholimetro.adapt.ServiceAdapter
import java.util.*


class ChatacteristicsOTA : AppCompatActivity(), CharacteristicAdapter.OnItemClickListener {

    private var actionBar : ActionBar? = null
    private lateinit var recyclerView : RecyclerView
    private lateinit var characteristicAdapter: CharacteristicAdapter
    private var listOfCharacteristicMap = ServiceAdapter.listOfCharacteristicMap
    private lateinit var listView : ListView

    private lateinit var bluetoothGatt : BluetoothGatt
    private lateinit var list : MutableList<BluetoothGattCharacteristic>

    companion object {
        lateinit var messageList : ArrayList<String>
        lateinit var adapter : ArrayAdapter<String>
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.characteristics_ota)

        recyclerView = findViewById(R.id.recycler4)
        listView = findViewById(R.id.listView2)
        bluetoothGatt = ServicesOTA.bluetoothGatt

        actionBar = supportActionBar
        MainActivity.Companion.ActionBarStyle.changeActionBarColor(actionBar!!)

        messageList = arrayListOf()
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, messageList)
        listView.adapter = adapter

        recyclerView.layoutManager = LinearLayoutManager(this)
        var pos = this.intent.extras?.get("position") as Int
        Log.d(":::OTA", pos.toString())
        getCharacteristics()
        list = listOfCharacteristicMap[pos][pos]!!
        characteristicAdapter.setData(list)

    }


    override fun onItemClick(position: Int) {
        val characteristic = list[position]
        Log.d(":::CHAROTA", position.toString())

        if(characteristic.properties == 4){
            val intent = Intent(this, UploadZip::class.java)
            intent.putExtra("characteristic", characteristic)
            startActivity(intent)
        }
    }

    private fun getCharacteristics(){
        characteristicAdapter = CharacteristicAdapter(this)
        recyclerView.adapter = characteristicAdapter
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
    }
}
