package com.bejohen.pikapp.view.categoryProduct

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bejohen.pikapp.R
import com.bejohen.pikapp.databinding.FragmentCategoryBinding
import com.bejohen.pikapp.models.model.ItemHomeCategory
import com.bejohen.pikapp.viewmodel.categoryProduct.CategoryViewModel

class CategoryFragment : Fragment() {

    private lateinit var viewModel: CategoryViewModel
    private var categoryUuid = 0
    private var currentCategory: ItemHomeCategory? = null
    private lateinit var dataBinding: FragmentCategoryBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_category, container, false)
        viewModel = ViewModelProviders.of(this).get(CategoryViewModel::class.java)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            categoryUuid = CategoryFragmentArgs.fromBundle(it).categoryUuid
        }
        viewModel.fetch(categoryUuid)

        observeViewModel()
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun observeViewModel() {
        viewModel.categoryData.observe(this, Observer { category ->
            currentCategory = category
            currentCategory?.let {
                dataBinding.category = category
            }
        })
    }
}