package com.tsab.pikapp.viewmodel.menu

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonSizeSpec
import com.skydoves.balloon.createBalloon
import com.tsab.pikapp.R
import com.tsab.pikapp.models.network.PikappApiService
import com.tsab.pikapp.util.SessionManager
import com.tsab.pikapp.util.SharedPreferencesUtil
import com.tsab.pikapp.viewmodel.BaseViewModel
import io.reactivex.disposables.CompositeDisposable

class EditMenuViewModel (application: Application) : BaseViewModel(application) {

    private var sessionManager = SessionManager(getApplication())
    private var prefHelper = SharedPreferencesUtil(getApplication())

    private val apiService = PikappApiService()
    private val disposable = CompositeDisposable()

    private val mutableMenu = MutableLiveData(Uri.EMPTY)
    private val mutableMenuError = MutableLiveData("")
    private val isMenuValid = MutableLiveData(false)
    val menu: LiveData<Uri> get() = mutableMenu
    val menuError: LiveData<String> get() = mutableMenuError

    fun showTooltip(context: Context): Balloon {
        var balloon = createBalloon(context) {
            setArrowSize(10)
            setWidth(BalloonSizeSpec.WRAP)
            setHeight(65)
            setMarginRight(14)
            setArrowPosition(0.9f)
            setCornerRadius(4f)
            setAlpha(0.8f)
            setPaddingRight(20)
            setPaddingLeft(20)
            setAutoDismissDuration(5000L)
            setText("Harga belum termasuk\n" + "pajak dan biaya layanan")
            setTextColorResource(R.color.tooltipText)
            setBackgroundColorResource(R.color.tooltipBackground)
            onBalloonClickListener?.let { setOnBalloonClickListener(it) }
            setBalloonAnimation(BalloonAnimation.FADE)
            setLifecycleOwner(lifecycleOwner)
        }
        return balloon
    }

    fun validateMenu(menu: Uri?): Boolean {
        if (menu == null || menu == Uri.EMPTY) {
            mutableMenuError.value = "KTP tidak boleh kosong"
            isMenuValid.value = false
        } else {
            mutableMenuError.value = ""
            mutableMenu.value = menu
            isMenuValid.value = true
        }
        return isMenuValid.value!!
    }
}