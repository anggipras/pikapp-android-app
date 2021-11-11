package com.tsab.pikapp.view.homev2.transaction.manualTxn

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.constraintlayout.widget.Constraints
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tsab.pikapp.R
import com.tsab.pikapp.models.model.AdvanceAdditionalMenu

class ManualChildAdvMenuAdapter(
        val context: Context,
        private val indexOfMenu: Int,
        private val choiceType: String,
        private val menuChoiceList: MutableList<AdvanceAdditionalMenu>,
        var addMenuChoice: ArrayList<ManualAddAdvMenuFragment.AddAdvMenuTemp>,
        private val listener: OnItemClickListener
) : RecyclerView.Adapter<ManualChildAdvMenuAdapter.ViewHolder>() {
    lateinit var linearLayoutManager: LinearLayoutManager
    private var lastChecked: RadioButton? = null
    private var lastCheckedPos = 0

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var menuRadioConstraint: ConstraintLayout = itemView.findViewById(R.id.menuChoiceRadio_constraint)
        var menuChoiceRadio: RadioButton = itemView.findViewById(R.id.menuChoiceAdv_radio)
        var menuPriceRadio: TextView = itemView.findViewById(R.id.menuChoiceAdv_radio_price)

        var menuCheckConstraint: ConstraintLayout = itemView.findViewById(R.id.menuChoiceCheck_constraint)
        var menuChoiceCheck: CheckBox = itemView.findViewById(R.id.menuChoiceAdv_checkbox)
        var menuPriceCheck: TextView = itemView.findViewById(R.id.menuChoiceAdv_check_price)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.child_adv_menu_choice_list, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (choiceType == "RADIO") {
            holder.menuRadioConstraint.isVisible = true
            holder.menuCheckConstraint.isVisible = false
            holder.menuChoiceRadio.text = menuChoiceList[position].ext_menu_name
            holder.menuPriceRadio.text = "+${menuChoiceList[position].ext_menu_price}"
            holder.menuChoiceRadio.tag = position

            //for default check in first item
            if(position == 0 && holder.menuChoiceRadio.isChecked) {
                lastChecked = holder.menuChoiceRadio;
                lastCheckedPos = 0;
            }

            holder.menuChoiceRadio.setOnClickListener { v ->
                val cb = v as RadioButton
                val clickedPos = (cb.tag as Int).toInt()
                val dummyEachData = ManualAddAdvMenuFragment.AddMenuChoicesTemp(ext_menu_name = menuChoiceList[position].ext_menu_name, ext_menu_price = menuChoiceList[position].ext_menu_price)
                addMenuChoice[indexOfMenu].ext_menus = listOf(dummyEachData).toMutableList()
                listener.onItemClick()
                Log.e("MENUCHOICE", addMenuChoice.toString())
                if (cb.isChecked) {
                    lastChecked?.isChecked = false
                    lastChecked = cb
                    lastCheckedPos = clickedPos
                } else lastChecked = null
            }
        } else {
            holder.menuRadioConstraint.isVisible = false
            holder.menuCheckConstraint.isVisible = true
            holder.menuChoiceCheck.text = menuChoiceList[position].ext_menu_name
            holder.menuPriceCheck.text = "+${menuChoiceList[position].ext_menu_price}"
            val dummyEachDataCheck = ManualAddAdvMenuFragment.AddMenuChoicesTemp(ext_menu_name = menuChoiceList[position].ext_menu_name, ext_menu_price = menuChoiceList[position].ext_menu_price)
            val dummyEachDataCheckRemoved = ManualAddAdvMenuFragment.AddMenuChoicesTemp(ext_menu_name = "", ext_menu_price = "0")
            holder.menuChoiceCheck.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    addMenuChoice[indexOfMenu].ext_menus[position] = dummyEachDataCheck
                } else {
                    addMenuChoice[indexOfMenu].ext_menus[position] = dummyEachDataCheckRemoved
                }
                listener.onItemClick()
                Log.e("RESULT", addMenuChoice[indexOfMenu].ext_menus.toString())
            }
        }
    }

    override fun getItemCount(): Int {
        return menuChoiceList.size
    }

    interface OnItemClickListener {
        fun onItemClick()
    }
}