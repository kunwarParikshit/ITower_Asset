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
import androidx.compose.ui.unit.sp
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.isl.assetManagement.dataViewModel.RoomViewModel
import com.isl.assetManagement.jetpackcompose.AddUpdateTabs
import com.isl.assetManagement.jetpackcompose.BottomBar
import com.isl.assetManagement.jetpackcompose.RequestItemCardView
import com.isl.assetManagement.jetpackcompose.TopBar
import com.isl.assetManagement.room.entity.AssetRequests
import com.isl.assetManagement.room.repository.RoomRepository
import com.isl.assetManagement.utils.CustomToastMsg
import com.isl.assetManagement.utils.DataViewModelFactory
import com.isl.itower.MyApp
import infozech.itower.R

class AddScreen : DialogFragment() {
    private lateinit var dataRepository: RoomRepository
    private lateinit var viewModel: RoomViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize ViewModel
        dataRepository = RoomRepository(MyApp.getAssetDatabase().dataDao())
        viewModel = ViewModelProvider(
            this,
            DataViewModelFactory(dataRepository)
        ).get(RoomViewModel::class.java)
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
        val tabItems = listOf("Assets", "Documents", "Details")
        var selectedTabIndex by remember { mutableStateOf(0) }

        Scaffold(
            topBar = {
                TopBar(
                    onBackClicked = {
                        dismiss()
                    },
                    onSearchClicked = {
                        CustomToastMsg.showCustomToast(requireContext(), "Coming Soon")
                    },
                    "Movement Requests",
                    "Total Count",
                    1
                )
            },

            bottomBar = {
                BottomBar(
                    onCancelClicked = {
                        dismiss()
                    },
                    onUpdateClicked = {
                        // Handle submit action here
                    },
                    "Cancel",
                    "Submit"
                )
            },
            content = {
                Column {
                    AddUpdateTabs(
                        tabItems = tabItems,
                        selectedTabIndex = mutableStateOf(selectedTabIndex), // Pass the state directly
                        onTabSelected = { index ->
                            selectedTabIndex = index // Update the selected index properly
                        }
                    )

                    when (selectedTabIndex) {
                        0 -> addDocument()
                        1 -> addAsset()
                        2 -> addDetails()
                    }
                }
            }
        )
    }


    @Composable
    fun addDocument() {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            Text("Assets Content", fontSize = 20.sp)
        }
    }

    @Composable
    fun addAsset() {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Cyan),
            contentAlignment = Alignment.Center
        ) {
            val someKey = "Assigned"
            viewModel.getiAssetRequest(someKey)
            val assetRequests = viewModel.iAssetRequest.observeAsState(emptyList())

            DataItem(assetRequests.value)
            //Text("Documents Content", fontSize = 20.sp)
        }
    }

    @Composable
    fun addDetails() {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Yellow),
            contentAlignment = Alignment.Center
        ) {
            Text("Details Content", fontSize = 20.sp)
        }
    }
}

   @Composable
   fun DataItem(items: List<AssetRequests>) {
    var result: HashMap<String, String> = hashMapOf()
    // Check if items list is not empty before rendering LazyColumn
    if (items.isNotEmpty()) {
        LazyColumn {
            items(items) { item ->
                RequestItemCardView(item,
                    onClick = {

                    }, result)
            }
        }
    }
}
