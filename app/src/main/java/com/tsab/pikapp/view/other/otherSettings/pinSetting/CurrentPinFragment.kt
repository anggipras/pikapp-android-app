package com.tsab.pikapp.view.other.otherSettings.pinSetting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentCurrentPinBinding

class CurrentPinFragment : Fragment() {

    private lateinit var dataBinding: FragmentCurrentPinBinding

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        dataBinding = FragmentCurrentPinBinding.inflate(inflater, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataBinding.currentPin.setOnClickListener { Navigation.findNavController(view).navigate(R.id.navigateTo_newPinFragment) }
    }
}