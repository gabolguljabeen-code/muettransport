package com.example.data.repository

import com.example.data.database.AppDatabase
import com.example.data.model.BoardingPassEntity
import com.example.data.model.RouteEntity
import com.example.data.model.ShuttlePoint
import com.example.data.model.StopPointEntity
import com.example.data.model.StopRequestEntity
import com.example.data.model.TrafficAlertEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class ShuttleRepository(private val database: AppDatabase) {

    private val shuttleDao = database.shuttleDao()
    private val routeDao = database.routeDao()
    private val boardingPassDao = database.boardingPassDao()
    private val stopRequestDao = database.stopRequestDao()
    private val trafficAlertDao = database.trafficAlertDao()

    val allShuttles: Flow<List<ShuttlePoint>> = shuttleDao.getAllShuttles()
    val allRoutes: Flow<List<RouteEntity>> = routeDao.getAllRoutes()
    val allStops: Flow<List<StopPointEntity>> = routeDao.getAllStops()
    val primaryBoardingPass: Flow<BoardingPassEntity?> = boardingPassDao.getPrimaryStudentPass()
    val stopRequests: Flow<List<StopRequestEntity>> = stopRequestDao.getAllStopRequests()
    val activeAlerts: Flow<List<TrafficAlertEntity>> = trafficAlertDao.getActiveAlerts()

    suspend fun seedInitialDataIfNeeded() {
        val existingRoutes = routeDao.getAllRoutes().firstOrNull()
        if (existingRoutes.isNullOrEmpty()) {
            // Seed Routes
            val routes = listOf(
                RouteEntity(
                    id = "R_LATIFABAD",
                    routeName = "Route 1: Latifabad & Qasimabad Express",
                    code = "R-01",
                    startLocation = "Latifabad Unit 7",
                    endLocation = "MUET Auditorium / Admin",
                    via = "Auto Bhan - Qasim Chowk - Naseem Nagar - Jamshoro Toll",
                    totalStops = 10,
                    morningDeparture = "07:15 AM",
                    afternoonDeparture = "01:45 PM",
                    eveningDeparture = "05:15 PM",
                    distanceKm = 24.5,
                    estimatedMinutes = 55
                ),
                RouteEntity(
                    id = "R_SADDAR",
                    routeName = "Route 2: Saddar Cantt & Thandi Sarak",
                    code = "R-02",
                    startLocation = "Saddar Cantonment",
                    endLocation = "MUET Central Library",
                    via = "Gari Khata - Radio Pak - Thandi Sarak - Hyderabad Bypass",
                    totalStops = 8,
                    morningDeparture = "07:20 AM",
                    afternoonDeparture = "01:45 PM",
                    eveningDeparture = "05:15 PM",
                    distanceKm = 21.0,
                    estimatedMinutes = 48
                ),
                RouteEntity(
                    id = "R_AUTOBHAN",
                    routeName = "Route 3: Auto Bhan & Citizen Colony",
                    code = "R-03",
                    startLocation = "Citizen Colony Gate",
                    endLocation = "MUET Engineering Complex",
                    via = "Auto Bhan Commercial - Wadhu Wah - Indus Highway",
                    totalStops = 7,
                    morningDeparture = "07:25 AM",
                    afternoonDeparture = "01:45 PM",
                    eveningDeparture = "05:15 PM",
                    distanceKm = 22.8,
                    estimatedMinutes = 50
                ),
                RouteEntity(
                    id = "R_KOTRI",
                    routeName = "Route 4: Kotri Barrage & Al-Manzar",
                    code = "R-04",
                    startLocation = "Kotri Barrage Colony",
                    endLocation = "MUET Main Gate & Girls Hostel",
                    via = "Al-Manzar - Sindh University Rd - Zero Point",
                    totalStops = 6,
                    morningDeparture = "07:30 AM",
                    afternoonDeparture = "01:45 PM",
                    eveningDeparture = "05:15 PM",
                    distanceKm = 16.4,
                    estimatedMinutes = 35
                )
            )
            routeDao.insertRoutes(routes)

            // Seed Stops
            val stops = listOf(
                StopPointEntity(routeId = "R_LATIFABAD", stopName = "Latifabad Unit 7", landmark = "Near Chandia Mor", orderIndex = 1, scheduledTime = "07:15 AM", latitude = 25.3620, longitude = 68.3680),
                StopPointEntity(routeId = "R_LATIFABAD", stopName = "Unit 8 Market", landmark = "Al-Rahim Shopping", orderIndex = 2, scheduledTime = "07:22 AM", latitude = 25.3700, longitude = 68.3610),
                StopPointEntity(routeId = "R_LATIFABAD", stopName = "Auto Bhan Towers", landmark = "Boulevard Mall Mor", orderIndex = 3, scheduledTime = "07:30 AM", latitude = 25.3800, longitude = 68.3540),
                StopPointEntity(routeId = "R_LATIFABAD", stopName = "Qasim Chowk", landmark = "Main Roundabout", orderIndex = 4, scheduledTime = "07:40 AM", latitude = 25.3960, longitude = 68.3450),
                StopPointEntity(routeId = "R_LATIFABAD", stopName = "Naseem Nagar Chowk", landmark = "Citizen Plaza", orderIndex = 5, scheduledTime = "07:48 AM", latitude = 25.4050, longitude = 68.3370),
                StopPointEntity(routeId = "R_LATIFABAD", stopName = "Wadhu Wah Phir", landmark = "Canal Bridge", orderIndex = 6, scheduledTime = "07:55 AM", latitude = 25.4130, longitude = 68.3280),
                StopPointEntity(routeId = "R_LATIFABAD", stopName = "Sheedi Goth Cut", landmark = "Highway Entry", orderIndex = 7, scheduledTime = "08:05 AM", latitude = 25.4210, longitude = 68.3120),
                StopPointEntity(routeId = "R_LATIFABAD", stopName = "Jamshoro Toll Plaza", landmark = "River Indus Bridge", orderIndex = 8, scheduledTime = "08:15 AM", latitude = 25.4300, longitude = 68.2880),
                StopPointEntity(routeId = "R_LATIFABAD", stopName = "MUET Main Gate", landmark = "Welcome Arch", orderIndex = 9, scheduledTime = "08:25 AM", latitude = 25.4050, longitude = 68.2620, isCampusStop = true),
                StopPointEntity(routeId = "R_LATIFABAD", stopName = "MUET Auditorium & Admin", landmark = "Central Campus", orderIndex = 10, scheduledTime = "08:30 AM", latitude = 25.4020, longitude = 68.2580, isCampusStop = true)
            )
            routeDao.insertStops(stops)

            // Seed Iconic MUET Points (Matches Point 27, 23, 30 from user images!)
            val shuttles = listOf(
                ShuttlePoint(
                    id = "POINT_27",
                    pointNumber = "Point 27",
                    busPlate = "EB-0109",
                    routeId = "R_LATIFABAD",
                    routeName = "Route 1: Latifabad / Qasimabad",
                    driverName = "Ustad Ghulam Rasool",
                    driverPhone = "+92 300 7862700",
                    totalSeats = 52,
                    occupiedSeats = 38,
                    standingPassengers = 4,
                    status = "ON_ROUTE",
                    currentStopName = "Qasim Chowk",
                    nextStopName = "Naseem Nagar",
                    latitude = 25.3960,
                    longitude = 68.3450,
                    speedKmph = 42,
                    delayMinutes = 0
                ),
                ShuttlePoint(
                    id = "POINT_23",
                    pointNumber = "Point 23",
                    busPlate = "EB-0102",
                    routeId = "R_SADDAR",
                    routeName = "Route 2: Saddar Cantt / Thandi Sarak",
                    driverName = "Ustad Allah Dino Solangi",
                    driverPhone = "+92 301 5552300",
                    totalSeats = 52,
                    occupiedSeats = 50,
                    standingPassengers = 12,
                    status = "ON_ROUTE",
                    currentStopName = "Thandi Sarak",
                    nextStopName = "Jamshoro Toll Plaza",
                    latitude = 25.4120,
                    longitude = 68.3180,
                    speedKmph = 55,
                    delayMinutes = 4
                ),
                ShuttlePoint(
                    id = "POINT_30",
                    pointNumber = "Point 30",
                    busPlate = "EB-0110",
                    routeId = "R_AUTOBHAN",
                    routeName = "Route 3: Auto Bhan & Citizen Colony",
                    driverName = "Ustad Muhammad Ismail",
                    driverPhone = "+92 302 9993000",
                    totalSeats = 52,
                    occupiedSeats = 26,
                    standingPassengers = 0,
                    status = "ON_ROUTE",
                    currentStopName = "Auto Bhan Towers",
                    nextStopName = "Wadhu Wah Road",
                    latitude = 25.3800,
                    longitude = 68.3540,
                    speedKmph = 38,
                    delayMinutes = 0
                ),
                ShuttlePoint(
                    id = "POINT_14",
                    pointNumber = "Point 14",
                    busPlate = "EB-0094",
                    routeId = "R_KOTRI",
                    routeName = "Route 4: Kotri Barrage / Al-Manzar",
                    driverName = "Ustad Tariq Brohi",
                    driverPhone = "+92 303 4441400",
                    totalSeats = 52,
                    occupiedSeats = 45,
                    standingPassengers = 6,
                    status = "ON_ROUTE",
                    currentStopName = "Al-Manzar Point",
                    nextStopName = "Sindh Univ Gate",
                    latitude = 25.3650,
                    longitude = 68.3050,
                    speedKmph = 46,
                    delayMinutes = 0
                )
            )
            shuttleDao.insertShuttles(shuttles)

            // Seed Boarding Pass
            val pass = BoardingPassEntity(
                studentRollNo = "21SW089",
                studentName = "Gul Jabeen Gabol",
                department = "Software Engineering",
                batch = "21SW (Final Year)",
                routeName = "Route 1: Latifabad / Qasimabad",
                preferredStop = "Qasim Chowk",
                semesterPassType = "Regular Transport Pass",
                validUntil = "31-Dec-2026",
                passStatus = "ACTIVE",
                qrCodeHash = "MUET-PASS-21SW089-R01-VERIFIED-2026",
                feeReceiptNo = "TR-88921-MUET"
            )
            boardingPassDao.insertPass(pass)

            // Seed Initial Traffic Alerts
            val alerts = listOf(
                TrafficAlertEntity(
                    pointNumber = "Point 27 & 23",
                    routeName = "Route 1 & 2",
                    title = "Jamshoro Old Bridge Congestion",
                    message = "Heavy truck queue approaching Jamshoro Toll. Expect +8 to 12 minutes delay for morning shuttle points.",
                    severity = "WARNING"
                ),
                TrafficAlertEntity(
                    pointNumber = "Point 30",
                    routeName = "Route 3",
                    title = "Auto Bhan Signal Maintenance",
                    message = "Traffic diverted through Citizen Colony backroad. Point will pause at Wadhu Wah canal junction.",
                    severity = "INFO"
                )
            )
            alerts.forEach { trafficAlertDao.insertAlert(it) }

            // Seed sample stop request
            val sampleRequest = StopRequestEntity(
                studentRollNo = "21SW089",
                studentName = "Gul Jabeen Gabol",
                pointId = "POINT_27",
                stopName = "Qasim Chowk",
                requestType = "PICKUP",
                note = "Waiting near gate of pharmacy with 2 classmates"
            )
            stopRequestDao.insertRequest(sampleRequest)
        }
    }

    // Driver Operations
    suspend fun updateSeatsByDriver(pointId: String, occupied: Int, standing: Int) {
        shuttleDao.updateSeatAvailability(pointId, occupied, standing)
    }

    suspend fun updateDriverProgress(
        pointId: String,
        currentStop: String,
        nextStop: String,
        lat: Double,
        lng: Double,
        speed: Int,
        delayMins: Int
    ) {
        shuttleDao.updateLivePosition(pointId, currentStop, nextStop, lat, lng, speed, delayMins)
    }

    suspend fun broadcastAlert(
        pointNumber: String,
        routeName: String,
        title: String,
        message: String,
        severity: String
    ) {
        trafficAlertDao.insertAlert(
            TrafficAlertEntity(
                pointNumber = pointNumber,
                routeName = routeName,
                title = title,
                message = message,
                severity = severity
            )
        )
    }

    // Student Operations
    suspend fun submitStopRequest(
        rollNo: String,
        name: String,
        pointId: String,
        stopName: String,
        requestType: String,
        note: String
    ) {
        stopRequestDao.insertRequest(
            StopRequestEntity(
                studentRollNo = rollNo,
                studentName = name,
                pointId = pointId,
                stopName = stopName,
                requestType = requestType,
                note = note
            )
        )
    }

    suspend fun acknowledgeStopRequest(id: Long) {
        stopRequestDao.updateRequestStatus(id, "ACKNOWLEDGED")
    }

    suspend fun completeStopRequest(id: Long) {
        stopRequestDao.updateRequestStatus(id, "COMPLETED")
    }

    suspend fun dismissAlert(id: Long) {
        trafficAlertDao.dismissAlert(id)
    }

    // Background GPS simulation ticker to simulate Point movement smoothly in Hyderabad / Jamshoro!
    fun startSimulation(scope: CoroutineScope) {
        scope.launch(Dispatchers.IO) {
            var step = 0
            while (isActive) {
                delay(4000)
                step++
                val shuttles = shuttleDao.getAllShuttles().firstOrNull() ?: emptyList()
                shuttles.forEach { bus ->
                    if (bus.status == "ON_ROUTE") {
                        // Smoothly oscillate and advance coordinates slightly along Hyderabad-Jamshoro axis
                        val deltaLat = (Math.sin((step + bus.pointNumber.hashCode()) * 0.2) * 0.0012)
                        val deltaLng = (Math.cos((step + bus.pointNumber.hashCode()) * 0.2) * 0.0015)
                        val newSpeed = (35 + (step % 25)).coerceIn(20, 65)

                        shuttleDao.updateLivePosition(
                            pointId = bus.id,
                            currentStop = bus.currentStopName,
                            nextStop = bus.nextStopName,
                            lat = bus.latitude + deltaLat * 0.3,
                            lng = bus.longitude + deltaLng * 0.3,
                            speed = newSpeed,
                            delay = bus.delayMinutes
                        )
                    }
                }
            }
        }
    }
}
