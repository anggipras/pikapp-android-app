package com.tsab.pikapp.view.onboarding.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.tsab.pikapp.R
import kotlinx.android.synthetic.main.fragment_carousel_one.*
import java.io.Serializable

class CarouselOneFragment : Fragment() {
    companion object {
        private const val CALLBACK_ON_NEXT = "callback_on_next"

        // When initiating the fragment, use this method.
        fun newInstance(onNext: () -> Unit) = CarouselOneFragment().apply {
            // Put onNext callback function to argument bundle
            arguments = bundleOf(CALLBACK_ON_NEXT to onNext as Serializable)
        }
    }

    private var onNextCallback: () -> Unit = {}

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_carousel_one, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Attempt to fetch onNext callback function from arguments
        try {
            onNextCallback = arguments?.getSerializable(CALLBACK_ON_NEXT) as () -> Unit
            nextBtn?.setOnClickListener { onNextCallback.invoke() }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}