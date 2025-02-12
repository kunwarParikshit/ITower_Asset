package com.isl.assetManagement.taskDetails
import UpdateScreen
import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

import com.isl.assetManagement.constants.DefaultLevel
import com.isl.assetManagement.dataViewModel.RoomViewModel
import com.isl.assetManagement.dataViewModel.SharedViewModel
import com.isl.assetManagement.jetpackcompose.BottomBar
import com.isl.assetManagement.jetpackcompose.DialogFragmentTopBar
import com.isl.assetManagement.room.repository.RoomRepository
import com.isl.assetManagement.utils.DataViewModelFactory
import com.isl.assetManagement.utils.Util
import com.isl.itower.MyApp
import infozech.itower.R
import java.util.concurrent.Executors

class DetailsScreen : BottomSheetDialogFragment() {

    private lateinit var roomRepository: RoomRepository
    private lateinit var viewModel: RoomViewModel
    private var requestId: String? = null
    // Get the shared ViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        roomRepository = RoomRepository(MyApp.getAssetDatabase().dataDao())
        // Initialize ViewModel
        viewModel = ViewModelProvider(
            this,
            DataViewModelFactory(roomRepository)
        ).get(RoomViewModel::class.java)

        // Retrieve requestId from arguments
        arguments?.let {
            requestId = it.getString("requestId") // Get the requestId
        }

