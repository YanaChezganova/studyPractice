package com.example.goodmorning.ui.main

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.work.*
import com.example.goodmorning.work_manager.CountWorker
import com.example.goodmorning.work_manager.WORK_TAG
import java.util.concurrent.TimeUnit

class MainViewModel() : ViewModel() {
lateinit var outputInfoWorkItem: LiveData<List<WorkInfo>>

    //create "createWorkRequest" to apply the count
    fun createWorkRequest(context: Context){
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresBatteryNotLow(false)
            .build()
       val countRequest = OneTimeWorkRequestBuilder<CountWorker>()
            .setConstraints(constraints)
            .setInitialDelay(1, TimeUnit.SECONDS)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 1, TimeUnit.SECONDS)
            .addTag(WORK_TAG)
            .build()
        val workManager = WorkManager.getInstance(context)
        workManager.beginWith(countRequest).enqueue()
        println("VM enqueu")
        outputInfoWorkItem = workManager.getWorkInfosByTagLiveData(WORK_TAG)
    }
}