package com.example.photos.ui.main

import android.content.ContentValues
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.photos.databinding.FragmentSecondTakePhotoBinding
import com.example.photos.ui.main.database.AlbumDao
import com.example.photos.ui.main.database.Photos
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor

private const val FILE_DATE_FORMAT = "yyyy-MM-dd_HH:mm:ss"

class SecondFragmentTakePhoto : Fragment() {
    private var _binding: FragmentSecondTakePhotoBinding? = null
    private val binding get() = _binding!!
    private lateinit var executor: Executor
    private var imageCapture: ImageCapture? = null
    private val date = SimpleDateFormat(FILE_DATE_FORMAT, Locale.US)
        .format(System.currentTimeMillis())
    private val viewModel by viewModels<MainViewModel>()
    {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val albumDao: AlbumDao =
                    (activity?.application as Application).photoAlbum.albumDao()
                return MainViewModel(albumDao) as T
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSecondTakePhotoBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        executor = ContextCompat.getMainExecutor(this.requireContext())
        startCamera()
        binding.buttonPhoto.setOnClickListener {
            takePhoto()
        }
    }

    private fun takePhoto() {

        val imageCapture = imageCapture ?: return
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, date)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        }
        val context = this.context
        val outputOptions = this.context?.let {
            ImageCapture.OutputFileOptions
                .Builder(
                    it.contentResolver,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues
                ).build()
        }
        if (outputOptions != null) {
            imageCapture.takePicture(
                outputOptions,
                executor,
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        Toast.makeText(
                            context, "Photo saved on ${outputFileResults.savedUri}",
                            Toast.LENGTH_SHORT
                        ).show()
                        Glide.with(this@SecondFragmentTakePhoto)
                            .load(outputFileResults.savedUri)
                            .centerCrop()
                            .into(binding.imageView)
                        val uri = outputFileResults.savedUri!!.toString()
                        lifecycleScope.launch {
                            viewModel.albumDao.insertPhoto(
                                Photos(
                                    null, uri, date,
                                    "Image$date"
                                )
                            )
                        }
                    }

                    override fun onError(exception: ImageCaptureException) {
                        Toast.makeText(
                            context, "Photo failed: ${exception.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                        exception.printStackTrace()
                    }
                }
            )
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this.requireContext())
        cameraProviderFuture.addListener(
            {
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build()
                preview.setSurfaceProvider(binding.previewView.surfaceProvider)
                imageCapture = ImageCapture.Builder().build()
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, CameraSelector.DEFAULT_BACK_CAMERA,
                    preview, imageCapture
                )
            }, executor
        )
    }
}