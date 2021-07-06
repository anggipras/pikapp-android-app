package com.tsab.pikapp.view.store.myProduct

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentStoreMyProductBinding
import com.tsab.pikapp.view.home.HomeViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator

class StoreMyProductFragment : Fragment() {

    private lateinit var dataBinding: FragmentStoreMyProductBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_store_my_product, container, false)

        val fragmentList = arrayListOf<Fragment>(
            StoreMyProductAvailableFragment(),
            StoreMyProductNotAvailableFragment()
        )

        val adapter = HomeViewPagerAdapter(fragmentList, requireActivity().supportFragmentManager, lifecycle)
        dataBinding.myProductViewPager.adapter = adapter
        dataBinding.myProductViewPager.isUserInputEnabled = false
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        TabLayoutMediator(
            dataBinding.tabLayoutMyProduct,
            dataBinding.myProductViewPager
        ) { tab, position ->
            if (position == 0) {
                tab.text = "Tersedia"
            } else if (position == 1) {
                tab.text = "Tidak Tersedia"
            }
            dataBinding.myProductViewPager.setCurrentItem(tab.position, true)
        }.attach()

        dataBinding.buttonToAddProduct.setOnClickListener {
            val action = StoreMyProductFragmentDirections.actionToStoreMyProductFormFragment(true, "")
            Navigation.findNavController(view).navigate(action)
        }
    }
}