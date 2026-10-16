package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ShuttlePoint
import com.example.data.model.StopPointEntity
import com.example.data.model.StopRequestEntity
import com.example.ui.theme.MuetGold
import com.example.ui.theme.MuetGreen
import com.example.ui.theme.MuetOrange
import com.example.ui.theme.MuetPrimary
import com.example.ui.theme.MuetSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StopRequestsScreen(
    requests: List<StopRequestEntity>,
    shuttles: List<ShuttlePoint>,
    stops: List<StopPointEntity>,
    onSubmitRequest: (pointId: String, stopName: String, type: String, note: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedPointId by remember { mutableStateOf(shuttles.firstOrNull()?.id ?: "POINT_27") }
    var selectedStopName by remember { mutableStateOf("Qasim Chowk") }
    var requestType by remember { mutableStateOf("PICKUP") }
    var noteText by remember { mutableStateOf("") }

    var pointDropdownExpanded by remember { mutableStateOf(false) }
    var stopDropdownExpanded by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("stop_requests_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Form Card to Submit Request
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Request Stop Point for Pickup / Drop-Off",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = MuetPrimary)
                    )
                    Text(
                        text = "Driver will be alerted of your boarding location before approaching.",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Request Type Toggle (Pickup vs Drop-Off)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FilterChip(
                            selected = requestType == "PICKUP",
                            onClick = { requestType = "PICKUP" },
                            label = { Text("🚏 Morning Pickup") },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = requestType == "DROP_OFF",
                            onClick = { requestType = "DROP_OFF" },
                            label = { Text("🏠 Evening Drop-off") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Select Point Shuttle Dropdown
                    ExposedDropdownMenuBox(
                        expanded = pointDropdownExpanded,
                        onExpandedChange = { pointDropdownExpanded = it }
                    ) {
                        val currentBus = shuttles.find { it.id == selectedPointId }
                        OutlinedTextField(
                            value = "${currentBus?.pointNumber ?: "Select Point"} (${currentBus?.busPlate ?: ""})",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Target MUET Point Shuttle") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = pointDropdownExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = pointDropdownExpanded,
                            onDismissRequest = { pointDropdownExpanded = false }
                        ) {
                            shuttles.forEach { bus ->
                                DropdownMenuItem(
                                    text = { Text("${bus.pointNumber} - ${bus.routeName}") },
                                    onClick = {
                                        selectedPointId = bus.id
                                        pointDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Select Stop Dropdown
                    val currentBus = shuttles.find { it.id == selectedPointId }
                    val relevantStops = stops.filter { it.routeId == currentBus?.routeId }.ifEmpty { stops }

                    ExposedDropdownMenuBox(
                        expanded = stopDropdownExpanded,
                        onExpandedChange = { stopDropdownExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedStopName,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Designated Pickup / Drop-off Stop") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = stopDropdownExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = stopDropdownExpanded,
                            onDismissRequest = { stopDropdownExpanded = false }
                        ) {
                            relevantStops.forEach { s ->
                                DropdownMenuItem(
                                    text = { Text("${s.stopName} (${s.landmark})") },
                                    onClick = {
                                        selectedStopName = s.stopName
                                        stopDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Optional Note
                    OutlinedTextField(
                        value = noteText,
                        onValueChange = { noteText = it },
                        label = { Text("Note for Driver (e.g., waiting at pharmacy entrance)") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            onSubmitRequest(selectedPointId, selectedStopName, requestType, noteText)
                            noteText = ""
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("submit_stop_request_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MuetPrimary)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Send Request to Driver", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }
        }

        // List of Active / Past Requests
        item {
            Text(
                text = "My Stop Point Requests",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        if (requests.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFF8FAFC),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No active stop requests. Submit above to notify your driver.",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                }
            }
        } else {
            items(requests) { req ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (req.requestType == "PICKUP") MuetPrimary.copy(alpha = 0.12f) else MuetGold.copy(alpha = 0.15f)
                            ) {
                                Box(modifier = Modifier.padding(8.dp)) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = if (req.requestType == "PICKUP") MuetPrimary else MuetGold,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = "${if (req.requestType == "PICKUP") "Pickup at" else "Drop-off at"} ${req.stopName}",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "Target: ${req.pointId.replace("_", " ")}",
                                    style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                                if (req.note.isNotBlank()) {
                                    Text(
                                        text = "\"${req.note}\"",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, color = Color(0xFF475569))
                                    )
                                }
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = when (req.status) {
                                "COMPLETED" -> MuetGreen.copy(alpha = 0.15f)
                                "ACKNOWLEDGED" -> MuetSecondary.copy(alpha = 0.15f)
                                else -> MuetOrange.copy(alpha = 0.15f)
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = when (req.status) {
                                        "COMPLETED" -> Icons.Default.Check
                                        "ACKNOWLEDGED" -> Icons.Default.DirectionsBus
                                        else -> Icons.Default.HourglassTop
                                    },
                                    contentDescription = null,
                                    tint = when (req.status) {
                                        "COMPLETED" -> MuetGreen
                                        "ACKNOWLEDGED" -> MuetSecondary
                                        else -> MuetOrange
                                    },
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = req.status,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp,
                                        color = when (req.status) {
                                            "COMPLETED" -> MuetGreen
                                            "ACKNOWLEDGED" -> MuetSecondary
                                            else -> MuetOrange
                                        }
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
