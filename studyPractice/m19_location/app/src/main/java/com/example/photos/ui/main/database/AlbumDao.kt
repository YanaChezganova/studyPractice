package com.example.photos.ui.main.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AlbumDao {
    @Query("SELECT * FROM photos ORDER BY date DESC")
    fun getAllPhotos(): Flow<List<Photos>>

    @Query("DELETE FROM photos")
    suspend fun delete()

    @Insert(entity = Photos::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photo: Photos)
}