package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.LosThroughputAnalytics
import com.example.ui.viewmodel.HospitalViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LosThroughputScreen(
    viewModel: HospitalViewModel,
    modifier: Modifier = Modifier
) {
    val selectedMonthId by viewModel.selectedMonthId.collectAsState()
    val throughputMetrics by viewModel.currentThroughputMetrics.collectAsState()
    val dischargeHours = viewModel.dischargeHourDistributions
    val deptBenchmarks = viewModel.departmentBenchmarks
    val throughputTrends = viewModel.monthlyThroughputTrends

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(MaterialTheme.colorScheme.background)
            .testTag("los_throughput_screen")
    ) {
        // Header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Length of Stay (LOS) & Patient Throughput",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Throughput velocity, bed turnover metrics, and discharge timing benchmarks",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Month filter chips
                Text(
                    text = "ANALYSIS COHORT:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val months = listOf(
                        "all" to "All Months",
                        "nov" to "Nov 2025",
                        "dec" to "Dec 2025",
                        "jan" to "Jan 2026",
                        "feb" to "Feb 2026"
                    )
                    months.forEach { (id, label) ->
                        FilterChip(
                            selected = selectedMonthId == id,
                            onClick = { viewModel.selectMonth(id) },
                            label = { Text(label, fontSize = 12.sp) },
                            modifier = Modifier.testTag("throughput_month_$id")
                        )
                    }
                }
            }
        }

        // Throughput Component Body
        Column(modifier = Modifier.padding(16.dp)) {
            LosThroughputAnalytics(
                metrics = throughputMetrics,
                dischargeHourDistributions = dischargeHours,
                departmentBenchmarks = deptBenchmarks,
                throughputTrends = throughputTrends,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
