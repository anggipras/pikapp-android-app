package com.tsab.pikapp.view.onboarding.login

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.tsab.pikapp.R
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
        var bank: EditText = view.findViewById(R.id.namaBankInputText)
        bank.isFocusable = false
        bank.isFocusableInTouchMode = false
        navController = Navigation.findNavController(view)
        if (bankName != null) {
            bank.setText("$bankName")
            bank.setTextColor(Color.parseColor("#000000"))
        }
        if (resto != null) {
            namaRestoranInputText.setText("$resto")
            namaRestoranInputText.setTextColor(Color.parseColor("#000000"))
        }
        if (fcourt != null) {
            namaFoodcourtInputText.setText("$fcourt")
            namaFoodcourtInputText.setTextColor(Color.parseColor("#000000"))
        }
        if (alamat1 != null) {
            alamatInputText.setText("$alamat1")
            alamatInputText.setTextColor(Color.parseColor("#000000"))
        }
        if (rekno != null) {
            nomorRekeningInputText.setText("$rekno")
            nomorRekeningInputText.setTextColor(Color.parseColor("#000000"))
        }
        if (rekname != null) {
            namaRekeningInputText.setText("$rekname")
            namaRekeningInputText.setTextColor(Color.parseColor("#000000"))
        }
        bank.setOnClickListener(this)
        view.findViewById<Button>(R.id.nextButton).setOnClickListener(this)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_signup_v2_second, container, false)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.namaBankInputText -> {
                val bundle = bundleOf("restaurant" to namaRestoranInputText.text.toString(),
                        "foodcourt" to namaFoodcourtInputText.text.toString(),
                        "alamat" to alamatInputText.text.toString(),
                        "norek" to nomorRekeningInputText.text.toString(),
                        "namarek" to namaRekeningInputText.text.toString(), "email" to email,
                        "name" to name, "phone" to phone, "pin" to pin)
                navController!!.navigate(R.id.action_signupV2Second_to_bankNameFragment2, bundle)
            }
            R.id.nextButton -> {
                if (namaRestoranInputText.text.isEmpty()) {
                    namaRestoranErrorText.text = "Nama Restoran Tidak Boleh Kosong"
                    namaRestoranErrorText.setTextColor(Color.parseColor("#DC6A84"))
                } else if (namaRestoranInputText.text.toString().trim().length <= 3) {
                    namaRestoranErrorText.text = "Nama Restoran Harus Lebih Dari 3 Karakter"
                    namaRestoranErrorText.setTextColor(Color.parseColor("#DC6A84"))
                }

                if (alamatInputText.text.isEmpty()) {
                    alamatErrorText.text = "Alamat Tidak Boleh Kosong"
                    alamatErrorText.setTextColor(Color.parseColor("#DC6A84"))
                } else if (alamatInputText.text.toString().trim().length <= 6) {
                    alamatErrorText.text = "Alamat Harus Lebih Dari 6 Karakter"
                    alamatErrorText.setTextColor(Color.parseColor("#DC6A84"))
                }

                if (namaBankInputText.text.isEmpty()) {
                    namaBankErrorText.text = "Silahkan Pilih Bank Anda"
                    namaBankErrorText.setTextColor(Color.parseColor("#DC6A84"))
                }

                if (nomorRekeningInputText.text.isEmpty()) {
                    nomorRekeningErrorText.text = "Nomor Rekening Bank Tidak Boleh Kosong"
                    nomorRekeningErrorText.setTextColor(Color.parseColor("#DC6A84"))
                } else if (nomorRekeningInputText.text.toString().trim().length <= 6) {
                    nomorRekeningErrorText.text = "Nomor Rekening Harus Lebih Dari 6 Karakter"
                    nomorRekeningErrorText.setTextColor(Color.parseColor("#DC6A84"))
                }

                if (namaRekeningInputText.text.isEmpty()) {
                    namaRekeningErrorText.text = "Nama Pemilik Rekening Tidak Boleh Kosong"
                    namaRekeningErrorText.setTextColor(Color.parseColor("#DC6A84"))
                } else if (namaRekeningInputText.text.toString().trim().length <= 3) {
                    namaRekeningErrorText.text = "Nama Pemilik Bank Harus Lebih Dari 3 Karakter"
                    namaRekeningErrorText.setTextColor(Color.parseColor("#DC6A84"))
                }

                if (namaRestoranInputText.text.isNotEmpty()
                        && alamatInputText.text.isNotEmpty()
                        && namaBankInputText.text.isNotEmpty()
                        && nomorRekeningInputText.text.isNotEmpty()
                        && namaRekeningInputText.text.isNotEmpty()
                        && namaRekeningInputText.text.toString()
                                .trim().length > 3 && alamatInputText.text.toString()
                                .trim().length > 6
                        && nomorRekeningInputText.text.toString()
                                .trim().length > 6 && namaRestoranInputText.text.toString()
                                .trim().length > 3) {
                    val bundle = bundleOf(
                            "restaurant" to namaRestoranInputText.text.toString(),
                            "foodcourt" to namaFoodcourtInputText.text.toString(),
                            "alamat" to alamatInputText.text.toString(),
                            "norek" to nomorRekeningInputText.text.toString(),
                            "namarek" to namaRekeningInputText.text.toString(),
                            "bank" to bankName.toString(),
                            "email" to email, "name" to name, "phone" to phone, "pin" to pin)
                    navController!!.navigate(R.id.action_signupV2Second_to_signupV2ThirdFragment,
                            bundle)
                }
            }
        }
    }
}
