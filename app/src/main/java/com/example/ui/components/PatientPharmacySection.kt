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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Visibility
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
import com.example.data.model.FamilyMember
import com.example.data.model.PharmacyPrescription
import com.example.ui.viewmodel.HospitalViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientPharmacySection(
    viewModel: HospitalViewModel,
    modifier: Modifier = Modifier
) {
    val prescriptions by viewModel.activePatientPrescriptions.collectAsState()
    val familyMembers by viewModel.familyMembers.collectAsState()
    val activeSession by viewModel.activeUserSession.collectAsState()
    val selectedRegNumber by viewModel.selectedRegNumber.collectAsState()

    var selectedEncounterFilter by remember { mutableStateOf("All") }
    var showAddPrescriptionDialog by remember { mutableStateOf(false) }
    var selectedPrescriptionForViewer by remember { mutableStateOf<PharmacyPrescription?>(null) }

    val currentReg = selectedRegNumber ?: activeSession?.hospitalRegNumber ?: "SGH-2026-0288"
    val currentMember = familyMembers.firstOrNull { it.hospitalRegNumber == currentReg }
    val displayName = currentMember?.fullName ?: activeSession?.fullName ?: "Patient"

    val filteredPrescriptions = prescriptions.filter { p ->
        selectedEncounterFilter == "All" || p.encounterType.contains(selectedEncounterFilter, ignoreCase = true)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("patient_pharmacy_section")
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
                            Icon(Icons.Default.LocalPharmacy, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Prescribed Medicines ($displayName)",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = "UHID: $currentReg • Auto-synced from Hospital Pharmacy Module",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    OutlinedButton(
                        onClick = { showAddPrescriptionDialog = true },
                        modifier = Modifier.height(30.dp).testTag("btn_add_rx"),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Rx", fontSize = 10.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Family Member Profile Chips
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
                                modifier = Modifier.testTag("chip_rx_family_${member.fullName.replace(" ", "_")}")
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }

                // Encounter Filters: All, OPD Consultation, Inpatient Treatment, Discharge Medication
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("All", "OPD Consultation", "Inpatient Treatment", "Discharge Medication").forEach { enc ->
                        FilterChip(
                            selected = selectedEncounterFilter == enc,
                            onClick = { selectedEncounterFilter = enc },
                            label = { Text(enc, fontSize = 10.sp) },
                            modifier = Modifier.testTag("filter_rx_${enc.replace(" ", "_")}")
                        )
                    }
                }
            }
        }

        // Prescriptions List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Live HMS Pharmacy Sync Notice
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
                                text = "Pharmacy Dispensation Live Synchronization",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF166534)
                            )
                            Text(
                                text = "Medications prescribed by doctors in OPD or after Inpatient treatment automatically flow to the hospital pharmacy and update here.",
                                fontSize = 10.sp,
                                color = Color(0xFF15803D)
                            )
                        }
                    }
                }
            }

            if (filteredPrescriptions.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Medication, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No medications found for $selectedEncounterFilter", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("New prescriptions from doctor visits will appear here.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                        }
                    }
                }
            } else {
                items(filteredPrescriptions, key = { it.id }) { prescription ->
                    PrescriptionCard(
                        prescription = prescription,
                        onMarkCollected = {
                            viewModel.updatePrescriptionStatus(prescription.id, "Collected")
                        },
                        onViewDocument = {
                            selectedPrescriptionForViewer = prescription
                        }
                    )
                }
            }
        }
    }

    if (showAddPrescriptionDialog) {
        AddPrescriptionDialog(
            defaultPatientName = displayName,
            defaultRegNumber = currentReg,
            onDismiss = { showAddPrescriptionDialog = false },
            onConfirm = { reg, name, doctor, dept, encounter, med, dosage, freq, dur, inst ->
                viewModel.addPrescription(reg, name, doctor, dept, encounter, med, dosage, freq, dur, inst)
                showAddPrescriptionDialog = false
            }
        )
    }

    if (selectedPrescriptionForViewer != null) {
        ClinicalDocumentViewerDialog(
            documentData = ClinicalDocumentData.Prescription(selectedPrescriptionForViewer!!),
            onDismiss = { selectedPrescriptionForViewer = null }
        )
    }
}

