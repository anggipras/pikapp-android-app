package com.tsab.pikapp.view.other.otherReport

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentFilterPageReportBinding
import com.tsab.pikapp.util.SessionManager
import com.tsab.pikapp.viewmodel.other.ReportViewModel
import kotlinx.android.synthetic.main.fragment_filter_page_report.*
import java.text.SimpleDateFormat
import java.util.*

class FilterPageReport : Fragment() {

    private lateinit var dataBinding: FragmentFilterPageReportBinding
    private val viewModel: ReportViewModel by activityViewModels()
    private var date = "today"
    private var dateISO = "today"
    private var sdate = ""
    private var edate = ""
    private var sdateISO = ""
    private var edateISO = ""
    private var startDate = ""
    private var endDate = ""
    private var startDateTime: Long = 0
    private var endDateTime: Long = 0
    private val id = Locale("in", "ID")
    private val sdf = SimpleDateFormat("EEEE, d MMMM yyyy", id)
    private val dateFormatISO = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
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
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.dateSelection.observe(viewLifecycleOwner, androidx.lifecycle.Observer { date ->
            clearSelection()
            this.date = "available"
            when(date) {
                "Kemarin" -> {
                    yesterday.setBackgroundResource(R.drawable.button_green_square)
                    yesterday.setTextColor(Color.parseColor("#ffffff"))
                }
                "2 Hari yang lalu" -> {
                    last2day.setBackgroundResource(R.drawable.button_green_square)
                    last2day.setTextColor(Color.parseColor("#ffffff"))
                }
                "Minggu ini" -> {
                    thisWeek.setBackgroundResource(R.drawable.button_green_square)
                    thisWeek.setTextColor(Color.parseColor("#ffffff"))
                }
                "Minggu lalu" -> {
                    lastWeek.setBackgroundResource(R.drawable.button_green_square)
                    lastWeek.setTextColor(Color.parseColor("#ffffff"))
                }
                "Bulan ini" -> {
                    thisMonth.setBackgroundResource(R.drawable.button_green_square)
                    thisMonth.setTextColor(Color.parseColor("#ffffff"))
                }
                "Bulan lalu" -> {
                    lastMonth.setBackgroundResource(R.drawable.button_green_square)
                    lastMonth.setTextColor(Color.parseColor("#ffffff"))
                }
                "Rentang waktu" -> {
                    dataBinding.startDate.backgroundTintList = null
                    dataBinding.startDate.isEnabled = true
                    dataBinding.startDate.setText(viewModel.startDate.value)
                    dataBinding.endDate.backgroundTintList = null
                    dataBinding.endDate.isEnabled = true
                    dataBinding.endDate.setText(viewModel.endDate.value)
                    filterCustom.setBackgroundResource(R.drawable.icon_filter_custom_on)
                }
                else -> {
                    today.setBackgroundResource(R.drawable.button_green_square)
                    today.setTextColor(Color.parseColor("#ffffff"))
                }
            }
        })
    }

    private fun attatchInputListener() {
        dateSelection()

        dataBinding.backImage.setColorFilter(
                ContextCompat.getColor(
                        requireContext(),
                        R.color.textSubtle
                ), android.graphics.PorterDuff.Mode.SRC_IN
        )

        dataBinding.backButton.setOnClickListener{
            view?.let { Navigation.findNavController(it).popBackStack() }
        }

        dataBinding.btnNext.setOnClickListener {
            if (date == ""){
                if (endDateTime < startDateTime || startDate == "" || endDate == ""){
                    dataBinding.btnNext.isEnabled = false
                    Toast.makeText(context, "invalid start date and end date", Toast.LENGTH_SHORT).show()
                } else {
                    view?.let { Navigation.findNavController(it).popBackStack() }
                }
            } else {
                view?.let { Navigation.findNavController(it).popBackStack() }
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

        var myCalendar = Calendar.getInstance()
        val datePickerOnDataSetListener = DatePickerDialog(context, { _, year, monthOfYear, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            myCalendar.set(Calendar.HOUR_OF_DAY, 0)
            myCalendar.set(Calendar.MINUTE, 0)
            myCalendar.set(Calendar.SECOND, 0)
            myCalendar.set(Calendar.MILLISECOND, 0)
            setText(sdf.format(myCalendar.time))
            dateFormatISO.timeZone = TimeZone.getDefault()
            dataBinding.btnNext.isEnabled = true
            if (status == "start"){
                startDate = sdf.format(myCalendar.time)
                startDateTime = myCalendar.timeInMillis
                dateISO = dateFormatISO.format(myCalendar.time)
                viewModel.getStartDate(startDate)
                viewModel.getStartISO(dateISO)
            } else if (status == "end"){
                val nowCalendar = Calendar.getInstance()
                val dateNow = sdf.format(nowCalendar.time)
                val selectedEndDate = sdf.format(myCalendar.time)
                if (dateNow == selectedEndDate) {
                    myCalendar = Calendar.getInstance()
                } else {
                    myCalendar.set(Calendar.HOUR_OF_DAY, 23)
                    myCalendar.set(Calendar.MINUTE, 59)
                    myCalendar.set(Calendar.SECOND, 59)
                    myCalendar.set(Calendar.MILLISECOND, 999)
                }

                dateISO = dateFormatISO.format(myCalendar.time)
                endDate = sdf.format(myCalendar.time)
                endDateTime = myCalendar.timeInMillis
                viewModel.getEndDate(endDate)
                viewModel.getEndISO(dateISO)
            }
            viewModel.getDateSelection("Rentang waktu")
        }, myCalendar[Calendar.YEAR], myCalendar[Calendar.MONTH], myCalendar[Calendar.DAY_OF_MONTH])

        //set minimum of date picker
        cal = Calendar.getInstance()
        cal.add(Calendar.DATE, - 30)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        datePickerOnDataSetListener.datePicker.minDate = cal.timeInMillis

        //set maximum of date picker
        cal = Calendar.getInstance()
        datePickerOnDataSetListener.datePicker.maxDate = cal.timeInMillis

        //showing calendar
        datePickerOnDataSetListener.show()
    }

    private fun dateSelection() {
        dataBinding.today.setOnClickListener {
            clearSelection()
            dateFormatISO.timeZone = TimeZone.getDefault()
            cal = Calendar.getInstance()
            date = sdf.format(cal.time)
            dateISO = dateFormatISO.format(cal.time)
            viewModel.getEndDate(date)
            viewModel.getEndISO(dateISO)

            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            date = sdf.format(cal.time)
            dateISO = dateFormatISO.format(cal.time)
            viewModel.getStartDate(date)
            viewModel.getStartISO(dateISO)
            viewModel.getDateSelection("Hari ini")
            today.setBackgroundResource(R.drawable.button_green_square)
            today.setTextColor(Color.parseColor("#ffffff"))
        }

        dataBinding.yesterday.setOnClickListener {
            clearSelection()
            dateFormatISO.timeZone = TimeZone.getDefault()
            cal = Calendar.getInstance()
            cal.add(Calendar.DATE, - 1)
            cal.set(Calendar.HOUR_OF_DAY, 23)
            cal.set(Calendar.MINUTE, 59)
            cal.set(Calendar.SECOND, 59)
            cal.set(Calendar.MILLISECOND, 999)
            date = sdf.format(cal.time)
            dateISO = dateFormatISO.format(cal.time)
            viewModel.getEndDate(date)
            viewModel.getEndISO(dateISO)

            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            date = sdf.format(cal.time)
            dateISO = dateFormatISO.format(cal.time)
            viewModel.getStartDate(date)
            viewModel.getStartISO(dateISO)
            viewModel.getDateSelection("Kemarin")
            yesterday.setBackgroundResource(R.drawable.button_green_square)
            yesterday.setTextColor(Color.parseColor("#ffffff"))
        }

        dataBinding.last2day.setOnClickListener {
            clearSelection()
            dateFormatISO.timeZone = TimeZone.getDefault()
            cal = Calendar.getInstance()
            date = sdf.format(cal.time)
            dateISO = dateFormatISO.format(cal.time)
            viewModel.getEndDate(date)
            viewModel.getEndISO(dateISO)

            cal.add(Calendar.DATE, - 2)
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            date = sdf.format(cal.time)
            dateISO = dateFormatISO.format(cal.time)
            viewModel.getStartDate(date)
            viewModel.getStartISO(dateISO)
            viewModel.getDateSelection("2 Hari yang lalu")
            last2day.setBackgroundResource(R.drawable.button_green_square)
            last2day.setTextColor(Color.parseColor("#ffffff"))
        }

        dataBinding.thisWeek.setOnClickListener {
            clearSelection()
            dateFormatISO.timeZone = TimeZone.getDefault()
            cal = Calendar.getInstance()
            cal.set(Calendar.DAY_OF_WEEK, 2)
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            sdate = sdf.format(cal.time)
            sdateISO = dateFormatISO.format(cal.time)
            viewModel.getStartDate(sdate)
            viewModel.getStartISO(sdateISO)
            edate = sdf.format(Calendar.getInstance().time)
            edateISO = dateFormatISO.format(Calendar.getInstance().time)
            viewModel.getEndDate(edate)
            viewModel.getEndISO(edateISO)
            date = "$sdate - $edate"
            viewModel.getDateSelection("Minggu ini")
            thisWeek.setBackgroundResource(R.drawable.button_green_square)
            thisWeek.setTextColor(Color.parseColor("#ffffff"))
        }

        dataBinding.lastWeek.setOnClickListener {
            clearSelection()
            dateFormatISO.timeZone = TimeZone.getDefault()
            cal = Calendar.getInstance()
            cal.set(Calendar.DAY_OF_WEEK, 2)
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            cal.add(Calendar.DATE, -7)
            sdate = sdf.format(cal.time)
            sdateISO = dateFormatISO.format(cal.time)
            viewModel.getStartDate(sdate)
            viewModel.getStartISO(sdateISO)
            cal = Calendar.getInstance()
            cal.set(Calendar.DAY_OF_WEEK, 1)
            cal.set(Calendar.HOUR_OF_DAY, 23)
            cal.set(Calendar.MINUTE, 59)
            cal.set(Calendar.SECOND, 59)
            cal.set(Calendar.MILLISECOND, 999)
            edate = sdf.format(cal.time)
            edateISO = dateFormatISO.format(cal.time)
            viewModel.getEndDate(edate)
            viewModel.getEndISO(edateISO)
            date = "$sdate - $edate"
            viewModel.getDateSelection("Minggu lalu")
            lastWeek.setBackgroundResource(R.drawable.button_green_square)
            lastWeek.setTextColor(Color.parseColor("#ffffff"))
        }

        dataBinding.thisMonth.setOnClickListener {
            clearSelection()
            dateFormatISO.timeZone = TimeZone.getDefault()
            cal = Calendar.getInstance()
            cal.set(Calendar.DAY_OF_MONTH, 1)
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            sdate = sdf.format(cal.time)
            sdateISO = dateFormatISO.format(cal.time)
            viewModel.getStartDate(sdate)
            viewModel.getStartISO(sdateISO)
            edate = sdf.format(Calendar.getInstance().time)
            edateISO = dateFormatISO.format(Calendar.getInstance().time)
            viewModel.getEndDate(edate)
            viewModel.getEndISO(edateISO)
            date = "$sdate - $edate"
            viewModel.getDateSelection("Bulan ini")
            thisMonth.setBackgroundResource(R.drawable.button_green_square)
            thisMonth.setTextColor(Color.parseColor("#ffffff"))
        }

        dataBinding.lastMonth.setOnClickListener {
            clearSelection()
            dateFormatISO.timeZone = TimeZone.getDefault()
            cal = Calendar.getInstance()
            cal.set(Calendar.DAY_OF_MONTH, 1)
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            cal.add(Calendar.DATE, -30)
            sdate = sdf.format(cal.time)
            sdateISO = dateFormatISO.format(cal.time)
            viewModel.getStartDate(sdate)
            viewModel.getStartISO(sdateISO)
            cal = Calendar.getInstance()
            cal.add(Calendar.MONTH, -1)
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
            cal.set(Calendar.HOUR_OF_DAY, 23)
            cal.set(Calendar.MINUTE, 59)
            cal.set(Calendar.SECOND, 59)
            cal.set(Calendar.MILLISECOND, 999)
            edate = sdf.format(cal.time)
            edateISO = dateFormatISO.format(cal.time)
            viewModel.getEndDate(edate)
            viewModel.getEndISO(edateISO)
            date = "$sdate - $edate"
            viewModel.getDateSelection("Bulan lalu")
            lastMonth.setBackgroundResource(R.drawable.button_green_square)
            lastMonth.setTextColor(Color.parseColor("#ffffff"))
        }

        dataBinding.tvFilterCustom.setOnClickListener {
            clearSelection()
            viewModel.clearStartEndDate()
            dataBinding.startDate.backgroundTintList = null
            dataBinding.startDate.isEnabled = true
            dataBinding.endDate.backgroundTintList = null
            dataBinding.endDate.isEnabled = true
            filterCustom.setBackgroundResource(R.drawable.icon_filter_custom_on)
        }

        dataBinding.filterCustom.setOnClickListener {
            clearSelection()
            viewModel.clearStartEndDate()
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