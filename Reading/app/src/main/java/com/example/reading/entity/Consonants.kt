package com.example.reading.entity

class Consonants() : Letters {
    override val consonant = true
    override val vowel = false
    override val simpleToSayLetters = listOf<Char>('М', 'Н', 'Х', 'К', 'Т', 'П', 'В', 'Г', 'Б', 'Д')
    override val simpleLettersSize = 10
    override val difficultToSayLetters = listOf<Char>('Ф', 'С', 'Й', 'З', 'Л', 'Ш', 'Р', 'Ж', 'Ц', 'Ч', 'Щ')
    override val difficultLettersSize = 11
}