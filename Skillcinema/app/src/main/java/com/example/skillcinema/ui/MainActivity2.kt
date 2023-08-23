package com.example.skillcinema.ui

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.skillcinema.MainActivity
import com.example.skillcinema.R
import com.example.skillcinema.databinding.ActivityMain2Binding
import com.example.skillcinema.ui.data.Repository
import com.example.skillcinema.ui.models.SliderData
import com.example.skillcinema.ui.support.SliderAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


private const val KEY = "AppIntro"

@AndroidEntryPoint
class MainActivity2 @Inject constructor() : AppCompatActivity() {

private lateinit var binding: ActivityMain2Binding
    lateinit var viewPager: ViewPager

    lateinit var appContext: Context
    @Inject
    lateinit var sliderAdapter: SliderAdapter
    lateinit var sliderList: ArrayList<SliderData>


    lateinit var skipButton: Button
    lateinit var indicatorSlideOne: TextView
    lateinit var indicatorSlideTwo: TextView
    lateinit var indicatorSlideThree: TextView
    @Inject
    lateinit var repository: Repository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        repository = Repository()
        appContext = applicationContext
        val appIntroWatched = repository.loadFromSharedPreference(this, KEY)
        if (appIntroWatched == "yes") {
            val activity = Intent(this@MainActivity2, MainActivity::class.java)
            startActivity(activity)
        }
        repository.loadToSharedPreference(this, KEY, "yes")
        viewPager = findViewById(R.id.view_pager)
        skipButton = findViewById(R.id.button_skip)
        indicatorSlideOne = findViewById(R.id.slide_one)
        indicatorSlideTwo = findViewById(R.id.slide_two)
        indicatorSlideThree = findViewById(R.id.slide_three)

        skipButton.setOnClickListener {
            repository.loadToSharedPreference(appContext, KEY, "yes")
            val activity = Intent(this@MainActivity2, MainActivity::class.java)
            startActivity(activity)
        }
        sliderList = ArrayList()
        sliderList.add(
            SliderData(
                resources.getString(R.string.app_name),
                resources.getString(R.string.know_about_premieres),
                R.drawable.ic_app_intro_man_tv
            )
        )
        sliderList.add(
            SliderData(
                resources.getString(R.string.app_name),
                resources.getString(R.string.lets_create_collection),
                R.drawable.ic_app_intro_woman_flowers
            )
        )
        sliderList.add(
            SliderData(
                resources.getString(R.string.app_name),
                resources.getString(R.string.share_with_friends),
                R.drawable.ic_app_intro_man_skate
            )
        )
        sliderAdapter = SliderAdapter()
        sliderAdapter.setData(sliderList, appContext)
        viewPager.adapter = sliderAdapter
        viewPager.addOnPageChangeListener(viewListener)

    }
    // for view pager
    var viewListener: ViewPager.OnPageChangeListener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
        }

        override fun onPageSelected(position: Int) {

            if (position == 0) {
                indicatorSlideTwo.setTextColor(Color.GRAY)
                indicatorSlideThree.setTextColor(Color.GRAY)
                indicatorSlideOne.setTextColor(Color.BLACK)

            } else if (position == 1) {
                indicatorSlideTwo.setTextColor(Color.BLACK)
                indicatorSlideThree.setTextColor(Color.GRAY)
                indicatorSlideOne.setTextColor(Color.GRAY)
            } else {
                indicatorSlideTwo.setTextColor(Color.GRAY)
                indicatorSlideThree.setTextColor(Color.BLACK)
                indicatorSlideOne.setTextColor(Color.GRAY)
            }
        }

        // below method is use to check scroll state.
        override fun onPageScrollStateChanged(state: Int) {}
    }



    }


