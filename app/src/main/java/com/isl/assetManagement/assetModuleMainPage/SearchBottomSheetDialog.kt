package com.isl.assetManagement.assetModuleMainPage

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import androidx.compose.ui.graphics.Color as ComposeColor

class SearchBottomSheetDialog : BottomSheetDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.behavior.apply {
            state = BottomSheetBehavior.STATE_EXPANDED
            skipCollapsed = true
        }
        bottomSheetDialog.setOnShowListener { dialog ->
            val d = dialog as BottomSheetDialog
            d.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            d.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        bottomSheetDialog.setContentView(
            ComposeView(requireContext()).apply {
                setContent {
                    // Use MaterialTheme if you have one
                    MaterialTheme {
                        SearchBottomSheetUI(
                            onClose = { dismiss() }
                        )
                    }
                }
            }
        )
        return bottomSheetDialog
    }
}

@Composable
fun SearchBottomSheetUI(
    onClose: () -> Unit = {}
) {
    // States
    var siteId by remember { mutableStateOf("") }
    var movementStatus by remember { mutableStateOf("") }
    var movementReason by remember { mutableStateOf("") }
    var movementSubReason by remember { mutableStateOf("") }
    var fromSiteId by remember { mutableStateOf("") }
    var toSiteId by remember { mutableStateOf("") }

    // Dimmed background behind the sheet is handled by BottomSheet automatically,
    // so we only need a Surface to shape the top corners.
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.95f), // take ~95% of screen height
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        color = ComposeColor.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            // Top bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Search",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                    color = ComposeColor.Black
                )
                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close",
                        tint = ComposeColor.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Dropdown Fields
            DropdownField("Site Id", siteId) { siteId = it }
            DropdownField("Movement Status", movementStatus) { movementStatus = it }
            DropdownField("Movement Reason", movementReason) { movementReason = it }
            DropdownField("Movement Sub Reason", movementSubReason) { movementSubReason = it }
            DropdownField("From Site Id", fromSiteId) { fromSiteId = it }
            DropdownField("To Site Id", toSiteId) { toSiteId = it }

            Spacer(modifier = Modifier.height(24.dp))

            // Bottom buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = { /* Reset Action */ },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.dp, ComposeColor.Gray),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = ComposeColor.Black
                    )
                ) {
                    Text("Reset")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { /* Show Results Action */ },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ComposeColor(0xFF6200EE), // Purple
                        contentColor = ComposeColor.White
                    )
                ) {
                    Text("Show Results (02)")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownField(
    label: String,
    selectedValue: String,
    onValueChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val displayText = if (selectedValue.isEmpty()) "Select" else selectedValue

    Column(modifier = Modifier.fillMaxWidth()) {
        // Top label
        Text(
            text = label,
            fontSize = 14.sp,
            color = ComposeColor.Gray
        )

        Spacer(modifier = Modifier.height(4.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp)) // round corners
        ) {
            // Use an OutlinedTextField but with a filled container color and no border
            OutlinedTextField(
                value = displayText,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                shape = RoundedCornerShape(8.dp),
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                interactionSource = remember { MutableInteractionSource() },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = ComposeColor(0xFFF2E6F9), // Light purple/gray background
                    unfocusedContainerColor = ComposeColor(0xFFF2E6F9),
                    disabledContainerColor = ComposeColor(0xFFF2E6F9),
                    focusedIndicatorColor = ComposeColor.Transparent, // Hide underline
                    unfocusedIndicatorColor = ComposeColor.Transparent,
                    disabledIndicatorColor = ComposeColor.Transparent,
                    focusedTextColor = ComposeColor.Black, // ✅ Correct property
                    unfocusedTextColor = ComposeColor.Black,
                    disabledTextColor = ComposeColor.Gray,
                    unfocusedPlaceholderColor = ComposeColor.Gray, // ✅ Correct property
                    focusedPlaceholderColor = ComposeColor.Gray
                )
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                listOf("Option 1", "Option 2", "Option 3").forEach { selection ->
                    DropdownMenuItem(
                        text = { Text(selection) },
                        onClick = {
                            onValueChange(selection)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
    Spacer(modifier = Modifier.height(12.dp))

}

@Preview(showBackground = true)
@Composable
fun PreviewSearchBottomSheetUI() {
    val context = LocalContext.current
    MaterialTheme {
        SearchBottomSheetUI()
    }
}
