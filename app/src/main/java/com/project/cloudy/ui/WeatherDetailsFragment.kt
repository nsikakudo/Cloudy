package com.project.cloudy.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import coil.load
import com.project.cloudy.databinding.FragmentWeatherDetailsBinding
import com.project.cloudy.ui.adapters.HourAdapter
import com.project.cloudy.utils.Resource
import com.project.cloudy.makeToast
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class WeatherDetailsFragment : Fragment() {
    private var _binding : FragmentWeatherDetailsBinding? = null
    private val binding get() = _binding!!
    private val args: WeatherDetailsFragmentArgs by navArgs()
    private val viewModel: WeatherViewModel by activityViewModels()
    private val adapter: HourAdapter by lazy { HourAdapter() }
    private lateinit var location: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentWeatherDetailsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        location =
            "${args.locationDetails.lat},${args.locationDetails.lon}" //or betterstill pass the name
        binding.recentsRv.adapter = adapter
        binding.recentsRv.setHasFixedSize(true)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.savingEvents.collect { event ->
                when (event) {
                    is WeatherViewModel.Events.Successful -> {
                        makeToast("Location Saved")
                    }
                    is WeatherViewModel.Events.Failure -> {
                        makeToast("Saved location size cannot exceed 5")
                    }
                }
            }
        }

        setUpUi(viewModel, location)
    }

    private fun setUpUi(viewModel: WeatherViewModel, location: String) {
        viewModel.updateWeatherSearchedLocation(location)

        viewModel.searchLocationWeatherResult.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Successful -> {
                    binding.progressBar.isVisible = false
                    var current = response.data?.current
                    binding.apply {
                        imgWeather.load("http:${current?.condition?.icon}")
                        degree.text = "${current?.tempC}°c"
                        cityName.text = response.data?.location?.name// current?.lastUpdated
                        dayTv.text = current?.condition?.text
                        val line = checkDateFormat(System.currentTimeMillis())
                        Log.d("TIMEING", "$line")
                        tempText.text = "${current?.tempC}°c"
                        humidityTitle.text = current?.humidity.toString()
                        windText.text = "${current?.windMph}Mph"
                        adapter.submitList(response.data?.forecast?.forecastday?.get(0)?.hour)
                        response.data?.forecast?.forecastday?.get(0)?.hour?.get(0)?.condition?.icon

                    }
                }
                is Resource.Failure -> {
                    binding.progressBar.isVisible = false
                    binding.cityName.text = response.msg
                }
                is Resource.Empty -> {
                    binding.progressBar.isVisible = false
                    binding.cityName.text = "LIST IS EMPTY"
                }
                is Resource.Loading->{
                    binding.progressBar.isVisible = true
                }
                else -> Unit
            }
        }
    }

    private fun checkDateFormat(time: Long): String {
        val dateFormat = SimpleDateFormat("yy-MM-dd hh:mm:ss", Locale.getDefault())
        return dateFormat.format(time)
    }
}