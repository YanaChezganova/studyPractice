package com.example.contact.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Person::class], version = 1)
abstract class ContactDataBase: RoomDatabase() {
    abstract fun contactDao(): ContactDao
}