package com.tsab.pikapp.view.homev2.transaction

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.ActivityTransactionTrackingBinding
import com.tsab.pikapp.models.model.TrackingDetail

class TransactionTrackingActivity : AppCompatActivity() {
    private lateinit var dataBinding: ActivityTransactionTrackingBinding
    private lateinit var recyclerAdapter: TransactionTrackListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_transaction_tracking)

        dataBinding.headerTracking.headerTitle.text = getString(R.string.tracking_order)
        dataBinding.headerTracking.backImage.setOnClickListener {
            finish()
        }

        initRecyclerView()
        getTrackTransactionOrder()
    }

    private fun initRecyclerView() {
        dataBinding.recyclerviewTransactionTrack.layoutManager = LinearLayoutManager(baseContext, LinearLayoutManager.VERTICAL, false)
        recyclerAdapter = TransactionTrackListAdapter()
        dataBinding.recyclerviewTransactionTrack.adapter = recyclerAdapter
    }

    private fun getTrackTransactionOrder() {
        val trackOrderList: MutableList<TrackingDetail> = ArrayList()
        trackOrderList.add(TrackingDetail(note = "SHIPMENT RECEIVED BY JNE COUNTER OFFICER AT [JAKARTA]", updated_at = "2021-03-16T18:17:00+07:00", status = "dropping_off"))
        trackOrderList.add(TrackingDetail(note = "RECEIVED AT SORTING CENTER [JAKARTA]", updated_at = "2021-03-16T21:15:00+07:00", status = "dropping_off"))
        trackOrderList.add(TrackingDetail(note = "SHIPMENT FORWARDED TO DESTINATION [JAKARTA , HUB VETERAN BINTARO]", updated_at = "2021-03-16T23:12:00+07:00", status = "dropping_off"))
        trackOrderList.reverse()
        recyclerAdapter.setTransactionTrackingList(trackOrderList)
    }
}