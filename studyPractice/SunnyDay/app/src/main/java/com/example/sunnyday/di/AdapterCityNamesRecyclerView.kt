package com.example.sunnyday.di

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sunnyday.databinding.ItemCityTemperatureBinding

class AdapterCityNamesRecyclerView: RecyclerView.Adapter<WeatherViewHolder>() {
    private var data: List<String> = emptyList()
    private lateinit var onClick: (String) -> Unit
    fun setData(data: List<String>, onClick: (String) -> Unit) {
        this.data = data
        this.onClick = onClick
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        return WeatherViewHolder(
            ItemCityTemperatureBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        val item = data.getOrNull(position)
        with(holder.binding) {
            date.text = item
        }
        holder.binding.root.setOnClickListener { item?.let { onClick(item) } }
    }

    override fun getItemCount(): Int = data.size
}

