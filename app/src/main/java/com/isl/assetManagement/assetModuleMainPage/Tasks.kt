package com.isl.assetManagement.assetModuleMainPage

import CallbackSearch
import Search
import SnackbarUtils
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.isl.assetManagement.constants.DefaultLevel
import com.isl.assetManagement.dataViewModel.RoomViewModel
import com.isl.assetManagement.dataViewModel.SharedViewModel
import com.isl.assetManagement.jetpackcompose.AddUpdateTabs
import com.isl.assetManagement.jetpackcompose.LoadingDialog
import com.isl.assetManagement.jetpackcompose.RequestItemCardView
import com.isl.assetManagement.jetpackcompose.TopBar
import com.isl.assetManagement.requests.SearchTaskRequest
import com.isl.assetManagement.room.entity.AssetRequests
import com.isl.assetManagement.room.repository.RoomRepository
import com.isl.assetManagement.taskDetails.DetailsScreen
import com.isl.assetManagement.utils.CustomToastMsg
import com.isl.assetManagement.utils.CustomToastMsg.Companion.showCustomToast
import com.isl.assetManagement.utils.DataViewModelFactory
import com.isl.assetManagement.utils.Util
import com.isl.itower.MyApp
import infozech.itower.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class Tasks : Fragment {
    constructor() : super(R.layout.task_frag)

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var roomRepository: RoomRepository
    private lateinit var viewModel: RoomViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe when DialogFragment B is dismissed
        sharedViewModel.isUpdateTaskDetails.observe(viewLifecycleOwner, Observer { dismissed ->
            /*if(dismissed){
                init()
                //sharedViewModel.notifyUpdateTaskDetails(false)
            }*/
        })

        roomRepository = RoomRepository(MyApp.getAssetDatabase().dataDao())

        // Initialize ViewModel
        viewModel = ViewModelProvider(
            this,
            DataViewModelFactory(roomRepository)
        ).get(RoomViewModel::class.java)

        init()
    }


    fun init() {
        lifecycleScope.launch(Dispatchers.Main) {
            //viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            val searchRequest = SearchTaskRequest(
                requestId = "",
                requestStatus = "",
                fromLocation = "",
                toLocation = "",
                fromDate = "",
                toDate = ""
            )
            taskSummary(searchRequest)
        }
    }


    private fun taskSummary(searchRequest: SearchTaskRequest) {

        SnackbarUtils.showLoading(requireActivity(), "Please wait...")
        var tabItems = mutableListOf<String>()
        // Use safe coroutine with lifecycleScope.launchWhenStarted
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            val token = roomRepository.fetchToken()
            //val token = "dasdasdas"
            if (token != null) {
                viewModel.fetchAndSaveTaskSummaryFromApi(
                    token = token,
                    requestId = searchRequest.requestId,
                    requestStatus = searchRequest.requestStatus,
                    fromLocation = searchRequest.fromLocation,
                    toLocation = searchRequest.toLocation,
                    fromDate = searchRequest.fromDate,
                    toDate = searchRequest.toDate,
                    onDataInserted = { result ->
                        if (result == 0) {
                            showCustomToast(
                                requireActivity(),
                                "Unable to fetch summary data from the server. Loading data from the local database."
                            )
                        }
                        viewModel.getTaskSummary()
                        viewModel.getTaskSummary.observe(viewLifecycleOwner, Observer { entity ->

                            if (entity != null) {
                                tabItems = mutableListOf(
                                    "Assigned (${entity.assigned})",
                                    "Raised (${entity.raised})",
                                    "Rejected (${entity.rejected})",
                                    "Closed (${entity.completed})"
                                )
                            } else {
                                tabItems = mutableListOf(
                                    "Assigned(0)",
                                    "Raised(0)",
                                    "Rejected(0)",
                                    "Closed(0)"
                                )
                            }

                            // Set the total task count safely
                            var total: String = "Total : 0"
                            if (entity != null) {
                                total = "Total : " + ((entity.assigned ?: 0) +
                                        (entity.raised ?: 0) +
                                        (entity.rejected ?: 0) +
                                        (entity.completed ?: 0)).toString()
                            }

                            view?.findViewById<ComposeView>(R.id.compose_view)?.setContent {
                                UIScreen(viewModel, tabItems, total, searchRequest)
                            }
                            //taskGrid(tabItems,"Assigned",0)
                        })
                    }
                )
            } else {
                showCustomToast(requireActivity(), "Token authentication failed. Try again.")
                activity?.finish()
            }
        }
        // Hide progress bar after the task is completed
        //hideProgressBar()
        SnackbarUtils.hideLoading()
    }


    @SuppressLint("NotConstructor")
    @Composable
    fun UIScreen(
        viewModel: RoomViewModel,
        tabItems: List<String>,
        countSummary: String,
        searchRequest: SearchTaskRequest
    ) {
        var selectedTabIndex by remember { mutableStateOf(0) }
        var filterCount by remember { mutableStateOf("") } // Initial filter count

        Scaffold(
            topBar = {
                TopBar(
                    onBackClicked = {
                        activity?.finish()
                    },
                    onSearchClicked = {
                        val bottomSheet = Search(object : CallbackSearch {
                            override fun invoke(searchTaskRequest: SearchTaskRequest, count: Int) {
                                taskSummary(searchTaskRequest)
                                filterCount = "%02d".format(count)
                            }
                        })
                        bottomSheet.show(childFragmentManager, "myBottomSheet")
                    },
                    onAddClicked = {
                        CustomToastMsg.showCustomToast(requireContext(), "Coming Soon")
                    },
                    "Movement Requests",
                    countSummary,
                    "Request +",
                    1,
                    filterCount = filterCount // Use the dynamic filter count
                )
            },
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(colorResource(id = R.color.color_background))
                        .padding(bottom = 60.dp)
                ) {
                    AddUpdateTabs(
                        tabItems = tabItems,
                        selectedTabIndex = mutableStateOf(selectedTabIndex), // Pass the state directly
                        onTabSelected = { index ->
                            selectedTabIndex = index // Update the selected index properly
                        }
                    )

                    when (selectedTabIndex) {
                        0 -> myData(viewModel, "Assigned", searchRequest = searchRequest)
                        1 -> myData(viewModel, "Raised", searchRequest = searchRequest)
                        2 -> myData(viewModel, "Rejected", searchRequest = searchRequest)
                        3 -> myData(viewModel, "Completed", searchRequest = searchRequest)
                    }
                }
            }
        )
    }


    @Composable
    fun myData(viewModel: RoomViewModel, key: String, searchRequest: SearchTaskRequest) {
        //showProgressBar()
        //SnackbarUtils.showLoading(requireActivity(),"Please wait...")
        var isLoading by remember { mutableStateOf(false) }
        // Use a state variable to track the results
        var results by remember { mutableStateOf(10) }
        val assetRequests by viewModel.iAssetRequest.observeAsState(emptyList())

        // Side-effect for fetching data
        LaunchedEffect(Unit) {
            isLoading = true  // Show loading before API call
            val token = roomRepository.fetchToken()
            if (token != null) {
                viewModel.fetchAndSaveAssetRequestFromApi(
                    token,
                    key,
                    requestId = searchRequest.requestId,
                    requestStatus = searchRequest.requestStatus,
                    fromLocation = searchRequest.fromLocation,
                    toLocation = searchRequest.toLocation,
                    fromDate = searchRequest.fromDate,
                    toDate = searchRequest.toDate,
                    onDataInserted = { result ->
                        results = result // Update the state variable
                        if (result == 0) {
                            activity?.let {
                                showCustomToast(
                                    it,
                                    "Unable to fetch movement request data from the server. Loading data from the local database."
                                )
                            }
                        }
                    }
                )
            } else {
                isLoading = false
                showCustomToast(requireActivity(), "Token authentication failed. Try again.")
            }
        }

        // Reactively display the grid when results change
        if (results == 0 || results == 1) {
            viewModel.getiAssetRequest(key)
            Grid(assetRequests)
            //hideProgressBar()
            //SnackbarUtils.hideLoading()
            isLoading = false
        }

        // Show the LoadingDialog when `isLoading` is true
        if (isLoading) {
            LoadingDialog { isLoading = false }
        }
    }


    @Composable
    fun Grid(items: List<AssetRequests>) {
        var selectedRequestId by remember { mutableStateOf<String?>(null) }
        var isLoading by remember { mutableStateOf(false) }

        // If requestId is selected, call taskDetails() in LaunchedEffect
        LaunchedEffect(selectedRequestId) {
            selectedRequestId?.let { requestId ->
                isLoading = true
                taskDetails(requestId)  // Call task details logic
                isLoading = false
            }
        }
        // Check if items list is not empty before rendering LazyColumn
        var result: HashMap<String, String> = hashMapOf()
        result = Util.stringToHashMap("" + DefaultLevel.msg()["assetStatus"])
        if (items.isNotEmpty()) {
            LazyColumn {
                items(items) { item ->
                    RequestItemCardView(
                        item,
                        onClick = {
                            //if(item.requestStatus == "Pending_for_Sender"
                            //    || item.requestStatus == "Pending_for_Receiver"){
                            selectedRequestId = item.requestId  // Trigger LaunchedEffect
                            //taskDetails(item.requestId)
                            // }/*else{
                            /*val bundle = Bundle()
                            bundle.putString("requestId",item.requestId)
                            bundle.putString("mode","AllDetails")
                            val fragment = AllDetailsScreen()
                            fragment.arguments = bundle
                            val transaction = childFragmentManager.beginTransaction()
                            transaction.add(fragment, "AddDetailsScreen")
                            transaction.commitAllowingStateLoss()*/

                            // }*/

                        }, result
                    )
                }
            }
            // Show the LoadingDialog when `isLoading` is true
            if (isLoading) {
                LoadingDialog { isLoading = false }
            }
        }
    }

    private fun taskDetails(requestId: String) {
        if (isAdded && view != null) {
            lifecycleScope.launch(Dispatchers.Main) {
                //lifecycleScope.launchWhenStarted{
                val token = roomRepository.fetchToken()
                //val token = "dasdasdas"
                if (token != null) {
                    requestId?.let {
                        viewModel.fetchAndSaveTaskDetailFromApi(
                            token = token,
                            requestId = it,
                            onDataInserted = { result ->
                                if (result == 0) {
                                    CustomToastMsg.showCustomToast(
                                        requireActivity(),
                                        "Unable to fetch summary data from the server. Loading data from the local database."
                                    )
                                }

                                val bundle = Bundle()
                                bundle.putString("requestId", requestId)
                                val fragment = DetailsScreen()
                                fragment.arguments = bundle
                                val transaction = childFragmentManager.beginTransaction()
                                transaction.add(fragment, "DetailsScreen")
                                transaction.commitAllowingStateLoss()
                            }
                        )
                    }
                } else {
                    CustomToastMsg.showCustomToast(
                        requireActivity(),
                        "Token authentication failed. Try again."
                    )
                }
            }
        }

        //SnackbarUtils.hideLoading()
    }

}









