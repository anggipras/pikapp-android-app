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
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentDynamicBinding
import com.tsab.pikapp.models.model.SearchItem
import com.tsab.pikapp.util.SessionManager
import com.tsab.pikapp.view.AdvanceMenuActivity
import com.tsab.pikapp.viewmodel.homev2.DynamicViewModel
import java.io.Serializable

class DynamicFragment : Fragment() {
    companion object {
        const val argumentCategoryName = "CATEGORY_NAME"

        fun newInstance(): DynamicFragment {
            return DynamicFragment()
        }
    }

    private val viewModel: DynamicViewModel by activityViewModels()
    private lateinit var dataBinding: FragmentDynamicBinding
    private val sessionManager = SessionManager()
    var menuList: MutableList<SearchItem> = mutableListOf()
    lateinit var dynamicAdapter: DynamicListAdapter
    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var categoryName: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_dynamic, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        categoryName = arguments?.getString(argumentCategoryName).toString()
        viewModel.getMenuList()

        observeViewModel()
        setupRecyclerView()
        attachInputListeners()
    }

    private fun observeViewModel() {
        viewModel.menuList.observe(viewLifecycleOwner, Observer { menuList ->
            this.menuList.clear()
            this.menuList.addAll(menuList.filter {
                categoryName == "ALL" || it.merchant_category_name == categoryName
            })

            dynamicAdapter.updateMenuList(this.menuList)
            if (this.menuList.isEmpty()) {
                hideList()
            } else {
                showList()
            }
        })
    }

    private fun setupRecyclerView() {
        dynamicAdapter = DynamicListAdapter(
            activity?.baseContext!!,
            menuList.toMutableList(),
            object : DynamicListAdapter.OnItemClickListener {
                override fun onItemClick(position: Int, menuList: SearchItem) {
                    Intent(activity?.baseContext, AdvanceMenuActivity::class.java).apply {
                        putExtra(AdvanceMenuActivity.EXTRA_TYPE, AdvanceMenuActivity.TYPE_EDIT)
                        putExtra(AdvanceMenuActivity.MENU_LIST, menuList as Serializable)
                        sessionManager.setMenuDefInit(1)
                        startActivity(this)
                        activity?.overridePendingTransition(R.anim.slide_up, R.anim.no_animation)
                    }
                }
            })
        dataBinding.listMenuDetail.adapter = dynamicAdapter

        linearLayoutManager = LinearLayoutManager(requireView().context)
        dataBinding.listMenuDetail.layoutManager = linearLayoutManager
    }

    private fun attachInputListeners() {
        dataBinding.tambahMenuEmptyButton.setOnClickListener {
            Intent(activity?.baseContext, AdvanceMenuActivity::class.java).apply {
                putExtra(AdvanceMenuActivity.EXTRA_TYPE, AdvanceMenuActivity.TYPE_ADD)
                sessionManager.setMenuDefInit(1)
                startActivity(this)
                activity?.overridePendingTransition(R.anim.slide_up, R.anim.no_animation)
            }
        }

        dataBinding.tambahMenuButton.setOnClickListener {
            Intent(activity?.baseContext, AdvanceMenuActivity::class.java).apply {
                putExtra(AdvanceMenuActivity.EXTRA_TYPE, AdvanceMenuActivity.TYPE_ADD)
                sessionManager.setMenuDefInit(1)
                startActivity(this)
                activity?.overridePendingTransition(R.anim.slide_up, R.anim.no_animation)
            }
        }
    }

    private fun hideList() {
        dataBinding.menuEmptyImage.visibility = View.VISIBLE
        dataBinding.menuEmptyText.visibility = View.VISIBLE
        dataBinding.tambahMenuEmptyButton.visibility = View.VISIBLE

        dataBinding.listMenuDetail.visibility = View.GONE
        dataBinding.tambahMenuButton.visibility = View.GONE
    }

    private fun showList() {
        dataBinding.menuEmptyImage.visibility = View.GONE
        dataBinding.menuEmptyText.visibility = View.GONE
        dataBinding.tambahMenuEmptyButton.visibility = View.GONE

        dataBinding.listMenuDetail.visibility = View.VISIBLE
        dataBinding.tambahMenuButton.visibility = View.VISIBLE
    }
}