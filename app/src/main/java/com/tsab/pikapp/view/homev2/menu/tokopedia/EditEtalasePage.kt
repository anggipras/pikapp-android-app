package com.tsab.pikapp.view.homev2.menu.tokopedia

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentEditCategoryPageBinding
import com.tsab.pikapp.databinding.FragmentEditEtalasePageBinding
import kotlinx.android.synthetic.main.delete_category_popup.view.*
import kotlinx.android.synthetic.main.delete_etalase_popup.view.*
import kotlinx.android.synthetic.main.fragment_add_category_page.*
import kotlinx.android.synthetic.main.fragment_edit_etalase_page.*

class EditEtalasePage : Fragment() {

    private var navController: NavController? = null
    private lateinit var dataBinding: FragmentEditEtalasePageBinding
    var size = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        dataBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_edit_etalase_page,
            container, false
        )
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //navController = Navigation.findNavController(view)
        //dataBinding.categoryName.append(viewModel.namaCatagory.value.toString())

        observeViewModel()
        attachInputListeners()
    }

    private fun observeViewModel() {
        TODO("Not yet implemented")
    }

    private fun attachInputListeners(){
        dataBinding.backBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }

        dataBinding.deleteEtalase.setOnClickListener {
            if (size == 0){
                openDeleteDialogNoMenu()
            } else {
                openDeleteDialogMenu()
            }
        }

        dataBinding.toggleButton.setOnCheckedChangeListener{ _, isChecked ->
            if (isChecked){
                Toast.makeText(context, "true", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "false", Toast.LENGTH_SHORT).show()
            }
        }

        dataBinding.saveBtn.setOnClickListener {
            Toast.makeText(context, "Nama Makanan :$etalaseName", Toast.LENGTH_SHORT).show()
        }

    }

    private fun openDeleteDialogNoMenu(){
        val mDialogView = LayoutInflater.from(requireActivity()).inflate(R.layout.delete_etalase_popup, null)
        val mBuilder = AlertDialog.Builder(requireActivity())
            .setView(mDialogView)
        val mAlertDialog = mBuilder.show()
        mAlertDialog.window?.setBackgroundDrawable(
            AppCompatResources.getDrawable(
                requireActivity(),
                R.drawable.dialog_background
            )
        )
        mDialogView.dialog_etalase_back.setOnClickListener {
            mAlertDialog.dismiss()
        }
        mDialogView.dialog_etalase_close.setOnClickListener {
            mAlertDialog.dismiss()
        }
        mDialogView.dialog_etalase_ok.setOnClickListener {
            Toast.makeText(context, "clicked", Toast.LENGTH_SHORT).show()
            mAlertDialog.dismiss()

        }
    }

    private fun openDeleteDialogMenu(){
        val mDialogView = LayoutInflater.from(requireActivity()).inflate(R.layout.delete_etalase_menu_popup, null)
        val mBuilder = AlertDialog.Builder(requireActivity())
            .setView(mDialogView)
        val mAlertDialog = mBuilder.show()
        mAlertDialog.window?.setBackgroundDrawable(
            AppCompatResources.getDrawable(
                requireActivity(),
                R.drawable.dialog_background
            )
        )
        mDialogView.dialog_etalase_back.setOnClickListener {
            mAlertDialog.dismiss()
        }
    }

}