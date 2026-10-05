package com.example.ui.components

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
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PatientFlowStage
import com.example.data.model.PatientRecord
import com.example.ui.viewmodel.HospitalViewModel

@Composable
fun PatientKanbanBoard(
    viewModel: HospitalViewModel,
    onDischargeClick: (PatientRecord) -> Unit,
    modifier: Modifier = Modifier
) {
    val activePatients by viewModel.activePatients.collectAsState()
    val wipLimits by viewModel.kanbanWipLimits.collectAsState()
    val searchQuery by viewModel.patientSearchQuery.collectAsState()

    var selectedDoctorFilter by remember { mutableStateOf("All") }
    var selectedAcuityFilter by remember { mutableIntStateOf(0) } // 0 means all
    var showWipSettingsDialog by remember { mutableStateOf(false) }

    // Dialog state for direct stage selector
    var patientToMoveStage by remember { mutableStateOf<PatientRecord?>(null) }

    // Filter patients based on search, doctor, and acuity
    val filteredPatients = activePatients.filter { p ->
        val matchesSearch = searchQuery.isBlank() ||
                p.patientName.contains(searchQuery, ignoreCase = true) ||
                p.ipdNumber.contains(searchQuery, ignoreCase = true) ||
                p.department.contains(searchQuery, ignoreCase = true)

        val matchesDoctor = selectedDoctorFilter == "All" || p.consultantName == selectedDoctorFilter
        val matchesAcuity = selectedAcuityFilter == 0 || p.acuityLevel == selectedAcuityFilter

        matchesSearch && matchesDoctor && matchesAcuity
    }

    // Group patients into stages:
    // If patient's flowStage doesn't match enum or is blank, default by losDays:
    // losDays 0 -> Admission, losDays 1 -> Diagnostics, losDays 2+ -> Treatment
    val stages = PatientFlowStage.entries
    val patientsByStage: Map<PatientFlowStage, List<PatientRecord>> = stages.associateWith { stage ->
        filteredPatients.filter { patient ->
            val patientStageKey = patient.flowStage.ifBlank {
                when {
                    patient.losDays == 0 -> "Admission"
                    patient.losDays == 1 -> "Diagnostics"
                    else -> "Treatment"
                }
            }
            patientStageKey.equals(stage.stageKey, ignoreCase = true)
        }
    }

    // Identify primary bottleneck stage (stage with largest count / wipLimit ratio where count > wipLimit)
    val bottleneckStage = stages
        .map { stage ->
            val count = patientsByStage[stage]?.size ?: 0
            val limit = wipLimits[stage.stageKey] ?: stage.defaultWipLimit
            Triple(stage, count, count.toDouble() / limit.toDouble())
        }
        .filter { it.third > 1.0 }
        .maxByOrNull { it.third }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("patient_kanban_board")
    ) {
        // Bottleneck & Kanban Header Summary
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 1.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Patient Status Transitions (Kanban Flow)",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = "Visual pull system across 4 stages with Work-In-Progress (WIP) bottleneck alerts",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    OutlinedButton(
                        onClick = { showWipSettingsDialog = true },
                        modifier = Modifier
                            .height(32.dp)
                            .testTag("btn_configure_wip"),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("WIP Limits", fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Bottleneck Callout Banner
                if (bottleneckStage != null) {
                    val stage = bottleneckStage.first
                    val count = bottleneckStage.second
                    val limit = wipLimits[stage.stageKey] ?: stage.defaultWipLimit
                    val overflow = count - limit

                    Surface(
                        color = Color(0xFFFEF2F2),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCA5A5)),
                        modifier = Modifier.fillMaxWidth().testTag("kanban_bottleneck_banner")
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color(0xFFDC2626),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "🚨 FLOW BOTTLENECK DETECTED: ${stage.title} Stage",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF991B1B)
                                )
                                Text(
                                    text = "$count patients queued vs WIP limit of $limit ($overflow patient queue overflow). Upstream stages should pause pushing until this bottleneck is resolved.",
                                    fontSize = 11.sp,
                                    color = Color(0xFF7F1D1D)
                                )
                            }
                        }
                    }
                } else {
                    Surface(
                        color = Color(0xFFF0FDF4),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF86EFAC)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF16A34A),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Hospital Flow Balanced: All stages operating within configured WIP limits (${filteredPatients.size} active patients).",
                                fontSize = 11.sp,
                                color = Color(0xFF166534),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Quick Filters: Doctor & Acuity
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        "All" to "All Doctors",
                        "Dr. Rahul Sinha" to "Dr. Rahul Sinha (40%)",
                        "Dr. M.S. Islam" to "Dr. M.S. Islam",
                        "Dr. Rajnish Kumar" to "Dr. Rajnish Kumar",
                        "Dr. Javed Akhtar" to "Dr. Javed Akhtar"
                    ).forEach { (docId, docLabel) ->
                        FilterChip(
                            selected = selectedDoctorFilter == docId,
                            onClick = { selectedDoctorFilter = docId },
                            label = { Text(docLabel, fontSize = 10.sp) },
                            modifier = Modifier.testTag("filter_doc_${docId.replace(" ", "_")}")
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    listOf(
                        0 to "All Acuity",
                        1 to "L1 Stable",
                        2 to "L2 Moderate",
                        3 to "L3 High (HDU)",
                        4 to "L4 Critical (ICU)"
                    ).forEach { (lvl, label) ->
                        FilterChip(
                            selected = selectedAcuityFilter == lvl,
                            onClick = { selectedAcuityFilter = lvl },
                            label = { Text(label, fontSize = 10.sp) },
                            modifier = Modifier.testTag("filter_acuity_$lvl")
                        )
                    }
                }
            }
        }

        // Horizontal Scrolling Kanban Columns
        Row(
            modifier = Modifier
                .fillMaxSize()
                .horizontalScroll(rememberScrollState())
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            stages.forEach { stage ->
                val stagePatients = patientsByStage[stage] ?: emptyList()
                val limit = wipLimits[stage.stageKey] ?: stage.defaultWipLimit

                KanbanColumn(
                    stage = stage,
                    patients = stagePatients,
                    wipLimit = limit,
                    onMoveStage = { patient, newStage ->
                        viewModel.updatePatientFlowStage(patient.id, newStage)
                    },
                    onOpenMoveDialog = { patient -> patientToMoveStage = patient },
                    onDischargeClick = onDischargeClick,
                    modifier = Modifier
                        .width(290.dp)
                        .fillMaxHeight()
                )
            }
        }
    }

    // Dialog to select any stage directly
    patientToMoveStage?.let { patient ->
        MovePatientStageDialog(
            patient = patient,
            currentStage = PatientFlowStage.fromKey(patient.flowStage),
            onDismiss = { patientToMoveStage = null },
            onConfirm = { targetStage ->
                viewModel.updatePatientFlowStage(patient.id, targetStage.stageKey)
                patientToMoveStage = null
            }
        )
    }

    // Dialog to configure WIP limits
    if (showWipSettingsDialog) {
        WipSettingsDialog(
            currentLimits = wipLimits,
            onDismiss = { showWipSettingsDialog = false },
            onSave = { updatedLimits ->
                updatedLimits.forEach { (key, limit) ->
                    viewModel.updateWipLimit(key, limit)
                }
                showWipSettingsDialog = false
            }
        )
    }
}

