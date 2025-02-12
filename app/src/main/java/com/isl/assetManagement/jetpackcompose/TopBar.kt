package com.isl.assetManagement.jetpackcompose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import infozech.itower.R

@Composable
fun TopBar(onBackClicked: () -> Unit, onSearchClicked: () -> Unit, title: String, subTitle: String,mode : Int) {
    TopAppBar(backgroundColor = colorResource(id = R.color.white),
        modifier = Modifier
            .fillMaxWidth()
            .background(colorResource(id = R.color.white))
            .height(135.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(0.dp),
            modifier = Modifier
                .fillMaxWidth()
                .background(colorResource(id = R.color.white))
                .padding(start = 10.dp, end = 10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.back_icon),
                    contentDescription = "Back Icon",
                    tint = colorResource(id = R.color.search),
                    modifier = Modifier.clickable { onBackClicked() }
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    painter = painterResource(id = R.drawable.search_icon),
                    contentDescription = "Search Icon",
                    tint = colorResource(id = R.color.search),
                    modifier = Modifier.clickable { onSearchClicked() }
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(65.dp)
                    .padding(top = 17.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(0.dp),
                    modifier = Modifier.height(57.dp)
                ) {
                    Text(
                        text = title, //Movement Requests
                        style = TextStyle(
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorResource(id = R.color.color_movement)
                        )
                    )
                    Text(
                        text = subTitle,
                        style = TextStyle(
                            fontSize = 12.sp,
                            color = colorResource(id = R.color.color_count)
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if(mode==1){
                    Spacer(modifier = Modifier.weight(1f))
                    Box(
                        modifier = Modifier
                            .height(42.dp)
                            .width(102.dp)
                            .padding(top = 7.dp)
                            .background(
                                color = colorResource(id = R.color.color_add_request_border),
                                shape = RoundedCornerShape(2.dp)
                            )
                            .padding(1.dp)
                            .background(
                                color = colorResource(id = R.color.color_add_request_background),
                                shape = RoundedCornerShape(1.dp)
                            )
                            .wrapContentSize(Alignment.Center)
                    ) {
                        Text(
                            text = "Request +",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = colorResource(id = R.color.color_add_request)
                            )
                        )
                    }
                }
            }
        }
    }
}