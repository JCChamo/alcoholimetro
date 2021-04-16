package com.example.alcoholimetro

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Characteristics : AppCompatActivity(), CharacteristicAdapter.OnItemClickListener {

    private lateinit var recyclerView : RecyclerView
    private lateinit var characteristicAdapter: CharacteristicAdapter
    private var listOfCharacteristicMap = ServiceAdapter.listOfCharacteristicMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.characteristics)

        recyclerView = findViewById(R.id.recycler2)
        recyclerView.layoutManager = LinearLayoutManager(this)
        var position = this.intent.extras?.get("position") as Int
        getCharacteristics()
        characteristicAdapter.setData(listOfCharacteristicMap[position][position]!!)

    }

    override fun onItemClick(position: Int) {
        Toast.makeText(applicationContext, "TOCADA CARACTERÍSTICA", Toast.LENGTH_SHORT).show()
        connectToWifi()
    }

    private fun getCharacteristics(){
        characteristicAdapter = CharacteristicAdapter(this)
        recyclerView.adapter = characteristicAdapter
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun connectToWifi() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.connect_dialog)
        var connectButton = dialog.findViewById<Button>(R.id.connectButton)
        var psswd = dialog.findViewById<EditText>(R.id.psswd)
        var ssidText = dialog.findViewById<TextView>(R.id.ssid)
        //psswd.setText(wifiPsswd);
        connectButton.setOnClickListener(View.OnClickListener {
            val checkpsswd: String = psswd.getText().toString()
            dialog.dismiss()
        })
        dialog.show()
    }
}