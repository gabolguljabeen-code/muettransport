package com.example.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BoardingPassEntity
import com.example.ui.components.MuetBusGraphic
import com.example.ui.theme.MuetGold
import com.example.ui.theme.MuetGreen
import com.example.ui.theme.MuetPrimary
import com.example.ui.theme.MuetRed
import com.example.ui.theme.MuetSecondary

@Composable
fun BoardingPassScreen(
    pass: BoardingPassEntity?,
    onTriggerSos: () -> Unit,
    modifier: Modifier = Modifier
) {
    val studentPass = pass ?: BoardingPassEntity(
        studentRollNo = "21SW089",
        studentName = "Gul Jabeen Gabol",
        department = "Department of Software Engineering",
        batch = "21SW (Final Year)",
        routeName = "Route 1: Latifabad / Qasimabad",
        preferredStop = "Qasim Chowk",
        semesterPassType = "Regular Transport Pass",
        validUntil = "31-Dec-2026",
        passStatus = "ACTIVE",
        qrCodeHash = "MUET-PASS-21SW089-R01-VERIFIED-2026",
        feeReceiptNo = "TR-88921-MUET"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("boarding_pass_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Digital Pass Card
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("student_digital_pass_card")
            ) {
                Column {
                    // University Header Banner
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MuetPrimary)
                            .padding(18.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "MEHRAN UNIVERSITY",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color.White,
                                            letterSpacing = 1.sp
                                        )
                                    )
                                    Text(
                                        text = "OF ENGINEERING & TECHNOLOGY, JAMSHORO",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = MuetGold,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp
                                        )
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MuetGreen
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Verified,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "VERIFIED",
                                            color = Color.White,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 9.sp
                                            )
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "STUDENT POINT BOARDING PASS • FALL 2026",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }

                    // Student Details Section
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                modifier = Modifier.size(56.dp),
                                shape = CircleShape,
                                color = MuetPrimary.copy(alpha = 0.12f)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = MuetPrimary,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Text(
                                    text = studentPass.studentName,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)
                                )
                                Text(
                                    text = "Roll No: ${studentPass.studentRollNo}",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = MuetPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    text = studentPass.department,
                                    style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        HorizontalDivider(color = Color(0xFFE2E8F0))

                        Spacer(modifier = Modifier.height(14.dp))

                        // Route & Stop Information
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "ASSIGNED ROUTE",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                                Text(
                                    text = studentPass.routeName,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "DESIGNATED STOP",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                                Text(
                                    text = studentPass.preferredStop,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = MuetPrimary)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "FEE RECEIPT TOKEN",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                                Text(
                                    text = studentPass.feeReceiptNo,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "VALID UNTIL",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                                Text(
                                    text = studentPass.validUntil,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold, color = MuetGreen)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Scannable Dynamic QR Code representation
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF8FAFC), shape = RoundedCornerShape(16.dp))
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Canvas(modifier = Modifier.size(150.dp)) {
                                    val s = size.width
                                    val cellSize = s / 15f

                                    // Draw Finder Patterns (Top-Left, Top-Right, Bottom-Left)
                                    val finders = listOf(
                                        Offset(0f, 0f),
                                        Offset(s - cellSize * 5, 0f),
                                        Offset(0f, s - cellSize * 5)
                                    )
                                    finders.forEach { corner ->
                                        // Outer box
                                        drawRect(
                                            color = Color.Black,
                                            topLeft = corner,
                                            size = Size(cellSize * 5, cellSize * 5)
                                        )
                                        // Inner white
                                        drawRect(
                                            color = Color.White,
                                            topLeft = Offset(corner.x + cellSize, corner.y + cellSize),
                                            size = Size(cellSize * 3, cellSize * 3)
                                        )
                                        // Center black dot
                                        drawRect(
                                            color = Color.Black,
                                            topLeft = Offset(corner.x + cellSize * 2, corner.y + cellSize * 2),
                                            size = Size(cellSize, cellSize)
                                        )
                                    }

                                    // Seeded pseudo-random matrix cells for authentic QR look
                                    val hash = studentPass.qrCodeHash.hashCode()
                                    for (r in 0 until 15) {
                                        for (c in 0 until 15) {
                                            // Skip finder zones
                                            val inTL = r < 5 && c < 5
                                            val inTR = r < 5 && c >= 10
                                            val inBL = r >= 10 && c < 5
                                            if (!inTL && !inTR && !inBL) {
                                                val bit = ((hash xor (r * 31 + c * 17)) and 1) == 0
                                                if (bit) {
                                                    drawRect(
                                                        color = Color(0xFF0F172A),
                                                        topLeft = Offset(c * cellSize, r * cellSize),
                                                        size = Size(cellSize - 1f, cellSize - 1f)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.QrCodeScanner,
                                        contentDescription = null,
                                        tint = MuetPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Scan at Point Conductor Entry Gate",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = MuetPrimary)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Emergency SOS & Directorate Support
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Emergency,
                                contentDescription = null,
                                tint = MuetRed,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Directorate of Transport Hotline",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = MuetRed)
                            )
                        }

                        Button(
                            onClick = onTriggerSos,
                            colors = ButtonDefaults.buttonColors(containerColor = MuetRed),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("SOS HELP", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold))
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Jamshoro Campus Control Office: +92 22 2772250 • Available 06:30 AM – 08:00 PM for breakdown, medical, or missing item assistance.",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF7F1D1D), fontSize = 11.sp)
                    )
                }
            }
        }
    }
}
