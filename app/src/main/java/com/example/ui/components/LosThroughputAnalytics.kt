package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DepartmentLosBenchmark
import com.example.data.model.DischargeHourDistribution
import com.example.data.model.ThroughputMetrics
import com.example.data.model.ThroughputTrendPoint

@Composable
fun LosThroughputAnalytics(
    metrics: ThroughputMetrics,
    dischargeHourDistributions: List<DischargeHourDistribution>,
    departmentBenchmarks: List<DepartmentLosBenchmark>,
    throughputTrends: List<ThroughputTrendPoint>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("los_throughput_analytics"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Core Throughput Scorecard Cards (2x2)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            KpiCard(
                value = "${metrics.totalAdmissions} / ${metrics.totalDischarges}",
                label = "Admissions / Discharges",
                subtext = "Net Flow: ${if (metrics.netCensusChange >= 0) "+${metrics.netCensusChange}" else "${metrics.netCensusChange}"}",
                icon = Icons.Default.CompareArrows,
                modifier = Modifier.weight(1f),
                testTag = "kpi_adm_disch_ratio"
            )

            KpiCard(
                value = "${metrics.bedOccupancyRate}%",
                label = "Bed Occupancy Rate (BOR)",
                subtext = if (metrics.bedOccupancyRate <= 85.0) "Optimal (<85%)" else "High Congestion",
                isGood = metrics.bedOccupancyRate <= 85.0,
                isAlert = metrics.bedOccupancyRate > 85.0,
                icon = Icons.Default.Speed,
                modifier = Modifier.weight(1f),
                testTag = "kpi_bor"
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            KpiCard(
                value = "${metrics.bedTurnoverRate} pts",
                label = "Bed Turnover Rate (BTR)",
                subtext = "Patients/bed/month",
                icon = Icons.Default.TrendingUp,
                modifier = Modifier.weight(1f),
                testTag = "kpi_btr"
            )

            KpiCard(
                value = "${metrics.bedTurnoverIntervalHours} hrs",
                label = "Turnover Interval (BTI)",
                subtext = "Bed vacant time",
                isGood = metrics.bedTurnoverIntervalHours <= 20.0,
                icon = Icons.Default.HourglassBottom,
                modifier = Modifier.weight(1f),
                testTag = "kpi_bti"
            )
        }

        // Discharge Turnaround Time (DTT) vs Historical Benchmark Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("card_discharge_turnaround_benchmark"),
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
                            text = "Discharge Turnaround Time (DTT)",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Doctor verbal discharge order to final billing gate pass",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Surface(
                        color = Color(0xFFDCFCE7),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "-70.8% Drop",
                            color = Color(0xFF166534),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                        Text("Pre-Lean Baseline", fontSize = 11.sp, color = Color(0xFF991B1B), fontWeight = FontWeight.SemiBold)
                        Text("180.4 min", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
                        Text("3.0 hours wait", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrendingDown,
                            contentDescription = null,
                            tint = Color(0xFF16A34A),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                        Text("Post-Lean Rounds", fontSize = 11.sp, color = Color(0xFF166534), fontWeight = FontWeight.SemiBold)
                        Text("52.6 min", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                        Text("Target: ≤ 45 min", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    color = Color(0xFFF0FDF4),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF16A34A),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Paired t-Test Significance: t = 4.82, p < 0.001 (Highly Significant reduction of 127.8 min per patient).",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF14532D)
                        )
                    }
                }
            }
        }

        // Discharge Time of Day Bottleneck Curve
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("card_discharge_hour_distribution"),
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
                            text = "Discharge Timing Bottleneck by Hour of Day",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Historical late afternoon discharge lag (57% post 2 PM) vs Lean 10 AM target",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                DischargeHourDistributionChart(
                    distributions = dischargeHourDistributions,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Monthly Throughput Velocity Chart
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("card_throughput_velocity"),
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
                            text = "Monthly Patient Throughput Velocity",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Admissions vs Discharges with Average LOS Trend Curve",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.Assessment,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                ThroughputVelocityChart(
                    trends = throughputTrends,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Department Length of Stay (LOS) Analytics & Benchmark Card
        DepartmentLosBenchmarkCard(
            benchmarks = departmentBenchmarks,
            hospitalAvgLos = metrics.actualAvgLos,
            hospitalNationalBenchmark = metrics.nationalBenchmarkLos,
            hospitalLeanTarget = metrics.leanTargetLos,
            modifier = Modifier.fillMaxWidth()
        )

        // Strategic Insight Pill Banner
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Insights,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Bed Days Gained: ${metrics.bedDaysGainedVsNational} Days / Cohort",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "At 3.29 days avg LOS, Synergy Global Hospital operates 22% faster than the national 4.2-day benchmark, liberating bed capacity for emergency admissions.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
