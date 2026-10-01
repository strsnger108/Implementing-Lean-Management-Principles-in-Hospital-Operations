package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.ConsultantParetoChart
import com.example.ui.components.KpiCard
import com.example.ui.components.LeanInsightCard
import com.example.ui.components.LosDistributionBarChart
import com.example.ui.components.LosDoughnutChart
import com.example.ui.components.MonthlyTrendChart
import com.example.ui.viewmodel.HospitalViewModel

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: HospitalViewModel,
    onNavigateToVsm: () -> Unit,
    onNavigateToKaizen: () -> Unit,
    onNavigateToSimulator: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedMonthId by viewModel.selectedMonthId.collectAsState()
    val viewMode by viewModel.dashboardViewMode.collectAsState()
    val metrics by viewModel.currentMetrics.collectAsState()

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(MaterialTheme.colorScheme.background)
            .testTag("dashboard_screen")
    ) {
        // Hero Header Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0F172A),
                            Color(0xFF1E3A8A)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Surface(
                            color = Color(0xFF2563EB).copy(alpha = 0.3f),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = "SYNERGY GLOBAL HOSPITAL",
                                color = Color(0xFF93C5FD),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Lean Hospital Operations",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Executive Dashboard | Ranchi, Jharkhand | Nov 2025 – Feb 2026",
                            color = Color(0xFFCBD5E1),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    Image(
                        painter = painterResource(id = R.drawable.app_icon_lean_hospital),
                        contentDescription = "Lean Logo",
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    color = Color.White.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "MBA Quality Improvement Study: Gunjan Prakash (Dr. D. Y. Patil Vidyapeeth)",
                        color = Color(0xFFE2E8F0),
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Filter Bar: Month Selector & View Selector
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .testTag("filter_bar_card"),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "FILTER BY MONTH:",
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
                            modifier = Modifier.testTag("month_chip_$id")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "ANALYTICS FOCUS:",
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
                    val views = listOf(
                        "overview" to "Overview",
                        "los" to "LOS Focus",
                        "consultant" to "Consultant Workload"
                    )
                    views.forEach { (id, label) ->
                        FilterChip(
                            selected = viewMode == id,
                            onClick = { viewModel.selectViewMode(id) },
                            label = { Text(label, fontSize = 12.sp) },
                            modifier = Modifier.testTag("view_mode_$id")
                        )
                    }
                }
            }
        }

        // Active Period Label
        Text(
            text = "Active Cohort: ${metrics.label}",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 2.dp)
        )

        // KPI Cards Row (2x2 Grid / Flow)
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                KpiCard(
                    value = "${metrics.admissions}",
                    label = "Total Admissions",
                    subtext = "${String.format("%.1f", metrics.avgPerDay)} avg/day",
                    icon = Icons.Default.Hotel,
                    modifier = Modifier.weight(1f),
                    testTag = "kpi_total_admissions"
                )
                KpiCard(
                    value = "${String.format("%.2f", metrics.avgLOS)} d",
                    label = "Avg Length of Stay",
                    subtext = if (metrics.avgLOS <= 2.8) "Target Met" else "Target ≤ 2.8 d",
                    isAlert = metrics.avgLOS > 3.3,
                    isGood = metrics.avgLOS <= 2.8,
                    icon = Icons.Default.HourglassBottom,
                    modifier = Modifier.weight(1f),
                    testTag = "kpi_avg_los"
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                KpiCard(
                    value = "${String.format("%.1f", metrics.sameDayPct)}%",
                    label = "Same-Day Discharge",
                    subtext = "${metrics.sameDay} Patients (0d)",
                    isGood = true,
                    icon = Icons.Default.CheckCircle,
                    modifier = Modifier.weight(1f),
                    testTag = "kpi_same_day"
                )
                KpiCard(
                    value = "${String.format("%.1f", metrics.longStayPct)}%",
                    label = "Extended Stay 6+d",
                    subtext = "${metrics.longStay} Patients",
                    isAlert = metrics.longStayPct > 15.0,
                    icon = Icons.Default.ReportProblem,
                    modifier = Modifier.weight(1f),
                    testTag = "kpi_long_stay"
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            KpiCard(
                value = "${String.format("%.1f", metrics.incompletePct)}% (${metrics.incomplete} files)",
                label = "Documentation Gap (Incomplete Records)",
                subtext = if (metrics.incompletePct < 2.0) "Within Lean Target" else "Lean Target: < 2.0%",
                isAlert = metrics.incompletePct > 5.0,
                isGood = metrics.incompletePct < 2.0,
                icon = Icons.Default.Assessment,
                modifier = Modifier.fillMaxWidth(),
                testTag = "kpi_incomplete_records"
            )
        }

        // Charts Section based on selected view mode
        Spacer(modifier = Modifier.height(10.dp))

        if (viewMode == "overview" || viewMode == "los") {
            // Chart 1: Monthly Admission & Avg LOS Trend
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
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
                        Text(
                            text = "Monthly Admissions & Avg LOS Trend",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Icon(
                            imageVector = Icons.Default.BarChart,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Text(
                        text = "Bar: Admissions | Red Line: Average Length of Stay (Days)",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    MonthlyTrendChart(modifier = Modifier.fillMaxWidth())
                }
            }

            // Chart 2: LOS Distribution
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Length of Stay (LOS) Distribution",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Histogram of patient days (0 to 18 days)",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    LosDistributionBarChart(
                        labels = viewModel.losDistributionLabels,
                        values = viewModel.losDistributionValues,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Chart 3: LOS Breakdown Doughnut
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
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
                        Text(
                            text = "LOS Category Breakdown",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Icon(
                            imageVector = Icons.Default.PieChart,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    LosDoughnutChart(
                        sameDay = metrics.sameDay,
                        shortStay = metrics.shortStay,
                        mediumStay = metrics.mediumStay,
                        longStay = metrics.longStay,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        if (viewMode == "overview" || viewMode == "consultant") {
            // Chart 4: Consultant Pareto Workload
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
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
                        Text(
                            text = "Consultant Workload Pareto (80/20 Rule)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Text(
                        text = "Bar: Cases admitted | Amber line: Cumulative percentage",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    ConsultantParetoChart(
                        consultants = viewModel.consultantStats,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Lean Improvement Insights Section
        Spacer(modifier = Modifier.height(12.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Lean Improvement Insights & Action Items",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Data-driven operational recommendations from hospital study",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            viewModel.insights.forEach { insight ->
                LeanInsightCard(insight = insight)
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
