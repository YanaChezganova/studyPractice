package com.example.skillcinema.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.example.skillcinema.R
import com.example.skillcinema.ui.data.Constants.NAME
import com.example.skillcinema.ui.data.Constants.POSITION
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DialogConfirmationDeleting : DialogFragment() {
    private var collectionName: String? = null
    private var position: Long? = 0
    private var bundle = Bundle()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView: View =
            layoutInflater.inflate(
                R.layout.dialog_confirmation_deleting_collection,
                container,
                false
            )
        arguments.let {
            collectionName = it?.getString(NAME)
            position = it?.getLong(POSITION)
        }
        val dialogDescription = rootView.findViewById<TextView>(R.id.description_cancel_text)
        dialogDescription.text =
            resources.getString(R.string.description_of_deleting, collectionName)
        val buttonCancel = rootView.findViewById<Button>(R.id.button_cancel)
        buttonCancel.setOnClickListener {
            dismiss()
        }
        val buttonDeleteCollection = rootView.findViewById<Button>(R.id.button_delete)
        buttonDeleteCollection.setOnClickListener {
            bundle = Bundle().apply {
                putString("yes", "yes")
                putLong(POSITION, position!!)
            }
            Toast.makeText(
                requireContext(),
                "Коллекция ${collectionName.toString()} удалена",
                Toast.LENGTH_SHORT
            ).show()
            dismiss()
            findNavController().navigate(R.id.action_global_navigation_profile, bundle)
        }
        return rootView
    }
}
