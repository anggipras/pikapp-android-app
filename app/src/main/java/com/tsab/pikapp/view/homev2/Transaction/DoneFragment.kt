package com.tsab.pikapp.view.homev2.Transaction

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentDoneBinding
import com.tsab.pikapp.databinding.FragmentProccessBinding
import com.tsab.pikapp.viewmodel.homev2.TransactionViewModel
import kotlinx.android.synthetic.main.fragment_proccess.*

class DoneFragment : Fragment() {

    private val viewModel: TransactionViewModel by activityViewModels()
    lateinit var transactionListAdapter: TransactionListAdapter
    lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var dataBinding: FragmentDoneBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_done,
                container, false
        )
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerview_transaction.setHasFixedSize(true)
        linearLayoutManager =
                LinearLayoutManager(requireView().context, LinearLayoutManager.VERTICAL, false)
        recyclerview_transaction.layoutManager = linearLayoutManager

        activity?.let { viewModel.getStoreOrderList(it.baseContext, recyclerview_transaction, "Done", requireActivity().supportFragmentManager, emptyState) }

        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        observeViewModel()
        activity?.let { viewModel.getStoreOrderList(it.baseContext, recyclerview_transaction, "Done", requireActivity().supportFragmentManager, emptyState) }
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            dataBinding.loadingOverlay.loadingView.visibility =
                    if (isLoading) View.VISIBLE else View.GONE
        })
    }

}