package com.tsab.pikapp.view

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tsab.pikapp.R
import com.tsab.pikapp.models.model.BaseResponse
import com.tsab.pikapp.models.model.MenuCategoryRequest
import com.tsab.pikapp.models.network.PikappApiService
import com.tsab.pikapp.util.*
import kotlinx.android.synthetic.main.activity_add_category_page.*
import retrofit2.Call
import retrofit2.Response

class AddCategoryPage : AppCompatActivity() {

    val gson = Gson()
    val type = object : TypeToken<BaseResponse>() {}.type
    var namecheck: Boolean = false
    var activation: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_category_page)

        val backBtn = findViewById<TextView>(R.id.backBtn)
        backBtn.setOnClickListener {
            val intent = Intent(this, TestingMenu::class.java)
            startActivity(intent)
        }

        val toggle: ToggleButton = findViewById(R.id.toggleButton)
        toggle.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                toggleTrue()
            } else {
                toggleFalse()
            }
        }

        val saveBtn = findViewById<ImageButton>(R.id.saveBtn)
        saveBtn.setOnClickListener {

            if (categoryName.text.isEmpty()) {
                namaerror.text = "Field Name tidak boleh kosong"
                namaerror.setTextColor(Color.parseColor("#DC6A84"))
                namecheck = false
            } else if (categoryName.text.toString().trim().length >= 30) {
                namaerror.text = "Nama Kategori menu tidak boleh melebihi 30 karakter"
                namaerror.setTextColor(Color.parseColor("#DC6A84"))
                namecheck = false
            } else {
                namecheck = true
            }
            if (namecheck == true) {

                val size = intent.getStringExtra("category_size")
                val catSize = size.toInt()

                var sessionManager = SessionManager(application)
                val email = sessionManager.getUserData()!!.email!!
                val token = sessionManager.getUserToken()!!
                val timestamp = getTimestamp()
                val signature = getSignature(email, timestamp)
                val mid = sessionManager.getUserData()!!.mid!!
                var categoryReq = MenuCategoryRequest()
                categoryReq.category_name = categoryName.text.toString()
                categoryReq.category_order = catSize + 1
                categoryReq.activation = activation

                PikappApiService().api.menuCategory(
                    getUUID(), timestamp, getClientID(), signature, token, mid, categoryReq
                ).enqueue(object : retrofit2.Callback<BaseResponse> {
                    override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                        Toast.makeText(this@AddCategoryPage, "failed", Toast.LENGTH_SHORT).show()
                    }

                    override fun onResponse(
                        call: Call<BaseResponse>,
                        response: Response<BaseResponse>
                    ) {
                        if (response.code() == 200 && response.body()!!.errCode.toString() == "EC0000") {
                            Toast.makeText(this@AddCategoryPage, "added", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@AddCategoryPage, TestingMenu::class.java)
                            startActivity(intent)
                        } else {
                            var errorResponse: BaseResponse? =
                                gson.fromJson(response.errorBody()!!.charStream(), type)
                            Toast.makeText(
                                this@AddCategoryPage,
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
        }
    }

    fun toggleTrue() {
        activation = true
    }

    fun toggleFalse() {
        activation = false
    }

}