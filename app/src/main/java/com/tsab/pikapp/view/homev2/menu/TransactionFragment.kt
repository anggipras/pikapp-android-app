package com.tsab.pikapp.view.homev2.menu

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.tsab.pikapp.R
import com.tsab.pikapp.view.homev2.HomeNavigation
import com.tsab.pikapp.view.homev2.Transaction.CancelFragment
import com.tsab.pikapp.view.homev2.Transaction.DoneFragment
import com.tsab.pikapp.view.homev2.Transaction.ProcessFragment
import com.tsab.pikapp.view.homev2.Transaction.TransactionAdapter
import com.tsab.pikapp.viewmodel.homev2.TransactionViewModel
import kotlinx.android.synthetic.main.transaction_fragment.*

class TransactionFragment : Fragment() {

    companion object {
        fun newInstance() = TransactionFragment()
    }

    private lateinit var viewModel: TransactionViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        closeKeyboard()
        return inflater.inflate(R.layout.transaction_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(TransactionViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpTabs()
    }

    fun closeKeyboard() {
        val activity = activity as HomeNavigation

        val view = activity.currentFocus
        if (view != null) {
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun setUpTabs(){
        val adapter = activity?.let { TransactionAdapter(it.supportFragmentManager) }
        if (adapter != null) {
            adapter.addFragment(ProcessFragment(), "Diproses")
            adapter.addFragment(DoneFragment(), "Selesai")
            adapter.addFragment(CancelFragment(), "Batal")
            viewpager.adapter = adapter
            tabs.setupWithViewPager(viewpager)
        }
    }

}