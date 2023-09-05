package com.example.sunnyday.data

import android.content.Context
import android.util.Log
import javax.inject.Inject

private const val PREFERENCE_NAME = "sunny_day_preference"

class DataRepository @Inject constructor(){
    fun loadFromSharedPreference(context: Context, key: String): String {
        val value: String
        val prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)!!
        value = prefs.getString(key, "...").toString()
        return value
    }
    fun loadToSharedPreference(context: Context, key: String, value: String?) {
        val prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)!!
        val editor = prefs.edit()
        editor.putString(key, value)
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
            Log.d("finally SVM", "preference ${prefs.all}")
        }
    }
}