package com.isl.assetManagement.jetpackcompose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import infozech.itower.R

@Composable
fun DialogFragmentTopBar(onGrips: () -> Unit, closed: () -> Unit,title : String) {
    /*Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(
                RoundedCornerShape(
                    topStart = 16.dp
                )
            ) // Rounded corners for the header
            .background(colorResource(id = R.color.white))
            .padding(start = 20.dp, end = 10.dp, top = 0.dp),
            *//*.shadow(elevation = 50.dp,
                spotColor = Color(0xFFDC0101),
                ambientColor = Color(0xFFDC0101)),*//*
            *//*.shadow(
                elevation = 32.dp, // Adjust elevation for blur intensity (equivalent to blur effect)
                shape = RoundedCornerShape(topEnd = 16.dp),
                ambientColor = Color.Black.copy(alpha = 0.15f), // Custom shadow color
                spotColor = Color.Black.copy(alpha = 0.15f)
            ),*//*
        contentAlignment = Alignment.Center
    ) */
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
           /* .shadow(elevation = 16.dp,
                spotColor = Color(0x1A000000),
                ambientColor = Color(0x1A000000))*/

            .shadow(
                elevation = 100.dp, // Adjust elevation for blur intensity (equivalent to blur effect)
                shape = RoundedCornerShape(topEnd = 16.dp),
                ambientColor = Color.Black.copy(alpha = 0.30f), // Custom shadow color
                spotColor = Color.Black.copy(alpha = 0.30f)
            )
            .background(colorResource(id = R.color.white))
            .padding(start = 20.dp, end = 10.dp, top = 0.dp)

    )


    {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp
                    )
                ), // Rounded corners for the header
               /* .shadow(elevation = 16.dp,
                    spotColor = Color(0x1A000000),
                    ambientColor = Color(0x1A000000)),*/
                //.background(colorResource(id = R.color.white)),
                contentAlignment = Alignment.Center

        ){

        Text(
            text = title,
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = colorResource(id = R.color.search),

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
                .padding(start = 0.dp, end = 0.dp, top = 14.dp)
                .clickable { onGrips() } // Invoke close action when clicked
        )
        Icon(
            painter = painterResource(id = R.drawable.close_icon),
            contentDescription = "Close icon",
            tint = colorResource(id = R.color.from_to_color),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(start = 0.dp, end = 10.dp, top = 5.dp)
                .clickable { closed() } // Invoke close action when clicked

        )
    }

}
}



