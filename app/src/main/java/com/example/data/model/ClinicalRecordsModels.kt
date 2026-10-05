package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pharmacy_prescriptions")
data class PharmacyPrescription(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val hospitalRegNumber: String, // UHID / MRN e.g. "SGH-2026-0288"
    val patientName: String,
    val doctorName: String,
    val department: String,
    val encounterType: String, // "OPD Consultation", "Inpatient Treatment", "Discharge Medication"
    val medicineName: String,
    val dosage: String, // e.g. "625 mg", "40 mg", "500 mg"
    val frequency: String, // e.g. "1-0-1 (Twice Daily After Meals)"
    val duration: String, // e.g. "5 Days", "14 Days", "30 Days Ongoing"
    val instructions: String, // e.g. "Take with plenty of water. Do not skip doses."
    val dispensationStatus: String, // "Dispensed / Ready", "In Preparation", "Collected", "Pending Pharmacy Clearance"
    val datePrescribed: String,
    val rxNumber: String = "RX-2026-104",
    val pharmacyCounter: String = "Counter 3 (Main Ground Floor Pharmacy)"
)

@Entity(tableName = "blood_lab_reports")
data class BloodLabReport(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val hospitalRegNumber: String, // UHID / MRN
    val patientName: String,
    val doctorName: String,
    val department: String,
    val encounterType: String, // "OPD Routine Workup", "Post-Treatment Evaluation", "Discharge Clearance"
    val testCategory: String, // "Complete Blood Count (CBC)", "Kidney Function Test (KFT)", "Liver Function Test (LFT)", "Blood Glucose / HbA1c"
    val reportTitle: String,
    val sampleDate: String,
    val verifiedBy: String = "Dr. S. K. Mitra (MD, Chief Pathologist)",
    val overallStatus: String = "Normal", // "Normal", "Attention Required", "Critical Alert"
    val parametersSummary: String = "", // e.g. "Hb: 13.8 g/dL, WBC: 7,400 /uL, Plt: 2.4 L/uL"
    val labBarcode: String = "LAB-98421"
)

data class LabParameterItem(
    val name: String,
    val resultValue: String,
    val unit: String,
    val referenceRange: String,
    val flag: String // "NORMAL", "HIGH", "LOW", "CRITICAL"
)

object SampleLabDefinitions {
    fun getParametersForCategory(category: String, isAbnormal: Boolean = false): List<LabParameterItem> {
        return when (category) {
            "Complete Blood Count (CBC)" -> listOf(
                LabParameterItem("Hemoglobin (Hb)", if (isAbnormal) "9.4" else "13.8", "g/dL", "12.0 - 15.5", if (isAbnormal) "LOW" else "NORMAL"),
                LabParameterItem("Total Leukocyte Count (WBC)", if (isAbnormal) "14,800" else "7,400", "/uL", "4,000 - 11,000", if (isAbnormal) "HIGH" else "NORMAL"),
                LabParameterItem("Platelet Count", if (isAbnormal) "1.1" else "2.4", "Lakhs/uL", "1.5 - 4.5", if (isAbnormal) "LOW" else "NORMAL"),
                LabParameterItem("Neutrophils", if (isAbnormal) "82" else "65", "%", "40 - 75", if (isAbnormal) "HIGH" else "NORMAL"),
                LabParameterItem("Lymphocytes", "26", "%", "20 - 45", "NORMAL"),
                LabParameterItem("Packed Cell Volume (PCV)", "41.2", "%", "36 - 46", "NORMAL")
            )
            "Kidney Function Test (KFT)" -> listOf(
                LabParameterItem("Serum Creatinine", if (isAbnormal) "2.6" else "1.0", "mg/dL", "0.7 - 1.3", if (isAbnormal) "HIGH" else "NORMAL"),
                LabParameterItem("Blood Urea Nitrogen (BUN)", if (isAbnormal) "48" else "18", "mg/dL", "8 - 23", if (isAbnormal) "HIGH" else "NORMAL"),
                LabParameterItem("Serum Potassium (K+)", if (isAbnormal) "5.6" else "4.2", "mEq/L", "3.5 - 5.0", if (isAbnormal) "HIGH" else "NORMAL"),
                LabParameterItem("Serum Sodium (Na+)", "139", "mEq/L", "135 - 145", "NORMAL"),
                LabParameterItem("Serum Uric Acid", "5.8", "mg/dL", "3.5 - 7.2", "NORMAL")
            )
            "Liver Function Test (LFT)" -> listOf(
                LabParameterItem("Total Bilirubin", if (isAbnormal) "2.4" else "0.8", "mg/dL", "0.2 - 1.2", if (isAbnormal) "HIGH" else "NORMAL"),
                LabParameterItem("Direct Bilirubin", "0.3", "mg/dL", "0.0 - 0.3", "NORMAL"),
                LabParameterItem("SGOT / AST", if (isAbnormal) "68" else "28", "U/L", "10 - 40", if (isAbnormal) "HIGH" else "NORMAL"),
                LabParameterItem("SGPT / ALT", if (isAbnormal) "74" else "32", "U/L", "10 - 45", if (isAbnormal) "HIGH" else "NORMAL"),
                LabParameterItem("Alkaline Phosphatase (ALP)", "88", "U/L", "44 - 147", "NORMAL"),
                LabParameterItem("Total Serum Protein", "7.1", "g/dL", "6.0 - 8.3", "NORMAL")
            )
            else -> listOf( // Glycemic
                LabParameterItem("Fasting Blood Sugar (FBS)", if (isAbnormal) "148" else "92", "mg/dL", "70 - 100", if (isAbnormal) "HIGH" else "NORMAL"),
                LabParameterItem("Post-Prandial (PPBS)", if (isAbnormal) "210" else "128", "mg/dL", "70 - 140", if (isAbnormal) "HIGH" else "NORMAL"),
                LabParameterItem("HbA1c (Glycated Hemoglobin)", if (isAbnormal) "7.8" else "5.4", "%", "4.0 - 5.6", if (isAbnormal) "HIGH" else "NORMAL")
            )
        }
    }
}
