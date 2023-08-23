package com.example.skillcinema.ui.support

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.skillcinema.databinding.CountryGenreTextItemBinding
import javax.inject.Inject

class SearchTextItemAdapterRecyclerView @Inject constructor() :
    RecyclerView.Adapter<CountryGenreItemViewHolder>() {
    private var data: List<String> = emptyList()
    private lateinit var onClick: (String) -> Unit
    fun setData(data: List<String>, onClick: (String) -> Unit) {
        this.data = data
        this.onClick = onClick
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryGenreItemViewHolder {
        return CountryGenreItemViewHolder(
            CountryGenreTextItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: CountryGenreItemViewHolder, position: Int) {
        val item = data.getOrNull(position)

        with(holder.binding) {
            titleWord.text = item

        }
        holder.binding.root.setOnClickListener { item?.let { onClick(item) } }
    }

    override fun getItemCount(): Int = data.size
}

class CountryGenreItemViewHolder @Inject constructor(val binding: CountryGenreTextItemBinding) :
    RecyclerView.ViewHolder(binding.root)

