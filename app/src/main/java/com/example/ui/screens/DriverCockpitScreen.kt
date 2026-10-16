package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import com.example.data.model.ShuttlePoint
import com.example.data.model.StopRequestEntity
import com.example.ui.components.MuetBusGraphic
import com.example.ui.theme.MuetGold
import com.example.ui.theme.MuetGreen
import com.example.ui.theme.MuetOrange
import com.example.ui.theme.MuetPrimary
import com.example.ui.theme.MuetRed
import com.example.ui.theme.MuetSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverCockpitScreen(
    shuttles: List<ShuttlePoint>,
    selectedPointId: String,
    stopRequests: List<StopRequestEntity>,
    onSelectPoint: (String) -> Unit,
    onUpdateSeats: (pointId: String, deltaSeats: Int, deltaStanding: Int) -> Unit,
    onSetPreset: (pointId: String, preset: String) -> Unit,
    onAdvanceStop: (pointId: String) -> Unit,
    onBroadcastAlert: (title: String, message: String, severity: String) -> Unit,
    onAcknowledgeRequest: (Long) -> Unit,
    onCompleteRequest: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentBus = shuttles.find { it.id == selectedPointId } ?: shuttles.firstOrNull()
    var pointDropdownExpanded by remember { mutableStateOf(false) }
    var showDelayDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("driver_cockpit_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Driver Header & Bus Selector
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MuetPrimary),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "DRIVER COCKPIT",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MuetGold,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.sp
                                )
                            )
                            Text(
                                text = currentBus?.driverName ?: "Ustad Ghulam Rasool",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = currentBus?.pointNumber ?: "Point 27",
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Switch Bus Dropdown
                    ExposedDropdownMenuBox(
                        expanded = pointDropdownExpanded,
                        onExpandedChange = { pointDropdownExpanded = it }
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White,
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.DirectionsBus,
                                        contentDescription = null,
                                        tint = MuetPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "${currentBus?.pointNumber} (${currentBus?.busPlate}) - ${currentBus?.routeName}",
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = MuetPrimary)
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.NavigateNext,
                                    contentDescription = null,
                                    tint = MuetPrimary
                                )
                            }
                        }

                        ExposedDropdownMenu(
                            expanded = pointDropdownExpanded,
                            onDismissRequest = { pointDropdownExpanded = false }
                        ) {
                            shuttles.forEach { bus ->
                                DropdownMenuItem(
                                    text = { Text("${bus.pointNumber} (${bus.busPlate}) - ${bus.driverName}") },
                                    onClick = {
                                        onSelectPoint(bus.id)
                                        pointDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Live Seat Availability Controller
        if (currentBus != null) {
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier.testTag("seat_controller_card")
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Live Seat Availability Control",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "Tap to update student app in real-time",
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = when {
                                    currentBus.availableSeats == 0 -> MuetRed
                                    currentBus.availableSeats <= 8 -> MuetOrange
                                    else -> MuetGreen
                                }
                            ) {
                                Text(
                                    text = currentBus.seatStatus.uppercase(),
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Large Numeric Counters
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Seated Counter
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${currentBus.occupiedSeats}",
                                    style = MaterialTheme.typography.headlineLarge.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MuetPrimary
                                    )
                                )
                                Text(
                                    text = "Occupied Seats (of ${currentBus.totalSeats})",
                                    style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                                Text(
                                    text = "${currentBus.availableSeats} Available",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (currentBus.availableSeats == 0) MuetRed else MuetGreen
                                    )
                                )
                            }

                            // Standing Counter
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${currentBus.standingPassengers}",
                                    style = MaterialTheme.typography.headlineLarge.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MuetOrange
                                    )
                                )
                                Text(
                                    text = "Standing Passengers",
                                    style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Progress bar
                        LinearProgressIndicator(
                            progress = { currentBus.occupancyPercentage },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(5.dp)),
                            color = when {
                                currentBus.availableSeats == 0 -> MuetRed
                                currentBus.availableSeats <= 8 -> MuetOrange
                                else -> MuetGreen
                            },
                            trackColor = Color(0xFFE2E8F0)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Quick +/- Controls for Seats
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { onUpdateSeats(currentBus.id, -5, 0) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5F9), contentColor = MuetPrimary)
                            ) {
                                Text("-5 Seats", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                            }

                            Button(
                                onClick = { onUpdateSeats(currentBus.id, -1, 0) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5F9), contentColor = MuetPrimary)
                            ) {
                                Icon(imageVector = Icons.Default.Remove, contentDescription = "-1")
                            }

                            Button(
                                onClick = { onUpdateSeats(currentBus.id, 1, 0) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MuetPrimary)
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = "+1")
                            }

                            Button(
                                onClick = { onUpdateSeats(currentBus.id, 5, 0) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MuetPrimary)
                            ) {
                                Text("+5 Seats", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Presets Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = currentBus.availableSeats > 15,
                                onClick = { onSetPreset(currentBus.id, "AVAILABLE") },
                                label = { Text("🟢 Vacant") },
                                modifier = Modifier.weight(1f)
                            )
                            FilterChip(
                                selected = currentBus.availableSeats in 1..15,
                                onClick = { onSetPreset(currentBus.id, "MODERATE") },
                                label = { Text("🟡 Normal") },
                                modifier = Modifier.weight(1f)
                            )
                            FilterChip(
                                selected = currentBus.availableSeats == 0,
                                onClick = { onSetPreset(currentBus.id, "FULL") },
                                label = { Text("🔴 Bus Full") },
                                modifier = Modifier.weight(1f),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MuetRed,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            // Route Stops Advancement Card
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Route Progress & GPS Checkpoint",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF1F5F9),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "CURRENT STOP",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    )
                                    Text(
                                        text = currentBus.currentStopName,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = MuetPrimary)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Next: ${currentBus.nextStopName}",
                                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    )
                                }

                                Button(
                                    onClick = { onAdvanceStop(currentBus.id) },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MuetSecondary)
                                ) {
                                    Icon(imageVector = Icons.Default.NavigateNext, contentDescription = null)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Depart to Next")
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Broadcast Delay Button
                        OutlinedButton(
                            onClick = { showDelayDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = MuetOrange,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Broadcast Traffic / Delay Alert",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = MuetOrange)
                            )
                        }
                    }
                }
            }
        }

        // Incoming Student Pickup / Drop-off Requests for this bus
        item {
            Text(
                text = "Incoming Passenger Requests",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        val busRequests = stopRequests.filter { it.pointId == selectedPointId }
        if (busRequests.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFF8FAFC),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.padding(20.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No pending boarding requests for this point.",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                }
            }
        } else {
            items(busRequests) { req ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = MuetPrimary.copy(alpha = 0.12f),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = null,
                                            tint = MuetPrimary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = req.studentName,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = "Roll: ${req.studentRollNo} • ${req.requestType} at ${req.stopName}",
                                        style = MaterialTheme.typography.labelSmall.copy(color = MuetPrimary, fontWeight = FontWeight.SemiBold)
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = when (req.status) {
                                    "COMPLETED" -> MuetGreen.copy(alpha = 0.15f)
                                    "ACKNOWLEDGED" -> MuetSecondary.copy(alpha = 0.15f)
                                    else -> MuetOrange.copy(alpha = 0.15f)
                                }
                            ) {
                                Text(
                                    text = req.status,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when (req.status) {
                                            "COMPLETED" -> MuetGreen
                                            "ACKNOWLEDGED" -> MuetSecondary
                                            else -> MuetOrange
                                        }
                                    )
                                )
                            }
                        }

                        if (req.note.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "\"${req.note}\"",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF475569), fontSize = 11.sp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Action Buttons for Driver
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            if (req.status != "ACKNOWLEDGED" && req.status != "COMPLETED") {
                                OutlinedButton(
                                    onClick = { onAcknowledgeRequest(req.id) },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Acknowledge", style = MaterialTheme.typography.labelSmall)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                            }

                            if (req.status != "COMPLETED") {
                                Button(
                                    onClick = { onCompleteRequest(req.id) },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MuetGreen)
                                ) {
                                    Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Passenger Boarded", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Delay Alert Modal Dialog
    if (showDelayDialog) {
        var alertTitle by remember { mutableStateOf("Jamshoro Bridge Traffic Delay") }
        var alertMsg by remember { mutableStateOf("Heavy truck movement causing +12 min delay approaching Jamshoro Toll.") }
        var severity by remember { mutableStateOf("WARNING") }

        AlertDialog(
            onDismissRequest = { showDelayDialog = false },
            title = {
                Text(
                    text = "Broadcast Traffic & Delay Notice",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Quick Presets:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        FilterChip(
                            selected = alertTitle.contains("Bridge"),
                            onClick = {
                                alertTitle = "Jamshoro Bridge Traffic Delay"
                                alertMsg = "Heavy vehicular jam on Jamshoro bridge. Point delayed by +12 mins."
                            },
                            label = { Text("Bridge Jam", fontSize = 10.sp) }
                        )
                        FilterChip(
                            selected = alertTitle.contains("Highway"),
                            onClick = {
                                alertTitle = "Indus Highway Roadwork Delay"
                                alertMsg = "Road repair work near Sheedi Goth. Point moving at reduced speed (+15 mins)."
                            },
                            label = { Text("Roadwork", fontSize = 10.sp) }
                        )
                    }

                    OutlinedTextField(
                        value = alertTitle,
                        onValueChange = { alertTitle = it },
                        label = { Text("Alert Title") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = alertMsg,
                        onValueChange = { alertMsg = it },
                        label = { Text("Notice Message") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onBroadcastAlert(alertTitle, alertMsg, severity)
                        showDelayDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MuetOrange)
                ) {
                    Text("Broadcast Alert")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDelayDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
