package com.isl.assetManagement.jetpackcompose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.isl.assetManagement.responses.Timeline

object JetpackUIs {

    @Composable
    fun TimelineScreen(timelines: List<Timeline>?) {
        timelines ?: return

        // Use Box to overlay a continuously drawn line behind the timeline items
        Box(modifier = Modifier.fillMaxSize()) {
            // Draw the continuous vertical line
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp)  // Align with the timeline items’ left padding
            ) {
                // Determine the x-coordinate for the line. (Adjust 12.dp if needed)
                val lineX = 12.dp.toPx()
                drawLine(
                    color = Color.LightGray,  // Default line color; you might change it per your design
                    start = Offset(x = lineX, y = 0f),
                    end = Offset(x = lineX, y = size.height),
                    strokeWidth = 2.dp.toPx()
                )
            }

            // The timeline items (with a transparent left column so the dot overlays the line)
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, top = 16.dp, bottom = 16.dp, end = 16.dp)
            ) {
                itemsIndexed(timelines) { index, timeline ->
                    TimelineItem(
                        timeline = timeline,
                        isApproved = timeline.status == "Approved"
                    )
                }
            }
        }
    }

    @Composable
    fun TimelineItem(timeline: Timeline, isApproved: Boolean) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // Left column: only used to place the dot on the continuous line.
            Box(
                modifier = Modifier
                    .width(24.dp)
                    .fillMaxHeight()
            ) {
                // Dot indicator drawn on top of the continuous line
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .align(Alignment.Center)
                        .background(
                            color = if (isApproved) Color(0xFFE36C27) else Color.LightGray,
                            shape = CircleShape
                        )
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Right column: timeline details.
            Column(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth()
            ) {
                Text(text = timeline.stage, fontWeight = FontWeight.Bold)
                Text(text = "Status: ${timeline.status}", style = MaterialTheme.typography.body2)
                timeline.user?.let { user ->
                    Text(text = "User: $user", style = MaterialTheme.typography.body2)
                }
                Text(text = "Date: ${timeline.timestamp}", style = MaterialTheme.typography.body2)
                Text(
                    text = "Location: (${timeline.latitude}, ${timeline.longitude})",
                    style = MaterialTheme.typography.body2
                )
            }
        }
    }
}



