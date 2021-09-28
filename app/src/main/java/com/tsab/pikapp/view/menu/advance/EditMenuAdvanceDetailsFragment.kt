package com.tsab.pikapp.view.menu.advance

import android.app.Activity
import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentEditMenuAdvanceDetailsBinding
import com.tsab.pikapp.models.model.AdvanceAdditionalMenuEdit
import com.tsab.pikapp.util.setAllOnClickListener
import com.tsab.pikapp.view.menu.advance.lists.AdvanceMenuEditAdditionalAdapter
import com.tsab.pikapp.viewmodel.menu.advance.AdvanceMenuViewModel

class EditMenuAdvanceDetailsFragment : Fragment() {
    companion object {
        const val ARGUMENT_IS_EDIT = "isEditing"
        const val ARGUMENT_NAMA_PILIHAN = "detailsNamaPilihan"
        const val ARGUMENT_AKTIF = "detailsAktif"
        const val ARGUMENT_WAJIB = "detailsWajib"
        const val ARGUMENT_PILIHAN_MAKSIMAL = "detailsPilihanMaksimal"
        const val ARGUMENT_ID_ADVANCE = "idAdvanceMenu"
        const val ARGUMENT_ADDITIONAL_MENU = "detailsAdditionalMenu"
    }

    private val viewModel: AdvanceMenuViewModel by activityViewModels()
    private lateinit var navController: NavController
    private lateinit var dataBinding: FragmentEditMenuAdvanceDetailsBinding
    var numberOfChoice = mutableListOf<String>()
    var selectedChoice = "1"

