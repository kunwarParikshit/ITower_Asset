package com.isl.assetManagement.jetpackcompose
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import infozech.itower.R

@Composable
fun LoadingDialog(onDismiss: () -> Unit = {}) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .height(50.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colors.surface)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                //Text(text = "Loading...", style = MaterialTheme.typography.h6)

                //Spacer(modifier = Modifier.height(16.dp))

                // Custom animated progress bar to match the uploaded image
                AnimatedLinearProgressIndicator()
            }
        }
    }
}

@Composable
fun AnimatedLinearProgressIndicator() {
    val infiniteTransition = rememberInfiniteTransition()
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    LinearProgressIndicator(
        progress = progress,
        modifier = Modifier
            .fillMaxWidth()
            .height(4.dp),  // Thin progress bar like in the image
        color = colorResource(id = R.color.search), // Match the orange color in the image
        backgroundColor = colorResource(id = R.color.white), // Light gray background
        strokeCap = StrokeCap.Round // Rounded edges like the image
    )
}
