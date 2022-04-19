package com.tsab.pikapp.view.promo

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.squareup.picasso.Picasso
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.ActivityPromoDetailPageBinding
import com.tsab.pikapp.models.model.*
import com.tsab.pikapp.models.network.PikappApiService
import com.tsab.pikapp.util.*
import com.tsab.pikapp.view.homev2.HomeActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.one_button_dialog.view.*
import java.text.SimpleDateFormat
import java.util.*

class PromoDetailPageActivity : AppCompatActivity() {
    companion object {
        const val PROMO_DETAIL_FLOW = "add"
        const val PROMO_DETAIL_DATA = "promo_data"
    }

    private lateinit var dataBinding: ActivityPromoDetailPageBinding
    private val id = Locale("in", "ID")
    var registerSuccessFlow = 0
    private val sessionManager = SessionManager()
    private val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_promo_detail_page)

        dataBinding.headerTracking.backImage.setOnClickListener {
            if (registerSuccessFlow == 0) {
                finish()
                overridePendingTransition(R.anim.no_animation, R.anim.slide_down)
            } else {
                backToHomeAfterRegis()
            }
        }

        val promoDetailFlow = intent.getStringExtra(PROMO_DETAIL_FLOW)
        if (promoDetailFlow == "REGIS") {
            val promoRegisDetailData = intent.getSerializableExtra(PROMO_DETAIL_DATA) as? PromoRegisListData
            dataBinding.headerTracking.headerTitle.text = getString(R.string.promo_detail_regis_title, promoRegisDetailData?.campaign_name)
            mapPromoDetailData(promoRegisDetailData)

            dataBinding.registrationButton.setOnClickListener {
                /* AFTER HIT REGIS PROMO API */
                regisPromo(promoRegisDetailData)
            }
        } else {
            val promoAppliedDetailData = intent.getSerializableExtra(PROMO_DETAIL_DATA) as? PromoAppliedListData
            dataBinding.headerTracking.headerTitle.text = getString(R.string.promo_detail_regis_title, promoAppliedDetailData?.campaign_name)
            mapPromoAppliedDetailData(promoAppliedDetailData)
        }

        dataBinding.checkboxPromoRegistration.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                dataBinding.registrationButton.isEnabled = true
                dataBinding.registrationButton.setBackgroundResource(R.drawable.button_green_small)
            } else {
                dataBinding.registrationButton.isEnabled = false
                dataBinding.registrationButton.setBackgroundResource(R.drawable.button_dark_gray_small)
            }
        }

        setPolicyText()
        MyCalc()
    }

    private fun regisPromo(promoRegisDetailData: PromoRegisListData?) {
        dataBinding.loadingOverlay.loadingView.isVisible = true
        val email = sessionManager.getUserData()!!.email!!
        val token = sessionManager.getUserToken()!!
        val timestamp = getTimestamp()
        val signature = getSignature(email, timestamp)
        val mid = sessionManager.getUserData()!!.mid!!

        val promoRegisRequest = PromoRegisRequest(
            merchant_id = mid,
            campaign_id = promoRegisDetailData?.id
        )

        disposable.add(
            PikappApiService().api.registerCampaignPromo(getUUID(), timestamp, getClientID(), signature, token, promoRegisRequest)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<BaseResponse>() {
                    override fun onSuccess(t: BaseResponse) {
                        dataBinding.loadingOverlay.loadingView.isVisible = false
                        successRegisDialog(promoRegisDetailData)
                    }

                    override fun onError(e: Throwable) {
                        dataBinding.loadingOverlay.loadingView.isVisible = false
                        Toast.makeText(baseContext, "Gagal registrasi promo", Toast.LENGTH_SHORT).show()
                    }

                })
        )
    }

    private fun mapPromoDetailData(promoRegisDetailData: PromoRegisListData?) {
        dataBinding.promoDetailContent.text = promoRegisDetailData?.campaign_detail
        Picasso.get().load(promoRegisDetailData?.campaign_image).into(dataBinding.promoDetailImg)
        dateFormatterRegisPromo(promoRegisDetailData)
    }

    private fun mapPromoAppliedDetailData(promoAppliedDetailData: PromoAppliedListData?) {
        dataBinding.promoDetailContent.text = promoAppliedDetailData?.campaign_detail
        Picasso.get().load(promoAppliedDetailData?.campaign_image).into(dataBinding.promoDetailImg)
        dateFormatterAppliedPromo(promoAppliedDetailData)

        dataBinding.promoDeadlineLayout.isVisible = false
        dataBinding.promoVoucherCodeLayout.isVisible = true
        dataBinding.promoVoucherCodeContent.text = baseContext.getString(R.string.detail_voucher_code_content, promoAppliedDetailData?.campaign_code)
        dataBinding.promoStatusLayout.isVisible = true
        dataBinding.promoStatusContent.text = baseContext.getString(R.string.detail_status_content, promoAppliedDetailData?.campaign_status)
        dataBinding.registrationPolicyLayout.isVisible = false
        dataBinding.registrationButton.text = "Lihat Performa"
        dataBinding.registrationButton.setBackgroundResource(R.drawable.button_green_small)
        dataBinding.registrationButton.isEnabled = true
        dataBinding.registrationButton.setOnClickListener {
            /* GO TO SEE PERFORMANCE WEB VIEW */
            Toast.makeText(baseContext, "Lihat Performa", Toast.LENGTH_SHORT).show()
        }
    }

    private fun dateFormatterRegisPromo(promoRegisDetailData: PromoRegisListData?) {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val formatterDate = SimpleDateFormat("dd MMMM yyyy", id)
        val outputStartDate = formatterDate.format(parser.parse(promoRegisDetailData?.campaign_start_date))
        val outputEndDate = formatterDate.format(parser.parse(promoRegisDetailData?.campaign_end_date))
        val outputDeadlineDate = formatterDate.format(parser.parse(promoRegisDetailData?.campaign_deadline_date))

        dataBinding.promoDurationContent.text = baseContext.getString(R.string.detail_duration_content, outputStartDate, outputEndDate)
        dataBinding.promoDeadlineContent.text = baseContext.getString(R.string.detail_deadline_content, outputDeadlineDate)
    }

    private fun dateFormatterAppliedPromo(promoAppliedPromoDetailData: PromoAppliedListData?) {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val formatterDate = SimpleDateFormat("dd MMMM yyyy", id)
        val outputStartDate = formatterDate.format(parser.parse(promoAppliedPromoDetailData?.campaign_start_date))
        val outputEndDate = formatterDate.format(parser.parse(promoAppliedPromoDetailData?.campaign_end_date))
        val outputDeadlineDate = formatterDate.format(parser.parse(promoAppliedPromoDetailData?.campaign_deadline_date))

        dataBinding.promoDurationContent.text = baseContext.getString(R.string.detail_duration_content, outputStartDate, outputEndDate)
        dataBinding.promoDeadlineContent.text = baseContext.getString(R.string.detail_deadline_content, outputDeadlineDate)
    }

    private fun setPolicyText() {
        val firstSentence: String = getColoredSpanned("Saya menyetujui", "#111111")
        val midSentence: String = getColoredSpanned("Syarat dan Ketentuan", "#4BB7AC")
        val lastSentence: String = getColoredSpanned("yang berlaku", "#111111")
        dataBinding.registrationPolicyText.text = Html.fromHtml("$firstSentence $midSentence $lastSentence")
        dataBinding.registrationPolicyText.setOnClickListener {
            /* LATER, OPEN THE POLICY DIALOG */
            Toast.makeText(baseContext, "Web Page Syarat dan Ketentuan", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getColoredSpanned(text: String, color: String): String {
        return "<font color=$color>$text</font>"
    }

    private fun successRegisDialog(promoRegisDetailData: PromoRegisListData?) {
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.one_button_dialog, null)
        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)
        val mAlertDialog = mBuilder.show()
        mAlertDialog.getWindow()?.setBackgroundDrawable(
            AppCompatResources.getDrawable(
                this,
                R.drawable.dialog_background
            )
        )
        mDialogView.oneButton_dialog_text.text = getString(R.string.registration_promo_succeed)
        mDialogView.oneButton_dialog_close.setOnClickListener {
            mAlertDialog.dismiss()
            uiAfterRegister(promoRegisDetailData)
        }
        mDialogView.oneButton_dialog_ok.setOnClickListener {
            mAlertDialog.dismiss()
            uiAfterRegister(promoRegisDetailData)
        }
        mAlertDialog.setOnCancelListener {
            uiAfterRegister(promoRegisDetailData)
        }
    }

    private fun uiAfterRegister(promoRegisDetailData: PromoRegisListData?) {
        registerSuccessFlow = 1
        dataBinding.promoDetailImg.alpha = 0.5F
        dataBinding.promoDeadlineLayout.isVisible = false
        dataBinding.promoVoucherCodeLayout.isVisible = true
        dataBinding.promoVoucherCodeContent.text = baseContext.getString(R.string.detail_voucher_code_content, promoRegisDetailData?.campaign_name)
        dataBinding.promoStatusLayout.isVisible = true
        dataBinding.promoStatusContent.text = baseContext.getString(R.string.detail_status_content, "Terdaftar")
        dataBinding.promoStatusContent.setTextColor(resources.getColor(R.color.colorGrey))
        dataBinding.registrationPolicyLayout.isVisible = false
        dataBinding.registrationButton.text = "Lihat Performa"
        dataBinding.registrationButton.isEnabled = false
        dataBinding.registrationButton.setBackgroundResource(R.drawable.button_dark_gray_small)
    }

    private fun backToHomeAfterRegis() {
        Intent(baseContext, HomeActivity::class.java).apply {
            startActivity(this)
            finish()
            overridePendingTransition(R.anim.no_animation, R.anim.slide_down)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (registerSuccessFlow == 0) {
            finish()
            overridePendingTransition(R.anim.no_animation, R.anim.slide_down)
        } else {
            backToHomeAfterRegis()
        }
    }
}

interface Calculations {
    fun calculateCircumstance(radius:Double) : Double
    fun calculateArea(radius:Double) : Double
}

class MyCalc : Calculations {
    private val pi = 3.14

    override fun calculateCircumstance(radius: Double): Double {
        return 2 * pi * radius
    }

    override fun calculateArea(radius: Double): Double {
        return pi * radius * radius
    }
}