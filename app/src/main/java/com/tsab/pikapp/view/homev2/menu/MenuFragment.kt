package com.tsab.pikapp.view.homev2.menu

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.MenuFragmentBinding
import com.tsab.pikapp.view.homev2.SearchActivity
import com.tsab.pikapp.view.menuCategory.CategoryNavigation
import com.tsab.pikapp.view.menuCategory.SortActivity
import com.tsab.pikapp.viewmodel.homev2.MenuViewModel
import kotlinx.android.synthetic.main.menu_fragment.*

class MenuFragment : Fragment() {
    private val viewModel: MenuViewModel by activityViewModels()
    private lateinit var dataBinding: MenuFragmentBinding
    lateinit var linearLayoutManager: LinearLayoutManager

    var categoryName: String = ""
    private var viewPager: ViewPager? = null
    private var tabLayout: TabLayout? = null
    private var tabCount: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.menu_fragment, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getMenuCategoryList()
        setMenuInvisible()
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()

        setMenuInvisible()
        observeViewModel()
        attachInputListeners()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.restartFragment()
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            viewModel.size.observe(viewLifecycleOwner, Observer {
                initViews()
            })

            dataBinding.loadingOverlay.loadingView.visibility =
                if (isLoading) View.VISIBLE else View.GONE

            if (!isLoading) {
                shimmerFrameLayout.stopShimmer()
                dataBinding.shimmerFrameLayout.visibility = View.GONE
                if (viewModel.size.value == 0) {
                    invisibleMenu()
                    dataBinding.textview2.visibility = View.VISIBLE
                    dataBinding.imageView18.visibility = View.VISIBLE
                    dataBinding.addCategory.visibility = View.VISIBLE
                } else if (viewModel.size.value != null) {
                    viewModel.categoryListResult.observe(
                        viewLifecycleOwner,
                        Observer { categoryList ->
                            categoryList?.let {
                                invisibleMenuNull()
                                dataBinding.viewpager.visibility = View.VISIBLE
                                dataBinding.tabs.visibility = View.VISIBLE
                                dataBinding.appbar.visibility = View.VISIBLE
                                dataBinding.plusBtn.visibility = View.VISIBLE
                            }
                        })
                }
            }
        })
    }

    private fun attachInputListeners() {
        dataBinding.addCategory.setOnClickListener {
            if (viewModel.size.value == 0) {
                Intent(activity?.baseContext, MenuNavigation::class.java).apply {
                    activity?.startActivity(this)
                }
            }
        }

        dataBinding.plusBtn.setOnClickListener {
            Intent(activity?.baseContext, CategoryNavigation::class.java).apply {
                activity?.startActivity(this)
            }
        }

        dataBinding.sortButton.setOnClickListener {
            if (viewModel.size.value != null) {
                Intent(activity?.baseContext, SortActivity::class.java).apply {
                    activity?.startActivity(this)
                }
            } else if (viewModel.size.value == 0) {
                dataBinding.textview3.visibility = View.VISIBLE
            }
        }

        dataBinding.searchButton.setOnClickListener {
            if (viewModel.size.value != null) {
                Intent(activity?.baseContext, SearchActivity::class.java).apply {
                    activity?.startActivity(this)
                }
            }
        }
    }

    private fun invisibleMenuNull() {
        dataBinding.textview2.visibility = View.GONE
        dataBinding.imageView18.visibility = View.GONE
        dataBinding.addCategory.visibility = View.GONE
        dataBinding.textview3.visibility = View.GONE
    }

    private fun invisibleMenu() {
        dataBinding.plusBtn.visibility = View.GONE
        dataBinding.tabs.visibility = View.GONE
        dataBinding.viewpager.visibility = View.GONE
        dataBinding.appbar.visibility = View.GONE
    }

    private fun setMenuInvisible() {
        dataBinding.tabs.visibility = View.GONE
        dataBinding.viewpager.visibility = View.GONE
        dataBinding.appbar.visibility = View.GONE
        dataBinding.textview2.visibility = View.GONE
        dataBinding.imageView18.visibility = View.GONE
        dataBinding.addCategory.visibility = View.GONE
        dataBinding.plusBtn.visibility = View.GONE
        dataBinding.textview3.visibility = View.GONE
        dataBinding.shimmerFrameLayout.visibility = View.VISIBLE
    }

    private fun initViews() {
        viewPager = view?.findViewById(R.id.viewpager)
        tabLayout = view?.findViewById(R.id.tabs)

        dataBinding.viewpager.offscreenPageLimit = 5
        dataBinding.viewpager.addOnPageChangeListener(
            TabLayout.TabLayoutOnPageChangeListener(
                dataBinding.tabs
            )
        )

        dataBinding.tabs.setOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                dataBinding.viewpager.currentItem = tab.position
                categoryName = tab.text.toString()
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        setDynamicFragmentToTabLayout()
    }

    private fun setDynamicFragmentToTabLayout() {
        if (tabCount != viewModel.size.value?.toInt()) {
            viewModel.categoryListResult.value?.forEach { resCategory ->
                dataBinding.tabs.addTab(
                    dataBinding.tabs.newTab().setText(resCategory.category_name)
                )
                tabCount = dataBinding.tabs.tabCount
            }
        }

        val mDynamicFragmentAdapter =
            DynamicFragmentAdapter(fragmentManager, dataBinding.tabs.tabCount, dataBinding.tabs)

        dataBinding.viewpager.adapter = mDynamicFragmentAdapter
        dataBinding.viewpager.currentItem = 0
    }
}