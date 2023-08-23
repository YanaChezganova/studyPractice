package com.example.skillcinema.ui.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CinemaBaseDao {
    @Transaction
    @Query("SELECT * FROM folder WHERE uniqueName LIKE :folderUniqueName")
    fun getFilmsForFolder(folderUniqueName: Long):List<FilmsInFolder>

    @Query("SELECT * FROM film WHERE isWatched LIKE :watched")
    fun getWatchedFilms(watched: Boolean = true): Flow<List<FilmDB>>

    @Query("SELECT * FROM film LIMIT 20")
    fun getInterestedFilms(): Flow<List<FilmDB>>

    @Query("SELECT * FROM folder ORDER BY uniqueName ASC")
    fun getAllFolders(): Flow<List<FolderDB>>

    @Query("SELECT * FROM film ")
    fun getAllFilms(): Flow<List<FilmDB>>

    @Query("SELECT * FROM film  WHERE idFilm LIKE :idFilm")
    fun findFilmsInBase(idFilm: Int): FilmDB?

    @Transaction
    @Query("SELECT * FROM folder_with_film  WHERE idFilm LIKE :idFilm AND idFolder LIKE :idFolder")
    fun findFilmInFolder(idFilm: Int, idFolder: Long): FolderWithFilm?

    @Transaction
    @Query("SELECT * FROM folder_with_film  WHERE idFilm LIKE :idFilm")
    fun findAllFoldersForFilm(idFilm: Int): List<FolderWithFilm>?

    @Transaction
   @Insert(entity = FolderWithFilm::class, onConflict = OnConflictStrategy.IGNORE)
   fun addFilmInFolder(folderWithFilm: FolderWithFilm)

    @Query("UPDATE film SET isWatched = :isWatched WHERE idFilm LIKE :idFilm")
    fun markFilmIsWatched(idFilm: Int, isWatched: Boolean)

    @Insert(entity = FilmDB::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFilmDB(movie: FilmDB)

    @Insert(entity = FolderDB::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFolderDB(folder: FolderDB)

    @Transaction
    @Query("SELECT COUNT (idFilm) FROM folder_with_film  WHERE idFolder LIKE :idFolder")
    fun countFilmsInFolder(idFolder: Long): CountFilmsInFolder

    @Query("UPDATE folder SET count = :count WHERE uniqueName LIKE :uniqueName")
    fun setCountOfFilmsInFolder(uniqueName: Long, count: Int)

    @Query("DELETE FROM folder_with_film WHERE idFilm LIKE :idFilm AND idFolder LIKE :idFolder")
    suspend fun deleteFilmFromFolder(idFilm: Int, idFolder: Long)

    @Transaction
    @Query("DELETE FROM folder_with_film WHERE idFolder LIKE :idFolder")
    suspend fun deleteFolderWithFilms(idFolder: Long)

    @Query("DELETE FROM folder WHERE uniqueName LIKE :idFolder")
    suspend fun deleteFolder(idFolder: Long)

    @Query("DELETE FROM film")
    suspend fun deleteFilmsFromBase()

    @Transaction
    @Query("DELETE FROM folder_with_film")
    suspend fun deleteConnectionsFilmToFolder()
}