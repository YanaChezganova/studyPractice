package com.example.skillcinema.ui.support

import androidx.recyclerview.widget.DiffUtil
import com.example.skillcinema.ui.models.Movie
import com.example.skillcinema.ui.models.Photo

object MovieDiffUtilCallback: DiffUtil.ItemCallback<Movie>() {
    override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean = oldItem.kinopoiskId == newItem.kinopoiskId

    override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean = oldItem == newItem
}

object PhotoDiffUtilCallback: DiffUtil.ItemCallback<Photo>() {
    override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean = oldItem.imageUrl == newItem.imageUrl

    override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean = oldItem == newItem
}
