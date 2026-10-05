package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import com.example.data.model.DepartmentLosBenchmark
import com.example.data.model.DischargeHourDistribution
import com.example.data.model.ThroughputTrendPoint

@Composable
fun DischargeHourDistributionChart(
    distributions: List<DischargeHourDistribution>,
    modifier: Modifier = Modifier
) {
    var selectedIndex by remember { mutableIntStateOf(-1) }
    val historicalColor = Color(0xFFDC2626) // Red - late afternoon spike
    val leanTargetColor = Color(0xFF16A34A) // Green - early morning target

    Column(modifier = modifier) {
        if (selectedIndex in distributions.indices) {
            val d = distributions[selectedIndex]
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${d.timeSlot}: ${d.patientCount} patients (${d.historicalPct}%)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = historicalColor
                    )
                    Text(
                        text = "Lean Target: ${d.leanTargetPct}%",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = leanTargetColor
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .testTag("discharge_hour_canvas")
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            val slotWidth = size.width / distributions.size
                            val index = (offset.x / slotWidth).toInt().coerceIn(0, distributions.size - 1)
                            selectedIndex = if (selectedIndex == index) -1 else index
                        }
                    }
            ) {
                val w = size.width
                val h = size.height
                val bottomPadding = 42f
                val topPadding = 24f
                val leftPadding = 28f
                val rightPadding = 28f
                val chartWidth = w - leftPadding - rightPadding
                val chartHeight = h - topPadding - bottomPadding
                val slotWidth = chartWidth / distributions.size

                val maxPct = 50f

                // Draw background guidelines
                for (step in listOf(10f, 25f, 40f)) {
                    val y = topPadding + chartHeight * (1f - (step / maxPct))
                    drawLine(
                        color = Color.LightGray.copy(alpha = 0.5f),
                        start = Offset(leftPadding, y),
                        end = Offset(w - rightPadding, y),
                        strokeWidth = 1f
                    )
                }

                val histPoints = mutableListOf<Offset>()
                val targetPoints = mutableListOf<Offset>()

                distributions.forEachIndexed { i, d ->
                    val centerX = leftPadding + (i + 0.5f) * slotWidth
                    val histY = topPadding + chartHeight * (1f - (d.historicalPct.toFloat() / maxPct)).coerceIn(0f, 1f)
                    val targetY = topPadding + chartHeight * (1f - (d.leanTargetPct.toFloat() / maxPct)).coerceIn(0f, 1f)

                    histPoints.add(Offset(centerX, histY))
                    targetPoints.add(Offset(centerX, targetY))

                    // Draw vertical guide if selected
                    if (selectedIndex == i) {
                        drawLine(
                            color = Color.LightGray,
                            start = Offset(centerX, topPadding),
                            end = Offset(centerX, topPadding + chartHeight),
                            strokeWidth = 2f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
                        )
                    }

                    // Draw label below
                    drawContext.canvas.nativeCanvas.apply {
                        val paint = android.graphics.Paint().apply {
                            color = android.graphics.Color.DKGRAY
                            textSize = 18f
                            textAlign = android.graphics.Paint.Align.CENTER
                            isAntiAlias = true
                        }
                        val label = d.timeSlot.replace("–", "\n")
                        drawText(label, centerX, h - 8f, paint)
                    }
                }

                // Draw Historical Curve (Red)
                if (histPoints.size > 1) {
                    val histPath = Path().apply {
                        moveTo(histPoints[0].x, histPoints[0].y)
                        for (i in 1 until histPoints.size) {
                            val prev = histPoints[i - 1]
                            val curr = histPoints[i]
                            val cx = (prev.x + curr.x) / 2
                            cubicTo(cx, prev.y, cx, curr.y, curr.x, curr.y)
                        }
                    }
                    drawPath(histPath, color = historicalColor, style = Stroke(width = 6f))

                    histPoints.forEachIndexed { i, pt ->
                        drawCircle(color = Color.White, radius = 7f, center = pt)
                        drawCircle(color = historicalColor, radius = 4.5f, center = pt)
                    }
                }

                // Draw Lean Target Curve (Green)
                if (targetPoints.size > 1) {
                    val targetPath = Path().apply {
                        moveTo(targetPoints[0].x, targetPoints[0].y)
                        for (i in 1 until targetPoints.size) {
                            val prev = targetPoints[i - 1]
                            val curr = targetPoints[i]
                            val cx = (prev.x + curr.x) / 2
                            cubicTo(cx, prev.y, cx, curr.y, curr.x, curr.y)
                        }
                    }
                    drawPath(
                        path = targetPath,
                        color = leanTargetColor,
                        style = Stroke(
                            width = 5f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
                        )
                    )

                    targetPoints.forEachIndexed { i, pt ->
                        drawCircle(color = Color.White, radius = 6f, center = pt)
                        drawCircle(color = leanTargetColor, radius = 4f, center = pt)
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
            Box(modifier = Modifier.size(8.dp).background(historicalColor, CircleShape))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Historical Discharges (Late 2–5 PM Spike)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.width(16.dp))
            Box(modifier = Modifier.size(8.dp).background(leanTargetColor, CircleShape))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Lean Target (10 AM–12 PM Morning)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun ThroughputVelocityChart(
    trends: List<ThroughputTrendPoint>,
    modifier: Modifier = Modifier
) {
    var selectedIndex by remember { mutableIntStateOf(-1) }
    val admissionColor = Color(0xFF2563EB) // Blue
    val dischargeColor = Color(0xFF0D9488) // Teal
    val losLineColor = Color(0xFFDC2626)   // Red

    Column(modifier = modifier) {
        if (selectedIndex in trends.indices) {
            val t = trends[selectedIndex]
            Text(
                text = "${t.period}: ${t.admissions} Adm vs ${t.discharges} Disch (Net: ${t.admissions - t.discharges}) | Avg LOS: ${t.avgLOS}d | BOR: ${t.occupancyRate}%",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .testTag("throughput_velocity_canvas")
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            val slotWidth = size.width / trends.size
                            val index = (offset.x / slotWidth).toInt().coerceIn(0, trends.size - 1)
                            selectedIndex = if (selectedIndex == index) -1 else index
                        }
                    }
            ) {
                val w = size.width
                val h = size.height
                val bottomPadding = 32f
                val topPadding = 20f
                val leftPadding = 30f
                val rightPadding = 30f
                val chartWidth = w - leftPadding - rightPadding
                val chartHeight = h - topPadding - bottomPadding
                val slotWidth = chartWidth / trends.size
                val barWidth = slotWidth * 0.30f
                val maxVolume = 140f

                val losPoints = mutableListOf<Offset>()

                trends.forEachIndexed { i, t ->
                    val centerX = leftPadding + (i + 0.5f) * slotWidth

                    // Draw Admissions Bar (Left)
                    val admHeight = (t.admissions / maxVolume) * chartHeight
                    val admTop = topPadding + (chartHeight - admHeight)
                    drawRoundRect(
                        color = admissionColor,
                        topLeft = Offset(centerX - barWidth - 2f, admTop),
                        size = Size(barWidth, admHeight),
                        cornerRadius = CornerRadius(4f, 4f)
                    )

                    // Draw Discharges Bar (Right)
                    val disHeight = (t.discharges / maxVolume) * chartHeight
                    val disTop = topPadding + (chartHeight - disHeight)
                    drawRoundRect(
                        color = dischargeColor,
                        topLeft = Offset(centerX + 2f, disTop),
                        size = Size(barWidth, disHeight),
                        cornerRadius = CornerRadius(4f, 4f)
                    )

                    // Line point for LOS (scale 2.0 to 4.5 days)
                    val losRatio = ((t.avgLOS.toFloat() - 2.0f) / 2.5f).coerceIn(0f, 1f)
                    val losY = topPadding + chartHeight * (1f - losRatio)
                    losPoints.add(Offset(centerX, losY))

                    // Period Label
                    drawContext.canvas.nativeCanvas.apply {
                        val paint = android.graphics.Paint().apply {
                            color = android.graphics.Color.DKGRAY
                            textSize = 20f
                            textAlign = android.graphics.Paint.Align.CENTER
                            isAntiAlias = true
                        }
                        drawText(t.period, centerX, h - 8f, paint)
                    }
                }

                // LOS Trend Line
                if (losPoints.size > 1) {
                    val path = Path().apply {
                        moveTo(losPoints[0].x, losPoints[0].y)
                        for (i in 1 until losPoints.size) {
                            lineTo(losPoints[i].x, losPoints[i].y)
                        }
                    }
                    drawPath(path, color = losLineColor, style = Stroke(width = 5f))
                    losPoints.forEach { pt ->
                        drawCircle(color = Color.White, radius = 6f, center = pt)
                        drawCircle(color = losLineColor, radius = 4f, center = pt)
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(8.dp).background(admissionColor, RoundedCornerShape(2.dp)))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Admissions", fontSize = 11.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Box(modifier = Modifier.size(8.dp).background(dischargeColor, RoundedCornerShape(2.dp)))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Discharges", fontSize = 11.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Box(modifier = Modifier.size(8.dp).background(losLineColor, CircleShape))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Avg LOS Days", fontSize = 11.sp)
        }
    }
}

@Composable
fun DepartmentBenchmarkComparisonChart(
    benchmarks: List<DepartmentLosBenchmark>,
    modifier: Modifier = Modifier
) {
    val nationalColor = Color(0xFF94A3B8)
    val actualColor = Color(0xFF2563EB)
    val targetColor = Color(0xFF16A34A)

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        benchmarks.forEach { b ->
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = b.department,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = when (b.status) {
                            "Benchmark Leader" -> Color(0xFFDCFCE7)
                            "Within Target" -> Color(0xFFFEF3C7)
                            else -> Color(0xFFFEE2E2)
                        }
                    ) {
                        Text(
                            text = "${b.actualLos}d vs ${b.nationalBenchmark}d Nat.",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (b.status) {
                                "Benchmark Leader" -> Color(0xFF166534)
                                "Within Target" -> Color(0xFFB45309)
                                else -> Color(0xFF991B1B)
                            },
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Comparative bars
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(14.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(4.dp))
                ) {
                    val maxScale = 7.0f

                    // National benchmark marker
                    val natRatio = (b.nationalBenchmark.toFloat() / maxScale).coerceIn(0f, 1f)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(natRatio)
                            .height(14.dp)
                            .background(nationalColor.copy(alpha = 0.35f), RoundedCornerShape(4.dp))
                    )

                    // Actual LOS bar
                    val actRatio = (b.actualLos.toFloat() / maxScale).coerceIn(0f, 1f)
                    val barColor = when {
                        b.actualLos <= b.leanTarget -> Color(0xFF16A34A)
                        b.actualLos <= b.nationalBenchmark -> Color(0xFF2563EB)
                        else -> Color(0xFFDC2626)
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(actRatio)
                            .height(14.dp)
                            .background(barColor, RoundedCornerShape(4.dp))
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Target: ${b.leanTarget}d", fontSize = 10.sp, color = Color(0xFF16A34A))
                    Text("Throughput: ${b.monthlyThroughput} patients", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Nat. Avg: ${b.nationalBenchmark}d", fontSize = 10.sp, color = Color.Gray)
                }
            }
        }
    }
}
