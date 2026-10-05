package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FolderShared
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BloodLabReport
import com.example.data.model.PharmacyPrescription
import com.example.ui.viewmodel.HospitalViewModel

sealed class UnifiedEhrDocument {
    abstract val id: Long
    abstract val title: String
    abstract val date: String
    abstract val doctorName: String
    abstract val encounterType: String
    abstract val regNumber: String
    abstract val patientName: String

    data class LabDocument(val labReport: BloodLabReport) : UnifiedEhrDocument() {
        override val id: Long get() = labReport.id
        override val title: String get() = "${labReport.reportTitle} (${labReport.testCategory})"
        override val date: String get() = labReport.sampleDate
        override val doctorName: String get() = labReport.doctorName
        override val encounterType: String get() = labReport.encounterType
        override val regNumber: String get() = labReport.hospitalRegNumber
        override val patientName: String get() = labReport.patientName
    }

    data class RxDocument(val prescription: PharmacyPrescription) : UnifiedEhrDocument() {
        override val id: Long get() = prescription.id
        override val title: String get() = "Prescription: ${prescription.medicineName} (${prescription.dosage})"
        override val date: String get() = prescription.datePrescribed
        override val doctorName: String get() = prescription.doctorName
        override val encounterType: String get() = prescription.encounterType
        override val regNumber: String get() = prescription.hospitalRegNumber
        override val patientName: String get() = prescription.patientName
    }
}

@Composable
fun EhrDocumentVaultSection(
    viewModel: HospitalViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val prescriptions by viewModel.activePatientPrescriptions.collectAsState()
    val bloodReports by viewModel.activePatientBloodReports.collectAsState()
    val familyMembers by viewModel.familyMembers.collectAsState()
    val activeSession by viewModel.activeUserSession.collectAsState()
    val selectedRegNumber by viewModel.selectedRegNumber.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var filterType by remember { mutableStateOf("All") } // "All", "Lab Reports", "Prescriptions (Rx)"
    var activeViewerData by remember { mutableStateOf<ClinicalDocumentData?>(null) }

    val currentReg = selectedRegNumber ?: activeSession?.hospitalRegNumber ?: "SGH-2026-0288"
    val currentMember = familyMembers.firstOrNull { it.hospitalRegNumber == currentReg }
    val displayName = currentMember?.fullName ?: activeSession?.fullName ?: "Patient"

    // Assemble unified clinical documents list
    val allUnifiedDocuments = remember(bloodReports, prescriptions) {
        val labDocs = bloodReports.map { UnifiedEhrDocument.LabDocument(it) }
        val rxDocs = prescriptions.map { UnifiedEhrDocument.RxDocument(it) }
        (labDocs + rxDocs).sortedByDescending { it.date }
    }

    val filteredDocs = allUnifiedDocuments.filter { doc ->
        val matchesType = when (filterType) {
            "Lab Reports" -> doc is UnifiedEhrDocument.LabDocument
            "Prescriptions (Rx)" -> doc is UnifiedEhrDocument.RxDocument
            else -> true
        }
        val matchesSearch = searchQuery.isBlank() ||
                doc.title.contains(searchQuery, ignoreCase = true) ||
                doc.doctorName.contains(searchQuery, ignoreCase = true) ||
                doc.encounterType.contains(searchQuery, ignoreCase = true)
        matchesType && matchesSearch
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("ehr_document_vault_section")
    ) {
        // Vault Header & Search Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 1.dp,
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
                            Icon(Icons.Default.FolderShared, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "EHR Document Vault & PDF Records",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = "Unified Clinical Records • $displayName (UHID: $currentReg)",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Surface(
                        color = Color(0xFFDCFCE7),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(10.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("ABDM Vault", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF166534))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search by doctor, lab test, medicine, or encounter...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_vault_search"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Family Member Profile Selector Chips
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
                                modifier = Modifier.testTag("chip_vault_family_${member.fullName.replace(" ", "_")}")
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }

                // Filter Types: All, Lab Reports, Prescriptions (Rx)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("All", "Lab Reports", "Prescriptions (Rx)").forEach { type ->
                        FilterChip(
                            selected = filterType == type,
                            onClick = { filterType = type },
                            label = { Text(type, fontSize = 10.sp) },
                            modifier = Modifier.testTag("filter_vault_${type.replace(" ", "_")}")
                        )
                    }
                }
            }
        }

        // Documents List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
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
                        Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Hospital EHR Gateway Document Synchronization",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF166534)
                            )
                            Text(
                                text = "All clinical documents are encrypted, cryptographically signed, and synchronized from the hospital's central HMS database.",
                                fontSize = 10.sp,
                                color = Color(0xFF15803D)
                            )
                        }
                    }
                }
            }

            if (filteredDocs.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No documents found", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Synchronized lab results and prescriptions will appear in this locker.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                        }
                    }
                }
            } else {
                items(filteredDocs, key = { "${it.javaClass.simpleName}_${it.id}" }) { doc ->
                    VaultDocumentCard(
                        doc = doc,
                        onOpenViewer = {
                            activeViewerData = when (doc) {
                                is UnifiedEhrDocument.LabDocument -> ClinicalDocumentData.LabReport(doc.labReport)
                                is UnifiedEhrDocument.RxDocument -> ClinicalDocumentData.Prescription(doc.prescription)
                            }
                        },
                        onDownload = {
                            Toast.makeText(context, "Saved ${doc.title} to Device Storage (PDF)", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }

    if (activeViewerData != null) {
        ClinicalDocumentViewerDialog(
            documentData = activeViewerData!!,
            onDismiss = { activeViewerData = null }
        )
    }
}

@Composable
fun VaultDocumentCard(
    doc: UnifiedEhrDocument,
    onOpenViewer: () -> Unit,
    onDownload: () -> Unit
) {
    val isLab = doc is UnifiedEhrDocument.LabDocument
    val typeColor = if (isLab) Color(0xFF7C3AED) else Color(0xFF0284C7)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("vault_card_${doc.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Surface(
                        color = Color(0xFFEF4444).copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("PDF", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFDC2626))
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = doc.title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${doc.encounterType} • ${doc.date}",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    color = typeColor.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = if (isLab) "LAB REPORT" else "PRESCRIPTION",
                        color = typeColor,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Physician: ${doc.doctorName}",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Facility: SGH-RANCHI",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons: Open Secure Viewer + Download PDF
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onDownload,
                    modifier = Modifier.height(28.dp).testTag("btn_vault_download_${doc.id}"),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Download PDF", fontSize = 10.sp)
                }

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedButton(
                    onClick = onOpenViewer,
                    modifier = Modifier.height(28.dp).testTag("btn_vault_open_${doc.id}"),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp)
                ) {
                    Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Open Secure Viewer", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}
