package com.isl.assetManagement.jetpackcompose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.isl.assetManagement.responses.Timeline
import infozech.itower.R

object JetpackUIs {
    object TimeLine {
        @Composable
        fun TimelineScreen(timelines: List<Timeline>?) {
            timelines ?: return
            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 16.dp, top = 16.dp, bottom = 16.dp, end = 16.dp)
                ) {
                    itemsIndexed(timelines) { _, timeline ->
                        TimelineItem(timeline, timeline.status == "Approved"
                                || timeline.status == "NewRequest")
                    }
                }
            }
        }

        @Composable
        private fun TimelineItem(timeline: Timeline, isApproved: Boolean) {
            val lineColor = if (isApproved) Color(0xFFE36C27) else Color.LightGray
            Box(modifier = Modifier.fillMaxWidth()) {
                Canvas(modifier = Modifier.matchParentSize()) {
                    val lineX = 12.dp.toPx()
                    drawLine(
                        color = lineColor,
                        start = Offset(x = lineX, y = 0f),
                        end = Offset(x = lineX, y = size.height),
                        strokeWidth = 2.dp.toPx()
                    )
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .width(24.dp)
                            .fillMaxHeight()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .align(Alignment.Center)
                                .background(color = lineColor, shape = CircleShape)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = timeline.stage,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorResource(id = R.color.tab_unselect_text_color))
                        timeline.user?.let { user ->
                            Text(text = "User: $user",
                                fontSize = 12.sp,
                                color = colorResource(id = R.color.color_717E95)
                            )
                        }
                        Text(
                            text = "Status: ${timeline.status}",
                            fontSize = 12.sp,
                            color = colorResource(id = R.color.color_717E95)
                        )

                        Text(
                            text = "Date: ${timeline.timestamp}",
                            fontSize = 12.sp,
                            color = colorResource(id = R.color.color_717E95)
                        )
                        /*Text(
                            text = "Location: (${timeline.latitude}, ${timeline.longitude})",
                            style = MaterialTheme.typography.body2,
                            color = colorResource(id = R.color.color_717E95)
                        )*/
                    }
                }
            }
        }
    }
}
