package com.example.skillcinema.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.skillcinema.R
import com.example.skillcinema.databinding.FragmentProfileBinding
import com.example.skillcinema.ui.data.*
import com.example.skillcinema.ui.data.Constants.FILM_ID
import com.example.skillcinema.ui.data.Constants.FOLDER_ID
import com.example.skillcinema.ui.data.Constants.HEADER
import com.example.skillcinema.ui.data.Constants.NAME
import com.example.skillcinema.ui.data.Constants.POSITION
import com.example.skillcinema.ui.data.Constants.TYPE
import com.example.skillcinema.ui.support.DbMovieAdapterRecyclerView
import com.example.skillcinema.ui.support.FooterAdapterWithTrash
import com.example.skillcinema.ui.support.ViewGroupFolderSquare
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TITLE = "folderTitle"
private const val UNIQUE_NAME = "unique"

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private val viewModel: ProfileViewModel by viewModels()
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private var bundle = Bundle()
    private lateinit var moviesWatchedRecyclerView: RecyclerView
    private lateinit var moviesInterestingRecyclerView: RecyclerView

    @Inject
    lateinit var moviesWatchedAdapter: DbMovieAdapterRecyclerView

    @Inject
    lateinit var moviesInterestingAdapter: DbMovieAdapterRecyclerView

    @Inject
    lateinit var footerAdapter: FooterAdapterWithTrash
    private var folderTitle: String? = ""
    private var uniqueName: Long? = null
    private var answer: String? = ""
    private var position: Long? = null
    private val dialog = DialogConfirmationDeleting()

    @Inject
    lateinit var roomRepository: RoomRepository

    @Inject
    lateinit var cinemaBaseDao: CinemaBaseDao

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        binding.progressBar.isVisible = true
        moviesWatchedAdapter = DbMovieAdapterRecyclerView()
        moviesInterestingAdapter = DbMovieAdapterRecyclerView()
        footerAdapter = FooterAdapterWithTrash()
        footerAdapter.setData { deleteAllFilmsFromBase() }
        roomRepository =
            RoomRepository(RoomModule().providesDao(RoomModule().providesRoomDatabase(requireContext())))
        cinemaBaseDao =
            RoomModule().providesDao(RoomModule().providesRoomDatabase(requireContext()))

        binding.firstViewGroup.setBlueArrowVisible()
        binding.firstViewGroup.setHeader(resources.getString(R.string.watched_films))
        moviesWatchedRecyclerView = binding.firstViewGroup.binding.viewGroupHorizontalRecyclerView
        moviesWatchedRecyclerView.adapter = moviesWatchedAdapter
        binding.secondViewGroup.setBlueArrowVisible()
        binding.secondViewGroup.setHeader(resources.getString(R.string.it_was_interesting))
        moviesInterestingRecyclerView =
            binding.secondViewGroup.binding.viewGroupHorizontalRecyclerView
        val concatAdapter = ConcatAdapter(
            moviesInterestingAdapter,
            footerAdapter
        )
        moviesInterestingRecyclerView.adapter = concatAdapter

        var listOfFilms = listOf<FilmDB>()
        val scope = CoroutineScope(Dispatchers.Default)
        scope.launch {
            roomRepository.allFilms()
                .collect {
                    listOfFilms = it
                }
        }
        var allFolders = listOf<FolderDB>()
        scope.launch {
            roomRepository.allFolders()
                .collect {
                    allFolders = it
                }
        }
        var listOfWatchedFilms = listOf<FilmDB>()
        scope.launch {
            roomRepository.listOfWatchedFilms()
                .collect {
                    listOfWatchedFilms = it
                }
        }
        scope.launch {
            delay(300)
            allFolders.onEach {
                val countFilmsInFolder = cinemaBaseDao.countFilmsInFolder(it.uniqueName)
                delay(50)
                cinemaBaseDao.setCountOfFilmsInFolder(it.uniqueName, countFilmsInFolder.countFilms)
            }

        }
        // make X close
        binding.secondViewGroup.binding.textHeader.setOnClickListener {
            dialog.show(parentFragmentManager, "deletingDialog")
        }
        lifecycleScope.launchWhenCreated {
            delay(1000)
            //making folders "favorite" and "want to see"
            // types of folders: 1 - favorite, 2 - want to see, 3 - person

            arguments?.let {
                folderTitle = it.getString(TITLE)
                uniqueName = it.getLong(UNIQUE_NAME)
                answer = it.getString("yes")
                position = it.getLong(POSITION)
            }
            if (!folderTitle.isNullOrBlank() && uniqueName != null) {
                Toast.makeText(
                    requireContext(),
                    "Коллекция $folderTitle создана",
                    Toast.LENGTH_SHORT
                ).show()
                roomRepository.addFolderInBase(3, uniqueName!!, folderTitle)
                delay(50)
                arguments?.clear()
            }
            if (!answer.isNullOrBlank() && position != null) {
                cinemaBaseDao.deleteFolderWithFilms(position!!)
                cinemaBaseDao.deleteFolder(position!!)
                arguments?.clear()
            }
            scope.launch {
                roomRepository.allFolders()
                    .collect {
                        allFolders = it
                    }
            }
            delay(500)
            // types of icons: 1 - heart, 2 - flag, 3 - person
            val tableLayout = binding.tableLayout
            // gravity center = 1, start = 8
            var counter = 1
            if (allFolders.size % 2 == 0) {
                while (counter in 1..allFolders.size) {
                    makeRowOfCollection(tableLayout, allFolders, counter)
                    counter += 2
                }
            } else {
                while (counter in 1..allFolders.size - 1) {
                    makeRowOfCollection(tableLayout, allFolders, counter)
                    counter += 2
                }
                val tableRow = TableRow(requireContext())
                tableRow.setHorizontalGravity(8)
                makeViewOfCollection(allFolders, tableRow, counter - 1)
                tableLayout.addView(tableRow)
            }
            // make list of interesting films
            moviesInterestingAdapter.setData(listOfFilms.take(20), ::onItemClick)
            binding.secondViewGroup.setCountOfElements(listOfFilms.size)
            binding.secondViewGroup.binding.buttonAllElements.setOnClickListener {
                bundle = Bundle().apply {
                    putString(HEADER, resources.getString(R.string.it_was_interesting))
                    putString(TYPE, "interesting")
                }
                findNavController().navigate(R.id.action_global_filmsInCollection, bundle)
            }
            // make list of watched films
            binding.progressBar.isVisible = false
            moviesWatchedAdapter.setData(listOfWatchedFilms.take(20), ::onItemClick)
            binding.firstViewGroup.setCountOfElements(listOfWatchedFilms.size)
            binding.firstViewGroup.binding.buttonAllElements.setOnClickListener {
                bundle = Bundle().apply {
                    putString(HEADER, resources.getString(R.string.watched_films))
                    putString(TYPE, "watched")
                }
                findNavController().navigate(R.id.action_global_filmsInCollection, bundle)
            }
            binding.buttonCreateCollection.setOnClickListener {
                val dialog = DialogEnterTitleOfCollection()
                dialog.show(parentFragmentManager, "creatingDialog")
            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onItemClick(item: FilmDB) {
        bundle = Bundle().apply {
            putInt(FILM_ID, item.idFilm)
        }
        findNavController().navigate(R.id.action_global_cardOfFilm, bundle)
    }

    private fun makeRowOfCollection(
        tableLayout: TableLayout,
        listOfFolders: List<FolderDB>,
        counter: Int
    ) {
        // gravity center = 1, start = 8
        val tableRow = TableRow(requireContext())
        tableRow.setHorizontalGravity(1)
        makeViewOfCollection(listOfFolders, tableRow, counter - 1)
        makeViewOfCollection(listOfFolders, tableRow, counter)

        tableLayout.addView(tableRow)

    }

    private fun makeViewOfCollection(
        listOfFolders: List<FolderDB>,
        tableRow: TableRow,
        counter: Int
    ) {

        val view = ViewGroupFolderSquare(requireContext())
        val folder = listOfFolders[counter]
        if (folder.name != null)
            view.setNameFolderCollection(folder.name)
        view.setIcon(folder.type)
        view.setCountOfFilms(folder.count)
        if (folder.type == 3) view.setCloseButtonVisible()
        view.setCountOfFilms(folder.count)
        view.binding.buttonClose.setOnClickListener {
            bundle = Bundle().apply {
                putString(NAME, folder.name)
                putLong(POSITION, folder.uniqueName)
            }
            dialog.arguments = bundle
            dialog.show(parentFragmentManager, "deletingDialog")
        }
        view.setOnClickListener {
            bundle = Bundle().apply {
                putString(HEADER, folder.name)
                putLong(FOLDER_ID, folder.uniqueName)
            }
            findNavController().navigate(R.id.action_global_filmsInCollection, bundle)
        }
        tableRow.addView(view)
    }

    private fun deleteAllFilmsFromBase() {
        val scope = CoroutineScope(Dispatchers.Default)
        scope.launch {
            roomRepository.deleteFilmsFromBase()
        }
        findNavController().navigate(R.id.action_global_navigation_profile)

    }
}

