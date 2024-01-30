package com.example.contact.ui.main

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import com.example.contact.R
import com.example.contact.databinding.BottomDialogMistakeBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

private const val HEIGHT = 228

class BottomFragmentMistake : BottomSheetDialogFragment() {
    lateinit var binding: BottomDialogMistakeBinding
    override fun getTheme() = R.style.AppBottomSheetDialogTheme

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = BottomDialogMistakeBinding
            .bind(inflater.inflate(R.layout.bottom_dialog_mistake, container))
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStart() {
        super.onStart()

        val density = requireContext().resources.displayMetrics.density

        dialog?.let {
            val bottomSheet =
                it.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
            val behavior = BottomSheetBehavior.from(bottomSheet)

            behavior.peekHeight = (HEIGHT * density).toInt()
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            binding.buttonClose.setOnClickListener {
                behavior.isHideable = true
                behavior.setState(BottomSheetBehavior.STATE_HIDDEN)
            }
        }
    }
}