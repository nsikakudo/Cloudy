package com.project.cloudy.ui

import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.project.cloudy.R
import com.project.cloudy.databinding.FragmentForecastDetailBinding
import com.project.cloudy.ui.adapters.ForecastAdapter
import com.project.cloudy.utils.Resource
import com.tutorial.weatheria.isConnected
import com.tutorial.weatheria.makeToast
import com.tutorial.weatheria.ui.adapters.DailyForecastAdapter

class ForecastWeatherDetailFragment : Fragment() {
    private var _binding: FragmentForecastDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: WeatherViewModel by activityViewModels()
    private val weeklyAdapter: ForecastAdapter by lazy { ForecastAdapter() }
    private val dailyAdapter: DailyForecastAdapter by lazy { DailyForecastAdapter() }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentForecastDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val networkManager =
            activity?.getSystemService(ConnectivityManager::class.java) as ConnectivityManager
        binding.dailyForecastRV.adapter = dailyAdapter
        binding.dailyForecastRV.setHasFixedSize(true)

        binding.weeklyForecastRV.adapter = weeklyAdapter
        binding.weeklyForecastRV.setHasFixedSize(true)


        if (isConnected(networkManager)) {
            onlineLogic()

        } else {
            offlineLogic()
        }

    }

    private fun onlineLogic() {
        viewModel.weatherForecast.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Successful -> {
                    binding.errorStateText.isVisible = false
                    dailyAdapter.submitList(response.data?.forecast?.forecastday?.get(0)?.hour)
                    weeklyAdapter.submitList(response.data?.forecast?.forecastday)
                }
                is Resource.Failure -> {
                    binding.errorStateText.isVisible = true
                    binding.errorStateText.text = response.msg
                }
                is Resource.Empty -> {
                    binding.errorStateText.isVisible = true
                    binding.errorStateText.text = "LIST IS EMPTY"
                }
                else -> Unit
            }
        }

    }

    private fun offlineLogic() {
        viewModel.reportDbSitu()
        viewModel.situFrmDab.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Successful -> {
                    binding.errorStateText.isVisible = false
                    dailyAdapter.submitList(response.data?.forecast?.forecastday?.get(0)?.hour)
                    weeklyAdapter.submitList(response.data?.forecast?.forecastday)
                }
                is Resource.Failure -> {
                    binding.errorStateText.isVisible = true
                    val text = "${response.msg}, check network and refresh"
                    makeToast(text)
                }
                is Resource.Loading -> {
                    binding.errorStateText.isVisible = true
                    val text = "Please wait a moment..."
                    makeToast(text)
                }
                else -> Unit
            }
        }
    }

}
