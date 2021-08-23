package com.tsab.pikapp.view.homev2.menu

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.MenuFragmentBinding
import com.tsab.pikapp.view.categoryMenu.CategoryAdapter
import com.tsab.pikapp.view.categoryMenu.CategoryNavigation
import com.tsab.pikapp.viewmodel.homev2.MenuViewModel
import kotlinx.android.synthetic.main.menu_fragment.*

class MenuFragment : Fragment() {
    private val viewModel: MenuViewModel by activityViewModels()

    lateinit var categoryAdapter: CategoryAdapter
    lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var dataBinding: MenuFragmentBinding

    private var viewPager: ViewPager? = null
    private var mTabLayout: TabLayout? = null
    private var x: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.menu_fragment, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerview_category.setHasFixedSize(true)
        linearLayoutManager =
            LinearLayoutManager(requireView().context, LinearLayoutManager.HORIZONTAL, false)
        recyclerview_category.layoutManager = linearLayoutManager

        setMenuInvisible()

        activity?.let { viewModel.getMenuCategoryList(it.baseContext, recyclerview_category) }
        Log.e("size menu fragment", viewModel.size.value.toString())

        viewModel.isLoading.observe(viewLifecycleOwner, Observer { status ->
            viewModel.size.observe(viewLifecycleOwner, Observer { number ->
                Log.e("observe oncreate", viewModel.size.value.toString())
                initViews()
            })
        })

        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        recyclerview_category.setHasFixedSize(true)
        linearLayoutManager =
                LinearLayoutManager(requireView().context, LinearLayoutManager.HORIZONTAL, false)
        recyclerview_category.layoutManager = linearLayoutManager

        setMenuInvisible()

        activity?.let { viewModel.getMenuCategoryList(it.baseContext, recyclerview_category) }

        observeViewModel()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.restartFragment()
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            if (!isLoading) {
                Log.e("size observe", viewModel.size.value.toString())
                Log.e("category name observe", viewModel.categoryName.value.toString())
                if (viewModel.size.value == 0) {
                    invisibleMenu()
                    Log.e("intent observe", "pindah gan")
                    dataBinding.textview2.isVisible = true
                    dataBinding.imageView18.isVisible = true
                    dataBinding.addCategory.isVisible = true

                    dataBinding.addCategory.setOnClickListener {
                        val intent = Intent(activity?.baseContext, MenuNavigation::class.java)
                        activity?.startActivity(intent)
                    }

                    dataBinding.buttonSort.setOnClickListener {
                        dataBinding.textview3.isVisible = true
                    }
                } else if (viewModel.size.value != null) {
                    Log.e("!= null", viewModel.size.value.toString())
                    viewModel.categoryListResult.observe(viewLifecycleOwner, Observer { ioo ->
                        ioo?.let {
                            invisibleMenuNull()
                            //initViews()
                            dataBinding.viewpager.isVisible = true
                            dataBinding.tabs.isVisible = true
                            dataBinding.appbar.isVisible = true
                            dataBinding.plusBtn.isVisible = true
                            dataBinding.plusBtn.setOnClickListener {
                                val intent =
                                    Intent(activity?.baseContext, CategoryNavigation::class.java)
                                activity?.startActivity(intent)
                            }
                            dataBinding.buttonSort.setOnClickListener {
                                val intent =
                                        Intent(activity?.baseContext, CategoryNavigation::class.java)
                                activity?.startActivity(intent)
                            }
                        }
                    })
                }
            }
        })
    }

    private fun invisibleMenuNull() {
        dataBinding.textview2.isVisible = false
        dataBinding.imageView18.isVisible = false
        dataBinding.addCategory.isVisible = false
        dataBinding.textview3.isVisible = false
        dataBinding.recyclerviewCategory.isVisible = false
    }

    private fun invisibleMenu() {
        dataBinding.plusBtn.isVisible = false
        dataBinding.tabs.isVisible = false
        dataBinding.viewpager.isVisible = false
        dataBinding.appbar.isVisible = false
    }

    private fun setMenuInvisible() {
        dataBinding.tabs.isVisible = false
        dataBinding.viewpager.isVisible = false
        dataBinding.appbar.isVisible = false
        dataBinding.textview2.isVisible = false
        dataBinding.imageView18.isVisible = false
        dataBinding.addCategory.isVisible = false
        dataBinding.plusBtn.isVisible = false
        dataBinding.textview3.isVisible = false
    }

    private fun initViews() {
        viewPager = view?.findViewById(R.id.viewpager)
        mTabLayout = view?.findViewById(R.id.tabs)

        dataBinding.viewpager.offscreenPageLimit = 5
        dataBinding.viewpager.addOnPageChangeListener(
            TabLayout.TabLayoutOnPageChangeListener(
                dataBinding.tabs
            )
        )
        dataBinding.tabs.setOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                // setCurrentItem as the tab position
                dataBinding.viewpager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
        setDynamicFragmentToTabLayout()
    }

    private fun setDynamicFragmentToTabLayout() {
        // here we have given 10 as the tab number
        // you can give any number here

        Log.e("size tablayout", viewModel.size.value.toString())

        if (x != viewModel.size.value?.toInt()) {
            viewModel.categoryListResult.value?.forEach { resCategory ->
                tabs.addTab(tabs.newTab().setText(resCategory.category_name))
                x = tabs.tabCount
                Log.e("tabs count", x.toString())
            }
        }

        val mDynamicFragmentAdapter =
            DynamicFragmentAdapter(fragmentManager, tabs.tabCount)

        // set the adapter
        viewpager.adapter = mDynamicFragmentAdapter

        // set the current item as 0 (when app opens for first time)
        viewpager.currentItem = 0
    }
}