package com.example

import com.example.data.model.ThroughputMetrics
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ThroughputAnalyticsTest {

    @Test
    fun testThroughputMetricsCalculations() {
        val metrics = ThroughputMetrics(
            periodLabel = "All Months (Nov 2025 – Feb 2026)",
            totalAdmissions = 381,
            totalDischarges = 374,
            netCensusChange = 7,
            bedOccupancyRate = 76.8,
            bedTurnoverRate = 2.54,
            bedTurnoverIntervalHours = 18.4,
            avgDischargeTurnaroundMinutes = 180,
            targetDischargeTurnaroundMinutes = 45,
            actualAvgLos = 3.29,
            nationalBenchmarkLos = 4.20,
            leanTargetLos = 2.80
        )

        // National benchmark is 4.20, actual is 3.29, delta is 0.91 days
        // Bed days gained = 0.91 * 381 = ~346 bed-days
        assertTrue("Bed days gained should be positive", metrics.bedDaysGainedVsNational > 300)
        assertEquals(346, metrics.bedDaysGainedVsNational)

        // Turnaround reduction from 180 min to 45 min = (180 - 45)/180 * 100 = 75%
        assertEquals(75.0, metrics.turnaroundReductionPct, 0.1)

        // Net census change = 381 admissions - 374 discharges = 7
        assertEquals(7, metrics.netCensusChange)
    }
}
