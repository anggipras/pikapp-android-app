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
import com.tsab.pikapp.util.SessionManager
import com.tsab.pikapp.view.homev2.menu.MenuFragment
import com.tsab.pikapp.view.homev2.other.OtherFragment
import com.tsab.pikapp.view.homev2.promo.PromoFragment
import com.tsab.pikapp.view.homev2.menu.TransactionFragment
import com.tsab.pikapp.view.homev2.menu.WebMenuActivity
import com.tsab.pikapp.view.menuCategory.SortActivity
import com.tsab.pikapp.viewmodel.categoryMenu.CategoryViewModel
import kotlinx.android.synthetic.main.activity_home_navigation.*
import kotlinx.android.synthetic.main.layout_header_drawer.view.*
import naci.showcaseview.ShowcaseView
import naci.showcaseview.listener.IShowcaseListener
import smartdevelop.ir.eram.showcaseviewlib.GuideView

class HomeActivity : AppCompatActivity() {
    val model: CategoryViewModel by viewModels()
    private val transactionFragment = TransactionFragment()
    private val menuFragment = MenuFragment()
    private val promoFragment = PromoFragment()
    private val otherFragment = OtherFragment()
    private val sessionManager = SessionManager()
    private lateinit var dataBinding: ActivityHomeNavigationBinding

    var showcaseView: ShowcaseView? = null
    var showcaseView2: ShowcaseView? = null

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

