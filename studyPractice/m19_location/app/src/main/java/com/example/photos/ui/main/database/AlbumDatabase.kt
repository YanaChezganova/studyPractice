package com.example.photos.ui.main.database


import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Photos::class], version = 1)
abstract class AlbumDatabase: RoomDatabase()  {
    abstract fun albumDao(): AlbumDao
}