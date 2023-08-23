package com.example.skillcinema.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TableLayout
import android.widget.TableRow
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.skillcinema.R
import com.example.skillcinema.databinding.BottomDialogAddToCollectionBinding
import com.example.skillcinema.ui.data.*
import com.example.skillcinema.ui.data.Constants.FILM_ID
import com.example.skillcinema.ui.data.Constants.HEADER
import com.example.skillcinema.ui.data.Constants.RATING
import com.example.skillcinema.ui.data.Constants.URL_IMAGE
import com.example.skillcinema.ui.support.ViewGroupCollectionHorizontalIconText
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


private const val HEIGHT = 228
private const val YEAR_GENRE = "yearAndGenre"

@AndroidEntryPoint
class BottomFragmentAddToCollection : BottomSheetDialogFragment() {

    lateinit var binding: BottomDialogAddToCollectionBinding
    private var headerOfFilm: String? = null
    private var yearAndGenre: String? = null
    private var rating: String? = null
    private var imageUrl: String? = null
    private var filmId: Int = 0
    private var allFolders = listOf<FolderDB>()
    @Inject
    lateinit var roomRepository: RoomRepository
    @Inject
    lateinit var cinemaBaseDao: CinemaBaseDao
    private var bundle = Bundle()
    val viewModel: ProfileViewModel by viewModels()

    override fun getTheme() = R.style.AppBottomSheetDialogTheme

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = BottomDialogAddToCollectionBinding
            .bind(inflater.inflate(R.layout.bottom_dialog_add_to_collection, container))
        roomRepository =
            RoomRepository(RoomModule().providesDao(RoomModule().providesRoomDatabase(requireContext())))
        cinemaBaseDao =
            RoomModule().providesDao(RoomModule().providesRoomDatabase(requireContext()))
        arguments?.let {
            headerOfFilm = it.getString(HEADER)
            filmId = it.getInt(FILM_ID)
            yearAndGenre = it.getString(YEAR_GENRE)
            rating = it.getString(RATING)
            imageUrl = it.getString(URL_IMAGE)
        }
        Glide.with(binding.filmPoster.context)
            .load(imageUrl)
            .into(binding.filmPoster)
        binding.filmName.text = headerOfFilm
        binding.filmYearGenre.text = yearAndGenre
        binding.rating.text = rating
        //get from DBase list of folders

