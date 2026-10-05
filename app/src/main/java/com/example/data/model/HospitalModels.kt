package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

data class MonthlyMetrics(
    val monthId: String,
    val label: String,
    val admissions: Int,
    val validLOS: Int,
    val avgLOS: Double,
    val sameDay: Int,
    val shortStay: Int,
    val mediumStay: Int,
    val longStay: Int,
    val incomplete: Int,
    val dailyAdmissions: List<Int> = emptyList()
) {
    val sameDayPct: Double get() = if (validLOS > 0) (sameDay.toDouble() / validLOS) * 100 else 0.0
    val longStayPct: Double get() = if (validLOS > 0) (longStay.toDouble() / validLOS) * 100 else 0.0
    val incompletePct: Double get() = if (admissions > 0) (incomplete.toDouble() / admissions) * 100 else 0.0
    val avgPerDay: Double get() = (admissions.toDouble() / (dailyAdmissions.size.takeIf { it > 0 } ?: 30))
}

data class ConsultantStat(
    val name: String,
    val cases: Int,
    val cumulativePct: Double
)

data class LeanInsight(
    val id: String,
    val title: String,
    val description: String,
    val actionItem: String,
    val type: InsightType
)

enum class InsightType {
    WARNING, INFO, SUCCESS
}

data class VsmStage(
    val stageName: String,
    val department: String,
    val processTimeMinutes: Int, // Value-added
    val waitTimeMinutes: Int,    // Non-value-added
    val wasteType: String,
    val keyBottleneck: String,
    val leanCountermeasure: String
)

data class ThroughputMetrics(
    val periodLabel: String,
    val totalAdmissions: Int,
    val totalDischarges: Int,
    val netCensusChange: Int,
    val bedOccupancyRate: Double, // e.g. 76.5%
    val bedTurnoverRate: Double,  // e.g. 2.54 patients/bed/month
    val bedTurnoverIntervalHours: Double, // e.g. 18.4 hours
    val avgDischargeTurnaroundMinutes: Int, // 180 min baseline
    val targetDischargeTurnaroundMinutes: Int = 45,
    val actualAvgLos: Double,
    val nationalBenchmarkLos: Double = 4.20,
    val leanTargetLos: Double = 2.80
) {
    val bedDaysGainedVsNational: Int
        get() = ((nationalBenchmarkLos - actualAvgLos) * totalAdmissions).toInt().coerceAtLeast(0)
    val turnaroundReductionPct: Double
        get() = ((avgDischargeTurnaroundMinutes - targetDischargeTurnaroundMinutes).toDouble() / avgDischargeTurnaroundMinutes) * 100.0
}

data class DischargeHourDistribution(
    val timeSlot: String,
    val historicalPct: Double,
    val leanTargetPct: Double,
    val patientCount: Int
)

data class DepartmentLosBenchmark(
    val department: String,
    val actualLos: Double,
    val nationalBenchmark: Double,
    val leanTarget: Double,
    val monthlyThroughput: Int,
    val avgDischargeMinutes: Int,
    val status: String // "Benchmark Leader", "Within Target", "Extended LOS Alert"
)

data class ThroughputTrendPoint(
    val period: String,
    val admissions: Int,
    val discharges: Int,
    val avgLOS: Double,
    val occupancyRate: Double
)

@Entity(tableName = "patient_records")
data class PatientRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val patientName: String,
    val ipdNumber: String,
    val age: Int,
    val gender: String,
    val consultantName: String,
    val department: String,
    val admissionDate: String,
    val dischargeDate: String? = null,
    val losDays: Int = 0,
    val isDischarged: Boolean = false,
    val delayReason: String? = null,
    val clinicalPathwayFollowed: Boolean = true,
    val notes: String = "",
    val acuityLevel: Int = 1, // 1: Stable (1.0x), 2: Moderate (1.5x), 3: High/HDU (2.5x), 4: Critical/ICU (4.0x)
    val flowStage: String = "Treatment", // "Admission", "Diagnostics", "Treatment", "Discharge"
    val attendingNurseName: String = "Staff Nurse Priya Sharma (RN)",
    val estimatedDischargeTime: String = "Today, 04:30 PM",
    val bedNumber: String = "Ward 3B - Bed 14"
) {
    val acuityWeight: Double get() = AcuityDefinitions.getWeight(acuityLevel)
    val acuityLabel: String get() = AcuityDefinitions.getLevelName(acuityLevel)
}

