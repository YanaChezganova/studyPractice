package com.example.photos.ui.main.support

class Repository{
    suspend  fun getInformation(idOfPlace: String): PlaceOfInterest{
        println("i1223")
       return RetrofitServices.searchPlacesApi.getInformation(idOfPlace)
    }
     suspend fun  getListOfPlaces(longitude: Double, latitude: Double): ListOfPlaces {
       return RetrofitServices.searchPlacesApi.getListOfPlaces(
            longitude,
            latitude
        )
    }
    /*    fun updateItemValue(idOfPlace: String) {
            val scope = CoroutineScope(dispatcher)
            scope.launch {RetrofitServices.searchPlacesApi.getInformation(idOfPlace)
        }*/

}