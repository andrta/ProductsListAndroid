package com.tamboo.profile.presentation

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AnimatedBlobHeader() {
    val infiniteTransition = rememberInfiniteTransition(label = "blobAnimation")

    val colorPrimaryContainer = MaterialTheme.colorScheme.primaryContainer
    val colorBackground = MaterialTheme.colorScheme.background
    val colorPrimary = MaterialTheme.colorScheme.primary
    val colorSecondary = MaterialTheme.colorScheme.secondary

    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "phase"
    )

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val centerX = canvasWidth / 2
        val centerY = canvasHeight / 0.9f

        val blobPath = Path().apply {
            val numPoints = 360
            val radius = 550

            for (i in 0..numPoints) {
                val angle = (i.toFloat() / numPoints) * 2f * Math.PI.toFloat()

                val distortion = 250 * sin(angle * 3f + phase) * cos(angle * 2f - phase)
                val currentRadius = radius + distortion

                val x = centerX + currentRadius * cos(angle)
                val y = centerY + currentRadius * sin(angle)

                if (i == 0) moveTo(x, y) else lineTo(x, y)
            }
            close()
        }

        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    colorPrimaryContainer,
                    colorBackground
                )
            )
        )

        drawPath(
            path = blobPath,
            brush = Brush.radialGradient(
                colors = listOf(
                    colorPrimary.copy(alpha = 0.4f),
                    colorSecondary.copy(alpha = 0.1f)
                ),
                center = Offset(centerX, centerY)
            )
        )
    }
}
