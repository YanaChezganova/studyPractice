package com.example.skillcinema.ui.support

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.skillcinema.databinding.ArrowInCircleBinding
import javax.inject.Inject

class FooterAdapter @Inject constructor() : RecyclerView.Adapter<ArrowViewHolder>() {
    private lateinit var header: String
    private lateinit var idOfRequest: String
    private var country = 0
    private var genre = 0
    private var pagedRequest = false
    private lateinit var onAllClick: (String, String, Int, Int, Boolean) -> Unit
    fun setData(
        header: String,
        idOfRequest: String,
        country: Int,
        genre: Int,
        pagedRequest: Boolean,
        onAllClick: (String, String, Int, Int, Boolean) -> Unit
    ) {
        this.onAllClick = onAllClick
        this.header = header
        this.idOfRequest = idOfRequest
        this.country = country
        this.genre = genre
        this.pagedRequest = pagedRequest
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArrowViewHolder {
        return ArrowViewHolder(
            ArrowInCircleBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ArrowViewHolder, position: Int) {
        holder.binding.root.setOnClickListener {
            onAllClick(
                header,
                idOfRequest,
                country,
                genre,
                pagedRequest
            )
        }

    }

    override fun getItemCount(): Int = 1
}

class ArrowViewHolder @Inject constructor(val binding: ArrowInCircleBinding) :
    RecyclerView.ViewHolder(binding.root)