package com.example

import com.example.data.model.AppUser
import com.example.data.model.FamilyMember
import com.example.data.model.HmsEhrConfig
import com.example.data.model.HospitalDirectory
import com.example.data.model.OpdQueueItem
import com.example.data.model.UserRole
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthAndHmsSyncTest {

    @Test
    fun testHospitalDirectoryCodes() {
        val sghName = HospitalDirectory.getHospitalName("SGH-RANCHI")
        assertTrue(sghName.contains("Synergy Global Hospital"))

        val aiimsName = HospitalDirectory.getHospitalName("AIIMS-ND")
        assertTrue(aiimsName.contains("All India Institute of Medical Sciences"))

        val customName = HospitalDirectory.getHospitalName("CITY-GEN")
        assertEquals("Hospital Node (CITY-GEN)", customName)
    }

    @Test
    fun testUserRoleCreationAndHospitalCode() {
        val patientUser = AppUser(
            id = 1,
            role = "PATIENT",
            hospitalCode = "SGH-RANCHI",
            hospitalName = "Synergy Global Hospital",
            identifier = "gunjanprakash2670@gmail.com",
            fullName = "Gunjan Prakash",
            hospitalRegNumber = "SGH-2026-0288",
            isLoggedIn = true
        )
        assertEquals("PATIENT", patientUser.role)
        assertEquals("SGH-RANCHI", patientUser.hospitalCode)
        assertEquals("gunjanprakash2670@gmail.com", patientUser.identifier)
        assertEquals("SGH-2026-0288", patientUser.hospitalRegNumber)
    }

    @Test
    fun testFamilyMemberLinking() {
        val parentIdentifier = "gunjanprakash2670@gmail.com"
        val members = listOf(
            FamilyMember(
                id = 1,
                primaryUserIdentifier = parentIdentifier,
                fullName = "Gunjan Prakash",
                relation = "Self",
                age = 24,
                gender = "Male",
                hospitalRegNumber = "SGH-2026-0288"
            ),
            FamilyMember(
                id = 2,
                primaryUserIdentifier = parentIdentifier,
                fullName = "Bimla Devi",
                relation = "Mother",
                age = 72,
                gender = "Female",
                hospitalRegNumber = "SGH-2026-0294"
            )
        )

        assertEquals(2, members.size)
        members.forEach {
            assertEquals(parentIdentifier, it.primaryUserIdentifier)
            assertTrue(it.hospitalRegNumber.startsWith("SGH-"))
        }
    }

    @Test
    fun testOpdWaitingTimeCalculation() {
        val clinic = OpdQueueItem(
            department = "General Medicine",
            doctorName = "Dr. Rahul Sinha",
            roomNumber = "Room 102",
            currentServingToken = 14,
            totalTokensIssued = 32,
            avgConsultationMinutes = 8,
            estimatedWaitMinutes = 48,
            status = "Peak Hour"
        )

        val myTokenNumber = 20
        val remainingPatients = myTokenNumber - clinic.currentServingToken
        val calculatedWait = remainingPatients * clinic.avgConsultationMinutes

        assertEquals(6, remainingPatients)
        assertEquals(48, calculatedWait)
    }

    @Test
    fun testHmsEhrAutoSyncConfig() {
        val config = HmsEhrConfig(
            hospitalCode = "SGH-RANCHI",
            hospitalName = "Synergy Global Hospital",
            hmsSystemType = "ABDM FHIR v2 / OpenEMR Enterprise",
            endpointUrl = "https://hms.synergyhospital.org/api/v2/ehr-sync",
            apiKeyMasked = "sgh_live_sec_84712****312",
            autoSyncIntervalMinutes = 5,
            isAutoSyncEnabled = true,
            totalRecordsSynced = 1420
        )

        assertTrue(config.isAutoSyncEnabled)
        assertEquals(5, config.autoSyncIntervalMinutes)
        assertEquals(1420, config.totalRecordsSynced)
        assertTrue(config.hmsSystemType.contains("FHIR"))
    }
}