@Composable
fun KanbanColumn(
    stage: PatientFlowStage,
    patients: List<PatientRecord>,
    wipLimit: Int,
    onMoveStage: (PatientRecord, String) -> Unit,
    onOpenMoveDialog: (PatientRecord) -> Unit,
    onDischargeClick: (PatientRecord) -> Unit,
    modifier: Modifier = Modifier
) {
    val count = patients.size
    val isOverLimit = count > wipLimit
    val headerColor = Color(stage.headerColor)
    val progressRatio = (count.toFloat() / wipLimit.toFloat()).coerceIn(0f, 1f)

    Card(
        modifier = modifier.testTag("kanban_column_${stage.stageKey}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Column Header
            Surface(
                color = if (isOverLimit) Color(0xFFFEF2F2) else Color(stage.badgeBgColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(if (isOverLimit) Color(0xFFDC2626) else headerColor, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (stage) {
                                        PatientFlowStage.ADMISSION -> Icons.Default.Hotel
                                        PatientFlowStage.DIAGNOSTICS -> Icons.Default.Biotech
                                        PatientFlowStage.TREATMENT -> Icons.Default.LocalHospital
                                        PatientFlowStage.DISCHARGE -> Icons.Default.ExitToApp
                                    },
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stage.title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isOverLimit) Color(0xFF991B1B) else headerColor
                            )
                        }

                        // WIP Counter Badge
                        Surface(
                            color = if (isOverLimit) Color(0xFFDC2626) else headerColor,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "$count / $wipLimit WIP",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stage.description,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { progressRatio },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = if (isOverLimit) Color(0xFFDC2626) else headerColor,
                        trackColor = Color.White.copy(alpha = 0.5f)
                    )

                    if (isOverLimit) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color(0xFFDC2626),
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "WIP Limit Exceeded! Bottleneck Alert",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFDC2626)
                            )
                        }
                    }
                }
            }

            // Patients in this Column
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (patients.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "No patients in ${stage.title}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Pull capacity ready",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                } else {
                    items(patients, key = { it.id }) { patient ->
                        KanbanPatientCard(
                            patient = patient,
                            currentStage = stage,
                            onMoveStage = onMoveStage,
                            onOpenMoveDialog = { onOpenMoveDialog(patient) },
                            onDischargeClick = { onDischargeClick(patient) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun KanbanPatientCard(
    patient: PatientRecord,
    currentStage: PatientFlowStage,
    onMoveStage: (PatientRecord, String) -> Unit,
    onOpenMoveDialog: () -> Unit,
    onDischargeClick: () -> Unit
) {
    val acuityColor = when (patient.acuityLevel) {
        1 -> Color(0xFF16A34A)
        2 -> Color(0xFF2563EB)
        3 -> Color(0xFFD97706)
        4 -> Color(0xFFDC2626)
        else -> Color.Gray
    }

    val stageIndex = PatientFlowStage.entries.indexOf(currentStage)
    val hasPrev = stageIndex > 0
    val hasNext = stageIndex < PatientFlowStage.entries.size - 1

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("kanban_patient_card_${patient.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            // Patient Name & Acuity Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = patient.patientName,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )

                Surface(
                    color = acuityColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = patient.acuityLabel,
                        color = acuityColor,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            // IPD Number & Demographics
            Text(
                text = "${patient.ipdNumber} • ${patient.age}y (${patient.gender}) • Day ${patient.losDays + 1}",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Consultant & Dept
            Text(
                text = "${patient.consultantName} (${patient.department})",
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )

            if (patient.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = patient.notes,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Stage Transition Action Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back button
                if (hasPrev) {
                    val prevStage = PatientFlowStage.entries[stageIndex - 1]
                    IconButton(
                        onClick = { onMoveStage(patient, prevStage.stageKey) },
                        modifier = Modifier.size(28.dp).testTag("btn_kanban_prev_${patient.id}")
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Move to ${prevStage.title}", modifier = Modifier.size(14.dp))
                    }
                } else {
                    Spacer(modifier = Modifier.width(28.dp))
                }

                // Middle menu / Move to any stage
                IconButton(
                    onClick = onOpenMoveDialog,
                    modifier = Modifier.size(28.dp).testTag("btn_kanban_menu_${patient.id}")
                ) {
                    Icon(imageVector = Icons.Default.MoreHoriz, contentDescription = "Select Stage", modifier = Modifier.size(16.dp))
                }

                // Forward button or Finalize Discharge
                if (currentStage == PatientFlowStage.DISCHARGE) {
                    Button(
                        onClick = onDischargeClick,
                        modifier = Modifier.height(28.dp).testTag("btn_kanban_discharge_${patient.id}"),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A))
                    ) {
                        Text("Discharge", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                } else if (hasNext) {
                    val nextStage = PatientFlowStage.entries[stageIndex + 1]
                    Button(
                        onClick = { onMoveStage(patient, nextStage.stageKey) },
                        modifier = Modifier.height(28.dp).testTag("btn_kanban_next_${patient.id}"),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(nextStage.title, fontSize = 10.sp)
                        Spacer(modifier = Modifier.width(2.dp))
                        Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(12.dp))
                    }
                } else {
                    Spacer(modifier = Modifier.width(28.dp))
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// Dialog: Move Patient to Any Stage
// -------------------------------------------------------------------------
@Composable
fun MovePatientStageDialog(
    patient: PatientRecord,
    currentStage: PatientFlowStage,
    onDismiss: () -> Unit,
    onConfirm: (PatientFlowStage) -> Unit
) {
    var selectedStage by remember { mutableStateOf(currentStage) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Transition Patient Flow Stage") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Patient: ${patient.patientName} (${patient.ipdNumber})",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Current Stage: ${currentStage.title}",
                    fontSize = 11.sp,
                    color = Color(currentStage.headerColor),
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(4.dp))
                Text("Select Target Transition Stage:", fontSize = 12.sp, fontWeight = FontWeight.Bold)

                PatientFlowStage.entries.forEach { stage ->
                    val isSelected = selectedStage == stage
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) Color(stage.badgeBgColor) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, Color(stage.headerColor)) else null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedStage = stage }
                            .testTag("select_stage_${stage.stageKey}")
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stage.title,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(stage.headerColor)
                                )
                                Text("WIP Cap: ${stage.defaultWipLimit}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Text(stage.description, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedStage) },
                modifier = Modifier.testTag("btn_confirm_move_stage")
            ) {
                Text("Move to ${selectedStage.title}")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

// -------------------------------------------------------------------------
// Dialog: Configure Stage WIP Limits
// -------------------------------------------------------------------------
@Composable
fun WipSettingsDialog(
    currentLimits: Map<String, Int>,
    onDismiss: () -> Unit,
    onSave: (Map<String, Int>) -> Unit
) {
    var admissionLimit by remember { mutableIntStateOf(currentLimits["Admission"] ?: 5) }
    var diagnosticsLimit by remember { mutableIntStateOf(currentLimits["Diagnostics"] ?: 6) }
    var treatmentLimit by remember { mutableIntStateOf(currentLimits["Treatment"] ?: 10) }
    var dischargeLimit by remember { mutableIntStateOf(currentLimits["Discharge"] ?: 4) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Configure Lean WIP Limits") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Setting Work-In-Progress (WIP) caps highlights bottlenecks when queues build up. Standard limits are calibrated from hospital bed capacity.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                WipStepperRow(
                    label = "Admission Stage Cap:",
                    value = admissionLimit,
                    color = Color(0xFF0284C7),
                    onIncrement = { admissionLimit++ },
                    onDecrement = { if (admissionLimit > 1) admissionLimit-- }
                )

                WipStepperRow(
                    label = "Diagnostics Stage Cap:",
                    value = diagnosticsLimit,
                    color = Color(0xFF7C3AED),
                    onIncrement = { diagnosticsLimit++ },
                    onDecrement = { if (diagnosticsLimit > 1) diagnosticsLimit-- }
                )

                WipStepperRow(
                    label = "Treatment Stage Cap:",
                    value = treatmentLimit,
                    color = Color(0xFF16A34A),
                    onIncrement = { treatmentLimit++ },
                    onDecrement = { if (treatmentLimit > 1) treatmentLimit-- }
                )

                WipStepperRow(
                    label = "Discharge Stage Cap:",
                    value = dischargeLimit,
                    color = Color(0xFFEA580C),
                    onIncrement = { dischargeLimit++ },
                    onDecrement = { if (dischargeLimit > 1) dischargeLimit-- }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        mapOf(
                            "Admission" to admissionLimit,
                            "Diagnostics" to diagnosticsLimit,
                            "Treatment" to treatmentLimit,
                            "Discharge" to dischargeLimit
                        )
                    )
                },
                modifier = Modifier.testTag("btn_save_wip_limits")
            ) {
                Text("Save WIP Limits")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun WipStepperRow(
    label: String,
    value: Int,
    color: Color,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color)
            Text("Max concurrent patients", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedButton(
                onClick = onDecrement,
                modifier = Modifier.size(32.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
            ) {
                Text("-", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }

            Text(
                text = "$value",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            OutlinedButton(
                onClick = onIncrement,
                modifier = Modifier.size(32.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
            ) {
                Text("+", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
