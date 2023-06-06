package com.project.cloudy.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.project.cloudy.databinding.FragmentHomeBinding
import com.project.cloudy.utils.Resource
import com.project.cloudy.makeToast
import com.project.cloudy.ui.adapters.HourAdapter
import com.project.cloudy.isConnected
import com.project.cloudy.makeAlert
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("MissingPermission")
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: WeatherViewModel by activityViewModels()

    private val adapter: HourAdapter by lazy { HourAdapter() }
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var userLocation: String? = null
    private lateinit var networkManager: ConnectivityManager


    private  fun getDateFormat(date: String):String{
        val format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        val localDate = LocalDateTime.parse(date, format)

        val dateFormatter = DateTimeFormatter.ofPattern("EEE, MMM d, hh:mm a", Locale.getDefault())
        return localDate.format(dateFormatter)
    }

    private fun checkGps(): Boolean {
        val manager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER) || manager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private val request = LocationRequest.create().apply {
        interval = 1000L
        fastestInterval = 5000L
        priority = LocationRequest.PRIORITY_LOW_POWER
    }
    private val callBack = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            super.onLocationResult(p0)
            val location = p0.lastLocation
            Log.d("LOCATING", "Location CallBAck $location")
            if (location != null) {
                userLocation = "${location.latitude},${location.longitude}"
            }
        }
    }

    private fun requestLocation() {
        fusedLocationProviderClient.requestLocationUpdates(
            request,
            callBack,
            Looper.getMainLooper()
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        (activity as AppCompatActivity).supportActionBar?.show()
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())//FusedLocationProviderClient(requireContext())

        binding.recentsRv.adapter = adapter
        binding.recentsRv.setHasFixedSize(true)

        networkManager =
            activity?.getSystemService(ConnectivityManager::class.java) as ConnectivityManager

        doNetworkOperation()

        binding.btnSearchLocation.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionCurrentWeatherFragmentToSearchLocationFragment())
        }

    }

    override fun onPause() {
        super.onPause()
        fusedLocationProviderClient.removeLocationUpdates(callBack)
    }

    override fun onResume() {
        super.onResume()
        //TODO check network state too
        if (!checkGps()) {
            val text = "No permission, activate and refresh"
            makeToast(text)
            return
        }
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())//FusedLocationProviderClient(requireContext())

    }
    private fun setUpUi(viewModel: WeatherViewModel, location: String) {
        viewModel.updateWeather(location)
        adapter.submitList(emptyList())
        viewModel.weatherForecast.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Successful -> {
                    binding.progressBar.isVisible = false
                    val current = response.data?.current
                    binding.apply {
                        imgWeather.load("http:${current?.condition?.icon}")
                        cityName.text = response.data?.location?.name// current?.lastUpdated
                        degree.text = "${current?.tempC}°c"
                        dayTv.text = current?.condition?.text
                        dateTV.text = current?.lastUpdated?.let { getDateFormat(it) } ?: current?.lastUpdated
                        tempText.text = "${current?.tempC}°c"
                        humidityTitle.text = current?.humidity.toString()
                        windText.text = "${current?.windMph}Mph"
                    }
                    adapter.submitList(response.data?.forecast?.forecastday?.get(0)?.hour)

                }
                is Resource.Failure -> {
                    binding.progressBar.isVisible = false
                    binding.cityName.text = response.msg
                }
                is Resource.Loading -> {
                }
                else -> Unit
            }
        }
    }

    private val requestLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            when {
                it.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    doNetworkOperation()
                }
                it.getOrDefault(Manifest.permission.ACCESS_BACKGROUND_LOCATION, false) -> {
                    doNetworkOperation()
                }
                it.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    doNetworkOperation()
                }
                else -> {
                    val title = "ACCEPT PERMISSION REQUEST"
                    val text =
                        "You need to allow permission for app to work, to enable permission go to app settings and accept or relaunch app"
                    makeAlert(title, text)
                }
            }
        }

    private fun requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            requestLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            requestLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun checkPerms(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val is1 = ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            val is2 = ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            return is1 && is2
        } else {
            return ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        }
    }


    private fun assertPerms() {
        MaterialAlertDialogBuilder(requireContext()).apply {
            setMessage("You need to allow permission for app to work properly")
            setTitle("ACCEPT PERMISSION REQUEST")
            setPositiveButton("OK") { dialogInterface, int ->
                requestLocationPermission()
                dialogInterface.dismiss()
            }
            create()
            show()
        }
    }

    private fun runGpsAndMainOperation() {
//        if (true) {
        if (checkGps()) {
            fusedLocationProviderClient.lastLocation.addOnCompleteListener(
                OnCompleteListener {
                    if (it.result == null) {
                        requestLocation()
                    } else {
                        Log.d("LOCATING", "LastLocation()  ${it.result}")
                        userLocation = "${it.result.latitude},${it.result.longitude}"
                        setUpUi(viewModel, userLocation!!)
                    }

                })
        } else {
            val text =
                "You need to allow permission for app to work  go to settings to enable permission"
            val title = "GPS REQUIRED"
            makeAlert(title, text)
        }
    }

    private fun onlineWholeLogic() {
        when (checkPerms()) {
            true -> {
                runGpsAndMainOperation()
            }
            false -> {
                assertPerms()
            }
        }
    }
    private fun doNetworkOperation() {
        if (isConnected(networkManager)) {
            onlineWholeLogic()
        } else {
            offlineWholeLogic()
        }
    }
    private fun offlineWholeLogic() {
        when (checkPerms()) {
            true -> {
                doOffline()
            }
            false -> {
                assertPerms()
            }
        }
    }
    private fun doOffline() {
        viewModel.reportDbSitu()
        viewModel.situFrmDab.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Successful -> {
                    val current = response.data?.current

                    binding.apply {
                        progressBar.visibility = View.GONE
                        dateTV.isClickable = true
                        degree.text = "${current?.tempC}°c"
                        imgWeather.load("http:${current?.condition?.icon}")
                        cityName.text = response.data?.location?.name
                        dayTv.text = current?.condition?.text
                        dateTV.text = current?.lastUpdated?.let { getDateFormat(it) }
                            ?: current?.lastUpdated
                        tempText.text = current?.tempC.toString()
                        humidityTitle.text = current?.humidity.toString()
                        windText.text = current?.windMph.toString()
                        adapter.submitList(response.data?.forecast?.forecastday?.get(0)?.hour)
                    }
                }
                is Resource.Failure -> {
                    binding.progressBar.visibility = View.GONE
                    val text = "${response.msg}--Weather returns null, check network and refresh"
                    makeToast(text)
                }
                is Resource.Loading -> {
                    val text = "Loading , Please Wait a moment"
                    makeToast(text)
                }
                else -> Unit
            }
        }
    }
}