package com.tsab.pikapp.view.other.otherReport

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.tsab.pikapp.R
import kotlinx.android.synthetic.main.tokped_reject_popup.view.*
import kotlinx.android.synthetic.main.tokped_reject_popup.view.dialog_tokped_back
import kotlinx.android.synthetic.main.tokped_reject_popup.view.dialog_tokped_close
import kotlinx.android.synthetic.main.tokped_reject_popup.view.dialog_tokped_ok
import kotlinx.android.synthetic.main.upload_file_popup.view.*
import kotlinx.android.synthetic.main.upload_report_list.view.*

class UploadReportAdapter(
        private var uploadFragment: Fragment,
        private var uploadTotal: ArrayList<String>,
        private var uploadContext: Context,
        private var nextBtn: Button,
        private var uploadActivity: Activity
): RecyclerView.Adapter<UploadReportAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var uploadBtn: Button = itemView.fileName
        var shopee: RadioButton = itemView.shopee
        var uploadImg: ImageView = itemView.emptyFile
        var removeFile: ImageView = itemView.closeFile
        var gofood: RadioButton = itemView.gofood
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UploadReportAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.upload_report_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: UploadReportAdapter.ViewHolder, position: Int) {
        var count: Int = 0
        if(uploadTotal[position] != ""){
            holder.uploadImg.visibility = View.INVISIBLE
            holder.removeFile.visibility = View.VISIBLE
        }

        holder.shopee.setOnCheckedChangeListener { buttonView, isChecked ->
            if(uploadTotal[position] != ""){
                uploadTotal[position] = ""
                notifyDataSetChanged()
                nextBtn.setBackgroundResource(R.drawable.button_dark_gray)
                nextBtn.isClickable = false
                nextBtn.isEnabled = false
                nextBtn.isFocusable = false
                holder.uploadImg.visibility = View.VISIBLE
                holder.removeFile.visibility = View.INVISIBLE
                Toast.makeText(uploadContext, "Silahkan Pilih Report Yang Sesuai", Toast.LENGTH_SHORT).show()
            }
        }

        holder.gofood.setOnCheckedChangeListener { buttonView, isChecked ->
            if(uploadTotal[position] != ""){
                uploadTotal[position] = ""
                notifyDataSetChanged()
                nextBtn.setBackgroundResource(R.drawable.button_dark_gray)
                nextBtn.isClickable = false
                nextBtn.isEnabled = false
                nextBtn.isFocusable = false
                holder.uploadImg.visibility = View.VISIBLE
                holder.removeFile.visibility = View.INVISIBLE
                Toast.makeText(uploadContext, "Silahkan Pilih Report Yang Sesuai", Toast.LENGTH_SHORT).show()
            }
        }

        holder.removeFile.setOnClickListener {
            holder.uploadImg.visibility = View.VISIBLE
            holder.removeFile.visibility = View.INVISIBLE
            nextBtn.setBackgroundResource(R.drawable.button_dark_gray)
            nextBtn.isClickable = false
            nextBtn.isEnabled = false
            nextBtn.isFocusable = false
            uploadTotal[position] = ""
            notifyDataSetChanged()
        }

        holder.uploadBtn.setText(uploadTotal[position])
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
            if(file != ""){
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

}