package com.tsab.pikapp.view.homev2.menu

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentDynamicBinding
import com.tsab.pikapp.view.menu.UpdateMenuActivity
import com.tsab.pikapp.viewmodel.homev2.DynamicViewModel
import com.tsab.pikapp.viewmodel.homev2.SearchViewModel

class DynamicFragment : Fragment() {
    companion object {
        fun newInstance(): DynamicFragment {
            return DynamicFragment()
        }
    }

    private lateinit var dataBinding: FragmentDynamicBinding
    lateinit var linearLayoutManager: LinearLayoutManager
    private val viewModel: DynamicViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_dynamic, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        attachInputListeners()

        linearLayoutManager = LinearLayoutManager(requireView().context)
        dataBinding.listMenuDetail.layoutManager = linearLayoutManager
        val nama: String = arguments?.getString("position").toString()
        activity?.let { viewModel.getSearchList(it.baseContext, dataBinding.listMenuDetail, dataBinding.imageView17,
                dataBinding.textview, nama) }
    }

    /**
     * Initialize empty state layout.
     */
    private fun attachInputListeners() {
        dataBinding.tambahMenuButton.setOnClickListener {
            Log.d("Dynamic Fragment", "Add button clicked!")
            Intent(activity?.baseContext, UpdateMenuActivity::class.java).apply {
                putExtra(UpdateMenuActivity.EXTRA_TYPE, UpdateMenuActivity.TYPE_ADD)
                activity?.startActivity(this)
            }
        }
    }
}