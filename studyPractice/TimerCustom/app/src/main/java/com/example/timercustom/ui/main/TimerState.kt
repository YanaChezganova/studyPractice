package com.example.timercustom.ui.main


class TimerState(val timeInMillis: Long,
                 val hour: Int,
                 val minute: Int,
                 val second: Int,
                 val isPlayed: Boolean ) {

 }
class TimerPlaying(
                 val hour: Int,
                 val minute: Int,
                 val second: Int,
                 val isPlayed: Boolean ) {

}