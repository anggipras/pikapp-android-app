package com.tsab.pikapp.view.homev2.transaction.manualTxn

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentCustomerFindLocationBinding
import com.tsab.pikapp.models.model.CurrentLatLng
import com.tsab.pikapp.view.other.otherSettings.shippingSetting.GoogleListPlacesAdapter
import com.tsab.pikapp.viewmodel.homev2.ManualTxnViewModel

class CustomerFindLocationFragment : Fragment(), GoogleListPlacesAdapter.OnPlaceClickListener {
    private lateinit var dataBinding: FragmentCustomerFindLocationBinding
    private val viewModel: ManualTxnViewModel by activityViewModels()
    private lateinit var recyclerAdapter: GoogleListPlacesAdapter

    var delay: Long = 500 // 1 seconds after user stops typing
    var lastTextEdit: Long = 0
    var handler: Handler = Handler()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_customer_find_location,
            container, false
        )
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataBinding.headerInsideSettings.headerTitle.text = getString(R.string.find_location_title)
        dataBinding.headerInsideSettings.backImage.setOnClickListener {
            Navigation.findNavController(view).navigateUp()
        }
        dataBinding.findLocation.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || event == null || event.keyCode == KeyEvent.KEYCODE_ENTER) {
                hideKeyboard()
            }
            true
        }
        viewModel.setLiveDataPlaces(arrayListOf())

        initRecyclerView()
        initViewModel()

        dataBinding.findLocation.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                //You need to remove this to run only once
                handler.removeCallbacks(inputFinishChecker)
            }

            override fun afterTextChanged(s: Editable) {
                //avoid triggering event when text is empty
                if (s.isNotEmpty()) {
                    lastTextEdit = System.currentTimeMillis()
                    handler.postDelayed(inputFinishChecker, delay)
                }
            }

        })
    }

    private fun initViewModel() {
        viewModel.getLiveDataGooglePlacesListObserver().observe(viewLifecycleOwner, {
            if (it != null) {
                recyclerAdapter.setPlaces(it)
            }
        })
    }

    private fun initRecyclerView() {
        dataBinding.recyclerviewGooglePlaces.layoutManager = LinearLayoutManager(requireView().context, LinearLayoutManager.VERTICAL, false)
        recyclerAdapter = GoogleListPlacesAdapter(this)
        dataBinding.recyclerviewGooglePlaces.adapter = recyclerAdapter
    }

    private val inputFinishChecker = Runnable {
        if (System.currentTimeMillis() > lastTextEdit + delay - 200) {
            viewModel.getListGooglePlaces(dataBinding.findLocation.text.toString())
        }
    }

    private fun hideKeyboard() {
        val inputManager: InputMethodManager = activity?.getSystemService(
            Activity.INPUT_METHOD_SERVICE
        ) as InputMethodManager

        if (inputManager.isAcceptingText) {
            inputManager.hideSoftInputFromWindow(activity?.currentFocus?.windowToken, 0)
        }
    }

    override fun onPlaceClick(lat: Double, lng: Double) {
        viewModel.setCurrentLocation(CurrentLatLng(latitude = lat, longitude = lng))
        view?.let { Navigation.findNavController(it).navigateUp() }
    }
}