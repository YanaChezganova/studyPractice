package com.example.goodmorning.ui.main

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.goodmorning.R
import com.example.goodmorning.databinding.FragmentMainBinding
import com.example.goodmorning.databinding.FragmentStartBinding

class StartFragment: Fragment() {
    private val viewModel by viewModels<MainViewModel>()
    private lateinit var binding: FragmentStartBinding
    // request of location permissions
    val launcher: ActivityResultLauncher<Array<String>> = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    )
    { map ->
        if (map.values.all { it }) {
            println("Permissions is Granted")
        } else {
            Toast.makeText(
                this.requireContext(), "To continue work needs permissions",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStartBinding.inflate(layoutInflater)
        if (checkPermissions()) {
            findNavController().navigate(R.id.action_global_mainFragment)
        }
        binding.buttonSet.setOnClickListener {
            if (checkPermissions()) {
                findNavController().navigate(R.id.action_global_mainFragment)
            }
            findNavController().navigate(R.id.action_global_startFragment)

        }

        return binding.root
}
    @RequiresApi(Build.VERSION_CODES.Q)
    fun checkPermissions(): Boolean {
        val isAllGranted = MainFragment.REQUEST_PERMISSIONS.all { permission ->
            ContextCompat.checkSelfPermission(
                this.requireContext(),
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }
        if (isAllGranted) {
            Toast.makeText(this.requireContext(), "Permission is granted", Toast.LENGTH_SHORT)
                .show()
            return true
        } else {
            launcher.launch(MainFragment.REQUEST_PERMISSIONS)
            return false
        }
    }
}