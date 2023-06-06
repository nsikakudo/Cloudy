package com.project.cloudy

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.room.TypeConverter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.project.cloudy.model.Day
import com.project.cloudy.model.Hour

class cb(val context: Context, val application: AppCompatActivity) {
    fun checkPerms(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val is1 = ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            val is2 = ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            return is1 && is2
        } else {
            return ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    val requestLauncher =
        application.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            when {
                it.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    requestLocationPermission()
                }
                it.getOrDefault(Manifest.permission.ACCESS_BACKGROUND_LOCATION, false) -> {
                    requestLocationPermission()
                }
                it.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    requestLocationPermission()
                }
                else -> {

                    MaterialAlertDialogBuilder(context).apply {
                        setMessage("You need to allow permission for app to work ")
                        setTitle("ACCEPT PERMISSION REQUEST")
                        setPositiveButton("OK") { dialogInterface, Int ->
                            dialogInterface.dismiss() // or you can request permission again
                        }
                        create()
                        show()
                    }
                }
            }
        }

    @RequiresApi(Build.VERSION_CODES.N)
    fun requestLocationPermission() {

        if (checkPerms()) {
            Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
        } else {
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
    }

    @TypeConverter
    fun toHourString(list: List<Hour>): String {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun toHourList(value: String): List<Hour> {
        return Gson().fromJson(value, Array<Hour>::class.java).toList()
    }

    fun fromStringToDay(string: String): Day {
        return Gson().fromJson(string, Day::class.java)
    }


    enum class NetworkState {
        CONNECTED,
        DISCONNECTED
    }

    class NetworkReceiver : BroadcastReceiver() {
        companion object {
            var isConnected: NetworkState = NetworkState.CONNECTED
        }

        @RequiresApi(Build.VERSION_CODES.M)
        override fun onReceive(p0: Context?, p1: Intent?) {
            val manager =
                p0?.getSystemService(ConnectivityManager::class.java) as ConnectivityManager
            val networkCapabilities = manager.getNetworkCapabilities(manager.activeNetwork)

            isConnected = when {
                networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true -> {
                    Log.d("STATE", "isConnected")
                    NetworkState.CONNECTED
                }
                networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true -> {
                    Log.d("STATE", "isConnected")
                    NetworkState.CONNECTED
                }
                networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) == true -> {
                    Log.d("STATE", "isConnected")
                    NetworkState.CONNECTED
                }
                else -> {
                    Log.d("STATE", "Disconnected")
                    NetworkState.DISCONNECTED
                }
            }
        }
    }

    private fun reqNetwork() {
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()


        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
            }

            override fun onLost(network: Network) {
                super.onLost(network)

            }

            override fun onUnavailable() {
                super.onUnavailable()

            }
        }

    }

}

@RequiresApi(Build.VERSION_CODES.M)
fun Fragment.isConnected(networkManager: ConnectivityManager): Boolean {
    val network = networkManager.getNetworkCapabilities(networkManager.activeNetwork)
    return network?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true ||
            network?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true ||
            network?.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) == true
}

fun Fragment.makeToast(text: String) {
    Toast.makeText(
        requireContext(),
        text,
        Toast.LENGTH_SHORT
    ).show()
}

fun Fragment.makeAlert(title: String, text: String) {
    MaterialAlertDialogBuilder(requireContext()).apply {
        setMessage(text)
        setTitle(title)
        setPositiveButton("OK") { dialogInterface, int ->
            dialogInterface.dismiss()
        }

        create()
        show()
    }
}

fun Fragment.makeSnackAction(title: String, text: String, action: () -> Unit) {
    MaterialAlertDialogBuilder(requireContext()).apply {
        setMessage(text)
        setTitle(title)
        setPositiveButton("OK") { dialogInterface, int ->
            dialogInterface.dismiss()
            action()
        }

        create()
        show()
    }
}
