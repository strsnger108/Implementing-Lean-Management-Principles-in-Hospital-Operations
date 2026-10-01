package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ConsultantStat
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun MonthlyTrendChart(
    months: List<String> = listOf("Nov 2025", "Dec 2025", "Jan 2026", "Feb 2026"),
    admissions: List<Int> = listOf(103, 116, 84, 78),
    avgLOS: List<Double> = listOf(3.03, 3.04, 3.62, 3.72),
    modifier: Modifier = Modifier
) {
    var selectedIndex by remember { mutableIntStateOf(-1) }
    val primaryColor = MaterialTheme.colorScheme.primary
    val losLineColor = Color(0xFFDC2626)
    val gridColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)

    Column(modifier = modifier) {
        if (selectedIndex in admissions.indices) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${months[selectedIndex]}: ${admissions[selectedIndex]} Admissions | Avg LOS: ${String.format("%.2f", avgLOS[selectedIndex])} days",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryColor
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .testTag("trend_chart_canvas")
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            val slotWidth = size.width / months.size
                            val index = (offset.x / slotWidth).toInt().coerceIn(0, months.size - 1)
                            selectedIndex = if (selectedIndex == index) -1 else index
                        }
                    }
            ) {
                val w = size.width
                val h = size.height
                val bottomPadding = 36f
                val topPadding = 24f
                val leftPadding = 36f
                val rightPadding = 44f
                val chartWidth = w - leftPadding - rightPadding
                val chartHeight = h - topPadding - bottomPadding

                val maxAdmissions = 140f
                val minLOS = 2.0f
                val maxLOS = 4.5f

                // Draw horizontal grid lines
                val gridSteps = 4
                for (i in 0..gridSteps) {
                    val y = topPadding + (chartHeight / gridSteps) * i
                    drawLine(
                        color = gridColor,
                        start = Offset(leftPadding, y),
                        end = Offset(w - rightPadding, y),
                        strokeWidth = 1f
                    )
                }

                val barSlotWidth = chartWidth / months.size
                val barWidth = barSlotWidth * 0.45f
                val losPoints = mutableListOf<Offset>()

                months.forEachIndexed { i, month ->
                    val centerX = leftPadding + (i + 0.5f) * barSlotWidth
                    val barHeight = (admissions[i] / maxAdmissions) * chartHeight
                    val barTop = topPadding + (chartHeight - barHeight)

                    val isSelected = selectedIndex == i
                    val barColor = if (isSelected) primaryColor else primaryColor.copy(alpha = 0.75f)

                    // Draw Bar
                    drawRoundRect(
                        color = barColor,
                        topLeft = Offset(centerX - barWidth / 2, barTop),
                        size = Size(barWidth, barHeight),
                        cornerRadius = CornerRadius(6f, 6f)
                    )

                    // Compute LOS line point
                    val losRatio = ((avgLOS[i].toFloat() - minLOS) / (maxLOS - minLOS)).coerceIn(0f, 1f)
                    val losY = topPadding + chartHeight - (losRatio * chartHeight)
                    losPoints.add(Offset(centerX, losY))

                    // Draw month label
                    drawContext.canvas.nativeCanvas.apply {
                        val paint = android.graphics.Paint().apply {
                            color = android.graphics.Color.DKGRAY
                            textSize = 24f
                            textAlign = android.graphics.Paint.Align.CENTER
                            isAntiAlias = true
                        }
                        drawText(month.replace(" 20", "\n'"), centerX, h - 8f, paint)
                    }
                }

                // Draw LOS Trend line
                if (losPoints.size > 1) {
                    val path = Path().apply {
                        moveTo(losPoints[0].x, losPoints[0].y)
                        for (i in 1 until losPoints.size) {
                            lineTo(losPoints[i].x, losPoints[i].y)
                        }
                    }
                    drawPath(
                        path = path,
                        color = losLineColor,
                        style = Stroke(width = 6f)
                    )

                    losPoints.forEachIndexed { i, pt ->
                        drawCircle(color = Color.White, radius = 9f, center = pt)
                        drawCircle(color = losLineColor, radius = 6f, center = pt)

                        // Draw text value above point
                        drawContext.canvas.nativeCanvas.apply {
                            val paint = android.graphics.Paint().apply {
                                color = android.graphics.Color.parseColor("#DC2626")
                                textSize = 22f
                                textAlign = android.graphics.Paint.Align.CENTER
                                isFakeBoldText = true
                                isAntiAlias = true
                            }
                            drawText("${String.format("%.2f", avgLOS[i])}d", pt.x, pt.y - 12f, paint)
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(10.dp).background(primaryColor, RoundedCornerShape(2.dp)))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Admissions (Left)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.width(16.dp))
            Box(modifier = Modifier.size(10.dp).background(losLineColor, CircleShape))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Avg LOS Days (Right)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun LosDistributionBarChart(
    labels: List<String>,
    values: List<Int>,
    modifier: Modifier = Modifier
) {
    var selectedIndex by remember { mutableIntStateOf(-1) }
    val maxVal = (values.maxOrNull() ?: 100).toFloat()

    val shortColor = Color(0xFF16A34A) // <= 2 days
    val mediumColor = Color(0xFFD97706) // 3-5 days
    val longColor = Color(0xFFDC2626) // 6+ days

    Column(modifier = modifier) {
        if (selectedIndex in values.indices) {
            Text(
                text = "LOS ${labels[selectedIndex]} days: ${values[selectedIndex]} patients (${String.format("%.1f", (values[selectedIndex].toDouble() / values.sum()) * 100)}%)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .testTag("los_distribution_canvas")
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            val slotWidth = size.width / labels.size
                            val index = (offset.x / slotWidth).toInt().coerceIn(0, labels.size - 1)
                            selectedIndex = if (selectedIndex == index) -1 else index
                        }
                    }
            ) {
                val w = size.width
                val h = size.height
                val bottomPadding = 32f
                val topPadding = 20f
                val chartHeight = h - topPadding - bottomPadding
                val slotWidth = w / labels.size
                val barWidth = slotWidth * 0.72f

                values.forEachIndexed { i, count ->
                    val color = when {
                        i <= 2 -> shortColor
                        i <= 5 -> mediumColor
                        else -> longColor
                    }
                    val centerX = (i + 0.5f) * slotWidth
                    val barHeight = ((count / maxVal) * chartHeight).coerceAtLeast(4f)
                    val barTop = topPadding + (chartHeight - barHeight)
                    val isSelected = selectedIndex == i

                    drawRoundRect(
                        color = if (isSelected) color else color.copy(alpha = 0.85f),
                        topLeft = Offset(centerX - barWidth / 2, barTop),
                        size = Size(barWidth, barHeight),
                        cornerRadius = CornerRadius(4f, 4f)
                    )

                    // Draw count label
                    if (count > 15 || isSelected) {
                        drawContext.canvas.nativeCanvas.apply {
                            val paint = android.graphics.Paint().apply {
                                this.color = android.graphics.Color.DKGRAY
                                textSize = 20f
                                textAlign = android.graphics.Paint.Align.CENTER
                                isAntiAlias = true
                            }
                            drawText("$count", centerX, barTop - 4f, paint)
                        }
                    }

                    // Draw X axis label
                    drawContext.canvas.nativeCanvas.apply {
                        val paint = android.graphics.Paint().apply {
                            this.color = android.graphics.Color.GRAY
                            textSize = 20f
                            textAlign = android.graphics.Paint.Align.CENTER
                            isAntiAlias = true
                        }
                        drawText(labels[i], centerX, h - 8f, paint)
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).background(shortColor, CircleShape))
                Spacer(modifier = Modifier.width(4.dp))
                Text("0–2d Short", fontSize = 10.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).background(mediumColor, CircleShape))
                Spacer(modifier = Modifier.width(4.dp))
                Text("3–5d Medium", fontSize = 10.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).background(longColor, CircleShape))
                Spacer(modifier = Modifier.width(4.dp))
                Text("6+d Extended", fontSize = 10.sp)
            }
        }
    }
}

