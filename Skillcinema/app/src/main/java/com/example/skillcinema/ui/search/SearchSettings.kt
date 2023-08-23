package com.example.skillcinema.ui.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.skillcinema.R
import com.example.skillcinema.databinding.SearchSettingsBinding
import com.example.skillcinema.ui.data.Constants.COUNTRY
import com.example.skillcinema.ui.data.Constants.GENRE
import com.example.skillcinema.ui.data.Constants.HEADER
import com.example.skillcinema.ui.data.Constants.ORDER
import com.example.skillcinema.ui.data.Constants.RANGE
import com.example.skillcinema.ui.data.Constants.RATING
import com.example.skillcinema.ui.data.Constants.RATING_FROM
import com.example.skillcinema.ui.data.Constants.RATING_TO
import com.example.skillcinema.ui.data.Constants.SORT
import com.example.skillcinema.ui.data.Constants.WATCHED
import com.example.skillcinema.ui.data.Constants.YEAR_SINCE
import com.example.skillcinema.ui.data.Constants.YEAR_UNTIL
import com.google.android.material.slider.RangeSlider
import dagger.hilt.android.AndroidEntryPoint


private const val YEAR = "YEAR"
private const val POPULAR = "NUM_VOTE"
private const val ALL = "ALL"
private const val FILM = "FILM"
private const val SERIAL = "TV_SERIES"

@AndroidEntryPoint
class SearchSettings : Fragment() {
    private var _binding: SearchSettingsBinding? = null
    private val binding get() = _binding!!
    private var bundle = Bundle()
    private val viewModel: SearchViewModel by viewModels()
    private var country: String? = "Россия"
    private var genre: String? = "драма"
    private var yearSince: String? = "2000"
    private var yearUntil: String? = "2015"
    private var position: MutableList<Float> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SearchSettingsBinding.inflate(inflater, container, false)
        binding.layoutWithHeader.setHeader(resources.getString(R.string.search_settings))
        binding.layoutWithHeader.binding.buttonBack.setOnClickListener {
            findNavController().navigate(R.id.action_global_navigation_search)
        }
        binding.countryButton.setOnClickListener {
            bundle = Bundle().apply {
                putString(HEADER, resources.getString(R.string.country))
            }
            findNavController().navigate(R.id.action_global_countryChoice, bundle)
        }
        country = loadInformation(binding.chosenCountry, COUNTRY)

        binding.genreButton.setOnClickListener {
            bundle = Bundle().apply {
                putString(HEADER, resources.getString(R.string.genre))
            }
            findNavController().navigate(R.id.action_global_countryChoice, bundle)
        }

        genre = loadInformation(binding.chosenGenre, GENRE)
        binding.yearButton.setOnClickListener {
            findNavController().navigate(R.id.action_global_yearChoice)
        }
        when (arguments?.getString(YEAR_SINCE)) {
            null -> {
                println("bundleYS is null")
            }
            else -> {
                yearSince = arguments?.getString(YEAR_SINCE)
                yearUntil = arguments?.getString(YEAR_UNTIL)
                if (yearSince!!.toInt() <= yearUntil!!.toInt()) {
                    binding.chosenYear.text = resources.getString(
                        R.string.year_since_until,
                        yearSince, yearUntil
                    )
                } else {
                    binding.chosenYear.text = resources.getString(
                        R.string.year_since_until,
                        yearUntil, yearSince
                    )
                }
                viewModel.loadToSharedPreference(
                    requireContext(),
                    YEAR_SINCE,
                    arguments?.getString(YEAR_SINCE)!!
                )
                viewModel.loadToSharedPreference(
                    requireContext(),
                    YEAR_UNTIL,
                    arguments?.getString(YEAR_UNTIL)!!
                )
            }
        }

        try {
            val value = viewModel.loadFromSharedPreference(requireContext(), YEAR_SINCE)
            when (value) {
                "..." -> {
                    println("preferenseYS null")
                }
                null -> {
                    println("preferenseYS null")
                }
                else -> {
                    yearSince = viewModel.loadFromSharedPreference(requireContext(), YEAR_SINCE)
                    yearUntil = viewModel.loadFromSharedPreference(requireContext(), YEAR_UNTIL)
                    if (yearSince!!.toInt() <= yearUntil!!.toInt()) {
                        binding.chosenYear.text = resources.getString(
                            R.string.year_since_until,
                            yearSince, yearUntil
                        )
                    } else {
                        binding.chosenYear.text = resources.getString(
                            R.string.year_since_until,
                            yearUntil, yearSince
                        )
                    }
                }
            }
        } catch (e: ClassCastException) {
            Log.d("ClassCastException", "again..")
        }
          binding.discreteSlider.addOnSliderTouchListener(object : RangeSlider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: RangeSlider) {
            }

