package com.tsab.pikapp.models.jsonfiles

import android.content.Context
import java.io.File
import java.io.FileWriter
import java.io.IOException

class WriteJson {
    @Throws(IOException::class)
    fun save(context: Context, jsonString: String?) {
        val rootFolder = context.getExternalFilesDir(null)
        val jsonFile = File(rootFolder, "merchant_profile.json")
        val writer = FileWriter(jsonFile)
        writer.write(jsonString)
        writer.close()
    }

    @Throws(IOException::class)
    fun saveTwo(context: Context, jsonString: String?) {
        val rootFolder = context.getExternalFilesDir(null)
        val jsonFile = File(rootFolder, "merchant_shop_management.json")
        val writer = FileWriter(jsonFile)
        writer.write(jsonString)
        writer.close()
    }
}