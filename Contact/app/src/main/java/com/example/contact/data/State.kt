package com.example.contact.data

sealed class State{
    object Ready: State()
    object Loading: State()
    object Done: State()
    object Error: State()
}
