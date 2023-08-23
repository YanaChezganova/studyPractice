package com.example.skillcinema.ui.support

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.skillcinema.R
import com.example.skillcinema.databinding.TextTableLayoutBinding

class ViewGroupTableYearChoice
@JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attributeSet, defStyleAttr) {
    val binding = TextTableLayoutBinding.inflate(LayoutInflater.from(context))
    private var yearValue = "2000"

    init {
        addView(binding.root)
    }

    val listOfView: List<TextView> = listOf(
        binding.textView1, binding.textView2,
        binding.textView3, binding.textView4, binding.textView5, binding.textView6,
        binding.textView7, binding.textView8, binding.textView9, binding.textView10,
        binding.textView11, binding.textView12
    )
    private var sinceYear = 0
    fun setPeriod(since: Int) {
        binding.textView0.text = resources
            .getString(R.string.year_to_year, since, since + 11)
        sinceYear = since
        setValuesToTable()
    }

    fun shiftToFuture() {
        clearSelection()
        sinceYear += 3
        setPeriod(sinceYear)
        setValuesToTable()
    }

    fun shiftToPast() {
        clearSelection()
        sinceYear -= 3
        setPeriod(sinceYear)
        setValuesToTable()
    }

    private fun setValuesToTable() {
        var counter = 0
        listOfView.onEach {
            it.text = resources.getString(R.string.year, sinceYear + counter)
            counter++
        }
    }

    private fun clearSelection() {
        listOfView.onEach {
            it.setBackgroundResource(R.color.white)
        }
    }

    fun onClick(view: TextView) {
        view.setOnClickListener {
            clearSelection()
            yearValue = view.text.toString()
            view.setBackgroundResource(R.drawable.gradient_blue)
        }
    }

    fun onMakeChoice(): String {
        return yearValue
    }
}