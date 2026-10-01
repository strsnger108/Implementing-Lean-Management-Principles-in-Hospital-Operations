package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.ConsultantStat
import com.example.data.model.FiveSAudit
import com.example.data.model.KaizenProject
import com.example.data.model.LeanInsight
import com.example.data.model.LeanWasteLog
import com.example.data.model.MonthlyMetrics
import com.example.data.model.PatientRecord
import com.example.data.model.VsmStage
import com.example.data.repository.HospitalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HospitalViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    val repository = HospitalRepository(db)

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

    // Kaizen Projects State
    val kaizenProjects: StateFlow<List<KaizenProject>> = repository.kaizenProjects.stateIn(
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
        notes: String
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
                notes = notes
            )
            repository.addPatient(patient)
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
}
