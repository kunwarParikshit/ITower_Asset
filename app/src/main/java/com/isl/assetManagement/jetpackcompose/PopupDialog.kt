package com.isl.assetManagement.jetpackcompose
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import infozech.itower.R

@Composable
fun CaptureImagePopup(msg: String, showDialog: Boolean, onDismiss: () -> Unit, onCapture: () -> Unit) {
    if (showDialog) {
        Dialog(
            onDismissRequest = { onDismiss() },
            properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = false)
        ) {
            // Apply margins to the Alert Box
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp), // Set left & right margins
                shape = RoundedCornerShape(4.dp),
                backgroundColor = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Capture Image",
                        style = TextStyle(
                            fontSize = 15.sp,
                            color = colorResource(id = R.color.color_count)
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Example Image (Replace with actual drawable)
                   /* Image(
                        painter = painterResource(id = R.drawable.ic_camera_example),
                        contentDescription = "Example Image",
                        modifier = Modifier
                            .size(120.dp)
                            .padding(8.dp)
                    )*/

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        "Please capture a clear image of the $msg",
                        style = TextStyle(
                            fontSize = 13.sp,
                            color = colorResource(id = R.color.color_count)
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))


                    Surface(
                       // elevation = 4.dp,
                        //modifier = modifier.fillMaxWidth() // Use the passed modifier here
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(colorResource(id = R.color.white))
                                .height(62.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 0.dp, end = 0.dp, top = 10.dp, bottom = 0.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { onDismiss() },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(42.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = colorResource(id = R.color.btn)
                                    ),
                                    border = BorderStroke(1.dp, colorResource(id = R.color.btn))
                                ) {
                                    Text("Cancel", color = colorResource(id = R.color.btn))
                                }

                                Spacer(modifier = Modifier.width(24.dp))

                                Button(
                                    onClick = { onCapture() },
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
                                        Text("Capture")
                                    }
                                }
                           }
                        }
                    }
                }
            }
        }
    }
}


