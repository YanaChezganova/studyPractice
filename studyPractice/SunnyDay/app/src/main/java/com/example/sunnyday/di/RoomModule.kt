package com.example.sunnyday.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.sunnyday.App
import com.example.sunnyday.data.RoomRepository
import com.example.sunnyday.data.SunnyDayDataBase
import com.example.sunnyday.data.WeatherBaseDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class RoomModule {
    @Provides
    @Singleton
    fun providesApplication(): Application = Application()

    @Singleton
    @Provides
    fun providesApplicationContext(): Context = providesApplication().applicationContext

    @Singleton
    @Provides
    fun providesRoomDatabase(@ApplicationContext context : Context): SunnyDayDataBase = Room.databaseBuilder(
        context,
        SunnyDayDataBase::class.java,
        "SunnyDataBase11")
        .allowMainThreadQueries()
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    @Singleton
    fun providesDao(database: SunnyDayDataBase): WeatherBaseDao = database.sunnyDayBaseDao()

}