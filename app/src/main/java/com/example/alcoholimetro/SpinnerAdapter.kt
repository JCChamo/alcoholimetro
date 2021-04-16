package com.example.alcoholimetro

import android.bluetooth.BluetoothGattCharacteristic
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class SpinnerAdapter(context: Context, resource: Int, list: MutableList<BluetoothGattCharacteristic>) : ArrayAdapter<BluetoothGattCharacteristic>(context, resource, list), View.OnClickListener {

    private lateinit var list : MutableList<BluetoothGattCharacteristic>

    inner class ViewHolder (view : View) {
        val characteristicName : TextView = view.findViewById(R.id.name)
        val characteristicUuid : TextView = view.findViewById(R.id.uuid)
        val characteristicProperties : TextView = view.findViewById(R.id.priority)
    }

    override fun getCount(): Int = list.size

    override fun getItem(position: Int): BluetoothGattCharacteristic? {
        return list[position]
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var viewHolder = ViewHolder(convertView as View)

        viewHolder.characteristicUuid.text = list[position].uuid.toString().substringAfter("0000").toUpperCase()
        viewHolder.characteristicName.text = "Característica desconocida"
        viewHolder.characteristicProperties.text = list[position].properties.toString()
        return convertView
    }

    override fun onClick(p0: View?) {
        TODO("Not yet implemented")
    }


}