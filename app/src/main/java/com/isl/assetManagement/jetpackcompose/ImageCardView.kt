package com.isl.assetManagement.jetpackcompose
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale

import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter

import com.isl.assetManagement.responses.Documents
import com.isl.assetManagement.utils.Util
import infozech.itower.R

@Composable
fun ImageCardView(
    item: Documents,
    mode: Int,
    flag : Int,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 4.dp,
                bottom = 4.dp) // Conditionally set bottom margin
            .clickable { onClick() }
    ) {
        val imagePainter = when {
            item.url != null -> rememberAsyncImagePainter(item.url) //uri
            else -> null
        }
        imagePainter?.let {
            Image(
                painter = it,
                contentDescription = "Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(130.dp)
                    .width(90.dp)
                    .clip(RectangleShape) // Ensures no rounded corners
                    .background(Color.White)
            )
        }

        // Card Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp), // Match Image Height
            shape = RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp, topStart = 0.dp, bottomStart = 0.dp), // Round only the right side
            border = BorderStroke(1.dp, colorResource(id = R.color.border_asset_card))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colorResource(id = R.color.white))
                    .padding(start = 8.dp, top = 8.dp, end = 8.dp, bottom = 8.dp),
                verticalArrangement = Arrangement.Center
            ) {
                DetailRow("Tag : ", item.tagName)

                //DetailRow("Time : ", Util.convertDate(item.timeStamp,
                //"dd-MMM-yyyy HH:mm"))
                DetailRow("Time : ", item.timeStamp)
                DetailRow("Lat : ", ""+item.latiude)
                DetailRow("Long : ", ""+item.longitude)

            }
        }
    }
}


@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(vertical = 1.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            modifier = Modifier
                .padding(end = 5.dp),
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            color = colorResource(id = R.color.color_48484A)
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.padding(end = 5.dp),
            color = colorResource(id = R.color.color_48484A),
            maxLines = 1,  // Ensures only one line is displayed
            overflow = TextOverflow.Ellipsis,
        )
    }
}



