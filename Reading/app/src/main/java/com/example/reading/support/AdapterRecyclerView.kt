package com.example.reading.support

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.marginStart
import androidx.recyclerview.widget.RecyclerView
import com.example.reading.R
import com.example.reading.databinding.RateItemBinding
import com.google.android.material.shape.CornerFamily

class AdapterRecyclerView: RecyclerView.Adapter<IconsViewHolder>() {

    private lateinit var onClick: (Int) -> Unit
    fun setData(onClick: (Int) -> Unit) {
        this.onClick = onClick
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconsViewHolder {
        return IconsViewHolder(
            RateItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: IconsViewHolder, position: Int) {
        with(holder.binding) {
           when (position) {
                0 -> {iconText.text = iconText.context.getString(R.string.ma)
                    iconImage.background = AppCompatResources.getDrawable(iconImage.context, R.drawable.menu_girl)}
                1 -> { iconText.text = iconText.context.getString(R.string.sha)
                    iconImage.background = AppCompatResources.getDrawable(iconImage.context, R.drawable.menu_park)}

               2 ->{ iconText.text = iconText.context.getString(R.string.ne)
                   iconImage.background = AppCompatResources.getDrawable(iconImage.context, R.drawable.menu_kite)}
               3 ->{ iconText.text = iconText.context.getString(R.string.lu)
                   iconImage.background = AppCompatResources.getDrawable(iconImage.context, R.drawable.menu_mom)}
               4 -> { iconText.text = iconText.context.getString(R.string.threeLetter)
                   iconText.textSize = 55.0F

                   iconImage.background = AppCompatResources.getDrawable(iconImage.context, R.drawable.menu_tree)}
               5 -> { iconText.text = iconText.context.getString(R.string.fourLetter)
                   iconText.textSize = 50.0F
                   iconImage.background = AppCompatResources.getDrawable(iconImage.context, R.drawable.menu_children)}
               6 -> { iconText.text = iconText.context.getString(R.string.withAdditional)
                   iconImage.background = AppCompatResources.getDrawable(iconImage.context, R.drawable.menu_goose)}
               7 -> { iconText.text = iconText.context.getString(R.string.intonation)
                   iconText.textSize = 55.0F
                   iconImage.background = AppCompatResources.getDrawable(iconImage.context, R.drawable.menu_cat)}
           }

            iconImage.shapeAppearanceModel
                .toBuilder()
                .setTopRightCorner(CornerFamily.ROUNDED, 16.0F)
                .setTopLeftCorner(CornerFamily.ROUNDED, 16.0F)
                .setBottomLeftCorner(CornerFamily.ROUNDED, 16.0F)
                .setBottomRightCorner(CornerFamily.ROUNDED, 16.0F)
                .build()

        }
        holder.binding.root.setOnClickListener { position.let { onClick(position) } }

    }

    override fun getItemCount(): Int = 8
}

class IconsViewHolder(val binding: RateItemBinding) :
    RecyclerView.ViewHolder(binding.root)