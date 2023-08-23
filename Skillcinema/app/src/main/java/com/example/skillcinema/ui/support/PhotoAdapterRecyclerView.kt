package com.example.skillcinema.ui.support

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.skillcinema.databinding.GalleryPhotoItemBinding
import com.example.skillcinema.ui.models.Photo
import com.google.android.material.shape.CornerFamily
import javax.inject.Inject

class PhotoAdapterRecyclerView @Inject constructor() : RecyclerView.Adapter<PhotoViewHolder>() {
    private var data: List<Photo> = emptyList()
    var dataSize = data.size
    private lateinit var onClick: (Photo) -> Unit
    fun setData(data: List<Photo>, onClick: (Photo) -> Unit) {
        this.data = data
        this.onClick = onClick
        dataSize = data.size
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        return PhotoViewHolder(
            GalleryPhotoItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val item = data.getOrNull(position)
        with(holder.binding) {
            item?.let {
                galleryPhoto.visibility = View.VISIBLE
                galleryPhoto.shapeAppearanceModel
                    .toBuilder()
                    .setTopRightCorner(CornerFamily.ROUNDED, 16.0F)
                    .setTopLeftCorner(CornerFamily.ROUNDED, 16.0F)
                    .setBottomLeftCorner(CornerFamily.ROUNDED, 16.0F)
                    .setBottomRightCorner(CornerFamily.ROUNDED, 16.0F)
                    .build()
                Glide.with(galleryPhoto.context)
                    .load(it.previewUrl)
                    .into(galleryPhoto)
            }
        }
        holder.binding.root.setOnClickListener { item?.let { onClick(item) } }
    }

    override fun getItemCount(): Int = data.size
}

class PhotoViewHolder @Inject constructor(val binding: GalleryPhotoItemBinding) :
    RecyclerView.ViewHolder(binding.root)