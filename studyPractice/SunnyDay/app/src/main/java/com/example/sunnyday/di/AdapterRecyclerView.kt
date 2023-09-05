package com.example.sunnyday.di

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sunnyday.databinding.ItemCityTemperatureBinding
import com.example.sunnyday.entity.CityWeather
import com.google.android.material.shape.CornerFamily
import javax.inject.Inject

class AdapterRecyclerView  : RecyclerView.Adapter<WeatherViewHolder>() {
    private var data: List<CityWeather> = emptyList()
    fun setData(data: List<CityWeather>) {
        this.data = data
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
                date.text = item?.date
                weather.text = item?.temperature.toString()
                item?.let {
                Glide.with(icon.context)
                    .load(it.weatherUrl)
                    .into(icon)
            }
        }
    }

    override fun getItemCount(): Int = data.size
}

class WeatherViewHolder @Inject constructor(val binding: ItemCityTemperatureBinding) :
    RecyclerView.ViewHolder(binding.root)