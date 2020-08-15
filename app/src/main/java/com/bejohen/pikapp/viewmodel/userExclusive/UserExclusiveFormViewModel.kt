package com.bejohen.pikapp.viewmodel.userExclusive

import android.app.Application
import android.view.View
import androidx.navigation.Navigation
import com.bejohen.pikapp.util.SharedPreferencesUtil
import com.bejohen.pikapp.view.userExclusive.UserExclusiveFormFragmentDirections
import com.bejohen.pikapp.viewmodel.BaseViewModel

class UserExclusiveFormViewModel(application: Application): BaseViewModel(application) {

    private var prefHelper = SharedPreferencesUtil(getApplication())

    fun getUserExclusiveFormFinished(): Boolean {
        return prefHelper.isUserExclusiveFormFinished() ?: false
    }

    fun goToUserExclusiveHome(view: View) {
        val action = UserExclusiveFormFragmentDirections.actionToUserExclusiveHomeFragment()
        Navigation.findNavController(view).navigate(action)
    }
}