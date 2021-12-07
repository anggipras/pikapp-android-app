package com.tsab.pikapp.view.homev2.menu

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.MenuFragmentBinding
import com.tsab.pikapp.util.SessionManager
import com.tsab.pikapp.view.LoginV2Activity
import com.tsab.pikapp.view.homev2.HomeActivity
import com.tsab.pikapp.view.homev2.SearchActivity
import com.tsab.pikapp.view.homev2.transaction.manualTxn.ManualTxnActivity
import com.tsab.pikapp.view.menuCategory.CategoryNavigation
import com.tsab.pikapp.view.menuCategory.SortActivity
import com.tsab.pikapp.viewmodel.homev2.DynamicViewModel
import com.tsab.pikapp.viewmodel.homev2.MenuViewModel
import kotlinx.android.synthetic.main.menu_fragment.*
import kotlinx.android.synthetic.main.menu_fragment.topAppBar
import kotlinx.android.synthetic.main.transaction_fragment.*
import java.io.File

class MenuFragment : Fragment() {
    private val viewModel: MenuViewModel by activityViewModels()
    private lateinit var dataBinding: MenuFragmentBinding

    private var categoryList: List<String> = listOf()
    lateinit var linearLayoutManager: LinearLayoutManager
    private val sessionManager = SessionManager()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.menu_fragment, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.let { viewModel.getMenuCategoryList(it.baseContext) }
        setMenuInvisible()
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()

        attachInputListeners()
//        dataBinding.tabs.getTabAt(sessionManager.getMenuPageTab()!!)?.select()
//        dataBinding.viewpager.currentItem = sessionManager.getMenuPageTab()!!
//        sessionManager.setMenuDefInit(0)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.restartFragment()
        deleteCache(requireContext())
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            dataBinding.shimmerFrameLayoutCategory.visibility =
                if (isLoading) View.VISIBLE else View.GONE
            if (!isLoading) {
                initViews()
                dataBinding.shimmerFrameLayoutMenu.visibility = View.GONE
            }
        })

        viewModel.categoryListResult.observe(viewLifecycleOwner, Observer { categoryList ->
            this.categoryList = categoryList.map { it.category_name ?: "" }

            dataBinding.tabs.removeAllTabs()
            this.categoryList.forEach { categoryName ->
                dataBinding.tabs.newTab().apply {
                    text = categoryName
                    dataBinding.tabs.addTab(this)
                }
            }
        })

        viewModel.size.observe(viewLifecycleOwner, Observer { size ->
            if (size == 0 && viewModel.isLoading.value == false) {
                invisibleMenu()
                dataBinding.textview2.visibility = View.VISIBLE
                dataBinding.imageView18.visibility = View.VISIBLE
                dataBinding.addCategory.visibility = View.VISIBLE
            } else {
                invisibleMenuNull()
                dataBinding.viewpager.visibility = View.VISIBLE
            }

            if (size != 0) {
                dataBinding.tabs.visibility = View.VISIBLE
                dataBinding.appbar.visibility = View.VISIBLE
                dataBinding.plusBtn.visibility = View.VISIBLE
            }
        })

        viewModel.errCode.observe(viewLifecycleOwner, Observer { errCode ->
            if (errCode == "EC0032" || errCode == "EC0021" || errCode == "EC0017") {
                sessionManager.logout()
                Intent(activity?.baseContext, LoginV2Activity::class.java).apply {
                    activity?.startActivity(this)
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

        topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navDraw -> {
                    (activity as HomeActivity).openCloseDrawer(requireView())
                    true
                }
                else -> false
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
        dataBinding.shimmerFrameLayoutMenu.visibility = View.VISIBLE
    }

    private fun initViews() {
        dataBinding.viewpager.offscreenPageLimit = 5
        dataBinding.viewpager.addOnPageChangeListener(
            TabLayout.TabLayoutOnPageChangeListener(
                dataBinding.tabs
            )
        )

        dataBinding.tabs.setOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                dataBinding.viewpager.currentItem = tab.position

//                if (sessionManager.getMenuDefInit() == 0) {
//                    dataBinding.viewpager.currentItem = tab.position
//                    sessionManager.setMenuPageTab(tab.position)
//                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        setDynamicFragmentToTabLayout()
    }

    private fun setDynamicFragmentToTabLayout() {
        dataBinding.tabs.removeAllTabs()
        categoryList.forEach { categoryName ->
            dataBinding.tabs.newTab().apply {
                text = categoryName
                dataBinding.tabs.addTab(this)
            }
        }

        val mDynamicFragmentAdapter =
            DynamicFragmentAdapter(childFragmentManager, categoryList.size, categoryList)
        dataBinding.viewpager.adapter = mDynamicFragmentAdapter
        dataBinding.viewpager.currentItem = 0
    }

    fun deleteCache(context: Context) {
        try {
            val dir: File = context.cacheDir
            deleteDir(dir)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun deleteDir(dir: File?): Boolean {
        return if (dir != null && dir.isDirectory) {
            val children: Array<String> = dir.list()
            for (i in children.indices) {
                val success = deleteDir(File(dir, children[i]))
                if (!success) {
                    return false
                }
            }
            dir.delete()
        } else if (dir != null && dir.isFile) {
            dir.delete()
        } else {
            false
        }
    }
}