package com.tsab.pikapp.view.other.otherSettings.informationSetting

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.squareup.picasso.Picasso
import com.tsab.pikapp.databinding.InformationFragmentBinding
import com.tsab.pikapp.util.SessionManager
import com.tsab.pikapp.util.getFileName
import com.tsab.pikapp.viewmodel.other.OtherSettingViewModel
import kotlinx.android.synthetic.main.information_fragment.*
import java.io.File

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
    ): View? {
        dataBinding = InformationFragmentBinding.inflate(inflater, container, false)

        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Picasso.get().load(sessionManager.getMerchantProfile()?.merchantBanner).into(information_banner)
        Picasso.get().load(sessionManager.getMerchantProfile()?.merchantLogo).into(information_img)
        dataBinding.restaurantNameInput.setText("PIKAPP RESTO")
        dataBinding.restaurantAddressInput.setText("JALAN PIKAPP")

        dataBinding.saveInformationButton.setOnClickListener {
            val restoName = dataBinding.restaurantNameInput.text.toString()
            val restoAddress = dataBinding.restaurantAddressInput.text.toString()
            viewModel.setNewMerchNameAndAddress(restoName, restoAddress)
        }

        dataBinding.backButtonInformation.setOnClickListener {
            requireActivity().onBackPressed()
        }

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

        uploadInformationData()

////        OTHER WAY TO GET IMAGE FROM LOCAL
//        dataBinding.informationBannerIcChange.setOnClickListener {
//            registerForActivityResult(ActivityResultContracts.GetContent(),
//                    ActivityResultCallback { uri: Uri ->
//                        dataBinding.informationBanner.setImageURI(uri)
//                        dataBinding.informationBanner.alpha = 1.toFloat()
//                        dataBinding.informationBannerIcChange.visibility = View.GONE
//                    }
//            ).launch("image/*")
//        }
//
//        dataBinding.informationImgIcChange.setOnClickListener {
//            registerForActivityResult(ActivityResultContracts.GetContent(),
//                    ActivityResultCallback { uri: Uri ->
//                        dataBinding.informationImg.setImageURI(uri)
//                        dataBinding.informationImg.alpha = 1.toFloat()
//                        dataBinding.informationImgIcChange.visibility = View.GONE
//                    }
//            ).launch("image/*")
//        }
    }

    private fun observeViewModel() {
        viewModel._restaurantName.observe(viewLifecycleOwner, Observer { resultChange ->
            requireActivity().onBackPressed()
        })
    }

    private fun uploadInformationData() {
        val merchantBanner = File(requireActivity().cacheDir, requireActivity().contentResolver.getFileName(viewModel._restaurantBanner.value!!))
        val merchantLogo = File(requireActivity().cacheDir, requireActivity().contentResolver.getFileName(viewModel._restaurantLogo.value!!))

        viewModel.uploadMerchantProfile(merchantBanner, merchantLogo)
    }

}