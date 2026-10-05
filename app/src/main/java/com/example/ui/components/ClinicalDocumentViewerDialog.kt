package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.BloodLabReport
import com.example.data.model.PharmacyPrescription
import com.example.data.model.SampleLabDefinitions

sealed class ClinicalDocumentData {
    data class LabReport(val report: BloodLabReport) : ClinicalDocumentData()
    data class Prescription(val prescription: PharmacyPrescription) : ClinicalDocumentData()
    data class OpdPaper(
        val summary: com.example.data.model.PatientDiagnosisSummary,
        val medications: List<PharmacyPrescription> = emptyList()
    ) : ClinicalDocumentData()
}

@Composable
fun ClinicalDocumentViewerDialog(
    documentData: ClinicalDocumentData,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var zoomLevel by remember { mutableFloatStateOf(1.0f) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1E293B))
                .testTag("clinical_document_viewer_dialog"),
            color = Color(0xFF1E293B)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Action & Security Bar
                Surface(
                    color = Color(0xFF0F172A),
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = onDismiss, modifier = Modifier.size(36.dp)) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = when (documentData) {
                                            is ClinicalDocumentData.LabReport -> "Pathology Lab Slip PDF"
                                            is ClinicalDocumentData.Prescription -> "Clinical Rx Slip PDF"
                                            is ClinicalDocumentData.OpdPaper -> "OPD Consultation Paper PDF"
                                        },
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        color = Color(0xFF16A34A).copy(alpha = 0.25f),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF4ADE80), modifier = Modifier.size(10.dp))
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text("256-BIT ENCRYPTED EHR", color = Color(0xFF4ADE80), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                                Text(
                                    text = "ABDM / FHIR R4 Synchronized • Synergy Global Hospital",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 10.sp
                                )
                            }
                        }

                        // Zoom & Print Controls
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { if (zoomLevel > 0.8f) zoomLevel -= 0.15f },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.ZoomOut, contentDescription = "Zoom Out", tint = Color(0xFFCBD5E1), modifier = Modifier.size(18.dp))
                            }

                            Text(
                                text = "${(zoomLevel * 100).toInt()}%",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )

                            IconButton(
                                onClick = { if (zoomLevel < 1.4f) zoomLevel += 0.15f },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.ZoomIn, contentDescription = "Zoom In", tint = Color(0xFFCBD5E1), modifier = Modifier.size(18.dp))
                            }

                            IconButton(
                                onClick = { zoomLevel = 1.0f },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.RestartAlt, contentDescription = "Reset Zoom", tint = Color(0xFFCBD5E1), modifier = Modifier.size(18.dp))
                            }

                            Spacer(modifier = Modifier.width(4.dp))

                            IconButton(
                                onClick = {
                                    Toast.makeText(context, "Document downloaded & saved to Device Storage (PDF)", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Download, contentDescription = "Download PDF", tint = Color(0xFF38BDF8), modifier = Modifier.size(18.dp))
                            }

                            IconButton(
                                onClick = {
                                    Toast.makeText(context, "Sending document to Hospital Network Printer...", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Print, contentDescription = "Print Document", tint = Color(0xFFCBD5E1), modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }

                // Document Canvas Area (Scrollable A4 Paper view)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF334155))
                        .verticalScroll(rememberScrollState())
                        .horizontalScroll(rememberScrollState())
                        .padding(16.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Box(
                        modifier = Modifier
                            .scale(zoomLevel)
                            .width(620.dp)
                            .background(Color.White, RoundedCornerShape(2.dp))
                            .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(2.dp))
                            .padding(24.dp)
                    ) {
                        // Watermark Canvas Background
                        Canvas(modifier = Modifier.matchParentSize()) {
                            // Draw simulated watermark line
                        }

                        Column {
                            // Hospital Header & Letterhead
                            HospitalOfficialLetterhead()

                            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), thickness = 2.dp, color = Color(0xFF0F172A))

                            when (documentData) {
                                is ClinicalDocumentData.LabReport -> {
                                    LabReportDocumentBody(report = documentData.report)
                                }
                                is ClinicalDocumentData.Prescription -> {
                                    PrescriptionDocumentBody(prescription = documentData.prescription)
                                }
                                is ClinicalDocumentData.OpdPaper -> {
                                    OpdPaperDocumentBody(summary = documentData.summary, medications = documentData.medications)
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // Official Footer & ABDM Security Stamp
                            ClinicalDocumentFooter()
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// Hospital Letterhead
// -------------------------------------------------------------------------
@Composable
fun HospitalOfficialLetterhead() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                color = Color(0xFF0F172A),
                shape = CircleShape,
                modifier = Modifier.size(46.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.LocalHospital,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "SYNERGY GLOBAL HOSPITAL",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF0F172A),
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Tertiary Care & Multi-Specialty Research Institute",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF475569)
                )
                Text(
                    text = "Bariatu Medical Enclave, Ranchi, Jharkhand • Tel: +91 651 2489000",
                    fontSize = 9.sp,
                    color = Color(0xFF64748B)
                )
            }
        }

        Column(horizontalAlignment = Alignment.End) {
            Surface(
                color = Color(0xFFF1F5F9),
                shape = RoundedCornerShape(4.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1))
            ) {
                Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), horizontalAlignment = Alignment.End) {
                    Text("NABL ACCREDITED LAB", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                    Text("NABH ISO 15189:2022", fontSize = 7.sp, color = Color(0xFF64748B))
                    Text("ABDM HFR: JH-SGH-0091", fontSize = 7.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2563EB))
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// Lab Report Document Body
// -------------------------------------------------------------------------
@Composable
fun LabReportDocumentBody(report: BloodLabReport) {
    val isAbnormal = report.overallStatus != "Normal"
    val parameters = SampleLabDefinitions.getParametersForCategory(report.testCategory, isAbnormal)

    // Patient & Test Metadata Box
    Surface(
        color = Color(0xFFF8FAFC),
        shape = RoundedCornerShape(4.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Patient Name: ${report.patientName}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                    Text("UHID / Reg No: ${report.hospitalRegNumber}", fontSize = 10.sp, color = Color(0xFF334155), fontFamily = FontFamily.Monospace)
                    Text("Department: ${report.department}", fontSize = 10.sp, color = Color(0xFF475569))
                }
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                    Text("Sample Date: ${report.sampleDate}", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F172A))
                    Text("Encounter: ${report.encounterType}", fontSize = 10.sp, color = Color(0xFF2563EB), fontWeight = FontWeight.Bold)
                    Text("Ref Doctor: ${report.doctorName}", fontSize = 10.sp, color = Color(0xFF475569))
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            HorizontalDivider(color = Color(0xFFE2E8F0))
            Spacer(modifier = Modifier.height(4.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                BarcodeLinesSimulation(barcode = report.labBarcode)
                Text("Lab Accession: ${report.labBarcode}", fontSize = 9.sp, fontFamily = FontFamily.Monospace, color = Color(0xFF64748B))
            }
        }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Department Header Banner
    Surface(
        color = Color(0xFF0F172A),
        shape = RoundedCornerShape(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "DEPARTMENT OF PATHOLOGY & BIOCHEMISTRY: ${report.testCategory.uppercase()}",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Surface(
                color = if (isAbnormal) Color(0xFFEF4444) else Color(0xFF22C55E),
                shape = RoundedCornerShape(2.dp)
            ) {
                Text(
                    text = report.overallStatus.uppercase(),
                    color = Color.White,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Tabular Analyte Parameters
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            // Table Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF1F5F9))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Investigation Analyte", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A), modifier = Modifier.weight(2f))
                Text("Observed Result", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A), modifier = Modifier.weight(1.2f))
                Text("Biological Ref Range", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A), modifier = Modifier.weight(1.2f))
                Text("Flag", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A), textAlign = TextAlign.End, modifier = Modifier.weight(0.6f))
            }

            HorizontalDivider(color = Color(0xFFCBD5E1))

            parameters.forEachIndexed { index, param ->
                val isParamAbnormal = param.flag != "NORMAL"
                val rowBg = if (isParamAbnormal) Color(0xFFFEF2F2) else if (index % 2 == 0) Color.White else Color(0xFFF8FAFC)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(rowBg)
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(param.name, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1E293B), modifier = Modifier.weight(2f))
                    Text(
                        text = "${param.resultValue} ${param.unit}",
                        fontSize = 10.sp,
                        fontWeight = if (isParamAbnormal) FontWeight.Bold else FontWeight.Normal,
                        color = if (isParamAbnormal) Color(0xFFDC2626) else Color(0xFF0F172A),
                        modifier = Modifier.weight(1.2f)
                    )
                    Text(
                        text = "${param.referenceRange} ${param.unit}",
                        fontSize = 9.sp,
                        color = Color(0xFF475569),
                        modifier = Modifier.weight(1.2f)
                    )
                    Text(
                        text = param.flag,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isParamAbnormal) Color(0xFFDC2626) else Color(0xFF16A34A),
                        textAlign = TextAlign.End,
                        modifier = Modifier.weight(0.6f)
                    )
                }

                if (index < parameters.lastIndex) {
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Interpretation Note
    Surface(
        color = Color(0xFFF8FAFC),
        shape = RoundedCornerShape(4.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text("Clinical Interpretation & Notes:", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            Text(
                text = if (isAbnormal) {
                    "Findings indicate elevated laboratory markers requiring clinical correlation. Follow-up electrolyte review and clinical re-evaluation advised within 48 hours."
                } else {
                    "All investigated parameters within physiological reference intervals. Normal baseline report."
                },
                fontSize = 9.sp,
                color = Color(0xFF475569),
                lineHeight = 13.sp
            )
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Signatures Block
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Column {
            Text("Report Verified Electronically by:", fontSize = 8.sp, color = Color(0xFF64748B))
            Text(report.verifiedBy, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            Text("Chief Pathologist & Laboratory Director", fontSize = 8.sp, color = Color(0xFF64748B))
            Text("Reg No: NABL-PATH-98402", fontSize = 8.sp, fontFamily = FontFamily.Monospace, color = Color(0xFF94A3B8))
        }

        // Circular Digital Stamp
        DigitalValidationStamp(label = "LABORATORY VALIDATED")
    }
}

// -------------------------------------------------------------------------
// Prescription Document Body
// -------------------------------------------------------------------------
@Composable
fun PrescriptionDocumentBody(prescription: PharmacyPrescription) {
    // Patient Metadata Box
    Surface(
        color = Color(0xFFF8FAFC),
        shape = RoundedCornerShape(4.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Patient: ${prescription.patientName}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                    Text("UHID / Reg No: ${prescription.hospitalRegNumber}", fontSize = 10.sp, color = Color(0xFF334155), fontFamily = FontFamily.Monospace)
                    Text("Consultant: ${prescription.doctorName}", fontSize = 10.sp, color = Color(0xFF0F172A), fontWeight = FontWeight.SemiBold)
                }
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                    Text("Rx Date: ${prescription.datePrescribed}", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F172A))
                    Text("Encounter: ${prescription.encounterType}", fontSize = 10.sp, color = Color(0xFF2563EB), fontWeight = FontWeight.Bold)
                    Text("Rx ID: ${prescription.rxNumber}", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = Color(0xFF64748B))
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Medical Rx Symbol Header
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("℞", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A), fontFamily = FontFamily.Serif)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "CLINICAL PRESCRIPTION & MEDICATION ORDER",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A),
            letterSpacing = 0.5.sp
        )
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Prescribed Drug Card
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(4.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF0F172A)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "1. ${prescription.medicineName}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "Dosage: ${prescription.dosage} • Route: Oral",
                        fontSize = 11.sp,
                        color = Color(0xFF334155),
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Surface(
                    color = Color(0xFFF1F5F9),
                    shape = RoundedCornerShape(4.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1))
                ) {
                    Text(
                        text = prescription.dispensationStatus.uppercase(),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                color = Color(0xFFF8FAFC),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Dosing Frequency: ${prescription.frequency}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2563EB))
                    Text("Duration: ${prescription.duration}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                }
            }

            if (prescription.instructions.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Special Instructions:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569))
                Text(prescription.instructions, fontSize = 10.sp, color = Color(0xFF1E293B))
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = Color(0xFFE2E8F0))
            Spacer(modifier = Modifier.height(6.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Dispensing Location: ${prescription.pharmacyCounter}", fontSize = 9.sp, color = Color(0xFF64748B))
                Text("Batch: SG-2026-B81 • Exp: 2028-09", fontSize = 9.sp, fontFamily = FontFamily.Monospace, color = Color(0xFF64748B))
            }
        }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Doctor & Pharmacist Sign-Off
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Column {
            Text("Prescribing Physician:", fontSize = 8.sp, color = Color(0xFF64748B))
            Text(prescription.doctorName, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            Text("MD (Internal Medicine) • Reg: MCI-2014-98124", fontSize = 8.sp, color = Color(0xFF475569))
            Text("Synergy Global Hospital Medical Staff", fontSize = 8.sp, color = Color(0xFF94A3B8))
        }

        DigitalValidationStamp(label = "PHARMACY DISPENSED")
    }
}

