package com.example.reading.entity

import kotlin.random.Random

interface Letters {
    val consonant: Boolean
    val vowel: Boolean
    val simpleToSayLetters: List<Char>
    val simpleLettersSize: Int
    val difficultToSayLetters: List<Char>
    val difficultLettersSize: Int
    fun takeRandomLetter(size: Int) = Random.nextInt(0, size)
}