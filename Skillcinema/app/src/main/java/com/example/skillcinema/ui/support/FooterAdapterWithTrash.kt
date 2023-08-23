package com.example.skillcinema.ui.support

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.skillcinema.R
import com.example.skillcinema.databinding.ArrowInCircleBinding
import javax.inject.Inject

class FooterAdapterWithTrash @Inject constructor() : RecyclerView.Adapter<ArrowViewHolder>() {
    private lateinit var deleteAllFilmsFromBase: () -> Unit
    fun setData(deleteAllFilmsFromBase: () -> Unit) {
        this.deleteAllFilmsFromBase = deleteAllFilmsFromBase
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
        with(holder.binding) {
            textDescription.text = "Очистить историю"
            icon.setImageResource(R.drawable.ic_trash)
        }
        holder.binding.root.setOnClickListener { deleteAllFilmsFromBase() }

    }

    override fun getItemCount(): Int = 1
}