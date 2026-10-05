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
import androidx.compose.material.icons.filled.AssignmentInd
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Tune
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import com.example.data.model.AcuityDefinitions
import com.example.data.model.ConsultantWorkload
import com.example.data.model.PatientRecord
import com.example.data.model.WorkloadStatus
import com.example.ui.components.KpiCard
import com.example.ui.viewmodel.HospitalViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsultantWorkloadScreen(
    viewModel: HospitalViewModel,
    modifier: Modifier = Modifier
) {
    val workloads by viewModel.consultantWorkloads.collectAsState()
    val activePatients by viewModel.activePatients.collectAsState()

    var selectedStatusFilter by remember { mutableStateOf("All") }
    var showAcuityGuideDialog by remember { mutableStateOf(false) }

    // Dialog state for patient reassignment
    var patientToReassign by remember { mutableStateOf<PatientRecord?>(null) }
    // Dialog state for acuity level adjustment
    var patientToAdjustAcuity by remember { mutableStateOf<PatientRecord?>(null) }

    val totalActiveInpatients = activePatients.size
    val totalAcuityPoints = workloads.sumOf { it.totalAcuityPoints }
    val totalMaxCapacity = workloads.sumOf { it.maxAcuityCapacity }
    val overallUtilizationPct = if (totalMaxCapacity > 0) (totalAcuityPoints / totalMaxCapacity) * 100.0 else 0.0
    val overloadedCount = workloads.count { it.workloadStatus == WorkloadStatus.OVERLOADED }

    val filteredWorkloads = workloads.filter { w ->
        when (selectedStatusFilter) {
            "Overloaded" -> w.workloadStatus == WorkloadStatus.OVERLOADED
            "Near Capacity" -> w.workloadStatus == WorkloadStatus.NEAR_CAPACITY
            "Optimal" -> w.workloadStatus == WorkloadStatus.OPTIMAL
            "Available" -> w.workloadStatus == WorkloadStatus.UNDERUTILIZED
            else -> true
        }
    }

    Scaffold(
        modifier = modifier.testTag("consultant_workload_screen")
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Consultant Workload & Capacity",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Patient Acuity Weighting & Heijunka Capacity Levelling",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(
                            onClick = { showAcuityGuideDialog = true },
                            modifier = Modifier.testTag("btn_acuity_guide")
                        ) {
                            Icon(
                                imageVector = Icons.Default.HelpOutline,
                                contentDescription = "Acuity Scoring Guide",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Institutional Workload Summary (3 KPI Cards)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        KpiCard(
                            value = "$totalActiveInpatients pts",
                            label = "Active Census",
                            subtext = "${String.format("%.1f", totalAcuityPoints)} Acuity Pts",
                            icon = Icons.Default.MedicalServices,
                            modifier = Modifier.weight(1f),
                            testTag = "kpi_active_census"
                        )
                        KpiCard(
                            value = "${String.format("%.0f", overallUtilizationPct)}%",
                            label = "Hospital Capacity",
                            subtext = "Ceiling: ${totalMaxCapacity.toInt()} pts",
                            isGood = overallUtilizationPct in 40.0..85.0,
                            icon = Icons.Default.Speed,
                            modifier = Modifier.weight(1f),
                            testTag = "kpi_workload_utilization"
                        )
                        KpiCard(
                            value = "$overloadedCount staff",
                            label = "Overloaded",
                            subtext = if (overloadedCount > 0) "Needs Levelling" else "Balanced",
                            isAlert = overloadedCount > 0,
                            isGood = overloadedCount == 0,
                            icon = Icons.Default.Balance,
                            modifier = Modifier.weight(1f),
                            testTag = "kpi_overloaded_staff"
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Workload Status Filter Chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            "All" to "All Staff (${workloads.size})",
                            "Overloaded" to "Overloaded (${workloads.count { it.workloadStatus == WorkloadStatus.OVERLOADED }})",
                            "Near Capacity" to "Near Cap (${workloads.count { it.workloadStatus == WorkloadStatus.NEAR_CAPACITY }})",
                            "Optimal" to "Optimal (${workloads.count { it.workloadStatus == WorkloadStatus.OPTIMAL }})",
                            "Available" to "Available (${workloads.count { it.workloadStatus == WorkloadStatus.UNDERUTILIZED }})"
                        ).forEach { (id, label) ->
                            FilterChip(
                                selected = selectedStatusFilter == id,
                                onClick = { selectedStatusFilter = id },
                                label = { Text(label, fontSize = 11.sp) },
                                modifier = Modifier.testTag("filter_workload_$id")
                            )
                        }
                    }
                }
            }

            // Workload List Body
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    // Lean Heijunka Banner
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Balance,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Lean Workload Levelling (Heijunka)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Acuity-weighted assignment prevents consultant fatigue, rounds delays, and patient safety hazards. Standard cap: 25 acuity points.",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                if (filteredWorkloads.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp).fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("No consultants match the selected filter.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                } else {
                    items(filteredWorkloads, key = { it.consultantName }) { workload ->
                        ConsultantWorkloadCard(
                            workload = workload,
                            onReassignPatient = { patient -> patientToReassign = patient },
                            onAdjustAcuity = { patient -> patientToAdjustAcuity = patient }
                        )
                    }
                }
            }
        }

        // Dialog: Patient Reassignment
        patientToReassign?.let { patient ->
            ReassignPatientDialog(
                patient = patient,
                workloads = workloads,
                onDismiss = { patientToReassign = null },
                onConfirm = { newConsultant ->
                    viewModel.reassignPatient(patient.id, newConsultant)
                    patientToReassign = null
                }
            )
        }

        // Dialog: Adjust Acuity
        patientToAdjustAcuity?.let { patient ->
            AdjustAcuityDialog(
                patient = patient,
                onDismiss = { patientToAdjustAcuity = null },
                onConfirm = { newAcuity ->
                    viewModel.updatePatientAcuity(patient.id, newAcuity)
                    patientToAdjustAcuity = null
                }
            )
        }

        // Dialog: Acuity Scoring Reference Guide
        if (showAcuityGuideDialog) {
            AcuityScoringGuideDialog(onDismiss = { showAcuityGuideDialog = false })
        }
    }
}

