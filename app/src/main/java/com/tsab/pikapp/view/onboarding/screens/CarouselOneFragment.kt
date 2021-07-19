package com.tsab.pikapp.view.onboarding.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.tsab.pikapp.R
import com.tsab.pikapp.viewmodel.CarouselViewModel
import kotlinx.android.synthetic.main.fragment_carousel_one.*

class CarouselOneFragment : Fragment() {
    private val viewModel: CarouselViewModel by activityViewModels()

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_carousel_one, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        nextBtn.setOnClickListener { viewModel.nextPage() }
    }
}