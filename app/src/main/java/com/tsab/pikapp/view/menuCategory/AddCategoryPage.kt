package com.tsab.pikapp.view.menuCategory

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentAddCategoryPageBinding
import com.tsab.pikapp.viewmodel.categoryMenu.CategoryViewModel
import kotlinx.android.synthetic.main.fragment_add_category_page.*
import java.util.*

class AddCategoryPage : Fragment() {

    private var navController: NavController? = null
    private lateinit var dataBinding: FragmentAddCategoryPageBinding
    private val viewModel: CategoryViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_add_category_page,
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

    private fun observeViewModel() {
        viewModel.namaCategoryError.observe(viewLifecycleOwner, Observer { namaCategoryError ->
            dataBinding.namaerror.text = if (namaCategoryError.isEmpty()) "" else namaCategoryError
        })
    }

    private fun attachInputListeners() {
        dataBinding.saveBtn.setOnClickListener {
            viewModel.validateNama(categoryName.text.toString())
            if (viewModel.validateNama(categoryName.text.toString())) {
                activity?.let { viewModel.postCategory(categoryName.text.toString(), it.baseContext) }
                Handler().postDelayed({
                    requireActivity().onBackPressed()
                }, 500)
            }
        }

        dataBinding.backBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }

        dataBinding.toggleButton.setOnCheckedChangeListener { _, isChecked ->
            viewModel.activation = isChecked
        }
    }
}