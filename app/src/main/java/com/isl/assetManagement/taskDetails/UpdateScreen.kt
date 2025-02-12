import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.*
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
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.isl.assetManagement.assetDetails.VerifyAsset
import com.isl.assetManagement.constants.DefaultLevel
import com.isl.assetManagement.dataViewModel.RoomViewModel
import com.isl.assetManagement.dataViewModel.SharedViewModel
import com.isl.assetManagement.jetpackcompose.*
import com.isl.assetManagement.jetpackcompose.JetpackUIs.TimelineScreen
import com.isl.assetManagement.responses.Assets
import com.isl.assetManagement.responses.Timeline
import com.isl.assetManagement.room.repository.RoomRepository
import com.isl.assetManagement.utils.DataViewModelFactory
import com.isl.assetManagement.utils.Util
import com.isl.itower.MyApp
import infozech.itower.R
import kotlinx.coroutines.launch

class UpdateScreen : BottomSheetDialogFragment() {
    private lateinit var roomRepository: RoomRepository
    private lateinit var viewModel: RoomViewModel
    private var requestId: String? = null
    private val sharedViewModel: SharedViewModel by activityViewModels()

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
                if (dismissed) {
                    println("Dialog B was dismissed!")
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


    @SuppressLint("NotConstructor")
    @Composable
    fun UpdateScreen() {

        requestId?.let { viewModel.getTaskDetail(it) }
        val taskDetail by viewModel.getTaskDetail.observeAsState()

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
                    val tabItems = listOf(
                        "Update Request",
                        "Assets (${taskDetail?.assets?.size ?: 0})",
                        "Track",
                        "Files"
                    )
                    var selectedTabIndex by remember { mutableStateOf(0) }

                    AddUpdateTabs(
                        tabItems = tabItems,
                        selectedTabIndex = mutableStateOf(selectedTabIndex),
                        onTabSelected = { index -> selectedTabIndex = index }
                    )

                    //for testing
                    val sampleTimelines = listOf(
                        Timeline(
                            stage = "Approver 1",
                            status = "Approved",
                            user = "approver1@email.com",
                            timestamp = "22 Jun, 2024",
                            latitude = 12.9716,
                            longitude = 77.5946
                        ),
                        Timeline(
                            stage = "Approver 2",
                            status = "Approved",
                            user = "approver2@email.com",
                            timestamp = "22 Jun, 2024",
                            latitude = 12.9716,
                            longitude = 77.5946
                        ),
                        Timeline(
                            stage = "Approver 3",
                            status = "Approved",
                            user = "approver3@email.com",
                            timestamp = "22 Jun, 2024",
                            latitude = 12.9716,
                            longitude = 77.5946
                        ),
                        Timeline(
                            stage = "Issuer",
                            status = "Dispatched",
                            user = "issuer@email.com",
                            timestamp = "22 Jun, 2024",
                            latitude = 12.9716,
                            longitude = 77.5946
                        ),
                        Timeline(
                            stage = "Receiver",
                            status = "Dispatched",
                            user = "receiver@email.com",
                            timestamp = "22 Jun, 2024",
                            latitude = 12.9716,
                            longitude = 77.5946
                        )
                        ,     Timeline(
                            stage = "Receiver",
                            status = "Dispatched",
                            user = "receiver@email.com",
                            timestamp = "22 Jun, 2024",
                            latitude = 12.9716,
                            longitude = 77.5946
                        ),     Timeline(
                            stage = "Receiver",
                            status = "Dispatched",
                            user = "receiver@email.com",
                            timestamp = "22 Jun, 2024",
                            latitude = 12.9716,
                            longitude = 77.5946
                        ),     Timeline(
                            stage = "Receiver",
                            status = "Dispatched",
                            user = "receiver@email.com",
                            timestamp = "22 Jun, 2024",
                            latitude = 12.9716,
                            longitude = 77.5946
                        )
                    )



                    when (selectedTabIndex) {
                        0 -> addDetails()
                        1 -> addAsset()
                //        2 -> TimelineScreen(taskDetail?.timelines)
                        2 -> TimelineScreen(sampleTimelines)
                        3 -> ImageDetailCard()
                    }
                }
            }
        )
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun addDetails() {
        var requestId by remember { mutableStateOf("") }
        var expanded by remember { mutableStateOf(false) }
        var selectedOption by remember { mutableStateOf("Select") }
        val options = listOf("Dispatched", "Installed")

        // FocusManager and LazyListState
        val focusManager = LocalFocusManager.current
        val listState = rememberLazyListState()
        val coroutineScope = rememberCoroutineScope()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(WindowInsets.ime.asPaddingValues()), // Adjust for keyboard visibility
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
                        text = "Action Taken",
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
                                expanded = it
                            }
                        ) {
                            Text(
                                text = selectedOption,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 12.dp)
                                    .clickable { expanded = true },
                                style = TextStyle(
                                    color = colorResource(id = R.color.input_box_text_color),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Normal
                                )
                            )

                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                options.forEach { option ->
                                    DropdownMenuItem(
                                        onClick = {
                                            selectedOption = option
                                            expanded = false
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
                            text = remark,
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
                                value = requestId,
                                onValueChange = { requestId = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 12.dp)
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
    fun ImageDetailCard() {
        ImageCardView(
            imageRes = R.drawable.itower_test_image,
            fileTag = "image.jpg",
            timeStamp = "20-Jun-2024, 14:30 adasfdasfasfdfasdfasdfasfdsadas",
            lat = "-32424.4924",
            long = "-32424.4923",
            onClick = {
            }
        )
    }


    @Composable
    fun addAsset() {
        SnackbarUtils.showLoading(requireActivity(), "Please wait...")
        requestId?.let { viewModel.getTaskDetail(it) }
        val taskDetail by viewModel.getTaskDetail.observeAsState()
        taskDetail?.let {
            //Grid(it.assets,it.fromLocation.siteId)
            Grid(it.assets, it.fromLocation.siteId)
        }
        SnackbarUtils.hideLoading()
    }


    @Composable
    fun Grid(items: List<Assets>, fromSid: String) {
        // Check if items list is not empty before rendering LazyColumn
        var result: HashMap<String, String> = hashMapOf()
        result = Util.stringToHashMap("" + DefaultLevel.msg()["assetStatus"])
        if (items.isNotEmpty()) {
            LazyColumn {
                items(items) { item ->
                    AssetCardView(item,
                        onClick = {
                            val bundle = Bundle()
                            bundle.putParcelable("tranAssetDetails", item)
                            bundle.putString("fromSid", fromSid)
                            bundle.putString("requestId", requestId)
                            val fragment = VerifyAsset()
                            fragment.arguments = bundle
                            fragment.show(requireActivity().supportFragmentManager, "VerifyAsset")
                        })
                }
            }
        }
    }

}




