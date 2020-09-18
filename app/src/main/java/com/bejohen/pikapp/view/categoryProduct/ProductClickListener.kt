package com.bejohen.pikapp.view.categoryProduct

import android.view.View

interface ProductClickListener {
    fun onProductClicked(v: View)
}

interface ProductListClickListener {
    fun onProductListClicked(v: View)
//    fun onAddProductClicked(v: View, position: Int)
}