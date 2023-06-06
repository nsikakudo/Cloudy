package com.project.cloudy.ui

import android.annotation.SuppressLint
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.project.cloudy.databinding.FragmentSavedWeatherBinding
import com.project.cloudy.ui.adapters.SavedWeatherAdapter
import com.project.cloudy.utils.Resource
import com.project.cloudy.isConnected

@SuppressLint("MissingPermission")
class SavedWeatherFragment : Fragment() {
    private var _binding: FragmentSavedWeatherBinding? = null
    private val binding get() = _binding!!
    private val viewModel: WeatherViewModel by activityViewModels()
    private lateinit var networkManager: ConnectivityManager
    private val adapter: SavedWeatherAdapter by lazy { SavedWeatherAdapter() }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSavedWeatherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        networkManager =
            activity?.getSystemService(ConnectivityManager::class.java) as ConnectivityManager

        binding.RvForcast.adapter = adapter
        binding.RvForcast.setHasFixedSize(true)
        wholeLogic()
        adapter.adapterClick {
            viewModel.deleteSavedWeather(it).also { adapter.submitList(emptyList()) }
            wholeLogic()
        }

    }
    private fun wholeLogic(){
        if (isConnected(networkManager)) {
            onlineLogic()
        } else {
            offlineLogic()
        }
    }

    private fun onlineLogic() {
        viewModel.doOnlineSavedOperation()
        viewModel.onlineSavedWeatherResultTest.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Successful -> {
                    binding.progressBar.isVisible = false
                    adapter.submitList(response.data)
                }
                is Resource.Failure -> {
                    binding.progressBar.isVisible = false
                }
                is Resource.Loading -> {
                }
                else -> Unit
            }
        }
    }

    private fun offlineLogic() {
        viewModel.reportSavedDbSitu()
        viewModel.offLineSavedWeatherResultTest.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Successful -> {
                    adapter.submitList(response.data)
                    binding.progressBar.isVisible = false
                }
                is Resource.Failure -> {
                    binding.progressBar.isVisible = false
                    val text = "${response.msg}, check network and refresh"
                }
                is Resource.Loading -> {
                }
                else-> Unit
            }
        }
    }
}