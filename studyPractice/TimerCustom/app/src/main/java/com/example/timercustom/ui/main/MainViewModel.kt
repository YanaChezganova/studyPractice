package com.example.timercustom.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    var timerState = TimerState(0, 0, 0, 0, false)
    val updatedTimer = MutableLiveData<TimerPlaying>()
    var timerPlaying = TimerPlaying(0, 0, 0, false)

    fun changeTimerState(time: Long, hour: Int, minute: Int, second: Int, state: Boolean) {
        timerState = TimerState(time, hour, minute, second, state)

    }

    fun changeTimerPlaying(hour: Int, minute: Int, second: Int, state: Boolean) {
        timerPlaying = TimerPlaying(hour, minute, second, state)
        updateTime(timerPlaying)
    }


    private fun updateTime(timer: TimerPlaying) {
        updatedTimer.value = timer
    }

}