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

class MainViewModel(private val contactDao: ContactDao) : ViewModel() {
    val repository = Repository(contactDao)
    private var _state = MutableStateFlow<State>(State.Ready)
    val stateOfView = _state.asStateFlow()
    private val _contactsFromDB = MutableStateFlow<List<Person>>(emptyList())
    val contactsFromDB = _contactsFromDB.asStateFlow()
    private val _contactsFromNetwork = MutableStateFlow<List<Contact>>(emptyList())
    val contactsFromNetwork = _contactsFromNetwork.asStateFlow()

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
                    // repository.addPersonInDB(it.results.first())
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
                println("adding to db $it")
                repository.addPersonInDB(it)
            }
        }
    }

    fun getContactListFromDB() {
      //  _state.value = State.Loading
        viewModelScope.launch {
            kotlin.runCatching {
                repository.allContacts
            }.fold(
                onSuccess = {
                    _contactsFromDB.value = it.value
           //         _state.value = State.Done
                    println("from db ${it.value}")
                },
                onFailure = {
                    Log.d("VM loadContactsFromDB", " ${it.message} ${it.cause}")
             //       _state.value = State.Error
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

        println("from view model $person")

        delay(100)
        return person
    }

}