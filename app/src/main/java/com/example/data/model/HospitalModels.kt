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
    val notes: String = ""
)

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
