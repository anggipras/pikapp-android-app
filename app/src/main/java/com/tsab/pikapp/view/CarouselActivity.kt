package com.tsab.pikapp.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.tsab.pikapp.R
import com.tsab.pikapp.view.onboarding.CarouselViewPagerAdapter
import com.tsab.pikapp.view.onboarding.screens.CarouselOneFragment
import com.tsab.pikapp.view.onboarding.screens.CarouselThreeFragment
import com.tsab.pikapp.view.onboarding.screens.CarouselTwoFragment

class CarouselActivity : AppCompatActivity() {
    lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carousel)

        viewPager = findViewById(R.id.carousel_viewpager)

        val onNextPageCallback = { viewPager.currentItem += 1 }
        val fragments: ArrayList<Fragment> = arrayListOf(
                CarouselOneFragment.newInstance(onNextPageCallback),
                CarouselTwoFragment.newInstance(onNextPageCallback),
                CarouselThreeFragment()
        )

        val adapter = CarouselViewPagerAdapter(fragments, this)
        viewPager.adapter = adapter
    }

    override fun onBackPressed() {
        if (viewPager.currentItem == 0) {
            super.onBackPressed()
        } else {
            viewPager.currentItem = viewPager.currentItem - 1
        }
    }
}