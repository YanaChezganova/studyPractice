package com.example.photos.ui.main

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.photos.R
import com.example.photos.databinding.ItemViewBinding
import com.example.photos.databinding.MainFragmentBinding
import com.example.photos.ui.main.database.AlbumDao
import com.example.photos.ui.main.database.Photos
import kotlinx.coroutines.launch

class MainFragment : Fragment() {
    private var _binding: MainFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var recycleView: RecyclerView
    private val adapter = Adapter { marsModel -> onItemClick(marsModel) }
    private var bundle = Bundle()
    private val launcher: ActivityResultLauncher<Array<String>> = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions())
    { map -> if (map.values.all{it})
    { println("Permissions is Granted")}
    else { Toast.makeText(this.requireContext(), "Permissions is not Granted",
        Toast.LENGTH_SHORT).show()
    }}

    private val viewModel by viewModels<MainViewModel>()
    {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val albumDao: AlbumDao = (activity?.application as Application).photoAlbum.albumDao()
                return MainViewModel(albumDao) as T
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MainFragmentBinding.inflate(inflater)
        recycleView = binding.photoRecyclerView
        recycleView.layoutManager = GridLayoutManager(context,2)
        recycleView.adapter = adapter
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.allPhotos.collect{
                adapter.setData(it)
                println("some information to create fakeListObject for tests. it needs 6 photos $it")
            }
        }
        binding.takePhoto.setOnClickListener {
            checkPermissions()
            findNavController().navigate(R.id.action_mainFragment_to_secondFragmentTakePhoto, null)
        }

        binding.openMap.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_mapsFragment, null)
        }
        binding.deleteAll.setOnClickListener {
            lifecycleScope.launch {
            viewModel.albumDao.delete() }
        }
        return binding.root
    }

    private fun checkPermissions(){
        val isAllGranted = REQUEST_PERMISSIONS.all {
            permission ->
            ContextCompat.checkSelfPermission(this.requireContext(),
                permission) == PackageManager.PERMISSION_GRANTED
        }
        if(isAllGranted) {
            Toast.makeText(this.requireContext(), "Permission is granted", Toast.LENGTH_SHORT)
                .show()
        } else {
            launcher.launch(REQUEST_PERMISSIONS)
        }
    }
    fun onItemClick(item: Photos){
        bundle = Bundle().apply {
            putString("date", item.date)
            putString("URI", item.uri)
        }
        findNavController().navigate(R.id.action_mainFragment_to_seePhotoFragment, bundle)
    }
    companion object{
        private val REQUEST_PERMISSIONS: Array<String> = buildList {
            add(Manifest.permission.CAMERA)
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P){
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)}
            else{
                println("Version SDK > 29")}}.toTypedArray()
    }
// -----------------------------------------------------------------------------
    private inner class Holder(val viewBinding: ItemViewBinding) : RecyclerView.ViewHolder(viewBinding.root)

    private inner class Adapter(private val onClick: (Photos) -> Unit) : RecyclerView.Adapter<Holder>() {
        private lateinit var data: List<Photos>
        fun setData(data: List<Photos>){
            this.data = data
            notifyDataSetChanged()
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            var binding: ItemViewBinding? = null
            return Holder(ItemViewBinding.inflate(layoutInflater, binding?.root, false))
        }

        override fun onBindViewHolder(holder: Holder, position: Int) {
            val item = data.getOrNull(position)
            with(holder.viewBinding)
                { date.text = item?.date
                Glide.with(this@MainFragment)
                    .load(Uri.parse(item?.uri))
                    .centerInside()
                    .into(photo)
            }
            holder.viewBinding.root.setOnClickListener { item?.let { onClick(item) } }
        }
        override fun getItemCount(): Int = data.size
    }
}