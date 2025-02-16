package com.isl.assetManagement.assetDetails
import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

import com.isl.assetManagement.dataViewModel.RoomViewModel
import com.isl.assetManagement.jetpackcompose.BottomBar
import com.isl.assetManagement.jetpackcompose.DialogFragmentTopBar
import com.isl.assetManagement.responses.AssetDetailsResponse
import com.isl.assetManagement.responses.Assets
import com.isl.assetManagement.room.entity.TaskDetailEntity
import com.isl.assetManagement.room.repository.RoomRepository
import com.isl.assetManagement.utils.CustomToastMsg
import com.isl.assetManagement.utils.DataViewModelFactory
import com.isl.itower.MyApp
import infozech.itower.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AssetDetails : BottomSheetDialogFragment() {
    private lateinit var roomRepository: RoomRepository
    private lateinit var viewModel: RoomViewModel
    private var requestId: String? = null
    private var formAssetDetails: List<AssetDetailsResponse>? = null
    private var tranAssetDetails: Assets? = null
    private lateinit var fromSid : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         tranAssetDetails = arguments?.getParcelable("tranAssetDetails")
         formAssetDetails = arguments?.getParcelableArrayList("formAssetDetails")
         fromSid = arguments?.getString("fromSid")?.let { it } ?: ""
        // Retrieve requestId from arguments
        arguments?.let {
            requestId = it.getString("requestId") // Get the requestId
        }

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        roomRepository = RoomRepository(MyApp.getAssetDatabase().dataDao())
        // Initialize ViewModel
        viewModel = ViewModelProvider(
            this,
            DataViewModelFactory(roomRepository)
        ).get(RoomViewModel::class.java)


        val dialog = BottomSheetDialog(requireContext(), theme)
        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            val bottomSheet =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)

            bottomSheet?.apply {
                setBackgroundColor(android.graphics.Color.TRANSPARENT) // Set transparent background
                val behavior = BottomSheetBehavior.from(this)
                // Set the height to 70% of the screen size
                val seventyPercentHeight = (resources.displayMetrics.heightPixels * 0.60).toInt()
                behavior.peekHeight = seventyPercentHeight
                behavior.isFitToContents = false
                behavior.state = BottomSheetBehavior.STATE_COLLAPSED
                behavior.isDraggable = false
                // Set the maximum height to 60% screen height
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
                    DetailsScreen(formAssetDetails)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter", "NotConstructor")
    @Composable
    fun DetailsScreen(formAssetDetails: List<AssetDetailsResponse>?) {

        requestId?.let { viewModel.getTaskDetail(it) }
        val taskDetailEntity by viewModel.getTaskDetail.observeAsState()


        Scaffold(
            topBar = {
                DialogFragmentTopBar(
                    onGrips = {
                        //dismiss()
                    },
                    closed = {
                        dismiss()
                    },
                    "Asset Details"
                )
            },

            bottomBar = {
                BottomBar(
                    onCancelClicked = {
                        dismiss()
                    },
                    onUpdateClicked = {
                        //if (validations()) {
                        if (validations(taskDetailEntity)) {
                            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                                formAssetDetails?.get(0)?.let { assetDetails ->
                                    val newAsset = assetDetails.assetType?.let {
                                        assetDetails.itemCode?.let { it1 ->
                                            tranAssetDetails?.let { it2 ->
                                                Assets(
                                                    assetType = it,
                                                    assetId = assetDetails.assetId,
                                                    itemCode = it1,
                                                    qrCode = assetDetails.qrCode,
                                                    availableQty = 0,
                                                    requestedQty = 0,
                                                    approvedQty = it2.approvedQty,
                                                    status = 1,
                                                    id = tranAssetDetails?.id
                                                    //id = tranAssetDetails?.id?.takeIf { it.isNotBlank() } ?: ""

                                                )
                                            }
                                        }
                                    }

                                    requestId?.let { requestId ->
                                         withContext(Dispatchers.Main) {
                                            if (newAsset != null) {
                                                viewModel.addUpdateAssetToExistingRequestDetails(requestId, newAsset) { success ->
                                                    if (success) {
                                                        // Only dismiss when update is successful
                                                        val fragmentManager = requireActivity().supportFragmentManager
                                                        val verifyAssetFragment = fragmentManager.findFragmentByTag(VerifyAsset::class.java.simpleName)
                                                        verifyAssetFragment?.let { (it as DialogFragment).dismiss() }
                                                         dismiss()
                                                    } else {
                                                         CustomToastMsg.showCustomToast(requireActivity(), "Update failed")
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    },

                    "Back",
                    "Move"
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
                            horizontalArrangement = Arrangement.Start // Aligning everything to the left
                        ) {
                            if (formAssetDetails != null && formAssetDetails.isNotEmpty()) {
                                formAssetDetails.get(0).assetType?.let {
                                    androidx.compose.material3.Text(
                                        text = it,
                                        style = TextStyle(
                                            fontSize = 13.sp,
                                            color = colorResource(id = R.color.category_color)
                                        ),
                                        modifier = Modifier.weight(1f),
                                        maxLines = 1, // Limits the text to a single line
                                        overflow = TextOverflow.Ellipsis, // Adds the ellipsis when the text overflows
                                    )
                                }
                            }

                            if (formAssetDetails != null && formAssetDetails.isNotEmpty()) {
                                formAssetDetails.get(0).qrCode?.let {
                                    androidx.compose.material3.Text(
                                        text = it,
                                        style = TextStyle(
                                            fontSize = 12.sp,
                                            color = colorResource(id = R.color.count_grid_color)
                                        )
                                    )
                                }
                            }

                           /* requestId?.let { viewModel.getTaskDetail(it) }
                            val taskDetailEntity by viewModel.getTaskDetail.observeAsState()
                            if (validations(taskDetailEntity)) {
                                Spacer(modifier = Modifier.width(0.dp)) // Optional space between text and image
                                androidx.compose.material3.Icon(
                                    painter = painterResource(id = R.drawable.right_icon), // Default back arrow icon
                                    contentDescription = "",
                                    modifier = Modifier.padding(start = 5.dp),
                                    tint = colorResource(id = R.color.qr_code_verify) // Color for the arrow icon
                                )
                            }*/
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start // Aligning everything to the left
                        ) {
                            if (formAssetDetails != null && formAssetDetails.isNotEmpty()) {
                                formAssetDetails.get(0).itemCode?.let {
                                    androidx.compose.material3.Text(
                                        text = it,
                                        style = TextStyle(
                                            fontSize = 12.sp,
                                            color = colorResource(id = R.color.category_color)
                                        ),
                                        modifier = Modifier.weight(1f),
                                        maxLines = 1, // Limits the text to a single line
                                        overflow = TextOverflow.Ellipsis, // Adds the ellipsis when the text overflows
                                    )
                                }
                            }

                            if (formAssetDetails != null && formAssetDetails.isNotEmpty()) {
                                formAssetDetails.get(0).assetId?.let {
                                    androidx.compose.material3.Text(
                                        text = it,
                                        style = TextStyle(
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = colorResource(id = R.color.count_grid_color)
                                        )
                                    )
                                }
                            }

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
                            if (formAssetDetails != null && formAssetDetails.isNotEmpty()) {
                                androidx.compose.material3.Text(
                                    text = "Asset Criteria",
                                    style = TextStyle(
                                        fontSize = 12.sp,
                                        color = colorResource(id = R.color.category_color)
                                    ),
                                    modifier = Modifier.weight(1f),
                                    maxLines = 1, // Limits the text to a single line
                                    overflow = TextOverflow.Ellipsis, // Adds the ellipsis when the text overflows
                                )
                            }

                            if (formAssetDetails != null && formAssetDetails.isNotEmpty()) {
                                formAssetDetails.get(0).assetCriteria?.let {
                                    androidx.compose.material3.Text(
                                        text = it,
                                        style = TextStyle(
                                            fontSize = 11.sp,
                                            color = colorResource(id = R.color.count_grid_color)
                                        )
                                    )
                                }
                            }

                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start // Aligning everything to the left
                        ) {
                            if (formAssetDetails != null && formAssetDetails.isNotEmpty()) {
                                androidx.compose.material3.Text(
                                    text = "Site Id",
                                    style = TextStyle(
                                        fontSize = 12.sp,
                                        color = colorResource(id = R.color.category_color)
                                    ),
                                    modifier = Modifier.weight(1f),
                                    maxLines = 1, // Limits the text to a single line
                                    overflow = TextOverflow.Ellipsis, // Adds the ellipsis when the text overflows
                                )
                            }

                            if (formAssetDetails != null && formAssetDetails.isNotEmpty()) {
                                formAssetDetails.get(0).siteId?.let {
                                    androidx.compose.material3.Text(
                                        text = it,
                                        style = TextStyle(
                                            fontSize = 11.sp,
                                            color = colorResource(id = R.color.count_grid_color)
                                        )
                                    )
                                }
                            }

                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start // Aligning everything to the left
                        ) {
                            androidx.compose.material3.Text(
                                text = "Site Name",
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    color = colorResource(id = R.color.category_color)
                                ),
                                modifier = Modifier.weight(1f),
                                maxLines = 1, // Limits the text to a single line
                                overflow = TextOverflow.Ellipsis, // Adds the ellipsis when the text overflows
                            )

                            if (formAssetDetails != null && formAssetDetails.isNotEmpty()) {
                                formAssetDetails.get(0).siteName?.let {
                                    androidx.compose.material3.Text(
                                        text = it,
                                        style = TextStyle(
                                            fontSize = 11.sp,
                                            color = colorResource(id = R.color.count_grid_color)
                                        )
                                    )
                                }
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
                                maxLines = 1, // Limits the text to a single line
                                overflow = TextOverflow.Ellipsis, // Adds the ellipsis when the text overflows
                            )

                            if (formAssetDetails != null && formAssetDetails.isNotEmpty()) {
                                formAssetDetails.get(0).siteAddress?.let {
                                    androidx.compose.material3.Text(
                                        text = it,
                                        style = TextStyle(
                                            fontSize = 11.sp,
                                            color = colorResource(id = R.color.count_grid_color)
                                        )
                                    )
                                }
                            }

                        }

                        //Spacer(modifier = Modifier.height(16.dp))
                        Spacer(modifier = Modifier.height(30.dp))

                    }
                }
            }
        )
    }

    fun validations(taskDetailEntity: TaskDetailEntity?): Boolean {
        //return true
        if ((formAssetDetails?.isNotEmpty() == true) &&
            (formAssetDetails?.firstOrNull()?.siteId.isNullOrBlank()
                    || fromSid != formAssetDetails?.firstOrNull()?.siteId)
        ) {
            CustomToastMsg.showCustomToast(
                requireActivity(), "SiteID mismatch."
            )
            return false
        }else if((formAssetDetails?.isNotEmpty() == true)
            && (tranAssetDetails?.itemCode?.isNotEmpty()==true)
            && (tranAssetDetails?.itemCode?.isNotBlank()==true)
            && (formAssetDetails?.firstOrNull()?.itemCode ?: "") != tranAssetDetails?.itemCode)
            {
                CustomToastMsg.showCustomToast(
                    requireActivity(),"Item code mismatch")
             return false
        }else if((formAssetDetails?.isNotEmpty() == true)
            && (tranAssetDetails?.assetType?.isNotEmpty()==true)
            && (tranAssetDetails?.assetType?.isNotBlank()==true)
            && (formAssetDetails?.firstOrNull()?.assetType ?: "") != tranAssetDetails?.assetType)
        {
            CustomToastMsg.showCustomToast(
                requireActivity(),"Asset type mismatch")
            return false
        }else if((formAssetDetails?.isNotEmpty() == true)
            && (tranAssetDetails?.assetId?.isNotEmpty()==true)
            && (tranAssetDetails?.assetId?.isNotBlank()==true)
            && (formAssetDetails?.firstOrNull()?.assetId ?: "") != tranAssetDetails?.assetId)
        {
            CustomToastMsg.showCustomToast(
                requireActivity(),"Asset id mismatch")
            return false
        }
        // verify unique asset id
        else if((formAssetDetails?.isNotEmpty() == true)
            && hasDuplicateAssetById(taskDetailEntity,
                formAssetDetails?.firstOrNull()?.assetId ?: "")
            )
        {
            CustomToastMsg.showCustomToast(
                requireActivity(),"This asset has already been added to the list and verified.")
            return false
        }
        return true
    }



    // Function to check for duplicates of a specific assetId entered by the user
    fun hasDuplicateAssetById(taskDetailEntity: TaskDetailEntity?, userEnteredAssetId: String?): Boolean {
        // Only proceed if taskDetailEntity and userEnteredAssetId are not null
        if (taskDetailEntity != null && userEnteredAssetId != null) {
            taskDetailEntity.assets.let { assets ->
                // Filter assets with status 1
                val filteredAssets = assets.filter { it.status == 1 }

                // Check if the assetId already exists in the assets list
                val assetExists = filteredAssets.any { it.assetId == userEnteredAssetId }

                // If the asset already exists, return false (indicating it's a duplicate)
                if (assetExists) {
                    return true
                }
            }
        }

        // If the asset doesn't exist, return true (allow adding the asset)
        return false
    }

}