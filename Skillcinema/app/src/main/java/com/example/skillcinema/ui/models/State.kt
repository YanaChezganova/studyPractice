package com.example.skillcinema.ui.models

sealed class State{
    object Loading: State()
    object Done: State()
    object Error: State()
}
