package com.example.alcoholimetro

import android.app.Dialog
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.alcoholimetro.adapt.ServiceAdapter
import com.example.alcoholimetro.adapt.CharacteristicAdapter

import java.util.*

class Characteristics : AppCompatActivity(), CharacteristicAdapter.OnItemClickListener {

    private lateinit var recyclerView : RecyclerView
    private lateinit var characteristicAdapter: CharacteristicAdapter
    private var listOfCharacteristicMap = ServiceAdapter.listOfCharacteristicMap
    private var pos: Int = 0
    private lateinit var listView : ListView

    private lateinit var bluetoothGatt : BluetoothGatt
    private lateinit var bluetoothDevice: BluetoothDevice
    private lateinit var readCharacteristic: BluetoothGattCharacteristic
    private lateinit var id : String
    private lateinit var list : MutableList<BluetoothGattCharacteristic>

    companion object {
        lateinit var messageList : ArrayList<String>
        lateinit var adapter : ArrayAdapter<String>
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.characteristics)

        recyclerView = findViewById(R.id.recycler2)
        listView = findViewById(R.id.listView)
        bluetoothGatt = Services.bluetoothGatt
        bluetoothDevice = MainActivity.bluetoothDevice

        messageList = arrayListOf()
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, messageList)
        listView.adapter = adapter

        recyclerView.layoutManager = LinearLayoutManager(this)
        var pos = this.intent.extras?.get("position") as Int
        getCharacteristics()
        list = listOfCharacteristicMap[pos][pos]!!
        characteristicAdapter.setData(list)

    }

    override fun onItemClick(position: Int) {
//        val id = list[position].uuid.toString().split("-")[0].substringAfter("0000").toUpperCase()
        val writeCharacteristic = list[position] //FFA2
        readCharacteristic = list[position + 1] //FFA1

        buildDialog(writeCharacteristic)
//        adapter.notifyDataSetChanged()
    }



    private fun getMessage(characteristic: BluetoothGattCharacteristic) {
        val SERVICE_UUID = UUID.fromString("0000ffa0-0000-1000-8000-00805f9b34fb")
        val CHAR_UUID = UUID.fromString("0000ffa1-0000-1000-8000-00805f9b34fb")
        val DESCRIPTOR_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
        val char = bluetoothGatt.getService(SERVICE_UUID).getCharacteristic(CHAR_UUID)
        if (characteristic.properties != 0 && BluetoothGattCharacteristic.PROPERTY_INDICATE != 0) {
            bluetoothGatt.setCharacteristicNotification(characteristic, true)
        }
        if (characteristic.isNotifiable()) {
            val descriptor = characteristic.getDescriptor(DESCRIPTOR_UUID)
            descriptor.apply {
                value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            }
            bluetoothGatt.writeDescriptor(descriptor)
            bluetoothGatt.readCharacteristic(characteristic)
        }
    }

    private fun buildDialog(characteristic : BluetoothGattCharacteristic) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.connect_dialog)
        var sendButton = dialog.findViewById<Button>(R.id.sendButton)
        var cancelButton = dialog.findViewById<Button>(R.id.cancelButton)
        var acquisitionButton = dialog.findViewById<Button>(R.id.acquisitionButton)
        var calibrationButton = dialog.findViewById<Button>(R.id.calibrationButton)
        var cal1Button = dialog.findViewById<Button>(R.id.cal1Button)
        var cal2Button = dialog.findViewById<Button>(R.id.cal2Button)
        var message1 = dialog.findViewById<EditText>(R.id.message1)
        var x = dialog.findViewById<TextView>(R.id.x)
        var message2 = dialog.findViewById<EditText>(R.id.message2)
        var message3 = dialog.findViewById<EditText>(R.id.message3)


        var string = ""

        acquisitionButton.setOnClickListener{
            x.text = "0x"
            message2.visibility = View.GONE
            message3.visibility = View.GONE
            cal1Button.visibility = View.GONE
            cal2Button.visibility = View.GONE
            string = "02020D81A00E"
            message1.setText(string)
            Log.d(":::", "Pulsado adquisición")
        }

        calibrationButton.setOnClickListener{
            message2.setText("0")
            message2.visibility = View.VISIBLE
            message3.visibility = View.VISIBLE
            cal1Button.visibility = View.VISIBLE
            cal2Button.visibility = View.VISIBLE
            message3.hint = "0.25"
            string = "02020DA1B00E"
            message1.setText(string)
            Log.d(":::", "Pulsado calibración")
        }
        sendButton.setOnClickListener {
            characteristic.value = string.decodeHex()

            bluetoothGatt.writeCharacteristic(characteristic)
            Toast.makeText(applicationContext, "MENSAJE ENVIADO", Toast.LENGTH_SHORT).show()

            getMessage(readCharacteristic)
        }

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }
        cal1Button.setOnClickListener {
            val firstValue = Integer.parseInt(message2.text.toString()) * 100
            val checkSum = 4 + 161 + 1 + firstValue
            characteristic.value = "0202A101${firstValue.toString(16)}${checkSum.toString(16)}0E".decodeHex()
            bluetoothGatt.writeCharacteristic(characteristic)
            Log.d(":::", "Mensaje de calibración 1")
        }

        cal2Button.setOnClickListener{
            val secondValue = Integer.parseInt(message3.text.toString()) * 100
            val checkSum = 4 + 161 + 1 + secondValue
            characteristic.value = "0202A101${secondValue.toString(16)}${checkSum.toString(16)}0E".decodeHex()
            bluetoothGatt.writeCharacteristic(characteristic)
            Log.d(":::", "Mensaje de calibración 2")
        }
        dialog.show()
    }

    private fun getCharacteristics(){
        characteristicAdapter = CharacteristicAdapter(this)
        recyclerView.adapter = characteristicAdapter
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun String.decodeHex(): ByteArray = chunked(2)
            .map { it.toInt(16).toByte() }
            .toByteArray()

    private fun decimalToHexadecimal(number : Int) : String {
        return number.toString(16).toUpperCase()
    }
}

fun BluetoothGattCharacteristic.isNotifiable(): Boolean =
        containsProperty(BluetoothGattCharacteristic.PROPERTY_NOTIFY)

fun BluetoothGattCharacteristic.containsProperty(property: Int): Boolean {
    return properties and property != 0
}
