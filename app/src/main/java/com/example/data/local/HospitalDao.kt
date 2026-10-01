package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.FiveSAudit
import com.example.data.model.KaizenProject
import com.example.data.model.LeanWasteLog
import com.example.data.model.PatientRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface PatientDao {
    @Query("SELECT * FROM patient_records ORDER BY id DESC")
    fun getAllPatients(): Flow<List<PatientRecord>>

    @Query("SELECT * FROM patient_records WHERE isDischarged = 0 ORDER BY id DESC")
    fun getActivePatients(): Flow<List<PatientRecord>>

    @Query("SELECT * FROM patient_records WHERE isDischarged = 1 ORDER BY id DESC")
    fun getDischargedPatients(): Flow<List<PatientRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPatient(patient: PatientRecord): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPatients(patients: List<PatientRecord>)

    @Update
    suspend fun updatePatient(patient: PatientRecord)

    @Delete
    suspend fun deletePatient(patient: PatientRecord)

    @Query("SELECT COUNT(*) FROM patient_records")
    suspend fun getCount(): Int
}

@Dao
interface LeanWasteDao {
    @Query("SELECT * FROM lean_waste_logs ORDER BY timestamp DESC")
    fun getAllWasteLogs(): Flow<List<LeanWasteLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWasteLog(log: LeanWasteLog): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWasteLogs(logs: List<LeanWasteLog>)

    @Delete
    suspend fun deleteWasteLog(log: LeanWasteLog)

    @Query("SELECT COUNT(*) FROM lean_waste_logs")
    suspend fun getCount(): Int
}

@Dao
interface FiveSAuditDao {
    @Query("SELECT * FROM five_s_audits ORDER BY id DESC")
    fun getAllAudits(): Flow<List<FiveSAudit>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAudit(audit: FiveSAudit): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAudits(audits: List<FiveSAudit>)

    @Query("SELECT COUNT(*) FROM five_s_audits")
    suspend fun getCount(): Int
}

@Dao
interface KaizenDao {
    @Query("SELECT * FROM kaizen_projects ORDER BY id DESC")
    fun getAllKaizen(): Flow<List<KaizenProject>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKaizen(project: KaizenProject): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKaizenList(projects: List<KaizenProject>)

    @Update
    suspend fun updateKaizen(project: KaizenProject)

    @Delete
    suspend fun deleteKaizen(project: KaizenProject)

    @Query("SELECT COUNT(*) FROM kaizen_projects")
    suspend fun getCount(): Int
}
