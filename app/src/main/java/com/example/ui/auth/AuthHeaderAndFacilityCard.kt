package com.example.ui.auth

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HospitalDirectory

/**
 * Modular Header and Hospital Facility Node Selector
 */
@Composable
fun AuthHeaderAndFacilityCard(
    selectedRoleIndex: Int,
    onRoleSelected: (Int) -> Unit,
    selectedHospitalCode: String,
    onHospitalCodeSelected: (String) -> Unit,
    onDismiss: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val primaryRoleColor = when (selectedRoleIndex) {
        0 -> Color(0xFF16A34A) // Green for Patient
        1 -> Color(0xFF2563EB) // Blue for Staff
        else -> Color(0xFF7C3AED) // Purple for Admin
    }

    val hospitalName = HospitalDirectory.getHospitalName(selectedHospitalCode)

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (onDismiss != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                TextButton(onClick = onDismiss) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Return to Dashboard", fontSize = 12.sp)
                }
            }
        } else {
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Hospital Branding Icon
        Surface(
            color = primaryRoleColor.copy(alpha = 0.12f),
            shape = CircleShape,
            modifier = Modifier.size(64.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = when (selectedRoleIndex) {
                        0 -> Icons.Default.Person
                        1 -> Icons.Default.MedicalServices
                        else -> Icons.Default.AdminPanelSettings
                    },
                    contentDescription = null,
                    tint = primaryRoleColor,
                    modifier = Modifier.size(34.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Hospital Portal Login",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "Role-Based Access for Patients, Clinical Staff & Hospital Administration",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Hospital Facility Node Selector Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Connected Healthcare Node",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("ABDM Gateway Online", fontSize = 10.sp, color = Color(0xFF16A34A), fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = hospitalName,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    HospitalDirectory.hospitals.forEach { (code, _) ->
                        FilterChip(
                            selected = selectedHospitalCode == code,
                            onClick = { onHospitalCodeSelected(code) },
                            label = {
                                Text(
                                    code,
                                    fontSize = 10.sp,
                                    fontWeight = if (selectedHospitalCode == code) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            modifier = Modifier.testTag("chip_hosp_$code")
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Role Selector Tiles: Patient, Staff, Admin
        Text(
            text = "Select Authentication Flow:",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            RoleTile(
                title = "Patient",
                subtext = "Family & Care",
                icon = Icons.Default.Person,
                accentColor = Color(0xFF16A34A),
                isSelected = selectedRoleIndex == 0,
                onClick = { onRoleSelected(0) },
                modifier = Modifier.weight(1f),
                testTag = "tile_role_patient"
            )

            RoleTile(
                title = "Staff",
                subtext = "Doctor / RN",
                icon = Icons.Default.MedicalServices,
                accentColor = Color(0xFF2563EB),
                isSelected = selectedRoleIndex == 1,
                onClick = { onRoleSelected(1) },
                modifier = Modifier.weight(1f),
                testTag = "tile_role_staff"
            )

            RoleTile(
                title = "Admin",
                subtext = "Executive / Lean",
                icon = Icons.Default.AdminPanelSettings,
                accentColor = Color(0xFF7C3AED),
                isSelected = selectedRoleIndex == 2,
                onClick = { onRoleSelected(2) },
                modifier = Modifier.weight(1f),
                testTag = "tile_role_admin"
            )
        }
    }
}

@Composable
private fun RoleTile(
    title: String,
    subtext: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = ""
) {
    Surface(
        color = if (isSelected) accentColor else MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(10.dp),
        shadowElevation = if (isSelected) 3.dp else 1.dp,
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) accentColor else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
                shape = RoundedCornerShape(10.dp)
            )
            .testTag(testTag)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                color = if (isSelected) Color.White.copy(alpha = 0.25f) else accentColor.copy(alpha = 0.12f),
                shape = CircleShape,
                modifier = Modifier.size(34.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isSelected) Color.White else accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = subtext,
                fontSize = 10.sp,
                color = if (isSelected) Color.White.copy(alpha = 0.9f) else MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}
