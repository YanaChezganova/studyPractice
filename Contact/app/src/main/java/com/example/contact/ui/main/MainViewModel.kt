package com.example.contact.ui.main


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.contact.data.Contact
import com.example.contact.data.ContactDao
import com.example.contact.data.Person
import com.example.contact.support.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(private val contactDao: ContactDao) : ViewModel() {
    val repository = Repository(contactDao)

    private val _contactsFromDB = MutableStateFlow<List<Person>>(emptyList())
    val contactsFromDB = _contactsFromDB.asStateFlow()
    private val _contactsFromNetwork = MutableStateFlow<List<Contact>>(emptyList())
    val contactsFromNetwork = _contactsFromNetwork.asStateFlow()

    fun loadContactsFromNetwork() {
        viewModelScope.launch {
            kotlin.runCatching {
                repository.getContactListFromNetwork()
            }.fold(
                onSuccess = { _contactsFromNetwork.value = it.contactList },
                onFailure = {
                    Log.d("VM loadContactsFromNetwork", " ${it.message} ${it.cause}")
                }
            )
        }
    }

    fun loadContactsInDB() {
        viewModelScope.launch {
            contactsFromNetwork.value.forEach {
                repository.addPersonInDB(it)
            }
        }
    }

    fun getContactListFromDB() {
        viewModelScope.launch {
            kotlin.runCatching {
                repository.allContacts
            }.fold(
                onSuccess = { _contactsFromDB.value = it.value },
                onFailure = {
                    Log.d("VM loadContactsFromDB", " ${it.message} ${it.cause}")
                }
            )
        }
    }


}
