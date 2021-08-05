package com.tsab.pikapp.view.categoryMenu

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tsab.pikapp.R
import com.tsab.pikapp.models.model.*
import com.tsab.pikapp.models.network.PikappApiService
import com.tsab.pikapp.util.*
import com.tsab.pikapp.view.homev2.HomeNavigation
import kotlinx.android.synthetic.main.activity_test.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class SortActivity : AppCompatActivity(), SortCategoryAdapter.OnItemClickListener {

    val gson = Gson()
    val type = object : TypeToken<BaseResponse>() {}.type

    val dataList: MutableList<CategoryListResult> = mutableListOf()
    var categoryListName: MutableList<categories_name> = mutableListOf()
    lateinit var sortCategoryAdapter: SortCategoryAdapter
    lateinit var linearLayoutManager: LinearLayoutManager

    var size: String = "0"
    var categoryName: String = ""
    var categoryOrder: String = ""
    var activationToggle: String = ""
    var categoryId: String = ""
    //var list: List<CategoryListName>? = List()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        recyclerview_category.setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(this)
        recyclerview_category.layoutManager = linearLayoutManager



        val itemTouchHelper = object: ItemTouchHelper.Callback(){
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
                return makeMovementFlags(dragFlags, 0)
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                Collections.swap(sortCategoryAdapter.menuCategoryList, viewHolder.adapterPosition, target.adapterPosition)
                sortCategoryAdapter.notifyItemMoved(viewHolder.adapterPosition, target.adapterPosition)
                //recyclerView.adapter?.notifyItemMoved(viewHolder.adapterPosition, target.adapterPosition)

                categoryListName = sortCategoryAdapter.menuCategoryList.map {
                    categories_name(category_name = it.category_name, category_order = it.category_order, activation = it.is_active, id = it.id)
                }.toMutableList()
                Log.e("touchhelper", categoryListName.toString())

                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            }

        }

        val itemTouchHelperCallback = ItemTouchHelper(itemTouchHelper)
        itemTouchHelperCallback.attachToRecyclerView(recyclerview_category)

        var sessionManager = SessionManager(getApplication())
        val email = sessionManager.getUserData()!!.email!!
        val token = sessionManager.getUserToken()!!
        val timestamp = getTimestamp()
        val signature = getSignature(email, timestamp)
        val mid = sessionManager.getUserData()!!.mid!!

        PikappApiService().api.getMenuCategoryList(
            getUUID(), timestamp, getClientID(), signature, token, mid
        ).enqueue(object : Callback<MerchantListCategoryResponse> {
            override fun onFailure(call: Call<MerchantListCategoryResponse>, t: Throwable) {
                Log.e("failed", t.message.toString())
            }

            override fun onResponse(call: Call<MerchantListCategoryResponse>, response: Response<MerchantListCategoryResponse>) {

                val categoryResponse = response.body()
                val categoryResult = response.body()?.results
                Log.e("result", categoryResponse?.results.toString())
                Log.e("Response raw", response.raw().toString())
                Log.e("response body", response.body().toString())
                Log.d("SUCCEED", "succeed")

                Log.e("size", categoryResponse?.results?.size.toString())
                size = categoryResponse?.results?.size.toString()
                Log.e("size on response", size)

                sortCategoryAdapter = SortCategoryAdapter(baseContext, categoryResult as MutableList<CategoryListResult>, this@SortActivity)
                sortCategoryAdapter.notifyDataSetChanged()
                recyclerview_category.adapter = sortCategoryAdapter

                categoryListName = sortCategoryAdapter.menuCategoryList.map {
                    categories_name(category_name = it.category_name, category_order = it.category_order, activation = it.is_active, id = it.id)
                }.toMutableList()
                Log.e("get", categoryListName.toString())
            }

        })

        saveBtn.setOnClickListener {
            var sessionManager = SessionManager(getApplication())
            val email = sessionManager.getUserData()!!.email!!
            val token = sessionManager.getUserToken()!!
            val timestamp = getTimestamp()
            val signature = getSignature(email, timestamp)
            val mid = sessionManager.getUserData()!!.mid!!

            var sortReq = SortCategoryRequest()
            sortReq.categories_menu = categoryListName
            Log.e("sort", sortReq.categories_menu.toString())

            PikappApiService().api.sortMenuCategory(
                getUUID(), timestamp, getClientID(), signature, token, mid, sortReq
            ).enqueue(object: retrofit2.Callback<BaseResponse> {
                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    Toast.makeText(baseContext, "failed", Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                    if(response.code() == 200 && response.body()!!.errCode.toString() == "EC0000"){
                        Toast.makeText(baseContext, "sorted", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@SortActivity, HomeNavigation::class.java)
                        startActivity(intent)
                    }else {
                        var errorResponse: BaseResponse? = gson.fromJson(response.errorBody()!!.charStream(), type)
                        Toast.makeText(baseContext, generateResponseMessage(errorResponse?.errCode, errorResponse?.errMessage).toString(), Toast.LENGTH_LONG).show()
                    }
                }
            })
        }

        backBtn.setOnClickListener {
            val intent = Intent(this@SortActivity, CategoryNavigation::class.java)
            startActivity(intent)
        }

    }

    override fun onItemClick(position: Int) {
        Toast.makeText(this, "item $position clicked", Toast.LENGTH_SHORT).show()
        //sortCategoryAdapter.notifyItemChanged(position)

        categoryName = sortCategoryAdapter.menuCategoryList[position].category_name.toString()
        Log.e("category name", categoryName)

        categoryOrder = sortCategoryAdapter.menuCategoryList[position].category_order.toString()
        Log.e("category order", categoryOrder)

        activationToggle = sortCategoryAdapter.menuCategoryList[position].is_active.toString()
        Log.e("activation", activationToggle)

        categoryId = sortCategoryAdapter.menuCategoryList[position].id.toString()
        Log.e("category id", categoryId)
    }
}