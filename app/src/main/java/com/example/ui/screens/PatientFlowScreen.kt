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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PatientRecord
import com.example.ui.viewmodel.HospitalViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientFlowScreen(
    viewModel: HospitalViewModel,
    modifier: Modifier = Modifier
) {
    val patients by viewModel.filteredPatients.collectAsState()
    val searchQuery by viewModel.patientSearchQuery.collectAsState()
    val filterStatus by viewModel.selectedFilterStatus.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var patientToDischarge by remember { mutableStateOf<PatientRecord?>(null) }

    Scaffold(
        modifier = modifier.testTag("patient_flow_screen"),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                modifier = Modifier.testTag("fab_add_patient")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Patient")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Screen Header
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Patient Flow & Inpatient Registry",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Track LOS, discharge bottlenecks, and clinical pathway compliance",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setPatientSearchQuery(it) },
                        placeholder = { Text("Search by name, IPD #, doctor, ward...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("patient_search_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    val tabs = listOf("All", "Inpatient", "Discharged")
                    val selectedTabIndex = tabs.indexOf(filterStatus).coerceAtLeast(0)
                    TabRow(
                        selectedTabIndex = selectedTabIndex,
                        containerColor = Color.Transparent
                    ) {
                        tabs.forEach { tabName ->
                            Tab(
                                selected = filterStatus == tabName,
                                onClick = { viewModel.setFilterStatus(tabName) },
                                text = { Text(tabName, fontSize = 13.sp, fontWeight = FontWeight.SemiBold) },
                                modifier = Modifier.testTag("tab_$tabName")
                            )
                        }
                    }
                }
            }

            // Patient List
            if (patients.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Hotel,
                            contentDescription = null,
                            tint = Color.LightGray,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No patient records found",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(patients, key = { it.id }) { patient ->
                        PatientCard(
                            patient = patient,
                            onDischargeClick = { patientToDischarge = patient }
                        )
                    }
                }
            }
        }

        // Add Patient Dialog
        if (showAddDialog) {
            AddPatientDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { name, ipd, age, gender, consultant, dept, admDate, notes ->
                    viewModel.addPatient(name, ipd, age, gender, consultant, dept, admDate, notes)
                    showAddDialog = false
                }
            )
        }

        // Discharge Dialog
        if (patientToDischarge != null) {
            DischargePatientDialog(
                patient = patientToDischarge!!,
                onDismiss = { patientToDischarge = null },
                onConfirm = { dischargeDate, los, delayReason, notes ->
                    viewModel.dischargePatient(patientToDischarge!!, dischargeDate, los, delayReason, notes)
                    patientToDischarge = null
                }
            )
        }
    }
}

