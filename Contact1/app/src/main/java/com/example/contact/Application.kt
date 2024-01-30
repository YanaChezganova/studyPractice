package com.example.contact

import android.app.Application
import androidx.room.Room
import com.example.contact.data.ContactDataBase

class Application: Application()  {
    lateinit var contactsList: ContactDataBase
    override fun onCreate() {
        super.onCreate()
        contactsList = Room.databaseBuilder(
            applicationContext,
            ContactDataBase::class.java,
            "Contacts").build()
    }
}