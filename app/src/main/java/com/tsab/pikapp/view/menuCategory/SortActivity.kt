package com.tsab.pikapp.view.menuCategory

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
import com.tsab.pikapp.view.homev2.HomeActivity
import kotlinx.android.synthetic.main.activity_sort.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class SortActivity : AppCompatActivity(), SortCategoryAdapter.OnItemClickListener {
    val gson = Gson()
    val type = object : TypeToken<BaseResponse>() {}.type

    var categoryListName: MutableList<categories_name> = mutableListOf()
    lateinit var sortCategoryAdapter: SortCategoryAdapter
    lateinit var linearLayoutManager: LinearLayoutManager

    var size: String = "0"
    var categoryName: String = ""
    var categoryOrder: String = ""
    var activationToggle: String = ""
    var categoryId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sort)

        recyclerview_category.setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(this)
        recyclerview_category.layoutManager = linearLayoutManager


        val itemTouchHelper = object : ItemTouchHelper.Callback() {
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
                Collections.swap(
                    sortCategoryAdapter.menuCategoryList,
                    viewHolder.adapterPosition,
                    target.adapterPosition
                )

                sortCategoryAdapter.notifyItemMoved(
                    viewHolder.adapterPosition,
                    target.adapterPosition
                )

                categoryListName = sortCategoryAdapter.menuCategoryList.mapIndexed { index, value ->
                    categories_name(
                            category_name = value.category_name,
                            category_order = index,
                            activation = value.is_active,
                            id = value.id,
                            product_size = value.product_size
                    )
                }.toMutableList()

                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
        }

        val itemTouchHelperCallback = ItemTouchHelper(itemTouchHelper)
        itemTouchHelperCallback.attachToRecyclerView(recyclerview_category)

        val sessionManager = SessionManager(application)

        val email = sessionManager.getUserData()!!.email!!
        val token = sessionManager.getUserToken()!!
        val timestamp = getTimestamp()
        val signature = getSignature(email, timestamp)
        val mid = sessionManager.getUserData()!!.mid!!

        // TODO: Update API call.
        PikappApiService().api.getSortMenuCategoryList(
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
                size = categoryResponse?.results?.size.toString()

                sortCategoryAdapter = SortCategoryAdapter(
                    baseContext,
                    categoryResult as MutableList<CategoryListResult>,
                    this@SortActivity
                )
                sortCategoryAdapter.notifyDataSetChanged()
                recyclerview_category.adapter = sortCategoryAdapter

                categoryListName = sortCategoryAdapter.menuCategoryList.map {
                    categories_name(
                        category_name = it.category_name,
                        category_order = it.category_order,
                        activation = it.is_active,
                        id = it.id,
                            product_size = it.product_size
                    )
                }.toMutableList()
            }

        })

        saveBtn.setOnClickListener {
            var sessionManager = SessionManager(application)
            val email = sessionManager.getUserData()!!.email!!
            val token = sessionManager.getUserToken()!!
            val timestamp = getTimestamp()
            val signature = getSignature(email, timestamp)
            val mid = sessionManager.getUserData()!!.mid!!

            var sortReq = SortCategoryRequest()
            sortReq.categories_menu = categoryListName

            PikappApiService().api.sortMenuCategory(
                getUUID(), timestamp, getClientID(), signature, token, mid, sortReq
            ).enqueue(object : retrofit2.Callback<BaseResponse> {
                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    Toast.makeText(baseContext, "failed", Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    if (response.code() == 200 && response.body()!!.errCode.toString() == "EC0000") {
                        Toast.makeText(baseContext, "Urutan kategori berhasil diubah", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@SortActivity, CategoryNavigation::class.java)
                        startActivity(intent)
                    } else {
                        var errorResponse: BaseResponse? =
                            gson.fromJson(response.errorBody()!!.charStream(), type)
                        Toast.makeText(
                            baseContext,
                            generateResponseMessage(
                                errorResponse?.errCode,
                                errorResponse?.errMessage
                            ).toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            })
        }

        headerLayout.setOnClickListener {
            val sortNav = intent.getIntExtra("SORT_NAV", 0)
            if (sortNav == 1) {
                finish()
            } else {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    override fun onItemClick(position: Int) {
        categoryName = sortCategoryAdapter.menuCategoryList[position].category_name.toString()
        categoryOrder = sortCategoryAdapter.menuCategoryList[position].category_order.toString()
        activationToggle = sortCategoryAdapter.menuCategoryList[position].is_active.toString()
        categoryId = sortCategoryAdapter.menuCategoryList[position].id.toString()
    }

    override fun onBackPressed() {
        val sortNav = intent.getIntExtra("SORT_NAV", 0)
        if (sortNav == 1) {
            finish()
        } else {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}