package com.tsab.pikapp.view.homev2.transaction.shipment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import com.tsab.pikapp.R
import kotlinx.android.synthetic.main.dialog_resi_tokopedia.*
import timber.log.Timber

class ResiTokopediaDialogFragment : DialogFragment() {
    companion object {
        const val tag = "SubmitResiDialog"
    }

    private lateinit var scanResiLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.dialog_resi_tokopedia, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        scanResiLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val resultData = result.data?.getStringExtra(ScanResiActivity.RESULT)
                    handleResi(resultData)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        attachInputListeners()
    }

    private fun attachInputListeners() {
        closeButton.setOnClickListener {
            dialog?.dismiss()
        }

        resiInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                handleResi(s.toString(), false)
            }
        })

        scanButton.setOnClickListener {
            onScanResi()
        }

        konfirmasiButton.setOnClickListener {
            Timber.tag(javaClass.simpleName).d("Konfirmasi Clicked!")
        }
    }

    private fun onScanResi() {
        Intent(activity, ScanResiActivity::class.java).apply {
            scanResiLauncher.launch(this)
        }
    }

    /**
     * Method to handle resi input from text input or from scan activity.
     * @param resiData nullable string for raw input.
     * @param isScan boolean whether the input comes from scan activity or not.
     */
    private fun handleResi(resiData: String?, isScan: Boolean = true) {
        if (resiData.isNullOrEmpty()) {
            Timber.tag(javaClass.simpleName).d("Scan Result Empty!")
            konfirmasiButton.isEnabled = false
            return
        }

        val resi = if (isScan) parseResi(resiData) else resiData

        Timber.tag(javaClass.simpleName).d("Scan Result: $resi")
        if (isScan) resiInput.setText(resi)

        konfirmasiButton.isEnabled = validateResi(resi)
    }

    /**
     * Parse raw resi input from scan activity. Extract the resi number only.
     * @param resiData nullable string for raw input.
     */
    private fun parseResi(resiData: String?): String {
        if (resiData.isNullOrEmpty()) return ""

        // TODO: Extract resi number from the raw input.
        return resiData
    }

    /**
     * Validate whether the resi is valid or not.
     * @param resi string that is already extracted.
     */
    private fun validateResi(resi: String): Boolean {
        // TODO: Do resi validation with API, etc.
        return true
    }
}