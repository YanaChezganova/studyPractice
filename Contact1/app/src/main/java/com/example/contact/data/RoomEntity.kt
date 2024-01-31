package com.example.contact.data

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Person")
    data class Person(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "firstName")
    val first: String,
    @ColumnInfo(name = "lastName")
    val last: String,
    @ColumnInfo(name = "gender")
    val gender: String,
    @ColumnInfo(name = "dateOfBirth")
    val date: String,
    @ColumnInfo(name = "age")
    val age: Int,
    @ColumnInfo(name = "bigPictureUrl")
    val large: String,
    @ColumnInfo(name = "mediumPictureUrl")
    val medium: String,
    @ColumnInfo(name = "thumbnailUrl")
    val thumbnail: String,
    @Embedded
    val address: Address
)


 data class Address(
    @ColumnInfo(name = "e-mail")
    val email: String,
    @ColumnInfo(name = "phone")
    val phone: String,
    @ColumnInfo(name = "home number")
    val homeNumber: Int,
    @ColumnInfo(name = "street")
    val street: String,
    @ColumnInfo(name = "city")
    val city: String ,
    @ColumnInfo(name = "state")
    val state: String,
    @ColumnInfo(name = "country")
    val country: String,
    @ColumnInfo(name = "latitude")
    val latitude: Double ,
    @ColumnInfo(name = "longitude")
    val longitude: Double
 )


