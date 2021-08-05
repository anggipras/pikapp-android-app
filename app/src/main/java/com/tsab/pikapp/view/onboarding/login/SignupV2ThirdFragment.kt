package com.tsab.pikapp.view.onboarding.login

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentSignupV2ThirdBinding
import com.tsab.pikapp.models.model.BaseResponse
import com.tsab.pikapp.models.network.PikappApiService
import com.tsab.pikapp.util.*
import com.tsab.pikapp.view.LoginV2Activity
import com.tsab.pikapp.viewmodel.onboarding.signup.SignupOnboardingViewModelV2
import kotlinx.android.synthetic.main.fragment_signup_v2_third.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class SignupV2ThirdFragment : Fragment() {
    private val viewModel: SignupOnboardingViewModelV2 by activityViewModels()
    private lateinit var navController: NavController
    private lateinit var dataBinding: FragmentSignupV2ThirdBinding

    private val gson = Gson()
    private val typeToken = object : TypeToken<BaseResponse>() {}.type

    private var fcm: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(ContentValues.TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            fcm = token.toString()
        })
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_signup_v2_third,
                container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        observeViewModel()
        attachInputListeners()
    }

    private fun observeViewModel() {
        viewModel.ktp.observe(viewLifecycleOwner, Observer { ktpUri ->
            dataBinding.ktpImageSelector.setImageURI(ktpUri)
        })

        viewModel.logo.observe(viewLifecycleOwner, Observer { logoUri ->
            dataBinding.logoRestoranImageSelector.setImageURI(logoUri)
        })

        viewModel.latar.observe(viewLifecycleOwner, Observer { latarUri ->
            dataBinding.latarRestoranImageSelector.setImageURI(latarUri)
        })
    }

    private fun attachInputListeners() {
        dataBinding.ktpImageSelector.setOnClickListener {
            registerForActivityResult(ActivityResultContracts.GetContent(),
                    ActivityResultCallback { uri: Uri ->
                        viewModel.validateKtp(uri)
                    }
            ).launch("image/*")
        }

        dataBinding.logoRestoranImageSelector.setOnClickListener {
            registerForActivityResult(ActivityResultContracts.GetContent(),
                    ActivityResultCallback { uri: Uri ->
                        viewModel.validateLogo(uri)
                    }
            ).launch("image/*")
        }

        dataBinding.latarRestoranImageSelector.setOnClickListener {
            registerForActivityResult(ActivityResultContracts.GetContent(),
                    ActivityResultCallback { uri: Uri ->
                        viewModel.validateLatar(uri)
                    }
            ).launch("image/*")
        }

        dataBinding.nextButton.setOnClickListener {
            viewModel.validateKtp(viewModel.ktp.value)
            viewModel.validateLogo(viewModel.logo.value)
            viewModel.validateLatar(viewModel.latar.value)

            if (!viewModel.validateThirdPage()) return@setOnClickListener

            toggleLoadingView(true)
            uploadData()
        }
    }

    private fun uploadData() {
        val ktpParcelFileDescriptor = requireActivity().contentResolver.openFileDescriptor(
                viewModel.ktp.value!!, "r", null) ?: return
        val ktpInputStream = FileInputStream(ktpParcelFileDescriptor.fileDescriptor)
        val ktpFile = File(requireActivity().cacheDir,
                requireActivity().contentResolver.getFileName(viewModel.ktp.value!!))
        val ktpOutputStream = FileOutputStream(ktpFile)
        ktpInputStream.copyTo(ktpOutputStream)

        val logoParcelFileDescriptor = requireActivity().contentResolver.openFileDescriptor(
                viewModel.logo.value!!, "r", null) ?: return
        val logoInputStream = FileInputStream(logoParcelFileDescriptor.fileDescriptor)
        val logoFile = File(requireActivity().cacheDir,
                requireActivity().contentResolver.getFileName(viewModel.logo.value!!))
        val logoOutputStream = FileOutputStream(logoFile)
        logoInputStream.copyTo(logoOutputStream)

        val latarParcelFileDescriptor = requireActivity().contentResolver.openFileDescriptor(
                viewModel.latar.value!!, "r", null) ?: return
        val latarInputStream = FileInputStream(latarParcelFileDescriptor.fileDescriptor)
        val latarFile = File(requireActivity().cacheDir,
                requireActivity().contentResolver.getFileName(viewModel.latar.value!!))
        val latarOutputStream = FileOutputStream(latarFile)
        latarInputStream.copyTo(latarOutputStream)

        val branch = "${viewModel.namaRestoran.value} Branch"

        PikappApiService().api.uploadRegister(
                getUUID(), getClientID(), getTimestamp(),
                MultipartBody.Part.createFormData("file_01", ktpFile.name,
                        RequestBody.create(MediaType.parse("multipart/form-data"), ktpFile)),
                MultipartBody.Part.createFormData("file_02", logoFile.name,
                        RequestBody.create(MediaType.parse("multipart/form-data"), logoFile)),
                MultipartBody.Part.createFormData("file_03", latarFile.name,
                        RequestBody.create(MediaType.parse("multipart/form-data"), latarFile)),
                RequestBody.create(MediaType.parse("multipart/form-data"),
                        viewModel.alamat.value!!),
                RequestBody.create(MediaType.parse("multipart/form-data"),
                        "1"),
                RequestBody.create(MediaType.parse("multipart/form-data"),
                        viewModel.namaBank.value!!),
                RequestBody.create(MediaType.parse("multipart/form-data"),
                        branch),
                RequestBody.create(MediaType.parse("multipart/form-data"),
                        viewModel.nomorRekening.value!!),
                RequestBody.create(MediaType.parse("multipart/form-data"),
                        viewModel.namaRekening.value!!),
                RequestBody.create(MediaType.parse("multipart/form-data"),
                        viewModel.email.value!!),
                RequestBody.create(MediaType.parse("multipart/form-data"),
                        viewModel.phone.value!!),
                RequestBody.create(MediaType.parse("multipart/form-data"),
                        viewModel.namaRestoran.value!!),
                RequestBody.create(MediaType.parse("multipart/form-data"),
                        fcm.toString()),
                RequestBody.create(MediaType.parse("multipart/form-data"),
                        viewModel.pin.value!!),
                RequestBody.create(MediaType.parse("multipart/form-data"),
                        "No Bank"),
                RequestBody.create(MediaType.parse("multipart/form-data"),
                        viewModel.namaFoodcourt.value!!)
        ).enqueue(object : Callback<BaseResponse> {
            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                Log.e("UploadRegisterError", t.message.toString())
                toggleLoadingView(false)
            }

            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                toggleLoadingView(false)

                if (response.code() == 200 && response.body()!!.errCode.toString() == "EC0000") {
                    Intent(activity?.baseContext, LoginV2Activity::class.java).apply {
                        activity?.startActivity(this)
                        this@SignupV2ThirdFragment.activity?.finish()
                    }
                } else {
                    val errorResponse: BaseResponse? = gson.fromJson(
                            response.errorBody()!!.charStream(), typeToken)
                    Toast.makeText(context, generateResponseMessage(errorResponse?.errCode,
                            errorResponse?.errMessage), Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun toggleLoadingView(isLoading: Boolean) {
        loadingView.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}