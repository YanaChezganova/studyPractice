package com.example.skillcinema.ui.support

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.skillcinema.R
import com.example.skillcinema.databinding.ViewGroupTitlesOfFoldersBinding

class ViewGroupCollectionHorizontalIconText
@JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attributeSet, defStyleAttr) {
    val binding = ViewGroupTitlesOfFoldersBinding.inflate(LayoutInflater.from(context))

    init {
        addView(binding.root)
    }

    fun setTitleCollection(title: String) {
        binding.textNameFolder.text = title
    }

    fun setTextAllElementsInvisible() {
        binding.textCountOfFilms.visibility = View.INVISIBLE
    }

    fun setCountOfElements(count: Int) {
        binding.textCountOfFilms.text = count.toString()
    }

    fun setIcon(type: Int) {
        // 1 - ic_square, 2 - ic_square_with_mark, 3 - ic_plus
        when (type) {
            1 -> binding.icSquare.setImageResource(R.drawable.ic_square)
            2 -> binding.icSquare.setImageResource(R.drawable.ic_square_with_mark)
            else -> binding.icSquare.setImageResource(R.drawable.ic_plus)
        }
    }
}