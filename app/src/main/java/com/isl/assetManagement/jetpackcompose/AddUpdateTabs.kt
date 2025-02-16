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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import infozech.itower.R

@Composable
fun AddUpdateTabs(tabItems: List<String>,
                  selectedTabIndex: MutableState<Int>,
                  onTabSelected: (Int) -> Unit) {

        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex.value,
            modifier = Modifier
                .fillMaxWidth()
                .height(85.dp)// height is 85-11-11=63 (remove padding height)
                .padding(start = 5.dp, end = 5.dp, bottom = 5.dp, top = 11.dp), // Consistent padding for the ScrollableTabRow

            backgroundColor = colorResource(id = R.color.white),
            contentColor = colorResource(id = R.color.tab_unselect_text_color),
            edgePadding = 0.dp, // Removes unnecessary spacing at the edges
            indicator = {},
            divider = {}


        ) {
            tabItems.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex.value == index,
                    onClick = { onTabSelected(index) },
                    modifier = Modifier
                        .padding(horizontal = 5.dp, vertical = 0.dp) // Adjust spacing for each tab
                        .height(36.dp) // Consistent height for all tabs
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                if (selectedTabIndex.value == index)
                                    colorResource(id = R.color.tab_bg_selected) // Selected tab color
                                else
                                    colorResource(id = R.color.tab_bg_unselected), // Unselected tab color
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp), // Inner padding for tab content
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = title,
                            style = TextStyle(fontSize = 13.sp),
                            color = if (selectedTabIndex.value == index)
                                colorResource(id = R.color.tab_select_text_color)
                            else
                                colorResource(id = R.color.tab_unselect_text_color)
                        )
                    }
                }
            }
        }
}


