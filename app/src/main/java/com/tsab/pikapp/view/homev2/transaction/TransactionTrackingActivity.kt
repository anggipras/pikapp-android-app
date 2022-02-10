package com.tsab.pikapp.view.homev2.transaction

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.ActivityTransactionTrackingBinding
import com.tsab.pikapp.models.model.*
import com.tsab.pikapp.models.network.PikappApiService
import com.tsab.pikapp.util.getClientID
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class TransactionTrackingActivity : AppCompatActivity() {
    companion object {
        const val WAYBILL_ID = "waybill_id"
        const val COURIER_NAME = "courier_name"
    }

    private lateinit var dataBinding: ActivityTransactionTrackingBinding
    private lateinit var recyclerAdapter: TransactionTrackListAdapter
    private val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_transaction_tracking)

        dataBinding.headerTracking.headerTitle.text = getString(R.string.tracking_order)
        dataBinding.headerTracking.backImage.setOnClickListener {
            finish()
        }

        initRecyclerView()

        val waybillIdProps = intent.getStringExtra(WAYBILL_ID)
        val courierNameProps = intent.getStringExtra(COURIER_NAME)
        getTrackTransactionOrder(waybillIdProps, courierNameProps)
    }

    private fun initRecyclerView() {
        dataBinding.recyclerviewTransactionTrack.layoutManager = LinearLayoutManager(baseContext, LinearLayoutManager.VERTICAL, false)
        recyclerAdapter = TransactionTrackListAdapter()
        dataBinding.recyclerviewTransactionTrack.adapter = recyclerAdapter
    }

    private fun getTrackTransactionOrder(waybillId: String?, courierName: String?) {
        dataBinding.loadingOverlay.loadingView.isVisible = true
        disposable.add(
            PikappApiService().courierApi.getTrackOrderDetail(getClientID(), TrackingOrderRequest(waybillId, courierName!!.lowercase()))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<TrackingDetailResponse>() {
                    override fun onSuccess(t: TrackingDetailResponse) {
                        if (t.errCode == "200") {
                            val trackResult = t.result
                            val waybillIdPass = waybillId ?: "0123082100003094"
                            dataBinding.shipmentWaybill.text = "Resi Pengiriman: $waybillIdPass"
                            dataBinding.driverName.text = trackResult.courier?.name ?: "Hadrian"
                            dataBinding.driverPhone.text = trackResult.courier?.phone ?: "081293955247"
                            dataBinding.callDriverBtn.setOnClickListener {
                                openWhatsApp(trackResult.courier?.phone ?: "081293955247")
                            }
                            dataBinding.clipboardCopy.setOnClickListener {
                                copyInvoice(waybillIdPass)
                            }

                            val trackOrderList: MutableList<TrackingDetail> = ArrayList()
                            trackOrderList.addAll(trackResult.history ?: dummyTrackingList())
                            trackOrderList.reverse()
                            recyclerAdapter.setTransactionTrackingList(trackOrderList)
                            dataBinding.loadingOverlay.loadingView.isVisible = false
                        }
                    }

                    override fun onError(e: Throwable) {
                        Log.e("ERROR", e.message.toString())
                        dataBinding.loadingOverlay.loadingView.isVisible = false
                    }
                })
        )
    }

    private fun openWhatsApp(phone: String?) {
        val temp = phone?.substringAfter("0")
        val waNumber = "+62$temp"

        val url = "https://api.whatsapp.com/send?phone=$waNumber"
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

    private fun copyInvoice(waybillID: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, waybillID)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    private fun dummyTrackingList() : List<TrackingDetail> {
        val trackOrderList: MutableList<TrackingDetail> = ArrayList()
        trackOrderList.add(
            TrackingDetail(
                note = "SHIPMENT RECEIVED BY JNE COUNTER OFFICER AT [JAKARTA]",
                updated_at = "2021-03-16T18:17:00+07:00",
                status = "dropping_off"
            )
        )
        trackOrderList.add(
            TrackingDetail(
                note = "RECEIVED AT SORTING CENTER [JAKARTA]",
                updated_at = "2021-03-16T21:15:00+07:00",
                status = "dropping_off"
            )
        )
        trackOrderList.add(
            TrackingDetail(
                note = "SHIPMENT FORWARDED TO DESTINATION [JAKARTA , HUB VETERAN BINTARO]",
                updated_at = "2021-03-16T23:12:00+07:00",
                status = "dropping_off"
            )
        )
        return trackOrderList
    }

    private fun dummyTrackingDetail(): TrackingDetailResult {
        val courierDetail = CourierDriver(
            company = "jne",
            name = "Hadrian",
            phone = "081293955247"
        )

        val originPlace = OriginPlace(
            contactName = "[INSTANT COURIER] BITESHIP/FIE",
            address = "JALAN TANJUNG 16 NO.5, RT.8/RW.2, WEST TANJUNG,SOUTH JAKARTA CITY, JAKARTA, IN"
        )

        val destinationPlace = DestinationPlace(
            contactName = "ADITARA MADJID",
            address = "THE PAKUBUWONO RESIDENCE, JALAN PAKUBUWONO VI,RW.1, GUNUNG, SOUTH JAKARTA CITY"
        )

        val trackOrderList: MutableList<TrackingDetail> = ArrayList()
        trackOrderList.add(
            TrackingDetail(
                note = "SHIPMENT RECEIVED BY JNE COUNTER OFFICER AT [JAKARTA]",
                updated_at = "2021-03-16T18:17:00+07:00",
                status = "dropping_off"
            )
        )
        trackOrderList.add(
            TrackingDetail(
                note = "RECEIVED AT SORTING CENTER [JAKARTA]",
                updated_at = "2021-03-16T21:15:00+07:00",
                status = "dropping_off"
            )
        )
        trackOrderList.add(
            TrackingDetail(
                note = "SHIPMENT FORWARDED TO DESTINATION [JAKARTA , HUB VETERAN BINTARO]",
                updated_at = "2021-03-16T23:12:00+07:00",
                status = "dropping_off"
            )
        )
        trackOrderList.reverse()

        return TrackingDetailResult(
            success = true,
            messsage = "Successfully get tracking info",
            resultObject = "tracking",
            id = "6051861741a37414e6637fab",
            waybillID = "0123082100003094",
            courier = courierDetail,
            origin = originPlace,
            destination = destinationPlace,
            history = trackOrderList,
            link = "-",
            orderID = null,
            status = "delivered"
        )
    }
}