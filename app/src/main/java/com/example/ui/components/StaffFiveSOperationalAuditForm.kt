package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FiveSAudit
import com.example.data.model.FiveSRubricDefaults
import com.example.ui.viewmodel.HospitalViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 5S Operational Audit Checklist Form for Hospital Staff
 * Covers:
 * - 1S: Sort (Seiri)
 * - 2S: Set in Order (Seiton)
 * - 3S: Shine (Seiso)
 * - 4S: Standardize (Seiketsu)
 * - 5S: Sustain (Shitsuke)
 *
 * Backed by local state persistence (in-memory draft preservation and Room database storage).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffFiveSOperationalAuditForm(
    viewModel: HospitalViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activeSession by viewModel.activeUserSession.collectAsState()
    val allAudits by viewModel.fiveSAudits.collectAsState()

    // 0: Operational Audit Checklist Form, 1: Audit History & Local Logs
    var selectedViewTab by rememberSaveable { mutableIntStateOf(0) }

    // Department list for clinical staff audits
    val departments = listOf(
        "Inpatient Ward 3B (Internal Medicine)",
        "Inpatient Ward 2 (Surgery)",
        "Operation Theatre (OT Complex)",
        "Emergency & Triage Wing",
        "Critical Care & ICU / HDU",
        "Central Inpatient Pharmacy",
        "Orthopedics Trauma Ward",
        "Maternal & OB/GYN Ward",
        "Pediatrics Ward",
        "Billing & Discharge Counter"
    )

    // Form Local State (persisted across tab changes / recompositions)
    var selectedDepartment by rememberSaveable { mutableStateOf(activeSession?.department?.ifBlank { "Inpatient Ward 3B (Internal Medicine)" } ?: "Inpatient Ward 3B (Internal Medicine)") }
    var auditorName by rememberSaveable { mutableStateOf(activeSession?.fullName?.ifBlank { "Staff Nurse Priya Sharma (RN)" } ?: "Staff Nurse Priya Sharma (RN)") }
    var shiftName by rememberSaveable { mutableStateOf("Morning Shift A (08:00 - 16:00)") }
    var remarks by rememberSaveable { mutableStateOf("") }

    // Pillar Scores (1-5 each)
    var sortScore by rememberSaveable { mutableIntStateOf(4) }
    var setInOrderScore by rememberSaveable { mutableIntStateOf(4) }
    var shineScore by rememberSaveable { mutableIntStateOf(5) }
    var standardizeScore by rememberSaveable { mutableIntStateOf(3) }
    var sustainScore by rememberSaveable { mutableIntStateOf(4) }

    // Pillar Observation Notes
    var sortNotes by rememberSaveable { mutableStateOf("") }
    var setInOrderNotes by rememberSaveable { mutableStateOf("") }
    var shineNotes by rememberSaveable { mutableStateOf("") }
    var standardizeNotes by rememberSaveable { mutableStateOf("") }
    var sustainNotes by rememberSaveable { mutableStateOf("") }

    // Checklist item checklist states (checklist questions checked)
    val checkedQuestions = remember { mutableStateMapOf<String, Boolean>() }

    // Calculated Scores
    val totalScore = sortScore + setInOrderScore + shineScore + standardizeScore + sustainScore
    val percentage = (totalScore.toDouble() / 25.0) * 100.0

    val complianceLevel = when {
        percentage >= 90.0 -> "Level 5: World-Class Lean Ward"
        percentage >= 80.0 -> "Level 4: NABH Standard Compliant"
        percentage >= 70.0 -> "Level 3: Operational Pass"
        else -> "Level 2: Remediation / CAPA Required"
    }

    val complianceBadgeColor = when {
        percentage >= 90.0 -> Color(0xFF16A34A)
        percentage >= 80.0 -> Color(0xFF0284C7)
        percentage >= 70.0 -> Color(0xFFD97706)
        else -> Color(0xFFDC2626)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Form Header Console
        item {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(12.dp),
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
                                color = Color(0xFFDCFCE7),
                                shape = CircleShape,
                                modifier = Modifier.size(42.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.FactCheck,
                                        contentDescription = null,
                                        tint = Color(0xFF16A34A),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "5S Operational Audit Checklist",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Sort • Set in Order • Shine • Standardize • Sustain",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Persistence Status Badge
                        Surface(
                            color = Color(0xFFDBEAFE),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Room Local Storage", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1D4ED8))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Tab Switcher: 0 = Checklist Form, 1 = Past Audit Logs
                    TabRow(
                        selectedTabIndex = selectedViewTab,
                        containerColor = Color.Transparent
                    ) {
                        Tab(
                            selected = selectedViewTab == 0,
                            onClick = { selectedViewTab = 0 },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CleaningServices, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Checklist Audit Form", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            },
                            modifier = Modifier.testTag("tab_fives_form")
                        )

                        Tab(
                            selected = selectedViewTab == 1,
                            onClick = { selectedViewTab = 1 },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Audit History (${allAudits.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            },
                            modifier = Modifier.testTag("tab_fives_history")
                        )
                    }
                }
            }
        }

        if (selectedViewTab == 0) {
            // =========================================================================
            // VIEW TAB 0: 5S OPERATIONAL AUDIT CHECKLIST FORM
            // =========================================================================

            // Live Calculated Total Score Card
            item {
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
                                Text(
                                    text = "Current Audit Score",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = "$totalScore",
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Black,
                                        color = complianceBadgeColor
                                    )
                                    Text(
                                        text = " / 25 pts",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(bottom = 4.dp, start = 2.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "(${String.format(Locale.getDefault(), "%.1f", percentage)}%)",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = complianceBadgeColor,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                }
                            }

                            Surface(
                                color = complianceBadgeColor.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, complianceBadgeColor.copy(alpha = 0.4f))
                            ) {
                                Text(
                                    text = complianceLevel,
                                    color = complianceBadgeColor,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        LinearProgressIndicator(
                            progress = { (totalScore / 25f).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = complianceBadgeColor,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Pillar mini progress score pills
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            PillarMiniScore(label = "1S Sort", score = sortScore, color = Color(0xFFDC2626))
                            PillarMiniScore(label = "2S Set", score = setInOrderScore, color = Color(0xFFD97706))
                            PillarMiniScore(label = "3S Shine", score = shineScore, color = Color(0xFF2563EB))
                            PillarMiniScore(label = "4S Std", score = standardizeScore, color = Color(0xFF7C3AED))
                            PillarMiniScore(label = "5S Sust", score = sustainScore, color = Color(0xFF16A34A))
                        }
                    }
                }
            }

            // Department & Auditor Context Form Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Audit Context & Staff Details",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        // Department Dropdown
                        var deptExpanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = deptExpanded,
                            onExpandedChange = { deptExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = selectedDepartment,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Clinical Ward / Department") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = deptExpanded) },
                                leadingIcon = { Icon(Icons.Default.LocalHospital, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                                modifier = Modifier.menuAnchor().fillMaxWidth().testTag("select_audit_department")
                            )
                            ExposedDropdownMenu(
                                expanded = deptExpanded,
                                onDismissRequest = { deptExpanded = false }
                            ) {
                                departments.forEach { dept ->
                                    DropdownMenuItem(
                                        text = { Text(dept, fontSize = 12.sp) },
                                        onClick = {
                                            selectedDepartment = dept
                                            deptExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = auditorName,
                                onValueChange = { auditorName = it },
                                label = { Text("Staff Auditor Name") },
                                modifier = Modifier.weight(1f).testTag("input_auditor_name"),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = shiftName,
                                onValueChange = { shiftName = it },
                                label = { Text("Shift") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }
                    }
                }
            }

            // PILLAR 1: SORT (SEIRI)
            item {
                PillarAuditChecklistCard(
                    pillarNumber = "1S",
                    pillarTitle = "Sort (Seiri - 整理)",
                    description = "Eliminate clutter, scrap broken items, and verify no expired drugs or surplus stock.",
                    pillarColor = Color(0xFFDC2626),
                    rubricKey = "SORT",
                    currentScore = sortScore,
                    onScoreChanged = { sortScore = it },
                    notes = sortNotes,
                    onNotesChanged = { sortNotes = it },
                    checkedQuestions = checkedQuestions,
                    testTagPrefix = "pillar_sort"
                )
            }

            // PILLAR 2: SET IN ORDER (SEITON)
            item {
                PillarAuditChecklistCard(
                    pillarNumber = "2S",
                    pillarTitle = "Set in Order (Seiton - 整頓)",
                    description = "A place for everything: visual color demarcation, crash cart checklist, and LASA medicine bins.",
                    pillarColor = Color(0xFFD97706),
                    rubricKey = "SET_IN_ORDER",
                    currentScore = setInOrderScore,
                    onScoreChanged = { setInOrderScore = it },
                    notes = setInOrderNotes,
                    onNotesChanged = { setInOrderNotes = it },
                    checkedQuestions = checkedQuestions,
                    testTagPrefix = "pillar_set_in_order"
                )
            }

            // PILLAR 3: SHINE (SEISO)
            item {
                PillarAuditChecklistCard(
                    pillarNumber = "3S",
                    pillarTitle = "Shine & Sanitize (Seiso - 清掃)",
                    description = "Infection control, bed turnover hygiene, clean BMW color bins, and zero bio-fluid hazards.",
                    pillarColor = Color(0xFF2563EB),
                    rubricKey = "SHINE",
                    currentScore = shineScore,
                    onScoreChanged = { shineScore = it },
                    notes = shineNotes,
                    onNotesChanged = { shineNotes = it },
                    checkedQuestions = checkedQuestions,
                    testTagPrefix = "pillar_shine"
                )
            }

            // PILLAR 4: STANDARDIZE (SEIKETSU)
            item {
                PillarAuditChecklistCard(
                    pillarNumber = "4S",
                    pillarTitle = "Standardize (Seiketsu - 清潔)",
                    description = "Visual work instructions, SBAR shift handover checklists, and uniform color labeling.",
                    pillarColor = Color(0xFF7C3AED),
                    rubricKey = "STANDARDIZE",
                    currentScore = standardizeScore,
                    onScoreChanged = { standardizeScore = it },
                    notes = standardizeNotes,
                    onNotesChanged = { standardizeNotes = it },
                    checkedQuestions = checkedQuestions,
                    testTagPrefix = "pillar_standardize"
                )
            }

            // PILLAR 5: SUSTAIN (SHITSUKE)
            item {
                PillarAuditChecklistCard(
                    pillarNumber = "5S",
                    pillarTitle = "Sustain (Shitsuke - 躾)",
                    description = "Daily shift adherence, proactive discipline, closed CAPA corrective loops, and Kaizen culture.",
                    pillarColor = Color(0xFF16A34A),
                    rubricKey = "SUSTAIN",
                    currentScore = sustainScore,
                    onScoreChanged = { sustainScore = it },
                    notes = sustainNotes,
                    onNotesChanged = { sustainNotes = it },
                    checkedQuestions = checkedQuestions,
                    testTagPrefix = "pillar_sustain"
                )
            }

            // Action Plan & Remarks Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Auditor Summary Remarks & Countermeasures",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = remarks,
                            onValueChange = { remarks = it },
                            label = { Text("Operational Remarks / Observed Gaps / Action Items") },
                            placeholder = { Text("e.g. Broken BP monitor needs biomedical work order. Crash cart visually compliant.") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(90.dp)
                                .testTag("input_audit_remarks"),
                            maxLines = 4
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Submit Button
                        Button(
                            onClick = {
                                viewModel.addFiveSAudit(
                                    department = selectedDepartment,
                                    sort = sortScore,
                                    set = setInOrderScore,
                                    shine = shineScore,
                                    standardize = standardizeScore,
                                    sustain = sustainScore,
                                    auditorName = auditorName.ifBlank { "Staff Nurse (RN)" },
                                    remarks = remarks.ifBlank { "Routine operational 5S audit completed for $selectedDepartment." }
                                )

                                Toast.makeText(
                                    context,
                                    "5S Audit successfully persisted for $selectedDepartment ($totalScore/25 pts)!",
                                    Toast.LENGTH_LONG
                                ).show()

                                // Reset form remarks & switch to history tab
                                remarks = ""
                                sortNotes = ""
                                setInOrderNotes = ""
                                shineNotes = ""
                                standardizeNotes = ""
                                sustainNotes = ""
                                selectedViewTab = 1
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("btn_submit_5s_audit")
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Save & Persist 5S Audit ($totalScore/25 pts)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        } else {
            // =========================================================================
            // VIEW TAB 1: AUDIT HISTORY & ROOM LOCAL STATE LOGS
            // =========================================================================
            if (allAudits.isEmpty()) {
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
                            Icon(Icons.Default.FactCheck, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No 5S audits saved yet", fontWeight = FontWeight.Bold)
                            Text("Complete the checklist above to persist operational audits to Room database.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            } else {
                items(allAudits, key = { it.id }) { audit ->
                    SavedAuditCard(
                        audit = audit,
                        onDelete = { viewModel.deleteFiveSAudit(audit) }
                    )
                }
            }
        }
    }
}

/**
 * Reusable Card for each of the 5 Pillars (Sort, Set in Order, Shine, Standardize, Sustain)
 */
@Composable
private fun PillarAuditChecklistCard(
    pillarNumber: String,
    pillarTitle: String,
    description: String,
    pillarColor: Color,
    rubricKey: String,
    currentScore: Int,
    onScoreChanged: (Int) -> Unit,
    notes: String,
    onNotesChanged: (String) -> Unit,
    checkedQuestions: MutableMap<String, Boolean>,
    testTagPrefix: String
) {
    var isExpanded by remember { mutableStateOf(true) }

    val rubric = FiveSRubricDefaults.rubrics.firstOrNull { it.pillarKey == rubricKey }
    val questions = rubric?.questions ?: emptyList()

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, pillarColor.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Surface(
                        color = pillarColor.copy(alpha = 0.15f),
                        shape = CircleShape,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = pillarNumber,
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp,
                                color = pillarColor
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = pillarTitle,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = description,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = if (isExpanded) 3 else 1
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = pillarColor,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "$currentScore / 5",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    // Rating Bar (1 to 5 points)
                    Text(
                        text = "Rate Operational Compliance (1 - 5 Points):",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        (1..5).forEach { score ->
                            val isSelected = currentScore == score
                            Surface(
                                color = if (isSelected) pillarColor else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onScoreChanged(score) }
                                    .testTag("${testTagPrefix}_score_$score")
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = if (score <= currentScore) Icons.Default.Star else Icons.Default.StarBorder,
                                        contentDescription = null,
                                        tint = if (isSelected) Color.White else if (score <= currentScore) pillarColor else Color.Gray,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "$score pt",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Checklist Rubric Questions
                    Text(
                        text = "Clinical Audit Checklist Questions:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    questions.forEachIndexed { qIdx, question ->
                        val key = "${rubricKey}_$qIdx"
                        val isChecked = checkedQuestions[key] ?: true
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .clickable { checkedQuestions[key] = !isChecked },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isChecked) Icons.Default.CheckCircle else Icons.Default.StarBorder,
                                contentDescription = null,
                                tint = if (isChecked) pillarColor else Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = question,
                                fontSize = 11.sp,
                                color = if (isChecked) MaterialTheme.colorScheme.onSurface else Color.Gray,
                                lineHeight = 15.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = notes,
                        onValueChange = onNotesChanged,
                        label = { Text("Specific Findings for $pillarNumber") },
                        placeholder = { Text("e.g. Any defective items, spill areas, or visual non-conformances observed") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }
        }
    }
}

@Composable
private fun PillarMiniScore(label: String, score: Int, color: Color) {
    Surface(
        color = color.copy(alpha = 0.12f),
        shape = RoundedCornerShape(6.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = color)
            Text("$score/5", fontSize = 11.sp, fontWeight = FontWeight.Black, color = color)
        }
    }
}

/**
 * Saved Audit Card from Room database
 */
@Composable
private fun SavedAuditCard(
    audit: FiveSAudit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = audit.department,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Auditor: ${audit.auditorName} • Date: ${audit.auditDate}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = when {
                            audit.percentage >= 80.0 -> Color(0xFFDCFCE7)
                            audit.percentage >= 70.0 -> Color(0xFFFEF3C7)
                            else -> Color(0xFFFEE2E2)
                        },
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "${audit.totalScore}/25 (${String.format(Locale.getDefault(), "%.0f", audit.percentage)}%)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                audit.percentage >= 80.0 -> Color(0xFF166534)
                                audit.percentage >= 70.0 -> Color(0xFFB45309)
                                else -> Color(0xFF991B1B)
                            },
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Pillar Scores Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                PillarBadge(name = "1S Sort", score = audit.sortScore, color = Color(0xFFDC2626))
                PillarBadge(name = "2S Set", score = audit.setInOrderScore, color = Color(0xFFD97706))
                PillarBadge(name = "3S Shine", score = audit.shineScore, color = Color(0xFF2563EB))
                PillarBadge(name = "4S Std", score = audit.standardizeScore, color = Color(0xFF7C3AED))
                PillarBadge(name = "5S Sust", score = audit.sustainScore, color = Color(0xFF16A34A))
            }

            if (audit.remarks.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Remarks: ${audit.remarks}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun PillarBadge(name: String, score: Int, color: Color) {
    Surface(
        color = color.copy(alpha = 0.12f),
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = "$name: $score",
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
        )
    }
}
