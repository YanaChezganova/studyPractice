package com.example.contact.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.contact.Application
import com.example.contact.R
import com.example.contact.data.Contact
import com.example.contact.data.ContactDao
import com.example.contact.data.ContactMinimal
import com.example.contact.data.State
import com.example.contact.databinding.FragmentMainBinding
import com.example.contact.support.AdapterRecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private var bundle = Bundle()
    private lateinit var contactRecycleView: RecyclerView
    private lateinit var contactAdapter: AdapterRecyclerView
    private val viewModel by viewModels<MainViewModel>()
    {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val contactDao: ContactDao =
                    (activity?.application as Application).contactList.contactDao()
                return MainViewModel(contactDao) as T
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        viewModel.getContactListFromDB()
        contactAdapter = AdapterRecyclerView()
        contactRecycleView = binding.contactsVerticalRecyclerView
        contactRecycleView.adapter = contactAdapter
        val scope = CoroutineScope(Dispatchers.Default)
        var listOfPerson = emptyList<ContactMinimal>()
        viewLifecycleOwner.lifecycleScope.launch {
            delay(100)
            viewModel.contactsFromDB.collect {
                listOfPerson = it
                println("list of person 1 $it")
                contactAdapter.setData(it, ::onItemClick)
            }
        }
        val listOfContacts = mutableListOf<ContactMinimal>()

        viewLifecycleOwner.lifecycleScope.launch {
            delay(2000)
            if (listOfPerson.isEmpty()) {

                scope.launch {
                    viewModel.loadContactsFromNetwork()
                }
                delay(500)
                scope.launch {
                    viewModel.contactsFromNetwork.collect {
                        it.forEach { contact ->
                            listOfContacts.add(contactMinimiser(contact))
                        }
                        println("list of person one way $it")
                    }
                }
                delay(500)
                contactAdapter.setData(listOfContacts, ::onItemClick)
            }
            viewModel.stateOfView.collect { state ->
                when (state) {
                    State.Ready -> {
                        binding.progressBar.visibility = View.GONE
                        contactAdapter.setData(listOfContacts, ::onItemClick)
                    }
                    State.Error -> {
                        binding.progressBar.visibility = View.GONE
                        findNavController().navigate(R.id.action_global_bottomFragmentMistake)
                    }
                    State.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    State.Done -> {
                        viewModel.getContactListFromDB()
                        binding.progressBar.visibility = View.GONE
                        contactAdapter.setData(listOfContacts, ::onItemClick)
                    }
                }
            }
        }
        binding.buttonReload.setOnClickListener {
            lifecycleScope.launch {
                viewModel.repository.deleteContacts()
                findNavController().navigate(R.id.action_global_mainFragment)
            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onItemClick(name: String, lastName: String) {
        bundle = Bundle().apply {
            putString("name", name)
            putString("lastName", lastName)
        }
        findNavController().navigate(R.id.action_global_blankFragment, bundle)
    }

    fun contactMinimiser(contact: Contact): ContactMinimal {
        with(contact) {
            return ContactMinimal(
                name.title,
                name.first,
                name.last,
                phone,
                location.country,
                location.city,
                picture.thumbnail
            )
        }
    }
}

