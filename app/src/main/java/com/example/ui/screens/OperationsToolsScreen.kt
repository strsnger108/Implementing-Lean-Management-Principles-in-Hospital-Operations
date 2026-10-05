package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.model.FiveSAudit
import com.example.data.model.KaizenProject
import com.example.ui.components.KpiCard
import com.example.ui.viewmodel.HospitalViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OperationsToolsScreen(
    viewModel: HospitalViewModel,
    modifier: Modifier = Modifier
) {
    var selectedToolTab by remember { mutableIntStateOf(0) }
    val fiveSAudits by viewModel.fiveSAudits.collectAsState()
    val kaizenProjects by viewModel.kaizenProjects.collectAsState()

    var showAdd5SDialog by remember { mutableStateOf(false) }
    var showAddKaizenDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.testTag("operations_tools_screen"),
        floatingActionButton = {
            if (selectedToolTab == 0) {
                FloatingActionButton(
                    onClick = { showAdd5SDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("fab_add_5s_audit")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add 5S Audit")
                }
            } else if (selectedToolTab == 1) {
                FloatingActionButton(
                    onClick = { showAddKaizenDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("fab_add_kaizen")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "New Kaizen Initiative")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Lean Operations & Quality Tools",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "5S Audits • Kaizen Continuous Improvement • Bed Capacity Simulator",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    ScrollableTabRow(
                        selectedTabIndex = selectedToolTab,
                        edgePadding = 0.dp,
                        containerColor = Color.Transparent
                    ) {
                        Tab(
                            selected = selectedToolTab == 0,
                            onClick = { selectedToolTab = 0 },
                            text = { Text("5 Whys RCA", fontWeight = FontWeight.SemiBold, fontSize = 12.sp) },
                            modifier = Modifier.testTag("tab_5_whys_rca")
                        )
                        Tab(
                            selected = selectedToolTab == 1,
                            onClick = { selectedToolTab = 1 },
                            text = { Text("Staff Workload", fontWeight = FontWeight.SemiBold, fontSize = 12.sp) },
                            modifier = Modifier.testTag("tab_staff_workload")
                        )
                        Tab(
                            selected = selectedToolTab == 2,
                            onClick = { selectedToolTab = 2 },
                            text = { Text("Kaizen Tracker", fontWeight = FontWeight.SemiBold, fontSize = 12.sp) },
                            modifier = Modifier.testTag("tab_kaizen_tracker")
                        )
                        Tab(
                            selected = selectedToolTab == 3,
                            onClick = { selectedToolTab = 3 },
                            text = { Text("Lean Simulator", fontWeight = FontWeight.SemiBold, fontSize = 12.sp) },
                            modifier = Modifier.testTag("tab_lean_simulator")
                        )
                        Tab(
                            selected = selectedToolTab == 4,
                            onClick = { selectedToolTab = 4 },
                            text = { Text("Study Report", fontWeight = FontWeight.SemiBold, fontSize = 12.sp) },
                            modifier = Modifier.testTag("tab_study_report")
                        )
                        Tab(
                            selected = selectedToolTab == 5,
                            onClick = { selectedToolTab = 5 },
                            text = { Text("OPD Queues", fontWeight = FontWeight.SemiBold, fontSize = 12.sp) },
                            modifier = Modifier.testTag("tab_opd_queues")
                        )
                        Tab(
                            selected = selectedToolTab == 6,
                            onClick = { selectedToolTab = 6 },
                            text = { Text("HMS / EHR Sync", fontWeight = FontWeight.SemiBold, fontSize = 12.sp) },
                            modifier = Modifier.testTag("tab_hms_sync")
                        )
                    }
                }
            }

            when (selectedToolTab) {
                0 -> {
                    // Root Cause Analysis (5 Whys Technique)
                    RootCauseAnalysisScreen(
                        viewModel = viewModel,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                1 -> {
                    // Consultant Workload & Patient Acuity Management
                    ConsultantWorkloadScreen(
                        viewModel = viewModel,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                2 -> {
                    // Kaizen Action Tracker
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(kaizenProjects, key = { it.id }) { project ->
                            KaizenCard(
                                project = project,
                                onStatusChange = { newStatus -> viewModel.updateKaizenStatus(project, newStatus) }
                            )
                        }
                    }
                }
                3 -> {
                    // Lean Simulator
                    LeanCapacitySimulator(viewModel = viewModel)
                }
                4 -> {
                    // Study Report
                    StudyReportScreen(modifier = Modifier.fillMaxSize())
                }
                5 -> {
                    // Live OPD Queues & Waiting Times
                    OpdWaitingTimeScreen(
                        viewModel = viewModel,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                6 -> {
                    // Hospital HMS & EHR Auto-Sync Integration Hub
                    HmsEhrSyncScreen(
                        viewModel = viewModel,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        // Add 5S Audit Dialog
        if (showAdd5SDialog) {
            AddFiveSAuditDialog(
                onDismiss = { showAdd5SDialog = false },
                onConfirm = { dept, s1, s2, s3, s4, s5, auditor, rem ->
                    viewModel.addFiveSAudit(dept, s1, s2, s3, s4, s5, auditor, rem)
                    showAdd5SDialog = false
                }
            )
        }

        // Add Kaizen Dialog
        if (showAddKaizenDialog) {
            AddKaizenDialog(
                onDismiss = { showAddKaizenDialog = false },
                onConfirm = { title, dept, prob, sol, target, lead, saved ->
                    viewModel.addKaizenProject(title, dept, prob, sol, target, lead, saved)
                    showAddKaizenDialog = false
                }
            )
        }
    }
}

@Composable
fun FiveSAuditCard(audit: FiveSAudit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = audit.department,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Surface(
                    color = if (audit.percentage >= 80.0) Color(0xFFDCFCE7) else Color(0xFFFEF3C7),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "${audit.totalScore}/25 (${String.format("%.0f", audit.percentage)}%)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (audit.percentage >= 80.0) Color(0xFF166534) else Color(0xFFB45309),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 5S Score Pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ScorePill(name = "Sort", score = audit.sortScore)
                ScorePill(name = "Set", score = audit.setInOrderScore)
                ScorePill(name = "Shine", score = audit.shineScore)
                ScorePill(name = "Std", score = audit.standardizeScore)
                ScorePill(name = "Sustain", score = audit.sustainScore)
            }

            Spacer(modifier = Modifier.height(10.dp))
            LinearProgressIndicator(
                progress = { (audit.percentage / 100.0).toFloat() },
                modifier = Modifier.fillMaxWidth().height(6.dp),
                color = if (audit.percentage >= 80.0) Color(0xFF16A34A) else Color(0xFFD97706),
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            if (audit.remarks.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Findings: ${audit.remarks}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Audited by: ${audit.auditorName} • ${audit.auditDate}",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ScorePill(name: String, score: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(name, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Surface(
            shape = CircleShape,
            color = when {
                score >= 4 -> Color(0xFFDCFCE7)
                score == 3 -> Color(0xFFFEF3C7)
                else -> Color(0xFFFEE2E2)
            },
            modifier = Modifier.size(24.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "$score",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        score >= 4 -> Color(0xFF166534)
                        score == 3 -> Color(0xFF92400E)
                        else -> Color(0xFF991B1B)
                    }
                )
            }
        }
    }
}

@Composable
fun KaizenCard(
    project: KaizenProject,
    onStatusChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = project.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (project.status) {
                        "Implemented" -> Color(0xFFDCFCE7)
                        "In Progress" -> Color(0xFFFEF3C7)
                        else -> Color(0xFFE0F2FE)
                    }
                ) {
                    Text(
                        text = project.status,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (project.status) {
                            "Implemented" -> Color(0xFF166534)
                            "In Progress" -> Color(0xFF92400E)
                            else -> Color(0xFF0369A1)
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Problem: ${project.problemStatement}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Countermeasure: ${project.proposedCountermeasure}", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Target Metric: ${project.targetMetric}", fontSize = 11.sp, color = Color(0xFF16A34A), fontWeight = FontWeight.SemiBold)

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Impact: ~${project.estimatedBedDaysSavedYearly} bed-days freed/yr",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row {
                    if (project.status != "In Progress") {
                        TextButton(onClick = { onStatusChange("In Progress") }) {
                            Text("Set In Progress", fontSize = 11.sp)
                        }
                    }
                    if (project.status != "Implemented") {
                        Button(
                            onClick = { onStatusChange("Implemented") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A))
                        ) {
                            Text("Mark Done", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LeanCapacitySimulator(viewModel: HospitalViewModel) {
    var targetLos by remember { mutableFloatStateOf(2.8f) }
    var bedCost by remember { mutableFloatStateOf(3500f) }
    var annualAdmissions by remember { mutableFloatStateOf(1143f) }

    val baselineLos = 3.29f
    val losDelta = (baselineLos - targetLos).coerceAtLeast(0f)
    val totalBedDaysSaved = (annualAdmissions * losDelta).toInt()
    val additionalPatientsAccommodated = if (targetLos > 0) (totalBedDaysSaved / targetLos).toInt() else 0
    val financialOpportunityINR = (totalBedDaysSaved * bedCost).toLong()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Hospital Bed Capacity & Financial Simulator",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Simulate the operational and financial impact of reducing Average Length of Stay (LOS) at Synergy Global Hospital",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Target Avg LOS: ${String.format("%.2f", targetLos)} days (Baseline: 3.29 days)", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Slider(
                        value = targetLos,
                        onValueChange = { targetLos = it },
                        valueRange = 2.0f..3.5f,
                        steps = 14,
                        modifier = Modifier.testTag("slider_target_los")
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Daily Hospital Bed Value: ₹${bedCost.toInt()} / day", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Slider(
                        value = bedCost,
                        onValueChange = { bedCost = it },
                        valueRange = 1500f..7000f,
                        steps = 11
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Projected Annual Inpatients: ${annualAdmissions.toInt()} admissions", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Slider(
                        value = annualAdmissions,
                        onValueChange = { annualAdmissions = it },
                        valueRange = 800f..2000f,
                        steps = 12
                    )
                }
            }
        }

        item {
            Text("Simulation Results & Capacity Released", fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                KpiCard(
                    value = "$totalBedDaysSaved",
                    label = "Annual Bed-Days Freed",
                    subtext = "${String.format("%.1f", (losDelta / baselineLos) * 100)}% capacity unlocked",
                    isGood = true,
                    icon = Icons.Default.Hotel,
                    modifier = Modifier.weight(1f)
                )
                KpiCard(
                    value = "+$additionalPatientsAccommodated",
                    label = "Additional Patients",
                    subtext = "Without building new beds",
                    isGood = true,
                    icon = Icons.Default.CheckCircle,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Projected Annual Financial Value Released",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF93C5FD)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "₹ ${String.format("%,d", financialOpportunityINR)}",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF38BDF8)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Derived from ${totalBedDaysSaved} bed-days freed × ₹${bedCost.toInt()} daily overhead & opportunity value.",
                        fontSize = 11.sp,
                        color = Color(0xFFCBD5E1)
                    )
                }
            }
        }
    }
}

@Composable
fun AddFiveSAuditDialog(
    onDismiss: () -> Unit,
    onConfirm: (dept: String, sort: Int, set: Int, shine: Int, std: Int, sus: Int, auditor: String, remarks: String) -> Unit
) {
    var department by remember { mutableStateOf("Emergency Department") }
    var sort by remember { mutableIntStateOf(4) }
    var setInOrder by remember { mutableIntStateOf(4) }
    var shine by remember { mutableIntStateOf(4) }
    var standardize by remember { mutableIntStateOf(3) }
    var sustain by remember { mutableIntStateOf(3) }
    var auditor by remember { mutableStateOf("Gunjan Prakash") }
    var remarks by remember { mutableStateOf("") }

    val departments = listOf("Emergency Department", "Central Pharmacy", "Inpatient Ward 2", "Inpatient Ward 3", "Operation Theatre", "Billing Counter", "Pathology Lab")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Record Department 5S Audit") },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    Text("Department: $department", fontWeight = FontWeight.Bold)
                }
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Sort (Seiri) 1-5:")
                        ScoreSelector(value = sort, onSelect = { sort = it })
                    }
                }
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Set in Order (Seiton) 1-5:")
                        ScoreSelector(value = setInOrder, onSelect = { setInOrder = it })
                    }
                }
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Shine (Seiso) 1-5:")
                        ScoreSelector(value = shine, onSelect = { shine = it })
                    }
                }
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Standardize (Seiketsu) 1-5:")
                        ScoreSelector(value = standardize, onSelect = { standardize = it })
                    }
                }
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Sustain (Shitsuke) 1-5:")
                        ScoreSelector(value = sustain, onSelect = { sustain = it })
                    }
                }
                item {
                    OutlinedTextField(
                        value = auditor,
                        onValueChange = { auditor = it },
                        label = { Text("Auditor Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = remarks,
                        onValueChange = { remarks = it },
                        label = { Text("Audit Observations") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(department, sort, setInOrder, shine, standardize, sustain, auditor, remarks) }) {
                Text("Save Audit")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun ScoreSelector(value: Int, onSelect: (Int) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        (1..5).forEach { score ->
            Surface(
                shape = CircleShape,
                color = if (value == score) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.size(28.dp),
                onClick = { onSelect(score) }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "$score",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (value == score) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun AddKaizenDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, dept: String, problem: String, solution: String, target: String, lead: String, saved: Int) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var department by remember { mutableStateOf("Hospital Operations") }
    var problem by remember { mutableStateOf("") }
    var solution by remember { mutableStateOf("") }
    var target by remember { mutableStateOf("") }
    var lead by remember { mutableStateOf("Gunjan Prakash") }
    var savedDaysStr by remember { mutableStateOf("120") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Kaizen Improvement Project") },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Project Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = department,
                        onValueChange = { department = it },
                        label = { Text("Department") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = problem,
                        onValueChange = { problem = it },
                        label = { Text("Problem Statement") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = solution,
                        onValueChange = { solution = it },
                        label = { Text("Proposed Lean Countermeasure") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = target,
                        onValueChange = { target = it },
                        label = { Text("Target Metric (e.g. < 45m discharge)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = savedDaysStr,
                        onValueChange = { savedDaysStr = it },
                        label = { Text("Est. Bed-Days Freed / Year") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirm(
                    title.ifBlank { "Discharge Process Optimization" },
                    department,
                    problem,
                    solution,
                    target,
                    lead,
                    savedDaysStr.toIntOrNull() ?: 100
                )
            }) {
                Text("Launch Kaizen")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
