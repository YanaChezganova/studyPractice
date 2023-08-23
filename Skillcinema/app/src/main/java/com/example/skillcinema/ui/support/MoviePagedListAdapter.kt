package com.example.skillcinema.ui.support

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import com.bumptech.glide.Glide
import com.example.skillcinema.databinding.CardOfFilmBlueRateLittleBinding
import com.example.skillcinema.ui.models.Movie
import com.google.android.material.shape.CornerFamily
import javax.inject.Inject

class MoviePagedListAdapter @Inject constructor(
) : PagingDataAdapter<Movie, MovieViewHolder>(MovieDiffUtilCallback) {
    private lateinit var data: PagingData<List<Movie>>
    private lateinit var onClick: (Movie) -> Unit
    fun submitData(pagingData: PagingData<List<Movie>>) {
        this.data = pagingData
        notifyDataSetChanged()
    }

    fun loadParameters(onClick: (Movie) -> Unit) {
        this.onClick = onClick

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        return MovieViewHolder(
            CardOfFilmBlueRateLittleBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val item = getItem(position)
        with(holder.binding) {
            if (item?.nameRu !== null)
                filmName.text = item.nameRu
            else {
                if (item?.nameEn !== null)
                    filmName.text = item.nameEn
                else filmName.text = item?.nameOriginal
            }

            if (item?.rating !== null) filmRating.text = item.rating
            else {
                if (item?.ratingKinopoisk != null)
                    filmRating.text = item.ratingKinopoisk.toString()
                else {
                    if (item?.ratingImdb != null) {
                        filmRating.text = item.ratingImdb.toString()
                    } else {
                        filmRating.text = "1.0"
                    }
                }
            }
            filmGenre.text = item?.genres?.get(0)?.genres
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
        holder.binding.root.setOnClickListener { item?.let { onClick(item) } }
    }

}