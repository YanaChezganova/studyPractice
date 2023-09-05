package com.example.goodmorning.work_manager

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.goodmorning.alarm.UseCase
import com.example.goodmorning.location.LastLocationProvider
import com.example.goodmorning.ui.main.RISE_TIME
import kotlinx.coroutines.*
import org.shredzone.commons.suncalc.SunTimes
import java.text.SimpleDateFormat
import java.util.*


const val WORK_TAG = "CountTime"
const val RESULT_KEY = "AlarmTime"
class CountWorker(
  val  appContext: Context,
    workerParams: WorkerParameters
): CoroutineWorker(appContext, workerParams)
{

    val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.US)
    private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.US)

    @RequiresApi(Build.VERSION_CODES.Q)
    override suspend fun doWork(): Result = coroutineScope {
        try {
            println("do work")
            val timer = countTime()
            if (timer == 0L) Result.retry()
            val outputData = workDataOf(RESULT_KEY to timer)
            return@coroutineScope Result.success(outputData)

        } catch (e: java.lang.Exception) {
            return@coroutineScope   Result.failure()
        }
    }

     @RequiresApi(Build.VERSION_CODES.Q)
     suspend fun countTime(): Long{
        var riseTimeInMillis = 0L
        val scope = CoroutineScope(Dispatchers.Default)
         val location = LastLocationProvider(applicationContext).getLocationTask()
        scope.launch {
            // detect location
            delay(2000)
            val timeZone = Calendar.getInstance().timeZone.id
            val dayDetect =
                calendar.timeInMillis + 86400000 // add 1 day to detect sunrise tomorrow (day = 86400000 millisecond)
            // detect year, month, date in Integer to give SunTimes.compute() arguments
            val dateY = dateFormat.format(dayDetect).toString().takeLast(4).toInt()
            val dateM = dateFormat.format(dayDetect).toString().drop(3).take(2).toInt()
            val dateD = dateFormat.format(dayDetect).toString().take(2).toInt()
            var latitude = 0.0
            var longitude = 0.0
            if (location.isComplete){

                latitude = location.result.latitude
                longitude = location.result.longitude
            } else {
                println("not detect coordinate")
                }
            println("time zone ${timeZone}, date=$dateY.$dateM.$dateD. latitude $latitude, longitude $longitude")
            //detect time of sunrise
            riseTimeInMillis = counterSunrise(dateY, dateM,
                dateD, latitude,
                longitude, timeZone)
        }
        delay(2500)
         return riseTimeInMillis
    }
    fun counterSunrise(dateY: Int, dateM: Int,
                               dateD: Int, latitude: Double,
                               longitude: Double, timeZone: String): Long{

        val times = SunTimes.compute()
            .on(dateY, dateM, dateD)   // set a date int year, int month, int date
            .at(latitude, longitude)   // set a location
            .timezone(timeZone)     // set a time zone id string like "Asia/Bangkok"
            .execute()    // get the results
       val riseTimeInMillis = times.rise.time
        val riseTimeFormated = timeFormat.format(times.rise.time)
        println("Sunrise: $riseTimeInMillis")
        println("Sunrise: $riseTimeFormated")
        println("Sunset: ${timeFormat.format(times.set.time)}")
    return riseTimeInMillis
    }

}
