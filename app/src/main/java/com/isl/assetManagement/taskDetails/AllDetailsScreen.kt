package com.isl.assetManagement.taskDetails
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.isl.assetManagement.dataViewModel.RoomViewModel
import com.isl.assetManagement.jetpackcompose.*
import com.isl.assetManagement.jetpackcompose.JetpackUIs.TimeLine.TimelineScreen
import com.isl.assetManagement.responses.Assets
import com.isl.assetManagement.responses.Documents
import com.isl.assetManagement.responses.Timeline
import com.isl.assetManagement.room.entity.TaskDetailEntity
import com.isl.assetManagement.room.repository.RoomRepository
import com.isl.assetManagement.utils.CustomToastMsg
import com.isl.assetManagement.utils.DataViewModelFactory
import com.isl.itower.MyApp
import infozech.itower.R

class AllDetailsScreen : DialogFragment() {
    private lateinit var dataRepository: RoomRepository
    private lateinit var viewModel: RoomViewModel
    private var requestId: String? = null
    private var mode: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize ViewModel
        dataRepository = RoomRepository(MyApp.getAssetDatabase().dataDao())
        viewModel = ViewModelProvider(
            this,
            DataViewModelFactory(dataRepository)
        ).get(RoomViewModel::class.java)

        // Retrieve requestId from arguments
        arguments?.let {
            requestId = it.getString("requestId") // Get the requestId
        }
        // Retrieve requestId from arguments
        arguments?.let {
            mode = it.getString("mode") // Get the requestId
        }

    }

    @SuppressLint("ResourceAsColor")
    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setBackgroundDrawableResource(R.color.white)
            val params = attributes
            params.width = ViewGroup.LayoutParams.MATCH_PARENT
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT
            attributes = params
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {

            setContent {
                MaterialTheme(
                    colors = lightColors(
                        primary = colorResource(id = R.color.white),
                        background = colorResource(id = R.color.white)
                    )
                ) {
                    AddScreen()
                }
            }
        }
    }


    @SuppressLint("NotConstructor")
    @Composable
    fun AddScreen() {
        //val tabItems = listOf("Details", "Assets", "Track","Files")
        var selectedTabIndex by remember { mutableStateOf(0) }
        requestId?.let { viewModel.getTaskDetail(it) }
        val taskDetailEntity by viewModel.getTaskDetail.observeAsState()
        val totalApprovedQty = taskDetailEntity?.assets?.sumOf { it.approvedQty } ?: 0

        val tabItems = listOf(
            "Details",
            //"Assets (${taskDetailEntity?.assets?.size ?: 0})",
            "Assets ($totalApprovedQty)",
            "Track",
            "Files (${taskDetailEntity?.documents?.size ?: 0})"
        )

        val subtitle = buildString {
            if (!taskDetailEntity?.requestId.isNullOrEmpty()) append(taskDetailEntity?.requestId)
            if (!taskDetailEntity?.fromLocation?.siteId.isNullOrEmpty()) {
                if (isNotEmpty()) append(" | ") // Add a pipe if there's already something before
                append(taskDetailEntity?.fromLocation?.siteId)
            }
            if (!taskDetailEntity?.toLocation?.siteId.isNullOrEmpty()) {
                if (isNotEmpty()) append(" | ")
                append(taskDetailEntity?.toLocation?.siteId)
            }
        }

        Scaffold(
            topBar = {
                TopBar(
                    onBackClicked = {
                        dismiss()
                    },
                    onSearchClicked = {
                        CustomToastMsg.showCustomToast(requireContext(), "Coming Soon")
                    },
                    onAddClicked = {
                        //dismiss()
                        val bundle = Bundle()
                        bundle.putString("requestId",requestId)
                        val fragment = UpdateScreen()
                        fragment.arguments = bundle
                        val transaction = childFragmentManager.beginTransaction()
                        transaction.add(fragment, "UpdateScreen")
                        transaction.commitAllowingStateLoss()
                    },
                    "Movement Details",
                    subtitle,
                    "",
                    mode = if (mode == "Details") 2 else 3
                )
            },

            /*bottomBar = {
                BottomBar(
                    onCancelClicked = {
                        dismiss()
                    },
                    onUpdateClicked = {
                        //dismiss()
                        val bundle = Bundle()
                        bundle.putString("requestId",requestId)
                        val fragment = UpdateScreen()
                        fragment.arguments = bundle
                        val transaction = childFragmentManager.beginTransaction()
                        transaction.add(fragment, "UpdateScreen")
                        transaction.commitAllowingStateLoss()
                    },
                    "Back",
                    "Update"
                )
            },*/
            content = {
                Column {
                    DetailsTab(
                        tabItems = tabItems,
                        selectedTabIndex = mutableStateOf(selectedTabIndex), // Pass the state directly
                        onTabSelected = { index ->
                            selectedTabIndex = index // Update the selected index properly
                        }
                    )

                    when (selectedTabIndex) {
                        0 -> allDetails(taskDetailEntity)
                        1 -> taskDetailEntity?.let {assets(it.assets)}
                        2 -> taskDetailEntity?.let {track(it.timelines)}
                        3 -> taskDetailEntity?.let {files(it.documents)}
                    }
                }
            }
        )
    }


    @Composable
    fun allDetails(taskDetail: TaskDetailEntity?) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                   // .padding(paddingValues) // Apply scaffold padding to prevent overlap
            ) {
                RequestDetails(taskDetail,1,requireContext())
            }
    }

    @Composable
    fun assets(items: List<Assets>) {
        if (items.isNotEmpty()) {
            LazyColumn(
                contentPadding = PaddingValues(bottom = 80.dp) // Add bottom padding
            ) {
                items(items) { item ->
                    AssetCardView(item, onClick = {})
                }
            }
        }
    }

    @Composable
    fun track(items: List<Timeline>) {
        TimelineScreen(timelines = items)
    }
}
   @Composable
   fun files(items: List<Documents>) {
       if (items.isNotEmpty()) {
           LazyColumn {
               items(items) { item ->
                   ImageCardView(
                       item,
                       mode = 1,
                       flag = 1,
                       onClick = { }
                   )
               }
        }
    }
}
