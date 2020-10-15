package com.bejohen.pikapp.view.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bejohen.pikapp.R
import com.bejohen.pikapp.databinding.ItemHomeBannerSliderBinding
import com.bejohen.pikapp.models.model.ItemHomeBannerSlider

class HomeBannerSliderListAdapter(val sliderList: ArrayList<ItemHomeBannerSlider>) :
    RecyclerView.Adapter<HomeBannerSliderListAdapter.SliderViewHolder>() {
    class SliderViewHolder(var view: ItemHomeBannerSliderBinding) :
        RecyclerView.ViewHolder(view.root)

    fun updateHomeBannerList(newDogsList: List<ItemHomeBannerSlider>) {
        sliderList.clear()
        sliderList.addAll(newDogsList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = DataBindingUtil.inflate<ItemHomeBannerSliderBinding>(
            inflater,
            R.layout.item_home_banner_slider,
            parent,
            false
        )
        return SliderViewHolder(view)
    }

    override fun getItemCount(): Int = sliderList.size

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        holder.view.sliderList = sliderList[position]
    }
}