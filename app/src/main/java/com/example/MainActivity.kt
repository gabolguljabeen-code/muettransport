package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.ShuttlePoint
import com.example.ui.components.StopRequestDialog
import com.example.ui.screens.AdminFleetDashboard
import com.example.ui.screens.BoardingPassScreen
import com.example.ui.screens.DriverCockpitScreen
import com.example.ui.screens.SchedulesScreen
import com.example.ui.screens.StopRequestsScreen
import com.example.ui.screens.StudentTrackingScreen
import com.example.ui.screens.WebPortalScreen
import com.example.ui.theme.MuetGold
import com.example.ui.theme.MuetGreen
import com.example.ui.theme.MuetPrimary
import com.example.ui.theme.MuetSecondary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.MuetTransitViewModel
import com.example.ui.viewmodel.StudentTab
import com.example.ui.viewmodel.UserRole

enum class DisplayMode {
    WEB_PORTAL,
    MOBILE_APP
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainTransitApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTransitApp(viewModel: MuetTransitViewModel = viewModel()) {
    val userRole by viewModel.userRole.collectAsStateWithLifecycle()
    val studentTab by viewModel.studentTab.collectAsStateWithLifecycle()
    val shuttles by viewModel.shuttles.collectAsStateWithLifecycle()
    val routes by viewModel.routes.collectAsStateWithLifecycle()
    val stops by viewModel.stops.collectAsStateWithLifecycle()
    val boardingPass by viewModel.boardingPass.collectAsStateWithLifecycle()
    val stopRequests by viewModel.stopRequests.collectAsStateWithLifecycle()
    val activeAlerts by viewModel.activeAlerts.collectAsStateWithLifecycle()
    val selectedShuttle by viewModel.selectedShuttle.collectAsStateWithLifecycle()
    val driverSelectedPointId by viewModel.driverSelectedPointId.collectAsStateWithLifecycle()
    val snackbarMessage by viewModel.snackbarMessage.collectAsStateWithLifecycle()

    var displayMode by remember { mutableStateOf(DisplayMode.WEB_PORTAL) }
    val snackbarHostState = remember { SnackbarHostState() }
    var showRoleMenu by remember { mutableStateOf(false) }
    var dialogBus by remember { mutableStateOf<ShuttlePoint?>(null) }

    // Handle Snackbar messages
    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbar()
        }
    }

    // Hardware BackHandler
    BackHandler(enabled = userRole != UserRole.STUDENT || studentTab != StudentTab.TRACKING_MAP) {
        if (userRole != UserRole.STUDENT) {
            viewModel.setRole(UserRole.STUDENT)
        } else if (studentTab != StudentTab.TRACKING_MAP) {
            viewModel.setStudentTab(StudentTab.TRACKING_MAP)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("main_scaffold"),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "MUET Point Track",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = MuetGold
                            ) {
                                Text(
                                    text = if (displayMode == DisplayMode.WEB_PORTAL) "WEB PORTAL" else "APP",
                                    color = Color(0xFF0F172A),
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontSize = 8.sp)
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
                },
                actions = {
                    // View Mode Switcher (Web Portal vs App)
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White.copy(alpha = 0.2f),
                        modifier = Modifier
                            .clickable {
                                displayMode = if (displayMode == DisplayMode.WEB_PORTAL) DisplayMode.MOBILE_APP else DisplayMode.WEB_PORTAL
                            }
                            .testTag("toggle_web_view_btn")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (displayMode == DisplayMode.WEB_PORTAL) Icons.Default.Language else Icons.Default.PhoneAndroid,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (displayMode == DisplayMode.WEB_PORTAL) "Switch to App" else "Switch to Web",
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Role Switcher Button
                    Box {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White.copy(alpha = 0.18f),
                            modifier = Modifier
                                .clickable { showRoleMenu = true }
                                .testTag("role_switcher_btn")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = when (userRole) {
                                        UserRole.STUDENT -> Icons.Default.School
                                        UserRole.DRIVER -> Icons.Default.DirectionsBus
                                        UserRole.ADMIN -> Icons.Default.AdminPanelSettings
                                    },
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = when (userRole) {
                                        UserRole.STUDENT -> "Student"
                                        UserRole.DRIVER -> "Driver"
                                        UserRole.ADMIN -> "Admin"
                                    },
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = showRoleMenu,
                            onDismissRequest = { showRoleMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("🎓 Student Portal") },
                                onClick = {
                                    viewModel.setRole(UserRole.STUDENT)
                                    showRoleMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("🚌 Driver Cockpit (Seats & Routes)") },
                                onClick = {
                                    viewModel.setRole(UserRole.DRIVER)
                                    showRoleMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("📊 Admin Fleet Analytics") },
                                onClick = {
                                    viewModel.setRole(UserRole.ADMIN)
                                    showRoleMenu = false
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MuetPrimary,
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            if (displayMode == DisplayMode.MOBILE_APP && userRole == UserRole.STUDENT) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp,
                    modifier = Modifier.testTag("student_nav_bar")
                ) {
                    NavigationBarItem(
                        selected = studentTab == StudentTab.TRACKING_MAP,
                        onClick = { viewModel.setStudentTab(StudentTab.TRACKING_MAP) },
                        icon = { Icon(Icons.Default.Map, contentDescription = "Map") },
                        label = { Text("Tracking") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MuetPrimary,
                            selectedTextColor = MuetPrimary,
                            indicatorColor = MuetPrimary.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.testTag("tab_tracking")
                    )

                    NavigationBarItem(
                        selected = studentTab == StudentTab.SCHEDULES,
                        onClick = { viewModel.setStudentTab(StudentTab.SCHEDULES) },
                        icon = { Icon(Icons.Default.CalendarMonth, contentDescription = "Schedules") },
                        label = { Text("Schedules") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MuetPrimary,
                            selectedTextColor = MuetPrimary,
                            indicatorColor = MuetPrimary.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.testTag("tab_schedules")
                    )

                    NavigationBarItem(
                        selected = studentTab == StudentTab.BOARDING_PASS,
                        onClick = { viewModel.setStudentTab(StudentTab.BOARDING_PASS) },
                        icon = { Icon(Icons.Default.Badge, contentDescription = "Pass") },
                        label = { Text("Pass") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MuetPrimary,
                            selectedTextColor = MuetPrimary,
                            indicatorColor = MuetPrimary.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.testTag("tab_pass")
                    )

                    NavigationBarItem(
                        selected = studentTab == StudentTab.STOP_REQUESTS,
                        onClick = { viewModel.setStudentTab(StudentTab.STOP_REQUESTS) },
                        icon = { Icon(Icons.Default.NearMe, contentDescription = "Stop Requests") },
                        label = { Text("Requests") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MuetPrimary,
                            selectedTextColor = MuetPrimary,
                            indicatorColor = MuetPrimary.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.testTag("tab_requests")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            if (displayMode == DisplayMode.WEB_PORTAL) {
                WebPortalScreen(
                    shuttles = shuttles,
                    routes = routes,
                    stops = stops,
                    alerts = activeAlerts,
                    boardingPass = boardingPass,
                    stopRequests = stopRequests,
                    selectedShuttle = selectedShuttle,
                    onSelectShuttle = { viewModel.selectShuttle(it) },
                    onRequestStop = { bus -> dialogBus = bus },
                    onUpdateSeats = { pt, s, std -> viewModel.updateSeats(pt, s, std) },
                    onSetPreset = { pt, pr -> viewModel.setSeatPreset(pt, pr) },
                    onAdvanceStop = { pt -> viewModel.advanceDriverStop(pt) },
                    onBroadcastAlert = { t, m, s -> viewModel.broadcastAlert(t, m, s) },
                    onDismissAlert = { viewModel.dismissAlert(it) }
                )
            } else {
                AnimatedContent(
                    targetState = userRole,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "role_content_anim"
                ) { role ->
                    when (role) {
                        UserRole.STUDENT -> {
                            AnimatedContent(
                                targetState = studentTab,
                                transitionSpec = { fadeIn() togetherWith fadeOut() },
                                label = "student_tab_anim"
                            ) { tab ->
                                when (tab) {
                                    StudentTab.TRACKING_MAP -> {
                                        StudentTrackingScreen(
                                            shuttles = shuttles,
                                            stops = stops,
                                            alerts = activeAlerts,
                                            boardingPass = boardingPass,
                                            selectedShuttle = selectedShuttle,
                                            onSelectShuttle = { viewModel.selectShuttle(it) },
                                            onRequestStopClick = { bus -> dialogBus = bus },
                                            onDismissAlert = { viewModel.dismissAlert(it) }
                                        )
                                    }
                                    StudentTab.SCHEDULES -> {
                                        SchedulesScreen(
                                            routes = routes,
                                            stops = stops,
                                            shuttles = shuttles,
                                            onSelectBus = {
                                                viewModel.selectShuttle(it)
                                                viewModel.setStudentTab(StudentTab.TRACKING_MAP)
                                            }
                                        )
                                    }
                                    StudentTab.BOARDING_PASS -> {
                                        BoardingPassScreen(
                                            pass = boardingPass,
                                            onTriggerSos = {
                                                viewModel.showMessage("Emergency signal sent to Directorate Office!")
                                            }
                                        )
                                    }
                                    StudentTab.STOP_REQUESTS -> {
                                        StopRequestsScreen(
                                            requests = stopRequests,
                                            shuttles = shuttles,
                                            stops = stops,
                                            onSubmitRequest = { pt, st, tp, nt ->
                                                viewModel.submitStudentStopRequest(pt, st, tp, nt)
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        UserRole.DRIVER -> {
                            DriverCockpitScreen(
                                shuttles = shuttles,
                                selectedPointId = driverSelectedPointId,
                                stopRequests = stopRequests,
                                onSelectPoint = { viewModel.selectDriverPoint(it) },
                                onUpdateSeats = { pt, s, std -> viewModel.updateSeats(pt, s, std) },
                                onSetPreset = { pt, pr -> viewModel.setSeatPreset(pt, pr) },
                                onAdvanceStop = { pt -> viewModel.advanceDriverStop(pt) },
                                onBroadcastAlert = { t, m, s -> viewModel.broadcastAlert(t, m, s) },
                                onAcknowledgeRequest = { viewModel.acknowledgeRequest(it) },
                                onCompleteRequest = { viewModel.completeRequest(it) }
                            )
                        }

                        UserRole.ADMIN -> {
                            AdminFleetDashboard(
                                shuttles = shuttles,
                                routes = routes,
                                requests = stopRequests,
                                onBroadcastEmergencyNotice = { t, m ->
                                    viewModel.broadcastAlert(t, m, "CRITICAL")
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Modal dialog for requesting stop point
    dialogBus?.let { bus ->
        StopRequestDialog(
            bus = bus,
            stops = stops,
            onDismiss = { dialogBus = null },
            onSubmit = { pt, st, tp, nt ->
                viewModel.submitStudentStopRequest(pt, st, tp, nt)
            }
        )
    }
}
