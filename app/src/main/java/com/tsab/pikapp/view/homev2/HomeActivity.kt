package com.tsab.pikapp.view.homev2

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.ActivityHomeNavigationBinding
import com.tsab.pikapp.receiver.AlarmReceiver
import com.tsab.pikapp.util.SessionManager
import com.tsab.pikapp.view.homev2.menu.MenuFragment
import com.tsab.pikapp.view.homev2.other.OtherFragment
import com.tsab.pikapp.view.homev2.promo.PromoFragment
import com.tsab.pikapp.view.homev2.menu.TransactionFragment
import com.tsab.pikapp.view.menuCategory.SortActivity
import com.tsab.pikapp.viewmodel.categoryMenu.CategoryViewModel
import kotlinx.android.synthetic.main.activity_home_navigation.*
import kotlinx.android.synthetic.main.layout_header_drawer.view.*
import java.util.*

class HomeActivity : AppCompatActivity() {
    val model: CategoryViewModel by viewModels()

    private val transactionFragment = TransactionFragment()
    private val menuFragment = MenuFragment()
    private val promoFragment = PromoFragment()
    private val otherFragment = OtherFragment()
    private val sessionManager = SessionManager()
    private lateinit var dataBinding: ActivityHomeNavigationBinding

    private var alarmMgr: AlarmManager? = null
    private lateinit var alarmIntent: PendingIntent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        dataBinding = ActivityHomeNavigationBinding.inflate(layoutInflater)
        setContentView(dataBinding.root)
        when {
            sessionManager.getHomeNav() == 0 -> {
                replaceFragment(transactionFragment)
                bottom_navigation.selectedItemId = R.id.nav_transaction
            }
            sessionManager.getHomeNav() == 1 -> {
                overridePendingTransition(R.anim.bottom_down, R.anim.no_animation)
                replaceFragment(menuFragment)
                bottom_navigation.selectedItemId = R.id.nav_menu
            }
            sessionManager.getHomeNav() == 2 -> {
                replaceFragment(promoFragment)
                bottom_navigation.selectedItemId = R.id.nav_promo
            }
            sessionManager.getHomeNav() == 3 -> {
                overridePendingTransition(R.anim.bottom_down, R.anim.no_animation)
                replaceFragment(otherFragment)
                bottom_navigation.selectedItemId = R.id.nav_other
            }
        }

        nav_view.getHeaderView(0).close_drawer.setOnClickListener { v ->
            openCloseDrawer(v)
        }

        nav_view.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.search_menu -> startActivity(Intent(this, SearchActivity::class.java))
                R.id.sort_menu-> Intent(this, SortActivity::class.java).apply {
                    putExtra("SORT_NAV", 0)
                    startActivity(this)}
                R.id.share_link -> Log.e("fqiwf", "qefbjhwq")
            }
            true
        }

        bottom_navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_transaction -> replaceFragment(transactionFragment)
                R.id.nav_menu -> replaceFragment(menuFragment)
                R.id.nav_promo -> replaceFragment(promoFragment)
                R.id.nav_other -> replaceFragment(otherFragment)
            }
            true
        }
    }

    private fun setAlarm() {
        alarmMgr = getSystemService(ALARM_SERVICE) as AlarmManager
        alarmIntent = Intent(this, AlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(this, 0, intent, 0)
        }

        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 9)
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
            val channelName: CharSequence = getString(R.string.notification_channel_name)
            val description = getString(R.string.notification_channel_description)
            val notificationChannel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.YELLOW
            notificationChannel.enableVibration(true)
            notificationChannel.description = description

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }

    private fun hideKeyboard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if (inputManager.isAcceptingText) {
            inputManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        }
    }

    override fun onBackPressed() {
        if (bottom_navigation.selectedItemId == R.id.nav_transaction) {
            sessionManager.setHomeNav(0)
            finishAffinity()
        } else {
            bottom_navigation.selectedItemId = R.id.nav_transaction
            sessionManager.setHomeNav(0)
        }
    }

    fun openCloseDrawer(view: View){
        if(open_drawer.isDrawerOpen(GravityCompat.END)){
            open_drawer.closeDrawer(GravityCompat.END)
        }else{
            open_drawer.openDrawer(GravityCompat.END)
        }
    }
}