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
import com.example.contact.data.*
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
                    (activity?.application as Application).contactsList.contactDao()
                return MainViewModel(contactDao) as T
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        contactAdapter = AdapterRecyclerView()
        contactRecycleView = binding.contactsVerticalRecyclerView
        contactRecycleView.adapter = contactAdapter
        println("start viewModel.getContactListFromDB()")
        viewModel.getContactListFromDB()
        var listOfPerson = listOf<Person>()
        val scope = CoroutineScope(Dispatchers.Default)

        viewLifecycleOwner.lifecycleScope.launch {
            scope.launch {
                delay(1500)
                viewModel.contactsFromDB
                    .collect {
                        listOfPerson = it
                        println("list of person 1 $listOfPerson")

                    }
            }
            delay(300)
            println("list of person 2 $listOfPerson")

            delay(500)
            if (listOfPerson.isEmpty()) {
                println("list is empty")
                viewModel.loadContactsFromNetwork()
                println("start delay 1500 ffter network")
                delay(1500)
                println("end delay 1500 ffter network")

                viewModel.loadContactsInDB()
                println("start delay 1500 to load in db")

                delay(500)
                println("end delay 1500 to load in db")

                viewModel.getContactListFromDB()
                delay(200)
                scope.launch {
                    viewModel.contactsFromDB
                        .collect {
                            listOfPerson = it
                            println("it=$it")
                        }
                }
            }

            viewModel.stateOfView.collect { state ->
                when (state) {
                    State.Ready -> {
                        binding.progressBar.visibility = View.GONE
                        println("Ready")
                        // viewModel.getContactListFromDB()
                        //  var listOfContact =listOf<Contact>()
                        // val listOfPerson = mutableListOf<Person>()
                        //   lifecycleScope.launch {
                        //      viewModel.contactsFromNetwork.collect {
                        // listOfContact = it
                        //it.forEach {contact -> listOfPerson.add(contactToPerson(contact))}
                        println("list of person ready $listOfPerson")
                        contactAdapter.setData(listOfPerson, ::onItemClick)
                        // }
                        // }

                    }
                    State.Error -> {
                        binding.progressBar.visibility = View.GONE
                        println("Error")
                        findNavController().navigate(R.id.action_global_bottomFragmentMistake)
                    }
                    State.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        println("Loading")
                    }
                    State.Done -> {
                        binding.progressBar.visibility = View.GONE
                        println("Done")
                        /*  var listOfPerson =listOf<Person>()
                          lifecycleScope.launch {
                              viewModel.contactsFromDB.collect {
                                  listOfPerson = it
                              }
                          }
                          // viewModel.getContactListFromDB()
                          delay(200)
                          // println("from done ${viewModel.contactsFromDB.value.first()}")

                          contactAdapter.setData(listOfPerson, ::onItemClick)*/
                        //   val listOfPerson = mutableListOf<Person>()
                        // lifecycleScope.launch {
                        //     viewModel.contactsFromNetwork.collect {
                        // listOfContact = it
                        //        it.forEach {contact -> listOfPerson.add(contactToPerson(contact))}
                        println("list of person done  $listOfPerson")

                        contactAdapter.setData(listOfPerson, ::onItemClick)

                    }

                }

            }

            //}
            // }
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
        println("open fr2")
        findNavController().navigate(R.id.action_global_blankFragment, bundle)
    }

}

fun contactToPerson(contact: Contact): Person {
    with(contact) {
        return Person(
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