@Composable
fun PatientCard(
    patient: PatientRecord,
    onDischargeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("patient_card_${patient.id}"),
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                if (patient.isDischarged) Color(0xFFDCFCE7) else Color(0xFFE0F2FE),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (patient.isDischarged) Icons.Default.CheckCircle else Icons.Default.Hotel,
                            contentDescription = null,
                            tint = if (patient.isDischarged) Color(0xFF16A34A) else Color(0xFF0284C7),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = patient.patientName,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${patient.ipdNumber} • ${patient.age}y / ${patient.gender}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (patient.isDischarged) Color(0xFFF1F5F9) else Color(0xFFFEF3C7)
                ) {
                    Text(
                        text = if (patient.isDischarged) "${patient.losDays} Days LOS" else "Active (Day ${patient.losDays + 1})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (patient.isDischarged) Color(0xFF334155) else Color(0xFFB45309),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Consultant:",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = patient.consultantName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Column {
                    Text(
                        text = "Department:",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = patient.department,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Column {
                    Text(
                        text = "Admission:",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = patient.admissionDate,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            if (!patient.delayReason.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = Color(0xFFFEE2E2),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color(0xFFDC2626),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Discharge Bottleneck: ${patient.delayReason}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF991B1B)
                        )
                    }
                }
            }

            if (patient.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = patient.notes,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (!patient.isDischarged) {
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onDischargeClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_discharge_${patient.id}"),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(imageVector = Icons.Default.HourglassTop, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Process Lean Discharge", fontSize = 12.sp)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPatientDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, ipd: String, age: Int, gender: String, consultant: String, dept: String, admDate: String, notes: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var ipd by remember { mutableStateOf("SGH-2026-${(100..999).random()}") }
    var ageStr by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Male") }
    var consultant by remember { mutableStateOf("Dr. Rahul Sinha") }
    var department by remember { mutableStateOf("General Medicine") }
    var notes by remember { mutableStateOf("") }

    val doctors = listOf(
        "Dr. Rahul Sinha",
        "Dr. M.S. Islam",
        "Dr. Rajnish Kumar",
        "Dr. Javed Akhtar",
        "Dr. Vivek Goswami",
        "Dr. Manjar Ali",
        "Emergency Dept"
    )

    val departments = listOf(
        "General Medicine",
        "General Surgery",
        "Orthopedics",
        "Gynecology & Obs",
        "Emergency",
        "Pediatrics",
        "ICU / Critical Care"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Inpatient Admission") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Patient Full Name") },
                    modifier = Modifier.fillMaxWidth().testTag("input_patient_name")
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = ipd,
                        onValueChange = { ipd = it },
                        label = { Text("IPD Number") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = ageStr,
                        onValueChange = { ageStr = it },
                        label = { Text("Age") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(0.6f).testTag("input_patient_age")
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { gender = if (gender == "Male") "Female" else "Male" },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Gender: $gender")
                    }
                }
                // Consultant selector
                var doctorExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = doctorExpanded,
                    onExpandedChange = { doctorExpanded = it }
                ) {
                    OutlinedTextField(
                        value = consultant,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Consultant") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = doctorExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = doctorExpanded,
                        onDismissRequest = { doctorExpanded = false }
                    ) {
                        doctors.forEach { doc ->
                            DropdownMenuItem(
                                text = { Text(doc) },
                                onClick = {
                                    consultant = doc
                                    doctorExpanded = false
                                }
                            )
                        }
                    }
                }

                // Department
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
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = deptExpanded,
                        onDismissRequest = { deptExpanded = false }
                    ) {
                        departments.forEach { d ->
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

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Diagnosis & Clinical Notes") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val age = ageStr.toIntOrNull() ?: 35
                    val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
                    onConfirm(name.ifBlank { "Patient" }, ipd, age, gender, consultant, department, today, notes)
                },
                modifier = Modifier.testTag("dialog_confirm_add_patient")
            ) {
                Text("Admit Patient")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DischargePatientDialog(
    patient: PatientRecord,
    onDismiss: () -> Unit,
    onConfirm: (dischargeDate: String, los: Int, delayReason: String?, notes: String) -> Unit
) {
    var losDays by remember { mutableIntStateOf(patient.losDays.coerceAtLeast(1)) }
    var selectedDelayReason by remember { mutableStateOf("None (Streamlined Discharge)") }
    var notes by remember { mutableStateOf("") }

    val delayReasons = listOf(
        "None (Streamlined Discharge)",
        "TPA Insurance Billing Reconciliation (180 min wait)",
        "Discharge Summary Doctor Signature Pending",
        "Pharmacy Medicine Return Clearance Lag",
        "Patient Attendant Delay / Transport Arrangement",
        "Awaiting Diagnostic Confirmation Test"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Discharge: ${patient.patientName}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Admitted on: ${patient.admissionDate} | Consultant: ${patient.consultantName}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Calculated LOS:", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedButton(
                            onClick = { if (losDays > 0) losDays-- },
                            modifier = Modifier.size(36.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                        ) { Text("-") }
                        Text(
                            "$losDays days",
                            modifier = Modifier.padding(horizontal = 12.dp),
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        OutlinedButton(
                            onClick = { losDays++ },
                            modifier = Modifier.size(36.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                        ) { Text("+") }
                    }
                }

                Text(
                    text = "Discharge Bottleneck / Delay Reason:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )

                var delayExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = delayExpanded,
                    onExpandedChange = { delayExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedDelayReason,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = delayExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = delayExpanded,
                        onDismissRequest = { delayExpanded = false }
                    ) {
                        delayReasons.forEach { reason ->
                            DropdownMenuItem(
                                text = { Text(reason, fontSize = 12.sp) },
                                onClick = {
                                    selectedDelayReason = reason
                                    delayExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Discharge summary remarks") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
                    val delay = if (selectedDelayReason.startsWith("None")) null else selectedDelayReason
                    onConfirm(today, losDays, delay, notes)
                },
                modifier = Modifier.testTag("dialog_confirm_discharge")
            ) {
                Text("Confirm Discharge")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
