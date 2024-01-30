package com.example.contact.ui.main

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController


import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.example.contact.Application
import com.example.contact.R
import com.example.contact.data.*
import com.example.contact.databinding.FragmentMainBinding
import com.example.contact.support.AdapterRecyclerView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private var bundle = Bundle()
    lateinit var contactRecycleView: RecyclerView
    lateinit var contactAdapter: AdapterRecyclerView


    companion object {
        fun newInstance() = MainFragment()
    }

    private val viewModel by viewModels<MainViewModel>()
    {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val contactDao: ContactDao = (activity?.application as Application).contactList.contactDao()
                return MainViewModel(contactDao) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        contactAdapter = AdapterRecyclerView()
        contactRecycleView = binding.contactsVerticalRecyclerView
        contactRecycleView.adapter = contactAdapter
        val navController = NavHostFragment.findNavController(this)
       // navController.navigate(R.id.blankFragment)
        lifecycleScope.launch {
            viewModel.getContactListFromDB()
            delay(100)
           if (viewModel.contactsFromDB.value.isEmpty()) {
               println("list is empty")
               viewModel.loadContactsFromNetwork()
               delay(5000)
               viewModel.loadContactsInDB()
               delay(5000)
           }
            viewModel.getContactListFromDB()
            delay(300)
            viewModel.stateOfView.collect { state ->
                when (state) {
                    State.Ready -> {
                        binding.progressBar.visibility = View.GONE
                        println("Ready")
                       // viewModel.getContactListFromDB()
                        var listOfPerson =listOf<Person>()
                        lifecycleScope.launch {
                            viewModel.contactsFromDB.collect {
                                listOfPerson = it
                            }
                        }
                        delay(100)
                        contactAdapter.setData(listOfPerson, ::onItemClick)
                    }
                    State.Error -> {
                        binding.progressBar.visibility = View.GONE
                        println("Error")
                        //show fragment mistake
                    }
                    State.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        println("Loading")
                    }
                    State.Done -> {
                        binding.progressBar.visibility = View.GONE
                        println("Done")
                        var listOfPerson =listOf<Person>()
                        lifecycleScope.launch {
                            viewModel.contactsFromDB.collect {
                                listOfPerson = it
                            }
                        }
                       // viewModel.getContactListFromDB()
                        delay(200)
                       // println("from done ${viewModel.contactsFromDB.value.first()}")

                        contactAdapter.setData(listOfPerson, ::onItemClick)


                    }

                }
            }
        }
binding.buttonReload.setOnClickListener {
    lifecycleScope.launch {
        viewModel.repository.deleteContacts()
        navController.navigate(R.id.mainFragment)
    }
}
            return binding.root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    fun onItemClick(personId: Int) {
        bundle = Bundle().apply {
            putInt("ID", personId)
           }
        println("open fr2")
         }

    }
    fun contactToPerson(contact: Contact): Person {
        with(contact) {
            return    Person(
                    id = 10,
                    title = name.title,
                    first = name.first,
                    last = name.last,
                    gender = gender,
                    date = dob.date,
                    age = dob.age,
                    large = picture.large,
                    medium = picture.medium,
                    thumbnail = picture.thumbnail,
                    address = Address(
                        email = email,
                        phone = phone,
                        homeNumber = location.street.number,
                        street = location.street.name,
                        city = location.city,
                        state = location.state,
                        country = location.country,
                        latitude = location.coordinates.latitude,
                        longitude = location.coordinates.longitude
                    )
                )
        }
    }

