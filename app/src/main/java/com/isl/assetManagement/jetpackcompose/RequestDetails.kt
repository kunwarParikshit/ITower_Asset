package com.isl.assetManagement.jetpackcompose

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.isl.assetManagement.constants.DefaultLevel
import com.isl.common.activities.MapActivity
import com.isl.assetManagement.room.entity.TaskDetailEntity
import com.isl.assetManagement.utils.Util
import infozech.itower.R

@SuppressLint("SuspiciousIndentation")
@Composable
fun RequestDetails(taskDetail: TaskDetailEntity?,mode : Int,context : Context){
    var map: HashMap<String, String> = hashMapOf()
    map = Util.stringToHashMap("" + DefaultLevel.msg()["assetStatus"])

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 20.dp, end = 20.dp, top = 33.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Scrollable content here
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth() // Ensures the Row takes up the full width
            ) {

                taskDetail?.requestId?.let {
                    Text(
                        text = it?: "",
                        style = TextStyle(
                            fontSize = 12.sp,
                            color = colorResource(id = R.color.id_color)
                        ),
                        modifier = Modifier.weight(1f),
                        maxLines = 1, // Limits the text to a single line
                        overflow = TextOverflow.Ellipsis, // Adds the ellipsis when the text overflows
                    )
                }

                // map[taskDetail?.status]?.let {
                Text(
                    text = map[taskDetail?.status?.
                    replace(" ","")].toString(),
                    //text = it?: "",
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
                //}

                /*taskDetail?.status?.let {
                    map[taskDetail!!.status]?.let
                    Text(
                        //text = item.requestStatus,
                        text = it,  //"Waiting Approval 2",
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
                }*/
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
                    .padding(top = 0.dp),

                ) {
                taskDetail?.fromLocation?.let {
                    Text(
                        text = it.siteId?: "",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorResource(id = R.color.from_to_color)
                        )
                    )
                }
                Spacer(modifier = Modifier.width(0.dp)) // Optional space between text and image
                Icon(
                    imageVector = Icons.Filled.ArrowForward, // Default back arrow icon
                    contentDescription = "Back",
                    tint = colorResource(id = R.color.from_to_color) // Color for the arrow icon
                )
                Spacer(modifier = Modifier.width(0.dp)) // Optional space between icon and text
                taskDetail?.toLocation?.let {
                    Text(
                        text = it.siteId ?: "",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorResource(id = R.color.from_to_color)
                        ),
                        maxLines = 1, // Limits the text to a single line
                        overflow = TextOverflow.Ellipsis, // Adds the ellipsis when the text overflows

                    )
                }

                // Spacer to create some space between the last Text and the "Assets" text
                /*Spacer(modifier = Modifier.weight(1f)) // This will push the "Assets" text to the far right

                androidx.compose.material3.Text(
                    text = "Requested On",
                    style = TextStyle(
                        fontSize = 11.sp,
                        color = colorResource(id = R.color.count_grid_color)
                    )
                )*/

            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start // Aligning everything to the left
            ) {
                taskDetail?.reasonCategory?.let {
                    Text(
                        text = it?: "",
                        style = TextStyle(
                            fontSize = 12.sp,
                            color = colorResource(id = R.color.category_color)
                        ),
                        modifier = Modifier.weight(1f),
                        maxLines = 1, // Limits the text to a single line
                        overflow = TextOverflow.Ellipsis, // Adds the ellipsis when the text overflows
                    )
                }

                // map["dateFormate"]?.let {
                val parts = taskDetail?.requestDate?.split("T")
                taskDetail?.requestDate?.let {
                    Text(
                        //text =it,
                        text = Util.convertDate1(parts?.get(0) ?: "",
                            map["dateFormate"].toString()),
                        //text = Util.convertDate(it, map["dateFormate"].toString()),
                        style = TextStyle(
                            fontSize = 11.sp,
                            color = colorResource(id = R.color.count_grid_color)
                        )
                    )
                }
                /*taskDetail?.requestDate?.let {
                    androidx.compose.material3.Text(
                        text = it?: "",
                        style = TextStyle(
                            fontSize = 11.sp,
                            color = colorResource(id = R.color.count_grid_color)
                        )
                    )
                }*/
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start // Aligning everything to the left
            ) {
                taskDetail?.reasonSubCategory?.let {
                    Text(
                        text = it?: "",
                        style = TextStyle(
                            fontSize = 12.sp,
                            color = colorResource(id = R.color.category_color)
                        ),
                        modifier = Modifier.weight(1f),
                        maxLines = 1, // Limits the text to a single line
                        overflow = TextOverflow.Ellipsis, // Adds the ellipsis when the text overflows
                    )
                }
                /*androidx.compose.material3.Text(
                    text = "Closed On",
                    style = TextStyle(
                        fontSize = 11.sp,
                        color = colorResource(id = R.color.count_grid_color)
                    )
                )*/
            }

            Spacer(modifier = Modifier.height(8.dp))
            Divider(
                color = colorResource(id = R.color.input_box_border),
                thickness = 1.dp,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            /*Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start // Aligning everything to the left
            ) {
                androidx.compose.material3.Text(
                    text = "Asset Stock",
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = colorResource(id = R.color.category_color)
                    ),
                    modifier = Modifier.weight(1f),
                    maxLines = 1, // Limits the text to a single line
                    overflow = TextOverflow.Ellipsis, // Adds the ellipsis when the text overflows
                )
                androidx.compose.material3.Text(
                    text = "01/02",
                    style = TextStyle(
                        fontSize = 11.sp,
                        color = colorResource(id = R.color.count_grid_color)
                    )
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start // Aligning everything to the left
            ) {
                androidx.compose.material3.Text(
                    text = "Vendor Name",
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = colorResource(id = R.color.category_color)
                    ),
                    modifier = Modifier.weight(1f),
                    maxLines = 1, // Limits the text to a single line
                    overflow = TextOverflow.Ellipsis, // Adds the ellipsis when the text overflows
                )
                androidx.compose.material3.Text(
                    text = "Ericsson & Co.",
                    style = TextStyle(
                        fontSize = 11.sp,
                        color = colorResource(id = R.color.count_grid_color)
                    )
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start // Aligning everything to the left
            ) {
                androidx.compose.material3.Text(
                    text = "Product Name",
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = colorResource(id = R.color.category_color)
                    ),
                    modifier = Modifier.weight(1f),
                    maxLines = 1, // Limits the text to a single line
                    overflow = TextOverflow.Ellipsis, // Adds the ellipsis when the text overflows
                )
                androidx.compose.material3.Text(
                    text = "-",
                    style = TextStyle(
                        fontSize = 11.sp,
                        color = colorResource(id = R.color.count_grid_color)
                    )
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start // Aligning everything to the left
            ) {
                androidx.compose.material3.Text(
                    text = "Project Name",
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = colorResource(id = R.color.category_color)
                    ),
                    modifier = Modifier.weight(1f),
                    maxLines = 1, // Limits the text to a single line
                    overflow = TextOverflow.Ellipsis, // Adds the ellipsis when the text overflows
                )
                androidx.compose.material3.Text(
                    text = "-",
                    style = TextStyle(
                        fontSize = 11.sp,
                        color = colorResource(id = R.color.count_grid_color)
                    )
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start // Aligning everything to the left
            ) {
                androidx.compose.material3.Text(
                    text = "SO Numeber",
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = colorResource(id = R.color.category_color)
                    ),
                    modifier = Modifier.weight(1f),
                    maxLines = 1, // Limits the text to a single line
                    overflow = TextOverflow.Ellipsis, // Adds the ellipsis when the text overflows
                )
                androidx.compose.material3.Text(
                    text = "SO987654",
                    style = TextStyle(
                        fontSize = 11.sp,
                        color = colorResource(id = R.color.count_grid_color)
                    )
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start // Aligning everything to the left
            ) {
                androidx.compose.material3.Text(
                    text = "Activity Id",
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = colorResource(id = R.color.category_color)
                    ),
                    modifier = Modifier.weight(1f),
                    maxLines = 1, // Limits the text to a single line
                    overflow = TextOverflow.Ellipsis, // Adds the ellipsis when the text overflows
                )
                androidx.compose.material3.Text(
                    text = "W987654",
                    style = TextStyle(
                        fontSize = 11.sp,
                        color = colorResource(id = R.color.count_grid_color)
                    )
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start // Aligning everything to the left
            ) {
                androidx.compose.material3.Text(
                    text = "Ticket ID",
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = colorResource(id = R.color.category_color)
                    ),
                    modifier = Modifier.weight(1f),
                    maxLines = 1, // Limits the text to a single line
                    overflow = TextOverflow.Ellipsis, // Adds the ellipsis when the text overflows
                )
                androidx.compose.material3.Text(
                    text = "Y567890",
                    style = TextStyle(
                        fontSize = 11.sp,
                        color = colorResource(id = R.color.count_grid_color)
                    )
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Divider(
                color = Color.Black,
                thickness = 1.dp,
                modifier = Modifier.fillMaxWidth()
            )*/
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start // Aligning everything to the left
            ) {
                Text(
                    text = "From Site Id",
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = colorResource(id = R.color.category_color)
                    ),
                    modifier = Modifier.weight(1f),
                    maxLines = 1, // Limits the text to a single line
                    overflow = TextOverflow.Ellipsis, // Adds the ellipsis when the text overflows
                )
                taskDetail?.fromLocation?.let {
                    Text(
                        text = it.siteId?: "",
                        style = TextStyle(
                            fontSize = 11.sp,
                            color = colorResource(id = R.color.count_grid_color)
                        )
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start // Aligning everything to the left
            ) {
                Text(
                    text = "Site Address",
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = colorResource(id = R.color.category_color)
                    ),
                    modifier = Modifier.weight(1f),
                    maxLines = 1, // Limits the text to a single line
                    overflow = TextOverflow.Ellipsis, // Adds the ellipsis when the text overflows
                )
                taskDetail?.fromLocation?.let {
                    Text(
                        text = it.address?: "",
                        style = TextStyle(
                            fontSize = 11.sp,
                            color = colorResource(id = R.color.count_grid_color)
                        )
                    )
                }
            }



            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start // Aligning everything to the left
            ) {
                Text(
                    text = "District",
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = colorResource(id = R.color.category_color)
                    ),
                    modifier = Modifier.weight(1f),
                    maxLines = 1, // Limits the text to a single line
                    overflow = TextOverflow.Ellipsis, // Adds the ellipsis when the text overflows
                )
                taskDetail?.fromLocation?.let {
                    Text(
                        text = it.locationLevel3?: "",
                        style = TextStyle(
                            fontSize = 11.sp,
                            color = colorResource(id = R.color.count_grid_color)
                        )
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start // Aligning everything to the left
            ) {
                Text(
                    text = "City",
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = colorResource(id = R.color.category_color)
                    ),
                    modifier = Modifier.weight(1f),
                    maxLines = 1, // Limits the text to a single line
                    overflow = TextOverflow.Ellipsis, // Adds the ellipsis when the text overflows
                )
                taskDetail?.fromLocation?.let {
                    Text(
                        text = it.locationLevel4?: "",
                        style = TextStyle(
                            fontSize = 11.sp,
                            color = colorResource(id = R.color.count_grid_color)
                        )
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start // Aligning everything to the left
            ) {
                Text(
                    text = "Lat/Long",
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = colorResource(id = R.color.category_color)
                    ),
                    modifier = Modifier.weight(1f),
                    maxLines = 1, // Limits the text to a single line
                    overflow = TextOverflow.Ellipsis, // Adds the ellipsis when the text overflows
                )
                Text(
                    // text = "21.56789/34.67890",
                    text = ""+taskDetail?.fromLocation?.latitude+"/"+taskDetail?.fromLocation?.longitude,
                    style = TextStyle(
                        fontSize = 11.sp,
                        color = colorResource(id = R.color.count_grid_color)
                    )
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween, // Keep distance and 23km aligned properly
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Distance",
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = colorResource(id = R.color.category_color)
                    ),
                    modifier = Modifier.weight(1f),
                    maxLines = 1, // Limits the text to a single line
                    overflow = TextOverflow.Ellipsis, // Adds the ellipsis when the text overflows
                )

                Text(
                    text = "23km",
                    style = TextStyle(
                        fontSize = 11.sp,
                        color = colorResource(id = R.color.count_grid_color)
                    )
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End, // Align to the right side
                modifier = Modifier.fillMaxWidth()
            ) {
                ClickableText(
                    text = AnnotatedString(
                        text = "Get Directions>",
                        spanStyles = listOf(
                            AnnotatedString.Range(
                                item = SpanStyle(
                                    fontSize = 11.sp,
                                    color = Color.Red,
                                    textDecoration = TextDecoration.Underline
                                ),
                                start = 0,
                                end = "Get Directions>".length
                            )
                        )
                    ),
                    modifier = Modifier.padding(top = 2.dp),
                    onClick = {
                        val intent = Intent(context, MapActivity::class.java)
                        intent.putExtra("LATITUDE", taskDetail?.fromLocation?.latitude)
                        intent.putExtra("LONGITUDE", taskDetail?.fromLocation?.longitude)
                        context.startActivity(intent)
                    }
                )
            }


            Spacer(modifier = Modifier.height(8.dp))
            Divider(
                color = colorResource(id = R.color.input_box_border),
                thickness = 1.dp,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start // Aligning everything to the left
            ) {
                Text(
                    text = "To Site Id",
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = colorResource(id = R.color.category_color)
                    ),
                    modifier = Modifier.weight(1f),
                    maxLines = 1, // Limits the text to a single line
                    overflow = TextOverflow.Ellipsis, // Adds the ellipsis when the text overflows
                )
                taskDetail?.toLocation?.let {
                    Text(
                        text = it.siteId?: "",
                        style = TextStyle(
                            fontSize = 11.sp,
                            color = colorResource(id = R.color.count_grid_color)
                        )
                    )
                }
            }
            /*Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start // Aligning everything to the left
            ) {
                androidx.compose.material3.Text(
                    text = "Site Address",
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = colorResource(id = R.color.category_color)
                    ),
                    modifier = Modifier.weight(1f),
                    // Limits the text to a single line
                    overflow = TextOverflow.Ellipsis, // Adds the ellipsis when the text overflows
                )
                taskDetail?.toLocation?.let {
                    androidx.compose.material3.Text(
                        text = it.address?: "",
                        style = TextStyle(
                            fontSize = 11.sp,
                            color = colorResource(id = R.color.count_grid_color)
                        ),
                        modifier = Modifier.fillMaxWidth(0.4f), // Adjust width constraint to allow wrapping
                        maxLines = Int.MAX_VALUE, // Allow unlimited lines
                        overflow = TextOverflow.Clip // Let text flow naturally to next line
                    )
                }
            }*/

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start // Aligning everything to the left
            ) {
                Text(
                    text = "Site Address",
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = colorResource(id = R.color.category_color)
                    ),
                    modifier = Modifier.weight(1f),
                    maxLines = 1, // Limits the text to a single line
                    overflow = TextOverflow.Ellipsis, // Adds the ellipsis when the text overflows
                )
                taskDetail?.toLocation?.let {
                    Text(
                        text = it.address?: "",
                        style = TextStyle(
                            fontSize = 11.sp,
                            color = colorResource(id = R.color.count_grid_color)
                        )
                    )
                }
            }


            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start // Aligning everything to the left
            ) {
                Text(
                    text = "District",
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = colorResource(id = R.color.category_color)
                    ),
                    modifier = Modifier.weight(1f),
                    maxLines = 1, // Limits the text to a single line
                    overflow = TextOverflow.Ellipsis, // Adds the ellipsis when the text overflows
                )
                taskDetail?.toLocation?.let {
                    Text(
                        text = it.locationLevel3?: "",
                        style = TextStyle(
                            fontSize = 11.sp,
                            color = colorResource(id = R.color.count_grid_color)
                        )
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start // Aligning everything to the left
            ) {
                Text(
                    text = "City",
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = colorResource(id = R.color.category_color)
                    ),
                    modifier = Modifier.weight(1f),
                    maxLines = 1, // Limits the text to a single line
                    overflow = TextOverflow.Ellipsis, // Adds the ellipsis when the text overflows
                )
                taskDetail?.toLocation?.let {
                    Text(
                        text = it.locationLevel4?: "",
                        style = TextStyle(
                            fontSize = 11.sp,
                            color = colorResource(id = R.color.count_grid_color)
                        )
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start // Aligning everything to the left
            ) {
                Text(
                    text = "Lat/Long",
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = colorResource(id = R.color.category_color)
                    ),
                    modifier = Modifier.weight(1f),
                    maxLines = 1, // Limits the text to a single line
                    overflow = TextOverflow.Ellipsis, // Adds the ellipsis when the text overflows
                )
                Text(
                    // text = "21.56789/34.67890",
                    text = ""+taskDetail?.toLocation?.latitude+"/"+taskDetail?.toLocation?.longitude,
                    style = TextStyle(
                        fontSize = 11.sp,
                        color = colorResource(id = R.color.count_grid_color)
                    )
                )
            }
             Row(
                 verticalAlignment = Alignment.CenterVertically,
                 horizontalArrangement = Arrangement.SpaceBetween, // Keep distance and 23km aligned properly
                 modifier = Modifier.fillMaxWidth()
             ) {
                 Text(
                     text = "Distance",
                     style = TextStyle(
                         fontSize = 12.sp,
                         color = colorResource(id = R.color.category_color)
                     ),
                     modifier = Modifier.weight(1f),
                     maxLines = 1, // Limits the text to a single line
                     overflow = TextOverflow.Ellipsis, // Adds the ellipsis when the text overflows
                 )

                    Text(
                         text = "23km",
                         style = TextStyle(
                             fontSize = 11.sp,
                             color = colorResource(id = R.color.count_grid_color)
                         )
                     )
             }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End, // Align to the right side
                modifier = Modifier.fillMaxWidth()
            ) {
                ClickableText(
                    text = AnnotatedString(
                        text = "Get Directions>",
                        spanStyles = listOf(
                            AnnotatedString.Range(
                                item = SpanStyle(
                                    fontSize = 11.sp,
                                    color = Color.Red,
                                    textDecoration = TextDecoration.Underline
                                ),
                                start = 0,
                                end = "Get Directions>".length
                            )
                        )
                    ),
                    modifier = Modifier.padding(top = 2.dp),
                    onClick = {
                        val intent = Intent(context, MapActivity::class.java)
                        intent.putExtra("LATITUDE", taskDetail?.toLocation?.latitude)
                        intent.putExtra("LONGITUDE", taskDetail?.toLocation?.longitude)
                        context.startActivity(intent)
                    }
                )
            }


            /* Row(
                 verticalAlignment = Alignment.CenterVertically,
                 horizontalArrangement = Arrangement.Start // Aligning everything to the left
             ) {
                 androidx.compose.material3.Text(
                     text = "Site Access & Compliance",
                     style = TextStyle(
                         fontSize = 12.sp,
                         color = colorResource(id = R.color.category_color)
                     ),
                     modifier = Modifier.weight(1f),
                     maxLines = 1, // Limits the text to a single line
                     overflow = TextOverflow.Ellipsis, // Adds the ellipsis when the text overflows
                 )
                 Column(
                     horizontalAlignment = Alignment.End // Align text to the right side within the column
                 ) {

                     Text(
                         text = "Pending",
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
                     ClickableText(
                         text = AnnotatedString(
                             text = "Get Directions>",
                             spanStyles = listOf(
                                 AnnotatedString.Range(
                                     item = SpanStyle(
                                         fontSize = 11.sp,
                                         color = Color.Red,
                                         textDecoration = TextDecoration.Underline
                                     ),
                                     start = 0,
                                     end = "Get Directions>".length
                                 )
                             )
                         ),
                         modifier = Modifier.padding(top = 2.dp),
                         onClick = {
                             // Add logic to handle the click event
                             // Example: navigate to a maps intent
                         }
                     )
                 }
             }*/
            Spacer(modifier = Modifier.height(8.dp))
            Divider(
                color = colorResource(id = R.color.input_box_border),
                thickness = 1.dp,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(80.dp))
            /*Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start // Aligning everything to the left
            ) {
                androidx.compose.material3.Text(
                    text = "Approver 1",
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = colorResource(id = R.color.category_color)
                    ),
                    modifier = Modifier.weight(1f),
                    maxLines = 1, // Limits the text to a single line
                    overflow = TextOverflow.Ellipsis, // Adds the ellipsis when the text overflows
                )
                androidx.compose.material3.Text(
                    text = "abc@gmail.com",
                    style = TextStyle(
                        fontSize = 11.sp,
                        color = colorResource(id = R.color.count_grid_color)
                    )
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start // Aligning everything to the left
            ) {
                androidx.compose.material3.Text(
                    text = "Vendor Name",
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = colorResource(id = R.color.category_color)
                    ),
                    modifier = Modifier.weight(1f),
                    maxLines = 1, // Limits the text to a single line
                    overflow = TextOverflow.Ellipsis, // Adds the ellipsis when the text overflows
                )
                androidx.compose.material3.Text(
                    text = "Ericsson & Co.",
                    style = TextStyle(
                        fontSize = 11.sp,
                        color = colorResource(id = R.color.count_grid_color)
                    )
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start // Aligning everything to the left
            ) {
                androidx.compose.material3.Text(
                    text = "Product Name",
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = colorResource(id = R.color.category_color)
                    ),
                    modifier = Modifier.weight(1f),
                    maxLines = 1, // Limits the text to a single line
                    overflow = TextOverflow.Ellipsis, // Adds the ellipsis when the text overflows
                )
                androidx.compose.material3.Text(
                    text = "-",
                    style = TextStyle(
                        fontSize = 11.sp,
                        color = colorResource(id = R.color.count_grid_color)
                    )
                )
            }*/

    }

}