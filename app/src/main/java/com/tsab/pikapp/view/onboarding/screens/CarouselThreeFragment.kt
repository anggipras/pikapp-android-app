package com.tsab.pikapp.view.onboarding.screens

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tsab.pikapp.R
import com.tsab.pikapp.view.LoginV2Activity
import com.tsab.pikapp.view.RegisterV2Activity
import kotlinx.android.synthetic.main.fragment_carousel_three.*

class CarouselThreeFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_carousel_three, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        masuk.setOnClickListener {
            val intent = Intent(activity?.baseContext, LoginV2Activity::class.java)
            activity?.startActivity(intent)
        }

        daftarBtn.setOnClickListener {
            val intent = Intent(activity?.baseContext, RegisterV2Activity::class.java)
            activity?.startActivity(intent)
        }
    }
}