// -------------------------------------------------------------------------
// Helper Components: Digital Stamp & Barcode
// -------------------------------------------------------------------------
@Composable
fun DigitalValidationStamp(label: String) {
    Surface(
        color = Color(0xFFF0FDF4),
        shape = CircleShape,
        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF16A34A)),
        modifier = Modifier.size(68.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.Verified, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(16.dp))
            Text("EHR VALID", fontSize = 7.sp, fontWeight = FontWeight.Bold, color = Color(0xFF166534))
            Text(label, fontSize = 5.sp, textAlign = TextAlign.Center, color = Color(0xFF15803D), maxLines = 2)
        }
    }
}

@Composable
fun BarcodeLinesSimulation(barcode: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(1.5.dp)) {
        val widths = listOf(1, 2, 1, 3, 1, 2, 1, 1, 3, 2, 1, 2, 1, 3, 1, 2)
        widths.forEach { w ->
            Box(
                modifier = Modifier
                    .width(w.dp)
                    .height(14.dp)
                    .background(Color(0xFF0F172A))
            )
        }
    }
}

@Composable
fun ClinicalDocumentFooter() {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        HorizontalDivider(color = Color(0xFFCBD5E1), thickness = 1.dp)
        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "ABDM Certified Electronic Health Record • End-to-End Encrypted",
                    fontSize = 8.sp,
                    color = Color(0xFF64748B)
                )
            }

            Text(
                text = "Page 1 of 1 • Official Hospital Copy",
                fontSize = 8.sp,
                color = Color(0xFF94A3B8)
            )
        }

        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "This document is electronically verified through Synergy Global Hospital EHR Gateway. Any tampering or unauthorized alteration is punishable under IT Act 2000.",
            fontSize = 7.sp,
            color = Color(0xFF94A3B8),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun OpdPaperDocumentBody(
    summary: com.example.data.model.PatientDiagnosisSummary,
    medications: List<PharmacyPrescription>
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "OUTPATIENT CONSULTATION RECORD (OPD)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF0F172A)
                )
                Text("Department: ${summary.department}", fontSize = 10.sp, color = Color(0xFF475569))
            }
            Surface(
                color = Color(0xFFF1F5F9),
                shape = RoundedCornerShape(4.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1))
            ) {
                Text(
                    text = "ICD-10: ${summary.icd10Code}",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Demographics Box
        Surface(
            color = Color(0xFFF8FAFC),
            shape = RoundedCornerShape(4.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Patient: ${summary.patientName}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                    Text("UHID: ${summary.hospitalRegNumber}", fontSize = 9.sp, color = Color(0xFF475569))
                }
                Column {
                    Text("Date: ${summary.encounterDate}", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F172A))
                    Text("Doctor: ${summary.doctorName}", fontSize = 9.sp, color = Color(0xFF475569))
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Vitals
        Text("RECORDED CLINICAL VITALS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
        Spacer(modifier = Modifier.height(4.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Surface(color = Color(0xFFF1F5F9), shape = RoundedCornerShape(4.dp), modifier = Modifier.weight(1f)) {
                Column(modifier = Modifier.padding(6.dp)) {
                    Text("Blood Pressure", fontSize = 8.sp, color = Color.Gray)
                    Text(summary.bloodPressure, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                }
            }
            Surface(color = Color(0xFFF1F5F9), shape = RoundedCornerShape(4.dp), modifier = Modifier.weight(1f)) {
                Column(modifier = Modifier.padding(6.dp)) {
                    Text("Pulse", fontSize = 8.sp, color = Color.Gray)
                    Text(summary.pulseRate, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                }
            }
            Surface(color = Color(0xFFF1F5F9), shape = RoundedCornerShape(4.dp), modifier = Modifier.weight(1f)) {
                Column(modifier = Modifier.padding(6.dp)) {
                    Text("SpO2", fontSize = 8.sp, color = Color.Gray)
                    Text(summary.spo2, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                }
            }
            Surface(color = Color(0xFFF1F5F9), shape = RoundedCornerShape(4.dp), modifier = Modifier.weight(1f)) {
                Column(modifier = Modifier.padding(6.dp)) {
                    Text("Temp", fontSize = 8.sp, color = Color.Gray)
                    Text(summary.temperature, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Diagnosis & Complaints
        Text("CLINICAL EVALUATION & DIAGNOSIS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
        Spacer(modifier = Modifier.height(4.dp))
        Surface(color = Color(0xFFF8FAFC), shape = RoundedCornerShape(4.dp), border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Primary Diagnosis: ${summary.primaryDiagnosis}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                Text("Chief Complaints: ${summary.chiefComplaints}", fontSize = 9.sp, color = Color(0xFF475569))
                Text("Clinical Advice: ${summary.clinicalAdvice}", fontSize = 9.sp, color = Color(0xFF334155))
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Prescriptions List
        if (medications.isNotEmpty()) {
            Text("Rx - PRESCRIBED MEDICATIONS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            Spacer(modifier = Modifier.height(4.dp))
            medications.forEachIndexed { idx, m ->
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(4.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1)),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("${idx + 1}. ${m.medicineName} (${m.dosage})", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                            Text("${m.frequency} • ${m.duration} • ${m.instructions}", fontSize = 8.sp, color = Color(0xFF475569))
                        }
                        Text(m.dispensationStatus, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Physician Signature & Digital Stamp
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Surface(
                color = Color(0xFFF1F5F9),
                shape = RoundedCornerShape(4.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1))
            ) {
                Row(modifier = Modifier.padding(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Verified, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("OPD CONSULTATION ELECTRONICALLY SEALED", fontSize = 7.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text("Dr. Rahul Sinha, MD", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                Text("Reg. No: MCI-2011-84920", fontSize = 8.sp, color = Color(0xFF64748B))
                Text("Senior Consultant Physician", fontSize = 8.sp, color = Color(0xFF64748B))
            }
        }
    }
}
