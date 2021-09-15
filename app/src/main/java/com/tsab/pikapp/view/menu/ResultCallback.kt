package com.tsab.pikapp.view.menu

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.tsab.pikapp.view.AdvanceMenuActivity

class ResultCallback : ActivityResultContract<String, String>() {
    override fun createIntent(context: Context, input: String?):
            Intent = Intent(context, AdvanceMenuActivity::class.java).apply {
        putExtra("item", input)
    }

    override fun parseResult(resultCode: Int, intent: Intent?):
            String? = when {
        resultCode != Activity.RESULT_OK -> null
        else -> intent?.getStringExtra("RESULT_DATA")
    }
}