package com.tsab.pikapp.view.homev2.transaction.manualTxn

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tsab.pikapp.R
import com.tsab.pikapp.models.model.DummyAddChoices

//import com.tsab.pikapp.models.model.DummyChoices

class ManualChildAdvMenuAdapter(
    val context: Context,
    private val menuChoiceList: MutableList<ManualAddAdvMenuFragment.DummyChoices>
) : RecyclerView.Adapter<ManualChildAdvMenuAdapter.ViewHolder>() {
    lateinit var linearLayoutManager: LinearLayoutManager
    private var lastChecked: RadioButton? = null
    private var lastCheckedPos = 0
    var dummyAddChoices: MutableList<DummyAddChoices> = ArrayList()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var menuChoice: RadioButton = itemView.findViewById(R.id.menuChoiceAdv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.radiochild_adv_menu_choice_list, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.menuChoice.text = menuChoiceList[position].menuName
        holder.menuChoice.tag = position

        //for default check in first item
        if(position == 0 && holder.menuChoice.isChecked) {
            lastChecked = holder.menuChoice;
            lastCheckedPos = 0;
        }

        holder.menuChoice.setOnClickListener { v ->
            val cb = v as RadioButton
            val clickedPos = (cb.tag as Int).toInt()
            dummyAddChoices.add(DummyAddChoices(menuName = menuChoiceList[position].menuName, menuPrice = menuChoiceList[position].menuPrice))
            Log.e("MENUCHOICE", dummyAddChoices.toString())
            // TO BE CONTINUE - HOW TO GET VALUE FROM EACH RADIO / CHECKBOX - NEED TO FIGURE OUT!!
            if (cb.isChecked) {
                lastChecked?.isChecked = false
                lastChecked = cb
                lastCheckedPos = clickedPos
            } else lastChecked = null
        }
    }

    override fun getItemCount(): Int {
        return menuChoiceList.size
    }
}