@Composable
fun PrescriptionCard(
    prescription: PharmacyPrescription,
    onMarkCollected: () -> Unit,
    onViewDocument: () -> Unit
) {
    val statusColor = when (prescription.dispensationStatus) {
        "Collected" -> Color(0xFF16A34A) // Green
        "Dispensed / Ready" -> Color(0xFF0284C7) // Blue
        "In Preparation" -> Color(0xFFD97706) // Amber
        else -> Color.Gray
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("prescription_card_${prescription.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Medicine Name & Dispensation Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Medication, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = prescription.medicineName,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${prescription.encounterType} • ${prescription.rxNumber}",
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
                        text = prescription.dispensationStatus,
                        color = statusColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Dosage & Frequency Grid
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Dosage & Strength", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(prescription.dosage, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    }
                    Column {
                        Text("Frequency", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(prescription.frequency, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Duration", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(prescription.duration, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }

            if (prescription.instructions.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Instructions: ",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = prescription.instructions,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Footer: Doctor & Counter
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Prescribed by ${prescription.doctorName} (${prescription.datePrescribed})",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Pickup: ${prescription.pharmacyCounter}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedButton(
                        onClick = onViewDocument,
                        modifier = Modifier.height(26.dp).testTag("btn_view_rx_pdf_${prescription.id}"),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
                    ) {
                        Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(10.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("View Rx PDF", fontSize = 9.sp)
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    if (prescription.dispensationStatus == "Dispensed / Ready") {
                        OutlinedButton(
                            onClick = onMarkCollected,
                            modifier = Modifier.height(26.dp).testTag("btn_collect_rx_${prescription.id}"),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(10.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("Collected", fontSize = 9.sp)
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// Dialog: Add Prescription
// -------------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPrescriptionDialog(
    defaultPatientName: String,
    defaultRegNumber: String,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String, String, String, String, String, String, String) -> Unit
) {
    var medName by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("500 mg") }
    var frequency by remember { mutableStateOf("1-0-1 (Twice Daily After Meals)") }
    var duration by remember { mutableStateOf("5 Days") }
    var instructions by remember { mutableStateOf("Take with warm water.") }
    var encounterType by remember { mutableStateOf("OPD Consultation") }
    var doctorName by remember { mutableStateOf("Dr. Rahul Sinha") }

    val encounters = listOf("OPD Consultation", "Inpatient Treatment", "Discharge Medication")
    var encExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Prescribe Pharmacy Medicine", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Patient: $defaultPatientName ($defaultRegNumber)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                OutlinedTextField(
                    value = medName,
                    onValueChange = { medName = it },
                    label = { Text("Medicine Name (e.g. Tab Augmentin)") },
                    modifier = Modifier.fillMaxWidth().testTag("input_rx_med_name")
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = dosage,
                        onValueChange = { dosage = it },
                        label = { Text("Dosage") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = duration,
                        onValueChange = { duration = it },
                        label = { Text("Duration") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = frequency,
                    onValueChange = { frequency = it },
                    label = { Text("Frequency (e.g. 1-0-1 After Meals)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = instructions,
                    onValueChange = { instructions = it },
                    label = { Text("Instructions") },
                    modifier = Modifier.fillMaxWidth()
                )

                ExposedDropdownMenuBox(
                    expanded = encExpanded,
                    onExpandedChange = { encExpanded = it }
                ) {
                    OutlinedTextField(
                        value = encounterType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Encounter Source") },
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
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (medName.isNotBlank()) {
                        onConfirm(
                            defaultRegNumber,
                            defaultPatientName,
                            doctorName,
                            "General Medicine",
                            encounterType,
                            medName,
                            dosage,
                            frequency,
                            duration,
                            instructions
                        )
                    }
                },
                enabled = medName.isNotBlank(),
                modifier = Modifier.testTag("btn_confirm_add_rx")
            ) {
                Text("Prescribe & Sync")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
