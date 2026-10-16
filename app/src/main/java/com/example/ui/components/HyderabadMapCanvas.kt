package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ShuttlePoint
import com.example.data.model.StopPointEntity
import com.example.ui.theme.MuetGold
import com.example.ui.theme.MuetGreen
import com.example.ui.theme.MuetPrimary
import com.example.ui.theme.MuetRed
import com.example.ui.theme.MuetSecondary

@Composable
fun HyderabadMapCanvas(
    shuttles: List<ShuttlePoint>,
    stops: List<StopPointEntity>,
    selectedShuttle: ShuttlePoint?,
    onShuttleClick: (ShuttlePoint) -> Unit,
    modifier: Modifier = Modifier
) {
    // Pulse animation for active GPS bus markers
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_trans")
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 12f,
        targetValue = 32f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "alpha"
    )

    // Interactive pan & zoom state
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val transformState = rememberTransformableState { zoomChange, offsetChange, _ ->
        scale = (scale * zoomChange).coerceIn(0.8f, 2.5f)
        offset += offsetChange
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFE8EEF5))
            .testTag("hyderabad_live_map")
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .transformable(state = transformState)
                .pointerInput(shuttles) {
                    detectTapGestures { tapOffset ->
                        // Hit test bus markers
                        val w = size.width
                        val h = size.height
                        val minLat = 25.3500
                        val maxLat = 25.4400
                        val minLng = 68.2500
                        val maxLng = 68.3800

                        shuttles.forEach { bus ->
                            val normX = ((bus.longitude - minLng) / (maxLng - minLng)).toFloat()
                            val normY = (1f - ((bus.latitude - minLat) / (maxLat - minLat)).toFloat())
                            val busX = normX * w
                            val busY = normY * h
                            val dist = Math.hypot((tapOffset.x - busX).toDouble(), (tapOffset.y - busY).toDouble())
                            if (dist < 40f) {
                                onShuttleClick(bus)
                            }
                        }
                    }
                }
        ) {
            val w = size.width
            val h = size.height

            // 1. Draw River Indus (Sindhu Darya) separating Hyderabad & Jamshoro
            val riverPath = Path().apply {
                moveTo(w * 0.40f, 0f)
                cubicTo(
                    w * 0.42f, h * 0.25f,
                    w * 0.36f, h * 0.55f,
                    w * 0.44f, h
                )
                lineTo(w * 0.52f, h)
                cubicTo(
                    w * 0.44f, h * 0.55f,
                    w * 0.50f, h * 0.25f,
                    w * 0.48f, 0f
                )
                close()
            }
            drawPath(
                path = riverPath,
                color = Color(0xFFB0D5F7)
            )

            // 2. River Bank Labels & Bridge
            // Jamshoro Old & New Bridges
            drawRoundRect(
                color = Color(0xFF78909C),
                topLeft = Offset(w * 0.36f, h * 0.45f),
                size = Size(w * 0.16f, 12f),
                cornerRadius = CornerRadius(4f, 4f)
            )
            drawRoundRect(
                color = Color(0xFF546E7A),
                topLeft = Offset(w * 0.37f, h * 0.48f),
                size = Size(w * 0.14f, 8f),
                cornerRadius = CornerRadius(4f, 4f)
            )

            // 3. Main Highway Arteries
            // M-9 / National Highway from Latifabad through Qasimabad to Jamshoro
            val mainTransitRoute = Path().apply {
                // Latifabad Unit 7
                moveTo(w * 0.88f, h * 0.85f)
                // Auto Bhan
                lineTo(w * 0.78f, h * 0.72f)
                // Qasim Chowk
                lineTo(w * 0.70f, h * 0.56f)
                // Naseem Nagar
                lineTo(w * 0.62f, h * 0.48f)
                // Across Jamshoro Bridge
                lineTo(w * 0.44f, h * 0.46f)
                // Jamshoro Toll Plaza
                lineTo(w * 0.30f, h * 0.44f)
                // MUET Campus Gate & Admin Block
                lineTo(w * 0.14f, h * 0.36f)
            }
            // Route glow background
            drawPath(
                path = mainTransitRoute,
                color = MuetSecondary.copy(alpha = 0.35f),
                style = Stroke(width = 16f, pathEffect = PathEffect.cornerPathEffect(18f))
            )
            // Route active road
            drawPath(
                path = mainTransitRoute,
                color = MuetPrimary,
                style = Stroke(width = 6f, pathEffect = PathEffect.cornerPathEffect(18f))
            )

            // Secondary Route: Saddar / Thandi Sarak
            val saddarRoute = Path().apply {
                moveTo(w * 0.85f, h * 0.40f) // Saddar
                lineTo(w * 0.75f, h * 0.42f) // Thandi Sarak
                lineTo(w * 0.62f, h * 0.48f) // Joins Qasimabad
                lineTo(w * 0.44f, h * 0.46f) // Jamshoro Bridge
                lineTo(w * 0.18f, h * 0.32f) // MUET Library
            }
            drawPath(
                path = saddarRoute,
                color = Color(0xFF64748B),
                style = Stroke(width = 4f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 10f), 0f))
            )

            // 4. District Boundary Landmarks
            // Jamshoro Campus Zone (Greenery)
            drawRoundRect(
                color = Color(0xFFC8E6C9).copy(alpha = 0.6f),
                topLeft = Offset(w * 0.05f, h * 0.20f),
                size = Size(w * 0.22f, h * 0.35f),
                cornerRadius = CornerRadius(16f, 16f)
            )

            // 5. Draw Scheduled Stops
            val minLat = 25.3500
            val maxLat = 25.4400
            val minLng = 68.2500
            val maxLng = 68.3800

            stops.forEach { stop ->
                val normX = ((stop.longitude - minLng) / (maxLng - minLng)).toFloat()
                val normY = (1f - ((stop.latitude - minLat) / (maxLat - minLat)).toFloat())
                val stopX = normX * w
                val stopY = normY * h

                // Campus stops get gold accent, city stops get blue
                val stopColor = if (stop.isCampusStop) MuetGold else MuetPrimary
                drawCircle(
                    color = Color.White,
                    radius = 7f,
                    center = Offset(stopX, stopY)
                )
                drawCircle(
                    color = stopColor,
                    radius = 5f,
                    center = Offset(stopX, stopY)
                )
            }

            // 6. Draw Moving Shuttle Point Buses
            shuttles.forEach { bus ->
                val normX = ((bus.longitude - minLng) / (maxLng - minLng)).toFloat()
                val normY = (1f - ((bus.latitude - minLat) / (maxLat - minLat)).toFloat())
                val busX = normX * w
                val busY = normY * h

                val isSelected = bus.id == selectedShuttle?.id

                // Pulse circle
                val pulseColor = when (bus.seatStatus) {
                    "Bus Full" -> MuetRed
                    else -> MuetSecondary
                }

                drawCircle(
                    color = pulseColor.copy(alpha = pulseAlpha),
                    radius = pulseRadius * (if (isSelected) 1.5f else 1f),
                    center = Offset(busX, busY)
                )

                // Bus Icon Base Card
                val markerW = if (isSelected) 56f else 46f
                val markerH = if (isSelected) 34f else 28f
                val cardTopLeft = Offset(busX - markerW / 2, busY - markerH / 2)

                drawRoundRect(
                    color = if (isSelected) MuetGold else MuetPrimary,
                    topLeft = cardTopLeft,
                    size = Size(markerW, markerH),
                    cornerRadius = CornerRadius(8f, 8f),
                    style = Stroke(width = if (isSelected) 4f else 2f)
                )
                drawRoundRect(
                    color = if (isSelected) Color(0xFF0D47A1) else Color.White,
                    topLeft = cardTopLeft,
                    size = Size(markerW, markerH),
                    cornerRadius = CornerRadius(8f, 8f)
                )

                // Draw Text via Android Canvas
                drawContext.canvas.nativeCanvas.apply {
                    val paint = android.graphics.Paint().apply {
                        color = if (isSelected) android.graphics.Color.WHITE else android.graphics.Color.BLACK
                        textSize = if (isSelected) 22f else 18f
                        isFakeBoldText = true
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                    val num = bus.pointNumber.replace("Point", "Pt.").trim()
                    drawText(num, busX, busY + 6f, paint)
                }

                // Small Seat Availability Dot
                val seatDotColor = when {
                    bus.availableSeats == 0 -> android.graphics.Color.RED
                    bus.availableSeats <= 8 -> android.graphics.Color.rgb(255, 152, 0)
                    else -> android.graphics.Color.rgb(46, 125, 50)
                }
                drawContext.canvas.nativeCanvas.apply {
                    val dotPaint = android.graphics.Paint().apply {
                        color = seatDotColor
                        style = android.graphics.Paint.Style.FILL
                    }
                    drawCircle(busX + markerW / 2 - 2f, busY - markerH / 2 + 2f, 6f, dotPaint)
                }
            }
        }

        // Overlay Map Watermark / Legend
        Surface(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(10.dp),
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
            shadowElevation = 2.dp
        ) {
            Box(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                Text(
                    text = "INDUS RIVER / HYDERABAD - JAMSHORO TRANSIT CORRIDOR",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = MuetPrimary
                    )
                )
            }
        }

        // Live Active GPS Badge
        Surface(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(10.dp),
            shape = RoundedCornerShape(20.dp),
            color = MuetGreen,
            shadowElevation = 2.dp
        ) {
            Text(
                text = "● LIVE GPS",
                color = Color.White,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}
