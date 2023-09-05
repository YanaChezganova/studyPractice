package com.example.photos.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photos.ui.main.support.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AdditionalViewModel(val repository: Repository) : ViewModel()  {


    var infoAboutPlace = PlaceOfInterest(
        "",
        "", address = Address("", "", "", "", "", ""), "", "", "",
        "", sources = Sources("", listOf("", "")), "", infoAboutPlace = InfoAboutPlace(
            "", "", "", "", ""), "", "",
        preview = Preview("", 0, 0), textAboutPlace = TextAboutPlace("", ""),
        pointLocation = PointLocation(0.0, 0.0)
    )


    suspend fun loadInfoAboutPlace(idOfPlace: String, dispatcher: CoroutineDispatcher): PlaceOfInterest {
        viewModelScope.launch(dispatcher) {
            try {
                infoAboutPlace = repository.getInformation(idOfPlace)
            } catch (e: Exception) {
                println(message = e.message)
            } finally {
                println("Received info = ${infoAboutPlace}")
            }
        }
        delay(1500)
        return infoAboutPlace
    }
}