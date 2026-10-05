package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DepartmentLosBenchmark
import kotlin.math.roundToInt

/**
 * Length of Stay (LOS) Analytics Dashboard Card
 * Displays average patient stay times and identifies clinical departments
 * exceeding hospital/national benchmarks using an interactive comparative bar chart.
 */
@Composable
fun DepartmentLosBenchmarkCard(
    benchmarks: List<DepartmentLosBenchmark>,
    hospitalAvgLos: Double = 3.29,
    hospitalNationalBenchmark: Double = 4.20,
    hospitalLeanTarget: Double = 2.80,
    modifier: Modifier = Modifier
) {
    // 0: All Departments, 1: Exceeding Benchmark Only, 2: Within/Below Benchmark
    var selectedFilter by remember { mutableIntStateOf(0) }
    var selectedDepartmentIndex by remember { mutableIntStateOf(-1) }

    val exceedingDepartments = remember(benchmarks) {
        benchmarks.filter { it.actualLos > it.nationalBenchmark }
    }

    val displayedBenchmarks = remember(benchmarks, selectedFilter) {
        when (selectedFilter) {
            1 -> benchmarks.filter { it.actualLos > it.nationalBenchmark }
            2 -> benchmarks.filter { it.actualLos <= it.nationalBenchmark }
            else -> benchmarks
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("card_los_analytics_benchmark"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = Color(0xFFEFF6FF),
                        shape = CircleShape,
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.BarChart,
                                contentDescription = null,
                                tint = Color(0xFF2563EB),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Length of Stay (LOS) Analytics",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Department Stay Times vs Hospital Benchmark",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Alert Badge if departments exceed benchmark
                if (exceedingDepartments.isNotEmpty()) {
                    Surface(
                        color = Color(0xFFFEE2E2),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.testTag("badge_exceeding_departments")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color(0xFFDC2626),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${exceedingDepartments.size} Exceeds Benchmark",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFDC2626)
                            )
                        }
                    }
                } else {
                    Surface(
                        color = Color(0xFFDCFCE7),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF16A34A),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "All Departments Optimal",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF16A34A)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Hospital-Wide ALOS Metric Strip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    color = Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("Hospital ALOS", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = "${hospitalAvgLos} Days",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (hospitalAvgLos > hospitalNationalBenchmark) Color(0xFFDC2626) else Color(0xFF16A34A)
                        )
                        Text("Active patient cohort", fontSize = 10.sp, color = Color.Gray)
                    }
                }

                Surface(
                    color = Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("Private Benchmark", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${hospitalNationalBenchmark} Days", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569))
                        Text("NABH / Nat. Private", fontSize = 10.sp, color = Color.Gray)
                    }
                }

                Surface(
                    color = Color(0xFFDCFCE7).copy(alpha = 0.5f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("Lean Target", fontSize = 11.sp, color = Color(0xFF166534), fontWeight = FontWeight.SemiBold)
                        Text("${hospitalLeanTarget} Days", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                        Text("Target LOS (Kaizen)", fontSize = 10.sp, color = Color(0xFF166534))
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Filter Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Filter:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                FilterChip(
                    selected = selectedFilter == 0,
                    onClick = {
                        selectedFilter = 0
                        selectedDepartmentIndex = -1
                    },
                    label = { Text("All (${benchmarks.size})", fontSize = 11.sp) },
                    modifier = Modifier.height(28.dp)
                )
                FilterChip(
                    selected = selectedFilter == 1,
                    onClick = {
                        selectedFilter = 1
                        selectedDepartmentIndex = -1
                    },
                    label = {
                        Text("Exceeding (${exceedingDepartments.size})", fontSize = 11.sp)
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFDC2626),
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.height(28.dp)
                )
                FilterChip(
                    selected = selectedFilter == 2,
                    onClick = {
                        selectedFilter = 2
                        selectedDepartmentIndex = -1
                    },
                    label = { Text("Within Benchmark", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF16A34A),
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.height(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Chart Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(10.dp).background(Color(0xFFDC2626), RoundedCornerShape(2.dp)))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Exceeds Benchmark", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFDC2626))
                    Spacer(modifier = Modifier.width(10.dp))
                    Box(modifier = Modifier.size(10.dp).background(Color(0xFF16A34A), RoundedCornerShape(2.dp)))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Optimal / Leader", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF16A34A))
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(10.dp).border(1.5.dp, Color(0xFF475569), RoundedCornerShape(2.dp)))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Benchmark Target", fontSize = 10.sp, color = Color(0xFF475569))
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Interactive Bar Chart Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFFAFAFA))
                    .testTag("los_department_benchmark_canvas")
            ) {
                DepartmentLosBarChartCanvas(
                    benchmarks = displayedBenchmarks,
                    selectedIndex = selectedDepartmentIndex,
                    onSelectIndex = { index ->
                        selectedDepartmentIndex = if (selectedDepartmentIndex == index) -1 else index
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Selected Department Detail or Prominent Warning Callout
            AnimatedVisibility(
                visible = selectedDepartmentIndex in displayedBenchmarks.indices,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                val selectedDept = displayedBenchmarks.getOrNull(selectedDepartmentIndex)
                if (selectedDept != null) {
                    val isExceeding = selectedDept.actualLos > selectedDept.nationalBenchmark
                    val variance = selectedDept.actualLos - selectedDept.nationalBenchmark
                    val variancePct = ((variance / selectedDept.nationalBenchmark) * 100).roundToInt()

                    Surface(
                        color = if (isExceeding) Color(0xFFFEF2F2) else Color(0xFFF0FDF4),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = if (isExceeding) Color(0xFFFCA5A5) else Color(0xFF86EFAC),
                                shape = RoundedCornerShape(8.dp)
                            )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (isExceeding) Icons.Default.ReportProblem else Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = if (isExceeding) Color(0xFFDC2626) else Color(0xFF16A34A),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${selectedDept.department} Performance",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isExceeding) Color(0xFF991B1B) else Color(0xFF166534)
                                    )
                                }

                                Surface(
                                    color = if (isExceeding) Color(0xFFDC2626) else Color(0xFF16A34A),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = if (isExceeding) "+${"%.2f".format(variance)}d Overrun (+${variancePct}%)" else "-${"%.2f".format(-variance)}d Below Benchmark",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Actual: ${selectedDept.actualLos}d • Benchmark: ${selectedDept.nationalBenchmark}d • Lean Target: ${selectedDept.leanTarget}d",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Throughput: ${selectedDept.monthlyThroughput} pts",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Clinical insight / root cause
                            val insightText = when {
                                selectedDept.department.contains("Ortho", ignoreCase = true) ->
                                    "Primary Delay Driver: Post-operative rehabilitation sign-off and surgical implant billing verification delay morning discharges."
                                selectedDept.department.contains("Medicine", ignoreCase = true) ->
                                    "Primary Delay Driver: Serial diagnostic test waiting (KFT/CBC re-verification) and multi-consultant countersignatures."
                                selectedDept.department.contains("Surgery", ignoreCase = true) ->
                                    "Benchmark Success: Early post-op ambulation protocol and standardized clinical pathways ensure rapid recovery."
                                selectedDept.department.contains("ICU", ignoreCase = true) ->
                                    "Primary Delay Driver: Step-down HDU/ward bed availability delay transfer out of intensive care."
                                else ->
                                    "Standard discharge turnaround: 125 minutes average physician verification time."
                            }

                            Text(
                                text = "💡 $insightText",
                                fontSize = 11.sp,
                                color = if (isExceeding) Color(0xFF7F1D1D) else Color(0xFF14532D)
                            )
                        }
                    }
                }
            }

            // Exceeding Departments Alert Banner (visible by default when none is tapped)
            if (selectedDepartmentIndex !in displayedBenchmarks.indices && exceedingDepartments.isNotEmpty()) {
                val primaryExceeding = exceedingDepartments.first()
                val delta = primaryExceeding.actualLos - primaryExceeding.nationalBenchmark
                Surface(
                    color = Color(0xFFFEF2F2),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFFFCA5A5), RoundedCornerShape(8.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color(0xFFDC2626),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Action Required: ${primaryExceeding.department} Exceeds Benchmark",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF991B1B)
                            )
                            Text(
                                text = "${primaryExceeding.department} stay time of ${primaryExceeding.actualLos} days exceeds the ${primaryExceeding.nationalBenchmark}-day benchmark by +${"%.2f".format(delta)} days. Tap the bar above for Kaizen countermeasures.",
                                fontSize = 11.sp,
                                color = Color(0xFF7F1D1D)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Custom Canvas Bar Chart for Department Length of Stay vs Benchmarks
 */
@Composable
private fun DepartmentLosBarChartCanvas(
    benchmarks: List<DepartmentLosBenchmark>,
    selectedIndex: Int,
    onSelectIndex: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (benchmarks.isEmpty()) {
        Box(contentAlignment = Alignment.Center, modifier = modifier.fillMaxSize()) {
            Text("No department benchmarks to display", fontSize = 12.sp, color = Color.Gray)
        }
        return
    }

    val maxLos = remember(benchmarks) {
        val maxVal = benchmarks.maxOfOrNull { maxOf(it.actualLos, it.nationalBenchmark) } ?: 6.0
        (maxVal * 1.25).toFloat()
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(benchmarks) {
                detectTapGestures { offset ->
                    val totalWidth = size.width
                    val slotWidth = totalWidth / benchmarks.size
                    val index = (offset.x / slotWidth).toInt().coerceIn(0, benchmarks.size - 1)
                    onSelectIndex(index)
                }
            }
    ) {
        val w = size.width
        val h = size.height
        val bottomMargin = 40f
        val topMargin = 28f
        val chartHeight = h - bottomMargin - topMargin
        val slotWidth = w / benchmarks.size

        // Horizontal Gridlines & Values (0d, 2d, 4d, 6d)
        val gridSteps = 4
        val stepDays = maxLos / gridSteps
        val gridPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.LTGRAY
            textSize = 20f
            textAlign = android.graphics.Paint.Align.RIGHT
            isAntiAlias = true
        }

        for (i in 0..gridSteps) {
            val days = i * stepDays
            val y = topMargin + chartHeight * (1f - (days / maxLos))
            drawLine(
                color = Color(0xFFE2E8F0),
                start = Offset(0f, y),
                end = Offset(w, y),
                strokeWidth = 1f
            )
            drawContext.canvas.nativeCanvas.drawText(
                "${"%.1f".format(days)}d",
                w - 8f,
                y - 4f,
                gridPaint
            )
        }

        // Draw each department's grouped bars
        benchmarks.forEachIndexed { index, b ->
            val slotStart = index * slotWidth
            val slotCenter = slotStart + slotWidth / 2f
            val isSelected = selectedIndex == index
            val isExceeding = b.actualLos > b.nationalBenchmark

            val barWidth = (slotWidth * 0.35f).coerceIn(12f, 28f)
            val spacing = barWidth * 0.2f

            // Actual LOS Bar Height
            val actHeight = (b.actualLos.toFloat() / maxLos) * chartHeight
            val actTop = topMargin + chartHeight - actHeight
            val actLeft = slotCenter - barWidth - spacing / 2f

            // Benchmark Bar Height
            val benchHeight = (b.nationalBenchmark.toFloat() / maxLos) * chartHeight
            val benchTop = topMargin + chartHeight - benchHeight
            val benchLeft = slotCenter + spacing / 2f

            // Benchmark Bar Color
            val benchColor = Color(0xFF94A3B8).copy(alpha = 0.5f)
            val actColor = when {
                isExceeding -> Color(0xFFDC2626)
                b.actualLos <= b.leanTarget -> Color(0xFF16A34A)
                else -> Color(0xFF2563EB)
            }

            // Draw Selected Background highlight
            if (isSelected) {
                drawRoundRect(
                    color = actColor.copy(alpha = 0.08f),
                    topLeft = Offset(slotStart + 4f, 4f),
                    size = Size(slotWidth - 8f, h - 6f),
                    cornerRadius = CornerRadius(8f, 8f)
                )
            }

            // Draw Benchmark Bar (Reference Bar)
            drawRoundRect(
                color = benchColor,
                topLeft = Offset(benchLeft, benchTop),
                size = Size(barWidth, benchHeight),
                cornerRadius = CornerRadius(4f, 4f)
            )

            // Draw Actual LOS Bar
            drawRoundRect(
                color = actColor,
                topLeft = Offset(actLeft, actTop),
                size = Size(barWidth, actHeight),
                cornerRadius = CornerRadius(4f, 4f)
            )

            // Draw value labels above bars
            val valuePaint = android.graphics.Paint().apply {
                color = if (isExceeding) android.graphics.Color.parseColor("#DC2626") else android.graphics.Color.DKGRAY
                textSize = 20f
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                textAlign = android.graphics.Paint.Align.CENTER
                isAntiAlias = true
            }

            drawContext.canvas.nativeCanvas.drawText(
                "${b.actualLos}d",
                actLeft + barWidth / 2f,
                actTop - 6f,
                valuePaint
            )

            // Benchmark value above benchmark bar
            val benchTextPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.GRAY
                textSize = 18f
                textAlign = android.graphics.Paint.Align.CENTER
                isAntiAlias = true
            }
            drawContext.canvas.nativeCanvas.drawText(
                "${b.nationalBenchmark}d",
                benchLeft + barWidth / 2f,
                benchTop - 6f,
                benchTextPaint
            )

            // Warning Exclamation Flag if Exceeding
            if (isExceeding) {
                val flagPaint = android.graphics.Paint().apply {
                    color = android.graphics.Color.parseColor("#DC2626")
                    textSize = 22f
                    typeface = android.graphics.Typeface.DEFAULT_BOLD
                    textAlign = android.graphics.Paint.Align.CENTER
                    isAntiAlias = true
                }
                drawContext.canvas.nativeCanvas.drawText(
                    "⚠️",
                    slotCenter,
                    topMargin - 10f,
                    flagPaint
                )
            }

            // Department Abbreviation Label below bar
            val labelText = when {
                b.department.contains("Medicine", ignoreCase = true) -> "Med"
                b.department.contains("Surgery", ignoreCase = true) -> "Surg"
                b.department.contains("Ortho", ignoreCase = true) -> "Ortho"
                b.department.contains("Obstetrics", ignoreCase = true) -> "OB/Gyn"
                b.department.contains("ICU", ignoreCase = true) -> "ICU"
                b.department.contains("Pediatrics", ignoreCase = true) -> "Peds"
                else -> b.department.take(5)
            }

            val labelPaint = android.graphics.Paint().apply {
                color = if (isExceeding) android.graphics.Color.parseColor("#B91C1C") else android.graphics.Color.BLACK
                textSize = 22f
                typeface = if (isExceeding) android.graphics.Typeface.DEFAULT_BOLD else android.graphics.Typeface.DEFAULT
                textAlign = android.graphics.Paint.Align.CENTER
                isAntiAlias = true
            }

            drawContext.canvas.nativeCanvas.drawText(
                labelText,
                slotCenter,
                h - 12f,
                labelPaint
            )
        }
    }
}
