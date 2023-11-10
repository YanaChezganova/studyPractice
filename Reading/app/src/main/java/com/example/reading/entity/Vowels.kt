package com.example.reading.entity

class Vowels: Letters {
    override val consonant = false
    override val vowel = true
    override val simpleToSayLetters = listOf<Char>('А', 'У', 'И', 'О', 'Ы')
    override val simpleLettersSize = 5
    override val difficultToSayLetters = listOf<Char>('Е', 'Ё', 'Я', 'Э', 'Ю')
    override val difficultLettersSize = 5

}