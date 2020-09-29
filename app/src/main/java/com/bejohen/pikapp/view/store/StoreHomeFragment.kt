package com.bejohen.pikapp.view.store

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.bejohen.pikapp.R
import com.bejohen.pikapp.databinding.FragmentStoreHomeBinding

class StoreHomeFragment : Fragment() {

    private lateinit var dataBinding: FragmentStoreHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        dataBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_store_home, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataBinding.buttonMyProduct.setOnClickListener {
            val action = StoreHomeFragmentDirections.actionToStoreMyProductFragment()
            Navigation.findNavController(view).navigate(action)
        }
    }
}