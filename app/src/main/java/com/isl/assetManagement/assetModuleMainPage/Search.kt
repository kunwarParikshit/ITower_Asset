@file:OptIn(ExperimentalMaterialApi::class)
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.isl.assetManagement.requests.SearchTaskRequest
import infozech.itower.R
import java.util.*
import androidx.compose.foundation.*
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.Dp
import com.google.android.material.bottomsheet.BottomSheetBehavior

class Search : BottomSheetDialogFragment() {

    interface SearchListener {
        fun onSearchRequest(searchRequest: SearchTaskRequest)
    }

    private var searchListener: SearchListener? = null

    fun setSearchListener(listener: SearchListener) {
        searchListener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
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
                // Set the maximum height to 90% screen height
                layoutParams.height = seventyPercentHeight
                layoutParams = layoutParams

            }
        }
        dialog.setContentView(
            ComposeView(requireContext()).apply {
                setContent {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(topStart = 16.dp,topEnd = 16.dp,
                                bottomStart = 0.dp,bottomEnd = 0.dp))
                            .background(Color.White) // Apply a background color
                    ) {
                        SearchScreen(
                            onSearch = { searchRequest ->
                                searchListener?.onSearchRequest(searchRequest)
                                dismiss()
                            },
                            onReset = { dismiss() },
                            onClose = { dismiss() }  // Handle close action

                        )
                    }
                }
            }
        )
        return dialog
    }


}

