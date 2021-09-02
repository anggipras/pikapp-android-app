package com.tsab.pikapp.view.homev2.Transaction

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.tsab.pikapp.R
import com.tsab.pikapp.viewmodel.homev2.TransactionViewModel
import kotlinx.android.synthetic.main.fragment_proccess.*

class ProcessFragment : Fragment(), TransactionListAdapter.OnItemClickListener {

    private val viewModel: TransactionViewModel by activityViewModels()
    lateinit var transactionListAdapter: TransactionListAdapter
    lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var dataBinding: ProcessFragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_proccess, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerview_transaction.setHasFixedSize(true)
        linearLayoutManager =
                LinearLayoutManager(requireView().context, LinearLayoutManager.VERTICAL, false)
        recyclerview_transaction.layoutManager = linearLayoutManager
        activity?.let { viewModel.getStoreOrderList(it.baseContext, recyclerview_transaction, this, "Proses") }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if(isVisibleToUser){
            recyclerview_transaction.setHasFixedSize(true)
            linearLayoutManager =
                LinearLayoutManager(requireView().context, LinearLayoutManager.VERTICAL, false)
            recyclerview_transaction.layoutManager = linearLayoutManager
            activity?.let { viewModel.getStoreOrderList(it.baseContext, recyclerview_transaction, this, "Proses") }
        }
    }

    override fun onItemClick(position: Int) {
        TODO("Not yet implemented")
    }
}