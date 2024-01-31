package com.example.contact.data

data class ContactList(
    val results: List<Contact>
)

data class Contact(
    val gender: String,
    val name: Name,
    val location: LocationData,
    val email: String,
    val dob: DateOfBirth,
    val phone: String,
    val picture: Picture
)

data class Name(
    val title: String,
    val first: String,
    val last: String
)

data class LocationData(
    val street: StreetAndHome,
    val city: String,
    val state: String,
    val country: String,
    val coordinates: Coordinate
)

data class StreetAndHome(
    val number: Int,
    val name: String
    )

data class Coordinate(
    val latitude: Double,
    val longitude: Double
)
 data class DateOfBirth(
     val date: String,
     val age: Int
 )

data class Picture(
    val large: String,
    val medium: String,
    val thumbnail: String
)

data class ContactMinimal(
   val title: String,
   val firstName: String,
   val lastName: String,
   val phone: String,
   val country: String,
   val city: String,
   val thumbnailUrl: String
)