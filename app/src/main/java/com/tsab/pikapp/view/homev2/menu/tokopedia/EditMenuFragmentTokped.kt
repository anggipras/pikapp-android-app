package com.tsab.pikapp.view.homev2.menu.tokopedia

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.skydoves.balloon.showAlignTop
import com.squareup.picasso.Picasso
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentAddMenu2Binding
import com.tsab.pikapp.databinding.FragmentEditMenuBinding
import com.tsab.pikapp.databinding.FragmentEditMenuTokpedBinding
import com.tsab.pikapp.util.SessionManager
import com.tsab.pikapp.util.setAllOnClickListener
import com.tsab.pikapp.view.homev2.HomeActivity
import com.tsab.pikapp.viewmodel.menu.MenuViewModel
import kotlinx.android.synthetic.main.alert_dialog.view.*
import kotlinx.android.synthetic.main.fragment_edit_menu.*

class EditMenuFragmentTokped : Fragment() {
    private val viewModel: MenuViewModel by activityViewModels()
    private lateinit var navController: NavController
    private lateinit var dataBinding: FragmentEditMenuTokpedBinding
    private var sessionManager = SessionManager()

    private val pickerContent =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                viewModel.validateImg(uri)
            }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.firstOpen()
        sessionManager.setHomeNav(1)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = FragmentEditMenuTokpedBinding.inflate(
                inflater, container, false
        )
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataBinding.menuImg.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT

        navController = Navigation.findNavController(view)


        attachInputListeners()
        observeViewModel()

    }

    private fun attachInputListeners() {
        dataBinding.header.backButton.setAllOnClickListener(View.OnClickListener {
            activity?.finish()
        }, view)

        dataBinding.infoBtn.setOnClickListener {
            dataBinding.infoBtn.showAlignTop(viewModel.showTooltip(requireView().context))
        }

        dataBinding.menuImg.setOnClickListener {
            pickerContent.launch("image/*")
        }

        dataBinding.kategori.isFocusable = false
        dataBinding.kategori.isFocusableInTouchMode = false
        /* dataBinding.kategori.setOnClickListener {
             navController.navigate(R.id.action_update_menu_add_adv_to_category_name)
         }*/

        dataBinding.etalase.isFocusable = false
        dataBinding.etalase.isFocusableInTouchMode = false
        dataBinding.etalase.setOnClickListener {
            navController.navigate(R.id.action_addMenuFragment_to_chooseEtalaseFragment)
        }

        dataBinding.kategori.isFocusable = false
        dataBinding.kategori.isFocusableInTouchMode = false
        dataBinding.kategori.setOnClickListener {
            navController.navigate(R.id.action_addMenuFragment_to_chooseCategoryFragment2)
        }

        dataBinding.aktifkanSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setMenuActive(isChecked)
        }

        dataBinding.deleteMenuText.setOnClickListener {
            openDialog()
        }

        dataBinding.btnNext.setOnClickListener {
            viewModel.validateMenu(viewModel.img.value)
            viewModel.validateNama(dataBinding.namaMenu.text.toString())
            viewModel.validateHarga(dataBinding.harga.text.toString())
            viewModel.validateDesc(dataBinding.descMenu.text.toString())

            if (viewModel.validatePageTokped()) {
                Toast.makeText(activity, "Menu Added", Toast.LENGTH_SHORT).show()
                activity?.finish()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner, Observer { bool ->
            dataBinding.loadingOverlay.loadingView.isVisible = bool
        })

        viewModel.isLoadingFinish.observe(viewLifecycleOwner, Observer { bool ->
            if (!bool) {
                Intent(activity?.baseContext, HomeActivity::class.java).apply {
                    startActivity(this)
                    activity?.finish()
                }
            }
        })

        viewModel.img.observe(viewLifecycleOwner, Observer { menuUri ->
            dataBinding.menuImg.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
            dataBinding.menuImg.setImageURI(menuUri)
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

        viewModel.etalase.observe(viewLifecycleOwner, Observer { etalase ->
            dataBinding.etalase.setText(if (etalase.isEmpty()) "" else etalase)
        })

        viewModel.hargaError.observe(viewLifecycleOwner, Observer { hargaError ->
            dataBinding.hargaErrorText.text = if (hargaError.isEmpty()) "" else hargaError
        })

        viewModel.descError.observe(viewLifecycleOwner, Observer { descError ->
            dataBinding.descErrorText.text = if (descError.isEmpty()) "" else descError
        })

        viewModel.isMenuActive.observe(viewLifecycleOwner, Observer { menuActive ->
            dataBinding.aktifkanSwitch.isChecked = menuActive
        })

        viewModel.menuList.observe(viewLifecycleOwner, Observer { menu ->
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
            //viewModelMenu.deleteMenu()
            Toast.makeText(activity, "clicked", Toast.LENGTH_SHORT).show()
        }
    }

}