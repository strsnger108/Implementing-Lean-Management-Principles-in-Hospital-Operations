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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StudyReportScreen(modifier: Modifier = Modifier) {
    var selectedChapter by remember { mutableIntStateOf(0) }
    val chapters = listOf(
        "Executive Summary",
        "Hospital Profile",
        "5 Objectives",
        "Methodology & t-Test",
        "Key Findings",
        "Recommendations"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("study_report_screen")
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "MBA Project Research Report",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Gunjan Prakash • Dr. D. Y. Patil Vidyapeeth Pune",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                ScrollableTabRow(
                    selectedTabIndex = selectedChapter,
                    edgePadding = 0.dp,
                    containerColor = Color.Transparent
                ) {
                    chapters.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedChapter == index,
                            onClick = { selectedChapter = index },
                            text = { Text(title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
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
            when (selectedChapter) {
                0 -> {
                    // Executive Summary
                    item {
                        ReportCard(
                            title = "Project Overview & Academic Context",
                            subtitle = "MBA in Hospital Administration and Healthcare Management"
                        ) {
                            Text(
                                text = "Topic: Implementing Lean Management Principles in Hospital Operations: A Case Study of Synergy Global Hospital, Ranchi, Jharkhand.",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Candidate: Gunjan Prakash (ERP ID: 241115821, PRN: 240502009315)\nGuide: Dr. Saurav Bhowmik\nInstitution: Centre for Online Learning, Dr. D. Y. Patil Vidyapeeth, Pune\nInternship Host: Synergy Global Hospital, Kadru Bypass, Ranchi",
                                fontSize = 12.sp,
                                lineHeight = 18.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    item {
                        ReportCard(title = "Abstract & Core Problem") {
                            Text(
                                text = "Hospital operations in private tier-2 healthcare centers face severe bed gridlock, high non-value-added waiting times, and discharge documentation gaps. This study investigated 381 patient admissions across 4 months (Nov 2025 to Feb 2026) using Toyota Lean production principles (5S, Value Stream Mapping, Kaizen, Muda reduction).",
                                fontSize = 13.sp,
                                lineHeight = 19.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Key Insight: Average LOS stood at 3.29 days. However, Value Stream Mapping revealed that patients spent only 21.5% of their total hospital journey in value-added clinical activity; 78.5% was consumed by queues, administrative batching, and discharge billing waits.",
                                fontSize = 13.sp,
                                lineHeight = 19.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
                1 -> {
                    // Hospital Profile
                    item {
                        ReportCard(title = "Synergy Global Hospital Profile") {
                            Text(
                                text = "Location: Kadru Bypass, Ranchi, Jharkhand - 834002\nType: Multi-specialty Tertiary Care Hospital\nBed Capacity: 150 Inpatient Beds (General Wards, Semi-Private, Deluxe, ICU/HDU/NICU)\nAccreditation: In-process NABH Entry-Level\nCatchment Area: Greater Ranchi, Bokaro, Ramgarh, and tribal districts of Jharkhand.",
                                fontSize = 12.sp,
                                lineHeight = 18.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Key Clinical Departments: General Medicine, General & Laparoscopic Surgery, Orthopedics & Joint Replacement, Obstetrics & Gynecology, Pediatrics & Neonatology, Critical Care, 24/7 Emergency & Trauma, Dialysis, Diagnostic Imaging (CT/USG/X-Ray).",
                                fontSize = 12.sp,
                                lineHeight = 18.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                2 -> {
                    // 5 Objectives
                    item {
                        ReportCard(title = "Core Research Objectives (Mandatory 5 Points)") {
                            val objectives = listOf(
                                "1. Patient Flow & LOS Evaluation: Analyze inpatient admission patterns and length of stay (LOS) trends across departments to establish baseline operational metrics.",
                                "2. Value Stream Mapping (VSM): Map the end-to-end patient journey from admission triage to final gate-pass discharge to quantify value-added versus non-value-added waiting time.",
                                "3. Hospital Waste (Muda) Identification: Systematically identify and categorize the 7 Lean wastes in hospital departments (waiting, overprocessing, motion, defects, inventory, transportation, underutilized staff).",
                                "4. Workplace Standardization (5S): Evaluate and implement 5S workplace organization standards across Emergency, Wards, Operation Theatres, and Pharmacy to eliminate physical searching and chaos.",
                                "5. Kaizen Continuous Improvement Framework: Design and test practical Kaizen countermeasures (10:00 AM standardized discharge rounds, discharge lounge, electronic Kanban bed board) to reduce discharge delays from 180 min to under 45 min."
                            )
                            objectives.forEach { obj ->
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                ) {
                                    Text(
                                        text = obj,
                                        fontSize = 12.sp,
                                        lineHeight = 18.sp,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                3 -> {
                    // Methodology & t-Test
                    item {
                        ReportCard(title = "Methodology & Statistical Hypothesis Testing") {
                            Text(
                                text = "Research Design: Mixed-methods observational quality improvement study with retrospective cohort analysis and prospective process timing.\nSample Size: N = 381 consecutive inpatient admissions between Nov 1, 2025 and Feb 28, 2026.\nPrimary Outcome: Discharge Turnaround Time (minutes from doctor intimation to physical bed release).",
                                fontSize = 12.sp,
                                lineHeight = 18.sp
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                color = Color(0xFFDCFCE7),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "Paired t-Test Statistical Results:",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF166534)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "• Pre-Intervention Discharge Time: Mean = 180.4 min (SD = 42.1)\n• Post-Lean Kaizen Rounds: Mean = 52.6 min (SD = 16.8)\n• Mean Reduction: 127.8 minutes (70.8% decrease!)\n• Test Statistic: t = 4.82, Degrees of Freedom df = 76\n• Significance: p < 0.001 (Highly Statistically Significant)\n• Conclusion: Null hypothesis rejected. Lean implementation significantly accelerates patient discharge flow and bed turnover.",
                                        fontSize = 12.sp,
                                        lineHeight = 18.sp,
                                        color = Color(0xFF14532D)
                                    )
                                }
                            }
                        }
                    }
                }
                4 -> {
                    // Key Findings
                    item {
                        ReportCard(title = "Empirical Study Findings") {
                            val findings = listOf(
                                "Average LOS is 3.29 Days: Synergy Global Hospital outperforms national Indian private hospital average (4.2 days) by 22%, but upward trend (3.03d in Nov to 3.72d in Feb) signals emerging ward bottlenecks.",
                                "Consultant Workload Concentration (Pareto): Dr. Rahul Sinha alone managed 127 out of 381 patients (40.1% of all cases), creating severe round delay risks and single-point clinical queuing.",
                                "Value Stream Efficiency of 21.5%: Value-adding clinical touch time accounts for only 16.9 hours of an average 78.5-hour stay; the remaining 61.6 hours consist of non-value delays.",
                                "50% Short-Stay Patients: Half of all admitted patients are discharged within 2 days, representing prime opportunity for standardized clinical fast-track pathways.",
                                "Discharge Bottleneck Dominance: Final billing reconciliation and physical clearance take an average of 180 minutes, delaying bed turnover until 3:00 PM."
                            )
                            findings.forEach { f ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(f, fontSize = 12.sp, lineHeight = 17.sp)
                                }
                            }
                        }
                    }
                }
                5 -> {
                    // Recommendations
                    item {
                        ReportCard(title = "Lean Implementation Roadmap") {
                            Text("1. Short-Term Interventions (Months 1–3):", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 13.sp)
                            Text("• Daily 10:00 AM standardized multi-disciplinary discharge rounds\n• Establishment of a 6-recliner Discharge Lounge to free beds by 11:30 AM\n• Visual 5S organization in ward nursing stations and pharmacy", fontSize = 12.sp, lineHeight = 17.sp)

                            Spacer(modifier = Modifier.height(8.dp))
                            Text("2. Medium-Term Interventions (Months 3–6):", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 13.sp)
                            Text("• Kanban electronic bed management board syncing ward and housekeeping\n• Pre-authorization billing protocol 24 hours prior to anticipated discharge\n• Consultant load leveling (Heijunka) with secondary specialist coverage", fontSize = 12.sp, lineHeight = 17.sp)

                            Spacer(modifier = Modifier.height(8.dp))
                            Text("3. Long-Term Strategic Goals (Months 6–12):", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 13.sp)
                            Text("• Clinical Pathways for top 10 diagnostic categories\n• Digital HMS integration with real-time VSM telemetry and auto-alerts\n• Institutionalize monthly Kaizen rewards for frontline nursing and housekeeping staff", fontSize = 12.sp, lineHeight = 17.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReportCard(
    title: String,
    subtitle: String? = null,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            content()
        }
    }
}
