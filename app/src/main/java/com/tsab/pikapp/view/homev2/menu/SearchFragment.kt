package com.tsab.pikapp.view.homev2.menu

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentAddMenuBinding
import com.tsab.pikapp.databinding.FragmentSearchBinding
import com.tsab.pikapp.view.homev2.HomeNavigation
import com.tsab.pikapp.viewmodel.homev2.SearchViewModel
import com.tsab.pikapp.viewmodel.menu.MenuViewModel
import kotlinx.android.synthetic.main.fragment_category_name.*
import kotlinx.android.synthetic.main.fragment_search.*
import java.util.*

class SearchFragment : Fragment() {

    private val viewModel: SearchViewModel by activityViewModels()
    private lateinit var dataBinding: FragmentSearchBinding

    lateinit var linearLayoutManager: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_search,
            container, false
        )
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        linearLayoutManager = LinearLayoutManager(requireView().context)
        dataBinding.listMenu.layoutManager = linearLayoutManager
        dataBinding.listMenu.setHasFixedSize(false)

        dataBinding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                activity?.let { viewModel.getSearchList(query.toString(), it.baseContext, dataBinding.listMenu, dataBinding.noFound,
                    dataBinding.noFoundText) }
                view.hideKeyboard()
                dataBinding.searchView.onActionViewCollapsed()
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
        attachInputListeners()
    }

    fun View.hideKeyboard() {
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun attachInputListeners(){
        dataBinding.back.setOnClickListener {
            val intent =
                Intent(activity?.baseContext, HomeNavigation::class.java)
            activity?.startActivity(intent)
        }

        dataBinding.searchView.setOnClickListener {
            dataBinding.searchView.onActionViewExpanded()
        }
    }
}