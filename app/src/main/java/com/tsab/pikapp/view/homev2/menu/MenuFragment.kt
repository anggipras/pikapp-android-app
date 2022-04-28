package com.tsab.pikapp.view.homev2.menu

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.squareup.picasso.Picasso
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.MenuFragmentBinding
import com.tsab.pikapp.models.model.TutorialGetResponse
import com.tsab.pikapp.models.network.PikappApiService
import com.tsab.pikapp.services.CacheService
import com.tsab.pikapp.services.OnlineService
import com.tsab.pikapp.util.*
import com.tsab.pikapp.view.LoginV2Activity
import com.tsab.pikapp.view.homev2.HomeActivity
import com.tsab.pikapp.view.menuCategory.CategoryNavigation
import com.tsab.pikapp.viewmodel.homev2.MenuViewModel
import com.tsab.pikapp.viewmodel.homev2.OtherViewModel
import com.tsab.pikapp.viewmodel.homev2.TutorialViewModel
import kotlinx.android.synthetic.main.activity_home_navigation.*
import kotlinx.android.synthetic.main.menu_fragment.*
import kotlinx.android.synthetic.main.other_fragment.*
import java.io.File
import kotlinx.android.synthetic.main.layout_page_problem.view.*
import kotlinx.android.synthetic.main.menu_fragment.*
import kotlinx.android.synthetic.main.menu_fragment.tabs
import kotlinx.android.synthetic.main.menu_fragment.topAppBar
import kotlinx.android.synthetic.main.transaction_fragment.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import smartdevelop.ir.eram.showcaseviewlib.GuideView

class MenuFragment : Fragment(), AdapterView.OnItemSelectedListener {
    private val viewModel: MenuViewModel by activityViewModels()
    private val viewModel1: TutorialViewModel by activityViewModels()
    private lateinit var dataBinding: MenuFragmentBinding
    private val otherViewModel: OtherViewModel by activityViewModels()

    private var categoryList: List<String> = listOf()
    lateinit var linearLayoutManager: LinearLayoutManager
    private val sessionManager = SessionManager()

    var list_of_items = arrayOf("Pikapp")
    var merchantName = ""
    var name = ""
    //    var list_of_items = arrayOf("Pikapp", "Tokopedia", "Shopee")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.menu_fragment, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getMenuData()
        general_error_menu.try_button.setOnClickListener {
            viewModel.mutableIsLoading.value = true
            getMenuData()
        }

//        getTutorial("TUTORIAL_MENU")

//       ShowIntro("Add Kategori", "Pada halaman menu, anda dapat menambahkan menu yang merchant anda jual.", dataBinding.appbar, 2)

