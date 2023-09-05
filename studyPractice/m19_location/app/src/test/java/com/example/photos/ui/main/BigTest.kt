package com.example.photos.ui.main

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BigTest {
    @Test
    @Throws(Exception::class)
    fun setUp() {
      val context = ApplicationProvider.getApplicationContext<Context>()
    }
}