        bottom_navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_transaction -> replaceFragment(transactionFragment)
                R.id.nav_menu -> replaceFragment(menuFragment)
                R.id.nav_promo -> replaceFragment(promoFragment)
                R.id.nav_other -> replaceFragment(otherFragment)
            }
            true
        }

        ShowIntro("Navigation Bar",
            "Terdapat navigation bar dengan 4 tombol yang memiliki fungsi berbeda",
        bottom_navigation, 2)
    }


    private fun ShowIntro(title: String, desc:String, view: View, type: Int){
        GuideView.Builder(this)
            .setTitle(title)
            .setContentText(desc)
            .setGravity(GuideView.Gravity.auto)
            .setTargetView(view)
            .setContentTextSize(12)
            .setTitleTextSize(14)
            .setGuideListener {
                if (type == 1) {
                    ShowIntro("Navigation Bar",
                        "Terdapat navigation bar dengan 4 tombol yang memiliki fungsi berbeda",
                        bottom_navigation, 2)
                }else if (type == 2){
                    replaceFragment(transactionFragment)
                    ShowIntro("Transaction Button",
                        "Tombol transaksi digunakan untuk mengakses halaman transaksi pesanan dari merchant anda.",
                        findViewById(R.id.nav_transaction), 3)
                }else if (type == 3){
                    ShowIntro("Transaction Page",
                        "Halaman ini akan berisi daftar pesanan dari merchant anda.",
                        findViewById(R.id.header), 4)
                }else if (type == 4){
                    replaceFragment(menuFragment)
                    ShowIntro("Menu Button",
                        "Tombol menu digunakan untuk mengakses halaman yang berisi daftar menu yang dimiliki merchant anda.",
                        findViewById(R.id.nav_menu), 5)
                }else if (type == 5){
                    replaceFragment(promoFragment)
                    ShowIntro("Promo Button",
                        "Tombol Promo digunakan untuk mengakses halaman “Promo” yang dimiliki oleh merchant.",
                        findViewById(R.id.nav_promo), 6)
                }else if (type == 6){
                    replaceFragment(otherFragment)
                    ShowIntro("Other Page",
                        "Tombol lainnya digunakan untuk mengkases halaman yang berisi informasi dari merchant anda.",
                        findViewById(R.id.nav_other), 7)
                }
            }
            .build()
            .show()
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

   /* fun firstShowcase(){
        val showcaseView = ShowcaseView.Builder(this)
            .setTargetView(bottom_navigation)
            .setBackgroundOverlayColor(Color.parseColor("#66000000"))
            .setRingColor(Color.BLUE)
            .setShowCircles(true)
            .setHideOnTouchOutside(false)
            .setShowcaseShape(ShowcaseView.SHAPE_SKEW)
            .setShowcaseListener(object :
                IShowcaseListener {
                override fun onShowcaseDisplayed(showcaseView: ShowcaseView) {
                    //TODO : do something..
                }

                override fun onShowcaseDismissed(showcaseView: ShowcaseView) {
                    //TODO : do something..
                }

                override fun onShowcaseSkipped(showcaseView: ShowcaseView) {
                    //TODO : do something..
                }
            })
            .addCustomView(R.layout.layout_showcase_body, Gravity.TOP)
            .build()

        showcaseView.show()

        showcaseView!!.setClickListenerOnView(
            R.id.skipBtn,
            View.OnClickListener {
                // showcaseView!!.showcaseSkipped() // Use this when skip button clicked
                showcaseView!!.hide()
            }
        )

        showcaseView!!.setClickListenerOnView(
            R.id.btn_Next,
            View.OnClickListener {
                // showcaseView!!.showcaseSkipped() // Use this when skip button clicked
                Log.e("Next", "Next")

                secondShowcase()
                showcaseView!!.hide()
            }
        )
    }

    private fun secondShowcase(){
        val showcaseView2 = ShowcaseView.Builder(this)
            .setTargetView(findViewById(R.id.nav_transaction))
            .setBackgroundOverlayColor(Color.parseColor("#66000000"))
            .setRingColor(Color.BLUE)
            .setShowCircles(true)
            .setHideOnTouchOutside(false)
            .setShowcaseShape(ShowcaseView.SHAPE_SKEW)
            .setShowcaseListener(object :
                IShowcaseListener {
                override fun onShowcaseDisplayed(showcaseView: ShowcaseView) {
                    //TODO : do something..
                }

                override fun onShowcaseDismissed(showcaseView: ShowcaseView) {
                    //TODO : do something..
                }

                override fun onShowcaseSkipped(showcaseView: ShowcaseView) {
                    //TODO : do something..
                }
            })
            .addCustomView(R.layout.layout_showcase_body2, Gravity.TOP)
            .build()

        showcaseView2!!.setClickListenerOnView(
            R.id.skipBtn,
            View.OnClickListener {
                // showcaseView!!.showcaseSkipped() // Use this when skip button clicked
                showcaseView2!!.hide()
            }
        )

        showcaseView2!!.setClickListenerOnView(
            R.id.btn_Next,
            View.OnClickListener {
                // showcaseView!!.showcaseSkipped() // Use this when skip button clicked
                Log.e("Next", "Next")

                thirdShowcase()
                showcaseView2!!.hide()
            }
        )

        showcaseView2!!.setClickListenerOnView(
            R.id.btn_back,
            View.OnClickListener {
                // showcaseView!!.showcaseSkipped() // Use this when skip button clicked
                Log.e("Next", "Next")

                firstShowcase()
                showcaseView2!!.hide()
            }
        )

        showcaseView2.show()
    }

    private fun thirdShowcase() {
        val showcaseView3 = ShowcaseView.Builder(this)
            .setBackgroundOverlayColor(Color.parseColor("#66000000"))
            .setTargetView(transactionFragment.requireView().findViewById(R.id.header))
            .setRingColor(Color.BLUE)
            .setShowCircles(true)
            .setHideOnTouchOutside(false)
            .setShowcaseMargin(15F)
            .setShowcaseShape(ShowcaseView.SHAPE_SKEW)
            .setRingWidth(
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    2f,
                    resources.displayMetrics
                )
            )
            .setDistanceBetweenShowcaseCircles(10)
            .setShowcaseListener(object :
                IShowcaseListener {
                override fun onShowcaseDisplayed(showcaseView: ShowcaseView) {
                    //TODO : do something..
                }

                override fun onShowcaseDismissed(showcaseView: ShowcaseView) {
                    //TODO : do something..
                }

                override fun onShowcaseSkipped(showcaseView: ShowcaseView) {
                    //TODO : do something..
                }
            })
            .addCustomView(R.layout.layout_showcase_body3, Gravity.CENTER,
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    2f,
                    resources.displayMetrics
                ),
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    0f,
                    resources.displayMetrics
                ),
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    -30f,
                    resources.displayMetrics
                ),
                0f)
            .build()

        showcaseView3!!.setClickListenerOnView(
            R.id.skipBtn,
            View.OnClickListener {
                // showcaseView!!.showcaseSkipped() // Use this when skip button clicked
                showcaseView3!!.hide()
            }
        )

        showcaseView3!!.setClickListenerOnView(
            R.id.btn_Next,
            View.OnClickListener {
                // showcaseView!!.showcaseSkipped() // Use this when skip button clicked
                Log.e("Next", "Next")

                fourthShowcase()
                showcaseView3!!.hide()
            }
        )

        showcaseView3!!.setClickListenerOnView(
            R.id.btn_back,
            View.OnClickListener {
                // showcaseView!!.showcaseSkipped() // Use this when skip button clicked
                Log.e("Next", "Next")

                secondShowcase()
                showcaseView3!!.hide()
            }
        )

        showcaseView3.show()
    }

    private fun fourthShowcase() {
        val showcaseView3 = ShowcaseView.Builder(this)
            .setBackgroundOverlayColor(Color.parseColor("#66000000"))
            .setTargetView(findViewById(R.id.nav_menu))
            .setRingColor(Color.BLUE)
            .setShowCircles(true)
            .setHideOnTouchOutside(false)
            .setShowcaseMargin(15F)
            .setShowcaseShape(ShowcaseView.SHAPE_SKEW)
            .setRingWidth(
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    2f,
                    resources.displayMetrics
                )
            )
            .setDistanceBetweenShowcaseCircles(10)
            .setShowcaseListener(object :
                IShowcaseListener {
                override fun onShowcaseDisplayed(showcaseView: ShowcaseView) {
                    //TODO : do something..
                }

                override fun onShowcaseDismissed(showcaseView: ShowcaseView) {
                    //TODO : do something..
                }

                override fun onShowcaseSkipped(showcaseView: ShowcaseView) {
                    //TODO : do something..
                }
            })
            .addCustomView(R.layout.layout_showcase_body4, Gravity.TOP,
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    2f,
                    resources.displayMetrics
                ),
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    0f,
                    resources.displayMetrics
                ),
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    -30f,
                    resources.displayMetrics
                ),
                0f)
            .build()

        showcaseView3!!.setClickListenerOnView(
            R.id.skipBtn,
            View.OnClickListener {
                // showcaseView!!.showcaseSkipped() // Use this when skip button clicked
                showcaseView3!!.hide()
            }
        )

        showcaseView3!!.setClickListenerOnView(
            R.id.btn_Next,
            View.OnClickListener {
                // showcaseView!!.showcaseSkipped() // Use this when skip button clicked
                Log.e("Next", "Next")

                fifthShowcase()
                showcaseView3!!.hide()
            }
        )

        showcaseView3!!.setClickListenerOnView(
            R.id.btn_back,
            View.OnClickListener {
                // showcaseView!!.showcaseSkipped() // Use this when skip button clicked
                Log.e("Next", "Next")

                thirdShowcase()
                showcaseView3!!.hide()
            }
        )

        showcaseView3.show()
    }

    private fun fifthShowcase() {
        val showcaseView3 = ShowcaseView.Builder(this)
            .setBackgroundOverlayColor(Color.parseColor("#66000000"))
            .setTargetView(findViewById(R.id.nav_promo))
            .setRingColor(Color.BLUE)
            .setShowCircles(true)
            .setHideOnTouchOutside(false)
            .setShowcaseMargin(15F)
            .setShowcaseShape(ShowcaseView.SHAPE_SKEW)
            .setRingWidth(
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    2f,
                    resources.displayMetrics
                )
            )
            .setDistanceBetweenShowcaseCircles(10)
            .setShowcaseListener(object :
                IShowcaseListener {
                override fun onShowcaseDisplayed(showcaseView: ShowcaseView) {
                    //TODO : do something..
                }

                override fun onShowcaseDismissed(showcaseView: ShowcaseView) {
                    //TODO : do something..
                }

                override fun onShowcaseSkipped(showcaseView: ShowcaseView) {
                    //TODO : do something..
                }
            })
            .addCustomView(R.layout.layout_showcase_body5, Gravity.TOP,
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    2f,
                    resources.displayMetrics
                ),
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    0f,
                    resources.displayMetrics
                ),
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    -30f,
                    resources.displayMetrics
                ),
                0f)
            .build()

        showcaseView3!!.setClickListenerOnView(
            R.id.skipBtn,
            View.OnClickListener {
                // showcaseView!!.showcaseSkipped() // Use this when skip button clicked
                showcaseView3!!.hide()
            }
        )

        showcaseView3!!.setClickListenerOnView(
            R.id.btn_Next,
            View.OnClickListener {
                // showcaseView!!.showcaseSkipped() // Use this when skip button clicked
                Log.e("Next", "Next")

                sixthShowcase()
                showcaseView3!!.hide()
            }
        )

        showcaseView3!!.setClickListenerOnView(
            R.id.btn_back,
            View.OnClickListener {
                // showcaseView!!.showcaseSkipped() // Use this when skip button clicked
                Log.e("Next", "Next")

                fourthShowcase()
                showcaseView3!!.hide()
            }
        )

        showcaseView3.show()
    }

    private fun sixthShowcase() {
        val showcaseView3 = ShowcaseView.Builder(this)
            .setBackgroundOverlayColor(Color.parseColor("#66000000"))
            .setTargetView(findViewById(R.id.nav_other))
            .setRingColor(Color.BLUE)
            .setShowCircles(true)
            .setHideOnTouchOutside(false)
            .setShowcaseMargin(15F)
            .setShowcaseShape(ShowcaseView.SHAPE_SKEW)
            .setRingWidth(
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    2f,
                    resources.displayMetrics
                )
            )
            .setDistanceBetweenShowcaseCircles(10)
            .setShowcaseListener(object :
                IShowcaseListener {
                override fun onShowcaseDisplayed(showcaseView: ShowcaseView) {
                    //TODO : do something..
                }

                override fun onShowcaseDismissed(showcaseView: ShowcaseView) {
                    //TODO : do something..
                }

                override fun onShowcaseSkipped(showcaseView: ShowcaseView) {
                    //TODO : do something..
                }
            })
            .addCustomView(R.layout.layout_showcase_body6, Gravity.TOP,
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    2f,
                    resources.displayMetrics
                ),
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    0f,
                    resources.displayMetrics
                ),
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    -30f,
                    resources.displayMetrics
                ),
                0f)
            .build()

        showcaseView3!!.setClickListenerOnView(
            R.id.skipBtn,
            View.OnClickListener {
                // showcaseView!!.showcaseSkipped() // Use this when skip button clicked
                showcaseView3!!.hide()
            }
        )

        showcaseView3!!.setClickListenerOnView(
            R.id.btn_Next,
            View.OnClickListener {
                // showcaseView!!.showcaseSkipped() // Use this when skip button clicked
                Log.e("Next", "Next")
                showcaseView3!!.hide()
            }
        )

        showcaseView3!!.setClickListenerOnView(
            R.id.btn_back,
            View.OnClickListener {
                // showcaseView!!.showcaseSkipped() // Use this when skip button clicked
                Log.e("Next", "Next")

                fifthShowcase()
                showcaseView3!!.hide()
            }
        )

        showcaseView3.show()
    }*/

}