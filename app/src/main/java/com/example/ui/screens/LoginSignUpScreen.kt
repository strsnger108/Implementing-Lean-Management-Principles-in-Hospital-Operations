package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.model.FamilyMember
import com.example.ui.auth.AdminAuthFlow
import com.example.ui.auth.AuthHeaderAndFacilityCard
import com.example.ui.auth.PatientAuthFlow
import com.example.ui.auth.QuickDemoAuthBar
import com.example.ui.auth.StaffAuthFlow
import com.example.ui.viewmodel.HospitalViewModel

/**
 * Modular Hospital Portal Authentication Screen
 * Orchestrates distinct, modular authentication flows for:
 * 1. PATIENTS (with Hospital Registration Numbers & Multi-User Family Association)
 * 2. STAFF (with Employee IDs, Clinical Designations & Station Credentials)
 * 3. ADMINS (with Directorate Clearance Tokens & Master Passphrases)
 */
@Composable
fun LoginSignUpScreen(
    viewModel: HospitalViewModel,
    modifier: Modifier = Modifier,
    onDismiss: (() -> Unit)? = null
) {
    // 0: Patient, 1: Staff, 2: Admin
    var selectedRoleIndex by remember { mutableIntStateOf(0) }
    var selectedHospitalCode by remember { mutableStateOf("SGH-RANCHI") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("login_signup_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Header & Connected Hospital Facility Node Card
            AuthHeaderAndFacilityCard(
                selectedRoleIndex = selectedRoleIndex,
                onRoleSelected = { selectedRoleIndex = it },
                selectedHospitalCode = selectedHospitalCode,
                onHospitalCodeSelected = { selectedHospitalCode = it },
                onDismiss = onDismiss
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Role-Specific Modular Authentication Flows
            when (selectedRoleIndex) {
                0 -> {
                    // PATIENT AUTH FLOW: UHID input & Multi-User Family Member Association
                    PatientAuthFlow(
                        onPatientLogin = { identifier, fullName, uhid, _, isSignUp, familyMembers ->
                            if (isSignUp && familyMembers.isNotEmpty()) {
                                viewModel.registerPatientWithFamily(
                                    hospitalCode = selectedHospitalCode,
                                    identifier = identifier,
                                    fullName = fullName,
                                    hospitalRegNumber = uhid,
                                    familyMembers = familyMembers
                                )
                            } else {
                                viewModel.loginOrRegister(
                                    role = "PATIENT",
                                    hospitalCode = selectedHospitalCode,
                                    identifier = identifier,
                                    fullName = fullName,
                                    hospitalRegNumber = uhid,
                                    department = "General Medicine"
                                )
                            }
                            onDismiss?.invoke()
                        }
                    )
                }

                1 -> {
                    // STAFF AUTH FLOW: Employee ID, Clinical Designation & Station
                    StaffAuthFlow(
                        onStaffLogin = { identifier, fullName, staffId, department, _, _ ->
                            viewModel.loginOrRegister(
                                role = "STAFF",
                                hospitalCode = selectedHospitalCode,
                                identifier = identifier,
                                fullName = fullName,
                                hospitalRegNumber = staffId,
                                department = department
                            )
                            onDismiss?.invoke()
                        }
                    )
                }

                2 -> {
                    // ADMIN AUTH FLOW: Executive Clearance ID & Master Passphrase
                    AdminAuthFlow(
                        onAdminLogin = { identifier, fullName, adminId, directorate, _ ->
                            viewModel.loginOrRegister(
                                role = "ADMIN",
                                hospitalCode = selectedHospitalCode,
                                identifier = identifier,
                                fullName = fullName,
                                hospitalRegNumber = adminId,
                                department = directorate
                            )
                            onDismiss?.invoke()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Quick 1-Tap Demo Logins & Interoperability Footer
            QuickDemoAuthBar(
                onQuickPatientLogin = {
                    viewModel.loginOrRegister(
                        role = "PATIENT",
                        hospitalCode = "SGH-RANCHI",
                        identifier = "gunjanprakash2670@gmail.com",
                        fullName = "Gunjan Prakash",
                        hospitalRegNumber = "SGH-2026-0288",
                        department = "General Medicine"
                    )
                    onDismiss?.invoke()
                },
                onQuickDoctorLogin = {
                    viewModel.loginOrRegister(
                        role = "STAFF",
                        hospitalCode = "SGH-RANCHI",
                        identifier = "dr.sinha@synergy.org",
                        fullName = "Dr. Rahul Sinha",
                        hospitalRegNumber = "EMP-DOC-102",
                        department = "General Medicine"
                    )
                    onDismiss?.invoke()
                },
                onQuickNurseLogin = {
                    viewModel.loginOrRegister(
                        role = "STAFF",
                        hospitalCode = "SGH-RANCHI",
                        identifier = "priya.sharma@synergy.org",
                        fullName = "Staff Nurse Priya Sharma (RN)",
                        hospitalRegNumber = "RN-WARD-3B",
                        department = "Ward 3B (Internal Medicine)"
                    )
                    onDismiss?.invoke()
                },
                onQuickAdminLogin = {
                    viewModel.loginOrRegister(
                        role = "ADMIN",
                        hospitalCode = "SGH-RANCHI",
                        identifier = "admin@synergy.org",
                        fullName = "Chief Medical Superintendent & Lean Director",
                        hospitalRegNumber = "ADM-SGH-01",
                        department = "Hospital Operations & Lean Quality"
                    )
                    onDismiss?.invoke()
                }
            )
        }
    }
}
