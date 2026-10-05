package com.example.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FiveSAudit
import com.example.data.model.FiveSCapaItem
import com.example.data.model.FiveSRedTagItem
import com.example.data.model.FiveSRubricDefaults
import com.example.ui.components.KpiCard
import com.example.ui.viewmodel.HospitalViewModel
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiveSAuditScreen(
    viewModel: HospitalViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val audits by viewModel.fiveSAudits.collectAsState()
    val redTags by viewModel.redTags.collectAsState()
    val capas by viewModel.capas.collectAsState()

    var showAddRedTagDialog by remember { mutableStateOf(false) }
    var showAddCapaDialog by remember { mutableStateOf(false) }

    val avgAuditScore = if (audits.isNotEmpty()) {
        audits.map { it.percentage }.average()
    } else 0.0

    val activeRedTagCount = redTags.count { it.status != "Resolved / Removed" }
    val openCapaCount = capas.count { it.status != "Closed" }

    Scaffold(
        modifier = modifier.testTag("five_s_audit_screen"),
        floatingActionButton = {
            when (selectedTab) {
                2 -> {
                    FloatingActionButton(
                        onClick = { showAddRedTagDialog = true },
                        containerColor = Color(0xFFDC2626),
                        contentColor = Color.White,
                        modifier = Modifier.testTag("fab_add_red_tag")
                    ) {
                        Icon(imageVector = Icons.Default.Label, contentDescription = "Add Red Tag")
                    }
                }
                3 -> {
                    FloatingActionButton(
                        onClick = { showAddCapaDialog = true },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White,
                        modifier = Modifier.testTag("fab_add_capa")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add CAPA Action")
                    }
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
                Column(modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "5S Operational Quality Audits",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Synergy Global Hospital • Workplace Standardization System",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Surface(
                            color = when {
                                avgAuditScore >= 80.0 -> Color(0xFFDCFCE7)
                                avgAuditScore >= 70.0 -> Color(0xFFFEF3C7)
                                else -> Color(0xFFFEE2E2)
                            },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Avg: ${String.format("%.0f", avgAuditScore)}%",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = when {
                                    avgAuditScore >= 80.0 -> Color(0xFF166534)
                                    avgAuditScore >= 70.0 -> Color(0xFFB45309)
                                    else -> Color(0xFF991B1B)
                                },
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Summary KPI Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        KpiCard(
                            value = "${audits.size}",
                            label = "Audits",
                            subtext = "Depts",
                            icon = Icons.Default.FactCheck,
                            modifier = Modifier.weight(1f),
                            testTag = "kpi_audits_count"
                        )
                        KpiCard(
                            value = "$activeRedTagCount",
                            label = "Red Tags",
                            subtext = "Active",
                            isAlert = activeRedTagCount > 0,
                            icon = Icons.Default.Label,
                            modifier = Modifier.weight(1f),
                            testTag = "kpi_red_tags_count"
                        )
                        KpiCard(
                            value = "$openCapaCount",
                            label = "CAPAs",
                            subtext = "Pending",
                            isAlert = openCapaCount > 2,
                            icon = Icons.Default.PendingActions,
                            modifier = Modifier.weight(1f),
                            testTag = "kpi_capas_count"
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    ScrollableTabRow(
                        selectedTabIndex = selectedTab,
                        edgePadding = 0.dp,
                        containerColor = Color.Transparent
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = { Text("Audit Tracker", fontWeight = FontWeight.SemiBold, fontSize = 12.sp) },
                            modifier = Modifier.testTag("tab_audit_tracker")
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { Text("Perform Audit", fontWeight = FontWeight.SemiBold, fontSize = 12.sp) },
                            modifier = Modifier.testTag("tab_perform_audit")
                        )
                        Tab(
                            selected = selectedTab == 2,
                            onClick = { selectedTab = 2 },
                            text = { Text("Red Tags ($activeRedTagCount)", fontWeight = FontWeight.SemiBold, fontSize = 12.sp) },
                            modifier = Modifier.testTag("tab_red_tags")
                        )
                        Tab(
                            selected = selectedTab == 3,
                            onClick = { selectedTab = 3 },
                            text = { Text("CAPA Actions ($openCapaCount)", fontWeight = FontWeight.SemiBold, fontSize = 12.sp) },
                            modifier = Modifier.testTag("tab_capas")
                        )
                    }
                }
            }

            // Tab Content
            when (selectedTab) {
                0 -> {
                    // TAB 0: Audit Tracker & Department Leaderboard
                    AuditTrackerTab(
                        audits = audits,
                        onPerformAuditClick = { selectedTab = 1 }
                    )
                }
                1 -> {
                    // TAB 1: Perform 5S Audit Runner
                    PerformAuditRunnerTab(
                        viewModel = viewModel,
                        onAuditSubmitted = { selectedTab = 0 }
                    )
                }
                2 -> {
                    // TAB 2: Red Tag Registry (Seiri)
                    RedTagRegistryTab(
                        redTags = redTags,
                        onUpdateStatus = { item, newStatus -> viewModel.updateRedTagStatus(item, newStatus) },
                        onAddRedTagClick = { showAddRedTagDialog = true }
                    )
                }
                3 -> {
                    // TAB 3: CAPA Actions Tracker
                    CapaTrackerTab(
                        capas = capas,
                        onUpdateStatus = { item, newStatus -> viewModel.updateCapaStatus(item, newStatus) },
                        onAddCapaClick = { showAddCapaDialog = true }
                    )
                }
            }
        }

        // Add Red Tag Dialog
        if (showAddRedTagDialog) {
            AddRedTagDialog(
                onDismiss = { showAddRedTagDialog = false },
                onConfirm = { dept, name, cat, reason, action, taggedBy, notes ->
                    viewModel.addRedTag(dept, name, cat, reason, action, taggedBy, notes)
                    showAddRedTagDialog = false
                }
            )
        }

        // Add CAPA Dialog
        if (showAddCapaDialog) {
            AddCapaDialog(
                onDismiss = { showAddCapaDialog = false },
                onConfirm = { dept, pillar, finding, action, owner, targetDate, severity ->
                    viewModel.addCapa(dept, pillar, finding, action, owner, targetDate, severity)
                    showAddCapaDialog = false
                }
            )
        }
    }
}

