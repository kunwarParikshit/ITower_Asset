package com.isl.common.activities
import FetchDeviceIDRequest
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.isl.assetManagement.dataViewModel.RemoteViewModel
import com.isl.assetManagement.dataViewModel.RoomViewModel
import com.isl.assetManagement.room.db.IAssetDatabase
import com.isl.common.fragments.Module
import com.isl.common.fragments.Notifications
import com.isl.common.fragments.Profile
import com.isl.assetManagement.assetModuleMainPage.Tasks
import com.isl.assetManagement.room.repository.RemoteRepository
import com.isl.assetManagement.room.repository.RoomRepository
import com.isl.assetManagement.sharedPref.KotlinPrefkeeper
import com.isl.assetManagement.utils.CustomToastMsg
import com.isl.assetManagement.utils.DataViewModelFactory
import com.isl.assetManagement.utils.HomeViewModelFactory
import com.isl.dao.cache.AppPreferences
import com.isl.itower.MyApp
import infozech.itower.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class Home : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView
    lateinit var iAssetDatabase: IAssetDatabase
    private lateinit var roomViewModel: RoomViewModel
    private lateinit var roomRepository: RoomRepository
    private lateinit var remoteViewModel: RemoteViewModel

    var mAppPreferences: AppPreferences? = null
      override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.asset_home_module_activity)

        init()
      }


    private fun init() {
        roomRepository = RoomRepository(MyApp.getAssetDatabase().dataDao())
        iAssetDatabase = IAssetDatabase.getDatabase(applicationContext)
        remoteViewModel = ViewModelProvider(this,
            HomeViewModelFactory(RemoteRepository()))[RemoteViewModel::class.java]
        roomViewModel = ViewModelProvider(this,
            DataViewModelFactory(RoomRepository
                (MyApp.getAssetDatabase().dataDao())))[RoomViewModel::class.java]
        fetchDeviceId()
    }



    private fun openFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, fragment)  // Replace the content of the FrameLayout with the selected fragment
            .commit()
    }

    private fun fetchDeviceId() {
        var mAppPref: AppPreferences? = null
        mAppPref = AppPreferences(this@Home)
        val deviceToken = mAppPref.gcmRegistationId
        val loginId = mAppPref.loginId
        if (KotlinPrefkeeper.deviceUUID == null || KotlinPrefkeeper.deviceUUID!!.isEmpty()) {  //updating only after logout (as data will be cleared)
            KotlinPrefkeeper.deviceUUID = UUID.randomUUID().toString()
        }

        lifecycleScope.launch(Dispatchers.Main) {
            val token = roomRepository.fetchToken()
            if(token!=null){

                /*remoteViewModel.getAssetDetails(
                    token = token,
                    siteId = "",
                    assetId = "TG-000010004",
                    qrCode = "",
                    onDataInserted = { status ->
                        Log.d("DataInsertion", "Insertion Status: $status")
                    }
                ) { assets ->
                    assets?.let {
                        Log.d("Assets", "Fetched Assets: $it")
                    } ?: Log.e("Assets", "No assets found")
                }*/

                val fetchDeviceIDRequest =
                    FetchDeviceIDRequest(
                        loginId = "mast.admin@gmail.com",
                        //loginId = loginId,
                        pushToken = deviceToken,
                        deviceId = KotlinPrefkeeper.deviceUUID
                    )
                //showProgressBar()
                SnackbarUtils.showLoading(this@Home, "Loading data...")
                remoteViewModel.getUserId(
                    { successResponse ->
                        successResponse?.let { response ->
                            //hideProgressBar()
                            SnackbarUtils.hideLoading()
                            if (response.userId != null) {
                                KotlinPrefkeeper.assetUserId = response.userId.toString()
                            } else {
                                CustomToastMsg.showCustomToast(this@Home,
                                    "User's ID is empty")
                                //finish()
                                //KotlinPrefkeeper.assetUserId = "1"
                            }
                            init2()
                        }
                    },
                    { errorMessage ->
                        //hideProgressBar()
                        SnackbarUtils.hideLoading()
                        //showToastMessage("Unable to get user's ID")
                        CustomToastMsg.showCustomToast(this@Home,
                            "Unable to get user's ID")
                        //finish()
                        //KotlinPrefkeeper.assetUserId = "1"
                        init2()

                    },
                    body = fetchDeviceIDRequest,
                    token = token
                )
            }else{
                CustomToastMsg.showCustomToast(
                    this@Home,"Token authentication failed. Try again.")
                //this@Home?.finish()
            }
        }


    }

    private fun setClickListeners() {
   // Initialize Bottom Navigation View
        bottomNav = findViewById(R.id.bottomNavigationView)
        // text size of menu item
        val menu: Menu = bottomNav.getMenu()
        for (i in 0 until menu.size()) {
            val item: MenuItem = menu.getItem(i)
            val spannableString = SpannableString(item.getTitle())
            spannableString.setSpan(
                AbsoluteSizeSpan(12, true),
                0,
                spannableString.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            ) // Set size to 14sp
            item.setTitle(spannableString)
        }



        // Set up BottomNavigationView to load fragments
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.task -> {
                    openFragment(Tasks())
                    true
                }
                R.id.notification -> {
                    openFragment(Notifications())
                    true
                }
                R.id.module -> {
                    val bottomSheetFragment = Module()
                    bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
                    true
                }
                R.id.profile -> {
                    openFragment(Profile())
                    true
                }
                else -> false
            }
        }
    }

    /*private fun showProgressBar() {
        val progressBar: ProgressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        val progressBar: ProgressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.GONE
    }*/

    private fun init2(){
        roomViewModel.fetchLevelFromApi()
        roomViewModel.fetchParamFromApi()
        openFragment(Tasks())
        setClickListeners()
    }
}
