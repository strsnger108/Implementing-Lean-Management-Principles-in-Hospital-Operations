package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.model.ConsultantStat
import com.example.data.model.FiveSAudit
import com.example.data.model.InsightType
import com.example.data.model.KaizenProject
import com.example.data.model.LeanInsight
import com.example.data.model.LeanWasteLog
import com.example.data.model.MonthlyMetrics
import com.example.data.model.PatientRecord
import com.example.data.model.VsmStage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class HospitalRepository(private val db: AppDatabase) {

    val allPatients: Flow<List<PatientRecord>> = db.patientDao().getAllPatients()
    val activePatients: Flow<List<PatientRecord>> = db.patientDao().getActivePatients()
    val dischargedPatients: Flow<List<PatientRecord>> = db.patientDao().getDischargedPatients()
    val wasteLogs: Flow<List<LeanWasteLog>> = db.leanWasteDao().getAllWasteLogs()
    val fiveSAudits: Flow<List<FiveSAudit>> = db.fiveSAuditDao().getAllAudits()
    val kaizenProjects: Flow<List<KaizenProject>> = db.kaizenDao().getAllKaizen()

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
                    notes = "Hypertensive urgency managed. Preparing discharge summary."
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

    suspend fun addFiveSAudit(audit: FiveSAudit) = withContext(Dispatchers.IO) {
        db.fiveSAuditDao().insertAudit(audit)
    }

    suspend fun addKaizen(project: KaizenProject) = withContext(Dispatchers.IO) {
        db.kaizenDao().insertKaizen(project)
    }

    suspend fun updateKaizen(project: KaizenProject) = withContext(Dispatchers.IO) {
        db.kaizenDao().updateKaizen(project)
    }
}
