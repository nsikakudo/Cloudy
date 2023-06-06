package com.project.cloudy.ui.adapters

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.project.cloudy.R
import com.project.cloudy.databinding.SaveWeatherViewHolderBinding
import com.project.cloudy.model.SavedWeather
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
class SavedWeatherAdapter : ListAdapter<SavedWeather, SavedWeatherAdapter.ViewHolder>(diffObject) {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = SaveWeatherViewHolderBinding.bind(view)
        fun bind(savedWeather: SavedWeather) {
            binding.apply {

                dateTV.text = savedWeather.current?.lastUpdated?.let { getDateFormat(it) }//savedWeather.current?.lastUpdated
                location.text = savedWeather.location?.name
                condition.text = savedWeather.current?.condition?.text
                tempC.text = "${savedWeather.current?.tempC}℃"
                weathericon.load("http://${savedWeather.current?.condition?.icon}") {
                    error(R.drawable.ic_launcher_background)
                    placeholder(R.drawable.loading_img)
                }

            }
            binding.root.setOnLongClickListener {
                listener?.invoke(savedWeather)
                true
            }

        }

    }

    companion object {
        val diffObject = object : DiffUtil.ItemCallback<SavedWeather>() {
            override fun areItemsTheSame(oldItem: SavedWeather, newItem: SavedWeather): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: SavedWeather, newItem: SavedWeather): Boolean {
                return oldItem.id == newItem.id && oldItem.location == newItem.location
            }
        }
    }

    private var listener: ((SavedWeather) -> Unit)? = null
    fun adapterClick(listener: (SavedWeather) -> Unit) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.save_weather_view_holder, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pos = getItem(position)
        if (pos != null)
            holder.bind(pos)
    }




    private  fun getDateFormat(date: String):String{
        val format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        val localDate = LocalDateTime.parse(date, format)

        val dateFormatter = DateTimeFormatter.ofPattern("EEE, MMM d", Locale.getDefault())
        return localDate.format(dateFormatter)
    }
}