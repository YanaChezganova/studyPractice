package com.example.contact.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {

    @Insert(entity = Person::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPersonInDB(person: Person)

/*    @Insert(entity = Address::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAddressInDB(address: Address)*/

    @Query("SELECT * FROM Person")
    fun getAllPersons(): Flow<List<Person>>

    @Query("DELETE FROM Person")
    suspend fun deletePerson()


}