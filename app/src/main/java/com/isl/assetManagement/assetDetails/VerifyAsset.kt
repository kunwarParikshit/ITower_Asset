package com.isl.assetManagement.assetDetails
import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.fragment.app.DialogFragment
import com.google.zxing.integration.android.IntentIntegrator
import infozech.itower.R
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.isl.assetManagement.dataViewModel.RemoteViewModel
import com.isl.assetManagement.dataViewModel.RoomViewModel
import com.isl.assetManagement.dataViewModel.SharedViewModel
import com.isl.assetManagement.jetpackcompose.BottomBar
import com.isl.assetManagement.jetpackcompose.LoadingDialog
import com.isl.assetManagement.jetpackcompose.TopBar
import com.isl.assetManagement.responses.AssetDetailsResponse
import com.isl.assetManagement.responses.Assets
import com.isl.assetManagement.room.repository.RemoteRepository
import com.isl.assetManagement.room.repository.RoomRepository
import com.isl.assetManagement.utils.CustomToastMsg
import com.isl.assetManagement.utils.DataViewModelFactory
import com.isl.assetManagement.utils.HomeViewModelFactory
import com.isl.itower.MyApp
import kotlinx.coroutines.launch

class VerifyAsset : DialogFragment() {
    private lateinit var roomRepository: RoomRepository
    private lateinit var roomViewModel: RoomViewModel
    private lateinit var remoteViewModel: RemoteViewModel
    private var tranAssetDetails: Assets? = null
    private var requestId: String? = null
    private var scannedResult = mutableStateOf<String?>(null)
    private lateinit var fromSid : String
    // Get the shared ViewModel
    private val sharedViewModel: SharedViewModel by activityViewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tranAssetDetails = arguments?.getParcelable("tranAssetDetails")
        fromSid = arguments?.getString("fromSid")?.let { it } ?: ""
        arguments?.let {
            requestId = it.getString("requestId") // Get the requestId
        }
        roomRepository = RoomRepository(MyApp.getAssetDatabase().dataDao())
        roomViewModel = ViewModelProvider(this, DataViewModelFactory(roomRepository)).get(RoomViewModel::class.java)
        remoteViewModel = ViewModelProvider(requireActivity(), HomeViewModelFactory(RemoteRepository()))[RemoteViewModel::class.java]
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

