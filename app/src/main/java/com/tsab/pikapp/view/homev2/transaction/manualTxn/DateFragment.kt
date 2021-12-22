package com.tsab.pikapp.view.homev2.transaction.manualTxn

import android.graphics.Color
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.fragment.app.activityViewModels
import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tsab.pikapp.R
import com.tsab.pikapp.viewmodel.homev2.ManualTxnViewModel
import kotlinx.android.synthetic.main.expanded_txn_fragment.*
import kotlinx.android.synthetic.main.fragment_asal.*
import kotlinx.android.synthetic.main.fragment_checkout.*
import kotlinx.android.synthetic.main.fragment_date.*
import java.text.NumberFormat
import java.util.*
import kotlin.math.min

class DateFragment : BottomSheetDialogFragment(){

    private val viewModel: ManualTxnViewModel by activityViewModels()
    private var sekarangStatus: Boolean = false
    private var customDateStatus: Boolean = false
    private var nowTime1: Boolean = false
    private var nowTime2: Boolean = false
    private var nowTime3: Boolean = false
    private var customTimeStatus: Boolean = false
    var am_pm = ""
    var hourNow = 0
    var day = 0
    var minuteNow = 0
    var monthNow = 0
    var yearNow = 0
    var bulan: Int =0
    var date = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_date, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        attachInputListeners()
        observeViewModel()
    }

    private fun attachInputListeners() {
        nowDate.setOnClickListener {
            if (!sekarangStatus){
                nowDate.setBackgroundResource(R.drawable.button_green_square)
                nowDate.setTextColor(Color.parseColor("#ffffff"))
                customDate.setBackgroundResource(R.drawable.btn_date_disable)
                customDate.setTextColor(Color.parseColor("#aaaaaa"))
                jamTitle.visibility = View.GONE
                chooseTime.visibility = View.GONE
                timePick.visibility = View.GONE
                datePicker.visibility = View.GONE
                customDate.text = "Custom Tanggal"
                sekarangStatus = true
                customDateStatus = false
                saveStateBtn.visibility = View.GONE
                saveStateBtn.visibility = View.GONE
                btnSaveDate.visibility = View.VISIBLE
                btnSaveDate.setBackgroundResource(R.drawable.button_green_square)
            }
        }

        customDate.setOnClickListener {
                customDate.setBackgroundResource(R.drawable.button_green_square)
                customDate.setTextColor(Color.parseColor("#ffffff"))
                nowDate.setBackgroundResource(R.drawable.btn_date_disable)
                nowDate.setTextColor(Color.parseColor("#aaaaaa"))
                jamTitle.visibility = View.GONE
                chooseTime.visibility = View.GONE
                timePick.visibility = View.GONE
                datePicker.visibility = View.VISIBLE
                val today = Calendar.getInstance()
                datePicker.init(today.get(Calendar.YEAR), today.get(Calendar.MONTH),
                    today.get(Calendar.DAY_OF_MONTH), DatePicker.OnDateChangedListener { view, year, monthOfYear, dayOfMonth ->
                        var month = getMonth(monthOfYear.toString())
                        bulan = monthOfYear + 1
                        day = dayOfMonth
                        monthNow = monthOfYear
                        yearNow = year
                        Log.e("Month", monthOfYear.toString())
                        date = "$dayOfMonth" + month + "$year"
                        customDate.text = date
                    })
                datePicker.minDate = System.currentTimeMillis() - 1000
                btnSaveDate.visibility = View.GONE
                sekarangStatus = false
                customDateStatus = true
                saveStateBtn.visibility = View.VISIBLE
                btnSaveDateTime.setOnClickListener {
                    if(day == today.get(Calendar.DAY_OF_MONTH) && monthNow == today.get(Calendar.MONTH) && yearNow == today.get(Calendar.YEAR)){
                        nowTime.text = (today.get(Calendar.HOUR) + 1).toString() + ".00"
                        nowTimeSecond.text = (today.get(Calendar.HOUR) + 2).toString() + ".00"
                        nowTimeThird.text = (today.get(Calendar.HOUR) + 3).toString() + ".00"
                    }else{
                        nowTime.text = "10.00"
                        nowTimeSecond.text = "11.00"
                        nowTimeThird.text = "12.00"
                    }
                    customDate.text = date
                    datePicker.visibility = View.GONE
                    btnSaveDate.visibility = View.VISIBLE
                    saveStateBtn.visibility = View.GONE
                    jamTitle.visibility = View.VISIBLE
                    chooseTime.visibility = View.VISIBLE
                    timePick.visibility = View.GONE
                }
                cancel.setOnClickListener {
                    datePicker.visibility = View.GONE
                    btnSaveDate.visibility = View.VISIBLE
                    saveStateBtn.visibility = View.GONE
                }
        }

        nowTime.setOnClickListener {
            nowTime.setBackgroundResource(R.drawable.button_green_square)
            nowTime.setTextColor(Color.parseColor("#ffffff"))
            customTime.setBackgroundResource(R.drawable.btn_date_disable)
            customTime.setTextColor(Color.parseColor("#aaaaaa"))
            nowTimeSecond.setBackgroundResource(R.drawable.btn_date_disable)
            nowTimeSecond.setTextColor(Color.parseColor("#aaaaaa"))
            nowTimeThird.setBackgroundResource(R.drawable.btn_date_disable)
            nowTimeThird.setTextColor(Color.parseColor("#aaaaaa"))
            nowTime1 = true
            nowTime2 = false
            btnSaveDate.setBackgroundResource(R.drawable.button_green_square)
            nowTime3 = false
            customTimeStatus = false
        }

        nowTimeSecond.setOnClickListener {
            nowTimeSecond.setBackgroundResource(R.drawable.button_green_square)
            nowTimeSecond.setTextColor(Color.parseColor("#ffffff"))
            customTime.setBackgroundResource(R.drawable.btn_date_disable)
            customTime.setTextColor(Color.parseColor("#aaaaaa"))
            nowTime.setBackgroundResource(R.drawable.btn_date_disable)
            nowTime.setTextColor(Color.parseColor("#aaaaaa"))
            nowTimeThird.setBackgroundResource(R.drawable.btn_date_disable)
            nowTimeThird.setTextColor(Color.parseColor("#aaaaaa"))
            nowTime1 = false
            nowTime2 = true
            nowTime3 = false
            customTimeStatus = false
            btnSaveDate.setBackgroundResource(R.drawable.button_green_square)
        }

        nowTimeThird.setOnClickListener {
            nowTimeThird.setBackgroundResource(R.drawable.button_green_square)
            customTime.setBackgroundResource(R.drawable.btn_date_disable)
            customTime.setTextColor(Color.parseColor("#aaaaaa"))
            nowTimeThird.setTextColor(Color.parseColor("#ffffff"))
            nowTime.setBackgroundResource(R.drawable.btn_date_disable)
            nowTime.setTextColor(Color.parseColor("#aaaaaa"))
            nowTimeSecond.setBackgroundResource(R.drawable.btn_date_disable)
            nowTimeSecond.setTextColor(Color.parseColor("#aaaaaa"))
            nowTime1 = false
            nowTime2 = false
            nowTime3 = true
            customTimeStatus = false
            btnSaveDate.setBackgroundResource(R.drawable.button_green_square)
        }

        customTime.setOnClickListener {
            customTime.setBackgroundResource(R.drawable.button_green_square)
            customTime.setTextColor(Color.parseColor("#ffffff"))
            nowTime.setBackgroundResource(R.drawable.btn_date_disable)
            nowTime.setTextColor(Color.parseColor("#aaaaaa"))
            nowTimeThird.setBackgroundResource(R.drawable.btn_date_disable)
            nowTimeThird.setTextColor(Color.parseColor("#aaaaaa"))
            nowTimeSecond.setBackgroundResource(R.drawable.btn_date_disable)
            nowTimeSecond.setTextColor(Color.parseColor("#aaaaaa"))
            timePick.visibility = View.VISIBLE
            datePicker.visibility = View.GONE
            nowTime1 = false
            nowTime2 = false
            nowTime3 = false
            customTimeStatus = true
            btnSaveDate.visibility = View.GONE
            saveStateBtn.visibility = View.VISIBLE
            timePick.setOnTimeChangedListener { _, hour, minute ->
                hourNow = hour
                minuteNow = minute
                am_pm = ""
                // AM_PM decider logic
                when {
                    hour == 0 -> {
                        hourNow += 12
                        am_pm = "AM"
                    }
                    hour == 12 -> am_pm = "PM"
                    hour > 12 -> {
                        hourNow -= 12
                        am_pm = "PM"
                    }
                    else -> am_pm = "AM"
                }
            }
            btnSaveDateTime.setOnClickListener {
                if(am_pm == "PM" && hourNow != 12){
                    getHourPM(hourNow)
                }
                if(am_pm == "AM" && hourNow == 12){
                    getHourAM(hourNow)
                }
                var minute = ""
                if(minuteNow < 10){
                    minute= "0$minuteNow"
                }else{
                    minute = "$minuteNow"
                }
                customTime.text = "$hourNow.$minute"
                timePick.visibility = View.GONE
                btnSaveDate.visibility = View.VISIBLE
                saveStateBtn.visibility = View.GONE
                btnSaveDate.setBackgroundResource(R.drawable.button_green_square)
            }
            cancel.setOnClickListener {
                timePick.visibility = View.GONE
                btnSaveDate.visibility = View.VISIBLE
                saveStateBtn.visibility = View.GONE
            }
        }

        btnSaveDate.setOnClickListener {
            if(sekarangStatus){
                viewModel.setWaktu("Sekarang", " ")
                viewModel.mutablePostWaktu.value = Calendar.getInstance().get(Calendar.YEAR).toString() + "-" + (Calendar.getInstance().get(Calendar.MONTH) + 1).toString() + "-" + Calendar.getInstance().get(Calendar.DAY_OF_MONTH).toString() + " " +
                        Calendar.getInstance().get(Calendar.HOUR_OF_DAY).toString() + ":" + Calendar.getInstance().get(Calendar.MINUTE).toString() + ":" + Calendar.getInstance().get(Calendar.SECOND).toString()
                viewModel.mutableStatusTime.value = "NOW"
                dismiss()
            }else if(customDateStatus && nowTime1){
                viewModel.setWaktu("Custom", customDate.text.toString() + " " + nowTime.text)
                viewModel.setDate(customDate.text.toString())
                viewModel.setTime(nowTime.text.toString())
                var hourPost = nowTime.text.toString().substringBefore(".")
                var minutePost = nowTime.text.toString().substringAfter(".")
                viewModel.mutablePostWaktu.value = yearNow.toString() + "-" + (monthNow + 1).toString() + "-" + day.toString() + " " +
                        hourPost + ":" + minutePost + ":" + "00"
                viewModel.mutableStatusTime.value = "CUSTOM"
                dismiss()
            }else if(customDateStatus && nowTime2){
                viewModel.setWaktu("Custom", customDate.text.toString() + " " + nowTimeSecond.text)
                viewModel.setDate(customDate.text.toString())
                viewModel.setTime(nowTimeSecond.text.toString())
                var hourPost = nowTimeSecond.text.toString().substringBefore(".")
                var minutePost = nowTimeSecond.text.toString().substringAfter(".")
                viewModel.mutablePostWaktu.value = yearNow.toString() + "-" + (monthNow + 1).toString() + "-" + day.toString() + " " +
                        hourPost + ":" + minutePost + ":" + "00"
                viewModel.mutableStatusTime.value = "CUSTOM"
                dismiss()
            }else if(customDateStatus && nowTime3){
                viewModel.setWaktu("Custom", customDate.text.toString() + " " + nowTimeThird.text)
                viewModel.setDate(customDate.text.toString())
                viewModel.setTime(nowTimeThird.text.toString())
                var hourPost = nowTimeThird.text.toString().substringBefore(".")
                var minutePost = nowTimeThird.text.toString().substringAfter(".")
                viewModel.mutablePostWaktu.value = yearNow.toString() + "-" + (monthNow + 1).toString() + "-" + day.toString() + " " +
                        hourPost + ":" + minutePost + ":" + "00"
                viewModel.mutableStatusTime.value = "CUSTOM"
                dismiss()
            }else if(customDateStatus && customTimeStatus){
                viewModel.setWaktu("Custom", customDate.text.toString() + " " + customTime.text)
                viewModel.setDate(customDate.text.toString())
                viewModel.setTime(customTime.text.toString())
                var hourPost = customTime.text.toString().substringBefore(".")
                var minutePost = customTime.text.toString().substringAfter(".")
                viewModel.mutablePostWaktu.value = yearNow.toString() + "-" + (monthNow + 1).toString() + "-" + day.toString() + " " +
                        hourPost + ":" + minutePost + ":" + "00"
                Log.e("Status", viewModel.mutablePostWaktu.value.toString())
                viewModel.mutableStatusTime.value = "CUSTOM"
                dismiss()
            }
        }
    }

    fun getHourAM(hour: Int){
        hourNow = 24
    }

    fun getHourPM(hour: Int){
        if(hour == 1){
            hourNow = 13
        } else if(hour == 2){
            hourNow = 14
        }else if(hour == 3){
            hourNow = 15
        }else if(hour == 4){
            hourNow = 16
        }else if(hour == 5){
            hourNow = 17
        }else if(hour == 6){
            hourNow = 18
        }else if(hour == 7){
            hourNow = 19
        }else if(hour == 8){
            hourNow = 20
        }else if(hour == 9){
            hourNow = 21
        }else if(hour == 10){
            hourNow = 22
        }else if(hour == 11){
            hourNow = 23
        }
    }

    fun getMonth(bulanTemp: String): String{
        var bulan = ""
        if (bulanTemp == "0") {
            bulan = " Januari "
        } else if (bulanTemp == "1") {
            bulan = " Februari "
        } else if (bulanTemp == "2") {
            bulan = " Maret "
        } else if (bulanTemp == "3") {
            bulan = " April "
        } else if (bulanTemp == "4") {
            bulan = " Mei "
        } else if (bulanTemp == "5") {
            bulan = " Juni "
        } else if (bulanTemp == "6") {
            bulan = " Juli "
        } else if (bulanTemp == "7") {
            bulan = " Agustus "
        } else if (bulanTemp == "8") {
            bulan = " September "
        } else if (bulanTemp == "9") {
            bulan = " Oktober "
        } else if (bulanTemp == "10") {
            bulan = " November "
        } else if (bulanTemp == "11") {
            bulan = " Desember "
        }
        return bulan
    }

    private fun observeViewModel(){
      viewModel.WaktuPesan.observe(viewLifecycleOwner, androidx.lifecycle.Observer { waktu->
          if(waktu != ""){
              if(waktu == "Sekarang"){
                  nowDate.setBackgroundResource(R.drawable.button_green_square)
                  nowDate.setTextColor(Color.parseColor("#ffffff"))
                  customDate.setBackgroundResource(R.drawable.btn_date_disable)
                  customDate.setTextColor(Color.parseColor("#aaaaaa"))
                  jamTitle.visibility = View.GONE
                  chooseTime.visibility = View.GONE
                  timePick.visibility = View.GONE
                  datePicker.visibility = View.GONE
                  sekarangStatus = true
                  customDate.text = "Custom Tanggal"
                  customDateStatus = false
                  saveStateBtn.visibility = View.GONE
                  saveStateBtn.visibility = View.GONE
                  btnSaveDate.visibility = View.VISIBLE
                  btnSaveDate.setBackgroundResource(R.drawable.button_green_square)
              }else if(waktu == "Custom"){
                  customDate.setBackgroundResource(R.drawable.button_green_square)
                  customDate.setTextColor(Color.parseColor("#ffffff"))
                  nowDate.setBackgroundResource(R.drawable.btn_date_disable)
                  nowDate.setTextColor(Color.parseColor("#aaaaaa"))
                  datePicker.visibility = View.GONE
                  btnSaveDate.visibility = View.VISIBLE
                  jamTitle.visibility = View.VISIBLE
                  sekarangStatus = false
                  customDateStatus = true
                  chooseTime.visibility = View.VISIBLE
                  saveStateBtn.visibility = View.GONE
                  btnSaveDate.setBackgroundResource(R.drawable.button_green_square)
              }
          }
      })

        viewModel.DatePesanan.observe(viewLifecycleOwner, androidx.lifecycle.Observer { tanggal ->
            if(tanggal != ""){
                customDate.text = tanggal
            }
        })

        viewModel.JamPesanan.observe(viewLifecycleOwner, androidx.lifecycle.Observer { tanggal ->
            if(tanggal != ""){
                nowTime1 = false
                nowTime2 = false
                nowTime3 = false
                customTimeStatus = true
                customTime.text = tanggal
                customTime.setBackgroundResource(R.drawable.button_green_square)
                customTime.setTextColor(Color.parseColor("#ffffff"))
            }
        })
    }
}