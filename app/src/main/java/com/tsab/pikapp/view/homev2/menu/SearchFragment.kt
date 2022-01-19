package com.tsab.pikapp.view.homev2.menu

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentSearchBinding
import com.tsab.pikapp.util.SessionManager
import com.tsab.pikapp.view.LoginV2Activity
import com.tsab.pikapp.view.homev2.HomeActivity
import com.tsab.pikapp.viewmodel.homev2.SearchViewModel

class SearchFragment : Fragment() {

    private val viewModel: SearchViewModel by activityViewModels()
    private lateinit var dataBinding: FragmentSearchBinding
    private val sessionManager = SessionManager()
    lateinit var linearLayoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager.setHomeNav(1)
    }

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

        dataBinding.searchField.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                observeViewModel()
                activity?.let { viewModel.getSearchList(query.toString(), it.baseContext, dataBinding.listMenu, dataBinding.noFound,
                    dataBinding.noFoundText) }
                view.hideKeyboard()
                dataBinding.searchField.onActionViewCollapsed()
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
        attachInputListeners()
        onBackPressed()
    }

    private fun onBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(activity?.baseContext, HomeActivity::class.java)
                activity?.startActivity(intent)
                activity?.finish()
            }
        })
    }

    fun View.hideKeyboard() {
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun observeViewModel(){
        viewModel.isLoading.observe(viewLifecycleOwner, androidx.lifecycle.Observer { isLoading ->
            dataBinding.loadingOverlay.loadingView.visibility =
                    if (isLoading) View.VISIBLE else View.GONE
        })
        viewModel.errCode.observe(viewLifecycleOwner, Observer { errCode ->
            if (errCode == "EC0032" || errCode == "EC0021" || errCode == "EC0017"){
                sessionManager.logout()
                Intent(activity?.baseContext, LoginV2Activity::class.java).apply {
                    activity?.startActivity(this)
                    activity?.finish()
                }
            }
        })
    }

    private fun attachInputListeners(){

        dataBinding.topAppBar.setNavigationOnClickListener {
            val intent = Intent(activity?.baseContext, HomeActivity::class.java)
            activity?.startActivity(intent)
            activity?.finish()
        }

        dataBinding.searchField.setOnClickListener {
            dataBinding.searchField.onActionViewExpanded()
        }
    }
}