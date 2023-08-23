package com.example.skillcinema.ui.support

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.skillcinema.databinding.CardOfFilmWhiteRateLittleBinding
import com.example.skillcinema.ui.data.FilmDB
import com.google.android.material.shape.CornerFamily
import javax.inject.Inject

class DbMovieSecondViewAdapterRecyclerView @Inject constructor() :
    RecyclerView.Adapter<DbMovieSecondViewHolder>() {
    private var data: List<FilmDB> = emptyList()
    private lateinit var onClick: (FilmDB) -> Unit
    fun setData(data: List<FilmDB>, onClick: (FilmDB) -> Unit) {
        this.data = data
        this.onClick = onClick
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DbMovieSecondViewHolder {
        return DbMovieSecondViewHolder(
            CardOfFilmWhiteRateLittleBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: DbMovieSecondViewHolder, position: Int) {
        val item = data.getOrNull(position)
        with(holder.binding) {
            filmName.text = if (item?.filmName != null)
                item.filmName else ""
            rating.text = if (item?.rating != null) item.rating
            else 1.0.toString()
            filmYearGenre.text = if (item?.genre != null) item.genre else ""
                filmPoster.shapeAppearanceModel
                .toBuilder()
                .setTopRightCorner(CornerFamily.ROUNDED, 16.0F)
                .setTopLeftCorner(CornerFamily.ROUNDED, 16.0F)
                .setBottomLeftCorner(CornerFamily.ROUNDED, 16.0F)
                .setBottomRightCorner(CornerFamily.ROUNDED, 16.0F)
                .build()
            if (item?.posterUrl != null) {
                item.let {
                    Glide.with(filmPoster.context)
                        .load(it.posterUrl)
                        .into(filmPoster)
                }
            }
        }
        holder.binding.root.setOnClickListener { item?.let { onClick(item) } }
    }

    override fun getItemCount(): Int = data.size
}

class DbMovieSecondViewHolder @Inject constructor(val binding: CardOfFilmWhiteRateLittleBinding) :
    RecyclerView.ViewHolder(binding.root)