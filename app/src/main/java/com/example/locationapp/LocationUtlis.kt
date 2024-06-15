package com.example.locationapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import java.util.Locale

class LocationUtlis(val context:Context) {
    //reading location
    private val _fusedLocationClient:FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)//allwo mw to get latitude and longitude
    @SuppressLint("MissingPermission")
    //lint tool checks your android proh=ject souce files for potentila bugs and optimization improvements for correctness,security,perfromance,usuabiliy

    fun requestLocationUpdates(viewModel:LocationViewModel){
        val locationCallback=object:LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                locationResult.lastLocation?.let{
                    val location=LocationData(latitude = it.latitude, longitude = it.longitude)
                    viewModel.updateLocation(location)
                }
            }
        }
       val locationRequest= LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,1000).build()

       _fusedLocationClient.requestLocationUpdates(locationRequest,locationCallback, Looper.getMainLooper())
           //looper is used to configure  threats for loaction.....



    }

    fun hasLocationPermission(context: Context):Boolean{
     return if(ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED
         &&   ContextCompat.checkSelfPermission(context,
             Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED

         ){
            return true;
        }else{
            false

        }
    }

fun reverseGeocodeLocation(location:LocationData): String {
    val geocoder=Geocoder(context, Locale.getDefault())
    val coordinate= LatLng(location.latitude,location.longitude)
 val address:MutableList<Address>? = geocoder.getFromLocation(coordinate.latitude,coordinate.longitude,1)
return if (address?.isNotEmpty()==true){//tracking
    address[0].getAddressLine(0)
}
    else{
        "Address not found"
    }
}


}