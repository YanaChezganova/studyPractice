package com.example.skillcinema.ui.support

import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.skillcinema.databinding.CardOfFilmWhiteRateLittleBinding
import com.example.skillcinema.ui.models.Movie
import com.google.android.material.shape.CornerFamily
import javax.inject.Inject

class PagedAdapterWhiteRatingWithWatchedFilms @Inject constructor(
) : PagingDataAdapter<Movie, MovieWhiteRatingViewHolder>(MovieDiffUtilCallback) {
    private lateinit var onClick: (Movie) -> Unit
    private lateinit var data: PagingData<List<Movie>>
    private var watchedId: List<Int> = emptyList()
    private var key: Boolean? = null
    fun submitData(pagingData: PagingData<List<Movie>>) {
        this.data = pagingData
        notifyDataSetChanged()
    }

    fun loadParameters(watchedId: List<Int>, key: Boolean?, onClick: (Movie) -> Unit) {
        this.onClick = onClick
        this.watchedId = watchedId
        this.key = key
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieWhiteRatingViewHolder {
        return MovieWhiteRatingViewHolder(
            CardOfFilmWhiteRateLittleBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: MovieWhiteRatingViewHolder, position: Int) {
        val item = getItem(position)
        with(holder.binding) {
            val filmId = if (item?.kinopoiskId!! > item.filmId)
                item.kinopoiskId
            else item.filmId
            when (key) {
                true -> {
                    if (!watchedId.contains(filmId)) {
                        loadView(holder, item)
                        holder.itemView.visibility = VISIBLE
                        blueShadow.visibility = View.INVISIBLE
                    } else {
                        loadView(holder, item)
                        holder.itemView.visibility = VISIBLE
                        blueShadow.visibility = View.VISIBLE
                    }
                }
                else -> {
                    println("yes")
                    if (watchedId.contains(filmId)) {
                        holder.itemView.visibility = GONE
                        holder.itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
                    } else {
                        loadView(holder, item)
                    holder.itemView.visibility = VISIBLE
                    holder.itemView.layoutParams =
                        RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)}
                }
            }
        }
        holder.binding.root.setOnClickListener { item?.let { onClick(item) } }
    }

    private fun loadView(holder: MovieWhiteRatingViewHolder, item: Movie?) {
        with(holder.binding) {
            if (item?.nameRu !== null)
                filmName.text = item.nameRu
            else {
                if (item?.nameEn !== null)
                    filmName.text = item.nameEn
                else filmName.text = item?.nameOriginal
            }

            if (item?.rating !== null) rating.text = item.rating
            else {
                if (item?.ratingKinopoisk != null)
                    rating.text = item.ratingKinopoisk.toString()
                else {
                    if (item?.ratingImdb != null) {
                        rating.text = item.ratingImdb.toString()
                    } else {
                        rating.text = "1.0"
                    }
                }
            }
            var year = ""
            if (item?.year != null) year = item.year
            var genre = ""
            if (item?.genres != null) genre = item.genres[0].genres
            var descriptionText = ""
            descriptionText = year + ", " + genre
            filmYearGenre.text = descriptionText

            filmPoster.shapeAppearanceModel
                .toBuilder()
                .setTopRightCorner(CornerFamily.ROUNDED, 16.0F)
                .setTopLeftCorner(CornerFamily.ROUNDED, 16.0F)
                .setBottomLeftCorner(CornerFamily.ROUNDED, 16.0F)
                .setBottomRightCorner(CornerFamily.ROUNDED, 16.0F)
                .build()
            item?.let {
                Glide.with(filmPoster.context)
                    .load(it.posterUrl)
                    .into(filmPoster)
            }

        }
    }
}