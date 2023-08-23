package com.example.skillcinema.ui.data

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.skillcinema.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton



class RoomRepository @Inject constructor(private val dao: CinemaBaseDao) {

    val scope = CoroutineScope(Dispatchers.Default)


    fun allFilms(): StateFlow<List<FilmDB>> =
        dao.getAllFilms()
            .stateIn(
                scope = scope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = emptyList()
            )


    fun listOfWatchedFilms(): StateFlow<List<FilmDB>> =
        dao.getWatchedFilms()
            .stateIn(
                scope = scope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = emptyList()
            )


    fun allFolders(): StateFlow<List<FolderDB>> =
        dao.getAllFolders()
            .stateIn(
                scope = scope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = emptyList()
            )
    suspend fun addFilmInBase(filmId: Int, isWatched: Boolean = false,
                              posterUrl: String?, filmName: String?,
                              rating: String?, genre: String?, country: String? ) {
        var films = listOf<FilmDB>()
        val scope = CoroutineScope(Dispatchers.Default)
        scope.launch {
            allFilms().collect {
                films = it
            }
        }
        delay(100)
        val size = films.size
        println("HVM size films list = $size")
        scope.launch {
            dao.insertFilmDB(
                FilmDB(
                    id = size,
                    idFilm = filmId,
                    isWatched = isWatched,
                    posterUrl = posterUrl,
                    filmName = filmName,
                    rating = rating,
                    genre = genre,
                    country = country
                )
            )
        }
        delay(100)
    }

    suspend fun findFilmInBase(filmId: Int): Boolean {
        val scope = CoroutineScope(Dispatchers.Default)
        val totalDeferred = scope.async {
            val total = kotlin.runCatching {
                dao.findFilmsInBase(filmId)
            }.fold(
                onSuccess = { if (it != null) 1 else 2 },
                onFailure = { Log.d("RoomR find film in base", " ${it.message} ${it.cause}")}
            )
            return@async total}
        return kotlin.run { totalDeferred.await() } == 1
    }

    suspend fun addFilmInFolder(filmId: Int, folderId: Long ){
        // folder Id = 0 - favorite, 1 - want to see
        val id = if (folderId < 2) detectUniqueNumberForFolder(folderId.toInt())
        else folderId
        val scope = CoroutineScope(Dispatchers.Default)
        scope.launch {
            dao.addFilmInFolder(FolderWithFilm( id, filmId)
            )
        }
    }

    private suspend fun detectUniqueNumberForFolder(folderType: Int): Long {
        var folders = emptyList<FolderDB>()
        val scope = CoroutineScope(Dispatchers.Default)
        scope.launch {
            allFolders().collect {
                folders = it
                println("RoomR = $it")
            }
        }
        delay(200)
        val idFavorite : Long
        val idWantToSee: Long
        try{  folders[0].uniqueName
            folders[1].uniqueName}
        catch (t: Throwable){
            Log.d("RoomRep mistake detecting Unique Number For Folder", "try again")
        } finally {
            delay(100)
            idFavorite = folders[0].uniqueName
            idWantToSee = folders[1].uniqueName
        }
        return when (folderType){
            0 -> idFavorite
            else -> idWantToSee
        }
    }
    suspend fun findFilmInFolder(filmId: Int, folderId: Long): Boolean{
        // folderId = 0 - favorite, 1 - want to see
        val id = if (folderId < 2) detectUniqueNumberForFolder(folderId.toInt())
        else folderId
        val scope = CoroutineScope(Dispatchers.Default)
        val totalDeferred = scope.async {
            val total = kotlin.runCatching {
                dao.findFilmInFolder(idFilm = filmId, id)
            }.fold(
                onSuccess = { if (it != null) 1 else 2 },
                onFailure = {
                    Log.d("RoomRep find film in folder", " ${it.message} ${it.cause}")}
            )
            return@async total}
        return kotlin.run { totalDeferred.await() } == 1
    }

    suspend fun checkFilmIsWatched(idFilm: Int): Boolean{
        val scope = CoroutineScope(Dispatchers.Default)
        val totalDeferred = scope.async {
            val total = kotlin.runCatching {
                dao.findFilmsInBase(idFilm)
            }.fold(
                onSuccess = { if (it == null) 2 else
                {if (it.isWatched) 1 else 2}
                },
                onFailure = {
                    Log.d("RoomRep check is watched", " ${it.message} ${it.cause}")}
            )
            return@async total}
        return kotlin.run { totalDeferred.await() } == 1
    }
    fun markFilmWatched(idFilm: Int, isWatched: Boolean){
        val scope = CoroutineScope(Dispatchers.Default)
        scope.launch {
            dao.markFilmIsWatched(idFilm, isWatched)}
    }
    suspend fun deleteFilmFromFolder(filmId: Int, folderId: Long) {
        // folderId = 0 - favorite, 1 - want to see
        val id = if (folderId < 2) detectUniqueNumberForFolder(folderId.toInt())
        else folderId
        val scope = CoroutineScope(Dispatchers.Default)
        scope.launch { dao.deleteFilmFromFolder(filmId, id)}
    }
    suspend fun addFolderInBase(type: Int, uniqueName: Long, title: String?) {
        var folders = listOf<FolderDB>()
        val scope = CoroutineScope(Dispatchers.Default)
        scope.launch {
            allFolders().collect {
                folders = it
            }
        }
        delay(100)
        val size = folders.size
        scope.launch {
            dao.insertFolderDB(
                FolderDB(
                    id = size,
                    type = type,
                    uniqueName = uniqueName,
                    name = title,
                    count = 0
                )
            )
        }
        delay(50)
    }
    fun setCountOfFilmsInFolder(uniqueName: Long, count: Int){
        val scope = CoroutineScope(Dispatchers.Default)
        scope.launch {
            dao.setCountOfFilmsInFolder(uniqueName, count)
        }
    }
    suspend fun findAllFoldersForFilm(filmId: Int): List<FolderWithFilm>?{
        var listWithFolders = emptyList<FolderWithFilm>()

        val scope = CoroutineScope(Dispatchers.Default)
        scope.launch {
            val total = kotlin.runCatching {
                dao.findAllFoldersForFilm(idFilm = filmId)
            }.fold(
                onSuccess = { listWithFolders = it!!  },
                onFailure = {
                    Log.d("RoomR find list film in folders in base", " ${it.message} ${it.cause}")}
            )
        }
        delay(500)
        return listWithFolders
    }
    suspend fun countFilmsInFolder(uniqueName: Long): Int{
        var count: CountFilmsInFolder = CountFilmsInFolder(0)
        val scope = CoroutineScope(Dispatchers.Default)
        scope.launch {
          count = dao.countFilmsInFolder(uniqueName)
        }
        delay(200)
        return count.countFilms
    }

    suspend fun getFilmsInFolder(uniqueName: Long): List<FilmDB>{
        var films = listOf<FilmsInFolder>(FilmsInFolder(FolderDB(1,1,1,"",0),
            listOf<FilmDB>(FilmDB(1,2,false,"","","","", ""))))
        var listOfFilms = listOf<FilmDB>(FilmDB(1,2,false,"","","","", ""))
        val scope = CoroutineScope(Dispatchers.Default)
        scope.launch {
            films = dao.getFilmsForFolder(uniqueName)
        }
        delay(300)
        films.onEach {
            listOfFilms = it.filmsList
        }
        return listOfFilms
    }
    suspend fun deleteFilmsFromBase(){
        val scope = CoroutineScope(Dispatchers.Default)
        scope.launch { dao.deleteFilmsFromBase()
            dao.deleteConnectionsFilmToFolder()
        }
    }
}