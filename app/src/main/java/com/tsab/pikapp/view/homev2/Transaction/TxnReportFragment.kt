package com.tsab.pikapp.view.homev2.Transaction

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.Color.green
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.tsab.pikapp.R
import com.tsab.pikapp.util.SwipeUpUtil
import com.tsab.pikapp.view.homev2.HomeNavigation
import id.rizmaulana.floatingslideupsheet.view.FloatingSlideUpBuilder
import kotlinx.android.synthetic.main.expanded_txn_fragment.*
import kotlinx.android.synthetic.main.expanded_txn_fragment.tabs
import kotlinx.android.synthetic.main.fragment_txn_cart.*
import kotlinx.android.synthetic.main.fragment_txn_report.*
import kotlinx.android.synthetic.main.list_report_items.*
import kotlinx.android.synthetic.main.transaction_fragment.*
import java.util.*
import kotlin.collections.ArrayList

class TxnReportFragment : Fragment() {

    val listTest:ArrayList<String> = ArrayList()

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_txn_report, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpTabs()

        backbtn.setOnClickListener {
            activity?.finish()
        }

        val floatingSlideUpBuilder = SwipeUpUtil(requireView().context, floating)
                .setFloatingMenuRadiusInDp(32)
                .setFloatingMenu(container)
                .setPanel(expanded)
                .build()
        date.setText(Date().toString())

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    val status: Boolean = floatingSlideUpBuilder.collapseBottomSheetStatus()
                    Log.e("mkwmd", status.toString())
                    if(status == true){
                        floatingSlideUpBuilder.collapseBottomSheet()
                    }else{
                        activity?.finish()
                    }
                }
            })


        listTest.add("shopee")
        listTest.add("grab")
        listTest.add("pikapp")
        listTest.add("tokopedia")

        recyclerview_transaction.layoutManager =LinearLayoutManager(requireView().context)
        recyclerview_transaction.adapter = TxnReportAdapter(listTest)
    }

    private fun setUpTabs(){
        val adapter = activity?.let { RingkasanAdapter(it.supportFragmentManager) }
        if (adapter != null) {
            adapter.addFragment(AllTxnFragment(), "Semua Transaksi")
            adapter.addFragment(TxnDetailFragment(), "Rincian")
            viewPager.adapter = adapter
            tabs.setupWithViewPager(viewPager)
        }
    }
}