package com.example.alcoholimetro

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.collections.ArrayList

class ServiceAdapter (var listener: OnItemClickListener) : RecyclerView.Adapter<ServiceAdapter.ViewHolder>() {

    private lateinit var serviceList : ArrayList<BluetoothGattService>
    private lateinit var characteristicList : MutableList<BluetoothGattCharacteristic>


    inner class ViewHolder (view : View) : RecyclerView.ViewHolder(view), View.OnClickListener{

        val serviceName : TextView = view.findViewById(R.id.name)
        val serviceUuid : TextView = view.findViewById(R.id.uuid)
        val servicePriority : TextView = view.findViewById(R.id.priority)

        val characteristicName : TextView = view.findViewById(R.id.name2)
        val characteristicUuid : TextView = view.findViewById(R.id.uuid2)
        val characteristicProperties : TextView = view.findViewById(R.id.properties)


        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION)
                listener.onItemClick(position)
        }

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.service, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = serviceList.size

    override fun onBindViewHolder(holder: ServiceAdapter.ViewHolder, position: Int) {
        val service = serviceList[position]
        val serviceNameCodeTokens = service.uuid.toString().split('-')
        val serviceName = serviceNameCodeTokens[0].substringAfter("0000").toUpperCase()
        when(serviceName){
            "1800" -> holder.serviceName.text = "Acceso Genérico"
            "1801" -> holder.serviceName.text = "Atributo Genérico"
            "180D" -> holder.serviceName.text = "Ritmo cardiaco"
            "180F" -> holder.serviceName.text = "Servicio de Batería"
            "181C" -> holder.serviceName.text = "Datos de Usuario"
            else -> holder.serviceName.text = "Servicio Desconocido"
        }
        holder.serviceUuid.append(Html.fromHtml("<b><font color=#000>0x$serviceName</b>"))
        holder.servicePriority.text = "SERVICIO PRIMARIO"
        characteristicList = service.characteristics.toMutableList()
        Log.d(":::", characteristicList.size.toString())
        characteristicList[position]
    }

    fun setData(serviceList : ArrayList<BluetoothGattService>) {
        this.serviceList = serviceList
        notifyDataSetChanged()
    }

    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }

}