package com.isl.assetManagement.jetpackcompose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.rememberAsyncImagePainter
import com.isl.assetManagement.responses.Documents
import infozech.itower.R


@Composable
fun ImageCardView(
    item: Documents,
    mode: Int,
    flag: Int,
    onClick: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    val imagePainter = rememberAsyncImagePainter(
        model = item.url ?: "",
        placeholder = painterResource(id = R.drawable.bg_login),
        error = painterResource(id = R.drawable.no_media)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 4.dp)
            .clickable { showDialog = true } // Open image popup
    ) {
        Image(
            painter = imagePainter,
            contentDescription = "Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .height(130.dp)
                .width(90.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.White)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp),
            shape = RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp),
            border = BorderStroke(1.dp, colorResource(id = R.color.border_asset_card))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colorResource(id = R.color.white))
                    .padding(8.dp),
                verticalArrangement = Arrangement.Center
            ) {
                DetailRow("Tag : ", item.tagName)
                DetailRow("Time : ", item.timeStamp)
                DetailRow("Lat : ", "" + item.latiude)
                DetailRow("Long : ", "" + item.longitude)
            }
        }
    }

    // **Updated Dialog UI - Full Image Without Cropping**
    if (showDialog) {
        var scale by remember { mutableStateOf(1f) }
        Dialog(
            onDismissRequest = { showDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false) // Full screen
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize() // Fullscreen Dialog
                    .background(Color.White) // White Background
            ) {
                // Close button at the top-right
                IconButton(
                    onClick = { showDialog = false },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.close_icon),
                        contentDescription = "Close",
                        tint = Color.Black
                    )
                }

                var scale by remember { mutableStateOf(1f) }
                var offset by remember { mutableStateOf(Offset.Zero) }

                Image(
                    painter = imagePainter,
                    contentDescription = "Full Image",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .align(Alignment.Center)
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = offset.x,
                            translationY = offset.y
                        )
                        .pointerInput(Unit) {
                            detectTransformGestures { centroid, pan, zoom, _ ->
                                val newScale = (scale * zoom).coerceIn(1f, 5f)

                                val panMultiplier = 1.5f // Increase for smoother panning
                                val newOffset = offset + pan * panMultiplier // Faster panning

                                val maxX = (newScale - 1) * size.width / 2
                                val maxY = (newScale - 1) * size.height / 2

                                offset = Offset(
                                    newOffset.x.coerceIn(-maxX, maxX),
                                    newOffset.y.coerceIn(-maxY, maxY)
                                )

                                scale = newScale
                            }
                        }
                )






            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 1.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            modifier = Modifier.padding(end = 5.dp),
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
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
