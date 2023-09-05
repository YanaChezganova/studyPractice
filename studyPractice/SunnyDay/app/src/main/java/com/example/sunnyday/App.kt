package com.example.sunnyday

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.sunnyday.data.SunnyDayDataBase
import com.example.sunnyday.di.AppComponent
import dagger.Provides
import dagger.android.DaggerApplication
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Named
import javax.inject.Singleton

@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        super.onCreate()

    }


}
