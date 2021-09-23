package com.tsab.pikapp.view.homev2.menu.tokopedia

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.tsab.pikapp.R
import com.tsab.pikapp.viewmodel.menu.MenuViewModel
import kotlinx.android.synthetic.main.fragment_choose_category.*
import kotlinx.android.synthetic.main.fragment_choose_category.listKategoti
import kotlinx.android.synthetic.main.fragment_choose_etalase.*
import kotlinx.android.synthetic.main.fragment_txn_report.*

class ChooseCategoryFragment : Fragment() {

    val categoryList = ArrayList<DummyData>()
    lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var navController: NavController
    private val viewModel: MenuViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_choose_category, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        categoryList.add(
            DummyData(
                "Makanan",
                listOf("Nasi", "Ayam", "Sapi", "Sop")
            ))
        categoryList.add(
            DummyData(
                "Minuman",
                listOf("Teh", "Kopi", "Susu", "Soft Drink")
            ))

        btnNextCategory.setOnClickListener {
            viewModel.validateCategory(pHolderCategory.text.toString())
            navController?.navigateUp()
        }

        listKategoti.layoutManager =LinearLayoutManager(requireView().context)
        val categoryAdapter = activity?.let { CategoryAdapter(it.baseContext,categoryList, btnNextCategory, pHolderCategory) }
        listKategoti.adapter = categoryAdapter
    }

}