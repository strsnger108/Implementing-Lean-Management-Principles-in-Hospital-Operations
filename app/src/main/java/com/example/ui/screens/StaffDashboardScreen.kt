package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.SwitchAccount
import androidx.compose.material.icons.filled.ViewKanban
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NurseCallRequest
import com.example.data.model.PatientRecord
import com.example.data.model.PharmacyPrescription
import com.example.ui.components.RoleSwitcherDialog
import com.example.ui.components.StaffFiveSOperationalAuditForm
import com.example.ui.viewmodel.HospitalViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffDashboardScreen(
    viewModel: HospitalViewModel,
    onSwitchRole: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activeSession by viewModel.activeUserSession.collectAsState()
    val allNurseCalls by viewModel.allNurseCalls.collectAsState()
    val allPatients by viewModel.allPatients.collectAsState()
    val allPrescriptions by viewModel.allPrescriptions.collectAsState()
    val currentServingToken by viewModel.currentServingTokenNumber.collectAsState()
    val chamberStatus by viewModel.currentChamberStatus.collectAsState()

    var selectedStaffTab by remember { mutableIntStateOf(0) }
    var showRoleDialog by remember { mutableStateOf(false) }

    val activeNurseCalls = remember(allNurseCalls) {
        allNurseCalls.filter { it.status != "Resolved" && !it.status.contains("Cancelled", ignoreCase = true) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("staff_dashboard_screen")
    ) {
        // Staff Header Console
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = Color(0xFFDBEAFE),
                            shape = CircleShape,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Badge,
                                    contentDescription = null,
                                    tint = Color(0xFF1D4ED8),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = activeSession?.fullName ?: "Staff Nurse Priya Sharma (RN)",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = Color(0xFF2563EB).copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(4.dp),
                                    modifier = Modifier.clickable { showRoleDialog = true }
                                ) {
                                    Text(
                                        text = "STAFF",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1D4ED8),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Station 3B (Internal Medicine) • Shift A • Synergy Global Hospital",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { showRoleDialog = true },
                            modifier = Modifier.testTag("btn_staff_switch_role")
                        ) {
                            Icon(Icons.Default.SwitchAccount, contentDescription = "Switch Role", tint = MaterialTheme.colorScheme.primary)
                        }
                        IconButton(
                            onClick = { viewModel.logout() },
                            modifier = Modifier.testTag("btn_staff_logout")
                        ) {
                            Icon(Icons.Default.Logout, contentDescription = "Logout", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Active Emergency Alert Pill Banner if calls exist
                if (activeNurseCalls.isNotEmpty()) {
                    Surface(
                        color = Color(0xFFFEE2E2),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF87171)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedStaffTab = 0 }
                            .testTag("staff_nurse_call_alert_banner")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${activeNurseCalls.size} Active Bed Buzzers Pending Response",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF991B1B)
                                )
                            }
                            Text("Attend Now ➔", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Scrollable Navigation Tabs for Staff Hub
                ScrollableTabRow(
                    selectedTabIndex = selectedStaffTab,
                    edgePadding = 0.dp,
                    containerColor = Color.Transparent
                ) {
                    Tab(
                        selected = selectedStaffTab == 0,
                        onClick = { selectedStaffTab = 0 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Nurse Call Desk (${activeNurseCalls.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        },
                        modifier = Modifier.testTag("tab_staff_nurse_call")
                    )

                    Tab(
                        selected = selectedStaffTab == 1,
                        onClick = { selectedStaffTab = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.ViewKanban, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Patient Flow Kanban", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        },
                        modifier = Modifier.testTag("tab_staff_kanban")
                    )

                    Tab(
                        selected = selectedStaffTab == 2,
                        onClick = { selectedStaffTab = 2 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Doctor OPD Calling Desk", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        },
                        modifier = Modifier.testTag("tab_staff_opd_chamber")
                    )

                    Tab(
                        selected = selectedStaffTab == 3,
                        onClick = { selectedStaffTab = 3 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocalPharmacy, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Pharmacy Queue", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        },
                        modifier = Modifier.testTag("tab_staff_pharmacy")
                    )

                    Tab(
                        selected = selectedStaffTab == 4,
                        onClick = { selectedStaffTab = 4 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.PostAdd, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Bedside Charges Logger", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        },
                        modifier = Modifier.testTag("tab_staff_charges")
                    )

                    Tab(
                        selected = selectedStaffTab == 5,
                        onClick = { selectedStaffTab = 5 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.FactCheck, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("5S Operational Audit", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        },
                        modifier = Modifier.testTag("tab_staff_fives")
                    )
                }
            }
        }

        // Tab Content
        when (selectedStaffTab) {
            0 -> {
                // Tab 0: Real-Time Nurse Call & Station Alarms
                StaffNurseCallDeskSection(
                    allCalls = allNurseCalls,
                    onUpdateStatus = { id, st -> viewModel.updateNurseCallStatus(id, st) },
                    onSimulateCall = {
                        viewModel.triggerNurseCall(
                            regNumber = "SGH-2026-0294",
                            patientName = "Bimla Devi",
                            bedNumber = "Ward 3B - Bed 14",
                            wardName = "Internal Medicine",
                            reason = "IV Fluid Bottle Beep / Empty"
                        )
                        Toast.makeText(context, "Bed Buzzer Alert triggered for Ward 3B!", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            1 -> {
                // Tab 1: IPD Patient Flow Kanban Board
                StaffKanbanFlowSection(
                    patients = allPatients.filter { !it.isDischarged },
                    onAdvanceStage = { id, nextStage -> viewModel.updatePatientFlowStage(id, nextStage) }
                )
            }

            2 -> {
                // Tab 2: Doctor OPD Chamber Calling Desk
                StaffOpdChamberDeskSection(
                    currentServingToken = currentServingToken,
                    chamberStatus = chamberStatus,
                    onCallNext = { viewModel.callNextOpdToken() },
                    onHold = { viewModel.holdCurrentOpdToken() },
                    onComplete = { viewModel.completeCurrentOpdToken() }
                )
            }

            3 -> {
                // Tab 3: Pharmacy Dispensation Queue
                StaffPharmacyQueueSection(
                    prescriptions = allPrescriptions,
                    onUpdateStatus = { id, st -> viewModel.updatePrescriptionStatus(id, st) }
                )
            }

            4 -> {
                // Tab 4: Bedside Charges & Daily Inpatient Logger
                StaffBedsideChargesLoggerSection(
                    patients = allPatients.filter { !it.isDischarged },
                    onLogCharge = { reg, name, day, room, doc, meds, lab, cons ->
                        viewModel.addBedsideDailyCharge(reg, name, day, room, doc, meds, lab, cons)
                        Toast.makeText(context, "Bedside shift charges logged for $name!", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            5 -> {
                // Tab 5: 5S Operational Audit Checklist Form with local Room state persistence
                StaffFiveSOperationalAuditForm(
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        if (showRoleDialog) {
            RoleSwitcherDialog(
                currentRole = activeSession?.role ?: "STAFF",
                onDismiss = { showRoleDialog = false },
                onRoleSelected = { newRole ->
                    viewModel.switchActiveRole(newRole)
                    showRoleDialog = false
                    onSwitchRole()
                },
                onOpenLoginScreen = onSwitchRole
            )
        }
    }
}

// -------------------------------------------------------------------------
// Tab 0: Staff Nurse Call Desk & Station Alarms
// -------------------------------------------------------------------------
@Composable
fun StaffNurseCallDeskSection(
    allCalls: List<NurseCallRequest>,
    onUpdateStatus: (Long, String) -> Unit,
    onSimulateCall: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Real-Time Station Call Buzzers",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Live alerts from patient bedside units across Inpatient Wards",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = onSimulateCall,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                    modifier = Modifier.testTag("btn_simulate_buzzer")
                ) {
                    Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Simulate Call", fontSize = 11.sp)
                }
            }
        }

        if (allCalls.isEmpty()) {
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.DoneAll, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No active bedside buzzers ringing", fontWeight = FontWeight.Bold)
                        Text("Station 3B is all clear. All requests acknowledged.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        } else {
            items(allCalls, key = { it.id }) { call ->
                StaffNurseCallItemCard(
                    call = call,
                    onAcknowledge = { onUpdateStatus(call.id, "Nurse Dispatched") },
                    onResolve = { onUpdateStatus(call.id, "Resolved") },
                    onEscalate = { onUpdateStatus(call.id, "Escalated to RMO") }
                )
            }
        }
    }
}

@Composable
fun StaffNurseCallItemCard(
    call: NurseCallRequest,
    onAcknowledge: () -> Unit,
    onResolve: () -> Unit,
    onEscalate: () -> Unit
) {
    val isPending = call.status == "Active Call"
    val isDispatched = call.status == "Nurse Dispatched"
    val isEscalated = call.status.contains("Escalated", ignoreCase = true)
    val isResolved = call.status == "Resolved"

    val borderColor = when {
        isEscalated -> Color(0xFFDC2626)
        isPending -> Color(0xFFEA580C)
        isDispatched -> Color(0xFF0284C7)
        else -> Color(0xFF16A34A)
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, borderColor),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().testTag("staff_nurse_call_item_${call.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = when {
                            isEscalated || isPending -> Color(0xFFFEE2E2)
                            isDispatched -> Color(0xFFE0F2FE)
                            else -> Color(0xFFDCFCE7)
                        },
                        shape = CircleShape,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (isEscalated) Icons.Default.Warning else Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = when {
                                    isEscalated -> Color(0xFFDC2626)
                                    isPending -> Color(0xFFEA580C)
                                    isDispatched -> Color(0xFF0284C7)
                                    else -> Color(0xFF16A34A)
                                },
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "${call.bedNumber} • ${call.patientName}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "UHID: ${call.hospitalRegNumber} • Time: ${call.callTime}",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    color = when {
                        isEscalated -> Color(0xFFFEE2E2)
                        isPending -> Color(0xFFFFEDD5)
                        isDispatched -> Color(0xFFE0F2FE)
                        else -> Color(0xFFDCFCE7)
                    },
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = call.status,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            isEscalated -> Color(0xFFDC2626)
                            isPending -> Color(0xFFC2410C)
                            isDispatched -> Color(0xFF0369A1)
                            else -> Color(0xFF16A34A)
                        },
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Buzzer Reason: ${call.callReason}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (!isResolved) {
                    if (!isDispatched) {
                        Button(
                            onClick = onAcknowledge,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.SupportAgent, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Dispatch Nurse", fontSize = 11.sp)
                        }
                    }

                    Button(
                        onClick = onResolve,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Mark Resolved", fontSize = 11.sp)
                    }

                    OutlinedButton(
                        onClick = onEscalate,
                        modifier = Modifier.weight(0.9f)
                    ) {
                        Text("Escalate", fontSize = 11.sp, color = Color(0xFFDC2626))
                    }
                } else {
                    Surface(
                        color = Color(0xFFDCFCE7),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "✓ Assistance Completed & Resolved by Station 3B",
                            fontSize = 11.sp,
                            color = Color(0xFF166534),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(6.dp)
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// Tab 1: Staff IPD Patient Flow Kanban Board
// -------------------------------------------------------------------------
@Composable
fun StaffKanbanFlowSection(
    patients: List<PatientRecord>,
    onAdvanceStage: (Long, String) -> Unit
) {
    val stages = listOf("Admission", "Diagnostics", "Treatment", "Discharge")
    var selectedFilterStage by remember { mutableStateOf("All") }

    val filteredPatients = remember(patients, selectedFilterStage) {
        if (selectedFilterStage == "All") patients else patients.filter { it.flowStage.equals(selectedFilterStage, ignoreCase = true) }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("IPD Patient Flow Kanban", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Text("Real-time clinical pathway progression & discharge prep", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Surface(color = Color(0xFFE0F2FE), shape = RoundedCornerShape(6.dp)) {
                    Text(
                        text = "${patients.size} Admitted",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0369A1),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Filter chips
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("All", "Admission", "Diagnostics", "Treatment", "Discharge").forEach { st ->
                    FilterChip(
                        selected = selectedFilterStage == st,
                        onClick = { selectedFilterStage = st },
                        label = { Text(st, fontSize = 11.sp) }
                    )
                }
            }
        }

        items(filteredPatients, key = { it.id }) { patient ->
            StaffPatientKanbanCard(
                patient = patient,
                onAdvance = {
                    val next = when (patient.flowStage) {
                        "Admission" -> "Diagnostics"
                        "Diagnostics" -> "Treatment"
                        "Treatment" -> "Discharge"
                        else -> "Discharged"
                    }
                    onAdvanceStage(patient.id, next)
                }
            )
        }
    }
}

@Composable
fun StaffPatientKanbanCard(
    patient: PatientRecord,
    onAdvance: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(patient.patientName, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("IPD #${patient.ipdNumber} • ${patient.bedNumber}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Surface(
                    color = when (patient.flowStage) {
                        "Admission" -> Color(0xFFE0F2FE)
                        "Diagnostics" -> Color(0xFFEDE9FE)
                        "Treatment" -> Color(0xFFDCFCE7)
                        else -> Color(0xFFFFEDD5)
                    },
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = patient.flowStage,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (patient.flowStage) {
                            "Admission" -> Color(0xFF0369A1)
                            "Diagnostics" -> Color(0xFF6D28D9)
                            "Treatment" -> Color(0xFF166534)
                            else -> Color(0xFFC2410C)
                        },
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text("Consultant: ${patient.consultantName} • Nurse: ${patient.attendingNurseName}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Current Stay: ${patient.losDays + 1} Days • Acuity Level ${patient.acuityLevel}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Expected: ${patient.estimatedDischargeTime}", fontSize = 10.sp, color = Color(0xFF16A34A), fontWeight = FontWeight.SemiBold)

                Button(
                    onClick = onAdvance,
                    modifier = Modifier.height(32.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
                ) {
                    Text(
                        text = when (patient.flowStage) {
                            "Admission" -> "Move to Diagnostics ➔"
                            "Diagnostics" -> "Move to Treatment ➔"
                            "Treatment" -> "Start Discharge ➔"
                            else -> "Final Gate Pass ✓"
                        },
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// Tab 2: Doctor OPD Chamber Calling Desk
// -------------------------------------------------------------------------
@Composable
fun StaffOpdChamberDeskSection(
    currentServingToken: Int,
    chamberStatus: String,
    onCallNext: () -> Unit,
    onHold: () -> Unit,
    onComplete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Doctor OPD Chamber Console", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("Dr. Rahul Sinha, MD • Chamber 102 (OPD Block A)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Surface(color = Color(0xFFDCFCE7), shape = RoundedCornerShape(6.dp)) {
                        Text("Chamber Active", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A), modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Big Token Display
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("CURRENT SERVING TOKEN", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("#$currentServingToken", fontSize = 44.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                        Text("Status: $chamberStatus", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0284C7))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Calling Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onCallNext,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                        modifier = Modifier.weight(1.3f).testTag("btn_call_next_token")
                    ) {
                        Icon(Icons.Default.SkipNext, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Call Next Token (#${currentServingToken + 1})")
                    }

                    OutlinedButton(
                        onClick = onHold,
                        modifier = Modifier.weight(0.9f)
                    ) {
                        Icon(Icons.Default.Pause, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Hold", fontSize = 11.sp)
                    }

                    Button(
                        onClick = onComplete,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Complete", fontSize = 11.sp)
                    }
                }
            }
        }

        // Upcoming in Chamber Queue
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("Next Tokens in Waiting Area", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                QueueWaitingRow(token = currentServingToken + 1, name = "Preeti Roy", wait = "~8 mins")
                QueueWaitingRow(token = currentServingToken + 2, name = "Suraj Oraon", wait = "~16 mins")
                QueueWaitingRow(token = currentServingToken + 3, name = "Gunjan Prakash", wait = "~24 mins")
            }
        }
    }
}

@Composable
fun QueueWaitingRow(token: Int, name: String, wait: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(color = Color(0xFFE0F2FE), shape = CircleShape, modifier = Modifier.size(24.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Text("#$token", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0369A1))
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(name, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
        Text("Wait: $wait", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

// -------------------------------------------------------------------------
// Tab 3: Pharmacy Dispensation Queue
// -------------------------------------------------------------------------
@Composable
fun StaffPharmacyQueueSection(
    prescriptions: List<PharmacyPrescription>,
    onUpdateStatus: (Long, String) -> Unit
) {
    var filterStatus by remember { mutableStateOf("All") }

    val filtered = remember(prescriptions, filterStatus) {
        if (filterStatus == "All") prescriptions else prescriptions.filter { it.dispensationStatus.contains(filterStatus, ignoreCase = true) }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text("Pharmacy Dispensation Console", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Text("Inpatient satellite pharmacy & outpatient dispensation desk", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("All", "In Preparation", "Ready", "Collected").forEach { st ->
                    FilterChip(
                        selected = filterStatus == st,
                        onClick = { filterStatus = st },
                        label = { Text(st, fontSize = 11.sp) }
                    )
                }
            }
        }

        items(filtered, key = { it.id }) { rx ->
            StaffPharmacyItemCard(
                prescription = rx,
                onMarkReady = { onUpdateStatus(rx.id, "Dispensed / Ready") },
                onMarkCollected = { onUpdateStatus(rx.id, "Collected") }
            )
        }
    }
}

@Composable
fun StaffPharmacyItemCard(
    prescription: PharmacyPrescription,
    onMarkReady: () -> Unit,
    onMarkCollected: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("${prescription.medicineName} (${prescription.dosage})", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text("Patient: ${prescription.patientName} • UHID: ${prescription.hospitalRegNumber}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Surface(
                    color = when (prescription.dispensationStatus) {
                        "Collected" -> Color(0xFFDCFCE7)
                        "Dispensed / Ready" -> Color(0xFFE0F2FE)
                        else -> Color(0xFFFFEDD5)
                    },
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = prescription.dispensationStatus,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (prescription.dispensationStatus) {
                            "Collected" -> Color(0xFF166534)
                            "Dispensed / Ready" -> Color(0xFF0369A1)
                            else -> Color(0xFFC2410C)
                        },
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text("Regimen: ${prescription.frequency} • ${prescription.duration}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (prescription.dispensationStatus != "Dispensed / Ready" && prescription.dispensationStatus != "Collected") {
                    Button(onClick = onMarkReady, modifier = Modifier.weight(1f).height(32.dp)) {
                        Text("Mark Ready at Counter", fontSize = 10.sp)
                    }
                }
                if (prescription.dispensationStatus != "Collected") {
                    Button(onClick = onMarkCollected, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)), modifier = Modifier.weight(1f).height(32.dp)) {
                        Text("Hand Over / Collected", fontSize = 10.sp)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// Tab 4: Bedside Charges & Daily Inpatient Logger
// -------------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffBedsideChargesLoggerSection(
    patients: List<PatientRecord>,
    onLogCharge: (
        regNumber: String,
        patientName: String,
        dayNum: Int,
        roomFee: Double,
        doctorFee: Double,
        medsFee: Double,
        labFee: Double,
        consumablesFee: Double
    ) -> Unit
) {
    var selectedPatientIndex by remember { mutableIntStateOf(0) }
    val patient = patients.getOrNull(selectedPatientIndex) ?: patients.firstOrNull()

    var roomFeeText by remember { mutableStateOf("3500") }
    var doctorFeeText by remember { mutableStateOf("2000") }
    var medsFeeText by remember { mutableStateOf("3800") }
    var labFeeText by remember { mutableStateOf("2200") }
    var consumablesFeeText by remember { mutableStateOf("1100") }

    val total = (roomFeeText.toDoubleOrNull() ?: 0.0) +
        (doctorFeeText.toDoubleOrNull() ?: 0.0) +
        (medsFeeText.toDoubleOrNull() ?: 0.0) +
        (labFeeText.toDoubleOrNull() ?: 0.0) +
        (consumablesFeeText.toDoubleOrNull() ?: 0.0)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Bedside Daily Inpatient Expense Logger", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Text("Log bedside medications, specialist consults, and disposables for midnight audit reconciliation.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        if (patient != null) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Selected Inpatient: ${patient.patientName}", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("IPD #${patient.ipdNumber} • ${patient.bedNumber} • Consultant: ${patient.consultantName}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = roomFeeText,
                        onValueChange = { roomFeeText = it },
                        label = { Text("Room Accommodation & Nursing Station Fee (₹)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = doctorFeeText,
                        onValueChange = { doctorFeeText = it },
                        label = { Text("Specialist / Consultant Visit Rounds (₹)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = medsFeeText,
                        onValueChange = { medsFeeText = it },
                        label = { Text("Inpatient IV Infusions & Pharmacy (₹)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = labFeeText,
                        onValueChange = { labFeeText = it },
                        label = { Text("Diagnostic Lab & Pathology (₹)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = consumablesFeeText,
                        onValueChange = { consumablesFeeText = it },
                        label = { Text("Consumables, PPE & Surgical Dressings (₹)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Surface(
                        color = Color(0xFFDCFCE7),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Shift Itemized Total:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("₹ ${"%,.2f".format(total)}", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF16A34A))
                        }
                    }

                    Button(
                        onClick = {
                            onLogCharge(
                                patient.ipdNumber,
                                patient.patientName,
                                patient.losDays + 1,
                                roomFeeText.toDoubleOrNull() ?: 0.0,
                                doctorFeeText.toDoubleOrNull() ?: 0.0,
                                medsFeeText.toDoubleOrNull() ?: 0.0,
                                labFeeText.toDoubleOrNull() ?: 0.0,
                                consumablesFeeText.toDoubleOrNull() ?: 0.0
                            )
                        },
                        modifier = Modifier.fillMaxWidth().testTag("btn_save_shift_charge")
                    ) {
                        Icon(Icons.Default.PostAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Log Shift Charges to Ledger")
                    }
                }
            }
        }
    }
}
