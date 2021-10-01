package com.tsab.pikapp.view.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tsab.pikapp.R
import com.tsab.pikapp.models.model.CategoryListResult
import com.tsab.pikapp.view.CategoryAdapter
import com.tsab.pikapp.viewmodel.menu.MenuViewModel
import kotlinx.android.synthetic.main.fragment_category_name.*

class CategoryNameFragment : BottomSheetDialogFragment(), CategoryAdapter.OnItemClickListener {
    private val viewModel: MenuViewModel by activityViewModels()
    private var navController: NavController? = null

    lateinit var linearLayoutManager: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_category_name, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = parentFragment?.view?.let { Navigation.findNavController(it) }
        recyclerview_category.setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(requireView().context)
        recyclerview_category.layoutManager = linearLayoutManager

        val category: RecyclerView = requireView().findViewById(R.id.recyclerview_category)
        activity?.let { viewModel.getCategory(it.baseContext, category, this) }

        observeViewModel()
        attachInputListeners()
    }

    private fun observeViewModel() {

    }

    private fun attachInputListeners() {
        closeBtn.setOnClickListener {
            navController?.navigateUp()
        }

        okButton.setOnClickListener {
            navController?.navigateUp()
        }
    }

    override fun onItemClick(categList: CategoryListResult) {
        val categoryName = categList.category_name
        val categoryId = categList.id.toString()
        viewModel.validateCategory(categoryName.toString())
        viewModel.validateCategoryId(categoryId)
    }
}
