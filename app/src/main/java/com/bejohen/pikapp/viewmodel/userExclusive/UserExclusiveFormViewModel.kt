package com.bejohen.pikapp.viewmodel.userExclusive

import android.app.Application
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import com.bejohen.pikapp.models.model.*
import com.bejohen.pikapp.models.network.PikappApiService
import com.bejohen.pikapp.util.SessionManager
import com.bejohen.pikapp.util.SharedPreferencesUtil
import com.bejohen.pikapp.util.containsForbiddenCharacter
import com.bejohen.pikapp.view.userExclusive.UserExclusiveFormFragmentDirections
import com.bejohen.pikapp.viewmodel.BaseViewModel
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException

class UserExclusiveFormViewModel(application: Application) : BaseViewModel(application) {

    private var prefHelper = SharedPreferencesUtil(getApplication())
    private var sessionManager = SessionManager(getApplication())
    private val apiService = PikappApiService()
    private val disposable = CompositeDisposable()

    val userExclusiveRecommendationResponse = MutableLiveData<UserExclusiveRecommendationResponse>()
    val errorResponse = MutableLiveData<ErrorResponse>()
    val loading = MutableLiveData<Boolean>()

    private fun getUserData(): UserAccess? {
        return sessionManager.getUserData()
    }

    fun getUserExclusiveFormFinished(): Boolean {
        return prefHelper.isUserExclusiveFormFinished() ?: false
    }

    fun beginSubmission(
        m1: String,
        ma1: String,
        m2: String,
        ma2: String,
        m3: String,
        ma3: String,
        m4: String,
        ma4: String,
        m5: String,
        ma5: String
    ) {

        if (m1.isEmpty() || m2.isEmpty() || m3.isEmpty()  || m4.isEmpty()  || m5.isEmpty() || ma1.isEmpty() || ma2.isEmpty() || ma3.isEmpty() || ma4.isEmpty() || ma5.isEmpty()) {
            Toast.makeText(
                getApplication(),
                "Mohon isi seluruh form",
                Toast.LENGTH_SHORT
            ).show()
        } else if (containsForbiddenCharacter(m1) || containsForbiddenCharacter(m2) || containsForbiddenCharacter(m3) || containsForbiddenCharacter(m4) || containsForbiddenCharacter(m5) || containsForbiddenCharacter(ma1) || containsForbiddenCharacter(ma2) || containsForbiddenCharacter(ma3) || containsForbiddenCharacter(ma4) || containsForbiddenCharacter(ma5))  {
            Toast.makeText(
                getApplication(),
                "Karakter <>\"'=;() dilarang",
                Toast.LENGTH_SHORT
            ).show()
        }
        else {
            submitForm("${m1}|${ma1}", "${m2}|${ma2}", "${m3}|${ma3}", "${m4}|${ma4}", "${m5}|${ma5}")
        }
    }

    private fun submitForm(rec1: String, rec2: String, rec3: String, rec4: String, rec5: String) {
        val userData = getUserData()!!
        val token = sessionManager.getUserToken()!!
        val recommendationRequest = UserExclusiveRecommendationRequest(rec1, rec2, rec3, rec4, rec5)
        loading.value = true
        disposable.add(
            apiService.sendRecommendation(userData.email!!, token, recommendationRequest)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object :
                    DisposableSingleObserver<UserExclusiveRecommendationResponse>() {
                    override fun onSuccess(t: UserExclusiveRecommendationResponse) {
                        submitSuccess(t)
                    }

                    override fun onError(e: Throwable) {
                        Log.d("Debug", "error : " + e)
                        var errorResponse: ErrorResponse
                        try {
                            val responseBody = (e as HttpException)
                            val body = responseBody.response()?.errorBody()?.string()
                            errorResponse =
                                Gson().fromJson(body, ErrorResponse::class.java)
                        } catch (err: Throwable) {
                            errorResponse =
                                ErrorResponse(
                                    "503",
                                    "Service Unavailable"
                                )
                        }
                        submitFail(errorResponse)
                        Toast.makeText(
                            getApplication(),
                            errorResponse.errMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                })
        )
    }

    fun submitSuccess(response: UserExclusiveRecommendationResponse) {
        userExclusiveRecommendationResponse.value = response
        loading.value = false
    }

    private fun submitFail(response: ErrorResponse) {
        errorResponse.value = response
        loading.value = false
    }

    fun goToUserExclusiveHome(view: View) {
        prefHelper.saveUserExclusiveForm(true)
        val action = UserExclusiveFormFragmentDirections.actionToUserExclusiveHomeFragment()
        Navigation.findNavController(view).navigate(action)
    }

    fun createToast(m: String) {
        Toast.makeText(getApplication(), m, Toast.LENGTH_SHORT).show()
    }
}