        setMenuInvisible()
        observeViewModel()
    }

    private fun getMenuData() {
        val onlineService = OnlineService()
        if (onlineService.isOnline(context)) {
            activity?.let { viewModel.getMenuCategoryList(it.baseContext, requireActivity(), general_error_menu) }
//            otherViewModel.getMerchantProfile(requireContext(), requireActivity(), general_error_menu)
            general_error_menu.isVisible = false
        } else {
            general_error_menu.isVisible = true
            viewModel.mutableIsLoading.value = false
            onlineService.networkDialog(requireActivity())
        }
    }

    override fun onResume() {
        super.onResume()

        attachInputListeners()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.restartFragment()
        CacheService().deleteCache(requireContext())
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            dataBinding.shimmerFrameLayoutCategory.visibility =
                if (isLoading) View.VISIBLE else View.GONE
            if (!isLoading) {
                initViews()
                dataBinding.shimmerFrameLayoutMenu.visibility = View.GONE
            }
        })

        viewModel.categoryListResult.observe(viewLifecycleOwner, Observer { categoryList ->
            this.categoryList = categoryList.map { it.category_name ?: "" }

            dataBinding.tabs.removeAllTabs()
            this.categoryList.forEach { categoryName ->
                dataBinding.tabs.newTab().apply {
                    text = categoryName
                    dataBinding.tabs.addTab(this)
                }
            }
        })

        viewModel.size.observe(viewLifecycleOwner, Observer { size ->
            if (size == 0 && viewModel.isLoading.value == false) {
                invisibleMenu()
                dataBinding.textview2.visibility = View.VISIBLE
                dataBinding.imageView18.visibility = View.VISIBLE
                dataBinding.addCategory.visibility = View.VISIBLE
            } else {
                invisibleMenuNull()
                dataBinding.viewpager.visibility = View.VISIBLE
            }

            if (size != 0) {
                dataBinding.tabs.visibility = View.VISIBLE
                dataBinding.appbar.visibility = View.VISIBLE
                dataBinding.plusBtn.visibility = View.VISIBLE
            }
        })

        viewModel.errCode.observe(viewLifecycleOwner, Observer { errCode ->
            if (errCode == "EC0032" || errCode == "EC0021" || errCode == "EC0017") {
                sessionManager.logout()
                Intent(activity?.baseContext, LoginV2Activity::class.java).apply {
                    activity?.startActivity(this)
                }
            }
        })

        otherViewModel.merchantResult.observe(viewLifecycleOwner, Observer { merchantProfile ->
            merchantName = merchantProfile.merchantName.toString()
            list_of_items = arrayOf(
                "Pikapp - ${merchantProfile.merchantName}"
                //"Tokopedia - ${merchantProfile.merchantName}"
                //"Shopee - ${merchantProfile.merchantName}"
            )
            val spinner = this.menuSpinner

            spinner!!.onItemSelectedListener = this

            val array_adapter =
                context?.let { ArrayAdapter(it, android.R.layout.simple_spinner_item, list_of_items) }
            array_adapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            spinner.adapter = array_adapter

        })
    }

    private fun attachInputListeners() {
        dataBinding.addCategory.setOnClickListener {
            if (viewModel.size.value == 0) {
                Intent(activity?.baseContext, MenuNavigation::class.java).apply {
                    activity?.startActivity(this)
                    activity?.overridePendingTransition(R.anim.slide_up, R.anim.no_animation)
                }
            }
        }

        dataBinding.plusBtn.setOnClickListener {
            Intent(activity?.baseContext, CategoryNavigation::class.java).apply {
                activity?.startActivity(this)
                activity?.overridePendingTransition(R.anim.slide_up, R.anim.no_animation)
            }
        }

        topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navDraw -> {
                    (activity as HomeActivity).openCloseDrawer(requireView())
                    true
                }
                else -> false
            }
        }
    }

    private fun invisibleMenuNull() {
        dataBinding.textview2.visibility = View.GONE
        dataBinding.imageView18.visibility = View.GONE
        dataBinding.addCategory.visibility = View.GONE
        dataBinding.textview3.visibility = View.GONE
    }

    private fun invisibleMenu() {
        dataBinding.plusBtn.visibility = View.GONE
        dataBinding.tabs.visibility = View.GONE
        dataBinding.viewpager.visibility = View.GONE
        dataBinding.appbar.visibility = View.GONE
    }

    private fun setMenuInvisible() {
        dataBinding.tabs.visibility = View.GONE
        dataBinding.viewpager.visibility = View.GONE
        dataBinding.appbar.visibility = View.GONE
        dataBinding.textview2.visibility = View.GONE
        dataBinding.imageView18.visibility = View.GONE
        dataBinding.addCategory.visibility = View.GONE
        dataBinding.plusBtn.visibility = View.GONE
        dataBinding.textview3.visibility = View.GONE
        dataBinding.shimmerFrameLayoutMenu.visibility = View.VISIBLE
    }

    private fun initViews() {
        dataBinding.viewpager.offscreenPageLimit = 5
        dataBinding.viewpager.addOnPageChangeListener(
            TabLayout.TabLayoutOnPageChangeListener(
                dataBinding.tabs
            )
        )

        dataBinding.tabs.setOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (sessionManager.getMenuDefInit() == 0) {
                    dataBinding.viewpager.currentItem = tab.position
                    sessionManager.setMenuPageTab(tab.position)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        setDynamicFragmentToTabLayout()
    }

    private fun setDynamicFragmentToTabLayout() {
        dataBinding.tabs.removeAllTabs()
        categoryList.forEach { categoryName ->
            dataBinding.tabs.newTab().apply {
                text = categoryName
                dataBinding.tabs.addTab(this)
            }
        }

        val mDynamicFragmentAdapter =
            DynamicFragmentAdapter(childFragmentManager, categoryList.size, categoryList)
        dataBinding.viewpager.adapter = mDynamicFragmentAdapter
        if (sessionManager.getMenuDefInit() != 0) {
            dataBinding.tabs.getTabAt(sessionManager.getMenuPageTab()!!)?.select()
            dataBinding.viewpager.currentItem = sessionManager.getMenuPageTab()!!
            sessionManager.setMenuDefInit(0)
        } else {
            dataBinding.viewpager.currentItem = 0
        }
    }

    fun ShowIntro(title: String, desc:String, view: View, type: Int){
        GuideView.Builder(requireContext())
            .setTitle(title)
            .setContentText(desc)
            .setGravity(GuideView.Gravity.auto)
            .setTargetView(view)
            .setDismissType(GuideView.DismissType.anywhere)
            .setContentTextSize(12)
            .setTitleTextSize(14)
            .setGuideListener {
                if(type == 2){
                    viewModel1.postTutorial("TUTORIAL_MENU")
                }
            }
            .build()
            .show()
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
                        ShowIntro("Add Kategori", "Pada halaman menu, anda dapat menambahkan menu yang merchant anda jual.", dataBinding.appbar, 2)
                    }else{
                        for (i in response.body()?.results!!){
                            if(i.tutorial_page == name){
                                status = true
                            }
                        }
                        if(status == false){
                            ShowIntro("Add Kategori", "Pada halaman menu, anda dapat menambahkan menu yang merchant anda jual.", dataBinding.appbar, 2)
                        }
                    }
                }

                override fun onFailure(call: Call<TutorialGetResponse>, t: Throwable) {
                    Log.e("error", t.message.toString())
                }

            })
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        //Toast.makeText(context, list_of_items[position], Toast.LENGTH_SHORT).show()
        name = list_of_items[position]
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }
}