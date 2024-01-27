package com.example.contact.support

import com.example.contact.data.ContactList
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface ContactApi {
    @GET("/api/?results=50&inc=gender,name,location,email,dob,phone,picture&noinfo&nat=us,dk,fr,gb")
    suspend fun getContactList(
       // @Query("query") result: Int
    ): ContactList
}

//https://randomuser.me/api/?results=50&inc=name,gender,nat&noinfo&nat=us,dk,fr,gb
private const val BASE_URL = "https://randomuser.me/"
object RetrofitServices {

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val contactApi: ContactApi = retrofit.create(ContactApi::class.java)

}