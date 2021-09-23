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
import com.tsab.pikapp.view.onboarding.login.navController
import com.tsab.pikapp.viewmodel.menu.MenuViewModel
import kotlinx.android.synthetic.main.fragment_choose_category.*
import kotlinx.android.synthetic.main.fragment_choose_category.listKategoti
import kotlinx.android.synthetic.main.fragment_choose_etalase.*

class ChooseEtalaseFragment : Fragment() {

    val etalaseList = ArrayList<DummyDataEtalase>()
    lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var navController: NavController
    private val viewModel: MenuViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_choose_etalase, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        etalaseList.add(DummyDataEtalase(
            "Makanan"
        ))
        etalaseList.add(DummyDataEtalase(
                "Jus"
        ))
        etalaseList.add(DummyDataEtalase(
                "Pasta"
        ))
        etalaseList.add(DummyDataEtalase(
                "Air Mineral"
        ))
        etalaseList.add(DummyDataEtalase(
                "Minuman"
        ))

        btnNext.setOnClickListener {
            viewModel.validateEtalase(pHolder.text.toString())
            navController?.navigateUp()
        }

        listKategoti.layoutManager =LinearLayoutManager(requireView().context)
        val etalaseAdapter = activity?.let { EtalaseAdapter(it.baseContext,etalaseList, btnNext, pHolder) }
        listKategoti.adapter = etalaseAdapter
    }
}