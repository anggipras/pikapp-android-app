package com.tsab.pikapp.view.onboarding.login

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.tsab.pikapp.R
import kotlinx.android.synthetic.main.fragment_signup_v2_first.*
import kotlinx.android.synthetic.main.fragment_signup_v2_second.*

class SignupV2Second : Fragment(), View.OnClickListener {
    var navController: NavController? = null
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
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var bank: EditText = view.findViewById(R.id.bank)
        bank.isFocusable = false
        bank.isFocusableInTouchMode = false
        navController = Navigation.findNavController(view)
        if(bankName != null){
            bank.setText("$bankName")
            bank.setTextColor(Color.parseColor("#000000"))
        }
        if(resto != null){
            restaurant.setText("$resto")
            restaurant.setTextColor(Color.parseColor("#000000"))
        }
        if(fcourt != null){
            foodcourt.setText("$fcourt")
            foodcourt.setTextColor(Color.parseColor("#000000"))
        }
        if(alamat1 != null){
            alamat.setText("$alamat1")
            alamat.setTextColor(Color.parseColor("#000000"))
        }
        if(rekno != null){
            norek.setText("$rekno")
            norek.setTextColor(Color.parseColor("#000000"))
        }
        if(rekname != null){
            namarek.setText("$rekname")
            namarek.setTextColor(Color.parseColor("#000000"))
        }
        bank.setOnClickListener(this)
        view.findViewById<ImageButton>(R.id.btnNext).setOnClickListener(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signup_v2_second, container, false)
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.bank ->{
                val bundle = bundleOf("restaurant" to restaurant.text.toString(),
                        "foodcourt" to foodcourt.text.toString(), "alamat" to alamat.text.toString(),
                        "norek" to norek.text.toString(), "namarek" to namarek.text.toString(),  "email" to email,
                        "name" to name, "phone" to phone, "pin" to pin)
                navController!!.navigate(R.id.action_signupV2Second_to_bankNameFragment2, bundle)
            }
            R.id.btnNext -> {
                if(restaurant.text.isEmpty()){
                    restauranterror.setText("Nama Restoran Tidak Boleh Kosong")
                    restauranterror.setTextColor(Color.parseColor("#DC6A84"))
                }else if(restaurant.text.toString().trim().length <= 3){
                    restauranterror.setText("Nama Restoran Harus Lebih Dari 3 Karakter")
                    restauranterror.setTextColor(Color.parseColor("#DC6A84"))
                }

                if(alamat.text.isEmpty()){
                    alamaterror.setText("Alamat Tidak Boleh Kosong")
                    alamaterror.setTextColor(Color.parseColor("#DC6A84"))
                } else if(alamat.text.toString().trim().length <= 6){
                    alamaterror.setText("Alamat Harus Lebih Dari 6 Karakter")
                    alamaterror.setTextColor(Color.parseColor("#DC6A84"))
                }

                if(bank.text.isEmpty()){
                    bankerror.setText("Silahkan Pilih Bank Anda")
                    bankerror.setTextColor(Color.parseColor("#DC6A84"))
                }

                if(norek.text.isEmpty()){
                    norekerror.setText("Nomor Rekening Bank Tidak Boleh Kosong")
                    norekerror.setTextColor(Color.parseColor("#DC6A84"))
                }else if(norek.text.toString().trim().length <= 6){
                    norekerror.setText("Nomor Rekening Harus Lebih Dari 6 Karakter")
                    norekerror.setTextColor(Color.parseColor("#DC6A84"))
                }

                if(namarek.text.isEmpty()){
                    namarekerror.setText("Nama Pemilik Rekening Tidak Boleh Kosong")
                    namarekerror.setTextColor(Color.parseColor("#DC6A84"))
                }else if(namarek.text.toString().trim().length <= 3){
                    namarekerror.setText("Nama Pemilik Bank Harus Lebih Dari 3 Karakter")
                    namarekerror.setTextColor(Color.parseColor("#DC6A84"))
                }

                if(!restaurant.text.isEmpty() && !alamat.text.isEmpty() && !bank.text.isEmpty() && !norek.text.isEmpty() && !namarek.text.isEmpty()
                        && namarek.text.toString().trim().length > 3 && alamat.text.toString().trim().length > 6
                        && norek.text.toString().trim().length > 6 && restaurant.text.toString().trim().length > 3){
                    val bundle = bundleOf("restaurant" to restaurant.text.toString(),
                            "foodcourt" to foodcourt.text.toString(), "alamat" to alamat.text.toString(),
                            "norek" to norek.text.toString(), "namarek" to namarek.text.toString(),"bank" to bankName.toString(),
                            "email" to email, "name" to name, "phone" to phone, "pin" to pin)
                    navController!!.navigate(R.id.action_signupV2Second_to_signupV2ThirdFragment, bundle)}
                }
            }
    }
}
