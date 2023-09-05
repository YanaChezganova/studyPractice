package com.example.sunnyday.entity

sealed class State{
    object Ready: State()
    object Loading: State()
    object Done: State()
    object Error: State()
}
