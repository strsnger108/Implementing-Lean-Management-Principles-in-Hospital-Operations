package com.example.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import com.example.data.model.FamilyMember

/**
 * Modular Authentication Component for Patients
 * Supports:
 * - Hospital Registration Number (UHID / MRN) input
 * - Multi-user association for family members (linking dependents under primary account)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientAuthFlow(
    onPatientLogin: (identifier: String, fullName: String, uhid: String, password: String, isSignUp: Boolean, familyMembers: List<FamilyMember>) -> Unit,
    modifier: Modifier = Modifier
) {
    var isSignUpMode by remember { mutableStateOf(false) }

    // Primary patient fields
    var identifier by remember { mutableStateOf("gunjanprakash2670@gmail.com") }
    var fullName by remember { mutableStateOf("Gunjan Prakash") }
    var hospitalRegNumber by remember { mutableStateOf("SGH-2026-0288") }
    var password by remember { mutableStateOf("••••••••") }
    var enableFamilyAssociation by remember { mutableStateOf(true) }

    // Multi-user association for family members
    val stagedFamilyMembers = remember {
        mutableStateListOf(
            FamilyMember(
                primaryUserIdentifier = "gunjanprakash2670@gmail.com",
                fullName = "Bimla Devi",
                relation = "Mother",
                age = 72,
                gender = "Female",
                hospitalRegNumber = "SGH-2026-0294",
                bloodGroup = "O+"
            ),
            FamilyMember(
                primaryUserIdentifier = "gunjanprakash2670@gmail.com",
                fullName = "Rajesh Gope",
                relation = "Father",
                age = 58,
                gender = "Male",
                hospitalRegNumber = "SGH-2026-0275",
                bloodGroup = "B+"
            )
        )
    }

    var showAddMemberForm by remember { mutableStateOf(false) }
    var newMemberName by remember { mutableStateOf("") }
    var newMemberRelation by remember { mutableStateOf("Spouse") }
    var newMemberAge by remember { mutableStateOf("28") }
    var newMemberGender by remember { mutableStateOf("Female") }
    var newMemberUhid by remember { mutableStateOf("") }
    var newMemberBloodGroup by remember { mutableStateOf("O+") }

    val primaryColor = Color(0xFF16A34A) // Emerald Green for Patient care

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
                        text = if (isSignUpMode) "Patient Portal Registration" else "Patient Portal Sign In",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor
                    )
                    Text(
                        text = "Access personal UHID records & associated family health cards",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                TextButton(
                    onClick = { isSignUpMode = !isSignUpMode },
                    modifier = Modifier.testTag("btn_toggle_patient_auth_mode")
                ) {
                    Text(if (isSignUpMode) "Sign In" else "Sign Up", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Full Name (in registration mode)
            if (isSignUpMode) {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Patient Full Name") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = primaryColor) },
                    modifier = Modifier.fillMaxWidth().testTag("input_patient_name"),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Mobile Phone / Gmail ID
            OutlinedTextField(
                value = identifier,
                onValueChange = { identifier = it },
                label = { Text("Primary Identifier (Mobile Phone / Gmail)") },
                placeholder = { Text("9876543210 or user@gmail.com") },
                leadingIcon = {
                    Icon(
                        imageVector = if (identifier.contains("@")) Icons.Default.Email else Icons.Default.Phone,
                        contentDescription = null,
                        tint = primaryColor
                    )
                },
                modifier = Modifier.fillMaxWidth().testTag("input_patient_identifier"),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Primary Hospital Registration Number (UHID / MRN)
            OutlinedTextField(
                value = hospitalRegNumber,
                onValueChange = { hospitalRegNumber = it.uppercase() },
                label = { Text("Primary Hospital Registration # (UHID / MRN)") },
                placeholder = { Text("e.g. SGH-2026-0288") },
                leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = primaryColor) },
                supportingText = {
                    Text("Found on OPD card, hospital admission pass, or discharge summary", fontSize = 10.sp)
                },
                modifier = Modifier.fillMaxWidth().testTag("input_patient_uhid"),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Password / Passcode
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(if (isSignUpMode) "Create Portal Passcode / PIN" else "Portal Passcode / OTP") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = primaryColor) },
                modifier = Modifier.fillMaxWidth().testTag("input_patient_password"),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(14.dp))

            // MULTI-USER ASSOCIATION FOR FAMILY MEMBERS SECTION
            Surface(
                color = Color(0xFFF0FDF4),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFBBF7D0), RoundedCornerShape(10.dp))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = primaryColor.copy(alpha = 0.15f),
                                shape = CircleShape,
                                modifier = Modifier.size(28.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.FamilyRestroom,
                                        contentDescription = null,
                                        tint = primaryColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Multi-User Family Association",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF166534)
                                )
                                Text(
                                    text = "Link dependent family members with their own UHIDs",
                                    fontSize = 10.sp,
                                    color = Color(0xFF15803D)
                                )
                            }
                        }

                        Switch(
                            checked = enableFamilyAssociation,
                            onCheckedChange = { enableFamilyAssociation = it },
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    if (enableFamilyAssociation) {
                        Spacer(modifier = Modifier.height(10.dp))

                        // Currently Associated Family Members
                        if (stagedFamilyMembers.isNotEmpty()) {
                            Text(
                                text = "Associated Dependents (${stagedFamilyMembers.size}):",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF166534)
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            stagedFamilyMembers.forEachIndexed { index, member ->
                                Surface(
                                    color = Color.White,
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDCFCE7)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 3.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                            .fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = member.fullName,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF166534)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Surface(
                                                    color = Color(0xFFDCFCE7),
                                                    shape = RoundedCornerShape(4.dp)
                                                ) {
                                                    Text(
                                                        text = member.relation,
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFF166534),
                                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                            Text(
                                                text = "UHID: ${member.hospitalRegNumber} • ${member.age} yrs (${member.gender}) • Blood: ${member.bloodGroup}",
                                                fontSize = 10.sp,
                                                color = Color(0xFF4B5563)
                                            )
                                        }

                                        IconButton(
                                            onClick = { stagedFamilyMembers.removeAt(index) },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Remove",
                                                tint = Color(0xFFDC2626),
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Add new family member inline drawer / dialog trigger
                        if (!showAddMemberForm) {
                            OutlinedButton(
                                onClick = { showAddMemberForm = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("btn_show_add_family_form")
                            ) {
                                Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("+ Associate Another Family Member", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                        } else {
                            // Inline Form for Adding Associated Member
                            Surface(
                                color = Color.White,
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF86EFAC)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "Add Family Member to Account",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = primaryColor
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedTextField(
                                        value = newMemberName,
                                        onValueChange = { newMemberName = it },
                                        label = { Text("Family Member Full Name") },
                                        modifier = Modifier.fillMaxWidth().testTag("input_new_member_name"),
                                        singleLine = true
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        var relExpanded by remember { mutableStateOf(false) }
                                        ExposedDropdownMenuBox(
                                            expanded = relExpanded,
                                            onExpandedChange = { relExpanded = it },
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            OutlinedTextField(
                                                value = newMemberRelation,
                                                onValueChange = {},
                                                readOnly = true,
                                                label = { Text("Relation") },
                                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = relExpanded) },
                                                modifier = Modifier.menuAnchor()
                                            )
                                            ExposedDropdownMenu(
                                                expanded = relExpanded,
                                                onDismissRequest = { relExpanded = false }
                                            ) {
                                                listOf("Spouse", "Child", "Mother", "Father", "Sibling", "Other").forEach { rel ->
                                                    DropdownMenuItem(
                                                        text = { Text(rel, fontSize = 12.sp) },
                                                        onClick = {
                                                            newMemberRelation = rel
                                                            relExpanded = false
                                                        }
                                                    )
                                                }
                                            }
                                        }

                                        OutlinedTextField(
                                            value = newMemberAge,
                                            onValueChange = { newMemberAge = it },
                                            label = { Text("Age") },
                                            modifier = Modifier.weight(0.7f),
                                            singleLine = true
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    // Family Member UHID Input Field
                                    OutlinedTextField(
                                        value = newMemberUhid,
                                        onValueChange = { newMemberUhid = it.uppercase() },
                                        label = { Text("Member Hospital Registration # (UHID / MRN)") },
                                        placeholder = { Text("e.g. SGH-2026-0294") },
                                        leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = primaryColor) },
                                        modifier = Modifier.fillMaxWidth().testTag("input_new_member_uhid"),
                                        singleLine = true
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        TextButton(onClick = { showAddMemberForm = false }) {
                                            Text("Cancel", fontSize = 11.sp)
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Button(
                                            onClick = {
                                                if (newMemberName.isNotBlank()) {
                                                    val uhid = if (newMemberUhid.isNotBlank()) newMemberUhid else "MRN-${(1000..9999).random()}"
                                                    stagedFamilyMembers.add(
                                                        FamilyMember(
                                                            primaryUserIdentifier = identifier,
                                                            fullName = newMemberName.trim(),
                                                            relation = newMemberRelation,
                                                            age = newMemberAge.toIntOrNull() ?: 30,
                                                            gender = newMemberGender,
                                                            hospitalRegNumber = uhid,
                                                            bloodGroup = newMemberBloodGroup
                                                        )
                                                    )
                                                    newMemberName = ""
                                                    newMemberUhid = ""
                                                    showAddMemberForm = false
                                                }
                                            },
                                            enabled = newMemberName.isNotBlank(),
                                            colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                                            shape = RoundedCornerShape(6.dp),
                                            modifier = Modifier.testTag("btn_confirm_add_staged_member")
                                        ) {
                                            Text("Save Dependent", fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Submit Button
            Button(
                onClick = {
                    onPatientLogin(
                        identifier.trim(),
                        fullName.trim(),
                        hospitalRegNumber.trim(),
                        password.trim(),
                        isSignUpMode,
                        if (enableFamilyAssociation) stagedFamilyMembers.toList() else emptyList()
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("btn_submit_patient_login")
            ) {
                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isSignUpMode) "Register Patient & Link ${stagedFamilyMembers.size} Dependents" else "Sign In to Patient Portal",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
