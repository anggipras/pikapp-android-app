package com.tsab.pikapp.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.tsab.pikapp.R
import com.tsab.pikapp.view.onboarding.CarouselViewPagerAdapter
import com.tsab.pikapp.view.onboarding.screens.CarouselOneFragment
import com.tsab.pikapp.view.onboarding.screens.CarouselThreeFragment
import com.tsab.pikapp.view.onboarding.screens.CarouselTwoFragment
import kotlinx.android.synthetic.main.fragment_onboarding_view_pager.*

class CarouselActivity : AppCompatActivity() {
    lateinit var viewPager: ViewPager2
    lateinit var nextBtn: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carousel)

      viewPager =  findViewById(R.id.carousel_viewpager)
      nextBtn = findViewById(R.id.nextBtn)

        val fragments: ArrayList<Fragment> = arrayListOf(
            CarouselOneFragment(),
            CarouselTwoFragment(),
            CarouselThreeFragment()
        )

        nextBtn.setOnClickListener {
            viewPager.currentItem = viewPager.currentItem + 1
        }

        val adapter = CarouselViewPagerAdapter(fragments, this)
        viewPager.adapter = adapter

    }


    override fun onBackPressed() {
        if(viewPager.currentItem == 0){
            super.onBackPressed()
        }else{
            viewPager.currentItem = viewPager.currentItem - 1
        }
    }

}