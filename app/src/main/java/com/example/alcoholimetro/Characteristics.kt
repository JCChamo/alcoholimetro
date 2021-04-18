package com.example.alcoholimetro

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
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
        sendMessage()
    }

    private fun getCharacteristics(){
        characteristicAdapter = CharacteristicAdapter(this)
        recyclerView.adapter = characteristicAdapter
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun sendMessage() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.connect_dialog)
        var sendButton = dialog.findViewById<Button>(R.id.sendButton)
        var cancelButton = dialog.findViewById<Button>(R.id.cancelButton)
        var message = dialog.findViewById<EditText>(R.id.message)
        sendButton.setOnClickListener(View.OnClickListener {
            val message = message.text.toString()
            dialog.dismiss()
        })

        cancelButton.setOnClickListener(View.OnClickListener {
            dialog.dismiss()
        })
        dialog.show()
    }
}