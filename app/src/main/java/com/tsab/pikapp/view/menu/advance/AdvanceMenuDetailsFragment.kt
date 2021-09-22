package com.tsab.pikapp.view.menu.advance

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentAdvanceMenuDetailsBinding
import com.tsab.pikapp.models.model.AdvanceAdditionalMenu
import com.tsab.pikapp.util.setAllOnClickListener
import com.tsab.pikapp.view.menu.advance.lists.AdvanceMenuAdditionalAdapter
import com.tsab.pikapp.viewmodel.menu.advance.AdvanceMenuViewModel

class AdvanceMenuDetailsFragment : Fragment() {
    companion object {
        const val ARGUMENT_IS_EDIT = "isEditing"
        const val ARGUMENT_NAMA_PILIHAN = "detailsNamaPilihan"
        const val ARGUMENT_AKTIF = "detailsAktif"
        const val ARGUMENT_WAJIB = "detailsWajib"
        const val ARGUMENT_PILIHAN_MAKSIMAL = "detailsPilihanMaksimal"
        const val ARGUMENT_ADDITIONAL_MENU = "detailsAdditionalMenu"
    }

    private val viewModel: AdvanceMenuViewModel by activityViewModels()
    private lateinit var navController: NavController
    private lateinit var dataBinding: FragmentAdvanceMenuDetailsBinding
    var numberOfChoice = mutableListOf<String>()
    var selectedChoice = "1"

    private lateinit var additionalMenuAdapter: AdvanceMenuAdditionalAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_advance_menu_details,
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
                navController.navigate(R.id.action_advanceMenuDetailsFragment_to_advanceMenuMainFragment,
                        bundleOf(AdvanceMenuMainFragment.ARGUMENT_ADVANCE_EDIT to false))
            }
        })
    }

    private fun spinnerView() {
        numberOfChoice.clear()
        for (i in 1..(viewModel.detailsAdditionalMenuList.value?.size ?: 1)) {
            numberOfChoice.plusAssign(i.toString())
        }
        var arrayChoiceAdapter: ArrayAdapter<String> = ArrayAdapter(requireActivity(), R.layout.style_spinner_menu, numberOfChoice)
        dataBinding.spinnerMenuChoice.adapter = arrayChoiceAdapter
        dataBinding.spinnerMenuChoice.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedChoice = numberOfChoice.get(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
    }

    private fun fetchArguments() {
        Log.d("ADVANCEMENU", viewModel.detailsAdditionalMenuList.value.toString())
        Log.d("BOOL", arguments?.getBoolean(ARGUMENT_IS_EDIT).toString())
        if (arguments?.getBoolean(ARGUMENT_IS_EDIT) == true) {
            viewModel.setDetailsNamaPilihan(arguments?.getString(ARGUMENT_NAMA_PILIHAN) ?: "")
            viewModel.setDetailsAktif(arguments?.getBoolean(ARGUMENT_AKTIF) ?: true)
            viewModel.setDetailsWajib(arguments?.getBoolean(ARGUMENT_WAJIB) ?: true)
            viewModel.setDetailsPilihanMaksimal(arguments?.getInt(ARGUMENT_PILIHAN_MAKSIMAL) ?: 1)
            selectedChoice = if (arguments?.getInt(ARGUMENT_PILIHAN_MAKSIMAL) != 1) arguments?.getInt(ARGUMENT_PILIHAN_MAKSIMAL).toString() else "1"
            dataBinding.spinnerMenuChoice.setSelection(selectedChoice.toInt() - 1)
            viewModel.setDetailsAdditionalMenuList(
                    arguments?.get(ARGUMENT_ADDITIONAL_MENU) as List<AdvanceAdditionalMenu>? ?: listOf()
            )
        }
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            dataBinding.loadingOverlay.loadingView.visibility =
                if (isLoading) View.VISIBLE else View.GONE
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
                dataBinding.jumlahPilihanMaksimalErrorText.visibility =
                    if (pilihanMaksimalError.isEmpty() || viewModel.detailsAdditionalMenuList.value!!.isEmpty()) View.GONE else View.VISIBLE
                dataBinding.jumlahPilihanMaksimalErrorText.text = pilihanMaksimalError
            })

        viewModel.detailsAdditionalMenuList.observe(
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

                Log.d("AdvanceMenuDetails", additionalMenuList.toString())
                additionalMenuAdapter.setAdditionalMenuList(additionalMenuList)
            })
    }

    private fun attachInputListeners() {
        dataBinding.headerLayout.backButton.setAllOnClickListener(View.OnClickListener {
            navController.navigate(R.id.action_advanceMenuDetailsFragment_to_advanceMenuMainFragment, bundleOf(AdvanceMenuMainFragment.ARGUMENT_ADVANCE_EDIT to false))
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
            navController.navigate(R.id.action_advanceMenuDetailsFragment_to_advanceMenuDetailsSortFragment)
        }

        dataBinding.tambahPilihanGroup.setAllOnClickListener(View.OnClickListener {
            hideKeyboard()

            viewModel.validateDetailsNamaPilihan(dataBinding.namaPilihanInputText.text.toString())
            selectedChoice?.let { it1 -> viewModel.validateDetailsPilihanMaksimal(it1) }

            if (!viewModel.validateDetailsScreen()) return@OnClickListener

            viewModel.resetAdditionalScreen()
            navController.navigate(R.id.action_advanceMenuDetailsFragment_to_advanceMenuAdditionalFragment)
        }, view)

        dataBinding.nextButton.setOnClickListener {
            hideKeyboard()

            viewModel.validateDetailsNamaPilihan(dataBinding.namaPilihanInputText.text.toString())
            selectedChoice?.let { it1 -> viewModel.validateDetailsPilihanMaksimal(it1) }

            if (!viewModel.validateDetailsScreen()) return@setOnClickListener
            navController.navigate(R.id.action_advanceMenuDetailsFragment_to_advanceMenuMainFragment,
                    bundleOf(AdvanceMenuMainFragment.ARGUMENT_ADVANCE_EDIT to false))
        }
    }

    private fun setupRecyclerView() {
        additionalMenuAdapter = AdvanceMenuAdditionalAdapter(
            viewModel.detailsAdditionalMenuList.value!!.toMutableList(),
            object : AdvanceMenuAdditionalAdapter.OnItemClickListener {
                override fun onItemClick(advanceAdditionalMenu: AdvanceAdditionalMenu) {
                    navController.navigate(
                        R.id.action_advanceMenuDetailsFragment_to_advanceMenuAdditionalFragment,
                        bundleOf(
                            AdvanceMenuAdditionalFragment.ARGUMENT_IS_EDIT to true,
                            AdvanceMenuAdditionalFragment.ARGUMENT_MENU_NAME to advanceAdditionalMenu.ext_menu_name,
                            AdvanceMenuAdditionalFragment.ARGUMENT_MENU_PRICE to advanceAdditionalMenu.ext_menu_price
                        )
                    )
                }
            }
        )
        dataBinding.daftarPilihanRecyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = additionalMenuAdapter
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