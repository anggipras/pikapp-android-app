package com.tsab.pikapp.view

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.ActivityCarouselBinding
import com.tsab.pikapp.util.SessionManager
import com.tsab.pikapp.view.onboarding.CarouselViewPagerAdapter
import com.tsab.pikapp.view.onboarding.screens.CarouselOneFragment
import com.tsab.pikapp.view.onboarding.screens.CarouselThreeFragment
import com.tsab.pikapp.view.onboarding.screens.CarouselTwoFragment
import com.tsab.pikapp.viewmodel.CarouselViewModel

class CarouselActivity : AppCompatActivity() {
    private val viewModel: CarouselViewModel by viewModels()
    private lateinit var dataBinding: ActivityCarouselBinding

    private var sessionManager = SessionManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataBinding = ActivityCarouselBinding.inflate(layoutInflater)
        setContentView(dataBinding.root)

        sessionManager.setFirstApp(1)
        sessionManager.setProfileNum(0)

        setupViewPager()
        attachInputListener()

        observeViewModel()
    }

    override fun onBackPressed() {
        val firstApp = sessionManager.getFirstApp()
        if (viewModel.currentPage.value == 0) {
            if (firstApp == 1) {
                finishAffinity()
            }
        } else {
            viewModel.previousPage()
        }
    }

    private fun setupViewPager() {
        val fragments: ArrayList<Fragment> = arrayListOf(
                CarouselOneFragment(),
                CarouselTwoFragment(),
                CarouselThreeFragment()
        )

        val adapter = CarouselViewPagerAdapter(fragments, this)
        dataBinding.carouselViewPager.adapter = adapter
        dataBinding.carouselViewPager.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                viewModel.setCurrentPage(position)
                super.onPageSelected(position)
            }
        })
    }

    private fun attachInputListener() {
        dataBinding.nextButton.setOnClickListener {
            if (dataBinding.nextButton.visibility == View.VISIBLE) viewModel.nextPage()
        }

        dataBinding.carouselPage1.setOnClickListener { viewModel.setCurrentPage(0) }
        dataBinding.carouselPage2.setOnClickListener { viewModel.setCurrentPage(1) }
        dataBinding.carouselPage3.setOnClickListener { viewModel.setCurrentPage(2) }
    }

    private fun observeViewModel() {
        viewModel.currentPage.observe(this, androidx.lifecycle.Observer { currentPage ->
            dataBinding.carouselViewPager.currentItem = currentPage

            dataBinding.carouselPage1.setColorFilter(
                    if (currentPage == 0) ContextCompat.getColor(applicationContext,
                            R.color.colorGreen)
                    else Color.TRANSPARENT)
            dataBinding.carouselPage2.setColorFilter(
                    if (currentPage == 1) ContextCompat.getColor(applicationContext,
                            R.color.colorGreen)
                    else Color.TRANSPARENT)
            dataBinding.carouselPage3.setColorFilter(
                    if (currentPage == 2) ContextCompat.getColor(applicationContext,
                            R.color.colorGreen)
                    else Color.TRANSPARENT)

            dataBinding.nextButton.visibility = if (currentPage == 2) View.INVISIBLE else View.VISIBLE
        })
    }
}