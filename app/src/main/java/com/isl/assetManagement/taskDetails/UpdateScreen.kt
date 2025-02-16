package com.isl.assetManagement.taskDetails

import SnackbarUtils
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ContentValues
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.isl.assetManagement.assetDetails.VerifyAsset
import com.isl.assetManagement.constants.DefaultLevel
import com.isl.assetManagement.dataViewModel.RoomViewModel
import com.isl.assetManagement.dataViewModel.SharedViewModel
import com.isl.assetManagement.jetpackcompose.*
import com.isl.assetManagement.requests.Asset
import com.isl.assetManagement.requests.Document
import com.isl.assetManagement.requests.TaskUploadPayload
import com.isl.assetManagement.responses.*
import com.isl.assetManagement.room.entity.TaskDetailEntity
import com.isl.assetManagement.room.repository.RoomRepository
import com.isl.assetManagement.sharedPref.KotlinPrefkeeper
import com.isl.assetManagement.utils.CustomToastMsg
import com.isl.assetManagement.utils.DataViewModelFactory
import com.isl.assetManagement.utils.ImageUtil
import com.isl.assetManagement.utils.Util
import com.isl.dao.cache.AppPreferences
import com.isl.itower.GPSTracker
import com.isl.itower.MyApp
import infozech.itower.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import androidx.compose.foundation.background

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle


class UpdateScreen : DialogFragment() {
    private lateinit var roomRepository: RoomRepository
    private lateinit var viewModel: RoomViewModel
    private var requestId: String? = null
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val formViewModel: FormViewModel by activityViewModels()
    private var options = listOf("Approve", "Dispatch", "Reject")
    private lateinit var imageUri: Uri
    private var currentTag: String = ""
    // Define the launcher for camera image capture
    //private lateinit var takePictureLauncher: ActivityResultLauncher<Uri>


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

            bottomSheet?.let {
                //setBackgroundColor(android.graphics.Color.TRANSPARENT)
                it.setBackgroundColor(android.graphics.Color.TRANSPARENT) // Set a rounded background drawable
            }

