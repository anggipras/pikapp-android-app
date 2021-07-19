package com.tsab.pikapp.view

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.tsab.pikapp.R
import com.tsab.pikapp.view.onboarding.CarouselViewPagerAdapter
import com.tsab.pikapp.view.onboarding.screens.CarouselOneFragment
import com.tsab.pikapp.view.onboarding.screens.CarouselThreeFragment
import com.tsab.pikapp.view.onboarding.screens.CarouselTwoFragment
import com.tsab.pikapp.viewmodel.CarouselViewModel

class CarouselActivity : AppCompatActivity() {
    private val viewModel: CarouselViewModel by viewModels()

    lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carousel)

        viewPager = findViewById(R.id.carousel_viewpager)
        viewModel.currentPage.observe(this, androidx.lifecycle.Observer { currentPage ->
            viewPager.currentItem = currentPage
        })

        val fragments: ArrayList<Fragment> = arrayListOf(
                CarouselOneFragment(),
                CarouselTwoFragment(),
                CarouselThreeFragment()
        )

        val adapter = CarouselViewPagerAdapter(fragments, this)
        viewPager.adapter = adapter
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                viewModel.setCurrentPage(position)
                super.onPageSelected(position)
            }
        })
    }

    override fun onBackPressed() {
        if (viewPager.currentItem == 0) {
            super.onBackPressed()
        } else {
            viewModel.previousPage()
        }
    }
}