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

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
var navController: NavController? = null
/**
 * A simple [Fragment] subclass.
 * Use the [LoginV2First.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginV2First : Fragment(), View.OnClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login_v2_first, container, false)

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LoginV2First.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoginV2First().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
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