package com.example.skillcinema.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.skillcinema.R
import com.example.skillcinema.databinding.SeasonsBinding
import com.example.skillcinema.ui.data.Constants.FILM_ID
import com.example.skillcinema.ui.data.Constants.HEADER
import com.example.skillcinema.ui.models.Episode
import com.example.skillcinema.ui.support.TextItemAdapterRecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject


@AndroidEntryPoint
class Serials: Fragment() {

    private var _binding: SeasonsBinding? = null
    private val binding get() = _binding!!
    private var bundle = Bundle()
    private lateinit var seriesRecyclerView: RecyclerView
    private lateinit var chipGroup: ChipGroup
    @Inject
    lateinit var seriesAdapter: TextItemAdapterRecyclerView
    private val viewModel: HomeViewModel by viewModels()

    private var header: String? = null
    private var countOfSeries: String? = null
    private var filmId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SeasonsBinding.inflate(inflater, container, false)
        seriesAdapter = TextItemAdapterRecyclerView()

        arguments?.let {
            header = it.getString(HEADER)
            filmId = it.getInt(FILM_ID)
        }
        binding.layoutWithHeader.setHeader(header!!)
        binding.textSelectedSeason.text = countOfSeries
        binding.layoutWithHeader.binding.buttonBack.setOnClickListener {
            bundle = Bundle().apply {
                putInt(FILM_ID, filmId)
            }
            findNavController().navigate(R.id.action_global_cardOfFilm, bundle)
        }
        lifecycleScope.launchWhenCreated {
            val countOfSeasons = viewModel.loadSerialSeriesInfo(filmId)
            delay(600)
            binding.layoutWithHeader.setHeader(header!!)
            seriesRecyclerView = binding.seriesHorizontalRecyclerView
            seriesRecyclerView.adapter = seriesAdapter
            chipGroup = binding.chipGroup
            viewModel.serialSeriesInfo.onEach {
                for (i in 0 until countOfSeasons) {
                    addChip(i + 1, it[i].episodes)
                }
                val list = it.first().episodes
                val countSeries = resources.getQuantityString(R.plurals.count_series, list.size, list.size)
                binding.textSelectedSeason.text = resources.getString(R.string.season, 1) + " " + countSeries

                seriesAdapter.setData(list)
            }.launchIn(viewLifecycleOwner.lifecycleScope)

        }
        return binding.root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
      private fun addChip(numberOfChip: Int, listOfSeries: List<Episode>) {
        val chip = Chip(this.requireContext())
        chipGroup.addView(chip)
        val size = listOfSeries.size
          val text = "  " + numberOfChip.toString() + "  "
        chip.text = text

        chip.setOnClickListener {
            val countSeries = resources.getQuantityString(R.plurals.count_series, size, size)
            binding.textSelectedSeason.text = resources.getString(R.string.season, numberOfChip) + " " + countSeries
            seriesAdapter.setData(listOfSeries)
        }
    }
}
