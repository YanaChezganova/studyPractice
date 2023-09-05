package com.example.goodmorning.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.util.Log
import android.widget.Toast
import com.example.goodmorning.R
import com.example.goodmorning.ui.main.MainFragment

class AlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, p1: Intent?) {
        Log.d("onReceive", "Broadcast Receiver is very happy")
        Toast.makeText(context, "OnReceive alarm test", Toast.LENGTH_SHORT).show()
        val mp = MediaPlayer.create(context, R.raw.rington)
        mp.start()
    }
}