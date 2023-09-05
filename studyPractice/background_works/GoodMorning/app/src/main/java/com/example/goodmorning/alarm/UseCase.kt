package com.example.goodmorning.alarm

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.goodmorning.ui.main.MainFragment

private const val PREFERENCE_NAME = "good_morning_preference"
class UseCase {
    fun loadFromSharedPreference(context: Context, key: String): Long {
        val value: Long
        val prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)!!
        value = prefs.getLong(key, 0L)
        return value
    }
    fun loadToSharedPreference(context: Context, key: String, value: Long) {
        val prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)!!
        val editor = prefs.edit()
        editor.putLong(key, value)
        editor.apply()
    }
    fun clearSharedPreference(context: Context, key: String) {  //не все, а по ключам
        val prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)!!
        val editor = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).edit()

        try {
            editor.remove(key)
            editor.apply()
        } catch (e: Exception) {
            Log.d("onCleared", "not cleared shared preference")
        } finally {
            Log.d("finally UseCase", "preference ${prefs.all}")
        }
    }
}