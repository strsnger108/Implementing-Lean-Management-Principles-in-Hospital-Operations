package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class UserRole {
    PATIENT, STAFF, ADMIN
}

@Entity(tableName = "app_users")
data class AppUser(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val role: String, // "PATIENT", "STAFF", "ADMIN"
    val hospitalCode: String, // e.g. "SGH-RANCHI", "AIIMS-ND"
    val hospitalName: String,
    val identifier: String, // Email or Phone Number
    val fullName: String,
    val hospitalRegNumber: String = "", // MRN / UHID / Staff ID
    val department: String = "", // for staff/admin
    val passwordHash: String = "",
    val isLoggedIn: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "family_members")
data class FamilyMember(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val primaryUserIdentifier: String, // Parent phone or gmail
    val fullName: String,
    val relation: String, // "Self", "Spouse", "Child", "Father", "Mother", "Other"
    val age: Int,
    val gender: String,
    val hospitalRegNumber: String, // UHID / MRN e.g. "SGH-2026-0288"
    val bloodGroup: String = "O+",
    val emergencyContact: String = ""
)

@Entity(tableName = "hms_ehr_config")
data class HmsEhrConfig(
    @PrimaryKey val hospitalCode: String,
    val hospitalName: String,
    val hmsSystemType: String = "OpenEMR / ABDM FHIR",
    val endpointUrl: String = "https://hms.synergyhospital.org/api/v2/ehr-sync",
    val apiKeyMasked: String = "hms_live_sec_984712****312",
    val autoSyncIntervalMinutes: Int = 5,
    val isAutoSyncEnabled: Boolean = true,
    val lastSyncTimestamp: Long = System.currentTimeMillis() - 120_000,
    val lastSyncStatus: String = "Success (All 381 Inpatient & OPD records in sync)",
    val totalRecordsSynced: Int = 1420
)

data class OpdQueueItem(
    val department: String,
    val doctorName: String,
    val roomNumber: String,
    val currentServingToken: Int,
    val totalTokensIssued: Int,
    val avgConsultationMinutes: Int,
    val estimatedWaitMinutes: Int,
    val status: String // "On Schedule", "Slight Delay", "Peak Hour"
)

data class PatientOpdToken(
    val id: String,
    val tokenNumber: Int,
    val patientName: String,
    val doctorName: String,
    val department: String,
    val roomNumber: String,
    val currentServingToken: Int,
    val estWaitMinutes: Int,
    val bookingTime: String,
    val status: String // "Waiting", "In Consultation", "Completed"
)

object HospitalDirectory {
    val hospitals = listOf(
        "SGH-RANCHI" to "Synergy Global Hospital (Main Campus, Ranchi)",
        "AIIMS-ND" to "All India Institute of Medical Sciences (New Delhi)",
        "APOLLO-DELHI" to "Apollo Multi-Speciality Hospital (New Delhi)",
        "MAX-HEALTH" to "Max Super Speciality Hospital (Saket)",
        "FORTIS-BLR" to "Fortis Hospital (Bannerghatta, Bangalore)"
    )

    fun getHospitalName(code: String): String {
        return hospitals.firstOrNull { it.first.equals(code.trim(), ignoreCase = true) }?.second
            ?: "Hospital Node (${code.uppercase()})"
    }
}
