package com.tsab.pikapp.view.homev2.transaction.manualTxn

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.tsab.pikapp.R
import com.tsab.pikapp.models.model.AddManualAdvMenu
import com.tsab.pikapp.viewmodel.homev2.ManualTxnViewModel
import com.tsab.pikapp.viewmodel.homev2.MenuViewModel

class ManualTxnActivity : AppCompatActivity() {
    companion object {
        const val MENU_LIST_DATA = "menu_list_data"
        const val MENU_QTY = "quantity"
        const val MENU_PRICE = "menu_price"
        const val MENU_TOTAL_PRICE = "menu_total_price"
        const val MANUAL_TXN_ACT = "action to hit api"
    }

    private val viewModel: MenuViewModel by viewModels()
    private val viewModelManualTxn: ManualTxnViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.bottom_up, R.anim.no_animation)
        setContentView(R.layout.activity_manual_txn)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val maknyus = data?.getSerializableExtra("MENU_LIST_BRO") as? List<AddManualAdvMenu>
        if (resultCode == RESULT_OK) {
            val menuListData = data?.getSerializableExtra("MENU_LIST_DATA") as? List<AddManualAdvMenu>
            val menuQty = data?.getIntExtra("MENU_QTY", 0)
            val menuPrice = data?.getStringExtra("MENU_PRICE")
            val menuTotalPrice = data?.getLongExtra("MENU_TOTAL_PRICE", 0)
            val manualTxnAct = data?.getIntExtra("MANUAL_TXN_ACT", 0)

            if (!menuListData.isNullOrEmpty()) {
                viewModelManualTxn.setSelectedMenu(menuListData)
                viewModelManualTxn.addTotalItems(menuQty ?: 0)
                viewModelManualTxn.cartTotalPrice(menuTotalPrice.toString(), menuPrice.toString())
            }
            viewModel.mutableManualTransAct.value = manualTxnAct
        }
    }
}