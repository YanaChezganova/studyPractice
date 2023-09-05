package com.example.photos.ui.main

import android.Manifest
import android.content.pm.PackageManager
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.photos.R
import com.example.photos.databinding.FragmentMapsBinding
import com.example.photos.databinding.FragmentSeePhotoBinding
import com.example.photos.ui.main.database.AlbumDao
import com.google.android.gms.location.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MapsFragment : Fragment() {
    private val launcher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions())
        { map ->
            if (map.values.isNotEmpty() && map.values.all { it }) {
                startLocation()
            }
        }
    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!
    private lateinit var myMap: GoogleMap
    private lateinit var fusedClient: FusedLocationProviderClient
    private var bundle = Bundle()
    private val viewModel by viewModels<MainViewModel>()
    {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val albumDao: AlbumDao =
                    (activity?.application as Application).photoAlbum.albumDao()
                return MainViewModel(albumDao) as T
            }
        }
    }

    private val callback = OnMapReadyCallback { googleMap ->
        myMap = googleMap
        with(googleMap.uiSettings) {
            this.isZoomControlsEnabled = true
            isMyLocationButtonEnabled = true
            isMapToolbarEnabled = true
        }
        myMap.isMyLocationEnabled = true
    }
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            println("My location is ${result.lastLocation.toString()} ")
            viewModel.latitude = result.lastLocation!!.latitude
            viewModel.longitude = result.lastLocation!!.longitude
            viewModel.loadPlacesOfInterest()
            lifecycleScope.launch {
                viewModel.places.collectLatest { item ->
                    item.onEach {
                        val longitude = it.geometry.coordinates[0]
                        val latitude = it.geometry.coordinates[1]
                        val coordinate = LatLng(latitude, longitude)
                        myMap.addMarker(
                            MarkerOptions()
                                .position(coordinate)
                                .title(it.properties.nameOfPlace)
                                .snippet(it.properties.xid)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                        )?.showInfoWindow()
                        myMap.setOnMarkerClickListener { marker ->
                            bundle = Bundle().apply {
                                putString("xid", marker.snippet)
                                putString("title", marker.title)
                            }
                            findNavController().navigate(
                                R.id.action_mapsFragment_to_infoFragment,
                                bundle
                            )
                            true
                        }
                    }
                }
            }
            myMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
            val currentLocation =
                LatLng(result.lastLocation!!.latitude, result.lastLocation!!.longitude)
            myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 13f))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapsBinding.inflate(inflater)
        checkPermissions()
        fusedClient = LocationServices.getFusedLocationProviderClient(requireContext())
        startLocation()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedClient.removeLocationUpdates(locationCallback)
    }

    companion object {
        private val REQUIRED_PERMISSIONS: Array<String> = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    private fun startLocation() {
        val request = LocationRequest.create()
            .setInterval(500000)
            .setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY)
        fusedClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper())
    }

    private fun checkPermissions() {
        if (REQUIRED_PERMISSIONS.all { permission ->
                ContextCompat.checkSelfPermission(this.requireContext(), permission) ==
                        PackageManager.PERMISSION_GRANTED
            }) {
            println("Permissions granted")
        } else {
            launcher.launch(REQUIRED_PERMISSIONS)
        }
    }
}