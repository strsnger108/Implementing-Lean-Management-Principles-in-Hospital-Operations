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
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Visibility
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import com.example.data.model.BloodLabReport
import com.example.data.model.LabParameterItem
import com.example.data.model.SampleLabDefinitions
import com.example.ui.viewmodel.HospitalViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientBloodReportsSection(
    viewModel: HospitalViewModel,
    modifier: Modifier = Modifier
) {
    val reports by viewModel.activePatientBloodReports.collectAsState()
    val familyMembers by viewModel.familyMembers.collectAsState()
    val activeSession by viewModel.activeUserSession.collectAsState()
    val selectedRegNumber by viewModel.selectedRegNumber.collectAsState()

    var selectedCategoryFilter by remember { mutableStateOf("All") }
    var showAddReportDialog by remember { mutableStateOf(false) }
    var selectedReportForViewer by remember { mutableStateOf<BloodLabReport?>(null) }

    val currentReg = selectedRegNumber ?: activeSession?.hospitalRegNumber ?: "SGH-2026-0288"
    val currentMember = familyMembers.firstOrNull { it.hospitalRegNumber == currentReg }
    val displayName = currentMember?.fullName ?: activeSession?.fullName ?: "Patient"

    val filteredReports = reports.filter { r ->
        selectedCategoryFilter == "All" || r.testCategory.contains(selectedCategoryFilter, ignoreCase = true)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("patient_blood_reports_section")
    ) {
        // Patient / Family Member Selector Header
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
                            Icon(Icons.Default.Biotech, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Blood & Diagnostic Reports ($displayName)",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = "UHID: $currentReg • Verified by Hospital Pathology Laboratory",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    OutlinedButton(
                        onClick = { showAddReportDialog = true },
                        modifier = Modifier.height(30.dp).testTag("btn_add_lab_report"),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Lab", fontSize = 10.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Family Member Profiles Chips
                if (familyMembers.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        familyMembers.forEach { member ->
                            val isSelected = (selectedRegNumber ?: activeSession?.hospitalRegNumber) == member.hospitalRegNumber
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.setSelectedPatientRegNumber(member.hospitalRegNumber) },
                                label = { Text("${member.fullName} (${member.relation})", fontSize = 10.sp) },
                                leadingIcon = {
                                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(12.dp))
                                },
                                modifier = Modifier.testTag("chip_lab_family_${member.fullName.replace(" ", "_")}")
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }

                // Category Filter Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("All", "Complete Blood Count (CBC)", "Kidney Function Test (KFT)", "Liver Function Test (LFT)", "Blood Glucose / HbA1c").forEach { cat ->
                        FilterChip(
                            selected = selectedCategoryFilter == cat,
                            onClick = { selectedCategoryFilter = cat },
                            label = { Text(cat, fontSize = 10.sp) },
                            modifier = Modifier.testTag("filter_lab_${cat.replace(" ", "_")}")
                        )
                    }
                }
            }
        }

        // Reports List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Pathology LIMS Banner
            item {
                Surface(
                    color = Color(0xFFF0FDF4),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF86EFAC)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Pathology LIMS & ABDM EHR Integrated",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF166534)
                            )
                            Text(
                                text = "Lab blood samples drawn in OPD or post-treatment are validated by the pathologist and auto-synced directly to this timeline.",
                                fontSize = 10.sp,
                                color = Color(0xFF15803D)
                            )
                        }
                    }
                }
            }

            if (filteredReports.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Science, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No blood test reports found for $selectedCategoryFilter", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Pathology investigation results will be published here upon validation.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                        }
                    }
                }
            } else {
                items(filteredReports, key = { it.id }) { report ->
                    BloodReportCard(
                        report = report,
                        onViewDocument = { selectedReportForViewer = report }
                    )
                }
            }
        }
    }

    if (showAddReportDialog) {
        AddBloodReportDialog(
            defaultPatientName = displayName,
            defaultRegNumber = currentReg,
            onDismiss = { showAddReportDialog = false },
            onConfirm = { reg, name, doctor, dept, encounter, category, title, summary, status ->
                viewModel.addBloodReport(reg, name, doctor, dept, encounter, category, title, summary, status)
                showAddReportDialog = false
            }
        )
    }

    if (selectedReportForViewer != null) {
        ClinicalDocumentViewerDialog(
            documentData = ClinicalDocumentData.LabReport(selectedReportForViewer!!),
            onDismiss = { selectedReportForViewer = null }
        )
    }
}

