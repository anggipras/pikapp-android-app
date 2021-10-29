package com.tsab.pikapp.view.other.otherReport

import android.app.Activity.RESULT_OK
import android.content.ContentResolver
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentUploadReportBinding
import com.tsab.pikapp.viewmodel.other.ReportViewModel
import java.io.InputStream

class UploadReportFragment : Fragment() {

    private lateinit var dataBinding: FragmentUploadReportBinding
    private val viewModel: ReportViewModel by activityViewModels()
    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var adapter: UploadReportAdapter
    private lateinit var navController: NavController
    val uploadCount: ArrayList<uploadData> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = FragmentUploadReportBinding.inflate(inflater, container, false)
        return dataBinding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode < 21 && resultCode == RESULT_OK && data != null){
            var path: Uri = data.data!!
            var validateShopee: Boolean = false
            var validateGofood: Boolean = false
            var inputStream: InputStream = requireActivity().contentResolver.openInputStream(path)!!
            var byte: ByteArray = ByteArray(inputStream.available())
            inputStream.read(byte)
            if(getFileName(path, requireActivity().contentResolver).substringAfterLast(".") == "csv"){
                validateShopee = true
            }else{
                validateShopee = false
            }

            validateGofood = getFileName(path, requireActivity().contentResolver).substringAfterLast(".") == "xlsx"
            uploadCount[requestCode] = uploadData(getFileName(path, requireActivity().contentResolver), validateShopee, validateGofood, path)
            adapter.notifyDataSetChanged()
        }
    }

    fun getFileName(uri: Uri, resolver: ContentResolver): String{
        var result: String = ""
        if(uri.scheme != null && uri.scheme .equals("content")){
            var cursor: Cursor = resolver.query(uri, null, null, null, null)!!
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor.close()
            }
        }
        if (result == null) {
            result = uri.path!!
            val cut = result.lastIndexOf('/')
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(uploadCount.isEmpty()){
            uploadCount.add(uploadData("", false, false))
        }

        navController = Navigation.findNavController(view)

        linearLayoutManager = LinearLayoutManager(requireView().context, LinearLayoutManager.VERTICAL, false)
        dataBinding.uploadList.layoutManager = linearLayoutManager
        adapter = UploadReportAdapter(this, uploadCount, requireContext(), dataBinding.btnNext,dataBinding.loadingOverlay, dataBinding.btnPlus, requireActivity(), viewModel.getMid(), view)
        dataBinding.uploadList.adapter = adapter

        dataBinding.backButton.setOnClickListener {
            navController?.navigateUp()
        }

        dataBinding.btnPlus.setOnClickListener {
            if(uploadCount.size == 9){
                dataBinding.btnPlus.setBackgroundResource(R.drawable.button_dark_gray)
                dataBinding.btnNext.setBackgroundResource(R.drawable.button_dark_gray)
                dataBinding.btnNext.isClickable = false
                dataBinding.btnNext.isEnabled = false
                dataBinding.btnNext.isFocusable = false
                uploadCount.add(uploadData("", false, false))
                adapter.notifyItemInserted(uploadCount.size - 1)
            }else if(uploadCount.size < 9){
                dataBinding.btnNext.setBackgroundResource(R.drawable.button_dark_gray)
                dataBinding.btnNext.isClickable = false
                dataBinding.btnNext.isEnabled = false
                dataBinding.btnNext.isFocusable = false
                uploadCount.add(uploadData("", false, false))
                adapter.notifyItemInserted(uploadCount.size - 1)
            }
        }

        dataBinding.statusUpload.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_uploadReportFragment_to_uploadStatusFragment)
        }
    }

    data class uploadData(var namaFile: String, var shopee: Boolean = false, var gofood: Boolean = false, var nfile: Uri? = null)
}