package com.example.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HmsEhrConfig
import com.example.ui.components.KpiCard
import com.example.ui.viewmodel.HospitalViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HmsEhrSyncScreen(
    viewModel: HospitalViewModel,
    modifier: Modifier = Modifier
) {
    val hmsConfig by viewModel.hmsConfig.collectAsState()
    val isSyncing by viewModel.isHmsSyncing.collectAsState()
    val syncMessage by viewModel.hmsSyncMessage.collectAsState()
    val activeSession by viewModel.activeUserSession.collectAsState()

    val hospitalCode = activeSession?.hospitalCode ?: "SGH-RANCHI"
    val hospitalName = activeSession?.hospitalName ?: "Synergy Global Hospital"

    var endpointUrl by remember(hmsConfig) { mutableStateOf(hmsConfig?.endpointUrl ?: "https://hms.synergyhospital.org/api/v2/ehr-sync") }
    var systemType by remember(hmsConfig) { mutableStateOf(hmsConfig?.hmsSystemType ?: "ABDM FHIR v2 / OpenEMR Enterprise") }
    var apiKey by remember(hmsConfig) { mutableStateOf(hmsConfig?.apiKeyMasked ?: "sgh_live_sec_84712****312") }
    var autoSyncEnabled by remember(hmsConfig) { mutableStateOf(hmsConfig?.isAutoSyncEnabled ?: true) }
    var intervalMinutes by remember(hmsConfig) { mutableIntStateOf(hmsConfig?.autoSyncIntervalMinutes ?: 5) }

    val systemTypes = listOf(
        "ABDM FHIR v2 / OpenEMR Enterprise",
        "HL7 v2.8 / Epic FHIR Gateway",
        "Cerner Millennium FHIR R4",
        "Custom Hospital REST API (JSON)",
        "On-Premise PostgreSQL / DICOM PACS"
    )

    var typeDropdownExpanded by remember { mutableStateOf(false) }

    // Infinite rotation for spinning sync icon
    val infiniteTransition = rememberInfiniteTransition(label = "sync_spin")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spin_angle"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .testTag("hms_ehr_sync_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Hospital HMS & EHR Interoperability Hub",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Bi-directional Auto-Update Pipeline • $hospitalCode",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Surface(
                            color = Color(0xFFDCFCE7),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(modifier = Modifier.size(6.dp).background(Color(0xFF16A34A), CircleShape))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("FHIR Online", fontSize = 10.sp, color = Color(0xFF166534), fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        KpiCard(
                            value = "${hmsConfig?.totalRecordsSynced ?: 1420}",
                            label = "Records Synced",
                            subtext = "IPD + OPD + Vitals",
                            icon = Icons.Default.CloudDone,
                            isGood = true,
                            modifier = Modifier.weight(1f)
                        )

                        KpiCard(
                            value = "Every ${intervalMinutes}m",
                            label = "Sync Frequency",
                            subtext = if (autoSyncEnabled) "Auto-sync active" else "Manual sync only",
                            icon = Icons.Default.Speed,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Live Sync Trigger Box
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Trigger Immediate HMS Auto-Update",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Pull live patient admissions, bed turnover, and OPD token waiting times",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Button(
                            onClick = { viewModel.triggerHmsSync(hospitalCode) },
                            enabled = !isSyncing,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("btn_trigger_hms_sync")
                        ) {
                            if (isSyncing) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp).rotate(angle)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Syncing...", fontSize = 11.sp)
                            } else {
                                Icon(imageVector = Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Sync Now", fontSize = 11.sp)
                            }
                        }
                    }

                    if (syncMessage != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            color = Color.White.copy(alpha = 0.7f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(syncMessage ?: "", fontSize = 11.sp, color = Color(0xFF166534), fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }

        // HMS Configuration Form
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "HMS / EHR Connection Settings",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // System Type Dropdown
                    ExposedDropdownMenuBox(
                        expanded = typeDropdownExpanded,
                        onExpandedChange = { typeDropdownExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = systemType,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("EHR Standard / Gateway") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeDropdownExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = typeDropdownExpanded,
                            onDismissRequest = { typeDropdownExpanded = false }
                        ) {
                            systemTypes.forEach { t ->
                                DropdownMenuItem(text = { Text(t) }, onClick = { systemType = t; typeDropdownExpanded = false })
                            }
                        }
                    }

                    OutlinedTextField(
                        value = endpointUrl,
                        onValueChange = { endpointUrl = it },
                        label = { Text("HMS Endpoint Webhook / REST URL") },
                        leadingIcon = { Icon(Icons.Default.Link, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth().testTag("input_hms_endpoint"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = apiKey,
                        onValueChange = { apiKey = it },
                        label = { Text("ABDM / EHR API Token (Encrypted)") },
                        leadingIcon = { Icon(Icons.Default.Key, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Automatic Background Sync", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            Text("Auto-refreshes bed status & OPD queue", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Switch(
                            checked = autoSyncEnabled,
                            onCheckedChange = { autoSyncEnabled = it },
                            modifier = Modifier.testTag("switch_auto_sync")
                        )
                    }

                    Button(
                        onClick = {
                            val config = HmsEhrConfig(
                                hospitalCode = hospitalCode,
                                hospitalName = hospitalName,
                                hmsSystemType = systemType,
                                endpointUrl = endpointUrl,
                                apiKeyMasked = apiKey,
                                autoSyncIntervalMinutes = intervalMinutes,
                                isAutoSyncEnabled = autoSyncEnabled
                            )
                            viewModel.saveHmsConfig(config)
                        },
                        modifier = Modifier.fillMaxWidth().testTag("btn_save_hms_config")
                    ) {
                        Text("Save HMS Gateway Configuration")
                    }
                }
            }
        }

        // Cross-Platform Architectural Compatibility
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Devices, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Universal Cross-Platform Deployment",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "• Android: Native Jetpack Compose with offline-first Room database synchronization.\n• Web: Progressive Web App (PWA) and RESTful API endpoints for clinical workstations.\n• iOS: Shared Kotlin Multiplatform (KMP) data layer with native Swift UI rendering.\n• Telemetry: Real-time SSE / WebSockets stream live OPD queue tokens and ward bed occupancy directly to patient and staff screens.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 17.sp
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