@Composable
fun BloodReportCard(
    report: BloodLabReport,
    onViewDocument: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val statusColor = when (report.overallStatus) {
        "Normal" -> Color(0xFF16A34A)
        "Attention Required" -> Color(0xFFD97706)
        else -> Color(0xFFDC2626)
    }

    val isAbnormal = report.overallStatus != "Normal"
    val parameters = SampleLabDefinitions.getParametersForCategory(report.testCategory, isAbnormal)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("blood_report_card_${report.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Report Title & Overall Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(statusColor.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isAbnormal) Icons.Default.Warning else Icons.Default.Science,
                            contentDescription = null,
                            tint = statusColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = report.reportTitle,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${report.testCategory} • ${report.encounterType}",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    color = statusColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = report.overallStatus,
                        color = statusColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Parameters Summary Chip
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "Key Findings: ${report.parametersSummary}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Sample Drawn: ${report.sampleDate} • Barcode: ${report.labBarcode}",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Expandable Detailed Laboratory Parameter Table
            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    Text(
                        text = "Detailed Analyte Reference Breakdown:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    parameters.forEach { param ->
                        LabParameterRow(param = param)
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Verified: ${report.verifiedBy}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF166534)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Footer: Toggle Details & Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.height(28.dp).testTag("btn_toggle_lab_details_${report.id}")
                ) {
                    Text(if (expanded) "Hide Detailed Table" else "View Full Analytes", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedButton(
                        onClick = onViewDocument,
                        modifier = Modifier.height(26.dp).testTag("btn_view_pdf_${report.id}"),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
                    ) {
                        Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(10.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("View PDF", fontSize = 9.sp)
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    OutlinedButton(
                        onClick = onViewDocument,
                        modifier = Modifier.height(26.dp).testTag("btn_download_pdf_${report.id}"),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
                    ) {
                        Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(10.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("Download PDF", fontSize = 9.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun LabParameterRow(param: LabParameterItem) {
    val isAbnormal = param.flag != "NORMAL"
    val flagColor = when (param.flag) {
        "HIGH", "CRITICAL" -> Color(0xFFDC2626)
        "LOW" -> Color(0xFFD97706)
        else -> Color(0xFF16A34A)
    }

    Surface(
        color = if (isAbnormal) flagColor.copy(alpha = 0.08f) else Color.Transparent,
        shape = RoundedCornerShape(6.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(param.name, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                Text("Ref: ${param.referenceRange} ${param.unit}", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${param.resultValue} ${param.unit}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isAbnormal) flagColor else MaterialTheme.colorScheme.onSurface
                )
                if (isAbnormal) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        color = flagColor.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = param.flag,
                            color = flagColor,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// Dialog: Add Diagnostic Blood Report
// -------------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBloodReportDialog(
    defaultPatientName: String,
    defaultRegNumber: String,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String, String, String, String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("Complete Hemogram with Platelets") }
    var category by remember { mutableStateOf("Complete Blood Count (CBC)") }
    var encounterType by remember { mutableStateOf("OPD Routine Workup") }
    var summary by remember { mutableStateOf("Hb: 13.5 g/dL • WBC: 6,800 /uL • Platelets: 2.5 L/uL") }
    var overallStatus by remember { mutableStateOf("Normal") }
    var doctorName by remember { mutableStateOf("Dr. Rahul Sinha") }

    val categories = listOf(
        "Complete Blood Count (CBC)",
        "Kidney Function Test (KFT)",
        "Liver Function Test (LFT)",
        "Blood Glucose / HbA1c"
    )
    val encounters = listOf("OPD Routine Workup", "Post-Treatment Evaluation", "Discharge Clearance", "Admission Workup")
    val statuses = listOf("Normal", "Attention Required", "Critical Alert")

    var catExpanded by remember { mutableStateOf(false) }
    var encExpanded by remember { mutableStateOf(false) }
    var statExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Record Diagnostic Blood Report", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Patient: $defaultPatientName ($defaultRegNumber)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Report Title") },
                    modifier = Modifier.fillMaxWidth().testTag("input_lab_title")
                )

                ExposedDropdownMenuBox(
                    expanded = catExpanded,
                    onExpandedChange = { catExpanded = it }
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Test Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = catExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = catExpanded,
                        onDismissRequest = { catExpanded = false }
                    ) {
                        categories.forEach { c ->
                            DropdownMenuItem(text = { Text(c) }, onClick = { category = c; catExpanded = false })
                        }
                    }
                }

                ExposedDropdownMenuBox(
                    expanded = encExpanded,
                    onExpandedChange = { encExpanded = it }
                ) {
                    OutlinedTextField(
                        value = encounterType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Encounter Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = encExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = encExpanded,
                        onDismissRequest = { encExpanded = false }
                    ) {
                        encounters.forEach { e ->
                            DropdownMenuItem(text = { Text(e) }, onClick = { encounterType = e; encExpanded = false })
                        }
                    }
                }

                OutlinedTextField(
                    value = summary,
                    onValueChange = { summary = it },
                    label = { Text("Findings Summary") },
                    modifier = Modifier.fillMaxWidth()
                )

                ExposedDropdownMenuBox(
                    expanded = statExpanded,
                    onExpandedChange = { statExpanded = it }
                ) {
                    OutlinedTextField(
                        value = overallStatus,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Overall Status") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = statExpanded,
                        onDismissRequest = { statExpanded = false }
                    ) {
                        statuses.forEach { s ->
                            DropdownMenuItem(text = { Text(s) }, onClick = { overallStatus = s; statExpanded = false })
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(
                            defaultRegNumber,
                            defaultPatientName,
                            doctorName,
                            "General Medicine",
                            encounterType,
                            category,
                            title,
                            summary,
                            overallStatus
                        )
                    }
                },
                enabled = title.isNotBlank(),
                modifier = Modifier.testTag("btn_confirm_add_lab")
            ) {
                Text("Publish to Patient Portal")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
