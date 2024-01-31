package com.example.contact.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.contact.data.*
import com.example.contact.support.Repository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(contactDao: ContactDao) : ViewModel() {
    val repository = Repository(contactDao)
    private var _state = MutableStateFlow<State>(State.Ready)
    val stateOfView = _state.asStateFlow()
    private val _contactsFromDB = MutableStateFlow<List<ContactMinimal>>(emptyList())
    val contactsFromDB = _contactsFromDB.asStateFlow()
    private val _contactsFromNetwork = MutableStateFlow<List<Contact>>(emptyList())
    val contactsFromNetwork = _contactsFromNetwork.asStateFlow()

    init {
        viewModelScope.launch {
            getContactListFromDB()
        }
    }
    fun loadContactsFromNetwork() {
        _state.value = State.Loading
        viewModelScope.launch {
            kotlin.runCatching {
                repository.getContactListFromNetwork()
            }.fold(
                onSuccess = {
                    _contactsFromNetwork.value = it.results
                    _state.value = State.Done
                    println("contacts ${it.results}")
                    loadContactsInDB()
                },
                onFailure = {
                    _state.value = State.Error
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
        _state.value = State.Loading
        viewModelScope.launch {
            kotlin.runCatching {
                repository.contactMinimal
            }.fold(
                onSuccess = {
                    _contactsFromDB.value = it.value
                    _state.value = State.Done
                    println("from db ${it.value}")
                },
                onFailure = {
                    Log.d("VM loadContactsFromDB", " ${it.message} ${it.cause}")
                    _state.value = State.Error
                }
            )
        }
    }

    suspend fun getPersonByName(name: String, lastName: String): Person {
        var person = Person(
            0, "", "", "", "",
            "", 0, "", "", "", Address(
                "", "",
                0, "", "", "", "", 0.0, 0.0
            )
        )
        viewModelScope.launch {
            person = repository.getPersonByIName(name, lastName)
        }
        delay(100)
        return person
    }

}