package com.tsab.pikapp.view.homev2.Transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentCancelBinding
import com.tsab.pikapp.viewmodel.homev2.TransactionViewModel
import kotlinx.android.synthetic.main.fragment_cancel.*
import kotlinx.android.synthetic.main.fragment_proccess.recyclerview_transaction

class CancelFragment : Fragment(), TransactionListAdapter.OnItemClickListener {

    private val viewModel: TransactionViewModel by activityViewModels()
    lateinit var transactionListAdapter: TransactionListAdapter
    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var linearLayoutManager1: LinearLayoutManager
    private lateinit var dataBinding: FragmentCancelBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_cancel,
            container, false
        )
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerview_transaction.setHasFixedSize(true)
        linearLayoutManager =
            LinearLayoutManager(requireView().context, LinearLayoutManager.VERTICAL, false)
        linearLayoutManager1 =
            LinearLayoutManager(requireView().context, LinearLayoutManager.VERTICAL, false)
        recyclerview_transaction.layoutManager = linearLayoutManager
        recyclerview_tokopedia_cancel.layoutManager = linearLayoutManager1
        activity?.let {
            viewModel.getStoreOrderList(
                it.baseContext,
                recyclerview_transaction,
                "Batal",
                requireActivity().supportFragmentManager,
                emptyState1,
                this
            )
        }
        activity?.let {
            viewModel.getListOmni(
                it.baseContext,
                recyclerview_tokopedia_cancel,
                requireActivity().supportFragmentManager,
                requireActivity(),
                "Batal",
                emptyState1,
                requireParentFragment()
            )
        }

        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        dataBinding.emptyState1.isVisible =
            viewModel.batal.value == 0 && viewModel.batalOmni.value == 0
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            dataBinding.loadingOverlay.loadingView.visibility =
                if (isLoading) View.VISIBLE else View.GONE
        })
    }

    override fun onItemClick(i: Int) {
        TODO("Not yet implemented")
    }
}
