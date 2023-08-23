package com.example.skillcinema.ui.support

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.skillcinema.databinding.CardOfFilmBlueRateLittleBinding
import com.example.skillcinema.ui.models.Movie
import com.google.android.material.shape.CornerFamily
import javax.inject.Inject


class AdapterRecyclerView @Inject constructor() : RecyclerView.Adapter<MovieViewHolder>() {
    private var data: List<Movie> = emptyList()
    private lateinit var onClick: (Movie) -> Unit
    fun setData(data: List<Movie>, onClick: (Movie) -> Unit) {
        this.onClick = onClick
        this.data = data
        notifyDataSetChanged()
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
        val item = data.getOrNull(position)
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
            if (item?.genres != null) filmGenre.text = item.genres[0].genres
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

class MovieViewHolder @Inject constructor(val binding: CardOfFilmBlueRateLittleBinding) :
    RecyclerView.ViewHolder(binding.root)