package com.tsab.pikapp.view.homev2.menu

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.tsab.pikapp.viewmodel.homev2.DynamicViewModel
import com.tsab.pikapp.viewmodel.homev2.MenuViewModel
import kotlinx.android.synthetic.main.menu_fragment.*

class MenuFragment : Fragment() {
    private val viewModel: MenuViewModel by activityViewModels()
    private val viewModelDynamic: DynamicViewModel by activityViewModels()
    lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var dataBinding: MenuFragmentBinding

    var categoryName: String = ""

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

        viewModel.isLoading.observe(viewLifecycleOwner, Observer { status ->
            viewModel.size.observe(viewLifecycleOwner, Observer { number ->
                initViews()
            })
        })

        dataBinding.editText.setOnClickListener {
            val intent = Intent(activity?.baseContext, SearchActivity::class.java)
            activity?.startActivity(intent)
        }

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
        viewModelDynamic.isLoading.observe(viewLifecycleOwner, Observer {
            if (!it) dataBinding.shimmerFrameLayoutMenu.visibility = View.INVISIBLE
        })

        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            if (!isLoading) {
                dataBinding.shimmerFrameLayoutCategory.visibility = View.INVISIBLE
                if (viewModel.size.value == 0) {
                    invisibleMenu()
                    dataBinding.textview2.visibility = View.VISIBLE
                    dataBinding.imageView18.visibility = View.VISIBLE
                    dataBinding.addCategory.visibility = View.VISIBLE
                    dataBinding.addCategory.setOnClickListener {
                        val intent = Intent(activity?.baseContext, MenuNavigation::class.java)
                        activity?.startActivity(intent)
                    }

                    dataBinding.buttonSort.setOnClickListener {
                        dataBinding.textview3.visibility = View.VISIBLE
                    }
                } else if (viewModel.size.value != null) {
                    viewModel.categoryListResult.observe(viewLifecycleOwner, Observer { ioo ->
                        ioo?.let {
                            invisibleMenuNull()
                            dataBinding.viewpager.visibility = View.VISIBLE
                            dataBinding.tabs.visibility = View.VISIBLE
                            dataBinding.appbar.visibility = View.VISIBLE
                            dataBinding.plusBtn.visibility = View.VISIBLE
                            dataBinding.plusBtn.setOnClickListener {
                                val intent =
                                    Intent(activity?.baseContext, CategoryNavigation::class.java)
                                activity?.startActivity(intent)
                            }
                            dataBinding.buttonSort.setOnClickListener {
                                val intent =
                                        Intent(activity?.baseContext, SortActivity::class.java)
                                activity?.startActivity(intent)
                            }
                        }
                    })
                }
            }
        })
    }

    private fun invisibleMenuNull() {
        dataBinding.textview2.visibility = View.GONE
        dataBinding.imageView18.visibility = View.GONE
        dataBinding.addCategory.visibility = View.GONE
        dataBinding.textview3.visibility = View.GONE
        dataBinding.recyclerviewCategory.visibility = View.GONE
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
        dataBinding.shimmerFrameLayoutCategory.visibility = View.VISIBLE
        dataBinding.shimmerFrameLayoutMenu.visibility = View.VISIBLE
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
                dataBinding.viewpager.currentItem = tab.position
                categoryName = tab.text.toString()
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
        setDynamicFragmentToTabLayout()
    }

    private fun setDynamicFragmentToTabLayout() {

        if (x != viewModel.size.value?.toInt()) {
            viewModel.categoryListResult.value?.forEach { resCategory ->
                tabs.addTab(tabs.newTab().setText(resCategory.category_name))
                x = tabs.tabCount
            }
        }

        val mDynamicFragmentAdapter =
            DynamicFragmentAdapter(fragmentManager, tabs.tabCount, tabs)

        viewpager.adapter = mDynamicFragmentAdapter

        viewpager.currentItem = 0
    }
}