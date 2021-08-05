package com.tsab.pikapp.view.onboarding.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.tsab.pikapp.R
import kotlin.system.exitProcess

class CarouselOneFragment : Fragment() {
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
                object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        activity?.moveTaskToBack(true)
                        System.exit(-1)
                    }
                })

        return inflater.inflate(R.layout.fragment_carousel_one, container, false)
    }
}