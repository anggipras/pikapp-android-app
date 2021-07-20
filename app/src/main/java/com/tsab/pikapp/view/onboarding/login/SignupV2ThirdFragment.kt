package com.tsab.pikapp.view.onboarding.login

import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tsab.pikapp.R
import com.tsab.pikapp.models.model.BaseResponse
import com.tsab.pikapp.models.network.PikappApiService
import com.tsab.pikapp.util.*
import com.tsab.pikapp.view.LoginV2Activity
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

val gson = Gson()
val typeToken = object : TypeToken<BaseResponse>() {}.type

class SignupV2ThirdFragment : Fragment() {
    private var ktpImage: Uri? = null
    private var logoImage: Uri? = null
    private var latarImage: Uri? = null

    lateinit var email: String
    lateinit var name: String
    lateinit var phone: String
    lateinit var pin: String

    var bankName: String? = null
    var resto: String? = null
    var fcourt: String? = null
    var alamat: String? = null
    var rekno: String? = null
    var rekname: String? = null

    private var fcm: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        email = requireArguments().getString("email").toString()
        name = requireArguments().getString("name").toString()
        phone = requireArguments().getString("phone").toString()
        pin = requireArguments().getString("pin").toString()
        bankName = requireArguments().getString("bank")
        resto = requireArguments().getString("restaurant")
        fcourt = requireArguments().getString("foodcourt")
        alamat = requireArguments().getString("alamat")
        rekno = requireArguments().getString("norek")
        rekname = requireArguments().getString("namarek")

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ktp.setOnClickListener {
            registerForActivityResult(ActivityResultContracts.GetContent(),
                    ActivityResultCallback { uri ->
                        ktp.setImageURI(uri)
                        ktpImage = uri
                    }
            ).launch("image/*")
        }

        logo.setOnClickListener {
            registerForActivityResult(ActivityResultContracts.GetContent(),
                    ActivityResultCallback { uri ->
                        logo.setImageURI(uri)
                        logoImage = uri
                    }
            ).launch("image/*")
        }

        latar.setOnClickListener {
            registerForActivityResult(ActivityResultContracts.GetContent(),
                    ActivityResultCallback { uri ->
                        latar.setImageURI(uri)
                        latarImage = uri
                    }
            ).launch("image/*")
        }

        btnNext.setOnClickListener {
            if (ktpImage == null) {
                ktperror.text = "KTP Tidak Boleh Kosong"
                ktperror.setTextColor(Color.parseColor("#DC6A84"))
            }

            if (logoImage == null) {
                logoerror.text = "Logo Restoran Tidak Boleh Kosong"
                logoerror.setTextColor(Color.parseColor("#DC6A84"))
            }

            if (latarImage == null) {
                latarerror.text = "Gambar Latar Tidak Boleh Kosong"
                latarerror.setTextColor(Color.parseColor("#DC6A84"))
            }

            if (ktpImage != null && logoImage != null && latarImage != null) {
                toggleLoadingView(true)
                uploadData()
            }
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signup_v2_third, container, false)
    }

    private fun uploadData() {
        val parcelFileDescriptor = requireActivity().contentResolver.openFileDescriptor(ktpImage!!,
                "r", null) ?: return
        val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
        val file = File(requireActivity().cacheDir,
                requireActivity().contentResolver.getFileName(ktpImage!!))
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)

        val parcelFileDescriptor1 = requireActivity().contentResolver.openFileDescriptor(
                logoImage!!, "r", null) ?: return
        val inputStream1 = FileInputStream(parcelFileDescriptor1.fileDescriptor)
        val file1 = File(requireActivity().cacheDir,
                requireActivity().contentResolver.getFileName(logoImage!!))
        val outputStream1 = FileOutputStream(file1)
        inputStream1.copyTo(outputStream1)

        val parcelFileDescriptor2 = requireActivity().contentResolver.openFileDescriptor(
                latarImage!!, "r", null) ?: return
        val inputStream2 = FileInputStream(parcelFileDescriptor2.fileDescriptor)
        val file2 = File(requireActivity().cacheDir,
                requireActivity().contentResolver.getFileName(latarImage!!))
        val outputStream2 = FileOutputStream(file2)
        inputStream2.copyTo(outputStream2)

        val branch = "$resto Branch"

        PikappApiService().api.uploadRegister(
                getUUID(), getClientID(), getTimestamp(),
                MultipartBody.Part.createFormData("file_01", file.name,
                        RequestBody.create(MediaType.parse("multipart/form-data"), file)),
                MultipartBody.Part.createFormData("file_02", file1.name,
                        RequestBody.create(MediaType.parse("multipart/form-data"), file1)),
                MultipartBody.Part.createFormData("file_03", file2.name,
                        RequestBody.create(MediaType.parse("multipart/form-data"), file2)),
                RequestBody.create(MediaType.parse("multipart/form-data"), alamat),
                RequestBody.create(MediaType.parse("multipart/form-data"), "1"),
                RequestBody.create(MediaType.parse("multipart/form-data"), bankName),
                RequestBody.create(MediaType.parse("multipart/form-data"), branch),
                RequestBody.create(MediaType.parse("multipart/form-data"), rekno),
                RequestBody.create(MediaType.parse("multipart/form-data"), rekname),
                RequestBody.create(MediaType.parse("multipart/form-data"), email),
                RequestBody.create(MediaType.parse("multipart/form-data"), phone),
                RequestBody.create(MediaType.parse("multipart/form-data"), resto),
                RequestBody.create(MediaType.parse("multipart/form-data"), fcm.toString()),
                RequestBody.create(MediaType.parse("multipart/form-data"), pin),
                RequestBody.create(MediaType.parse("multipart/form-data"), "No Bank"),
                RequestBody.create(MediaType.parse("multipart/form-data"), fcourt)
        ).enqueue(object : Callback<BaseResponse> {
            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                toggleLoadingView(false)
            }

            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                toggleLoadingView(false)

                if (response.code() == 200 && response.body()!!.errCode.toString() == "EC0000") {
                    val intent = Intent(activity?.baseContext, LoginV2Activity::class.java)
                    activity?.startActivity(intent)
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