    override fun onResume() {
        super.onResume()
        Log.d("FragmentLifecycle", "onResume() called in ${this::class.java.simpleName}")
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        // Notify A that DialogFragment B was dismissed
        sharedViewModel.notifyDialogVerifyAssetDismissed()
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
        var isLoading by remember { mutableStateOf(false) } // Show progress initially
        var qrCode by remember { mutableStateOf("") }
        var assetId by remember { mutableStateOf("") }
        val coroutineScope = rememberCoroutineScope()
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

                    },
                    "Select Asset(s)",
                    "Scan QR code & select assets in movement",
                    "",
                    0
                )
            },
            bottomBar = {
                BottomBar(
                    onCancelClicked = {
                        dismiss()
                    },
                    onUpdateClicked = {
                        if(qrCode.isNotEmpty() || assetId.isNotEmpty()){
                            coroutineScope.launch {

                                getDetails(qrCode.uppercase(), assetId) { success ->
                                    // Handle the completion status here
                                    isLoading = false
                                }
                            }
                        }else{
                            CustomToastMsg.showCustomToast(requireActivity(),
                                "Scan QR code or enter QR code or enter asset Id")


                        }
                    },
                    "Cancel",
                    "Search"
                )
            },
            content = { paddingValues -> // Respect bottom bar height
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues) // Apply scaffold padding to prevent overlap
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = 20.dp, end = 20.dp, top = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item () {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(end = 10.dp), // Optional spacing from the right edge
                                contentAlignment = Alignment.Center // Aligns the circle to the right side
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp) // Adjust the size of the circle
                                        .clip(CircleShape) // Clip to a circular shape
                                        .background(colorResource(id = R.color.btn)) // Background color of the circle
                                        .clickable {
                                            qrScanner()
                                        },
                                    contentAlignment = Alignment.Center // Center the icon inside the circle
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.qr_code_icon),
                                        contentDescription = "QR Code scanner icon",
                                        modifier = Modifier.size(24.dp), // Adjust icon size inside the circle
                                        tint = Color.White // Change icon color if needed
                                    )
                                }
                            }

                            Text(
                                text = buildAnnotatedString {
                                    append("QR Code")
                                    withStyle(style = SpanStyle(color = colorResource(R.color.search))) {
                                        append(" *")  // Asterisk in red
                                    }
                                },
                                //text = "QR Code",
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
                                    .height(47.dp) // Set fixed height
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
                                    value = qrCode,
                                    onValueChange = {
                                        qrCode = it
                                        if (it.isNotEmpty()) assetId = ""
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 12.dp, start = 12.dp),
                                    textStyle = TextStyle(
                                        color = colorResource(id = R.color.input_box_text_color),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Normal
                                    ),
                                    singleLine = true,
                                    cursorBrush = SolidColor(colorResource(id = R.color.input_box_text_color))
                                )
                            }

                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "or",
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = colorResource(id = R.color.label)
                                    ),
                                    modifier = Modifier.padding(top = 10.dp, bottom = 0.dp)
                                )
                            }

                            Text(
                                text = buildAnnotatedString {
                                    append("Asset Id")
                                    withStyle(style = SpanStyle(color = colorResource(R.color.search))) {
                                        append(" *")  // Asterisk in red
                                    }
                                },
                                //text = "Asset Id",
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
                                    .height(47.dp)
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
                                    value = assetId,
                                    onValueChange = {
                                        assetId = it
                                        if (it.isNotEmpty()) qrCode = "" // Clear qrCode when assetId is entered
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 12.dp, start = 12.dp),
                                    textStyle = TextStyle(
                                        color = colorResource(id = R.color.input_box_text_color),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Normal
                                    ),
                                    singleLine = true,
                                    cursorBrush = SolidColor(colorResource(id = R.color.input_box_text_color))
                                )
                            }


                        }

                    }//end of main column
                }//end of box first
            } //end of Scaffold
        )

        scannedResult.value?.let {
            VerifyAssetScreen(it)
        }

        // Show the LoadingDialog when `isLoading` is true
        if (isLoading) {
            LoadingDialog { isLoading = false }
        }
    }

    // Register for activity result
    private val qrScannerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val scanResult = result.data?.getStringExtra("SCAN_RESULT")

            if (scanResult.isNullOrEmpty()) {
                CustomToastMsg.showCustomToast(requireContext(), "Scan Cancelled")
            } else {
                scannedResult.value = null
                Handler(Looper.getMainLooper()).postDelayed({
                    scannedResult.value = scanResult
                }, 100)
            }
        } else {
            CustomToastMsg.showCustomToast(requireContext(), "Scan Failed")
        }
    }

    fun qrScanner() {
        val intent = IntentIntegrator(requireActivity()).createScanIntent()
        qrScannerLauncher.launch(intent)
    }


    @Composable
    fun VerifyAssetScreen(scanResult: String) {
        var isLoading by remember { mutableStateOf(false) } // Show progress initially
        val coroutineScope = rememberCoroutineScope()


        coroutineScope.launch {
            //isLoading = true
            getDetails(scanResult, "") { success ->
                // Handle the completion status here
                isLoading = false
            }
        }

        // Show the LoadingDialog when `isLoading` is true
        if (isLoading) {
            LoadingDialog { isLoading = false }
        }

    }


    suspend fun getDetails(scanResult: String,assetId: String,onComplete: (Boolean) -> Unit) {
            onComplete(true)
            val token = roomRepository.fetchToken()
            if (token != null) {
                remoteViewModel.getAssetDetails(
                    token = token,
                    siteId = "",
                    assetId = assetId,
                    qrCode = scanResult,
                    onDataInserted = {status ->}
                ) { assets: List<AssetDetailsResponse>? ->
                    assets?.let {

                        val bundle = Bundle()
                        bundle.putParcelable("tranAssetDetails",tranAssetDetails)
                        bundle.putParcelableArrayList("formAssetDetails", ArrayList(it))
                        bundle.putString("fromSid",fromSid)
                        bundle.putString("requestId",requestId)
                        val fragment = AssetDetails()
                        fragment.arguments = bundle
                        val transaction = childFragmentManager.beginTransaction()
                        transaction.add(fragment, "AssetDetails")
                        transaction.commitAllowingStateLoss()
                    } ?: run {
                        onComplete(false)
                        CustomToastMsg.showCustomToast(requireActivity(), "No assets found")
                    }
                }
            } else {
                onComplete(false)
                CustomToastMsg.showCustomToast(requireActivity(),
                    "Token authentication failed. Try again.")
            }

    }
}


