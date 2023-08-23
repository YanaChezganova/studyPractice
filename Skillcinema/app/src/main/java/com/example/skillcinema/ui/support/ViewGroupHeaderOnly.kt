package com.example.skillcinema.ui.support

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.skillcinema.databinding.ViewGroupHeaderSmallTextSizeBinding

class ViewGroupHeaderOnly
@JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attributeSet, defStyleAttr) {
    val binding = ViewGroupHeaderSmallTextSizeBinding.inflate(LayoutInflater.from(context))

    init {
        addView(binding.root)
    }

    fun setHeader(header: String) {
        binding.textHeader.text = header
    }
}