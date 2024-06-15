package com.example.locationapp

import android.content.Context
import android.os.Bundle
import android.Manifest
import android.widget.Toast

import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.locationapp.ui.theme.LocationAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel:LocationViewModel = viewModel()
            LocationAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                        myApp(viewModel)
                }
            }
        }
    }
}
@Composable
fun myApp(viewModel: LocationViewModel) {
    val context= LocalContext.current
    val locationUtlis=LocationUtlis(context)
   LocationDisplay(locationUtlis = locationUtlis, viewModel,context = context)


}
@Composable
fun LocationDisplay(
    locationUtlis: LocationUtlis,
    viewModel: LocationViewModel,
    context: Context
){
    val location=viewModel.location.value


    val address=location?.let { //unpack the location
        locationUtlis.reverseGeocodeLocation(location)
    }

    val requestPermissionLauncher= rememberLauncherForActivityResult(
        contract =ActivityResultContracts.RequestMultiplePermissions() , //Activity is a popup
        onResult = {permissions ->
            if(permissions[Manifest.permission.ACCESS_COARSE_LOCATION]==true
                && permissions[Manifest.permission.ACCESS_FINE_LOCATION]==true){
                //i have access to location


                locationUtlis.requestLocationUpdates(viewModel=viewModel)
            }
            else {
                //ASKING FOR A PERMISSION.....PERMISSION........
                val rationaleRequired=ActivityCompat.shouldShowRequestPermissionRationale(
                    context as MainActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )||ActivityCompat.shouldShowRequestPermissionRationale(
                    context as MainActivity,
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                if(rationaleRequired){
                    Toast.makeText(context,
                        "Location Permission is required for this feature to work",Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(context,
                        "Location Permission is required.Please enable it in Android settings",Toast.LENGTH_LONG).show()
                }

            }
        })


    Column (modifier=Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center){
        if(location!=null){
            Text(text = "Address: ${location.latitude} ${location.longitude}\n $address")



        }
        else{
            Text(text = "Location Not Available")
        }

//        Text(text = "Location not available")

        Button(onClick = {
            if(locationUtlis.hasLocationPermission(context)){
                //permisiion already Granted
                locationUtlis.requestLocationUpdates(viewModel)
            }
            else {
                //request location permission


                requestPermissionLauncher.launch(arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ))

            }

        }) {
            Text(text = "Get Location")
        }
    }
}
