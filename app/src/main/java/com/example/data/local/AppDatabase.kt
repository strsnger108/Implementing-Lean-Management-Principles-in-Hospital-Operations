package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.AppUser
import com.example.data.model.BloodLabReport
import com.example.data.model.DailyIpdBillEntry
import com.example.data.model.FamilyMember
import com.example.data.model.FiveSAudit
import com.example.data.model.FiveSCapaItem
import com.example.data.model.FiveSRedTagItem
import com.example.data.model.HmsEhrConfig
import com.example.data.model.KaizenProject
import com.example.data.model.LeanWasteLog
import com.example.data.model.NurseCallRequest
import com.example.data.model.PatientBill
import com.example.data.model.PatientFeedback
import com.example.data.model.PatientInsurance
import com.example.data.model.PatientRecord
import com.example.data.model.PharmacyPrescription
import com.example.data.model.RootCauseAnalysis

@Database(
    entities = [
        PatientRecord::class,
        LeanWasteLog::class,
        FiveSAudit::class,
        FiveSRedTagItem::class,
        FiveSCapaItem::class,
        KaizenProject::class,
        RootCauseAnalysis::class,
        AppUser::class,
        FamilyMember::class,
        HmsEhrConfig::class,
        PharmacyPrescription::class,
        BloodLabReport::class,
        PatientBill::class,
        DailyIpdBillEntry::class,
        PatientInsurance::class,
        NurseCallRequest::class,
        PatientFeedback::class
    ],
    version = 8,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun patientDao(): PatientDao
    abstract fun leanWasteDao(): LeanWasteDao
    abstract fun fiveSAuditDao(): FiveSAuditDao
    abstract fun fiveSRedTagDao(): FiveSRedTagDao
    abstract fun fiveSCapaDao(): FiveSCapaDao
    abstract fun kaizenDao(): KaizenDao
    abstract fun rootCauseDao(): RootCauseDao
    abstract fun userDao(): UserDao
    abstract fun familyMemberDao(): FamilyMemberDao
    abstract fun hmsConfigDao(): HmsConfigDao
    abstract fun pharmacyDao(): PharmacyDao
    abstract fun bloodReportDao(): BloodReportDao
    abstract fun patientBillingDao(): PatientBillingDao
    abstract fun dailyIpdBillDao(): DailyIpdBillDao
    abstract fun patientInsuranceDao(): PatientInsuranceDao
    abstract fun nurseCallDao(): NurseCallDao
    abstract fun patientFeedbackDao(): PatientFeedbackDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "lean_hospital_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
