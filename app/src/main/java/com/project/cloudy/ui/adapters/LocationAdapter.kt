package com.project.cloudy.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.project.cloudy.R
import com.project.cloudy.databinding.SearchLocationViewHolderBinding
import com.project.cloudy.model.SearchLocationResponseItem

class LocationAdapter:ListAdapter<SearchLocationResponseItem, LocationAdapter.ViewHolder>(diffObject) {

    inner class ViewHolder(val binding: SearchLocationViewHolderBinding):RecyclerView.ViewHolder(binding.root) {

        fun bind(location: SearchLocationResponseItem){
            binding.nameTv.text = location.name
            binding.regionTv.text = location.region
            binding.countryTv.text = location.country
            binding.root.setOnClickListener{
                listener?.let { it1 -> it1(location) }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_location_view_holder,parent,false)
        val v = SearchLocationViewHolderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pos = getItem(position)
        if (pos != null)
            holder.bind(pos)
    }

    companion object {
        val diffObject = object : DiffUtil.ItemCallback<SearchLocationResponseItem>() {
            override fun areItemsTheSame(oldItem: SearchLocationResponseItem, newItem: SearchLocationResponseItem): Boolean {
                return oldItem.name == newItem.name
            }
            override fun areContentsTheSame(oldItem: SearchLocationResponseItem, newItem: SearchLocationResponseItem): Boolean {
                return oldItem.lat  == newItem.lat && oldItem.lon  == newItem.lon
            }
        }
    }

    private var listener:((SearchLocationResponseItem)->Unit)? = null
    fun adapterClick(listener:(SearchLocationResponseItem)->Unit){
        this.listener = listener
    }
}