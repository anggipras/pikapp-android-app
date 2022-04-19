package com.tsab.pikapp.viewmodel.homev2

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.tsab.pikapp.models.model.*
import com.tsab.pikapp.models.network.PikappApiService
import com.tsab.pikapp.util.*
import com.tsab.pikapp.view.homev2.promo.PromoListAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PromoViewModel : ViewModel() {
    lateinit var promoListAdapter: PromoListAdapter
    private var sessionManager = SessionManager()
    private val mutablePromoList = MutableLiveData<MutableList<PromoListModel>>()
    val promoList: LiveData<MutableList<PromoListModel>> = mutablePromoList

    // All Promo Pagination
    private val mutableFinishPromoPage = MutableLiveData(false)
    val finishPromoPage: LiveData<Boolean> get() = mutableFinishPromoPage
    private val mutableNumberAllPromoPage = MutableLiveData(0)
    val numberAllPromoPage: LiveData<Int> get() = mutableNumberAllPromoPage

    private var liveDataPromoRegisList: MutableLiveData<MutableList<PromoRegisListData>> = MutableLiveData()
    fun getLiveDataPromoRegisListObserver(): MutableLiveData<MutableList<PromoRegisListData>> {
        return liveDataPromoRegisList
    }

    fun getPromoRegisList(flow: Int) {
        val timeStamp = getTimestamp()
        val email = sessionManager.getUserData()!!.email!!
        val signature = getSignature(email, timeStamp)
        val token = sessionManager.getUserToken()!!
        val promoListRequest = PromoListRequest(
            start_date = null,
            end_date = null,
            campaign_name = null,
            campaign_status = listOf("SCHEDULED", "OPEN_REGISTRATION"),
            page = 0,
            size = 10
        )
        CoroutineScope(Dispatchers.IO).launch {
            val response = PikappApiService().api.getPromoList(
                getUUID(),
                timeStamp,
                getClientID(),
                signature,
                token,
                promoListRequest
            )
            if (response.isSuccessful) {
                if (response.code() == 200 && response.body()!!.errCode.toString() == "EC0000") {
                    if (response.body()!!.results?.isNotEmpty() == true) {
                        val promoListResponse = response.body()!!.results

                        /* MAPPING DATA WITH VIEWTYPE */
                        if (flow == 0) { // 0 means register promo list at home page
                            val promoListData: MutableList<PromoRegisListData> = ArrayList()
                            promoListResponse?.forEachIndexed { ind, it ->
                                if (ind < 5) {
                                    promoListData.add(addPromoData(0, it))
                                }
                            }
                            withContext(Dispatchers.Main) {
                                liveDataPromoRegisList.postValue(promoListData)
                            }
                        } else { // 1 means register promo list at see all
                            val promoListData: MutableList<PromoRegisListData> = ArrayList()
                            promoListResponse?.forEach {
                                promoListData.add(addPromoData(1, it))
                            }

                            withContext(Dispatchers.Main) {
                                if (promoListData.size < 5) {
                                    mutableFinishPromoPage.value = true
                                }
                                liveDataPromoRegisList.postValue(promoListData)
                            }
                        }
                    }
                }
            }
        }
    }

    fun getPromoRegisListPagination(page: Int) {
        val timeStamp = getTimestamp()
        val email = sessionManager.getUserData()!!.email!!
        val signature = getSignature(email, timeStamp)
        val token = sessionManager.getUserToken()!!
        val promoListRequest = PromoListRequest(
            start_date = null,
            end_date = null,
            campaign_name = null,
            campaign_status = listOf("SCHEDULED", "OPEN_REGISTRATION"),
            page = page,
            size = 10
        )
        CoroutineScope(Dispatchers.IO).launch {
            val response = PikappApiService().api.getPromoList(
                getUUID(),
                timeStamp,
                getClientID(),
                signature,
                token,
                promoListRequest
            )
            if (response.isSuccessful) {
                if (response.code() == 200 && response.body()!!.errCode.toString() == "EC0000") {
                    if (response.body()!!.results?.isNotEmpty() == true) {
                        val promoListResponse = response.body()!!.results

                        val promoListData: MutableList<PromoRegisListData> = ArrayList()
                        if (getLiveDataPromoRegisListObserver().value?.isNotEmpty() == true) {
                            getLiveDataPromoRegisListObserver().value?.let { promoListData.addAll(it) }
                        }
                        promoListResponse?.forEach {
                            promoListData.add(addPromoData(1, it))
                        }

                        withContext(Dispatchers.Main) {
                            liveDataPromoRegisList.postValue(promoListData)
                            mutableNumberAllPromoPage.value = page
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            mutableFinishPromoPage.value = true
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        mutableFinishPromoPage.value = true
                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    mutableFinishPromoPage.value = true
                }
            }
        }
    }

    private fun addPromoData(viewType: Int, it: PromoRegisListModel): PromoRegisListData {
        return PromoRegisListData(
            viewType = viewType,
            id = it.id,
            campaign_name = it.campaign_name,
            campaign_image = it.campaign_image,
            campaign_quota = it.campaign_quota,
            discount_amt_type = it.discount_amt_type,
            discount_amt = it.discount_amt,
            campaign_start_date = it.campaign_start_date,
            campaign_end_date = it.campaign_end_date,
            campaign_deadline_date = it.campaign_deadline_date,
            campaign_detail = it.campaign_detail,
            campaign_status = it.campaign_status,
            campaign_code = it.campaign_code
        )
    }

    // All Applied Promo Pagination
    private val mutableFinishAppliedPromoPage = MutableLiveData(false)
    val finishAppliedPromoPage: LiveData<Boolean> get() = mutableFinishAppliedPromoPage
    private val mutableNumberAppliedPromoPage = MutableLiveData(0)
    val numberAppliedPromoPage: LiveData<Int> get() = mutableNumberAppliedPromoPage

    private var liveDataPromoAppliedList: MutableLiveData<MutableList<PromoAppliedListData>> = MutableLiveData()
    fun getLiveDataPromoAppliedListObserver(): MutableLiveData<MutableList<PromoAppliedListData>> {
        return liveDataPromoAppliedList
    }

    fun getPromoAppliedList(page: Int) {
        val timeStamp = getTimestamp()
        val email = sessionManager.getUserData()!!.email!!
        val signature = getSignature(email, timeStamp)
        val token = sessionManager.getUserToken()!!
        val mid = sessionManager.getUserData()!!.mid!!
        val promoAppliedListRequest = PromoAppliedListRequest(
            merchant_id = mid,
            merchant_status = listOf("REGISTERED", "ON_REVIEW", "APPROVED", "FAILED"),
            start_date = null,
            end_date = null,
            page = page,
            size = 5
        )
        CoroutineScope(Dispatchers.IO).launch {
            val response = PikappApiService().api.getAppliedPromoList(
                getUUID(),
                timeStamp,
                getClientID(),
                signature,
                token,
                promoAppliedListRequest
            )
            if (response.isSuccessful) {
                if (response.code() == 200 && response.body()!!.errCode.toString() == "EC0000") {
                    if (response.body()!!.results?.isNotEmpty() == true) {
                        val promoListResponse = response.body()!!.results

                        /* MAPPING DATA WITH VIEWTYPE */
                        val promoListData: MutableList<PromoAppliedListData> = ArrayList()
                        if (getLiveDataPromoAppliedListObserver().value?.isNotEmpty() == true) {
                            getLiveDataPromoAppliedListObserver().value?.let { promoListData.addAll(it) }
                        }
                        promoListResponse?.forEach {
                            promoListData.add(addAppliedPromoData(it))
                        }

                        withContext(Dispatchers.Main) {
                            if (page == 0 && promoListData.size < 5) {
                                mutableFinishPromoPage.value = true
                            }

                            liveDataPromoAppliedList.postValue(promoListData)
                            mutableNumberAppliedPromoPage.value = page
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            mutableFinishAppliedPromoPage.value = true
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        mutableFinishAppliedPromoPage.value = true
                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    mutableFinishAppliedPromoPage.value = true
                }
            }
        }
    }

    private fun addAppliedPromoData(it: PromoAppliedListModel): PromoAppliedListData {
        return PromoAppliedListData(
            viewType = 0,
            id = it.id,
            campaign_name = it.campaign_name,
            campaign_image = it.campaign_image,
            campaign_quota = it.campaign_quota,
            discount_amt_type = it.discount_amt_type,
            discount_amt = it.discount_amt,
            campaign_start_date = it.campaign_start_date,
            campaign_end_date = it.campaign_end_date,
            campaign_deadline_date = it.campaign_deadline_date,
            campaign_detail = it.campaign_detail,
            campaign_status = it.campaign_status,
            campaign_code = it.campaign_code,
            merchant_status = it.merchant_status
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

    private fun promoRegisListDummyData() {
        /* DUMMY DATA MOCKING FROM BE*/
//        val promoListResponse: MutableList<PromoRegisListModel> = ArrayList()
//        promoListResponse.add(PromoRegisListModel(
//            campaign_name = "SPESIAL1",
//            campaign_image = "https://www.electgo.com/wp-content/uploads/2021/05/HCD2-Banner-1080x600-2.jpg",
//            campaign_quota = "20",
//            discount_amt_type = "PERCENTAGE",
//            discount_amt = 10,
//            campaign_start_date = "2021-05-01T09:00:00",
//            campaign_end_date = "2021-06-01T21:00:00",
//            campaign_deadline_date = "2021-04-20T21:00:00",
//            campaign_detail = "Diskon Kilat Spesial hadir untuk membantu Anda meraih pelanggan sebanyak-banyaknya dengan penawaran diskon hingga 10%"
//        ))
//        promoListResponse.add(PromoRegisListModel(
//            campaign_name = "SPESIAL2",
//            campaign_image = "https://www.electgo.com/wp-content/uploads/2021/05/HCD2-Banner-1080x600-2.jpg",
//            campaign_quota = "30",
//            discount_amt_type = "PERCENTAGE",
//            discount_amt = 20,
//            campaign_start_date = "2021-05-02T09:00:00",
//            campaign_end_date = "2021-06-02T21:00:00",
//            campaign_deadline_date = "2021-04-21T21:00:00",
//            campaign_detail = "Diskon Kilat Spesial hadir untuk membantu Anda meraih pelanggan sebanyak-banyaknya dengan penawaran diskon hingga 20%"
//        ))
//        promoListResponse.add(PromoRegisListModel(
//            campaign_name = "SPESIAL3",
//            campaign_image = "https://www.electgo.com/wp-content/uploads/2021/05/HCD2-Banner-1080x600-2.jpg",
//            campaign_quota = "40",
//            discount_amt_type = "ABSOLUTE",
//            discount_amt = 30000,
//            campaign_start_date = "2021-05-03T09:00:00",
//            campaign_end_date = "2021-06-03T21:00:00",
//            campaign_deadline_date = "2021-04-22T21:00:00",
//            campaign_detail = "Diskon Kilat Spesial hadir untuk membantu Anda meraih pelanggan sebanyak-banyaknya dengan penawaran diskon hingga 30rb"
//        ))
//        promoListResponse.add(PromoRegisListModel(
//            campaign_name = "SPESIAL4",
//            campaign_image = "https://lelogama.go-jek.com/post_featured_image/promo-tokopedia-agustus.jpg",
//            campaign_quota = "50",
//            discount_amt_type = "ABSOLUTE",
//            discount_amt = 40500,
//            campaign_start_date = "2021-05-04T09:00:00",
//            campaign_end_date = "2021-06-04T21:00:00",
//            campaign_deadline_date = "2021-04-23T21:00:00",
//            campaign_detail = "Diskon Kilat Spesial hadir untuk membantu Anda meraih pelanggan sebanyak-banyaknya dengan penawaran diskon hingga 40.5rb"
//        ))
//        promoListResponse.add(PromoRegisListModel(
//            campaign_name = "SPESIAL5",
//            campaign_image = "https://www.electgo.com/wp-content/uploads/2021/05/HCD2-Banner-1080x600-2.jpg",
//            campaign_quota = "60",
//            discount_amt_type = "PERCENTAGE",
//            discount_amt = 50,
//            campaign_start_date = "2021-05-05T09:00:00",
//            campaign_end_date = "2021-06-05T21:00:00",
//            campaign_deadline_date = "2021-04-24T21:00:00",
//            campaign_detail = "Diskon Kilat Spesial hadir untuk membantu Anda meraih pelanggan sebanyak-banyaknya dengan penawaran diskon hingga 50%"
//        ))
//        promoListResponse.add(PromoRegisListModel(
//            campaign_name = "SPESIAL6",
//            campaign_image = "https://www.electgo.com/wp-content/uploads/2021/05/HCD2-Banner-1080x600-2.jpg",
//            campaign_quota = "70",
//            discount_amt_type = "PERCENTAGE",
//            discount_amt = 60,
//            campaign_start_date = "2021-05-06T09:00:00",
//            campaign_end_date = "2021-06-06T21:00:00",
//            campaign_deadline_date = "2021-04-25T21:00:00",
//            campaign_detail = "Diskon Kilat Spesial hadir untuk membantu Anda meraih pelanggan sebanyak-banyaknya dengan penawaran diskon hingga 60%"
//        ))
//        promoListResponse.add(PromoRegisListModel(
//            campaign_name = "SPESIAL7",
//            campaign_image = "https://www.electgo.com/wp-content/uploads/2021/05/HCD2-Banner-1080x600-2.jpg",
//            campaign_quota = "80",
//            discount_amt_type = "ABSOLUTE",
//            discount_amt = 70000,
//            campaign_start_date = "2021-05-07T09:00:00",
//            campaign_end_date = "2021-06-07T21:00:00",
//            campaign_deadline_date = "2021-04-26T21:00:00",
//            campaign_detail = "Diskon Kilat Spesial hadir untuk membantu Anda meraih pelanggan sebanyak-banyaknya dengan penawaran diskon hingga 70rb"
//        ))
    }
}