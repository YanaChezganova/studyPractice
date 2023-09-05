package com.example.photos.ui.main

import android.app.Application
import androidx.room.Room
import com.example.photos.ui.main.database.AlbumDatabase

class Application: Application() {
    lateinit var photoAlbum: AlbumDatabase
    override fun onCreate() {
        super.onCreate()
        photoAlbum = Room.databaseBuilder(
            applicationContext,
            AlbumDatabase::class.java,
            "PhotoAlbum").build()
    }
}