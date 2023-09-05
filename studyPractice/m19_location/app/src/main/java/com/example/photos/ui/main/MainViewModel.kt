package com.example.photos.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photos.ui.main.database.AlbumDao
import com.example.photos.ui.main.database.Photos
import com.example.photos.ui.main.support.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(val albumDao: AlbumDao) : ViewModel() {
    private val _photos = MutableStateFlow<List<Photos>>(emptyList())
    val photos = _photos.asStateFlow()

    var latitude = 38.364285
    var longitude = 59.855685

    private var _places = MutableStateFlow<List<PlaceOfInterestShotInfo>>(emptyList())
    var places = _places.asStateFlow()

    var infoAboutPlace = PlaceOfInterest(
        "",
        "", address = Address("", "", "", "", "", ""), "", "", "",
        "", sources = Sources("", listOf("", "")), "", infoAboutPlace = InfoAboutPlace(
            "", "", "", "", ""), "", "",
        preview = Preview("", 0, 0), textAboutPlace = TextAboutPlace("", ""),
        pointLocation = PointLocation(0.0, 0.0)
    )

    val allPhotos: StateFlow<List<Photos>> = this@MainViewModel.albumDao.getAllPhotos()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    fun loadPlacesOfInterest() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _places.value = Repository().getListOfPlaces(
                    longitude,
                    latitude
                ).listOfPlaces
            } catch (e: Exception) {
                println(message = e.message)
            } finally {
                println("Received places = ${_places.value}")
            }
        }
    }
    suspend fun loadInfoAboutPlace(idOfPlace: String): PlaceOfInterest {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                infoAboutPlace = Repository().getInformation(idOfPlace)
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

/*

2022-10-16 14:52:51.319 21256-21495
/com.example.photos I/System.out: Received places =
[PlaceOfInterestShotInfo
(type=Feature, id=4174768,
geometry=Geometry(type=Point, coordinates=[82.9905396, 55.0636215]),
properties=Properties(xid=N3409118776, nameOfPlace=Истребитель Миг-15, rate=1, kinds=historic,monuments_and_memorials,interesting_places,monuments
)),
PlaceOfInterestShotInfo(type=Feature, id=4174769, geometry=Geometry(type=Point, coordinates=[82.989624, 55.0643616]),
 properties=Properties(xid=N3409118771, nameOfPlace=В. П. Чкалов, rate=1, kinds=historic,monuments_and_memorials,interesting_places,monuments)),

 PlaceOfInterestShotInfo(type=Feature, id=4992632, geometry=Geometry(type=Point, coordinates=[82.9795761, 55.0568275]),
  properties=Properties(xid=N660745857, nameOfPlace=ДК им. Чкалова, rate=1, kinds=cultural,cinemas,theatres_and_entertainments,interesting_places)),

   PlaceOfInterestShotInfo(type=Feature, id=7055245, geometry=Geometry(type=Point, coordinates=[82.9884491, 55.0650101]),
   properties=Properties(xid=W53372140, nameOfPlace=Музей НАПО им. В. П. Чкалова, rate=1, kinds=cultural,museums,interesting_places,other_museums)),
    PlaceOfInterestShotInfo(type=Feature, id=7088689, geometry=Geometry(type=Point, coordinates=[82.9862518, 55.0425224]),
    properties=Properties(xid=W25643597, nameOfPlace=Горизонт, rate=1, kinds=cultural,cinemas,theatres_and_entertainments,interesting_places)),
    PlaceOfInterestShotInfo(type=Feature, id=7338688, geometry=Geometry(type=Point, coordinates=[82.9892502, 55.0620956]),
    properties=Properties(xid=N674930342, nameOfPlace=Ф.Э. Дзержинский, rate=1, kinds=historic,monuments_and_memorials,interesting_places,monuments)), PlaceOfInterestShotInfo(type=Feature, id=4174767, geometry=Geometry(type=Point, coordinates=[82.990242, 55.0639839]), properties=Properties(xid=W243283961, nameOfPlace=, rate=0, kinds=fountains,cultural,urban_environment,interesting_places)), PlaceOfInterestShotInfo(type=Feature, id=4174810, geometry=Geometry(type=Point, coordinates=[82.9785004, 55.1106834]), properties=Properties(xid=N662738620, nameOfPlace=, rate=0, kinds=historic,monuments_and_memorials,interesting_places,monuments)), PlaceOfInterestShotInfo(type=Feature, id=6943825, geometry=Geometry(type=Point, coordinates=[82.9923019, 55.0526428]), properties=Properties(xid=N5936237279, nameOfPlace=, rate=0, kinds=natural,interesting_places,beaches,other_beaches)), PlaceOfInterestShotInfo(type=Feature, id=7190246, geometry=Geometry(type=Point, coordinates=[82.9902496, 55.0639877]), properties=Properties(xid=N3409093913, nameOfPlace=, rate=0, kinds=fountains,cultural,urban_environment,interesting_places)), PlaceOfInterestShotInfo(type=Feature, id=9815864, geometry=Geometry(type=Point, coordinates=[82.9835205, 55.0660019]), properties=Properties(xid=W559844534, nameOfPlace=, rate=0, kinds=other,unclassified_objects,interesting_places,historic_object)), PlaceOfInterestShotInfo(type=Feature, id=9815865, geometry=Geometry(type=Point, coordinates=[82.9827881, 55.0669327]), properties=Properties(xid=W559844535, nameOfPlace=, rate=0, kinds=other,unclassified_objects,interesting_places,historic_object))]
*/