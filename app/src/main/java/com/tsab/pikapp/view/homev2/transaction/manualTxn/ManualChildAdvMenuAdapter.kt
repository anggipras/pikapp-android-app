package com.tsab.pikapp.view.homev2.transaction.manualTxn

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.RadioButton
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tsab.pikapp.R
import com.tsab.pikapp.models.model.DummyChoices

class ManualChildAdvMenuAdapter(
        val context: Context,
        private val indexOfMenu: Int,
        private val choiceType: String,
        private val menuChoiceList: MutableList<DummyChoices>,
        var dummyAddChoice: ArrayList<ManualAddAdvMenuFragment.AddAdvDummy>
) : RecyclerView.Adapter<ManualChildAdvMenuAdapter.ViewHolder>() {
    lateinit var linearLayoutManager: LinearLayoutManager
    private var lastChecked: RadioButton? = null
    private var lastCheckedPos = 0

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var menuChoiceRadio: RadioButton = itemView.findViewById(R.id.menuChoiceAdv_radio)
        var menuChoiceCheck: CheckBox = itemView.findViewById(R.id.menuChoiceAdv_checkbox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.child_adv_menu_choice_list, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (choiceType == "radio") {
            holder.menuChoiceRadio.isVisible = true
            holder.menuChoiceCheck.isVisible = false
            holder.menuChoiceRadio.text = menuChoiceList[position].ext_menu_name
            holder.menuChoiceRadio.tag = position

            //for default check in first item
            if(position == 0 && holder.menuChoiceRadio.isChecked) {
                lastChecked = holder.menuChoiceRadio;
                lastCheckedPos = 0;
            }

            holder.menuChoiceRadio.setOnClickListener { v ->
                val cb = v as RadioButton
                val clickedPos = (cb.tag as Int).toInt()
                val dummyEachData = ManualAddAdvMenuFragment.AddChoicesDummy(ext_menu_name = menuChoiceList[position].ext_menu_name, ext_menu_price = menuChoiceList[position].ext_menu_price)
                dummyAddChoice[indexOfMenu].ext_menus = listOf(dummyEachData).toMutableList()
                Log.e("MENUCHOICE", dummyAddChoice.toString())
                if (cb.isChecked) {
                    lastChecked?.isChecked = false
                    lastChecked = cb
                    lastCheckedPos = clickedPos
                } else lastChecked = null
            }
        } else {
            holder.menuChoiceRadio.isVisible = false
            holder.menuChoiceCheck.isVisible = true
            holder.menuChoiceCheck.text = menuChoiceList[position].ext_menu_name
            val dummyEachDataCheck = ManualAddAdvMenuFragment.AddChoicesDummy(ext_menu_name = menuChoiceList[position].ext_menu_name, ext_menu_price = menuChoiceList[position].ext_menu_price)
            val dummyEachDataCheckRemoved = ManualAddAdvMenuFragment.AddChoicesDummy(ext_menu_name = "", ext_menu_price = "0")
            holder.menuChoiceCheck.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    dummyAddChoice[indexOfMenu].ext_menus[position] = dummyEachDataCheck
                } else {
                    dummyAddChoice[indexOfMenu].ext_menus[position] = dummyEachDataCheckRemoved
                }
                Log.e("RESULT", dummyAddChoice[indexOfMenu].ext_menus.toString())
            }
        }
    }

    override fun getItemCount(): Int {
        return menuChoiceList.size
    }
}