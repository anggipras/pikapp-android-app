package com.tsab.pikapp.view.homev2.transaction.manualTxn

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tsab.pikapp.R
import com.tsab.pikapp.models.model.AddManualAdvMenu
import com.tsab.pikapp.models.model.AdvanceAdditionalMenu
import com.tsab.pikapp.models.model.AdvanceMenu

class ManualAdvMenuAdapterDev(
    val context: Context,
    private val indexOfCart: Int,
    private val addManualAdvMenu: MutableList<AddManualAdvMenu>,
    private val manualAdvMenuList: MutableList<AdvanceMenu>,
    private val addAdvMenuTemplate: ArrayList<ManualAddAdvMenuActivity.AddAdvMenuTemp>,
    private val advMenuEdit: Boolean,
    private val listener: ManualChildAdvMenuAdapterDev.OnItemClickListener
) : RecyclerView.Adapter<ManualAdvMenuAdapterDev.ViewHolder>() {
    lateinit var linearLayoutManager: LinearLayoutManager

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var parentMenuChoice: TextView = itemView.findViewById(R.id.advMenu_parentChoice)
        var parentMenuChoiceMaxChoose: TextView = itemView.findViewById(R.id.advMenu_parentChoice_maxChoose)
        var rViewRadio: RecyclerView = itemView.findViewById(R.id.recyclerview_childMenuChoice)
        var rViewCheck: RecyclerView = itemView.findViewById(R.id.recyclerview_childMenuChoice_check)
        var radioSelection: RadioGroup = itemView.findViewById(R.id.radio_childMenuChoice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.manual_advance_menu_list, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.parentMenuChoice.text = manualAdvMenuList[position].template_name
        val childMenuChoice = manualAdvMenuList[position].ext_menus as MutableList<AdvanceAdditionalMenu>

        if (manualAdvMenuList[position].template_type == "RADIO") {
            holder.radioSelection.isVisible = true
            holder.rViewCheck.isVisible = false
            if (manualAdvMenuList[position].mandatory) {
                holder.parentMenuChoiceMaxChoose.text = context.getString(R.string.max_choose, "Wajib, Pilih 1")
            } else {
                holder.parentMenuChoiceMaxChoose.text = context.getString(R.string.max_choose, "Optional, Pilih 1")
            }
            val indexOfMenu = manualAdvMenuList.indexOf(manualAdvMenuList[position])
            setChildManualAdvMenu(holder.rViewRadio, indexOfMenu, manualAdvMenuList[position].template_type, childMenuChoice, addAdvMenuTemplate, advMenuEdit, listener)
        } else {
            holder.radioSelection.isVisible = false
            holder.rViewCheck.isVisible = true
            if (manualAdvMenuList[position].mandatory) {
                val maxChoose = "Wajib, Max ${manualAdvMenuList[position].max_choose} items"
                holder.parentMenuChoiceMaxChoose.text = context.getString(R.string.max_choose, maxChoose)
            } else {
                val maxChoose = "Optional, Max ${manualAdvMenuList[position].max_choose} items"
                holder.parentMenuChoiceMaxChoose.text = context.getString(R.string.max_choose, maxChoose)
            }
            val indexOfMenu = manualAdvMenuList.indexOf(manualAdvMenuList[position])
            setChildManualAdvMenu(holder.rViewCheck, indexOfMenu, manualAdvMenuList[position].template_type, childMenuChoice, addAdvMenuTemplate, advMenuEdit, listener)
        }
    }

    override fun getItemCount(): Int {
        return manualAdvMenuList.size
    }

    private fun setChildManualAdvMenu(rView: RecyclerView, indexOfMenu: Int, choiceType: String, childMenuChoice: MutableList<AdvanceAdditionalMenu>, dummyAddChoice: ArrayList<ManualAddAdvMenuActivity.AddAdvMenuTemp>, isMenuEdit: Boolean, listener: ManualChildAdvMenuAdapterDev.OnItemClickListener) {
        linearLayoutManager = LinearLayoutManager(context)
        rView.layoutManager = linearLayoutManager
        rView.setHasFixedSize(false)
        var childRecyclerView = ManualChildAdvMenuAdapterDev(context, addManualAdvMenu, manualAdvMenuList, indexOfCart, indexOfMenu, choiceType, childMenuChoice, dummyAddChoice, isMenuEdit, listener)
        rView.adapter = childRecyclerView
    }
}