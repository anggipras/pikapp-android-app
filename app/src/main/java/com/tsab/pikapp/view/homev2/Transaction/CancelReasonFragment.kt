package com.tsab.pikapp.view.homev2.Transaction

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.activityViewModels
import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialogFragment
import com.tsab.pikapp.R
import com.tsab.pikapp.util.SessionManager
import com.tsab.pikapp.view.homev2.HomeActivity
import com.tsab.pikapp.viewmodel.homev2.TransactionViewModel
import kotlinx.android.synthetic.main.cancel_dialog.view.*
import kotlinx.android.synthetic.main.cancel_reason_fragment.*

class CancelReasonFragment: RoundedBottomSheetDialogFragment() {

    private val viewModel: TransactionViewModel by activityViewModels()
    private val sessionManager = SessionManager()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.cancel_reason_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        closeBtn.setOnClickListener {
            dismiss()
        }

        btnNext.setOnClickListener {
            openCancelDialog()
        }

    }

    private fun openCancelDialog() {
        val mDialogView = LayoutInflater.from(requireActivity()).inflate(R.layout.cancel_dialog, null)
        val mBuilder = AlertDialog.Builder(requireActivity())
                .setView(mDialogView)
        val mAlertDialog = mBuilder.show()
        mAlertDialog.window?.setBackgroundDrawable(
            AppCompatResources.getDrawable(
                requireActivity(),
                R.drawable.dialog_background
            )
        )
        mDialogView.dialog_back.setOnClickListener {
            mAlertDialog.dismiss()
        }
        mDialogView.dialog_close.setOnClickListener {
            mAlertDialog.dismiss()
        }
        mDialogView.dialog_ok.setOnClickListener {
            Log.e("ID", arguments?.getString("TransactionID").toString())
            viewModel.postUpdate(arguments?.getString("TransactionID").toString(), "FAILED")
            sessionManager.transactionUpdate()
            val intent = Intent(activity?.baseContext, HomeActivity::class.java)
            activity?.startActivity(intent)
        }
    }

}