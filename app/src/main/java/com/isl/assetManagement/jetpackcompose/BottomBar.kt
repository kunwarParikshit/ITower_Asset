package com.isl.assetManagement.jetpackcompose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import infozech.itower.R

@Composable
fun BottomBar(
    onCancelClicked: () -> Unit,
    onUpdateClicked: () -> Unit,
    btPrimary: String,
    btSecondary: String,
    modifier: Modifier = Modifier // Pass a modifier for dynamic padding
) {
    Surface(
        elevation = 4.dp,
        modifier = modifier.fillMaxWidth() // Use the passed modifier here
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(colorResource(id = R.color.white))
                .height(81.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 19.dp, bottom = 20.dp)
            ) {
                OutlinedButton(
                    onClick = { onCancelClicked() },
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = colorResource(id = R.color.btn)
                    ),
                    border = BorderStroke(1.dp, colorResource(id = R.color.btn))
                ) {
                    Text(btPrimary, color = colorResource(id = R.color.btn))
                }

                Spacer(modifier = Modifier.width(24.dp))

                Button(
                    onClick = { onUpdateClicked() },
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = colorResource(id = R.color.btn),
                        contentColor = Color.White
                    )
                )


                {
                    Row(
                        verticalAlignment = Alignment.CenterVertically, // Align text and icon
                        horizontalArrangement = Arrangement.Center // Center content
                    ) {
                        Text(btSecondary)
                        if(btSecondary.equals("Update")){
                            Spacer(modifier = Modifier.width(6.dp)) // Space between text and icon

                            Icon(
                                painter = painterResource(id = R.drawable.edit_icon), // Your pencil icon
                                contentDescription = "",
                                tint = Color.White, // Adjust color as needed
                                modifier = Modifier.size(18.dp) // Adjust size as needed
                            )
                        }else  if(btSecondary.equals("Search")){
                            Spacer(modifier = Modifier.width(6.dp)) // Space between text and icon

                            Icon(
                                painter = painterResource(id = R.drawable.forword_icon), // Your pencil icon
                                contentDescription = "",
                                tint = Color.White, // Adjust color as needed
                                modifier = Modifier.size(18.dp) // Adjust size as needed
                            )
                        }else  if(btSecondary.equals("Move")){
                            Spacer(modifier = Modifier.width(6.dp)) // Space between text and icon
                            Icon(
                                painter = painterResource(id = R.drawable.move), // Your pencil icon
                                contentDescription = "",
                                tint = Color.White, // Adjust color as needed
                                modifier = Modifier.size(18.dp) // Adjust size as needed
                            )
                        }else  if(btSecondary.equals("Submit")){
                            Spacer(modifier = Modifier.width(6.dp)) // Space between text and icon
                            Icon(
                                painter = painterResource(id = R.drawable.forword_icon), // Your pencil icon
                                contentDescription = "",
                                tint = Color.White, // Adjust color as needed
                                modifier = Modifier.size(18.dp) // Adjust size as needed
                            )
                        }


                    }



                }
            }
        }
    }
}

