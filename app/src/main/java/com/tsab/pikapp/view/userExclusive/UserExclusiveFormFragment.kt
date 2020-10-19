package com.tsab.pikapp.view.userExclusive

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentUserExclusiveFormBinding
import com.tsab.pikapp.viewmodel.userExclusive.UserExclusiveFormViewModel

class UserExclusiveFormFragment : Fragment() {

    private lateinit var viewModel: UserExclusiveFormViewModel
    private lateinit var dataBinding: FragmentUserExclusiveFormBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        dataBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_user_exclusive_form,
            container,
            false
        )
        viewModel = ViewModelProviders.of(this).get(UserExclusiveFormViewModel::class.java)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataBinding.buttonFormSubmit.setOnClickListener {
            sendData()
        }
        observeViewModel()
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun observeViewModel() {
        viewModel.userExclusiveRecommendationResponse.observe(this, Observer { response ->
            response?.let {
                viewModel.goToUserExclusiveHome(dataBinding.root)
            }
        })
        viewModel.loading.observe(this, Observer { isLoading ->
            isLoading?.let {
                dataBinding.loadingViewForm.visibility = if (it) View.VISIBLE else View.GONE
                dataBinding.buttonFormSubmit.isClickable = !it
            }
        })
        viewModel.errorResponse.observe(this, Observer { errorResponse ->
            errorResponse?.let {
                errorResponse.errMessage?.let { it1 -> viewModel.createToast(it1) }
                Log.d("debug", "errorrrr : ${errorResponse.errCode}, ${errorResponse.errMessage}")
            }
        })
    }

    private fun sendData() {
        val merchant1 = dataBinding.textFieldMerchant1.text.toString().trim()
        val merchantAddress1 = dataBinding.textFieldAddressMerchant1.text.toString().trim()

        val merchant2 = dataBinding.textFieldMerchant2.text.toString().trim()
        val merchantAddress2 = dataBinding.textFieldAddressMerchant2.text.toString().trim()

        val merchant3 = dataBinding.textFieldMerchant3.text.toString().trim()
        val merchantAddress3 = dataBinding.textFieldAddressMerchant3.text.toString().trim()

        val merchant4 = dataBinding.textFieldMerchant4.text.toString().trim()
        val merchantAddress4 = dataBinding.textFieldAddressMerchant4.text.toString().trim()

        val merchant5 = dataBinding.textFieldMerchant5.text.toString().trim()
        val merchantAddress5 = dataBinding.textFieldAddressMerchant5.text.toString().trim()

        viewModel.beginSubmission(
            merchant1,
            merchantAddress1,
            merchant2,
            merchantAddress2,
            merchant3,
            merchantAddress3,
            merchant4,
            merchantAddress4,
            merchant5,
            merchantAddress5
        )
    }
}