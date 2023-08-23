package com.example.skillcinema.ui.support

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.example.skillcinema.R
import com.example.skillcinema.ui.models.SliderData
import javax.inject.Inject

class SliderAdapter @Inject constructor() : PagerAdapter() {
    private lateinit var context: Context
    private lateinit var sliderList: ArrayList<SliderData>
    fun setData(sliderList: ArrayList<SliderData>, context: Context) {
        this.sliderList = sliderList
        this.context = context
    }

    override fun getCount(): Int {
        return sliderList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        // inside isView From object method we are
        // returning our Relative layout object.
        return view === `object` as RelativeLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        // in this method we will initialize all our layout
        // items and inflate our layout file as well.
        val layoutInflater: LayoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater.inflate(R.layout.slider_item, container, false)

        val imageView: ImageView = view.findViewById(R.id.image)
        //    val sliderHeading: TextView = view.findViewById(R.id.header)
        val sliderDescription: TextView = view.findViewById(R.id.description)

        val sliderData: SliderData = sliderList.get(position)
        //   sliderHeading.text = sliderData.slideTitle
        sliderDescription.text = sliderData.slideDescription
        imageView.setImageResource(sliderData.slideImage)

        container.addView(view)

        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as RelativeLayout)
    }

}