@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SearchScreen(
    onSearch: (SearchTaskRequest) -> Unit,
    onReset: () -> Unit,
    onClose: () -> Unit
) {

    var requestId by remember { mutableStateOf("") }


    // State to manage dropdown expanded status
    var expanded by remember { mutableStateOf(false) }

    // State to hold the selected value
    var selectedOption by remember { mutableStateOf("Select") }

    // List of options
    val options = listOf("Option 1", "Option 2", "Option 3","Option 1", "Option 2", "Option 3",
        "Option 1", "Option 2", "Option 3","Option 1", "Option 2", "Option 3","Option 1", "Option 2", "Option 3")
    // Define common suggestions list
    val allSuggestions = listOf(
        "Apple", "Banana", "Orange", "Peach", "Pineapple",
        "Strawberry", "Grapes", "Mango", "Watermelon", "Blueberry",
        "Apple1", "Apple2", "Apple3", "Apple4", "Apple5", "Apple6", "Apple7"
    )

    // Separate state for 'From' and 'To' location queries
    var fromLocationQuery by remember { mutableStateOf("") }
    var toLocationQuery by remember { mutableStateOf("") }

    // State for filtered suggestions (can be shared)
    var fromLocationFilteredSuggestions by remember { mutableStateOf<List<String>>(emptyList()) }
    var toLocationFilteredSuggestions by remember { mutableStateOf<List<String>>(emptyList()) }

    // Filter suggestions for 'From Location'
    LaunchedEffect(fromLocationQuery) {
        fromLocationFilteredSuggestions = if (fromLocationQuery.length >= 3) {
            allSuggestions.filter { it.contains(fromLocationQuery, ignoreCase = true) }
        } else {
            emptyList() // Clear suggestions if input is less than 3 characters
        }
    }

    // Filter suggestions for 'To Location'
    LaunchedEffect(toLocationQuery) {
        toLocationFilteredSuggestions = if (toLocationQuery.length >= 3) {
            allSuggestions.filter { it.contains(toLocationQuery, ignoreCase = true) }
        } else {
            emptyList() // Clear suggestions if input is less than 3 characters
        }
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(
                        RoundedCornerShape(
                            topStart = 16.dp
                        )
                    ) // Rounded corners for the header
                    .background(colorResource(id = R.color.white))
                    .padding(start = 20.dp, end = 12.dp, top = 17.dp)
                    .shadow(
                        elevation = 32.dp, // Adjust elevation for blur intensity (equivalent to blur effect)
                        shape = RoundedCornerShape(topEnd = 16.dp),
                        ambientColor = Color.Black.copy(alpha = 0.15f), // Custom shadow color
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
                        .clickable { onClose() } // Invoke close action when clicked

                )
            }
            Divider(
                color = Color.Gray.copy(alpha = 0.1f), // Subtle shadow line effect
                thickness = 1.dp
            )
        },
        bottomBar = {

            Surface(
                elevation = 4.dp, // Adds elevation for shadow effect
                modifier = Modifier.fillMaxWidth()
            ) {
                Divider(
                    color = Color.Gray.copy(alpha = 0.1f), // Subtle shadow line effect
                    thickness = 1.dp
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colorResource(id = R.color.white))
                        .height(90.dp)
                        .padding(horizontal = 0.dp) // Padding around the buttons

                        .shadow(
                            elevation = 32.dp, // Adjust elevation for blur intensity (equivalent to blur effect)
                            shape = RoundedCornerShape(topEnd = 16.dp),
                            ambientColor = Color.Black.copy(alpha = 0.15f), // Custom shadow color
                            spotColor = Color.Black.copy(alpha = 0.15f)
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .padding(
                                start = 20.dp, // Left margin
                                end = 20.dp, // Right margin
                                top = 10.dp, // Top margin
                                bottom = 25.dp // Bottom margin
                            )// Fixed at the bottom
//                        .horizontalArrangement(Arrangement.SpaceBetween)
                    ) {
//                    Button(
//                        onClick = { onReset() },
//                        modifier = Modifier.weight(1f) // Make "Reject" button take equal width
//                    ) {
//                        Text("Reject")
//                    }
                        OutlinedButton(
                            onClick = { onReset() },
                            modifier = Modifier.weight(1f).height(42.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = colorResource(id = R.color.btn) // Text color for the outlined button
                            ),
                            border = BorderStroke(
                                1.dp,
                                colorResource(id = R.color.btn)
                            ) // Outline color for the button
                        ) {
                            Text("Reset", color = colorResource(id = R.color.btn))
                        }

                        Spacer(modifier = Modifier.width(24.dp)) // Space between buttons
                        Button(
                            onClick = {
                                val searchRequest = SearchTaskRequest(
                                    requestId = "requestId",
                                    fromLocation = "fromLocation",
                                    toLocation = "toLocation",
                                    requestStatus = "requestStatus",
                                    fromDate = "",
                                    toDate = ""
                                )
                                onSearch(searchRequest)
                            },
                            modifier = Modifier.weight(1f).height(42.dp),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = colorResource(id = R.color.btn), // Button background color
                                contentColor = Color.White // Text color for the button
                            )// Make "Submit" button take equal width
                        ) {
                            Text("Update")
                        }
                    }
                }

            }
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
                        .padding(start = 20.dp, end = 20.dp, top = 23.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    item () {
                        SearchDDL(
                            caption = "From Location",
                            query = fromLocationQuery,
                            onQueryChange = { newQuery -> fromLocationQuery = newQuery }, // Update the query
                            filteredSuggestions = fromLocationFilteredSuggestions,
                            onSuggestionSelected = { selectedSuggestion ->
                            fromLocationQuery = selectedSuggestion // Set the query to the selected suggestion

                            }
                        )
                    }

                    item () {
                        SearchDDL(
                            caption = "To Location",
                            query = toLocationQuery,
                            onQueryChange = { newQuery -> toLocationQuery = newQuery }, // Update the query
                            filteredSuggestions = toLocationFilteredSuggestions,
                            onSuggestionSelected = { selectedSuggestion ->
                            toLocationQuery = selectedSuggestion // Set the query to the selected suggestion

                            }
                        )
                    }

                    item () {
                        // Input Fields
                        Text(
                            text = "Request Id",
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
                                value = requestId,
                                onValueChange = { requestId = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp, start = 12.dp),
                                textStyle = TextStyle(
                                    color = colorResource(id = R.color.input_box_text_color),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Normal
                                ),
                                singleLine = true, // Ensure single-line input*/
                                cursorBrush = SolidColor(colorResource(id = R.color.input_box_text_color)) // Sets the cursor color
                            )
                        }
                    }

                    item () {
                        Text(
                            text = "Request Status",
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
                            // Spinner (Dropdown)
                            ExposedDropdownMenuBox(
                                expanded = expanded,
                                onExpandedChange = { expanded = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(47.dp) // Set fixed height
                                    .background(
                                        color = colorResource(id = R.color.input_box), // Background color
                                        shape = RoundedCornerShape(4.dp) // Rounded corners
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = colorResource(id = R.color.input_box_border),
                                        shape = RoundedCornerShape(4.dp) // Border with rounded corners
                                    )
                                    .padding(0.dp) // Remove padding inside the box

                            ) {
                                // Text showing the selected value
                                BasicTextField(
                                    value = selectedOption,
                                    onValueChange = { selectedOption = it },
                                    readOnly = true, // Make the TextField read-only to show the selected item
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 12.dp, start = 12.dp),

                                    textStyle = TextStyle(
                                        color = colorResource(id = R.color.input_box_text_color),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Normal
                                    )
                                )

                                // ExposedDropdownMenu to display the list of options with Radio Buttons
                                ExposedDropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    options.forEach { option ->
                                        DropdownMenuItem(
                                            onClick = {
                                                selectedOption = option // Update selected value
                                                expanded =
                                                    false // Close the dropdown after selection
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 0.dp), // Ensures the item spans full width
                                            contentPadding = PaddingValues(0.dp) // Removes default padding
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(start = 12.dp)
                                            ) {
                                                CustomRadioButton(
                                                    selected = option == selectedOption,
                                                    onClick = {
                                                        selectedOption = option
                                                        expanded = false
                                                    },
                                                    selectedColor = colorResource(id = R.color.input_box_text_color), // Customize color
                                                    unselectedColor = colorResource(id = R.color.input_box_border),
                                                    size = 20.dp // Customize size
                                                )
                                                Spacer(modifier = Modifier.width(0.dp))
                                                BasicText(
                                                    text = option,
                                                    /*modifier = Modifier.fillMaxWidth()
                                                    .padding(top=12.dp, start = 12.dp)
                                                    .height(47.dp),*/
                                                    modifier = Modifier
                                                        .align(Alignment.CenterVertically)
                                                        .padding(start = 12.dp),
                                                    style = TextStyle(
                                                        color = colorResource(id = R.color.input_box_text_color),
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.Normal
                                                    )
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // Drop-down Icon
                            Icon(
                                painter = painterResource(id = R.drawable.keyboard_arrow_down), // Replace with your drawable resource
                                contentDescription = "Drop-down",
                                tint = colorResource(id = R.color.tint_ddl), // Optional tint
                                modifier = Modifier
                                    .align(Alignment.CenterEnd) // Align to the right side
                                    .padding(end = 8.dp) // Adjust padding as needed
                                    .size(24.dp) // Adjust icon size
                            )
                        }
                    }

                    item () {
                        // Input Fields
                        Text(
                            text = "From Date",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                color = colorResource(id = R.color.label)
                            ),
                            modifier = Modifier.padding(top = 10.dp, bottom = 2.dp)
                        )
                        FromDatePicker()

                    }

                    item () {
                        // Input Fields
                        Text(
                            text = "To Date",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                color = colorResource(id = R.color.label)
                            ),
                            modifier = Modifier.padding(top = 10.dp, bottom = 2.dp)
                        )
                        ToDatePicker()
                    }

                }//end of main column
            }//end of box first
        } //end of Scaffold
    ) //end of Scaffold
} //end of function

