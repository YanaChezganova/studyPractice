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

class PhotoBigAdapterRecyclerView @Inject constructor() :
    RecyclerView.Adapter<PhotoBigViewHolder>() {
    private var data: List<Photo> = emptyList()
    var dataSize = data.size
    private lateinit var onClick: (Photo, Int) -> Unit
    fun setData(data: List<Photo>, onClick: (Photo, Int) -> Unit) {
        this.data = data
        this.onClick = onClick
        dataSize = data.size
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoBigViewHolder {
        return PhotoBigViewHolder(
            GalleryPhotoItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: PhotoBigViewHolder, position: Int) {
        val item = data.getOrNull(position)
        with(holder.binding) {
            if ((position + 1) % 3 == 0) {
                galleryBigPhoto.visibility = View.VISIBLE
                galleryBigPhoto.shapeAppearanceModel = galleryBigPhoto.shapeAppearanceModel
                    .toBuilder()
                    .setTopRightCorner(CornerFamily.ROUNDED, 16.0F)
                    .setTopLeftCorner(CornerFamily.ROUNDED, 16.0F)
                    .setBottomLeftCorner(CornerFamily.ROUNDED, 16.0F)
                    .setBottomRightCorner(CornerFamily.ROUNDED, 16.0F)
                    .build()
                item?.let {
                    Glide.with(galleryBigPhoto.context)
                        .load(it.previewUrl)
                        .into(galleryBigPhoto)
                }
            } else {
                galleryPhoto.shapeAppearanceModel
                    .toBuilder()
                    .setTopRightCorner(CornerFamily.ROUNDED, 16.0F)
                    .setTopLeftCorner(CornerFamily.ROUNDED, 16.0F)
                    .setBottomLeftCorner(CornerFamily.ROUNDED, 16.0F)
                    .setBottomRightCorner(CornerFamily.ROUNDED, 16.0F)
                    .build()
                galleryPhoto.visibility = View.VISIBLE
                item?.let {
                    Glide.with(galleryPhoto.context)
                        .load(it.previewUrl)
                        .into(galleryPhoto)
                }
            }
        }
        holder.binding.root.setOnClickListener { item?.let { onClick(item, position) } }
    }

    override fun getItemCount(): Int = data.size
}

class PhotoBigViewHolder @Inject constructor(val binding: GalleryPhotoItemBinding) :
    RecyclerView.ViewHolder(binding.root)

