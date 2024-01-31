package com.example.contact

import android.app.Application
import androidx.room.Room
import com.example.contact.data.ContactDataBase

class Application: Application()  {
    lateinit var contactList: ContactDataBase
    override fun onCreate() {
        super.onCreate()
        contactList = Room.databaseBuilder(
            applicationContext,
            ContactDataBase::class.java,
            "Contacts1").build()
    }
}