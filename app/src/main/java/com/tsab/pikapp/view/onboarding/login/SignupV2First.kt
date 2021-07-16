package com.tsab.pikapp.view.onboarding.login

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.tsab.pikapp.R
import kotlinx.android.synthetic.main.fragment_login_v2_first.*
import kotlinx.android.synthetic.main.fragment_signup_v2_first.*
import kotlinx.android.synthetic.main.fragment_signup_v2_first.emailerror

class SignupV2First : Fragment(), View.OnClickListener {
    var navController: NavController? = null

    var emailcheck: Boolean = false
    var namacheck: Boolean = false
    var phonecheck: Boolean = false
    var pwordcheck: Boolean = false


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        view.findViewById<ImageButton>(R.id.btnNext).setOnClickListener(this)
    }

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
        return inflater.inflate(R.layout.fragment_signup_v2_first, container, false)
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.btnNext -> {
                if(email.text.isEmpty()){
                    emailerror.setText("Email Tidak Boleh Kosong")
                    emailerror.setTextColor(Color.parseColor("#DC6A84"))
                    emailcheck = false
                }else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()){
                    emailerror.setText("Silahkan Masukkan Email Anda Yang Valid")
                    emailerror.setTextColor(Color.parseColor("#DC6A84"))
                    emailcheck = false
                }else{
                    emailerror.setTextColor(Color.parseColor("#FFFFFF"))
                    emailcheck = true
                }

                if(nama.text.isEmpty()){
                    namaerror.setText("Nama Lengkap Tidak Boleh Kosong")
                    namaerror.setTextColor(Color.parseColor("#DC6A84"))
                    namacheck = false
                }else if(nama.text.toString().trim().length <= 3){
                    namaerror.setText("Nama Lengkap Harus Lebih Dari 3 Karakter")
                    namaerror.setTextColor(Color.parseColor("#DC6A84"))
                    namacheck = false
                }else{
                    namaerror.setTextColor(Color.parseColor("#FFFFFF"))
                    namacheck = true
                }

                if(telp.text.isEmpty()){
                    phoneerror.setText("Nomor Telepon Tidak Boleh Kosong")
                    phoneerror.setTextColor(Color.parseColor("#DC6A84"))
                    phonecheck = false
                }else if(telp.text.toString().trim().length <= 8){
                    phoneerror.setText("Nomor Telepon Harus Lebih Dari 8 Digit Angka")
                    phoneerror.setTextColor(Color.parseColor("#DC6A84"))
                    phonecheck = false
                }else{
                    phoneerror.setTextColor(Color.parseColor("#FFFFFF"))
                    phonecheck = true
                }

                if(pass.text.isEmpty()){
                    pworderror.setText("Password Tidak Boleh Kosong")
                    pworderror.setTextColor(Color.parseColor("#DC6A84"))
                    pwordcheck = false
                }else if(pass.text.toString().trim().length < 6){
                    pworderror.setText("Pin Harus Terdiri Dari 6 Digit")
                    pworderror.setTextColor(Color.parseColor("#DC6A84"))
                    pwordcheck = false
                }else if(pass.text.length == 6){
                    var firstPin: Char = pass.text[0]
                    var secondPin: Char = pass.text[1]
                    var thirdPin: Char = pass.text[2]
                    var fourthPin: Char = pass.text[3]
                    var fifthPin: Char = pass.text[4]
                    var sixthPin: Char = pass.text[5]
                    if(firstPin != secondPin && firstPin != thirdPin && firstPin != fourthPin &&
                            firstPin != fifthPin && firstPin != sixthPin && secondPin != thirdPin && secondPin != fourthPin
                            && secondPin != fifthPin && secondPin != sixthPin && thirdPin != fourthPin && thirdPin != fifthPin
                            && thirdPin != sixthPin && fourthPin != fifthPin && fourthPin != sixthPin && fifthPin != sixthPin){
                        pworderror.setText("Pin Harus Memiliki Angka Yang Berulang")
                        pworderror.setTextColor(Color.parseColor("#DC6A84"))
                        pwordcheck = false
                    }else{
                        pworderror.setTextColor(Color.parseColor("#FFFFFF"))
                        pwordcheck = true
                    }
                }

                if(emailcheck && namacheck && phonecheck && pwordcheck){
                    val bundle = bundleOf("email" to email.text.toString(),
                    "name" to nama.text.toString(), "phone" to telp.text.toString(),
                    "pin" to pass.text.toString())
                    navController!!.navigate(R.id.action_signupV2First_to_signupV2Second, bundle)
                }
            }
        }
    }
}