enum class PatientFlowStage(
    val stageKey: String,
    val title: String,
    val description: String,
    val defaultWipLimit: Int,
    val headerColor: Long,
    val badgeBgColor: Long
) {
    ADMISSION(
        stageKey = "Admission",
        title = "Admission",
        description = "Registration, triage, bed allocation & intake workup",
        defaultWipLimit = 5,
        headerColor = 0xFF0284C7, // Blue
        badgeBgColor = 0xFFE0F2FE
    ),
    DIAGNOSTICS(
        stageKey = "Diagnostics",
        title = "Diagnostics",
        description = "Lab tests, radiology/CT/MRI & pathology reports",
        defaultWipLimit = 6,
        headerColor = 0xFF7C3AED, // Purple
        badgeBgColor = 0xFFEDE9FE
    ),
    TREATMENT(
        stageKey = "Treatment",
        title = "Treatment",
        description = "Medication, OT/surgery, ward rounds & nursing care",
        defaultWipLimit = 10,
        headerColor = 0xFF16A34A, // Green
        badgeBgColor = 0xFFDCFCE7
    ),
    DISCHARGE(
        stageKey = "Discharge",
        title = "Discharge",
        description = "Summary draft, pharmacy returns & billing gate pass",
        defaultWipLimit = 4,
        headerColor = 0xFFEA580C, // Amber / Orange
        badgeBgColor = 0xFFFFEDD5
    );

    companion object {
        fun fromKey(key: String): PatientFlowStage {
            return entries.firstOrNull { it.stageKey.equals(key, ignoreCase = true) } ?: TREATMENT
        }
    }
}

data class PatientAcuityConfig(
    val level: Int,
    val name: String,
    val shortName: String,
    val weight: Double,
    val description: String,
    val clinicalCriteria: String
)

object AcuityDefinitions {
    val levels = listOf(
        PatientAcuityConfig(
            level = 1,
            name = "Level 1: Low Acuity / Stable",
            shortName = "L1 Stable",
            weight = 1.0,
            description = "Self-care, routine vitals every 8-12h, oral meds, discharge-ready",
            clinicalCriteria = "Ambulatory, stable post-op day 3+, awaiting administrative gate pass"
        ),
        PatientAcuityConfig(
            level = 2,
            name = "Level 2: Moderate Acuity",
            shortName = "L2 Moderate",
            weight = 1.5,
            description = "Assisted ADLs, IV antibiotics/fluids, surgical drains, vitals every 4-6h",
            clinicalCriteria = "Active medication titration, wound dressing changes, step-down monitoring"
        ),
        PatientAcuityConfig(
            level = 3,
            name = "Level 3: High Acuity (Step-down / HDU)",
            shortName = "L3 High",
            weight = 2.5,
            description = "Non-invasive ventilation, continuous ECG telemetry, vitals every 1-2h",
            clinicalCriteria = "Unstable blood glucose, labile BP, blood transfusions, HDU bed"
        ),
        PatientAcuityConfig(
            level = 4,
            name = "Level 4: Critical Acuity (ICU)",
            shortName = "L4 Critical",
            weight = 4.0,
            description = "Mechanical ventilation, multiple inotropes, continuous hemodialysis",
            clinicalCriteria = "1:1 or 1:2 intensive nursing, severe sepsis, multi-organ dysfunction"
        )
    )

    fun getWeight(level: Int): Double = when (level) {
        1 -> 1.0
        2 -> 1.5
        3 -> 2.5
        4 -> 4.0
        else -> 1.0
    }

    fun getLevelName(level: Int): String = when (level) {
        1 -> "Level 1 (Stable • 1.0x)"
        2 -> "Level 2 (Moderate • 1.5x)"
        3 -> "Level 3 (High Acuity • 2.5x)"
        4 -> "Level 4 (Critical ICU • 4.0x)"
        else -> "Level 1"
    }
}

