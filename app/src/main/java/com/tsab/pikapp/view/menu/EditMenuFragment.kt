package com.tsab.pikapp.view.menu

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.skydoves.balloon.showAlignTop
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentAddMenuBinding
import com.tsab.pikapp.databinding.FragmentEditMenuBinding
import com.tsab.pikapp.viewmodel.menu.EditMenuViewModel
import com.tsab.pikapp.viewmodel.menu.MenuViewModel

class EditMenuFragment : Fragment() {
    private val viewModel: EditMenuViewModel by activityViewModels()
    private lateinit var dataBinding: FragmentEditMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_menu,
                container, false)

        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataBinding.infoBtn.setOnClickListener {
            dataBinding.infoBtn.showAlignTop(viewModel.showTooltip(requireView().context))
        }
        observeViewModel()
        attachInputListeners()
    }

    private fun attachInputListeners() {
        dataBinding.menuImg.setOnClickListener {
            registerForActivityResult(ActivityResultContracts.GetContent(),
                    ActivityResultCallback { uri: Uri ->
                        viewModel.validateMenu(uri)
                    }
            ).launch("image/*")
        }
    }

    private fun observeViewModel() {
        viewModel.menu.observe(viewLifecycleOwner, Observer { menuUri ->
            dataBinding.menuImg.setImageURI(menuUri)
        })
    }
}