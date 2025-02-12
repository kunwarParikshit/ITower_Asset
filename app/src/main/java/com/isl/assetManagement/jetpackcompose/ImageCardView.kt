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
import infozech.itower.R

@Composable
fun ImageCardView(
    imageRes: Int,
    fileTag: String,
    timeStamp: String,
    lat: String,
    long: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 4.dp)
            .clickable { onClick() }
    ) {
        // Image Section - Placed OUTSIDE the Card to avoid rounding
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .height(119.dp)
                .width(89.dp)
                .clip(RectangleShape) // Ensures no rounded corners
                .background(Color.White)
        )

        // Card Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(119.dp), // Match Image Height
            shape = RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp, topStart = 0.dp, bottomStart = 0.dp), // Round only the right side
            border = BorderStroke(1.dp, colorResource(id = R.color.border_asset_card))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colorResource(id = R.color.white))
                    .padding(start = 8.dp, top = 8.dp, end = 10.dp, bottom = 8.dp),
                verticalArrangement = Arrangement.Center
            ) {
                DetailRow("Tag : ", fileTag)
                DetailRow("Time : ", timeStamp)
                DetailRow("Lat : ", lat)
                DetailRow("Long : ", long)
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
            modifier = Modifier.padding(end = 10.dp),
            color = colorResource(id = R.color.color_48484A),
            maxLines = 1,  // Ensures only one line is displayed
            overflow = TextOverflow.Ellipsis,
        )
    }
}



