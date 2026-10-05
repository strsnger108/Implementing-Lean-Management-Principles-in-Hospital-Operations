package com.example.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FamilyMember

/**
 * Modular Quick Demo Auth Bar & ABDM Compliance Footer
 */
@Composable
fun QuickDemoAuthBar(
    onQuickPatientLogin: () -> Unit,
    onQuickDoctorLogin: () -> Unit,
    onQuickNurseLogin: () -> Unit,
    onQuickAdminLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Instant 1-Tap Demo Logins",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Tap to immediately authenticate as pre-configured clinical, patient, or administrative accounts:",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Patient Demo
                OutlinedButton(
                    onClick = onQuickPatientLogin,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().testTag("quick_patient_demo_btn")
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("👤 Patient: Gunjan Prakash", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF166534))
                        Text("UHID: SGH-2026-0288 • Linked Dependents: Bimla Devi & Rajesh Gope", fontSize = 10.sp, color = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Doctor Staff Demo
                OutlinedButton(
                    onClick = onQuickDoctorLogin,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().testTag("quick_staff_doctor_demo_btn")
                ) {
                    Icon(Icons.Default.MedicalServices, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("🩺 Doctor: Dr. Rahul Sinha (MD)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E40AF))
                        Text("Staff ID: EMP-DOC-102 • OPD Chamber Queue Calling & Inpatient Rounds", fontSize = 10.sp, color = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Nurse Staff Demo
                OutlinedButton(
                    onClick = onQuickNurseLogin,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().testTag("quick_staff_nurse_demo_btn")
                ) {
                    Icon(Icons.Default.Badge, contentDescription = null, tint = Color(0xFF0284C7), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("👩‍⚕️ Nurse: Priya Sharma (RN)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0369A1))
                        Text("Station 3B • Real-time Bedside Nurse Call & Daily Ledger Logger", fontSize = 10.sp, color = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Admin Demo
                OutlinedButton(
                    onClick = onQuickAdminLogin,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().testTag("quick_admin_demo_btn")
                ) {
                    Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = Color(0xFF7C3AED), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("🛡️ Administrator: Lean Operations Director", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF5B21B6))
                        Text("Admin ID: ADM-SGH-01 • Executive Lean Command, 5S & ABDM Gateways", fontSize = 10.sp, color = Color.Gray)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Interoperability & Compliance Footer
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Sync, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Ayushman Bharat Digital Mission (ABDM) Compliant",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "• Standardized on HL7 FHIR R4 Health Repository Gateway.\n• Hospital Information System (HMS) real-time interoperability.\n• 256-bit HIPAA and NABH compliant clinical encryption.",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
