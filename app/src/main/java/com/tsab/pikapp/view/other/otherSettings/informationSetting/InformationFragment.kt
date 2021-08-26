package com.tsab.pikapp.view.other.otherSettings.informationSetting

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.squareup.picasso.Picasso
import com.tsab.pikapp.databinding.InformationFragmentBinding
import com.tsab.pikapp.models.model.BaseResponse
import com.tsab.pikapp.models.network.PikappApiService
import com.tsab.pikapp.util.*
import com.tsab.pikapp.viewmodel.other.OtherSettingViewModel
import kotlinx.android.synthetic.main.information_fragment.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.net.URL
import java.util.*


class InformationFragment : Fragment() {
    private lateinit var dataBinding: InformationFragmentBinding
    private val viewModel: OtherSettingViewModel by activityViewModels()
    private val sessionManager = SessionManager()

    private val pickImg = 100
    private var bannerUri: Uri? = null
    private var logoUri: Uri? = null
    private var imgSelection = 0

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        dataBinding = InformationFragmentBinding.inflate(inflater, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Picasso.get().load(sessionManager.getMerchantProfile()?.merchantBanner)
            .into(information_banner)
        Picasso.get().load(sessionManager.getMerchantProfile()?.merchantLogo).into(information_img)

        dataBinding.restaurantNameInput.setText(sessionManager.getMerchantProfile()?.merchantName)
        dataBinding.restaurantAddressInput.setText(sessionManager.getMerchantProfile()?.address)

        attachInputListeners()
        observeViewModel()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImg) {
            if (imgSelection == 1) {
                bannerUri = data?.data
                dataBinding.informationBanner.setImageURI(bannerUri)
                dataBinding.informationBanner.alpha = 1.toFloat()
                dataBinding.informationBannerIcChange.visibility = View.GONE
                viewModel.setBannerImg(bannerUri)
            } else {
                logoUri = data?.data
                dataBinding.informationImg.setImageURI(logoUri)
                dataBinding.informationImg.alpha = 1.toFloat()
                dataBinding.informationImgIcChange.visibility = View.GONE
                viewModel.setLogoImg(logoUri)
            }
        }
    }

    private fun attachInputListeners() {
        dataBinding.saveInformationButton.setOnClickListener { uploadInformationData() }
        dataBinding.backButtonInformation.setOnClickListener { requireActivity().onBackPressed() }

        dataBinding.informationBannerIcChange.setOnClickListener {
            imgSelection = 1
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, pickImg)
        }

        dataBinding.informationImgIcChange.setOnClickListener {
            imgSelection = 2
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, pickImg)
        }
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner, Observer { load ->
            if (load) {
                dataBinding.loadingViewInformation.visibility = View.VISIBLE
            } else {
                dataBinding.loadingViewInformation.visibility = View.GONE
            }
        })

        viewModel.isLoadingBackButton.observe(viewLifecycleOwner, Observer { load ->
            if (load) {
                view?.let { Navigation.findNavController(it).popBackStack() }
                viewModel.isLoadingBackButton.value = false
            }
        })
    }

    private fun uploadInformationData() {
        val restoAddress = dataBinding.restaurantAddressInput.text.toString()
        val restoName = dataBinding.restaurantNameInput.text.toString()

        if (dataBinding.informationBannerIcChange.visibility == View.VISIBLE || dataBinding.informationImgIcChange.visibility == View.VISIBLE) {
            Toast.makeText(
                    requireActivity(),
                    "Mohon upload kembali banner dan logo untuk mengganti nama resto atau alamat",
                    Toast.LENGTH_SHORT
            ).show()
        } else if (restoName.isNullOrEmpty()) {
            Toast.makeText(
                    requireActivity(),
                    "Nama Restoran tidak boleh kosong",
                    Toast.LENGTH_SHORT
            ).show()
        } else if (restoAddress.isNullOrEmpty()) {
            Toast.makeText(
                    requireActivity(),
                    "Alamat Restoran tidak boleh kosong",
                    Toast.LENGTH_SHORT
            ).show()
        } else {
            viewModel.loadProcess(true)
            val merchantBannerParcelFileDescriptor =
                requireActivity().contentResolver.openFileDescriptor(
                        viewModel._restaurantBanner.value!!, "r", null
                ) ?: return
            val merchantBannerInputStream =
                FileInputStream(merchantBannerParcelFileDescriptor.fileDescriptor)
            val merchantBanner = File(
                    requireActivity().cacheDir,
                    requireActivity().contentResolver.getFileName(viewModel._restaurantBanner.value!!)
            )
            val merchantBannerOutputStream = FileOutputStream(merchantBanner)
            merchantBannerInputStream.copyTo(merchantBannerOutputStream)

            val merchantLogoParcelFileDescriptor =
                requireActivity().contentResolver.openFileDescriptor(
                        viewModel._restaurantLogo.value!!, "r", null
                ) ?: return
            val merchantLogoInputStream =
                FileInputStream(merchantLogoParcelFileDescriptor.fileDescriptor)
            val merchantLogo = File(
                    requireActivity().cacheDir,
                    requireActivity().contentResolver.getFileName(viewModel._restaurantLogo.value!!)
            )
            val merchantLogoOutputStream = FileOutputStream(merchantLogo)
            merchantLogoInputStream.copyTo(merchantLogoOutputStream)

            val timestamp = getTimestamp()
            val email = sessionManager.getUserData()!!.email!!
            val signature = getSignature(email, timestamp)
            val token = sessionManager.getUserToken()!!

            // From session
            val gender = sessionManager.getGenderProfile()
            val dob = sessionManager.getDOBProfile()
            val bankAccountNo = sessionManager.getMerchantProfile()?.bankAccountNo
            val bankAccountName = sessionManager.getMerchantProfile()?.bankAccountName
            val bankName = sessionManager.getMerchantProfile()?.bankName
            val mid = sessionManager.getMerchantProfile()?.mid

            PikappApiService().api.uploadMerchantProfile(
                    getUUID(), timestamp, getClientID(), signature, token,
                    MultipartBody.Part.createFormData(
                            "file_01",
                            merchantBanner.name,
                            RequestBody.create(MediaType.parse("multipart/form-data"), merchantBanner)
                    ),
                    MultipartBody.Part.createFormData(
                            "file_02",
                            merchantLogo.name,
                            RequestBody.create(MediaType.parse("multipart/form-data"), merchantLogo)
                    ),
                    RequestBody.create(MediaType.parse("multipart/form-data"), restoAddress),
                    RequestBody.create(MediaType.parse("multipart/form-data"), restoName),
                    RequestBody.create(MediaType.parse("multipart/form-data"), gender),
                    RequestBody.create(MediaType.parse("multipart/form-data"), dob),
                    RequestBody.create(MediaType.parse("multipart/form-data"), bankAccountNo),
                    RequestBody.create(MediaType.parse("multipart/form-data"), bankAccountName),
                    RequestBody.create(MediaType.parse("multipart/form-data"), bankName),
                    RequestBody.create(MediaType.parse("multipart/form-data"), mid)
            ).enqueue(object : Callback<BaseResponse> {
                override fun onResponse(
                        call: Call<BaseResponse>,
                        response: Response<BaseResponse>
                ) {
                    viewModel.getMerchantProfile()
                }

                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {}
            })
        }
    }
}