        val dialog = BottomSheetDialog(requireContext(), theme)
        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            val bottomSheet =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)

            bottomSheet?.apply {
                setBackgroundColor(android.graphics.Color.TRANSPARENT) // Set transparent background

                val behavior = BottomSheetBehavior.from(this)

                // Set the height to 70% of the screen size
                val seventyPercentHeight = (resources.displayMetrics.heightPixels * 0.85).toInt()
                behavior.peekHeight = seventyPercentHeight
                behavior.isFitToContents = false
                behavior.state = BottomSheetBehavior.STATE_COLLAPSED
                behavior.isDraggable = false
                // Set the maximum height to 70% screen height
                layoutParams.height = seventyPercentHeight
                layoutParams = layoutParams

            }
        }
       return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
          return ComposeView(requireContext()).apply {

            setContent {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(
                            RoundedCornerShape(
                                topStart = 16.dp, topEnd = 16.dp,
                                bottomStart = 0.dp, bottomEnd = 0.dp
                            )
                        )
                        .background(Color.White) // Apply a background color
                ) {
                    DetailsScreen(viewModel,0,
                        onUpdate = {},allDetails ={ dismiss()}

                    )
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    /*private fun taskDetails(view: View) {
        //showProgressBar()
        if (isAdded && view != null) {
           lifecycleScope.launchWhenStarted{
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
                                requestId?.let { viewModel.getTaskDetail(it) }
                                viewModel.getTaskDetail.observe(viewLifecycleOwner, Observer { entity ->
                                    if (view != null && entity != null) {
                                        //var requestId = entity.requestId
                                            view?.findViewById<ComposeView>(R.id.compose_view)?.setContent {

                                            DetailsScreen(viewModel,1,
                                                onUpdate = {
                                                },
                                                allDetails =
                                                {
                                                    dismiss()
                                                }

                                            )
                                        }
                                    }
                                })
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
            // Hide progress bar after the task is completed
            //hideProgressBar()
        }
    }*/


    @SuppressLint("UnusedMaterialScaffoldPaddingParameter", "NotConstructor")
    @Composable
    fun DetailsScreen(viewModel11 : RoomViewModel,mode : Int,
        onUpdate: () -> Unit,
        allDetails: () -> Unit
    ) {
        requestId?.let { viewModel.getTaskDetail(it) }
        val taskDetail by viewModel.getTaskDetail.observeAsState()
        var map: HashMap<String, String> = hashMapOf()

        map = Util.stringToHashMap("" + DefaultLevel.msg()["assetStatus"])
        Scaffold(
            topBar = {
                DialogFragmentTopBar(
                    onGrips = {
                        dismiss()
                    },
                    closed = {
                        dismiss()
                    },
                    "Movement Details"
                )
            },

            bottomBar = {
                BottomBar(
                    onCancelClicked = {
                        dismiss()
                    },
                    onUpdateClicked = {
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
            },

            content = { paddingValues -> // Respect bottom bar height
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues) // Apply scaffold padding to prevent overlap
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = 20.dp, end = 20.dp, top = 33.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Scrollable content here
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth() // Ensures the Row takes up the full width
                        ) {

                            taskDetail?.requestId?.let {
                                Text(
                                    text = it?: "",
                                    style = TextStyle(
                                        fontSize = 12.sp,
                                        color = colorResource(id = R.color.id_color)
                                    ),
                                    modifier = Modifier.weight(1f),
                                    maxLines = 1, // Limits the text to a single line
                                    overflow = TextOverflow.Ellipsis, // Adds the ellipsis when the text overflows
                                )
                            }

                            map[taskDetail?.status]?.let {
                                Text(
                                    //text = item.requestStatus,
                                    text = it?: "",
                                    style = TextStyle(
                                        fontSize = 11.sp,
                                        color = colorResource(id = R.color.status_color)
                                    ),
                                    modifier = Modifier
                                        .background(
                                            color = colorResource(id = R.color.status_bg_color),
                                            shape = RoundedCornerShape(8.dp) // Adjust the corner radius as needed
                                        )
                                        .padding(5.dp) // Optional, adds padding around the text
                                )
                            }

                            /*taskDetail?.status?.let {
                                map[taskDetail!!.status]?.let
                                Text(
                                    //text = item.requestStatus,
                                    text = it,  //"Waiting Approval 2",
                                    style = TextStyle(
                                        fontSize = 11.sp,
                                        color = colorResource(id = R.color.status_color)
                                    ),
                                    modifier = Modifier
                                        .background(
                                            color = colorResource(id = R.color.status_bg_color),
                                            shape = RoundedCornerShape(8.dp) // Adjust the corner radius as needed
                                        )
                                        .padding(5.dp) // Optional, adds padding around the text
                                )
                            }*/
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start,
                            modifier = Modifier.fillMaxWidth()
                                .padding(top = 0.dp),

                        ) {
                            taskDetail?.fromLocation?.let {
                                androidx.compose.material3.Text(
                                    text = it.siteId?: "",
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colorResource(id = R.color.from_to_color)
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.width(0.dp)) // Optional space between text and image
                            androidx.compose.material3.Icon(
                                imageVector = Icons.Filled.ArrowForward, // Default back arrow icon
                                contentDescription = "Back",
                                tint = colorResource(id = R.color.from_to_color) // Color for the arrow icon
                            )
                            Spacer(modifier = Modifier.width(0.dp)) // Optional space between icon and text
                            taskDetail?.toLocation?.let {
                                androidx.compose.material3.Text(
                                    text = it.siteId ?: "",
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colorResource(id = R.color.from_to_color)
                                    )
                                )
                            }

                            // Spacer to create some space between the last Text and the "Assets" text
                            /*Spacer(modifier = Modifier.weight(1f)) // This will push the "Assets" text to the far right

                            androidx.compose.material3.Text(
                                text = "Requested On",
                                style = TextStyle(
                                    fontSize = 11.sp,
                                    color = colorResource(id = R.color.count_grid_color)
                                )
                            )*/

                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start // Aligning everything to the left
                        ) {
                            taskDetail?.reasonCategory?.let {
                                androidx.compose.material3.Text(
                                    text = it?: "",
                                    style = TextStyle(
                                        fontSize = 12.sp,
                                        color = colorResource(id = R.color.category_color)
                                    ),
                                    modifier = Modifier.weight(1f),
                                    maxLines = 1, // Limits the text to a single line
                                    overflow = TextOverflow.Ellipsis, // Adds the ellipsis when the text overflows
                                )
                            }
                            taskDetail?.requestDate?.let {
                                androidx.compose.material3.Text(
                                    text = it?: "",
                                    style = TextStyle(
                                        fontSize = 11.sp,
                                        color = colorResource(id = R.color.count_grid_color)
                                    )
                                )
                            }
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start // Aligning everything to the left
                        ) {
                            taskDetail?.reasonSubCategory?.let {
                                androidx.compose.material3.Text(
                                    text = it?: "",
                                    style = TextStyle(
                                        fontSize = 12.sp,
                                        color = colorResource(id = R.color.category_color)
                                    ),
                                    modifier = Modifier.weight(1f),
                                    maxLines = 1, // Limits the text to a single line
                                    overflow = TextOverflow.Ellipsis, // Adds the ellipsis when the text overflows
                                )
                            }
                            /*androidx.compose.material3.Text(
                                text = "Closed On",
                                style = TextStyle(
                                    fontSize = 11.sp,
                                    color = colorResource(id = R.color.count_grid_color)
                                )
                            )*/
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Divider(
                            color = colorResource(id = R.color.input_box_border),
                            thickness = 1.dp,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        /*Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start // Aligning everything to the left
                        ) {
                            androidx.compose.material3.Text(
                                text = "Asset Stock",
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    color = colorResource(id = R.color.category_color)
                                ),
                                modifier = Modifier.weight(1f),
                                maxLines = 1, // Limits the text to a single line
                                overflow = TextOverflow.Ellipsis, // Adds the ellipsis when the text overflows
                            )
                            androidx.compose.material3.Text(
                                text = "01/02",
                                style = TextStyle(
                                    fontSize = 11.sp,
                                    color = colorResource(id = R.color.count_grid_color)
                                )
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start // Aligning everything to the left
                        ) {
                            androidx.compose.material3.Text(
                                text = "Vendor Name",
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    color = colorResource(id = R.color.category_color)
                                ),
                                modifier = Modifier.weight(1f),
                                maxLines = 1, // Limits the text to a single line
                                overflow = TextOverflow.Ellipsis, // Adds the ellipsis when the text overflows
                            )
                            androidx.compose.material3.Text(
                                text = "Ericsson & Co.",
                                style = TextStyle(
                                    fontSize = 11.sp,
                                    color = colorResource(id = R.color.count_grid_color)
                                )
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start // Aligning everything to the left
                        ) {
                            androidx.compose.material3.Text(
                                text = "Product Name",
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    color = colorResource(id = R.color.category_color)
                                ),
                                modifier = Modifier.weight(1f),
                                maxLines = 1, // Limits the text to a single line
                                overflow = TextOverflow.Ellipsis, // Adds the ellipsis when the text overflows
                            )
                            androidx.compose.material3.Text(
                                text = "-",
                                style = TextStyle(
                                    fontSize = 11.sp,
                                    color = colorResource(id = R.color.count_grid_color)
                                )
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start // Aligning everything to the left
                        ) {
                            androidx.compose.material3.Text(
                                text = "Project Name",
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    color = colorResource(id = R.color.category_color)
                                ),
                                modifier = Modifier.weight(1f),
                                maxLines = 1, // Limits the text to a single line
                                overflow = TextOverflow.Ellipsis, // Adds the ellipsis when the text overflows
                            )
                            androidx.compose.material3.Text(
                                text = "-",
                                style = TextStyle(
                                    fontSize = 11.sp,
                                    color = colorResource(id = R.color.count_grid_color)
                                )
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start // Aligning everything to the left
                        ) {
                            androidx.compose.material3.Text(
                                text = "SO Numeber",
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    color = colorResource(id = R.color.category_color)
                                ),
                                modifier = Modifier.weight(1f),
                                maxLines = 1, // Limits the text to a single line
                                overflow = TextOverflow.Ellipsis, // Adds the ellipsis when the text overflows
                            )
                            androidx.compose.material3.Text(
                                text = "SO987654",
                                style = TextStyle(
                                    fontSize = 11.sp,
                                    color = colorResource(id = R.color.count_grid_color)
                                )
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start // Aligning everything to the left
                        ) {
                            androidx.compose.material3.Text(
                                text = "Activity Id",
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    color = colorResource(id = R.color.category_color)
                                ),
                                modifier = Modifier.weight(1f),
                                maxLines = 1, // Limits the text to a single line
                                overflow = TextOverflow.Ellipsis, // Adds the ellipsis when the text overflows
                            )
                            androidx.compose.material3.Text(
                                text = "W987654",
                                style = TextStyle(
                                    fontSize = 11.sp,
                                    color = colorResource(id = R.color.count_grid_color)
                                )
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start // Aligning everything to the left
                        ) {
                            androidx.compose.material3.Text(
                                text = "Ticket ID",
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    color = colorResource(id = R.color.category_color)
                                ),
                                modifier = Modifier.weight(1f),
                                maxLines = 1, // Limits the text to a single line
                                overflow = TextOverflow.Ellipsis, // Adds the ellipsis when the text overflows
                            )
                            androidx.compose.material3.Text(
                                text = "Y567890",
                                style = TextStyle(
                                    fontSize = 11.sp,
                                    color = colorResource(id = R.color.count_grid_color)
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Divider(
                            color = Color.Black,
                            thickness = 1.dp,
                            modifier = Modifier.fillMaxWidth()
                        )*/
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start // Aligning everything to the left
                        ) {
                            androidx.compose.material3.Text(
                                text = "From Site Id",
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    color = colorResource(id = R.color.category_color)
                                ),
                                modifier = Modifier.weight(1f),
                                maxLines = 1, // Limits the text to a single line
                                overflow = TextOverflow.Ellipsis, // Adds the ellipsis when the text overflows
                            )
                            taskDetail?.fromLocation?.let {
                                androidx.compose.material3.Text(
                                    text = it.siteId?: "",
                                    style = TextStyle(
                                        fontSize = 11.sp,
                                        color = colorResource(id = R.color.count_grid_color)
                                    )
                                )
                            }
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start // Aligning everything to the left
                        ) {
                            androidx.compose.material3.Text(
                                text = "Site Address",
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    color = colorResource(id = R.color.category_color)
                                ),
                                modifier = Modifier.weight(1f),
                                // Limits the text to a single line
                                overflow = TextOverflow.Ellipsis, // Adds the ellipsis when the text overflows
                            )
                            taskDetail?.fromLocation?.let {
                                androidx.compose.material3.Text(
                                    text = it.address?: "",
                                    style = TextStyle(
                                        fontSize = 11.sp,
                                        color = colorResource(id = R.color.count_grid_color)
                                    ),
                                    modifier = Modifier.fillMaxWidth(0.4f), // Adjust width constraint to allow wrapping
                                    maxLines = Int.MAX_VALUE, // Allow unlimited lines
                                    overflow = TextOverflow.Clip // Let text flow naturally to next line
                                )
                            }
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start // Aligning everything to the left
                        ) {
                            androidx.compose.material3.Text(
                                text = "District",
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    color = colorResource(id = R.color.category_color)
                                ),
                                modifier = Modifier.weight(1f),
                                maxLines = 1, // Limits the text to a single line
                                overflow = TextOverflow.Ellipsis, // Adds the ellipsis when the text overflows
                            )
                            taskDetail?.fromLocation?.let {
                                androidx.compose.material3.Text(
                                    text = it.locationLevel3?: "",
                                    style = TextStyle(
                                        fontSize = 11.sp,
                                        color = colorResource(id = R.color.count_grid_color)
                                    )
                                )
                            }
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start // Aligning everything to the left
                        ) {
                            androidx.compose.material3.Text(
                                text = "City",
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    color = colorResource(id = R.color.category_color)
                                ),
                                modifier = Modifier.weight(1f),
                                maxLines = 1, // Limits the text to a single line
                                overflow = TextOverflow.Ellipsis, // Adds the ellipsis when the text overflows
                            )
                            taskDetail?.fromLocation?.let {
                                androidx.compose.material3.Text(
                                    text = it.locationLevel4?: "",
                                    style = TextStyle(
                                        fontSize = 11.sp,
                                        color = colorResource(id = R.color.count_grid_color)
                                    )
                                )
                            }
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start // Aligning everything to the left
                        ) {
                            androidx.compose.material3.Text(
                            text = "Lat/Long",
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    color = colorResource(id = R.color.category_color)
                                ),
                                modifier = Modifier.weight(1f),
                                maxLines = 1, // Limits the text to a single line
                                overflow = TextOverflow.Ellipsis, // Adds the ellipsis when the text overflows
                            )
                            androidx.compose.material3.Text(
                                // text = "21.56789/34.67890",
                                text = ""+taskDetail?.fromLocation?.latitude+"/"+taskDetail?.fromLocation?.latitude,
                                style = TextStyle(
                                    fontSize = 11.sp,
                                    color = colorResource(id = R.color.count_grid_color)
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Divider(
                            color = colorResource(id = R.color.input_box_border),
                            thickness = 1.dp,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start // Aligning everything to the left
                        ) {
                            androidx.compose.material3.Text(
                                text = "To Site Id",
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    color = colorResource(id = R.color.category_color)
                                ),
                                modifier = Modifier.weight(1f),
                                maxLines = 1, // Limits the text to a single line
                                overflow = TextOverflow.Ellipsis, // Adds the ellipsis when the text overflows
                            )
                            taskDetail?.toLocation?.let {
                                androidx.compose.material3.Text(
                                    text = it.siteId?: "",
                                    style = TextStyle(
                                        fontSize = 11.sp,
                                        color = colorResource(id = R.color.count_grid_color)
                                    )
                                )
                            }
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start // Aligning everything to the left
                        ) {
                            androidx.compose.material3.Text(
                                text = "Site Address",
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    color = colorResource(id = R.color.category_color)
                                ),
                                modifier = Modifier.weight(1f),
                                // Limits the text to a single line
                                overflow = TextOverflow.Ellipsis, // Adds the ellipsis when the text overflows
                            )
                            taskDetail?.toLocation?.let {
                                androidx.compose.material3.Text(
                                    text = it.address?: "",
                                    style = TextStyle(
                                        fontSize = 11.sp,
                                        color = colorResource(id = R.color.count_grid_color)
                                    ),
                                    modifier = Modifier.fillMaxWidth(0.4f), // Adjust width constraint to allow wrapping
                                    maxLines = Int.MAX_VALUE, // Allow unlimited lines
                                    overflow = TextOverflow.Clip // Let text flow naturally to next line
                                )
                            }
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start // Aligning everything to the left
                        ) {
                            androidx.compose.material3.Text(
                                text = "District",
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    color = colorResource(id = R.color.category_color)
                                ),
                                modifier = Modifier.weight(1f),
                                maxLines = 1, // Limits the text to a single line
                                overflow = TextOverflow.Ellipsis, // Adds the ellipsis when the text overflows
                            )
                            taskDetail?.toLocation?.let {
                                androidx.compose.material3.Text(
                                    text = it.locationLevel3?: "",
                                    style = TextStyle(
                                        fontSize = 11.sp,
                                        color = colorResource(id = R.color.count_grid_color)
                                    )
                                )
                            }
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start // Aligning everything to the left
                        ) {
                            androidx.compose.material3.Text(
                                text = "City",
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    color = colorResource(id = R.color.category_color)
                                ),
                                modifier = Modifier.weight(1f),
                                maxLines = 1, // Limits the text to a single line
                                overflow = TextOverflow.Ellipsis, // Adds the ellipsis when the text overflows
                            )
                            taskDetail?.toLocation?.let {
                                androidx.compose.material3.Text(
                                    text = it.locationLevel4?: "",
                                    style = TextStyle(
                                        fontSize = 11.sp,
                                        color = colorResource(id = R.color.count_grid_color)
                                    )
                                )
                            }
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start // Aligning everything to the left
                        ) {
                            androidx.compose.material3.Text(
                                text = "Lat/Long",
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    color = colorResource(id = R.color.category_color)
                                ),
                                modifier = Modifier.weight(1f),
                                maxLines = 1, // Limits the text to a single line
                                overflow = TextOverflow.Ellipsis, // Adds the ellipsis when the text overflows
                            )
                            androidx.compose.material3.Text(
                                // text = "21.56789/34.67890",
                                text = ""+taskDetail?.toLocation?.latitude+"/"+taskDetail?.toLocation?.latitude,
                                style = TextStyle(
                                    fontSize = 11.sp,
                                    color = colorResource(id = R.color.count_grid_color)
                                )
                            )
                        }
                       /* Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween, // Keep distance and 23km aligned properly
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            androidx.compose.material3.Text(
                                text = "Distance",
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    color = colorResource(id = R.color.category_color)
                                ),
                                modifier = Modifier.weight(1f),
                                maxLines = 1, // Limits the text to a single line
                                overflow = TextOverflow.Ellipsis, // Adds the ellipsis when the text overflows
                            )
                            Column(
                                horizontalAlignment = Alignment.End // Align text to the right side within the column
                            ) {
                                androidx.compose.material3.Text(
                                    text = "23km",
                                    style = TextStyle(
                                        fontSize = 11.sp,
                                        color = colorResource(id = R.color.count_grid_color)
                                    )
                                )
                                ClickableText(
                                    text = AnnotatedString(
                                        text = "Get Directions>",
                                        spanStyles = listOf(
                                            AnnotatedString.Range(
                                                item = SpanStyle(
                                                    fontSize = 11.sp,
                                                    color = Color.Red,
                                                    textDecoration = TextDecoration.Underline
                                                ),
                                                start = 0,
                                                end = "Get Directions>".length
                                            )
                                        )
                                    ),
                                    modifier = Modifier.padding(top = 2.dp),
                                    onClick = {
                                        // Add logic to handle the click event
                                        // Example: navigate to a maps intent
                                    }
                                )
                            }
                        }*/
                       /* Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start // Aligning everything to the left
                        ) {
                            androidx.compose.material3.Text(
                                text = "Site Access & Compliance",
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    color = colorResource(id = R.color.category_color)
                                ),
                                modifier = Modifier.weight(1f),
                                maxLines = 1, // Limits the text to a single line
                                overflow = TextOverflow.Ellipsis, // Adds the ellipsis when the text overflows
                            )
                            Column(
                                horizontalAlignment = Alignment.End // Align text to the right side within the column
                            ) {

                                Text(
                                    text = "Pending",
                                    style = TextStyle(
                                        fontSize = 11.sp,
                                        color = colorResource(id = R.color.status_color)
                                    ),
                                    modifier = Modifier
                                        .background(
                                            color = colorResource(id = R.color.status_bg_color),
                                            shape = RoundedCornerShape(8.dp) // Adjust the corner radius as needed
                                        )
                                        .padding(5.dp) // Optional, adds padding around the text
                                )
                                ClickableText(
                                    text = AnnotatedString(
                                        text = "Get Directions>",
                                        spanStyles = listOf(
                                            AnnotatedString.Range(
                                                item = SpanStyle(
                                                    fontSize = 11.sp,
                                                    color = Color.Red,
                                                    textDecoration = TextDecoration.Underline
                                                ),
                                                start = 0,
                                                end = "Get Directions>".length
                                            )
                                        )
                                    ),
                                    modifier = Modifier.padding(top = 2.dp),
                                    onClick = {
                                        // Add logic to handle the click event
                                        // Example: navigate to a maps intent
                                    }
                                )
                            }
                        }*/
                        Spacer(modifier = Modifier.height(8.dp))
                        Divider(
                            color = colorResource(id = R.color.input_box_border),
                            thickness = 1.dp,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(40.dp))
                        /*Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start // Aligning everything to the left
                        ) {
                            androidx.compose.material3.Text(
                                text = "Approver 1",
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    color = colorResource(id = R.color.category_color)
                                ),
                                modifier = Modifier.weight(1f),
                                maxLines = 1, // Limits the text to a single line
                                overflow = TextOverflow.Ellipsis, // Adds the ellipsis when the text overflows
                            )
                            androidx.compose.material3.Text(
                                text = "abc@gmail.com",
                                style = TextStyle(
                                    fontSize = 11.sp,
                                    color = colorResource(id = R.color.count_grid_color)
                                )
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start // Aligning everything to the left
                        ) {
                            androidx.compose.material3.Text(
                                text = "Vendor Name",
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    color = colorResource(id = R.color.category_color)
                                ),
                                modifier = Modifier.weight(1f),
                                maxLines = 1, // Limits the text to a single line
                                overflow = TextOverflow.Ellipsis, // Adds the ellipsis when the text overflows
                            )
                            androidx.compose.material3.Text(
                                text = "Ericsson & Co.",
                                style = TextStyle(
                                    fontSize = 11.sp,
                                    color = colorResource(id = R.color.count_grid_color)
                                )
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start // Aligning everything to the left
                        ) {
                            androidx.compose.material3.Text(
                                text = "Product Name",
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    color = colorResource(id = R.color.category_color)
                                ),
                                modifier = Modifier.weight(1f),
                                maxLines = 1, // Limits the text to a single line
                                overflow = TextOverflow.Ellipsis, // Adds the ellipsis when the text overflows
                            )
                            androidx.compose.material3.Text(
                                text = "-",
                                style = TextStyle(
                                    fontSize = 11.sp,
                                    color = colorResource(id = R.color.count_grid_color)
                                )
                            )
                        }*/
                    }
                }
            }
        )
    }



}