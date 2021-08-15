package com.tsab.pikapp.view.categoryMenu

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentEditCategoryPageBinding
import com.tsab.pikapp.viewmodel.categoryMenu.CategoryViewModel
import kotlinx.android.synthetic.main.fragment_add_category_page.*

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
        Log.e("size edit", viewModel.size)
        dataBinding.categoryName.append(viewModel.namaCatagory.value.toString())

        observeViewModel()
        attachInputListeners()
    }

    private fun attachInputListeners() {
        dataBinding.saveBtn.setOnClickListener {
            viewModel.validateNama(categoryName.text.toString())
            if (viewModel.validateNama(categoryName.text.toString())) {
                Log.e("yes", "bisaaa")
                activity?.let {
                    viewModel.updateCategory(
                        categoryName.text.toString(),
                        it.baseContext
                    )
                }
                requireActivity().onBackPressed()
            }
        }

        dataBinding.backBtn.setOnClickListener {
            navController?.navigate(R.id.action_editCategoryPage_to_categoryPage)
        }

        dataBinding.toggleButton.setOnClickListener {
            if (viewModel.activation == true) {
                dataBinding.toggleButton.setBackgroundResource(R.drawable.toggle_on)
                val inflater: LayoutInflater =
                    activity?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val view = inflater.inflate(R.layout.edit_category_popup, null)
                val popupWindow = PopupWindow(
                    view,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    popupWindow.elevation = 20.0F
                }

                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)

                val closeBtn = view.findViewById<ImageView>(R.id.closeBtn)
                val buttonContinue = view.findViewById<TextView>(R.id.buttonContinue)
                val buttonBack = view.findViewById<ImageView>(R.id.buttonBack)

                closeBtn.setOnClickListener {
                    viewModel.activation = true
                    popupWindow.dismiss()
                    dataBinding.toggleButton.setBackgroundResource(R.drawable.toggle_on)
                }

                buttonContinue.setOnClickListener {
                    Toast.makeText(requireView().context, "false", Toast.LENGTH_SHORT).show()
                    viewModel.activation = false
                    popupWindow.dismiss()
                    dataBinding.toggleButton.setBackgroundResource(R.drawable.toggle_off)
                }

                buttonBack.setOnClickListener {
                    viewModel.activation = true
                    popupWindow.dismiss()
                    dataBinding.toggleButton.setBackgroundResource(R.drawable.toggle_on)
                }
            } else if (!viewModel.activation == false) {
                dataBinding.toggleButton.setBackgroundResource(R.drawable.toggle_off)
            }
        }

        dataBinding.deleteCategory.setOnClickListener {
            val inflater: LayoutInflater =
                activity?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = inflater.inflate(R.layout.delete_category_popup, null)
            val popupWindow = PopupWindow(
                view, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                popupWindow.elevation = 20.0F
            }

            popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)

            val closeBtn = view.findViewById<ImageView>(R.id.closeBtn)
            val buttonContinue = view.findViewById<TextView>(R.id.buttonContinue)
            val buttonBack = view.findViewById<ImageView>(R.id.buttonBack)

            closeBtn.setOnClickListener {
                popupWindow.dismiss()
            }

            buttonContinue.setOnClickListener {
                Log.e("activation", viewModel.activation.toString())
                activity?.let {
                    viewModel.deleteCategoryPopup(
                        categoryName.text.toString(),
                        it.baseContext
                    )
                }
                navController?.navigate(R.id.action_editCategoryPage_to_categoryPage)
                popupWindow.dismiss()
            }

            buttonBack.setOnClickListener {
                popupWindow.dismiss()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.namaCategoryError.observe(viewLifecycleOwner, Observer { namaCategoryError ->
            dataBinding.namaerror.text = if (namaCategoryError.isEmpty()) "" else namaCategoryError
        })
    }
}