package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.AcuityDefinitions
import com.example.data.model.ConsultantStat
import com.example.data.model.ConsultantWorkload
import com.example.data.model.DepartmentLosBenchmark
import com.example.data.model.DischargeHourDistribution
import com.example.data.model.FiveSAudit
import com.example.data.model.FiveSCapaItem
import com.example.data.model.FiveSRedTagItem
import com.example.data.model.KaizenProject
import com.example.data.model.LeanInsight
import com.example.data.model.LeanWasteLog
import com.example.data.model.MonthlyMetrics
import com.example.data.model.PatientAcuityConfig
import com.example.data.model.AppUser
import com.example.data.model.BloodLabReport
import com.example.data.model.DailyIpdBillEntry
import com.example.data.model.FamilyMember
import com.example.data.model.HmsEhrConfig
import com.example.data.model.NurseCallRequest
import com.example.data.model.OpdQueueItem
import com.example.data.model.PatientBill
import com.example.data.model.PatientDiagnosisSummary
import com.example.data.model.PatientFeedback
import com.example.data.model.PatientInsurance
import com.example.data.model.PatientOpdToken
import com.example.data.model.PatientRecord
import com.example.data.model.PharmacyPrescription
import com.example.data.model.RootCauseAnalysis
import com.example.data.model.ThroughputMetrics
import com.example.data.model.ThroughputTrendPoint
import com.example.data.model.VsmStage
import com.example.data.model.WorkloadStatus
import com.example.data.repository.HospitalRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class HospitalViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    val repository = HospitalRepository(db)

    // User Session & Role State
    val activeUserSession: StateFlow<AppUser?> = repository.activeUserSession.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )

    // Family Members of Active User
    val familyMembers: StateFlow<List<FamilyMember>> = activeUserSession.flatMapLatest { user ->
        if (user != null) repository.getFamilyMembers(user.identifier) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Family Member Profile for Patient View
    val selectedFamilyMemberId = MutableStateFlow<Long?>(null)

    // HMS / EHR Configuration
    val hmsConfig: StateFlow<HmsEhrConfig?> = repository.hmsConfig.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )

    val isHmsSyncing = MutableStateFlow(false)
    val hmsSyncMessage = MutableStateFlow<String?>(null)

    // OPD Queue & Waiting Times
    val opdClinics: List<OpdQueueItem> = repository.defaultOpdClinics

    // Active Patient OPD Token State
    private val _myOpdTokens = MutableStateFlow<List<PatientOpdToken>>(
        listOf(
            PatientOpdToken(
                id = "token_1",
                tokenNumber = 18,
                patientName = "Gunjan Prakash",
                doctorName = "Dr. Rahul Sinha",
                department = "General Medicine",
                roomNumber = "Room 102",
                currentServingToken = 14,
                estWaitMinutes = 32,
                bookingTime = "09:30 AM",
                status = "Waiting"
            )
        )
    )
    val myOpdTokens: StateFlow<List<PatientOpdToken>> = _myOpdTokens.asStateFlow()

    // Selected Patient Reg Number for filtering Pharmacy & Blood Reports
    private val _selectedRegNumber = MutableStateFlow<String?>(null)
    val selectedRegNumber: StateFlow<String?> = _selectedRegNumber.asStateFlow()

    fun setSelectedPatientRegNumber(reg: String?) {
        _selectedRegNumber.value = reg
    }

    // Pharmacy Prescriptions for active user / selected dependent
    val activePatientPrescriptions: StateFlow<List<PharmacyPrescription>> = combine(
        activeUserSession,
        _selectedRegNumber
    ) { user, manualReg ->
        manualReg ?: user?.hospitalRegNumber ?: "SGH-2026-0288"
    }.flatMapLatest { reg ->
        repository.getPrescriptionsByRegNumber(reg)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPrescriptions: StateFlow<List<PharmacyPrescription>> = repository.allPrescriptions.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    // Blood Lab Reports for active user / selected dependent
    val activePatientBloodReports: StateFlow<List<BloodLabReport>> = combine(
        activeUserSession,
        _selectedRegNumber
    ) { user, manualReg ->
        manualReg ?: user?.hospitalRegNumber ?: "SGH-2026-0288"
    }.flatMapLatest { reg ->
        repository.getBloodReportsByRegNumber(reg)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allBloodReports: StateFlow<List<BloodLabReport>> = repository.allBloodReports.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    // Patient Bills for active user / selected dependent
    val activePatientBills: StateFlow<List<PatientBill>> = combine(
        activeUserSession,
        _selectedRegNumber
    ) { user, manualReg ->
        manualReg ?: user?.hospitalRegNumber ?: "SGH-2026-0288"
    }.flatMapLatest { reg ->
        repository.getBillsByRegNumber(reg)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allBills: StateFlow<List<PatientBill>> = repository.allBills.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    // Daily IPD Bill updates (with yesterday morning update)
    val activePatientDailyIpdBills: StateFlow<List<DailyIpdBillEntry>> = combine(
        activeUserSession,
        _selectedRegNumber
    ) { user, manualReg ->
        manualReg ?: user?.hospitalRegNumber ?: "SGH-2026-0288"
    }.flatMapLatest { reg ->
        repository.getDailyIpdBills(reg)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Patient TPA / Health Insurance
    val activePatientInsurance: StateFlow<PatientInsurance?> = combine(
        activeUserSession,
        _selectedRegNumber
    ) { user, manualReg ->
        manualReg ?: user?.hospitalRegNumber ?: "SGH-2026-0288"
    }.flatMapLatest { reg ->
        repository.getInsuranceByRegNumber(reg)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Nurse Calls for active patient
    val activePatientNurseCalls: StateFlow<List<NurseCallRequest>> = combine(
        activeUserSession,
        _selectedRegNumber
    ) { user, manualReg ->
        manualReg ?: user?.hospitalRegNumber ?: "SGH-2026-0288"
    }.flatMapLatest { reg ->
        repository.getNurseCallsByRegNumber(reg)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Nurse Call Dialog / Notification state
    val activeNurseCallAlert = MutableStateFlow<NurseCallRequest?>(null)

    // Patient Feedback list
    val activePatientFeedbacks: StateFlow<List<PatientFeedback>> = combine(
        activeUserSession,
        _selectedRegNumber
    ) { user, manualReg ->
        manualReg ?: user?.hospitalRegNumber ?: "SGH-2026-0288"
    }.flatMapLatest { reg ->
        repository.getFeedbacksByRegNumber(reg)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allNurseCalls: StateFlow<List<NurseCallRequest>> = repository.allNurseCalls.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val allFeedbacks: StateFlow<List<PatientFeedback>> = repository.allFeedbacks.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    // Clinical Diagnosis & OPD summaries
    val diagnosisSummaries: List<PatientDiagnosisSummary> = repository.getDefaultDiagnosisSummaries()

    fun updateNurseCallStatus(id: Long, status: String) {
        viewModelScope.launch {
            repository.updateNurseCallStatus(id, status)
            if (activeNurseCallAlert.value?.id == id) {
                activeNurseCallAlert.value = activeNurseCallAlert.value?.copy(status = status)
            }
        }
    }

    fun updatePatientFlowStage(patientId: Long, newStage: String) {
        viewModelScope.launch {
            val patient = allPatients.value.firstOrNull { it.id == patientId }
            if (patient != null) {
                val isDischarged = newStage.equals("Discharge", ignoreCase = true)
                repository.updatePatient(patient.copy(flowStage = newStage, isDischarged = isDischarged))
            }
        }
    }

    fun switchActiveRole(newRole: String) {
        viewModelScope.launch {
            repository.switchActiveRole(newRole)
        }
    }

    // Active OPD doctor chamber token state
    private val _currentServingTokenNumber = MutableStateFlow(14)
    val currentServingTokenNumber: StateFlow<Int> = _currentServingTokenNumber.asStateFlow()

    private val _currentChamberStatus = MutableStateFlow("In Consultation")
    val currentChamberStatus: StateFlow<String> = _currentChamberStatus.asStateFlow()

    fun callNextOpdToken() {
        _currentServingTokenNumber.value += 1
        _currentChamberStatus.value = "Patient Called"
        // Also update the patient OPD token wait estimate
        val currentTokens = _myOpdTokens.value
        _myOpdTokens.value = currentTokens.map {
            val newServing = _currentServingTokenNumber.value
            val wait = ((it.tokenNumber - newServing) * 8).coerceAtLeast(0)
            val st = if (it.tokenNumber == newServing) "In Consultation" else if (it.tokenNumber < newServing) "Completed" else "Waiting"
            it.copy(currentServingToken = newServing, estWaitMinutes = wait, status = st)
        }
    }

    fun holdCurrentOpdToken() {
        _currentChamberStatus.value = "On Hold (Diagnostic / ECG)"
    }

    fun completeCurrentOpdToken() {
        _currentChamberStatus.value = "Consultation Completed"
    }

    fun addBedsideDailyCharge(
        regNumber: String,
        patientName: String,
        dayNum: Int,
        roomFee: Double,
        doctorFee: Double,
        medsFee: Double,
        labFee: Double,
        consumablesFee: Double
    ) {
        viewModelScope.launch {
            val dayTotal = roomFee + doctorFee + medsFee + labFee + consumablesFee
            val existing = repository.getDailyIpdBills(regNumber)
            val cumulative = dayTotal // calculated or appended
            val entry = DailyIpdBillEntry(
                hospitalRegNumber = regNumber,
                patientName = patientName,
                dayNumber = dayNum,
                billDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date()) + " (Shift Ledger)",
                morningUpdateTimestamp = "Logged by Ward Staff at " + java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault()).format(java.util.Date()),
                roomAndNursingCharges = roomFee,
                doctorConsultationFee = doctorFee,
                medicationsAndPharmacy = medsFee,
                labAndDiagnostics = labFee,
                consumablesAndSurgical = consumablesFee,
                dayTotal = dayTotal,
                cumulativeRunningTotal = dayTotal + 26800.0, // baseline
                auditStatus = "Logged by Ward Nurse • Ready for Midnight Audit"
            )
            repository.addDailyIpdBill(entry)
        }
    }

    fun triggerNurseCall(
        regNumber: String,
        patientName: String,
        bedNumber: String,
        wardName: String,
        reason: String
    ) {
        viewModelScope.launch {
            val req = NurseCallRequest(
                hospitalRegNumber = regNumber,
                patientName = patientName,
                bedNumber = bedNumber,
                wardName = wardName,
                callReason = reason,
                callTime = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault()).format(java.util.Date()),
                status = "Nurse Dispatched",
                attendingNurseName = "Staff Nurse Priya Sharma (RN)",
                responseNotes = "Nurse notified at Central Nursing Station 3B • En route to $bedNumber"
            )
            val id = repository.insertNurseCall(req)
            activeNurseCallAlert.value = req.copy(id = id)
        }
    }

    fun dismissNurseCallAlert() {
        activeNurseCallAlert.value = null
    }

    fun cancelNurseCall(id: Long) {
        viewModelScope.launch {
            repository.updateNurseCallStatus(id, "Cancelled by Patient")
            activeNurseCallAlert.value = null
        }
    }

    fun submitFeedback(
        regNumber: String,
        patientName: String,
        encounterType: String,
        docScore: Int,
        nurseScore: Int,
        hygieneScore: Int,
        billingScore: Int,
        overallScore: Int,
        comments: String,
        autoSyncGoogle: Boolean
    ) {
        viewModelScope.launch {
            val avg = (docScore + nurseScore + hygieneScore + billingScore + overallScore) / 5.0
            val feedback = PatientFeedback(
                hospitalRegNumber = regNumber,
                patientName = patientName,
                encounterType = encounterType,
                doctorScore = docScore,
                nursingScore = nurseScore,
                hygieneScore = hygieneScore,
                billingTransparencyScore = billingScore,
                overallScore = overallScore,
                averageScore = Math.round(avg * 10.0) / 10.0,
                comments = comments,
                googleReviewSynced = autoSyncGoogle,
                submissionDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
            )
            repository.insertFeedback(feedback)
        }
    }

    fun updatePrescriptionStatus(id: Long, status: String) {
        viewModelScope.launch {
            repository.updatePrescriptionStatus(id, status)
        }
    }

    fun addPrescription(
        regNumber: String,
        patientName: String,
        doctor: String,
        dept: String,
        encounter: String,
        medicine: String,
        dosage: String,
        freq: String,
        duration: String,
        instructions: String,
        status: String = "Dispensed / Ready"
    ) {
        viewModelScope.launch {
            val p = PharmacyPrescription(
                hospitalRegNumber = regNumber,
                patientName = patientName,
                doctorName = doctor,
                department = dept,
                encounterType = encounter,
                medicineName = medicine,
                dosage = dosage,
                frequency = freq,
                duration = duration,
                instructions = instructions,
                dispensationStatus = status,
                datePrescribed = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
            )
            repository.addPrescription(p)
        }
    }

    fun addBloodReport(
        regNumber: String,
        patientName: String,
        doctor: String,
        dept: String,
        encounter: String,
        category: String,
        title: String,
        summary: String,
        status: String = "Normal"
    ) {
        viewModelScope.launch {
            val report = BloodLabReport(
                hospitalRegNumber = regNumber,
                patientName = patientName,
                doctorName = doctor,
                department = dept,
                encounterType = encounter,
                testCategory = category,
                reportTitle = title,
                sampleDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date()),
                overallStatus = status,
                parametersSummary = summary
            )
            repository.addBloodReport(report)
        }
    }

    // Dashboard State
    private val _selectedMonthId = MutableStateFlow("all")
    val selectedMonthId: StateFlow<String> = _selectedMonthId.asStateFlow()

    private val _dashboardViewMode = MutableStateFlow("overview") // overview, los, consultant
    val dashboardViewMode: StateFlow<String> = _dashboardViewMode.asStateFlow()

    val currentMetrics: StateFlow<MonthlyMetrics> = _selectedMonthId.combine(MutableStateFlow(Unit)) { monthId, _ ->
        repository.monthlyDataMap[monthId] ?: repository.monthlyDataMap["all"]!!
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        repository.monthlyDataMap["all"]!!
    )

    val currentThroughputMetrics: StateFlow<ThroughputMetrics> = _selectedMonthId.combine(MutableStateFlow(Unit)) { monthId, _ ->
        repository.throughputDataMap[monthId] ?: repository.throughputDataMap["all"]!!
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        repository.throughputDataMap["all"]!!
    )

    val dischargeHourDistributions: List<DischargeHourDistribution> = repository.dischargeHourDistributions
    val departmentBenchmarks: List<DepartmentLosBenchmark> = repository.departmentBenchmarks
    val monthlyThroughputTrends: List<ThroughputTrendPoint> = repository.monthlyThroughputTrends

    val consultantStats: List<ConsultantStat> = repository.consultantStats
    val losDistributionLabels = repository.losDistributionLabels
    val losDistributionValues = repository.losDistributionValues
    val insights: List<LeanInsight> = repository.defaultInsights
    val vsmStages: List<VsmStage> = repository.vsmStages

    // Patient Flow State
    val allPatients: StateFlow<List<PatientRecord>> = repository.allPatients.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val activePatients: StateFlow<List<PatientRecord>> = repository.activePatients.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val consultantWorkloads: StateFlow<List<ConsultantWorkload>> = activePatients.combine(MutableStateFlow(Unit)) { activeList, _ ->
        val standardList = repository.standardConsultants
        val standardNames = standardList.map { it.first }.toSet()

        val standardWorkloads = standardList.map { (name, dept) ->
            val assigned = activeList.filter { it.consultantName.equals(name, ignoreCase = true) }
            ConsultantWorkload(
                consultantName = name,
                department = dept,
                maxAcuityCapacity = 25.0,
                assignedPatients = assigned
            )
        }

        val extraConsultants = activeList.map { it.consultantName }
            .distinct()
            .filter { name -> name !in standardNames && name.isNotBlank() }
            .map { name ->
                val assigned = activeList.filter { it.consultantName.equals(name, ignoreCase = true) }
                ConsultantWorkload(
                    consultantName = name,
                    department = assigned.firstOrNull()?.department ?: "General",
                    maxAcuityCapacity = 25.0,
                    assignedPatients = assigned
                )
            }

        (standardWorkloads + extraConsultants).sortedByDescending { it.capacityUtilizationPct }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _patientSearchQuery = MutableStateFlow("")
    val patientSearchQuery: StateFlow<String> = _patientSearchQuery.asStateFlow()

    private val _selectedFilterStatus = MutableStateFlow("All") // All, Inpatient, Discharged
    val selectedFilterStatus: StateFlow<String> = _selectedFilterStatus.asStateFlow()

    val filteredPatients: StateFlow<List<PatientRecord>> = combine(
        allPatients,
        _patientSearchQuery,
        _selectedFilterStatus
    ) { list, query, status ->
        list.filter { p ->
            val matchesQuery = query.isBlank() ||
                p.patientName.contains(query, ignoreCase = true) ||
                p.ipdNumber.contains(query, ignoreCase = true) ||
                p.consultantName.contains(query, ignoreCase = true) ||
                p.department.contains(query, ignoreCase = true)

            val matchesStatus = when (status) {
                "Inpatient" -> !p.isDischarged
                "Discharged" -> p.isDischarged
                else -> true
            }

            matchesQuery && matchesStatus
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Lean Waste Logs State
    val wasteLogs: StateFlow<List<LeanWasteLog>> = repository.wasteLogs.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    // 5S Audits State
    val fiveSAudits: StateFlow<List<FiveSAudit>> = repository.fiveSAudits.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val redTags: StateFlow<List<FiveSRedTagItem>> = repository.redTags.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val capas: StateFlow<List<FiveSCapaItem>> = repository.capas.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    // Kaizen Projects State
    val kaizenProjects: StateFlow<List<KaizenProject>> = repository.kaizenProjects.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    // Root Cause Analysis (5 Whys) State
    val rootCauseAnalyses: StateFlow<List<RootCauseAnalysis>> = repository.rootCauseAnalyses.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    // Lean Simulator Parameters
    val targetLosState = MutableStateFlow(2.8f) // Current is 3.29
    val bedCostPerDayState = MutableStateFlow(3500f) // INR
    val annualAdmissionsState = MutableStateFlow(1143f) // 381 in 4 months -> ~1143/year

    init {
        viewModelScope.launch {
            repository.initializeSeedDataIfEmpty()
        }
    }

    fun selectMonth(monthId: String) {
        _selectedMonthId.value = monthId
    }

    fun selectViewMode(viewMode: String) {
        _dashboardViewMode.value = viewMode
    }

    fun setPatientSearchQuery(query: String) {
        _patientSearchQuery.value = query
    }

    fun setFilterStatus(status: String) {
        _selectedFilterStatus.value = status
    }

    fun addPatient(
        name: String,
        ipdNumber: String,
        age: Int,
        gender: String,
        consultant: String,
        department: String,
        admissionDate: String,
        notes: String,
        acuityLevel: Int = 1,
        flowStage: String = "Admission"
    ) {
        viewModelScope.launch {
            val patient = PatientRecord(
                patientName = name,
                ipdNumber = ipdNumber,
                age = age,
                gender = gender,
                consultantName = consultant,
                department = department,
                admissionDate = admissionDate,
                losDays = 0,
                isDischarged = false,
                clinicalPathwayFollowed = true,
                notes = notes,
                acuityLevel = acuityLevel,
                flowStage = flowStage
            )
            repository.addPatient(patient)
        }
    }

    fun reassignPatient(patientId: Long, newConsultant: String) {
        viewModelScope.launch {
            repository.reassignPatient(patientId, newConsultant)
        }
    }

    fun updatePatientAcuity(patientId: Long, newAcuity: Int) {
        viewModelScope.launch {
            repository.updatePatientAcuity(patientId, newAcuity)
        }
    }

    private val _kanbanWipLimits = MutableStateFlow(
        mapOf(
            "Admission" to 5,
            "Diagnostics" to 6,
            "Treatment" to 10,
            "Discharge" to 4
        )
    )
    val kanbanWipLimits: StateFlow<Map<String, Int>> = _kanbanWipLimits.asStateFlow()

    fun updateWipLimit(stageKey: String, newLimit: Int) {
        _kanbanWipLimits.value = _kanbanWipLimits.value.toMutableMap().apply {
            this[stageKey] = newLimit.coerceAtLeast(1)
        }
    }

    fun dischargePatient(
        patient: PatientRecord,
        dischargeDate: String,
        losDays: Int,
        delayReason: String?,
        notes: String
    ) {
        viewModelScope.launch {
            val updated = patient.copy(
                isDischarged = true,
                dischargeDate = dischargeDate,
                losDays = losDays,
                delayReason = delayReason,
                clinicalPathwayFollowed = delayReason.isNullOrBlank(),
                notes = if (notes.isNotBlank()) "${patient.notes}\n$notes" else patient.notes
            )
            repository.updatePatient(updated)
        }
    }

    fun addWasteObservation(
        category: String,
        department: String,
        description: String,
        minutesLost: Int,
        severity: String,
        rootCause: String
    ) {
        viewModelScope.launch {
            val log = LeanWasteLog(
                wasteCategory = category,
                department = department,
                description = description,
                estimatedMinutesLost = minutesLost,
                severity = severity,
                rootCause = rootCause,
                timestamp = System.currentTimeMillis()
            )
            repository.addWasteLog(log)
        }
    }

    fun deleteWasteLog(log: LeanWasteLog) {
        viewModelScope.launch {
            repository.deleteWasteLog(log)
        }
    }

    fun addFiveSAudit(
        department: String,
        sort: Int,
        set: Int,
        shine: Int,
        standardize: Int,
        sustain: Int,
        auditorName: String,
        remarks: String
    ) {
        viewModelScope.launch {
            val audit = FiveSAudit(
                department = department,
                sortScore = sort,
                setInOrderScore = set,
                shineScore = shine,
                standardizeScore = standardize,
                sustainScore = sustain,
                auditorName = auditorName,
                auditDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date()),
                remarks = remarks
            )
            repository.addFiveSAudit(audit)
        }
    }

    fun deleteFiveSAudit(audit: FiveSAudit) {
        viewModelScope.launch {
            repository.deleteFiveSAudit(audit)
        }
    }

    fun addRedTag(
        dept: String,
        itemName: String,
        category: String,
        reason: String,
        actionRequired: String,
        taggedBy: String,
        notes: String = ""
    ) {
        viewModelScope.launch {
            val tagNumber = "RT-2026-${(100..999).random()}"
            val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
            val item = FiveSRedTagItem(
                department = dept,
                tagNumber = tagNumber,
                itemName = itemName,
                category = category,
                reason = reason,
                actionRequired = actionRequired,
                taggedBy = taggedBy.ifBlank { "Gunjan Prakash (Lean Intern)" },
                dateTagged = today,
                status = "Active Tag",
                resolutionNotes = notes
            )
            repository.addRedTag(item)
        }
    }

    fun updateRedTagStatus(item: FiveSRedTagItem, newStatus: String, notes: String = "") {
        viewModelScope.launch {
            repository.updateRedTag(item.copy(status = newStatus, resolutionNotes = notes.ifBlank { item.resolutionNotes }))
        }
    }

    fun addCapa(
        dept: String,
        pillar: String,
        finding: String,
        action: String,
        owner: String,
        targetDate: String,
        severity: String
    ) {
        viewModelScope.launch {
            val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
            val capa = FiveSCapaItem(
                department = dept,
                pillar = pillar,
                finding = finding,
                correctiveAction = action,
                responsiblePerson = owner,
                targetDate = targetDate,
                status = "Open",
                severity = severity,
                dateCreated = today
            )
            repository.addCapa(capa)
        }
    }

    fun updateCapaStatus(item: FiveSCapaItem, newStatus: String) {
        viewModelScope.launch {
            repository.updateCapa(item.copy(status = newStatus))
        }
    }

    fun addKaizenProject(
        title: String,
        department: String,
        problem: String,
        solution: String,
        targetMetric: String,
        leadPerson: String,
        bedDaysSaved: Int
    ) {
        viewModelScope.launch {
            val project = KaizenProject(
                title = title,
                department = department,
                problemStatement = problem,
                proposedCountermeasure = solution,
                targetMetric = targetMetric,
                status = "Planned",
                leadPerson = leadPerson,
                estimatedBedDaysSavedYearly = bedDaysSaved,
                dateInitiated = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
            )
            repository.addKaizen(project)
        }
    }

    fun updateKaizenStatus(project: KaizenProject, newStatus: String) {
        viewModelScope.launch {
            repository.updateKaizen(project.copy(status = newStatus))
        }
    }

    fun addRootCauseAnalysis(analysis: RootCauseAnalysis) {
        viewModelScope.launch {
            repository.addRootCauseAnalysis(analysis)
        }
    }

    fun updateRootCauseAnalysis(analysis: RootCauseAnalysis) {
        viewModelScope.launch {
            repository.updateRootCauseAnalysis(analysis)
        }
    }

    fun deleteRootCauseAnalysis(analysis: RootCauseAnalysis) {
        viewModelScope.launch {
            repository.deleteRootCauseAnalysis(analysis)
        }
    }

    fun loginOrRegister(
        role: String,
        hospitalCode: String,
        identifier: String,
        fullName: String = "",
        hospitalRegNumber: String = "",
        department: String = ""
    ) {
        viewModelScope.launch {
            repository.loginOrRegister(
                role = role,
                hospitalCode = hospitalCode,
                identifier = identifier,
                fullName = fullName,
                hospitalRegNumber = hospitalRegNumber,
                department = department
            )
        }
    }

    fun registerPatientWithFamily(
        hospitalCode: String,
        identifier: String,
        fullName: String,
        hospitalRegNumber: String,
        familyMembers: List<FamilyMember>
    ) {
        viewModelScope.launch {
            repository.registerPatientWithFamily(
                hospitalCode = hospitalCode,
                identifier = identifier,
                fullName = fullName,
                hospitalRegNumber = hospitalRegNumber,
                familyMembers = familyMembers
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logoutUser()
        }
    }

    fun addFamilyMember(
        name: String,
        relation: String,
        age: Int,
        gender: String,
        regNumber: String,
        bloodGroup: String = "O+"
    ) {
        val user = activeUserSession.value ?: return
        viewModelScope.launch {
            val member = FamilyMember(
                primaryUserIdentifier = user.identifier,
                fullName = name,
                relation = relation,
                age = age,
                gender = gender,
                hospitalRegNumber = regNumber.ifBlank { "MRN-${(1000..9999).random()}" },
                bloodGroup = bloodGroup
            )
            repository.addFamilyMember(member)
        }
    }

    fun deleteFamilyMember(member: FamilyMember) {
        viewModelScope.launch {
            repository.deleteFamilyMember(member)
        }
    }

    fun saveHmsConfig(config: HmsEhrConfig) {
        viewModelScope.launch {
            repository.saveHmsConfig(config)
        }
    }

    fun triggerHmsSync(hospitalCode: String = activeUserSession.value?.hospitalCode ?: "SGH-DEL-01") {
        viewModelScope.launch {
            isHmsSyncing.value = true
            hmsSyncMessage.value = "Initiating ABDM FHIR auto-sync with $hospitalCode..."
            val result = repository.triggerHmsSync(hospitalCode)
            isHmsSyncing.value = false
            hmsSyncMessage.value = result.second
        }
    }

    fun bookOpdToken(
        patientName: String,
        doctorName: String,
        department: String,
        roomNumber: String
    ) {
        val clinic = opdClinics.firstOrNull { it.doctorName == doctorName }
        val nextToken = (clinic?.totalTokensIssued ?: 20) + 1
        val wait = (nextToken - (clinic?.currentServingToken ?: 1)) * (clinic?.avgConsultationMinutes ?: 8)
        val nowTime = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault()).format(java.util.Date())
        val newToken = PatientOpdToken(
            id = "token_${System.currentTimeMillis()}",
            tokenNumber = nextToken,
            patientName = patientName,
            doctorName = doctorName,
            department = department,
            roomNumber = roomNumber,
            currentServingToken = clinic?.currentServingToken ?: 1,
            estWaitMinutes = wait.coerceAtLeast(5),
            bookingTime = nowTime,
            status = "Waiting"
        )
        _myOpdTokens.value = listOf(newToken) + _myOpdTokens.value
    }
}
