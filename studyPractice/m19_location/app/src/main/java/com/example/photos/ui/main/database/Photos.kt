package com.example.photos.ui.main.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photos")
data class Photos(
    @ColumnInfo(name = "id")
    val id: Int? = null,
    @PrimaryKey
    @ColumnInfo(name = "uri")
    val uri: String,
    @ColumnInfo(name = "date")
    val date: String,
    @ColumnInfo(name = "fileName")
    val fileName: String
)