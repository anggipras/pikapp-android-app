package com.tsab.pikapp.view.homev2.Transaction

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_filter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        closeBtn.setOnClickListener {
            dismiss()
        }
        var size: Int = viewModel.proses.value!!.toInt() + viewModel.prosesOmni.value!!.toInt()

        btnNext.setText("Tampilkan " + size + " Pesanan")

        if (!tokopedia.isChecked && !grab.isChecked && !shopee.isChecked && !pikapp.isChecked){
            btnNext.setText("Tampilkan " + size.toString() + " Pesanan")
        }

        pikapp.setOnCheckedChangeListener { buttonView, isChecked ->
            if(pikapp.isChecked){
                if (!tokopedia.isChecked && !grab.isChecked && !shopee.isChecked) {

                    size = viewModel.proses.value!!.toInt()
                    btnNext.setText("Tampilkan " + size + " Pesanan")
                } else {

                    size = size + viewModel.proses.value!!.toInt()
                    btnNext.setText("Tampilkan " + size + " Pesanan")
                }
            }else if(!pikapp.isChecked){
                if (!tokopedia.isChecked && !grab.isChecked && !shopee.isChecked) {

                    size = viewModel.proses.value!!.toInt() + viewModel.prosesOmni.value!!.toInt()
                    btnNext.setText("Tampilkan " + size + " Pesanan")
                } else {

                    size = size - viewModel.proses.value!!.toInt()
                    btnNext.setText("Tampilkan " + size + " Pesanan")
                }
            }
        }

        tokopedia.setOnCheckedChangeListener { buttonView, isChecked ->
            if(tokopedia.isChecked){
                if (!pikapp.isChecked && !grab.isChecked && !shopee.isChecked) {

                    size = viewModel.prosesOmni.value!!.toInt()
                    btnNext.setText("Tampilkan " + size + " Pesanan")
                } else {

                    size = size + viewModel.prosesOmni.value!!.toInt()
                    btnNext.setText("Tampilkan " + size + " Pesanan")
                }
            }else if(!tokopedia.isChecked){
                if (!pikapp.isChecked && !grab.isChecked && !shopee.isChecked) {

                    size = viewModel.proses.value!!.toInt() + viewModel.prosesOmni.value!!.toInt()
                    btnNext.setText("Tampilkan " + size + " Pesanan")
                } else {

                    size = size - viewModel.prosesOmni.value!!.toInt()
                    btnNext.setText("Tampilkan " + size + " Pesanan")
                }
            }
        }

        grab.setOnCheckedChangeListener { buttonView, isChecked ->
            if(grab.isChecked){
                if (!tokopedia.isChecked && !pikapp.isChecked && !shopee.isChecked) {

                    size = 0
                    btnNext.setText("Tampilkan " + size + " Pesanan")
                } else {

                    size += 0
                    btnNext.setText("Tampilkan " + size + " Pesanan")
                }
            }else if(!grab.isChecked){
                if (!tokopedia.isChecked && !pikapp.isChecked && !shopee.isChecked) {

                    size = viewModel.proses.value!!.toInt() + viewModel.prosesOmni.value!!.toInt()
                    btnNext.setText("Tampilkan " + size + " Pesanan")
                } else {

                }
            }
        }

        shopee.setOnCheckedChangeListener { buttonView, isChecked ->
            if (shopee.isChecked) {
                if (!tokopedia.isChecked && !grab.isChecked && !pikapp.isChecked) {

                    size = 0
                    btnNext.setText("Tampilkan " + size + " Pesanan")
                } else {

                    size += 0
                    btnNext.setText("Tampilkan " + size + " Pesanan")
                }
            } else if (!shopee.isChecked) {
                if (!tokopedia.isChecked && !grab.isChecked && !pikapp.isChecked) {

                    size = viewModel.proses.value!!.toInt() + viewModel.prosesOmni.value!!.toInt()
                    btnNext.setText("Tampilkan " + size + " Pesanan")
                } else {

                }
            }
        }
        btnNext.setOnClickListener {
            viewModel.filterOn(pikapp.isChecked, tokopedia.isChecked, grab.isChecked, shopee.isChecked, size)
            dismiss()
        }
    }
}