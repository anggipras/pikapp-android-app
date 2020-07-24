package com.bejohen.pikapp.models

import androidx.room.Dao
import androidx.room.Insert

@Dao
interface PikappDao {
    @Insert
    suspend fun insertUser(vararg user: LoginResponse) : Long
}