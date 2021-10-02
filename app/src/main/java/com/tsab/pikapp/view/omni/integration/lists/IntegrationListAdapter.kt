package com.tsab.pikapp.view.omni.integration.lists

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.tsab.pikapp.R
import com.tsab.pikapp.models.model.Omnichannel
import com.tsab.pikapp.models.model.OmnichannelStatus

class IntegrationListAdapter(
    private var integrationList: MutableList<Omnichannel>,
    private val onItemSelectedListener: OnItemClickListener
) : RecyclerView.Adapter<IntegrationListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val logoImage: ImageView = view.findViewById(R.id.itemLogoImage)
        val nameText: TextView = view.findViewById(R.id.itemNameText)
        val statusImage: View = view.findViewById(R.id.statusTokoIndicator)
        val statusText: TextView = view.findViewById(R.id.itemStatusText)
        val omnichannelTypeImage: ImageView = view.findViewById(R.id.itemOmnichannelTypeImage)
    }

    interface OnItemClickListener {
        fun onItemClick(omnichannel: Omnichannel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_omnichannel_integration_status, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.nameText.text = integrationList[position].name

        val status = integrationList[position].status
        holder.statusImage.backgroundTintList = ContextCompat.getColorStateList(
            holder.itemView.context, when (status) {
                OmnichannelStatus.ON_PROGRESS -> R.color.yellow
                OmnichannelStatus.CONNECTED -> R.color.green
                else -> R.color.red
            }
        )
        holder.statusText.text = when (status) {
            OmnichannelStatus.ON_PROGRESS -> "Menunggu"
            OmnichannelStatus.CONNECTED -> "Terhubung"
            else -> "Kadaluwarsa"
        }

        holder.omnichannelTypeImage.setImageResource(
            when (integrationList[position].name) {
                "TOKOPEDIA" -> R.drawable.logo_tokopedia_small
                else -> R.drawable.logo_tokopedia_small
            }
        )

        holder.itemView.setOnClickListener {
            onItemSelectedListener.onItemClick(integrationList[position])
        }
    }

    override fun getItemCount(): Int = integrationList.size

    fun setIntegrationList(integrationList: MutableList<Omnichannel>) {
        this.integrationList.clear()
        this.integrationList.addAll(integrationList)
        notifyDataSetChanged()
    }
}