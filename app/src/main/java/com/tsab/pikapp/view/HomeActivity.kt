package com.tsab.pikapp.view

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.ActivityHomeBinding
import com.tsab.pikapp.viewmodel.home.HomeActivityViewModel

class HomeActivity : AppCompatActivity() {

    private lateinit var viewModel: HomeActivityViewModel
    private lateinit var dataBinding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(HomeActivityViewModel::class.java)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        setButton()
        viewModel.resetCart()
        dataBinding.buttonCart.setOnClickListener {
            viewModel.goToCart(this)
        }
    }

    @SuppressLint("ResourceType")
    private fun observeViewModel() {
        viewModel.cart.observe(this, Observer {
            if (it) {
                dataBinding.buttonCartContainer.visibility = View.VISIBLE
            } else {
                dataBinding.buttonCartContainer.visibility = View.GONE
            }
        })

        viewModel.cartError.observe(this, Observer {
            if(it.errCode == "EC0021") {
                viewModel.createToastShort(application, "Kamu login di perangkat lain. Silakan login kembali")
                viewModel.clearSession(this)
            }
        })
    }

    private fun setButton() {
        val buttonFloat: View? =
            this.findViewById<View>(R.id.buttonCartContainer)
        buttonFloat?.let { it ->
            if (it.isVisible) {
                val param = it.layoutParams as ViewGroup.MarginLayoutParams
                param.setMargins(0, 0, 10, 30)
                it.layoutParams = param
            }
        }

    }

    override fun onResume() {
        super.onResume()
        viewModel.getCartStatus()
        observeViewModel()
    }

    fun hideKeyboard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if (inputManager.isAcceptingText) {
            inputManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val view: View? = currentFocus
        if (view != null && (ev.action == MotionEvent.ACTION_UP || ev.action == MotionEvent.ACTION_MOVE) && view is EditText && !view.javaClass.getName().startsWith("android.webkit.")
        ) {
            val scrcoords = IntArray(2)
            view.getLocationOnScreen(scrcoords)
            val x: Float = ev.rawX + view.getLeft() - scrcoords[0]
            val y: Float = ev.rawY + view.getTop() - scrcoords[1]
            if (x < view.getLeft() || x > view.getRight() || y < view.getTop() || y > view.getBottom()) (this.getSystemService(
                Context.INPUT_METHOD_SERVICE
            ) as InputMethodManager).hideSoftInputFromWindow(
                this.window.decorView.applicationWindowToken, 0
            )
        }
        return super.dispatchTouchEvent(ev)
    }

}