@Composable
fun CustomRadioButton(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selectedColor: Color = MaterialTheme.colors.primary,
    unselectedColor: Color = Color.Gray,
    size: Dp = 20.dp,
    innerCircleSizeFraction: Float = 0.6f
)
{
    Box(
        modifier = modifier
            .size(size)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        // Outer circle
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = if (selected) selectedColor else unselectedColor,
                style = Stroke(width = size.toPx() / 10)
            )
        }

        // Inner circle (only visible when selected)
        if (selected) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    color = selectedColor,
                    radius = (size.toPx() * innerCircleSizeFraction) / 2
                )
            }
        }
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchDDL(
    caption: String,
    query: String,
    onQueryChange: (String) -> Unit,
    filteredSuggestions: List<String>,
    onSuggestionSelected: (String) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var isFocused by remember { mutableStateOf(false) }
    var showSuggestions by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = caption,
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = colorResource(id = R.color.label),
            ),
            modifier = Modifier.padding(top = 10.dp,bottom = 8.dp)
        )


        // Input Box with Suggestions
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column {

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

                    // Input Field
                    BasicTextField(
                        value = query,
                        onValueChange = { newValue ->
                            onQueryChange(newValue)
                            showSuggestions = newValue.length >= 3 // Show suggestions for 3+ chars
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp, start = 12.dp)
                            .onFocusChanged { focusState ->
                                isFocused = focusState.isFocused
                                showSuggestions = focusState.isFocused && query.length >= 4
                            },
                        textStyle = TextStyle(
                            color = colorResource(id = R.color.input_box_text_color),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal
                        ),
                        singleLine = true,
                        cursorBrush = SolidColor(colorResource(id = R.color.input_box_text_color))
                    )

                    // Drop-down Icon
                    Icon(
                        painter = painterResource(id = R.drawable.keyboard_arrow_down), // Replace with your drawable resource
                        contentDescription = "Drop-down",
                        tint = colorResource(id = R.color.tint_ddl), // Optional tint
                        modifier = Modifier
                            .align(Alignment.CenterEnd) // Align to the right side
                            .padding(end = 8.dp) // Adjust padding as needed
                            .size(24.dp) // Adjust icon size
                    )
                }

                // Suggestions List
                if (showSuggestions && filteredSuggestions.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = colorResource(id = R.color.white),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = colorResource(id = R.color.input_box_border),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .heightIn(max = 180.dp) // Limit the height of the dropdown
                    ) {
                        items(filteredSuggestions) { suggestion ->
                            Text(
                                text = suggestion,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onSuggestionSelected(suggestion)
                                        focusManager.clearFocus()
                                        keyboardController?.hide()
                                        showSuggestions = false
                                    }
                                    .padding(12.dp),
                                style = TextStyle(
                                    color = colorResource(id = R.color.input_box_text_color),
                                    fontSize = 14.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FromDatePicker() {
    // State to hold the selected date
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    var selectedDate by remember { mutableStateOf("") }

    // Show DatePickerDialog with custom theme
    val datePickerDialog = DatePickerDialog(
        context,
        R.style.DatePickerDialogTheme, // Optional custom theme for better visibility
        { _, selectedYear, selectedMonth, selectedDayOfMonth ->
            selectedDate = "$selectedDayOfMonth/${selectedMonth + 1}/$selectedYear"
        },
        year,
        month,
        day
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
        Text(
            text = if (selectedDate.isEmpty()) "" else selectedDate,
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color =  colorResource(id = R.color.input_box_text_color),
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(47.dp)
                .padding(top = 12.dp, start = 12.dp)
                .clickable { datePickerDialog.show() }
        )

        // calender Icon
        Icon(
            painter = painterResource(id = R.drawable.icon_calender), // Replace with your drawable resource
            contentDescription = "Drop-down",
            tint = colorResource(id = R.color.tint_calender), // Optional tint
            modifier = Modifier
                .align(Alignment.CenterEnd) // Align to the right side
                .padding(end = 8.dp) // Adjust padding as needed
                .size(16.dp) // Adjust icon size
        )

    }


}

@Composable
fun ToDatePicker() {
    // State to hold the selected date
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    var selectedDate by remember { mutableStateOf("") }

    // Show DatePickerDialog with custom theme
    val datePickerDialog = DatePickerDialog(
        context,
        R.style.DatePickerDialogTheme, // Optional custom theme for better visibility
        { _, selectedYear, selectedMonth, selectedDayOfMonth ->
            selectedDate = "$selectedDayOfMonth/${selectedMonth + 1}/$selectedYear"
        },
        year,
        month,
        day
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
        Text(
            text = if (selectedDate.isEmpty()) "" else selectedDate,
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color =  colorResource(id = R.color.input_box_text_color),
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(47.dp)
                .padding(top = 12.dp, start = 12.dp)
                .clickable { datePickerDialog.show() }
        )

        // calender Icon
        Icon(
            painter = painterResource(id = R.drawable.icon_calender), // Replace with your drawable resource
            contentDescription = "Drop-down",
            tint = colorResource(id = R.color.tint_calender), // Optional tint
            modifier = Modifier
                .align(Alignment.CenterEnd) // Align to the right side
                .padding(end = 8.dp) // Adjust padding as needed
                .size(16.dp) // Adjust icon size
        )

    }
}









