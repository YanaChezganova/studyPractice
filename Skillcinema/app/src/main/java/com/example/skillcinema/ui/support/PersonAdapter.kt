package com.example.skillcinema.ui.support

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.skillcinema.databinding.ActorInfoBigViewBinding
import com.example.skillcinema.ui.models.Person
import com.google.android.material.shape.CornerFamily
import javax.inject.Inject

class PersonAdapter @Inject constructor() : RecyclerView.Adapter<PersonBigViewHolder>() {
    private var data: List<Person> = emptyList()
    var dataSize = data.size
    private lateinit var onClick: (Person) -> Unit
    fun setData(data: List<Person>, onClick: (Person) -> Unit) {
        this.data = data
        this.onClick = onClick
        dataSize = data.size
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonBigViewHolder {
        return PersonBigViewHolder(
            ActorInfoBigViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: PersonBigViewHolder, position: Int) {
        val item = data.getOrNull(position)
        with(holder.binding) {
            personNameRu.text = item?.nameRu
            if (item?.description.isNullOrEmpty()) {
                if (item?.professionText?.last()?.compareTo('ы') == 0) {
                    personRole.text = item.professionText!!.dropLast(1)
                }
            } else personRole.text = item?.description
            personImage.shapeAppearanceModel
                .toBuilder()
                .setTopRightCorner(CornerFamily.ROUNDED, 16.0F)
                .setTopLeftCorner(CornerFamily.ROUNDED, 16.0F)
                .setBottomLeftCorner(CornerFamily.ROUNDED, 16.0F)
                .setBottomRightCorner(CornerFamily.ROUNDED, 16.0F)
                .build()
            item?.let {
                Glide.with(personImage.context)
                    .load(it.posterUrl)
                    .into(personImage)
            }
        }
        holder.binding.root.setOnClickListener { item?.let { onClick(item) } }
    }

    override fun getItemCount(): Int = data.size
}

class PersonBigViewHolder @Inject constructor(val binding: ActorInfoBigViewBinding) :
    RecyclerView.ViewHolder(binding.root)