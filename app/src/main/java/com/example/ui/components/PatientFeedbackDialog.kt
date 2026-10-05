package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PatientFeedbackDialog(
    hospitalRegNumber: String,
    patientName: String,
    initialEncounterType: String = "OPD", // "OPD" or "IPD"
    onDismiss: () -> Unit,
    onSubmitFeedback: (
        encounterType: String,
        doctorScore: Int,
        nursingScore: Int,
        hygieneScore: Int,
        billingScore: Int,
        overallScore: Int,
        comments: String,
        autoSyncGoogle: Boolean
    ) -> Unit
) {
    val context = LocalContext.current
    var encounterType by remember { mutableStateOf(initialEncounterType) }

    var doctorScore by remember { mutableIntStateOf(9) }
    var nursingScore by remember { mutableIntStateOf(9) }
    var hygieneScore by remember { mutableIntStateOf(9) }
    var billingScore by remember { mutableIntStateOf(8) }
    var overallScore by remember { mutableIntStateOf(9) }
    var comments by remember { mutableStateOf("Very satisfied with the clinical care and prompt attention.") }
    var autoSyncGoogle by remember { mutableStateOf(true) }
    var isSubmitted by remember { mutableStateOf(false) }

    val avgScore = remember(doctorScore, nursingScore, hygieneScore, billingScore, overallScore) {
        val sum = doctorScore + nursingScore + hygieneScore + billingScore + overallScore
        Math.round((sum / 5.0) * 10.0) / 10.0
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = Color(0xFFFEF3C7),
                    shape = CircleShape,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(20.dp))
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Patient Feedback & Review",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Synergy Global Hospital • $patientName",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        text = {
            if (isSubmitted) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(48.dp))
                    Text(
                        text = "Feedback Recorded Successfully!",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Your rating of $avgScore / 10 has been logged into hospital quality metrics.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (autoSyncGoogle) {
                        Surface(
                            color = Color(0xFFE0F2FE),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, tint = Color(0xFF0284C7), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Auto-synced with Google Reviews for Synergy Global Hospital!",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF0369A1)
                                )
                            }
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Encounter Type Selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = encounterType == "OPD",
                            onClick = { encounterType = "OPD" },
                            label = { Text("OPD Visit Experience") },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = encounterType == "IPD",
                            onClick = { encounterType = "IPD" },
                            label = { Text("IPD Inpatient Stay") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Average Score Banner
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Calculated Hospital Rating:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    text = "$avgScore / 10 (${if (avgScore >= 8.5) "Exceptional Care" else if (avgScore >= 7) "Good Experience" else "Needs Improvement"})",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Surface(
                                color = MaterialTheme.colorScheme.primary,
                                shape = CircleShape,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "${avgScore.toInt()}",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }
                    }

                    // Parameter 1: Doctor
                    ParameterRatingRow(
                        title = "1. Doctor Consultation & Explanation",
                        subtitle = "Doctor's clarity, listening, and treatment explanation",
                        score = doctorScore,
                        onScoreChanged = { doctorScore = it }
                    )

                    // Parameter 2: Nursing
                    ParameterRatingRow(
                        title = "2. Nursing Care & Promptness",
                        subtitle = "Gentle care, response time to bells, medication delivery",
                        score = nursingScore,
                        onScoreChanged = { nursingScore = it }
                    )

                    // Parameter 3: Hygiene
                    ParameterRatingRow(
                        title = "3. Cleanliness & Hospital Hygiene",
                        subtitle = "Room sanitization, clean bedsheets, washroom comfort",
                        score = hygieneScore,
                        onScoreChanged = { hygieneScore = it }
                    )

                    // Parameter 4: Billing
                    ParameterRatingRow(
                        title = "4. Waiting Time & Billing Transparency",
                        subtitle = "Queue time, clear estimates, cashless TPA assistance",
                        score = billingScore,
                        onScoreChanged = { billingScore = it }
                    )

                    // Parameter 5: Overall
                    ParameterRatingRow(
                        title = "5. Overall Hospital Recommendation",
                        subtitle = "Would you recommend Synergy Global Hospital to friends?",
                        score = overallScore,
                        onScoreChanged = { overallScore = it }
                    )

                    // Feedback Comments
                    OutlinedTextField(
                        value = comments,
                        onValueChange = { comments = it },
                        label = { Text("Patient Feedback / Testimonial") },
                        placeholder = { Text("Share any highlights or suggestions for the hospital...") },
                        modifier = Modifier.fillMaxWidth().testTag("input_feedback_comments"),
                        maxLines = 3
                    )

                    // Auto sync with Google Reviews Toggle
                    Surface(
                        color = Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { autoSyncGoogle = !autoSyncGoogle }
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = autoSyncGoogle,
                                onCheckedChange = { autoSyncGoogle = it },
                                modifier = Modifier.testTag("checkbox_google_sync")
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Auto-sync with Google Reviews",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("G", fontWeight = FontWeight.ExtraBold, color = Color(0xFF4285F4), fontSize = 13.sp)
                                }
                                Text(
                                    text = "Publishes your high rating to Synergy Global Hospital's official Google Maps listing",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (isSubmitted) {
                Button(onClick = onDismiss) { Text("Done") }
            } else {
                Button(
                    onClick = {
                        onSubmitFeedback(
                            encounterType,
                            doctorScore,
                            nursingScore,
                            hygieneScore,
                            billingScore,
                            overallScore,
                            comments,
                            autoSyncGoogle
                        )
                        isSubmitted = true

                        // If user enabled autoSyncGoogle, launch the Google Review URL / Intent
                        if (autoSyncGoogle) {
                            try {
                                val reviewUri = Uri.parse("https://search.google.com/local/writereview?placeid=ChIJx4SynergyGlobalHospitalRanchi")
                                val intent = Intent(Intent.ACTION_VIEW, reviewUri)
                                context.startActivity(intent)
                            } catch (_: Exception) {
                                // Handled safely if browser not present
                            }
                        }
                    },
                    modifier = Modifier.testTag("btn_submit_feedback")
                ) {
                    Icon(Icons.Default.RateReview, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Submit & Sync Review")
                }
            }
        },
        dismissButton = {
            if (!isSubmitted) {
                TextButton(onClick = onDismiss) { Text("Cancel") }
            }
        }
    )
}

@Composable
fun ParameterRatingRow(
    title: String,
    subtitle: String,
    score: Int,
    onScoreChanged: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text(subtitle, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Surface(
                color = when {
                    score >= 9 -> Color(0xFFDCFCE7)
                    score >= 7 -> Color(0xFFFEF3C7)
                    else -> Color(0xFFFEE2E2)
                },
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = "$score / 10",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        score >= 9 -> Color(0xFF16A34A)
                        score >= 7 -> Color(0xFFD97706)
                        else -> Color(0xFFDC2626)
                    },
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // 1 to 10 selector row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            (1..10).forEach { i ->
                val isSelected = i <= score
                val isExact = i == score
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(28.dp)
                        .background(
                            color = when {
                                isExact -> MaterialTheme.colorScheme.primary
                                isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
                                else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            },
                            shape = RoundedCornerShape(4.dp)
                        )
                        .clickable { onScoreChanged(i) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$i",
                        fontSize = 11.sp,
                        fontWeight = if (isExact) FontWeight.Bold else FontWeight.Normal,
                        color = if (isExact) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
