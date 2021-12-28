package com.tsab.pikapp.view.homev2

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.ActivityHomeNavigationBinding
import com.tsab.pikapp.models.network.PikappApiService
import com.tsab.pikapp.view.homev2.menu.MenuFragment
import com.tsab.pikapp.view.homev2.other.OtherFragment
import com.tsab.pikapp.view.homev2.promo.PromoFragment
import com.tsab.pikapp.view.homev2.menu.TransactionFragment
import com.tsab.pikapp.view.homev2.menu.WebMenuActivity
import com.tsab.pikapp.view.menuCategory.SortActivity
import com.tsab.pikapp.viewmodel.categoryMenu.CategoryViewModel
import kotlinx.android.synthetic.main.activity_home_navigation.*
import kotlinx.android.synthetic.main.layout_header_drawer.view.*
import kotlinx.android.synthetic.main.transaction_fragment.*
import naci.showcaseview.ShowcaseView
import naci.showcaseview.listener.IShowcaseListener
import smartdevelop.ir.eram.showcaseviewlib.GuideView
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.tsab.pikapp.models.model.TutorialGetResponse
import com.tsab.pikapp.util.*
import com.tsab.pikapp.viewmodel.homev2.TutorialViewModel
import kotlinx.android.synthetic.main.fragment_login_v2_first.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomeActivity : AppCompatActivity() {
    val model: CategoryViewModel by viewModels()
    val viewModel: TutorialViewModel by viewModels()
    private val transactionFragment = TransactionFragment()
    private val menuFragment = MenuFragment()
    private val promoFragment = PromoFragment()
    private val otherFragment = OtherFragment()
    private val sessionManager = SessionManager()
    private lateinit var dataBinding: ActivityHomeNavigationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        dataBinding = ActivityHomeNavigationBinding.inflate(layoutInflater)
        setContentView(dataBinding.root)

        replaceFragment(transactionFragment)
        sessionManager.setHomeNav(0)
        bottom_navigation.selectedItemId = R.id.nav_transaction

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
                R.id.see_menu -> startActivity(Intent(this, WebMenuActivity::class.java))
                R.id.share_link -> {
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        var midStore: String? = sessionManager.getUserData()?.mid
                        var phStore: String? = sessionManager.getUserData()?.phoneNumber
                        val menuWebApi = PikappApiService().menuWeb()
                        val linkURL = "${menuWebApi}${midStore}"
                        val linkText = "Klik disini untuk melihat menu toko kami : ${linkURL}\n\nUntuk info lebih lanjut, hubungi kami di ${phStore}"
                        putExtra(Intent.EXTRA_TEXT, linkText)
                        type = "text/plain"
                    }

                    val shareIntent = Intent.createChooser(sendIntent, null)
                    startActivity(shareIntent)
                }
            }
            true
        }

        modalView.visibility = View.GONE
        layerView.visibility = View.GONE

        getTutorial("TUTORIAL_TRANSACTION")

       /* if(viewModel.mutableMenuTabs.value == true){
            modalView.visibility = View.GONE
            layerView.visibility = View.GONE
        }else{
            desc1.visibility = View.GONE
            desc3.visibility = View.GONE
            nextModal.setOnClickListener {
                modalView.visibility = View.GONE
                layerView.visibility = View.GONE
                ShowIntro("Navigation Bar",
                    "Terdapat navigation bar dengan 4 tombol yang memiliki fungsi berbeda",
                    bottom_navigation, 2)
            }

            skipModal.setOnClickListener {
                modalView.visibility = View.GONE
                layerView.visibility = View.GONE
            }
        }*/

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


    fun ShowIntro(title: String, desc:String, view: View, type: Int){
        GuideView.Builder(this)
            .setTitle(title)
            .setContentText(desc)
            .setGravity(GuideView.Gravity.auto)
            .setTargetView(view)
            .setDismissType(GuideView.DismissType.anywhere)
            .setContentTextSize(12)
            .setTitleTextSize(14)
            .setGuideListener {
                if (type == 2){
                    replaceFragment(transactionFragment)
                    bottom_navigation.selectedItemId = R.id.nav_transaction
                    ShowIntro("Transaction Button",
                        "Tombol transaksi digunakan untuk mengakses halaman transaksi pesanan dari merchant anda.",
                        findViewById(R.id.nav_transaction), 4)
                } else if (type == 4){
                    replaceFragment(transactionFragment)
                    bottom_navigation.selectedItemId = R.id.nav_transaction
                    ShowIntro("Menu Button",
                        "Tombol menu digunakan untuk mengakses halaman yang berisi daftar menu yang dimiliki merchant anda.",
                        findViewById(R.id.nav_menu), 5)
                }else if (type == 5){
                    replaceFragment(transactionFragment)
                    bottom_navigation.selectedItemId = R.id.nav_transaction
                    ShowIntro("Promo Button",
                        "Tombol Promo digunakan untuk mengakses halaman “Promo” yang dimiliki oleh merchant.",
                        findViewById(R.id.nav_promo), 6)
                }else if (type == 6){
                    replaceFragment(transactionFragment)
                    bottom_navigation.selectedItemId = R.id.nav_transaction
                    ShowIntro("Other Page",
                        "Tombol lainnya digunakan untuk mengkases halaman yang berisi informasi dari merchant anda.",
                        findViewById(R.id.nav_other), 7)
                }else if (type == 7){
                    ShowIntro("Transaction Tab",
                        "Terdapat 3 pilihan tab pada halaman transaksi yang akan menampilkan daftar pesanan yang sedang diproses, telah selesai, dan yang telah dibatalkan.",
                        findViewById(R.id.tabs), 8)
                }else if (type == 8){
                    var proses = (tabs.getChildAt(0) as ViewGroup).getChildAt(0)
                    ShowIntro("Proses Tab",
                        "Pada tab Diproses akan berisi daftar dari pesanan yang sedang diproses oleh merchant.",
                        proses, 9)
                }else if (type == 9) {
                    var proses = (tabs.getChildAt(0) as ViewGroup).getChildAt(1)
                    ShowIntro(
                        "Selesai Tab",
                        "Pada tab Selesai akan berisi daftar dari pesanan yang Telah selesai diproses oleh merchant.",
                        proses, 10
                    )
                }else if (type == 10) {
                    var proses = (tabs.getChildAt(0) as ViewGroup).getChildAt(2)
                    ShowIntro(
                        "Batal Tab",
                        "Pada tab Batal akan berisi daftar dari pesanan yang Telah dibatalkan dan tidak diproses oleh merchant.",
                        proses, 11
                    )
                }else if (type == 11) {
                    ShowIntro(
                        "Manual Transaction",
                        "Tombol tambah digunakan untuk menambah transaksi masuk dari pelanggan",
                        findViewById(R.id.manualTxn), 12
                    )
                }else if (type == 12){
                    modalContent(1)
                }
            }
            .build()
            .show()
    }

    private fun modalContent(status: Int){
        modalView.visibility = View.VISIBLE
        layerView.visibility = View.VISIBLE
        if(status == 1){
            titleModal.text = "Pesanan Masuk"
            desc1.visibility = View.VISIBLE
            desc1.text = "Pesanan masuk akan menampilkan status seperti pada gambar dibawah"
            desc2.text = "Status “NEW” berarti pesanan tersebut adalah pesanan yang baru saja dipesan."
            pageNum.text = "2 Dari 8"
            image.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.masuk_modal))
            nextModal.setOnClickListener {
                modalContent(2)
            }
        }else if (status == 2){
            titleModal.text = "Pesanan Masuk"
            desc1.visibility = View.GONE
            desc2.visibility = View.VISIBLE
            desc3.visibility = View.VISIBLE
            desc3.text = "Tombol sudah bayar digunakan untuk mengonfirmasi bahwa pesanan tersebut telah dibayar "
            desc2.text = "Tombol tolak digunakan untuk menolak pesanan."
            pageNum.text = "3 Dari 8"
            image.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.modal_enter))
            nextModal.setOnClickListener {
                modalContent(3)
            }
        }else if(status == 3){
            titleModal.text = "Pesanan Diproses"
            desc1.visibility = View.VISIBLE
            desc2.visibility = View.VISIBLE
            desc3.visibility = View.GONE
            desc1.text = "Setelah Anda menekan tombol sudah bayar, maka status pesanan akan berubah menjadi “Diproses”."
            desc2.text = "Tombol “Pesanan Siap” digunakan untuk mengonfirmasi bahwa pesanan telah selesai dibuat dan siap untuk dihidangkan."
            pageNum.text = "4 Dari 8"
            image.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.modal_proses))
            nextModal.setOnClickListener {
                modalContent(4)
            }
        }else if(status == 4){
            titleModal.text = "Pesanan Dikirim"
            desc1.visibility = View.VISIBLE
            desc2.visibility = View.VISIBLE
            desc1.text = "Pesanan yang telah siap akan menampilkan status seperti pada gambar dibawah"
            desc2.text = "Status “Dikirim” digunakan untuk menjelaskan status dari suatu pesanan apabila pesanan tersebut telah dikirim kepada pelanggan"
            pageNum.text = "5 Dari 8"
            image.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.modal_kirim))
            nextModal.setOnClickListener {
                modalContent(5)
            }
        }else if(status == 5){
            titleModal.text = "Pesanan Dikirim"
            desc1.visibility = View.GONE
            desc2.text = "Tombol selesai digunakan untuk mengonfirmasi pesanan jika pesanan telah sampai ke pelanggan."
            pageNum.text = "6 Dari 8"
            image.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.modal_kirim_2))
            nextModal.setOnClickListener {
                modalContent(6)
            }
        }else if(status == 6){
            titleModal.text = "Pesanan Selesai"
            desc1.visibility = View.GONE
            desc2.text = "Status pesanan “selesai” digunakan untuk menjelaskan status dari pesanan jika telah terselesaikan."
            pageNum.text = "7 Dari 8"
            image.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.modal_done_3))
            nextModal.setOnClickListener {
                modalContent(7)
            }
        }else if(status == 7){
            titleModal.text = "Pesanan Dibatalkan"
            desc1.visibility = View.VISIBLE
            desc2.visibility = View.VISIBLE
            desc1.text = "Pesanan yang dibatalkan akan menampilkan status seperti pada gambar dibawah"
            desc2.text = "Status gagal digunakan untuk menunjukkan bahwa pesanan telah gagal dan tidak diproses oleh mechant."
            pageNum.text = "8 Dari 8"
            image.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.modal_cancel))
            nextModal.setOnClickListener {
                modalView.visibility = View.GONE
                layerView.visibility = View.GONE
                viewModel.postTutorial("TUTORIAL_TRANSACTION")
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
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

    fun getTutorial(name: String){
        val email = sessionManager.getUserData()?.email
        val token = sessionManager.getUserToken()!!
        var mid = sessionManager.getUserData()?.mid
        var timestamp = getTimestamp()
        var uuid = getUUID()
        var clientId = getClientID()
        var status = false
        var signature = getSignature(email, timestamp)

        PikappApiService().api.getTutorial(uuid, timestamp, clientId, signature, token, mid, mid.toString())
            .enqueue(object : Callback<TutorialGetResponse> {
                override fun onResponse(
                    call: Call<TutorialGetResponse>,
                    response: Response<TutorialGetResponse>
                ) {
                    Log.e("Response", response.code().toString())
                    Log.e("Response", response.body()?.results.toString())
                    if(response.body()?.results?.isEmpty() == true){
                        modalView.visibility = View.VISIBLE
                        layerView.visibility = View.VISIBLE
                        desc1.visibility = View.GONE
                        desc3.visibility = View.GONE
                        nextModal.setOnClickListener {
                            modalView.visibility = View.GONE
                            layerView.visibility = View.GONE
                            ShowIntro("Navigation Bar",
                                "Terdapat navigation bar dengan 4 tombol yang memiliki fungsi berbeda",
                                bottom_navigation, 2)
                        }

                        skipModal.setOnClickListener {
                            modalView.visibility = View.GONE
                            layerView.visibility = View.GONE
                        }

                    }else{
                        for (i in response.body()?.results!!){
                            if(i.tutorial_page == name){
                                status = true
                                modalView.visibility = View.GONE
                                layerView.visibility = View.GONE
                            }
                        }
                        if(status == false){
                            modalView.visibility = View.VISIBLE
                            layerView.visibility = View.VISIBLE
                            desc1.visibility = View.GONE
                            desc3.visibility = View.GONE
                            nextModal.setOnClickListener {
                                modalView.visibility = View.GONE
                                layerView.visibility = View.GONE
                                ShowIntro("Navigation Bar",
                                    "Terdapat navigation bar dengan 4 tombol yang memiliki fungsi berbeda",
                                    bottom_navigation, 2)
                            }

                            skipModal.setOnClickListener {
                                modalView.visibility = View.GONE
                                layerView.visibility = View.GONE
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<TutorialGetResponse>, t: Throwable) {
                    Log.e("error", t.message.toString())
                }

            })
    }
}