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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LinearScale
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LeanWasteLog
import com.example.data.model.VsmStage
import com.example.ui.viewmodel.HospitalViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ValueStreamMappingScreen(
    viewModel: HospitalViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val wasteLogs by viewModel.wasteLogs.collectAsState()
    var showAddWasteDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.testTag("vsm_screen"),
        floatingActionButton = {
            if (selectedTab == 1) {
                FloatingActionButton(
                    onClick = { showAddWasteDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("fab_add_waste_log")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Log Lean Waste")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Header
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Value Stream Mapping & Waste Analysis",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Quantifying Value-Add (21.5%) vs Non-Value-Add Waiting Time (78.5%)",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    TabRow(selectedTabIndex = selectedTab) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = { Text("Patient VSM Journey", fontWeight = FontWeight.SemiBold) }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { Text("7 Wastes (Muda) Logs", fontWeight = FontWeight.SemiBold) }
                        )
                    }
                }
            }

            if (selectedTab == 0) {
                // VSM Journey Tab
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        // Current vs Future State Comparison Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Current State vs Lean Future State Target",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Current State", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
                                        Text("Total Lead Time: 78.5 hrs", fontSize = 11.sp)
                                        Text("Value-Add (VA): 21.5%", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                        Text("NVA Waiting: 78.5%", fontSize = 11.sp, color = Color(0xFFDC2626))
                                    }
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.ArrowForward, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                    }
                                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                                        Text("Future Target", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                                        Text("Target Lead Time: 42.0 hrs", fontSize = 11.sp)
                                        Text("Target VA: 48.0%", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF16A34A))
                                        Text("Target NVA: 52.0%", fontSize = 11.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                Text("Process Efficiency Ratio: 21.5% Value-Added", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                LinearProgressIndicator(
                                    progress = { 0.215f },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp),
                                    color = Color(0xFF16A34A),
                                    trackColor = Color(0xFFFCA5A5)
                                )
                            }
                        }
                    }

                    item {
                        Text(
                            text = "Hospital Journey Stages & Bottlenecks",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }

                    items(viewModel.vsmStages) { stage ->
                        VsmStageCard(stage = stage)
                    }
                }
            } else {
                // 7 Wastes (Muda) Logs Tab
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Surface(
                            color = Color(0xFFFEF3C7),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "7 Wastes in Healthcare (Taiichi Ohno's Muda)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF92400E)
                                )
                                Text(
                                    text = "Waiting • Motion • Overprocessing • Defects • Inventory • Transportation • Overproduction • Underutilized Talent",
                                    fontSize = 11.sp,
                                    color = Color(0xFFB45309),
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }
                    }

                    items(wasteLogs, key = { it.id }) { log ->
                        WasteLogCard(log = log)
                    }
                }
            }
        }

        // Add Waste Dialog
        if (showAddWasteDialog) {
            AddWasteDialog(
                onDismiss = { showAddWasteDialog = false },
                onConfirm = { cat, dept, desc, min, sev, root ->
                    viewModel.addWasteObservation(cat, dept, desc, min, sev, root)
                    showAddWasteDialog = false
                }
            )
        }
    }
}

@Composable
fun VsmStageCard(stage: VsmStage) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stage.stageName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                ) {
                    Text(
                        text = stage.department,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).background(Color(0xFF16A34A), CircleShape))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Value-Add: ${stage.processTimeMinutes} min", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF16A34A))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).background(Color(0xFFDC2626), CircleShape))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("NVA Wait: ${stage.waitTimeMinutes} min", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFDC2626))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                color = Color(0xFFFEE2E2).copy(alpha = 0.7f),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.Top) {
                    Icon(Icons.Default.ReportProblem, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text("Bottleneck (${stage.wasteType}):", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF991B1B))
                        Text(stage.keyBottleneck, fontSize = 11.sp, color = Color(0xFF7F1D1D))
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Surface(
                color = Color(0xFFDCFCE7).copy(alpha = 0.7f),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.Top) {
                    Icon(Icons.Default.Lightbulb, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text("Lean Countermeasure:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF166534))
                        Text(stage.leanCountermeasure, fontSize = 11.sp, color = Color(0xFF14532D))
                    }
                }
            }
        }
    }
}

@Composable
fun WasteLogCard(log: LeanWasteLog) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = when (log.severity) {
                        "Critical" -> Color(0xFFFEE2E2)
                        "High" -> Color(0xFFFEF3C7)
                        else -> Color(0xFFE0F2FE)
                    },
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "${log.wasteCategory} Waste",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (log.severity) {
                            "Critical" -> Color(0xFF991B1B)
                            "High" -> Color(0xFF92400E)
                            else -> Color(0xFF0369A1)
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Text(
                    text = "${log.estimatedMinutesLost} min lost",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFDC2626)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(text = log.description, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)

            if (log.rootCause.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Root Cause: ${log.rootCause}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Dept: ${log.department} • Severity: ${log.severity}",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWasteDialog(
    onDismiss: () -> Unit,
    onConfirm: (category: String, dept: String, desc: String, minutesLost: Int, severity: String, rootCause: String) -> Unit
) {
    var category by remember { mutableStateOf("Waiting") }
    var department by remember { mutableStateOf("Billing / Discharge") }
    var description by remember { mutableStateOf("") }
    var minutesLostStr by remember { mutableStateOf("60") }
    var severity by remember { mutableStateOf("High") }
    var rootCause by remember { mutableStateOf("") }

    val categories = listOf("Waiting", "Motion", "Overprocessing", "Defects", "Inventory", "Transportation", "Overproduction", "Underutilized Talent")
    val departments = listOf("Billing / Discharge", "IPD Wards", "Emergency", "OPD Clinic", "OT Complex", "Pharmacy", "Radiology / Lab", "Housekeeping")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Lean Waste (Muda)") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                var catExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(expanded = catExpanded, onExpandedChange = { catExpanded = it }) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Waste Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = catExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = catExpanded, onDismissRequest = { catExpanded = false }) {
                        categories.forEach { c ->
                            DropdownMenuItem(text = { Text(c) }, onClick = { category = c; catExpanded = false })
                        }
                    }
                }

                var deptExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(expanded = deptExpanded, onExpandedChange = { deptExpanded = it }) {
                    OutlinedTextField(
                        value = department,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Department") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = deptExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = deptExpanded, onDismissRequest = { deptExpanded = false }) {
                        departments.forEach { d ->
                            DropdownMenuItem(text = { Text(d) }, onClick = { department = d; deptExpanded = false })
                        }
                    }
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Waste Observation") },
                    modifier = Modifier.fillMaxWidth().testTag("input_waste_desc")
                )

                OutlinedTextField(
                    value = minutesLostStr,
                    onValueChange = { minutesLostStr = it },
                    label = { Text("Est. Minutes Lost") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = rootCause,
                    onValueChange = { rootCause = it },
                    label = { Text("Root Cause") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val minutes = minutesLostStr.toIntOrNull() ?: 30
                    onConfirm(category, department, description.ifBlank { "Operational delay observed" }, minutes, severity, rootCause)
                },
                modifier = Modifier.testTag("btn_confirm_add_waste")
            ) {
                Text("Save Observation")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
