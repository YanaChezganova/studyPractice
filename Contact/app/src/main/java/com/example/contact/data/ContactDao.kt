package com.example.contact.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {

    @Insert(entity = Person::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPersonInDB(person: Person)

/*    @Insert(entity = Address::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAddressInDB(address: Address)*/

    @Query("SELECT * FROM person")
    fun getAllPersons(): Flow<List<Person>>

    @Query("DELETE FROM person")
    suspend fun deletePerson()



}