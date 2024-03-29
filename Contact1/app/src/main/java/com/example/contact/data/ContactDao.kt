package com.example.contact.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@Dao
interface ContactDao {

    @Insert(entity = Person::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPersonInDB(person: Person)

    @Query("SELECT * FROM Person")
    fun getAllPersons(): Flow<List<Person>>

    @Query("SELECT * FROM Person WHERE firstName LIKE :name AND lastName LIKE :lastName")
    fun getPersonsById(name: String, lastName: String): Person

    @Query("DELETE FROM Person")
    suspend fun deletePerson()

    @Query("SELECT title, firstName, lastName, phone, country, city, thumbnailUrl FROM Person")
    fun getContactMinimal(): Flow<List<ContactMinimal>>
    }