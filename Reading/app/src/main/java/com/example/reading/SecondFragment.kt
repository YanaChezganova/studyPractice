package com.example.reading

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.reading.databinding.FragmentSecondBinding
import com.example.reading.entity.Consonants
import com.example.reading.entity.Vowels
import com.example.reading.entity.Worlds
import kotlin.random.Random

private const val MENU_ID = "menu_id"

class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null
    private val binding get() = _binding!!
    private val vowels = Vowels()
    private val consonants = Consonants()
    private var menuRate = 0
    private var previousText = ""
    private var counterOfDoubles = 0
    private var counterOfClick = 0
    private var exceptions1 = listOf<String>("ЖЫ", "ШЫ", "ЩЫ", "ЧЫ")
    private var exceptions2 = listOf<String>("ЧЯ", "ЩЯ", "ШЯ", "ЧЮ", "ЩЮ", "ШЮ", "ЖЯ", "ЦЯ", "ЙЁ")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        arguments?.let {
            menuRate = it.getInt(MENU_ID)
        }
        binding.textviewFirst.text = makeSyllable(menuRate)
        counterOfClick++
        if (menuRate in 0..3) {
            binding.changePosition.visibility = View.VISIBLE
            binding.changePosition.setOnClickListener {
                counterOfClick++
                binding.counter.text = counterOfClick.toString()

                val text = binding.textviewFirst.text
                val firstLetter = text.first().toString()
                val lastLetter = text.last().toString()
                binding.textviewFirst.text =
                    this.getString(R.string.changed_text, lastLetter, firstLetter)

            }
        } else binding.changePosition.visibility = View.INVISIBLE

        binding.buttonNext.setOnClickListener {
            binding.funnyImage.visibility = View.GONE
            binding.greeting.visibility = View.GONE
            counterOfClick++
            counterOfDoubles = 0
            binding.textviewFirst.text = makeSyllable(menuRate)
            binding.textviewFirst.setTextColor(randomColor())
            binding.counter.text = counterOfClick.toString()
            if (counterOfClick % 6 == 0) showFunnyImage()
        }
        binding.buttonClose.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }
        binding.counter.text = counterOfClick.toString()
        return binding.root
    }

    private fun makeSyllable(menu: Int): String {
        when (menu) {
            0 -> {
                return makeSyllableToView(menu, vowels.simpleToSayLetters, vowels.simpleLettersSize,
                consonants.simpleToSayLetters, consonants.simpleLettersSize)
            }
            1 -> {
                return makeSyllableToView(menu, vowels.simpleToSayLetters, vowels.simpleLettersSize,
                    consonants.difficultToSayLetters, consonants.difficultLettersSize)
            }
            2 -> {
                return makeSyllableToView(menu, vowels.difficultToSayLetters, vowels.difficultLettersSize,
                    consonants.simpleToSayLetters, consonants.simpleLettersSize)
            }
            3 -> { return makeSyllableToView(menu, vowels.difficultToSayLetters, vowels.difficultLettersSize,
                consonants.difficultToSayLetters, consonants.difficultLettersSize)
            }
            4 -> {
                return makeWordToView(120.0F, Worlds().wordsThreeLetters)
            }
            5 -> {
                return makeWordToView(100.0F, Worlds().wordsFourLetters)
            }
            6 -> {
                return makeWordToView(80.0F, Worlds().wordsWithAdditionalSign)
            }
            else -> {
                return makeWordToView(80.0F, Worlds().wordsWithIntonation)
            }
        }
    }

    private fun compareWithPrevious(consonant: String, vowel: String, menu: Int): String {
        var text = consonant + vowel
        if (exceptions1.contains(text)) text = "ФУ"
        if (exceptions2.contains(text)) text = "ФЁ"
        if (previousText == text) {
            counterOfDoubles++
            if (counterOfDoubles < 3)
                makeSyllable(menu)
        }
        previousText = text
        return text
    }

    private fun randomColor(): Int {
        val firstPair = Random.nextInt(0, 255)
        val secondPair = Random.nextInt(0, 255)
        val thirdPair = Random.nextInt(0, 255)
        return Color.rgb(firstPair, secondPair, thirdPair)
    }

    private fun showFunnyImage() {
        val number = Random.nextInt(1, 6)
        binding.greeting.setTextColor(randomColor())
        binding.greeting.visibility = View.VISIBLE
        when (number) {
            1 -> {
                binding.funnyImage.background =
                    AppCompatResources.getDrawable(binding.funnyImage.context, R.drawable.fun_apple)
                binding.greeting.text = binding.funnyImage.context.getString(R.string.greeting1)
            }
            2 -> {
                binding.funnyImage.background = AppCompatResources.getDrawable(
                    binding.funnyImage.context,
                    R.drawable.fun_hangehog
                )
                binding.greeting.text = binding.funnyImage.context.getString(R.string.greeting2)
            }
            3 -> {
                binding.funnyImage.background = AppCompatResources.getDrawable(
                    binding.funnyImage.context,
                    R.drawable.fun_cucumber
                )
                binding.greeting.text = binding.funnyImage.context.getString(R.string.greeting3)
            }
            4 -> {
                binding.funnyImage.background =
                    AppCompatResources.getDrawable(binding.funnyImage.context, R.drawable.fun_dog)
                binding.greeting.text = binding.funnyImage.context.getString(R.string.greeting4)
            }
            5 -> {
                binding.funnyImage.background = AppCompatResources.getDrawable(
                    binding.funnyImage.context,
                    R.drawable.fun_gardener
                )
                binding.greeting.text = binding.funnyImage.context.getString(R.string.greeting5)
            }
            else -> {
                binding.funnyImage.background =
                    AppCompatResources.getDrawable(binding.funnyImage.context, R.drawable.fun_dog)
                binding.greeting.text = binding.funnyImage.context.getString(R.string.greeting1)
            }
        }
        binding.funnyImage.visibility = View.VISIBLE
    }

    private fun makeWordToView(textSize: Float, wordsList: List<String>): String {
        binding.textviewFirst.textSize = textSize
        val words = wordsList
        val maxLimit = words.size
        val indexInWordsList = Random.nextInt(0, maxLimit)
        return words[indexInWordsList]
    }
    private fun makeSyllableToView(menu: Int,
                                   vowelsList: List<Char>,
                                   vowelsListSize: Int,
                                   consonantList: List<Char>,
                                   consonantListSize: Int): String{
        val indexInListOfVowels = vowels.takeRandomLetter(vowelsListSize)
        val indexInListOfConsonants =
            consonants.takeRandomLetter(consonantListSize)
        val vowel = vowelsList[indexInListOfVowels].toString()
        val consonant = consonantList[indexInListOfConsonants].toString()
        return compareWithPrevious(consonant, vowel, menu)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}