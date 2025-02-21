package com.example.md2_lab01__mvvm

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

    class Adapter(diffCallback: DiffCallback,  private val viewModel: WeatherViewModel) : ListAdapter<WeatherList, Adapter.ViewHolder>(diffCallback) {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("DefaultLocale")
        fun bind(weatherList: WeatherList) {
            itemView.findViewById<TextView>(R.id.datetime).text = weatherList.dt_txt
            val tempCel = weatherList.main.temp - 273.15
            itemView.findViewById<TextView>(R.id.temp).text = String.format("%.2f °C", tempCel)
            val iconUrl = "https://openweathermap.org/img/wn/${weatherList.weather[0].icon}@2x.png"
            Glide.with(itemView.context)
                .load(iconUrl)
                .into(itemView.findViewById(R.id.temp_icon))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.rview_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val forecastItem = getItem(position)
        holder.bind(forecastItem)
    }
}

class DiffCallback : DiffUtil.ItemCallback<WeatherList>() {
    override fun areItemsTheSame(oldItem: WeatherList, newItem: WeatherList): Boolean {
        return oldItem.dt_txt == newItem.dt_txt
    }

    override fun areContentsTheSame(oldItem: WeatherList, newItem: WeatherList): Boolean {
        return oldItem == newItem
    }
}