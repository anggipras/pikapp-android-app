package com.tsab.pikapp.view.other.otherSettings.shippingSetting

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tsab.pikapp.R
import com.tsab.pikapp.models.model.ListGooglePlaces

class GoogleListPlacesAdapter(
    private val onPlaceClickListener: OnPlaceClickListener
) : RecyclerView.Adapter<GoogleListPlacesAdapter.ViewHolder>() {
    private var listOfPlaces: List<ListGooglePlaces> = ArrayList()

    fun setPlaces(listPlace: List<ListGooglePlaces>) {
        this.listOfPlaces = listPlace
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mainPlace: TextView = view.findViewById(R.id.placeTitleText)
        val detailPlace: TextView = view.findViewById(R.id.placeDescriptionText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_list_google_places, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mainPlace.text = listOfPlaces[position].name
        holder.detailPlace.text = listOfPlaces[position].formattedAddress

        holder.itemView.setOnClickListener {
            onPlaceClickListener.onPlaceClick(listOfPlaces[position].geometry.location.lat, listOfPlaces[position].geometry.location.lng)
        }
    }

    override fun getItemCount(): Int = listOfPlaces.size

    interface OnPlaceClickListener {
        fun onPlaceClick(lat: Double, lng: Double)
    }
}