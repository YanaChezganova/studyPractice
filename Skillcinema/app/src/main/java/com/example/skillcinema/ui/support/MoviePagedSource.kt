package com.example.skillcinema.ui.support

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bumptech.glide.load.HttpException
import com.example.skillcinema.ui.data.Constants.POPULAR
import com.example.skillcinema.ui.data.Constants.RANDOM1
import com.example.skillcinema.ui.data.Constants.SEARCH
import com.example.skillcinema.ui.data.Constants.SEARCH_BY
import com.example.skillcinema.ui.data.Constants.SERIAL
import com.example.skillcinema.ui.data.Constants.TOP
import com.example.skillcinema.ui.data.Repository
import com.example.skillcinema.ui.models.Movie
import java.io.IOException

class MoviePagedSource(
    private val typeOfRequest: String,
    private val country: Int,
    private val genre: Int,
    private val keyword: String
) : PagingSource<Int, Movie>() {
    private val repository = Repository()

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        val page = params.key ?: 1
        return if (typeOfRequest == POPULAR || typeOfRequest == TOP || typeOfRequest == SEARCH) {
            try {
                val response = when (typeOfRequest) {
                    SEARCH -> repository.getSearchResult(keyword, page)
                    POPULAR -> repository.getPopular(page)
                    else -> repository.getTopList(page)
                }
                LoadResult.Page(
                    data = response.films,
                    prevKey = if (page > 1) page - 1 else null,
                    nextKey = if (response.films.isNotEmpty()) page + 1 else null
                )
            } catch (e: IOException) {
                LoadResult.Error(e)
            } catch (e: HttpException) {
                LoadResult.Error(e)
            }
        } else {
            try {
                val response = when (typeOfRequest) {
                    SERIAL -> repository.getSerials(page)
                    RANDOM1 -> repository.getRandomListOfFilms(country, genre, page)
                    SEARCH_BY -> {
                        //keyword = $ratingFrom-$ratingTo+$yearFrom$yearTo
                        val ratingFrom = keyword.takeWhile { it != '-' }
                        val ratingTo =
                            keyword.dropWhile { it != '-' }.drop(1).takeWhile { it != '+' }
                        val order = keyword.dropWhile { it != '+' }.drop(1).takeWhile { it != '*' }
                        val type = keyword.dropWhile { it != '*' }.drop(1).takeWhile { it != '#' }
                        val yearFrom = keyword.dropLast(4).takeLast(4)
                        val yearTo = keyword.takeLast(4)

                        repository.getSearchFilmsBySettings(
                            country,
                            genre,
                            order,
                            type,
                            ratingFrom,
                            ratingTo,
                            yearFrom,
                            yearTo,
                            page
                        )
                    }
                    else -> repository.getRandomListOfFilms(country, genre, page)
                }
                LoadResult.Page(
                    data = response.items,
                    prevKey = if (page > 1) page - 1 else null,
                    nextKey = if (response.items.isNotEmpty()) page + 1 else null
                )
            } catch (e: IOException) {
                LoadResult.Error(e)
            } catch (e: HttpException) {
                LoadResult.Error(e)
            }
        }
    }
}


