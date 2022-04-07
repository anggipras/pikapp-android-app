package com.tsab.pikapp.viewmodel.homev2

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.tsab.pikapp.models.model.PromoListModel
import com.tsab.pikapp.models.model.PromoRegisListModel
import com.tsab.pikapp.models.model.TransactionListV2Data
import com.tsab.pikapp.view.homev2.promo.PromoListAdapter

class PromoViewModel : ViewModel() {
    lateinit var promoListAdapter: PromoListAdapter
    private val mutablePromoList = MutableLiveData<MutableList<PromoListModel>>()
    val promoList: LiveData<MutableList<PromoListModel>> = mutablePromoList

    private var liveDataPromoRegisList: MutableLiveData<MutableList<PromoRegisListModel>> = MutableLiveData()
    fun getLiveDataPromoRegisListObserver(): MutableLiveData<MutableList<PromoRegisListModel>> {
        return liveDataPromoRegisList
    }

    fun getPromoRegisList() {
        val promoListResponse: MutableList<PromoRegisListModel> = ArrayList()
        promoListResponse.add(PromoRegisListModel(
            campaign_name = "SPESIAL1",
            campaign_image = "https://ecs7.tokopedia.net/blog-tokopedia-com/uploads/2017/08/Banner-Blog-Seller-Center-1200x630.jpg",
            campaign_quota = "20",
            discount_amt_type = "PERCENTAGE",
            discount_amt = 10,
            campaign_start_date = "2021-05-01T09:00:00",
            campaign_end_date = "2021-06-01T21:00:00",
            campaign_regis_deadline_date = "2021-04-20T21:00:00",
            campaign_detail = "Diskon Kilat Spesial hadir untuk membantu Anda meraih pelanggan sebanyak-banyaknya dengan penawaran diskon hingga 10%"
        ))
        promoListResponse.add(PromoRegisListModel(
            campaign_name = "SPESIAL2",
            campaign_image = "https://ecs7.tokopedia.net/img/cache/730/kjjBfF/2021/6/14/658ede0f-7236-425e-bef3-a498a6c14912.jpg",
            campaign_quota = "30",
            discount_amt_type = "PERCENTAGE",
            discount_amt = 20,
            campaign_start_date = "2021-05-02T09:00:00",
            campaign_end_date = "2021-06-02T21:00:00",
            campaign_regis_deadline_date = "2021-04-21T21:00:00",
            campaign_detail = "Diskon Kilat Spesial hadir untuk membantu Anda meraih pelanggan sebanyak-banyaknya dengan penawaran diskon hingga 20%"
        ))
        promoListResponse.add(PromoRegisListModel(
            campaign_name = "SPESIAL3",
            campaign_image = "https://ecs7.tokopedia.net/img/cache/730/kjjBfF/2021/6/15/616df725-64b7-4de8-b6c7-4d8d9fff7a68.png",
            campaign_quota = "40",
            discount_amt_type = "ABSOLUTE",
            discount_amt = 30000,
            campaign_start_date = "2021-05-03T09:00:00",
            campaign_end_date = "2021-06-03T21:00:00",
            campaign_regis_deadline_date = "2021-04-22T21:00:00",
            campaign_detail = "Diskon Kilat Spesial hadir untuk membantu Anda meraih pelanggan sebanyak-banyaknya dengan penawaran diskon hingga 30rb"
        ))
        promoListResponse.add(PromoRegisListModel(
            campaign_name = "SPESIAL4",
            campaign_image = "https://ecs7.tokopedia.net/blog-tokopedia-com/uploads/2019/05/Banner_Kotak-Kejutan-900-x-494.jpg",
            campaign_quota = "50",
            discount_amt_type = "ABSOLUTE",
            discount_amt = 40500,
            campaign_start_date = "2021-05-04T09:00:00",
            campaign_end_date = "2021-06-04T21:00:00",
            campaign_regis_deadline_date = "2021-04-23T21:00:00",
            campaign_detail = "Diskon Kilat Spesial hadir untuk membantu Anda meraih pelanggan sebanyak-banyaknya dengan penawaran diskon hingga 40.5rb"
        ))

        liveDataPromoRegisList.postValue(promoListResponse)
    }

    fun retrievePromoList(baseContext: Context, status: String, rView: RecyclerView) {
        val promoListResponse: MutableList<PromoListModel> = ArrayList()
        promoListResponse.add(PromoListModel(promoID = "PROMO/01/02/02020019", startDate = "1 Oktober 2021", endDate = "2 Oktober 2021", status = "Aktif"))
        promoListResponse.add(PromoListModel(promoID = "PROMO/01/02/02020020", startDate = "3 Oktober 2021", endDate = "4 Oktober 2021", status = "Tidak Aktif"))
        promoListResponse.add(PromoListModel(promoID = "PROMO/01/02/02020021", startDate = "5 Oktober 2021", endDate = "6 Oktober 2021", status = "Akan Datang"))
        promoListResponse.add(PromoListModel(promoID = "PROMO/01/02/02020022", startDate = "7 Oktober 2021", endDate = "8 Oktober 2021", status = "Aktif"))
        promoListResponse.add(PromoListModel(promoID = "PROMO/01/02/02020023", startDate = "9 Oktober 2021", endDate = "10 Oktober 2021", status = "Akan Datang"))
        promoListResponse.add(PromoListModel(promoID = "PROMO/01/02/02020024", startDate = "11 Oktober 2021", endDate = "12 Oktober 2021", status = "Dihentikan"))
        promoListResponse.add(PromoListModel(promoID = "PROMO/01/02/02020025", startDate = "13 Oktober 2021", endDate = "14 Oktober 2021", status = "Dihentikan"))
        promoListResponse.add(PromoListModel(promoID = "PROMO/01/02/02020026", startDate = "15 Oktober 2021", endDate = "16 Oktober 2021", status = "Tidak Aktif"))

        var promoListFilter: MutableList<PromoListModel> = ArrayList()
        if (status == "ongoing") {
            promoListFilter = promoListResponse.filterIndexed { _, promoListModel -> promoListModel.status == "Aktif" } as MutableList<PromoListModel>
        } else if (status == "upcoming") {
            promoListFilter = promoListResponse.filterIndexed { _, promoListModel -> promoListModel.status == "Akan Datang" } as MutableList<PromoListModel>
        } else if (status == "expired") {
            promoListFilter = promoListResponse.filterIndexed { _, promoListModel -> promoListModel.status == "Tidak Aktif" } as MutableList<PromoListModel>
        } else if (status == "canceled") {
            promoListFilter = promoListResponse.filterIndexed { _, promoListModel -> promoListModel.status == "Dihentikan" } as MutableList<PromoListModel>
        } else {
            promoListFilter = promoListResponse
        }

        mutablePromoList.value = promoListFilter
        promoListAdapter = PromoListAdapter(baseContext, promoListFilter)
        promoListAdapter.notifyDataSetChanged()
        rView.adapter = promoListAdapter
    }
}