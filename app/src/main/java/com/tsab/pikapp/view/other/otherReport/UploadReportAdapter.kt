package com.tsab.pikapp.view.other.otherReport

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.LayoutLoadingOverlayBinding
import com.tsab.pikapp.models.model.LogisticsDetailOmni
import com.tsab.pikapp.models.model.OrderDetailOmni
import com.tsab.pikapp.models.model.ProductDetailOmni
import com.tsab.pikapp.view.homev2.Transaction.OmniTransactionListAdapter
import kotlinx.android.synthetic.main.delete_dialog.view.*
import kotlinx.android.synthetic.main.tokped_reject_popup.view.*
import kotlinx.android.synthetic.main.tokped_reject_popup.view.dialog_tokped_back
import kotlinx.android.synthetic.main.tokped_reject_popup.view.dialog_tokped_close
import kotlinx.android.synthetic.main.tokped_reject_popup.view.dialog_tokped_ok
import kotlinx.android.synthetic.main.upload_file_popup.view.*
import kotlinx.android.synthetic.main.upload_file_popup.view.dialog_upload_back
import kotlinx.android.synthetic.main.upload_file_popup.view.dialog_upload_ok
import kotlinx.android.synthetic.main.upload_report_list.view.*

class UploadReportAdapter(
        private var uploadFragment: Fragment,
        private var uploadTotal: ArrayList<UploadReportFragment.uploadData>,
        private var uploadContext: Context,
        private var nextBtn: Button,
        private var viewLoading:LayoutLoadingOverlayBinding,
        private var addBtn: Button,
        private var uploadActivity: Activity
): RecyclerView.Adapter<UploadReportAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var uploadBtn: Button = itemView.fileName
        var shopee: RadioButton = itemView.shopee
        var merchant: RadioGroup = itemView.radioReport
        var uploadImg: ImageView = itemView.emptyFile
        var deleteImg: ImageView = itemView.delete
        var removeFile: ImageView = itemView.closeFile
        var gofood: RadioButton = itemView.gofood
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UploadReportAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.upload_report_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: UploadReportAdapter.ViewHolder, position: Int) {
        var count: Int = 0
        if(uploadTotal[position].namaFile != ""){
            holder.uploadImg.visibility = View.INVISIBLE
            holder.removeFile.visibility = View.VISIBLE
            if (uploadTotal[position].namaFile.substringAfterLast(".") == "csv"){
                holder.shopee.isChecked =true
                uploadTotal[position].shopee = true
                uploadTotal[position].gofood = false
            }else if (uploadTotal[position].namaFile.substringAfterLast(".") == "xlsx"){
                holder.gofood.isChecked = true
                uploadTotal[position].gofood = true
                uploadTotal[position].shopee = false
            }
        }

        if(!uploadTotal[position].shopee && !uploadTotal[position].gofood){
            holder.merchant.clearCheck()
        }else{
            if(uploadTotal[position].shopee){
                holder.merchant.check(R.id.shopee)
            }else if(uploadTotal[position].gofood){
                holder.merchant.check(R.id.gofood)
            }
        }

        if (uploadTotal[position].namaFile == ""){
            holder.uploadImg.visibility = View.VISIBLE
            holder.removeFile.visibility = View.INVISIBLE
        }

        if(position == 0){
            holder.deleteImg.visibility = View.GONE
        }

        holder.deleteImg.setOnClickListener {
            deleteDialog(position, holder.merchant)
        }

        holder.removeFile.setOnClickListener {
            holder.uploadImg.visibility = View.VISIBLE
            holder.removeFile.visibility = View.INVISIBLE
            nextBtn.setBackgroundResource(R.drawable.button_dark_gray)
            nextBtn.isClickable = false
            nextBtn.isEnabled = false
            nextBtn.isFocusable = false
            uploadTotal[position].namaFile = ""
            notifyDataSetChanged()
        }

        holder.uploadBtn.setText(uploadTotal[position].namaFile)
        holder.uploadBtn.setOnClickListener {
            if(!holder.gofood.isChecked && !holder.shopee.isChecked){
                Toast.makeText(uploadContext, "Silahkan Pilih Tipe Report", Toast.LENGTH_SHORT).show()
            }else if(holder.gofood.isChecked){
                var intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                intent = Intent.createChooser(intent, "Report Upload")
                uploadFragment.startActivityForResult(intent, position)
            }else if(holder.shopee.isChecked) {
                var intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.setType("text/csv")
                intent = Intent.createChooser(intent, "Report Upload")
                uploadFragment.startActivityForResult(intent, position)
            }
        }

        for(file in uploadTotal){
            if(file.namaFile != ""){
                count++
            }
            if(count == uploadTotal.size){
                nextBtn.setBackgroundResource(R.drawable.button_green_small)
                nextBtn.isClickable = true
                nextBtn.isEnabled = true
                nextBtn.isFocusable = true
            }
        }

        nextBtn.setOnClickListener {
            uploadDialog(position)
        }
    }

    override fun getItemCount(): Int {
        return uploadTotal.size
    }

    private fun empty(shopee: RadioButton, gojek: RadioButton, upload: ImageView, delete: ImageView){
        upload.visibility = View.VISIBLE
        delete.visibility = View.INVISIBLE
    }

    private fun uploadDialog(position: Int) {
        val mDialogView = LayoutInflater.from(uploadActivity).inflate(R.layout.upload_file_popup, null)
        val mBuilder = AlertDialog.Builder(uploadActivity)
                .setView(mDialogView)
        val mAlertDialog = mBuilder.show()
        mAlertDialog.window?.setBackgroundDrawable(
                AppCompatResources.getDrawable(
                        uploadActivity,
                        R.drawable.dialog_background
                )
        )

        mDialogView.dialog_upload_ok.setOnClickListener {
            Toast.makeText(uploadContext, "File Sukses Di Upload", Toast.LENGTH_SHORT).show()
            mAlertDialog.dismiss()
        }

        mDialogView.dialog_upload_back.setOnClickListener {
            mAlertDialog.dismiss()
        }
        mDialogView.dialog_upload_close.setOnClickListener {
            mAlertDialog.dismiss()
        }
    }

    private fun deleteDialog(position: Int, merchant: RadioGroup) {
        val mDialogView = LayoutInflater.from(uploadActivity).inflate(R.layout.delete_dialog, null)
        val mBuilder = AlertDialog.Builder(uploadActivity)
                .setView(mDialogView)
        val mAlertDialog = mBuilder.show()
        mAlertDialog.window?.setBackgroundDrawable(
                AppCompatResources.getDrawable(
                        uploadActivity,
                        R.drawable.dialog_background
                )
        )

        mDialogView.dialog_delete_ok.setOnClickListener {
            viewLoading.loadingView.visibility = View.VISIBLE
            uploadTotal.removeAt(position)
            addBtn.setBackgroundResource(R.drawable.button_green_small)
            addBtn.isClickable = true
            addBtn.isEnabled = true
            addBtn.isFocusable = true
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, uploadTotal.size)
            notifyDataSetChanged()
            mAlertDialog.dismiss()
            Handler().postDelayed({
                viewLoading.loadingView.visibility = View.GONE
            }, 1000)
        }

        mDialogView.dialog_delete_back.setOnClickListener {
            mAlertDialog.dismiss()
        }
        mDialogView.dialog_delete_close.setOnClickListener {
            mAlertDialog.dismiss()
        }
    }
}