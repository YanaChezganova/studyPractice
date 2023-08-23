package com.example.skillcinema.ui.support

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.skillcinema.R
import com.example.skillcinema.databinding.SeasonTextItemBinding
import com.example.skillcinema.ui.models.Episode
import javax.inject.Inject

class TextItemAdapterRecyclerView @Inject constructor() :
    RecyclerView.Adapter<SeasonItemViewHolder>() {
    private var data: List<Episode> = emptyList()

    fun setData(data: List<Episode>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeasonItemViewHolder {
        return SeasonItemViewHolder(
            SeasonTextItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: SeasonItemViewHolder, position: Int) {
        val item = data.getOrNull(position)

        with(holder.binding) {
            var title = " "
            if (item?.nameRu != null) {
                title = item.nameRu
                if (item.nameEn != null) {
                    textSeriesNameEn.text = item.nameEn
                    textSeriesNameEn.visibility = View.VISIBLE
                }
            } else {
                if (item?.nameEn != null)
                    title = item.nameEn
                else title = " "
            }
            val text = textSeriesNumber.context.resources.getString(
                R.string.number_series_and_title,
                item?.episodeNumber,
                title
            )
            textSeriesNumber.text = text

            textSeriesData.text = item?.releaseDate
        }
    }

    override fun getItemCount(): Int = data.size
}

class SeasonItemViewHolder @Inject constructor(val binding: SeasonTextItemBinding) :
    RecyclerView.ViewHolder(binding.root)

