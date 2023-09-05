package com.example.sunnyday.ui.city

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.sunnyday.R
import com.example.sunnyday.databinding.FragmentCityBinding
import com.example.sunnyday.di.AdapterCityNamesRecyclerView
import com.example.sunnyday.entity.CityDB
import com.example.sunnyday.entity.State
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CityFragment : Fragment() {
    private var bundle = Bundle()
    lateinit var binding: FragmentCityBinding
    private lateinit var cityRecycleView: RecyclerView
    private lateinit var cityRVAdapter: AdapterCityNamesRecyclerView
    private val viewModel: CityViewModel by viewModels()

    /*  private val viewModel: CityViewModel by viewModels {
          object : ViewModelProvider.Factory {
              override fun <T : ViewModel> create(modelClass: Class<T>): T {
                  val weatherDao: WeatherBaseDao =
                      (activity?.application as App).weatherBase.sunnyDayBaseDao()
                  return CityViewModel(weatherDao) as T
              }
          }
      }*/

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCityBinding.inflate(inflater, container, false)
        viewModel.allCities()
        cityRVAdapter = AdapterCityNamesRecyclerView()
        cityRecycleView = binding.listOfCities
        cityRecycleView.adapter = cityRVAdapter
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
            var listForRecyclerView = emptyList<String>()
            if (listOfCities.isNotEmpty()) {
                println("good")
                listForRecyclerView = makeListForRV(listOfCities)
            } else {
                println("bad")
            }
            cityRVAdapter.setData(listForRecyclerView, ::onClick)
        }
        binding.buttonTrash.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.deleteAllFromDataBase()
                findNavController().navigate(R.id.action_global_navigation_city)
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

    private fun onClick(cityName: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loadSearchResults(cityName)
            delay(750)
            viewModel.stateOfView.collect { state ->
                when (state) {
                    State.Ready -> {
                        println("Ready")
                    }
                    State.Error -> {
                        println("Error")
                        bundle = Bundle().apply {
                            putString("city", cityName)
                            putBoolean("fromDB", true)
                        }
                        findNavController().navigate(R.id.action_global_weatherFragment, bundle)
                    }
                    State.Loading -> {
                        println("Loading")
                    }
                    State.Done -> {
                        println("Done")
                        if (viewModel.searchResult.value.success == "false") {
                            bundle = Bundle().apply {
                                putString("city", cityName)
                                putBoolean("fromDB", true)
                            }
                            findNavController().navigate(R.id.action_global_weatherFragment, bundle)
                            println("find the weather for city in db")
                        } else {
                            println("From site")
                            val result = viewModel.searchResult.value
                            viewModel.findWeatherInDB(result)
                            bundle = Bundle().apply {
                                putString("city", cityName)
                                putString("url", result.current.weather_icons.first())
                                putInt("temper", result.current.temperature!!)
                                putString("date", result.location.localtime)
                                putBoolean("fromDB", false)
                            }
                            findNavController().navigate(R.id.action_global_weatherFragment, bundle)
                        }
                    }
                }
            }
        }
    }
}