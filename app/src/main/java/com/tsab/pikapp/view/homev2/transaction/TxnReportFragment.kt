package com.tsab.pikapp.view.homev2.transaction

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.tsab.pikapp.R
import com.tsab.pikapp.util.SwipeUpUtil
import com.tsab.pikapp.viewmodel.homev2.TransactionViewModel
import kotlinx.android.synthetic.main.expanded_txn_fragment.*
import kotlinx.android.synthetic.main.expanded_txn_fragment.tabs
import kotlinx.android.synthetic.main.fragment_proccess.*
import kotlinx.android.synthetic.main.fragment_txn_report.*
import kotlinx.android.synthetic.main.fragment_txn_report.recyclerview_transaction
import java.util.*
import kotlin.collections.ArrayList

class TxnReportFragment : Fragment() {

    private val viewModel: TransactionViewModel by activityViewModels()

    val listTest:ArrayList<String> = ArrayList()
    val filterTest:ArrayList<String> = ArrayList()
    var buttonTokped : Int = 1
    var buttonGrab : Int = 1
    var buttonShopee : Int = 1
    var buttonPikapp : Int = 1
    val filterSheet = FilterFragment()

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_txn_report, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpTabs()

        backbtn.setOnClickListener {
            activity?.finish()
        }

        val floatingSlideUpBuilder = SwipeUpUtil(requireView().context, floating)
                .setFloatingMenuRadiusInDp(32)
                .setFloatingMenu(container)
                .setPanel(expanded)
                .build()
        date.setText(Date().toString())

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
                object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        val status: Boolean = floatingSlideUpBuilder.collapseBottomSheetStatus()
                        if (status == true) {
                            floatingSlideUpBuilder.collapseBottomSheet()
                        } else {
                            activity?.finish()
                        }
                    }
                })


        listTest.add("shopee")
        listTest.add("shopee")
        listTest.add("grab")
        listTest.add("grab")
        listTest.add("pikapp")
        listTest.add("tokopedia")
        listTest.add("tokopedia")
        listTest.add("tokopedia")

        filterTest.addAll(listTest)

        recyclerview_transaction.layoutManager = LinearLayoutManager(requireView().context)
        recyclerview_transaction.adapter = TxnReportAdapter(filterTest)

     /*   viewModel.editList(recyclerview_transaction, filterTest, buttonFilterPikapp, buttonFilterTokped,
        buttonFilterGrab, buttonFilterShopee)*/

        buttonFilterCount.setOnClickListener {
            val bundle = Bundle()
            bundle.putStringArrayList("allList", listTest)
            bundle.putStringArrayList("filterList", filterTest)
            filterSheet.arguments = bundle
            filterSheet.show(requireActivity().supportFragmentManager, "show")
        }

        buttonFilterTokped.setOnClickListener {
            if (buttonTokped == 1) {
                if (buttonGrab == 1 && buttonShopee == 1 && buttonPikapp == 1) {
                    filterTest.clear()
                    for (list in listTest) {
                        if (list == "tokopedia") {
                            filterTest.add(list)
                        }
                    }
                } else {
                    for (list in listTest) {
                        if (list == "tokopedia") {
                            filterTest.add(list)
                        }
                    }
                }
                recyclerview_transaction.adapter!!.notifyDataSetChanged()
                buttonTokped = 2
                buttonFilterTokped.setBackgroundResource(R.drawable.button_green_square)
                buttonFilterTokped.setTextColor(resources.getColor(R.color.white))
            } else if (buttonTokped == 2) {
                    if (buttonGrab == 1 && buttonShopee == 1 && buttonPikapp == 1) {
                        filterTest.clear()
                        filterTest.addAll(listTest)
                    } else {
                        for (list in listTest) {
                            if (list == "tokopedia") {
                                filterTest.remove(list)
                            }
                        }
                    }
                recyclerview_transaction.adapter!!.notifyDataSetChanged()
                buttonTokped = 1
                buttonFilterTokped.setBackgroundResource(R.drawable.gray_square_btn)
                buttonFilterTokped.setTextColor(resources.getColor(R.color.borderSubtle))
            }

            buttonFilterGrab.setOnClickListener {
                if (buttonGrab == 1) {
                    if (buttonTokped == 1 && buttonShopee == 1 && buttonPikapp == 1) {
                        filterTest.clear()
                        for (list in listTest) {
                            if (list == "grab") {
                                filterTest.add(list)
                            }
                        }
                    } else {
                        for (list in listTest) {
                            if (list == "grab") {
                                filterTest.add(list)
                            }
                        }
                    }
                    recyclerview_transaction.adapter!!.notifyDataSetChanged()
                    buttonGrab = 2
                    buttonFilterGrab.setBackgroundResource(R.drawable.button_green_square)
                    buttonFilterGrab.setTextColor(resources.getColor(R.color.white))
                } else if (buttonGrab == 2) {
                        if (buttonTokped == 1 && buttonShopee == 1 && buttonPikapp == 1) {
                            filterTest.clear()
                            filterTest.addAll(listTest)
                        } else {
                            for (list in listTest) {
                                if (list == "grab") {
                                    filterTest.remove(list)
                                }
                            }
                        }
                        recyclerview_transaction.adapter!!.notifyDataSetChanged()
                        buttonGrab = 1
                    buttonFilterGrab.setBackgroundResource(R.drawable.gray_square_btn)
                    buttonFilterGrab.setTextColor(resources.getColor(R.color.borderSubtle))
                    }
            }

            buttonFilterShopee.setOnClickListener {
                if (buttonShopee == 1) {
                    if (buttonTokped == 1 && buttonGrab== 1 && buttonPikapp == 1) {
                        filterTest.clear()
                        for (list in listTest) {
                            if (list == "shopee") {
                                filterTest.add(list)
                            }
                        }
                    } else {
                        for (list in listTest) {
                            if (list == "shopee") {
                                filterTest.add(list)
                            }
                        }
                    }
                    recyclerview_transaction.adapter!!.notifyDataSetChanged()
                    buttonShopee = 2
                    buttonFilterShopee.setBackgroundResource(R.drawable.button_green_square)
                    buttonFilterShopee.setTextColor(resources.getColor(R.color.white))
                }else if(buttonShopee == 2){
                    if (buttonTokped == 1 && buttonGrab== 1 && buttonPikapp == 1) {
                        filterTest.clear()
                        filterTest.addAll(listTest)
                    } else {
                        for (list in listTest) {
                            if (list == "shopee") {
                                filterTest.remove(list)
                            }
                        }
                    }
                    recyclerview_transaction.adapter!!.notifyDataSetChanged()
                    buttonShopee = 1
                    buttonFilterShopee.setBackgroundResource(R.drawable.gray_square_btn)
                    buttonFilterShopee.setTextColor(resources.getColor(R.color.borderSubtle))
                }
            }

            buttonFilterPikapp.setOnClickListener {
                if (buttonPikapp == 1) {
                    if (buttonTokped == 1 && buttonGrab== 1 && buttonShopee == 1) {
                        filterTest.clear()
                        for (list in listTest) {
                            if (list == "pikapp") {
                                filterTest.add(list)
                            }
                        }
                    } else {
                        for (list in listTest) {
                            if (list == "pikapp") {
                                filterTest.add(list)
                            }
                        }
                    }
                    recyclerview_transaction.adapter!!.notifyDataSetChanged()
                    buttonPikapp = 2
                    buttonFilterPikapp.setBackgroundResource(R.drawable.button_green_square)
                    buttonFilterPikapp.setTextColor(resources.getColor(R.color.white))
                } else if (buttonPikapp == 2) {
                    if (buttonTokped == 1 && buttonGrab== 1 && buttonShopee == 1) {
                        filterTest.clear()
                        filterTest.addAll(listTest)
                    } else {
                        for (list in listTest) {
                            if (list == "pikapp") {
                                filterTest.remove(list)
                            }
                        }
                    }
                    recyclerview_transaction.adapter!!.notifyDataSetChanged()
                    buttonPikapp = 1
                    buttonFilterPikapp.setBackgroundResource(R.drawable.gray_square_btn)
                    buttonFilterPikapp.setTextColor(resources.getColor(R.color.borderSubtle))
                }
            }
        }
    }
    fun setUpTabs() {
        val adapter = activity?.let { RingkasanAdapter(it.supportFragmentManager) }
        if (adapter != null) {
            adapter.addFragment(AllTxnFragment(), "Semua Transaksi")
            adapter.addFragment(TxnDetailFragment(), "Rincian")
            viewPager.adapter = adapter
            tabs.setupWithViewPager(viewPager)
        }
    }
}