// -------------------------------------------------------------------------------------
// TAB 0: Audit Tracker & Department Leaderboard
// -------------------------------------------------------------------------------------
@Composable
fun AuditTrackerTab(
    audits: List<FiveSAudit>,
    onPerformAuditClick: () -> Unit
) {
    var filterGrade by remember { mutableStateOf("All") }

    val filteredAudits = audits.filter {
        when (filterGrade) {
            "Grade A" -> it.percentage >= 80.0
            "Grade B" -> it.percentage in 70.0..79.9
            "Grade C" -> it.percentage < 70.0
            else -> true
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            // Radar / Pillar comparison breakdown card
            HospitalPillarComparisonCard(audits = audits)
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Department Audit History",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf("All", "Grade A", "Grade B", "Grade C").forEach { grade ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (filterGrade == grade) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                            modifier = Modifier.clickable { filterGrade = grade }
                        ) {
                            Text(
                                text = grade,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (filterGrade == grade) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        if (filteredAudits.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("No audits matching filter", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = onPerformAuditClick) {
                            Text("Perform First 5S Audit")
                        }
                    }
                }
            }
        } else {
            items(filteredAudits, key = { it.id }) { audit ->
                DetailedFiveSAuditCard(audit = audit)
            }
        }
    }
}

@Composable
fun HospitalPillarComparisonCard(audits: List<FiveSAudit>) {
    val sortAvg = if (audits.isNotEmpty()) audits.map { it.sortScore }.average() * 20 else 0.0
    val setAvg = if (audits.isNotEmpty()) audits.map { it.setInOrderScore }.average() * 20 else 0.0
    val shineAvg = if (audits.isNotEmpty()) audits.map { it.shineScore }.average() * 20 else 0.0
    val stdAvg = if (audits.isNotEmpty()) audits.map { it.standardizeScore }.average() * 20 else 0.0
    val susAvg = if (audits.isNotEmpty()) audits.map { it.sustainScore }.average() * 20 else 0.0

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Hospital 5-Pillar Performance Averages",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Overall institutional scores across the 5 pillars (Normalized to 100%)",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            PillarProgressRow(name = "1S: Sort (Seiri)", pct = sortAvg)
            PillarProgressRow(name = "2S: Set in Order (Seiton)", pct = setAvg)
            PillarProgressRow(name = "3S: Shine (Seiso)", pct = shineAvg)
            PillarProgressRow(name = "4S: Standardize (Seiketsu)", pct = stdAvg)
            PillarProgressRow(name = "5S: Sustain (Shitsuke)", pct = susAvg)
        }
    }
}

