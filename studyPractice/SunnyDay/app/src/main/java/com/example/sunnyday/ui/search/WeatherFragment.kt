package com.example.sunnyday.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.sunnyday.App
import com.example.sunnyday.data.WeatherBaseDao
import com.example.sunnyday.databinding.FragmentWeatherBinding
import com.example.sunnyday.di.AdapterRecyclerView
import com.example.sunnyday.entity.CityWeather
import com.example.sunnyday.entity.WeatherDB
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
@AndroidEntryPoint
class WeatherFragment : Fragment() {
    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = _binding!!
    lateinit var weatherRecycleView: RecyclerView
    private val viewModel: SearchViewModel by viewModels()

    /* private val viewModel: SearchViewModel by viewModels<SearchViewModel>() {
         object : ViewModelProvider.Factory {
             override fun <T : ViewModel> create(modelClass: Class<T>): T {
                 val weatherDao: WeatherBaseDao =
                     (activity?.application as App).weatherBase.sunnyDayBaseDao()
                 return SearchViewModel(weatherDao) as T
             }
         }
     }*/

    //  @Inject
    lateinit var weatherRVAdapter: AdapterRecyclerView
    var city = ""
    var url = ""
    var temperature = 0
    var date = ""
    var fromDB = false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeatherBinding.inflate(inflater, container, false)

        arguments?.let {
            city = it.getString("city").toString()
            url = it.getString("url").toString()
            temperature = it.getInt("temper")
            date = it.getString("date").toString()
            fromDB = it.getBoolean("fromDB")
        }
        binding.nameOfCity.text = city
        viewModel.getWeatherForCityFromDB(city)
        weatherRVAdapter = AdapterRecyclerView()
        weatherRecycleView = binding.listOfWeather
        weatherRecycleView.adapter = weatherRVAdapter
        if (fromDB) {
            binding.notReachable.isVisible = true
            var listOfWeather = emptyList<WeatherDB>()
            val scope = CoroutineScope(Dispatchers.Default)
            scope.launch {
                viewModel.getWeatherForCityFromDB(city).collect {
                    listOfWeather = it
                    println("get weather $it")
                }
            }
            viewLifecycleOwner.lifecycleScope.launch {

                delay(350)
                if (listOfWeather.isNotEmpty()) {
                    val listOfCityWeather = mutableListOf<CityWeather>()
                    listOfWeather.onEach {
                        listOfCityWeather.add(
                            CityWeather(
                                it.nameCity,
                                it.weatherIconUrl,
                                it.temperature,
                                it.localTime
                            )
                        )
                    }
                    println("list is ready $listOfCityWeather")
                    weatherRVAdapter.setData(listOfCityWeather)

                }
            }
        } else {
            println("from internet ")
            weatherRVAdapter.setData(listOf(CityWeather(city, url, temperature, date)))
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}