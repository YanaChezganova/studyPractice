package com.example.myapplication1

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.myapplication1.RetrofitServices.charactersApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel: ViewModel()  {

     val character = MutableStateFlow<Character>(
         Character(0,"","","","",
             Location("",""),""
         )
     )
    val episodeInfo = MutableStateFlow<List<Episode>>(
       listOf( Episode(0,"","","",
            listOf("",""),"", "" )
    ))
    fun loadEpisodeInfo(episodeNumber: List<Int>){
        viewModelScope.launch {
            kotlin.runCatching {
                charactersApi.getEpisodeInfo(episodeNumber)
            }.fold(
                onSuccess = {
                    //   state.value = State.Done
                    episodeInfo.value = it
                },
                onFailure = {
                    //   state.value = State.Error
                    Log.d("ViewModel", "EpisodeInfo ${it.message} ${it.cause}")
                }
            )
        }
    }

    fun loadCharacterInfo(characterId: Int) {
        // state.value = State.Loading
        viewModelScope.launch {
            kotlin.runCatching {
                charactersApi.getCharacterInfo(characterId)
            }.fold(
                onSuccess = {
                    //   state.value = State.Done
                    character.value = it
                },
                onFailure = {
                    //   state.value = State.Error
                    Log.d("ViewModel", "CharacterInfo ${it.message} ${it.cause}")
                }
           )
       }
   }
    /*   fun getResultStream(): Flow<PagingData<Character>> {
           return Pager(
               config = PagingConfig(
                   pageSize = 20,
                   enablePlaceholders = false
               ),
               pagingSourceFactory = { ListPagingSource(this) }
           ).flow
  }*/

}