@Composable
fun ConsultantWorkloadCard(
    workload: ConsultantWorkload,
    onReassignPatient: (PatientRecord) -> Unit,
    onAdjustAcuity: (PatientRecord) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val statusColor = Color(workload.workloadStatus.badgeColor)
    val progressRatio = (workload.capacityUtilizationPct / 100.0).toFloat().coerceIn(0f, 1f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("consultant_card_${workload.consultantName.replace(" ", "_")}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = workload.consultantName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = workload.department,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    color = statusColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = workload.workloadStatus.label,
                        color = statusColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Capacity Progress Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Capacity Utilization: ${String.format("%.1f", workload.capacityUtilizationPct)}%",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (workload.capacityUtilizationPct > 100.0) statusColor else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${String.format("%.1f", workload.totalAcuityPoints)} / ${workload.maxAcuityCapacity.toInt()} pts",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { progressRatio },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = statusColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Acuity Distribution Pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                AcuityCountPill(level = 1, count = workload.acuityBreakdown[1] ?: 0, weight = 1.0)
                AcuityCountPill(level = 2, count = workload.acuityBreakdown[2] ?: 0, weight = 1.5)
                AcuityCountPill(level = 3, count = workload.acuityBreakdown[3] ?: 0, weight = 2.5)
                AcuityCountPill(level = 4, count = workload.acuityBreakdown[4] ?: 0, weight = 4.0)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Toggle Expand Patients Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${workload.totalPatientCount} Active Patients Assigned",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                TextButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.testTag("btn_toggle_patients_${workload.consultantName.replace(" ", "_")}")
                ) {
                    Text(if (expanded) "Hide Patients" else "Manage Patients", fontSize = 11.sp)
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Expanded Patient List
            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (workload.assignedPatients.isEmpty()) {
                        Text(
                            text = "No active patients currently assigned. Consultant is available for new admissions.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        workload.assignedPatients.forEach { patient ->
                            AssignedPatientItemCard(
                                patient = patient,
                                onReassign = { onReassignPatient(patient) },
                                onAdjustAcuity = { onAdjustAcuity(patient) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AcuityCountPill(level: Int, count: Int, weight: Double) {
    val color = when (level) {
        1 -> Color(0xFF16A34A)
        2 -> Color(0xFF2563EB)
        3 -> Color(0xFFD97706)
        4 -> Color(0xFFDC2626)
        else -> Color.Gray
    }

    Surface(
        color = color.copy(alpha = 0.12f),
        shape = RoundedCornerShape(6.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(6.dp).background(color, CircleShape))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "L$level (${weight}x): $count",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
fun AssignedPatientItemCard(
    patient: PatientRecord,
    onReassign: () -> Unit,
    onAdjustAcuity: () -> Unit
) {
    val acuityColor = when (patient.acuityLevel) {
        1 -> Color(0xFF16A34A)
        2 -> Color(0xFF2563EB)
        3 -> Color(0xFFD97706)
        4 -> Color(0xFFDC2626)
        else -> Color.Gray
    }

    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth().testTag("assigned_patient_${patient.id}")
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = patient.patientName,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${patient.ipdNumber} • Age ${patient.age} (${patient.gender}) • LOS: ${patient.losDays}d",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    color = acuityColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = patient.acuityLabel,
                        color = acuityColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            if (patient.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = patient.notes,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = onAdjustAcuity,
                    modifier = Modifier.height(30.dp).testTag("btn_adjust_acuity_${patient.id}"),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Adjust Acuity", fontSize = 10.sp)
                }

                Spacer(modifier = Modifier.width(6.dp))

                Button(
                    onClick = onReassign,
                    modifier = Modifier.height(30.dp).testTag("btn_reassign_${patient.id}"),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(imageVector = Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reassign", fontSize = 10.sp)
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// Dialogs: Reassign Patient & Adjust Acuity
// -------------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReassignPatientDialog(
    patient: PatientRecord,
    workloads: List<ConsultantWorkload>,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var selectedConsultant by remember {
        mutableStateOf(
            workloads.firstOrNull { it.consultantName != patient.consultantName }?.consultantName ?: ""
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Reassign Inpatient (Workload Levelling)") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Patient: ${patient.patientName} (${patient.ipdNumber})",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Current Acuity: ${patient.acuityLabel} (${patient.acuityWeight} pts)",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Current Consultant: ${patient.consultantName}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))
                Text("Select Target Consultant (View Current Load):", fontSize = 12.sp, fontWeight = FontWeight.Bold)

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    workloads.filter { it.consultantName != patient.consultantName }.forEach { w ->
                        val isSelected = selectedConsultant == w.consultantName
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedConsultant = w.consultantName }
                                .testTag("select_consultant_${w.consultantName.replace(" ", "_")}")
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(w.consultantName, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text(w.department, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "${String.format("%.1f", w.capacityUtilizationPct)}% Utilized",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(w.workloadStatus.badgeColor)
                                    )
                                    Text(
                                        text = "${String.format("%.1f", w.totalAcuityPoints)} / 25 pts",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { if (selectedConsultant.isNotBlank()) onConfirm(selectedConsultant) },
                enabled = selectedConsultant.isNotBlank(),
                modifier = Modifier.testTag("btn_confirm_reassign")
            ) {
                Text("Confirm Reassignment")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AdjustAcuityDialog(
    patient: PatientRecord,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var selectedLevel by remember { mutableStateOf(patient.acuityLevel) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Update Patient Acuity Level") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Patient: ${patient.patientName} (${patient.ipdNumber})",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Adjust clinical workload points as patient improves or requires higher nursing supervision.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                AcuityDefinitions.levels.forEach { config ->
                    val isSelected = selectedLevel == config.level
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedLevel = config.level }
                            .testTag("acuity_choice_${config.level}")
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(config.name, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Surface(
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        "${config.weight}x Pts",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(config.description, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedLevel) },
                modifier = Modifier.testTag("btn_confirm_acuity")
            ) {
                Text("Save Acuity Level")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AcuityScoringGuideDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Patient Acuity Scoring Guide (Clinical Weights)") },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                item {
                    Text(
                        text = "Workload capacity is calculated using standard clinical acuity multipliers. A standard consultant shift has a ceiling of 25.0 weighted points.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                items(AcuityDefinitions.levels) { config ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(config.name, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text("${config.weight}x Weight", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("Criteria: ${config.clinicalCriteria}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface)
                            Text("Supervision: ${config.description}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) { Text("Close Guide") }
        }
    )
}
