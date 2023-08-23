package com.example.skillcinema.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.skillcinema.R
import com.example.skillcinema.databinding.SearchChoiceBinding
import com.example.skillcinema.ui.data.Constants.COUNTRY
import com.example.skillcinema.ui.data.Constants.GENRE
import com.example.skillcinema.ui.data.Constants.HEADER
import com.example.skillcinema.ui.support.SearchTextItemAdapterRecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class CountryChoice : Fragment() {
    private var _binding: SearchChoiceBinding? = null
    private val binding get() = _binding!!
    private var bundle = Bundle()
    private var header: String? = null
    private lateinit var textItemRecycleView: RecyclerView

    @Inject
    lateinit var textItemAdapter: SearchTextItemAdapterRecyclerView
    private val viewModel: SearchViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SearchChoiceBinding.inflate(inflater, container, false)
        textItemAdapter = SearchTextItemAdapterRecyclerView()

        arguments?.let {
            header = it.getString(HEADER)
        }
        binding.layoutWithHeader.setHeader(header!!)

        textItemRecycleView = binding.verticalChoiceRecyclerView
        textItemRecycleView.adapter = textItemAdapter
        val listForChose = mutableListOf<String>()
        if (header == resources.getString(R.string.country)) {
            binding.searchView.queryHint = "Введите страну..."
            viewModel.listOfCountries.onEach {
                listForChose.add(it.country)
            }
        } else {
            binding.searchView.queryHint = "Введите жанр..."
            viewModel.listOfGenres.onEach {
                listForChose.add(it.genre)
            }
        }
        lifecycleScope.launch {
            textItemAdapter.setData(listForChose, ::onItemClick)
        }
        binding.layoutWithHeader.binding.buttonBack.setOnClickListener {
            findNavController().navigate(R.id.action_global_searchSettings)
        }
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(request: String?): Boolean {
                val listSimilarWords = mutableListOf<String>()
                listForChose.onEach {
                    if (it.contains(request.toString())) {
                        listSimilarWords.add(it)
                    }
                    lifecycleScope.launch {
                        textItemAdapter.setData(listSimilarWords, ::onItemClick)
                    }
                }
                return false
            }
        })
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onItemClick(item: String) {
        if (header == resources.getString(R.string.country))
            bundle = Bundle().apply {
                putString(COUNTRY, item)
            }
        else bundle = Bundle().apply {
            putString(GENRE, item)
        }
        findNavController().navigate(R.id.action_global_searchSettings, bundle)
    }
}