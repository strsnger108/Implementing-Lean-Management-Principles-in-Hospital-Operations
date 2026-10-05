package com.example

import com.example.data.model.BloodLabReport
import com.example.data.model.PharmacyPrescription
import com.example.data.model.SampleLabDefinitions
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PharmacyAndBloodReportSyncTest {

    @Test
    fun testPharmacyPrescriptionModelAndDispensation() {
        val opdPrescription = PharmacyPrescription(
            id = 1,
            hospitalRegNumber = "SGH-2026-0288",
            patientName = "Gunjan Prakash",
            doctorName = "Dr. Rahul Sinha",
            department = "General Medicine",
            encounterType = "OPD Consultation",
            medicineName = "Tab Telmisartan 40mg",
            dosage = "40 mg",
            frequency = "1-0-0 (Morning)",
            duration = "30 Days",
            instructions = "Take after breakfast",
            dispensationStatus = "Dispensed / Ready",
            datePrescribed = "2026-02-28"
        )

        assertEquals("SGH-2026-0288", opdPrescription.hospitalRegNumber)
        assertEquals("OPD Consultation", opdPrescription.encounterType)
        assertEquals("Dispensed / Ready", opdPrescription.dispensationStatus)

        // Simulate patient collecting the medication
        val collected = opdPrescription.copy(dispensationStatus = "Collected")
        assertEquals("Collected", collected.dispensationStatus)
    }

    @Test
    fun testPostTreatmentDischargePrescription() {
        val dischargeMed = PharmacyPrescription(
            id = 2,
            hospitalRegNumber = "SGH-2026-0288",
            patientName = "Gunjan Prakash",
            doctorName = "Dr. Rahul Sinha",
            department = "General Medicine",
            encounterType = "Discharge Medication",
            medicineName = "Tab Augmentin 625mg",
            dosage = "625 mg",
            frequency = "1-0-1 (Twice Daily)",
            duration = "5 Days",
            instructions = "Complete entire course",
            dispensationStatus = "Dispensed / Ready",
            datePrescribed = "2026-02-27"
        )

        assertEquals("Discharge Medication", dischargeMed.encounterType)
        assertTrue(dischargeMed.medicineName.contains("Augmentin"))
    }

    @Test
    fun testBloodLabReportAndAnalyteParameters() {
        val normalCbc = BloodLabReport(
            id = 10,
            hospitalRegNumber = "SGH-2026-0288",
            patientName = "Gunjan Prakash",
            doctorName = "Dr. Rahul Sinha",
            department = "General Medicine",
            encounterType = "OPD Routine Workup",
            testCategory = "Complete Blood Count (CBC)",
            reportTitle = "Complete Hemogram",
            sampleDate = "2026-02-28",
            overallStatus = "Normal"
        )

        assertEquals("Normal", normalCbc.overallStatus)
        val normalParams = SampleLabDefinitions.getParametersForCategory(normalCbc.testCategory, isAbnormal = false)
        assertTrue(normalParams.isNotEmpty())
        val hbParam = normalParams.first { it.name.startsWith("Hemoglobin") }
        assertEquals("NORMAL", hbParam.flag)
        assertEquals("13.8", hbParam.resultValue)

        // Test abnormal post-treatment KFT report for renal patient
        val abnormalKft = BloodLabReport(
            id = 11,
            hospitalRegNumber = "SGH-2026-0294",
            patientName = "Bimla Devi",
            doctorName = "Dr. Rahul Sinha",
            department = "General Medicine",
            encounterType = "Post-Treatment Evaluation",
            testCategory = "Kidney Function Test (KFT)",
            reportTitle = "Urgent Renal Function",
            sampleDate = "2026-02-28",
            overallStatus = "Attention Required"
        )

        assertEquals("Attention Required", abnormalKft.overallStatus)
        val abnormalParams = SampleLabDefinitions.getParametersForCategory(abnormalKft.testCategory, isAbnormal = true)
        val creatinineParam = abnormalParams.first { it.name.contains("Creatinine") }
        assertEquals("HIGH", creatinineParam.flag)
        assertEquals("2.6", creatinineParam.resultValue)
    }

    @Test
    fun testFamilyMemberSegregation() {
        val allPrescriptions = listOf(
            PharmacyPrescription(
                id = 1,
                hospitalRegNumber = "SGH-2026-0288",
                patientName = "Gunjan Prakash",
                doctorName = "Dr. Rahul Sinha",
                department = "General Medicine",
                encounterType = "OPD Consultation",
                medicineName = "Tab Telmisartan 40mg",
                dosage = "40 mg",
                frequency = "1-0-0",
                duration = "30 Days",
                instructions = "",
                dispensationStatus = "Collected",
                datePrescribed = "2026-02-28"
            ),
            PharmacyPrescription(
                id = 2,
                hospitalRegNumber = "SGH-2026-0294",
                patientName = "Bimla Devi",
                doctorName = "Dr. Rahul Sinha",
                department = "General Medicine",
                encounterType = "Inpatient Treatment",
                medicineName = "Tab Torsemide 10mg",
                dosage = "10 mg",
                frequency = "1-0-0",
                duration = "30 Days",
                instructions = "",
                dispensationStatus = "Dispensed / Ready",
                datePrescribed = "2026-02-27"
            )
        )

        val gunjanMeds = allPrescriptions.filter { it.hospitalRegNumber == "SGH-2026-0288" }
        val bimlaMeds = allPrescriptions.filter { it.hospitalRegNumber == "SGH-2026-0294" }

        assertEquals(1, gunjanMeds.size)
        assertEquals("Tab Telmisartan 40mg", gunjanMeds.first().medicineName)

        assertEquals(1, bimlaMeds.size)
        assertEquals("Tab Torsemide 10mg", bimlaMeds.first().medicineName)
    }

    @Test
    fun testUnifiedEhrDocumentVaultAggregationAndFiltering() {
        val labReport = BloodLabReport(
            id = 101,
            hospitalRegNumber = "SGH-2026-0288",
            patientName = "Gunjan Prakash",
            doctorName = "Dr. S. K. Mitra",
            department = "Pathology",
            encounterType = "OPD Routine Workup",
            testCategory = "Complete Blood Count (CBC)",
            reportTitle = "Complete Hemogram",
            sampleDate = "2026-02-28",
            overallStatus = "Normal",
            labBarcode = "LAB-98421"
        )

        val prescription = PharmacyPrescription(
            id = 202,
            hospitalRegNumber = "SGH-2026-0288",
            patientName = "Gunjan Prakash",
            doctorName = "Dr. Rahul Sinha",
            department = "General Medicine",
            encounterType = "Post-Treatment Evaluation",
            medicineName = "Tab Telmisartan 40mg",
            dosage = "40 mg",
            frequency = "1-0-0",
            duration = "30 Days",
            instructions = "Take after breakfast",
            dispensationStatus = "Dispensed / Ready",
            datePrescribed = "2026-02-27",
            rxNumber = "RX-2026-104"
        )

        val labDoc = com.example.ui.components.UnifiedEhrDocument.LabDocument(labReport)
        val rxDoc = com.example.ui.components.UnifiedEhrDocument.RxDocument(prescription)

        val vaultList = listOf(labDoc, rxDoc)
        assertEquals(2, vaultList.size)

        // Test filtering by type
        val labOnly = vaultList.filterIsInstance<com.example.ui.components.UnifiedEhrDocument.LabDocument>()
        assertEquals(1, labOnly.size)
        assertEquals("Complete Hemogram (Complete Blood Count (CBC))", labOnly.first().title)

        val rxOnly = vaultList.filterIsInstance<com.example.ui.components.UnifiedEhrDocument.RxDocument>()
        assertEquals(1, rxOnly.size)
        assertEquals("Prescription: Tab Telmisartan 40mg (40 mg)", rxOnly.first().title)

        // Test searching across fields
        val searchDoctor = vaultList.filter { it.doctorName.contains("Mitra", ignoreCase = true) }
        assertEquals(1, searchDoctor.size)

        val searchEncounter = vaultList.filter { it.encounterType.contains("Post-Treatment", ignoreCase = true) }
        assertEquals(1, searchEncounter.size)
        assertEquals("RX-2026-104", (searchEncounter.first() as com.example.ui.components.UnifiedEhrDocument.RxDocument).prescription.rxNumber)
    }

    @Test
    fun testClinicalDocumentViewerDataVariants() {
        val lab = BloodLabReport(
            id = 1,
            hospitalRegNumber = "SGH-2026-0288",
            patientName = "Gunjan Prakash",
            doctorName = "Dr. S. K. Mitra",
            department = "Pathology",
            encounterType = "OPD Routine Workup",
            testCategory = "Complete Blood Count (CBC)",
            reportTitle = "Complete Hemogram",
            sampleDate = "2026-02-28",
            overallStatus = "Normal"
        )

        val rx = PharmacyPrescription(
            id = 2,
            hospitalRegNumber = "SGH-2026-0288",
            patientName = "Gunjan Prakash",
            doctorName = "Dr. Rahul Sinha",
            department = "General Medicine",
            encounterType = "OPD Consultation",
            medicineName = "Tab Telmisartan 40mg",
            dosage = "40 mg",
            frequency = "1-0-0",
            duration = "30 Days",
            instructions = "",
            dispensationStatus = "Dispensed / Ready",
            datePrescribed = "2026-02-28"
        )

        val docDataLab: com.example.ui.components.ClinicalDocumentData = com.example.ui.components.ClinicalDocumentData.LabReport(lab)
        val docDataRx: com.example.ui.components.ClinicalDocumentData = com.example.ui.components.ClinicalDocumentData.Prescription(rx)

        assertTrue(docDataLab is com.example.ui.components.ClinicalDocumentData.LabReport)
        assertTrue(docDataRx is com.example.ui.components.ClinicalDocumentData.Prescription)
    }

    @Test
    fun testPatientBillingAndLedgerCalculations() {
        val opdBill = com.example.data.model.PatientBill(
            id = 1,
            hospitalRegNumber = "SGH-2026-0288",
            patientName = "Gunjan Prakash",
            billNumber = "INV-2026-0891",
            billType = "OPD Consultation",
            billDate = "2026-02-28",
            totalAmount = 600.0,
            paidAmount = 600.0,
            dueAmount = 0.0,
            paymentStatus = "Paid",
            paymentMode = "UPI",
            itemizedSummary = "OPD Consultation with Dr. Rahul Sinha"
        )

        val labBill = com.example.data.model.PatientBill(
            id = 2,
            hospitalRegNumber = "SGH-2026-0288",
            patientName = "Gunjan Prakash",
            billNumber = "INV-2026-0895",
            billType = "Lab & Diagnostics",
            billDate = "2026-02-28",
            totalAmount = 1450.0,
            paidAmount = 1450.0,
            dueAmount = 0.0,
            paymentStatus = "Paid",
            paymentMode = "UPI",
            itemizedSummary = "Complete Hemogram and Fasting Blood Sugar"
        )

        val bills = listOf(opdBill, labBill)
        assertEquals(2050.0, bills.sumOf { it.totalAmount }, 0.01)
        assertEquals(2050.0, bills.sumOf { it.paidAmount }, 0.01)
        assertEquals(0.0, bills.sumOf { it.dueAmount }, 0.01)

        // Yesterday's IPD Bill update verification
        val yesterdayIpdBill = com.example.data.model.DailyIpdBillEntry(
            id = 10,
            hospitalRegNumber = "SGH-2026-0294",
            patientName = "Bimla Devi",
            dayNumber = 2,
            billDate = "2026-02-28 (Yesterday)",
            morningUpdateTimestamp = "Updated Today at 07:30 AM",
            roomAndNursingCharges = 3500.0,
            doctorConsultationFee = 2000.0,
            medicationsAndPharmacy = 4200.0,
            labAndDiagnostics = 2800.0,
            consumablesAndSurgical = 1300.0,
            dayTotal = 13800.0,
            cumulativeRunningTotal = 26800.0,
            auditStatus = "Audit Verified & Reconciled"
        )

        assertEquals(13800.0, yesterdayIpdBill.dayTotal, 0.01)
        assertEquals(26800.0, yesterdayIpdBill.cumulativeRunningTotal, 0.01)
        assertTrue(yesterdayIpdBill.morningUpdateTimestamp.contains("07:30 AM"))
    }

    @Test
    fun testPatientFeedbackAndGoogleReviewSync() {
        val feedback = com.example.data.model.PatientFeedback(
            id = 1,
            hospitalRegNumber = "SGH-2026-0288",
            patientName = "Gunjan Prakash",
            encounterType = "OPD",
            doctorScore = 9,
            nursingScore = 9,
            hygieneScore = 10,
            billingTransparencyScore = 9,
            overallScore = 10,
            averageScore = 9.4,
            comments = "Great hospital care",
            googleReviewSynced = true,
            submissionDate = "2026-02-28"
        )

        assertEquals(9.4, feedback.averageScore, 0.01)
        assertTrue(feedback.googleReviewSynced)
        assertTrue(feedback.overallScore >= 9)
    }

    @Test
    fun testAdmittedPatientNurseCallAndCareTeam() {
        val nurseCall = com.example.data.model.NurseCallRequest(
            id = 1,
            hospitalRegNumber = "SGH-2026-0294",
            patientName = "Bimla Devi",
            bedNumber = "Ward 3B - Bed 14",
            wardName = "Internal Medicine",
            callReason = "Pain Relief Medication",
            callTime = "10:15 AM",
            status = "Nurse Dispatched",
            attendingNurseName = "Staff Nurse Priya Sharma (RN)"
        )

        assertEquals("Staff Nurse Priya Sharma (RN)", nurseCall.attendingNurseName)
        assertEquals("Ward 3B - Bed 14", nurseCall.bedNumber)
        assertEquals("Nurse Dispatched", nurseCall.status)
    }
}
