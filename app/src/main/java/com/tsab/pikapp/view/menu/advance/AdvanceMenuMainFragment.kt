package com.tsab.pikapp.view.menu.advance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentAdvanceMenuMainBinding
import com.tsab.pikapp.models.model.AdvanceMenu
import com.tsab.pikapp.util.setAllOnClickListener
import com.tsab.pikapp.view.menu.advance.lists.AdvanceMenuAdapter
import com.tsab.pikapp.viewmodel.menu.MenuViewModel
import com.tsab.pikapp.viewmodel.menu.advance.AdvanceMenuViewModel

class AdvanceMenuMainFragment : Fragment() {
    companion object {
        const val ARGUMENT_MENU_EDIT = "isEditing"
        const val ARGUMENT_PRODUCT_ID = "productID"
        const val ARGUMENT_ADVANCE_EDIT = "isEditAdvMenu"
    }

    private val viewModel: AdvanceMenuViewModel by activityViewModels()
    private val viewModelMenu: MenuViewModel by activityViewModels()
    private lateinit var navController: NavController
    private lateinit var dataBinding: FragmentAdvanceMenuMainBinding

    private lateinit var advanceMenuAdapter: AdvanceMenuAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_advance_menu_main,
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
                navController.navigate(R.id.action_advanceMenuMainFragment_to_updateMenuAddAdvFragment)
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
        if (arguments?.getBoolean(ARGUMENT_MENU_EDIT) == true) viewModel.fetchAdvanceMenuData()
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            dataBinding.loadingOverlay.loadingView.visibility =
                if (isLoading) View.VISIBLE else View.GONE
        })

        viewModel.isAdvanceMenuActive.observe(viewLifecycleOwner, Observer { isAdvanceMenuActive ->
            dataBinding.aktifkanSwitch.isChecked = isAdvanceMenuActive

            dataBinding.tambahPilihanGroup.visibility =
                if (isAdvanceMenuActive) View.VISIBLE else View.GONE

            if ((viewModel.advanceMenuList.value?.size ?: 0) > 0 && isAdvanceMenuActive) {
                dataBinding.daftarPilihanGroup.visibility = View.VISIBLE
            } else {
                dataBinding.daftarPilihanGroup.visibility = View.GONE
            }
        })

        viewModel.advanceMenuList.observe(viewLifecycleOwner, Observer { advanceMenuList ->
            if (advanceMenuList.isNotEmpty()) {
                dataBinding.daftarPilihanGroup.visibility = View.VISIBLE
            } else {
                dataBinding.daftarPilihanGroup.visibility = View.GONE
            }

            advanceMenuAdapter.setAdvanceMenuList(advanceMenuList)
        })
    }

    private fun attachInputListeners() {
        dataBinding.topAppBar.setNavigationOnClickListener {
            navController.navigate(R.id.action_advanceMenuMainFragment_to_updateMenuAddAdvFragment)
        }

        dataBinding.aktifkanSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setAdvanceMenuActive(isChecked)
        }

        dataBinding.tambahPilihanGroup.setAllOnClickListener(View.OnClickListener {
            viewModel.resetDetailsScreen()
            navController.navigate(R.id.action_advanceMenuMainFragment_to_advanceMenuDetailsFragment)
        }, view)

        dataBinding.saveButton.setOnClickListener {
            viewModelMenu.setAdvanceMenuList(viewModel.advanceMenuList.value!!)
            navController.navigate(R.id.action_advanceMenuMainFragment_to_updateMenuAddAdvFragment)
        }
    }

    private fun setupRecyclerView() {
        advanceMenuAdapter = AdvanceMenuAdapter(
            viewModel.advanceMenuList.value!!.toMutableList(),
            object : AdvanceMenuAdapter.OnItemClickListener {
                override fun onItemClick(advanceMenu: AdvanceMenu) {
                    navController.navigate(
                        R.id.action_advanceMenuMainFragment_to_advanceMenuDetailsFragment,
                        bundleOf(
                            AdvanceMenuDetailsFragment.ARGUMENT_IS_EDIT to true,
                            AdvanceMenuDetailsFragment.ARGUMENT_NAMA_PILIHAN to advanceMenu.template_name,
                            AdvanceMenuDetailsFragment.ARGUMENT_AKTIF to advanceMenu.active,
                            AdvanceMenuDetailsFragment.ARGUMENT_WAJIB to advanceMenu.mandatory,
                            AdvanceMenuDetailsFragment.ARGUMENT_PILIHAN_MAKSIMAL to advanceMenu.max_choose,
                            AdvanceMenuDetailsFragment.ARGUMENT_ADDITIONAL_MENU to advanceMenu.ext_menus
                        )
                    )
                }
            }
        )

        dataBinding.daftarPilihanRecyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = advanceMenuAdapter
        }
    }
}