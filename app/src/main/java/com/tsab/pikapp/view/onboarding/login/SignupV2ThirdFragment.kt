package com.tsab.pikapp.view.onboarding.login

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tsab.pikapp.R
import com.tsab.pikapp.models.model.BaseResponse
import com.tsab.pikapp.models.model.LoginResponseV2
import com.tsab.pikapp.models.network.PikappApiService
import com.tsab.pikapp.util.*
import com.tsab.pikapp.view.HomeV2Activity
import com.tsab.pikapp.view.LoginV2Activity
import com.tsab.pikapp.view.StoreActivity
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

val gson1 = Gson()
val type1 = object : TypeToken<BaseResponse>() {}.type

class SignupV2ThirdFragment : Fragment() {
    private var KTPImage: Uri? = null
    private var LogoImage: Uri? = null
    private var LatarImage: Uri? = null
    lateinit var email: String
    lateinit var name: String
    lateinit var phone: String
    lateinit var pin: String
    var bankName: String? = null
    var resto: String? = null
    var fcourt: String? = null
    var alamat1: String? = null
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
        alamat1 = requireArguments().getString("alamat")
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
        val getactionktp = registerForActivityResult(
                ActivityResultContracts.GetContent(),
                ActivityResultCallback {
                    uri -> ktp.setImageURI(uri)
                    KTPImage = uri
                }
        )

        val getactionlogo = registerForActivityResult(
                ActivityResultContracts.GetContent(),
                ActivityResultCallback {
                    uri -> logo.setImageURI(uri)
                    LogoImage = uri
                }
        )

        val getactionlatar = registerForActivityResult(
                ActivityResultContracts.GetContent(),
                ActivityResultCallback {
                    uri -> latar.setImageURI(uri)
                    LatarImage = uri
                }
        )

        ktp.setOnClickListener {
            getactionktp.launch("image/*")
        }

        logo.setOnClickListener {
            getactionlogo.launch("image/*")
        }

        latar.setOnClickListener {
            getactionlatar.launch("image/*")
        }

        btnNext.setOnClickListener {
            if(KTPImage == null){
                ktperror.setText("KTP Tidak Boleh Kosong")
                ktperror.setTextColor(Color.parseColor("#DC6A84"))
            }

            if(LogoImage == null){
                logoerror.setText("Logo Restoran Tidak Boleh Kosong")
                logoerror.setTextColor(Color.parseColor("#DC6A84"))
            }

            if(LatarImage == null){
                latarerror.setText("Gambar Latar Tidak Boleh Kosong")
                latarerror.setTextColor(Color.parseColor("#DC6A84"))
            }

            if(KTPImage != null && LogoImage != null && LatarImage != null){
                uploadData()
            }
        }

    }

    private fun uploadData() {
        val parcelFileDescriptor = requireActivity().contentResolver.openFileDescriptor(KTPImage!!, "r", null) ?: return
        val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
        val file = File(requireActivity().cacheDir, requireActivity().contentResolver.getFileName(KTPImage!!))
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)

        val parcelFileDescriptor1 = requireActivity().contentResolver.openFileDescriptor(LogoImage!!, "r", null) ?: return
        val inputStream1 = FileInputStream(parcelFileDescriptor1.fileDescriptor)
        val file1 = File(requireActivity().cacheDir, requireActivity().contentResolver.getFileName(LogoImage!!))
        val outputStream1 = FileOutputStream(file1)
        inputStream1.copyTo(outputStream1)

        val parcelFileDescriptor2 = requireActivity().contentResolver.openFileDescriptor(LatarImage!!, "r", null) ?: return
        val inputStream2 = FileInputStream(parcelFileDescriptor2.fileDescriptor)
        val file2 = File(requireActivity().cacheDir, requireActivity().contentResolver.getFileName(LatarImage!!))
        val outputStream2 = FileOutputStream(file2)
        inputStream2.copyTo(outputStream2)

        val branch: String = resto + " Branch"

        PikappApiService().api.uploadRegister(
                getUUID(), getClientID(), getTimestamp(),
                MultipartBody.Part.createFormData("file_01", file.name, RequestBody.create(MediaType.parse("multipart/form-data"), file)),
                MultipartBody.Part.createFormData("file_02", file1.name, RequestBody.create(MediaType.parse("multipart/form-data"), file1)),
                MultipartBody.Part.createFormData("file_03", file2.name, RequestBody.create(MediaType.parse("multipart/form-data"), file2)),
                RequestBody.create(MediaType.parse("multipart/form-data"), alamat1),
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
        ).enqueue(object: Callback<BaseResponse> {
            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
            }

            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if(response.code() == 200 && response.body()!!.errCode.toString() == "EC0000"){
                    val intent = Intent(activity?.baseContext, LoginV2Activity::class.java)
                    activity?.startActivity(intent)
                }else {
                    var errorResponse: BaseResponse? = gson1.fromJson(response.errorBody()!!.charStream(), type1)
                    Toast.makeText(context, generateResponseMessage(errorResponse?.errCode, errorResponse?.errMessage), Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signup_v2_third, container, false)
    }
}