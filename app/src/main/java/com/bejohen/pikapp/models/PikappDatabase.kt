package com.bejohen.pikapp.models

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.bejohen.pikapp.models.model.ItemHomeBannerSlider
import com.bejohen.pikapp.models.model.ItemHomeCategory

@Database(entities = arrayOf(ItemHomeBannerSlider::class, ItemHomeCategory::class), version = 1)
abstract class PikappDatabase : RoomDatabase() {
    abstract fun pikappDao(): PikappDao

    companion object {
        @Volatile
        private var instance: PikappDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            PikappDatabase::class.java,
            "PikappDatabase"
        ).build()
    }
}