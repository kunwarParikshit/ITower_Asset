package com.isl.assetManagement.jetpackcompose
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Tab
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import infozech.itower.R

@Composable
fun DetailsTab(tabItems: List<String>,
                  selectedTabIndex: MutableState<Int>,
                  onTabSelected: (Int) -> Unit) {
    ScrollableTabRow(
        selectedTabIndex = selectedTabIndex.value,
        backgroundColor = colorResource(id = R.color.white), // Set your background color
        contentColor = colorResource(id = R.color.tab_unselect_text_color), // Unselected tab text color
        modifier = Modifier.fillMaxWidth(),
        edgePadding = 16.dp

    ) {
        tabItems.forEachIndexed { index, title ->
            Tab(
                selected = selectedTabIndex.value == index,
                onClick = { onTabSelected(index) },
                modifier = Modifier
                    .padding(horizontal = 5.dp, vertical = 0.dp) // Adjust spacing for each tab
                    .height(40.dp) // Consistent height for all tabs
            ) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 6.dp), // Inner padding for tab content
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        style = TextStyle(fontSize = 13.sp),
                        fontWeight = if (selectedTabIndex.value == index)
                            FontWeight.Bold
                        else
                            FontWeight.Normal,

                        color = if (selectedTabIndex.value == index)
                            colorResource(id = R.color.from_to_color)
                        else
                            colorResource(id = R.color.label)
                    )
                }
            }
        }
    }
}


