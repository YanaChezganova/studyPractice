package com.example.reading

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.reading.databinding.FragmentFirstBinding
import com.example.reading.support.AdapterRecyclerView

private const val MENU_ID = "menu_id"
class FirstFragment : Fragment() {
    var bundle = Bundle()
    private var _binding: FragmentFirstBinding? = null
    lateinit var adapter: AdapterRecyclerView
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        adapter = AdapterRecyclerView()
        //val text =
        binding.iconsRecyclerView.adapter = adapter
        binding.iconsRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        adapter.setData( ::onItemClick )
        return binding.root

    }

   private fun onItemClick(item: Int) {
            bundle = Bundle().apply {
                putInt(MENU_ID, item)
            }
        findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment, bundle)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}