@Composable
fun ConsultantParetoChart(
    consultants: List<ConsultantStat>,
    modifier: Modifier = Modifier
) {
    var selectedIndex by remember { mutableIntStateOf(-1) }
    val maxCases = (consultants.maxOfOrNull { it.cases } ?: 130).toFloat()
    val barColor = MaterialTheme.colorScheme.primary
    val paretoLineColor = Color(0xFFD97706)

    Column(modifier = modifier) {
        if (selectedIndex in consultants.indices) {
            val c = consultants[selectedIndex]
            Text(
                text = "${c.name}: ${c.cases} cases (${String.format("%.1f", c.cumulativePct)}% cum.)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = barColor,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .testTag("pareto_chart_canvas")
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            val slotWidth = size.width / consultants.size
                            val index = (offset.x / slotWidth).toInt().coerceIn(0, consultants.size - 1)
                            selectedIndex = if (selectedIndex == index) -1 else index
                        }
                    }
            ) {
                val w = size.width
                val h = size.height
                val bottomPadding = 48f
                val topPadding = 20f
                val leftPadding = 32f
                val rightPadding = 32f
                val chartWidth = w - leftPadding - rightPadding
                val chartHeight = h - topPadding - bottomPadding
                val slotWidth = chartWidth / consultants.size
                val barWidth = slotWidth * 0.65f

                // Draw 80% Pareto guideline
                val y80 = topPadding + chartHeight * (1f - 0.80f)
                drawLine(
                    color = Color.LightGray,
                    start = Offset(leftPadding, y80),
                    end = Offset(w - rightPadding, y80),
                    strokeWidth = 2f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                )

                val cumPoints = mutableListOf<Offset>()

                consultants.forEachIndexed { i, stat ->
                    val centerX = leftPadding + (i + 0.5f) * slotWidth
                    val barHeight = (stat.cases / maxCases) * chartHeight
                    val barTop = topPadding + (chartHeight - barHeight)
                    val isSelected = selectedIndex == i

                    drawRoundRect(
                        color = if (isSelected) barColor else barColor.copy(alpha = 0.75f),
                        topLeft = Offset(centerX - barWidth / 2, barTop),
                        size = Size(barWidth, barHeight),
                        cornerRadius = CornerRadius(4f, 4f)
                    )

                    // Cumulative percent point
                    val cumY = topPadding + chartHeight * (1f - (stat.cumulativePct.toFloat() / 100f))
                    cumPoints.add(Offset(centerX, cumY))

                    // Draw short consultant label
                    val shortName = stat.name.replace("Dr. ", "").take(5)
                    drawContext.canvas.nativeCanvas.apply {
                        val paint = android.graphics.Paint().apply {
                            this.color = android.graphics.Color.DKGRAY
                            textSize = 18f
                            textAlign = android.graphics.Paint.Align.CENTER
                            isAntiAlias = true
                        }
                        drawText(shortName, centerX, h - 14f, paint)
                    }
                }

                // Draw Cumulative % Line
                if (cumPoints.size > 1) {
                    val path = Path().apply {
                        moveTo(cumPoints[0].x, cumPoints[0].y)
                        for (i in 1 until cumPoints.size) {
                            lineTo(cumPoints[i].x, cumPoints[i].y)
                        }
                    }
                    drawPath(
                        path = path,
                        color = paretoLineColor,
                        style = Stroke(width = 5f)
                    )
                    cumPoints.forEach { pt ->
                        drawCircle(color = Color.White, radius = 7f, center = pt)
                        drawCircle(color = paretoLineColor, radius = 4f, center = pt)
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(8.dp).background(barColor, RoundedCornerShape(2.dp)))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Cases by Consultant", fontSize = 11.sp)
            Spacer(modifier = Modifier.width(16.dp))
            Box(modifier = Modifier.size(8.dp).background(paretoLineColor, CircleShape))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Cumulative % (80/20 Rule)", fontSize = 11.sp)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LosDoughnutChart(
    sameDay: Int,
    shortStay: Int,
    mediumStay: Int,
    longStay: Int,
    modifier: Modifier = Modifier
) {
    val total = (sameDay + shortStay + mediumStay + longStay).coerceAtLeast(1)
    val colors = listOf(
        Color(0xFF16A34A), // Same-day
        Color(0xFF0284C7), // Short
        Color(0xFFD97706), // Medium
        Color(0xFFDC2626)  // Extended
    )
    val slices = listOf(
        "Same-Day (0d)" to sameDay,
        "Short (1–2d)" to shortStay,
        "Medium (3–5d)" to mediumStay,
        "Extended (6+d)" to longStay
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(150.dp)
                .testTag("los_doughnut_canvas"),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 32f
                val radius = (size.minDimension - strokeWidth) / 2
                val center = Offset(size.width / 2, size.height / 2)
                var currentAngle = -90f

                slices.forEachIndexed { i, slice ->
                    val sweepAngle = (slice.second.toFloat() / total) * 360f
                    if (sweepAngle > 0f) {
                        drawArc(
                            color = colors[i],
                            startAngle = currentAngle,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            topLeft = Offset(center.x - radius, center.y - radius),
                            size = Size(radius * 2, radius * 2),
                            style = Stroke(width = strokeWidth)
                        )
                        currentAngle += sweepAngle
                    }
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$total",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Valid LOS",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            slices.forEachIndexed { i, slice ->
                val pct = (slice.second.toDouble() / total) * 100
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(10.dp).background(colors[i], CircleShape))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(slice.first, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                    Text(
                        "${slice.second} (${String.format("%.1f", pct)}%)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
