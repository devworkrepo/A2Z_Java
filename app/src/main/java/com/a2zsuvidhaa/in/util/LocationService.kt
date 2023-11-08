package com.a2zsuvidhaa.`in`.util

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.location.*
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import com.a2zsuvidhaa.`in`.util.dialogs.StatusDialog
import com.a2zsuvidhaa.`in`.util.file.FragmentImagePicker
import java.util.*
import kotlin.collections.ArrayList


object LocationService {

    fun setupListener(listener: MLocationListener?) {
        mListener = listener
    }

    private var mLocationListener: LocationListener? = object : LocationListener {
        override fun onLocationChanged(location: Location) {

            mListener?.onLocationChange(location)
            location.let {
                mLocationManager?.removeUpdates(this)
            }
        }


        override fun onStatusChanged(s: String, i: Int, bundle: Bundle) {}
        override fun onProviderEnabled(s: String) {}
        override fun onProviderDisabled(s: String) {}
    }
    private var mLocationManager: LocationManager? = null

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(mLocationManager: LocationManager) {
        LocationService.mLocationManager = mLocationManager
        val isGPSEnabled: Boolean =
            mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled: Boolean =
            mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        var location: Location? = null


        if (isNetworkEnabled) {
            mLocationListener?.let {
                mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    LOCATION_UPDATE_MIN_TIME,
                    LOCATION_UPDATE_MIN_DISTANCE, it
                )
            }
            location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        }
        if (isGPSEnabled) {
            mLocationListener?.let {
                mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    LOCATION_UPDATE_MIN_TIME,
                    LOCATION_UPDATE_MIN_DISTANCE, it
                )
            }
            location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        }
        if (location == null) {
            AppLog.d("location is null")
        }
    }

    private const val LOCATION_UPDATE_MIN_DISTANCE = 10f
    private const val LOCATION_UPDATE_MIN_TIME = 5000L


    private var mListener: MLocationListener? = null

    interface MLocationListener {
        fun onLocationChange(location: Location)
    }


    fun locationEnabled(context: Context): Boolean {
        val lm: LocationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var gps_enabled = false
        var network_enabled = false
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        if (!gps_enabled && !network_enabled) {
            AlertDialog.Builder(context)
                .setMessage("GPS Enable")
                .setPositiveButton(
                    "Settings"
                ) { _, _ ->
                    context.startActivity(
                        Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    )
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
        return gps_enabled && network_enabled
    }

    fun isLocationEnabled(context: Context): Boolean {
        val isEnabled = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // This is new method provided in API 28
            val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            lm.isLocationEnabled
        } else {
            // This is Deprecated in API 28
            val mode = Settings.Secure.getInt(
                context.contentResolver, Settings.Secure.LOCATION_MODE,
                Settings.Secure.LOCATION_MODE_OFF
            )
            mode != Settings.Secure.LOCATION_MODE_OFF
        }

        if (!isEnabled) {
            AlertDialog.Builder(context)
                .setMessage("GPS Enable")
                .setPositiveButton(
                    "Settings"
                ) { _, _ ->
                    context.startActivity(
                        Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    )
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
        return isEnabled
    }


    fun getAddress(context: Context, location: Location): Array<String> {
       try{
           val addresses: List<Address>
           val geocoder = Geocoder(context, Locale.getDefault())

           addresses = geocoder.getFromLocation(
               location.latitude,
               location.longitude,
               1
           )


           val address1: String = addresses[0].featureName ?: ""
           val address2: String = addresses[0].locality ?: ""
           val address3: String = addresses[0].subLocality ?: ""
           val address4: String = addresses[0].adminArea?: ""
           val address5: String = addresses[0].subAdminArea?: ""
           val city: String = addresses[0].locality?: ""
           val state: String = addresses[0].adminArea?: ""
           // val country: String = addresses[0].getCountryName()
           val postalCode: String = addresses[0].postalCode?: ""
           //  val knownName: String = addresses[0].getFeatureName()

           return arrayOf("$address1 $address3", city, "$state - $postalCode")
       }catch (e : Exception){
           return arrayOf("","","")
       }
    }


    fun fetchLocation(context: Context, onFetch: (Location) -> Unit) {
        val isLocationServiceEnable = isLocationEnabled(context)
        if (isLocationServiceEnable) {
            val dialog = StatusDialog.progress(context, "Fetching Location...")
            val locationManager =
                context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            getCurrentLocation(locationManager)
            setupListener(object : MLocationListener {
                override fun onLocationChange(location: Location) {
                    dialog.dismiss()
                    onFetch(location)
                }
            })
        }
    }

}