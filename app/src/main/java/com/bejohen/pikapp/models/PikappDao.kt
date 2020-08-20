package com.bejohen.pikapp.models

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.bejohen.pikapp.models.model.ItemHomeBannerSlider
import com.bejohen.pikapp.models.model.ItemHomeCategory

@Dao
interface PikappDao {

    //Home Banner Slider
    @Insert
    suspend fun insertAllHomeBannerSlider(vararg category: ItemHomeBannerSlider) : List<Long>

    @Query("SELECT * FROM ItemHomeBannerSlider")
    suspend fun getAllHomeBannerSlider(): List<ItemHomeBannerSlider>

    @Query("SELECT * FROM ItemHomeBannerSlider WHERE uuid = :uuid")
    suspend fun getHomeBannerSlider(uuid: Int): ItemHomeBannerSlider

    @Query("DELETE FROM ItemHomeBannerSlider")
    suspend fun deleteAllHomeBannerSlider()

    //Home Category
    @Insert
    suspend fun insertAllHomeCategory(vararg category: ItemHomeCategory) : List<Long>

    @Query("SELECT * FROM itemHomeCategory")
    suspend fun getAllHomeCategory(): List<ItemHomeCategory>

    @Query("SELECT * FROM itemHomeCategory WHERE uuid = :uuid")
    suspend fun getHomeCategory(uuid: Int): ItemHomeCategory

    @Query("DELETE FROM itemHomeCategory")
    suspend fun deleteAllHomeCategory()

}