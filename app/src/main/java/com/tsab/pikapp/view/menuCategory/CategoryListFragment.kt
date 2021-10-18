package com.tsab.pikapp.view.menuCategory

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentCategoryListBinding
import com.tsab.pikapp.models.model.MenuCategory
import com.tsab.pikapp.util.SessionManager
import com.tsab.pikapp.util.setAllOnClickListener
import com.tsab.pikapp.view.homev2.HomeActivity
import com.tsab.pikapp.view.menuCategory.lists.CategoryListAdapter
import com.tsab.pikapp.viewmodel.categoryMenu.CategoryViewModel

class CategoryListFragment : Fragment() {
    private val viewModel: CategoryViewModel by activityViewModels()
    private lateinit var navController: NavController
    private lateinit var dataBinding: FragmentCategoryListBinding

    private lateinit var categoryListAdapter: CategoryListAdapter
    private val sessionManager = SessionManager()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_category_list,
            container, false
        )
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    Intent(activity?.baseContext, HomeActivity::class.java).apply {
                        startActivity(this)
                        activity?.finish()
                    }
                }
            })

        navController = Navigation.findNavController(view)
        sessionManager.setHomeNav(1)

        observeViewModel()
        attachInputListeners()
        setupRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchCategoryList()
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            dataBinding.loadingOverlay.loadingView.visibility =
                if (isLoading) View.VISIBLE else View.GONE
        })

        viewModel.categoryList.observe(viewLifecycleOwner, Observer { categoryList ->
            categoryListAdapter.setCategoryList(categoryList)
        })
    }

    private fun attachInputListeners() {
        dataBinding.headerLayout.backButton.setAllOnClickListener(View.OnClickListener {
            Intent(activity?.baseContext, HomeActivity::class.java).apply {
                startActivity(this)
                activity?.finish()
            }
        }, view)

        dataBinding.daftarKategoriChangeOrderButton.setOnClickListener {
            Intent(activity?.baseContext, SortActivity::class.java).apply {
                putExtra("SORT_NAV", 1)
                startActivity(this)
            }
        }

        dataBinding.tambahKategoriGroup.setAllOnClickListener(View.OnClickListener {
            navController.navigate(R.id.action_categoryListPage_to_addCategoryPage)
        }, view)
    }

    private fun setupRecyclerView() {
        categoryListAdapter = CategoryListAdapter(
            viewModel.categoryList.value!!.toMutableList(),
            object : CategoryListAdapter.OnItemClickListener {
                override fun onItemClick(category: MenuCategory) {
                    viewModel.getCategoryName(category.categoryName)
                    viewModel.getCategoryOrder(category.categoryOrder.toString())
                    viewModel.getCategoryActivation(category.isActive.toString())
                    viewModel.getCategoryId(category.id.toString())
                    viewModel.getCategoryMenuSize(category.productSize.toString())
                    Log.e("activation rv", category.isActive.toString())
                    Log.e("vm", viewModel.activationToggle.value)

                    navController.navigate(R.id.action_categoryListPage_to_editCategoryPage)
                }
            }
        )

        dataBinding.daftarKategoriRecyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = categoryListAdapter
        }
    }
}
