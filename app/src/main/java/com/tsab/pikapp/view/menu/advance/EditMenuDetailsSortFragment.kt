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
import com.tsab.pikapp.databinding.FragmentEditMenuDetailsSortBinding
import com.tsab.pikapp.models.model.AdvanceAdditionalMenu
import com.tsab.pikapp.models.model.AdvanceAdditionalMenuEdit
import com.tsab.pikapp.util.setAllOnClickListener
import com.tsab.pikapp.view.menu.advance.lists.AdvanceMenuDetailsSortAdapter
import com.tsab.pikapp.view.menu.advance.lists.AdvanceMenuEditDetailsSortAdapter
import com.tsab.pikapp.viewmodel.menu.advance.AdvanceMenuViewModel
import kotlinx.android.synthetic.main.fragment_advance_menu_details_sort.*
import java.util.*

class EditMenuDetailsSortFragment : Fragment() {
    private val viewModel: AdvanceMenuViewModel by activityViewModels()
    private lateinit var navController: NavController
    private lateinit var dataBinding: FragmentEditMenuDetailsSortBinding
    private lateinit var additionalMenuEditAdapter: AdvanceMenuEditDetailsSortAdapter
    lateinit var linearLayoutManager: LinearLayoutManager
    private var menuEditChoiceList: MutableList<AdvanceAdditionalMenuEdit> = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        dataBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_edit_menu_details_sort, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        attachInputListeners()
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        additionalMenuEditAdapter = AdvanceMenuEditDetailsSortAdapter(viewModel.detailsAdditionalMenuListEdit.value!!.toMutableList())
        additionalMenuEditAdapter.notifyDataSetChanged()
        recyclerview_menuChoice.adapter = additionalMenuEditAdapter
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
                    additionalMenuEditAdapter.menuEditChoiceList,
                    viewHolder.adapterPosition,
                    target.adapterPosition
                )

                additionalMenuEditAdapter.notifyItemMoved(
                    viewHolder.adapterPosition,
                    target.adapterPosition
                )

                menuEditChoiceList = additionalMenuEditAdapter.menuEditChoiceList.mapIndexed { _, advanceAdditionalMenu ->
                    AdvanceAdditionalMenuEdit(
                        ext_menu_name = advanceAdditionalMenu.ext_menu_name,
                        ext_menu_price = advanceAdditionalMenu.ext_menu_price,
                        active = advanceAdditionalMenu.active,
                        ext_id = advanceAdditionalMenu.ext_id
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
            navController.navigate(R.id.action_editMenuDetailsSortFragment_to_editMenuAdvanceDetailsFragment)
        }, view)

        dataBinding.nextButton.setOnClickListener {
            viewModel.sortAdditionalMenuEdit(menuEditChoiceList)
            navController.navigate(R.id.action_editMenuDetailsSortFragment_to_editMenuAdvanceDetailsFragment)
        }
    }
}