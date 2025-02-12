package com.isl.assetManagement.jetpackcompose
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.isl.assetManagement.responses.Assets
import com.isl.assetManagement.room.entity.AssetRequests
import com.isl.assetManagement.utils.Util
import infozech.itower.R


@Composable
fun AssetCardView(item: Assets, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth() // Ensures the Card takes the full width
            .padding(start = 16.dp, end = 16.dp, top = 12.dp)  // Padding outside the Card
            .shadow(0.dp)  // Shadow for elevation effect
            .clickable { // Handling the click
                onClick()  // Trigger the passed onClick action
            }
            .border(
                width = 1.dp,
                color = colorResource(id = R.color.border_asset_card),
                shape = RoundedCornerShape(4.dp)
            )

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth() // Ensures Row takes full width
                .background(colorResource(id = R.color.white)), // Row background to white
            horizontalArrangement = Arrangement.SpaceBetween, // Distribute space evenly
            verticalAlignment = Alignment.CenterVertically // Align vertically to center
        ) {
            // Left column with 3 texts
            Column(
                modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 16.dp, bottom = 16.dp),  // Apply padding only to Column content
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth() // Ensures the Row takes up the full width
                ) {

                    Text(
                        text = ""+item.assetType,   //asset type
                        style = TextStyle(
                            fontSize = 12.sp,
                            color = colorResource(id = R.color.label)
                        ),
                        modifier = Modifier.weight(1f),
                        maxLines = 1, // Limits the text to a single line
                        overflow = TextOverflow.Ellipsis, // Adds the ellipsis when the text overflows
                    )

                    if (!item.assetId.isNullOrEmpty()) {
                        item.qrCode?.let {
                            Text(
                                text = it.toString(),
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    color = colorResource(id = R.color.label)
                                ),
                                modifier = Modifier.padding(end = 0.dp) // Optional, adds padding around the text
                            )
                        }
                        if(item.status==1) {
                            Spacer(modifier = Modifier.width(0.dp)) // Optional space between text and image
                            Icon(
                                painter = painterResource(id = R.drawable.right_icon), // Default back arrow icon
                                contentDescription = "",
                                modifier = Modifier.padding(start = 5.dp),
                                tint = colorResource(id = R.color.qr_code_verify) // Color for the arrow icon
                            )

                        }else{
                            Spacer(modifier = Modifier.width(0.dp)) // Optional space between text and image
                            Icon(
                                painter = painterResource(id = R.drawable.unverify), // Default back arrow icon
                                contentDescription = "",
                                modifier = Modifier.padding(start = 5.dp),
                                tint = colorResource(id = R.color.qr_code_unverify) // Color for the arrow icon
                            )
                        }
                    } else {
                        item.approvedQty?.let {
                            Text(
                                text = "Qty : $it",
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    color = colorResource(id = R.color.label)
                                ),
                                modifier = Modifier.padding(end = 17.dp) // Optional, adds padding around the text
                            )
                        }
                    }



                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth() // Ensures the Row takes up the full width
                ) {

                   Text(
                        text = ""+item.itemCode,  //item code
                        style = TextStyle(
                            fontSize = 12.sp,
                            color = colorResource(id = R.color.label)
                        ),
                        modifier = Modifier.weight(1f),
                        maxLines = 1, // Limits the text to a single line
                        overflow = TextOverflow.Ellipsis, // Adds the ellipsis when the text overflows
                    )

                    item.assetId?.let {
                        Text(
                            text = ""+it,
                            style = TextStyle(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = colorResource(id = R.color.label)
                            ),
                            modifier = Modifier.padding(end =17.dp) // Optional, adds padding around the text
                        )
                    }
               }


            }
        }
    }
}
