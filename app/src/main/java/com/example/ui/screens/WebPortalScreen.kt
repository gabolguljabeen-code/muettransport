package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BoardingPassEntity
import com.example.data.model.RouteEntity
import com.example.data.model.ShuttlePoint
import com.example.data.model.StopPointEntity
import com.example.data.model.StopRequestEntity
import com.example.data.model.TrafficAlertEntity
import com.example.ui.components.HyderabadMapCanvas
import com.example.ui.components.MuetBusGraphic
import com.example.ui.theme.MuetGold
import com.example.ui.theme.MuetGreen
import com.example.ui.theme.MuetOrange
import com.example.ui.theme.MuetPrimary
import com.example.ui.theme.MuetRed
import com.example.ui.theme.MuetSecondary

enum class WebNavSection {
    TRACKING_PORTAL,
    ROUTES_SCHEDULE,
    STUDENT_E_PASS,
    DRIVER_CONSOLE,
    ADMIN_COMMAND
}

@Composable
fun WebPortalScreen(
    shuttles: List<ShuttlePoint>,
    routes: List<RouteEntity>,
    stops: List<StopPointEntity>,
    alerts: List<TrafficAlertEntity>,
    boardingPass: BoardingPassEntity?,
    stopRequests: List<StopRequestEntity>,
    selectedShuttle: ShuttlePoint?,
    onSelectShuttle: (ShuttlePoint) -> Unit,
    onRequestStop: (ShuttlePoint) -> Unit,
    onUpdateSeats: (pointId: String, deltaSeats: Int, deltaStanding: Int) -> Unit,
    onSetPreset: (pointId: String, preset: String) -> Unit,
    onAdvanceStop: (pointId: String) -> Unit,
    onBroadcastAlert: (title: String, message: String, severity: String) -> Unit,
    onDismissAlert: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var activeSection by remember { mutableStateOf(WebNavSection.TRACKING_PORTAL) }
    var isDesktopView by remember { mutableStateOf(true) }
    val currentBus = selectedShuttle ?: shuttles.firstOrNull()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF1F5F9))
            .testTag("muet_web_portal")
    ) {
        // 1. Browser Navigation Shell Bar (URL Bar & Tabs)
        Surface(
            color = Color(0xFF1E293B),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                // Top browser tabs row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Browser window dots
                    Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        Box(modifier = Modifier.size(10.dp).background(Color(0xFFEF4444), CircleShape))
                        Box(modifier = Modifier.size(10.dp).background(Color(0xFFF59E0B), CircleShape))
                        Box(modifier = Modifier.size(10.dp).background(Color(0xFF10B981), CircleShape))
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Active Tab
                    Surface(
                        shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
                        color = Color(0xFF0F172A)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.DirectionsBus,
                                contentDescription = null,
                                tint = MuetGold,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "MUET Transport Portal — Jamshoro",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Desktop vs Mobile Viewport Switcher
                    Row(
                        modifier = Modifier
                            .background(Color(0xFF334155), RoundedCornerShape(12.dp))
                            .padding(2.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isDesktopView) MuetPrimary else Color.Transparent,
                            modifier = Modifier.clickable { isDesktopView = true }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Computer, contentDescription = "Desktop", tint = Color.White, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Web", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (!isDesktopView) MuetPrimary else Color.Transparent,
                            modifier = Modifier.clickable { isDesktopView = false }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.PhoneAndroid, contentDescription = "Mobile", tint = Color.White, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("App", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // URL Address Bar
                Surface(
                    color = Color(0xFF0F172A),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFF1E293B),
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "SSL Secure",
                                    tint = MuetGreen,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "https://transport.muet.edu.pk/portal/${activeSection.name.lowercase()}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(0xFF94A3B8),
                                        fontSize = 11.sp,
                                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reload",
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // 2. University Website Header (Navigation Bar)
        Surface(
            color = MuetPrimary,
            modifier = Modifier.fillMaxWidth(),
            shadowElevation = 4.dp
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // University Logo Branding
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.School,
                                    contentDescription = "MUET Crest",
                                    tint = MuetPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "MUET",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White,
                                        letterSpacing = 1.sp
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = MuetGold
                                ) {
                                    Text(
                                        text = "TRANSIT PORTAL",
                                        color = Color(0xFF0F172A),
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 8.sp
                                        )
                                    )
                                }
                            }
                            Text(
                                text = "Mehran University of Engineering & Technology, Jamshoro",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 9.sp
                                )
                            )
                        }
                    }

                    // Emergency Badge
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White.copy(alpha = 0.15f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.size(6.dp).background(MuetGreen, CircleShape))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "SYSTEM ONLINE",
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 8.sp
                                )
                            )
                        }
                    }
                }

                // Web Navigation Bar Links
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .background(Color(0xFF0B2E60))
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        WebNavSection.TRACKING_PORTAL to "📍 Live Fleet Map",
                        WebNavSection.ROUTES_SCHEDULE to "📅 Routes & Schedules",
                        WebNavSection.STUDENT_E_PASS to "🪪 Student E-Pass",
                        WebNavSection.DRIVER_CONSOLE to "🚍 Driver Cockpit",
                        WebNavSection.ADMIN_COMMAND to "📊 Fleet Analytics"
                    ).forEach { (section, label) ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (activeSection == section) MuetGold else Color.Transparent,
                            modifier = Modifier.clickable { activeSection = section }
                        ) {
                            Text(
                                text = label,
                                color = if (activeSection == section) Color(0xFF0F172A) else Color.White,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (activeSection == section) FontWeight.ExtraBold else FontWeight.Medium,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        // 3. Main Web Page Content
        Box(modifier = Modifier.weight(1f)) {
            when (activeSection) {
                WebNavSection.TRACKING_PORTAL -> {
                    WebTrackingPortalView(
                        shuttles = shuttles,
                        stops = stops,
                        alerts = alerts,
                        currentBus = currentBus,
                        onSelectShuttle = onSelectShuttle,
                        onRequestStop = onRequestStop,
                        onDismissAlert = onDismissAlert
                    )
                }

                WebNavSection.ROUTES_SCHEDULE -> {
                    SchedulesScreen(
                        routes = routes,
                        stops = stops,
                        shuttles = shuttles,
                        onSelectBus = {
                            onSelectShuttle(it)
                            activeSection = WebNavSection.TRACKING_PORTAL
                        }
                    )
                }

                WebNavSection.STUDENT_E_PASS -> {
                    BoardingPassScreen(
                        pass = boardingPass,
                        onTriggerSos = {}
                    )
                }

                WebNavSection.DRIVER_CONSOLE -> {
                    DriverCockpitScreen(
                        shuttles = shuttles,
                        selectedPointId = currentBus?.id ?: "POINT_27",
                        stopRequests = stopRequests,
                        onSelectPoint = { ptId ->
                            shuttles.find { it.id == ptId }?.let { onSelectShuttle(it) }
                        },
                        onUpdateSeats = onUpdateSeats,
                        onSetPreset = onSetPreset,
                        onAdvanceStop = onAdvanceStop,
                        onBroadcastAlert = onBroadcastAlert,
                        onAcknowledgeRequest = {},
                        onCompleteRequest = {}
                    )
                }

                WebNavSection.ADMIN_COMMAND -> {
                    AdminFleetDashboard(
                        shuttles = shuttles,
                        routes = routes,
                        requests = stopRequests,
                        onBroadcastEmergencyNotice = { t, m -> onBroadcastAlert(t, m, "CRITICAL") }
                    )
                }
            }
        }

        // 4. Web Portal Footer
        Surface(
            color = Color(0xFF0F172A),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "© 2026 Mehran University of Engineering and Technology, Jamshoro",
                    style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF94A3B8), fontSize = 9.sp)
                )
                Text(
                    text = "Directorate of Transport • Control Desk +92 22 2772250",
                    style = MaterialTheme.typography.labelSmall.copy(color = MuetGold, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

@Composable
fun WebTrackingPortalView(
    shuttles: List<ShuttlePoint>,
    stops: List<StopPointEntity>,
    alerts: List<TrafficAlertEntity>,
    currentBus: ShuttlePoint?,
    onSelectShuttle: (ShuttlePoint) -> Unit,
    onRequestStop: (ShuttlePoint) -> Unit,
    onDismissAlert: (Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Welcome Web Hero Banner
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Jamshoro – Hyderabad Transit Live Desk",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, color = MuetPrimary)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Real-time GPS positioning, seat availability counters, and automated bridge delay updates for all active MUET Point shuttles.",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF1F5F9),
                        modifier = Modifier.size(54.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            MuetBusGraphic(
                                modifier = Modifier.size(50.dp, 30.dp),
                                pointNumber = currentBus?.pointNumber ?: "27",
                                isMoving = true
                            )
                        }
                    }
                }
            }
        }

        // Active Alerts on Web Portal
        if (alerts.isNotEmpty()) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    alerts.forEach { alert ->
                        TrafficAlertCard(alert = alert, onDismiss = { onDismissAlert(alert.id) })
                    }
                }
            }
        }

        // Interactive Live Map Component
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Map, contentDescription = null, tint = MuetPrimary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Live Hyderabad & Jamshoro Transit Corridor",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        Text(
                            text = "River Indus / M-9 Bypass",
                            style = MaterialTheme.typography.labelSmall.copy(color = MuetSecondary, fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    HyderabadMapCanvas(
                        shuttles = shuttles,
                        stops = stops,
                        selectedShuttle = currentBus,
                        onShuttleClick = onSelectShuttle,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                    )
                }
            }
        }

        // Active Shuttle Fleet Web Cards
        item {
            Text(
                text = "Live Point Shuttle Availability",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = MuetPrimary)
            )
        }

        items(shuttles) { bus ->
            val isSelected = bus.id == currentBus?.id
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) Color(0xFFF0F7FF) else Color.White
                ),
                border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, MuetPrimary) else null,
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectShuttle(bus) }
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) MuetPrimary else Color(0xFFF1F5F9)
                            ) {
                                Text(
                                    text = bus.pointNumber,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (isSelected) Color.White else MuetPrimary
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = bus.routeName,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "Plate: ${bus.busPlate} • Driver: ${bus.driverName}",
                                    style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = when {
                                bus.availableSeats == 0 -> MuetRed
                                bus.availableSeats <= 8 -> MuetOrange
                                else -> MuetGreen
                            }
                        ) {
                            Text(
                                text = bus.seatStatus.uppercase(),
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Progress bar
                    LinearProgressIndicator(
                        progress = { bus.occupancyPercentage },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = when {
                            bus.availableSeats == 0 -> MuetRed
                            bus.availableSeats <= 8 -> MuetOrange
                            else -> MuetGreen
                        },
                        trackColor = Color(0xFFE2E8F0)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Current: ${bus.currentStopName} → ${bus.nextStopName}",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF334155))
                        )

                        OutlinedButton(
                            onClick = { onRequestStop(bus) },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("Request Stop", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }
    }
}
