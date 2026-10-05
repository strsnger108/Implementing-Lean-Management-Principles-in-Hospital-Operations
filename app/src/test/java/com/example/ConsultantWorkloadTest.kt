package com.example

import com.example.data.model.AcuityDefinitions
import com.example.data.model.ConsultantWorkload
import com.example.data.model.PatientRecord
import com.example.data.model.WorkloadStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ConsultantWorkloadTest {

    @Test
    fun testAcuityWeights() {
        assertEquals(1.0, AcuityDefinitions.getWeight(1), 0.001)
        assertEquals(1.5, AcuityDefinitions.getWeight(2), 0.001)
        assertEquals(2.5, AcuityDefinitions.getWeight(3), 0.001)
        assertEquals(4.0, AcuityDefinitions.getWeight(4), 0.001)
    }

    @Test
    fun testConsultantCapacityCalculationAndStatus() {
        // Create patients with varied acuity
        val patientL1 = PatientRecord(
            id = 1,
            patientName = "Patient A",
            ipdNumber = "SGH-001",
            age = 30,
            gender = "M",
            consultantName = "Dr. Rahul Sinha",
            department = "General Medicine",
            admissionDate = "2026-02-20",
            acuityLevel = 1 // 1.0x
        )
        val patientL3 = PatientRecord(
            id = 2,
            patientName = "Patient B",
            ipdNumber = "SGH-002",
            age = 65,
            gender = "F",
            consultantName = "Dr. Rahul Sinha",
            department = "General Medicine",
            admissionDate = "2026-02-21",
            acuityLevel = 3 // 2.5x
        )
        val patientL4 = PatientRecord(
            id = 3,
            patientName = "Patient C",
            ipdNumber = "SGH-003",
            age = 70,
            gender = "M",
            consultantName = "Dr. Rahul Sinha",
            department = "General Medicine",
            admissionDate = "2026-02-22",
            acuityLevel = 4 // 4.0x
        )

        val workload = ConsultantWorkload(
            consultantName = "Dr. Rahul Sinha",
            department = "General Medicine",
            maxAcuityCapacity = 25.0,
            assignedPatients = listOf(patientL1, patientL3, patientL4)
        )

        // Total points = 1.0 + 2.5 + 4.0 = 7.5
        assertEquals(7.5, workload.totalAcuityPoints, 0.001)
        // Utilization = (7.5 / 25.0) * 100 = 30% -> UNDERUTILIZED (<40%)
        assertEquals(30.0, workload.capacityUtilizationPct, 0.001)
        assertEquals(WorkloadStatus.UNDERUTILIZED, workload.workloadStatus)
    }

    @Test
    fun testOverloadedConsultantAndRebalancing() {
        // Create 11 patients of Level 3 (2.5x each) = 27.5 points on a 25.0 ceiling
        val heavyPatients = (1..11).map { index ->
            PatientRecord(
                id = index.toLong(),
                patientName = "High Acuity Patient $index",
                ipdNumber = "SGH-10$index",
                age = 55,
                gender = "M",
                consultantName = "Dr. Rahul Sinha",
                department = "General Medicine",
                admissionDate = "2026-02-20",
                acuityLevel = 3 // 2.5x
            )
        }

        val overloadedWorkload = ConsultantWorkload(
            consultantName = "Dr. Rahul Sinha",
            department = "General Medicine",
            maxAcuityCapacity = 25.0,
            assignedPatients = heavyPatients
        )

        // 11 * 2.5 = 27.5 acuity points
        assertEquals(27.5, overloadedWorkload.totalAcuityPoints, 0.001)
        // Utilization = (27.5 / 25.0) * 100 = 110%
        assertEquals(110.0, overloadedWorkload.capacityUtilizationPct, 0.001)
        assertEquals(WorkloadStatus.OVERLOADED, overloadedWorkload.workloadStatus)

        // Simulate Heijunka Reassignment: Transfer 3 patients to Dr. Vivek Goswami
        val remainingPatients = heavyPatients.drop(3) // 8 patients left = 20.0 points
        val leveledWorkload = overloadedWorkload.copy(assignedPatients = remainingPatients)

        assertEquals(20.0, leveledWorkload.totalAcuityPoints, 0.001)
        assertEquals(80.0, leveledWorkload.capacityUtilizationPct, 0.001)
        assertEquals(WorkloadStatus.NEAR_CAPACITY, leveledWorkload.workloadStatus)
    }
}
