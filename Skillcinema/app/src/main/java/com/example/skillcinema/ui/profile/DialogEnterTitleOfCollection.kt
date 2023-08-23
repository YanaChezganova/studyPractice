package com.example.skillcinema.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.example.skillcinema.R
import dagger.hilt.android.AndroidEntryPoint

private const val TITLE = "folderTitle"
private const val UNIQUE_NAME = "unique"

@AndroidEntryPoint
class DialogEnterTitleOfCollection : DialogFragment() {
    private var bundle = Bundle()

    private val date = System.currentTimeMillis()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView: View =
            layoutInflater.inflate(R.layout.dialog_enter_title_of_collection, container, false)
        val buttonClose = rootView.findViewById<ImageButton>(R.id.button_close)
        buttonClose.setOnClickListener {
            dismiss()
        }
        val buttonMakeCollection = rootView.findViewById<Button>(R.id.button_make_choice)
        buttonMakeCollection.setOnClickListener {
            val editText = rootView.findViewById<EditText>(R.id.edit_text)
            val title = editText.text.toString()
            bundle = Bundle().apply {
                putString(TITLE, title)
                putLong(UNIQUE_NAME, date)
            }
            dismiss()
            findNavController().navigate(R.id.action_global_navigation_profile, bundle)
        }
        return rootView
    }
}
