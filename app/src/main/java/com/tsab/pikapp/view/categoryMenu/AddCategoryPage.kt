package com.tsab.pikapp.view.categoryMenu

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentAddCategoryPageBinding
import com.tsab.pikapp.databinding.FragmentCategoryPageBinding
import com.tsab.pikapp.view.homev2.HomeNavigation
import com.tsab.pikapp.view.homev2.menu.MenuFragment
import com.tsab.pikapp.viewmodel.categoryMenu.CategoryViewModel
import kotlinx.android.synthetic.main.fragment_add_category_page.*
import kotlinx.android.synthetic.main.menu_fragment.*

class AddCategoryPage : Fragment() {

    private var navController: NavController? = null
    private lateinit var dataBinding: FragmentAddCategoryPageBinding
    private val viewModel: CategoryViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_category_page,
            container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        Log.e("size", viewModel.size)

        observeViewModel()
        attachInputListeners()
    }

    private fun observeViewModel() {
        viewModel.namaCategoryError.observe(viewLifecycleOwner, Observer { namaCategoryError ->
            dataBinding.namaerror.text = if (namaCategoryError.isEmpty()) "" else namaCategoryError
        })
    }

    private fun attachInputListeners() {
        dataBinding.saveBtn.setOnClickListener{
            viewModel.validateNama(categoryName.text.toString())
            if(viewModel.validateNama(categoryName.text.toString())){
                Log.e("yes", "bisaaa")
                activity?.let { viewModel.postCategory(categoryName.text.toString(), it.baseContext)}
                val intent = Intent(activity?.baseContext, HomeNavigation::class.java)
                activity?.startActivity(intent)
            }
        }

        dataBinding.backBtn.setOnClickListener {
            navController?.navigate(R.id.action_addCategoryPage_to_categoryPage)
        }

        dataBinding.toggleButton.setOnCheckedChangeListener {_, isChecked ->
            viewModel.activation = isChecked
        }

    }

}