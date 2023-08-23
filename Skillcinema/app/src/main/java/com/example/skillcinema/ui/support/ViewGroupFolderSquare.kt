package com.example.skillcinema.ui.support

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.skillcinema.R
import com.example.skillcinema.databinding.ViewGroupCollectionBinding

class ViewGroupFolderSquare
@JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attributeSet, defStyleAttr) {
    val binding = ViewGroupCollectionBinding.inflate(LayoutInflater.from(context))

    init {
        addView(binding.root)
    }

    fun setNameFolderCollection(name: String) {
        binding.textNameFolder.text = name
    }

    fun setCountOfFilms(count: Int) {
        binding.filmCount.text = count.toString()
    }

    fun setIcon(type: Int) {
        when (type) {
            1 -> binding.buttonHeart.setImageResource(R.drawable.ic_heart_black)
            2 -> binding.buttonHeart.setImageResource(R.drawable.ic_flag_black)
            else -> binding.buttonHeart.setImageResource(R.drawable.ic_person_black)
        }
    }

    fun setCloseButtonVisible() {
        binding.buttonClose.visibility = View.VISIBLE
    }
}