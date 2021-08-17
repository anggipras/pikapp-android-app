package com.tsab.pikapp.view.menuCategory

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentSortCategoryPageBinding
import com.tsab.pikapp.viewmodel.categoryMenu.CategoryViewModel
import kotlinx.android.synthetic.main.fragment_sort_category_page.*
import java.util.*


class SortCategoryPage : Fragment(), SortCategoryAdapter.OnItemClickListener {

    private var navController: NavController? = null
    lateinit var sortCategoryAdapter: SortCategoryAdapter
    lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var dataBinding: FragmentSortCategoryPageBinding
    private val viewModel: CategoryViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_sort_category_page,
            container, false
        )
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        recyclerview_category.setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(requireView().context)
        recyclerview_category.layoutManager = linearLayoutManager

        val touchHelper = ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0) {
            override fun onMove(
                p0: RecyclerView,
                p1: RecyclerView.ViewHolder,
                p2: RecyclerView.ViewHolder
            ): Boolean {
                val from_post = p1.adapterPosition
                val to_post = p2.adapterPosition

                //Collections.swap(sortCategoryAdapter.menuCategoryList, sourcePosition, targetPosition)
                Collections.swap(sortCategoryAdapter.menuCategoryList, from_post, to_post)
                sortCategoryAdapter.notifyItemMoved(from_post, to_post)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                TODO("Not yet implemented")
            }

        })

        touchHelper.attachToRecyclerView(recyclerview_category)

        /*val swipegesture = object : CategoryViewModel.SwipeGesture(requireView().context, recyclerview_category){
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                val fromPosition = viewHolder.adapterPosition
                val toPosition = target.adapterPosition

                Collections.swap(sortCategoryAdapter.menuCategoryList, fromPosition, toPosition)
                sortCategoryAdapter.notifyItemMoved(fromPosition, toPosition)

                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                TODO("Not yet implemented")
            }

        }*/

        //val touchHelper = ItemTouchHelper(swipegesture)
        //touchHelper.attachToRecyclerView(newRecyclerView)

        activity?.let {
            viewModel.getMenuCategoryListSort(
                it.baseContext,
                recyclerview_category,
                this
            )
        }
        attachInputListeners()
    }

    override fun onItemClick(position: Int) {
        viewModel.sortCategoryAdapter.notifyItemChanged(position)

        val categoryName = viewModel.sortCategoryAdapter.menuCategoryList[position].category_name
        Log.e("category name", categoryName.toString())
        viewModel.getCategoryName(categoryName.toString())

        val categoryOrder =
            viewModel.sortCategoryAdapter.menuCategoryList[position].category_order.toString()
        Log.e("category order", categoryOrder)
        viewModel.getCategoryOrder(categoryOrder)

        val activationToggle =
            viewModel.sortCategoryAdapter.menuCategoryList[position].is_active.toString()
        Log.e("activation", activationToggle)
        viewModel.getCategoryActivation(activationToggle)

        val categoryId = viewModel.sortCategoryAdapter.menuCategoryList[position].id.toString()
        Log.e("category id", categoryId)
        viewModel.getCategoryId(categoryId)
    }

    private fun attachInputListeners() {
        dataBinding.backBtn.setOnClickListener {
            navController?.navigate(R.id.action_sortCategoryPage_to_categoryPage)
        }

        dataBinding.saveBtn.setOnClickListener {
            Log.e("yes", "bisa")
        }

    }

    fun sortCategory() {
        //ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0))
    }


}