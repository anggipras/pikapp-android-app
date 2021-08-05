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
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentCategoryPageBinding
import com.tsab.pikapp.view.homev2.HomeNavigation
import com.tsab.pikapp.viewmodel.categoryMenu.CategoryViewModel
import kotlinx.android.synthetic.main.menu_fragment.recyclerview_category

class CategoryPage : Fragment(), MenuCategoryAdapter.OnItemClickListener {

    private var navController: NavController? = null
    lateinit var menuCategoryAdapter: MenuCategoryAdapter
    lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var dataBinding: FragmentCategoryPageBinding
    private val viewModel: CategoryViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_category_page,
            container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        recyclerview_category.setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(requireView().context)
        recyclerview_category.layoutManager = linearLayoutManager

        activity?.let { viewModel.getMenuCategoryList(it.baseContext, recyclerview_category, this)}

        attachInputListeners()

    }

    private fun attachInputListeners() {
        dataBinding.backBtn.setOnClickListener {
            val intent = Intent(activity?.baseContext, HomeNavigation::class.java)
            activity?.startActivity(intent)
        }

        dataBinding.buttonSort.setOnClickListener{
            val intent = Intent(activity?.baseContext, SortActivity::class.java)
            activity?.startActivity(intent)
        }

        dataBinding.addCategory.setOnClickListener {
            navController?.navigate(R.id.action_categoryPage_to_addCategoryPage)
        }
    }

    override fun onItemClick(position: Int) {
        viewModel.menuCategoryAdapter.notifyItemChanged(position)

        val categoryName = viewModel.menuCategoryAdapter.menuCategoryList[position].category_name
        Log.e("category name", categoryName)
        viewModel.getCategoryName(categoryName.toString())

        val categoryOrder = viewModel.menuCategoryAdapter.menuCategoryList[position].category_order.toString()
        Log.e("category order", categoryOrder)
        viewModel.getCategoryOrder(categoryOrder)

        val activationToggle = viewModel.menuCategoryAdapter.menuCategoryList[position].is_active.toString()
        Log.e("activation", activationToggle)
        viewModel.getCategoryActivation(activationToggle)

        val categoryId = viewModel.menuCategoryAdapter.menuCategoryList[position].id.toString()
        Log.e("category id", categoryId)
        viewModel.getCategoryId(categoryId)

        navController?.navigate(R.id.action_categoryPage_to_editCategoryPage)
    }

}
