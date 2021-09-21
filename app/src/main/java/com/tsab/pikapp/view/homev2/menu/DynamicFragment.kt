package com.tsab.pikapp.view.homev2.menu

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentDynamicBinding
import com.tsab.pikapp.models.model.SearchList
import com.tsab.pikapp.view.AdvanceMenuActivity
import com.tsab.pikapp.viewmodel.homev2.DynamicViewModel
import kotlinx.android.synthetic.main.fragment_dynamic.*
import java.io.Serializable

class DynamicFragment : Fragment(), DynamicListAdapter.OnItemClickListener {
    companion object {
        fun newInstance(): DynamicFragment {
            return DynamicFragment()
        }
    }

    private lateinit var dataBinding: FragmentDynamicBinding
    lateinit var linearLayoutManager: LinearLayoutManager
    private val viewModel: DynamicViewModel by activityViewModels()

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

        linearLayoutManager = LinearLayoutManager(requireView().context)
        dataBinding.listMenuDetail.layoutManager = linearLayoutManager
        val nama: String = arguments?.getString("position").toString()
        activity?.let { viewModel.getAmountOfMenu(it.baseContext, dataBinding.listMenuDetail, dataBinding.imageView17,
                dataBinding.textview, nama, dataBinding.tambahMenuEmptyButton, dataBinding.tambahMenuButton, this) }

        viewModel.isLoading.observe(viewLifecycleOwner, Observer {
            dataBinding.shimmerFrameLayoutMenu.visibility = if (it) View.VISIBLE else View.GONE
        })
    }

    /**
     * Initialize empty state layout.
     */
    private fun attachInputListeners() {
        dataBinding.tambahMenuEmptyButton.setOnClickListener {
            Intent(activity?.baseContext, AdvanceMenuActivity::class.java).apply {
                putExtra(AdvanceMenuActivity.EXTRA_TYPE, AdvanceMenuActivity.TYPE_ADD)
                startActivity(this)
            }
        }

        dataBinding.tambahMenuButton.setOnClickListener {
            Intent(activity?.baseContext, AdvanceMenuActivity::class.java).apply {
                putExtra(AdvanceMenuActivity.EXTRA_TYPE, AdvanceMenuActivity.TYPE_ADD)
                startActivity(this)
            }
        }
    }

    override fun onItemClick(position: Int, menuList: SearchList) {
        viewModel.dynamicAdapter.notifyItemChanged(position)
        Intent(activity?.baseContext, AdvanceMenuActivity::class.java).apply {
            putExtra(AdvanceMenuActivity.EXTRA_TYPE, AdvanceMenuActivity.TYPE_EDIT)
            putExtra(AdvanceMenuActivity.MENU_LIST, menuList as Serializable)
            startActivity(this)
        }
    }
}