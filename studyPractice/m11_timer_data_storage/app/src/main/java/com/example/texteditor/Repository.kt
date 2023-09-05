package com.example.texteditor

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

private const val PREFERENCE_NAME = "my_preference"
private const val SHARED_PREFERENCE_KEY = "shared_prefs_key"

class Repository {
    private var localVariable: String? = null

    fun getText(context: Context): String {
        return when {
            getDataFromLocalVariable() != null -> getDataFromLocalVariable()!!
            getDataFromSharedPreference(context) != null -> getDataFromSharedPreference(context)!!
            else -> "There is no text"
        }
    }

    private fun getDataFromSharedPreference(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE)
        return prefs.getString(SHARED_PREFERENCE_KEY, " ")
    }

    private fun getDataFromLocalVariable(): String? {
        return localVariable
    }

    private fun setDataInSharedPreference(context: Context, text: String) {
        val editor: SharedPreferences.Editor = context.getSharedPreferences(
            PREFERENCE_NAME,
            MODE_PRIVATE
        ).edit()
        editor.putString(SHARED_PREFERENCE_KEY, text)
        editor.apply()
    }

    private fun clearSharedPreference(context: Context) {
        val editor: SharedPreferences.Editor = context.getSharedPreferences(
            PREFERENCE_NAME,
            MODE_PRIVATE
        ).edit()
        editor.clear()
        editor.apply()
    }

    fun saveText(context: Context, text: String) {
        localVariable = text
        setDataInSharedPreference(context, text)
    }

    fun clearText(context: Context) {
        localVariable = null
        clearSharedPreference(context)
    }
}