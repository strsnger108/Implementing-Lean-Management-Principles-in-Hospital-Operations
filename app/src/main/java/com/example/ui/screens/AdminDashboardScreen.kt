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
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SupervisedUserCircle
import androidx.compose.material.icons.filled.SwitchAccount
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
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
import com.example.data.model.DailyIpdBillEntry
import com.example.data.model.PatientFeedback
import com.example.ui.components.DepartmentLosBenchmarkCard
import com.example.ui.components.KpiCard
import com.example.ui.components.LogOperationalWasteDialog
import com.example.ui.components.RoleSwitcherDialog
import com.example.ui.viewmodel.HospitalViewModel

@Composable
fun AdminDashboardScreen(
    viewModel: HospitalViewModel,
    onSwitchRole: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activeSession by viewModel.activeUserSession.collectAsState()
    val allPatients by viewModel.allPatients.collectAsState()
    val allBills by viewModel.allBills.collectAsState()
    val allFeedbacks by viewModel.allFeedbacks.collectAsState()
    val consultantWorkloads by viewModel.consultantWorkloads.collectAsState()
    val throughput by viewModel.currentThroughputMetrics.collectAsState()
    val hmsConfig by viewModel.hmsConfig.collectAsState()
    val isSyncing by viewModel.isHmsSyncing.collectAsState()

    var selectedAdminTab by remember { mutableIntStateOf(0) }
    var showRoleDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("admin_dashboard_screen")
    ) {
        // Top Command Bar
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
                            color = Color(0xFFFEE2E2),
                            shape = CircleShape,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.AdminPanelSettings,
                                    contentDescription = null,
                                    tint = Color(0xFFDC2626),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = activeSession?.fullName ?: "Medical Director & Operations Head",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = Color(0xFFDC2626).copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(4.dp),
                                    modifier = Modifier.clickable { showRoleDialog = true }
                                ) {
                                    Text(
                                        text = "ADMIN",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFDC2626),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Hospital Command & Operations Center • Synergy Global Hospital",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { showRoleDialog = true },
                            modifier = Modifier.testTag("btn_admin_switch_role")
                        ) {
                            Icon(Icons.Default.SwitchAccount, contentDescription = "Switch Role", tint = MaterialTheme.colorScheme.primary)
                        }
                        IconButton(
                            onClick = { viewModel.logout() },
                            modifier = Modifier.testTag("btn_admin_logout")
                        ) {
                            Icon(Icons.Default.Logout, contentDescription = "Logout", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Hospital Census Bar
                Surface(
                    color = Color(0xFF0F172A),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Live Census: 142/180 Beds (78.9% Occupancy)",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "ALOS: 3.29 Days • Lean Benchmark: 2.80 Days",
                            color = Color(0xFF4ADE80),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Scrollable Navigation Tabs for Admin
                ScrollableTabRow(
                    selectedTabIndex = selectedAdminTab,
                    edgePadding = 0.dp,
                    containerColor = Color.Transparent
                ) {
                    Tab(
                        selected = selectedAdminTab == 0,
                        onClick = { selectedAdminTab = 0 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Dashboard, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Executive Lean Command", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        },
                        modifier = Modifier.testTag("tab_admin_lean")
                    )

                    Tab(
                        selected = selectedAdminTab == 1,
                        onClick = { selectedAdminTab = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Midnight Billing Audit & TPA", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        },
                        modifier = Modifier.testTag("tab_admin_billing_audit")
                    )

                    Tab(
                        selected = selectedAdminTab == 2,
                        onClick = { selectedAdminTab = 2 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.SupervisedUserCircle, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Consultant Workload (Heijunka)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        },
                        modifier = Modifier.testTag("tab_admin_consultant")
                    )

                    Tab(
                        selected = selectedAdminTab == 3,
                        onClick = { selectedAdminTab = 3 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Patient Experience & Google Reviews", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        },
                        modifier = Modifier.testTag("tab_admin_feedback")
                    )

                    Tab(
                        selected = selectedAdminTab == 4,
                        onClick = { selectedAdminTab = 4 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.FactCheck, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Quality, 5S & Kaizen", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        },
                        modifier = Modifier.testTag("tab_admin_quality")
                    )

                    Tab(
                        selected = selectedAdminTab == 5,
                        onClick = { selectedAdminTab = 5 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("HMS / EHR & ABDM Gateway", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        },
                        modifier = Modifier.testTag("tab_admin_abdm")
                    )
                }
            }
        }

        // Tab Content
        when (selectedAdminTab) {
            0 -> {
                // Tab 0: Executive Lean Command
                AdminExecutiveLeanSection(
                    throughput = throughput,
                    benchmarks = viewModel.departmentBenchmarks
                )
            }

            1 -> {
                // Tab 1: Midnight Billing Audit & TPA Approvals Desk
                AdminBillingAuditSection(
                    bills = allBills,
                    onApproveSync = {
                        Toast.makeText(context, "Morning Ledger Audit Signed Off! Synchronized with patient dashboards.", Toast.LENGTH_LONG).show()
                    }
                )
            }

            2 -> {
                // Tab 2: Consultant Workload Leveling (Heijunka)
                AdminHeijunkaWorkloadSection(workloads = consultantWorkloads)
            }

            3 -> {
                // Tab 3: Patient Experience & Google Reviews Hub
                AdminPatientExperienceSection(feedbacks = allFeedbacks)
            }

            4 -> {
                // Tab 4: Quality, 5S & Kaizen Tracker
                AdminQualitySection(viewModel = viewModel)
            }

            5 -> {
                // Tab 5: ABDM Gateway & HMS/EHR Sync
                AdminAbdmGatewaySection(
                    config = hmsConfig,
                    isSyncing = isSyncing,
                    onTriggerSync = { viewModel.triggerHmsSync() }
                )
            }
        }

        if (showRoleDialog) {
            RoleSwitcherDialog(
                currentRole = activeSession?.role ?: "ADMIN",
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
// Tab 0: Executive Lean Command Section
// -------------------------------------------------------------------------
@Composable
fun AdminExecutiveLeanSection(
    throughput: com.example.data.model.ThroughputMetrics,
    benchmarks: List<com.example.data.model.DepartmentLosBenchmark>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("Executive Operations & Lean Command", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Text("National benchmark comparison, bed turnover (SMED), and turnaround KPIs", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                KpiCard(
                    label = "Actual ALOS",
                    value = "${throughput.actualAvgLos}d",
                    subtext = "Benchmark: 4.20d",
                    isAlert = throughput.actualAvgLos > 3.5,
                    modifier = Modifier.weight(1f)
                )
                KpiCard(
                    label = "Lean Target ALOS",
                    value = "${throughput.leanTargetLos}d",
                    subtext = "Potential: +1,040 Bed Days",
                    isGood = true,
                    modifier = Modifier.weight(1f)
                )
                KpiCard(
                    label = "Discharge Turnaround",
                    value = "${throughput.avgDischargeTurnaroundMinutes}m",
                    subtext = "Lean Target: 45m (75% Cut)",
                    isAlert = true,
                    modifier = Modifier.weight(1.1f)
                )
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Bed Turnover Interval & SMED Performance", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Current Turnover Interval: 18.4 hours between discharge and next admission.", fontSize = 11.sp)
                    Text("SMED Lean Countermeasure Target: < 4 hours bed turnaround time.", fontSize = 11.sp, color = Color(0xFF16A34A), fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // Length of Stay (LOS) Analytics & Department Benchmarks Bar Chart Card
        item {
            DepartmentLosBenchmarkCard(
                benchmarks = benchmarks,
                hospitalAvgLos = throughput.actualAvgLos,
                hospitalNationalBenchmark = throughput.nationalBenchmarkLos,
                hospitalLeanTarget = throughput.leanTargetLos,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun DeptBenchmarkRow(dept: String, actual: Double, benchmark: Double, status: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(dept, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Text("Actual: ${actual}d • Benchmark: ${benchmark}d", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Surface(
            color = if (status == "Extended LOS Alert") Color(0xFFFEE2E2) else Color(0xFFDCFCE7),
            shape = RoundedCornerShape(4.dp)
        ) {
            Text(
                text = status,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = if (status == "Extended LOS Alert") Color(0xFFDC2626) else Color(0xFF16A34A),
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
    }
}

// -------------------------------------------------------------------------
// Tab 1: Midnight Billing Audit & TPA Approvals Desk
// -------------------------------------------------------------------------
@Composable
fun AdminBillingAuditSection(
    bills: List<com.example.data.model.PatientBill>,
    onApproveSync: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("Midnight Billing Reconciliation & TPA Approvals", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Text("Audit daily inpatient charges before 07:30 AM morning synchronization with patient dashboards.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBF7D0)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Morning Audit Status: Verified", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF166534))
                        }
                        Text("Cutoff: 07:30 AM", fontSize = 11.sp, color = Color(0xFF166534))
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("All 12 Inpatient Wards reconciled. Daily room rent, specialist visits, and medications validated for morning sync.", fontSize = 11.sp, color = Color(0xFF14532D))
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = onApproveSync,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                        modifier = Modifier.fillMaxWidth().testTag("btn_approve_morning_sync")
                    ) {
                        Icon(Icons.Default.AssignmentTurnedIn, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Sign-Off & Synchronize Patient Portals")
                    }
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("TPA / Cashless Insurance Monitoring", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    TpaPolicyAdminRow(provider = "Star Health & Allied Insurance", policy = "POL-SH-9842109", approved = 150000.0, status = "Pre-Auth Approved (Cashless Active)")
                    TpaPolicyAdminRow(provider = "Medi Assist TPA (National)", policy = "POL-MA-774129", approved = 80000.0, status = "Enhancement Under Review")
                }
            }
        }
    }
}

@Composable
fun TpaPolicyAdminRow(provider: String, policy: String, approved: Double, status: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(provider, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text("Policy: $policy • Limit: ₹ ${"%,.0f".format(approved)}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Surface(
            color = if (status.contains("Active")) Color(0xFFDCFCE7) else Color(0xFFFEF3C7),
            shape = RoundedCornerShape(4.dp)
        ) {
            Text(
                text = status,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = if (status.contains("Active")) Color(0xFF16A34A) else Color(0xFFD97706),
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
    }
}

// -------------------------------------------------------------------------
// Tab 2: Consultant Workload Leveling (Heijunka)
// -------------------------------------------------------------------------
@Composable
fun AdminHeijunkaWorkloadSection(workloads: List<com.example.data.model.ConsultantWorkload>) {
    var heijunkaEnabled by remember { mutableStateOf(true) }

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
                    Text("Consultant Workload Leveling (Heijunka)", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Text("Prevent single-physician bottlenecking & level acuity", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Surface(
                    color = if (heijunkaEnabled) Color(0xFFDCFCE7) else Color(0xFFF1F5F9),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.clickable { heijunkaEnabled = !heijunkaEnabled }
                ) {
                    Text(
                        text = if (heijunkaEnabled) "Heijunka Active" else "Manual Leveling",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (heijunkaEnabled) Color(0xFF16A34A) else Color.Gray,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        items(workloads) { wl ->
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
                            Text(wl.consultantName, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("${wl.department} • ${wl.totalPatientCount} Active Patients", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Surface(
                            color = when (wl.workloadStatus) {
                                com.example.data.model.WorkloadStatus.OVERLOADED -> Color(0xFFFEE2E2)
                                com.example.data.model.WorkloadStatus.OPTIMAL -> Color(0xFFDCFCE7)
                                else -> Color(0xFFFEF3C7)
                            },
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "${wl.capacityUtilizationPct.toInt()}% Capacity",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (wl.workloadStatus) {
                                    com.example.data.model.WorkloadStatus.OVERLOADED -> Color(0xFFDC2626)
                                    com.example.data.model.WorkloadStatus.OPTIMAL -> Color(0xFF16A34A)
                                    else -> Color(0xFFD97706)
                                },
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// Tab 3: Patient Experience & Google Reviews Hub
// -------------------------------------------------------------------------
@Composable
fun AdminPatientExperienceSection(feedbacks: List<PatientFeedback>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Patient Experience & Google Reviews Command", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Text("Departmental ratings (1 to 10) and Google Reviews live synchronization", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        // NPS & Rating Scoreboard
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Hospital Quality Index (NPS: 9.1 / 10)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Doctor Consultation Care:", fontSize = 11.sp)
                        Text("9.2 / 10", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Nursing Promptness:", fontSize = 11.sp)
                        Text("8.8 / 10", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Hospital Cleanliness & Hygiene:", fontSize = 11.sp)
                        Text("9.4 / 10", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Waiting Time & Billing Transparency:", fontSize = 11.sp)
                        Text("8.6 / 10", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0284C7))
                    }
                }
            }
        }

        item {
            Text("Live Patient Reviews & Google Reviews Stream", fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }

        items(feedbacks, key = { it.id }) { fb ->
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
                            Text(fb.patientName, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("${fb.encounterType} • ${fb.submissionDate}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(color = Color(0xFFDCFCE7), shape = RoundedCornerShape(4.dp)) {
                                Text(
                                    text = "${fb.averageScore} / 10",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF16A34A),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            if (fb.googleReviewSynced) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(color = Color(0xFFE0F2FE), shape = RoundedCornerShape(4.dp)) {
                                    Text(
                                        text = "Google Maps Synced G",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0369A1),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(fb.comments, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// Tab 4: Quality, 5S & Kaizen Tracker
// -------------------------------------------------------------------------
@Composable
fun AdminQualitySection(viewModel: HospitalViewModel) {
    val redTags by viewModel.redTags.collectAsState()
    val capas by viewModel.capas.collectAsState()
    val kaizens by viewModel.kaizenProjects.collectAsState()
    val wasteLogs by viewModel.wasteLogs.collectAsState()
    var showLogMudaDialog by remember { mutableStateOf(false) }

    val totalMudaMinutes = remember(wasteLogs) { wasteLogs.sumOf { it.estimatedMinutesLost } }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Hospital Quality, 5S & Kaizen Improvements", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Text("NABH / 5S workplace audits, defective equipment tags & Lean Kaizen projects", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        // VSM & Muda Waste Summary Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Value Stream Muda & Waste Logs", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
                            Text("${wasteLogs.size} Gemba observations • ${"%.1f".format(totalMudaMinutes / 60.0)} hrs lost", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Button(
                            onClick = { showLogMudaDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("+ Log Muda", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        item {
            Text("5S Red Tag Quarantine Log (${redTags.size} Tagged Items)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }

        items(redTags) { tag ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("${tag.tagNumber} • ${tag.itemName}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Surface(color = if (tag.status == "Resolved") Color(0xFFDCFCE7) else Color(0xFFFEE2E2), shape = RoundedCornerShape(4.dp)) {
                            Text(tag.status, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = if (tag.status == "Resolved") Color(0xFF16A34A) else Color(0xFFDC2626), modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                        }
                    }
                    Text("Dept: ${tag.department} • Reason: ${tag.reason} • Action: ${tag.actionRequired}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }

    if (showLogMudaDialog) {
        LogOperationalWasteDialog(
            onDismiss = { showLogMudaDialog = false },
            onConfirm = { cat, dept, desc, min, sev, root ->
                viewModel.addWasteObservation(cat, dept, desc, min, sev, root)
                showLogMudaDialog = false
            }
        )
    }
}

// -------------------------------------------------------------------------
// Tab 5: ABDM Gateway & HMS/EHR Sync
// -------------------------------------------------------------------------
@Composable
fun AdminAbdmGatewaySection(
    config: com.example.data.model.HmsEhrConfig?,
    isSyncing: Boolean,
    onTriggerSync: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("ABDM Gateway & HMS / EHR Integration", fontSize = 15.sp, fontWeight = FontWeight.Bold)
        Text("Ayushman Bharat Digital Mission (ABDM) FHIR R4 Health Repository Gateway", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(color = Color(0xFFDCFCE7), shape = CircleShape, modifier = Modifier.size(32.dp)) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(18.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("ABDM HIP / HIU Bridge", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("HFR Code: JH-SGH-0091", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Surface(color = Color(0xFFDCFCE7), shape = RoundedCornerShape(4.dp)) {
                        Text("Active & Verified", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text("Endpoint: ${config?.endpointUrl ?: "https://hms.synergyhospital.org/api/v2/ehr-sync"}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Last Sync: ${config?.lastSyncStatus ?: "All Inpatient & OPD records in sync"}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onTriggerSync,
                    enabled = !isSyncing,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isSyncing) "Synchronizing Gateway..." else "Trigger Immediate ABDM Sync")
                }
            }
        }
    }
}
