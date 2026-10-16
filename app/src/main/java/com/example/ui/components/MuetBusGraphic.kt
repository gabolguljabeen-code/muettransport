package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.ui.theme.MuetGold
import com.example.ui.theme.MuetPrimary
import com.example.ui.theme.MuetSecondary

@Composable
fun MuetBusGraphic(
    modifier: Modifier = Modifier,
    pointNumber: String = "27",
    isMoving: Boolean = true
) {
    val transition = rememberInfiniteTransition(label = "bus_anim")
    val bounce by transition.animateFloat(
        initialValue = -1.5f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounce"
    )

    val wheelRotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wheel"
    )

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val offsetY = if (isMoving) bounce else 0f

            // Ground shadow
            drawOval(
                color = Color.Black.copy(alpha = 0.15f),
                topLeft = Offset(w * 0.08f, h * 0.82f),
                size = Size(w * 0.84f, h * 0.12f)
            )

            // Bus Dimensions
            val busLeft = w * 0.08f
            val busTop = h * 0.18f + offsetY
            val busWidth = w * 0.84f
            val busHeight = h * 0.62f

            // Upper Body (Off-white / Cream - authentic MUET Point styling)
            drawRoundRect(
                color = Color(0xFFF1F5F9),
                topLeft = Offset(busLeft, busTop),
                size = Size(busWidth, busHeight * 0.52f),
                cornerRadius = CornerRadius(16f, 16f)
            )

            // Lower Body (Iconic MUET Deep Blue)
            drawRoundRect(
                color = MuetPrimary,
                topLeft = Offset(busLeft, busTop + busHeight * 0.48f),
                size = Size(busWidth, busHeight * 0.52f),
                cornerRadius = CornerRadius(8f, 8f)
            )

            // Accent White & Gold Stripe along the body
            drawRect(
                color = Color.White,
                topLeft = Offset(busLeft, busTop + busHeight * 0.46f),
                size = Size(busWidth, busHeight * 0.04f)
            )
            drawRect(
                color = MuetGold,
                topLeft = Offset(busLeft, busTop + busHeight * 0.50f),
                size = Size(busWidth, busHeight * 0.025f)
            )

            // Destination Board on roof ("MUET POINT")
            drawRoundRect(
                color = Color(0xFF1B5E20), // Pakistani Green Banner
                topLeft = Offset(busLeft + busWidth * 0.60f, busTop + busHeight * 0.04f),
                size = Size(busWidth * 0.36f, busHeight * 0.14f),
                cornerRadius = CornerRadius(6f, 6f)
            )
            // Destination Text Box (simulated bright yellow LEDs)
            drawRoundRect(
                color = Color(0xFFFFD54F),
                topLeft = Offset(busLeft + busWidth * 0.62f, busTop + busHeight * 0.06f),
                size = Size(busWidth * 0.32f, busHeight * 0.09f),
                cornerRadius = CornerRadius(4f, 4f)
            )

            // Passenger Windows (Tinted glass)
            val windowCount = 5
            val windowSpacing = busWidth * 0.025f
            val windowWidth = (busWidth * 0.54f - windowSpacing * (windowCount - 1)) / windowCount
            val windowHeight = busHeight * 0.28f

            for (i in 0 until windowCount) {
                val winX = busLeft + busWidth * 0.05f + i * (windowWidth + windowSpacing)
                val winY = busTop + busHeight * 0.14f
                drawRoundRect(
                    color = Color(0xFF90CAF9),
                    topLeft = Offset(winX, winY),
                    size = Size(windowWidth, windowHeight),
                    cornerRadius = CornerRadius(6f, 6f)
                )
                // Window shine reflection
                drawLine(
                    color = Color.White.copy(alpha = 0.6f),
                    start = Offset(winX + 4f, winY + 4f),
                    end = Offset(winX + windowWidth - 6f, winY + windowHeight - 6f),
                    strokeWidth = 2f
                )
            }

            // Front Windscreen (Slanted front)
            val frontWindshield = Path().apply {
                moveTo(busLeft + busWidth * 0.62f, busTop + busHeight * 0.16f)
                lineTo(busLeft + busWidth * 0.94f, busTop + busHeight * 0.16f)
                lineTo(busLeft + busWidth * 0.96f, busTop + busHeight * 0.44f)
                lineTo(busLeft + busWidth * 0.62f, busTop + busHeight * 0.44f)
                close()
            }
            drawPath(frontWindshield, color = Color(0xFF64B5F6))

            // Front Headlights
            drawCircle(
                color = Color(0xFFFFF9C4),
                radius = busHeight * 0.08f,
                center = Offset(busLeft + busWidth * 0.95f, busTop + busHeight * 0.72f)
            )
            drawCircle(
                color = Color(0xFFFFB300),
                radius = busHeight * 0.04f,
                center = Offset(busLeft + busWidth * 0.95f, busTop + busHeight * 0.72f)
            )

            // Tail lights
            drawRoundRect(
                color = Color(0xFFE53935),
                topLeft = Offset(busLeft + 2f, busTop + busHeight * 0.65f),
                size = Size(busWidth * 0.02f, busHeight * 0.15f),
                cornerRadius = CornerRadius(2f, 2f)
            )

            // Front Grille
            for (g in 0..3) {
                drawLine(
                    color = Color(0xFF0D233A),
                    start = Offset(busLeft + busWidth * 0.88f, busTop + busHeight * 0.66f + g * 6f),
                    end = Offset(busLeft + busWidth * 0.96f, busTop + busHeight * 0.66f + g * 6f),
                    strokeWidth = 3f
                )
            }

            // Wheels (Front and Rear)
            val wheelRadius = busHeight * 0.22f
            val wheelY = busTop + busHeight * 0.90f
            val rearWheelX = busLeft + busWidth * 0.24f
            val frontWheelX = busLeft + busWidth * 0.78f

            listOf(rearWheelX, frontWheelX).forEach { cx ->
                // Wheel cut-out arch
                drawCircle(
                    color = Color(0xFF1E293B),
                    radius = wheelRadius * 1.15f,
                    center = Offset(cx, wheelY - 4f)
                )
                // Tire rubber
                drawCircle(
                    color = Color(0xFF263238),
                    radius = wheelRadius,
                    center = Offset(cx, wheelY)
                )
                // Steel Rim
                drawCircle(
                    color = Color(0xFFCFD8DC),
                    radius = wheelRadius * 0.55f,
                    center = Offset(cx, wheelY)
                )
                // Hubcap center
                drawCircle(
                    color = MuetPrimary,
                    radius = wheelRadius * 0.25f,
                    center = Offset(cx, wheelY)
                )
                // Rim spokes
                drawLine(
                    color = Color(0xFF90A4AE),
                    start = Offset(cx - wheelRadius * 0.45f, wheelY),
                    end = Offset(cx + wheelRadius * 0.45f, wheelY),
                    strokeWidth = 2f
                )
                drawLine(
                    color = Color(0xFF90A4AE),
                    start = Offset(cx, wheelY - wheelRadius * 0.45f),
                    end = Offset(cx, wheelY + wheelRadius * 0.45f),
                    strokeWidth = 2f
                )
            }
        }
    }
}
