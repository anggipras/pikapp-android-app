package com.tsab.pikapp.view.homev2.menu

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentLoginV2FirstBinding
import com.tsab.pikapp.databinding.MenuFragmentBinding
import com.tsab.pikapp.view.RegisterV2Activity
import com.tsab.pikapp.view.categoryMenu.CategoryAdapter
import com.tsab.pikapp.view.categoryMenu.CategoryNavigation
import com.tsab.pikapp.view.homev2.HomeNavigation
import com.tsab.pikapp.viewmodel.homev2.MenuViewModel
import com.tsab.pikapp.viewmodel.onboarding.signup.SignupOnboardingViewModelV2
import kotlinx.android.synthetic.main.menu_fragment.*

class MenuFragment : Fragment() {

    private val viewModel: MenuViewModel by activityViewModels()
    lateinit var categoryAdapter: CategoryAdapter
    lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var dataBinding: MenuFragmentBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.menu_fragment,
            container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerview_category.setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(requireView().context, LinearLayoutManager.HORIZONTAL, false)
        recyclerview_category.layoutManager = linearLayoutManager

        activity?.let { viewModel.getMenuCategoryList(it.baseContext, recyclerview_category)}

        dataBinding.plusBtn.setOnClickListener{
            val intent = Intent(activity?.baseContext, CategoryNavigation::class.java)
            intent.putExtra("category_size", viewModel.size)
            activity?.startActivity(intent)
        }
    }

}