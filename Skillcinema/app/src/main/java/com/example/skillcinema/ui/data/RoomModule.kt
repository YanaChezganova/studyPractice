package com.example.skillcinema.ui.data

import android.content.Context
import androidx.room.Room
import com.example.skillcinema.Application
import com.example.skillcinema.MainActivity
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RoomModule {
    @Provides
    @Singleton
    fun providesApplication(): Application = Application()

    @Provides
    @Singleton
    fun providesApplicationContext(): Context = Application().applicationContext

    @Provides
    @Singleton
    fun providesRoomDatabase(@ApplicationContext context : Context): ScillCinemaDataBase = Room.databaseBuilder(
            context,
            ScillCinemaDataBase::class.java,
            "CinemaBase416")
        .allowMainThreadQueries()
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    @Singleton
  fun providesDao(database: ScillCinemaDataBase): CinemaBaseDao = database.cinemaBaseDao()


}
