package com.tsab.pikapp.view.onboarding.screens

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentCarouselThreeBinding
import com.tsab.pikapp.view.LoginV2Activity
import com.tsab.pikapp.view.RegisterV2Activity

class CarouselThreeFragment : Fragment() {
    lateinit var dataBinding: FragmentCarouselThreeBinding

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_carousel_three, container,
                false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataBinding.masukButton.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        dataBinding.masukButton.setOnClickListener {
            val intent = Intent(activity?.baseContext, LoginV2Activity::class.java)
            activity?.startActivity(intent)
        }

        dataBinding.daftarButton.setOnClickListener {
            val intent = Intent(activity?.baseContext, RegisterV2Activity::class.java)
            activity?.startActivity(intent)
        }
    }
}