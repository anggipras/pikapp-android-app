package com.tsab.pikapp.view.promo

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.view.LayoutInflater
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.squareup.picasso.Picasso
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.ActivityPromoDetailPageBinding
import com.tsab.pikapp.models.model.PromoRegisListData
import com.tsab.pikapp.view.homev2.HomeActivity
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
    var handler: Handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_promo_detail_page)

        dataBinding.headerTracking.backImage.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.no_animation, R.anim.slide_down)
        }

        val promoDetailData = intent.getSerializableExtra(PROMO_DETAIL_DATA) as? PromoRegisListData
        dataBinding.headerTracking.headerTitle.text = getString(R.string.promo_detail_regis_title, promoDetailData?.campaign_name)
        mapPromoDetailData(promoDetailData)

        dataBinding.checkboxPromoRegistration.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                dataBinding.registrationButton.isEnabled = true
                dataBinding.registrationButton.setBackgroundResource(R.drawable.button_green_small)
            } else {
                dataBinding.registrationButton.isEnabled = false
                dataBinding.registrationButton.setBackgroundResource(R.drawable.button_dark_gray_small)
            }
        }

        dataBinding.registrationButton.setOnClickListener {
            /* AFTER HIT REGIS PROMO API */
            dataBinding.loadingOverlay.loadingView.isVisible = true
            handler.postDelayed({
                dataBinding.loadingOverlay.loadingView.isVisible = false
                successRegisDialog()
            }, 5000)
        }

        setPolicyText()
    }

    private fun mapPromoDetailData(promoDetailData: PromoRegisListData?) {
        dataBinding.promoDetailContent.text = promoDetailData?.campaign_detail
        Picasso.get().load(promoDetailData?.campaign_image).into(dataBinding.promoDetailImg)
        dateFormatter(promoDetailData)
    }

    private fun dateFormatter(promoDetailData: PromoRegisListData?) {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val formatterDate = SimpleDateFormat("dd MMMM yyyy", id)
        val outputStartDate = formatterDate.format(parser.parse(promoDetailData?.campaign_start_date))
        val outputEndDate = formatterDate.format(parser.parse(promoDetailData?.campaign_end_date))
        val outputDeadlineDate = formatterDate.format(parser.parse(promoDetailData?.campaign_regis_deadline_date))

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
        }
    }

    private fun getColoredSpanned(text: String, color: String): String {
        return "<font color=$color>$text</font>"
    }

    private fun successRegisDialog() {
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
            backToHomeAfterRegis()
        }
        mDialogView.oneButton_dialog_ok.setOnClickListener {
            mAlertDialog.dismiss()
            backToHomeAfterRegis()
        }
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
        finish()
        overridePendingTransition(R.anim.no_animation, R.anim.slide_down)
    }
}