            override fun onStopTrackingTouch(slider: RangeSlider) {
                position = slider.values
                binding.chosenRating.text = resources.getString(
                    R.string.rating_since_until,
                    position[0].toInt().toString(), position[1].toInt().toString()
                )
                val text = position[0].toInt().toString() + position[1].toInt().toString()
                viewModel.loadToSharedPreference(requireContext(), RANGE, text)
            }
        })
        var value: String? = null
        when (viewModel.loadFromSharedPreference(requireContext(), RANGE)) {
            "..." -> {
                println("preferenseR null")
            }
            null -> println("preferenseR $RANGE null")
            else -> {
                value = viewModel.loadFromSharedPreference(requireContext(), RANGE)
                when (value.length) {
                    0 -> println("preferense $RANGE null")
                    2 -> {
                        binding.chosenRating.text = resources.getString(
                            R.string.rating_since_until,
                            value.toCharArray().first().toString(),
                            value.toCharArray().last().toString()
                        )
                        position.add(value.toCharArray().first().toString().toFloat())
                        position.add(value.toCharArray().last().toString().toFloat())
                        binding.discreteSlider.values = position
                    }
                    3 -> {
                        binding.chosenRating.text = resources.getString(
                            R.string.rating_since_until,
                            value.toCharArray().first().toString(), "10"
                        )
                        position.add(value.toCharArray().first().toString().toFloat())
                        position.add(10F)
                        binding.discreteSlider.values = position
                    }
                    else -> {
                        binding.chosenRating.text = resources.getString(
                            R.string.rating_since_until,
                            "10", "10"
                        )
                        position = mutableListOf(10F, 10F)
                        binding.discreteSlider.values = position
                    }
                }
            }
        }

        binding.wordShowWatched.setOnClickListener {
            binding.wordShowWatched.visibility = View.GONE
            binding.iconShowWatched.visibility = View.GONE
            binding.iconHideWatched.visibility = View.VISIBLE
            binding.wordHideWatched.visibility = View.VISIBLE
        }
        binding.wordHideWatched.setOnClickListener {
            binding.wordHideWatched.visibility = View.GONE
            binding.iconHideWatched.visibility = View.GONE
            binding.iconShowWatched.visibility = View.VISIBLE
            binding.wordShowWatched.visibility = View.VISIBLE
        }
        val buttonAll = binding.buttonLeftShow  //position 1
        val buttonFilms = binding.buttonCenterShow  //position 2
        val buttonSerials = binding.buttonRightShow  //position 3

        when (viewModel.loadFromSharedPreference(requireContext(), SORT)) {
            SERIAL -> {
                clickOnSelectorButton(buttonSerials, 3, buttonAll, buttonFilms)
            }
            FILM -> {
                clickOnSelectorButton(buttonFilms, 2, buttonAll, buttonSerials)
            }
            else -> {
                clickOnSelectorButton(buttonAll, 1, buttonFilms, buttonSerials)
            }
        }
        val buttonYearSort = binding.buttonLeftSort //position 1
        val buttonPopular = binding.buttonCenterSort //position 2
        val buttonRatingSort = binding.buttonRightSort  //position 3
        when (viewModel.loadFromSharedPreference(requireContext(), ORDER)) {
            YEAR -> {
                clickOnSelectorButton(buttonYearSort, 1, buttonPopular, buttonRatingSort)
            }
            POPULAR -> {
                clickOnSelectorButton(buttonPopular, 2, buttonYearSort, buttonRatingSort)
            }
            else -> {
                clickOnSelectorButton(buttonRatingSort, 3, buttonYearSort, buttonPopular)
            }
        }

        buttonAll.setOnClickListener {
            clickOnSelectorButton(it, 1, buttonFilms, buttonSerials)
            viewModel.loadToSharedPreference(
                requireContext(), SORT, ALL
            )
        }
        buttonFilms.setOnClickListener {
            clickOnSelectorButton(it, 2, buttonAll, buttonSerials)
            viewModel.loadToSharedPreference(
                requireContext(), SORT, FILM
            )
        }
        buttonSerials.setOnClickListener {
            clickOnSelectorButton(it, 3, buttonAll, buttonFilms)
            viewModel.loadToSharedPreference(
                requireContext(), SORT, SERIAL
            )
        }
        buttonYearSort.setOnClickListener {
            clickOnSelectorButton(it, 1, buttonPopular, buttonRatingSort)
            viewModel.loadToSharedPreference(
                requireContext(), ORDER, YEAR
            )
        }
        buttonPopular.setOnClickListener {
            clickOnSelectorButton(it, 2, buttonYearSort, buttonRatingSort)
            viewModel.loadToSharedPreference(
                requireContext(), ORDER, POPULAR
            )
        }

        buttonRatingSort.setOnClickListener {
            clickOnSelectorButton(it, 3, buttonYearSort, buttonPopular)
            viewModel.loadToSharedPreference(
                requireContext(), ORDER, RATING
            )
        }
        binding.buttonMakeChoice.setOnClickListener {
            val ratingFrom = binding.chosenRating.text.drop(3).take(2).toString()
                .replace(" ", "", true).toInt().toString()
            val ratingTo = binding.chosenRating.text.takeLast(2).toString()
                .replace(" ", "", true).toInt().toString()
            val showWatched = binding.wordShowWatched.isVisible

            bundle = Bundle().apply {
                if (binding.chosenCountry.text.toString() != "...")
                    putString(COUNTRY, binding.chosenCountry.text.toString())
                else putString(COUNTRY, "Россия")
                if (binding.chosenGenre.text.toString() != "...")
                    putString(GENRE, binding.chosenGenre.text.toString())
                else putString(GENRE, "драма")
                if (!yearSince.isNullOrEmpty()) {
                    putString(YEAR_SINCE, yearSince)
                    putString(YEAR_UNTIL, yearUntil)
                } else {
                    putString(YEAR_SINCE, "1998")
                    putString(YEAR_UNTIL, "2023")
                }
                putString(RATING_FROM, ratingFrom)
                putString(RATING_TO, ratingTo)

                putBoolean(WATCHED, showWatched)
                if (buttonYearSort.isPressed)
                    putString(ORDER, YEAR)
                else {
                    if (buttonPopular.isPressed)
                        putString(ORDER, POPULAR)
                    else putString(ORDER, RATING)
                }
                if (buttonAll.isPressed)
                    putString(SORT, ALL)
                else {
                    if (buttonFilms.isPressed)
                        putString(SORT, FILM)
                    else putString(SORT, SERIAL)
                }
            }
            println("bundleCommon =$bundle ")
            findNavController().navigate(R.id.action_global_navigation_search, bundle)
        }
        return binding.root
    }

       private fun loadInformation(view: TextView, key: String): String? {
        var value: String? = null
        when (arguments?.getString(key)) {
            null -> println("bundle $key is null")
            else -> {
                value = arguments?.getString(key)
                view.text = value
                viewModel.loadToSharedPreference(requireContext(), key, value)
            }
        }
        when (viewModel.loadFromSharedPreference(requireContext(), key)) {
            null -> println("preferense $key null")
            else -> {
                value = viewModel.loadFromSharedPreference(requireContext(), key)
                view.text = value
            }
        }
        return value
    }

    private fun clickOnSelectorButton(
        clickedView: View, position:
        Int, otherViewFromLeftToRight1: View, otherViewFromLeftToRight2: View
    ) {
        clickedView.isPressed = true
        otherViewFromLeftToRight1.isPressed = false
        otherViewFromLeftToRight1.isPressed = false
        when (position) {
            1 -> clickedView.setBackgroundResource(R.drawable.button_left_rounding_blue_in_settings)
            2 -> clickedView.setBackgroundResource(R.drawable.button_without_rounding_blue_in_settings)
            else -> clickedView.setBackgroundResource(R.drawable.button_right_rounding_blue_in_settings)
        }
        if (position < 3)
            otherViewFromLeftToRight2.setBackgroundResource(R.drawable.button_right_rounding_white_in_settings)
        if (position > 1)
            otherViewFromLeftToRight1.setBackgroundResource(R.drawable.button_left_rounding_white_in_settings)
        if (position == 1)
            otherViewFromLeftToRight1.setBackgroundResource(R.drawable.button_without_rounding_white_in_settings)
        if (position == 3)
            otherViewFromLeftToRight2.setBackgroundResource(R.drawable.button_without_rounding_white_in_settings)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

