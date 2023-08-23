package com.example.skillcinema.ui.support

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.skillcinema.databinding.ViewGroupHeaderHorizontalListBinding

class ViewGroupFilmsList
@JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attributeSet, defStyleAttr) {
    val binding = ViewGroupHeaderHorizontalListBinding.inflate(LayoutInflater.from(context))

    init {
        addView(binding.root)
    }

    fun setHeader(header: String) {
        binding.textHeader.text = header
    }

    fun setBlueArrowVisible() {
        binding.buttonBlueArrow.visibility = View.VISIBLE
    }

    fun setCountOfElements(count: Int) {
        binding.textAllElements.text = count.toString()
    }

}