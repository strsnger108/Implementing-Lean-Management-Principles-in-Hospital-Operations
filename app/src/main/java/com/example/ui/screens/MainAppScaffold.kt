package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.HospitalViewModel

enum class AppDestination(val label: String, val icon: ImageVector, val tag: String) {
    DASHBOARD("Dashboard", Icons.Default.Dashboard, "nav_dashboard"),
    PATIENTS("Patients", Icons.Default.Hotel, "nav_patients"),
    VSM("VSM Waste", Icons.Default.Timeline, "nav_vsm"),
    LEAN_TOOLS("Lean Tools", Icons.Default.Build, "nav_tools"),
    REPORT("Study", Icons.AutoMirrored.Filled.MenuBook, "nav_report")
}

@Composable
fun MainAppScaffold(
    viewModel: HospitalViewModel,
    modifier: Modifier = Modifier
) {
    var currentDestination by remember { mutableStateOf(AppDestination.DASHBOARD) }

    // Handle back button: return to Dashboard if on any other tab
    BackHandler(enabled = currentDestination != AppDestination.DASHBOARD) {
        currentDestination = AppDestination.DASHBOARD
    }

    Scaffold(
        modifier = modifier.fillMaxSize().testTag("main_scaffold"),
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.testTag("bottom_navigation_bar")
            ) {
                AppDestination.entries.forEach { destination ->
                    val isSelected = currentDestination == destination
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentDestination = destination },
                        icon = {
                            Icon(
                                imageVector = destination.icon,
                                contentDescription = destination.label
                            )
                        },
                        label = {
                            Text(
                                text = destination.label,
                                fontSize = 11.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag(destination.tag)
                    )
                }
            }
        }
    ) { innerPadding ->
        when (currentDestination) {
            AppDestination.DASHBOARD -> DashboardScreen(
                viewModel = viewModel,
                onNavigateToVsm = { currentDestination = AppDestination.VSM },
                onNavigateToKaizen = { currentDestination = AppDestination.LEAN_TOOLS },
                onNavigateToSimulator = { currentDestination = AppDestination.LEAN_TOOLS },
                modifier = Modifier.padding(innerPadding)
            )
            AppDestination.PATIENTS -> PatientFlowScreen(
                viewModel = viewModel,
                modifier = Modifier.padding(innerPadding)
            )
            AppDestination.VSM -> ValueStreamMappingScreen(
                viewModel = viewModel,
                modifier = Modifier.padding(innerPadding)
            )
            AppDestination.LEAN_TOOLS -> OperationsToolsScreen(
                viewModel = viewModel,
                modifier = Modifier.padding(innerPadding)
            )
            AppDestination.REPORT -> StudyReportScreen(
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}