data class ConsultantWorkload(
    val consultantName: String,
    val department: String,
    val maxAcuityCapacity: Double = 25.0, // Standard clinical capacity ceiling (points)
    val assignedPatients: List<PatientRecord> = emptyList()
) {
    val totalPatientCount: Int get() = assignedPatients.size
    val totalAcuityPoints: Double
        get() = assignedPatients.sumOf { it.acuityWeight }

    val capacityUtilizationPct: Double
        get() = if (maxAcuityCapacity > 0) (totalAcuityPoints / maxAcuityCapacity) * 100.0 else 0.0

    val workloadStatus: WorkloadStatus
        get() = when {
            capacityUtilizationPct > 100.0 -> WorkloadStatus.OVERLOADED
            capacityUtilizationPct >= 80.0 -> WorkloadStatus.NEAR_CAPACITY
            capacityUtilizationPct >= 40.0 -> WorkloadStatus.OPTIMAL
            else -> WorkloadStatus.UNDERUTILIZED
        }

    val acuityBreakdown: Map<Int, Int>
        get() = assignedPatients.groupBy { it.acuityLevel }.mapValues { it.value.size }
}

enum class WorkloadStatus(val label: String, val badgeColor: Long) {
    OVERLOADED("Overloaded (>100%)", 0xFFDC2626),
    NEAR_CAPACITY("Near Capacity (80-100%)", 0xFFD97706),
    OPTIMAL("Optimal Workload (40-80%)", 0xFF16A34A),
    UNDERUTILIZED("Available Capacity (<40%)", 0xFF2563EB)
}

@Entity(tableName = "lean_waste_logs")
data class LeanWasteLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val wasteCategory: String, // Waiting, Motion, Overprocessing, Defects, Inventory, Transportation, Overproduction, Talent
    val department: String,
    val description: String,
    val estimatedMinutesLost: Int,
    val severity: String, // Low, Medium, High, Critical
    val rootCause: String = "",
    val reportedBy: String = "Lean Audit Team",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "five_s_audits")
data class FiveSAudit(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val department: String,
    val sortScore: Int,        // 1-5 (Seiri)
    val setInOrderScore: Int,  // 1-5 (Seiton)
    val shineScore: Int,       // 1-5 (Seiso)
    val standardizeScore: Int, // 1-5 (Seiketsu)
    val sustainScore: Int,     // 1-5 (Shitsuke)
    val auditorName: String,
    val auditDate: String,
    val remarks: String = ""
) {
    val totalScore: Int get() = sortScore + setInOrderScore + shineScore + standardizeScore + sustainScore
    val percentage: Double get() = (totalScore.toDouble() / 25.0) * 100.0
}

@Entity(tableName = "kaizen_projects")
data class KaizenProject(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val department: String,
    val problemStatement: String,
    val proposedCountermeasure: String,
    val targetMetric: String,
    val status: String, // Planned, In Progress, Implemented
    val leadPerson: String,
    val estimatedBedDaysSavedYearly: Int = 0,
    val dateInitiated: String
)

@Entity(tableName = "root_cause_analyses")
data class RootCauseAnalysis(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val sourceModule: String, // "LOS Analytics", "5S Audit", "VSM Bottleneck", "Consultant Workload", "Ward Operations"
    val department: String,
    val incidentDescription: String,
    val severity: String = "High", // Critical, High, Medium, Low
    val why1: String, // Immediate trigger
    val why2: String, // Process failure
    val why3: String, // Operational / Communication gap
    val why4: String, // Systemic / Procedural deficiency
    val why5: String, // Underlying Root Cause!
    val rootCauseStatement: String,
    val rootCauseCategory: String, // Process Design, Communication, People / Training, Policy / SOP, Equipment / IT Infrastructure
    val countermeasure: String,
    val actionOwner: String,
    val targetDate: String,
    val targetKpiImpact: String,
    val status: String = "Open", // Open, In Progress, Resolved
    val createdAt: Long = System.currentTimeMillis()
)
