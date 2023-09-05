package com.example.goodmorning.location

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.location.Location
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import androidx.core.content.PermissionChecker.checkSelfPermission
import com.example.goodmorning.ui.main.MainFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task

class LastLocationProvider constructor( private val context: Context){
    private lateinit var fusedClient: FusedLocationProviderClient
      @RequiresApi(Build.VERSION_CODES.Q)
      private fun getLocation(callback: (Result<Location>) -> Unit){
              if (!hasLocationPermission()) {
                   callback.invoke(Result.failure(IllegalAccessException("Missing location permission")))
                   return
               }
               val lastLocationTask = getLocationTask()
           }

           @RequiresApi(Build.VERSION_CODES.Q)
           fun getLocationTask(): Task<Location> {
               return LocationServices.getFusedLocationProviderClient(context).lastLocation
           }
           private fun Task<Location>.getLocation(): Location? {
           return if(isSuccessful && result != null){
               result!!
           } else {
               null
           }
           }
           private fun hasLocationPermission(): Boolean{
               return checkSelfPermission(context,ACCESS_FINE_LOCATION) == PERMISSION_GRANTED ||
                       checkSelfPermission(context, ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED
           }
}