            bottomSheet?.post {

                val behavior = BottomSheetBehavior.from(bottomSheet)

                // Set initial height (85% of screen height)
                val eightyFivePercentHeight = (resources.displayMetrics.heightPixels * 0.85).toInt()
                Log.d("BottomSheet", "Setting initial height to: $eightyFivePercentHeight")

                behavior.peekHeight = eightyFivePercentHeight
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.isHideable = false

                // Set initial layout params
                val layoutParams = bottomSheet.layoutParams
                layoutParams.height = eightyFivePercentHeight
                bottomSheet.layoutParams = layoutParams

                // Add OnGlobalLayoutListener to detect keyboard visibility
                val rootView = dialog.findViewById<View>(android.R.id.content)
                rootView?.viewTreeObserver?.addOnGlobalLayoutListener(object :
                    ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        val isKeyboardVisible = isKeyboardVisible(rootView)
                        val updatedHeight = if (isKeyboardVisible) {
                            (resources.displayMetrics.heightPixels * 0.62).toInt() // 62% when keyboard is open
                        } else {
                            (resources.displayMetrics.heightPixels * 0.85).toInt() // 85% when keyboard is closed
                        }

                        // Update BottomSheet height dynamically
                        val layoutParams = bottomSheet.layoutParams
                        layoutParams.height = updatedHeight
                        bottomSheet.layoutParams = layoutParams
                    }
                })
            }
        }


        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        dialog.setContentView(
            ComposeView(requireContext()).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        )

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
                )
                {
                    UpdateScreen()

                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe when DialogFragment B is dismissed
        sharedViewModel.dialogVerifyAssetDismissed.observe(
            viewLifecycleOwner,
            Observer { dismissed ->
                /* if (dismissed) {
                     println("Dialog B was dismissed!")
                 }*/
            })

        // Observe when DialogFragment B is dismissed
        sharedViewModel.isUpdateTaskDetails.observe(viewLifecycleOwner, Observer { dismissed ->
            if (dismissed) {
                dismissAllowingStateLoss()
            }
        })

    }

    // Helper function to detect if the keyboard is visible
    private fun isKeyboardVisible(rootView: View): Boolean {
        val rect = android.graphics.Rect()
        rootView.getWindowVisibleDisplayFrame(rect)
        val screenHeight = rootView.height
        val keypadHeight = screenHeight - rect.bottom
        return keypadHeight > screenHeight * 0.15 // Threshold for detecting keyboard visibility
    }


    @SuppressLint("NotConstructor", "SuspiciousIndentation")
    @Composable
    fun UpdateScreen() {
        var isLoading by remember { mutableStateOf(false) }  // State for loading indicator

        requestId?.let { viewModel.getTaskDetail(it) }
        val taskDetailEntity by viewModel.getTaskDetail.observeAsState()
        val assets = taskDetailEntity?.assets ?: emptyList()
        val totalApprovedQty = assets?.sumOf { it.approvedQty } ?: 0
        val totalCountWithStatus1 = assets?.count { it.status == 1 } ?: 0
        val status = taskDetailEntity?.status ?: ""
        //var a = taskDetailEntity?.status
        options = if (status.equals("Pending for Installer")) {
            listOf("Installed")
        } else if (status.equals("Pending for Issuer")) {
            listOf("Dispatch", "Reject")
        } else {
            listOf("NA")
        }
        Scaffold(
            topBar = {
                DialogFragmentTopBar(
                    onGrips = {
                        /* Handle grips */
                    },
                    closed = {
                        dismiss()
                    },
                    "Update Ticket"
                )
            },
            bottomBar = {
                BottomBar(
                    onCancelClicked = {
                        dismiss()
                    },
                    onUpdateClicked = {
                        // Validation for Action Taken (Dropdown)
                        if (formViewModel.selectedOption.value == "Select") {
                            CustomToastMsg.showCustomToast(requireActivity(), "Select action taken")
                        } else if (formViewModel.remarks.value.isNullOrEmpty()) {
                            CustomToastMsg.showCustomToast(
                                requireActivity(),
                                "Remarks cannot be blank"
                            )
                        }//else if (assets.any { it.status == 0 }) {
                        else if (totalApprovedQty > totalCountWithStatus1) {
                            CustomToastMsg.showCustomToast(
                                requireActivity(),
                                "Please verify the asset by scanning the QR code " +
                                        "and select asset for movement."
                            )
                        } else {
                            val payload =
                                mapTaskDetailEntityToPayload(taskDetailEntity, formViewModel)
                            if (payload != null) {
                                isLoading = true  // Show loading before API call
                                lifecycleScope.launch(Dispatchers.Main) {
                                    //SnackbarUtils.showLoading(requireActivity(),"Please wait...")
                                    //scaffoldState.snackbarHostState.showSnackbar("Your message")
                                    // Show an indefinite (loading) snackbar

                                    val token = roomRepository.fetchToken()
                                    if (token != null) {
                                        viewModel.addUpdateResuest(
                                            token,
                                            requestId!!,
                                            body = payload
                                        ) { response ->
                                            isLoading = false  // Hide loading after API response
                                            if (response.flag == "0") {
                                                // Success case
                                                //val data = response.data
                                                //println("✅ Upload Success! Request ID:
                                                // ${data?.id}, Replace ID: ${data?.replaceId}")
                                                CustomToastMsg.showCustomToast(
                                                    requireActivity(),
                                                    "Request updated successfully!"
                                                    //dismiss()
                                                )
                                                sharedViewModel.notifyUpdateTaskDetails(true)
                                                dismiss()
                                            } else {
                                                //sharedViewModel.notifyUpdateTaskDetails(true)
                                                //dismiss()
                                                // Error case
                                                val errorMessage =
                                                    response.errors?.firstOrNull()?.message
                                                        ?: "Unknown error"
                                                val errorCode =
                                                    response.errors?.firstOrNull()?.code
                                                        ?: "UNKNOWN_ERROR"
                                                /*CustomToastMsg.showCustomToast(
                                                    requireActivity(),
                                                    "Failed: $errorMessage ($errorCode)"*/
                                                CustomToastMsg.showCustomToast(
                                                    requireActivity(),
                                                    "$errorMessage"
                                                )
                                            }
                                        }
                                    } else {
                                        isLoading = false  // Hide loading after API response
                                        CustomToastMsg.showCustomToast(
                                            requireActivity(),
                                            "Token authentication failed. Try again."
                                        )
                                    }
                                }

                            }

                        }
                    },
                    "Back",
                    "Submit",
                    modifier = Modifier
                        .navigationBarsPadding() // Adds padding to handle navigation bars
                        .imePadding() // Adds padding to handle the on-screen keyboard
                )
            },
            content = { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colorResource(id = R.color.white))
                        .padding(innerPadding) // Use innerPadding to handle content insets
                ) {
                    // Assuming this is within your activity or fragment where you want to show the total approvedQty
                    val totalApprovedQty = taskDetailEntity?.assets?.sumOf { it.approvedQty } ?: 0
                    val tabItems = listOf(
                        "Update Request",
                        //"Assets (${taskDetailEntity?.assets?.size ?: 0})",
                        "Assets ($totalApprovedQty)",
                        "Files (${taskDetailEntity?.documents?.size ?: 0})"
                    )
                    var selectedTabIndex by remember { mutableStateOf(0) }

                    AddUpdateTabs(
                        tabItems = tabItems,
                        selectedTabIndex = mutableStateOf(selectedTabIndex),
                        onTabSelected = { index -> selectedTabIndex = index }
                    )

                    when (selectedTabIndex) {
                        0 -> addDetails(formViewModel)
                        1 -> addAsset()
                        2 -> addDocument()
                    }
                }
            }
        )

        // Show the LoadingDialog when `isLoading` is true
        if (isLoading) {
            LoadingDialog { isLoading = false }
        }
    }


    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun addDetails(formViewModel: FormViewModel) {
        val remarks by formViewModel.remarks.observeAsState("")
        val expanded by formViewModel.expanded.observeAsState(false)
        val selectedOption by formViewModel.selectedOption.observeAsState("Select")


        //val options = listOf("Approve","Dispatch","Reject")

        // FocusManager and LazyListState
        val focusManager = LocalFocusManager.current
        val listState = rememberLazyListState()
        val coroutineScope = rememberCoroutineScope()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(WindowInsets.ime.asPaddingValues()),
            contentAlignment = Alignment.Center
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = 20.dp,
                        end = 20.dp,
                        bottom = WindowInsets.ime
                            .asPaddingValues()
                            .calculateBottomPadding()
                    ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Action Taken Dropdown
                item {
                    Text(
                        text = buildAnnotatedString {
                            append("Action Taken ")
                            withStyle(style = SpanStyle(color = colorResource(R.color.search))) {
                                append("*")  // Asterisk in red
                            }
                        },
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = colorResource(id = R.color.label)
                        ),
                        modifier = Modifier.padding(top = 0.dp, bottom = 2.dp)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(47.dp)
                            .background(
                                color = colorResource(id = R.color.input_box),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = colorResource(id = R.color.input_box_border),
                                shape = RoundedCornerShape(4.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = {
                                focusManager.clearFocus()
                                //expanded = it
                                formViewModel.expanded.value = it
                            }
                        ) {
                            Text(
                                text = selectedOption,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 12.dp)
                                    .clickable { formViewModel.expanded.value = true },
                                style = TextStyle(
                                    color = colorResource(id = R.color.input_box_text_color),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Normal
                                )
                            )

                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { formViewModel.expanded.value = false }
                            ) {
                                options.forEach { option ->
                                    DropdownMenuItem(
                                        onClick = {
                                            //selectedOption = option
                                            formViewModel.selectedOption.value = option
                                            formViewModel.expanded.value = false
                                            formViewModel.expanded.value = false
                                        }
                                    ) {
                                        Text(
                                            text = option,
                                            style = TextStyle(
                                                color = colorResource(id = R.color.input_box_text_color),
                                                fontSize = 14.sp
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        Icon(
                            painter = painterResource(id = R.drawable.keyboard_arrow_down),
                            contentDescription = "Drop-down",
                            tint = colorResource(id = R.color.tint_ddl),
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 8.dp)
                                .size(24.dp)
                        )
                    }
                }

                // Remarks Fields
                listOf("Remarks").forEachIndexed { index, remark ->
                    item {
                        Text(
                            text = buildAnnotatedString {
                                append(remark)
                                withStyle(style = SpanStyle(color = colorResource(R.color.search))) {
                                    append(" *")  // Asterisk in red
                                }
                            },
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                color = colorResource(id = R.color.label)
                            ),
                            modifier = Modifier.padding(top = 10.dp, bottom = 2.dp)
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(86.dp)
                                .background(
                                    color = colorResource(id = R.color.input_box),
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = colorResource(id = R.color.input_box_border),
                                    shape = RoundedCornerShape(4.dp)
                                )
                        ) {
                            BasicTextField(
                                value = remarks,
                                onValueChange = { formViewModel.remarks.value = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 12.dp, top = 12.dp)
                                    .onFocusChanged { focusState ->
                                        if (focusState.isFocused) {
                                            coroutineScope.launch {
                                                // Dynamically scroll to the focused field
                                                listState.animateScrollToItem(index + 1)
                                            }
                                        }
                                    },
                                textStyle = TextStyle(
                                    color = colorResource(id = R.color.input_box_text_color),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Normal
                                ),
                                cursorBrush = SolidColor(colorResource(id = R.color.input_box_text_color))
                            )
                        }
                    }
                }
            }
        }
    }


    @Composable
    fun addAsset() {
        SnackbarUtils.showLoading(requireActivity(), "Please wait...")
        requestId?.let { viewModel.getTaskDetail(it) }
        val taskDetail by viewModel.getTaskDetail.observeAsState()
        taskDetail?.let {
            Grid(it.assets, it.fromLocation.siteId)
        }
        SnackbarUtils.hideLoading()
    }

    @Composable
    fun Grid(items: List<Assets>, fromSid: String) {


        if (items.isNotEmpty()) {
            LazyColumn {
                items(items) { item ->
                    AssetCardView(item,
                        onClick = {
                            if (item.status == 1) {
                                CustomToastMsg.showCustomToast(
                                    requireActivity(), "This asset has already been verified."
                                )
                            }/*else if(hasDuplicateAssetById(items,item)){
                                CustomToastMsg.showCustomToast(
                                    requireActivity(),
                                    "A maximum ${item.approvedQty} assets have been added for Asset Type" +
                                            "(${item.assetType}) and item code (${item.itemCode})."
                                )
                            }*/ else {
                                val bundle = Bundle()
                                bundle.putParcelable("tranAssetDetails", item)
                                bundle.putString("fromSid", fromSid)
                                bundle.putString("requestId", requestId)
                                val fragment = VerifyAsset()
                                fragment.arguments = bundle
                                fragment.show(
                                    requireActivity().supportFragmentManager,
                                    "VerifyAsset"
                                )
                            }
                        })
                }
            }
        }
    }

    // Function to check for duplicates of a specific assetId entered by the user
    fun hasDuplicateAssetById(
        items: List<Assets>,
        assets: Assets
    ): Boolean {
        // Filter assets with status 1, specific itemCode, and specific assetType
        val filteredAssets = items.filter {
            it.status == 1 &&
                    it.itemCode == assets.itemCode &&
                    it.assetType == assets.assetType
        }

        // Count the number of matching assets
        val count = filteredAssets.size

        if (count >= assets.approvedQty) {
            return true
        }
        return false
    }

    @Composable
    fun addDocument() {
        //SnackbarUtils.showLoading(requireActivity(),"Please wait...")
        requestId?.let { viewModel.getTaskDetail(it) }
        val taskDetail by viewModel.getTaskDetail.observeAsState()
        taskDetail?.let {
            //Grid(it.assets,it.fromLocation.siteId)
            documentView(it.documents)
        }
        //SnackbarUtils.hideLoading()
    }

    @Composable
    fun documentView(items: List<Documents>) {
        var showPopup by remember { mutableStateOf(false) }
        //var isUploading by remember { mutableStateOf(false) }  // State for progress dialog

        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 0.dp, end = 20.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.End // Aligns items to the right
            ) {
                Box(
                    modifier = Modifier
                        .height(36.dp)
                        .width(102.dp)
                        // Add end padding if needed
                        .background(
                            color = colorResource(id = R.color.color_add_request_border),
                            shape = RoundedCornerShape(2.dp)
                        )
                        .padding(1.dp)
                        .background(
                            color = colorResource(id = R.color.color_add_request_background),
                            shape = RoundedCornerShape(1.dp)
                        )
                        .wrapContentSize(Alignment.Center)
                ) {
                    Text(
                        text = "Files +",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorResource(id = R.color.color_add_request)
                        ),
                        modifier = Modifier.clickable {
                            showPopup = true
                        }
                    )
                }
            }
            var result: HashMap<String, String> = hashMapOf()
            result = Util.stringToHashMap("" + DefaultLevel.msg()["imgTag"])
            currentTag = result["" + items.size].toString()
            if (currentTag.isNullOrEmpty()) {
                currentTag = ""
            }
            CaptureImagePopup(
                msg = currentTag,
                showDialog = showPopup,
                onDismiss = { showPopup = false },
                onCapture = {
                    showPopup = false
                    //isUploading = true
                    addDoc()
                    //isUploading = false
                }
            )

            if (items.isNotEmpty()) {
                LazyColumn {
                    items(items) { item ->
                        ImageCardView(
                            item,
                            mode = 0, // R.drawable.itower_test_image,
                            flag = 1, // "image.jpg",
                            onClick = { }
                        )
                    }
                }
            }
        }
        // Show loading dialog when uploading
        /*if (isUploading) {
            LoadingDialog()
        }*/
    }

    // Register Activity Result Launcher for capturing image
    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {  // success full capture photo
                val gps = GPSTracker(requireActivity())
                val document = KotlinPrefkeeper.assetUserId?.let {
                    ImageUtil.convertImageUriToBase64(requireActivity(), imageUri)?.let { it1 ->
                        Documents(
                            userId = it,  //need to image payload
                            fileName = UUID.randomUUID()
                                .toString() + ".jpeg", //need to image payload
                            content = it1, //need to payload
                            latiude = gps.latitude,
                            latitude = gps.latitude, //need to image payload/image card view/final payload
                            longitude = gps.longitude, //need to image payload/image card view/final payload
                            tagName = currentTag, //need to image payload/image card view/final payload
                            timeStamp = Util.getCurrentDateTime(), //need to image payload/image card view/final payload
                            url = imageUri.toString(),//need to display in image card view
                            type = ".jpeg",
                            tempDocId = "", //need to payload we will update after upload to server
                            status = 0  // need to future 0 means initial/1 already from server
                            //2 means change status after upload in server
                        )
                    }
                }
                requestId?.let {
                    if (document != null) {
                        viewModel.uploadDocument(
                            "your_token_here",
                            requestId!!, body = document
                        ) { response ->
                            when (response) {
                                is DocUploadApiResponse.Success -> {

                                    val docId = response.data.docId
                                    val updatedDocument =
                                        document.copy(tempDocId = docId.toString(), content = "")
                                    viewModel.addDocToExistingRequestDetails(
                                        0,
                                        it,
                                        updatedDocument
                                    ) { success ->
                                        if (success) {
                                        }
                                    }
                                }

                                is DocUploadApiResponse.Error -> {
                                    CustomToastMsg.showCustomToast(
                                        requireActivity(),
                                        "Upload failed"
                                    )
                                }

                                else -> {}
                            }
                        }
                    }
                }
            } else {
                CustomToastMsg.showCustomToast(
                    requireActivity(), "There was an issue capturing the image. " +
                            "Please try re-capturing."
                )
            }
        }


    fun addDoc() {
        // Create the content values for the captured image
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.TITLE, "Captured Image")
            put(MediaStore.Images.Media.DESCRIPTION, "Image from camera")
        }
        // Get the URI for storing the image
        imageUri = requireContext().contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )!!
        // Launch the camera to capture the image
        takePictureLauncher.launch(imageUri)


    }

    private fun mapTaskDetailEntityToPayload(
        taskDetail: TaskDetailEntity?,
        formViewModel: FormViewModel
    ): TaskUploadPayload {
        var mAppPref: AppPreferences? = null
        mAppPref = AppPreferences(requireContext())
        return taskDetail?.let {
            TaskUploadPayload(
                woNumber = "",
                project = "",
                fromLocation = it.fromLocation.siteId,
                toLocation = it.toLocation.siteId,
                reasonCategory = it.reasonCategory,
                reasonSubCategory = it.reasonSubCategory,
                movementDate = "2025-02-15T07:49:37.192Z",   //it.movementDate
                remarks = "" + formViewModel.remarks.value,
                status = "" + formViewModel.selectedOption.value,
                source = "Mobile",
                user = mAppPref.loginId,
                /* assets = it.assets.map { asset ->
                     Asset(
                         assetId = asset.assetId ?: "",
                         qrCode = asset.qrCode ?: ""
                     )
                 },*/
                assets = it.assets.filter { asset ->
                    asset.status == 1  // Filter assets that have status == 1
                }.map { asset ->
                    Asset(
                        assetId = asset.assetId ?: "",
                        qrCode = asset.qrCode ?: ""
                    )
                },

                /*documents = it.documents.filter { document ->
                    document.status == 1  // Filter assets that have status == 1
                }.map { document ->
                    Document(
                        tempDocId = document.tempDocId ?: ""
                    )
                },*/
                /* documents = it.documents.map { doc ->
                     Document(
                         latiude = doc.latitude,
                         longitude = doc.longitude,
                         tagName = doc.tagName,
                         timeStamp = doc.timeStamp,
                         url = doc.url,
                         type = doc.type,
                         content = doc.content,
                         tempDocId = doc.tempDocId
                     )
                 }*/

                /* Filter documents where tempDocId is not null */
                documents = it.documents.filter { doc ->
                    doc.tempDocId != null // Only include documents with a non-null tempDocId
                }.map { doc ->
                    Document(
                        tempDocId = doc.tempDocId
                            ?: "" // Provide an empty string for null tempDocId
                    )
                }

                /*replacementDetail = ReplacementDetail(
                    fromLocation = it.fromLocation.siteId,
                    reasonCategory = it.reasonCategory,
                    reasonSubCategory = it.reasonSubCategory,
                    movementDate = it.movementDate
                )*/
            )
        } ?: TaskUploadPayload() // Return an empty payload if taskDetail is null
    }
}

class FormViewModel : ViewModel() {
    var remarks = MutableLiveData("")
    var expanded = MutableLiveData(false)
    var selectedOption = MutableLiveData("Select")
}






