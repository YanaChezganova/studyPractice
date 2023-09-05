package com.example.photos.ui.main

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.photos.databinding.FragmentInfoBinding
import com.example.photos.ui.main.database.AlbumDao
import com.example.photos.ui.main.support.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val ARG_PARAM1 = "xid"
private const val ARG_PARAM2 = "title"

class InfoFragment : Fragment() {
    private var xid: String? = null
    private var title: String? = null
    private var _binding: FragmentInfoBinding? = null
    private val binding get() = _binding!!
    private var info = PlaceOfInterest(
        "","", address = Address("", "", "", "", "", ""), "",
        "", "", "", sources = Sources("", listOf("", "")), "",
        infoAboutPlace = InfoAboutPlace("", "", "", "", ""
        ), "", "", preview = Preview("", 0, 0), textAboutPlace = TextAboutPlace("", ""),
        pointLocation = PointLocation(0.0, 0.0)
    )

    private val viewModel = AdditionalViewModel(Repository())
  /*  {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val albumDao: AlbumDao =
                    (activity?.application as Application).photoAlbum.albumDao()
                return MainViewModel(albumDao) as T
            }
        }
    }*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInfoBinding.inflate(inflater)
        arguments?.let {
            xid = it.getString(ARG_PARAM1)
            title = it.getString(ARG_PARAM2)
        }
        lifecycleScope.launch {
            info = viewModel.loadInfoAboutPlace(xid!!, Dispatchers.IO)
            binding.progressBar.visibility = View.INVISIBLE
            if (info.nameOfPlace.isEmpty()) {
                checkInfoAboutPlace(xid!!)

                val bodyText = "${info.infoAboutPlace.description} ${info.onOpenTripMapUrl}"
                addInformation(title!!, bodyText)
            } else {
                checkInfoAboutPlace(xid!!)
                addInformation(info.nameOfPlace, info.infoAboutPlace.description)
            }
        }
        return binding.root
    }
    private fun addInformation(textForTitle: String, bodyText: String) {
        Glide.with(this@InfoFragment)
            .load(Uri.parse(info.preview.sourceUrl))
            .into(binding.previewInfo)
        binding.titleTextInfo.text = textForTitle
        binding.bodyTextInfo.text = bodyText
    }
    suspend fun checkInfoAboutPlace(xid: String): String{
        run {
           viewModel.loadInfoAboutPlace(xid!!, Dispatchers.Unconfined)
        }
        return  viewModel.infoAboutPlace.xid
    }
}
