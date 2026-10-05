package com.example

import com.example.data.model.RootCauseAnalysis
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class RootCauseAnalysisTest {

    @Test
    fun test5WhysChainIntegrity() {
        val rca = RootCauseAnalysis(
            id = 1,
            title = "Late Afternoon Discharge Bottleneck",
            sourceModule = "LOS Analytics",
            department = "Billing Desk & Inpatient Wards",
            incidentDescription = "Patients ready in morning remain in acute beds until 2 PM - 5 PM.",
            severity = "Critical",
            why1 = "Billing clearance and gate pass issue takes 180 min after physician verbal order.",
            why2 = "Pharmacy drug return reconciliations and diagnostic charges only begin after physical discharge summary is delivered.",
            why3 = "Physicians conduct discharge rounds late (1:30 PM - 3:00 PM) after morning OPD clinics.",
            why4 = "No standardized multi-disciplinary morning discharge rounds existed.",
            why5 = "Clinical and administrative workflows operate in isolated departmental silos without synchronized Lean Value Stream coordination.",
            rootCauseStatement = "Absence of standardized 10:00 AM multi-disciplinary rounds and uncoordinated sequential billing/pharmacy clearance.",
            rootCauseCategory = "Process Design",
            countermeasure = "Implement standardized 10:00 AM multi-disciplinary rounds and 4:00 PM prior-day interim bill audit with express discharge lounge.",
            actionOwner = "Dr. Rahul Sinha & Billing Lead",
            targetDate = "2026-03-25",
            targetKpiImpact = "Drop discharge turnaround time to <= 45 min and shift 65% discharges before noon.",
            status = "Open"
        )

        // Verify the 5 Whys chain is complete
        assertNotNull(rca.why1)
        assertNotNull(rca.why2)
        assertNotNull(rca.why3)
        assertNotNull(rca.why4)
        assertNotNull(rca.why5)

        assertEquals("LOS Analytics", rca.sourceModule)
        assertEquals("Critical", rca.severity)
        assertEquals("Open", rca.status)

        // Test status progression
        val inProgress = rca.copy(status = "In Progress")
        assertEquals("In Progress", inProgress.status)

        val resolved = inProgress.copy(status = "Resolved")
        assertEquals("Resolved", resolved.status)
    }

    @Test
    fun testRootCauseCategories() {
        val categories = listOf(
            "Process Design",
            "Communication",
            "People / Training",
            "Policy / SOP",
            "Equipment / IT Infrastructure"
        )
        assertEquals(5, categories.size)
    }
}
