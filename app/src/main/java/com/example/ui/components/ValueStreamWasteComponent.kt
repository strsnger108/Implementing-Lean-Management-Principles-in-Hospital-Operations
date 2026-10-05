package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Speed
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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LeanWasteLog
import com.example.data.model.VsmStage
import com.example.ui.viewmodel.HospitalViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 8 Types of Operational Waste (Muda) in Healthcare
 * D.O.W.N.T.I.M.E. taxonomy adapted for clinical operations
 */
enum class MudaWasteCategory(
    val key: String,
    val title: String,
    val icon: ImageVector,
    val primaryColor: Color,
    val bgLightColor: Color,
    val clinicalDescription: String,
    val commonExamples: String,
    val typicalCountermeasure: String
) {
    WAITING(
        key = "Waiting",
        title = "Waiting (Delays)",
        icon = Icons.Default.HourglassBottom,
        primaryColor = Color(0xFFDC2626),
        bgLightColor = Color(0xFFFEE2E2),
        clinicalDescription = "Idle time waiting for next step: doctor rounds, diagnostic reports, TPA pre-authorization, or discharge papers.",
        commonExamples = "Patients waiting in OPD queues, delayed morning rounds delaying discharge, waiting for lab results.",
        typicalCountermeasure = "Visual Management, Heijunka (schedule leveling), Parallel processing of discharge paperwork."
    ),
    DEFECTS(
        key = "Defects",
        title = "Defects & Rework",
        icon = Icons.Default.ReportProblem,
        primaryColor = Color(0xFFE11D48),
        bgLightColor = Color(0xFFFFE4E6),
        clinicalDescription = "Clinical or clerical errors requiring correction: hemolyzed blood samples, wrong medication doses, illegible handwriting.",
        commonExamples = "Unlabeled blood tubes, missing physician signature on claim form, medication dosage re-clarification.",
        typicalCountermeasure = "Poka-Yoke (Mistake-proofing), Barcode scanning, Electronic Health Record validation gates."
    ),
    MOTION(
        key = "Motion",
        title = "Excess Motion",
        icon = Icons.Default.DirectionsWalk,
        primaryColor = Color(0xFF0284C7),
        bgLightColor = Color(0xFFE0F2FE),
        clinicalDescription = "Unnecessary physical movement by healthcare staff searching for supplies, medical files, or portable equipment.",
        commonExamples = "Nurses walking multiple times to central pharmacy, hunting for IV poles, searching for lost physical files.",
        typicalCountermeasure = "Point-of-Use Storage, 5S Organization, Decentralized satellite nursing medication stations."
    ),
    OVERPROCESSING(
        key = "Overprocessing",
        title = "Overprocessing",
        icon = Icons.Default.Psychology,
        primaryColor = Color(0xFF7C3AED),
        bgLightColor = Color(0xFFEDE9FE),
        clinicalDescription = "Performing more work or paperwork than required for clinical safety: redundant signatures, duplicate patient logs.",
        commonExamples = "Writing patient vitals in three different manual registers, multiple doctor countersignatures for routine tests.",
        typicalCountermeasure = "Single-source digital entry, Streamlined clinical protocols, Elimination of redundant forms."
    ),
    INVENTORY(
        key = "Inventory",
        title = "Excess Inventory",
        icon = Icons.Default.Inventory,
        primaryColor = Color(0xFFD97706),
        bgLightColor = Color(0xFFFEF3C7),
        clinicalDescription = "Excessive stock of pharmaceuticals, surgical supplies, or patients queuing in holding areas.",
        commonExamples = "Expired drugs in ward cupboard, overflowing suture boxes, admitted patients waiting on trolleys in corridors.",
        typicalCountermeasure = "Kanban 2-Bin replenishment, Just-in-Time (JIT) pharmacy requisition, FIFO inventory rotation."
    ),
    TRANSPORTATION(
        key = "Transportation",
        title = "Transportation",
        icon = Icons.Default.LocalShipping,
        primaryColor = Color(0xFF0D9488),
        bgLightColor = Color(0xFFCCFBF1),
        clinicalDescription = "Unnecessary movement of patients, lab specimens, medications, or equipment between buildings/floors.",
        commonExamples = "Transferring patients repeatedly between holding areas, physical transport of paper blood requisition slips.",
        typicalCountermeasure = "Pneumatic tube systems, Co-location of related clinical departments, Cellular ward layouts."
    ),
    OVERPRODUCTION(
        key = "Overproduction",
        title = "Overproduction",
        icon = Icons.Default.Speed,
        primaryColor = Color(0xFF4F46E5),
        bgLightColor = Color(0xFFEEF2FF),
        clinicalDescription = "Ordering investigations, medications, or preparing meals earlier or in larger quantities than needed.",
        commonExamples = "Routine daily lab orders not clinically indicated, pre-printing discharge packets for patients not yet approved.",
        typicalCountermeasure = "Pull-system ordering, Evidence-based order sets, Strict clinical criteria for repeat tests."
    ),
    TALENT(
        key = "Underutilized Talent",
        title = "Non-Utilized Talent",
        icon = Icons.Default.PersonOutline,
        primaryColor = Color(0xFF475569),
        bgLightColor = Color(0xFFF1F5F9),
        clinicalDescription = "Failing to engage staff expertise or assigning tasks below credentials (e.g., senior doctors doing billing clerk tasks).",
        commonExamples = "Consultants making manual phone calls to trace lab results, experienced nurses handling billing clearance errands.",
        typicalCountermeasure = "Top-of-license nursing practice, Dedicated ward coordinators, Multidisciplinary huddles."
    );

    companion object {
        fun fromKey(key: String): MudaWasteCategory {
            return entries.firstOrNull { it.key.equals(key, ignoreCase = true) } ?: WAITING
        }
    }
}

