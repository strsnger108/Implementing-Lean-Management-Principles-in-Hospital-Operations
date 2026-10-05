package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Timeline
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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RootCauseAnalysis
import com.example.ui.components.KpiCard
import com.example.ui.viewmodel.HospitalViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RootCauseAnalysisScreen(
    viewModel: HospitalViewModel,
    modifier: Modifier = Modifier
) {
    val rcaList by viewModel.rootCauseAnalyses.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedStatusFilter by remember { mutableStateOf("All") }
    var selectedModuleFilter by remember { mutableStateOf("All") }
    var showNewRcaDialog by remember { mutableStateOf(false) }

    val totalCount = rcaList.size
    val resolvedCount = rcaList.count { it.status == "Resolved" }
    val inProgressCount = rcaList.count { it.status == "In Progress" }
    val openCount = rcaList.count { it.status == "Open" }

    val filteredList = rcaList.filter { rca ->
        val matchesQuery = searchQuery.isBlank() ||
                rca.title.contains(searchQuery, ignoreCase = true) ||
                rca.department.contains(searchQuery, ignoreCase = true) ||
                rca.rootCauseStatement.contains(searchQuery, ignoreCase = true) ||
                rca.actionOwner.contains(searchQuery, ignoreCase = true)

        val matchesStatus = when (selectedStatusFilter) {
            "Open" -> rca.status == "Open"
            "In Progress" -> rca.status == "In Progress"
            "Resolved" -> rca.status == "Resolved"
            else -> true
        }

        val matchesModule = when (selectedModuleFilter) {
            "All" -> true
            else -> rca.sourceModule.equals(selectedModuleFilter, ignoreCase = true)
        }

        matchesQuery && matchesStatus && matchesModule
    }

    Scaffold(
        modifier = modifier.testTag("root_cause_analysis_screen"),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showNewRcaDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                modifier = Modifier.testTag("fab_new_rca")
            ) {
                Row(modifier = Modifier.padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "New 5 Whys RCA")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("New 5 Whys", fontWeight = FontWeight.Bold, fontSize = 13.sp)
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
            // Header Surface
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Root Cause Analysis (5 Whys Technique)",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Investigate bottlenecks & errors flagged across LOS, 5S Audits, and VSM flows",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Summary KPI cards
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        KpiCard(
                            value = "$totalCount RCAs",
                            label = "Total Findings",
                            subtext = "Documented",
                            icon = Icons.Default.Psychology,
                            modifier = Modifier.weight(1f),
                            testTag = "kpi_rca_total"
                        )
                        KpiCard(
                            value = "$resolvedCount",
                            label = "Standardized",
                            subtext = "Root cause fixed",
                            isGood = true,
                            icon = Icons.Default.CheckCircle,
                            modifier = Modifier.weight(1f),
                            testTag = "kpi_rca_resolved"
                        )
                        KpiCard(
                            value = "$inProgressCount",
                            label = "In Progress",
                            subtext = "$openCount open",
                            isAlert = openCount > 0,
                            icon = Icons.Default.HourglassTop,
                            modifier = Modifier.weight(1f),
                            testTag = "kpi_rca_in_progress"
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Search input
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search by problem, department, or root cause...", fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                        modifier = Modifier.fillMaxWidth().testTag("input_search_rca"),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Filter Status Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("All", "Open", "In Progress", "Resolved").forEach { status ->
                            FilterChip(
                                selected = selectedStatusFilter == status,
                                onClick = { selectedStatusFilter = status },
                                label = { Text(status, fontSize = 11.sp) },
                                modifier = Modifier.testTag("filter_rca_status_$status")
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        listOf("All", "LOS Analytics", "5S Audit", "VSM Bottleneck", "Consultant Workload").forEach { mod ->
                            FilterChip(
                                selected = selectedModuleFilter == mod,
                                onClick = { selectedModuleFilter = mod },
                                label = { Text(if (mod == "All") "All Modules" else mod, fontSize = 11.sp) },
                                modifier = Modifier.testTag("filter_rca_module_${mod.replace(" ", "_")}")
                            )
                        }
                    }
                }
            }

            // RCA Findings List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    // Educational Banner
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "The 5 Whys Discipline in Lean Healthcare",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "By asking 'Why?' five times iteratively, teams drill past superficial human errors to uncover broken processes, communication silos, and absent standard operating procedures.",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                if (filteredList.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(
                                modifier = Modifier.padding(32.dp).fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FactCheck,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("No Root Cause Analyses found.", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                Text("Click 'New 5 Whys' below to document an investigation.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                } else {
                    items(filteredList, key = { it.id }) { rca ->
                        RootCauseCard(
                            rca = rca,
                            onStatusCycle = {
                                val nextStatus = when (rca.status) {
                                    "Open" -> "In Progress"
                                    "In Progress" -> "Resolved"
                                    else -> "Open"
                                }
                                viewModel.updateRootCauseAnalysis(rca.copy(status = nextStatus))
                            },
                            onDelete = { viewModel.deleteRootCauseAnalysis(rca) }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(72.dp)) // Space for FAB
                }
            }
        }

        // New 5 Whys Dialog Form
        if (showNewRcaDialog) {
            New5WhysAnalysisDialog(
                onDismiss = { showNewRcaDialog = false },
                onSave = { analysis ->
                    viewModel.addRootCauseAnalysis(analysis)
                    showNewRcaDialog = false
                }
            )
        }
    }
}

@Composable
fun RootCauseCard(
    rca: RootCauseAnalysis,
    onStatusCycle: () -> Unit,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val statusColor = when (rca.status) {
        "Resolved" -> Color(0xFF16A34A)
        "In Progress" -> Color(0xFFD97706)
        else -> Color(0xFFDC2626)
    }

    val severityColor = when (rca.severity) {
        "Critical" -> Color(0xFFDC2626)
        "High" -> Color(0xFFEA580C)
        "Medium" -> Color(0xFFD97706)
        else -> Color(0xFF16A34A)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("rca_card_${rca.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header row: Module & Severity + Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = rca.sourceModule,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Surface(
                        color = severityColor.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "${rca.severity} Impact",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = severityColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Surface(
                    color = statusColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.clickable { onStatusCycle() }.testTag("rca_status_btn_${rca.id}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(6.dp).background(statusColor, CircleShape))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = rca.status,
                            color = statusColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Title & Department
            Text(
                text = rca.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Department: ${rca.department}",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = rca.incidentDescription,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Root cause callout box
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Category,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Root Cause (${rca.rootCauseCategory}):",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = rca.rootCauseStatement,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Countermeasure Box
            Surface(
                color = Color(0xFFF0FDF4),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "Lean Countermeasure (Action Plan):",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF166534)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = rca.countermeasure,
                        fontSize = 12.sp,
                        color = Color(0xFF14532D)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Lead: ${rca.actionOwner}", fontSize = 10.sp, color = Color(0xFF15803D), fontWeight = FontWeight.SemiBold)
                        Text("Target: ${rca.targetDate}", fontSize = 10.sp, color = Color(0xFF15803D))
                    }
                    if (rca.targetKpiImpact.isNotBlank()) {
                        Text("Target KPI: ${rca.targetKpiImpact}", fontSize = 10.sp, color = Color(0xFF166534))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Expand 5 Whys Chain Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.testTag("btn_toggle_whys_${rca.id}")
                ) {
                    Text(if (expanded) "Hide 5 Whys Chain" else "View Full 5 Whys Ladder", fontSize = 11.sp)
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp).testTag("btn_delete_rca_${rca.id}")
                ) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete RCA", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                }
            }

            // Expanded 5 Whys Ladder
            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    WhyStepCard(stepNumber = 1, title = "Why #1 (Direct Trigger)", text = rca.why1)
                    WhyStepCard(stepNumber = 2, title = "Why #2 (Process Failure)", text = rca.why2)
                    WhyStepCard(stepNumber = 3, title = "Why #3 (Operational Gap)", text = rca.why3)
                    WhyStepCard(stepNumber = 4, title = "Why #4 (Systemic Deficiency)", text = rca.why4)
                    WhyStepCard(stepNumber = 5, title = "Why #5 (Root Vulnerability)", text = rca.why5, isRoot = true)
                }
            }
        }
    }
}

