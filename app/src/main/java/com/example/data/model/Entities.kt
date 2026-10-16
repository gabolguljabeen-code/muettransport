package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shuttle_points")
data class ShuttlePoint(
    @PrimaryKey
    val id: String,                    // e.g. "POINT_27"
    val pointNumber: String,          // e.g. "Point 27"
    val busPlate: String,             // e.g. "EB-0102"
    val routeId: String,              // e.g. "R_LATIFABAD"
    val routeName: String,            // e.g. "Latifabad to MUET Campus"
    val driverName: String,           // e.g. "Ustad Ghulam Rasool"
    val driverPhone: String,          // e.g. "+92 300 1234567"
    val totalSeats: Int = 52,
    val occupiedSeats: Int = 38,
    val standingPassengers: Int = 5,
    val status: String = "ON_ROUTE",  // ON_ROUTE, AT_CAMPUS, DEPARTING, DELAYED
    val currentStopName: String = "Qasim Chowk",
    val nextStopName: String = "Naseem Nagar",
    val latitude: Double = 25.3960,
    val longitude: Double = 68.3450,
    val speedKmph: Int = 42,
    val delayMinutes: Int = 0,
    val shift: String = "MORNING",    // MORNING, AFTERNOON, EVENING
    val lastUpdated: Long = System.currentTimeMillis()
) {
    val availableSeats: Int get() = (totalSeats - occupiedSeats).coerceAtLeast(0)
    val occupancyPercentage: Float get() = (occupiedSeats.toFloat() / totalSeats.toFloat()).coerceIn(0f, 1f)
    val seatStatus: String get() = when {
        availableSeats == 0 -> "Bus Full"
        availableSeats <= 8 -> "Limited Seats ($availableSeats left)"
        else -> "$availableSeats Seats Available"
    }
}

@Entity(tableName = "routes")
data class RouteEntity(
    @PrimaryKey
    val id: String,
    val routeName: String,
    val code: String,                 // e.g. "R-01"
    val startLocation: String,        // e.g. "Latifabad Unit 7"
    val endLocation: String,          // e.g. "MUET Admin / Central Library"
    val via: String,                  // e.g. "Auto Bhan - Qasimabad - Jamshoro Bridge"
    val totalStops: Int,
    val morningDeparture: String,     // "07:15 AM"
    val afternoonDeparture: String,   // "01:45 PM"
    val eveningDeparture: String,     // "05:15 PM"
    val distanceKm: Double,
    val estimatedMinutes: Int
)

@Entity(tableName = "stop_points")
data class StopPointEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val routeId: String,
    val stopName: String,
    val landmark: String,
    val orderIndex: Int,
    val scheduledTime: String,
    val latitude: Double,
    val longitude: Double,
    val isCampusStop: Boolean = false
)

@Entity(tableName = "boarding_passes")
data class BoardingPassEntity(
    @PrimaryKey
    val studentRollNo: String,        // e.g. "21SW089"
    val studentName: String,
    val department: String,           // e.g. "Software Engineering"
    val batch: String,                // e.g. "21-Batch"
    val routeName: String,            // e.g. "Route 1: Latifabad / Qasimabad"
    val preferredStop: String,        // e.g. "Auto Bhan Chowk"
    val semesterPassType: String,     // "Semester Transport Pass"
    val validUntil: String,           // "31 Dec 2026"
    val passStatus: String = "ACTIVE",// ACTIVE, EXPIRED, SUSPENDED
    val qrCodeHash: String,
    val feeReceiptNo: String
)

@Entity(tableName = "stop_requests")
data class StopRequestEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val studentRollNo: String,
    val studentName: String,
    val pointId: String,
    val stopName: String,
    val requestType: String,          // "PICKUP" or "DROP_OFF"
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "PENDING",   // PENDING, ACKNOWLEDGED, COMPLETED
    val note: String = ""
)

@Entity(tableName = "traffic_alerts")
data class TrafficAlertEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val pointNumber: String,
    val routeName: String,
    val title: String,
    val message: String,
    val severity: String = "WARNING", // INFO, WARNING, CRITICAL
    val timestamp: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
)
