package com.example.myapplication1

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import java.io.IOException


class ListPagingSource(private val viewModel: MainViewModel): PagingSource<Int, Character>() {
    override fun getRefreshKey(state: PagingState<Int, Character>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int,Character> {
        val position = params.key ?: 1
        return try {
            val response = RetrofitServices.charactersApi.getCharacters(position)
            val repos = response.results
            val nextKey = if (repos.isEmpty()) {
                null
            } else {
                position + (params.loadSize / 20)
            }
            LoadResult.Page(
                data = repos,
                prevKey = if (position == 1) null else position - 1,
                nextKey = nextKey
            )
        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        }
    }
companion object {
    fun pager(viewModel: MainViewModel) = Pager(config = PagingConfig(pageSize = 20),
    pagingSourceFactory = {ListPagingSource(viewModel)})
}
}

