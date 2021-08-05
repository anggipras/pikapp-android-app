package com.tsab.pikapp.view.menu

import android.app.Instrumentation
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.registerForActivityResult
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.skydoves.balloon.*
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentAddMenuBinding
import com.tsab.pikapp.databinding.FragmentLoginV2SecondBinding
import com.tsab.pikapp.view.onboarding.login.navController
import com.tsab.pikapp.view.store.StoreHomeFragmentDirections
import com.tsab.pikapp.viewmodel.menu.MenuViewModel
import com.tsab.pikapp.viewmodel.onboarding.login.LoginOnboardingViewModelV2
import kotlinx.android.synthetic.main.fragment_add_menu.*
import kotlinx.android.synthetic.main.fragment_login_v2_first.*
import kotlinx.android.synthetic.main.fragment_signup_v2_third.*
import kotlin.system.exitProcess

class AddMenuFragment : Fragment() {
    private val viewModel: MenuViewModel by activityViewModels()
    var navController: NavController? = null
    private lateinit var dataBinding: FragmentAddMenuBinding
    private val pickerContent =  registerForActivityResult(ActivityResultContracts.GetContent(),
            ActivityResultCallback { uri ->
                viewModel.validateImg(uri)
            }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.firstOpen()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_menu,
                container, false)

        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataBinding.infoBtn.setOnClickListener {
            dataBinding.infoBtn.showAlignTop(viewModel.showTooltip(requireView().context))
        }

        dataBinding.kategori.setText(viewModel.getCategoryName())

        navController = Navigation.findNavController(view)

        attachInputListeners()
        observeViewModel()
    }

    private fun attachInputListeners() {
        dataBinding.menuImg.setOnClickListener {
           pickerContent.launch("image/*")
        }

        dataBinding.kategori.isFocusable = false
        dataBinding.kategori.isFocusableInTouchMode = false
        dataBinding.kategori.setOnClickListener {
            navController?.navigate(R.id.action_addMenuFragment_to_categoryNameFragment)
        }

        dataBinding.btnNext.setOnClickListener {
            viewModel.validateMenu(viewModel.img.value)
            viewModel.validateNama(dataBinding.namaMenu.text.toString())
            viewModel.validateHarga(dataBinding.harga.text.toString())
            if(viewModel.validatePage()){
                viewModel.postMenu()
            }
        }

    }

    private fun observeViewModel() {
        viewModel.img.observe(viewLifecycleOwner, Observer { menuUri ->
            dataBinding.menuImg.setImageURI(menuUri)
        })

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
                object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        viewModel.backBtn()
                    }
                })

        viewModel.menuError.observe(viewLifecycleOwner, Observer { menuError ->
            dataBinding.menuErrorText.text = if (menuError.isEmpty()) "" else menuError
        })

        viewModel.category.observe(viewLifecycleOwner, Observer { category ->
            dataBinding.kategori.setText(if (category.isEmpty()) "" else category)
        })

        viewModel.namaError.observe(viewLifecycleOwner, Observer { namaError ->
            dataBinding.namaErrorText.text = if (namaError.isEmpty()) "" else namaError
        })

        viewModel.hargaError.observe(viewLifecycleOwner, Observer { hargaError ->
            dataBinding.hargaErrorText.text = if (hargaError.isEmpty()) "" else hargaError
        })

    }
}