package com.tsab.pikapp.view.onboarding.login

import android.app.Dialog
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
import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialog
import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialogFragment
import com.tsab.pikapp.R
import kotlinx.android.synthetic.main.fragment_bank_name.*
import kotlinx.android.synthetic.main.fragment_login_v2_first.*
import kotlinx.android.synthetic.main.fragment_signup_v2_second.*

class BankNameFragment : RoundedBottomSheetDialogFragment(), View.OnClickListener{
    var navController: NavController? = null
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var resto: String? = null
    var fcourt: String? = null
    var address: String? = null
    var rekno: String? = null
    var rekname: String? = null
    lateinit var email: String
    lateinit var fullName: String
    lateinit var phone: String
    lateinit var pin: String
    var name: String = "BANK CENTRAL ASIA (BCA)"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resto = requireArguments().getString("restaurant")
        fcourt = requireArguments().getString("foodcourt")
        address = requireArguments().getString("alamat")
        rekno = requireArguments().getString("norek")
        rekname = requireArguments().getString("namarek")
        email = requireArguments().getString("email").toString()
        fullName = requireArguments().getString("name").toString()
        phone = requireArguments().getString("phone").toString()
        pin = requireArguments().getString("pin").toString()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = parentFragment?.view?.let { Navigation.findNavController(it) }
        view.findViewById<ImageView>(R.id.closeBtn).setOnClickListener(this)
        view.findViewById<ImageButton>(R.id.btnoke).setOnClickListener(this)

        bankname.setOnCheckedChangeListener{group, checkedId ->
            if(checkedId == R.id.BCA){
                name = BCA.text.toString()
            }

            if(checkedId == R.id.BNI){
                name = BNI.text.toString()
            }

            if(checkedId == R.id.BRI){
                name = BRI.text.toString()
            }

            if(checkedId == R.id.Mandiri){
                name = Mandiri.text.toString()
            }

            if(checkedId == R.id.CIMB){
                name = CIMB.text.toString()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bank_name, container, false)
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.closeBtn -> navController!!.navigate(R.id.action_bankNameFragment_to_signupV2Second)
            R.id.btnoke -> {
                val bundle = bundleOf("bank" to name, "restaurant" to resto,
                        "foodcourt" to fcourt, "alamat" to address,
                        "norek" to rekno, "namarek" to rekname, "name" to fullName, "phone" to phone, "pin" to pin,
                "email" to email)
                navController!!.navigate(R.id.action_bankNameFragment_to_signupV2Second, bundle)}
        }
    }

}