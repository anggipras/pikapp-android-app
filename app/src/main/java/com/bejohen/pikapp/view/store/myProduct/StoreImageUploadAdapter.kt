package com.bejohen.pikapp.view.store.myProduct

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bejohen.pikapp.R
import com.bejohen.pikapp.databinding.ItemStoreProductPhotoFormBinding
import com.bejohen.pikapp.models.model.StoreImage
import com.bejohen.pikapp.util.getProgressDrawable
import com.bejohen.pikapp.util.loadImage

class StoreImageUploadAdapter(
    private val imageUriList: ArrayList<StoreImage>,
    private val deleteImageInterface: DeleteImageInterface
) : RecyclerView.Adapter<StoreImageUploadAdapter.ProductViewHolder>() {

    private lateinit var dataBinding: ItemStoreProductPhotoFormBinding

    fun addProductList(newUri: StoreImage) {
        imageUriList.add(newUri)
        notifyItemInserted(imageUriList.size - 1)
    }

    fun deleteProductList(p: Int) {
        imageUriList.removeAt(p)
        notifyDataSetChanged()
    }

    fun addAllProductList(newUris: List<StoreImage>) {
        imageUriList.addAll(newUris)
        notifyDataSetChanged()
    }

    class ProductViewHolder(var view: ItemStoreProductPhotoFormBinding) :
        RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        dataBinding =
            DataBindingUtil.inflate(inflater, R.layout.item_store_product_photo_form, parent, false)
        return ProductViewHolder(dataBinding)
    }

    override fun getItemCount(): Int = imageUriList.size

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val uri = imageUriList[position].imageUri.toString()
        if(uri.contains("https://")) {
            holder.view.imageDisplay.loadImage(uri, getProgressDrawable(dataBinding.root.context))
        } else {
            holder.view.storeImage = imageUriList[position]
        }
        holder.view.btnClose.setOnClickListener {
            deleteImageInterface.onDelete(position)
        }
    }

    interface DeleteImageInterface {
        fun onDelete(p: Int)
    }

}