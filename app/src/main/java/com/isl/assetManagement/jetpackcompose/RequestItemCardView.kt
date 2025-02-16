package com.isl.assetManagement.jetpackcompose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.isl.assetManagement.room.entity.AssetRequests
import com.isl.assetManagement.utils.Util
import infozech.itower.R


@Composable
fun RequestItemCardView(item: AssetRequests, onClick: () -> Unit, level: HashMap<String, String> = hashMapOf()) {
    Card(
        modifier = Modifier
            .fillMaxWidth() // Ensures the Card takes the full width
            .padding(7.dp)  // Padding outside the Card
            .shadow(0.dp)  // Shadow for elevation effect
            .clickable { // Handling the click
                onClick()  // Trigger the passed onClick action
            }

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth() // Ensures Row takes full width
                .background(colorResource(id = R.color.card_bg_color)), // Row background to white
            horizontalArrangement = Arrangement.SpaceBetween, // Distribute space evenly
            verticalAlignment = Alignment.CenterVertically // Align vertically to center
        ) {
            // Left column with 3 texts
            Column(
                modifier = Modifier.padding(16.dp),  // Apply padding only to Column content
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth() // Ensures the Row takes up the full width
                ) {

                    Text(
                        text = item.requestId,
                        style = TextStyle(
                            fontSize = 12.sp,
                            color = colorResource(id = R.color.id_color)
                        ),
                        modifier = Modifier.weight(1f),
                        maxLines = 1, // Limits the text to a single line
                        overflow = TextOverflow.Ellipsis, // Adds the ellipsis when the text overflows
                    )
                    level[item.requestStatus]?.let {
                        Text(
                            //text = item.requestStatus,
                            text = it,
                            style = TextStyle(
                                fontSize = 11.sp,
                                color = colorResource(id = R.color.status_color)
                            ),
                            modifier = Modifier
                                .background(
                                    color = colorResource(id = R.color.status_bg_color),
                                    shape = RoundedCornerShape(8.dp) // Adjust the corner radius as needed
                                )
                                .padding(5.dp) // Optional, adds padding around the text
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start // Aligning everything to the left
                ) {
                    Text(
                        text = item.fromLocation,
                        style = TextStyle(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorResource(id = R.color.from_to_color)
                        )
                    )
                    Spacer(modifier = Modifier.width(0.dp)) // Optional space between text and image
                    Icon(
                        imageVector = Icons.Filled.ArrowForward, // Default back arrow icon
                        contentDescription = "Back",
                        tint = colorResource(id = R.color.from_to_color) // Color for the arrow icon
                    )
                    Spacer(modifier = Modifier.width(0.dp)) // Optional space between icon and text
                    Text(
                        text = item.toLocation,
                        style = TextStyle(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorResource(id = R.color.from_to_color)
                        )
                    )

                    // Spacer to create some space between the last Text and the "Assets" text
                    Spacer(modifier = Modifier.weight(1f)) // This will push the "Assets" text to the far right

                    /*Text(
                        text = "Assets : ${item.totalAssetCount}",
                        style = TextStyle(
                            fontSize = 11.sp,
                            color = colorResource(id = R.color.count_grid_color)
                        )
                    )*/
                }

                Text(
                    text = "Assets : ${item.totalAssetCount}",
                    style = TextStyle(
                        fontSize = 11.sp,
                        color = colorResource(id = R.color.category_color)
                    )
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth() // Ensures the Row takes up the full width
                ) {
                    Text(
                        text = item.reasonCategory,
                        style = TextStyle(
                            fontSize = 12.sp,
                            color = colorResource(id = R.color.category_color)
                        ),
                        modifier = Modifier.weight(1f),
                        maxLines = 1, // Limits the text to a single line
                        overflow = TextOverflow.Ellipsis, // Adds the ellipsis when the text overflows
                    )
                    level["dateFormate"]?.let {
                        Text(
                            text = Util.convertDate(item.requestDate,it),
                            style = TextStyle(
                                fontSize = 11.sp,
                                color = colorResource(id = R.color.date_color)
                            )
                        )
                    }
                }


                Text(
                    text = item.reasonSubCategory,
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = colorResource(id = R.color.category_color)
                    )
                )

            }
        }
    }
}
