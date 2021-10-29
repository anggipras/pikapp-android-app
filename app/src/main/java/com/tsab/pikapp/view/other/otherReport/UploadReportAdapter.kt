package com.tsab.pikapp.view.other.otherReport

import android.app.Activity
import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.FileUtils
import android.os.Handler
import android.util.Log
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
import androidx.room.util.FileUtil
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.LayoutLoadingOverlayBinding
import com.tsab.pikapp.models.model.LogisticsDetailOmni
import com.tsab.pikapp.models.model.OrderDetailOmni
import com.tsab.pikapp.models.model.ProductDetailOmni
import com.tsab.pikapp.models.model.UploadReportResponse
import com.tsab.pikapp.models.network.PikappApi
import com.tsab.pikapp.models.network.PikappApiService
import com.tsab.pikapp.util.getFileName
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
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class UploadReportAdapter(
        private var uploadFragment: Fragment,
        private var uploadTotal: ArrayList<UploadReportFragment.uploadData>,
        private var uploadContext: Context,
        private var nextBtn: Button,
        private var viewLoading:LayoutLoadingOverlayBinding,
        private var addBtn: Button,
        private var uploadActivity: Activity,
        private var mid: String
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
        
        if(position != 0){
            holder.merchant.visibility = View.INVISIBLE
        }

        holder.merchant.setOnCheckedChangeListener { group, checkedId ->
            if(checkedId == R.id.shopee){
                uploadTotal[position].shopee = true
                uploadTotal[position].gofood = false
            }else if (checkedId == R.id.gofood){
                uploadTotal[position].shopee = false
                uploadTotal[position].gofood = true
            }
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
            var gofoodStat: Boolean = uploadTotal[0].gofood
            var shopeeStat: Boolean = uploadTotal[0].shopee
            if(!gofoodStat && !shopeeStat){
                Toast.makeText(uploadContext, "Silahkan Pilih Tipe Report", Toast.LENGTH_SHORT).show()
            }else if(gofoodStat){
                var intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                intent = Intent.createChooser(intent, "Report Upload")
                uploadFragment.startActivityForResult(intent, position)
            }else if(shopeeStat) {
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
            viewLoading.loadingView.visibility = View.VISIBLE
            val lists: ArrayList<Uri> = ArrayList()
            var vendor: String = ""
            for (list in uploadTotal){
                if(list.nfile != null){
                    lists.add(list.nfile!!)
                }
            }
            if(uploadTotal[0].gofood){
                vendor = "GoFood"
            }else if(uploadTotal[0].shopee){
                vendor = "ShopeeFood"
            }
            uploadReport(lists, vendor, mid)
            mAlertDialog.dismiss()
            viewLoading.loadingView.visibility = View.INVISIBLE
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

    private fun uploadReport(files: List<Uri>, platform: String, mid: String){
        val lists: ArrayList<MultipartBody.Part> = ArrayList()
        var i: Int = 0
        for(uri in files){
            var fileRequest: MultipartBody.Part = prepareFile("files[]", uri)
            lists.add(fileRequest)
        }

//        val apiUpload = Retrofit.Builder().baseUrl("https://dev-report-api.pikapp.id/").addConverterFactory(GsonConverterFactory.create()).build().create(PikappApi::class.java)
        PikappApiService().reportApi.uploadReport(lists, RequestBody.create(MediaType.parse("multipart/form-data"), platform),
                RequestBody.create(MediaType.parse("multipart/form-data"), mid)).enqueue(object : Callback<UploadReportResponse>{
            override fun onResponse(call: Call<UploadReportResponse>, response: Response<UploadReportResponse>) {
                if(response.code() == 200){
                    Toast.makeText(uploadContext, "Report Berhasil Di Upload", Toast.LENGTH_SHORT).show()
                }else if(response.code() == 404){
                    Toast.makeText(uploadContext, "Report Sudah Pernah Di Upload", Toast.LENGTH_SHORT).show()
                    Log.e("Fail", response.code().toString())
                    Log.e("Fail", platform)
                }else if(response.code() == 500){
                    Toast.makeText(uploadContext, "Report Gagal Di Upload", Toast.LENGTH_SHORT).show()
                    Log.e("Fail", response.code().toString())
                    Log.e("Fail", platform)
                }
            }

            override fun onFailure(call: Call<UploadReportResponse>, t: Throwable) {
                Log.e("ehgiwe", t.toString())
            }

        })

    }

    private fun prepareFile(s: String, uri: Uri): MultipartBody.Part {
        val application = uploadActivity

        val reportParcelFileDescriptor = application.contentResolver.openFileDescriptor(
                uri,
                "r", null
        )
        val reportInputStream = FileInputStream(reportParcelFileDescriptor?.fileDescriptor)
        val reportFile = File(
                application.cacheDir, application.contentResolver.getFileName(
                uri
        )
        )
        val ktpOutputStream = FileOutputStream(reportFile)
        reportInputStream.copyTo(ktpOutputStream)
        var file: RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), reportFile)
        return MultipartBody.Part.createFormData(s, reportFile.name, file)
    }
}