@Composable
fun PillarProgressRow(name: String, pct: Double) {
    val color = when {
        pct >= 80.0 -> Color(0xFF16A34A)
        pct >= 65.0 -> Color(0xFFD97706)
        else -> Color(0xFFDC2626)
    }

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(name, fontSize = 11.sp, fontWeight = FontWeight.Medium)
            Text("${String.format("%.0f", pct)}%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
        }
        Spacer(modifier = Modifier.height(3.dp))
        LinearProgressIndicator(
            progress = { (pct / 100.0).toFloat().coerceIn(0f, 1f) },
            modifier = Modifier.fillMaxWidth().height(6.dp),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
fun DetailedFiveSAuditCard(audit: FiveSAudit) {
    var expanded by remember { mutableStateOf(false) }

    val grade = when {
        audit.percentage >= 80.0 -> "Grade A (Benchmark)"
        audit.percentage >= 70.0 -> "Grade B (Satisfactory)"
        else -> "Grade C (Action Required)"
    }

    val gradeColor = when {
        audit.percentage >= 80.0 -> Color(0xFF16A34A)
        audit.percentage >= 70.0 -> Color(0xFFD97706)
        else -> Color(0xFFDC2626)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("audit_card_${audit.id}"),
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = audit.department,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Auditor: ${audit.auditorName} • ${audit.auditDate}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    color = when {
                        audit.percentage >= 80.0 -> Color(0xFFDCFCE7)
                        audit.percentage >= 70.0 -> Color(0xFFFEF3C7)
                        else -> Color(0xFFFEE2E2)
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "${audit.totalScore}/25 (${String.format("%.0f", audit.percentage)}%)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = gradeColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 5S Score Indicator Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ScoreBadge(label = "1S Sort", score = audit.sortScore)
                ScoreBadge(label = "2S Set", score = audit.setInOrderScore)
                ScoreBadge(label = "3S Shine", score = audit.shineScore)
                ScoreBadge(label = "4S Std", score = audit.standardizeScore)
                ScoreBadge(label = "5S Sust", score = audit.sustainScore)
            }

            if (audit.remarks.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Findings: ${audit.remarks}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = gradeColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = grade,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = gradeColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                TextButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.testTag("btn_toggle_audit_details_${audit.id}")
                ) {
                    Text(if (expanded) "Hide Details" else "View Details", fontSize = 11.sp)
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Pillar Diagnostic Notes:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        PillarDetailNote(pillar = "1S Sort", score = audit.sortScore, defaultDesc = "Red-tagging of unneeded files & broken supplies")
                        PillarDetailNote(pillar = "2S Set in Order", score = audit.setInOrderScore, defaultDesc = "Color coding, visual crash cart demarcations")
                        PillarDetailNote(pillar = "3S Shine", score = audit.shineScore, defaultDesc = "Terminal wipe-downs, bio-hazard bin cleanliness")
                        PillarDetailNote(pillar = "4S Standardize", score = audit.standardizeScore, defaultDesc = "SOP compliance, structured shift handovers")
                        PillarDetailNote(pillar = "5S Sustain", score = audit.sustainScore, defaultDesc = "Audit adherence, self-discipline & CAPA closure")
                    }
                }
            }
        }
    }
}

