package com.example.sunnyday.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.sunnyday.App
import com.example.sunnyday.R
import com.example.sunnyday.data.WeatherBaseDao
import com.example.sunnyday.databinding.FragmentSearchBinding
import com.example.sunnyday.entity.CityDB
import com.example.sunnyday.entity.State
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
@AndroidEntryPoint
class SearchFragment : Fragment() {

    lateinit var binding: FragmentSearchBinding
    private var bundle = Bundle()
  /*  private val viewModel: SearchViewModel by viewModels<SearchViewModel>() {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val weatherDao: WeatherBaseDao =
                    (activity?.application as App).weatherBase.sunnyDayBaseDao()
                return SearchViewModel(weatherDao) as T
            }
        }
    }*/

     private val viewModel: SearchViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //   roomRepository = RoomRepository(RoomModule().providesDao(RoomModule().providesRoomDatabase(requireContext())))

        binding = FragmentSearchBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        val lastSearch = viewModel.loadFromSharedPreference(requireContext(),"last_search")
        binding.searchView.text.insert(0, lastSearch)
        viewModel.allCities()
        var listOfCities = emptyList<CityDB>()
        val scope = CoroutineScope(Dispatchers.Default)
        scope.launch {
            viewModel.allCities().collect {
                listOfCities = it
                println("get cities $it")
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {

            delay(550)
            println(listOfCities)
            var listForAutoComplete = emptyList<String>()
            if (listOfCities.isNotEmpty()) {
                println("good")
                listForAutoComplete = makeListForRV(listOfCities)
            } else {
                println("bad")
            }
            val adapter = ArrayAdapter<String>(
                requireContext(), android.R.layout.simple_dropdown_item_1line, listForAutoComplete
            )
            binding.searchView.setAdapter(adapter)
            // Минимальное число символов для показа выпадающего списка
            binding.searchView.threshold = 2
        }
        binding.searchView.onItemClickListener = AdapterView.OnItemClickListener { parent, _,
                                                                                   position, id ->
            val selectedItem = parent.getItemAtPosition(position).toString()
            binding.textNotFound.visibility = View.GONE
            viewModel.loadSearchResults(selectedItem)
        }
        // Обработчик щелчка для корневого элемента макета (LinearLayout или др.)
        binding.searchFragmentLayout.setOnClickListener {
            val text = binding.searchView.text.toString()
            viewModel.loadSearchResults(text)
        }

        // Если к компоненту перешёл фокус
        binding.searchView.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                // Выводим выпадающий список
                binding.searchView.showDropDown()
            }
        }
        binding.buttonClose.setOnClickListener {
            binding.searchView.text.clear()
        }

        binding.buttonSearch.setOnClickListener {
            val text = binding.searchView.text.toString()
            viewModel.loadSearchResults(text)
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.stateOfView.collect { state ->
                    when (state) {
                        State.Ready -> {
                            println("Ready")
                        }
                        State.Error -> {
                            println("Error")
                            binding.textNotFound.isVisible = true
                        }

                        State.Loading -> {
                            println("Loading")
                        }
                        State.Done -> {
                            println("Done")
                            if (viewModel.searchResult.value.success == "false") {
                                binding.textNotFound.isVisible = true
                                println("nothing")
                            } else {
                                println("Done-done")
                                loadWeatherInformation()


                            }
                        }

                    }
                }
            }

        }
        return binding.root
    }

    private fun makeListForRV(listOfCities: List<CityDB>): List<String> {
        val listForRV = mutableListOf<String>()
        listOfCities.forEach {
            listForRV.add(it.nameCity)
        }
        return listForRV
    }
    private suspend fun loadWeatherInformation(){
        val result = viewModel.searchResult.value
        viewModel.loadToSharedPreference(requireContext(), "last_search", result.location.name)
        val city = result.request.query
        viewModel.findCityInDB(result.location)
        viewModel.findWeatherInDB(result)
        val imageUrl = result.current.weather_icons.first()
        val temperature = result.current.temperature
        val date = result.location.localtime
        bundle = Bundle().apply {
            putString("city", city)
            putString("url", imageUrl)
            putInt("temper", temperature!!)
            putString("date", date)
            putBoolean("fromDB", false)
        }
        println("bundle = $bundle")
        findNavController().navigate(
            R.id.action_global_weatherFragment,
            bundle
        )

    }
}