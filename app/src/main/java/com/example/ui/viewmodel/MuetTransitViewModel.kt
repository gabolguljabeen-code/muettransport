package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.model.BoardingPassEntity
import com.example.data.model.RouteEntity
import com.example.data.model.ShuttlePoint
import com.example.data.model.StopPointEntity
import com.example.data.model.StopRequestEntity
import com.example.data.model.TrafficAlertEntity
import com.example.data.repository.ShuttleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class UserRole {
    STUDENT,
    DRIVER,
    ADMIN
}

enum class StudentTab {
    TRACKING_MAP,
    SCHEDULES,
    BOARDING_PASS,
    STOP_REQUESTS
}

class MuetTransitViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ShuttleRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = ShuttleRepository(db)
        viewModelScope.launch {
            repository.seedInitialDataIfNeeded()
            repository.startSimulation(viewModelScope)
        }
    }

    val shuttles: StateFlow<List<ShuttlePoint>> = repository.allShuttles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val routes: StateFlow<List<RouteEntity>> = repository.allRoutes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val stops: StateFlow<List<StopPointEntity>> = repository.allStops
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val boardingPass: StateFlow<BoardingPassEntity?> = repository.primaryBoardingPass
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val stopRequests: StateFlow<List<StopRequestEntity>> = repository.stopRequests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeAlerts: StateFlow<List<TrafficAlertEntity>> = repository.activeAlerts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI States
    private val _userRole = MutableStateFlow(UserRole.STUDENT)
    val userRole: StateFlow<UserRole> = _userRole.asStateFlow()

    private val _studentTab = MutableStateFlow(StudentTab.TRACKING_MAP)
    val studentTab: StateFlow<StudentTab> = _studentTab.asStateFlow()

    private val _selectedShuttle = MutableStateFlow<ShuttlePoint?>(null)
    val selectedShuttle: StateFlow<ShuttlePoint?> = _selectedShuttle.asStateFlow()

    private val _driverSelectedPointId = MutableStateFlow("POINT_27")
    val driverSelectedPointId: StateFlow<String> = _driverSelectedPointId.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    fun setRole(role: UserRole) {
        _userRole.value = role
    }

    fun setStudentTab(tab: StudentTab) {
        _studentTab.value = tab
    }

    fun selectShuttle(shuttle: ShuttlePoint?) {
        _selectedShuttle.value = shuttle
    }

    fun selectDriverPoint(pointId: String) {
        _driverSelectedPointId.value = pointId
    }

    fun showMessage(msg: String) {
        _snackbarMessage.value = msg
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    // Driver controls
    fun updateSeats(pointId: String, deltaSeats: Int, deltaStanding: Int = 0) {
        val current = shuttles.value.find { it.id == pointId } ?: return
        val newOccupied = (current.occupiedSeats + deltaSeats).coerceIn(0, current.totalSeats)
        val newStanding = (current.standingPassengers + deltaStanding).coerceAtLeast(0)

        viewModelScope.launch {
            repository.updateSeatsByDriver(pointId, newOccupied, newStanding)
            _snackbarMessage.value = "Updated: $newOccupied/${current.totalSeats} seats filled"
        }
    }

    fun setSeatPreset(pointId: String, preset: String) {
        val current = shuttles.value.find { it.id == pointId } ?: return
        val (occupied, standing) = when (preset) {
            "FULL" -> Pair(current.totalSeats, 8)
            "MODERATE" -> Pair(current.totalSeats - 15, 0)
            "AVAILABLE" -> Pair(15, 0)
            else -> Pair(current.occupiedSeats, current.standingPassengers)
        }
        viewModelScope.launch {
            repository.updateSeatsByDriver(pointId, occupied, standing)
            _snackbarMessage.value = "Point status set to: $preset"
        }
    }

    fun advanceDriverStop(pointId: String) {
        val current = shuttles.value.find { it.id == pointId } ?: return
        val routeStops = stops.value.filter { it.routeId == current.routeId }.sortedBy { it.orderIndex }
        if (routeStops.isEmpty()) return

        val currentIndex = routeStops.indexOfFirst { it.stopName == current.currentStopName }
        val nextIndex = if (currentIndex >= 0 && currentIndex + 1 < routeStops.size) currentIndex + 1 else 0
        val upcomingIndex = if (nextIndex + 1 < routeStops.size) nextIndex + 1 else 0

        val newCurrent = routeStops[nextIndex]
        val newNext = routeStops[upcomingIndex]

        viewModelScope.launch {
            repository.updateDriverProgress(
                pointId = pointId,
                currentStop = newCurrent.stopName,
                nextStop = newNext.stopName,
                lat = newCurrent.latitude,
                lng = newCurrent.longitude,
                speed = 45,
                delayMins = current.delayMinutes
            )
            _snackbarMessage.value = "Point arrived at: ${newCurrent.stopName}"
        }
    }

    fun broadcastAlert(title: String, message: String, severity: String) {
        val currentBus = shuttles.value.find { it.id == driverSelectedPointId.value }
        val pointName = currentBus?.pointNumber ?: "MUET Transport"
        val routeName = currentBus?.routeName ?: "Jamshoro-Hyderabad Route"

        viewModelScope.launch {
            repository.broadcastAlert(pointName, routeName, title, message, severity)
            _snackbarMessage.value = "Alert broadcasted to students & admin"
        }
    }

    fun submitStudentStopRequest(pointId: String, stopName: String, type: String, note: String) {
        val pass = boardingPass.value
        val roll = pass?.studentRollNo ?: "21SW089"
        val name = pass?.studentName ?: "Gul Jabeen Gabol"

        viewModelScope.launch {
            repository.submitStopRequest(roll, name, pointId, stopName, type, note)
            _snackbarMessage.value = "Stop request submitted for $stopName!"
        }
    }

    fun acknowledgeRequest(requestId: Long) {
        viewModelScope.launch {
            repository.acknowledgeStopRequest(requestId)
            _snackbarMessage.value = "Request acknowledged by driver"
        }
    }

    fun completeRequest(requestId: Long) {
        viewModelScope.launch {
            repository.completeStopRequest(requestId)
            _snackbarMessage.value = "Passenger boarding completed"
        }
    }

    fun dismissAlert(alertId: Long) {
        viewModelScope.launch {
            repository.dismissAlert(alertId)
        }
    }
}
