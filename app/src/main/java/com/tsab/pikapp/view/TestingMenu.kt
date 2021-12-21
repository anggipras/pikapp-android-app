package com.tsab.pikapp.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tsab.pikapp.R
import com.tsab.pikapp.models.model.MerchantListCategoryResponse
import com.tsab.pikapp.models.network.PikappApiService
import com.tsab.pikapp.util.*
import kotlinx.android.synthetic.main.activity_testing_menu.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TestingMenu : AppCompatActivity() {

    val gson = Gson()
    val type = object : TypeToken<MerchantListCategoryResponse>() {}.type
    var activation: Boolean = true
    lateinit var linearLayoutManager: LinearLayoutManager

    companion object {
        const val COUNT_KEY = "COUNT_KEY"
    }

    var size: String = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_testing_menu)

        recyclerview_category.setHasFixedSize(true)
        linearLayoutManager =
            LinearLayoutManager(this@TestingMenu, LinearLayoutManager.HORIZONTAL, false)
        recyclerview_category.layoutManager = linearLayoutManager

        var sessionManager = SessionManager(application)
        val email = sessionManager.getUserData()!!.email!!
        val token = sessionManager.getUserToken()!!
        val timestamp = getTimestamp()
        val signature = getSignature(email, timestamp)
        val mid = sessionManager.getUserData()!!.mid!!

        // TODO: Update API call.
        PikappApiService().api.getMenuCategoryList(
            getUUID(), timestamp, getClientID(), signature, token, mid
        ).enqueue(object : Callback<MerchantListCategoryResponse> {
            override fun onFailure(call: Call<MerchantListCategoryResponse>, t: Throwable) {
                Log.e("failed", t.message.toString())
            }

            override fun onResponse(
                call: Call<MerchantListCategoryResponse>,
                response: Response<MerchantListCategoryResponse>
            ) {

                val categoryResponse = response.body()
                size = categoryResponse?.results?.size.toString()
            }
        })

        plusBtn.setOnClickListener {
            val intent = Intent(this@TestingMenu, AddCategoryPage::class.java)
            intent.putExtra("category_size", size)
            startActivity(intent)
        }
    }

}