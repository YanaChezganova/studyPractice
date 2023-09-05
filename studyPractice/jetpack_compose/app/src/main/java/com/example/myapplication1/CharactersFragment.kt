package com.example.myapplication1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.example.myapplication1.ui.theme.Grey900


class CharactersFragment: Fragment() {
    private var bundle: Bundle? = null
    private val viewModel by viewModels<MainViewModel>()
    private val pageData by lazy {
        ListPagingSource.pager(viewModel).flow
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setContent {
            setBackgroundColor(resources.getColor(R.color.grey_900))
            val title = stringResource(R.string.app_name)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .background(Grey900)
            )

            { Row (Modifier.padding(bottom = 20.dp), Arrangement.Center){
                Text(
                    text = title, modifier = Modifier
                        .weight(1f),
                    fontSize = 22.sp,
                    color = Color(resources.getColor(R.color.white)),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
                List(viewModel)
            }
        }
    }

    @Composable
    fun List(viewModel: MainViewModel) {
        val itemsCharacters: LazyPagingItems<Character> = pageData.collectAsLazyPagingItems()
        LazyColumn(modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(10.dp))
        { items(itemsCharacters) {
            it?.let {
                viewModel.character.value = it
                CharacterView(character = it, ::onClick)
            } ?: Text ("not loaded")
        }
            itemsCharacters.apply {

                when{
                    loadState.refresh is LoadState.Loading -> {
                        item{
                            Box(modifier = Modifier.fillParentMaxSize(),
                            contentAlignment = Alignment.Center){
                                CircularProgressIndicator()
                                println("4ff")

                            }
                        }
                    }
                    loadState.append is LoadState.Loading -> {
                        item{
                            CircularProgressIndicator()
                        }
                    }
                    loadState.refresh is LoadState.Error -> {
                        val error = itemsCharacters.loadState.refresh as LoadState.Error
                        item{
                            Column(modifier = Modifier.fillParentMaxSize()) {
                                error.error.localizedMessage?.let {
                                    Text(text = it)

                                }
                                Button(onClick = { retry() }) {
                                    Text(text = "Retry")
                                }
                            }
                        }
                    }
                    loadState.append is LoadState.Error -> {
                        val error = itemsCharacters.loadState.append as LoadState.Error
                        item{
                            Column(modifier = Modifier.fillParentMaxSize(),
                            verticalArrangement = Arrangement.Center) {
                                error.error.localizedMessage?.let{
                                    Text(text = it)
                                }
                                Button(onClick = { retry() }) {
                                    Text(text = "Retry")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    private fun onClick(character: Character){
        bundle = Bundle().apply {
            putInt("id", character.id)
            putString("url", character.image)
            putStringArray("episodes", character.episode.toTypedArray())
        }
        println("bundle $bundle")
        parentFragmentManager.commit {
            replace(R.id.container, DetailsFragment()::class.java, bundle)
        }
    }
}
