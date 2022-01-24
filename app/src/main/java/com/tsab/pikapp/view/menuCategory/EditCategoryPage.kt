package com.tsab.pikapp.view.menuCategory

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentEditCategoryPageBinding
import com.tsab.pikapp.util.setAllOnClickListener
import com.tsab.pikapp.view.homev2.HomeActivity
import com.tsab.pikapp.viewmodel.categoryMenu.CategoryViewModel
import kotlinx.android.synthetic.main.delete_category_popup.view.*
import kotlinx.android.synthetic.main.edit_category_popup.view.*
import kotlinx.android.synthetic.main.fragment_add_category_page.*
import kotlinx.android.synthetic.main.fragment_add_category_page.categoryName
import kotlinx.android.synthetic.main.fragment_edit_category_page.*

class EditCategoryPage : Fragment() {

    private var navController: NavController? = null
    private lateinit var dataBinding: FragmentEditCategoryPageBinding
    private val viewModel: CategoryViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_edit_category_page,
            container, false
        )
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        dataBinding.categoryName.append(viewModel.namaCatagory.value.toString())

        observeViewModel()
        attachInputListeners()
    }

    private fun attachInputListeners() {
        dataBinding.saveBtn.setOnClickListener {
            viewModel.validateNama(categoryName.text.toString())
            if (viewModel.validateNama(categoryName.text.toString())) {
                activity?.let {
                    viewModel.updateCategory(
                            categoryName.text.toString(),
                            it.baseContext
                    )
                }
                Handler().postDelayed({
                    navController?.navigate(R.id.action_editCategoryPage_to_categoryPage)
                }, 500)
            }
        }

        dataBinding.topAppBar.setNavigationOnClickListener {
            navController?.navigate(R.id.action_editCategoryPage_to_categoryPage)
        }

        dataBinding.toggleButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.activation = isChecked
            } else {
                popupActivation()
                viewModel.activation = false
            }
        }

        dataBinding.deleteCategory.setOnClickListener {
            if (viewModel.categoryMenuSize.value != "0") {
                val mDialogView = LayoutInflater.from(requireActivity())
                    .inflate(R.layout.delete_category_warning_popup, null)
                val mBuilder = AlertDialog.Builder(requireActivity())
                    .setView(mDialogView)
                val mAlertDialog = mBuilder.show()
                mAlertDialog.window?.setBackgroundDrawable(
                    AppCompatResources.getDrawable(
                        requireActivity(),
                        R.drawable.dialog_background
                    )
                )
                mDialogView.buttonBack.setOnClickListener {
                    mAlertDialog.dismiss()
                }
                mDialogView.closeBtn.setOnClickListener {
                    mAlertDialog.dismiss()
                }
            } else {
                val mDialogView = LayoutInflater.from(requireActivity())
                    .inflate(R.layout.delete_category_popup, null)
                val mBuilder = AlertDialog.Builder(requireActivity())
                    .setView(mDialogView)
                val mAlertDialog = mBuilder.show()
                mAlertDialog.window?.setBackgroundDrawable(
                    AppCompatResources.getDrawable(
                        requireActivity(),
                        R.drawable.dialog_background
                    )
                )
                mDialogView.buttonBack.setOnClickListener {
                    mAlertDialog.dismiss()
                }
                mDialogView.closeBtn.setOnClickListener {
                    mAlertDialog.dismiss()
                }
                mDialogView.buttonContinue.setOnClickListener {
                    activity?.let {
                        viewModel.deleteCategoryPopup(
                            categoryName.text.toString(),
                            it.baseContext
                        )
                    }
                    mAlertDialog.dismiss()
                }
            }
        }
    }

    private fun observeViewModel() {
        viewModel.namaCategoryError.observe(viewLifecycleOwner, Observer { namaCategoryError ->
            dataBinding.namaerror.text = if (namaCategoryError.isEmpty()) "" else namaCategoryError
        })

        viewModel.isLoadingFinish.observe(viewLifecycleOwner, Observer { loadMove ->
            if (!loadMove) {
                navController?.navigate(R.id.action_editCategoryPage_to_categoryPage)
                viewModel.setLoadingFinish(true)
            }
        })
    }

    private fun popupActivation() {
        val mDialogView =
            LayoutInflater.from(requireActivity()).inflate(R.layout.edit_category_popup, null)
        val mBuilder = AlertDialog.Builder(requireActivity())
            .setView(mDialogView)
        val mAlertDialog = mBuilder.show()
        mAlertDialog.window?.setBackgroundDrawable(
            AppCompatResources.getDrawable(
                requireActivity(),
                R.drawable.dialog_background
            )
        )
        mDialogView.buttonBackActivation.setOnClickListener {
            viewModel.activation = true
            dataBinding.toggleButton.isChecked = true
            mAlertDialog.dismiss()
        }
        mDialogView.closeBtnActivation.setOnClickListener {
            viewModel.activation = true
            dataBinding.toggleButton.isChecked = true
            mAlertDialog.dismiss()
        }
        mDialogView.buttonContinueActivation.setOnClickListener {
            viewModel.activation = false
            mAlertDialog.dismiss()
        }
    }
}