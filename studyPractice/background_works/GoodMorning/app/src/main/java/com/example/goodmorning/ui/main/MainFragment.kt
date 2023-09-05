package com.example.goodmorning.ui.main

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
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
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.work.WorkInfo
import com.example.goodmorning.R
import com.example.goodmorning.alarm.AlarmReceiver
import com.example.goodmorning.alarm.UseCase
import com.example.goodmorning.databinding.FragmentMainBinding
import com.example.goodmorning.work_manager.RESULT_KEY
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*

private const val KEY_ALARM_TIME = "AlarmTime"
const val RISE_TIME = "riseTime"

class MainFragment : Fragment() {
    companion object {
        @RequiresApi(Build.VERSION_CODES.Q)
        val REQUEST_PERMISSIONS: Array<String> = buildList {
            add(Manifest.permission.ACCESS_COARSE_LOCATION)
            add(Manifest.permission.ACCESS_FINE_LOCATION)
        }.toTypedArray()

        fun newInstance() = MainFragment()

        fun createIntent(context: Context): Intent {
            return Intent(context, MainFragment()::class.java)
        }
    }

    private var riseTimeInMillis = 0L
    private var riseTimeFormated = ""
    private lateinit var binding: FragmentMainBinding
    private val viewModel by viewModels<MainViewModel>()

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
    val calendar = Calendar.getInstance()
    private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.US)
    private val useCase = UseCase()
    private var additionalTime = 0L

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(layoutInflater)
        viewModel.createWorkRequest(requireContext())
        // detect alarm Time From Last Session
        val alarmTimeFromLastSession =
            useCase.loadFromSharedPreference(requireContext(), KEY_ALARM_TIME)
        println("get from last session $alarmTimeFromLastSession ${alarmTimeFromLastSession > 0L}")
        if (alarmTimeFromLastSession > 0L) {
            if (calendar.timeInMillis <= alarmTimeFromLastSession) {
                //if current time less then alarm time, need to create Alarm.
                createBackgroundAlarm(alarmTimeFromLastSession)
                Toast.makeText(
                    this.requireContext(),
                    "Будильник установлен на ${timeFormat.format(alarmTimeFromLastSession)}",
                    Toast.LENGTH_SHORT
                )
                    .show()
                binding.buttonSet.isVisible = false
                binding.editTextTime.isVisible = false

            } else {
                binding.editTextTime.isVisible = true
                binding.buttonSet.isVisible = true
                useCase.clearSharedPreference(requireContext(), KEY_ALARM_TIME)
            }
        }

        binding.buttonSet.setOnClickListener { buttonSet ->
            if (checkPermissions()) {
                // viewModel.createWorkRequest(requireContext())

                val additionalTimeText = binding.editTextTime.text.toString()
                if (checkEnteredTime(additionalTimeText)) {

                    viewModel.outputInfoWorkItem.observe(viewLifecycleOwner) { info ->
                        onStateChange(info.first(), binding, additionalTime)
                    }
                }
            }
        }
        binding.buttonDelete.setOnClickListener {
            //deleting alarm
            binding.editTextTime.isVisible = true
            binding.buttonSet.isVisible = true
            binding.buttonSet.isClickable = true
            binding.editTextTime.isClickable = true
            useCase.clearSharedPreference(requireContext(), KEY_ALARM_TIME)
            Snackbar.make(
                binding.buttonSet,
                "Будильник удалён",
                Snackbar.LENGTH_LONG
            ).show()
        }
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun checkPermissions(): Boolean {
        val isAllGranted = REQUEST_PERMISSIONS.all { permission ->
            ContextCompat.checkSelfPermission(
                this.requireContext(),
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }
        if (isAllGranted) {
            println("Permission is granted")
            return true
        } else {
            launcher.launch(REQUEST_PERMISSIONS)
            return false
        }
    }

    private fun createBackgroundAlarm(timeInUTC: Long) {
        val alarmManager = this.requireContext().getSystemService(ALARM_SERVICE) as AlarmManager
        val alarmTimeUTC = timeInUTC
        val alarmType = AlarmManager.RTC_WAKEUP
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent(requireContext(), AlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(requireContext(), 1, intent, 0)
            alarmManager.setExactAndAllowWhileIdle(alarmType, alarmTimeUTC, pendingIntent)
        }
    }

    private fun onStateChange(info: WorkInfo, binding: FragmentMainBinding, additionalTime: Long) {
        if (info.state.isFinished) {
            riseTimeInMillis = info.outputData.getLong(RESULT_KEY, 123)
            useCase.loadToSharedPreference(requireContext(), RISE_TIME, riseTimeInMillis)
            println("Finished $riseTimeInMillis")
            binding.editTextTime.isClickable = false
            binding.buttonSet.isClickable = false
            val alarmTime = riseTimeInMillis + additionalTime
            createBackgroundAlarm(alarmTime)
            Snackbar.make(
                binding.buttonSet,
                "Будильник установлен на ${timeFormat.format(alarmTime)}",
                Snackbar.LENGTH_LONG
            ).show()
            useCase.loadToSharedPreference(requireContext(), KEY_ALARM_TIME, alarmTime)
            binding.buttonSet.isVisible = true
            binding.buttonDelete.isVisible = true
        } else {
            println("NOT Finished ${info.state}")
            binding.buttonSet.isVisible = false
            binding.buttonDelete.isVisible = false
        }
    }

    private fun checkEnteredTime(time: String): Boolean {
        val timeFormatInEnteredWord =
            time.contains(Regex("""^[0-2][0-9]:[0-9][0-9]$"""))
        if (time.isNullOrEmpty()) {
            Snackbar.make(
                binding.buttonSet,
                "Введите значение времени в формате  01:20",
                Snackbar.LENGTH_LONG
            ).show()
            println("1, =$time")
            return false
        } else {
            if (!timeFormatInEnteredWord) {
                Snackbar.make(
                    binding.buttonSet,
                    "Введите значение времени в формате  01:20",
                    Snackbar.LENGTH_LONG
                ).show()
                println("2 =$time")
                return false

            } else {
                println(
                    "additional Time H=${
                        time.takeWhile { it.isDigit() }.toLong()
                    } " +
                            "M=${time.takeLastWhile { it.isDigit() }.toLong()}"
                )
                additionalTime = time.takeWhile { it.isDigit() }
                    .toLong() * 3600000 + time.takeLastWhile { it.isDigit() }
                    .toLong() * 60000
                return true

            }
        }
    }
}


