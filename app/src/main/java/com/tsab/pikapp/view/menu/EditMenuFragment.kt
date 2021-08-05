package com.tsab.pikapp.view.menu

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.skydoves.balloon.showAlignTop
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentEditMenuBinding
import com.tsab.pikapp.viewmodel.menu.EditMenuViewModel

class EditMenuFragment : Fragment() {
    private val viewModel: EditMenuViewModel by activityViewModels()
    var navController: NavController? = null
    private lateinit var dataBinding: FragmentEditMenuBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_edit_menu,
            container, false
        )
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        observeViewModel()
        attachInputListeners()
    }

    private fun attachInputListeners() {
        dataBinding.infoBtn.setOnClickListener {
            dataBinding.infoBtn.showAlignTop(viewModel.showTooltip(requireView().context))
        }

        dataBinding.menuImg.setOnClickListener {
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri ->
                viewModel.validateMenu(
                    uri
                )
            }.launch("image/*")
        }

        dataBinding.kategori.isFocusable = false
        dataBinding.kategori.isFocusableInTouchMode = false
        dataBinding.kategori.setOnClickListener {
            navController?.navigate(R.id.action_update_menu_add_to_category_name)
        }

        dataBinding.btnNext.setOnClickListener {
            // TODO: Add button "Simpan"
        }
    }

    private fun observeViewModel() {
        viewModel.menu.observe(viewLifecycleOwner, Observer { menuUri ->
            dataBinding.menuImg.setImageURI(menuUri)
        })
    }
}