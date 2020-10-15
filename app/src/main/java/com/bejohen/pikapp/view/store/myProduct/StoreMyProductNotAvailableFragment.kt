package com.bejohen.pikapp.view.store.myProduct

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.bejohen.pikapp.R
import com.bejohen.pikapp.databinding.FragmentStoreMyProductNotAvailableBinding
import com.bejohen.pikapp.view.StoreActivity
import com.bejohen.pikapp.viewmodel.store.StoreProductListViewModel

class StoreMyProductNotAvailableFragment : Fragment(), StoreMyProductListAdapter.ProductListInterface {

    private lateinit var dataBinding: FragmentStoreMyProductNotAvailableBinding
    private lateinit var viewModel: StoreProductListViewModel
    private val myProductListAdapter = StoreMyProductListAdapter(arrayListOf(), this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        dataBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_store_my_product_not_available,
            container,
            false
        )
        viewModel = ViewModelProviders.of(this).get(StoreProductListViewModel::class.java)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataBinding.myProductNotAvailList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = myProductListAdapter
        }

        dataBinding.pListNotAvailableRefreshLayout.setOnRefreshListener {
            viewModel.getProductListAvailable(false)
            dataBinding.pListNotAvailableRefreshLayout.isRefreshing = false
        }

        viewModel.getProductListAvailable(false)
        observeViewModel()
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun observeViewModel() {
        viewModel.storeProductListResponse.observe(this, Observer { it ->
            myProductListAdapter.updateProductList(it)
        })

    }

    override fun changeToOnOff(pid: String, position: Int, status: Boolean) {
        viewModel.setOnOffProduct(pid, position, !status)
    }

    override fun onEditTapped(pid: String) {
        val action = StoreMyProductFragmentDirections.actionToStoreMyProductFormFragment(false, pid)
        Navigation.findNavController(dataBinding.root).navigate(action)
    }

    override fun onDeleteTapped(pid: String, position: Int, pName: String) {
        val builder = AlertDialog.Builder(activity as StoreActivity)
        builder.apply {
            setTitle("Hapus Produk")
            setMessage("Anda yakin akan menghapus produk \"${pName}\"?")
            setPositiveButton("Hapus") { dialog, which ->
                // Do something when user press the positive button
                viewModel.deleteProduct(pid, position)
            }
            builder.setNegativeButton("Kepencet") { dialog, which ->

            }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
    }
}