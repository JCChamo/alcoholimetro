package com.example.alcoholimetro

import android.bluetooth.BluetoothGattService
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ServiceAdapter (var listener: OnItemClickListener) : RecyclerView.Adapter<ServiceAdapter.ViewHolder>() {

    private lateinit var serviceList : ArrayList<BluetoothGattService>


    inner class ViewHolder (view : View) : RecyclerView.ViewHolder(view), View.OnClickListener{

        val characteristic : TextView = view.findViewById(R.id.characteristic)
        val uuid : TextView = view.findViewById(R.id.uuid)
        val properties : TextView = view.findViewById(R.id.properties)


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
        holder.characteristic.text = service.characteristics.toString()
        holder.uuid.text = service.uuid.toString()
        holder.properties.text = service.type.toString()
    }

    fun setData(serviceList : ArrayList<BluetoothGattService> ) {
        this.serviceList = serviceList
        notifyDataSetChanged()
    }

    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }

}