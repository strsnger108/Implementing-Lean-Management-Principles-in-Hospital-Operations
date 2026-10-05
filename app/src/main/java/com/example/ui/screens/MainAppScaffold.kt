package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SwitchAccount
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.HospitalViewModel

enum class AppDestination(val label: String, val icon: ImageVector, val tag: String) {
    DASHBOARD("Dashboard", Icons.Default.Dashboard, "nav_dashboard"),
    FIVE_S("5S Audits", Icons.Default.FactCheck, "nav_five_s"),
    PATIENTS("Patients", Icons.Default.Hotel, "nav_patients"),
    VSM("VSM Waste", Icons.Default.Timeline, "nav_vsm"),
    LEAN_TOOLS("Lean Tools", Icons.Default.Build, "nav_tools")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScaffold(
    viewModel: HospitalViewModel,
    modifier: Modifier = Modifier
) {
    val activeSession by viewModel.activeUserSession.collectAsState()
    var currentDestination by remember { mutableStateOf(AppDestination.DASHBOARD) }
    var showLoginScreen by remember { mutableStateOf(false) }

    // If not logged in, or if user requests the login/account switcher interface
    if (activeSession == null || showLoginScreen) {
        LoginSignUpScreen(
            viewModel = viewModel,
            onDismiss = if (activeSession != null) { { showLoginScreen = false } } else null,
            modifier = modifier.fillMaxSize()
        )
        return
    }

    val user = activeSession!!

    when (user.role.uppercase()) {
        "STAFF" -> {
            StaffDashboardScreen(
                viewModel = viewModel,
                onSwitchRole = { showLoginScreen = true },
                modifier = modifier.fillMaxSize()
            )
        }
        "ADMIN" -> {
            AdminDashboardScreen(
                viewModel = viewModel,
                onSwitchRole = { showLoginScreen = true },
                modifier = modifier.fillMaxSize()
            )
        }
        else -> {
            PatientPortalScreen(
                viewModel = viewModel,
                onSwitchToStaffMode = { showLoginScreen = true },
                modifier = modifier.fillMaxSize()
            )
        }
    }
}
