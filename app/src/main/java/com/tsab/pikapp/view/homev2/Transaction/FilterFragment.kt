package com.tsab.pikapp.view.homev2.Transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
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

    override fun onResume() {
        super.onResume()
        dialog!!.pikapp.isChecked = viewModel.pikappFilter.value!!
        dialog!!.tokopedia.isChecked = viewModel.tokpedFilter.value!!
        dialog!!.grab.isChecked = viewModel.grabFilter.value!!
        dialog!!.shopee.isChecked = viewModel.shopeeFilter.value!!
        dialog!!.btnNext.text = "Tampilkan " + viewModel.countTxn.value!!.toInt() + " Pesanan"
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

        var size: Int = viewModel.countTxn.value!!.toInt()

        btnNext.text = "Tampilkan " + size + " Pesanan"

        if (!tokopedia.isChecked && !grab.isChecked && !shopee.isChecked && !pikapp.isChecked) {
            size = viewModel.proses.value!!.toInt() + viewModel.prosesOmni.value!!.toInt()
            btnNext.text = "Tampilkan " + size.toString() + " Pesanan"
        }

        pikapp.setOnCheckedChangeListener { buttonView, isChecked ->
            if (pikapp.isChecked) {
                if (!tokopedia.isChecked && !grab.isChecked && !shopee.isChecked) {

                    size = viewModel.proses.value!!.toInt()
                    btnNext.text = "Tampilkan " + size + " Pesanan"
                } else {

                    size = size + viewModel.proses.value!!.toInt()
                    btnNext.text = "Tampilkan " + size + " Pesanan"
                }
            } else if (!pikapp.isChecked) {
                if (!tokopedia.isChecked && !grab.isChecked && !shopee.isChecked) {

                    size = viewModel.proses.value!!.toInt() + viewModel.prosesOmni.value!!.toInt()
                    btnNext.text = "Tampilkan " + size + " Pesanan"
                } else {

                    size = size - viewModel.proses.value!!.toInt()
                    btnNext.text = "Tampilkan " + size + " Pesanan"
                }
            }
        }

        tokopedia.setOnCheckedChangeListener { buttonView, isChecked ->
            if (tokopedia.isChecked) {
                if (!pikapp.isChecked && !grab.isChecked && !shopee.isChecked) {

                    size = viewModel.prosesOmni.value!!.toInt()
                    btnNext.text = "Tampilkan " + size + " Pesanan"
                } else {

                    size = size + viewModel.prosesOmni.value!!.toInt()
                    btnNext.text = "Tampilkan " + size + " Pesanan"
                }
            } else if (!tokopedia.isChecked) {
                if (!pikapp.isChecked && !grab.isChecked && !shopee.isChecked) {

                    size = viewModel.proses.value!!.toInt() + viewModel.prosesOmni.value!!.toInt()
                    btnNext.text = "Tampilkan " + size + " Pesanan"
                } else {

                    size = size - viewModel.prosesOmni.value!!.toInt()
                    btnNext.text = "Tampilkan " + size + " Pesanan"
                }
            }
        }

        grab.setOnCheckedChangeListener { buttonView, isChecked ->
            if (grab.isChecked) {
                if (!tokopedia.isChecked && !pikapp.isChecked && !shopee.isChecked) {

                    size = 0
                    btnNext.text = "Tampilkan " + size + " Pesanan"
                } else {

                    size += 0
                    btnNext.text = "Tampilkan " + size + " Pesanan"
                }
            } else if (!grab.isChecked) {
                if (!tokopedia.isChecked && !pikapp.isChecked && !shopee.isChecked) {

                    size = viewModel.proses.value!!.toInt() + viewModel.prosesOmni.value!!.toInt()
                    btnNext.text = "Tampilkan " + size + " Pesanan"
                } else {

                }
            }
        }

        shopee.setOnCheckedChangeListener { buttonView, isChecked ->
            if (shopee.isChecked) {
                if (!tokopedia.isChecked && !grab.isChecked && !pikapp.isChecked) {

                    size = 0
                    btnNext.text = "Tampilkan " + size + " Pesanan"
                } else {

                    size += 0
                    btnNext.text = "Tampilkan " + size + " Pesanan"
                }
            } else if (!shopee.isChecked) {
                if (!tokopedia.isChecked && !grab.isChecked && !pikapp.isChecked) {

                    size = viewModel.proses.value!!.toInt() + viewModel.prosesOmni.value!!.toInt()
                    btnNext.text = "Tampilkan " + size + " Pesanan"
                } else {

                }
            }
        }
        btnNext.setOnClickListener {
            viewModel.filterOn(
                pikapp.isChecked,
                tokopedia.isChecked,
                grab.isChecked,
                shopee.isChecked,
                size
            )
            dismiss()
        }
    }
}