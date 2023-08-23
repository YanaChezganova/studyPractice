package com.example.skillcinema.ui.support

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.skillcinema.R
import com.example.skillcinema.databinding.ViewGroupHeaderHorizontalListBinding
import com.example.skillcinema.ui.data.Constants.COUNTRY
import com.example.skillcinema.ui.data.Constants.FILM_ID
import com.example.skillcinema.ui.data.Constants.GENRE
import com.example.skillcinema.ui.data.Constants.HEADER
import com.example.skillcinema.ui.data.Constants.PAGED
import com.example.skillcinema.ui.data.Constants.REQUEST_ID
import com.example.skillcinema.ui.models.Movie
import javax.inject.Inject

class ViewGroupFilmsListWithArrowInEnd
@JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attributeSet, defStyleAttr) {
    val binding = ViewGroupHeaderHorizontalListBinding.inflate(LayoutInflater.from(context))
    private lateinit var moviesRecycleView: RecyclerView

    @Inject
    lateinit var footerAdapter: FooterAdapter

    @Inject
    lateinit var moviesAdapter: AdapterRecyclerViewWithWatchedFilms
    private var bundle = Bundle()

    init {
        addView(binding.root)
    }

    fun setHeader(header: String) {
        binding.textHeader.text = header
    }

    fun makeListWithFilms(
        listWithFilms: List<Movie>,
        listWithWatchedId: List<Int>, header: String,
        idOfRequest: String,
        country: Int,
        genre: Int,
        pagedRequest: Boolean
    ) {
        moviesAdapter = AdapterRecyclerViewWithWatchedFilms()
        footerAdapter = FooterAdapter()
        val concatAdapter = ConcatAdapter(
            moviesAdapter,
            footerAdapter
        )
        moviesRecycleView = binding.viewGroupHorizontalRecyclerView
        moviesRecycleView.adapter = concatAdapter
        moviesAdapter.setData(listWithFilms, listWithWatchedId, ::onItemClick)
        footerAdapter.setData(header, idOfRequest, country, genre, pagedRequest, ::onAllClick)
    }

    fun onItemClick(item: Movie) {
        if (item.kinopoiskId > item.filmId)
            bundle = Bundle().apply {
                putInt(FILM_ID, item.kinopoiskId)
            }
        else bundle = Bundle().apply {
            putInt(FILM_ID, item.filmId)
        }
        findNavController().navigate(R.id.action_global_cardOfFilm, bundle)
    }

    fun onAllClick(
        header: String,
        idOfRequest: String,
        country: Int,
        genre: Int,
        pagedRequest: Boolean
    ) {
        bundle = Bundle().apply {
            putString(HEADER, header)
            putString(REQUEST_ID, idOfRequest)
            putInt(COUNTRY, country)
            putInt(GENRE, genre)
            putBoolean(PAGED, pagedRequest)
        }
        findNavController().navigate(R.id.action_global_listOfFilms, bundle)
    }
}