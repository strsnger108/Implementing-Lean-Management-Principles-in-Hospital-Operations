package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.BloodLabReport
import com.example.data.model.PharmacyPrescription
import kotlinx.coroutines.flow.Flow

@Dao
interface PharmacyDao {
    @Query("SELECT * FROM pharmacy_prescriptions ORDER BY id DESC")
    fun getAllPrescriptions(): Flow<List<PharmacyPrescription>>

    @Query("SELECT * FROM pharmacy_prescriptions WHERE hospitalRegNumber = :regNumber ORDER BY id DESC")
    fun getPrescriptionsByRegNumber(regNumber: String): Flow<List<PharmacyPrescription>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrescription(prescription: PharmacyPrescription): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrescriptions(prescriptions: List<PharmacyPrescription>)

    @Update
    suspend fun updatePrescription(prescription: PharmacyPrescription)

    @Query("UPDATE pharmacy_prescriptions SET dispensationStatus = :newStatus WHERE id = :id")
    suspend fun updateDispensationStatus(id: Long, newStatus: String)

    @Query("SELECT COUNT(*) FROM pharmacy_prescriptions")
    suspend fun getCount(): Int
}

@Dao
interface BloodReportDao {
    @Query("SELECT * FROM blood_lab_reports ORDER BY id DESC")
    fun getAllReports(): Flow<List<BloodLabReport>>

    @Query("SELECT * FROM blood_lab_reports WHERE hospitalRegNumber = :regNumber ORDER BY id DESC")
    fun getReportsByRegNumber(regNumber: String): Flow<List<BloodLabReport>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: BloodLabReport): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReports(reports: List<BloodLabReport>)

    @Query("SELECT COUNT(*) FROM blood_lab_reports")
    suspend fun getCount(): Int
}