        val scope = CoroutineScope(Dispatchers.Default)
        scope.launch {
            roomRepository.allFolders()
                .collect {
                    allFolders = it
                }
        }
        setCountOfFilmsToAllFolders(allFolders)
        lifecycleScope.launchWhenCreated {
            delay(200)
            val tableLayout = binding.tableLayout
            var counter = 0
            while (counter in allFolders.indices) {
                makeRowOfCollection(tableLayout, allFolders, counter)
                counter += 1
            }
            delay(600)
            val tableRow = TableRow(requireContext())
            tableRow.setVerticalGravity(8)
            val viewPlus = ViewGroupCollectionHorizontalIconText(requireContext())
            viewPlus.setTitleCollection(resources.getString(R.string.lets_create_collection))
            viewPlus.setIcon(3)
            viewPlus.setTextAllElementsInvisible()
            tableRow.addView(viewPlus)
            tableLayout.addView(tableRow)
            viewPlus.setOnClickListener {
                val dialog = DialogEnterTitleOfCollection()
                dialog.show(parentFragmentManager, "creatingDialog")
            }
        }
        return binding.root
    }
    override fun onStart() {
        super.onStart()
        // Плотность
        val density = requireContext().resources.displayMetrics.density

        dialog?.let {
            // Находим сам bottomSheet и достаём из него Behaviour
            val bottomSheet =
                it.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
            val behavior = BottomSheetBehavior.from(bottomSheet)

            // Выставляем высоту
            behavior.peekHeight = (HEIGHT * density).toInt()
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            binding.buttonClose.setOnClickListener {
                behavior.isHideable = true
                behavior.state = BottomSheetBehavior.STATE_HIDDEN
                bundle = Bundle().apply {
                    putInt(FILM_ID, filmId)
                }
                findNavController().navigate(R.id.action_global_cardOfFilm, bundle)
            }
        }
    }

    private fun makeRowOfCollection(
        tableLayout: TableLayout,
        listOfFolders: List<FolderDB>,
        counter: Int
    ) {
        val tableRow = TableRow(requireContext())
        tableRow.setVerticalGravity(8)
        val view = ViewGroupCollectionHorizontalIconText(requireContext())
        var folder = listOfFolders[counter]
        if (folder.name != null)
            view.setTitleCollection(folder.name!!)
        var listWithFoldersForFilm: List<FolderWithFilm>? = listOf(FolderWithFilm(0, 0))
        val scope = CoroutineScope(Dispatchers.Default)
        scope.launch {
            listWithFoldersForFilm = roomRepository.findAllFoldersForFilm(filmId)
        }
        lifecycleScope.launchWhenCreated {
            delay(600)
            setCountOfFilmsToAllFolders(allFolders)
            if (listWithFoldersForFilm?.isNullOrEmpty() == true) {
                view.setIcon(1)
            } else {
                listWithFoldersForFilm?.onEach {
                    if (it.idFolder == folder.uniqueName)
                        view.setIcon(2)
                }
            }

        view.setCountOfElements(folder.count)
        tableRow.addView(view)
        tableLayout.addView(tableRow)
        }
        view.setOnClickListener {
            setCountOfFilmsToAllFolders(allFolders)
            scope.launch {
                listWithFoldersForFilm = roomRepository.findAllFoldersForFilm(filmId)
            }
            lifecycleScope.launchWhenCreated {
                val countFilms = roomRepository.countFilmsInFolder(folder.uniqueName)
                println("countFilms $countFilms. ${folder.name}")
                delay(300)
                if (listWithFoldersForFilm?.isNullOrEmpty() == true) {
                    view.setIcon(2)
                    roomRepository.addFilmInFolder(filmId, folder.uniqueName)
                    delay(30)
                    println("countFilms $countFilms +1. ${folder.name}")

                    cinemaBaseDao.setCountOfFilmsInFolder(folder.uniqueName, countFilms + 1)
                    delay(30)

                    view.setCountOfElements(countFilms + 1)
                } else
                {
                    println("countFilms $countFilms. ${folder.name}")

                    listWithFoldersForFilm!!.onEach {
                        if (it.idFolder == folder.uniqueName) {
                            view.setIcon(1)
                            roomRepository.deleteFilmFromFolder(filmId, folder.uniqueName)
                            delay(30)
                            roomRepository.setCountOfFilmsInFolder(folder.uniqueName, countFilms - 1)
                            delay(30)
                            println("countFilms $countFilms -1. ${folder.name}")

                            //val countFilms = roomRepository.countFilmsInFolder(folder.uniqueName)
                            view.setCountOfElements(countFilms - 1)

                        } else {
                            println("countFilms $countFilms. ${folder.name}")

                            view.setIcon(2)
                            roomRepository.addFilmInFolder(filmId, folder.uniqueName)
                            delay(30)

                            cinemaBaseDao.setCountOfFilmsInFolder(folder.uniqueName, countFilms + 1)
                            delay(30)
                            println("countFilms $countFilms -1. ${folder.name}")

                            view.setCountOfElements(countFilms + 1)
                        }
                    }
                }
            }
        }

    }
    private fun setCountOfFilmsToAllFolders(allFolders: List<FolderDB>){
        val scope = CoroutineScope(Dispatchers.Default)
        scope.launch {
            delay(300)
            allFolders.onEach {
                val countFilmsInFolder = cinemaBaseDao.countFilmsInFolder(it.uniqueName)
                delay(50)
                cinemaBaseDao.setCountOfFilmsInFolder(it.uniqueName, countFilmsInFolder.countFilms)
            }
        }
    }

}
