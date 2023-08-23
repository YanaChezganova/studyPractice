package com.example.skillcinema.ui.support

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.skillcinema.databinding.CardOfFilmWhiteRateLittleBinding
import com.example.skillcinema.ui.models.Movie
import com.google.android.material.shape.CornerFamily
import javax.inject.Inject

class MovieWhiteRatingAdapterRecyclerView @Inject constructor() :
    RecyclerView.Adapter<MovieWhiteRatingViewHolder>() {
    private var data: List<Movie> = emptyList()
    private lateinit var onClick: (Movie) -> Unit
    fun setData(data: List<Movie>, onClick: (Movie) -> Unit) {
        this.data = data
        this.onClick = onClick
        notifyDataSetChanged()
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
        val item = data.getOrNull(position)
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
            var descriptionText = ""
            if (item?.description != null) descriptionText = item.description
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
        holder.binding.root.setOnClickListener { item?.let { onClick(item) } }

    }

    override fun getItemCount(): Int = data.size
}

class MovieWhiteRatingViewHolder @Inject constructor(val binding: CardOfFilmWhiteRateLittleBinding) :
    RecyclerView.ViewHolder(binding.root)