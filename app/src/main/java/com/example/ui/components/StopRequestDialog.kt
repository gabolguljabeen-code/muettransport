package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.ShuttlePoint
import com.example.data.model.StopPointEntity
import com.example.ui.theme.MuetPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StopRequestDialog(
    bus: ShuttlePoint,
    stops: List<StopPointEntity>,
    onDismiss: () -> Unit,
    onSubmit: (pointId: String, stopName: String, type: String, note: String) -> Unit
) {
    var requestType by remember { mutableStateOf("PICKUP") }
    val relevantStops = stops.filter { it.routeId == bus.routeId }.ifEmpty { stops }
    var selectedStop by remember { mutableStateOf(relevantStops.firstOrNull()?.stopName ?: "Qasim Chowk") }
    var stopDropdownExpanded by remember { mutableStateOf(false) }
    var note by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MuetPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Request Stop on ${bus.pointNumber}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Driver (${bus.driverName}) will be notified on their dashboard before reaching this stop.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = requestType == "PICKUP",
                        onClick = { requestType = "PICKUP" },
                        label = { Text("Pickup") },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = requestType == "DROP_OFF",
                        onClick = { requestType = "DROP_OFF" },
                        label = { Text("Drop-off") },
                        modifier = Modifier.weight(1f)
                    )
                }

                ExposedDropdownMenuBox(
                    expanded = stopDropdownExpanded,
                    onExpandedChange = { stopDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedStop,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Select Stop") },
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
                                    selectedStop = s.stopName
                                    stopDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Note for driver (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSubmit(bus.id, selectedStop, requestType, note)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = MuetPrimary)
            ) {
                Text("Confirm Request")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
