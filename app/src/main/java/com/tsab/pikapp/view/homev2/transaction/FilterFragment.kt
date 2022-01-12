package com.tsab.pikapp.view.homev2.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialogFragment
import com.tsab.pikapp.R
import com.tsab.pikapp.viewmodel.homev2.TransactionViewModel
import kotlinx.android.synthetic.main.fragment_filter.*

class FilterFragment : RoundedBottomSheetDialogFragment() {
    private val viewModel: TransactionViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_filter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        closeBtn.setOnClickListener {
            dismiss()
        }

        pikapp.isChecked = viewModel.pikappFilter.value!!
        tokopedia.isChecked = viewModel.tokpedFilter.value!!
        grab.isChecked = viewModel.grabFilter.value!!
        shopee.isChecked = viewModel.shopeeFilter.value!!
        whatsapp.isChecked = viewModel.whatsappFilter.value!!
        telepon.isChecked = viewModel.telpFilter.value!!

        observeViewModel()

        pikapp.setOnCheckedChangeListener { buttonView, isChecked ->
            if (pikapp.isChecked) {
                viewModel.mutablePikappFilter.value = true
                viewModel.filterOnBottomSheet("TXN", "TXN", true)
            } else if (!pikapp.isChecked) {
                viewModel.mutablePikappFilter.value = false
                viewModel.filterOnBottomSheet("TXN", "TXN", false)
            }
        }

        tokopedia.setOnCheckedChangeListener { buttonView, isChecked ->
            if (tokopedia.isChecked) {
                viewModel.mutableTokpedFilter.value = true
                viewModel.filterOnBottomSheet("CHANNEL", "TOKOPEDIA", true)
            } else if (!tokopedia.isChecked) {
                viewModel.mutableTokpedFilter.value = false
                viewModel.filterOnBottomSheet("CHANNEL", "TOKOPEDIA", false)
            }
        }

        grab.setOnCheckedChangeListener { buttonView, isChecked ->
            if (grab.isChecked) {
                viewModel.mutableGrabFilter.value = true
                viewModel.filterOnBottomSheet("CHANNEL", "GRAB", true)
            } else if (!grab.isChecked) {
                viewModel.mutableGrabFilter.value = false
                viewModel.filterOnBottomSheet("CHANNEL", "GRAB", false)
            }
        }

        shopee.setOnCheckedChangeListener { buttonView, isChecked ->
            if (shopee.isChecked) {
                viewModel.mutableShopeeFilter.value = true
                viewModel.filterOnBottomSheet("POS", "INSTAGRAM", true)
            } else if (!shopee.isChecked) {
                viewModel.mutableShopeeFilter.value = false
                viewModel.filterOnBottomSheet("POS", "INSTAGRAM", false)
            }
        }

        whatsapp.setOnCheckedChangeListener { _, _ ->
            if (whatsapp.isChecked) {
                viewModel.mutableWhatsappFilter.value = true
                viewModel.filterOnBottomSheet("POS", "WHATSAPP", true)
            } else if (!shopee.isChecked) {
                viewModel.mutableWhatsappFilter.value = false
                viewModel.filterOnBottomSheet("POS", "WHATSAPP", false)
            }
        }

        telepon.setOnCheckedChangeListener { _, _ ->
            if (telepon.isChecked) {
                viewModel.mutableTelpFilter.value = true
                viewModel.filterOnBottomSheet("POS", "PHONE_CALL", true)
            } else if (!shopee.isChecked) {
                viewModel.mutableTelpFilter.value = false
                viewModel.filterOnBottomSheet("POS", "PHONE_CALL", false)
            }
        }

        btnNext.setOnClickListener {
            if (!viewModel.pikappFilter.value!!
                && !viewModel.tokpedFilter.value!!
                && !viewModel.grabFilter.value!!
                && !viewModel.shopeeFilter.value!!
                && !viewModel.whatsappFilter.value!!
                && !viewModel.telpFilter.value!!) {
                viewModel.setAllFiterColor(false)
            } else {
                viewModel.setAllFiterColor(true)
            }
            viewModel.filterTransactionV2ListProcess()
            dismiss()
        }
    }

    override fun onResume() {
        super.onResume()
        dialog!!.pikapp.isChecked = viewModel.pikappFilter.value!!
        dialog!!.tokopedia.isChecked = viewModel.tokpedFilter.value!!
        dialog!!.grab.isChecked = viewModel.grabFilter.value!!
        dialog!!.shopee.isChecked = viewModel.shopeeFilter.value!!
        dialog!!.whatsapp.isChecked = viewModel.whatsappFilter.value!!
        dialog!!.telepon.isChecked = viewModel.telpFilter.value!!
    }

    private fun observeViewModel() {
        viewModel.sizeFilter.observe(viewLifecycleOwner, Observer {
            if (it == 0) {
                btnNext.text = "Tampilkan " + viewModel.getLiveDataTransListV2ProcessObserver().value?.size + " Pesanan"
            } else {
                btnNext.text = "Tampilkan " + it + " Pesanan"
            }
        })
    }
}