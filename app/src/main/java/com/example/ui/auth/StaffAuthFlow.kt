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
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Modular Authentication Component for Clinical & Nursing Staff
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffAuthFlow(
    onStaffLogin: (identifier: String, fullName: String, staffId: String, department: String, designation: String, isSignUp: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var isSignUpMode by remember { mutableStateOf(false) }

    var staffIdentifier by remember { mutableStateOf("dr.sinha@synergy.org") }
    var staffName by remember { mutableStateOf("Dr. Rahul Sinha") }
    var staffId by remember { mutableStateOf("EMP-DOC-102") }
    var staffDesignation by remember { mutableStateOf("Consultant Physician (MD)") }
    var staffDepartment by remember { mutableStateOf("General Medicine") }
    var staffPasscode by remember { mutableStateOf("••••••••") }

    val primaryColor = Color(0xFF2563EB) // Royal Blue for Staff

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row with Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isSignUpMode) "Staff Roster Onboarding" else "Clinical Staff Authentication",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor
                    )
                    Text(
                        text = "Physician, Nursing & Pharmacy Station Access",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                TextButton(
                    onClick = { isSignUpMode = !isSignUpMode },
                    modifier = Modifier.testTag("btn_toggle_staff_auth_mode")
                ) {
                    Text(if (isSignUpMode) "Sign In" else "Sign Up", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (isSignUpMode) {
                OutlinedTextField(
                    value = staffName,
                    onValueChange = { staffName = it },
                    label = { Text("Staff Full Name with Qualifications") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = primaryColor) },
                    modifier = Modifier.fillMaxWidth().testTag("input_staff_name"),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Staff Clinical Designation Dropdown
            var desigExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = desigExpanded,
                onExpandedChange = { desigExpanded = it }
            ) {
                OutlinedTextField(
                    value = staffDesignation,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Clinical Role / Designation") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = desigExpanded) },
                    leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = primaryColor) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = desigExpanded,
                    onDismissRequest = { desigExpanded = false }
                ) {
                    listOf(
                        "Consultant Physician (MD)",
                        "General Surgeon (MS)",
                        "Orthopedic Surgeon",
                        "Inpatient Staff Nurse (RN)",
                        "ICU In-Charge Specialist",
                        "Inpatient Hospital Pharmacist",
                        "Ward Station Coordinator"
                    ).forEach { d ->
                        DropdownMenuItem(
                            text = { Text(d, fontSize = 12.sp) },
                            onClick = {
                                staffDesignation = d
                                desigExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Clinical Department Dropdown
            var deptExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = deptExpanded,
                onExpandedChange = { deptExpanded = it }
            ) {
                OutlinedTextField(
                    value = staffDepartment,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Clinical Department / Station") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = deptExpanded) },
                    leadingIcon = { Icon(Icons.Default.MedicalServices, contentDescription = null, tint = primaryColor) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = deptExpanded,
                    onDismissRequest = { deptExpanded = false }
                ) {
                    listOf(
                        "General Medicine",
                        "General Surgery",
                        "Orthopedics",
                        "Critical Care & ICU",
                        "Emergency & Triage",
                        "Obstetrics & Gynecology",
                        "Pediatrics",
                        "Central Pharmacy",
                        "Ward 3B (Internal Medicine)"
                    ).forEach { dept ->
                        DropdownMenuItem(
                            text = { Text(dept, fontSize = 12.sp) },
                            onClick = {
                                staffDepartment = dept
                                deptExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Staff Employee ID / License Number
            OutlinedTextField(
                value = staffId,
                onValueChange = { staffId = it.uppercase() },
                label = { Text("Hospital Employee ID / License #") },
                placeholder = { Text("e.g. EMP-DOC-102") },
                leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = primaryColor) },
                modifier = Modifier.fillMaxWidth().testTag("input_staff_id"),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Staff Hospital Email
            OutlinedTextField(
                value = staffIdentifier,
                onValueChange = { staffIdentifier = it },
                label = { Text("Staff Hospital Email / Phone") },
                placeholder = { Text("dr.sinha@synergy.org") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = primaryColor) },
                modifier = Modifier.fillMaxWidth().testTag("input_staff_email"),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Shift Passcode
            OutlinedTextField(
                value = staffPasscode,
                onValueChange = { staffPasscode = it },
                label = { Text("Clinical Shift Passcode / PIN") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = primaryColor) },
                modifier = Modifier.fillMaxWidth().testTag("input_staff_passcode"),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    onStaffLogin(
                        staffIdentifier.trim(),
                        staffName.trim(),
                        staffId.trim(),
                        staffDepartment.trim(),
                        staffDesignation.trim(),
                        isSignUpMode
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("btn_submit_staff_login")
            ) {
                Icon(Icons.Default.MedicalServices, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isSignUpMode) "Register Staff Credentials" else "Authenticate Staff Station",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
