package com.tsab.pikapp.view.onboarding.login

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.goodiebag.pinview.Pinview
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tsab.pikapp.R
import com.tsab.pikapp.models.model.LoginRequestV2
import com.tsab.pikapp.models.model.LoginResponseV2
import com.tsab.pikapp.models.network.PikappApi
import com.tsab.pikapp.models.network.PikappApiService
import com.tsab.pikapp.util.SharedPreferencesUtil
import com.tsab.pikapp.util.getClientID
import com.tsab.pikapp.util.getTimestamp
import com.tsab.pikapp.util.getUUID
import com.tsab.pikapp.view.HomeV2Activity
import kotlinx.android.synthetic.main.fragment_login_v2_second.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
val gson = Gson()
val type = object : TypeToken<LoginResponseV2>() {}.type
/**
 * A simple [Fragment] subclass.
 * Use the [LoginV2Second.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginV2Second : Fragment() {
    // TODO: Rename and change types of parameters
    lateinit var email: String
    lateinit var fcm: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        email = requireArguments().getString("email").toString()
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            fcm = token.toString()
        })

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postApi()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view:View = inflater.inflate(R.layout.fragment_login_v2_second, container, false)

        return view
    }

    private fun postApi() {
        logPin.setPinViewEventListener { pinview, fromUser ->
            val phone = "$email"
            val pin = logPin.value.toString()
            val token = fcm

            PikappApiService().loginMerchant(phone, pin, token).enqueue(object : Callback<LoginResponseV2>{
                override fun onResponse(
                    call: Call<LoginResponseV2>,
                    response: Response<LoginResponseV2>
                ) {
                    if(response.code() == 200 && response.body()!!.errCode.toString() == "EC0000"){
                        Toast.makeText(activity, "login successful", Toast.LENGTH_SHORT).show()
                        val intent = Intent(activity?.baseContext, HomeV2Activity::class.java)
                        activity?.startActivity(intent)
                    } else {
                        var errorResponse: LoginResponseV2? = gson.fromJson(response.errorBody()!!.charStream(), type)
                        Toast.makeText(activity, errorResponse?.errMessage.toString(), Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponseV2>, t: Throwable) {
                    TODO("Not yet implemented")
                }

            })
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LoginV2Second.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoginV2Second().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}