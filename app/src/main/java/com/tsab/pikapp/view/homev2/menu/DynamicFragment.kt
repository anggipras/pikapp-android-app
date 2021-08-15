package com.tsab.pikapp.view.homev2.menu

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentDynamicBinding
import com.tsab.pikapp.view.menu.UpdateMenuActivity

class DynamicFragment : Fragment() {
    companion object {
        fun newInstance(): DynamicFragment {
            return DynamicFragment()
        }
    }

    private lateinit var dataBinding: FragmentDynamicBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_dynamic, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        attachInputListeners()
    }

    /**
     * Initialize empty state layout.
     */
    private fun attachInputListeners() {
        dataBinding.tambahMenuButton.setOnClickListener {
            Log.d("Dynamic Fragment", "Add button clicked!")
            Intent(activity?.baseContext, UpdateMenuActivity::class.java).apply {
                putExtra(UpdateMenuActivity.EXTRA_TYPE, UpdateMenuActivity.TYPE_ADD)
                activity?.startActivity(this)
            }
        }
    }
}