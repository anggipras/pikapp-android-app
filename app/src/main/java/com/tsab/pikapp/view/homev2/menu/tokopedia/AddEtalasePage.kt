package com.tsab.pikapp.view.homev2.menu.tokopedia

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentAddEtalasePageBinding
import com.tsab.pikapp.viewmodel.homev2.EtalaseViewModel
import kotlinx.android.synthetic.main.fragment_edit_etalase_page.*

class AddEtalasePage : Fragment() {
    private var navController: NavController? = null
    private lateinit var dataBinding: FragmentAddEtalasePageBinding
    private val viewModel: EtalaseViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        dataBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_add_etalase_page,
            container, false
        )
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        observeViewModel()
        attachInputListeners()
    }

    private fun observeViewModel(){
        viewModel.namaCategoryError.observe(viewLifecycleOwner, Observer { namaCategoryError ->
            dataBinding.namaerror.text = if (namaCategoryError.isEmpty()) "" else namaCategoryError
        })
    }

    private fun attachInputListeners(){
        dataBinding.backBtn.setOnClickListener {
            navController?.navigate(R.id.action_addEtalasePage_to_etalaseListPage)
        }

        dataBinding.toggleButton.setOnCheckedChangeListener{ _, isChecked ->
            if (isChecked){
                Toast.makeText(context, "true", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "false", Toast.LENGTH_SHORT).show()
            }
        }

        dataBinding.saveBtn.setOnClickListener {
            viewModel.validateNama(etalaseName.text.toString())
            if (viewModel.validateNama(etalaseName.text.toString())){
                Toast.makeText(context, "etalase name: ${etalaseName.text}", Toast.LENGTH_SHORT).show()
            }
        }
    }

}