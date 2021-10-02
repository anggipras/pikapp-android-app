package com.tsab.pikapp.view.homev2.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.tsab.pikapp.R
import com.tsab.pikapp.util.SessionManager
import com.tsab.pikapp.viewmodel.homev2.PromoViewModel

class PromoFragment : Fragment() {
    private val sessionManager = SessionManager()

    companion object {
        fun newInstance() = PromoFragment()
    }

    private lateinit var viewModel: PromoViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.promo_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PromoViewModel::class.java)
        sessionManager.setHomeNav(2)
    }

}