/**
 * Reusable Value Stream Mapping & Operational Waste (Muda) Logging Component
 */
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ValueStreamWasteComponent(
    viewModel: HospitalViewModel,
    modifier: Modifier = Modifier,
    initialTab: Int = 0
) {
    val wasteLogs by viewModel.wasteLogs.collectAsState()
    val vsmStages = viewModel.vsmStages

    var selectedViewMode by remember { mutableIntStateOf(initialTab) } // 0: VSM Flow & Timeline, 1: Muda Taxonomy & Logs, 2: Waste Analytics
    var showLogWasteDialog by remember { mutableStateOf(false) }
    var preselectedStageForLogging by remember { mutableStateOf<VsmStage?>(null) }
    var selectedCategoryFilter by remember { mutableStateOf<String?>(null) }
    var selectedSeverityFilter by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var wasteLogToDelete by remember { mutableStateOf<LeanWasteLog?>(null) }

    // Summary calculations
    val totalMinutesLost by remember(wasteLogs) {
        derivedStateOf { wasteLogs.sumOf { it.estimatedMinutesLost } }
    }
    val totalHoursLost by remember(totalMinutesLost) {
        derivedStateOf { totalMinutesLost / 60.0 }
    }
    val estimatedBedDaysSavedPotential by remember(totalHoursLost) {
        derivedStateOf { (totalHoursLost / 24.0).toInt().coerceAtLeast(1) }
    }
    val totalVaMinutes = remember(vsmStages) { vsmStages.sumOf { it.processTimeMinutes } }
    val totalNvaMinutes = remember(vsmStages) { vsmStages.sumOf { it.waitTimeMinutes } }
    val totalLeadTimeMinutes = totalVaMinutes + totalNvaMinutes
    val processCycleEfficiencyPct = if (totalLeadTimeMinutes > 0) {
        (totalVaMinutes.toDouble() / totalLeadTimeMinutes) * 100.0
    } else 0.0

    // Filtered logs
    val filteredLogs = remember(wasteLogs, selectedCategoryFilter, selectedSeverityFilter, searchQuery) {
        wasteLogs.filter { log ->
            val matchCat = selectedCategoryFilter == null || log.wasteCategory.equals(selectedCategoryFilter, ignoreCase = true)
            val matchSev = selectedSeverityFilter == null || log.severity.equals(selectedSeverityFilter, ignoreCase = true)
            val matchQuery = searchQuery.isBlank() ||
                    log.description.contains(searchQuery, ignoreCase = true) ||
                    log.department.contains(searchQuery, ignoreCase = true) ||
                    log.rootCause.contains(searchQuery, ignoreCase = true)
            matchCat && matchSev && matchQuery
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("vsm_waste_component")
    ) {
        // Sub-Navigation Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 1.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Speed,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Value Stream Mapping & Muda Tracker",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = "Patient Workflow Lead Time • 8 Wastes of Healthcare • Kaizen Countermeasures",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Button(
                        onClick = {
                            preselectedStageForLogging = null
                            showLogWasteDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("btn_log_muda_header")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Log Muda", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                TabRow(selectedTabIndex = selectedViewMode) {
                    Tab(
                        selected = selectedViewMode == 0,
                        onClick = { selectedViewMode = 0 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("VSM Journey Flow", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        },
                        modifier = Modifier.testTag("tab_vsm_flow")
                    )
                    Tab(
                        selected = selectedViewMode == 1,
                        onClick = { selectedViewMode = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.ReportProblem, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Muda Logs (${wasteLogs.size})", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        },
                        modifier = Modifier.testTag("tab_muda_logs")
                    )
                    Tab(
                        selected = selectedViewMode == 2,
                        onClick = { selectedViewMode = 2 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AccessTime, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Muda Analytics", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        },
                        modifier = Modifier.testTag("tab_muda_analytics")
                    )
                }
            }
        }

        // View Mode Content
        when (selectedViewMode) {
            0 -> {
                // Tab 0: VSM Patient Journey Pipeline
                VsmPipelineView(
                    stages = vsmStages,
                    totalVaMinutes = totalVaMinutes,
                    totalNvaMinutes = totalNvaMinutes,
                    totalLeadTimeMinutes = totalLeadTimeMinutes,
                    pcePct = processCycleEfficiencyPct,
                    onLogWasteForStage = { stage ->
                        preselectedStageForLogging = stage
                        showLogWasteDialog = true
                    }
                )
            }
            1 -> {
                // Tab 1: 8 Types of Muda Categorization & Incident Logs
                MudaIncidentLogsView(
                    wasteLogs = filteredLogs,
                    allLogs = wasteLogs,
                    selectedCategory = selectedCategoryFilter,
                    onCategorySelect = { selectedCategoryFilter = if (selectedCategoryFilter == it) null else it },
                    selectedSeverity = selectedSeverityFilter,
                    onSeveritySelect = { selectedSeverityFilter = if (selectedSeverityFilter == it) null else it },
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    onDeleteRequest = { wasteLogToDelete = it },
                    onAddNewRequest = {
                        preselectedStageForLogging = null
                        showLogWasteDialog = true
                    }
                )
            }
            2 -> {
                // Tab 2: Muda Analytics, Department Breakdown & Impact
                MudaAnalyticsView(
                    wasteLogs = wasteLogs,
                    totalMinutesLost = totalMinutesLost,
                    totalHoursLost = totalHoursLost,
                    potentialBedDays = estimatedBedDaysSavedPotential
                )
            }
        }
    }

    // Modal Dialog to Log New Operational Waste
    if (showLogWasteDialog) {
        LogOperationalWasteDialog(
            initialStage = preselectedStageForLogging,
            onDismiss = { showLogWasteDialog = false },
            onConfirm = { cat, dept, desc, min, sev, root ->
                viewModel.addWasteObservation(cat, dept, desc, min, sev, root)
                showLogWasteDialog = false
            }
        )
    }

    // Delete Confirmation Dialog
    wasteLogToDelete?.let { log ->
        AlertDialog(
            onDismissRequest = { wasteLogToDelete = null },
            title = { Text("Resolve / Delete Waste Log") },
            text = { Text("Are you sure you want to remove this logged waste observation (${log.wasteCategory} in ${log.department})? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteWasteLog(log)
                        wasteLogToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { wasteLogToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// -----------------------------------------------------------------------------
// Sub-view: VSM Pipeline Flow & Stages
// -----------------------------------------------------------------------------
@Composable
private fun VsmPipelineView(
    stages: List<VsmStage>,
    totalVaMinutes: Int,
    totalNvaMinutes: Int,
    totalLeadTimeMinutes: Int,
    pcePct: Double,
    onLogWasteForStage: (VsmStage) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            // Process Efficiency Hero Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Hospital Workflow Value Stream",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Surface(
                            color = if (pcePct >= 35.0) Color(0xFFDCFCE7) else Color(0xFFFEE2E2),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "PCE: ${"%.1f".format(pcePct)}%",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (pcePct >= 35.0) Color(0xFF16A34A) else Color(0xFFDC2626),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Lead Time Comparison
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            color = Color(0xFFDCFCE7).copy(alpha = 0.5f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Value-Added (VA)", fontSize = 11.sp, color = Color(0xFF15803D), fontWeight = FontWeight.SemiBold)
                                Text("${totalVaMinutes}m (${"%.1f".format(totalVaMinutes / 60.0)}h)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                                Text("Direct clinical care time", fontSize = 10.sp, color = Color(0xFF166534))
                            }
                        }

                        Surface(
                            color = Color(0xFFFEE2E2).copy(alpha = 0.6f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Non-Value-Added (NVA)", fontSize = 11.sp, color = Color(0xFFB91C1C), fontWeight = FontWeight.SemiBold)
                                Text("${totalNvaMinutes}m (${"%.1f".format(totalNvaMinutes / 60.0)}h)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
                                Text("Waiting & delay waste (Muda)", fontSize = 10.sp, color = Color(0xFF991B1B))
                            }
                        }

                        Surface(
                            color = Color(0xFFF1F5F9),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Total Lead Time", fontSize = 11.sp, color = Color(0xFF334155), fontWeight = FontWeight.SemiBold)
                                Text("${totalLeadTimeMinutes}m (${"%.1f".format(totalLeadTimeMinutes / 60.0)}h)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                                Text("Admission to Exit", fontSize = 10.sp, color = Color(0xFF64748B))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Progress bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Process Efficiency Bar (VA Green vs NVA Red)", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        Text("${"%.1f".format(pcePct)}% Efficient", fontSize = 11.sp, color = Color(0xFF16A34A), fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { (pcePct / 100.0).toFloat().coerceIn(0f, 1f) },
                        color = Color(0xFF16A34A),
                        trackColor = Color(0xFFF87171),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )
                }
            }
        }

        item {
            Text(
                text = "Sequential Hospital Workflow Stages (Tap Stage to Log Waste)",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // Each Stage Card
        items(stages) { stage ->
            val stageTotal = stage.processTimeMinutes + stage.waitTimeMinutes
            val stagePce = if (stageTotal > 0) (stage.processTimeMinutes.toDouble() / stageTotal) * 100 else 0.0

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(10.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("vsm_stage_${stage.stageName.replace(" ", "_")}")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(stage.stageName, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text("Department: ${stage.department}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        OutlinedButton(
                            onClick = { onLogWasteForStage(stage) },
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Log Waste", fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // VA vs NVA Metrics
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).background(Color(0xFF16A34A), CircleShape))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("VA: ${stage.processTimeMinutes}m", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF16A34A))
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).background(Color(0xFFDC2626), CircleShape))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("NVA Wait: ${stage.waitTimeMinutes}m", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFDC2626))
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Stage Efficiency: ${"%.0f".format(stagePce)}%", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Bottleneck & Muda description
                    Surface(
                        color = Color(0xFFFEE2E2),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.Top) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text("Key Bottleneck (${stage.wasteType}):", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF991B1B))
                                Text(stage.keyBottleneck, fontSize = 11.sp, color = Color(0xFF7F1D1D))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Lean Countermeasure
                    Surface(
                        color = Color(0xFFDCFCE7),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.Top) {
                            Icon(Icons.Default.Lightbulb, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text("Lean Countermeasure (Kaizen):", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF166534))
                                Text(stage.leanCountermeasure, fontSize = 11.sp, color = Color(0xFF14532D))
                            }
                        }
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// Sub-view: 8 Types of Muda Taxonomy & Logged Incidents
// -----------------------------------------------------------------------------
@Composable
private fun MudaIncidentLogsView(
    wasteLogs: List<LeanWasteLog>,
    allLogs: List<LeanWasteLog>,
    selectedCategory: String?,
    onCategorySelect: (String) -> Unit,
    selectedSeverity: String?,
    onSeveritySelect: (String) -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onDeleteRequest: (LeanWasteLog) -> Unit,
    onAddNewRequest: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Taxonomy Header Banner
        item {
            Surface(
                color = Color(0xFFFEF3C7),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ReportProblem, contentDescription = null, tint = Color(0xFFB45309), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Taiichi Ohno's 8 Wastes (Muda) in Healthcare",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF92400E)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Operational waste in hospitals consumes up to 35% of operational expenditure and prolongs patient length of stay. Select a category below to filter observations.",
                        fontSize = 11.sp,
                        color = Color(0xFF78350F)
                    )
                }
            }
        }

        // Horizontal Category Chips with Incident Counts
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(MudaWasteCategory.entries) { muda ->
                    val isSelected = selectedCategory.equals(muda.key, ignoreCase = true)
                    val count = allLogs.count { it.wasteCategory.equals(muda.key, ignoreCase = true) }

                    Surface(
                        color = if (isSelected) muda.primaryColor else muda.bgLightColor,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .clickable { onCategorySelect(muda.key) }
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) muda.primaryColor else muda.primaryColor.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(8.dp)
                            )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = muda.icon,
                                contentDescription = null,
                                tint = if (isSelected) Color.White else muda.primaryColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = muda.key,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else muda.primaryColor
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = if (isSelected) Color.White.copy(alpha = 0.25f) else muda.primaryColor.copy(alpha = 0.15f),
                                shape = CircleShape,
                                modifier = Modifier.size(18.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "$count",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else muda.primaryColor
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Search & Severity Filter Bar
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = { Text("Search logs by keyword, department, root cause...", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_search_muda"),
                    shape = RoundedCornerShape(8.dp)
                )

                // Severity Filter Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Severity:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    listOf("Critical", "High", "Medium", "Low").forEach { sev ->
                        val isSel = selectedSeverity.equals(sev, ignoreCase = true)
                        FilterChip(
                            selected = isSel,
                            onClick = { onSeveritySelect(sev) },
                            label = { Text(sev, fontSize = 10.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = when (sev) {
                                    "Critical" -> Color(0xFFDC2626)
                                    "High" -> Color(0xFFD97706)
                                    else -> MaterialTheme.colorScheme.primary
                                },
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.height(28.dp)
                        )
                    }
                }
            }
        }

        // Logs Header & Count
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Logged Observations (${wasteLogs.size})",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                if (selectedCategory != null || selectedSeverity != null || searchQuery.isNotBlank()) {
                    TextButton(onClick = {
                        onCategorySelect("")
                        onSeveritySelect("")
                        onSearchQueryChange("")
                    }) {
                        Text("Reset Filters", fontSize = 11.sp)
                    }
                }
            }
        }

        if (wasteLogs.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No waste observations found", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("No Muda recorded matching current filter parameters.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = onAddNewRequest) {
                            Text("Log New Waste Observation")
                        }
                    }
                }
            }
        } else {
            items(wasteLogs, key = { it.id }) { log ->
                val mudaMeta = MudaWasteCategory.fromKey(log.wasteCategory)
                val formattedDate = remember(log.timestamp) {
                    SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(log.timestamp))
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(10.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("waste_log_card_${log.id}")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = mudaMeta.bgLightColor,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Icon(mudaMeta.icon, contentDescription = null, tint = mudaMeta.primaryColor, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = log.wasteCategory,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = mudaMeta.primaryColor
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Surface(
                                    color = when (log.severity) {
                                        "Critical" -> Color(0xFFFEE2E2)
                                        "High" -> Color(0xFFFEF3C7)
                                        else -> Color(0xFFF1F5F9)
                                    },
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = log.severity,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when (log.severity) {
                                            "Critical" -> Color(0xFFDC2626)
                                            "High" -> Color(0xFFD97706)
                                            else -> Color(0xFF475569)
                                        },
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = Color(0xFFFEE2E2),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "${log.estimatedMinutesLost} min lost",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFDC2626),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }

                                IconButton(
                                    onClick = { onDeleteRequest(log) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray, modifier = Modifier.size(16.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = log.description,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        if (log.rootCause.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(
                                color = Color(0xFFF8FAFC),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text("Underlying Root Cause:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(log.rootCause, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Dept: ${log.department} • Reporter: ${log.reportedBy}",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = formattedDate,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// Sub-view: Muda Analytics & Department Impact
// -----------------------------------------------------------------------------
@Composable
private fun MudaAnalyticsView(
    wasteLogs: List<LeanWasteLog>,
    totalMinutesLost: Int,
    totalHoursLost: Double,
    potentialBedDays: Int
) {
    val categoryBreakdown = remember(wasteLogs) {
        wasteLogs.groupBy { it.wasteCategory }
            .mapValues { entry ->
                val minutes = entry.value.sumOf { it.estimatedMinutesLost }
                entry.value.size to minutes
            }
            .toList()
            .sortedByDescending { it.second.second }
    }

    val departmentBreakdown = remember(wasteLogs) {
        wasteLogs.groupBy { it.department }
            .mapValues { entry ->
                val minutes = entry.value.sumOf { it.estimatedMinutesLost }
                entry.value.size to minutes
            }
            .toList()
            .sortedByDescending { it.second.second }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // High-level KPI Banner
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(10.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Total Muda Time Lost", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${"%.1f".format(totalHoursLost)} hrs", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
                        Text("${totalMinutesLost} total minutes", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(10.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Bed Capacity Impact", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("+$potentialBedDays Bed Days", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                        Text("Gained by Muda cuts", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(10.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Logged Incidents", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${wasteLogs.size}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text("Gemba observations", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        // Pareto Category Distribution
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Waste Breakdown by Muda Category (DOWNTIME)", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("Ranked by cumulative minutes lost to operational friction", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Spacer(modifier = Modifier.height(12.dp))

                    categoryBreakdown.forEach { (cat, data) ->
                        val (count, minutes) = data
                        val pct = if (totalMinutesLost > 0) (minutes.toDouble() / totalMinutesLost) * 100.0 else 0.0
                        val mudaMeta = MudaWasteCategory.fromKey(cat)

                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(mudaMeta.icon, contentDescription = null, tint = mudaMeta.primaryColor, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(cat, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                    Text(" ($count observations)", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Text("${minutes}m (${"%.1f".format(pct)}%)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = mudaMeta.primaryColor)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { (pct / 100.0).toFloat().coerceIn(0f, 1f) },
                                color = mudaMeta.primaryColor,
                                trackColor = Color(0xFFE2E8F0),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                            )
                        }
                    }
                }
            }
        }

        // Department Bottlenecks
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Department Friction Index", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("Where delay and waste hotspots occur most frequently", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Spacer(modifier = Modifier.height(10.dp))

                    departmentBreakdown.forEach { (dept, data) ->
                        val (count, minutes) = data
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(dept, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text("$count logged incidents", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }

                            Surface(
                                color = if (minutes >= 120) Color(0xFFFEE2E2) else Color(0xFFFEF3C7),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "$minutes min lost",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (minutes >= 120) Color(0xFFDC2626) else Color(0xFFD97706),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// Interactive Dialog to Log & Categorize Operational Waste (Muda)
// -----------------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogOperationalWasteDialog(
    initialStage: VsmStage? = null,
    onDismiss: () -> Unit,
    onConfirm: (category: String, department: String, description: String, minutesLost: Int, severity: String, rootCause: String) -> Unit
) {
    var selectedCategory by remember {
        mutableStateOf(initialStage?.wasteType ?: MudaWasteCategory.WAITING.key)
    }
    var department by remember {
        mutableStateOf(initialStage?.department ?: "Billing / Discharge Desk")
    }
    var description by remember {
        mutableStateOf(initialStage?.keyBottleneck ?: "")
    }
    var minutesLostStr by remember {
        mutableStateOf(initialStage?.waitTimeMinutes?.toString() ?: "60")
    }
    var severity by remember { mutableStateOf("High") }
    var rootCause by remember { mutableStateOf("") }
    var proposedCountermeasure by remember {
        mutableStateOf(initialStage?.leanCountermeasure ?: "")
    }

    val departmentsList = listOf(
        "Billing / Discharge Desk",
        "IPD Wards (Floors 2-5)",
        "OPD Chambers & Waiting",
        "Emergency & Triage",
        "Central Pharmacy",
        "Radiology & Imaging",
        "Pathology / Central Lab",
        "Operation Theater (OT)",
        "Housekeeping & Bed Turnover",
        "TPA & Insurance Desk"
    )

    val currentMuda = MudaWasteCategory.fromKey(selectedCategory)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = currentMuda.bgLightColor,
                    shape = CircleShape,
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(currentMuda.icon, contentDescription = null, tint = currentMuda.primaryColor, modifier = Modifier.size(18.dp))
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("Log Operational Waste (Muda)", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("Gemba Walk & Value Stream Audit", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Category Picker
                item {
                    Text("1. Select Muda Type (8 Wastes)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    var catExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = catExpanded,
                        onExpandedChange = { catExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = "${currentMuda.title}",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Waste Category") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = catExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                                .testTag("select_muda_category")
                        )
                        ExposedDropdownMenu(
                            expanded = catExpanded,
                            onDismissRequest = { catExpanded = false }
                        ) {
                            MudaWasteCategory.entries.forEach { muda ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(muda.title, fontWeight = FontWeight.Bold, color = muda.primaryColor)
                                            Text(muda.clinicalDescription, fontSize = 10.sp, color = Color.Gray, maxLines = 1)
                                        }
                                    },
                                    leadingIcon = {
                                        Icon(muda.icon, contentDescription = null, tint = muda.primaryColor, modifier = Modifier.size(16.dp))
                                    },
                                    onClick = {
                                        selectedCategory = muda.key
                                        catExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Helper context for selected waste
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        color = currentMuda.bgLightColor,
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "💡 E.g.: ${currentMuda.commonExamples}",
                            fontSize = 10.sp,
                            color = currentMuda.primaryColor,
                            modifier = Modifier.padding(6.dp)
                        )
                    }
                }

                // Department
                item {
                    Text("2. Hospital Department", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    var deptExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = deptExpanded,
                        onExpandedChange = { deptExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = department,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Department") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = deptExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = deptExpanded,
                            onDismissRequest = { deptExpanded = false }
                        ) {
                            departmentsList.forEach { d ->
                                DropdownMenuItem(
                                    text = { Text(d) },
                                    onClick = {
                                        department = d
                                        deptExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Observation Description
                item {
                    Text("3. Waste Observation Description", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        placeholder = { Text("Specific bottleneck or delay observed at Gemba...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_waste_description")
                    )
                }

                // Minutes Lost with Quick Chips
                item {
                    Text("4. Estimated Time Lost (Minutes)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = minutesLostStr,
                        onValueChange = { minutesLostStr = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_minutes_lost")
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("15", "30", "60", "90", "120", "180").forEach { preset ->
                            Surface(
                                color = if (minutesLostStr == preset) MaterialTheme.colorScheme.primaryContainer else Color(0xFFF1F5F9),
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier.clickable { minutesLostStr = preset }
                            ) {
                                Text(
                                    text = "${preset}m",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (minutesLostStr == preset) MaterialTheme.colorScheme.primary else Color.Black,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                // Severity
                item {
                    Text("5. Severity & Risk", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Low", "Medium", "High", "Critical").forEach { s ->
                            val isSel = severity == s
                            Surface(
                                color = if (isSel) when (s) {
                                    "Critical" -> Color(0xFFDC2626)
                                    "High" -> Color(0xFFD97706)
                                    else -> MaterialTheme.colorScheme.primary
                                } else Color(0xFFF1F5F9),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { severity = s }
                            ) {
                                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 8.dp)) {
                                    Text(
                                        text = s,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSel) Color.White else Color.Black
                                    )
                                }
                            }
                        }
                    }
                }

                // Root Cause (5-Whys)
                item {
                    Text("6. Root Cause (5-Whys Analysis)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = rootCause,
                        onValueChange = { rootCause = it },
                        placeholder = { Text("What process failure created this Muda?") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_waste_root_cause")
                    )
                }

                // Proposed Countermeasure
                item {
                    Text("7. Proposed Lean Countermeasure", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = proposedCountermeasure,
                        onValueChange = { proposedCountermeasure = it },
                        placeholder = { Text("E.g. Visual Poka-Yoke, Kanban bins, Parallel billing...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_waste_countermeasure")
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val minutes = minutesLostStr.toIntOrNull() ?: 45
                    val finalDesc = description.ifBlank { "Operational delay observed in $department" }
                    val finalRoot = if (rootCause.isNotBlank()) {
                        rootCause + if (proposedCountermeasure.isNotBlank()) " • Countermeasure: $proposedCountermeasure" else ""
                    } else proposedCountermeasure

                    onConfirm(
                        selectedCategory,
                        department,
                        finalDesc,
                        minutes,
                        severity,
                        finalRoot
                    )
                },
                modifier = Modifier.testTag("btn_save_muda_observation")
            ) {
                Text("Log Observation")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
