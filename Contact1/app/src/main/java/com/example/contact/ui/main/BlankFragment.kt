package com.example.contact.ui.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.contact.Application
import com.example.contact.R
import com.example.contact.data.ContactDao
import com.example.contact.databinding.FragmentBlankBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val NAME = "name"
private const val LAST_NAME = "lastName"

class BlankFragment : Fragment() {
    private var personName: String? = null
    private var personLastName: String? = null
    private var _binding: FragmentBlankBinding? = null
    private val binding get() = _binding!!
    private var bundle = Bundle()
    private val viewModel by viewModels<MainViewModel>()
    {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val contactDao: ContactDao =
                    (activity?.application as Application).contactsList.contactDao()
                return MainViewModel(contactDao) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            personName = it.getString(NAME)
            personLastName = it.getString(LAST_NAME)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBlankBinding.inflate(inflater, container, false)
        viewLifecycleOwner.lifecycleScope.launch {

            val person = viewModel.getPersonByName(personName!!, personLastName!!)
            delay(300)
            binding.personName.text = resources.getString(
                R.string.person_title_and_name,
                person.title,
                person.first,
                person.last
            )

            binding.buttonPhone.text = person.address.phone
            binding.gender.text = person.gender
            binding.personAge.text = resources.getString(R.string.age, person.age)
            binding.personDayOfBirth.text = person.date.take(10)
            with(person.address){
                binding.buttonAddress.text = resources.getString(
                R.string.full_address,
                country,
                state,
                city,
                street,
                homeNumber
            )
            binding.buttonEmail.text = email
            }
            Glide.with(binding.personImage.context)
                .load(person.large)
                .into(binding.personImage)
            binding.buttonPhone.setOnClickListener {
                val phoneNumber = person.address.phone.filter { it.isDigit() }
                val callIntent = Intent(Intent.ACTION_DIAL)
                callIntent.data = Uri.parse("tel:$phoneNumber")
                startActivity(callIntent)
            }
            binding.buttonEmail.setOnClickListener {
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_EMAIL, person.address.email)
                    putExtra(Intent.EXTRA_CC, person.address.email)
                    putExtra(Intent.EXTRA_SUBJECT, "Letter from Contact app")
                    putExtra(Intent.EXTRA_TEXT, "Hi, dear ${person.first}!")
                    type = "text/plain"
                }
                startActivity(sendIntent)
            }


        }
        return binding.root
    }


}