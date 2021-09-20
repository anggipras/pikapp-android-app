package com.tsab.pikapp.view.omni.integration.connect

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentIntegrationConnectSecondTokopediaBinding
import com.tsab.pikapp.models.model.ShopCategory
import com.tsab.pikapp.receiver.AlarmReceiver
import com.tsab.pikapp.util.setAllOnClickListener
import com.tsab.pikapp.viewmodel.omni.integration.IntegrationViewModel
import com.tsab.pikapp.viewmodel.omni.integration.TokopediaIntegrationViewModel
import java.util.*

class TokopediaIntegrationConnectSecondFragment : Fragment() {
    private lateinit var navController: NavController
    private lateinit var dataBinding: FragmentIntegrationConnectSecondTokopediaBinding
    private val integrationViewModel: IntegrationViewModel by activityViewModels()
    private val screenViewModel: TokopediaIntegrationViewModel by activityViewModels()
    private var alarmMgr : AlarmManager? = null
    private lateinit var alarmIntent: PendingIntent

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_integration_connect_second_tokopedia, container, false
        )
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        observeViewModel()
        attachInputListeners()
//        observeDialogResult()
    }

    private fun observeViewModel() {
        // Integration connect state.
        screenViewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            dataBinding.loadingOverlay.loadingView.visibility =
                if (isLoading) View.VISIBLE else View.GONE
        })

        screenViewModel.isSetForAlarm.observe(viewLifecycleOwner, Observer { isAlarm ->
            if (isAlarm) {
                createNotificationChannel()
                setAlarm()
                screenViewModel.setForAlarm(false)
            }
        })

        // Integration success state and data.
        screenViewModel.successResult.observe(viewLifecycleOwner, Observer { omnichannel ->
            if (omnichannel != null) {
                integrationViewModel.addIntegration(omnichannel)
            }
        })

        screenViewModel.isSuccess.observe(viewLifecycleOwner, Observer { isSuccess ->
            if (isSuccess) {
                navController.navigate(R.id.action_integrationTokopediaConnectSecondFragment_to_integrationListFragment)
//                navController.navigate(R.id.action_integrationTokopediaConnectSecondFragment_to_tokopediaIntegrationDialog)
            }
        })

        // Input states.
        dataBinding.akunEmailInputText.setText(screenViewModel.email.value)
        screenViewModel.emailError.observe(viewLifecycleOwner, Observer { emailError ->
            dataBinding.akunEmailErrorText.text = emailError
            dataBinding.akunEmailErrorText.visibility =
                if (emailError == "") View.GONE else View.VISIBLE
        })

        dataBinding.nomorTeleponInputText.setText(screenViewModel.telepon.value)
        screenViewModel.teleponError.observe(viewLifecycleOwner, Observer { teleponError ->
            dataBinding.nomorTeleponErrorText.text = teleponError
            dataBinding.nomorTeleponErrorText.visibility =
                if (teleponError == "") View.GONE else View.VISIBLE
        })

        dataBinding.namaTokoInputText.setText(screenViewModel.namaToko.value)
        screenViewModel.namaTokoError.observe(viewLifecycleOwner, Observer { namaTokoError ->
            dataBinding.namaTokoErrorText.text = namaTokoError
            dataBinding.namaTokoErrorText.visibility =
                if (namaTokoError == "") View.GONE else View.VISIBLE
        })

        dataBinding.domainTokoInputText.setText(screenViewModel.domainToko.value)
        screenViewModel.domainTokoError.observe(viewLifecycleOwner, Observer { domainTokoError ->
            dataBinding.domainTokoErrorText.text = domainTokoError
            dataBinding.domainTokoErrorText.visibility =
                if (domainTokoError == "") View.GONE else View.VISIBLE
        })

        dataBinding.kategoriAkunRadioGroup.check(
            when (screenViewModel.shopCategory.value) {
                ShopCategory.OFFICIAL -> R.id.officialStoreRadioButton
                ShopCategory.POWER_MERCHANT -> R.id.powerMerchantRadioButton
                else -> R.id.officialStoreRadioButton
            }
        )
    }

    private fun attachInputListeners() {
        dataBinding.headerLayout.backButton.setAllOnClickListener(View.OnClickListener {
            activity?.finish()
        }, view)

        dataBinding.kirimButton.setOnClickListener {
            hideKeyboard()

            screenViewModel.validateEmail(dataBinding.akunEmailInputText.text.toString())
            screenViewModel.validateTelepon(dataBinding.nomorTeleponInputText.text.toString())
            screenViewModel.validateNamaToko(dataBinding.namaTokoInputText.text.toString())
            screenViewModel.validateDomainToko(dataBinding.domainTokoInputText.text.toString())
            screenViewModel.validateShopCategory(
                when (dataBinding.kategoriAkunRadioGroup.checkedRadioButtonId) {
                    R.id.officialStoreRadioButton -> ShopCategory.OFFICIAL
                    R.id.powerMerchantRadioButton -> ShopCategory.POWER_MERCHANT
                    else -> ShopCategory.OFFICIAL
                }
            )

            if (!screenViewModel.validateAll()) return@setOnClickListener
            screenViewModel.connectIntegration()
//            navController.navigate(R.id.action_integrationTokopediaConnectSecondFragment_to_tokopediaIntegrationDialog)
        }

        dataBinding.akunEmailInputText.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || event == null || event.keyCode == KeyEvent.KEYCODE_ENTER) {
                return@setOnEditorActionListener !screenViewModel.validateEmail(
                    dataBinding.akunEmailInputText.text.toString()
                )
            }
            false
        }

        dataBinding.nomorTeleponInputText.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || event == null || event.keyCode == KeyEvent.KEYCODE_ENTER) {
                return@setOnEditorActionListener !screenViewModel.validateTelepon(
                    dataBinding.nomorTeleponInputText.text.toString()
                )
            }
            false
        }

        dataBinding.namaTokoInputText.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || event == null || event.keyCode == KeyEvent.KEYCODE_ENTER) {
                return@setOnEditorActionListener !screenViewModel.validateNamaToko(
                    dataBinding.namaTokoInputText.text.toString()
                )
            }
            false
        }

        dataBinding.domainTokoInputText.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || event == null || event.keyCode == KeyEvent.KEYCODE_ENTER) {
                return@setOnEditorActionListener !screenViewModel.validateDomainToko(
                    dataBinding.domainTokoInputText.text.toString()
                ).also {
                    hideKeyboard()
                }
            }
            false
        }

        dataBinding.kategoriAkunRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.officialStoreRadioButton) {
                screenViewModel.validateShopCategory(ShopCategory.OFFICIAL)
            } else if (checkedId == R.id.powerMerchantRadioButton) {
                screenViewModel.validateShopCategory(ShopCategory.POWER_MERCHANT)
            }
        }
    }

//    private fun observeDialogResult() {
//        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>(dialogResult)
//            ?.observe(viewLifecycleOwner, Observer { isConfirmed ->
//                if (isConfirmed) {
//                    screenViewModel.connectIntegration()
//                }
//            })
//    }

    private fun hideKeyboard() {
        val inputManager: InputMethodManager = activity?.getSystemService(
            Activity.INPUT_METHOD_SERVICE
        ) as InputMethodManager

        if (inputManager.isAcceptingText) {
            inputManager.hideSoftInputFromWindow(activity?.currentFocus?.windowToken, 0)
        }
    }

    private fun setAlarm() {
        alarmMgr = requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmIntent = Intent(requireActivity(), AlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(requireActivity(), 0, intent, 0)
        }

        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 10)
            set(Calendar.AM_PM, Calendar.AM)
        }

        alarmMgr!!.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                alarmIntent
        )
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = getString(R.string.general_notif_id)
            val channelName : CharSequence = getString(R.string.notification_channel_name)
            val description = getString(R.string.notification_channel_description)
            val notificationChannel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.YELLOW
            notificationChannel.enableVibration(true)
            notificationChannel.description = description

            val notificationManager = requireActivity().getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}