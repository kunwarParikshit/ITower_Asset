@file:OptIn(ExperimentalMaterialApi::class)

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.isl.assetManagement.dataViewModel.RoomViewModel
import com.isl.assetManagement.jetpackcompose.JetpackUIs.Common.ComposeDatePicker
import com.isl.assetManagement.jetpackcompose.JetpackUIs.Common.ComposeSpinner
import com.isl.assetManagement.jetpackcompose.JetpackUIs.Common.ComposeTextBox
import com.isl.assetManagement.jetpackcompose.JetpackUIs.Common.DynamicComposeSpinner
import com.isl.assetManagement.requests.SearchTaskRequest
import com.isl.assetManagement.room.repository.RoomRepository
import com.isl.assetManagement.utils.CustomToastMsg
import com.isl.assetManagement.utils.DataViewModelFactory
import com.isl.assetManagement.utils.Util.Companion.isFirstDateGreater
import com.isl.itower.MyApp
import infozech.itower.R

private lateinit var viewModel: RoomViewModel
private lateinit var roomRepository: RoomRepository

class Search(private val callbackSearch: CallbackSearch) : BottomSheetDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), theme)
        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            val bottomSheet =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)

            bottomSheet?.apply {
                setBackgroundColor(android.graphics.Color.TRANSPARENT)
                val behavior = BottomSheetBehavior.from(this)
                val seventyPercentHeight = (resources.displayMetrics.heightPixels * 0.85).toInt()
                behavior.peekHeight = seventyPercentHeight
                behavior.isFitToContents = false
                behavior.state = BottomSheetBehavior.STATE_COLLAPSED
                behavior.isDraggable = false
                layoutParams.height = seventyPercentHeight
                layoutParams = layoutParams
            }
        }
        dialog.setContentView(ComposeView(requireContext()).apply {
            setContent {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(
                            RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 16.dp,
                                bottomStart = 0.dp,
                                bottomEnd = 0.dp
                            )
                        )
                        .background(Color.White)
                ) {
                    SearchScreen(
                        onSearch = { searchRequest, count ->
                            showResults(
                                fragment = this@Search,
                                searchRequest,
                                callbackSearch,
                                count
                            )
                            dismiss()
                        },
                        onReset = { dismiss() },
                        onClose = { dismiss() }
                    )
                }
            }
        })
        roomRepository = RoomRepository(MyApp.getAssetDatabase().dataDao())

        viewModel = ViewModelProvider(
            this, DataViewModelFactory(roomRepository)
        ).get(RoomViewModel::class.java)

        return dialog
    }
}

