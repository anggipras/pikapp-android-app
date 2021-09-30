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

        var listALL = requireArguments().getStringArrayList("allList")
        var filterALL = arguments?.getStringArrayList("filterList")
        var listFix : ArrayList<String> = ArrayList()

        if (!tokopedia.isChecked && !grab.isChecked && !shopee.isChecked && !pikapp.isChecked){
            if (listALL != null) {
                btnNext.setText("Tampilkan " + listALL.size.toString() + " Pesanan")
            }
        }

        pikapp.setOnCheckedChangeListener { buttonView, isChecked ->
            if(pikapp.isChecked){
                if (!tokopedia.isChecked && !grab.isChecked && !shopee.isChecked) {
                    listFix.clear()
                    if (listALL != null) {
                        for(list in listALL){
                            if(list == "pikapp"){
                                listFix.add(list)
                            }
                        }
                    }
                    btnNext.setText("Tampilkan " + listFix.size.toString() + " Pesanan")
                } else {
                    if (listALL != null) {
                        for(list in listALL){
                            if(list == "pikapp"){
                                listFix.add(list)
                            }
                        }
                    }
                    btnNext.setText("Tampilkan " + listFix.size.toString() + " Pesanan")
                }
            }else if(!pikapp.isChecked){
                if (!tokopedia.isChecked && !grab.isChecked && !shopee.isChecked) {
                    listFix.clear()
                    if (listALL != null) {
                        for (list in listALL){
                            listFix.add(list)
                        }
                    }
                    btnNext.setText("Tampilkan " + listFix.size.toString() + " Pesanan")
                } else {
                    if (listALL != null) {
                        for(list in listALL){
                            if(list == "pikapp"){
                                listFix.remove(list)
                            }
                        }
                    }
                    btnNext.setText("Tampilkan " + listFix.size.toString() + " Pesanan")
                }
            }
        }

        tokopedia.setOnCheckedChangeListener { buttonView, isChecked ->
            if(tokopedia.isChecked){
                if (!pikapp.isChecked && !grab.isChecked && !shopee.isChecked) {
                    listFix.clear()
                    if (listALL != null) {
                        for(list in listALL){
                            if(list == "tokopedia"){
                                listFix.add(list)
                            }
                        }
                    }
                    btnNext.setText("Tampilkan " + listFix.size.toString() + " Pesanan")
                } else {
                    if (listALL != null) {
                        for(list in listALL){
                            if(list == "tokopedia"){
                                listFix.add(list)
                            }
                        }
                    }
                    btnNext.setText("Tampilkan " + listFix.size.toString() + " Pesanan")
                }
            }else if(!tokopedia.isChecked){
                if (!pikapp.isChecked && !grab.isChecked && !shopee.isChecked) {
                    listFix.clear()
                    if (listALL != null) {
                        for (list in listALL){
                            listFix.add(list)
                        }
                    }
                    btnNext.setText("Tampilkan " + listFix.size.toString() + " Pesanan")
                } else {
                    if (listALL != null) {
                        for(list in listALL){
                            if(list == "tokopedia"){
                                listFix.remove(list)
                            }
                        }
                    }
                    btnNext.setText("Tampilkan " + listFix.size.toString() + " Pesanan")
                }
            }
        }

        grab.setOnCheckedChangeListener { buttonView, isChecked ->
            if(grab.isChecked){
                if (!tokopedia.isChecked && !pikapp.isChecked && !shopee.isChecked) {
                    listFix.clear()
                    if (listALL != null) {
                        for(list in listALL){
                            if(list == "grab"){
                                listFix.add(list)
                            }
                        }
                    }
                    btnNext.setText("Tampilkan " + listFix.size.toString() + " Pesanan")
                } else {
                    if (listALL != null) {
                        for(list in listALL){
                            if(list == "grab"){
                                listFix.add(list)
                            }
                        }
                    }
                    btnNext.setText("Tampilkan " + listFix.size.toString() + " Pesanan")
                }
            }else if(!grab.isChecked){
                if (!tokopedia.isChecked && !pikapp.isChecked && !shopee.isChecked) {
                    listFix.clear()
                    if (listALL != null) {
                        for (list in listALL){
                            listFix.add(list)
                        }
                    }
                    btnNext.setText("Tampilkan " + listFix.size.toString() + " Pesanan")
                } else {
                    if (listALL != null) {
                        for(list in listALL){
                            if(list == "grab"){
                                listFix.remove(list)
                            }
                        }
                    }
                    btnNext.setText("Tampilkan " + listFix.size.toString() + " Pesanan")
                }
            }
        }

        shopee.setOnCheckedChangeListener { buttonView, isChecked ->
            if(shopee.isChecked){
                if (!tokopedia.isChecked && !grab.isChecked && !pikapp.isChecked) {
                    listFix.clear()
                    if (listALL != null) {
                        for(list in listALL){
                            if(list == "shopee"){
                                listFix.add(list)
                            }
                        }
                    }
                    btnNext.setText("Tampilkan " + listFix.size.toString() + " Pesanan")
                } else {
                    if (listALL != null) {
                        for(list in listALL){
                            if(list == "shopee"){
                                listFix.add(list)
                            }
                        }
                    }
                    btnNext.setText("Tampilkan " + listFix.size.toString() + " Pesanan")
                }
            }else if(!shopee.isChecked){
                if (!tokopedia.isChecked && !grab.isChecked && !pikapp.isChecked) {
                    listFix.clear()
                    if (listALL != null) {
                        for (list in listALL){
                            listFix.add(list)
                        }
                    }
                    btnNext.setText("Tampilkan " + listFix.size.toString() + " Pesanan")
                } else {
                    if (listALL != null) {
                        for(list in listALL){
                            if(list == "shopee"){
                                listFix.remove(list)
                            }
                        }
                    }
                    btnNext.setText("Tampilkan " + listFix.size.toString() + " Pesanan")
                }
            }
        }

        btnNext.setOnClickListener {
            viewModel.filterOn(listFix, pikapp.isChecked, tokopedia.isChecked, grab.isChecked, shopee.isChecked)
            dismiss()
        }
    }
}