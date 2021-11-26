package com.tsab.pikapp.view.menuCategory

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentAddCategoryPageBinding
import com.tsab.pikapp.util.SessionManager
import com.tsab.pikapp.util.setAllOnClickListener
import com.tsab.pikapp.view.homev2.HomeActivity
import com.tsab.pikapp.viewmodel.categoryMenu.CategoryViewModel
import kotlinx.android.synthetic.main.fragment_add_category_page.*

class AddCategoryPage : Fragment() {

    private var navController: NavController? = null
    private lateinit var dataBinding: FragmentAddCategoryPageBinding
    private val viewModel: CategoryViewModel by activityViewModels()
    private val sessionManager = SessionManager()

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

        sessionManager.setHomeNav(1)

        observeViewModel()
        attachInputListeners()
    }

    private fun observeViewModel() {
        viewModel.namaCategoryError.observe(viewLifecycleOwner, Observer { namaCategoryError ->
            dataBinding.namaerror.text = if (namaCategoryError.isEmpty()) "" else namaCategoryError
        })

        viewModel.isLoadingIcon.observe(viewLifecycleOwner, Observer { load ->
            if (load) {
                dataBinding.loadingViewAddCategory.visibility = View.VISIBLE
            } else {
                Intent(activity?.baseContext, HomeActivity::class.java).apply {
                    startActivity(this)
                }
                dataBinding.loadingViewAddCategory.visibility = View.GONE
            }
        })
    }

    private fun attachInputListeners() {
        dataBinding.saveBtn.setOnClickListener {
            viewModel.validateNama(categoryName.text.toString())
            if (viewModel.validateNama(categoryName.text.toString())) {
                activity?.let {
                    viewModel.postCategory(
                        categoryName.text.toString(),
                        it.baseContext
                    )
                }
            }
        }

        dataBinding.headerLayout.backButton.setAllOnClickListener(View.OnClickListener {
            requireActivity().onBackPressed()
        }, view)

        dataBinding.toggleButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.activation = isChecked
            } else {
                viewModel.activation = false
            }
        }
    }
}