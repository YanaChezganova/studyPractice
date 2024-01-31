package com.example.contact.support

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.example.contact.R
import com.example.contact.data.ContactMinimal
import com.example.contact.data.Person
import com.example.contact.databinding.ItemContactBinding
import com.google.android.material.shape.CornerFamily

class AdapterRecyclerView: RecyclerView.Adapter<ContactViewHolder>() {
    private var data: List<ContactMinimal> = emptyList()
    private lateinit var onClick: (String, String) -> Unit
    fun setData(data: List<ContactMinimal>, onClick: (String, String) -> Unit) {
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
    @GlideModule
    class GlideApp : AppGlideModule()
    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val item = data.getOrNull(position)
        with(holder.binding) {
                title.text = item?.title
                firstAndLastName.text = firstAndLastName.context.getString(R.string.person_name, item?.firstName, item?.lastName)
                phone.text = item?.phone
                address.text = address.context.getString(R.string.person_name, item?.country, item?.city)
            item?.let {
                Glide.with(personImage.context)
                    .load(item.thumbnailUrl)
                    .into(personImage)
            }
        }
        holder.binding.root.setOnClickListener { item?.let { onClick(item.firstName, item.lastName) } }

    }

    override fun getItemCount(): Int = data.size
}

class ContactViewHolder (val binding: ItemContactBinding) :
    RecyclerView.ViewHolder(binding.root)