@Composable
fun ScoreBadge(label: String, score: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
fun PillarDetailNote(pillar: String, score: Int, defaultDesc: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier.size(6.dp).background(
                if (score >= 4) Color(0xFF16A34A) else if (score == 3) Color(0xFFD97706) else Color(0xFFDC2626),
                CircleShape
            )
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "$pillar ($score/5): $defaultDesc",
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// -------------------------------------------------------------------------------------
// TAB 1: Perform 5S Audit Runner
// -------------------------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerformAuditRunnerTab(
    viewModel: HospitalViewModel,
    onAuditSubmitted: () -> Unit
) {
    var selectedDepartment by remember { mutableStateOf("Emergency Department") }
    var auditorName by remember { mutableStateOf("Gunjan Prakash (Lean Intern)") }
    var shift by remember { mutableStateOf("Morning Shift (8:00 AM – 2:00 PM)") }

    var sortScore by remember { mutableIntStateOf(4) }
    var setScore by remember { mutableIntStateOf(4) }
    var shineScore by remember { mutableIntStateOf(4) }
    var stdScore by remember { mutableIntStateOf(3) }
    var susScore by remember { mutableIntStateOf(3) }

    var remarks by remember { mutableStateOf("") }

    val departments = listOf(
        "Emergency Department",
        "Central Pharmacy",
        "Operation Theatre (OT Complex)",
        "Inpatient Ward 1 (General Medicine)",
        "Inpatient Ward 2 (Surgery)",
        "ICU & High Dependency Unit (HDU)",
        "Billing & Discharge Counter",
        "Pathology & Diagnostic Labs"
    )

    val shifts = listOf(
        "Morning Shift (8:00 AM – 2:00 PM)",
        "Evening Shift (2:00 PM – 8:00 PM)",
        "Night Shift (8:00 PM – 8:00 AM)"
    )

    val currentTotal = sortScore + setScore + shineScore + stdScore + susScore
    val currentPct = (currentTotal.toDouble() / 25.0) * 100.0

    val grade = when {
        currentPct >= 80.0 -> "Grade A (Benchmark Quality)"
        currentPct >= 70.0 -> "Grade B (Satisfactory)"
        else -> "Grade C (Urgent Action Required)"
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            // Live Audit Scorecard Banner
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
                        Column {
                            Text("Live 5S Audit Score", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                "$currentTotal / 25 (${String.format("%.0f", currentPct)}%)",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Surface(
                            color = when {
                                currentPct >= 80.0 -> Color(0xFFDCFCE7)
                                currentPct >= 70.0 -> Color(0xFFFEF3C7)
                                else -> Color(0xFFFEE2E2)
                            },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = grade,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = when {
                                    currentPct >= 80.0 -> Color(0xFF166534)
                                    currentPct >= 70.0 -> Color(0xFFB45309)
                                    else -> Color(0xFF991B1B)
                                },
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { (currentPct / 100.0).toFloat() },
                        modifier = Modifier.fillMaxWidth().height(8.dp),
                        color = if (currentPct >= 80.0) Color(0xFF16A34A) else Color(0xFFD97706),
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }
        }

        item {
            // Department & Auditor metadata
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Audit Information", fontSize = 14.sp, fontWeight = FontWeight.Bold)

                    // Department selector
                    var deptExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = deptExpanded,
                        onExpandedChange = { deptExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedDepartment,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Department Under Audit") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = deptExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth().testTag("select_audit_department")
                        )
                        ExposedDropdownMenu(
                            expanded = deptExpanded,
                            onDismissRequest = { deptExpanded = false }
                        ) {
                            departments.forEach { d ->
                                DropdownMenuItem(
                                    text = { Text(d) },
                                    onClick = {
                                        selectedDepartment = d
                                        deptExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = auditorName,
                        onValueChange = { auditorName = it },
                        label = { Text("Lead Auditor Name") },
                        modifier = Modifier.fillMaxWidth().testTag("input_auditor_name")
                    )

                    var shiftExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = shiftExpanded,
                        onExpandedChange = { shiftExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = shift,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Inspection Shift") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = shiftExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = shiftExpanded,
                            onDismissRequest = { shiftExpanded = false }
                        ) {
                            shifts.forEach { s ->
                                DropdownMenuItem(
                                    text = { Text(s) },
                                    onClick = {
                                        shift = s
                                        shiftExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // 5 Pillar Rating Sections
        item {
            PillarAuditSection(
                rubric = FiveSRubricDefaults.rubrics[0],
                currentScore = sortScore,
                onScoreChange = { sortScore = it },
                testTagPrefix = "pillar_sort"
            )
        }

        item {
            PillarAuditSection(
                rubric = FiveSRubricDefaults.rubrics[1],
                currentScore = setScore,
                onScoreChange = { setScore = it },
                testTagPrefix = "pillar_set"
            )
        }

        item {
            PillarAuditSection(
                rubric = FiveSRubricDefaults.rubrics[2],
                currentScore = shineScore,
                onScoreChange = { shineScore = it },
                testTagPrefix = "pillar_shine"
            )
        }

        item {
            PillarAuditSection(
                rubric = FiveSRubricDefaults.rubrics[3],
                currentScore = stdScore,
                onScoreChange = { stdScore = it },
                testTagPrefix = "pillar_std"
            )
        }

        item {
            PillarAuditSection(
                rubric = FiveSRubricDefaults.rubrics[4],
                currentScore = susScore,
                onScoreChange = { susScore = it },
                testTagPrefix = "pillar_sus"
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Overall Observations & Corrective Actions", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = remarks,
                        onValueChange = { remarks = it },
                        placeholder = { Text("Note any immediate safety hazards, red-tag items, or areas of outstanding cleanliness...") },
                        modifier = Modifier.fillMaxWidth().testTag("input_audit_remarks"),
                        minLines = 3
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            viewModel.addFiveSAudit(
                                department = selectedDepartment,
                                sort = sortScore,
                                set = setScore,
                                shine = shineScore,
                                standardize = stdScore,
                                sustain = susScore,
                                auditorName = auditorName.ifBlank { "Gunjan Prakash" },
                                remarks = remarks.ifBlank { "Formal 5S operational audit completed." }
                            )
                            onAuditSubmitted()
                        },
                        modifier = Modifier.fillMaxWidth().testTag("btn_submit_5s_audit"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Save & Log 5S Operational Audit", fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun PillarAuditSection(
    rubric: com.example.data.model.FiveSPillarRubric,
    currentScore: Int,
    onScoreChange: (Int) -> Unit,
    testTagPrefix: String
) {
    Card(
        modifier = Modifier.fillMaxWidth().testTag(testTagPrefix),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = rubric.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = rubric.japaneseName,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    color = when {
                        currentScore >= 4 -> Color(0xFFDCFCE7)
                        currentScore == 3 -> Color(0xFFFEF3C7)
                        else -> Color(0xFFFEE2E2)
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "$currentScore / 5 pts",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            currentScore >= 4 -> Color(0xFF166534)
                            currentScore == 3 -> Color(0xFFB45309)
                            else -> Color(0xFF991B1B)
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = rubric.description,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Check questions list
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Healthcare Checkpoints:", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    rubric.questions.forEach { q ->
                        Row(verticalAlignment = Alignment.Top) {
                            Text("• ", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text(q, fontSize = 11.sp, lineHeight = 15.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Score Buttons (1 to 5)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Select Score:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    (1..5).forEach { score ->
                        val isSelected = currentScore == score
                        Surface(
                            shape = CircleShape,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .size(34.dp)
                                .clickable { onScoreChange(score) }
                                .testTag("${testTagPrefix}_score_$score")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "$score",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------
// TAB 2: Red Tag Registry (Seiri)
// -------------------------------------------------------------------------------------
@Composable
fun RedTagRegistryTab(
    redTags: List<FiveSRedTagItem>,
    onUpdateStatus: (FiveSRedTagItem, String) -> Unit,
    onAddRedTagClick: () -> Unit
) {
    var filterStatus by remember { mutableStateOf("All") }

    val filtered = redTags.filter {
        when (filterStatus) {
            "Active" -> it.status == "Active Tag"
            "Quarantine" -> it.status == "Under Quarantine"
            "Resolved" -> it.status == "Resolved / Removed"
            else -> true
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Surface(
                color = Color(0xFFFEE2E2),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Label,
                        contentDescription = null,
                        tint = Color(0xFFDC2626),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "5S Red Tag Holding Strategy (Seiri)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF991B1B)
                        )
                        Text(
                            text = "Identify, quarantine, and eliminate damaged, expired, or non-essential items from clinical wards.",
                            fontSize = 11.sp,
                            color = Color(0xFF7F1D1D)
                        )
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tagged Items Registry",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf("All", "Active", "Quarantine", "Resolved").forEach { status ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (filterStatus == status) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                            modifier = Modifier.clickable { filterStatus = status }
                        ) {
                            Text(
                                text = status,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (filterStatus == status) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        if (filtered.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No items under red tag quarantine", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                    }
                }
            }
        } else {
            items(filtered, key = { it.id }) { item ->
                RedTagItemCard(
                    item = item,
                    onStatusChange = { newStatus -> onUpdateStatus(item, newStatus) }
                )
            }
        }
    }
}

@Composable
fun RedTagItemCard(
    item: FiveSRedTagItem,
    onStatusChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().testTag("red_tag_card_${item.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color(0xFFDC2626),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = item.tagNumber,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (item.status) {
                        "Resolved / Removed" -> Color(0xFFDCFCE7)
                        "Under Quarantine" -> Color(0xFFFEF3C7)
                        else -> Color(0xFFFEE2E2)
                    }
                ) {
                    Text(
                        text = item.status,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (item.status) {
                            "Resolved / Removed" -> Color(0xFF166534)
                            "Under Quarantine" -> Color(0xFFB45309)
                            else -> Color(0xFF991B1B)
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = item.itemName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Dept: ${item.department} • Category: ${item.category}",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Reason: ${item.reason} • Action: ${item.actionRequired}",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFB91C1C)
            )

            if (item.resolutionNotes.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Notes: ${item.resolutionNotes}",
                        fontSize = 10.sp,
                        modifier = Modifier.padding(6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tagged by: ${item.taggedBy} (${item.dateTagged})",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row {
                    if (item.status == "Active Tag") {
                        TextButton(onClick = { onStatusChange("Under Quarantine") }) {
                            Text("Quarantine", fontSize = 11.sp)
                        }
                    }
                    if (item.status != "Resolved / Removed") {
                        Button(
                            onClick = { onStatusChange("Resolved / Removed") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                            modifier = Modifier.testTag("btn_resolve_red_tag_${item.id}")
                        ) {
                            Text("Resolve / Scrap", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------
// TAB 3: CAPA Tracker Tab
// -------------------------------------------------------------------------------------
@Composable
fun CapaTrackerTab(
    capas: List<FiveSCapaItem>,
    onUpdateStatus: (FiveSCapaItem, String) -> Unit,
    onAddCapaClick: () -> Unit
) {
    var filterStatus by remember { mutableStateOf("All") }

    val filtered = capas.filter {
        when (filterStatus) {
            "Open" -> it.status == "Open"
            "In Progress" -> it.status == "In Progress"
            "Closed" -> it.status == "Closed"
            else -> true
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Surface(
                color = Color(0xFFE0F2FE),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AssignmentTurnedIn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Corrective & Preventive Action (CAPA)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Close the loop on audit non-conformances with assigned owners and deadline accountability.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Action Plan Items (${filtered.size})",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf("All", "Open", "In Progress", "Closed").forEach { status ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (filterStatus == status) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                            modifier = Modifier.clickable { filterStatus = status }
                        ) {
                            Text(
                                text = status,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (filterStatus == status) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        items(filtered, key = { it.id }) { item ->
            CapaItemCard(
                capa = item,
                onStatusChange = { newStatus -> onUpdateStatus(item, newStatus) }
            )
        }
    }
}

@Composable
fun CapaItemCard(
    capa: FiveSCapaItem,
    onStatusChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().testTag("capa_card_${capa.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = when (capa.severity) {
                        "Critical" -> Color(0xFFDC2626)
                        "High" -> Color(0xFFD97706)
                        else -> MaterialTheme.colorScheme.primary
                    },
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "${capa.pillar} • ${capa.severity}",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (capa.status) {
                        "Closed" -> Color(0xFFDCFCE7)
                        "In Progress" -> Color(0xFFFEF3C7)
                        else -> Color(0xFFFEE2E2)
                    }
                ) {
                    Text(
                        text = capa.status,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (capa.status) {
                            "Closed" -> Color(0xFF166534)
                            "In Progress" -> Color(0xFFB45309)
                            else -> Color(0xFF991B1B)
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Finding: ${capa.finding}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Corrective Action: ${capa.correctiveAction}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Dept: ${capa.department} • Assigned: ${capa.responsiblePerson} • Target: ${capa.targetDate}",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if (capa.status == "Open") {
                    OutlinedButton(onClick = { onStatusChange("In Progress") }) {
                        Text("Start Work", fontSize = 11.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }
                if (capa.status != "Closed") {
                    Button(
                        onClick = { onStatusChange("Closed") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                        modifier = Modifier.testTag("btn_close_capa_${capa.id}")
                    ) {
                        Text("Mark Closed", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------
// Dialogs
// -------------------------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRedTagDialog(
    onDismiss: () -> Unit,
    onConfirm: (dept: String, name: String, cat: String, reason: String, action: String, taggedBy: String, notes: String) -> Unit
) {
    var dept by remember { mutableStateOf("Inpatient Ward 2 (Surgery)") }
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Medical Equipment") }
    var reason by remember { mutableStateOf("Damaged/Broken") }
    var actionRequired by remember { mutableStateOf("Biomedical Maintenance") }
    var taggedBy by remember { mutableStateOf("Gunjan Prakash (Lean Intern)") }
    var notes by remember { mutableStateOf("") }

    val departments = listOf(
        "Emergency Department", "Central Pharmacy", "Operation Theatre",
        "Inpatient Ward 1", "Inpatient Ward 2", "Billing Counter"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Issue 5S Red Tag (Seiri)") },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Item Name / Description") },
                        modifier = Modifier.fillMaxWidth().testTag("input_red_tag_name")
                    )
                }
                item {
                    OutlinedTextField(
                        value = dept,
                        onValueChange = { dept = it },
                        label = { Text("Hospital Department / Ward") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Category (Equipment, Consumables, Furniture)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = reason,
                        onValueChange = { reason = it },
                        label = { Text("Reason for Tagging (Broken, Expired, Clutter)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = actionRequired,
                        onValueChange = { actionRequired = it },
                        label = { Text("Action Required (Repair, Scrap, Return)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Location details & remarks") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        dept,
                        name.ifBlank { "Unidentified equipment" },
                        category,
                        reason,
                        actionRequired,
                        taggedBy,
                        notes
                    )
                },
                modifier = Modifier.testTag("btn_confirm_add_red_tag")
            ) {
                Text("Issue Red Tag")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AddCapaDialog(
    onDismiss: () -> Unit,
    onConfirm: (dept: String, pillar: String, finding: String, action: String, owner: String, targetDate: String, severity: String) -> Unit
) {
    var dept by remember { mutableStateOf("Central Pharmacy") }
    var pillar by remember { mutableStateOf("2S - Set in Order") }
    var finding by remember { mutableStateOf("") }
    var action by remember { mutableStateOf("") }
    var owner by remember { mutableStateOf("Department Head") }
    var targetDate by remember { mutableStateOf("2026-03-15") }
    var severity by remember { mutableStateOf("High") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New 5S Corrective Action (CAPA)") },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    OutlinedTextField(
                        value = finding,
                        onValueChange = { finding = it },
                        label = { Text("Audit Non-Conformance Finding") },
                        modifier = Modifier.fillMaxWidth().testTag("input_capa_finding")
                    )
                }
                item {
                    OutlinedTextField(
                        value = action,
                        onValueChange = { action = it },
                        label = { Text("Corrective & Preventive Action") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = dept,
                        onValueChange = { dept = it },
                        label = { Text("Department") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = pillar,
                        onValueChange = { pillar = it },
                        label = { Text("5S Pillar") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = owner,
                        onValueChange = { owner = it },
                        label = { Text("Action Owner (Responsible Person)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = targetDate,
                        onValueChange = { targetDate = it },
                        label = { Text("Target Resolution Date (YYYY-MM-DD)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        dept,
                        pillar,
                        finding.ifBlank { "5S non-conformance" },
                        action.ifBlank { "Standardize storage and labeling" },
                        owner,
                        targetDate,
                        severity
                    )
                },
                modifier = Modifier.testTag("btn_confirm_add_capa")
            ) {
                Text("Assign CAPA")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
