package com.example.contact.support

import androidx.lifecycle.viewModelScope
import com.example.contact.data.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class Repository(private val dao: ContactDao) {

    private val scope = CoroutineScope(Dispatchers.Default)

    val allContacts: StateFlow<List<Person>> = this.dao.getAllPersons()
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    suspend fun addPersonInDB(person: Contact) {
        var size = 0
        scope.launch {
            allContacts.collect {
                size = it.size
            }
        }
        scope.launch {
            delay(300)
            with(person) {
                dao.insertPersonInDB(
                    Person(
                        id = 10 + size,
                        title = name.title,
                        first = name.first,
                        last = name.last,
                        gender = gender,
                        date = dob.date,
                        age = dob.age,
                        large = picture.large,
                        medium = picture.medium,
                        thumbnail = picture.thumbnail,
                        address = Address(
                            email = email,
                            phone = phone,
                            homeNumber = location.street.number,
                            street = location.street.name,
                            city = location.city,
                            state = location.state,
                            country = location.country,
                            latitude = location.coordinates.latitude,
                            longitude = location.coordinates.longitude
                        )
                    )
                )
            }
        }
    }
    suspend fun getPersonByIName(name: String, lastName: String): Person{
        var person = Person(0, "","","","",
            "",0,"","","", Address("","",
                0,"","","","",0.0,0.0)
        )
        scope.launch {
            person = dao.getPersonsById(name, lastName)
        }
        delay(60)
        println("from repo $person")
        return person
    }
    suspend fun deleteContacts() {
        scope.launch {
            dao.deletePerson()
        }
    }

    suspend fun getContactListFromNetwork(): ContactList {
        return RetrofitServices.contactApi.getContactList()
    }
}

