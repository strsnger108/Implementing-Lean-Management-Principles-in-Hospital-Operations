package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.model.AppUser
import com.example.data.model.ConsultantStat
import com.example.data.model.DepartmentLosBenchmark
import com.example.data.model.DischargeHourDistribution
import com.example.data.model.FamilyMember
import com.example.data.model.FiveSAudit
import com.example.data.model.FiveSCapaItem
import com.example.data.model.FiveSRedTagItem
import com.example.data.model.HmsEhrConfig
import com.example.data.model.HospitalDirectory
import com.example.data.model.InsightType
import com.example.data.model.KaizenProject
import com.example.data.model.LeanInsight
import com.example.data.model.LeanWasteLog
import com.example.data.model.MonthlyMetrics
import com.example.data.model.OpdQueueItem
import com.example.data.model.PatientOpdToken
import com.example.data.model.PatientRecord
import com.example.data.model.PharmacyPrescription
import com.example.data.model.BloodLabReport
import com.example.data.model.DailyIpdBillEntry
import com.example.data.model.NurseCallRequest
import com.example.data.model.PatientBill
import com.example.data.model.PatientDiagnosisSummary
import com.example.data.model.PatientFeedback
import com.example.data.model.PatientInsurance
import com.example.data.model.RootCauseAnalysis
import com.example.data.model.ThroughputMetrics
import com.example.data.model.ThroughputTrendPoint
import com.example.data.model.VsmStage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class HospitalRepository(private val db: AppDatabase) {

    val activeUserSession: Flow<AppUser?> = db.userDao().getActiveSession()
    val hmsConfig: Flow<HmsEhrConfig?> = db.hmsConfigDao().getDefaultConfig()

    fun getFamilyMembers(identifier: String): Flow<List<FamilyMember>> {
        return db.familyMemberDao().getFamilyMembers(identifier)
    }

    val allPrescriptions: Flow<List<PharmacyPrescription>> = db.pharmacyDao().getAllPrescriptions()
    fun getPrescriptionsByRegNumber(regNumber: String): Flow<List<PharmacyPrescription>> {
        return db.pharmacyDao().getPrescriptionsByRegNumber(regNumber)
    }

    val allBloodReports: Flow<List<BloodLabReport>> = db.bloodReportDao().getAllReports()
    fun getBloodReportsByRegNumber(regNumber: String): Flow<List<BloodLabReport>> {
        return db.bloodReportDao().getReportsByRegNumber(regNumber)
    }

    val allBills: Flow<List<PatientBill>> = db.patientBillingDao().getAllBills()
    fun getBillsByRegNumber(regNumber: String): Flow<List<PatientBill>> {
        return db.patientBillingDao().getBillsByRegNumber(regNumber)
    }

    fun getDailyIpdBills(regNumber: String): Flow<List<DailyIpdBillEntry>> {
        return db.dailyIpdBillDao().getDailyBillsByRegNumber(regNumber)
    }

    fun getInsuranceByRegNumber(regNumber: String): Flow<PatientInsurance?> {
        return db.patientInsuranceDao().getInsuranceByRegNumber(regNumber)
    }

    val allNurseCalls: Flow<List<NurseCallRequest>> = db.nurseCallDao().getAllNurseCalls()
    fun getNurseCallsByRegNumber(regNumber: String): Flow<List<NurseCallRequest>> {
        return db.nurseCallDao().getNurseCallsByRegNumber(regNumber)
    }

    val allFeedbacks: Flow<List<PatientFeedback>> = db.patientFeedbackDao().getAllFeedbacks()
    fun getFeedbacksByRegNumber(regNumber: String): Flow<List<PatientFeedback>> {
        return db.patientFeedbackDao().getFeedbacksByRegNumber(regNumber)
    }

    val allPatients: Flow<List<PatientRecord>> = db.patientDao().getAllPatients()
    val activePatients: Flow<List<PatientRecord>> = db.patientDao().getActivePatients()
    val dischargedPatients: Flow<List<PatientRecord>> = db.patientDao().getDischargedPatients()
    val wasteLogs: Flow<List<LeanWasteLog>> = db.leanWasteDao().getAllWasteLogs()
    val fiveSAudits: Flow<List<FiveSAudit>> = db.fiveSAuditDao().getAllAudits()
    val redTags: Flow<List<FiveSRedTagItem>> = db.fiveSRedTagDao().getAllRedTags()
    val capas: Flow<List<FiveSCapaItem>> = db.fiveSCapaDao().getAllCapas()
    val kaizenProjects: Flow<List<KaizenProject>> = db.kaizenDao().getAllKaizen()
    val rootCauseAnalyses: Flow<List<RootCauseAnalysis>> = db.rootCauseDao().getAllAnalyses()

    val monthlyDataMap: Map<String, MonthlyMetrics> = mapOf(
        "all" to MonthlyMetrics(
            monthId = "all",
            label = "All Months (Nov 2025 – Feb 2026)",
            admissions = 381,
            validLOS = 322,
            avgLOS = 3.29,
            sameDay = 24,
            shortStay = 137,
            mediumStay = 100,
            longStay = 61,
            incomplete = 59
        ),
        "nov" to MonthlyMetrics(
            monthId = "nov",
            label = "November 2025",
            admissions = 103,
            validLOS = 77,
            avgLOS = 3.03,
            sameDay = 7,
            shortStay = 33,
            mediumStay = 22,
            longStay = 15,
            incomplete = 26,
            dailyAdmissions = listOf(2,3,4,4,3,3,4,3,4,3,2,3,4,3,4,3,4,4,3,3,4,3,2,3,4,4,3,4,3,3)
        ),
        "dec" to MonthlyMetrics(
            monthId = "dec",
            label = "December 2025",
            admissions = 116,
            validLOS = 112,
            avgLOS = 3.04,
            sameDay = 9,
            shortStay = 45,
            mediumStay = 36,
            longStay = 22,
            incomplete = 4,
            dailyAdmissions = listOf(5,6,4,3,4,5,3,4,4,3,4,3,4,3,4,4,3,4,4,3,4,3,3,4,4,3,3,4,3,4,3)
        ),
        "jan" to MonthlyMetrics(
            monthId = "jan",
            label = "January 2026",
            admissions = 84,
            validLOS = 76,
            avgLOS = 3.62,
            sameDay = 4,
            shortStay = 30,
            mediumStay = 25,
            longStay = 17,
            incomplete = 8,
            dailyAdmissions = listOf(2,3,4,2,3,2,3,4,3,3,4,2,3,3,2,3,2,3,2,3,3,2,3,2,3,3,2,3,2,2,2)
        ),
        "feb" to MonthlyMetrics(
            monthId = "feb",
            label = "February 2026",
            admissions = 78,
            validLOS = 57,
            avgLOS = 3.72,
            sameDay = 4,
            shortStay = 29,
            mediumStay = 17,
            longStay = 7,
            incomplete = 21,
            dailyAdmissions = listOf(3,4,3,3,3,3,3,3,3,3,2,2,3,2,3,3,3,2,2,3,2,3,3,2,2,3,2,2)
        )
    )

    val throughputDataMap: Map<String, ThroughputMetrics> = mapOf(
        "all" to ThroughputMetrics(
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
        ),
        "nov" to ThroughputMetrics(
            periodLabel = "November 2025",
            totalAdmissions = 103,
            totalDischarges = 99,
            netCensusChange = 4,
            bedOccupancyRate = 74.2,
            bedTurnoverRate = 2.62,
            bedTurnoverIntervalHours = 19.1,
            avgDischargeTurnaroundMinutes = 185,
            targetDischargeTurnaroundMinutes = 45,
            actualAvgLos = 3.03,
            nationalBenchmarkLos = 4.20,
            leanTargetLos = 2.80
        ),
        "dec" to ThroughputMetrics(
            periodLabel = "December 2025",
            totalAdmissions = 116,
            totalDischarges = 114,
            netCensusChange = 2,
            bedOccupancyRate = 82.5,
            bedTurnoverRate = 2.85,
            bedTurnoverIntervalHours = 15.6,
            avgDischargeTurnaroundMinutes = 175,
            targetDischargeTurnaroundMinutes = 45,
            actualAvgLos = 3.04,
            nationalBenchmarkLos = 4.20,
            leanTargetLos = 2.80
        ),
        "jan" to ThroughputMetrics(
            periodLabel = "January 2026",
            totalAdmissions = 84,
            totalDischarges = 82,
            netCensusChange = 2,
            bedOccupancyRate = 71.0,
            bedTurnoverRate = 2.12,
            bedTurnoverIntervalHours = 22.8,
            avgDischargeTurnaroundMinutes = 182,
            targetDischargeTurnaroundMinutes = 45,
            actualAvgLos = 3.62,
            nationalBenchmarkLos = 4.20,
            leanTargetLos = 2.80
        ),
        "feb" to ThroughputMetrics(
            periodLabel = "February 2026",
            totalAdmissions = 78,
            totalDischarges = 79,
            netCensusChange = -1,
            bedOccupancyRate = 68.4,
            bedTurnoverRate = 1.98,
            bedTurnoverIntervalHours = 24.1,
            avgDischargeTurnaroundMinutes = 178,
            targetDischargeTurnaroundMinutes = 45,
            actualAvgLos = 3.72,
            nationalBenchmarkLos = 4.20,
            leanTargetLos = 2.80
        )
    )

    val dischargeHourDistributions = listOf(
        DischargeHourDistribution("8–10 AM", 4.2, 20.0, 16),
        DischargeHourDistribution("10 AM–12 PM", 11.5, 45.0, 43),
        DischargeHourDistribution("12–2 PM", 18.2, 20.0, 68),
        DischargeHourDistribution("2–4 PM", 35.8, 10.0, 134),
        DischargeHourDistribution("4–6 PM", 21.4, 5.0, 80),
        DischargeHourDistribution("6–8 PM", 6.9, 0.0, 26),
        DischargeHourDistribution("Post 8 PM", 2.0, 0.0, 7)
    )

    val departmentBenchmarks = listOf(
        DepartmentLosBenchmark(
            department = "General Medicine",
            actualLos = 3.82,
            nationalBenchmark = 4.20,
            leanTarget = 3.20,
            monthlyThroughput = 142,
            avgDischargeMinutes = 180,
            status = "Within Benchmark"
        ),
        DepartmentLosBenchmark(
            department = "General Surgery",
            actualLos = 2.65,
            nationalBenchmark = 3.80,
            leanTarget = 2.40,
            monthlyThroughput = 86,
            avgDischargeMinutes = 145,
            status = "Benchmark Leader"
        ),
        DepartmentLosBenchmark(
            department = "Orthopedics",
            actualLos = 5.20,
            nationalBenchmark = 4.50,
            leanTarget = 3.60,
            monthlyThroughput = 58,
            avgDischargeMinutes = 225,
            status = "Extended LOS Alert"
        ),
        DepartmentLosBenchmark(
            department = "Obstetrics & Gyn",
            actualLos = 1.95,
            nationalBenchmark = 2.80,
            leanTarget = 1.80,
            monthlyThroughput = 48,
            avgDischargeMinutes = 110,
            status = "Benchmark Leader"
        ),
        DepartmentLosBenchmark(
            department = "ICU & HDU",
            actualLos = 5.40,
            nationalBenchmark = 6.80,
            leanTarget = 4.50,
            monthlyThroughput = 32,
            avgDischargeMinutes = 160,
            status = "Within Target"
        ),
        DepartmentLosBenchmark(
            department = "Pediatrics",
            actualLos = 2.15,
            nationalBenchmark = 3.10,
            leanTarget = 2.00,
            monthlyThroughput = 15,
            avgDischargeMinutes = 125,
            status = "Benchmark Leader"
        )
    )

    val monthlyThroughputTrends = listOf(
        ThroughputTrendPoint("Nov '25", 103, 99, 3.03, 74.2),
        ThroughputTrendPoint("Dec '25", 116, 114, 3.04, 82.5),
        ThroughputTrendPoint("Jan '26", 84, 82, 3.62, 71.0),
        ThroughputTrendPoint("Feb '26", 78, 79, 3.72, 68.4)
    )

    val losDistributionLabels = listOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9-10", "11-18")
    val losDistributionValues = listOf(24, 88, 49, 48, 31, 21, 14, 12, 18, 10, 7)

    val consultantStats: List<ConsultantStat> = run {
        val raw = listOf(
            "Dr. Rahul Sinha" to 127,
            "Dr. M.S. Islam" to 30,
            "Dr. Rajnish Kumar" to 29,
            "Dr. Javed Akhtar" to 22,
            "Emergency Dept" to 19,
            "Dr. Vivek Goswami" to 17,
            "Dr. Manjar Ali" to 16,
            "Dr. Jatin Sethi" to 8,
            "Dr. S. Nawal" to 6,
            "Other Visiting" to 43
        )
        val total = raw.sumOf { it.second }.toDouble()
        var accumulated = 0
        raw.map { (name, cases) ->
            accumulated += cases
            val cumPct = (accumulated / total) * 100.0
            ConsultantStat(name, cases, cumPct)
        }
    }

    val defaultInsights = listOf(
        LeanInsight(
            id = "los_trend",
            title = "Increasing LOS Trend (23% Surge)",
            description = "Average LOS increased from 3.03 days (Nov) to 3.72 days (Feb). Without intervention, this adds ~400 extra bed-days annually.",
            actionItem = "Implement standardized multi-disciplinary discharge rounds at 10:00 AM daily.",
            type = InsightType.WARNING
        ),
        LeanInsight(
            id = "consultant_bottleneck",
            title = "Consultant Bottleneck (Pareto 80/20)",
            description = "40.1% of all hospital admissions are managed by a single consultant (Dr. Rahul Sinha), creating a single-point-of-failure queue.",
            actionItem = "Develop structured clinical referral leveling and secondary cross-coverage rotas.",
            type = InsightType.WARNING
        ),
        LeanInsight(
            id = "vsm_efficiency",
            title = "Process Efficiency Gap (21.5% Value-Add)",
            description = "Value Stream Mapping demonstrates that only 21.5% of patient stay is value-adding. 78.5% is waiting, transport, and administrative lag.",
            actionItem = "Target discharge turnaround delay (180 min avg waiting time) as top Kaizen event.",
            type = InsightType.INFO
        ),
        LeanInsight(
            id = "national_benchmark",
            title = "Benchmark Performance Advantage",
            description = "Current avg LOS of 3.29 days is 22% lower than the Indian private hospital average of 4.2 days. Achieving 2.8 days releases 15% bed capacity.",
            actionItem = "Establish express discharge lounge to free beds by 11:30 AM.",
            type = InsightType.SUCCESS
        ),
        LeanInsight(
            id = "records_gap",
            title = "Discharge Documentation Gap",
            description = "15.5% of patient records in the study lacked complete discharge timestamps, hindering real-time bed analytics.",
            actionItem = "Standard work protocol for ward nurses & billing desk to lock discharge in HMS instantly.",
            type = InsightType.INFO
        ),
        LeanInsight(
            id = "short_stay_win",
            title = "High Short-Stay Cohort (Quick Win)",
            description = "50% of all admitted patients discharge within 48 hours — ideal candidates for standardized clinical pathways.",
            actionItem = "Introduce day-care & short-stay pre-cleared discharge protocols.",
            type = InsightType.SUCCESS
        )
    )

    val vsmStages = listOf(
        VsmStage(
            stageName = "Admission & Triage",
            department = "OPD / Emergency",
            processTimeMinutes = 20,
            waitTimeMinutes = 45,
            wasteType = "Waiting & Motion",
            keyBottleneck = "Manual registration form filling and file retrieval",
            leanCountermeasure = "Pre-admission digital registration kiosk & fast-track triage"
        ),
        VsmStage(
            stageName = "Diagnostic Workup",
            department = "Radiology & Pathology",
            processTimeMinutes = 25,
            waitTimeMinutes = 60,
            wasteType = "Overprocessing & Waiting",
            keyBottleneck = "Batching of lab samples and delay in radiologist sign-off",
            leanCountermeasure = "Single-piece flow for stat blood samples & auto-alert PACS"
        ),
        VsmStage(
            stageName = "Bed Allocation & Transfer",
            department = "IPD Wards",
            processTimeMinutes = 15,
            waitTimeMinutes = 70,
            wasteType = "Waiting & Transportation",
            keyBottleneck = "Waiting for housekeeping bed turnover notification",
            leanCountermeasure = "Kanban visual bed status board with real-time ward sync"
        ),
        VsmStage(
            stageName = "Clinical Treatment & Rounds",
            department = "IPD & Specialty Wards",
            processTimeMinutes = 180,
            waitTimeMinutes = 90,
            wasteType = "Motion & Underutilized Talent",
            keyBottleneck = "Unscheduled doctor rounding times and missing chart entries",
            leanCountermeasure = "Standardized 10:00 AM multi-disciplinary team rounding"
        ),
        VsmStage(
            stageName = "Discharge Order to Pharmacy Clearance",
            department = "Ward & Pharmacy",
            processTimeMinutes = 15,
            waitTimeMinutes = 55,
            wasteType = "Waiting & Overprocessing",
            keyBottleneck = "Physical transport of medication return and handwritten approvals",
            leanCountermeasure = "Electronic pharmacy reconciliation 2 hours prior to planned discharge"
        ),
        VsmStage(
            stageName = "Final Billing & Gate Pass",
            department = "Billing & TPA Insurance",
            processTimeMinutes = 20,
            waitTimeMinutes = 125,
            wasteType = "Defects & Waiting (Muda)",
            keyBottleneck = "Late insurance TPA queries, missing lab bills, and paper invoice queues",
            leanCountermeasure = "Parallel billing reconciliation with pre-authorized discharge summary"
        ),
        VsmStage(
            stageName = "Bed Sanitization & Turnover",
            department = "Housekeeping",
            processTimeMinutes = 25,
            waitTimeMinutes = 35,
            wasteType = "Motion",
            keyBottleneck = "Delayed intimation to housekeeping after patient physical departure",
            leanCountermeasure = "SMED (Single Minute Exchange of Die) bed turnover protocol (<30 min)"
        )
    )

    suspend fun initializeSeedDataIfEmpty() = withContext(Dispatchers.IO) {
        if (db.patientDao().getCount() == 0) {
            val samplePatients = listOf(
                PatientRecord(
                    patientName = "Amit Kumar Verma",
                    ipdNumber = "SGH-2025-1102",
                    age = 46,
                    gender = "Male",
                    consultantName = "Dr. Rahul Sinha",
                    department = "General Medicine",
                    admissionDate = "2025-11-04",
                    dischargeDate = "2025-11-06",
                    losDays = 2,
                    isDischarged = true,
                    clinicalPathwayFollowed = true,
                    notes = "Acute gastroenteritis. Fast recovery on standard protocol."
                ),
                PatientRecord(
                    patientName = "Pooja Kumari Devi",
                    ipdNumber = "SGH-2025-1145",
                    age = 32,
                    gender = "Female",
                    consultantName = "Dr. M.S. Islam",
                    department = "General Surgery",
                    admissionDate = "2025-11-12",
                    dischargeDate = "2025-11-15",
                    losDays = 3,
                    isDischarged = true,
                    clinicalPathwayFollowed = true,
                    notes = "Laparoscopic cholecystectomy. Uneventful recovery."
                ),
                PatientRecord(
                    patientName = "Rameshwar Prasad",
                    ipdNumber = "SGH-2025-1219",
                    age = 63,
                    gender = "Male",
                    consultantName = "Dr. Rajnish Kumar",
                    department = "Orthopedics",
                    admissionDate = "2025-12-05",
                    dischargeDate = "2025-12-11",
                    losDays = 6,
                    isDischarged = true,
                    delayReason = "TPA Insurance final approval delay (4 hours)",
                    clinicalPathwayFollowed = false,
                    notes = "Total knee arthroplasty. Mobilized Day 2."
                ),
                PatientRecord(
                    patientName = "Sunita Singh",
                    ipdNumber = "SGH-2025-1288",
                    age = 28,
                    gender = "Female",
                    consultantName = "Dr. Javed Akhtar",
                    department = "Gynecology & Obs",
                    admissionDate = "2025-12-18",
                    dischargeDate = "2025-12-19",
                    losDays = 1,
                    isDischarged = true,
                    clinicalPathwayFollowed = true,
                    notes = "Normal spontaneous delivery. Discharged in 24 hours."
                ),
                PatientRecord(
                    patientName = "Deepak Kumar Roy",
                    ipdNumber = "SGH-2026-0104",
                    age = 54,
                    gender = "Male",
                    consultantName = "Dr. Rahul Sinha",
                    department = "General Medicine",
                    admissionDate = "2026-01-08",
                    dischargeDate = "2026-01-13",
                    losDays = 5,
                    isDischarged = true,
                    delayReason = "Discharge summary physician signature delayed",
                    clinicalPathwayFollowed = false,
                    notes = "Type 2 Diabetes Mellitus with severe hyperglycemia."
                ),
                PatientRecord(
                    patientName = "Anita Soren",
                    ipdNumber = "SGH-2026-0158",
                    age = 39,
                    gender = "Female",
                    consultantName = "Dr. Vivek Goswami",
                    department = "General Surgery",
                    admissionDate = "2026-01-20",
                    dischargeDate = "2026-01-22",
                    losDays = 2,
                    isDischarged = true,
                    clinicalPathwayFollowed = true,
                    notes = "Appendectomy. Early enteral nutrition initiated."
                ),
                PatientRecord(
                    patientName = "Suresh Mahto",
                    ipdNumber = "SGH-2026-0210",
                    age = 71,
                    gender = "Male",
                    consultantName = "Dr. Rahul Sinha",
                    department = "General Medicine",
                    admissionDate = "2026-02-02",
                    dischargeDate = "2026-02-09",
                    losDays = 7,
                    isDischarged = true,
                    delayReason = "Diagnostic re-investigation & pharmacy return reconciliation",
                    clinicalPathwayFollowed = false,
                    notes = "COPD exacerbation with pneumonia."
                ),
                PatientRecord(
                    patientName = "Manish Pandey",
                    ipdNumber = "SGH-2026-0245",
                    age = 41,
                    gender = "Male",
                    consultantName = "Dr. Manjar Ali",
                    department = "Orthopedics",
                    admissionDate = "2026-02-14",
                    losDays = 3,
                    isDischarged = false,
                    clinicalPathwayFollowed = true,
                    notes = "Distal radius fracture ORIF. Scheduled for round evaluation."
                ),
                PatientRecord(
                    patientName = "Kavita Tirkey",
                    ipdNumber = "SGH-2026-0271",
                    age = 25,
                    gender = "Female",
                    consultantName = "Emergency Dept",
                    department = "Emergency",
                    admissionDate = "2026-02-22",
                    losDays = 1,
                    isDischarged = false,
                    clinicalPathwayFollowed = true,
                    notes = "Trauma observation, stable vitals. Awaiting step-down discharge."
                ),
                PatientRecord(
                    patientName = "Rajesh Gope",
                    ipdNumber = "SGH-2026-0288",
                    age = 58,
                    gender = "Male",
                    consultantName = "Dr. Rahul Sinha",
                    department = "General Medicine",
                    admissionDate = "2026-02-25",
                    losDays = 2,
                    isDischarged = false,
                    clinicalPathwayFollowed = true,
                    notes = "Hypertensive urgency managed. Preparing discharge summary.",
                    acuityLevel = 2
                ),
                PatientRecord(
                    patientName = "Amitav Sen",
                    ipdNumber = "SGH-2026-0291",
                    age = 64,
                    gender = "Male",
                    consultantName = "Dr. Rahul Sinha",
                    department = "General Medicine",
                    admissionDate = "2026-02-26",
                    losDays = 2,
                    isDischarged = false,
                    clinicalPathwayFollowed = true,
                    notes = "Bilateral lower lobe pneumonia on 4L nasal cannula.",
                    acuityLevel = 3
                ),
                PatientRecord(
                    patientName = "Bimla Devi",
                    ipdNumber = "SGH-2026-0294",
                    age = 72,
                    gender = "Female",
                    consultantName = "Dr. Rahul Sinha",
                    department = "General Medicine",
                    admissionDate = "2026-02-27",
                    losDays = 1,
                    isDischarged = false,
                    clinicalPathwayFollowed = false,
                    notes = "Acute on chronic kidney disease with hyperkalemia.",
                    acuityLevel = 3
                ),
                PatientRecord(
                    patientName = "Sanjay Murmu",
                    ipdNumber = "SGH-2026-0298",
                    age = 51,
                    gender = "Male",
                    consultantName = "Dr. Rahul Sinha",
                    department = "General Medicine",
                    admissionDate = "2026-02-27",
                    losDays = 1,
                    isDischarged = false,
                    clinicalPathwayFollowed = true,
                    notes = "Acute gastroenteritis with pre-renal azotemia.",
                    acuityLevel = 2
                ),
                PatientRecord(
                    patientName = "Preeti Roy",
                    ipdNumber = "SGH-2026-0301",
                    age = 43,
                    gender = "Female",
                    consultantName = "Dr. Rahul Sinha",
                    department = "General Medicine",
                    admissionDate = "2026-02-28",
                    losDays = 0,
                    isDischarged = false,
                    clinicalPathwayFollowed = true,
                    notes = "Newly diagnosed hypertension. Oral anti-hypertensive titration.",
                    acuityLevel = 1
                ),
                PatientRecord(
                    patientName = "Vikas Agarwal",
                    ipdNumber = "SGH-2026-0305",
                    age = 68,
                    gender = "Male",
                    consultantName = "Dr. Rahul Sinha",
                    department = "General Medicine",
                    admissionDate = "2026-02-28",
                    losDays = 0,
                    isDischarged = false,
                    clinicalPathwayFollowed = true,
                    notes = "Decompensated congestive heart failure. IV Furosemide infusion.",
                    acuityLevel = 3
                ),
                PatientRecord(
                    patientName = "Suraj Oraon",
                    ipdNumber = "SGH-2026-0310",
                    age = 46,
                    gender = "Male",
                    consultantName = "Dr. M.S. Islam",
                    department = "General Surgery",
                    admissionDate = "2026-02-26",
                    losDays = 2,
                    isDischarged = false,
                    clinicalPathwayFollowed = true,
                    notes = "Post-laparoscopic appendectomy Day 2. Tolerating oral diet.",
                    acuityLevel = 2
                ),
                PatientRecord(
                    patientName = "Kamla Soren",
                    ipdNumber = "SGH-2026-0312",
                    age = 53,
                    gender = "Female",
                    consultantName = "Dr. M.S. Islam",
                    department = "General Surgery",
                    admissionDate = "2026-02-27",
                    losDays = 1,
                    isDischarged = false,
                    clinicalPathwayFollowed = true,
                    notes = "Inguinal hernia mesh hernioplasty. Surgical drain in situ.",
                    acuityLevel = 2
                ),
                PatientRecord(
                    patientName = "Rameshwar Tudu",
                    ipdNumber = "SGH-2026-0320",
                    age = 65,
                    gender = "Male",
                    consultantName = "Dr. Rajnish Kumar",
                    department = "Orthopedics",
                    admissionDate = "2026-02-26",
                    losDays = 2,
                    isDischarged = false,
                    clinicalPathwayFollowed = true,
                    notes = "Bilateral total knee arthroplasty. Active physiotherapy protocol.",
                    acuityLevel = 3
                ),
                PatientRecord(
                    patientName = "Deepak Yadav",
                    ipdNumber = "SGH-2026-0322",
                    age = 29,
                    gender = "Male",
                    consultantName = "Dr. Rajnish Kumar",
                    department = "Orthopedics",
                    admissionDate = "2026-02-27",
                    losDays = 1,
                    isDischarged = false,
                    clinicalPathwayFollowed = true,
                    notes = "Closed femur shaft fracture s/p interlocking nail.",
                    acuityLevel = 2
                ),
                PatientRecord(
                    patientName = "Shabana Khatoon",
                    ipdNumber = "SGH-2026-0330",
                    age = 26,
                    gender = "Female",
                    consultantName = "Dr. Javed Akhtar",
                    department = "Obstetrics & Gyn",
                    admissionDate = "2026-02-27",
                    losDays = 1,
                    isDischarged = false,
                    clinicalPathwayFollowed = true,
                    notes = "Elective LSCS Day 2. Mother and baby healthy.",
                    acuityLevel = 1
                ),
                PatientRecord(
                    patientName = "Harishchandra Mahto",
                    ipdNumber = "SGH-2026-0340",
                    age = 69,
                    gender = "Male",
                    consultantName = "Dr. Jatin Sethi",
                    department = "Critical Care & ICU",
                    admissionDate = "2026-02-25",
                    losDays = 3,
                    isDischarged = false,
                    clinicalPathwayFollowed = true,
                    notes = "Urosepsis with septic shock on Noradrenaline support.",
                    acuityLevel = 4
                ),
                PatientRecord(
                    patientName = "Geeta Sharma",
                    ipdNumber = "SGH-2026-0342",
                    age = 57,
                    gender = "Female",
                    consultantName = "Dr. Jatin Sethi",
                    department = "Critical Care & ICU",
                    admissionDate = "2026-02-26",
                    losDays = 2,
                    isDischarged = false,
                    clinicalPathwayFollowed = true,
                    notes = "Severe ARDS s/p intubation and lung-protective ventilation.",
                    acuityLevel = 4
                ),
                PatientRecord(
                    patientName = "Priya Tirkey",
                    ipdNumber = "SGH-2026-0350",
                    age = 24,
                    gender = "Female",
                    consultantName = "Dr. Vivek Goswami",
                    department = "General Surgery",
                    admissionDate = "2026-02-28",
                    losDays = 0,
                    isDischarged = false,
                    clinicalPathwayFollowed = true,
                    notes = "Perianal abscess drainage Day 1. Dressing intact.",
                    acuityLevel = 1
                )
            )
            db.patientDao().insertPatients(samplePatients)
        }

        if (db.leanWasteDao().getCount() == 0) {
            val sampleWasteLogs = listOf(
                LeanWasteLog(
                    wasteCategory = "Waiting",
                    department = "Billing / Discharge Desk",
                    description = "Patients waiting on average 180 minutes between doctor verbal discharge and gate pass issue.",
                    estimatedMinutesLost = 180,
                    severity = "Critical",
                    rootCause = "Manual pharmacy reconciliation and physical paper clearance movement between floors."
                ),
                LeanWasteLog(
                    wasteCategory = "Motion",
                    department = "Inpatient Ward 3",
                    description = "Nurses walking 4.2 km per shift to collect emergency medication and blood products from ground floor.",
                    estimatedMinutesLost = 65,
                    severity = "High",
                    rootCause = "Lack of decentralized ward satellite emergency drug kit (Pyxis / Kanban)."
                ),
                LeanWasteLog(
                    wasteCategory = "Overprocessing",
                    department = "Admission & Triage",
                    description = "Patient basic demographic details re-entered 3 times on OPD slip, IPD admission ledger, and nursing chart.",
                    estimatedMinutesLost = 35,
                    severity = "Medium",
                    rootCause = "Non-integrated paper charts and partial HMS adoption."
                ),
                LeanWasteLog(
                    wasteCategory = "Defects",
                    department = "Medical Records / IPD",
                    description = "15.5% of patient discharge files missing exact discharge time and primary ICD-10 code.",
                    estimatedMinutesLost = 40,
                    severity = "High",
                    rootCause = "No mandatory validation check in HMS prior to file archive."
                ),
                LeanWasteLog(
                    wasteCategory = "Inventory",
                    department = "Main Pharmacy & OT",
                    description = "Excess surgical disposable stocks worth Rs 2.4 Lakhs found sitting in OT storage near expiry.",
                    estimatedMinutesLost = 50,
                    severity = "Medium",
                    rootCause = "Lack of visual Min-Max stock markers and Kanban replenishment trigger."
                ),
                LeanWasteLog(
                    wasteCategory = "Underutilized Talent",
                    department = "ICU & HDU",
                    description = "Senior nursing sisters spending 35% of duty time chasing billing statements and diagnostic reports.",
                    estimatedMinutesLost = 90,
                    severity = "Critical",
                    rootCause = "Absence of ward coordinator / ward clerk for administrative tasks."
                )
            )
            db.leanWasteDao().insertWasteLogs(sampleWasteLogs)
        }

        if (db.fiveSAuditDao().getCount() == 0) {
            val sampleAudits = listOf(
                FiveSAudit(
                    department = "Emergency Department",
                    sortScore = 4,
                    setInOrderScore = 4,
                    shineScore = 5,
                    standardizeScore = 3,
                    sustainScore = 3,
                    auditorName = "Gunjan Prakash (Lean Intern)",
                    auditDate = "2026-02-10",
                    remarks = "Crash carts well labeled. Needs standardization of triage zone paperwork."
                ),
                FiveSAudit(
                    department = "Central Pharmacy",
                    sortScore = 5,
                    setInOrderScore = 4,
                    shineScore = 4,
                    standardizeScore = 4,
                    sustainScore = 4,
                    auditorName = "Dr. Saurav Bhowmik / Lean Team",
                    auditDate = "2026-02-12",
                    remarks = "High-alert drugs color-coded. First-In-First-Out (FIFO) strictly maintained."
                ),
                FiveSAudit(
                    department = "Inpatient Ward 2 (Surgery)",
                    sortScore = 3,
                    setInOrderScore = 3,
                    shineScore = 4,
                    standardizeScore = 2,
                    sustainScore = 3,
                    auditorName = "Gunjan Prakash",
                    auditDate = "2026-02-15",
                    remarks = "Linen storage disorganized. Visual shadow boards recommended for BP apparatus."
                ),
                FiveSAudit(
                    department = "Operation Theatre (OT Complex)",
                    sortScore = 5,
                    setInOrderScore = 5,
                    shineScore = 5,
                    standardizeScore = 4,
                    sustainScore = 4,
                    auditorName = "Quality Coordinator",
                    auditDate = "2026-02-20",
                    remarks = "Excellent sterile discipline. Instrument trays standardized with photo-cards."
                ),
                FiveSAudit(
                    department = "Billing & Discharge Counter",
                    sortScore = 2,
                    setInOrderScore = 3,
                    shineScore = 3,
                    standardizeScore = 2,
                    sustainScore = 2,
                    auditorName = "Gunjan Prakash",
                    auditDate = "2026-02-24",
                    remarks = "Files stacked haphazardly during peak 12 PM - 3 PM discharge rush. Urgent 5S required."
                )
            )
            db.fiveSAuditDao().insertAudits(sampleAudits)
        }

        if (db.kaizenDao().getCount() == 0) {
            val sampleKaizens = listOf(
                KaizenProject(
                    title = "Standardized 10:00 AM Discharge Rounds",
                    department = "All Inpatient Wards",
                    problemStatement = "Discharge orders written late afternoon (2 PM - 4 PM), causing evening bed gridlock and 180 min billing delays.",
                    proposedCountermeasure = "Mandatory multi-disciplinary discharge huddle at 10 AM; identify anticipated discharges 24 hours prior.",
                    targetMetric = "Reduce average discharge turnaround time to < 45 min and lower overall LOS to ≤ 2.8 days.",
                    status = "In Progress",
                    leadPerson = "Dr. Rahul Sinha & Nursing Head",
                    estimatedBedDaysSavedYearly = 240,
                    dateInitiated = "2025-11-20"
                ),
                KaizenProject(
                    title = "Establishment of Inpatient Discharge Lounge",
                    department = "Hospital Operations",
                    problemStatement = "Patients occupy inpatient acute beds for 3-4 hours after clinical discharge while waiting for family or medication.",
                    proposedCountermeasure = "Convert vacant Ground Floor room into comfortable 6-recliner Discharge Hospitality Lounge with nurse monitoring.",
                    targetMetric = "Free inpatient beds by 11:30 AM for incoming emergency/OPD admissions.",
                    status = "Implemented",
                    leadPerson = "Operations Manager & Gunjan Prakash",
                    estimatedBedDaysSavedYearly = 180,
                    dateInitiated = "2025-12-10"
                ),
                KaizenProject(
                    title = "Kanban Electronic Bed Status Board",
                    department = "Housekeeping & Nursing",
                    problemStatement = "Average 45 min lag between patient departure and housekeeping bed sanitization intimation.",
                    proposedCountermeasure = "Real-time tablet-based color visual trigger (Green=Occupied, Yellow=Cleaning in progress, Blue=Ready).",
                    targetMetric = "Bed turnover time from 70 min down to 25 min (SMED technique).",
                    status = "In Progress",
                    leadPerson = "Housekeeping Supervisor",
                    estimatedBedDaysSavedYearly = 95,
                    dateInitiated = "2026-01-15"
                ),
                KaizenProject(
                    title = "Consultant Load Leveling (Heijunka)",
                    department = "Clinical Administration",
                    problemStatement = "Over 40% of patient admissions concentrated with Dr. Rahul Sinha, creating round delays and care bottlenecks.",
                    proposedCountermeasure = "Clinical team triaging and secondary specialist cross-coverage rotas for elective internal medicine admissions.",
                    targetMetric = "Level consultant distribution so no single doctor exceeds 25% of active bed census.",
                    status = "Planned",
                    leadPerson = "Medical Director",
                    estimatedBedDaysSavedYearly = 110,
                    dateInitiated = "2026-02-01"
                )
            )
            db.kaizenDao().insertKaizenList(sampleKaizens)
        }

        if (db.fiveSRedTagDao().getCount() == 0) {
            val sampleRedTags = listOf(
                FiveSRedTagItem(
                    department = "Inpatient Ward 2 (Surgery)",
                    tagNumber = "RT-2026-001",
                    itemName = "Defective NIBP Monitor (Broken Cuff Connector)",
                    category = "Medical Equipment",
                    reason = "Damaged/Broken",
                    actionRequired = "Biomedical Maintenance",
                    taggedBy = "Gunjan Prakash (Lean Intern)",
                    dateTagged = "2026-02-15",
                    status = "Under Quarantine",
                    resolutionNotes = "Sent to Biomedical Engineering Dept for sensor recalibration."
                ),
                FiveSRedTagItem(
                    department = "Operation Theatre (OT Complex)",
                    tagNumber = "RT-2026-002",
                    itemName = "Expired Povidone-Iodine Scrub Boxes (Exp: Dec 2025)",
                    category = "Consumables & Drugs",
                    reason = "Expired",
                    actionRequired = "Scrap / Bio-waste Disposal",
                    taggedBy = "OT Sister In-charge",
                    dateTagged = "2026-02-18",
                    status = "Resolved / Removed",
                    resolutionNotes = "Incinerated per hospital biomedical waste protocol."
                ),
                FiveSRedTagItem(
                    department = "Emergency Department",
                    tagNumber = "RT-2026-003",
                    itemName = "Broken Wheelchair with Detached Footrest",
                    category = "Furniture & Fixtures",
                    reason = "Damaged/Broken",
                    actionRequired = "Biomedical Maintenance",
                    taggedBy = "Triage Staff Nurse",
                    dateTagged = "2026-02-21",
                    status = "Active Tag",
                    resolutionNotes = "Blocking emergency corridor entrance."
                ),
                FiveSRedTagItem(
                    department = "Billing & Discharge Counter",
                    tagNumber = "RT-2026-004",
                    itemName = "Obsolete 2023 Hardcopy Pre-Printed TPA Claim Binders",
                    category = "Records & Forms",
                    reason = "Obsolete / No Owner",
                    actionRequired = "Scrap / Paper Recycling",
                    taggedBy = "Gunjan Prakash",
                    dateTagged = "2026-02-23",
                    status = "Active Tag",
                    resolutionNotes = "Occupying 3 shelves under counter."
                )
            )
            db.fiveSRedTagDao().insertRedTags(sampleRedTags)
        }

        if (db.fiveSCapaDao().getCount() == 0) {
            val sampleCapas = listOf(
                FiveSCapaItem(
                    department = "Inpatient Ward 2",
                    pillar = "1S - Sort",
                    finding = "Cluttered nursing counter with 14 non-active patient registers and empty sample vials.",
                    correctiveAction = "Install vertical wall file organizers and implement 5S desk clear protocol at shift change.",
                    responsiblePerson = "Sister In-charge (Ward 2)",
                    targetDate = "2026-03-05",
                    status = "In Progress",
                    severity = "High",
                    dateCreated = "2026-02-16"
                ),
                FiveSCapaItem(
                    department = "Central Pharmacy",
                    pillar = "2S - Set in Order",
                    finding = "High-Alert injection Potassium Chloride stored right next to regular Sodium Chloride vials.",
                    correctiveAction = "Separate high-alert medications into dedicated locked bin with bold RED visual borders and LASA warning stickers.",
                    responsiblePerson = "Chief Pharmacist",
                    targetDate = "2026-02-28",
                    status = "Closed",
                    severity = "Critical",
                    dateCreated = "2026-02-12"
                ),
                FiveSCapaItem(
                    department = "Emergency Department",
                    pillar = "3S - Shine",
                    finding = "Floor beneath trauma bay gurneys showing chemical stains and missed mop cycles during 2 AM-6 AM.",
                    correctiveAction = "Enforce scheduled terminal floor scrub every 8 hours with sign-off board on trauma bay pillar.",
                    responsiblePerson = "Housekeeping Supervisor",
                    targetDate = "2026-03-02",
                    status = "Open",
                    severity = "Medium",
                    dateCreated = "2026-02-22"
                ),
                FiveSCapaItem(
                    department = "Billing Counter",
                    pillar = "4S - Standardize",
                    finding = "Staff lack standardized discharge file intake sequence, creating patient queue bottlenecks.",
                    correctiveAction = "Implement visual 3-stage color tray system (Red: Urgent, Yellow: TPA Awaiting, Green: Ready for Gate Pass).",
                    responsiblePerson = "Billing Operations Lead",
                    targetDate = "2026-03-10",
                    status = "Open",
                    severity = "High",
                    dateCreated = "2026-02-25"
                )
            )
            db.fiveSCapaDao().insertCapas(sampleCapas)
        }

        if (db.rootCauseDao().getCount() == 0) {
            val sampleRCAs = listOf(
                RootCauseAnalysis(
                    title = "Late Afternoon Discharge Bottleneck (57% Discharges Post 2 PM)",
                    sourceModule = "LOS Analytics",
                    department = "Billing Desk & Inpatient Wards",
                    incidentDescription = "Patients are clinically ready for discharge in the morning but remain in acute inpatient beds until 2 PM–5 PM, preventing morning emergency admissions from being transferred into clean beds.",
                    severity = "Critical",
                    why1 = "Billing clearance and gate pass issue takes 180 minutes on average after physician verbal order.",
                    why2 = "Pharmacy drug return reconciliations and diagnostic charges only begin after physician writes physical discharge summary.",
                    why3 = "Physicians conduct ward discharge rounds late (1:30 PM–3:00 PM) after completing morning outpatient clinics and scheduled surgeries.",
                    why4 = "No standard operating procedure prioritized morning multi-disciplinary rounds or 24-hour anticipatory billing reconciliation.",
                    why5 = "Clinical and administrative departments work in disconnected functional silos without synchronized Lean Value Stream flow.",
                    rootCauseStatement = "Absence of structured morning multi-disciplinary discharge rounds and uncoordinated sequential billing/pharmacy reconciliation.",
                    rootCauseCategory = "Process Design",
                    countermeasure = "Standardize 10:00 AM multi-disciplinary discharge rounds with 4:00 PM prior-day interim bill audit and express discharge lounge.",
                    actionOwner = "Dr. Rahul Sinha & Operations Manager",
                    targetDate = "2026-03-15",
                    targetKpiImpact = "Reduce discharge turnaround from 180 min to 45 min and achieve 65% discharges before noon.",
                    status = "In Progress"
                ),
                RootCauseAnalysis(
                    title = "Medication & Consumable Search Delays (65 min/shift lag)",
                    sourceModule = "5S Audit",
                    department = "Inpatient Ward 3 & Nursing Station",
                    incidentDescription = "Ward 3 scored 11/25 on 5S operational audit. Nurses spend 65 minutes per shift searching through disorganized drawers for IV cannulas, syringes, and dressing trays.",
                    severity = "High",
                    why1 = "Emergency medicines and routine supplies are intermingled across unlabelled cabinets.",
                    why2 = "Broken equipment, expired surgical consumables, and unused furniture occupy 40% of utility room storage.",
                    why3 = "Ward staff never segregated necessary items from obsolete surplus materials.",
                    why4 = "No regular 5S workplace audit schedule or red-tag sorting protocols were instituted in inpatient wards.",
                    why5 = "Ward leadership lacked structured Lean 5S workplace organization standards and daily visual management routines.",
                    rootCauseStatement = "Lack of visual workplace organization standards (5S) and absence of periodic sorting and red-tagging mechanisms.",
                    rootCauseCategory = "Policy / SOP",
                    countermeasure = "Perform 5S Red-Tag sorting event, introduce visual shadow boards and min-max color bins for top 20 ward items.",
                    actionOwner = "Nursing Superintendent",
                    targetDate = "2026-03-10",
                    targetKpiImpact = "Increase 5S score to > 85% and reduce supply search time to under 10 minutes per shift.",
                    status = "Resolved"
                ),
                RootCauseAnalysis(
                    title = "Consultant Referral Concentration (Dr. Sinha Bottleneck)",
                    sourceModule = "Consultant Workload",
                    department = "General Medicine & Triage",
                    incidentDescription = "40.1% of all hospital admissions flow to Dr. Rahul Sinha, creating a single-point-of-failure queue with physician burnout and extended LOS.",
                    severity = "High",
                    why1 = "Triage desk and visiting referral physicians default to Dr. Sinha for all non-surgical admissions.",
                    why2 = "No clear admission caps or balanced rotational assignment policy existed.",
                    why3 = "Other competent internal medicine consultants were underutilized with capacity utilization below 35%.",
                    why4 = "Admission desk assigns consultants based on patient memory and habit rather than real-time workload visibility.",
                    why5 = "The hospital lacked a real-time patient acuity and capacity tracking system (Heijunka).",
                    rootCauseStatement = "Absence of real-time clinical workload visibility and lack of institutionalized Heijunka patient assignment leveling.",
                    rootCauseCategory = "Process Design",
                    countermeasure = "Implement live Consultant Workload Management dashboard with automated cross-coverage referral leveling when load exceeds 85%.",
                    actionOwner = "Medical Superintendent",
                    targetDate = "2026-03-20",
                    targetKpiImpact = "Distribute admissions evenly so no consultant exceeds 85% capacity utilization.",
                    status = "In Progress"
                )
            )
            db.rootCauseDao().insertAnalyses(sampleRCAs)
        }

        if (db.hmsConfigDao().getCount() == 0) {
            val defaultConfig = HmsEhrConfig(
                hospitalCode = "SGH-RANCHI",
                hospitalName = "Synergy Global Hospital (Main Campus, Ranchi)",
                hmsSystemType = "ABDM FHIR v2 / OpenEMR Enterprise",
                endpointUrl = "https://hms.synergyhospital.org/api/v2/ehr-sync",
                apiKeyMasked = "sgh_live_sec_84712****312",
                autoSyncIntervalMinutes = 5,
                isAutoSyncEnabled = true,
                lastSyncTimestamp = System.currentTimeMillis() - 180_000,
                lastSyncStatus = "Success (All 381 Inpatient & OPD records in sync)",
                totalRecordsSynced = 1420
            )
            db.hmsConfigDao().saveConfig(defaultConfig)
        }

        if (db.userDao().getUserCount() == 0) {
            val defaultUser = AppUser(
                role = "PATIENT",
                hospitalCode = "SGH-RANCHI",
                hospitalName = "Synergy Global Hospital (Main Campus, Ranchi)",
                identifier = "gunjanprakash2670@gmail.com",
                fullName = "Gunjan Prakash",
                hospitalRegNumber = "SGH-2026-0288",
                department = "General Medicine",
                isLoggedIn = true
            )
            db.userDao().insertUser(defaultUser)

            val initialFamily = listOf(
                FamilyMember(
                    primaryUserIdentifier = "gunjanprakash2670@gmail.com",
                    fullName = "Gunjan Prakash",
                    relation = "Self",
                    age = 24,
                    gender = "Male",
                    hospitalRegNumber = "SGH-2026-0288",
                    bloodGroup = "B+"
                ),
                FamilyMember(
                    primaryUserIdentifier = "gunjanprakash2670@gmail.com",
                    fullName = "Bimla Devi",
                    relation = "Mother",
                    age = 72,
                    gender = "Female",
                    hospitalRegNumber = "SGH-2026-0294",
                    bloodGroup = "O+"
                ),
                FamilyMember(
                    primaryUserIdentifier = "gunjanprakash2670@gmail.com",
                    fullName = "Rajesh Gope",
                    relation = "Father",
                    age = 58,
                    gender = "Male",
                    hospitalRegNumber = "SGH-2026-0275",
                    bloodGroup = "B+"
                )
            )
            db.familyMemberDao().insertMembers(initialFamily)
        }

        if (db.pharmacyDao().getCount() == 0) {
            val samplePrescriptions = listOf(
                // For Gunjan Prakash (SGH-2026-0288) - After OPD Consultation
                PharmacyPrescription(
                    hospitalRegNumber = "SGH-2026-0288",
                    patientName = "Gunjan Prakash",
                    doctorName = "Dr. Rahul Sinha",
                    department = "General Medicine",
                    encounterType = "OPD Consultation",
                    medicineName = "Tab Telmisartan 40mg",
                    dosage = "40 mg",
                    frequency = "1-0-0 (Morning After Breakfast)",
                    duration = "30 Days Ongoing",
                    instructions = "For blood pressure regulation. Do not discontinue without consulting doctor.",
                    dispensationStatus = "Dispensed / Ready",
                    datePrescribed = "2026-02-28",
                    rxNumber = "RX-2026-104",
                    pharmacyCounter = "Counter 3 (Main Ground Floor Pharmacy)"
                ),
                PharmacyPrescription(
                    hospitalRegNumber = "SGH-2026-0288",
                    patientName = "Gunjan Prakash",
                    doctorName = "Dr. Rahul Sinha",
                    department = "General Medicine",
                    encounterType = "OPD Consultation",
                    medicineName = "Tab Paracetamol 650mg (Dolo)",
                    dosage = "650 mg",
                    frequency = "SOS (Take as needed for pain/fever)",
                    duration = "5 Days",
                    instructions = "Take with water. Maintain min 6 hrs between doses. Max 3 tabs/day.",
                    dispensationStatus = "Collected",
                    datePrescribed = "2026-02-28",
                    rxNumber = "RX-2026-105",
                    pharmacyCounter = "Counter 3 (Main Ground Floor Pharmacy)"
                ),
                // For Gunjan Prakash (SGH-2026-0288) - After Inpatient Treatment / Discharge
                PharmacyPrescription(
                    hospitalRegNumber = "SGH-2026-0288",
                    patientName = "Gunjan Prakash",
                    doctorName = "Dr. Rahul Sinha",
                    department = "General Medicine",
                    encounterType = "Discharge Medication",
                    medicineName = "Tab Augmentin 625mg (Amoxyclav)",
                    dosage = "625 mg",
                    frequency = "1-0-1 (Twice Daily After Food)",
                    duration = "5 Days",
                    instructions = "Complete entire course even if feeling better.",
                    dispensationStatus = "Dispensed / Ready",
                    datePrescribed = "2026-02-27",
                    rxNumber = "RX-2026-098",
                    pharmacyCounter = "Inpatient Discharge Pharmacy (1st Floor)"
                ),
                PharmacyPrescription(
                    hospitalRegNumber = "SGH-2026-0288",
                    patientName = "Gunjan Prakash",
                    doctorName = "Dr. Rahul Sinha",
                    department = "General Medicine",
                    encounterType = "Discharge Medication",
                    medicineName = "Tab Pantoprazole 40mg (Pan-40)",
                    dosage = "40 mg",
                    frequency = "1-0-0 (Morning Empty Stomach 30m before food)",
                    duration = "14 Days",
                    instructions = "For gastric protection and acid suppression.",
                    dispensationStatus = "Dispensed / Ready",
                    datePrescribed = "2026-02-27",
                    rxNumber = "RX-2026-099",
                    pharmacyCounter = "Inpatient Discharge Pharmacy (1st Floor)"
                ),
                // For Bimla Devi (SGH-2026-0294) - Inpatient Treatment
                PharmacyPrescription(
                    hospitalRegNumber = "SGH-2026-0294",
                    patientName = "Bimla Devi",
                    doctorName = "Dr. Rahul Sinha",
                    department = "General Medicine",
                    encounterType = "Inpatient Treatment",
                    medicineName = "Tab Torsemide 10mg (Dytor)",
                    dosage = "10 mg",
                    frequency = "1-0-0 (Morning)",
                    duration = "30 Days Ongoing",
                    instructions = "Monitor daily urine output. Restrict salt intake.",
                    dispensationStatus = "Dispensed / Ready",
                    datePrescribed = "2026-02-27",
                    rxNumber = "RX-2026-112",
                    pharmacyCounter = "Ward 3 Satellite Dispensary"
                ),
                PharmacyPrescription(
                    hospitalRegNumber = "SGH-2026-0294",
                    patientName = "Bimla Devi",
                    doctorName = "Dr. Rahul Sinha",
                    department = "General Medicine",
                    encounterType = "Inpatient Treatment",
                    medicineName = "Syp Calcium Polystyrene Sulfonate (K-Bind)",
                    dosage = "15 gm",
                    frequency = "0-1-0 (Afternoon with water)",
                    duration = "7 Days",
                    instructions = "For lowering high serum potassium. Renal diet protocol.",
                    dispensationStatus = "In Preparation",
                    datePrescribed = "2026-02-27",
                    rxNumber = "RX-2026-113",
                    pharmacyCounter = "Ward 3 Satellite Dispensary"
                )
            )
            db.pharmacyDao().insertPrescriptions(samplePrescriptions)
        }

        if (db.bloodReportDao().getCount() == 0) {
            val sampleBloodReports = listOf(
                // For Gunjan Prakash (SGH-2026-0288) - After OPD Routine Workup
                BloodLabReport(
                    hospitalRegNumber = "SGH-2026-0288",
                    patientName = "Gunjan Prakash",
                    doctorName = "Dr. Rahul Sinha",
                    department = "General Medicine",
                    encounterType = "OPD Routine Workup",
                    testCategory = "Complete Blood Count (CBC)",
                    reportTitle = "Complete Hemogram with Platelets",
                    sampleDate = "2026-02-28",
                    verifiedBy = "Dr. S. K. Mitra (MD, Chief Pathologist)",
                    overallStatus = "Normal",
                    parametersSummary = "Hb: 13.8 g/dL (Normal) • WBC: 7,400 /uL (Normal) • Platelets: 2.4 Lakhs/uL (Normal)",
                    labBarcode = "LAB-2026-8812"
                ),
                BloodLabReport(
                    hospitalRegNumber = "SGH-2026-0288",
                    patientName = "Gunjan Prakash",
                    doctorName = "Dr. Rahul Sinha",
                    department = "General Medicine",
                    encounterType = "OPD Routine Workup",
                    testCategory = "Blood Glucose / HbA1c",
                    reportTitle = "Glycemic Assessment Panel",
                    sampleDate = "2026-02-28",
                    verifiedBy = "Dr. S. K. Mitra (MD, Chief Pathologist)",
                    overallStatus = "Normal",
                    parametersSummary = "Fasting Blood Sugar: 92 mg/dL • HbA1c: 5.4% (Normal Glycemic Control)",
                    labBarcode = "LAB-2026-8813"
                ),
                // For Gunjan Prakash (SGH-2026-0288) - Post-Treatment Evaluation
                BloodLabReport(
                    hospitalRegNumber = "SGH-2026-0288",
                    patientName = "Gunjan Prakash",
                    doctorName = "Dr. Rahul Sinha",
                    department = "General Medicine",
                    encounterType = "Post-Treatment Evaluation",
                    testCategory = "Kidney Function Test (KFT)",
                    reportTitle = "Renal Profile & Serum Electrolytes",
                    sampleDate = "2026-02-27",
                    verifiedBy = "Dr. S. K. Mitra (MD, Chief Pathologist)",
                    overallStatus = "Normal",
                    parametersSummary = "Creatinine: 1.0 mg/dL • Urea: 18 mg/dL • K+: 4.2 mEq/L • Na+: 139 mEq/L",
                    labBarcode = "LAB-2026-8740"
                ),
                // For Bimla Devi (SGH-2026-0294) - Post-Treatment Inpatient Lab (Hyperkalemia)
                BloodLabReport(
                    hospitalRegNumber = "SGH-2026-0294",
                    patientName = "Bimla Devi",
                    doctorName = "Dr. Rahul Sinha",
                    department = "General Medicine",
                    encounterType = "Post-Treatment Evaluation",
                    testCategory = "Kidney Function Test (KFT)",
                    reportTitle = "Urgent Renal Function & Electrolytes",
                    sampleDate = "2026-02-28",
                    verifiedBy = "Dr. S. K. Mitra (MD, Chief Pathologist)",
                    overallStatus = "Attention Required",
                    parametersSummary = "Creatinine: 2.6 mg/dL [HIGH] • BUN: 48 mg/dL [HIGH] • K+: 5.6 mEq/L [HIGH - Attention]",
                    labBarcode = "LAB-2026-8901"
                ),
                BloodLabReport(
                    hospitalRegNumber = "SGH-2026-0294",
                    patientName = "Bimla Devi",
                    doctorName = "Dr. Rahul Sinha",
                    department = "General Medicine",
                    encounterType = "Admission Workup",
                    testCategory = "Complete Blood Count (CBC)",
                    reportTitle = "Complete Hemogram (Inpatient Baseline)",
                    sampleDate = "2026-02-27",
                    verifiedBy = "Dr. S. K. Mitra (MD, Chief Pathologist)",
                    overallStatus = "Attention Required",
                    parametersSummary = "Hb: 9.4 g/dL [LOW - Anemia] • WBC: 14,800 /uL [HIGH - Infection] • Plt: 1.1 L/uL [LOW]",
                    labBarcode = "LAB-2026-8799"
                )
            )
            db.bloodReportDao().insertReports(sampleBloodReports)
        }

        if (db.patientBillingDao().getCount() == 0) {
            val sampleBills = listOf(
                // Gunjan Prakash (SGH-2026-0288) Bills
                PatientBill(
                    hospitalRegNumber = "SGH-2026-0288",
                    patientName = "Gunjan Prakash",
                    billNumber = "INV-2026-0891",
                    billType = "OPD Consultation",
                    billDate = "2026-02-28",
                    totalAmount = 600.0,
                    paidAmount = 600.0,
                    dueAmount = 0.0,
                    paymentStatus = "Paid",
                    paymentMode = "UPI / Online (PhonePe)",
                    itemizedSummary = "OPD Consultation with Dr. Rahul Sinha (General Medicine) • Vitals & BP Check • Clinical Assessment",
                    receiptBarcode = "REC-OPD-0891"
                ),
                PatientBill(
                    hospitalRegNumber = "SGH-2026-0288",
                    patientName = "Gunjan Prakash",
                    billNumber = "INV-2026-0895",
                    billType = "Lab & Diagnostics",
                    billDate = "2026-02-28",
                    totalAmount = 1450.0,
                    paidAmount = 1450.0,
                    dueAmount = 0.0,
                    paymentStatus = "Paid",
                    paymentMode = "UPI / Online (GPay)",
                    itemizedSummary = "Complete Hemogram (CBC) with Automated Differential (₹ 450) • Fasting Blood Sugar (FBS ₹ 150) • Glycated Hemoglobin (HbA1c ₹ 850)",
                    receiptBarcode = "REC-LAB-0895"
                ),
                PatientBill(
                    hospitalRegNumber = "SGH-2026-0288",
                    patientName = "Gunjan Prakash",
                    billNumber = "INV-2026-0902",
                    billType = "Pharmacy",
                    billDate = "2026-02-28",
                    totalAmount = 880.0,
                    paidAmount = 880.0,
                    dueAmount = 0.0,
                    paymentStatus = "Paid",
                    paymentMode = "Debit Card",
                    itemizedSummary = "Tab Telmisartan 40mg (Strip of 30 tabs ₹ 580) • Tab Paracetamol 650mg Dolo (Strip of 15 tabs ₹ 60) • Oral Rehydration Salts (₹ 240)",
                    receiptBarcode = "REC-PHARM-0902"
                ),
                PatientBill(
                    hospitalRegNumber = "SGH-2026-0288",
                    patientName = "Gunjan Prakash",
                    billNumber = "INV-2026-0412",
                    billType = "IPD Admission",
                    billDate = "2026-01-14",
                    totalAmount = 38400.0,
                    paidAmount = 38400.0,
                    dueAmount = 0.0,
                    paymentStatus = "Paid",
                    paymentMode = "TPA Cashless (Star Health)",
                    itemizedSummary = "3 Days Semi-Private Room Accommodation • Nursing & Resident Doctor rounds • IV Infusion therapy • Discharge summary & Gate pass clearance",
                    receiptBarcode = "REC-IPD-0412"
                ),
                // Bimla Devi (SGH-2026-0294) Bills (Admitted Inpatient)
                PatientBill(
                    hospitalRegNumber = "SGH-2026-0294",
                    patientName = "Bimla Devi",
                    billNumber = "INV-2026-1044",
                    billType = "IPD Admission",
                    billDate = "2026-02-28",
                    totalAmount = 26800.0,
                    paidAmount = 0.0,
                    dueAmount = 26800.0,
                    paymentStatus = "TPA Approved",
                    paymentMode = "TPA Cashless (Medi Assist)",
                    itemizedSummary = "2 Days Inpatient Acute Care (Ward 3B - Bed 14) • Daily Consultant Visits • Continuous Renal Protocol • Pre-Auth Cashless Approved for ₹ 80,000",
                    receiptBarcode = "REC-IPD-1044"
                ),
                PatientBill(
                    hospitalRegNumber = "SGH-2026-0294",
                    patientName = "Bimla Devi",
                    billNumber = "INV-2026-1048",
                    billType = "Lab & Diagnostics",
                    billDate = "2026-02-28",
                    totalAmount = 1200.0,
                    paidAmount = 1200.0,
                    dueAmount = 0.0,
                    paymentStatus = "Paid",
                    paymentMode = "Cash Desk",
                    itemizedSummary = "STAT Urgent Kidney Function Test (KFT) • Serum Electrolytes (Na+, K+, Cl-) • Blood Urea Nitrogen",
                    receiptBarcode = "REC-LAB-1048"
                )
            )
            db.patientBillingDao().insertBills(sampleBills)
        }

        if (db.dailyIpdBillDao().getCount() == 0) {
            val sampleDailyIpd = listOf(
                // For Bimla Devi (SGH-2026-0294) - Currently Admitted (Day 1 & Day 2)
                DailyIpdBillEntry(
                    hospitalRegNumber = "SGH-2026-0294",
                    patientName = "Bimla Devi",
                    dayNumber = 1,
                    billDate = "2026-02-27 (Admission Day)",
                    morningUpdateTimestamp = "Audit Verified on 2026-02-28 at 07:15 AM",
                    roomAndNursingCharges = 3500.0,
                    doctorConsultationFee = 2000.0,
                    medicationsAndPharmacy = 3400.0,
                    labAndDiagnostics = 3200.0,
                    consumablesAndSurgical = 900.0,
                    dayTotal = 13000.0,
                    cumulativeRunningTotal = 13000.0,
                    auditStatus = "Audit Verified & Reconciled"
                ),
                DailyIpdBillEntry(
                    hospitalRegNumber = "SGH-2026-0294",
                    patientName = "Bimla Devi",
                    dayNumber = 2,
                    billDate = "2026-02-28 (Yesterday's Stay)",
                    morningUpdateTimestamp = "Updated Today at 07:30 AM (Morning Audit Desk)",
                    roomAndNursingCharges = 3500.0,
                    doctorConsultationFee = 2000.0,
                    medicationsAndPharmacy = 4200.0,
                    labAndDiagnostics = 2800.0,
                    consumablesAndSurgical = 1300.0,
                    dayTotal = 13800.0,
                    cumulativeRunningTotal = 26800.0,
                    auditStatus = "Morning Ledger Audit Completed • Ready for TPA Submission"
                ),
                // For Gunjan Prakash (SGH-2026-0288) - From Inpatient Care Record
                DailyIpdBillEntry(
                    hospitalRegNumber = "SGH-2026-0288",
                    patientName = "Gunjan Prakash",
                    dayNumber = 1,
                    billDate = "2026-01-12 (Day 1)",
                    morningUpdateTimestamp = "Verified at 07:15 AM",
                    roomAndNursingCharges = 3200.0,
                    doctorConsultationFee = 1800.0,
                    medicationsAndPharmacy = 4200.0,
                    labAndDiagnostics = 2200.0,
                    consumablesAndSurgical = 800.0,
                    dayTotal = 12200.0,
                    cumulativeRunningTotal = 12200.0,
                    auditStatus = "Audit Verified"
                ),
                DailyIpdBillEntry(
                    hospitalRegNumber = "SGH-2026-0288",
                    patientName = "Gunjan Prakash",
                    dayNumber = 2,
                    billDate = "2026-01-13 (Day 2 - Yesterday of stay)",
                    morningUpdateTimestamp = "Updated Morning at 07:30 AM",
                    roomAndNursingCharges = 3200.0,
                    doctorConsultationFee = 1800.0,
                    medicationsAndPharmacy = 5400.0,
                    labAndDiagnostics = 3400.0,
                    consumablesAndSurgical = 1000.0,
                    dayTotal = 14800.0,
                    cumulativeRunningTotal = 27000.0,
                    auditStatus = "Audit Verified"
                ),
                DailyIpdBillEntry(
                    hospitalRegNumber = "SGH-2026-0288",
                    patientName = "Gunjan Prakash",
                    dayNumber = 3,
                    billDate = "2026-01-14 (Discharge Clearance Day)",
                    morningUpdateTimestamp = "Final Clearance at 11:15 AM",
                    roomAndNursingCharges = 3200.0,
                    doctorConsultationFee = 1800.0,
                    medicationsAndPharmacy = 4600.0,
                    labAndDiagnostics = 1200.0,
                    consumablesAndSurgical = 600.0,
                    dayTotal = 11400.0,
                    cumulativeRunningTotal = 38400.0,
                    auditStatus = "TPA Cashless Settled"
                )
            )
            db.dailyIpdBillDao().insertDailyBills(sampleDailyIpd)
        }

        if (db.patientInsuranceDao().getCount() == 0) {
            val sampleInsurances = listOf(
                PatientInsurance(
                    hospitalRegNumber = "SGH-2026-0288",
                    patientName = "Gunjan Prakash",
                    tpaProvider = "Star Health & Allied Insurance",
                    policyNumber = "POL-SH-9842109",
                    tpaCardNumber = "TPA-SH-2026-8819",
                    sumInsured = 500000.0,
                    preAuthApprovedAmount = 150000.0,
                    claimStatus = "Pre-Auth Approved (Cashless Active)",
                    claimNumber = "CLM-2026-STAR-449",
                    copayPercentage = 0,
                    tpaHelpdeskContact = "Star Health Desk Ext: 1044 / +91-9876543210 (24/7 Desk)"
                ),
                PatientInsurance(
                    hospitalRegNumber = "SGH-2026-0294",
                    patientName = "Bimla Devi",
                    tpaProvider = "Medi Assist Insurance TPA (National Insurance)",
                    policyNumber = "POL-MA-774129",
                    tpaCardNumber = "MA-CARD-9921",
                    sumInsured = 300000.0,
                    preAuthApprovedAmount = 80000.0,
                    claimStatus = "Pre-Auth Approved (Enhancement Pending)",
                    claimNumber = "CLM-MA-2026-1092",
                    copayPercentage = 10,
                    tpaHelpdeskContact = "Medi Assist Desk Ext: 1046 / +91-9835123456"
                )
            )
            db.patientInsuranceDao().insertInsurances(sampleInsurances)
        }

        if (db.nurseCallDao().getCount() == 0) {
            val defaultNurseCall = NurseCallRequest(
                hospitalRegNumber = "SGH-2026-0294",
                patientName = "Bimla Devi",
                bedNumber = "Ward 3B - Bed 14",
                wardName = "Internal Medicine (3rd Floor)",
                callReason = "IV Fluid Bottle Check & Assistance",
                callTime = "Today, 10:15 AM",
                status = "Nurse Dispatched",
                attendingNurseName = "Staff Nurse Priya Sharma (RN)",
                responseNotes = "Nurse notified at Central Nursing Station 3B"
            )
            db.nurseCallDao().insertNurseCall(defaultNurseCall)
        }

        if (db.patientFeedbackDao().getCount() == 0) {
            val defaultFeedback = PatientFeedback(
                hospitalRegNumber = "SGH-2026-0288",
                patientName = "Gunjan Prakash",
                encounterType = "OPD",
                doctorScore = 9,
                nursingScore = 9,
                hygieneScore = 10,
                billingTransparencyScore = 9,
                overallScore = 10,
                averageScore = 9.4,
                comments = "Very professional and smooth consultation with Dr. Rahul Sinha. Waiting time was minimal and billing transparency was outstanding.",
                googleReviewSynced = true,
                submissionDate = "2026-02-28"
            )
            db.patientFeedbackDao().insertFeedback(defaultFeedback)
        }
    }

    suspend fun addPatient(patient: PatientRecord) = withContext(Dispatchers.IO) {
        db.patientDao().insertPatient(patient)
    }

    suspend fun updatePatient(patient: PatientRecord) = withContext(Dispatchers.IO) {
        db.patientDao().updatePatient(patient)
    }

    suspend fun dischargePatient(id: Long, losDays: Int, delayReason: String?, notes: String) = withContext(Dispatchers.IO) {
        // Fetch and update
    }

    suspend fun addWasteLog(log: LeanWasteLog) = withContext(Dispatchers.IO) {
        db.leanWasteDao().insertWasteLog(log)
    }

    suspend fun deleteWasteLog(log: LeanWasteLog) = withContext(Dispatchers.IO) {
        db.leanWasteDao().deleteWasteLog(log)
    }

    suspend fun addFiveSAudit(audit: FiveSAudit) = withContext(Dispatchers.IO) {
        db.fiveSAuditDao().insertAudit(audit)
    }

    suspend fun deleteFiveSAudit(audit: FiveSAudit) = withContext(Dispatchers.IO) {
        db.fiveSAuditDao().deleteAudit(audit)
    }

    suspend fun addRedTag(item: FiveSRedTagItem) = withContext(Dispatchers.IO) {
        db.fiveSRedTagDao().insertRedTag(item)
    }

    suspend fun updateRedTag(item: FiveSRedTagItem) = withContext(Dispatchers.IO) {
        db.fiveSRedTagDao().updateRedTag(item)
    }

    suspend fun addCapa(capa: FiveSCapaItem) = withContext(Dispatchers.IO) {
        db.fiveSCapaDao().insertCapa(capa)
    }

    suspend fun updateCapa(capa: FiveSCapaItem) = withContext(Dispatchers.IO) {
        db.fiveSCapaDao().updateCapa(capa)
    }

    val standardConsultants = listOf(
        "Dr. Rahul Sinha" to "General Medicine",
        "Dr. M.S. Islam" to "General Surgery",
        "Dr. Rajnish Kumar" to "Orthopedics",
        "Dr. Javed Akhtar" to "Obstetrics & Gyn",
        "Dr. Jatin Sethi" to "Critical Care & ICU",
        "Dr. Vivek Goswami" to "General Surgery",
        "Dr. Manjar Ali" to "Orthopedics",
        "Dr. S. Nawal" to "Pediatrics"
    )

    suspend fun reassignPatient(patientId: Long, newConsultant: String) = withContext(Dispatchers.IO) {
        db.patientDao().reassignPatient(patientId, newConsultant)
    }

    suspend fun updatePatientAcuity(patientId: Long, newAcuity: Int) = withContext(Dispatchers.IO) {
        db.patientDao().updatePatientAcuity(patientId, newAcuity)
    }

    suspend fun updatePatientFlowStage(patientId: Long, newStage: String) = withContext(Dispatchers.IO) {
        db.patientDao().updateFlowStage(patientId, newStage)
    }

    suspend fun addKaizen(project: KaizenProject) = withContext(Dispatchers.IO) {
        db.kaizenDao().insertKaizen(project)
    }

    suspend fun updateKaizen(project: KaizenProject) = withContext(Dispatchers.IO) {
        db.kaizenDao().updateKaizen(project)
    }

    suspend fun addRootCauseAnalysis(analysis: RootCauseAnalysis) = withContext(Dispatchers.IO) {
        db.rootCauseDao().insertAnalysis(analysis)
    }

    suspend fun updateRootCauseAnalysis(analysis: RootCauseAnalysis) = withContext(Dispatchers.IO) {
        db.rootCauseDao().updateAnalysis(analysis)
    }

    suspend fun deleteRootCauseAnalysis(analysis: RootCauseAnalysis) = withContext(Dispatchers.IO) {
        db.rootCauseDao().deleteAnalysis(analysis)
    }

    val defaultOpdClinics = listOf(
        OpdQueueItem(
            department = "General Medicine",
            doctorName = "Dr. Rahul Sinha",
            roomNumber = "Room 102 (OPD Ground Floor)",
            currentServingToken = 14,
            totalTokensIssued = 32,
            avgConsultationMinutes = 8,
            estimatedWaitMinutes = 48,
            status = "Peak Hour"
        ),
        OpdQueueItem(
            department = "General Surgery",
            doctorName = "Dr. M.S. Islam",
            roomNumber = "Room 105 (Surgical Wing)",
            currentServingToken = 8,
            totalTokensIssued = 14,
            avgConsultationMinutes = 10,
            estimatedWaitMinutes = 20,
            status = "On Schedule"
        ),
        OpdQueueItem(
            department = "Orthopedics",
            doctorName = "Dr. Rajnish Kumar",
            roomNumber = "Room 108 (Trauma & Ortho)",
            currentServingToken = 11,
            totalTokensIssued = 24,
            avgConsultationMinutes = 7,
            estimatedWaitMinutes = 35,
            status = "Slight Delay"
        ),
        OpdQueueItem(
            department = "Obstetrics & Gyn",
            doctorName = "Dr. Javed Akhtar",
            roomNumber = "Room 201 (Maternal Care, 1st Fl)",
            currentServingToken = 16,
            totalTokensIssued = 22,
            avgConsultationMinutes = 9,
            estimatedWaitMinutes = 18,
            status = "On Schedule"
        ),
        OpdQueueItem(
            department = "Pediatrics",
            doctorName = "Dr. S. Nawal",
            roomNumber = "Room 204 (Child Health Clinic)",
            currentServingToken = 6,
            totalTokensIssued = 10,
            avgConsultationMinutes = 6,
            estimatedWaitMinutes = 12,
            status = "On Schedule"
        ),
        OpdQueueItem(
            department = "Critical Care & ICU",
            doctorName = "Dr. Jatin Sethi",
            roomNumber = "Room 301 (ICU Consult Room)",
            currentServingToken = 3,
            totalTokensIssued = 5,
            avgConsultationMinutes = 15,
            estimatedWaitMinutes = 15,
            status = "On Schedule"
        )
    )

    suspend fun loginOrRegister(
        role: String,
        hospitalCode: String,
        identifier: String,
        fullName: String,
        hospitalRegNumber: String,
        department: String
    ): AppUser = withContext(Dispatchers.IO) {
        db.userDao().logoutAll()
        val existing = db.userDao().findUser(identifier, hospitalCode)
        if (existing != null) {
            val updated = existing.copy(
                isLoggedIn = true,
                role = role,
                fullName = if (fullName.isNotBlank()) fullName else existing.fullName,
                hospitalRegNumber = if (hospitalRegNumber.isNotBlank()) hospitalRegNumber else existing.hospitalRegNumber,
                department = if (department.isNotBlank()) department else existing.department
            )
            db.userDao().updateUser(updated)
            updated
        } else {
            val newUser = AppUser(
                role = role,
                hospitalCode = hospitalCode.uppercase().trim(),
                hospitalName = HospitalDirectory.getHospitalName(hospitalCode),
                identifier = identifier.trim(),
                fullName = fullName.ifBlank { "User ${identifier.take(6)}" },
                hospitalRegNumber = hospitalRegNumber.ifBlank { "MRN-${(1000..9999).random()}" },
                department = department,
                isLoggedIn = true
            )
            val id = db.userDao().insertUser(newUser)
            if (role == "PATIENT") {
                db.familyMemberDao().insertMember(
                    FamilyMember(
                        primaryUserIdentifier = newUser.identifier,
                        fullName = newUser.fullName,
                        relation = "Self",
                        age = 30,
                        gender = "Male",
                        hospitalRegNumber = newUser.hospitalRegNumber
                    )
                )
            }
            newUser.copy(id = id)
        }
    }

    suspend fun registerPatientWithFamily(
        hospitalCode: String,
        identifier: String,
        fullName: String,
        hospitalRegNumber: String,
        familyMembers: List<FamilyMember>
    ): AppUser = withContext(Dispatchers.IO) {
        val user = loginOrRegister(
            role = "PATIENT",
            hospitalCode = hospitalCode,
            identifier = identifier,
            fullName = fullName,
            hospitalRegNumber = hospitalRegNumber,
            department = "General Medicine"
        )
        familyMembers.forEach { member ->
            if (member.relation != "Self") {
                db.familyMemberDao().insertMember(
                    member.copy(primaryUserIdentifier = user.identifier)
                )
            }
        }
        user
    }

    suspend fun logoutUser() = withContext(Dispatchers.IO) {
        db.userDao().logoutAll()
    }

    suspend fun switchActiveRole(newRole: String) = withContext(Dispatchers.IO) {
        db.userDao().updateActiveUserRole(newRole.uppercase().trim())
    }

    suspend fun addFamilyMember(member: FamilyMember) = withContext(Dispatchers.IO) {
        db.familyMemberDao().insertMember(member)
    }

    suspend fun deleteFamilyMember(member: FamilyMember) = withContext(Dispatchers.IO) {
        db.familyMemberDao().deleteMember(member)
    }

    suspend fun saveHmsConfig(config: HmsEhrConfig) = withContext(Dispatchers.IO) {
        db.hmsConfigDao().saveConfig(config)
    }

    suspend fun addPrescription(prescription: PharmacyPrescription) = withContext(Dispatchers.IO) {
        db.pharmacyDao().insertPrescription(prescription)
    }

    suspend fun updatePrescriptionStatus(id: Long, status: String) = withContext(Dispatchers.IO) {
        db.pharmacyDao().updateDispensationStatus(id, status)
    }

    suspend fun addBloodReport(report: BloodLabReport) = withContext(Dispatchers.IO) {
        db.bloodReportDao().insertReport(report)
    }

    suspend fun addBill(bill: PatientBill) = withContext(Dispatchers.IO) {
        db.patientBillingDao().insertBill(bill)
    }

    suspend fun addDailyIpdBill(entry: DailyIpdBillEntry) = withContext(Dispatchers.IO) {
        db.dailyIpdBillDao().insertDailyBill(entry)
    }

    suspend fun addInsurance(insurance: PatientInsurance) = withContext(Dispatchers.IO) {
        db.patientInsuranceDao().insertInsurance(insurance)
    }

    suspend fun insertNurseCall(request: NurseCallRequest): Long = withContext(Dispatchers.IO) {
        db.nurseCallDao().insertNurseCall(request)
    }

    suspend fun updateNurseCallStatus(id: Long, status: String) = withContext(Dispatchers.IO) {
        db.nurseCallDao().updateCallStatus(id, status)
    }

    suspend fun insertFeedback(feedback: PatientFeedback): Long = withContext(Dispatchers.IO) {
        db.patientFeedbackDao().insertFeedback(feedback)
    }

    suspend fun updateGoogleReviewSync(id: Long, synced: Boolean) = withContext(Dispatchers.IO) {
        db.patientFeedbackDao().updateGoogleReviewSync(id, synced)
    }

    fun getDefaultDiagnosisSummaries(): List<PatientDiagnosisSummary> = listOf(
        PatientDiagnosisSummary(
            id = "diag_1",
            hospitalRegNumber = "SGH-2026-0288",
            patientName = "Gunjan Prakash",
            encounterDate = "2026-02-28",
            encounterType = "OPD Consultation",
            primaryDiagnosis = "Essential Hypertension (Stage 1) with Mild Dehydration",
            icd10Code = "I10 / E86.0",
            doctorName = "Dr. Rahul Sinha, MD",
            department = "General Medicine",
            bloodPressure = "142/92 mmHg",
            pulseRate = "78 bpm",
            spo2 = "99%",
            temperature = "98.4 °F",
            chiefComplaints = "Occasional early morning headache and mild exertion fatigue for past 2 weeks.",
            clinicalAdvice = "Low sodium dietary restriction (<2g/day). 30 mins brisk walking. Tab Telmisartan 40mg OD. Review after 14 days with BP log."
        ),
        PatientDiagnosisSummary(
            id = "diag_2",
            hospitalRegNumber = "SGH-2026-0294",
            patientName = "Bimla Devi",
            encounterDate = "2026-02-27",
            encounterType = "Inpatient Admission",
            primaryDiagnosis = "Acute on Chronic Kidney Disease with Hyperkalemia (CKD Stage 3b)",
            icd10Code = "N18.3 / E87.5",
            doctorName = "Dr. Rahul Sinha, MD",
            department = "General Medicine (Nephrology Co-management)",
            bloodPressure = "158/96 mmHg",
            pulseRate = "84 bpm",
            spo2 = "96% on room air",
            temperature = "98.6 °F",
            chiefComplaints = "Bilateral lower extremity edema, reduced urine output, and nausea for 3 days.",
            clinicalAdvice = "Strict fluid restriction (1 L/24hr). Renal diet (low potassium, low phosphorus). Strict daily urine output tracking."
        )
    )

    suspend fun triggerHmsSync(hospitalCode: String): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        kotlinx.coroutines.delay(1200)
        val now = System.currentTimeMillis()
        val added = (5..15).random()
        val status = "Synced: Inpatient census, Bed occupancy, OPD tokens, Pharmacy medicines & Blood lab reports ($added delta records updated)"
        db.hmsConfigDao().updateSyncStatus(hospitalCode, now, added, status)
        Pair(true, status)
    }
}
