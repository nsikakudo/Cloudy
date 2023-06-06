package com.project.cloudy.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.project.cloudy.R
import com.project.cloudy.databinding.WeeklyForecastViewHolderBinding
import com.project.cloudy.model.Forecastday
import java.text.SimpleDateFormat
import java.util.*

class ForecastAdapter : ListAdapter<Forecastday, ForecastAdapter.ViewHolder>(diffObject) {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = WeeklyForecastViewHolderBinding.bind(view)
        fun bind(forecastday: Forecastday) {
            binding.apply {
                weathericon.load("http:${forecastday.day.condition.icon}") {
                    error(R.drawable.ic_launcher_background)
                    placeholder(R.drawable.ic_launcher_foreground)
                }


                val dateFormat = SimpleDateFormat("yy-MM-dd", Locale.getDefault())
                val text = dateFormat.parse(forecastday.date)
                val dateIndex = text.toString().split(" ")
                tempC.text = "${forecastday.day.avgtempC}℃"
                tempF.text = forecastday.day.condition.text
                dateTV.text = dateIndex[0]
                Log.d("date", "${dateIndex[0]..dateIndex[2]}")
                Log.d("date", "$dateIndex")
                // forecastday.date.. + "${dateIndex[0]..dateIndex[2]}"
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.weekly_forecast_view_holder, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pos = getItem(position)
        if (pos != null)
            holder.bind(pos)
    }

    companion object {
        val diffObject = object : DiffUtil.ItemCallback<Forecastday>() {
            override fun areItemsTheSame(oldItem: Forecastday, newItem: Forecastday): Boolean {
                return oldItem.date == newItem.date
            }

            override fun areContentsTheSame(oldItem: Forecastday, newItem: Forecastday): Boolean {
                return oldItem.date == newItem.date
            }
        }
    }
}