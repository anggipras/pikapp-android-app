package com.tsab.pikapp.view.menu

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.skydoves.balloon.showAlignTop
import com.squareup.picasso.Picasso
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentEditMenuBinding
import com.tsab.pikapp.services.OnlineService
import com.tsab.pikapp.util.SessionManager
import com.tsab.pikapp.view.homev2.HomeActivity
import com.tsab.pikapp.view.menu.advance.EditMenuAdvanceMainFragment
import com.tsab.pikapp.viewmodel.menu.MenuViewModel
import kotlinx.android.synthetic.main.alert_dialog.view.*
import kotlinx.android.synthetic.main.fragment_edit_menu.*

class EditMenuFragment : Fragment() {
    private val viewModelMenu: MenuViewModel by activityViewModels()
    private lateinit var navController: NavController
    private lateinit var dataBinding: FragmentEditMenuBinding
    private var sessionManager = SessionManager()

    private val pickerContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            viewModelMenu.validateImgEdit(uri)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModelMenu.firstOpen()
        sessionManager.setHomeNav(1)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = FragmentEditMenuBinding.inflate(
            inflater, container, false
        )
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        observeViewModel()
        attachInputListeners()
        onBackPressed()
    }

    private fun onBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                sessionManager.setMenuDefInit(0)
                activity?.finish()
                activity?.overridePendingTransition(R.anim.no_animation, R.anim.slide_down)
            }
        })
    }

    private fun attachInputListeners() {

        dataBinding.topAppBar.setNavigationOnClickListener {
            sessionManager.setMenuDefInit(0)
            activity?.finish()
            activity?.overridePendingTransition(R.anim.no_animation, R.anim.slide_down)
        }

        dataBinding.infoBtn.setOnClickListener {
            dataBinding.infoBtn.showAlignTop(viewModelMenu.showTooltip(requireView().context))
        }

        dataBinding.menuImg.setOnClickListener {
            pickerContent.launch("image/*")
        }

        dataBinding.pilihanMenuButton.setOnClickListener {
            navController.navigate(
                    R.id.action_updateMenuEditAdvFragment_to_advanceMenuMainFragment,
                    bundleOf(
                            EditMenuAdvanceMainFragment.ARGUMENT_MENU_EDIT to true,
                            EditMenuAdvanceMainFragment.ARGUMENT_PRODUCT_ID to (viewModelMenu.menuList.value?.product_id ?: ""),
                            EditMenuAdvanceMainFragment.ARGUMENT_ADVANCE_EDIT to true
                    )
            )
        }

        dataBinding.kategori.isFocusable = false
        dataBinding.kategori.isFocusableInTouchMode = false
        dataBinding.kategori.setOnClickListener {
            navController.navigate(R.id.action_update_menu_edit_adv_to_category_name)
        }

        dataBinding.aktifkanSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModelMenu.setMenuActive(isChecked)
        }

        dataBinding.deleteMenuText.setOnClickListener {
            openDialog()
        }

        dataBinding.btnNext.setOnClickListener {
            viewModelMenu.validateNama(dataBinding.namaMenu.text.toString())
            viewModelMenu.validateHarga(dataBinding.harga.text.toString())
            viewModelMenu.validateDesc(dataBinding.descMenu.text.toString())

            if (viewModelMenu.validatePage()) {
                updateConnectedFlags()
            }
        }
    }

    private fun updateConnectedFlags() {
        val onlineService = OnlineService()
        if (onlineService.isOnline(context)) {
            viewModelMenu.postMenu()
        } else {
            /* CHANGE UI */
            onlineService.showToast(requireContext())
        }
    }

    private fun observeViewModel() {
        viewModelMenu.isLoading.observe(viewLifecycleOwner, Observer { bool ->
            dataBinding.loadingOverlay.loadingView.isVisible = bool
        })

        viewModelMenu.isLoadingFinish.observe(viewLifecycleOwner, Observer { bool ->
            if (!bool) {
                Intent(activity?.baseContext, HomeActivity::class.java).apply {
                    startActivity(this)
                    activity?.finish()
                    activity?.overridePendingTransition(R.anim.no_animation, R.anim.slide_down)
                }
            }
        })

        viewModelMenu.img.observe(viewLifecycleOwner, Observer { menuUri ->
            dataBinding.menuImg.setImageURI(menuUri)
        })

        viewModelMenu.menuError.observe(viewLifecycleOwner, Observer { menuError ->
            dataBinding.menuErrorText.text = if (menuError.isEmpty()) "" else menuError
        })

        viewModelMenu.category.observe(viewLifecycleOwner, Observer { category ->
            dataBinding.kategori.setText(if (category.isEmpty()) "" else category)
        })

        viewModelMenu.namaError.observe(viewLifecycleOwner, Observer { namaError ->
            dataBinding.namaErrorText.text = if (namaError.isEmpty()) "" else namaError
        })

        viewModelMenu.hargaError.observe(viewLifecycleOwner, Observer { hargaError ->
            dataBinding.hargaErrorText.text = if (hargaError.isEmpty()) "" else hargaError
        })

        viewModelMenu.descError.observe(viewLifecycleOwner, Observer { descError ->
            dataBinding.descErrorText.text = if (descError.isEmpty()) "" else descError
        })

        viewModelMenu.categoryError.observe(viewLifecycleOwner, Observer { categError ->
            dataBinding.kategoriErrorText.text = if (categError.isEmpty()) "" else categError
        })

        viewModelMenu.isMenuActive.observe(viewLifecycleOwner, Observer { menuActive ->
            dataBinding.aktifkanSwitch.isChecked = menuActive
        })

        viewModelMenu.menuList.observe(viewLifecycleOwner, Observer { menu ->
            Picasso.get().load(menu.pict_01).into(menuImg)
            dataBinding.namaMenu.setText(menu.product_name)
            dataBinding.harga.setText(menu.price)
            dataBinding.kategori.setText(menu.merchant_category_name)
            dataBinding.descMenu.setText(menu.product_desc)
        })
    }

    private fun openDialog() {
        val mDialogView =
            LayoutInflater.from(requireActivity()).inflate(R.layout.alert_dialog, null)
        val mBuilder = AlertDialog.Builder(requireActivity())
            .setView(mDialogView)
        val mAlertDialog = mBuilder.show()
        mAlertDialog.window?.setBackgroundDrawable(
            AppCompatResources.getDrawable(
                requireActivity(),
                R.drawable.dialog_background
            )
        )
        mDialogView.dialog_back.setOnClickListener {
            mAlertDialog.dismiss()
        }
        mDialogView.dialog_close.setOnClickListener {
            mAlertDialog.dismiss()
        }
        mDialogView.dialog_ok.setOnClickListener {
            mAlertDialog.dismiss()
            viewModelMenu.deleteMenu()
        }
    }
}