@Composable
fun WhyStepCard(
    stepNumber: Int,
    title: String,
    text: String,
    isRoot: Boolean = false
) {
    Surface(
        color = if (isRoot) Color(0xFFFEF2F2) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        shape = RoundedCornerShape(8.dp),
        border = if (isRoot) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDC2626)) else null,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(if (isRoot) Color(0xFFDC2626) else MaterialTheme.colorScheme.primary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$stepNumber",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isRoot) Color(0xFF991B1B) else MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = text,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

// -------------------------------------------------------------------------
// New 5 Whys Analysis Dialog Form
// -------------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun New5WhysAnalysisDialog(
    onDismiss: () -> Unit,
    onSave: (RootCauseAnalysis) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var department by remember { mutableStateOf("Billing Desk") }
    var sourceModule by remember { mutableStateOf("LOS Analytics") }
    var severity by remember { mutableStateOf("High") }
    var incidentDescription by remember { mutableStateOf("") }

    var why1 by remember { mutableStateOf("") }
    var why2 by remember { mutableStateOf("") }
    var why3 by remember { mutableStateOf("") }
    var why4 by remember { mutableStateOf("") }
    var why5 by remember { mutableStateOf("") }

    var rootCauseStatement by remember { mutableStateOf("") }
    var rootCauseCategory by remember { mutableStateOf("Process Design") }
    var countermeasure by remember { mutableStateOf("") }
    var actionOwner by remember { mutableStateOf("Quality & Lean Lead") }
    var targetDate by remember { mutableStateOf("2026-03-31") }
    var targetKpiImpact by remember { mutableStateOf("") }

    val modules = listOf("LOS Analytics", "5S Audit", "VSM Bottleneck", "Consultant Workload", "Ward Operations")
    val severities = listOf("Critical", "High", "Medium", "Low")
    val categories = listOf("Process Design", "Communication", "People / Training", "Policy / SOP", "Equipment / IT Infrastructure")

    // Pre-fill templates helper
    fun loadTemplate(index: Int) {
        when (index) {
            0 -> {
                // Discharge lag template
                title = "Late Afternoon Discharge Bottleneck"
                sourceModule = "LOS Analytics"
                department = "Billing Desk & Inpatient Wards"
                severity = "Critical"
                incidentDescription = "Patients remain in acute ward beds until late afternoon (2 PM–5 PM) after morning doctor discharge, stalling incoming admissions."
                why1 = "Billing clearance and gate pass issue takes 180 min after physician verbal discharge order."
                why2 = "Pharmacy drug return reconciliations and diagnostic charges only begin after physical discharge summary is delivered."
                why3 = "Physicians conduct discharge rounds late (1:30 PM–3:00 PM) after morning OPD clinics."
                why4 = "No standardized multi-disciplinary morning discharge rounds existed."
                why5 = "Clinical and administrative workflows operate in isolated departmental silos without synchronized Lean Value Stream coordination."
                rootCauseStatement = "Absence of standardized 10:00 AM multi-disciplinary rounds and uncoordinated sequential billing/pharmacy clearance."
                rootCauseCategory = "Process Design"
                countermeasure = "Implement standardized 10:00 AM multi-disciplinary rounds and 4:00 PM prior-day interim bill audit with express discharge lounge."
                actionOwner = "Dr. Rahul Sinha & Billing Lead"
                targetDate = "2026-03-25"
                targetKpiImpact = "Drop discharge turnaround time to ≤ 45 min and shift 65% discharges before noon."
            }
            1 -> {
                // 5S Audit template
                title = "Ward Supply Disorganization & Searching Waste"
                sourceModule = "5S Audit"
                department = "Inpatient Ward 3"
                severity = "High"
                incidentDescription = "Ward 3 scored 11/25 on 5S operational audit. Nurses lose 65 minutes per shift searching for sterile consumables and emergency medications."
                why1 = "Emergency medicines and routine supplies are mixed across unlabelled drawers."
                why2 = "Obsolete equipment and expired consumables take up 40% of shelving space."
                why3 = "Ward staff never segregated necessary supplies from obsolete materials."
                why4 = "No structured 5S sorting schedule or red-tagging mechanism was institutionalized."
                why5 = "Ward leadership lacked structured Lean 5S workplace organization standards and daily visual audit routines."
                rootCauseStatement = "Lack of visual workplace organization standards (5S) and absence of periodic sorting and red-tagging mechanisms."
                rootCauseCategory = "Policy / SOP"
                countermeasure = "Conduct 5S red-tag sorting event; install visual shadow boards and color-coded bins for top 20 supplies."
                actionOwner = "Nursing In-charge"
                targetDate = "2026-03-20"
                targetKpiImpact = "Achieve 5S audit score > 85% and eliminate 80% of supply search time."
            }
            2 -> {
                // Consultant Workload template
                title = "Physician Workload Imbalance & Rounding Delays"
                sourceModule = "Consultant Workload"
                department = "General Medicine"
                severity = "High"
                incidentDescription = "40.1% of hospital admissions flow to Dr. Rahul Sinha, leading to rounding delays and 110%+ capacity utilization."
                why1 = "Triage desk and emergency intake routinely assign all complex non-surgical cases to Dr. Sinha."
                why2 = "No formal admission caps or acuity-based rotational triage rules were defined."
                why3 = "Other visiting internal medicine physicians were underutilized (<35% capacity utilization)."
                why4 = "Intake desk assigns consultants by habit and recall rather than real-time capacity visibility."
                why5 = "The hospital lacked a real-time clinical workload and acuity leveling dashboard (Heijunka)."
                rootCauseStatement = "Absence of real-time clinical workload visibility and lack of institutionalized Heijunka patient assignment leveling."
                rootCauseCategory = "Process Design"
                countermeasure = "Deploy live Consultant Workload Management dashboard with automated cross-coverage referral leveling when load exceeds 85%."
                actionOwner = "Medical Director"
                targetDate = "2026-03-30"
                targetKpiImpact = "Maintain all medical consultants between 40% and 80% capacity utilization."
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Structured 5 Whys Root Cause Investigation", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        text = "Quick Load Clinical Template:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedButton(
                            onClick = { loadTemplate(0) },
                            modifier = Modifier.height(30.dp).testTag("template_los_discharge"),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("LOS Discharge Bottleneck", fontSize = 10.sp)
                        }

                        OutlinedButton(
                            onClick = { loadTemplate(1) },
                            modifier = Modifier.height(30.dp).testTag("template_5s_clutter"),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("5S Supply Clutter", fontSize = 10.sp)
                        }

                        OutlinedButton(
                            onClick = { loadTemplate(2) },
                            modifier = Modifier.height(30.dp).testTag("template_workload_bottleneck"),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Workload Bottleneck", fontSize = 10.sp)
                        }
                    }
                }

                item {
                    Text("1. Incident & Problem Statement", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }

                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Investigation Title") },
                        modifier = Modifier.fillMaxWidth().testTag("input_rca_title")
                    )
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Source Module
                        var modExpanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = modExpanded,
                            onExpandedChange = { modExpanded = it },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = sourceModule,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Module") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = modExpanded) },
                                modifier = Modifier.menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = modExpanded,
                                onDismissRequest = { modExpanded = false }
                            ) {
                                modules.forEach { m ->
                                    DropdownMenuItem(text = { Text(m) }, onClick = { sourceModule = m; modExpanded = false })
                                }
                            }
                        }

                        // Severity
                        var sevExpanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = sevExpanded,
                            onExpandedChange = { sevExpanded = it },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = severity,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Severity") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sevExpanded) },
                                modifier = Modifier.menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = sevExpanded,
                                onDismissRequest = { sevExpanded = false }
                            ) {
                                severities.forEach { s ->
                                    DropdownMenuItem(text = { Text(s) }, onClick = { severity = s; sevExpanded = false })
                                }
                            }
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = department,
                        onValueChange = { department = it },
                        label = { Text("Department / Location") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = incidentDescription,
                        onValueChange = { incidentDescription = it },
                        label = { Text("Incident Description (What happened?)") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("2. The 5 Whys Causal Ladder", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text("Drill down through each layer of causality until reaching the root vulnerability.", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                item {
                    OutlinedTextField(
                        value = why1,
                        onValueChange = { why1 = it },
                        label = { Text("Why #1: Direct trigger / immediate cause") },
                        modifier = Modifier.fillMaxWidth().testTag("input_why1"),
                        minLines = 2
                    )
                }

                item {
                    OutlinedTextField(
                        value = why2,
                        onValueChange = { why2 = it },
                        label = { Text("Why #2: Process failure / breakdown") },
                        modifier = Modifier.fillMaxWidth().testTag("input_why2"),
                        minLines = 2
                    )
                }

                item {
                    OutlinedTextField(
                        value = why3,
                        onValueChange = { why3 = it },
                        label = { Text("Why #3: Operational / communication gap") },
                        modifier = Modifier.fillMaxWidth().testTag("input_why3"),
                        minLines = 2
                    )
                }

                item {
                    OutlinedTextField(
                        value = why4,
                        onValueChange = { why4 = it },
                        label = { Text("Why #4: Systemic / procedural deficiency") },
                        modifier = Modifier.fillMaxWidth().testTag("input_why4"),
                        minLines = 2
                    )
                }

                item {
                    OutlinedTextField(
                        value = why5,
                        onValueChange = { why5 = it },
                        label = { Text("Why #5: Root organizational vulnerability") },
                        modifier = Modifier.fillMaxWidth().testTag("input_why5"),
                        minLines = 2
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("3. Root Cause Determination & Kaizen Countermeasures", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }

                item {
                    OutlinedTextField(
                        value = rootCauseStatement,
                        onValueChange = { rootCauseStatement = it },
                        label = { Text("Synthesized Root Cause Statement") },
                        modifier = Modifier.fillMaxWidth().testTag("input_rca_statement"),
                        minLines = 2
                    )
                }

                item {
                    var catExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = catExpanded,
                        onExpandedChange = { catExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = rootCauseCategory,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Root Cause Category") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = catExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = catExpanded,
                            onDismissRequest = { catExpanded = false }
                        ) {
                            categories.forEach { c ->
                                DropdownMenuItem(text = { Text(c) }, onClick = { rootCauseCategory = c; catExpanded = false })
                            }
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = countermeasure,
                        onValueChange = { countermeasure = it },
                        label = { Text("Proposed Lean Countermeasure (Kaizen)") },
                        modifier = Modifier.fillMaxWidth().testTag("input_rca_countermeasure"),
                        minLines = 2
                    )
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = actionOwner,
                            onValueChange = { actionOwner = it },
                            label = { Text("Action Owner") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = targetDate,
                            onValueChange = { targetDate = it },
                            label = { Text("Target Date") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    OutlinedTextField(
                        value = targetKpiImpact,
                        onValueChange = { targetKpiImpact = it },
                        label = { Text("Target KPI Impact (e.g. DTT < 45 min)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && why1.isNotBlank()) {
                        val analysis = RootCauseAnalysis(
                            title = title,
                            sourceModule = sourceModule,
                            department = department,
                            incidentDescription = incidentDescription,
                            severity = severity,
                            why1 = why1,
                            why2 = why2.ifBlank { why1 },
                            why3 = why3.ifBlank { why2 },
                            why4 = why4.ifBlank { why3 },
                            why5 = why5.ifBlank { why4 },
                            rootCauseStatement = rootCauseStatement.ifBlank { why5.ifBlank { why1 } },
                            rootCauseCategory = rootCauseCategory,
                            countermeasure = countermeasure.ifBlank { "Develop standardized SOP" },
                            actionOwner = actionOwner,
                            targetDate = targetDate,
                            targetKpiImpact = targetKpiImpact,
                            status = "Open"
                        )
                        onSave(analysis)
                    }
                },
                enabled = title.isNotBlank() && why1.isNotBlank(),
                modifier = Modifier.testTag("btn_save_rca")
            ) {
                Text("Save Investigation")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
