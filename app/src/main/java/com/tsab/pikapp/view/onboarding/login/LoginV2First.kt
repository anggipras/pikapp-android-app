package com.tsab.pikapp.view.onboarding.login

import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.tsab.pikapp.R
import kotlinx.android.synthetic.main.fragment_login_v2_first.*

var navController: NavController? = null

class LoginV2First : Fragment(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login_v2_first, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        view.findViewById<ImageView>(R.id.nextBtn).setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.nextBtn -> {
                if(logPhone.text.isEmpty()){
                    emailerror.setText("Nomor Telepon Tidak Boleh Kosong")
                    emailerror.setTextColor(Color.parseColor("#DC6A84"))
                }else if(logPhone.text.toString().trim().length <= 8){
                    emailerror.setText("Nomor Telepon harus lebih dari 8 digit")
                    emailerror.setTextColor(Color.parseColor("#DC6A84"))
                }else{
                    val bundle = bundleOf("email" to logPhone.text.toString())
                    navController!!.navigate(R.id.action_loginV2First_to_loginV2Second, bundle)
                }
            }

        }
    }
}