package com.tsab.pikapp.view.menu.advance

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentAdvanceMenuDetailsSortBinding
import com.tsab.pikapp.models.model.AdvanceAdditionalMenu
import com.tsab.pikapp.util.setAllOnClickListener
import com.tsab.pikapp.view.menu.advance.lists.AdvanceMenuDetailsSortAdapter
import com.tsab.pikapp.viewmodel.menu.advance.AdvanceMenuViewModel
import kotlinx.android.synthetic.main.fragment_advance_menu_details_sort.*
import java.util.*

class AdvanceMenuDetailsSortFragment : Fragment() {
    private val viewModel: AdvanceMenuViewModel by activityViewModels()
    private lateinit var navController: NavController
    private lateinit var dataBinding: FragmentAdvanceMenuDetailsSortBinding
    private lateinit var additionalMenuAdapter: AdvanceMenuDetailsSortAdapter
    lateinit var linearLayoutManager: LinearLayoutManager
    private var menuChoiceList: MutableList<AdvanceAdditionalMenu> = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        dataBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_advance_menu_details_sort, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        attachInputListeners()
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        additionalMenuAdapter = AdvanceMenuDetailsSortAdapter(viewModel.detailsAdditionalMenuList.value!!.toMutableList())
        additionalMenuAdapter.notifyDataSetChanged()
        recyclerview_menuChoice.adapter = additionalMenuAdapter
    }

    private fun attachInputListeners() {
        recyclerview_menuChoice.setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(requireContext())
        recyclerview_menuChoice.layoutManager = linearLayoutManager

        val itemTouchHelper = object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
                return makeMovementFlags(dragFlags, 0)
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                Collections.swap(
                    additionalMenuAdapter.menuChoiceList,
                    viewHolder.adapterPosition,
                    target.adapterPosition
                )

                additionalMenuAdapter.notifyItemMoved(
                    viewHolder.adapterPosition,
                    target.adapterPosition
                )

                menuChoiceList = additionalMenuAdapter.menuChoiceList.mapIndexed { _, advanceAdditionalMenu ->
                    AdvanceAdditionalMenu(
                        ext_menu_name = advanceAdditionalMenu.ext_menu_name,
                        ext_menu_price = advanceAdditionalMenu.ext_menu_price,
                        active = advanceAdditionalMenu.active
                    )
                }.toMutableList()

                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                TODO("Not yet implemented")
            }
        }

        val itemTouchHelperCallback = ItemTouchHelper(itemTouchHelper)
        itemTouchHelperCallback.attachToRecyclerView(recyclerview_menuChoice)

        dataBinding.headerLayout.backButton.setAllOnClickListener(View.OnClickListener {
            navController.navigate(R.id.action_advanceMenuDetailsSortFragment_to_advanceMenuDetailsFragment)
        }, view)

        dataBinding.nextButton.setOnClickListener {
            viewModel.sortAdditionalMenu(menuChoiceList)
            navController.navigate(R.id.action_advanceMenuDetailsSortFragment_to_advanceMenuDetailsFragment)
        }
    }
}