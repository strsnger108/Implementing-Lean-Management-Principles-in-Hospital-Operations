package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "patient_bills")
data class PatientBill(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val hospitalRegNumber: String, // UHID / MRN e.g. "SGH-2026-0288"
    val patientName: String,
    val billNumber: String, // e.g. "INV-2026-0891"
    val billType: String, // "OPD Consultation", "Lab & Diagnostics", "Pharmacy", "IPD Admission"
    val billDate: String,
    val totalAmount: Double,
    val paidAmount: Double,
    val dueAmount: Double,
    val paymentStatus: String, // "Paid", "Pending", "TPA Approved", "Partially Paid"
    val paymentMode: String, // "UPI / Online", "TPA Cashless", "Debit Card", "Cash Desk"
    val itemizedSummary: String,
    val receiptBarcode: String = "REC-98214"
)

@Entity(tableName = "daily_ipd_bill_entries")
data class DailyIpdBillEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val hospitalRegNumber: String,
    val patientName: String,
    val dayNumber: Int, // e.g. Day 1, Day 2
    val billDate: String, // e.g. "2026-02-28 (Yesterday)"
    val morningUpdateTimestamp: String, // e.g. "Updated Today at 07:15 AM (Morning Audit)"
    val roomAndNursingCharges: Double,
    val doctorConsultationFee: Double,
    val medicationsAndPharmacy: Double,
    val labAndDiagnostics: Double,
    val consumablesAndSurgical: Double,
    val dayTotal: Double,
    val cumulativeRunningTotal: Double,
    val auditStatus: String = "Audit Verified & Reconciled"
)

@Entity(tableName = "patient_insurance_policies")
data class PatientInsurance(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val hospitalRegNumber: String,
    val patientName: String,
    val tpaProvider: String, // e.g. "Star Health & Allied Insurance", "Medi Assist TPA"
    val policyNumber: String,
    val tpaCardNumber: String,
    val sumInsured: Double,
    val preAuthApprovedAmount: Double,
    val claimStatus: String, // "Pre-Auth Approved (Cashless)", "Enhancement Requested", "Settled / Final Clearance"
    val claimNumber: String,
    val copayPercentage: Int = 0, // e.g. 0% or 10%
    val tpaHelpdeskContact: String = "Desk Ext: 1044 / +91-9876543210"
)

@Entity(tableName = "nurse_call_requests")
data class NurseCallRequest(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val hospitalRegNumber: String,
    val patientName: String,
    val bedNumber: String,
    val wardName: String,
    val callReason: String, // "Pain Relief", "IV Fluid Check", "Doctor Request", "Assistance / Emergency"
    val callTime: String,
    val status: String = "Active Call", // "Active Call", "Nurse Dispatched", "Resolved"
    val attendingNurseName: String = "Staff Nurse Priya Sharma (RN)",
    val responseNotes: String = "Nurse notified at Central Nursing Station 3B"
)

@Entity(tableName = "patient_feedbacks")
data class PatientFeedback(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val hospitalRegNumber: String,
    val patientName: String,
    val encounterType: String, // "OPD", "IPD"
    val doctorScore: Int, // 1 to 10
    val nursingScore: Int, // 1 to 10
    val hygieneScore: Int, // 1 to 10
    val billingTransparencyScore: Int, // 1 to 10
    val overallScore: Int, // 1 to 10
    val averageScore: Double, // computed average out of 10
    val comments: String,
    val googleReviewSynced: Boolean = false,
    val submissionDate: String
)

data class PatientDiagnosisSummary(
    val id: String,
    val hospitalRegNumber: String,
    val patientName: String,
    val encounterDate: String,
    val encounterType: String,
    val primaryDiagnosis: String,
    val icd10Code: String,
    val doctorName: String,
    val department: String,
    val bloodPressure: String,
    val pulseRate: String,
    val spo2: String,
    val temperature: String,
    val chiefComplaints: String,
    val clinicalAdvice: String
)
