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
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.FolderShared
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.SwitchAccount
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.VerifiedUser
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
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FamilyMember
import com.example.data.model.PatientRecord
import com.example.ui.components.ClinicalDocumentData
import com.example.ui.components.ClinicalDocumentViewerDialog
import com.example.ui.components.DailyIpdBillRow
import com.example.ui.components.EhrDocumentVaultSection
import com.example.ui.components.NurseCallDialog
import com.example.ui.components.OpdPaperAndDiagnosisCard
import com.example.ui.components.PatientBillsSection
import com.example.ui.components.PatientBloodReportsSection
import com.example.ui.components.PatientFeedbackDialog
import com.example.ui.components.PatientInsuranceCard
import com.example.ui.components.PatientPharmacySection
import com.example.ui.viewmodel.HospitalViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientPortalScreen(
    viewModel: HospitalViewModel,
    onSwitchToStaffMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeSession by viewModel.activeUserSession.collectAsState()
    val familyMembers by viewModel.familyMembers.collectAsState()
    val allPatients by viewModel.allPatients.collectAsState()
    val selectedRegNumber by viewModel.selectedRegNumber.collectAsState()

    val bills by viewModel.activePatientBills.collectAsState()
    val dailyIpdBills by viewModel.activePatientDailyIpdBills.collectAsState()
    val insurance by viewModel.activePatientInsurance.collectAsState()
    val activeNurseCallAlert by viewModel.activeNurseCallAlert.collectAsState()
    val activePrescriptions by viewModel.activePatientPrescriptions.collectAsState()
    val feedbacks by viewModel.activePatientFeedbacks.collectAsState()
    val diagnosisSummaries = viewModel.diagnosisSummaries

    // 0: Overview & OPD, 1: Hospital Bills & IPD, 2: Inpatient Journey, 3: Pharmacy & Meds, 4: Blood Reports, 5: OPD Papers & Vault, 6: Family
    var selectedPortalTab by remember { mutableIntStateOf(0) }
    var showAddFamilyDialog by remember { mutableStateOf(false) }
    var showNurseCallDialog by remember { mutableStateOf(false) }
    var showFeedbackDialog by remember { mutableStateOf(false) }
    var showRoleDialog by remember { mutableStateOf(false) }
    var selectedViewerDoc by remember { mutableStateOf<ClinicalDocumentData?>(null) }

    val userFullName = activeSession?.fullName ?: "Gunjan Prakash"
    val userIdentifier = activeSession?.identifier ?: "gunjanprakash2670@gmail.com"
    val hospitalCode = activeSession?.hospitalCode ?: "SGH-RANCHI"
    val hospitalName = activeSession?.hospitalName ?: "Synergy Global Hospital"

    // Active UHID determined by user selection or default session
    val currentUhid = selectedRegNumber ?: activeSession?.hospitalRegNumber ?: "SGH-2026-0288"
    val currentActiveMember = familyMembers.firstOrNull { it.hospitalRegNumber == currentUhid }
    val currentPatientName = currentActiveMember?.fullName ?: userFullName

    // Match admitted inpatient record for current active profile
    val myInpatientRecord = allPatients.firstOrNull { it.ipdNumber.equals(currentUhid, ignoreCase = true) }
        ?: if (currentUhid == "SGH-2026-0294") allPatients.firstOrNull { it.patientName.contains("Bimla", ignoreCase = true) } else null
    val isAdmitted = myInpatientRecord != null && !myInpatientRecord.isDischarged

    // Diagnosis summary for current profile
    val currentDiagnosis = diagnosisSummaries.firstOrNull { it.hospitalRegNumber == currentUhid }
        ?: diagnosisSummaries.firstOrNull()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("patient_portal_screen")
    ) {
        // Top Header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                // User & Role Switching
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = CircleShape,
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = currentPatientName,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                if (currentActiveMember?.relation != null) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = currentActiveMember.relation,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                            Text(
                                text = "UHID: $currentUhid • Account: $userIdentifier",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { showRoleDialog = true },
                            modifier = Modifier.testTag("btn_switch_staff_mode")
                        ) {
                            Icon(imageVector = Icons.Default.SwitchAccount, contentDescription = "Switch Role", tint = MaterialTheme.colorScheme.primary)
                        }
                        IconButton(
                            onClick = { viewModel.logout() },
                            modifier = Modifier.testTag("btn_logout")
                        ) {
                            Icon(imageVector = Icons.Default.Logout, contentDescription = "Logout", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Patient Scoping & Family Selector Chips (Requirement: Patient only see and access their detail and family members detail)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Profile:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    familyMembers.forEach { member ->
                        val isSelected = (selectedRegNumber ?: activeSession?.hospitalRegNumber ?: "SGH-2026-0288") == member.hospitalRegNumber
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                viewModel.setSelectedPatientRegNumber(member.hospitalRegNumber)
                            },
                            label = {
                                Text(
                                    text = "${member.fullName} (${member.relation})",
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            leadingIcon = if (isSelected) {
                                { Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(14.dp)) }
                            } else null,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }

                    IconButton(
                        onClick = { showAddFamilyDialog = true },
                        modifier = Modifier.size(28.dp).testTag("btn_add_family_header")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Family Member", tint = MaterialTheme.colorScheme.primary)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Quick Action Bar: Nurse Call, Feedback, Insurance
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (isAdmitted) {
                        Button(
                            onClick = { showNurseCallDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("btn_nurse_call_bar")
                        ) {
                            Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Call Duty Nurse (${myInpatientRecord?.bedNumber ?: "Bed 14"})", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    OutlinedButton(
                        onClick = { showFeedbackDialog = true },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("btn_rate_experience_bar")
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Rate Experience & Google Sync", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Surface(
                        color = Color(0xFFCCFBF1),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.clickable { selectedPortalTab = 1 }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = Color(0xFF0F766E), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "TPA: ${insurance?.claimStatus ?: "Cashless Active"}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F766E)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Scrollable Tab Navigation
                ScrollableTabRow(
                    selectedTabIndex = selectedPortalTab,
                    edgePadding = 0.dp,
                    containerColor = Color.Transparent
                ) {
                    Tab(
                        selected = selectedPortalTab == 0,
                        onClick = { selectedPortalTab = 0 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Today, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Overview & OPD", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        },
                        modifier = Modifier.testTag("tab_overview_opd")
                    )

                    Tab(
                        selected = selectedPortalTab == 1,
                        onClick = { selectedPortalTab = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("All Bills & Daily IPD (${bills.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        },
                        modifier = Modifier.testTag("tab_all_bills")
                    )

                    Tab(
                        selected = selectedPortalTab == 2,
                        onClick = { selectedPortalTab = 2 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Hotel, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (isAdmitted) "Inpatient Care (Admitted)" else "Inpatient Journey", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        },
                        modifier = Modifier.testTag("tab_inpatient_care")
                    )

                    Tab(
                        selected = selectedPortalTab == 3,
                        onClick = { selectedPortalTab = 3 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocalPharmacy, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Prescriptions (Rx)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        },
                        modifier = Modifier.testTag("tab_pharmacy_meds")
                    )

                    Tab(
                        selected = selectedPortalTab == 4,
                        onClick = { selectedPortalTab = 4 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Biotech, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Blood Reports", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        },
                        modifier = Modifier.testTag("tab_blood_reports")
                    )

                    Tab(
                        selected = selectedPortalTab == 5,
                        onClick = { selectedPortalTab = 5 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.FolderShared, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("OPD Papers & Vault", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        },
                        modifier = Modifier.testTag("tab_document_vault")
                    )

                    Tab(
                        selected = selectedPortalTab == 6,
                        onClick = { selectedPortalTab = 6 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.FamilyRestroom, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Family (${familyMembers.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        },
                        modifier = Modifier.testTag("tab_family_members")
                    )
                }
            }
        }

        // Tab Content
        when (selectedPortalTab) {
            0 -> {
                // Overview & OPD Queue & Highlights
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Admitted Patient Alert if applicable
                    if (isAdmitted && myInpatientRecord != null) {
                        item {
                            AdmittedPatientBanner(
                                patient = myInpatientRecord,
                                onCallNurse = { showNurseCallDialog = true },
                                onViewInpatient = { selectedPortalTab = 2 }
                            )
                        }
                    }

                    // OPD Waiting Time Card (Requirement: OPD waiting time)
                    item {
                        OpdWaitingSummaryCard(
                            patientName = currentPatientName,
                            uhid = currentUhid
                        )
                    }

                    // Insurance Details Card (Requirement: Insurance detail if have)
                    item {
                        PatientInsuranceCard(insurance = insurance)
                    }

                    // Clinical Diagnosis & OPD Consultation Paper Card (Requirement: Medicine and OPD paper or diagnosis and blood reports)
                    if (currentDiagnosis != null) {
                        item {
                            OpdPaperAndDiagnosisCard(
                                summary = currentDiagnosis,
                                onOpenDocViewer = {
                                    selectedViewerDoc = ClinicalDocumentData.OpdPaper(
                                        summary = currentDiagnosis,
                                        medications = activePrescriptions
                                    )
                                }
                            )
                        }
                    }

                    // Yesterday's Daily IPD Bill Update Highlight (Requirement: Daily bill update after day. Yesterday bill update at morning for IPD)
                    if (dailyIpdBills.isNotEmpty()) {
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth().testTag("yesterday_bill_highlight")
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Today, contentDescription = null, tint = Color(0xFF0284C7), modifier = Modifier.size(20.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Yesterday's IPD Bill (Updated at Morning)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        }
                                        TextButton(onClick = { selectedPortalTab = 1 }) {
                                            Text("All Bills", fontSize = 11.sp)
                                        }
                                    }
                                    val yesterdayBill = dailyIpdBills.lastOrNull()
                                    if (yesterdayBill != null) {
                                        DailyIpdBillRow(entry = yesterdayBill)
                                    }
                                }
                            }
                        }
                    }

                    // Feedback and Google Reviews Highlight (Requirement: Feedback form & auto sync with Google review)
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                            border = CardDefaults.outlinedCardBorder(),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Patient Feedback & Google Reviews", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Surface(color = Color(0xFFDCFCE7), shape = RoundedCornerShape(6.dp)) {
                                        Text(
                                            text = "Auto-Sync Enabled",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF16A34A),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Rate doctor consultation, nursing care, hygiene, and billing transparency from 1 to 10. Automatically publish verified testimonials to Google Reviews.",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Button(
                                    onClick = { showFeedbackDialog = true },
                                    modifier = Modifier.fillMaxWidth().testTag("btn_open_feedback_overview")
                                ) {
                                    Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Submit Feedback & Sync Review", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                // All Hospital Bills & Daily IPD (Requirement: see their all bills & yesterday bill update at morning for IPD)
                PatientBillsSection(
                    bills = bills,
                    dailyIpdBills = dailyIpdBills,
                    isAdmitted = isAdmitted,
                    modifier = Modifier.fillMaxSize()
                )
            }

            2 -> {
                // Inpatient Care, Care Team & Discharge Time (Requirement: IPD discharge time, consultant name, attending nurse name, nurse call)
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    if (myInpatientRecord != null) {
                        item {
                            PatientInpatientStatusCard(
                                patient = myInpatientRecord,
                                onCallNurse = { showNurseCallDialog = true }
                            )
                        }

                        // Daily Inpatient Ledger in Inpatient tab
                        if (dailyIpdBills.isNotEmpty()) {
                            item {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text("Daily IPD Ledger (Yesterday's Morning Update)", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("Updated every morning at 07:30 AM after clinical audit.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Spacer(modifier = Modifier.height(10.dp))
                                        dailyIpdBills.forEach { entry ->
                                            DailyIpdBillRow(entry = entry)
                                            Spacer(modifier = Modifier.height(6.dp))
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(24.dp).fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(Icons.Default.Hotel, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(48.dp))
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("No Active Inpatient Admission for $currentPatientName", fontWeight = FontWeight.Bold)
                                    Text("You are currently registered for Outpatient (OPD) care. If you are admitted, your room, attending nurse, consultant, and daily bills will appear here.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }

            3 -> {
                // Prescriptions & Meds
                PatientPharmacySection(
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxSize()
                )
            }

            4 -> {
                // Blood Reports
                PatientBloodReportsSection(
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxSize()
                )
            }

            5 -> {
                // OPD Papers & EHR Document Vault
                Column(modifier = Modifier.fillMaxSize()) {
                    if (currentDiagnosis != null) {
                        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                            OpdPaperAndDiagnosisCard(
                                summary = currentDiagnosis,
                                onOpenDocViewer = {
                                    selectedViewerDoc = ClinicalDocumentData.OpdPaper(
                                        summary = currentDiagnosis,
                                        medications = activePrescriptions
                                    )
                                }
                            )
                        }
                    }
                    EhrDocumentVaultSection(
                        viewModel = viewModel,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            6 -> {
                // Family Profiles Management
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Linked Family Members",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Only you and your linked family members can view medical records",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Button(
                                onClick = { showAddFamilyDialog = true },
                                modifier = Modifier.height(34.dp).testTag("btn_add_family_member"),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add Member", fontSize = 11.sp)
                            }
                        }
                    }

                    items(familyMembers, key = { it.id }) { member ->
                        FamilyMemberCard(
                            member = member,
                            isSelected = (selectedRegNumber ?: activeSession?.hospitalRegNumber ?: "SGH-2026-0288") == member.hospitalRegNumber,
                            onSelect = { viewModel.setSelectedPatientRegNumber(member.hospitalRegNumber) },
                            onDelete = { viewModel.deleteFamilyMember(member) }
                        )
                    }
                }
            }
        }

        // Dialogs
        if (showAddFamilyDialog) {
            AddFamilyMemberDialog(
                onDismiss = { showAddFamilyDialog = false },
                onConfirm = { name, relation, age, gender, regNumber, bloodGroup ->
                    viewModel.addFamilyMember(name, relation, age, gender, regNumber, bloodGroup)
                    showAddFamilyDialog = false
                }
            )
        }

        if (showNurseCallDialog) {
            NurseCallDialog(
                hospitalRegNumber = currentUhid,
                patientName = currentPatientName,
                bedNumber = myInpatientRecord?.bedNumber ?: "Ward 3B - Bed 14",
                wardName = myInpatientRecord?.department ?: "Internal Medicine (3rd Floor)",
                attendingNurseName = myInpatientRecord?.attendingNurseName ?: "Staff Nurse Priya Sharma (RN)",
                activeCall = activeNurseCallAlert,
                onDismiss = { showNurseCallDialog = false },
                onTriggerCall = { reason ->
                    viewModel.triggerNurseCall(
                        regNumber = currentUhid,
                        patientName = currentPatientName,
                        bedNumber = myInpatientRecord?.bedNumber ?: "Ward 3B - Bed 14",
                        wardName = myInpatientRecord?.department ?: "Internal Medicine (3rd Floor)",
                        reason = reason
                    )
                },
                onCancelCall = { id -> viewModel.cancelNurseCall(id) }
            )
        }

        if (showFeedbackDialog) {
            PatientFeedbackDialog(
                hospitalRegNumber = currentUhid,
                patientName = currentPatientName,
                initialEncounterType = if (isAdmitted) "IPD" else "OPD",
                onDismiss = { showFeedbackDialog = false },
                onSubmitFeedback = { enc, doc, nurse, hyg, bill, over, comm, autoG ->
                    viewModel.submitFeedback(
                        regNumber = currentUhid,
                        patientName = currentPatientName,
                        encounterType = enc,
                        docScore = doc,
                        nurseScore = nurse,
                        hygieneScore = hyg,
                        billingScore = bill,
                        overallScore = over,
                        comments = comm,
                        autoSyncGoogle = autoG
                    )
                }
            )
        }

        selectedViewerDoc?.let { doc ->
            ClinicalDocumentViewerDialog(
                documentData = doc,
                onDismiss = { selectedViewerDoc = null }
            )
        }

        if (showRoleDialog) {
            com.example.ui.components.RoleSwitcherDialog(
                currentRole = activeSession?.role ?: "PATIENT",
                onDismiss = { showRoleDialog = false },
                onRoleSelected = { newRole ->
                    viewModel.switchActiveRole(newRole)
                    showRoleDialog = false
                    if (newRole != "PATIENT") {
                        onSwitchToStaffMode()
                    }
                },
                onOpenLoginScreen = onSwitchToStaffMode
            )
        }
    }
}

@Composable
fun OpdWaitingSummaryCard(
    patientName: String,
    uhid: String,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier.fillMaxWidth().testTag("opd_waiting_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = Color(0xFFEDE9FE),
                        shape = CircleShape,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Schedule, contentDescription = null, tint = Color(0xFF7C3AED), modifier = Modifier.size(20.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "OPD Live Queue & Waiting Time",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "General Medicine • Dr. Rahul Sinha, MD",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    color = Color(0xFFDCFCE7),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "On Schedule",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF16A34A),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Your Token", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("#18", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                    }
                }

                Surface(
                    color = Color(0xFFE0F2FE),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Now Serving", fontSize = 10.sp, color = Color(0xFF0369A1))
                        Text("#14", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF0284C7))
                    }
                }

                Surface(
                    color = Color(0xFFFEF3C7),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1.3f)
                ) {
                    Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Estimated Wait", fontSize = 10.sp, color = Color(0xFF92400E))
                        Text("~28 mins", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD97706))
                        Text("4 patients ahead", fontSize = 9.sp, color = Color(0xFF92400E))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Doctor's Chamber: Room 102 (OPD Block A)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                    Text("Token Status: Waiting", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@Composable
fun AdmittedPatientBanner(
    patient: PatientRecord,
    onCallNurse: () -> Unit,
    onViewInpatient: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF3B82F6))),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth().testTag("admitted_patient_banner")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Hotel, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Currently Admitted • ${patient.bedNumber}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E40AF)
                    )
                }

                Surface(color = Color(0xFFDCFCE7), shape = RoundedCornerShape(6.dp)) {
                    Text(
                        text = "Expected Discharge: ${patient.estimatedDischargeTime}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF16A34A),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Clinical care team display (Requirement: show consultant name and attending nurse name)
            Text("Lead Consultant: ${patient.consultantName}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1E293B))
            Text("Attending Nurse: ${patient.attendingNurseName}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0369A1))

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onCallNurse,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                    modifier = Modifier.weight(1f).testTag("btn_nurse_call_banner")
                ) {
                    Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Call Duty Nurse", fontSize = 11.sp)
                }

                OutlinedButton(
                    onClick = onViewInpatient,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Inpatient Details", fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun PatientInpatientStatusCard(
    patient: PatientRecord,
    onCallNurse: () -> Unit
) {
    val stageColor = when (patient.flowStage) {
        "Admission" -> Color(0xFF0284C7)
        "Diagnostics" -> Color(0xFF7C3AED)
        "Treatment" -> Color(0xFF16A34A)
        "Discharge" -> Color(0xFFEA580C)
        else -> Color(0xFF16A34A)
    }

    Card(
        modifier = Modifier.fillMaxWidth().testTag("patient_inpatient_card"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Active Admission: ${patient.patientName}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "IPD #${patient.ipdNumber} • Admitted: ${patient.admissionDate}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    color = stageColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "${patient.flowStage} Stage",
                        color = stageColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 4-Stage Visual Progress Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf("Admission", "Diagnostics", "Treatment", "Discharge").forEach { st ->
                    val isActive = st.equals(patient.flowStage, ignoreCase = true)
                    val isPast = when (patient.flowStage) {
                        "Diagnostics" -> st == "Admission"
                        "Treatment" -> st in listOf("Admission", "Diagnostics")
                        "Discharge" -> true
                        else -> false
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(
                                when {
                                    isActive -> stageColor
                                    isPast -> Color(0xFF16A34A)
                                    else -> Color.LightGray.copy(alpha = 0.5f)
                                }
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Clinical Care Team Box (Requirement: For IPD show patient consultant name and attending nurse name)
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("CLINICAL CARE TEAM & BED DETAILS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text("Treating Lead Consultant: ${patient.consultantName}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Text("Attending Nurse on Shift: ${patient.attendingNurseName}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0369A1))
                    Text("Allocated Bed: ${patient.bedNumber} • Department: ${patient.department}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Current Stay: ${patient.losDays + 1} Days", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    if (patient.notes.isNotBlank()) {
                        Text("Clinical Notes: ${patient.notes}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // IPD Discharge Time Box (Requirement: IPD discharge time if admitted)
            Surface(
                color = Color(0xFFFFFBEB),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().testTag("ipd_discharge_time_card")
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.HourglassTop, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Estimated Discharge Time", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF92400E))
                        }
                        Text(patient.estimatedDischargeTime, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFB45309))
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Discharge Clearance Checklist:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF92400E))
                    DischargeCheckItem("1. Consultant Final Round & Discharge Order", isDone = true)
                    DischargeCheckItem("2. Discharge Summary & Prescriptions Draft", isDone = true)
                    DischargeCheckItem("3. Ward Pharmacy Return Reconciliation", isDone = true)
                    DischargeCheckItem("4. TPA Final Approval & Gate Pass", isDone = false)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Nurse Call Action Button (Requirement: Also add nurse call button for admitted patient)
            Button(
                onClick = onCallNurse,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                modifier = Modifier.fillMaxWidth().testTag("btn_nurse_call_inpatient_card")
            ) {
                Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Call Duty Nurse (${patient.bedNumber})", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun DischargeCheckItem(title: String, isDone: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontSize = 10.sp, color = Color(0xFF78350F))
        Surface(
            color = if (isDone) Color(0xFFDCFCE7) else Color(0xFFFEF3C7),
            shape = RoundedCornerShape(4.dp)
        ) {
            Text(
                text = if (isDone) "Cleared" else "In Progress",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDone) Color(0xFF16A34A) else Color(0xFFD97706),
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
            )
        }
    }
}

@Composable
fun FamilyMemberCard(
    member: FamilyMember,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .testTag("family_card_${member.fullName.replace(" ", "_")}"),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else CardDefaults.outlinedCardBorder(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                    shape = CircleShape,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = member.fullName,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = member.relation,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "UHID: ${member.hospitalRegNumber} • ${member.age} yrs (${member.gender}) • Blood: ${member.bloodGroup}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isSelected) {
                    Surface(color = Color(0xFFDCFCE7), shape = RoundedCornerShape(6.dp)) {
                        Text("Active", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }
                if (member.relation != "Self") {
                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFamilyMemberDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, Int, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var relation by remember { mutableStateOf("Spouse") }
    var ageText by remember { mutableStateOf("30") }
    var gender by remember { mutableStateOf("Female") }
    var regNumber by remember { mutableStateOf("SGH-2026-${(100..999).random()}") }
    var bloodGroup by remember { mutableStateOf("B+") }

    val relations = listOf("Spouse", "Child", "Father", "Mother", "Sibling", "Other")
    var relExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Family Member Profile", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth().testTag("input_family_name")
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ExposedDropdownMenuBox(
                        expanded = relExpanded,
                        onExpandedChange = { relExpanded = it },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = relation,
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
                            relations.forEach { r ->
                                DropdownMenuItem(text = { Text(r) }, onClick = { relation = r; relExpanded = false })
                            }
                        }
                    }

                    OutlinedTextField(
                        value = ageText,
                        onValueChange = { ageText = it },
                        label = { Text("Age") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = regNumber,
                    onValueChange = { regNumber = it },
                    label = { Text("Registration # (UHID / MRN)") },
                    modifier = Modifier.fillMaxWidth().testTag("input_family_reg")
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = gender,
                        onValueChange = { gender = it },
                        label = { Text("Gender") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = bloodGroup,
                        onValueChange = { bloodGroup = it },
                        label = { Text("Blood Group") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val age = ageText.toIntOrNull() ?: 30
                        onConfirm(name, relation, age, gender, regNumber, bloodGroup)
                    }
                },
                enabled = name.isNotBlank(),
                modifier = Modifier.testTag("btn_save_family_member")
            ) {
                Text("Add Member")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
