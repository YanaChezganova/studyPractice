package com.example.photos.ui.main

import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test
import org.junit.jupiter.api.Assertions
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.powermock.core.classloader.annotations.PrepareForTest


@RunWith(MockitoJUnitRunner::class)
@PrepareForTest(BlankFragment::class)

class BlankFragmentUnitTests {
    private val STRING = listOf<String>(
        "Good morning!",
        "Good day!",
        "Good evening!",
        "Good night!"
    )

    @Test
    fun testGreetingText() {
        val titleText = TitleText()
        val message = titleText.setGreetingText(0)
        Assertions.assertEquals(STRING[0], message)
    }

    @Test
    fun onButtonClickCheckTextTest() {
        val titleText = TitleText()
        val message = titleText.setGreetingText(3)
        val expectedResult = titleText.checkText(message)
        assertTrue(expectedResult)
    }

    @Test(expected = NullPointerException::class)
    fun nullStringTest() {
        val message: String? = null
        assertTrue(message!!.isEmpty())
    }

    @Test
    fun checkWrongTitleText() {
        val titleText = TitleText()
        val message = titleText.setGreetingText(1)
        assertFalse(message == "Hello World!")
    }
}
