package com.tsab.pikapp.view.promo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.ActivityAllRegisPromoBinding
import com.tsab.pikapp.models.model.PromoRegisListData
import com.tsab.pikapp.viewmodel.homev2.PromoViewModel
import java.io.Serializable

class AllRegisPromoActivity : AppCompatActivity(), PromoRegisAdapter.OnItemClickListener {
    private lateinit var dataBinding: ActivityAllRegisPromoBinding
    private lateinit var recyclerAdapter: PromoRegisAdapter
    private val viewModel: PromoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_all_regis_promo)

        dataBinding.headerTracking.backImage.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.no_animation, R.anim.slide_down)
        }

        dataBinding.headerTracking.headerTitle.text = getString(R.string.promo_detail_regis_title, "Promo untuk Anda")

        dataBinding.headerTracking.backImage.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.no_animation, R.anim.slide_down)
        }

        viewModel.getPromoRegisList(1)

        initRecyclerView()
        initViewModel()
    }

    private fun initRecyclerView() {
        dataBinding.recyclerviewAllPromoList.layoutManager = LinearLayoutManager(baseContext, LinearLayoutManager.VERTICAL, false)
        recyclerAdapter = PromoRegisAdapter(baseContext, this)
        dataBinding.recyclerviewAllPromoList.adapter = recyclerAdapter

        dataBinding.nestedScrollAllPromo.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->
            // on scroll change we are checking when users scroll as bottom.
            if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {
                if (!viewModel.finishPromoPage.value!!) {
                    // in this method we are incrementing page number,
                    // making progress bar visible and calling get data method.
                    val pageAllPromoAct = viewModel.numberAllPromoPage.value!! + 1
                    dataBinding.loadingPB.isVisible = true
                    viewModel.getPromoRegisListPagination(pageAllPromoAct)
                } else {
                    dataBinding.loadingPB.isVisible = false
                }
            }
        })
    }

    private fun initViewModel() {
        viewModel.getLiveDataPromoRegisListObserver().observe(this, {
            if (!it.isNullOrEmpty()) {
                recyclerAdapter.setPromoListAdapter(it)
            }
        })

        viewModel.finishPromoPage.observe(this, {
            if (it) {
                dataBinding.loadingPB.isVisible = false
            }
        })
    }

    override fun onItemRegisPromoClick(promoRegisValue: PromoRegisListData) {
        Intent(baseContext, PromoDetailPageActivity::class.java).apply {
            putExtra(PromoDetailPageActivity.PROMO_DETAIL_FLOW, "REGIS")
            putExtra(PromoDetailPageActivity.PROMO_DETAIL_DATA, promoRegisValue as Serializable)
            startActivity(this)
            overridePendingTransition(R.anim.slide_up, R.anim.no_animation)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        overridePendingTransition(R.anim.no_animation, R.anim.slide_down)
    }
}