@Composable
fun SearchScreen(
    onSearch: (SearchTaskRequest, Int) -> Unit,
    onReset: () -> Unit,
    onClose: () -> Unit
) {
    var fromLocation by remember { mutableStateOf("") }
    var toLocation by remember { mutableStateOf("") }
    var requestId by remember { mutableStateOf("") }
    var requestStatus by remember { mutableStateOf("") }
    var fromDate by remember { mutableStateOf("") }
    var toDate by remember { mutableStateOf("") }

    val options = listOf("Option 1", "Option 2", "Option 3")
    val allSuggestions = listOf(
        "Apple", "Banana", "Orange", "Peach", "Pineapple", "Strawberry",
        "Grapes", "Mango", "Watermelon", "Blueberry", "Apple1", "Apple2",
        "Apple3", "Apple4", "Apple5", "Apple6", "Apple7"
    )
    var fromLocationQuery by remember { mutableStateOf("") }
    var toLocationQuery by remember { mutableStateOf("") }
    var fromLocationFilteredSuggestions by remember { mutableStateOf<List<String>>(emptyList()) }
    var toLocationFilteredSuggestions by remember { mutableStateOf<List<String>>(emptyList()) }

    LaunchedEffect(fromLocationQuery) {
        fromLocationFilteredSuggestions = if (fromLocationQuery.length >= 3) {
            allSuggestions.filter { it.contains(fromLocationQuery, ignoreCase = true) }
        } else {
            emptyList()
        }
    }

    LaunchedEffect(toLocationQuery) {
        toLocationFilteredSuggestions = if (toLocationQuery.length >= 3) {
            allSuggestions.filter { it.contains(toLocationQuery, ignoreCase = true) }
        } else {
            emptyList()
        }
    }

    val selectedCount = listOf(
        fromLocation,
        toLocation,
        requestStatus,
        requestId,
        fromDate,
        toDate
    ).count { it.isNotEmpty() }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 16.dp))
                    .background(colorResource(id = R.color.white))
                    .padding(start = 20.dp, end = 12.dp, top = 17.dp)
                    .shadow(
                        elevation = 32.dp,
                        shape = RoundedCornerShape(topEnd = 16.dp),
                        ambientColor = Color.Black.copy(alpha = 0.15f),
                        spotColor = Color.Black.copy(alpha = 0.15f)
                    )
                    .height(50.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Search",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = colorResource(id = R.color.search)
                    ),
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(vertical = 8.dp)
                )
                Icon(
                    painter = painterResource(id = R.drawable.grip_lines),
                    contentDescription = "grip lines",
                    tint = colorResource(id = R.color.from_to_color),
                    modifier = Modifier.align(Alignment.Center)
                )
                Icon(
                    painter = painterResource(id = R.drawable.close_icon),
                    contentDescription = "Close icon",
                    tint = colorResource(id = R.color.from_to_color),
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .clickable { onClose() }
                )
            }
            Divider(
                color = Color.Gray.copy(alpha = 0.1f),
                thickness = 1.dp
            )
        },
        bottomBar = {
            Surface(
                elevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Divider(
                    color = Color.Gray.copy(alpha = 0.1f),
                    thickness = 1.dp
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colorResource(id = R.color.white))
                        .height(90.dp)
                        .padding(horizontal = 0.dp)
                        .shadow(
                            elevation = 32.dp,
                            shape = RoundedCornerShape(topEnd = 16.dp),
                            ambientColor = Color.Black.copy(alpha = 0.15f),
                            spotColor = Color.Black.copy(alpha = 0.15f)
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 25.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onReset() },
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = colorResource(id = R.color.btn)
                            ),
                            border = BorderStroke(1.dp, colorResource(id = R.color.btn))
                        ) {
                            Text("Reset", color = colorResource(id = R.color.btn))
                        }
                        Spacer(modifier = Modifier.width(24.dp))
                        Button(
                            onClick = {
                                val searchRequest = SearchTaskRequest(
                                    requestId = requestId,
                                    fromLocation = fromLocation,
                                    toLocation = toLocation,
                                    requestStatus = requestStatus,
                                    fromDate = fromDate,
                                    toDate = toDate
                                )
                                onSearch(searchRequest, selectedCount)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = colorResource(id = R.color.btn),
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = if (selectedCount > 0) "Show Result ($selectedCount)" else "Show Results"
                            )
                        }
                    }
                }
            }
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 20.dp, end = 20.dp, top = 23.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        DynamicComposeSpinner(
                            labelName = "From Location",
                            selectedOption = fromLocation,
                            onOptionSelected = { newSelection -> fromLocation = newSelection },
                            onTextChanged = { fromLocation = "" },
                            fetchOptions = { query -> viewModel.getSites(query) },
                            dynamicDataAfter = 3
                        )
                    }
                    item {
                        DynamicComposeSpinner(
                            labelName = "To Location",
                            selectedOption = toLocation,
                            onOptionSelected = { newSelection -> toLocation = newSelection },
                            onTextChanged = { toLocation = "" },
                            fetchOptions = { query -> viewModel.getSites(query) },
                            dynamicDataAfter = 3
                        )
                    }
                    item {
                        ComposeTextBox(
                            labelName = "Request Id",
                            text = null,
                            onTextChange = { newText -> requestId = newText },
                            isEditable = true
                        )
                    }
                    item {
                        ComposeSpinner(
                            labelName = "Request Status",
                            options = options,
                            selectedOption = requestStatus,
                            onOptionSelected = { newSelection -> requestStatus = newSelection },
                            isEditable = false
                        )
                    }
                    item {
                        ComposeDatePicker(
                            labelName = "From Date",
                            selectedDate = fromDate,
                            onDateSelected = { newDate -> fromDate = newDate }
                        )
                    }
                    item {
                        ComposeDatePicker(
                            labelName = "To Date",
                            selectedDate = toDate,
                            onDateSelected = { newDate -> toDate = newDate }
                        )
                    }
                }
            }
        }
    )
}

private fun showResults(
    fragment: Fragment,
    searchRequest: SearchTaskRequest,
    callbackSearch: CallbackSearch,
    filterCount: Int
) {
    val context = fragment.context ?: return

    when {
        searchRequest.fromLocation.isNotEmpty() && searchRequest.fromLocation == searchRequest.toLocation -> CustomToastMsg.showCustomToast(
            context, "From Location and To Location cannot be the same!"
        )

        isFirstDateGreater(
            searchRequest.fromDate,
            searchRequest.toDate
        ) -> CustomToastMsg.showCustomToast(context, "From Date cannot be greater than To Date!")

        else -> {
            callbackSearch(searchRequest, filterCount)
            (fragment as? BottomSheetDialogFragment)?.dismiss()
        }
    }
}

typealias CallbackSearch = (SearchTaskRequest, Int) -> Unit



