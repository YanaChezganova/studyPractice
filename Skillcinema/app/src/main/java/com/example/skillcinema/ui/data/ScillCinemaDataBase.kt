package com.example.skillcinema.ui.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities =
   [
    FolderDB::class,
    FilmDB::class,
    FolderWithFilm::class],
    version = 1)

abstract class ScillCinemaDataBase: RoomDatabase() {
    abstract fun cinemaBaseDao(): CinemaBaseDao
}