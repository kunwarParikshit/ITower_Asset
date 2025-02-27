@file:OptIn(ExperimentalMaterialApi::class)

package com.isl.assetManagement.jetpackcompose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.isl.assetManagement.responses.Timeline
import infozech.itower.R
import kotlinx.coroutines.delay
import java.util.Calendar

object JetpackUIs {
    object TimeLine {
        @Composable
        fun TimelineScreen(timelines: List<Timeline>?) {
            timelines ?: return
            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 16.dp, top = 16.dp, bottom = 16.dp, end = 16.dp)
                ) {
                    itemsIndexed(timelines) { _, timeline ->
                        TimelineItem(
                            timeline, timeline.status == "Approved"
                                    || timeline.status == "NewRequest"
                        )
                    }
                }
            }
        }

        @Composable
        private fun TimelineItem(timeline: Timeline, isApproved: Boolean) {
            val lineColor = if (isApproved) Color(0xFFE36C27) else Color.LightGray
            Box(modifier = Modifier.fillMaxWidth()) {
                Canvas(modifier = Modifier.matchParentSize()) {
                    val lineX = 12.dp.toPx()
                    drawLine(
                        color = lineColor,
                        start = Offset(x = lineX, y = 0f),
                        end = Offset(x = lineX, y = size.height),
                        strokeWidth = 2.dp.toPx()
                    )
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .width(24.dp)
                            .fillMaxHeight()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .align(Alignment.Center)
                                .background(color = lineColor, shape = CircleShape)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = timeline.stage,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorResource(id = R.color.tab_unselect_text_color)
                        )
                        timeline.user?.let { user ->
                            Text(
                                text = "User: $user",
                                fontSize = 12.sp,
                                color = colorResource(id = R.color.color_717E95)
                            )
                        }
                        Text(
                            text = "Status: ${timeline.status}",
                            fontSize = 12.sp,
                            color = colorResource(id = R.color.color_717E95)
                        )

                        Text(
                            text = "Date: ${timeline.timestamp}",
                            fontSize = 12.sp,
                            color = colorResource(id = R.color.color_717E95)
                        )
                        /*Text(
                            text = "Location: (${timeline.latitude}, ${timeline.longitude})",
                            style = MaterialTheme.typography.body2,
                            color = colorResource(id = R.color.color_717E95)
                        )*/
                    }
                }
            }
        }
    }

    object Common {

        @Composable
        fun DynamicComposeSpinner(
            labelName: String,
            selectedOption: String?,
            onOptionSelected: (String) -> Unit,
            onTextChanged: (String) -> Unit, // New callback for text change
            fetchOptions: suspend (String) -> List<String>,
            dynamicDataAfter: Int = 4
        ) {
            var expanded by remember { mutableStateOf(false) }
            var searchQuery by remember { mutableStateOf(selectedOption ?: "") }
            var options by remember { mutableStateOf<List<String>>(emptyList()) }

            LaunchedEffect(searchQuery) {
                if (searchQuery.length >= dynamicDataAfter) {
                    delay(300)
                    options = fetchOptions(searchQuery)
                }
            }

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = labelName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = colorResource(id = R.color.input_box_text_color),
                    modifier = Modifier.padding(bottom = 4.dp)
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
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it }
                    ) {
                        BasicTextField(
                            value = searchQuery,
                            onValueChange = { newText ->
                                searchQuery = newText
                                onTextChanged(newText) // Notify caller about text change
                                expanded = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            textStyle = TextStyle(
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
                                        searchQuery = option
                                        onOptionSelected(option)
                                        expanded = false
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    contentPadding = PaddingValues(12.dp)
                                ) {
                                    Text(
                                        text = option,
                                        modifier = Modifier.fillMaxWidth(),
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

                    Icon(
                        painter = painterResource(id = R.drawable.keyboard_arrow_down),
                        contentDescription = "Drop-down",
                        tint = colorResource(id = R.color.tint_ddl),
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 8.dp)
                            .size(24.dp)
                            .clickable { expanded = true }
                    )
                }
            }
        }



        @Composable
        fun ComposeSpinner(
            labelName: String,
            options: List<String>,
            selectedOption: String?,
            onOptionSelected: (String) -> Unit,
            isEditable: Boolean = false
        ) {
            var expanded by remember { mutableStateOf(false) }
            var textValue by remember { mutableStateOf(selectedOption) }

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = labelName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = colorResource(id = R.color.input_box_text_color),
                    modifier = Modifier.padding(bottom = 4.dp)
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
                    // Spinner (Dropdown)
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it }
                    ) {
                        BasicTextField(
                            value = textValue ?: "",
                            onValueChange = { newText ->
                                if (isEditable) textValue = newText
                            },
                            readOnly = !isEditable,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            textStyle = TextStyle(
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
                                        textValue = option
                                        onOptionSelected(option)
                                        expanded = false
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    contentPadding = PaddingValues(12.dp)
                                ) {
                                    Text(
                                        text = option,
                                        modifier = Modifier.fillMaxWidth(),
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

                    Icon(
                        painter = painterResource(id = R.drawable.keyboard_arrow_down), // Dropdown icon
                        contentDescription = "Drop-down",
                        tint = colorResource(id = R.color.tint_ddl),
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 8.dp)
                            .size(24.dp)
                            .clickable { expanded = true } // Opens dropdown when clicked
                    )
                }
            }
        }


        @Composable
        fun ComposeTextBox(
            labelName: String,
            text: String?,
            onTextChange: (String) -> Unit,
            isEditable: Boolean = true
        ) {
            var textValue by remember { mutableStateOf(text) }

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = labelName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = colorResource(id = R.color.input_box_text_color),
                    modifier = Modifier.padding(bottom = 4.dp)
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
                        value = textValue ?: "",
                        onValueChange = { newText ->
                            if (isEditable) {
                                textValue = newText
                                onTextChange(newText)
                            }
                        },
                        readOnly = !isEditable, // Set read-only based on isEditable
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        textStyle = TextStyle(
                            color = colorResource(id = R.color.input_box_text_color),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal
                        )
                    )
                }
            }
        }

        @Composable
        fun ComposeDatePicker(
            labelName: String,
            selectedDate: String?,
            onDateSelected: (String) -> Unit
        ) {
            val context = LocalContext.current
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            var dateValue by remember { mutableStateOf(selectedDate ?: "") }

            val datePickerDialog = android.app.DatePickerDialog(
                context,
                R.style.DatePickerDialogTheme, // Optional custom theme
                { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                    val newDate = "$selectedDayOfMonth/${selectedMonth + 1}/$selectedYear"
                    dateValue = newDate
                    onDateSelected(newDate)
                },
                year,
                month,
                day
            )

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = labelName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = colorResource(id = R.color.input_box_text_color),
                    modifier = Modifier.padding(bottom = 4.dp)
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
                        .clickable { datePickerDialog.show() } // Show DatePicker on click
                ) {
                    Text(
                        text = if (dateValue.isEmpty()) "Select date" else dateValue,
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = colorResource(id = R.color.input_box_text_color),
                        ),
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 12.dp)
                    )

                    Icon(
                        painter = painterResource(id = R.drawable.icon_calender),
                        contentDescription = "Calendar",
                        tint = colorResource(id = R.color.tint_calender),
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 8.dp)
                            .size(16.dp)
                    )
                }
            }
        }


    }
}
