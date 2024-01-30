package com.example.contact.support

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.contact.R
import com.example.contact.data.Person
import com.example.contact.databinding.ItemContactBinding
import com.google.android.material.shape.CornerFamily

class AdapterRecyclerView: RecyclerView.Adapter<ContactViewHolder>() {
    private var data: List<Person> = emptyList()
    private lateinit var onClick: (Int) -> Unit
    fun setData(data: List<Person>, onClick: (Int) -> Unit) {
        this.onClick = onClick
        this.data = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        return ContactViewHolder(
            ItemContactBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val item = data.getOrNull(position)
        with(holder.binding) {
                title.text = item?.title
                firstAndLastName.text = firstAndLastName.context.getString(R.string.person_name, item?.first, item?.last)
                phone.text = item?.address?.phone
                address.text = address.context.getString(R.string.person_name, item?.address?.country, item?.address?.city)
            personImage.shapeAppearanceModel
                .toBuilder()
                .setTopRightCorner(CornerFamily.ROUNDED, 16.0F)
                .setTopLeftCorner(CornerFamily.ROUNDED, 16.0F)
                .setBottomLeftCorner(CornerFamily.ROUNDED, 16.0F)
                .setBottomRightCorner(CornerFamily.ROUNDED, 16.0F)
                .build()
            item?.let {
                Glide.with(personImage.context)
                    .load(item.medium)
                    .into(personImage)
            }
        }
        holder.binding.root.setOnClickListener { item?.let { onClick(item.id) } }

    }

    override fun getItemCount(): Int = data.size
}

class ContactViewHolder (val binding: ItemContactBinding) :
    RecyclerView.ViewHolder(binding.root)