package com.tsab.pikapp.view.other.otherReport

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentFilterPageReportBinding
import com.tsab.pikapp.util.SessionManager
import com.tsab.pikapp.viewmodel.other.ReportViewModel
import kotlinx.android.synthetic.main.fragment_filter_page_report.*
import kotlinx.android.synthetic.main.item_store_my_product_list.*
import java.text.SimpleDateFormat
import java.util.*

class FilterPageReport : Fragment() {

    private lateinit var dataBinding: FragmentFilterPageReportBinding
    private val viewModel: ReportViewModel by activityViewModels()
    private val sessionManager = SessionManager()
    private var date = "today"
    private var sdate = ""
    private var edate = ""
    private var startDate = ""
    private var endDate = ""
    private val sdf = SimpleDateFormat("MM/dd/yyyy", Locale.UK)
    var cal = Calendar.getInstance()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        dataBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_filter_page_report,
            container, false
        )
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        attatchInputListener()
    }

    private fun attatchInputListener() {
        dateSelection()

        dataBinding.btnNext.setOnClickListener {
            if (date == ""){
                if (endDate < startDate || startDate == "" || endDate == ""){
                    dataBinding.btnNext.isEnabled = false
                    Toast.makeText(context, "invalid start date and end date", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "$startDate - $endDate", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "filter : $date", Toast.LENGTH_SHORT).show()
            }
        }

        dataBinding.startDate.setOnClickListener {
            dataBinding.startDate.transformIntoDatePicker(requireContext(), "MM/dd/yyyy", "start")
        }

        dataBinding.endDate.setOnClickListener {
            dataBinding.endDate.transformIntoDatePicker(requireContext(), "MM/dd/yyyy", "end")
        }

    }

    fun EditText.transformIntoDatePicker(context: Context, format: String, status: String, maxDate: Date? = null) {
        isFocusableInTouchMode = false
        isClickable = true
        isFocusable = false

        val myCalendar = Calendar.getInstance()
        val datePickerOnDataSetListener =
                DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    myCalendar.set(Calendar.YEAR, year)
                    myCalendar.set(Calendar.MONTH, monthOfYear)
                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    setText(sdf.format(myCalendar.time))
                    dataBinding.btnNext.isEnabled = true
                    if (status == "start"){
                        startDate = sdf.format(myCalendar.time)
                        viewModel.getStartDate(startDate)
                    } else if (status == "end"){
                        endDate = sdf.format(myCalendar.time)
                        viewModel.getEndDate(endDate)
                    }
                }

        setOnClickListener {
            DatePickerDialog(
                    context, datePickerOnDataSetListener, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)
            ).run {
                maxDate?.time?.also { datePicker.maxDate = it }
                show()
            }
        }
    }

    private fun dateSelection() {
        dataBinding.today.setOnClickListener {
            clearSelection()
            cal = Calendar.getInstance()
            date = sdf.format(cal.time)
            viewModel.getStartDate(date)
            viewModel.getEndDate(date)
            today.setBackgroundResource(R.drawable.button_green_square)
            today.setTextColor(Color.parseColor("#ffffff"))
        }

        dataBinding.yesterday.setOnClickListener {
            clearSelection()
            cal = Calendar.getInstance()
            cal.add(Calendar.DATE, - 1)
            date = sdf.format(cal.time)
            viewModel.getStartDate(date)
            viewModel.getEndDate(date)
            yesterday.setBackgroundResource(R.drawable.button_green_square)
            yesterday.setTextColor(Color.parseColor("#ffffff"))
        }

        dataBinding.last2day.setOnClickListener {
            clearSelection()
            cal = Calendar.getInstance()
            cal.add(Calendar.DATE, - 2)
            date = sdf.format(cal.time)
            viewModel.getStartDate(date)
            viewModel.getEndDate(date)
            last2day.setBackgroundResource(R.drawable.button_green_square)
            last2day.setTextColor(Color.parseColor("#ffffff"))
        }

        dataBinding.thisWeek.setOnClickListener {
            clearSelection()
            cal = Calendar.getInstance()
            cal.set(Calendar.DAY_OF_WEEK, 2)
            sdate = sdf.format(cal.time)
            viewModel.getStartDate(sdate)
            edate = sdf.format(Calendar.getInstance().time)
            viewModel.getEndDate(edate)
            date = "$sdate - $edate"
            thisWeek.setBackgroundResource(R.drawable.button_green_square)
            thisWeek.setTextColor(Color.parseColor("#ffffff"))
        }

        dataBinding.lastWeek.setOnClickListener {
            clearSelection()
            cal = Calendar.getInstance()
            cal.set(Calendar.DAY_OF_WEEK, 2)
            cal.add(Calendar.DATE, -7)
            sdate = sdf.format(cal.time)
            viewModel.getStartDate(sdate)
            cal = Calendar.getInstance()
            cal.set(Calendar.DAY_OF_WEEK, 1)
            edate = sdf.format(cal.time)
            viewModel.getEndDate(edate)
            date = "$sdate - $edate"
            lastWeek.setBackgroundResource(R.drawable.button_green_square)
            lastWeek.setTextColor(Color.parseColor("#ffffff"))
        }

        dataBinding.thisMonth.setOnClickListener {
            clearSelection()
            cal = Calendar.getInstance()
            cal.set(Calendar.DAY_OF_MONTH, 1)
            sdate = sdf.format(cal.time)
            viewModel.getStartDate(sdate)
            edate = sdf.format(Calendar.getInstance().time)
            viewModel.getEndDate(edate)
            date = "$sdate - $edate"
            thisMonth.setBackgroundResource(R.drawable.button_green_square)
            thisMonth.setTextColor(Color.parseColor("#ffffff"))
        }

        dataBinding.lastMonth.setOnClickListener {
            clearSelection()
            cal = Calendar.getInstance()
            cal.set(Calendar.DAY_OF_MONTH, 1)
            cal.add(Calendar.DATE, -30)
            sdate = sdf.format(cal.time)
            viewModel.getStartDate(sdate)
            cal = Calendar.getInstance()
            cal.add(Calendar.MONTH, -1)
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
            edate = sdf.format(cal.time)
            viewModel.getEndDate(edate)
            date = "$sdate - $edate"
            lastMonth.setBackgroundResource(R.drawable.button_green_square)
            lastMonth.setTextColor(Color.parseColor("#ffffff"))
        }

        dataBinding.tvFilterCustom.setOnClickListener {
            clearSelection()
            dataBinding.startDate.backgroundTintList = null
            dataBinding.startDate.isEnabled = true
            dataBinding.endDate.backgroundTintList = null
            dataBinding.endDate.isEnabled = true
            filterCustom.setBackgroundResource(R.drawable.icon_filter_custom_on)
        }

    }

    private fun clearSelection(){
        today.setBackgroundResource(R.drawable.gray_square_btn)
        today.setTextColor(Color.parseColor("#aaaaaa"))
        yesterday.setBackgroundResource(R.drawable.gray_square_btn)
        yesterday.setTextColor(Color.parseColor("#aaaaaa"))
        last2day.setBackgroundResource(R.drawable.gray_square_btn)
        last2day.setTextColor(Color.parseColor("#aaaaaa"))
        thisWeek.setBackgroundResource(R.drawable.gray_square_btn)
        thisWeek.setTextColor(Color.parseColor("#aaaaaa"))
        lastWeek.setBackgroundResource(R.drawable.gray_square_btn)
        lastWeek.setTextColor(Color.parseColor("#aaaaaa"))
        thisMonth.setBackgroundResource(R.drawable.gray_square_btn)
        thisMonth.setTextColor(Color.parseColor("#aaaaaa"))
        lastMonth.setBackgroundResource(R.drawable.gray_square_btn)
        lastMonth.setTextColor(Color.parseColor("#aaaaaa"))
        filterCustom.setBackgroundResource(R.drawable.icon_filter_custom_off)
        dataBinding.btnNext.isEnabled = true
        dataBinding.startDate.setText("")
        dataBinding.endDate.setText("")
        dataBinding.startDate.backgroundTintList = context?.resources?.getColorStateList(R.color.borderSubtle)
        dataBinding.startDate.isEnabled = false
        dataBinding.endDate.backgroundTintList = context?.resources?.getColorStateList(R.color.borderSubtle)
        dataBinding.endDate.isEnabled = false
        date = ""
    }
}