    private lateinit var additionalMenuEditAdapter: AdvanceMenuEditAdditionalAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        dataBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_edit_menu_advance_details,
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
        spinnerView()
        onBackPressed()
    }

    private fun onBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navController.navigate(R.id.action_editMenuAdvanceDetailsFragment_to_editMenuAdvanceMainFragment,
                        bundleOf(EditMenuAdvanceMainFragment.ARGUMENT_ADVANCE_EDIT to false))
            }
        })
    }

    private fun spinnerView() {
        numberOfChoice.clear()
        for (i in 1..(viewModel.detailsAdditionalMenuListEdit.value?.size ?: 1)) {
            numberOfChoice.plusAssign(i.toString())
        }
        var arrayChoiceAdapter: ArrayAdapter<String> = ArrayAdapter(requireActivity(), R.layout.style_spinner_menu, numberOfChoice)
        dataBinding.spinnerMenuChoice.adapter = arrayChoiceAdapter
        dataBinding.spinnerMenuChoice.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedChoice = numberOfChoice[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
    }

    private fun fetchArguments() {
        if (arguments?.getBoolean(AdvanceMenuDetailsFragment.ARGUMENT_IS_EDIT) == true) {
            viewModel.setDetailsNamaPilihan(arguments?.getString(AdvanceMenuDetailsFragment.ARGUMENT_NAMA_PILIHAN) ?: "")
            viewModel.setDetailsAktif(arguments?.getBoolean(AdvanceMenuDetailsFragment.ARGUMENT_AKTIF) ?: true)
            viewModel.setDetailsWajib(arguments?.getBoolean(AdvanceMenuDetailsFragment.ARGUMENT_WAJIB) ?: true)
            viewModel.setDetailsPilihanMaksimal(arguments?.getInt(AdvanceMenuDetailsFragment.ARGUMENT_PILIHAN_MAKSIMAL) ?: 1)
            selectedChoice = if (arguments?.getInt(AdvanceMenuDetailsFragment.ARGUMENT_PILIHAN_MAKSIMAL) != 1) arguments?.getInt(AdvanceMenuDetailsFragment.ARGUMENT_PILIHAN_MAKSIMAL).toString() else "1"
            dataBinding.spinnerMenuChoice.setSelection(selectedChoice.toInt() - 1)
            viewModel.setAdvanceId(arguments?.getLong(ARGUMENT_ID_ADVANCE) ?: 1)
            viewModel.setDetailsAdditionalMenuEditList(
                    arguments?.get(AdvanceMenuDetailsFragment.ARGUMENT_ADDITIONAL_MENU) as List<AdvanceAdditionalMenuEdit>? ?: listOf()
            )
        } else {
            viewModel.detailsAdditionalMenuListEdit.value?.size?.minus(1)?.let { viewModel.setDetailsPilihanMaksimal(it) }
        }
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            dataBinding.loadingOverlay.loadingView.visibility = if (isLoading) View.VISIBLE else View.GONE
        })

        viewModel.detailsNamaPilihan.observe(viewLifecycleOwner, Observer { namaPilihan ->
            dataBinding.namaPilihanInputText.setText(namaPilihan)
        })
        viewModel.detailsNamaPilihanError.observe(viewLifecycleOwner, Observer { namaPilihanError ->
            dataBinding.namaPilihanErrorText.visibility =
                    if (namaPilihanError.isEmpty()) View.GONE else View.VISIBLE
            dataBinding.namaPilihanErrorText.text = namaPilihanError
        })

        viewModel.isDetailsAktif.observe(viewLifecycleOwner, Observer { isAktif ->
            dataBinding.pilihanAktifSwitch.isChecked = isAktif
        })

        viewModel.isDetailsWajib.observe(viewLifecycleOwner, Observer { isWajib ->
            dataBinding.pilihanWajibSwitch.isChecked = isWajib
        })

        viewModel.detailsPilihanMaksimal.observe(viewLifecycleOwner, Observer { pilihanMaksimal ->
            dataBinding.spinnerMenuChoice.setSelection(pilihanMaksimal - 1)
        })
        viewModel.detailsPilihanMaksimalError.observe(
                viewLifecycleOwner,
                Observer { pilihanMaksimalError ->
                    dataBinding.jumlahPilihanMaksimalErrorText.visibility = if (pilihanMaksimalError.isEmpty() || viewModel.detailsAdditionalMenuListEdit.value!!.isEmpty()) View.GONE else View.VISIBLE
                    dataBinding.jumlahPilihanMaksimalErrorText.text = pilihanMaksimalError
                })

        viewModel.detailsAdditionalMenuListEdit.observe(
                viewLifecycleOwner,
                Observer { additionalMenuList ->
                    if (additionalMenuList.isNotEmpty()) {
                        dataBinding.jumlahPilihanMaksimalKosongText.visibility = View.GONE
                        dataBinding.spinnerMenuChoice.visibility = View.VISIBLE

                        dataBinding.daftarPilihanGroup.visibility = View.VISIBLE
                    } else {
                        dataBinding.jumlahPilihanMaksimalKosongText.visibility = View.VISIBLE
                        dataBinding.spinnerMenuChoice.visibility = View.GONE

                        dataBinding.daftarPilihanGroup.visibility = View.GONE
                    }

                    additionalMenuEditAdapter.setAdditionalMenuEditList(additionalMenuList)
                })

        viewModel.isLocalLoading.observe(viewLifecycleOwner, Observer { bool ->
            if (!bool) {
                navController.navigate(R.id.action_editMenuAdvanceDetailsFragment_to_editMenuAdvanceMainFragment,
                        bundleOf(
                                EditMenuAdvanceMainFragment.ARGUMENT_MENU_EDIT to true,
                                EditMenuAdvanceMainFragment.ARGUMENT_PRODUCT_ID to viewModel.productId.value
                        ))
                viewModel.setLocalLoading(true)
            }
        })
    }

    private fun attachInputListeners() {
        dataBinding.headerLayout.backButton.setAllOnClickListener(View.OnClickListener {
            navController.navigate(R.id.action_editMenuAdvanceDetailsFragment_to_editMenuAdvanceMainFragment, bundleOf(EditMenuAdvanceMainFragment.ARGUMENT_ADVANCE_EDIT to false))
        }, view)

        dataBinding.namaPilihanInputText.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || event == null || event.keyCode == KeyEvent.KEYCODE_ENTER) {
                return@setOnEditorActionListener !viewModel.validateDetailsNamaPilihan(
                        dataBinding.namaPilihanInputText.text.toString()
                )
            }
            false
        }

        dataBinding.pilihanAktifSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setDetailsAktif(isChecked)
        }
        dataBinding.pilihanWajibSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setDetailsWajib(isChecked)
        }

        dataBinding.daftarPilihanChangeOrderButton.setOnClickListener {
            navController.navigate(R.id.action_editMenuAdvanceDetailsFragment_to_editMenuDetailsSortFragment)
        }

        dataBinding.tambahPilihanGroup.setAllOnClickListener(View.OnClickListener {
            hideKeyboard()

            viewModel.validateDetailsNamaPilihan(dataBinding.namaPilihanInputText.text.toString())
            selectedChoice?.let { it1 -> viewModel.validateDetailsPilihanMaksimal(it1) }

            if (!viewModel.validateDetailsScreenEdit()) return@OnClickListener

            viewModel.resetAdditionalScreen()
            navController.navigate(R.id.action_editMenuAdvanceDetailsFragment_to_editMenuAdvanceAdditionalFragment)
            viewModel.setNewMenuChoice(true)
        }, view)

        dataBinding.nextButton.setOnClickListener {
            hideKeyboard()

            viewModel.validateDetailsNamaPilihan(dataBinding.namaPilihanInputText.text.toString())
            selectedChoice?.let { it1 -> viewModel.validateDetailsPilihanMaksimal(it1) }

            if (!viewModel.validateDetailsScreenForUpdate()) return@setOnClickListener
        }
    }

    private fun setupRecyclerView() {
        additionalMenuEditAdapter = AdvanceMenuEditAdditionalAdapter(
                viewModel.detailsAdditionalMenuListEdit.value!!.toMutableList(),
                object : AdvanceMenuEditAdditionalAdapter.OnItemClickListener {
                    override fun onItemClick(advanceAdditionalMenu: AdvanceAdditionalMenuEdit) {
                        viewModel.setNewMenuChoice(false)
                        navController.navigate(
                                R.id.action_editMenuAdvanceDetailsFragment_to_editMenuAdvanceAdditionalFragment,
                                bundleOf(
                                        EditMenuAdvanceAdditionalFragment.ARGUMENT_IS_EDIT to true,
                                        EditMenuAdvanceAdditionalFragment.ARGUMENT_MENU_NAME to advanceAdditionalMenu.ext_menu_name,
                                        EditMenuAdvanceAdditionalFragment.ARGUMENT_MENU_PRICE to advanceAdditionalMenu.ext_menu_price,
                                        EditMenuAdvanceAdditionalFragment.ARGUMENT_MENU_EXT_ID to advanceAdditionalMenu.ext_id
                                )
                        )
                    }
                }
        )
        dataBinding.daftarPilihanRecyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = additionalMenuEditAdapter
        }
    }

    private fun hideKeyboard() {
        val inputManager: InputMethodManager = activity?.getSystemService(
                Activity.INPUT_METHOD_SERVICE
        ) as InputMethodManager

        if (inputManager.isAcceptingText) {
            inputManager.hideSoftInputFromWindow(activity?.currentFocus?.windowToken, 0)
        }
    }
}