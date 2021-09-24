package com.tsab.pikapp.view.menu.advance

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentEditMenuAdvanceMainBinding
import com.tsab.pikapp.models.model.AdvanceMenuEdit
import com.tsab.pikapp.util.setAllOnClickListener
import com.tsab.pikapp.view.menu.advance.lists.AdvanceMenuEditAdapter
import com.tsab.pikapp.viewmodel.menu.MenuViewModel
import com.tsab.pikapp.viewmodel.menu.advance.AdvanceMenuViewModel

class EditMenuAdvanceMainFragment : Fragment() {
    companion object {
        const val ARGUMENT_MENU_EDIT = "isEditing"
        const val ARGUMENT_PRODUCT_ID = "productID"
        const val ARGUMENT_ADVANCE_EDIT = "isEditAdvMenu"
    }

    private val viewModel: AdvanceMenuViewModel by activityViewModels()
    private val viewModelMenu: MenuViewModel by activityViewModels()
    private lateinit var navController: NavController
    private lateinit var dataBinding: FragmentEditMenuAdvanceMainBinding

    private lateinit var advanceMenuEditAdapter: AdvanceMenuEditAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        dataBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_edit_menu_advance_main,
                container, false
        )
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        fetchArguments()
        observeViewModel()
        attachInputListeners()
        setupRecyclerView()
        onBackPressed()
    }

    private fun onBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navController.navigate(R.id.action_editMenuAdvanceMainFragment_to_updateMenuEditAdvFragment)
            }
        })
    }

    private fun fetchArguments() {
        if (arguments?.getBoolean(ARGUMENT_MENU_EDIT) == true) {
            viewModel.setAddOrEdit(true)
            viewModel.setProductId(arguments?.getString(ARGUMENT_PRODUCT_ID))
        } else {
            if (viewModel.addOrEdit.value == true) viewModel.setAddOrEdit(true) else viewModel.setAddOrEdit(false)
            viewModel.setProductId(null)
            viewModel.setLoading(false)
        }
        if (arguments?.getBoolean(ARGUMENT_MENU_EDIT) == true) viewModel.fetchAdvanceMenuEditData()
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            dataBinding.loadingOverlay.loadingView.visibility = if (isLoading) View.VISIBLE else View.GONE
        })

        viewModel.isAdvanceMenuActive.observe(viewLifecycleOwner, Observer { isAdvanceMenuActive ->
            dataBinding.aktifkanSwitch.isChecked = isAdvanceMenuActive

            dataBinding.tambahPilihanGroup.visibility =
                    if (isAdvanceMenuActive) View.VISIBLE else View.GONE

            if ((viewModel.advanceMenuEditList.value?.size ?: 0) > 0 && isAdvanceMenuActive) {
                dataBinding.daftarPilihanGroup.visibility = View.VISIBLE
            } else {
                dataBinding.daftarPilihanGroup.visibility = View.GONE
            }
        })

        viewModel.advanceMenuEditList.observe(viewLifecycleOwner, Observer { advanceMenuList ->
            if (advanceMenuList.isNotEmpty()) {
                dataBinding.daftarPilihanGroup.visibility = View.VISIBLE
            } else {
                dataBinding.daftarPilihanGroup.visibility = View.GONE
            }

            advanceMenuEditAdapter.setAdvanceMenuEditList(advanceMenuList)
        })
    }

    private fun attachInputListeners() {
        dataBinding.headerLayout.backButton.setAllOnClickListener(View.OnClickListener {
            navController.navigate(R.id.action_editMenuAdvanceMainFragment_to_updateMenuEditAdvFragment)
        }, view)

        dataBinding.aktifkanSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setAdvanceMenuActive(isChecked)
        }

        dataBinding.tambahPilihanGroup.setAllOnClickListener(View.OnClickListener {
            viewModel.resetDetailsScreen()
            navController.navigate(R.id.action_editMenuAdvanceMainFragment_to_editMenuAdvanceDetailsFragment)
        }, view)

        dataBinding.saveButton.setOnClickListener {
            viewModelMenu.setAdvanceMenuEditList(viewModel.advanceMenuEditList.value!!)
            navController.navigate(R.id.action_editMenuAdvanceMainFragment_to_updateMenuEditAdvFragment)
        }
    }

    private fun setupRecyclerView() {
        advanceMenuEditAdapter = AdvanceMenuEditAdapter(
                viewModel.advanceMenuEditList.value!!.toMutableList(),
                object : AdvanceMenuEditAdapter.OnItemClickListener {
                    override fun onItemClick(advanceMenu: AdvanceMenuEdit) {
                        navController.navigate(
                                R.id.action_editMenuAdvanceMainFragment_to_editMenuAdvanceDetailsFragment,
                                bundleOf(
                                        EditMenuAdvanceDetailsFragment.ARGUMENT_IS_EDIT to true,
                                        EditMenuAdvanceDetailsFragment.ARGUMENT_NAMA_PILIHAN to advanceMenu.template_name,
                                        EditMenuAdvanceDetailsFragment.ARGUMENT_AKTIF to advanceMenu.active,
                                        EditMenuAdvanceDetailsFragment.ARGUMENT_WAJIB to advanceMenu.mandatory,
                                        EditMenuAdvanceDetailsFragment.ARGUMENT_PILIHAN_MAKSIMAL to advanceMenu.max_choose,
                                        EditMenuAdvanceDetailsFragment.ARGUMENT_ID_ADVANCE to advanceMenu.id,
                                        EditMenuAdvanceDetailsFragment.ARGUMENT_ADDITIONAL_MENU to advanceMenu.ext_menus
                                )
                        )
                    }
                }
        )

        dataBinding.daftarPilihanRecyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = advanceMenuEditAdapter
        }
    }
}