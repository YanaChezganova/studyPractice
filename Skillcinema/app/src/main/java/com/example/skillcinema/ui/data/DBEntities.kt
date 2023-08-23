package com.example.skillcinema.ui.data

import androidx.room.*
import javax.inject.Inject

@Entity(tableName = "film")
data class FilmDB  @Inject constructor(

    @ColumnInfo(name = "id")
    val id: Int,
    @PrimaryKey
    @ColumnInfo(name = "idFilm")
    val idFilm: Int,

    @ColumnInfo(name = "isWatched")
    val isWatched: Boolean,

    @ColumnInfo(name = "posterUrl")
    val posterUrl: String?,

    @ColumnInfo(name = "filmName")
    val filmName: String?,

    @ColumnInfo(name = "rating")
    val rating: String?,

    @ColumnInfo(name = "genre")
    val genre: String?,

    @ColumnInfo(name = "country")
    val country: String?
)




@Entity(tableName = "folder")
data class FolderDB  (

    @ColumnInfo(name = "id")
    val id: Int?,

    @ColumnInfo(name = "type")  // 1=favorite, 2=wantToSee 3=person's folder
    val type: Int,
    @PrimaryKey
    @ColumnInfo(name = "uniqueName") // by current data on create moment
    val uniqueName: Long,

    @ColumnInfo(name = "name") // by user
    val name: String?,

    @ColumnInfo(name = "count") // films in folder
    val count: Int,
)
@Entity(tableName = "folder_with_film", primaryKeys = ["idFolder", "idFilm"])
data class FolderWithFilm(
    @ColumnInfo(name = "idFolder")
    val idFolder: Long,
    @ColumnInfo(name = "idFilm")
    val idFilm: Int,
)
data class CountFilmsInFolder (
    @ColumnInfo(name = "COUNT (idFilm)")
    val countFilms: Int,
        )



data class FilmsInFolder  (
    @Embedded
    val folder: FolderDB,
    @Relation(
        parentColumn = "uniqueName",
        entityColumn = "idFilm",
        associateBy = Junction(
            FolderWithFilm::class,
            parentColumn = "idFolder",
            entityColumn = "idFilm"
        )
        )
    val filmsList: List<FilmDB>
)
