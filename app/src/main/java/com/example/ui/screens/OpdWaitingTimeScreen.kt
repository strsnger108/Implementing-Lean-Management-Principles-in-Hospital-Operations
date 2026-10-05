package com.example.ui.screens

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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
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
import com.example.data.model.OpdQueueItem
import com.example.data.model.PatientOpdToken
import com.example.ui.components.KpiCard
import com.example.ui.viewmodel.HospitalViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpdWaitingTimeScreen(
    viewModel: HospitalViewModel,
    modifier: Modifier = Modifier
) {
    val activeSession by viewModel.activeUserSession.collectAsState()
    val myTokens by viewModel.myOpdTokens.collectAsState()
    val familyMembers by viewModel.familyMembers.collectAsState()

    var selectedDeptFilter by remember { mutableStateOf("All") }
    var showBookTokenDialog by remember { mutableStateOf(false) }

    val hospitalCode = activeSession?.hospitalCode ?: "SGH-RANCHI"
    val hospitalName = activeSession?.hospitalName ?: "Synergy Global Hospital"

    val clinics = viewModel.opdClinics.filter { clinic ->
        selectedDeptFilter == "All" || clinic.department.equals(selectedDeptFilter, ignoreCase = true)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("opd_waiting_time_screen")
    ) {
        // Header
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Live OPD Queue & Waiting Times",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = "Auto-synced with HMS Token Display • $hospitalCode",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Surface(
                        color = Color(0xFFDCFCE7),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.size(6.dp).background(Color(0xFF16A34A), CircleShape))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("HMS Live", fontSize = 10.sp, color = Color(0xFF166534), fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Dept Filters
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("All", "General Medicine", "General Surgery", "Orthopedics", "Obstetrics & Gyn", "Pediatrics", "Critical Care & ICU").forEach { dept ->
                        FilterChip(
                            selected = selectedDeptFilter == dept,
                            onClick = { selectedDeptFilter = dept },
                            label = { Text(dept, fontSize = 10.sp) },
                            modifier = Modifier.testTag("filter_opd_${dept.replace(" ", "_")}")
                        )
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Patient's Own Live Token Card (if any)
            if (myTokens.isNotEmpty()) {
                item {
                    Text(
                        text = "My Active OPD Queue Tokens",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }

                items(myTokens, key = { it.id }) { token ->
                    ActiveTokenCard(token = token)
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Department Clinics & Estimated Wait",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    OutlinedButton(
                        onClick = { showBookTokenDialog = true },
                        modifier = Modifier.height(30.dp).testTag("btn_book_opd_token"),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Get OPD Token", fontSize = 10.sp)
                    }
                }
            }

            // Department Clinics Cards
            items(clinics, key = { it.doctorName }) { clinic ->
                OpdClinicCard(
                    clinic = clinic,
                    onGetToken = {
                        val patientName = activeSession?.fullName ?: "Gunjan Prakash"
                        viewModel.bookOpdToken(
                            patientName = patientName,
                            doctorName = clinic.doctorName,
                            department = clinic.department,
                            roomNumber = clinic.roomNumber
                        )
                    }
                )
            }

            // Lean OPD Queue Bottleneck Insight
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.HourglassBottom, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Lean OPD Throughput & Waiting Time Telemetry",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Peak congestion occurs between 10:00 AM and 12:30 PM (Dr. Rahul Sinha averaging 48 min wait). Staggered 15-minute appointment slots and live HMS queue broadcast reduce waiting room congestion by 35%.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }

    if (showBookTokenDialog) {
        BookOpdTokenDialog(
            clinics = viewModel.opdClinics,
            familyMembers = familyMembers,
            activeUserName = activeSession?.fullName ?: "Gunjan Prakash",
            onDismiss = { showBookTokenDialog = false },
            onConfirm = { patientName, clinic ->
                viewModel.bookOpdToken(
                    patientName = patientName,
                    doctorName = clinic.doctorName,
                    department = clinic.department,
                    roomNumber = clinic.roomNumber
                )
                showBookTokenDialog = false
            }
        )
    }
}

@Composable
fun ActiveTokenCard(token: PatientOpdToken) {
    val remainingPatients = (token.tokenNumber - token.currentServingToken).coerceAtLeast(0)

    val waitColor = when {
        token.estWaitMinutes <= 15 -> Color(0xFF16A34A) // Green
        token.estWaitMinutes <= 35 -> Color(0xFFD97706) // Amber
        else -> Color(0xFFDC2626) // Red
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("active_token_card_${token.tokenNumber}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
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
                            .background(MaterialTheme.colorScheme.primary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "#${token.tokenNumber}",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Patient: ${token.patientName}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${token.department} • ${token.roomNumber}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    color = waitColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Est. Wait: ${token.estWaitMinutes} mins",
                        color = waitColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Current Serving:", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Token #${token.currentServingToken}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Ahead of You:", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("$remainingPatients patients", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text("Consultant:", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(token.doctorName, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }
    }
}

@Composable
fun OpdClinicCard(
    clinic: OpdQueueItem,
    onGetToken: () -> Unit
) {
    val waitColor = when {
        clinic.estimatedWaitMinutes <= 15 -> Color(0xFF16A34A)
        clinic.estimatedWaitMinutes <= 35 -> Color(0xFFD97706)
        else -> Color(0xFFDC2626)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("clinic_card_${clinic.doctorName.replace(" ", "_")}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = clinic.doctorName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${clinic.department} • ${clinic.roomNumber}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Estimated Wait Pill
                Surface(
                    color = waitColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "${clinic.estimatedWaitMinutes} min wait",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = waitColor
                        )
                        Text(
                            text = clinic.status,
                            fontSize = 9.sp,
                            color = waitColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Now in Room: ",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "#${clinic.currentServingToken}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Total Issued: #${clinic.totalTokensIssued}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                OutlinedButton(
                    onClick = onGetToken,
                    modifier = Modifier.height(28.dp).testTag("btn_get_token_${clinic.doctorName.replace(" ", "_")}"),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
                ) {
                    Text("Get Token", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// Dialog: Book / Generate OPD Token
// -------------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookOpdTokenDialog(
    clinics: List<OpdQueueItem>,
    familyMembers: List<FamilyMember>,
    activeUserName: String,
    onDismiss: () -> Unit,
    onConfirm: (String, OpdQueueItem) -> Unit
) {
    val patientOptions = listOf(activeUserName) + familyMembers.map { "${it.fullName} (${it.relation})" }
    var selectedPatientName by remember { mutableStateOf(patientOptions.first()) }
    var selectedClinic by remember { mutableStateOf(clinics.first()) }

    var patientDropdownExpanded by remember { mutableStateOf(false) }
    var clinicDropdownExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Generate Live OPD Token", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Select who this OPD token is for (Self or linked family member) and choose the consultation clinic.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Select Family Member / Self
                ExposedDropdownMenuBox(
                    expanded = patientDropdownExpanded,
                    onExpandedChange = { patientDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedPatientName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Patient") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = patientDropdownExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = patientDropdownExpanded,
                        onDismissRequest = { patientDropdownExpanded = false }
                    ) {
                        patientOptions.forEach { p ->
                            DropdownMenuItem(text = { Text(p) }, onClick = { selectedPatientName = p; patientDropdownExpanded = false })
                        }
                    }
                }

                // Select Doctor / Clinic
                ExposedDropdownMenuBox(
                    expanded = clinicDropdownExpanded,
                    onExpandedChange = { clinicDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = "${selectedClinic.doctorName} (${selectedClinic.department})",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Consultant & Department") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = clinicDropdownExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = clinicDropdownExpanded,
                        onDismissRequest = { clinicDropdownExpanded = false }
                    ) {
                        clinics.forEach { c ->
                            DropdownMenuItem(
                                text = { Text("${c.doctorName} - ${c.department} (${c.estimatedWaitMinutes} min wait)") },
                                onClick = { selectedClinic = c; clinicDropdownExpanded = false }
                            )
                        }
                    }
                }

                Surface(
                    color = Color(0xFFF0FDF4),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("Room: ${selectedClinic.roomNumber}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF166534))
                        Text("Current Serving: Token #${selectedClinic.currentServingToken}", fontSize = 10.sp, color = Color(0xFF15803D))
                        Text("Estimated Wait: ~${selectedClinic.estimatedWaitMinutes} minutes", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF15803D))
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedPatientName.substringBefore(" ("), selectedClinic) },
                modifier = Modifier.testTag("btn_confirm_token_booking")
            ) {
                Text("Confirm Token")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
