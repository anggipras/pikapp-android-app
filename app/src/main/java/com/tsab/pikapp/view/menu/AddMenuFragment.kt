package com.tsab.pikapp.view.menu

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.skydoves.balloon.showAlignTop
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentAddMenuBinding
import com.tsab.pikapp.util.SessionManager
import com.tsab.pikapp.util.setAllOnClickListener
import com.tsab.pikapp.view.homev2.HomeActivity
import com.tsab.pikapp.view.menu.advance.AdvanceMenuMainFragment
import com.tsab.pikapp.viewmodel.menu.MenuViewModel


class AddMenuFragment : Fragment() {
    private val viewModel: MenuViewModel by activityViewModels()
    private lateinit var navController: NavController
    private lateinit var dataBinding: FragmentAddMenuBinding
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
    ): View {
        dataBinding = FragmentAddMenuBinding.inflate(
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

        dataBinding.pilihanMenuButton.setOnClickListener {
            navController.navigate(R.id.action_updateMenuAddFragment_to_advanceMenuMainFragment,
                    bundleOf(
                            AdvanceMenuMainFragment.ARGUMENT_MENU_EDIT to false,
                            AdvanceMenuMainFragment.ARGUMENT_PRODUCT_ID to "none"
                    ))
        }

        dataBinding.kategori.isFocusable = false
        dataBinding.kategori.isFocusableInTouchMode = false
        dataBinding.kategori.setOnClickListener {
            navController.navigate(R.id.action_update_menu_add_adv_to_category_name)
        }

        dataBinding.btnNext.setOnClickListener {
            viewModel.validateMenu(viewModel.img.value)
            viewModel.validateNama(dataBinding.namaMenu.text.toString())
            viewModel.validateHarga(dataBinding.harga.text.toString())
            viewModel.validateDesc(dataBinding.descMenu.text.toString())

            if (viewModel.validatePage()) {
                viewModel.postMenu()
                Handler().postDelayed({
                    val intent = Intent(activity?.baseContext, HomeActivity::class.java)
                    activity?.startActivity(intent)
                }, 500)
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

        viewModel.hargaError.observe(viewLifecycleOwner, Observer { hargaError ->
            dataBinding.hargaErrorText.text = if (hargaError.isEmpty()) "" else hargaError
        })

        viewModel.descError.observe(viewLifecycleOwner, Observer { descError ->
            dataBinding.descErrorText.text = if (descError.isEmpty()) "" else descError
        })
    }
}