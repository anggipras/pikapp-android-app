package com.tsab.pikapp.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tsab.pikapp.R
import com.tsab.pikapp.models.model.CategoryListResult
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
    var namecheck: Boolean = false
    var activation: Boolean = true

    val dataList: MutableList<CategoryListResult> = mutableListOf()
    lateinit var categoryAdapter: CategoryAdapter
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

        val context: Context = this

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
                val categoryResult = response.body()?.results
                Log.e("result", categoryResponse?.results.toString())
                Log.e("Response raw", response.raw().toString())
                Log.e("response body", response.body().toString())
                Log.d("SUCCEED", "succeed")

                Log.i("MyTag", "onCreate")
                Log.e("size", categoryResponse?.results?.size.toString())
                size = categoryResponse?.results?.size.toString()
                Log.e("size on response", size)

                /*val intent = Intent(this@TestingMenu, AddCategoryPage::class.java)
                intent.putExtra("category_size",size)
                //startActivity(intent)*/

//                categoryAdapter = CategoryAdapter(baseContext, categoryResult as MutableList<CategoryListResult>)
//                categoryAdapter.notifyDataSetChanged()
//                recyclerview_category.adapter = categoryAdapter
            }
        })

        Log.e("size on response", size.toString())

        plusBtn.setOnClickListener {
            Log.e("size on click", size)
            val intent = Intent(this@TestingMenu, AddCategoryPage::class.java)
            intent.putExtra("category_size", size)
            startActivity(intent)
        }
        //Log.e("onCreate", size.toString())
    }

/*    override fun onResume() {
        super.onResume()
        Log.e("onResume", " onResume")
        si

    }

    override fun onSaveInstanceState(outState: Bundle) { // Here You have to save count value
        super.onSaveInstanceState(outState)
        Log.i("MyTag", "onSaveInstanceState")

        outState.putInt(COUNT_KEY, size)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) { // Here You have to restore count value
        super.onRestoreInstanceState(savedInstanceState)
        Log.i("MyTag", "onRestoreInstanceState")

        size = savedInstanceState.getInt(COUNT_KEY)
    }*/

}