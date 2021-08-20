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
import com.tsab.pikapp.view.menuCategory.CategoryAdapter
import com.tsab.pikapp.view.menuCategory.CategoryNavigation
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

    override fun onDestroy() {
        super.onDestroy()
        viewModel.restartFragment()
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            if (!isLoading) {
                Log.e("size observe", viewModel.size.value.toString())
                Log.e("category name observe", viewModel.categoryName.value.toString())
                if (viewModel.size.value == null) {
                    invisibleMenu()
                    Log.e("intent observe", "pindah gan")
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
                    Log.e("!= null", viewModel.size.value.toString())
                    viewModel.categoryListResult.observe(viewLifecycleOwner, Observer { ioo ->
                        ioo?.let {
                            invisibleMenuNull()
                            //initViews()
                            dataBinding.viewpager.visibility = View.VISIBLE
                            dataBinding.tabs.visibility = View.VISIBLE
                            dataBinding.appbar.visibility = View.VISIBLE
                            dataBinding.plusBtn.visibility = View.VISIBLE
                            dataBinding.plusBtn.setOnClickListener {
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