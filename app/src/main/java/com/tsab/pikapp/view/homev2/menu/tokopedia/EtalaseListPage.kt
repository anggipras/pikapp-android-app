package com.tsab.pikapp.view.homev2.menu.tokopedia

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentEtalaseListPageBinding
import com.tsab.pikapp.util.SessionManager
import com.tsab.pikapp.util.setAllOnClickListener
import com.tsab.pikapp.view.homev2.HomeActivity
import com.tsab.pikapp.viewmodel.homev2.EtalaseViewModel

class EtalaseListPage : Fragment() {

    private var navController: NavController? = null
    private lateinit var dataBinding: FragmentEtalaseListPageBinding
    private val sessionManager = SessionManager()
    private val viewModel: EtalaseViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        dataBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_etalase_list_page,
            container, false
        )
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    Intent(activity?.baseContext, HomeActivity::class.java).apply {
                        startActivity(this)
                    }
                }
            })

        navController = Navigation.findNavController(view)
        sessionManager.setHomeNav(1)

        observeViewModel()
        attachInputListeners()
    }

    private fun observeViewModel(){
        /*viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            dataBinding.loadingOverlay.loadingView.visibility =
                if (isLoading) View.VISIBLE else View.GONE
        })*/

    }

    private fun attachInputListeners(){
        dataBinding.headerLayout.backButton.setOnClickListener {
            Intent(activity?.baseContext, HomeActivity::class.java).apply {
                startActivity(this)
            }
        }
        dataBinding.tambahEtalaseGroup.setAllOnClickListener(View.OnClickListener {
            navController?.navigate(R.id.action_etalaseListPage_to_addEtalasePage)
        }, view)

        dataBinding.daftarEtalaseChangeOrderButton.setOnClickListener {
            navController?.navigate(R.id.action_etalaseListPage_to_editEtalasePage)
        }

    }
}