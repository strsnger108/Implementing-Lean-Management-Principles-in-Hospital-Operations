package com.example

import com.example.data.model.PatientFlowStage
import com.example.data.model.PatientRecord
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PatientKanbanTest {

    @Test
    fun testKanbanStagesEnum() {
        val stages = PatientFlowStage.entries
        assertEquals(4, stages.size)
        assertEquals("Admission", stages[0].stageKey)
        assertEquals("Diagnostics", stages[1].stageKey)
        assertEquals("Treatment", stages[2].stageKey)
        assertEquals("Discharge", stages[3].stageKey)

        assertEquals(5, PatientFlowStage.ADMISSION.defaultWipLimit)
        assertEquals(6, PatientFlowStage.DIAGNOSTICS.defaultWipLimit)
        assertEquals(10, PatientFlowStage.TREATMENT.defaultWipLimit)
        assertEquals(4, PatientFlowStage.DISCHARGE.defaultWipLimit)
    }

    @Test
    fun testPatientStageTransition() {
        val patient = PatientRecord(
            id = 10,
            patientName = "Rahul Verma",
            ipdNumber = "SGH-2026-901",
            age = 45,
            gender = "Male",
            consultantName = "Dr. Rahul Sinha",
            department = "General Medicine",
            admissionDate = "2026-03-01",
            flowStage = "Admission"
        )

        assertEquals("Admission", patient.flowStage)

        // Advance to Diagnostics
        val inDiagnostics = patient.copy(flowStage = "Diagnostics")
        assertEquals("Diagnostics", inDiagnostics.flowStage)

        // Advance to Treatment
        val inTreatment = inDiagnostics.copy(flowStage = "Treatment")
        assertEquals("Treatment", inTreatment.flowStage)

        // Advance to Discharge
        val inDischarge = inTreatment.copy(flowStage = "Discharge")
        assertEquals("Discharge", inDischarge.flowStage)
    }

    @Test
    fun testWipLimitBottleneckDetection() {
        val wipLimits = mapOf(
            "Admission" to 5,
            "Diagnostics" to 6,
            "Treatment" to 10,
            "Discharge" to 4
        )

        // Simulate 7 patients in Discharge queue (exceeds WIP limit of 4)
        val dischargePatients = (1..7).map {
            PatientRecord(
                id = it.toLong(),
                patientName = "Patient $it",
                ipdNumber = "SGH-00$it",
                age = 50,
                gender = "M",
                consultantName = "Dr. Rahul Sinha",
                department = "General Medicine",
                admissionDate = "2026-02-25",
                flowStage = "Discharge"
            )
        }

        val dischargeCount = dischargePatients.size
        val dischargeLimit = wipLimits["Discharge"] ?: 4

        val isBottleneck = dischargeCount > dischargeLimit
        val overflowPatients = dischargeCount - dischargeLimit
        val pressureRatio = dischargeCount.toDouble() / dischargeLimit.toDouble()

        assertTrue(isBottleneck)
        assertEquals(3, overflowPatients)
        assertEquals(1.75, pressureRatio, 0.01) // 175% WIP pressure
    }
}
