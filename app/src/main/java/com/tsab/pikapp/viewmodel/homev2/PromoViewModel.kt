package com.tsab.pikapp.viewmodel.homev2

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.tsab.pikapp.models.model.*
import com.tsab.pikapp.view.homev2.promo.PromoListAdapter

class PromoViewModel : ViewModel() {
    lateinit var promoListAdapter: PromoListAdapter
    private val mutablePromoList = MutableLiveData<MutableList<PromoListModel>>()
    val promoList: LiveData<MutableList<PromoListModel>> = mutablePromoList

    private var liveDataPromoRegisList: MutableLiveData<MutableList<PromoRegisListData>> = MutableLiveData()
    fun getLiveDataPromoRegisListObserver(): MutableLiveData<MutableList<PromoRegisListData>> {
        return liveDataPromoRegisList
    }

    fun getPromoRegisList(flow: Int) {
        /* DUMMY DATA MOCKING FROM BE*/
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
            campaign_image = "https://thumbs.dreamstime.com/z/christmas-tours-travel-promo-banner-design-template-vector-illu-illustration-130596950.jpg",
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
            campaign_image = "https://previews.123rf.com/images/vectorprodesign/vectorprodesign1911/vectorprodesign191100002/134068784-travel-promo-up-to-forty-percents-off-poster-vector-illustration-add-banner-with-blue-sky-and-flying.jpg",
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
            campaign_image = "https://previews.123rf.com/images/vectorprodesign/vectorprodesign1911/vectorprodesign191100002/134068784-travel-promo-up-to-forty-percents-off-poster-vector-illustration-add-banner-with-blue-sky-and-flying.jpg",
            campaign_quota = "50",
            discount_amt_type = "ABSOLUTE",
            discount_amt = 40500,
            campaign_start_date = "2021-05-04T09:00:00",
            campaign_end_date = "2021-06-04T21:00:00",
            campaign_regis_deadline_date = "2021-04-23T21:00:00",
            campaign_detail = "Diskon Kilat Spesial hadir untuk membantu Anda meraih pelanggan sebanyak-banyaknya dengan penawaran diskon hingga 40.5rb"
        ))
        promoListResponse.add(PromoRegisListModel(
            campaign_name = "SPESIAL5",
            campaign_image = "https://image.shutterstock.com/shutterstock/photos/1325995616/display_1500/stock-vector-travel-promo-banner-vacation-poster-design-travelling-and-tourism-web-banner-concept-summer-1325995616.jpg",
            campaign_quota = "60",
            discount_amt_type = "PERCENTAGE",
            discount_amt = 50,
            campaign_start_date = "2021-05-05T09:00:00",
            campaign_end_date = "2021-06-05T21:00:00",
            campaign_regis_deadline_date = "2021-04-24T21:00:00",
            campaign_detail = "Diskon Kilat Spesial hadir untuk membantu Anda meraih pelanggan sebanyak-banyaknya dengan penawaran diskon hingga 50%"
        ))
        promoListResponse.add(PromoRegisListModel(
            campaign_name = "SPESIAL6",
            campaign_image = "https://image.shutterstock.com/shutterstock/photos/1325995616/display_1500/stock-vector-travel-promo-banner-vacation-poster-design-travelling-and-tourism-web-banner-concept-summer-1325995616.jpg",
            campaign_quota = "70",
            discount_amt_type = "PERCENTAGE",
            discount_amt = 60,
            campaign_start_date = "2021-05-06T09:00:00",
            campaign_end_date = "2021-06-06T21:00:00",
            campaign_regis_deadline_date = "2021-04-25T21:00:00",
            campaign_detail = "Diskon Kilat Spesial hadir untuk membantu Anda meraih pelanggan sebanyak-banyaknya dengan penawaran diskon hingga 60%"
        ))
        promoListResponse.add(PromoRegisListModel(
            campaign_name = "SPESIAL7",
            campaign_image = "https://image.shutterstock.com/shutterstock/photos/1325995616/display_1500/stock-vector-travel-promo-banner-vacation-poster-design-travelling-and-tourism-web-banner-concept-summer-1325995616.jpg",
            campaign_quota = "80",
            discount_amt_type = "ABSOLUTE",
            discount_amt = 70000,
            campaign_start_date = "2021-05-07T09:00:00",
            campaign_end_date = "2021-06-07T21:00:00",
            campaign_regis_deadline_date = "2021-04-26T21:00:00",
            campaign_detail = "Diskon Kilat Spesial hadir untuk membantu Anda meraih pelanggan sebanyak-banyaknya dengan penawaran diskon hingga 70rb"
        ))

        /* MAPPING DATA WITH VIEWTYPE */
        if (flow == 0) {
            val promoListData: MutableList<PromoRegisListData> = ArrayList()
            promoListResponse.forEachIndexed { ind, it ->
                if (ind < 5) {
                    promoListData.add(addPromoData(0, it))
                }
            }
            liveDataPromoRegisList.postValue(promoListData)
        } else {
            val promoListData: MutableList<PromoRegisListData> = ArrayList()
            promoListResponse.forEach {
                promoListData.add(addPromoData(1, it))
            }
            liveDataPromoRegisList.postValue(promoListData)
        }
    }

    private fun addPromoData(viewType: Int, it: PromoRegisListModel): PromoRegisListData {
        return PromoRegisListData(
            viewType = viewType,
            campaign_name = it.campaign_name,
            campaign_image = it.campaign_image,
            campaign_quota = it.campaign_quota,
            discount_amt_type = it.discount_amt_type,
            discount_amt = it.discount_amt,
            campaign_start_date = it.campaign_start_date,
            campaign_end_date = it.campaign_end_date,
            campaign_regis_deadline_date = it.campaign_regis_deadline_date,
            campaign_detail = it.campaign_detail
        )
    }

    private var liveDataPromoAppliedList: MutableLiveData<MutableList<PromoAppliedListData>> = MutableLiveData()
    fun getLiveDataPromoAppliedListObserver(): MutableLiveData<MutableList<PromoAppliedListData>> {
        return liveDataPromoAppliedList
    }

    fun getPromoAppliedList() {
        /* DUMMY DATA MOCKING FROM BE*/
        val promoListResponse: MutableList<PromoAppliedListModel> = ArrayList()
        promoListResponse.add(PromoAppliedListModel(
            campaign_name = "SPESIAL1",
            campaign_image = "https://ecs7.tokopedia.net/blog-tokopedia-com/uploads/2017/08/Banner-Blog-Seller-Center-1200x630.jpg",
            campaign_quota = "20",
            discount_amt_type = "PERCENTAGE",
            discount_amt = 10,
            campaign_start_date = "2021-05-01T09:00:00",
            campaign_end_date = "2021-06-01T21:00:00",
            campaign_regis_deadline_date = "2021-04-20T21:00:00",
            campaign_detail = "Diskon Kilat Spesial hadir untuk membantu Anda meraih pelanggan sebanyak-banyaknya dengan penawaran diskon hingga 10%",
            campaign_status = "ONGOING"
        ))
        promoListResponse.add(PromoAppliedListModel(
            campaign_name = "SPESIAL2",
            campaign_image = "https://thumbs.dreamstime.com/z/christmas-tours-travel-promo-banner-design-template-vector-illu-illustration-130596950.jpg",
            campaign_quota = "30",
            discount_amt_type = "PERCENTAGE",
            discount_amt = 20,
            campaign_start_date = "2021-05-02T09:00:00",
            campaign_end_date = "2021-06-02T21:00:00",
            campaign_regis_deadline_date = "2021-04-21T21:00:00",
            campaign_detail = "Diskon Kilat Spesial hadir untuk membantu Anda meraih pelanggan sebanyak-banyaknya dengan penawaran diskon hingga 20%",
            campaign_status = "ONPROCESS"
        ))
        promoListResponse.add(PromoAppliedListModel(
            campaign_name = "SPESIAL3",
            campaign_image = "https://previews.123rf.com/images/vectorprodesign/vectorprodesign1911/vectorprodesign191100002/134068784-travel-promo-up-to-forty-percents-off-poster-vector-illustration-add-banner-with-blue-sky-and-flying.jpg",
            campaign_quota = "40",
            discount_amt_type = "ABSOLUTE",
            discount_amt = 30000,
            campaign_start_date = "2021-05-03T09:00:00",
            campaign_end_date = "2021-06-03T21:00:00",
            campaign_regis_deadline_date = "2021-04-22T21:00:00",
            campaign_detail = "Diskon Kilat Spesial hadir untuk membantu Anda meraih pelanggan sebanyak-banyaknya dengan penawaran diskon hingga 30rb",
            campaign_status = "ONPROCESS"
        ))
        promoListResponse.add(PromoAppliedListModel(
            campaign_name = "SPESIAL4",
            campaign_image = "https://previews.123rf.com/images/vectorprodesign/vectorprodesign1911/vectorprodesign191100002/134068784-travel-promo-up-to-forty-percents-off-poster-vector-illustration-add-banner-with-blue-sky-and-flying.jpg",
            campaign_quota = "50",
            discount_amt_type = "ABSOLUTE",
            discount_amt = 40500,
            campaign_start_date = "2021-05-04T09:00:00",
            campaign_end_date = "2021-06-04T21:00:00",
            campaign_regis_deadline_date = "2021-04-23T21:00:00",
            campaign_detail = "Diskon Kilat Spesial hadir untuk membantu Anda meraih pelanggan sebanyak-banyaknya dengan penawaran diskon hingga 40.5rb",
            campaign_status = "ONGOING"
        ))
        promoListResponse.add(PromoAppliedListModel(
            campaign_name = "SPESIAL5",
            campaign_image = "https://image.shutterstock.com/shutterstock/photos/1325995616/display_1500/stock-vector-travel-promo-banner-vacation-poster-design-travelling-and-tourism-web-banner-concept-summer-1325995616.jpg",
            campaign_quota = "60",
            discount_amt_type = "PERCENTAGE",
            discount_amt = 50,
            campaign_start_date = "2021-05-05T09:00:00",
            campaign_end_date = "2021-06-05T21:00:00",
            campaign_regis_deadline_date = "2021-04-24T21:00:00",
            campaign_detail = "Diskon Kilat Spesial hadir untuk membantu Anda meraih pelanggan sebanyak-banyaknya dengan penawaran diskon hingga 50%",
            campaign_status = "ONGOING"
        ))

        /* MAPPING DATA WITH VIEWTYPE */
        val promoListData: MutableList<PromoAppliedListData> = ArrayList()
        promoListResponse.forEach {
            promoListData.add(addAppliedPromoData(it))
        }
        liveDataPromoAppliedList.postValue(promoListData)
    }

    private fun addAppliedPromoData(it: PromoAppliedListModel): PromoAppliedListData {
        return PromoAppliedListData(
            viewType = 0,
            campaign_name = it.campaign_name,
            campaign_image = it.campaign_image,
            campaign_quota = it.campaign_quota,
            discount_amt_type = it.discount_amt_type,
            discount_amt = it.discount_amt,
            campaign_start_date = it.campaign_start_date,
            campaign_end_date = it.campaign_end_date,
            campaign_regis_deadline_date = it.campaign_regis_deadline_date,
            campaign_detail = it.campaign_detail,
            campaign_status = it.campaign_status
        )
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